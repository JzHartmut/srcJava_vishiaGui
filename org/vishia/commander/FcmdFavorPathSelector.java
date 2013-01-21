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

import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileRemote;
import org.vishia.util.FileWriter;
import org.vishia.util.KeyCode;
import org.vishia.util.StringPart;

/**This class implements the selection functionality of tabs and paths for the whole Java commander. 
 * It contains all actions which are instantiated one time but affects a special (the current) file panel.
 * */
class FcmdFavorPathSelector
{

  /**Version, history and license:
   * <ul>
   * <li>2012-03-09 Hartmut new menu entry menuBarFolderSync
   * <li>2012-02-04 Hartmut new menu entries for refresh and origin dir
   * </ul>
   * 
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 0x20120204;
  
  /**Entry in the favorite list. */
  static class FavorPath
  { /**The path of directory to select. */
    final String path;
    /**The name shown in the list. */
    final String selectName;
    /**The label on the tab in tabbed panel. */
    //String label;
    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
     * it means, a tab with the label will be created. */
    int mMainPanel;

    /**Origin dir adequate {@link #path}. It is null on initialization, but build on call of
     * {@link #getOriginDir()}. */
    private FileRemote dir;
    
    public FavorPath(String selectName, String path)
    { this.path = path;
      this.selectName = selectName;
    }

    
    /**Returns the dir instance for the origin path. The dir instance is built only one time
     * but only if it is necessary. It means it is built on the first call of this method.
     * @return
     */
    public FileRemote getOriginDir(){
      if(dir == null){ //build it only one time, but only if it is necessary.
        dir = new FileRemote(path);
      }
      return dir;
    }
    
    
    @Override public String toString(){ return path; } //for debug
  }
  
  
  static class FavorFolder
  {
    /**The label on the tab in tabbed panel. */
    final String label;
    /**The name shown in the list. */
    final String selectNameTab;
    /**The associated list of selectInfos. */
    final List<FavorPath> listfavorPaths = new LinkedList<FavorPath>();
    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
     * it means, a tab with the label will be created. */
    int mMainPanel;
    
    public FavorFolder(String label, String selectNameTab)
    { this.label = label;
      this.selectNameTab = selectNameTab;
    } 
 
    @Override public String toString(){ return label; } //for debug
    
  }
  
  /**All entries for the select list for all favorite path folders in the order of the configuration file. 
   * The elements of this list can be activated to present in cards with tabs. The cards contain the favor paths
   * which are contained in {@link FcmdFavorPathSelector.FavorFolder#listfavorPaths}.
   * But not all elements should be activated similar in all panels, it may be too much tabs per panel. 
   * */
  List<FcmdFavorPathSelector.FavorFolder> listAllFavorPathFolders = new LinkedList<FcmdFavorPathSelector.FavorFolder>();

  
  File fileCfg;
  
  /**Instance to write the cfgFile. */
  FileWriter writerCfg = new FileWriter();
  
  /**The three tabbed panels. */
  final FcmdLeftMidRightPanel panelLeft, panelMid, panelRight;
  
  GralFileSelector.WindowConfirmSearch windSearchFiles;
  
  private final GralMng mng;
  
  final Fcmd main;
  
  /**All entries which are shown in all three select lists. */
  List<FavorPath> listAllFavorPaths = new LinkedList<FavorPath>();
  
  /**For output messages. */
  final MainCmd_ifc console;
  
  
  /**The last selected SelectInfo. */
  FavorPath actFavorPathInfo;

  boolean bSyncMidRight;
  
  
  static class WindowConfirmAddFavorite
  {
    /**The window for confirming adding a new favorite. */
    GralWindow_ifc window;
    
    /**The panel from where the window was opened. It helps to get the current selected line in favorites. */
    FcmdLeftMidRightPanel panelInvocation;
  
    /**The short name input field in window confirm add favorite.  */
    GralTextField_ifc widgShortName;
    /**The tab input field in window confirm add favorite.  */
    GralTextField_ifc widgLabel;
    /**The path input field in window confirm add favorite.  */
    GralTextField_ifc widgPath;
    /**A field where "lmr" may be written to make the tab persistent.  */
    GralTextField_ifc widgPersistent;
  }
  
  WindowConfirmAddFavorite windAddFavorite = new WindowConfirmAddFavorite();
  
