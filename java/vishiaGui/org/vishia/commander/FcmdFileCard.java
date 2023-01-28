package org.vishia.commander;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.util.Assert;
import org.vishia.util.CheckVs;
import org.vishia.util.Debugutil;
import org.vishia.util.FileCompare;
import org.vishia.util.KeyCode;

/**This is one file table in the the.File.commander. Each main panel (left, middle, right)
 * has maybe more as one tabs, each tab has exactly one instance of this. The file table is reused
 * for the several tabs of the main panel, and they are reused too if the directory is changed.
 * <br><br>
 * This class is inherited from the {@link GralFileSelector}. This allows same handling for the files,
 * but additional handling for favors: 
 * <br>
 * The {@link #wdgFavorCard} is referenced here and contains names and {@link FcmdFavorCard.FavorPath}
 * to fill different directories to this {@link GralFileSelector} base data.
 * <br><br>
 * For different opened favors the #wdgCardSelector is a quasi tab to select this different favors.
 * The opened favors are well visible hence in this widget without focus the favor card itself. 
 * But the same functionality to select favors can also be done via the {@link FcmdFavorCard}
 * <br><br>
 * <b>Synchronization of 2 file cards</b>:<br>
 * If the synchronization is switch on with {@link FcmdFavorPathSelector#bSyncMidRight} then 
 * {@link #syncWithSecondPanel(String)} is called with the filename. Therewith the same file 
 * in the second panel (middle or right) will be selected if it exists. If an existing directory
 * is selected, the other file card follows it if it exists. So the user can walk through two file trees
 * which have the same structure, but maybe some different content. It is proper for comparison.  
 * <br><br>
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 */
public class FcmdFileCard extends GralFileSelector
{

  
  /**Version, history and license
   * <ul>
   * <li>2023-01-14 fixed: Now from begin the current tab is green and recognized as current.
   * <li>2022-12 Most of content is now in {@link GralFileSelector}, refactoring.  
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
  @SuppressWarnings("hiding")
  public static final int version = 0x20230114;
//  
//  /**Table widget for the select table of the file tab.*/
//  FcmdFavorCard wdgFavorCard;

  /**The component */
  final Fcmd main;
  
  /**The left, mid or right main panel where this tabbed file table is associated. */
  final FcmdLeftMidRightPanel mainPanel;
  
  /**Aggregation to the appropriate entry of the favors. */
  final FcmdFavorPathSelector.FavorFolder favorFolder;
  
  /**Three colors for the current line in the file card.
   * 
   */
  GralColor[] colorSelectFocused123 = new GralColor[3];
//  
//  /**The organization unit for this FileSelector. */
//  //final LeftMidRightPanel.FileTabs fileTabs;
  
  /**The search-name of the tabbed file panel where this Table is placed on. 
   * It is the visible label of the tab, following by ".1" till ".3" for the three panels. */
  final String nameFilePanel;
  
  
  /**It is build from the {@link #label} and the {@link #favorPathInfo}. {@link FavorPath#selectName}
   * due to the selected favor which is shown in this file table.
   * It is used to detect the correct other sync table, see {@link #syncTabSelection} 
   */
  String sTabSelection;
  
  /**If not null then the name of the own favor card and the favor card from the other panel for sync. */
  String syncTabSelection, syncPartnerTabSelection;
  
//  //final GralTextField_ifc widgLabel; /// 
//  
//  final GralHorizontalSelector<Object> wdgCardSelector;
//  
//  /**Association to the current used favor path selection.
//   * Note that this instance is re-used for more as one selection.
//   */
//  FcmdFavorPathSelector.FavorPath favorPathInfo;
//  
//  /**The last selected file and its directory. */
//  //FileRemote currentFile, currentDir;
  
