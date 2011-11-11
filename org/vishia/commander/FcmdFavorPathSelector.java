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

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.gral.widget.SelectList;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileWriter;
import org.vishia.util.KeyCode;

/**This class implements the selection functionality of tabs and paths for the whole Java commander. */
class FcmdFavorPathSelector
{

  /**Entry in the favorite list. */
  static class SelectInfo
  { /**The path of directory to select. */
    String path;
    /**The name shown in the list. */
    String selectName;
    /**The name of the tab in any of the three panels if it is associated to a Tab of the panel. */
    String[] tabName = new String[3]; //, tabName2, tabName3;
    //char active;
    @Override public String toString(){ return path; } //for debug
  }
  
  File fileCfg;
  
  /**Instance to write the cfgFile. */
  FileWriter writerCfg = new FileWriter();
  
  /**The three tabbed panels. */
  final LeftMidRightPanel panelLeft, panelMid, panelRight;
  
  private final GralWidgetMng mng;
  
  private final JavaCmd main;
  
  /**All entries which are shown in all three select lists. */
  List<SelectInfo> selectAll = new LinkedList<SelectInfo>();
  
  /**For output messages. */
  final MainCmd_ifc console;
  
  
  /**The last selected SelectInfo. */
  SelectInfo actSelectInfo;

  static class WindowConfirmAddFavorite
  {
    /**The window for confirming adding a new favorite. */
    GralWindow_ifc window;
    
    /**The panel from where the window was opened. It helps to get the current selected line in favorites. */
    LeftMidRightPanel panelInvocation;
  
    /**The short name input field in window confirm add favorite.  */
    GralTextField_ifc widgShortName;
    /**The tab input field in window confirm add favorite.  */
    GralTextField_ifc widgTab;
    /**The path input field in window confirm add favorite.  */
    GralTextField_ifc widgPath;
  }
  
  WindowConfirmAddFavorite windAddFavorite = new WindowConfirmAddFavorite();
  
