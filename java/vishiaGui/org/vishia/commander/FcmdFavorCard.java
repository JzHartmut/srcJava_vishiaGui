package org.vishia.commander;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralSelectList;
import org.vishia.util.KeyCode;

/**This is one table of favorite pathes in the file commander.  
 */
public class FcmdFavorCard  
//extends GralSelectList<FcmdFavorPathSelector.FavorPath>
{
//  /**The component */
//  final Fcmd main;
//
//  /**The tabbed panel where this class is member of. */
//  final FcmdLeftMidRightPanel mainPanel;
//  
//  /**Reference to the associated fileTable as the parent of this,
//   * which refers this in {@link FcmdFileCard#wdgFavorCard}.
//   */
//  final FcmdFileCard fileTable;
//  
//  /**Stores the last selected and used favor path (pressing enter or double click)
//   * to select the same line if the favor path will be re-opened.
//   */
//  String sActSelectedFavorPath = "";
//  
//  /**Index of all entries in the visible list. */
//  Map<String, FcmdFavorPathSelector.FavorPath> indexFavorPaths = new TreeMap<String, FcmdFavorPathSelector.FavorPath>();
//  
//  public FcmdFavorCard(Fcmd main, String name, FcmdFileCard fileTable, FcmdLeftMidRightPanel panel)
//  { //super(name, mng);
//    super(main.gui.gralMng.refPos(), name, 20, new int[]{2,15,0}, 'C');
//    this.main = main;
//    this.mainPanel = panel;
//    this.fileTable = fileTable;
//
//  }
//
//
//  /**Adds a line to this table.
//   * @param ix Show which index is used for a local table, 0..2 for left, mid, right,
//   *   than show the label in the left cell (column)
//   * @param favorPathInfo The favorite info
//   */
//  GralTableLine_ifc<FcmdFavorPathSelector.FavorPath> add(FcmdFavorPathSelector.FavorPath favorPathInfo)
//  {
//    if(indexFavorPaths.get(favorPathInfo.selectName) == null){
//      indexFavorPaths.put(favorPathInfo.selectName, favorPathInfo);
//      GralTableLine_ifc<FcmdFavorPathSelector.FavorPath> line = wdgdTable.addLine(null, null, favorPathInfo);
//      line.setCellText(favorPathInfo.selectName, 1);
//      line.setCellText(favorPathInfo.path, 2);
//      line.repaint(100,0);
//      return line;
//    }
//    else return null;
//  }
//  
//  
//  void clear()
//  {
//    indexFavorPaths.clear();
//    wdgdTable.clearTable();
//  }
//  
//
//  
//  /**Add all favor paths from the SelectTab newly
//   * @param favorTabInfo
//   */
//  void fillFavorPaths(FcmdFavorPathSelector.FavorFolder favorTabInfo)
//  {
//    clear();
//    int lineCt =0;
//
//    GralTableLine_ifc<FcmdFavorPathSelector.FavorPath> currentLine = null;
//    for( FcmdFavorPathSelector.FavorPath favorPathInfo: favorTabInfo.listfavorPaths){
//      GralTableLine_ifc<FcmdFavorPathSelector.FavorPath> line = add(favorPathInfo);
//      if(currentLine == null){ currentLine = line; }  //first line
//      if(favorPathInfo.selectName.equals(sActSelectedFavorPath)){
//        currentLine = line;  //or the last selected one.
//      }
//      lineCt +=1;
//    }
//    wdgdTable.setCurrentLine(currentLine, 3, 1);
//
//  }
//  
//  
//  /**Overrides the {@link GralFileSelector#setFocus()} and calls him, before that sets the color
//   * of the current line of table of all 3 current file panels to the 3-stage color
//   * to see which table has the focus. 
//   */
//  @Override public void setFocus(){ 
//    mainPanel.bFavorCardHasFocus = true;
//    mainPanel.bFavorThemeCardHasFocus = false;
//    main.setLastSelectedPanel(mainPanel);
//    //setActFilePanel_setColorCurrLine();
//    super.setFocus(); 
//  }
//  
//  
//
//  
//  
//  /**Removes this file card with its widgets and data. It is 'close tab'. */
//  @Override public boolean remove(){
//    super.remove();
//    indexFavorPaths.clear();
//    return true;
//  }
//  
//  
//  @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
//  {
//    //before changing the content of this fileTable, store the current directory
//    //to restore if this favor respectively selection is used ones more.
//    FileRemote dir = null;
//    String currentDir;
//    if(fileTable.favorPathInfo !=null){                    // save the current dir to restore it on re-enter the same favor.
//      dir = fileTable.getCurrentDir();
//      if(dir != null){
//        currentDir = dir.getAbsolutePath();
//        if(currentDir !=null){
//          mainPanel.indexActualDir.put(fileTable.favorPathInfo.selectName, currentDir);
//    } } }
//    //
//    //Now switch to the new favor in the file panel: 
//    FcmdFavorPathSelector.FavorPath favorPathInfo = (FcmdFavorPathSelector.FavorPath)line.getUserData();
//    fileTable.actionSetFromTabSelection.exec(KeyCode.menuEntered, null, favorPathInfo);
//    //main.favorPathSelector.actFavorPathInfo = favorPathInfo; //The last used selection (independent of tab left, middle, right)
//    this.sActSelectedFavorPath = favorPathInfo.selectName;
//    this.fileTable.sTabSelection = this.fileTable.label + "." + favorPathInfo.selectName;
//    if(  wdgdTable.name.startsWith(FcmdWidgetNames.tableFavoritesMain)) {
//      //use the root dir any time if the main favor path table is used.
//      currentDir = favorPathInfo.path;
//    } else {
//      currentDir  = mainPanel.indexActualDir.get(favorPathInfo.selectName);
//      if(currentDir == null){
//        currentDir = favorPathInfo.path;
//      }
//    }
//    //dir = new FileRemote(currentDir);  
//    //TODO this is exec in the graphic thread. Fill the card, access the file system in another thread!!!
//    dir = main.fileCluster.getFile(currentDir, null);
//    fileTable.setNewContent(favorPathInfo, dir);
//    
//    if(!fileTable.wdgCardSelector.setActItem(favorPathInfo.selectName)){
//      fileTable.wdgCardSelector.addItem(favorPathInfo.selectName, -1, favorPathInfo);
//    }
//    return true;
//  }
//
//  @Override
//  protected void actionLeft(Object userData, GralTableLine_ifc line)
//  {
//    // TODO Auto-generated method stub
//    
//  }
//
//  @Override
//  protected void actionRight(Object userData, GralTableLine_ifc line)
//  {
//    // TODO Auto-generated method stub
//    
//  }
//
//  /**Handle the keys for the JavaCommander-Selection of favorites
//   * <ul>
//   * <li>sh-F1 .. shF3: activates fileSelector for left, middle and right panel.
//   * </ul>
//   * @see org.vishia.gral.widget.GralSelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
//   */
//  @Override protected boolean actionUserKey(int key, Object userData,
//      GralTableLine_ifc line)
//  { boolean ret = true;
//    FcmdFavorPathSelector.FavorPath favorPathInfo = (FcmdFavorPathSelector.FavorPath)userData;
//    //TODO not used no more
//    if(key ==KeyCode.shift + KeyCode.F1){
//      File dir = new File(favorPathInfo.path);
//      //panelLeft.fileSelectorMain.fillIn(dir);
//    } else if (key ==KeyCode.shift + KeyCode.F2){
//      File dir = new File(favorPathInfo.path);
//      //panelMid.fileSelectorMain.fillIn(dir);
//    } else if (key ==KeyCode.shift + KeyCode.F3){
//        //panelRight.fileSelectorMain.fillIn(new File(data.path));
//    } else if (key ==KeyCode.shift + KeyCode.F5){
//      //reread the configuration file.
//      main.favorPathSelector.readCfg(main.favorPathSelector.fileCfg);
//      main.favorPathSelector.panelLeft.fillCards();
//      
//    /*
//    } else if (key == main.keyActions.keyPanelLeft){
//      //sets focus to left
//      FcmdFileCard fileTableLeft = null;
//      boolean found = false;
//      for(FcmdFileCard fileTable: mainPanel.listTabs){
//        if(fileTable.favorCard == this){ found = true;  break;}
//        fileTableLeft = fileTable;  //save this table as table left, use if found.
//      }
//      if(found){
//        if(fileTableLeft !=null){
//          fileTableLeft.favorCard.wdgdTable.setFocus();
//        } else {  //left from first is the selectAllTable of this panel.
//          mainPanel.cardFavorThemes.wdgdTable.setFocus();
//        }
//      }
//    } else if (key == main.keyActions.keyPanelRight){
//      //sets focus to right
//      FcmdFileCard fileTableRight = null;
//      boolean found = false; 
//      for(FcmdFileCard fileTable: mainPanel.listTabs){
//        if(found){ fileTableRight = fileTable; break; }  //use this next table if found before.
//        if(fileTable.favorCard == this) { found = true; }
//      }
//      if(fileTableRight !=null){
//        fileTableRight.favorCard.wdgdTable.setFocus();
//      }
//    } else if (key == main.keyActions.keyMainPanelLeft){
//      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelRight ?
//          main.favorPathSelector.panelMid : main.favorPathSelector.panelLeft;
//      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.favorCard.setFocus(); }
//    } else if (key == main.keyActions.keyMainPanelRight){
//      FcmdLeftMidRightPanel dstPanel = mainPanel == main.favorPathSelector.panelLeft ?
//          main.favorPathSelector.panelMid : main.favorPathSelector.panelRight;
//      if(dstPanel.actFileCard !=null){ dstPanel.actFileCard.favorCard.setFocus(); }
//    */
//    } else if (key == main.keyActions.keyCreateFavorite){
//      //main.favorPathSelector.actFavorPathInfo = favorPathInfo; //info in the line of table.
//      main.favorPathSelector.windAddFavorite.widgLabel.setText("file3");
//      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
//      //File lastSelectedFile = panelRight.fileSelectorMain.getSelectedFile();
//      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
//      //windAddFavorite.widgPath.setText(pathDir);
//      main.favorPathSelector.windAddFavorite.window.setFocus(); //WindowVisible(true);
//    } else {
//      ret = false;
//    }//
//    return ret;
//  }
//  
//  /**Action is called any time if a line was focused in the favor table. */
//  GralUserAction actionFavorSelected = new GralUserAction("actionFavorSelected"){
//    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
//      if(actionCode == KeyCode.userSelect){
//        mainPanel.bFavorCardHasFocus = true;
//        mainPanel.bFavorThemeCardHasFocus = false;
//        main.lastFavorCard = FcmdFavorCard.this;
//        main.currentFileCard = FcmdFavorCard.this.fileTable;
//        main.setLastSelectedPanel(mainPanel);
//        GralTable.TableLineData line = (GralTable.TableLineData) params[0];
//        //Object oData = line.getUserData();
//        //System.out.println("FcmdFavorCard.actionFavorSelected: " + fileTable.label);
//      }
//      return true;
//    }
//  };
//  
//
//  
//  @Override public String toString(){ return fileTable.toString(); }
//
}