  /**If not null, then should synchronize with this file card. Used in */
  FcmdFileCard otherFileCardtoSync; 
  
  
  /**If not null, then it is the base dir for synchronization with the {@link #otherFileCardtoSync}. 
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  String sDirSync;
  
  /**length of sDirSync or -1
   * It will be set in {@link FcmdFilesCp#setDirs()}. */
  int zDirSync;
  
//  String sLocalpath, sLocaldir;
//  
//  
  /**Creates the cards with tabs for the files and for the favorite paths.
   * This ctor will be called in the graphic thread. Therefore it can initialize the graphic 
   * for the fileCard and for the associated favor card in this code.
   * @param mainPanelP The left, mid or right panel where this cards are assigned to
   * @param label The label of the tab, it builds the name of all widgets.
   */
  FcmdFileCard(GralPos refPos, FcmdLeftMidRightPanel mainPanelP, FcmdFavorPathSelector.FavorFolder favorFolder){
    super(refPos, FcmdWidgetNames.tableFile + favorFolder.label+ "." + mainPanelP.cNr, null, 50, new int[]{2,0,-6,-12}, true, null
        , mainPanelP.main.fileViewer, mainPanelP.main.fileProps);
    this.main = mainPanelP.main;
    this.mainPanel = mainPanelP;
    this.favorFolder = favorFolder;
    this.nameFilePanel = favorFolder.label+ "." + mainPanelP.cNr;
    this.colorSelectFocused123[0] = GralColor.getColor("lgn");
    this.colorSelectFocused123[1] = GralColor.getColor("lbl");
    this.colorSelectFocused123[2] = GralColor.getColor("lgr");
//    String namePanelFile = FcmdWidgetNames.tableFile + nameFilePanel;
//    
//    main.idxFileSelector.put(namePanelFile, this); //it is WidgetNames.tableFile + label +.123, see super(...) 
//    GralMng mng = main.gui.gralMng;
//    //
//    //The favorite paths card
//    String nameTableSelection = FcmdWidgetNames.tableFavorites + nameFilePanel;
//    GralPanelContent panelFavors = mainPanel.tabbedPanelFavorCards.addTabPanel(FcmdWidgetNames.tabFavorites + nameFilePanel, label);
//    mng.setPosition(0, 0, 0, -0, 1, 'd');  
//    this.wdgFavorCard = new FcmdFavorCard(main, nameTableSelection, this, mainPanel);
//    this.wdgFavorCard.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorSelect.");
//    panelFavors.setPrimaryWidget(wdgFavorCard.wdgdTable);
//    //
//    //to show the properties of the selected file in the info line:
//    //
//    //sets this Widget to the selected panel, it is the grid panel which was created even yet.
//    /*
//    mng.setPosition(0, 2, 0, 20, 1, 'd');
//    String nameWidgLabel = FcmdWidgetNames.labelWidgFile + nameFilePanel;
//    widgLabel = mng.addTextField(nameWidgLabel, false, null, null);
//    */
//    mng.setPosition(0, 2, 0, 0, 1, 'd');
//    wdgCardSelector = new GralHorizontalSelector<Object>(mng.refPos(), "cards", actionSetFromTabSelection);
//    //mng.addHorizontalSelector(wdgCardSelector);
//
//    mng.setPosition(2, 0, 0, 0, 1, 'd');
//    //set the base class GralFileSelector to the panel. It contains the path and the table for file selection.
//    //GralPos.Coordinate[] columns = new GralPos.Coordinate[4];
//    //Sets the columns for the table.
//    //super.selectList.wdgdTable.setColumnWidth(50, new int[]{2,0,-6,-11});
//    super.wdgSelectList.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.fileSelect.");
//    GralMenu menuFolder = super.widgdPathDir.getContextMenu();
//    menuFolder.addMenuItem("contextfolder-setOrigin", main.idents.menuContextSetOriginDir, main.favorPathSelector.actionSetDirOrigin);
//    menuFolder.addMenuItem("menuContextCreateFavor", main.idents.menuContextCreateFavor, main.favorPathSelector.actionCreateFavor);
//    menuFolder.addMenuItem("context-filescp", main.idents.menuFilesCpContext, main.filesCp.actionConfirmCp);
//    menuFolder.addMenuItem("contextfolder-create", main.idents.menuConfirmMkDirFileContext, main.mkCmd.actionOpenDialog);
//    menuFolder.addMenuItem("contextfolder-search", main.idents.menuContextSearchFiles, main.favorPathSelector.actionSearchFiles);
//    menuFolder.addMenuItem("contextfolder-refresh", main.idents.menuFileNaviRefreshContext, main.favorPathSelector.actionRefreshFileTable);
//    ((GralPanelContent)super.pos().parent).setPrimaryWidget(super.wdgSelectList.wdgdTable);
//    //
//    //sets the action for a simple table: what to do on line selected: Show file names. 

    this.setActionOnFileSelected(this.actionOnFileSelection);
    setActionOnFocusedFileTable(this.actionFocused);       // sets the current file card from three ones in Fcmd  
    setActionSaveFavors(this.actionSaveFavors);
    
//    //Note: some menu entries are set in the super class already.
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFilePropsContext, main.filePropsCmd.actionOpenDialog);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFileViewContext, main.viewCmd.actionOpenView);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuContextEditIntern, main.editWind.actionOpenEdit);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuFileEditContext, main.fcmdActions.actionEdit);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuConfirmCopyContext, main.copyCmd.actionConfirmCopy);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuConfirmFileDelContext, main.deleteCmd.actionConfirmDelete);
    this.gui.widgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuExecuteContext, main.executer.actionExecuteFileByExtension);
