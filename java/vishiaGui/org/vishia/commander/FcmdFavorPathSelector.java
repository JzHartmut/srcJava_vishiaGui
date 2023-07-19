package org.vishia.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.FileAppend;
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
  
//  /**Entry in the favorite list. */
//  static class FavorPath
//  { /**The path of directory to select. */
//    final String path;
//    /**The name shown in the list. */
//    final String selectName;
//    
//    final FileCluster fileCluster;
//    /**The label on the tab in tabbed panel. */
//    //String label;
//    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
//     * it means, a tab with the label will be created. */
//    int mMainPanel;
//
//    /**Origin dir adequate {@link #path}. It is null on initialization, but build on call of
//     * {@link #getOriginDir()}. */
//    private FileRemote dir;
//    
//    public FavorPath(String selectName, String path, FileCluster fileCluster)
//    { this.fileCluster = fileCluster;
//      this.path = path;
//      this.selectName = selectName;
//    }
//
//    
//    /**Returns the dir instance for the origin path. The dir instance is built only one time
//     * but only if it is necessary. It means it is built on the first call of this method.
//     * @return
//     */
//    public FileRemote getOriginDir(){
//      if(dir == null){ //build it only one time, but only if it is necessary.
//        dir = fileCluster.getFile(path, null);  //new FileRemote(path);
//      }
//      return dir;
//    }
//    
//    
//    @Override public String toString(){ return path; } //for debug
//  }
  
  
  static class FavorFolder
  {
    /**The label on the tab in tabbed panel. */
    final String label;
    /**The name shown in the list. */
    final String selectNameTab;
    /**The associated list of selectInfos. */
    final List<GralFileSelector.FavorPath> listfavorPaths = new LinkedList<GralFileSelector.FavorPath>();
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
  FileAppend writerCfg = new FileAppend();
  
  /**The three tabbed panels. */
  final FcmdLeftMidRightPanel panelLeft, panelMid, panelRight;
  
  GralFileSelector.WindowConfirmSearch windSearchFiles;
  
  private final GralMng mng;
  
  final Fcmd main;
  
  /**All entries which are shown in all three select lists. */
  List<GralFileSelector.FavorPath> listAllFavorPaths = new LinkedList<GralFileSelector.FavorPath>();
  
  /**For output messages. */
  final LogMessage console;
  
  
  /**The last selected SelectInfo independent from the panel left, mid, right is never used. remove it. */
  //GralFileSelector.FavorPath actFavorPathInfo;

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
  
  FcmdFavorPathSelector(LogMessage console, Fcmd main)
  { this.main = main;
    this.console = console;
    this.mng = main.gui.gralMng;
    this.panelLeft = new FcmdLeftMidRightPanel(main, null, 'l', '1', this.mng); 
    this.panelMid = new FcmdLeftMidRightPanel(main, null, 'm','2',  this.mng); 
    this.panelRight = new FcmdLeftMidRightPanel(main, this.panelMid, 'r', '3',  this.mng);

  }
  

  void init() {
  }
  
  
  /**Builds the content of the add-favorite window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowAddFavorite(){ 
    assert(false);
    
    //main.gui.addMenuItemGThread("menuFileNaviRefresh", main.idents.menuFileNaviRefreshBar, actionRefreshFileTable); // /
    //main.gui.addMenuItemGThread("menubarFolderCreate", main.idents.menuConfirmMkdirFileBar, main.mkCmd.actionOpenDialog); // /
    //main.gui.addMenuItemGThread("menubarFolderSearch", main.idents.menuBarSearchFiles, actionSearchFiles); // /
    main.gui.menuBar.addMenuItem("menuBarFolderSyncMidRight", main.idents.menuBarFolderSyncMidRight, actionSyncMidRight); // /
    //main.gui.addMenuItemGThread("menubarFileProps", main.idents.menuFilePropsBar, main.filePropsCmd.actionOpenDialog);
    //main.gui.addMenuItemGThread("test", main.idents.menuFileViewBar, main.viewCmd.actionOpenView);
    //main.gui.addMenuItemGThread("test", main.idents.menuFileEditBar, main.actionEdit);
    //main.gui.addMenuItemGThread("test", main.idents.menuBarEditIntern, main.editWind.actionOpenEdit);
    //main.gui.addMenuItemGThread("test", main.idents.menuConfirmCopyBar, main.copyCmd.actionConfirmCopy);
    //main.gui.addMenuItemGThread("test", main.idents.menuConfirmFileDelBar, main.deleteCmd.actionConfirmDelete);
    //main.gui.addMenuItemGThread("test", main.idents.menuExecuteBar, main.executer.actionExecuteFileByExtension);
    //main.gui.addMenuItemGThread("test", main.idents.menuExecuteCmdBar, main.cmdSelector.actionExecCmdWithFiles);

    //main.gui.menuBar.addMenuItem("menuBarCreateFavor", main.idents.menuBarCreateFavor, actionCreateFavor); // /
    main.gui.menuBar.addMenuItem("menuDelTab", main.idents.menuDelTab, actionDelTab); // /
    main.gui.menuBar.addMenuItem("menuSaveFavoriteSel", main.idents.menuSaveFavoriteSel, actionSaveFavoritePathes); // /
    main.gui.menuBar.addMenuItem("menuReadFavoriteSel", main.idents.menuReadFavoriteSel, actionReadFavoritePathes); // /


    
    main.gui.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gui.gralMng.setPosition(-19, 0, -47, 0, 'r'); //right buttom, about half less display width and hight.
    

    windAddFavorite.window = main.gui.gralMng.createWindow("addFavoriteWindow", "add favorite", GralWindow.windConcurrently);
        
    main.gui.gralMng.setPosition(4, GralPos.size -4, 1, GralPos.size +34, 'r');
    windAddFavorite.widgLabel = main.gui.gralMng.addTextField("addFavoriteTab", true, "label", "t");
    windAddFavorite.widgLabel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.tab.");
    main.gui.gralMng.setPosition(4, GralPos.size -4, 35, GralPos.size +10, 'r');
    windAddFavorite.widgPersistent = main.gui.gralMng.addTextField("addFavoriteTab", true, "lmr ?", "t");
    windAddFavorite.widgLabel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.persist.");
    
    main.gui.gralMng.setPosition(8, GralPos.size -4, 1, GralPos.size +45, 'd');
    windAddFavorite.widgShortName = main.gui.gralMng.addTextField("addFavoriteAlias", true, "alias (show in list)", "t");
    windAddFavorite.widgShortName.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.alias.");
    windAddFavorite.widgPath = main.gui.gralMng.addTextField("addFavoritePath", true, "the directory path", "t");
    windAddFavorite.widgPath.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.dir.");
    
    main.gui.gralMng.setPosition(-4, -1, 1, 6, 'r');
    main.gui.gralMng.addButton("addFavoriteEsc", actionAddFavorite, "esc", null, "esc");
    main.gui.gralMng.setPosition(-4, -1, -14, GralPos.size +6, 'r',1);
    GralWidget widg = main.gui.gralMng.addButton("addFavoriteOk", actionAddFavorite, "temp", null, "temp");
    widg.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorNew.temp.");
    widg = main.gui.gralMng.addButton("addFavoriteOk", actionAddFavorite, "ok", null, "Save");
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
        //List<GralFileSelector.FavorPath> list = null;
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
                GralFileSelector.FavorPath favorPathInfo = new GralFileSelector.FavorPath(selectName, path, main.fileCluster);
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
        spLine.close();
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
          main.log.writeError("can't delete " + cfgFileOld.getAbsolutePath());
        }
      }
      if(bOk){
        bOk = cfgFile.renameTo(cfgFileOld);
        if(!bOk){
          main.log.writeError("can't rename " + cfgFile.getAbsolutePath());
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
          for(GralFileSelector.FavorPath favor: folder.listfavorPaths){
            writerCfg.append(favor.selectName).append(", ").append(favor.path).append("\n");
          }
          writerCfg.append("\n");
          
        }
      }
      catch(IOException exc){
        main.log.writeError("error writing" , exc);
      }
    }
    writerCfg.close();
  }
  
  
  
  private void XXXwriteCfgLine(GralFileSelector.FavorPath favorPathInfo) throws IOException
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
  void buildTabFromSelection(GralFileSelector.FavorPath info, GralPanelContent tabPanel)
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
  
  
  

  protected void setSortOrderFiles(char order){
    FcmdFileCard fileCard = main.getLastSelectedFileCard();
    if(fileCard !=null){
      fileCard.setSortOrder(order);
      fileCard.fillInCurrentDir();
    }
  }
  
  

  void stop(){}
  

  GralUserAction actionSearchFiles = new GralUserAction("actionSearchFiles"){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdFileCard fileCard = main.currentFileCard;
        windSearchFiles.confirmSearchInFiles(fileCard, main.gui.getOutputBox());
      }
      return true;
  } };

  
  void actionSyncMidRight() {
    if(!this.bSyncMidRight && this.panelMid.actFileCard !=null /*&& this.panelMid.actFileCard.favorPathInfo !=null */ 
        && this.panelRight.actFileCard !=null /*&& this.panelRight.actFileCard.favorPathInfo !=null */) {
      this.bSyncMidRight = true;
      this.panelRight.actFileCard.syncTabSelection = this.panelMid.actFileCard.syncPartnerTabSelection = this.panelRight.actFileCard.getLabelCurrFavor();
      this.panelMid.actFileCard.syncTabSelection = this.panelRight.actFileCard.syncPartnerTabSelection = this.panelMid.actFileCard.getLabelCurrFavor();
      this.main.statusLine.widgSyncInfoLeft.setBackColor(GralColor.getColor("gn"),0);
      this.main.statusLine.widgSyncInfoRight.setBackColor(GralColor.getColor("gn"),0);
      this.main.statusLine.widgSyncInfoLeft.setText(this.panelMid.actFileCard.syncTabSelection);
      this.main.statusLine.widgSyncInfoRight.setText(this.panelRight.actFileCard.syncTabSelection);
    } else {
      this.bSyncMidRight = false;
      if(this.panelMid.actFileCard !=null) { this.panelMid.actFileCard.syncTabSelection =  this.panelMid.actFileCard.syncPartnerTabSelection = null; }
      if(this.panelRight.actFileCard !=null) { this.panelRight.actFileCard.syncTabSelection = this.panelRight.actFileCard.syncPartnerTabSelection = null; }
      this.main.statusLine.widgSyncInfoLeft.setBackColor(GralColor.getColor("wh"),0);
      this.main.statusLine.widgSyncInfoRight.setBackColor(GralColor.getColor("wh"),0);
      this.main.statusLine.widgSyncInfoLeft.setText("");
      this.main.statusLine.widgSyncInfoRight.setText("");
    }
    
  }
  
  
  GralUserAction actionSyncMidRight = new GralUserAction(""){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        actionSyncMidRight();
      }
      return true;
  } };

  
  
