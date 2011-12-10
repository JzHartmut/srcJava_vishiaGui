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
import org.vishia.gral.base.GralWindow;
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
  static class FavorPath
  { /**The path of directory to select. */
    String path;
    /**The name shown in the list. */
    String selectName;
    /**The label on the tab in tabbed panel. */
    //String label;
    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
     * it means, a tab with the label will be created. */
    int mMainPanel;

    @Override public String toString(){ return path; } //for debug
  }
  
  
  static class FavorTab
  {
    /**The label on the tab in tabbed panel. */
    final String label;
    /**The name shown in the list. */
    final String selectNameTab;
    /**The associated list of selectInfos. */
    final List<FavorPath> favorPathInfo = new LinkedList<FavorPath>();
    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
     * it means, a tab with the label will be created. */
    int mMainPanel;
    
    public FavorTab(String label, String selectNameTab)
    { this.label = label;
      this.selectNameTab = selectNameTab;
    } 
 
    @Override public String toString(){ return label; } //for debug
    
  }
  
  /**All entries for the select list for all favorites in order of the file. */
  //Map<String, FcmdFavorPathSelector.SelectTab> selectListOverview = new TreeMap<String, FcmdFavorPathSelector.SelectTab>();
  List<FcmdFavorPathSelector.FavorTab> listAllFavorTabs = new LinkedList<FcmdFavorPathSelector.FavorTab>();

  
  File fileCfg;
  
  /**Instance to write the cfgFile. */
  FileWriter writerCfg = new FileWriter();
  
  /**The three tabbed panels. */
  final LeftMidRightPanel panelLeft, panelMid, panelRight;
  
  private final GralWidgetMng mng;
  
  final JavaCmd main;
  
  /**All entries which are shown in all three select lists. */
  List<FavorPath> listAllFavorPaths = new LinkedList<FavorPath>();
  
  /**For output messages. */
  final MainCmd_ifc console;
  
  
  /**The last selected SelectInfo. */
  FavorPath actFavorPathInfo;

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
    panelLeft = new LeftMidRightPanel(main, 'l', '1', mng); 
    panelMid = new LeftMidRightPanel(main, 'm','2',  mng); 
    panelRight = new LeftMidRightPanel(main,'r', '3',  mng);

  }
  

  
  
  
  /**Builds the content of the add-favorite window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowAddFavorite()
  {
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    windAddFavorite.window = main.gralMng.createWindow("addFavoriteWindow", "add favorite", GralWindow.windConcurrently);
    
    main.gralMng.setPosition(4, GralGridPos.size -4, 1, GralGridPos.size +45, 0, 'd');
    //main.panelMng.addText("Tab name:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    windAddFavorite.widgTab = main.gralMng.addTextField("addFavoriteTab", true, "file tab", 't');
    windAddFavorite.widgShortName = main.gralMng.addTextField("addFavoriteAlias", true, "alias (show in list)", 't');
    windAddFavorite.widgPath = main.gralMng.addTextField("addFavoritePath", true, "the directory path", 't');
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("addFavoriteEsc", actionAddFavorite, "esc", null, null, "esc");
    main.gralMng.setPosition(-4, -1, -6, -1, 0, 'r');
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
        panelLeft.listAllFavorPaths.clear();
        panelMid.listAllFavorPaths.clear();
        panelRight.listAllFavorPaths.clear();
        listAllFavorTabs.clear();
        String sLine;
        int posSep;
        //List<FavorPath> list = null;
        FcmdFavorPathSelector.FavorTab favorTabInfo = null;
        //boolean bAll = true;
        while( sError == null && (sLine = reader.readLine()) !=null){
          sLine = sLine.trim();
          if(sLine.length() >0){
            if( sLine.startsWith("==")){
              posSep = sLine.indexOf("==", 2);  
              //a new division
              final String sDiv = sLine.substring(2,posSep).trim();
              final int pos1 = sDiv.indexOf(':');
              final String sLabel = pos1 >=0 ? sDiv.substring(0, pos1).trim() : sDiv;
              final int pos2 = sDiv.indexOf(',');
              //sDivText is the same as sLabel if pos1 <0 
              final String sDivText = pos2 >=0 ? sDiv.substring(pos1+1, pos2).trim() : sDiv.substring(pos1).trim(); 
              final String sSelect = pos2 >=0 ? sDiv.substring(pos2): "";
              favorTabInfo = null; //selectListOverview.get(sDiv);
              if(favorTabInfo == null){
                favorTabInfo = new FcmdFavorPathSelector.FavorTab(sLabel, sDivText);
                if(sSelect.indexOf('l')>=0){ favorTabInfo.mMainPanel |=1;}
                if(sSelect.indexOf('m')>=0){ favorTabInfo.mMainPanel |=2;}
                if(sSelect.indexOf('r')>=0){ favorTabInfo.mMainPanel |=4;}
                listAllFavorTabs.add(favorTabInfo);
              }
              ///
              /*
              if(sDiv.equals("left")){ list = panelLeft.selectListAllFavorites; }
              else if(sDiv.equals("mid")){ list = panelMid.selectListAllFavorites; }
              else if(sDiv.equals("right")){ list = panelRight.selectListAllFavorites; }
              else if(sDiv.equals("all")){ list = selectAll; }
              else { sError = "Error in cfg file: ==" + sDiv + "=="; }
              */
            } else if(favorTabInfo !=null){ 
              String[] sParts = sLine.trim().split(",");
              if(sParts.length < 2){ 
                sError = "SelectTab format error; " + sLine; 
              } else {
                FavorPath favorPathInfo = new FavorPath();
                //info. = sParts[0].trim();
                favorPathInfo.selectName = sParts[0].trim();
                favorPathInfo.path = sParts[1].trim();
                if(sParts.length >2){
                  final String actTabEntry = sParts[2].trim();
                  //final String actTab;
                  /*
                  final int posColon = actTabEntry.indexOf(':');
                  if(posColon >0){
                    String sPanelChars = actTabEntry.substring(0, posColon);
                    actTab = actTabEntry.substring(posColon+1).trim();
                    if(sPanelChars.indexOf('l')>=0){ info.mMainPanel |= 1; }  
                    if(sPanelChars.indexOf('m')>=0){ info.mMainPanel |= 2; }  
                    if(sPanelChars.indexOf('r')>=0){ info.mMainPanel |= 4; }  
                  } else {
                    actTab = actTabEntry.trim();
                  }
                  info.label = actTab;
                  */
                }
                //info.active = cActive;
                //list.add(info);
                favorTabInfo.favorPathInfo.add(favorPathInfo);
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
        for(FavorPath entry: listAllFavorPaths){ writeCfgLine(entry); }
        writerCfg.append("==left==\n");
        for(FavorPath entry: panelLeft.listAllFavorPaths){ writeCfgLine(entry); }
        writerCfg.append("==mid==\n");
        for(FavorPath entry: panelMid.listAllFavorPaths){ writeCfgLine(entry); }
        writerCfg.append("==right==\n");
        for(FavorPath entry: panelRight.listAllFavorPaths){ writeCfgLine(entry); }
      }
      catch(IOException exc){
        main.mainCmd.writeError("error writing" , exc);
      }
    }
    writerCfg.close();
  }
  
  
  
  private void writeCfgLine(FavorPath favorPathInfo) throws IOException
  {
    writerCfg.append(favorPathInfo.selectName).append(", ").append(favorPathInfo.path);
    /*
    if(info.label !=null && info.label.length()>0){
      writerCfg.append(", ");
      if((info.mMainPanel & 1)!=0){ writerCfg.append('l'); }
      if((info.mMainPanel & 2)!=0){ writerCfg.append('m'); }
      if((info.mMainPanel & 4)!=0){ writerCfg.append('r'); }
      if((info.mMainPanel & 7)!=0){ writerCfg.append(": "); }
      writerCfg.append(info.label);
    }
    */
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
  void buildTabFromSelection(FcmdFavorPathSelector.FavorPath info, GralTabbedPanel tabPanel)
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
  FcmdFileTable getLastFileTab()
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
          //check whether the selectInfo should be associated to a local list.
          FavorPath favorPathInfo = actFavorPathInfo; //(SelectInfo)selectedLine.getUserData();
          List<FcmdFavorPathSelector.FavorPath> listAdd = windAddFavorite.panelInvocation.listAllFavorPaths;
          int posInList = listAdd.indexOf(favorPathInfo); //position of last selection
          if(posInList == -1){
            //maybe selected one entry from the tab-special list or one entry of the common list.
            listAdd = listAllFavorPaths;
            posInList = listAdd.indexOf(favorPathInfo); //position of last selection
          } //add new SelectInfo after the current used.
          FavorPath favorite = new FavorPath();
          favorite.path = windAddFavorite.widgPath.getText();
          favorite.selectName = windAddFavorite.widgShortName.getText();
          String tablabel = windAddFavorite.widgTab.getText();
          //int ixtabName = windAddFavorite.panelInvocation.cNr - '1';
          favorite.mMainPanel = 1<< (windAddFavorite.panelInvocation.cNr - '1');
          //favorite.tabName[ixtabName] = 
          ///favorite.label = tablabel;
          listAdd.add(posInList+1, favorite);
          if(listAdd == listAllFavorPaths){
            panelLeft.fillInTables(1);
            panelMid.fillInTables(2);
            panelRight.fillInTables(3);
          } else {
            int where = windAddFavorite.panelInvocation.cNr - '0';  //"lmr"
            windAddFavorite.panelInvocation.fillInTables(where); //windAddFavorite.panelInvocation.cc);
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
  
  
}