  FcmdFavorPathSelector(MainCmd_ifc console, JavaCmd main)
  { this.main = main;
    this.console = console;
    this.mng = main.gralMng;
    panelLeft = new LeftMidRightPanel(main, 'l', '1', this, mng); 
    panelMid = new LeftMidRightPanel(main, 'm','2',  this, mng); 
    panelRight = new LeftMidRightPanel(main,'r', '3',  this, mng);

  }
  

  
  
  
  /**Builds the content of the add-favorite window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowAddFavorite()
  {
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    windAddFavorite.window = main.gralMng.createWindow("addFavoriteWindow", "add favorite", false);
    
    main.gralMng.setPosition(4, GralGridPos.size -4, 1, GralGridPos.size +45, 0, 'd');
    //main.panelMng.addText("Tab name:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    windAddFavorite.widgTab = main.gralMng.addTextField("addFavoriteTab", true, "file tab", 't');
    windAddFavorite.widgShortName = main.gralMng.addTextField("addFavoriteAlias", true, "alias (show in list)", 't');
    windAddFavorite.widgPath = main.gralMng.addTextField("addFavoritePath", true, "the directory path", 't');
    
    main.gralMng.setPosition(-6, -3, 1, 6, 0, 'r');
    main.gralMng.addButton("addFavoriteEsc", actionAddFavorite, "esc", null, null, "esc");
    main.gralMng.setPosition(-6, -3, -6, -1, 0, 'r');
    main.gralMng.addButton("addFavoriteOk", actionAddFavorite, "ok", null, null, "OK");
  
  }
  
  

  
  
  
  /**Read the configuration file. It is called on startup of the Java commander.
   * @param cfgFile
   * @return an error message to output or null
   */
  String readCfg(File cfgFile)
  { this.fileCfg = cfgFile;
    String sError = null;
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(cfgFile));
    } catch(FileNotFoundException exc){ sError = "TabSelector - cfg file not found; " + cfgFile; }
    if(reader !=null){
      try{ 
        panelLeft.selectListAllFavorites.clear();
        panelMid.selectListAllFavorites.clear();
        panelRight.selectListAllFavorites.clear();
        String sLine;
        int posSep;
        List<SelectInfo> list = null;
        boolean bAll = true;
        while( sError == null && (sLine = reader.readLine()) !=null){
          sLine = sLine.trim();
          if(sLine.length() >0){
            if( sLine.startsWith("==")){
              posSep = sLine.indexOf("==", 2);  
              //a new division
              String sDiv = sLine.substring(2,posSep);
              if(sDiv.equals("left")){ list = panelLeft.selectListAllFavorites; }
              else if(sDiv.equals("mid")){ list = panelMid.selectListAllFavorites; }
              else if(sDiv.equals("right")){ list = panelRight.selectListAllFavorites; }
              else if(sDiv.equals("all")){ list = selectAll; }
              else { sError = "Error in cfg file: ==" + sDiv + "=="; }
            } else { 
              String[] sParts = sLine.trim().split(",");
              if(sParts.length < 2){ 
                sError = "SelectTab format error; " + sLine; 
              } else {
                SelectInfo info = new SelectInfo();
                //info. = sParts[0].trim();
                info.selectName = sParts[0].trim();
                info.path = sParts[1].trim();
                for(int ix = 2; ix < sParts.length; ++ix){
                  final String actTabEntry = sParts[ix].trim();
                  final String actTab;
                  if(actTabEntry.length() > 2 && actTabEntry.charAt(1) == ':'){
                    final char cWhere = actTabEntry.charAt(0);
                    final int ixWhere = "lmr".indexOf(cWhere);
                    if(ixWhere <0)throw new IllegalArgumentException("fault panel, use l:label or m:label or r:label in file; " + cfgFile + " line; " + sLine);
                    actTab = actTabEntry.substring(2).trim();
                    info.tabName[ixWhere] = actTab;
                  }
                }
                //info.active = cActive;
                list.add(info);
              }
            }
          }
        }
      } 
      catch(IOException exc){ sError = "selectTab - cfg file read error; " + cfgFile; }
      catch(IllegalArgumentException exc){ sError = "selectTab - cfg file error; " + cfgFile + exc.getMessage(); }
      catch(Exception exc){ sError = "selectTab - any exception; " + cfgFile + exc.getMessage(); }
      try{ reader.close(); reader = null; } catch(IOException exc){} //close is close.
    }
    return sError;
  }


  /**Writes the configuration file. It is called if the configuration should be stored after user invocation.
   * @param cfgFile
   * @return an error message to output or null
   */
  void writeCfg(File cfgFile)
  { boolean bOk = true;
    if(cfgFile.exists()){
      
      String sName = cfgFile.getName() + ".old";
      File cfgFileOld = new File(cfgFile.getParentFile(), sName);
      if(cfgFileOld.exists()){
        bOk = cfgFileOld.delete();
        if(!bOk){
          main.mainCmd.writeError("can't delete " + cfgFileOld.getAbsolutePath());
        }
      }
      if(bOk){
        bOk = cfgFile.renameTo(cfgFileOld);
        if(!bOk){
          main.mainCmd.writeError("can't rename " + cfgFile.getAbsolutePath());
        }
      }
    }
    if(bOk){
      bOk = writerCfg.open(cfgFile.getAbsolutePath(), false) ==0;
    }
    if(bOk){
      try{
        writerCfg.append("==all==\n");
        for(SelectInfo entry: selectAll){ writeCfgLine(entry); }
        writerCfg.append("==left==\n");
        for(SelectInfo entry: panelLeft.selectListAllFavorites){ writeCfgLine(entry); }
        writerCfg.append("==mid==\n");
        for(SelectInfo entry: panelMid.selectListAllFavorites){ writeCfgLine(entry); }
        writerCfg.append("==right==\n");
        for(SelectInfo entry: panelRight.selectListAllFavorites){ writeCfgLine(entry); }
      }
      catch(IOException exc){
        main.mainCmd.writeError("error writing" , exc);
      }
    }
    writerCfg.close();
  }
  
  
  
  private void writeCfgLine(SelectInfo info) throws IOException
  {
    writerCfg.append(info.selectName).append(", ").append(info.path);
    if(info.tabName[0] !=null){ writerCfg.append(", l:").append(info.tabName[0]); }
    if(info.tabName[1] !=null){ writerCfg.append(", m:").append(info.tabName[1]); }
    if(info.tabName[2] !=null){ writerCfg.append(", r:").append(info.tabName[2]); }
    writerCfg.append("\n");
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
  void buildTabFromSelection(FcmdFavorPathSelector.SelectInfo info, GralTabbedPanel tabPanel)
  { assert(false);
    /*
    tabPanel.addGridPanel(info.tabName1, info.tabName1,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    FileSelector fileSelector = new FileSelector("fileSelector-"+info.tabName1, mng);
    fileSelector.setActionOnEnterFile(main.executer.actionExecute);
    main.idxFileSelector.put(info.tabName1, fileSelector);
    fileSelector.setToPanel(mng, info.tabName1, 5, new int[]{2,20,5,10}, 'A');
    fileSelector.fillIn(new File(info.path));
    */
  }
  
  
  
  /**Searches the Tab which is focused at last.
   * @return Array of the tabs in order of last focus
   */
  private LeftMidRightPanel[] getLastTabs()
  { List<GralWidget> widgdFocus = mng.getWidgetsInFocus();
    LeftMidRightPanel[] lastTabs = new LeftMidRightPanel[3];
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
   * The searching process uses the {@link GralPanelMngWorking_ifc#getWidgetsInFocus()} routine.
   * A FileSelector-Tab is named starting with "tabFile_".
   * @return The last instance
   */
  private FcmdFileTable getLastFileTab()
  { List<GralWidget> widgdFocus = mng.getWidgetsInFocus();
    FcmdFileTable lastTab = null;
    //int ixTabs = 0;
    synchronized(widgdFocus){
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while(lastTab ==null && iterFocus.hasNext()){
        GralWidget widgd = iterFocus.next();
        Object oContentInfo;
        if(widgd.name.startsWith("tabFile_") && ( oContentInfo = widgd.getContentInfo()) instanceof FcmdFileTable){
          lastTab = (FcmdFileTable)oContentInfo;      
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
  
  
  
  GralUserAction actionAddFavorite = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(key == KeyCode.mouse1Up){
        if(infos.sCmd.equals("ok")){
          //GralTableLine_ifc selectedLine = windAddFavorite.panelInvocation.selectTable.wdgdTable.getCurrentLine();
          SelectInfo selectInfo = actSelectInfo; //(SelectInfo)selectedLine.getUserData();
          List<FcmdFavorPathSelector.SelectInfo> listAdd = windAddFavorite.panelInvocation.selectListAllFavorites;
          int posInList = listAdd.indexOf(selectInfo);
          if(posInList == -1){
            //maybe selected one entry from the tab-special list or one entry of the common list.
            listAdd = selectAll;
            posInList = listAdd.indexOf(selectInfo);
          }
          SelectInfo favorite = new SelectInfo();
          favorite.path = windAddFavorite.widgPath.getText();
          favorite.selectName = windAddFavorite.widgShortName.getText();
          String tablabel = windAddFavorite.widgTab.getText();
          int ixtabName = windAddFavorite.panelInvocation.cNr - '1';
          favorite.tabName[ixtabName] = tablabel;
          listAdd.add(posInList+1, favorite);
          if(listAdd == selectAll){
            panelLeft.fillInAllTables('.');
            panelMid.fillInAllTables('.');
            panelRight.fillInAllTables('.');
          } else {
            windAddFavorite.panelInvocation.fillInAllTables('.'); //windAddFavorite.panelInvocation.cc);
          }
        }
        main.gralMng.setWindowsVisible(windAddFavorite.window, null); //set it invisible.
      }
      return true;
    }
  };

  

  
  GralUserAction actionSaveFavoritePathes = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { writeCfg(fileCfg);
      return true;
  } };
  
  
  /**This is the list-panel which allows to select all opened tabs, all directories or other entries.  
   */
  class SelectTabList extends SelectList
  {
    
    /**The tabbed panel where the List is member of. */
    final LeftMidRightPanel panel;
    
    
    
    public SelectTabList(String name, LeftMidRightPanel panel, GralWidgetMng mng)
    { super(name, mng);
      this.panel = panel;
    }


    /**Adds a line to this table.
     * @param ix Show which index is used for a local table, 0..2 for left, mid, right,
     *   than show the label in the left cell (column)
     * @param info The favorite info
     */
    void add(int ix, SelectInfo info)
    {
      GralTableLine_ifc line = wdgdTable.insertLine(null, 0);
      line.setUserData(info);
      String label = info.tabName[ix];
      if(label !=null){
        line.setCellText(label, 0);
      }
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
      actSelectInfo = info; //The last used selection (independent of tab left, middle, right)
      int ixtabName = panel.cNr - '1';
      
      if(true){ //info.active != '.'){
        String tabName = info.tabName[ixtabName];  //The label of file tab.
        GralWidget widgd;
        if(tabName !=null && (widgd = mng.getWidget(tabName)) !=null){
          mng.setFocus(widgd);   //focus the file tab if it is exising. 
        } else {
          //a new path is selected:
          //save the path of the current selection
      
          String label = info.tabName[ixtabName];  //from favorite list
          FcmdFileTable fileTable;
          if(label  == null){ 
            fileTable = getLastFileTab();
            if(fileTable == null){
              if(panel.listTabs.size() >0){
                fileTable = panel.listTabs.get(0);
                label = info.tabName[ixtabName] = fileTable.labelTab;
              } else {
                label = "file" + panel.cNr;
                info.tabName[ixtabName] = label;
                fileTable = panel.searchOrCreateFileTabs(label);
              }
            }
            panel.fillInAllTables(panel.cc);
          } else {
            //label is known in the favorite list, use it. The panel should be existing or it is created.
            fileTable = panel.searchOrCreateFileTabs(label);
          }
         

          
          File currentDir;
          if(fileTable.actualDir !=null){
            currentDir = fileTable.getCurrentDir();
            if(currentDir !=null){
              panel.indexActualDir.put(fileTable.actualDir, currentDir);
          } }
          //fill in the standard file panel:
          fileTable.actualDir = info.selectName;
          currentDir = panel.indexActualDir.get(info.selectName);
          if(currentDir == null){
            currentDir = new File(info.path);
          }

          
          
          
          fileTable.fillIn(currentDir);
          fileTable.setFocus();
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
  
    /**Handle the keys for the JavaCommander-Selection of favorites
     * <ul>
     * <li>sh-F1 .. shF3: activates fileSelector for left, middle and right panel.
     * </ul>
     * @see org.vishia.gral.widget.SelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override protected boolean actionUserKey(int key, Object userData,
        GralTableLine_ifc line)
    { boolean ret = true;
      SelectInfo data = (SelectInfo)userData;
      //TODO not used no more
      if(key ==KeyCode.shift + KeyCode.F1){
        File dir = new File(data.path);
        //panelLeft.fileSelectorMain.fillIn(dir);
      } else if (key ==KeyCode.shift + KeyCode.F2){
        File dir = new File(data.path);
        //panelMid.fileSelectorMain.fillIn(dir);
      } else if (key ==KeyCode.shift + KeyCode.F3){
          //panelRight.fileSelectorMain.fillIn(new File(data.path));
      } else if (key ==KeyCode.shift + KeyCode.F5){
        //reread the configuration file.
        readCfg(fileCfg);
        panelLeft.fillInAllTables('l');
      } else if (key ==main.keyActions.keyCreateFavorite){
        actSelectInfo = data; //info in the line of table.
        windAddFavorite.widgTab.setText("file3");
        windAddFavorite.widgShortName.setText("alias");
        //File lastSelectedFile = panelRight.fileSelectorMain.getSelectedFile();
        //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
        //windAddFavorite.widgPath.setText(pathDir);
        windAddFavorite.window.setWindowVisible(true);
      } else {
        ret = false;
      }//
      return ret;
    }
  } //class SelectList_  
}