//    wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "test", main.idents.menuExecuteCmdContext, main.executer.cmdSelector.actionExecCmdWithFiles);
//    //selectList.wdgdTable.addContextMenuEntryGthread(1, "deSelDir", main.idents.deselectRecursFiles.menuContext, main.favorPathSelector.actionDeselectDirtree);
//    wdgFavorCard.wdgdTable.specifyActionOnLineSelected(wdgFavorCard.actionFavorSelected);
//    //
//    //sets the action for Select a file: open the execute menu
    setActionOnEnterFile(this.actionOnEnterFile);
//    setActionSetFileLineAttrib(actionSetFileLineAttrib);
  }

  
//  private void buildGraphic(){
//    //see ctor
//  }
//  
//  
//  /**Sets a new content for this file table because another favor or tab is selected
//   * @param favorPathInfoP
//   * @param dir
//   * @param mode 0 no tab, 1 -temporary tab, 2 - new tab
//   */
//  void setNewContent(FcmdFavorPathSelector.FavorPath favorPathInfoP, FileRemote dir){
//    favorPathInfo = favorPathInfoP;
//    if(wdgFavorCard == null){
//      System.err.println("FcmdFileCard.setNewContent - favorCard is null; " + favorPathInfo);
//    } else {
//      wdgFavorCard.add(favorPathInfo);  //only it is a new one, it will be checked.
//      setOriginDir(favorPathInfo.getOriginDir());
//      //widgLabel.setText(favorPathInfo.selectName);
//      fillIn(dir, false);
//      setFocus();
//    }
//  }
//  
//  void setFocusFavorOrFile(){
//    if(mainPanel.bFavorCardHasFocus){
//      wdgFavorCard.setFocus();
//    } else {
//      this.setFocus();
//    }
//  }
//  
//
//  
//  
//  /**Overrides the {@link GralFileSelector#setFocus()} and calls him, before that sets the color
//   * of the current line of table of all 3 current file panels to the 3-stage color
//   * to see which table has the focus. 
//   */
//  @Override public void setFocus(){ 
//    mainPanel.bFavorCardHasFocus = false;
//    mainPanel.bFavorThemeCardHasFocus = false;
//    setActFilePanel_setColorCurrLine();
//    super.setFocus(); 
//  }
//  
//  
//  
//  
//  
//  /**Removes this file card with its widgets and data. It is 'close tab'. */
//  @Override public boolean remove(){
//    if(wdgFavorCard !=null) {
//      wdgFavorCard.remove();
//    }
//    wdgFavorCard = null;
//    favorPathInfo = null;
//    return super.remove();
//  }
// 
//  
//  
//  /**Searches whether the given file has a comparison result in this file card.
//   * That method is used to present the file in the table with comparison result information
//   * and to change the comparison result if the file was copied.
//   * @param file The file, usual selected in the file table
//   * @return null a comparison result is not existed, elsewhere the result.
//   */
//  FileCompare.Result searchCompareResult(File file){
//    ///
//    final FileCompare.Result result;
//    if(sDirSync !=null){
//      zDirSync = sDirSync.length();
//      String sPath = file.getAbsolutePath();
//      if(sPath.startsWith(sDirSync) && sPath.length() > zDirSync){
//        String sLocalPath = sPath.substring(sDirSync.length()+1);
//        result = main.filesCp.idxFilepath4Result.get(sLocalPath);
//      } else {
//        result = null;  //outside of sDirSync
//      }
//    } else {
//      zDirSync = -1;
//      result = null;  //no comparison active
//    }
//    return result;
//  }
//  
//  
//  
//  
//  @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
//  { boolean ret = true;
//    //FileRemote data = (FileRemote)oData;
//    switch(keyCode){
//    //case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{data}, "ordersBackground"); break;
//    default: ret = false;
//    }
//    /*
//    if (keyCode == main.keyActions.keyCreateFavorite){
//      main.favorPathSelector.windAddFavorite.panelInvocation = mainPanel;
//      main.favorPathSelector.windAddFavorite.widgLabel.setText(nameFilePanel);
//      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
//      FileRemote lastSelectedFile = getSelectedFile();
//      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
//      main.favorPathSelector.windAddFavorite.widgPath.setText(lastSelectedFile.getParent());
//      main.favorPathSelector.windAddFavorite.window.setWindowVisible(true);
//    } else*/ 
//    if (keyCode == main.keyActions.keyPanelSelection){
//      //focuses the panel which is the selection panel for this file table.
//      GralWidget tableSelection = main.gui.gralMng.getWidget(FcmdWidgetNames.tableFavorites + nameFilePanel);
//      tableSelection.setFocus();
//    /*
//    } else if (keyCode == main.keyActions.keyPanelLeft){
//      //sets focus to left
//      FcmdFileCard fileTableLeft = null;
//      boolean found = false;
//      for(FcmdFileCard fileTable: mainPanel.listTabs){
//        if(fileTable == this){ found = true;  break;}
//        fileTableLeft = fileTable;  //save this table as table left, use if found.
//      }
//      if(found){
//        if(fileTableLeft !=null){
//          fileTableLeft.setFocus();
//        } else {  //left from first is the selectAllTable of this panel.
//          //panel.selectTableAll.wdgdTable.setFocus();
//        }
//      }
//    } else if (keyCode == main.keyActions.keyPanelRight){
//      //sets focus to right
//      FcmdFileCard fileTableRight = null;
//      boolean found = false; //(mainPanel.selectTableAll == this);
//      for(FcmdFileCard fileTable: mainPanel.listTabs){
//        if(found){ fileTableRight = fileTable; break; }  //use this next table if found before.
//        if(fileTable == this) { found = true; }
//      }
//      if(fileTableRight !=null){
//        fileTableRight.setFocus();
//      }
//      
//    } else if (keyCode == main.keyActions.keyMainPanelLeft){
//      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelRight ?
//          main.favorPathSelector.panelMid : main.favorPathSelector.panelLeft;
//      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.setFocus(); }
//    } else if (keyCode == main.keyActions.keyMainPanelRight){
//      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelLeft ?
//          main.favorPathSelector.panelMid : main.favorPathSelector.panelRight;
//      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.setFocus(); }
//    */
//    } else {
//      ret = false;
//    }
//    return ret;
//  }
//
//  
  
  /**This routine is invoked from {@link #actionOnFileSelection} action listener whenever a file in any file card
   * will be selected (key up, down, mouse click etc.).
   * The routine writes infos about the file and may synchronize with another file card.
   * @param file The currently selected file.
   * @param sFileName Text in the cell, especially ".." for the parent dir entry.
   */
  protected void actionOnFileSelection(FileRemote file, String sFileName){
    //note the file, able to use for some actions.
    this.gralMng().log().sendMsg(Fcmd.LogMsg.fmcdFileCard_selectFile, "actionOnFileSelected ixMainPanel=%d %s", this.mainPanel.ixMainPanel, file);
    if(sFileName.equals(".filelist"))
      Debugutil.stop();
    this.main.selectedFiles123[this.mainPanel.ixMainPanel] = file;
    
    if(this.mainPanel.orderMainPanel == 1){
      //only if it is the focused panel:
      //note the file card in order of usage.
      
      //this.main.lastFavorCard = this.wdgFavorCard;
      this.main.currentFileCard = this;
      this.mainPanel.actFileCard = this;
      this.main.statusLine.setFileInfo(this.getLabelCurrFavor(), file);
      //System.out.println("actionOnFileSelected: " + this.label + ":" + this.favorCard.sActSelectedFavorPath);
      String sPath = file.getAbsolutePath();
      if(  this.main.favorPathSelector.bSyncMidRight 
        && this.mainPanel.actFileCard == this    //from actFileCard to the second one!
        && this.mainPanel.orderMainPanel == 1
      ){
        try{ syncWithSecondPanel(sFileName); }
        catch(Exception exc){ 
          CharSequence msg = CheckVs.exceptionInfo("Fcmd.actionOnFileSelection.syncWithSecondPanel() - exception, ", exc, 0, 20);
          System.out.append(msg);
        }
        System.out.println("FcmdFileCard - syncWithSecondPanel; " + toString());
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
    System.out.println("FcmdFileCard -SyncWithSecondPanel;" + mainPanel.cc + ";" + sFileName);
    FcmdFileCard otherFileCard;
    boolean bFillInReq = false;
    if(mainPanel.cc == 'm'){ otherFileCard = main.favorPathSelector.panelRight.actFileCard; }
    else if(mainPanel.cc == 'r'){ otherFileCard = main.favorPathSelector.panelMid.actFileCard;  }
    else if(mainPanel.cc == 'l'){ otherFileCard = main.favorPathSelector.panelMid.actFileCard;  }
    else { otherFileCard = null; }
    if(otherFileCard !=null){  //NOTE: though mid and right is selected, the otherFileCard may be null because no tab is open.
      String sDirName = getCurrentDir().getName();
      //check whether the other file card contains a entry with this directory name
      GralTableLine_ifc<FileRemote> line = otherFileCard.gui.widgSelectList.wdgdTable.getLine(sDirName);
      if(line !=null){
        FileRemote dir = line.getUserData();
        bFillInReq = true;
        otherFileCard.fillIn(dir, false);    //use that directory.
      }
      boolean bSameFile = otherFileCard.selectFile(sFileName);  //".." also
      if(!bSameFile){
        //check whether the file is a directory and it is the directory of the other panel:
        boolean bToRoot = false;
        if(super.idata.currentFile.isDirectory()){
          FileRemote otherDir = otherFileCard.getCurrentDir();
          if(otherDir != null){
            String sDirPath = otherDir.getName();
            bToRoot = sDirPath.equals(sFileName);
            if(bToRoot){
              //the directory of other is the current selected dir of this:
              FileRemote otherParent = otherDir.getParentFile();
              if(!bFillInReq){
                otherFileCard.fillIn(otherParent, false);
                otherFileCard.selectFile(sFileName);
                bFillInReq = true;
              }
            }
          }
        }
        if(!bToRoot && otherFileCard !=null && otherFileCard.idata.currentFile !=null){
          //check whether a sub dir is selected:
          String sOtherSelectedFile = otherFileCard.idata.currentFile.getName();
          if(sOtherSelectedFile.equals(sDirName) && !bFillInReq){
            otherFileCard.fillIn(otherFileCard.idata.currentFile,false);
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
  protected void setActFilePanel_setColorCurrLine(){
    //main.lastFavorCard = wdgFavorCard;
    this.main.currentFileCard = this;
    this.mainPanel.actFileCard = FcmdFileCard.this;
    this.main.setLastSelectedPanel(mainPanel);
    //System.out.println("setActFilePanel: " + this.sTabSelection + " =^ " + this.label + "#" + this.wdgFavorCard.sActSelectedFavorPath);
    //System.out.println(Assert.stackInfo("FcmdFileCard - setActFilePanel_setColorLine;",10));
    int ixMainPanel = -1;
    for(FcmdLeftMidRightPanel panel: main.lastFilePanels){
      if(ixMainPanel >=2) {
        break;
      }
      if(panel.actFileCard !=null){                        // mark the current file card with green, last yellow
        panel.actFileCard.gui.widgSelectList.wdgdTable.setColorBackSelectedLine(this.colorSelectFocused123[++ixMainPanel]);
        panel.orderMainPanel = ixMainPanel +1;   //order in list 1, 2, 3
      } else {
        panel.orderMainPanel = 0; //not used.
      }
    }
    org.vishia.gral.base.GralTable<FileRemote>.TableLineData line  = FcmdFileCard.super.gui.widgSelectList.wdgdTable.getCurrentLine();
    FileRemote fileCurr = line.getData();
    String fName = line.getCellText(1);
    FcmdLeftMidRightPanel p1 = this.main.lastFilePanels.size() <=0 ? null: this.main.lastFilePanels.get(0);
    FcmdLeftMidRightPanel p2 = this.main.lastFilePanels.size() <=1 ? null: this.main.lastFilePanels.get(1);
    FcmdLeftMidRightPanel p3 = this.main.lastFilePanels.size() <=2 ? null: this.main.lastFilePanels.get(2);
    char c1 = p1 == null? '.' : p1.cc;
    char c2 = p2 == null? '.' : p2.cc;
    char c3 = p3 == null? '.' : p3.cc;
    String sOrderFilePanels = "" + c1 + c2 + c3;
    //this.gralMng().log.sendMsg(Fcmd.LogMsg.fmcdFileCard_setCurrFilePanel, "setCurrFilePanel : %s", sOrderFilePanels );
    actionOnFileSelection(fileCurr, fName);
  }
  
  
  /**Action to show the file properties in the info line. This action is called anytime if a line
   * was changed in the file view table. */
  GralUserAction actionOnFileSelection = new GralUserAction("FcmdFileCard-actionOnFileSelection"){
    /**The action called from {@link GralTable}.
     * @param params [0] is the Table line. The content of table cells are known here,
     *   because it is the file table itself. The {@link GralTableLine_ifc#getUserData()}
     *   returns the {@link FileRemote} file Object.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(int, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      // do it only if it is the current focused panel.    // prevent next operation for the second panel to sync!
      if(FcmdFileCard.this.main.lastFilePanels.size()>=1 && FcmdFileCard.this.main.lastFilePanels.get(0) == FcmdFileCard.this.mainPanel) {
        setActFilePanel_setColorCurrLine();                // action on this card: It is the first one
        FcmdFileCard.this.mainPanel.bFavorCardHasFocus = false;
        FcmdFileCard.this.mainPanel.bFavorThemeCardHasFocus = false;
        @SuppressWarnings("unchecked") 
        GralTableLine_ifc<FileRemote> line = (GralTableLine_ifc<FileRemote>) params[0];
        if(line ==null) {
          FcmdFileCard.this.gralMng.log.sendMsg(GralMng.LogMsg.gralFileSelector_fillinFinished, "ERROR no selected line");
        } else {
          String sFileCell = line.getCellText(GralFileSelector.Constants.kColFilename);
          Object oData = line.getUserData();
          if(oData instanceof File){
            actionOnFileSelection((FileRemote)oData, sFileCell);
          }
        }
      }
      return true;
    }
  };
  
  
//  /**Sets the color of the table line adequate to the select state of the file. */
//  GralUserAction actionSetFileLineAttrib = new GralUserAction("actionSetFileLineAttrib"){
//    /**@param params [0] the table line. It contains the file.
//     * @see org.vishia.gral.ifc.GralUserAction#exec(int, org.vishia.gral.ifc.GralWidget_ifc, java.lang.Object[])
//     */
//    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
//      //check whether any of the 2 compare directories are base for the current file:
//      try{
//        GralTableLine_ifc line = (GralTableLine_ifc)(params[0]);
//        File file = (File)line.getUserData();
//        if(file instanceof FileRemote){
//          if(file.getName().equals("exe"))
//            Assert.stop();
//          if(file.getName().equals("Fcmd.jar"))
//            Assert.stop();
//          FileRemote file2 = (FileRemote)file;
//          int flags = file2.getFlags();
//          if(file2.isMarked(0xffff)){
//            line.setMarked(1, null);
//            //line.setBackColor(GralColor.getColor("pbl"), 1);
//            line.setLineColor(GralColor.getColor("rd"), 1);
//          } else {
//            //line.setBackColor(GralColor.getColor("wh"), 1);
//            line.setLineColor(GralColor.getColor("bk"), 1);
//          }
//        }
//        FileCompare.Result result = searchCompareResult(file);
//        if(result !=null){
//          if(!result.equal){ line.setCellText("#", 0); }
//          else if(result.alone){ line.setCellText("+", 0); }
//          else if(result.missingFiles){ line.setCellText("-", 0); }
//        }
//      } catch(Exception exc){
//        main.gui.gralMng.log.sendMsg(0, "Exception in FcmdFileCard.actionSetFileLineAttrib"); 
//      }
//      return true;
//  } };  
//  
//  
  /**This action is bound in the File selection table. If it is focused, the current file tables
   * of the other file panels will gotten the {@link #colorSelectNonFocused} to show that are not
   * the first one. The file table of this is set with the {@link #colorSelectFocused}.
   * Twice the {@link Fcmd#lastFilePanels} list is ordered with this panel as first one. 
   * 
   */
  GralUserAction actionFocused = new GralUserAction("actionFocused"){
    @SuppressWarnings("synthetic-access") @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      if(actionCode == KeyCode.focusGained){
        setActFilePanel_setColorCurrLine();
      }
      return true;      
  } };
  
  
  
  /**This action is associated to the {@link FcmdFileCard}
   * respectively its base class {@link GralFileSelector#setActionOnEnterFile(GralUserAction)} 
   * It calls {@link FcmdExecuter#executeFileByExtension(File)}.
   */
  private GralUserAction actionOnEnterFile = new GralUserAction("actionOnEnterFile") { 
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params) { 
      setActFilePanel_setColorCurrLine();                  // action on this card: It is the first one
      FileRemote file = (FileRemote)params[0]; 
      FcmdFileCard.this.main.executer.executeFileByExtension(file);  
      return true;
    }
  };
  
  
  GralUserAction actionSaveFavors = new GralUserAction("actionSaveFavors"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      List<GralFileSelector.FavorPath> listfavorPaths = FcmdFileCard.this.favorFolder.listfavorPaths;
      listfavorPaths.clear();                              // Build this list newly with all entries in the table.
      for(GralTable<GralFileSelector.FavorPath>.TableLineData line : FcmdFileCard.this.gui.widgFavorTable.iterLines()) {
        GralFileSelector.FavorPath favor = line.getData();
        listfavorPaths.add(favor);
      }                                                    // and also write the file to disk for all favors (only one file for all).
      FcmdFileCard.this.main.favorPathSelector.writeCfg(FcmdFileCard.this.main.favorPathSelector.fileCfg);
      return true;      
  } };



//  /**Called if a tab is used to change the selection in the favorTab. 
//   * The adequate action is done too with selection in the favor tab. 
//   * */
//  private void actionSetFromTabSelection ( FcmdFavorPathSelector.FavorPath favorPathNew ) {
//    //before changing the content of this fileTable, store the current directory
//    //to restore if this favor respectively selection is used ones more.
//    FileRemote dir = null;
//    String sCurrentDir;
//    if(favorPathInfo !=null){
//      dir = getCurrentDir();
//      if(dir != null){
//        sCurrentDir = dir.getAbsolutePath();
//        if(sCurrentDir !=null){
//          mainPanel.indexActualDir.put(favorPathInfo.selectName, sCurrentDir);
//    } } }
//    //main.favorPathSelector.actFavorPathInfo = favorPathNew; //The last used selection (independent of tab left, middle, right)
//    if(favorPathNew == null){
//      //TODO clear filecard
//      this.wdgFavorCard.sActSelectedFavorPath = "??NO FavorPath.selectName";
//      System.out.println("actionSetFromTabSelection: favorPathNew = null");
//      this.main.favorPathSelector.bSyncMidRight = false;
//    } else {
//      this.wdgFavorCard.sActSelectedFavorPath = favorPathNew.selectName;
//      this.sTabSelection = this.label + "." + favorPathNew.selectName;
//      this.main.favorPathSelector.bSyncMidRight = 
//          this.syncTabSelection !=null 
//          && this.syncPartnerTabSelection !=null 
//          && this.syncTabSelection.equals(this.sTabSelection)
//          && this.mainPanel.partnerPanelToSync() !=null
//          && this.mainPanel.partnerPanelToSync().actFileCard !=null
//          && this.syncPartnerTabSelection.equals(this.mainPanel.partnerPanelToSync().actFileCard.sTabSelection);
//      GralColor syncColor = this.main.favorPathSelector.bSyncMidRight ? GralColor.getColor("gn") : GralColor.getColor("wh");
//      this.main.statusLine.widgSyncInfoLeft.setBackColor(syncColor,0);
//      this.main.statusLine.widgSyncInfoRight.setBackColor(syncColor,0);
//      if(this.mainPanel.cc == 'm') {
//        if(this.syncTabSelection ==null) {
//          this.main.statusLine.widgSyncInfoRight.setText("");
//        } else {
//          this.main.statusLine.widgSyncInfoLeft.setText(this.syncTabSelection);
//          this.main.statusLine.widgSyncInfoRight.setText(this.syncPartnerTabSelection);
//        }
//      }
//      else if(this.mainPanel.cc == 'r') {
//        if(this.syncTabSelection ==null) {
//          this.main.statusLine.widgSyncInfoLeft.setText("");
//        } else {
//          this.main.statusLine.widgSyncInfoLeft.setText(this.syncPartnerTabSelection);
//          this.main.statusLine.widgSyncInfoRight.setText(this.syncTabSelection);
//        }
//      }
//      System.out.println("actionSetFromTabSelection: " + favorPathNew.selectName);
//      sCurrentDir  = mainPanel.indexActualDir.get(favorPathNew.selectName);
//      if(sCurrentDir == null){
//        sCurrentDir = favorPathNew.path;
//      }
//      //dir = new FileRemote(currentDir);  
//      dir = main.fileCluster.getFile(sCurrentDir, null);
//      FcmdFileCard.this.setNewContent(favorPathNew, dir);
//    }
//    
//  }
//  
//  
//  /**This action is called if a tab is selected. */
//  GralUserAction actionSetFromTabSelection = new GralUserAction("actionSetFromTabSelection"){
//    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
//      FcmdFavorPathSelector.FavorPath favorPathNew = (FcmdFavorPathSelector.FavorPath)params[0];
//      actionSetFromTabSelection(favorPathNew);
//      return true;      
//  } };
//
//
//  
//  @Override public String toString(){ return label + "/" + nameFilePanel; }
  
  
}
