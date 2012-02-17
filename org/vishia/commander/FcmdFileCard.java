package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.GralFileSelector;

import org.vishia.util.FileCompare;
import org.vishia.util.FileRemote;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This is one file table in the Java commander. Each main panel (left, middle, right)
 * has maybe more as one tabs, each tab has exactly one file table. The file table is reused
 * for the several tabs of the main panel, and they are reused too if the directory is changed.
 * @author Hartmut Schorrig
 *
 */
public class FcmdFileCard extends GralFileSelector
{
  /**Version and History
   * <ul>
   * <li>2012-02-04 Hartmut new: {@link #searchCompareResult(File)} supports working with
   *   comparison result, used to set equal if a file was copied.
   * </ul>
   * 
   */
  public static final int version = 0x20120204;
  
  /**Table widget for the select table of the file tab.*/
  FcmdFavorCard favorCard;

  /**The component */
  final Fcmd main;
  
  /**The left, mid or right main panel where this tabbed file table is associated. */
  final FcmdLeftMidRightPanel mainPanel;
  
  GralColor[] colorSelectFocused123 = new GralColor[3];
  
  /**The organization unit for this FileSelector. */
  //final LeftMidRightPanel.FileTabs fileTabs;
  
  /**The search-name of the tabbed file panel where this Table is placed on. 
   * It is the visible label of the tab, following by ".1" till ".3" for the three panels. */
  final String nameFilePanel;
  
  /**The label which is written in the line of favor file after l:label m:label r:label
   * It is the label on the tab.
   */
  final String label;
  
  final GralTextField_ifc widgLabel; /// 
  
  /**Association to the current used favor path selection.
   * Note that this instance is re-used for more as one selection.
   */
  FcmdFavorPathSelector.FavorPath favorPathInfo;
  
  FileRemote currentFile;
  
  /**If not null, then should synchronize with this file card. */
  FcmdFileCard otherFileCardtoSync;  
  
