package org.vishia.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.PrepareCmd;
import org.vishia.cmd.CmdStore.CmdBlock;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.FileSelector;
import org.vishia.gral.widget.SelectList;
import org.vishia.gral.widget.TableLineGui_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

/**This class implements the selection functionality of tabs and pathes. */
class SelectTab
{

  static class SelectInfo
  { String path;
    String selectName;
    String tabName;
    char active;
  }
  
  /**Data for one tabbed panel
   */
  static class TabbedPanelData
  {
    /**Instance which works with all Tabs. */
    final SelectTab tabSelector;
    
    /**The container for all tabs of one TabbedPanel. */
    GralTabbedPanel tabbedPanel;
    
    /**All entries for the left select list in order of the file. */
    List<SelectInfo> selectList = new LinkedList<SelectInfo>();

    /**Table widget for the three select tables.*/
    SelectTabList selectTable;

    private final Map<String, String> indexActualDir = new TreeMap<String, String>();
    
    FileSelector fileSelectorMain;
    
    final char cc;
    
    final char cNr;
    
    TabbedPanelData(char cc, char cNr, SelectTab tabSelector){
      this.cc = cc;
      this.cNr = cNr;
      this.tabSelector = tabSelector;
      selectTable = tabSelector.new SelectTabList(this);
    }
    
    /**Fills the table list left. 
     * It have to be called in the graphic thread.
     */
    void fillInTable()
    { selectTable.clear();
      for(SelectInfo info: selectList){
        selectTable.add(info, cc);
        tabSelector.initActDir(indexActualDir, info.selectName, info.path);
      }
      for(SelectInfo info: tabSelector.selectAll){
        selectTable.add(info, cc);
        tabSelector.initActDir(indexActualDir, info.selectName, info.path);
      }
    }
    
    
  }
  
  final TabbedPanelData panelLeft, panelMid, panelRight;
  
  private final GralGridMngBase mng;
  
  private final JavaCmd main;
  
  /**All entries which are shown in all three select lists. */
  List<SelectInfo> selectAll = new LinkedList<SelectInfo>();
  
  /**For output messages. */
  final MainCmd_ifc console;
  
  
  int[] widthSelecttable = new int[]{2, 20, 30};
  

  
  SelectTab(MainCmd_ifc console, JavaCmd main)
  { this.main = main;
    this.console = console;
    this.mng = main.panelMng;
    panelLeft = new TabbedPanelData('l', '1', this); 
    panelMid = new TabbedPanelData('m','2',  this); 
    panelRight = new TabbedPanelData('r', '3',  this);

  }
  
