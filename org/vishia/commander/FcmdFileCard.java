package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.GralFileSelector;

import org.vishia.util.FileCompare;
import org.vishia.util.FileRemote;

/**This is one file table in the Java commander. Each main panel (left, middle, right)
 * has maybe more as one tabs, each tab has exactly one file table. The file table is reused
 * for the several tabs of the main panel, and they are reused too if the directory is changed.
 * @author Hartmut Schorrig
 *
 */
public class FcmdFileCard extends GralFileSelector
{
  /**Version, history and license
   * <ul>
   * <li>2012-03-09 Hartmut new: Now the synchronization between 2 panels works independent of
   *   the comparison with a improved algorithm. 
   * <li>2012-02-04 Hartmut new: {@link #searchCompareResult(File)} supports working with
   *   comparison result, used to set equal if a file was copied.
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
   * 
   * 
   */
  public static final int version = 0x20120309;
  
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
  
  /**The last selected file. */
  File currentFile;
  
  /**If not null, then should synchronize with this file card. */
  FcmdFileCard otherFileCardtoSync;  
  
  /**If not null, then it is the base dir for synchronization with the {@link #otherFileCardtoSync}. 
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  String sDirSync;
  
  /**length of sDirSync or -1
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  int zDirSync;
  
  String sLocalpath, sLocaldir;
  
  
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
    setToPanel(mng, namePanelFile, 5, new int[]{2,19,6,10}, 'A');
    //GralPos.Coordinate[] columns = new GralPos.Coordinate[4];
    //Sets the columns for the table.
    super.selectList.wdgdTable.setColumnWidth(50, new int[]{2,0,-6,-11});
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
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortNameCase, main.favorPathSelector.actionSortFilePerNameCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortNameNonCase, main.favorPathSelector.actionSortFilePerNameNonCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortExtCase, main.favorPathSelector.actionSortFilePerExtensionCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortExtNonCase, main.favorPathSelector.actionSortFilePerExtensionNonCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortOldest, main.favorPathSelector.actionSortFilePerTimestampOldestFirst);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortDateNewest, main.favorPathSelector.actionSortFilePerTimestamp);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortSizeLarge, main.favorPathSelector.actionSortFilePerLenghLargestFirst);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortSizeSmall, main.favorPathSelector.actionSortFilePerLenghSmallestFirst);

    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", main.idents.menuContextFileSortExtCase, main.favorPathSelector.actionSortFilePerExtensionCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFilePropsContext, main.filePropsCmd.actionOpenDialog);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFileViewContext, main.viewCmd.actionOpenView);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuContextEditIntern, main.editWind.actionOpenEdit);
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
    //FileRemote data = (FileRemote)oData;
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
   * @param sFileName Text in the cell, especially ".." for the parent dir entry.
   */
  private void actionOnFileSelection(File file, String sFileName){
    //note the file, able to use for some actions.
    currentFile = file;
    main.currentFile = file;
    main.selectedFiles123[mainPanel.ixMainPanel] = file;
    
    if(mainPanel.orderMainPanel == 1){
      //only if it is the focused panel:
      //note the file card in order of usage.
      
      main.lastFavorCard = favorCard;
      mainPanel.actFileCard = this;
      main.statusLine.setFileInfo(file);
      
      String sPath = file.getAbsolutePath();
      if(main.favorPathSelector.bSyncMidRight){
        syncWithSecondPanel(sFileName);
      }
      /*
      else {
        
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
      */
    }
  }
  
  
  
  void syncWithSecondPanel(String sFileName){
    //String fileName = currentFile.getName();
    System.out.println("FcmdFileCard " + mainPanel.cc + ":" + sFileName);
    FcmdFileCard otherFileCard;
    if(mainPanel.cc == 'm'){ otherFileCard = main.favorPathSelector.panelRight.actFileCard; }
    else if(mainPanel.cc == 'm'){ otherFileCard = main.favorPathSelector.panelMid.actFileCard;  }
    else { otherFileCard = null; }
    if(otherFileCard !=null){  //NOTE: though mid and right is selected, the otherFileCard may be null because no tab is open.
      String sDirName = getCurrentDir().getName();
      //check whether the other file card contains a entry with this directory name
      GralTableLine_ifc line = otherFileCard.selectList.wdgdTable.getLine(sDirName);
      if(line !=null){
        File dir = (File)line.getUserData();
        otherFileCard.fillIn(dir);    //use that directory.
      }
      boolean bSameFile = otherFileCard.selectFile(sFileName);  //".." also
      if(!bSameFile){
        //check whether the file is a directory and it is the directory of the other panel:
        boolean bToRoot = false;
        if(currentFile.isDirectory()){
          File otherDir = otherFileCard.getCurrentDir();
          if(otherDir != null){
            String sDirPath = otherDir.getName();
            if(bToRoot = sDirPath.equals(sFileName)){
              //the directory of other is the current selected dir of this:
              File otherParent = otherDir.getParentFile();
              otherFileCard.fillIn(otherParent);
              otherFileCard.selectFile(sFileName);
            }
          }
        }
        if(!bToRoot){
          //check whether a sub dir is selected:
          String sOtherSelectedFile = otherFileCard.currentFile.getName();
          if(sOtherSelectedFile.equals(sDirName)){
            otherFileCard.fillIn(otherFileCard.currentFile);
            otherFileCard.selectFile(sFileName);
          }
        }
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
        panel.orderMainPanel = ixMainPanel +1;
      } else {
        panel.orderMainPanel = 0; //not used.
      }
    }
  }
  
  
  
  
  /**Action to show the file properties in the info line. This action is called anytime if a line
   * was changed in the file view table. */
  GralUserAction actionOnFileSelection = new GralUserAction(){
    /**The action called from {@link GralTable}.
     * @param params [0] is the Table line. The content of table cells are known here,
     *   because it is the file table itself. The {@link GralTableLine_ifc#getUserData()}
     *   returns the {@link FileRemote} file Object.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(int, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      GralTableLine_ifc line = (GralTableLine_ifc) params[0];
      String sFileCell = line.getCellText(GralFileSelector.kColFilename);
      Object oData = line.getUserData();
      if(oData instanceof File){
        actionOnFileSelection((File)oData, sFileCell);
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