//  void confirmCreateNewFavor(){
//    FcmdLeftMidRightPanel panel = main.lastFilePanels.get(0);
//    FcmdFileCard fileCard = panel.actFileCard;
//    windAddFavorite.panelInvocation = panel;
//    windAddFavorite.widgLabel.setText(fileCard.label);
//    windAddFavorite.widgShortName.setText("alias");
//    File directory = fileCard.getCurrentDir();
//    //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
//    windAddFavorite.widgPath.setText(directory.getPath());
//    windAddFavorite.window.setFocus(); //WindowVisible(true);
//    
//  }
//  
//  
//  GralUserAction actionCreateFavor = new GralUserAction(""){
//    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
//      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
//        confirmCreateNewFavor();
//      }
//    return true;
//  } };
//  
  
  
  
  GralUserAction actionDelTab = new GralUserAction(""){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){
      if(main.currentFileCard !=null){
        //FcmdFavorCard favorCard = main.lastFavorCard;
        FcmdFileCard fileCard = main.currentFileCard; //favorCard.fileTable;
        FcmdLeftMidRightPanel panel = fileCard.mainPanel;
        if(fileCard !=null){
          fileCard.remove();
          panel.actFileCard = null;
          panel.listTabs.remove(fileCard);
        }
        String nameWidgFavorCard = FcmdWidgetNames.tabFavorites + fileCard.nameFilePanel;
        String nameWidgFileCard = FcmdWidgetNames.tabFile + fileCard.nameFilePanel;
        //panel.tabbedPanelFavorCards.removeWidget(nameWidgFavorCard);
        panel.tabbedPanelFileCards.removeWidget(nameWidgFileCard);
        panel.cardFavorThemes.setFocus();
      }
      return true;
  } };
  
  
  /**Sets the origin dir of the last focused file table.
   * <br>
   * Implementation note: The last focused file tab is searched using {@link Fcmd#getLastSelectedFileCards()}.
   */
  GralUserAction actionSetDirOrigin = new GralUserAction(""){
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
  GralUserAction actionRefreshFileTable = new GralUserAction(""){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      FcmdFileCard lastTab = main.getLastSelectedFileCards()[0];
      if(lastTab !=null){
        FileRemote dir = lastTab.getCurrentDir();
        if(dir !=null){
          lastTab.forcefillIn(dir, false);
        }
      } else {
        System.out.println("No filetable found to refresh");
        //throw new IllegalArgumentException("last file tab not able to found");
      }
      return true;
      } else return false;
    }
  };
  
  

  
  

  
  
  
  GralUserAction actionAddFavorite = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(infos.sCmd.equals("ok") || infos.sCmd.equals("temp")){
          String path = windAddFavorite.widgPath.getText();
          String selectName = windAddFavorite.widgShortName.getText();
          GralFileSelector.FavorPath favorite = new GralFileSelector.FavorPath(selectName, path, main.fileCluster);
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
        windAddFavorite.window.setVisible(false);
      }
      return true;
    }
  };

  

  
  GralUserAction actionSaveFavoritePathes = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { writeCfg(fileCfg);
      return true;
  } };
  
  
  GralUserAction actionReadFavoritePathes = new GralUserAction("")
  { @Override public boolean exec(int key, GralWidget_ifc widg, Object... params) { 
      readCfg(fileCfg);
      panelLeft.fillCards();
      panelMid.fillCards();
      panelRight.fillCards();
      return true;
  } };
  
  
  
  /**Sort action. Note that the sort action is contained in the {@link GralFileSelector}
   * already. But this sort action is available from menu of the Fcmd too. It is some different.
   */
  GralUserAction actionSortFilePerNameCase = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortName);
      return true;
  } };
  
  
  
  
  GralUserAction actionSortFilePerNameNonCase = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortNameNonCase);
      return true;
  } };
  
  
  
  
  GralUserAction actionSortFilePerExtensionCase = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortExtension);
      return true;
  } };
  
  
  GralUserAction actionSortFilePerExtensionNonCase = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortExtensionNonCase);
      return true;
  } };
  
  
  
  
  
  GralUserAction actionSortFilePerTimestamp = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortDateNewest);
      return true;
  } };

  
  
  
  GralUserAction actionSortFilePerTimestampOldestFirst = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortDateOldest);
      return true;
  } };

  
  
  
  
  
  GralUserAction actionSortFilePerLenghLargestFirst = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortSizeLargest);
      return true;
  } };

  
  
  
  GralUserAction actionSortFilePerLenghSmallestFirst = new GralUserAction("")
  { @Override public boolean userActionGui(int key, GralWidget infos, Object... params) { 
      setSortOrderFiles(GralFileSelector.Constants.kSortSizeSmallest);
      return true;
  } };

  

  /**This cleans all in the {@link FileCluster} and rereads and recreates all files from the file system.  
   * TODO It is too much for only deleselect! TODO Should be part of FileRemote, not in this application.
   */
  GralUserAction actionDeselectDirtree = new GralUserAction("actionDeselectDirtree")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      FcmdFileCard fileCard = main.getLastSelectedFileCard();
      FileRemote[] lastFiles = main.getLastSelectedFiles(true, 1);
      //if(fileCard !=null){
      if(lastFiles[0] !=null){
        FileCluster fc = lastFiles[0].itsCluster;
        String sStartDir = lastFiles[0].getCanonicalPath();
        Iterator<FileRemote> it = fc.listSubdirs(sStartDir);
        while(it.hasNext()){
          FileRemote dir1 = it.next();
          if(!dir1.getCanonicalPath().startsWith(sStartDir)) { break; }
          it.remove();
        }
        lastFiles[0].resetMarkedRecurs(0xffffffff, null);
        main.refreshFilePanel(lastFiles[0].getParentFile());
        //fileCard.f  //TODO refresh
      }
      return true;
  } };
  
  
  

  /**TODO similar {@link #actionDeselectDirtree}
   * 
   */
  GralUserAction actionCleanFileRemote = new GralUserAction("actionCleanFileRemote")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      FcmdFileCard fileCard = main.getLastSelectedFileCard();
      FileRemote[] lastFiles = main.getLastSelectedFiles(true, 1);
      //if(fileCard !=null){
      if(lastFiles[0] !=null){
        FileCluster fc = lastFiles[0].itsCluster;
        String sStartDir = lastFiles[0].getCanonicalPath();
        Iterator<FileRemote> it = fc.listSubdirs(sStartDir);
        while(it.hasNext()){
          FileRemote dir1 = it.next();
          if(!dir1.getCanonicalPath().startsWith(sStartDir)) { break; }
          it.remove();
        }
        lastFiles[0].resetMarkedRecurs(0xffffffff, null);
        lastFiles[0].cleanChildren();
        main.refreshFilePanel(lastFiles[0].getParentFile());
        //fileCard.f  //TODO refresh
      }
      return true;
  } };
  
  
  
  
}