  /**Read the configuration file. It is called on startup of the Java commander.
   * @param cfgFile
   * @return an error message to output or null
   */
  String readCfg(File cfgFile)
  {
    String sError = null;
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(cfgFile));
    } catch(FileNotFoundException exc){ sError = "TabSelector - cfg file not found; " + cfgFile; }
    if(reader !=null){
      panelLeft.selectList.clear();
      panelMid.selectList.clear();
      panelRight.selectList.clear();
      String sLine;
      int posSep;
      List<SelectInfo> list = null;
      boolean bAll = true;
      try{ 
        while( sError == null && (sLine = reader.readLine()) !=null){
          if( sLine.startsWith("==")){
            posSep = sLine.indexOf("==", 2);  
            //a new division
            String sDiv = sLine.substring(2,posSep);
            if(sDiv.equals("left")){ list = panelLeft.selectList; }
            else if(sDiv.equals("mid")){ list = panelMid.selectList; }
            else if(sDiv.equals("right")){ list = panelRight.selectList; }
            else if(sDiv.equals("all")){ list = selectAll; }
            else { sError = "Error in cfg file: ==" + sDiv + "=="; }
          } else { 
            char cActive = sLine.charAt(0);
            if( "lmr".indexOf(cActive) >=0 && sLine.charAt(1)==':'){
              sLine = sLine.substring(2);
            } else {
              cActive = '.';
            }
            String[] sParts = sLine.trim().split(",");
            if(sParts.length < 3){ 
              sError = "SelectTab format error; " + sLine; 
            } else {
              SelectInfo info = new SelectInfo();
              info.tabName = sParts[0].trim();
              info.selectName = sParts[1].trim();
              info.path = sParts[2].trim();
              info.active = cActive;
              list.add(info);
            }
          }      
        }
      } 
      catch(IOException exc){ sError = "selectTab - cfg file read error; " + cfgFile; }
      catch(IllegalArgumentException exc){ sError = "selectTab - cfg file error; " + cfgFile + exc.getMessage(); }
    }
    return sError;
  }


  /**Writes the configuration file. It is called if the configuration should be stored after user invocation.
   * @param cfgFile
   * @return an error message to output or null
   */
  String writeCfg(File cfgFile)
  {
    return "not ready yet.";
  }
  
  
  
  void buildInitialTabs(TabbedPanelData tabbedPanel)
  {
    tabbedPanel.tabbedPanel.addGridPanel("Sel" + tabbedPanel.cNr, "a-F"+tabbedPanel.cNr,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    tabbedPanel.selectTable.setToPanel(mng, "sel0", 5, widthSelecttable, 'A');
    tabbedPanel.fillInTable();
    
    tabbedPanel.tabbedPanel.addGridPanel("tabFile"+tabbedPanel.cNr, "file&"+tabbedPanel.cNr,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    tabbedPanel.fileSelectorMain = new FileSelector(console);
    main.idxFileSelector.put("filesLeft", tabbedPanel.fileSelectorMain);
    tabbedPanel.fileSelectorMain.setToPanel(mng, "filesLeft", 5, new int[]{2,20,5,10}, 'A');
    
    if(tabbedPanel.cNr == '1'){
      tabbedPanel.tabbedPanel.addGridPanel("cmd", "Cm&d",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{10,10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  
  
  void initActDir(Map<String, String> index, String key, String path)
  {
    if(index.get(key) == null){
      index.put(key, path);
    }
  }
  
  
  
  /**Builds a tab for file or command view from a selected line of selection.
   * @param info The selection info
   */
  void buildTabFromSelection(SelectTab.SelectInfo info, GralTabbedPanel tabPanel)
  { 
    tabPanel.addGridPanel(info.tabName, info.tabName,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    FileSelector fileSelector = new FileSelector(console);
    main.idxFileSelector.put(info.tabName, fileSelector);
    fileSelector.setToPanel(mng, info.tabName, 5, new int[]{2,20,5,10}, 'A');
    fileSelector.fillIn(new File(info.path));
  }
  

  void stop(){}
  
  
  /**This is the list-panel which allows to select all opened tabs, all directories or other entries.  
   */
  class SelectTabList extends SelectList
  {
    
    final TabbedPanelData panel;
    
    
    
    
    public SelectTabList(TabbedPanelData panel)
    { this.panel = panel;
    }


    void add(SelectInfo info, char where)
    {
      TableLineGui_ifc line = table.insertLine(null, 0);
      line.setUserData(info);
      line.setCellText(info.active == where ? "" + where: " ",0);
      line.setCellText(info.selectName, 1);
      line.setCellText(info.path, 2);
    }
    
    
    void clear()
    {
      table.clearTable();
    }
    
    
    @Override
    protected void actionOk(Object userData, TableLineGui_ifc line)
    {
      SelectInfo info = (SelectInfo)line.getUserData();
      if(info.active != '.'){
        String tabName = info.tabName;
        GralWidget widgd = mng.getWidget(tabName);
        if(widgd !=null){
          mng.setFocus(widgd);
        } else {
          //fill in the standard file panel:
          stop();
          panel.fileSelectorMain.fillIn(new File(info.path));
          //unnecessary, fileSlectorMain.setFocus is sufficient.: panel.tabbedPanel.selectTab("tabFile"+panel.cNr);
          panel.fileSelectorMain.setFocus();
        }
      }
      
    }
  
    @Override
    protected void actionLeft(Object userData, TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionRight(Object userData, TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionUserKey(String sKey, Object userData,
        TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  } //class SelectList_  
}
