package org.vishia.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.gral.widget.SelectList;
import org.vishia.mainCmd.MainCmd_ifc;

/**This class implements the selection functionality of tabs and pathes. */
class SelectTab
{

  static class SelectInfo
  { String path;
    String selectName;
    String tabName;
    char active;
  }
  
  
  final TabbedPanelData panelLeft, panelMid, panelRight;
  
  private final GralWidgetMng mng;
  
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
    panelLeft = new TabbedPanelData('l', '1', this, mng); 
    panelMid = new TabbedPanelData('m','2',  this, mng); 
    panelRight = new TabbedPanelData('r', '3',  this, mng);

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
  
  
  
  /**Build the initial content of one of the three tabbed panels, called in the build phase of the GUI.
   * @param tabbedPanel The TabbbedPanel, created and assigned in the main window.
   */
  void buildInitialTabs(TabbedPanelData tabbedPanel)
  {
    tabbedPanel.tabbedPanel.addGridPanel("Sel" + tabbedPanel.cNr, "a-F"+tabbedPanel.cNr,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    tabbedPanel.selectTable.setToPanel(mng, "sel0", 5, widthSelecttable, 'A');
    tabbedPanel.fillInTable();
    
    tabbedPanel.tabbedPanel.addGridPanel(WidgetNames.panelFile + "main"+tabbedPanel.cNr, "file&"+tabbedPanel.cNr,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    tabbedPanel.fileSelectorMain = new FileSelector("fileSelectorMain", console, (GralWidgetMng)mng);
    tabbedPanel.fileSelectorMain.setActionOnEnterFile(main.executer.actionExecute);
    String nameTab = WidgetNames.tableFile + "main" + tabbedPanel.cNr;
    main.idxFileSelector.put(nameTab, tabbedPanel.fileSelectorMain);
    tabbedPanel.fileSelectorMain.setToPanel(mng, nameTab, 5, new int[]{2,20,5,10}, 'A');
    if(tabbedPanel.cNr == '1'){ //commands only in the left panel.
      tabbedPanel.tabbedPanel.addGridPanel("cmd", "Cm&d",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{10,10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  
  
  void initActDir(Map<String, File> index, String key, String path)
  {
    if(index.get(key) == null){
      index.put(key, new File(path));
    }
  }
  
  
  
  /**Builds a tab for file or command view from a selected line of selection.
   * @param info The selection info
   */
  void buildTabFromSelection(SelectTab.SelectInfo info, GralTabbedPanel tabPanel)
  { 
    tabPanel.addGridPanel(info.tabName, info.tabName,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    FileSelector fileSelector = new FileSelector("fileSelector-"+info.tabName, console, mng);
    fileSelector.setActionOnEnterFile(main.executer.actionExecute);
    main.idxFileSelector.put(info.tabName, fileSelector);
    fileSelector.setToPanel(mng, info.tabName, 5, new int[]{2,20,5,10}, 'A');
    fileSelector.fillIn(new File(info.path));
  }
  
  
  
  /**Searches the Tab which is focused at last.
   * @return Array of the tabs in order of last focus
   */
  private TabbedPanelData[] getLastTabs()
  { List<GralWidget> widgdFocus = mng.getWidgetsInFocus();
    TabbedPanelData[] lastTabs = new TabbedPanelData[3];
    int ixTabs = 0;
    synchronized(widgdFocus){
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while(ixTabs < lastTabs.length && iterFocus.hasNext()){
        GralWidget widgd = iterFocus.next();
        if(widgd.name.startsWith("file")){
          
        }
      }
    }
    return lastTabs;
  }
  
  
  

  
  
  
  /**Searches the File-Tab which is focused at last.
   * @return The last instance
   */
  private FileSelector getLastFileTab()
  { List<GralWidget> widgdFocus = mng.getWidgetsInFocus();
    FileSelector lastTab = null;
    int ixTabs = 0;
    synchronized(widgdFocus){
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while(lastTab ==null && iterFocus.hasNext()){
        GralWidget widgd = iterFocus.next();
        Object oContentInfo;
        if(widgd.name.startsWith("tabFile_") && ( oContentInfo = widgd.getContentInfo()) instanceof FileSelector){
          lastTab = (FileSelector)oContentInfo;      
        }
      }
    }
    return lastTab;
  }
  
  
  

  
  
  

  void stop(){}
  
  
  /**Sets the origin dir of the last focused file table.
   * <br>
   * Implementation note: 
   * <ul>
   * <li>The last focused file tab is searched. The last focused widgets are stored
   * in a List and are returned from {@link GralWidgetMng#getWidgetsInFocus()} if the widget implementation
   * has the necessary focus action. Most of widgets have it, especially the File tables.
   * The instance of {@link FileSelector} is stored in the {@link GralWidget#getContentInfo()} .
   * The {@link #getLastFileTab()} method gets it.
   * <li>
   * </ul>
   */
  GralUserAction actionSetDirOrigin = new GralUserAction(){
    @Override public boolean userActionGui(String sIntension, GralWidget widgd, Object... params)
    { FileSelector lastTab = getLastFileTab();
      if(lastTab !=null){
        lastTab.fillInOriginDir();
      } else {
        throw new IllegalArgumentException("last file tab not able to found");
      }
      return false;
    }
  };
  
  
  /**This is the list-panel which allows to select all opened tabs, all directories or other entries.  
   */
  class SelectTabList extends SelectList
  {
    
    /**The tabbed panel where the List is member of. */
    final TabbedPanelData panel;
    
    
    
    public SelectTabList(String name, TabbedPanelData panel, GralWidgetMng mng)
    { super(name, mng);
      this.panel = panel;
    }


    void add(SelectInfo info, char where)
    {
      GralTableLine_ifc line = wdgdTable.insertLine(null, 0);
      line.setUserData(info);
      line.setCellText(info.active == where ? "" + where: " ",0);
      line.setCellText(info.selectName, 1);
      line.setCellText(info.path, 2);
    }
    
    
    void clear()
    {
      wdgdTable.clearTable();
    }
    
    
    @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
    {
      SelectInfo info = (SelectInfo)line.getUserData();
      if(info.active != '.'){
        String tabName = info.tabName;
        GralWidget widgd = mng.getWidget(tabName);
        if(widgd !=null){
          mng.setFocus(widgd);
        } else {
          //save the path of the current selection
          File currentDir;
          if(panel.actualDir !=null){
            currentDir = panel.fileSelectorMain.getCurrentDir();
            if(currentDir !=null){
              panel.indexActualDir.put(panel.actualDir, currentDir);
          } }
          //fill in the standard file panel:
          panel.actualDir = info.selectName;
          currentDir = panel.indexActualDir.get(info.selectName);
          if(currentDir == null){
            currentDir = new File(info.path);
          }
          panel.fileSelectorMain.fillIn(currentDir);
          //unnecessary, fileSlectorMain.setFocus is sufficient.: panel.tabbedPanel.selectTab("tabFile"+panel.cNr);
          panel.fileSelectorMain.setFocus();
        }
      }
      return true;
    }
  
    @Override
    protected void actionLeft(Object userData, GralTableLine_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionRight(Object userData, GralTableLine_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionUserKey(String sKey, Object userData,
        GralTableLine_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  } //class SelectList_  
}