  FcmdFavorPathSelector(MainCmd_ifc console, Fcmd main)
  { this.main = main;
    this.console = console;
    this.mng = main.gralMng;
    panelLeft = new FcmdLeftMidRightPanel(main, 'l', '1', mng); 
    panelMid = new FcmdLeftMidRightPanel(main, 'm','2',  mng); 
    panelRight = new FcmdLeftMidRightPanel(main,'r', '3',  mng);

  }
  

  
  
  
  /**Builds the content of the add-favorite window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowAddFavorite(){ 

    windSearchFiles = GralFileSelector.createWindowConfirmSearchGthread(mng);
    
    //main.gui.addMenuItemGThread("menuFileNaviRefresh", main.idents.menuFileNaviRefreshBar, actionRefreshFileTable); // /
    //main.gui.addMenuItemGThread("menubarFolderCreate", main.idents.menuConfirmMkdirFileBar, main.mkCmd.actionOpenDialog); // /
    //main.gui.addMenuItemGThread("menubarFolderSearch", main.idents.menuBarSearchFiles, actionSearchFiles); // /
    main.gui.addMenuItemGThread("menuBarFolderSyncMidRight", main.idents.menuBarFolderSyncMidRight, actionSyncMidRight); // /
    //main.gui.addMenuItemGThread("menubarFileProps", main.idents.menuFilePropsBar, main.filePropsCmd.actionOpenDialog);
    //main.gui.addMenuItemGThread("test", main.idents.menuFileViewBar, main.viewCmd.actionOpenView);
    //main.gui.addMenuItemGThread("test", main.idents.menuFileEditBar, main.actionEdit);
    //main.gui.addMenuItemGThread("test", main.idents.menuBarEditIntern, main.editWind.actionOpenEdit);
    //main.gui.addMenuItemGThread("test", main.idents.menuConfirmCopyBar, main.copyCmd.actionConfirmCopy);
    //main.gui.addMenuItemGThread("test", main.idents.menuConfirmFileDelBar, main.deleteCmd.actionConfirmDelete);
    //main.gui.addMenuItemGThread("test", main.idents.menuExecuteBar, main.executer.actionExecuteFileByExtension);
    //main.gui.addMenuItemGThread("test", main.idents.menuExecuteCmdBar, main.cmdSelector.actionExecCmdWithFiles);

    main.gui.addMenuItemGThread("menuBarCreateFavor", main.idents.menuBarCreateFavor, actionCreateFavor); // /
    main.gui.addMenuItemGThread("menuDelTab", main.idents.menuDelTab, actionDelTab); // /
    main.gui.addMenuItemGThread("menuSaveFavoriteSel", main.idents.menuSaveFavoriteSel, actionSaveFavoritePathes); // /
    main.gui.addMenuItemGThread("menuReadFavoriteSel", main.idents.menuReadFavoriteSel, actionReadFavoritePathes); // /


    
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    windAddFavorite.window = main.gralMng.createWindow("addFavoriteWindow", "add favorite", GralWindow.windConcurrently);
        
    main.gralMng.setPosition(4, GralPos.size -4, 1, GralPos.size +34, 0, 'r');
    windAddFavorite.widgLabel = main.gralMng.addTextField("addFavoriteTab", true, "label", "t");
    windAddFavorite.widgLabel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.tab.");
    main.gralMng.setPosition(4, GralPos.size -4, 35, GralPos.size +10, 0, 'r');
    windAddFavorite.widgPersistent = main.gralMng.addTextField("addFavoriteTab", true, "lmr ?", "t");
    windAddFavorite.widgLabel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.persist.");
    
    main.gralMng.setPosition(8, GralPos.size -4, 1, GralPos.size +45, 0, 'd');
    windAddFavorite.widgShortName = main.gralMng.addTextField("addFavoriteAlias", true, "alias (show in list)", "t");
    windAddFavorite.widgShortName.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.alias.");
    windAddFavorite.widgPath = main.gralMng.addTextField("addFavoritePath", true, "the directory path", "t");
    windAddFavorite.widgPath.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.dir.");
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("addFavoriteEsc", actionAddFavorite, "esc", null, "esc");
    main.gralMng.setPosition(-4, -1, -14, GralPos.size +6, 0, 'r',1);
    GralWidget widg = main.gralMng.addButton("addFavoriteOk", actionAddFavorite, "temp", null, "temp");
    widg.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.temp.");
    widg = main.gralMng.addButton("addFavoriteOk", actionAddFavorite, "ok", null, "Save");
    widg.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.save.");
  
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
        listAllFavorPathFolders.clear();
        String sLine;
        int posSep;
        //List<FavorPath> list = null;
        FcmdFavorPathSelector.FavorFolder favorTabInfo = null;
        StringPart spLine = new StringPart();
        StringBuilder uLine = new StringBuilder(1000);
        //boolean bAll = true;
        while( sError == null && (sLine = reader.readLine()) !=null){
          if(sLine.contains("$")){
            uLine.append(sLine.trim());
            spLine.assignReplaceEnv(uLine);
            sLine = uLine.toString();
          } else {
            sLine = sLine.trim();
            spLine.assign(sLine);
          }
          if(sLine.length() >0){
            if( sLine.startsWith("==")){
              posSep = sLine.indexOf("==", 2);  
              //a new division
              final String sDiv = sLine.substring(2,posSep).trim();
              final int pos1 = sDiv.indexOf(':');
              final String sLabel = pos1 >=0 ? sDiv.substring(0, pos1).trim() : sDiv;
              final int pos2 = sDiv.indexOf(',');
              //sDivText is the same as sLabel if pos1 <0 
              final String sDivText = pos2 >=0 ? sDiv.substring(pos1+1, pos2).trim() : sDiv.substring(pos1+1).trim(); 
              final String sSelect = pos2 >=0 ? sDiv.substring(pos2): "";
              favorTabInfo = null; //selectListOverview.get(sDiv);
              if(favorTabInfo == null){
                favorTabInfo = new FcmdFavorPathSelector.FavorFolder(sLabel, sDivText);
                if(sSelect.indexOf('l')>=0){ favorTabInfo.mMainPanel |=1;}
                if(sSelect.indexOf('m')>=0){ favorTabInfo.mMainPanel |=2;}
                if(sSelect.indexOf('r')>=0){ favorTabInfo.mMainPanel |=4;}
                listAllFavorPathFolders.add(favorTabInfo);
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
                //info. = sParts[0].trim();
                String selectName = sParts[0].trim();
                String path = sParts[1].trim();
                FavorPath favorPathInfo = new FavorPath(selectName, path);
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
                favorTabInfo.listfavorPaths.add(favorPathInfo);
              }
            }
          }
          uLine.setLength(0);
        }//while
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
        for(FcmdFavorPathSelector.FavorFolder folder: listAllFavorPathFolders){
          writerCfg.append("==").append(folder.label).append(": ").append(folder.selectNameTab);
          if((folder.mMainPanel & 7)!=0){ writerCfg.append(", "); }
          if((folder.mMainPanel & 1)!=0){ writerCfg.append('l'); }
          if((folder.mMainPanel & 2)!=0){ writerCfg.append('m'); }
          if((folder.mMainPanel & 4)!=0){ writerCfg.append('r'); }
          writerCfg.append("==\n");
          for(FavorPath favor: folder.listfavorPaths){
            writerCfg.append(favor.selectName).append(", ").append(favor.path).append("\n");
          }
          writerCfg.append("\n");
          
        }
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
  
  
  

  private void setSortOrderFiles(char order){
    FcmdFileCard fileCard = main.getLastSelectedFileCard();
    if(fileCard !=null){
      fileCard.setSortOrder(order);
      fileCard.fillInCurrentDir();
    }
  }
  
  

  void stop(){}
  

  GralUserAction actionSearchFiles = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdFileCard fileCard = main.lastFavorCard.fileTable;
        windSearchFiles.confirmSearchInFiles(fileCard, main.gui.getOutputBox());
      }
      return true;
  } };

  
  
  GralUserAction actionSyncMidRight = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        bSyncMidRight = ! bSyncMidRight;
        if(bSyncMidRight){
          main.statusLine.widgSyncInfo.setBackgroundColor(GralColor.getColor("gn"));
          main.statusLine.widgSyncInfo.setText("sync mid-right");
        } else {
          main.statusLine.widgSyncInfo.setBackgroundColor(GralColor.getColor("wh"));
          main.statusLine.widgSyncInfo.setText("");
        }
      }
      return true;
  } };

  
  
  void confirmCreateNewFavor(){
    FcmdLeftMidRightPanel panel = main.lastFilePanels.get(0);
    FcmdFileCard fileCard = panel.actFileCard;
    windAddFavorite.panelInvocation = panel;
    windAddFavorite.widgLabel.setText(fileCard.label);
    windAddFavorite.widgShortName.setText("alias");
    File directory = fileCard.getCurrentDir();
    //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
    windAddFavorite.widgPath.setText(directory.getPath());
    windAddFavorite.window.setWindowVisible(true);
    
  }
  
  
  GralUserAction actionCreateFavor = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        confirmCreateNewFavor();
      }
    return true;
  } };
  
  
  
  
  GralUserAction actionDelTab = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(main.lastFavorCard !=null){
        FcmdFavorCard favorCard = main.lastFavorCard;
        FcmdFileCard fileCard = favorCard.fileTable;
        FcmdLeftMidRightPanel panel = fileCard.mainPanel;
        fileCard.remove();
        panel.actFileCard = null;
        panel.listTabs.remove(fileCard);
        String nameWidgFavorCard = FcmdWidgetNames.tabFavorites + fileCard.nameFilePanel;
        String nameWidgFileCard = FcmdWidgetNames.tabFile + fileCard.nameFilePanel;
        panel.tabbedPanelFavorCards.removePanel(nameWidgFavorCard);
        panel.tabbedPanelFileCards.removePanel(nameWidgFileCard);
        panel.cardFavorThemes.setFocus();
      }
      return true;
  } };
  
  
  /**Sets the origin dir of the last focused file table.
   * <br>
   * Implementation note: The last focused file tab is searched using {@link Fcmd#getLastSelectedFileCards()}.
   */
  GralUserAction actionSetDirOrigin = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        GralFileSelector lastTab = main.getLastSelectedFileCards()[0];
        if(lastTab !=null){
          lastTab.fillInOriginDir();
        } else {
          throw new IllegalArgumentException("last file tab not able to found");
        }
        return true;
      } else return false;
    }
  };
  
  
  
  /**Sets the origin dir of the last focused file table.
   * <br>
   * Implementation note: The last focused file tab is searched using {@link Fcmd#getLastSelectedFileCards()}.
   */
  GralUserAction actionRefreshFileTable = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      FcmdFileCard lastTab = main.getLastSelectedFileCards()[0];
      if(lastTab !=null){
        File dir = lastTab.getCurrentDir();
        if(dir !=null){
          lastTab.fillIn(dir, true);
        }
      } else {
        throw new IllegalArgumentException("last file tab not able to found");
      }
      return true;
      } else return false;
    }
  };
  
  
  
  GralUserAction actionAddFavorite = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(infos.sCmd.equals("ok") || infos.sCmd.equals("temp")){
          String path = windAddFavorite.widgPath.getText();
          String selectName = windAddFavorite.widgShortName.getText();
          FavorPath favorite = new FavorPath(selectName, path);
          String tablabel = windAddFavorite.widgLabel.getText();
          favorite.mMainPanel = 1<< (windAddFavorite.panelInvocation.cNr - '1');
          FcmdFavorPathSelector.FavorFolder tabDst = null;
          for(FcmdFavorPathSelector.FavorFolder tab:listAllFavorPathFolders){ //note: used break in loop
            if(tab.label.equals(tablabel)){
              tabDst = tab;
              break;
            }
          }
          if(tabDst == null){
            //its a new tab
            tabDst = new FcmdFavorPathSelector.FavorFolder(tablabel, tablabel);
            listAllFavorPathFolders.add(tabDst);
          }
          tabDst.listfavorPaths.add(favorite);
          windAddFavorite.panelInvocation.fillCards();
          ///
          if(infos.sCmd.equals("ok")){
            writeCfg(fileCfg);
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
  
  
  GralUserAction actionReadFavoritePathes = new GralUserAction()
  { @Override public boolean exec(int key, GralWidget_ifc widg, Object... params) { 
      readCfg(fileCfg);
      panelLeft.fillCards();
      panelMid.fillCards();
      panelRight.fillCards();
      return true;
  } };
  
  
  
  GralUserAction actionSortFilePerNameCase = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortName);
      return true;
  } };
  
  
  
  
  GralUserAction actionSortFilePerNameNonCase = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortNameNonCase);
      return true;
  } };
  
  
  
  
  GralUserAction actionSortFilePerExtensionCase = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortExtension);
      return true;
  } };
  
  
  GralUserAction actionSortFilePerExtensionNonCase = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortExtensionNonCase);
      return true;
  } };
  
  
  
  
  
  GralUserAction actionSortFilePerTimestamp = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortDateNewest);
      return true;
  } };

  
  
  
  GralUserAction actionSortFilePerTimestampOldestFirst = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortDateOldest);
      return true;
  } };

  
  
  
  
  
  GralUserAction actionSortFilePerLenghLargestFirst = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortSizeLargest);
      return true;
  } };

  
  
  
  GralUserAction actionSortFilePerLenghSmallestFirst = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortSizeSmallest);
      return true;
  } };

  
 
  
  
}