  /**If not null, then it is the base dir for synchronization with the {@link #otherFileCardtoSync}. 
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  String sDirSync;
  
  /**length of sDirSync or -1
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  int zDirSync;
  
  String sLocalpath, sLocaldir;
  
  
  final DateFormat formatDateInfo = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
  
  /**Creates the cards with tabs for the files and for the favorite paths.
   * This ctor will be called in the graphic thread. Therefore it can initialize the graphic 
   * for the fileCard and for the associated favor card in this code.
   * @param mainPanelP The left, mid or right panel where this cards are assigned to
   * @param label The label of the tab, it builds the name of all widgets.
   */
  FcmdFileCard(FcmdLeftMidRightPanel mainPanelP, String label){
    super();
    this.label = label;
    this.main = mainPanelP.main;
    this.mainPanel = mainPanelP;
    this.nameFilePanel = label+ "." + mainPanelP.cNr;
    this.colorSelectFocused123[0] = GralColor.getColor("gn");
    this.colorSelectFocused123[1] = GralColor.getColor("lbl");
    this.colorSelectFocused123[2] = GralColor.getColor("lgr");
    String namePanelFile = FcmdWidgetNames.tableFile + nameFilePanel;
    main.idxFileSelector.put(namePanelFile, this); //it is WidgetNames.tableFile + label +.123, see super(...) 
    GralWidgetMng mng = main.gralMng;
    //
    //The favorite paths card
    favorCard = new FcmdFavorCard(main, this, mainPanel);
    String nameTableSelection = FcmdWidgetNames.tableFavorites + nameFilePanel;
    GralPanelContent panelFavors = mainPanel.tabbedPanelFavorCards.addGridPanel(FcmdWidgetNames.tabFavorites + nameFilePanel, label,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');  
    favorCard.setToPanel(mng, nameTableSelection, 5, mainPanel.widthSelecttableSub, 'A');
    favorCard.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorSelect.");
    panelFavors.setPrimaryWidget(favorCard.wdgdTable);
    //
    //The files card
    GralPanelContent panelFiles = mainPanel.tabbedPanelFileCards.addGridPanel(FcmdWidgetNames.tabFile + nameFilePanel, label,1,1,10,10);
    //to show the properties of the selected file in the info line:
    //
    //sets this Widget to the selected panel, it is the grid panel which was created even yet.
    mng.setPosition(0, 2, 0, 0, 1, 'd');
    String nameWidgLabel = FcmdWidgetNames.labelWidgFile + nameFilePanel;
    widgLabel = mng.addTextField(nameWidgLabel, false, null, null);
    mng.setPosition(2, 0, 0, 0, 1, 'd');
    //set the base class GralFileSelector to the panel. It contains the path and the table for file selection.
    setToPanel(mng, namePanelFile, 5, new int[]{2,20,5,10}, 'A');
    //GralPos.Coordinate[] columns = new GralPos.Coordinate[4];
    //Sets the columns for the table.
    super.selectList.wdgdTable.setColumnWidth(50, new int[]{2,0,-5,-10});
    super.selectList.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.fileSelect.");
    GralMenu menuFolder = super.widgdPath.getContextMenu();
    menuFolder.addMenuItemGthread("contextfolder-setOrigin", main.idents.menuFileNaviOriginDirContext, main.favorPathSelector.actionSetDirOrigin);
    menuFolder.addMenuItemGthread("menuContextCreateFavor", main.idents.menuContextCreateFavor, main.favorPathSelector.actionCreateFavor);
    menuFolder.addMenuItemGthread("context-filescp", main.idents.menuFilesCpContext, main.filesCp.actionConfirmCp);
    menuFolder.addMenuItemGthread("contextfolder-create", main.idents.menuConfirmMkDirFileContext, main.mkCmd.actionOpenDialog);
    menuFolder.addMenuItemGthread("contextfolder-search", main.idents.menuContextSearchFiles, main.favorPathSelector.actionSearchFiles);
    menuFolder.addMenuItemGthread("contextfolder-refresh", main.idents.menuFileNaviRefreshContext, main.favorPathSelector.actionRefreshFileTable);
    panelFiles.setPrimaryWidget(super.selectList.wdgdTable);
    //
    //sets the action for a simple table: what to do on line selected: Show file names. 
    this.setActionOnFileSelected(actionOnFileSelection);
    selectList.wdgdTable.setActionFocused(actionFocused);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFilePropsContext, main.filePropsCmd.actionOpenDialog);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFileViewContext, main.viewCmd.actionOpenView);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFileEditContext, main.actionEdit);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuConfirmCopyContext, main.copyCmd.actionConfirmCopy);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuConfirmMoveContext, main.copyCmd.actionConfirmCopy);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuConfirmFileDelContext, main.deleteCmd.actionConfirmDelete);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuExecuteContext, main.executer.actionExecuteFileByExtension);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuExecuteCmdContext, main.cmdSelector.actionExecCmdWithFiles);
    favorCard.wdgdTable.setActionOnLineSelected(favorCard.actionFavorSelected);
    //
    //sets the action for Select a file: open the execute menu
    setActionOnEnterFile(main.executer.actionOnEnterFile);
    setActionSetFileLineAttrib(actionSetFileLineAttrib);
  }

  
  private void buildGraphic(){
    //see ctor
  }
  
  
  void setNewContent(FcmdFavorPathSelector.FavorPath favorPathInfoP, File dir){
    favorPathInfo = favorPathInfoP;
    favorCard.add(favorPathInfo);  //only it is a new one, it will be checked.
    setOriginDir(favorPathInfo.getOriginDir());
    fillIn(dir);
    widgLabel.setText(favorPathInfo.selectName);
    setFocus();

  }
  
  
  
  
  /**Overrides the {@link GralFileSelector#setFocus()} and calls him, before that sets the color
   * of the current line of table of all 3 current file panels to the 3-stage color
   * to see which table has the focus. 
   */
  @Override public boolean setFocus(){ 
    setActFilePanel_setColorCurrLine();
    return super.setFocus(); 
  }
  
  
  
  /**Removes this file card with its widgets and data. It is 'close tab'. */
  @Override public boolean remove(){
    favorCard.remove();
    favorCard = null;
    favorPathInfo = null;
    currentFile = null;
    return super.remove();
  }
 
  
  
  /**Searches whether the given file has a comparison result in this file card.
   * That method is used to present the file in the table with comparison result information
   * and to change the comparison result if the file was copied.
   * @param file The file, usual selected in the file table
   * @return null a comparison result is not existed, elsewhere the result.
   */
  FileCompare.Result searchCompareResult(File file){
    ///
    final FileCompare.Result result;
    if(sDirSync !=null){
      zDirSync = sDirSync.length();
      String sPath = file.getAbsolutePath();
      if(sPath.startsWith(sDirSync)){
        String sLocalPath = sPath.substring(sDirSync.length()+1);
        result = main.filesCp.idxFilepath4Result.get(sLocalPath);
      } else {
        result = null;  //outside of sDirSync
      }
    } else {
      zDirSync = -1;
      result = null;  //no comparison active
    }
    return result;
  }
  
  
  
  
  @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
  { boolean ret = true;
    FileRemote data = (FileRemote)oData;
    switch(keyCode){
    //case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{data}, "ordersBackground"); break;
    default: ret = false;
    }
    /*
    if (keyCode == main.keyActions.keyCreateFavorite){
      main.favorPathSelector.windAddFavorite.panelInvocation = mainPanel;
      main.favorPathSelector.windAddFavorite.widgLabel.setText(nameFilePanel);
      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
      FileRemote lastSelectedFile = getSelectedFile();
      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
      main.favorPathSelector.windAddFavorite.widgPath.setText(lastSelectedFile.getParent());
      main.favorPathSelector.windAddFavorite.window.setWindowVisible(true);
    } else*/ 
    if (keyCode == main.keyActions.keyPanelSelection){
      //focuses the panel which is the selection panel for this file table.
      GralWidget tableSelection = main.gralMng.getWidget(FcmdWidgetNames.tableFavorites + nameFilePanel);
      tableSelection.setFocus();
    } else if (keyCode == main.keyActions.keyPanelLeft){
      //sets focus to left
      FcmdFileCard fileTableLeft = null;
      boolean found = false;
      for(FcmdFileCard fileTable: mainPanel.listTabs){
        if(fileTable == this){ found = true;  break;}
        fileTableLeft = fileTable;  //save this table as table left, use if found.
      }
      if(found){
        if(fileTableLeft !=null){
          fileTableLeft.setFocus();
        } else {  //left from first is the selectAllTable of this panel.
          //panel.selectTableAll.wdgdTable.setFocus();
        }
      }
    } else if (keyCode == main.keyActions.keyPanelRight){
      //sets focus to right
      FcmdFileCard fileTableRight = null;
      boolean found = false; //(mainPanel.selectTableAll == this);
      for(FcmdFileCard fileTable: mainPanel.listTabs){
        if(found){ fileTableRight = fileTable; break; }  //use this next table if found before.
        if(fileTable == this) { found = true; }
      }
      if(fileTableRight !=null){
        fileTableRight.setFocus();
      }
      
    } else if (keyCode == main.keyActions.keyMainPanelLeft){
      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelRight ?
          main.favorPathSelector.panelMid : main.favorPathSelector.panelLeft;
      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.setFocus(); }
    } else if (keyCode == main.keyActions.keyMainPanelRight){
      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelLeft ?
          main.favorPathSelector.panelMid : main.favorPathSelector.panelRight;
      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.setFocus(); }
    } else {
      ret = false;
    }
    return ret;
  }

  
  
  /**This routine is invoked from {@link #actionOnFileSelection} action listener whenever a file in any file card
   * will be selected (key up, down, mouse click etc.).
   * The routine writes infos about the file and may synchronize with another file card.
   * @param file The currently selected file.
   */
  private void actionOnFileSelection(FileRemote file){
    //note the file, able to use for some actions.
    currentFile = file;
    main.currentFile = file;
    main.selectedFiles123[mainPanel.ixMainPanel] = file;
    //note the file card in order of usage.
    main.lastFavorCard = favorCard;
    mainPanel.actFileCard = this;
    long lastModified = file.lastModified();
    String sDate = formatDateInfo.format(new Date(lastModified));
    String sLenShort = //String.format("", file.length)
      file.length() >= 1000000 ? String.format("%2.1f MByte", file.length()/1000000.0) :
      file.length() >=    1000 ? String.format("%2.1f kByte", file.length()/1000.0) :
      String.format("%3d Byte", file.length());  
    String info = sDate + " = " + lastModified + ", length= " + sLenShort;        
    main.widgFileInfo.setText(info);
    String sPath = file.getAbsolutePath();
    main.widgFilePath.setText(sPath);
    boolean bSync = main.filesCp.widgSyncWalk.isOn()
      && sDirSync !=null && sPath.length() >= zDirSync;
    if(bSync){
      sLocalpath = sPath.substring(zDirSync);
      String sDir = getCurrentDirPath();
      bSync = sDir.length() >= zDirSync;
      if(bSync){
        sLocaldir = sDir.substring(zDirSync);
        String sDirOtherSet = otherFileCardtoSync.sDirSync + sLocaldir;
        String sDirOtherAct = otherFileCardtoSync.getCurrentDirPath();
        if(!sDirOtherSet.equals(sDirOtherAct)){
          //bSync = false;
          otherFileCardtoSync.fillIn(new FileRemote(sDirOtherSet));
        }
        String fileName = file.getName();
        if(fileName.endsWith(".~1~")){
          fileName = fileName.substring(0, fileName.length() -4);
        }
        otherFileCardtoSync.selectFile(fileName);
      }
    }
  }
  
  
  
  
  
  /**Sets the panel which contains this File card as actual, adjust the order of actual file panels
   * and sets the color of the current line of table of all 3 current file panels to the 3-stage color
   * to see which table has the focus. 
   * Sets {@link Fcmd#lastFavorCard}, {@link FcmdLeftMidRightPanel#actFileCard}, 
   * {@link Fcmd#lastFilePanels}. 
   */
  private void setActFilePanel_setColorCurrLine(){
    main.lastFavorCard = favorCard;
    mainPanel.actFileCard = FcmdFileCard.this;
    if(main.lastFilePanels.size() == 0 || main.lastFilePanels.get(0) != mainPanel){
      main.lastFilePanels.remove(mainPanel);  //if it is in list on higher position
      main.lastFilePanels.add(0, mainPanel);
    }
    int ixMainPanel = -1;
    for(FcmdLeftMidRightPanel panel: main.lastFilePanels){
      if(panel.actFileCard !=null){
        panel.actFileCard.selectList.wdgdTable.setColorCurrLine(colorSelectFocused123[++ixMainPanel]);
      }
    }
  }
  
  
  
  
  /**Action to show the file properties in the info line. This action is called anytime if a line
   * was changed in the file view table. */
  GralUserAction actionOnFileSelection = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      GralTableLine_ifc line = (GralTableLine_ifc) params[0];
      Object oData = line.getUserData();
      if(oData instanceof FileRemote){
        actionOnFileSelection((FileRemote)oData);
      }
      return true;
    }
  };
  
  
  GralUserAction actionSetFileLineAttrib = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      //check whether any of the 2 compare directories are base for the current file:
      try{
        GralTableLine_ifc line = (GralTableLine_ifc)(params[0]);
        File file = (File)line.getUserData();
        FileCompare.Result result = searchCompareResult(file);
        if(result !=null){
          if(!result.equal){ line.setCellText("#", 0); }
          else if(result.alone){ line.setCellText("+", 0); }
          else if(result.missingFiles){ line.setCellText("-", 0); }
        }
      } catch(Exception exc){
        main.gralMng.log.sendMsg(0, "Exception in FcmdFileCard.actionSetFileLineAttrib"); 
      }
      return true;
  } };  
  
  
  /**This action is bound in the File selection table. If it is focused, the current file tables
   * of the other file panels will gotten the {@link #colorSelectNonFocused} to show that are not
   * the first one. The file table of this is set with the {@link #colorSelectFocused}.
   * Twice the {@link Fcmd#lastFilePanels} list is ordered with this panel as first one. 
   * 
   */
  GralUserAction actionFocused = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      setActFilePanel_setColorCurrLine();
      return true;      
  } };
  
  
  @Override public String toString(){ return label + "/" + nameFilePanel; }
  
  
}
