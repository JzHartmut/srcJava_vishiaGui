package org.vishia.commander;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.SelectList;
import org.vishia.util.KeyCode;

/**This is one table of favorite pathes in the file commander.  
 */
public class FcmdFavorCard  extends SelectList
{
  /**The component */
  final Fcmd main;

  /**The tabbed panel where this class is member of. */
  final FcmdLeftMidRightPanel mainPanel;
  
  final FcmdFileCard fileTable;
  
  /**Index of all entries in the visible list. */
  Map<String, FcmdFavorPathSelector.FavorPath> indexFavorPaths = new TreeMap<String, FcmdFavorPathSelector.FavorPath>();
  
  public FcmdFavorCard(Fcmd main, FcmdFileCard fileTable, FcmdLeftMidRightPanel panel)
  { //super(name, mng);
    this.main = main;
    this.mainPanel = panel;
    this.fileTable = fileTable;
  }


  /**Adds a line to this table.
   * @param ix Show which index is used for a local table, 0..2 for left, mid, right,
   *   than show the label in the left cell (column)
   * @param favorPathInfo The favorite info
   */
  void add(FcmdFavorPathSelector.FavorPath favorPathInfo)
  {
    if(indexFavorPaths.get(favorPathInfo.selectName) == null){
      indexFavorPaths.put(favorPathInfo.selectName, favorPathInfo);
      GralTableLine_ifc line = wdgdTable.insertLine(null, 0);
      line.setUserData(favorPathInfo);
      /*
      if(info.label !=null){
        line.setCellText(info.label, 0);
      }
      */
      line.setCellText(favorPathInfo.selectName, 1);
      line.setCellText(favorPathInfo.path, 2);
    }
  }
  
  
  void clear()
  {
    indexFavorPaths.clear();
    wdgdTable.clearTable();
  }
  
  
  @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
  {
    FcmdFavorPathSelector.FavorPath favorPathInfo = (FcmdFavorPathSelector.FavorPath)line.getUserData();
    main.favorPathSelector.actFavorPathInfo = favorPathInfo; //The last used selection (independent of tab left, middle, right)
    int ixtabName = mainPanel.cNr - '1';
    
    //String tabName = info.label;  //The label of file tab.
    GralWidget widgd;
      //a new path is selected:
      //save the path of the current selection
  
    //before changing the content of this fileTable, store the current directory
    //to restore if this favor respectively selection is used ones more.
    String currentDir;
    if(fileTable.favorPathInfo !=null){
      currentDir = fileTable.getCurrentDir();
      if(currentDir !=null){
        mainPanel.indexActualDir.put(fileTable.favorPathInfo.selectName, currentDir);
    } }
    
    //fill in the standard file panel, use maybe a current directory.
    fileTable.favorPathInfo = favorPathInfo;
    if(  wdgdTable.name.startsWith(FcmdWidgetNames.tableFavoritesMain)   //use the root dir anytime if the main favor path table is used.
      || (currentDir  = mainPanel.indexActualDir.get(favorPathInfo.selectName)) == null){  //use the root if the entry wasn't use till now
      currentDir = favorPathInfo.path;
    }
    fileTable.favorCard.add(favorPathInfo);
    fileTable.fillIn(currentDir);
    fileTable.setFocus();
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
    FcmdFavorPathSelector.FavorPath favorPathInfo = (FcmdFavorPathSelector.FavorPath)userData;
    //TODO not used no more
    if(key ==KeyCode.shift + KeyCode.F1){
      File dir = new File(favorPathInfo.path);
      //panelLeft.fileSelectorMain.fillIn(dir);
    } else if (key ==KeyCode.shift + KeyCode.F2){
      File dir = new File(favorPathInfo.path);
      //panelMid.fileSelectorMain.fillIn(dir);
    } else if (key ==KeyCode.shift + KeyCode.F3){
        //panelRight.fileSelectorMain.fillIn(new File(data.path));
    } else if (key ==KeyCode.shift + KeyCode.F5){
      //reread the configuration file.
      main.favorPathSelector.readCfg(main.favorPathSelector.fileCfg);
      main.favorPathSelector.panelLeft.fillCards();
      
    } else if (key == main.keyActions.keyPanelLeft){
      //sets focus to left
      FcmdFileCard fileTableLeft = null;
      boolean found = false;
      for(FcmdFileCard fileTable: mainPanel.listTabs){
        if(fileTable.favorCard == this){ found = true;  break;}
        fileTableLeft = fileTable;  //save this table as table left, use if found.
      }
      if(found){
        if(fileTableLeft !=null){
          fileTableLeft.favorCard.wdgdTable.setFocus();
        } else {  //left from first is the selectAllTable of this panel.
          mainPanel.selectTabCard.wdgdTable.setFocus();
        }
      }
    } else if (key == main.keyActions.keyPanelRight){
      //sets focus to right
      FcmdFileCard fileTableRight = null;
      boolean found = false; 
      for(FcmdFileCard fileTable: mainPanel.listTabs){
        if(found){ fileTableRight = fileTable; break; }  //use this next table if found before.
        if(fileTable.favorCard == this) { found = true; }
      }
      if(fileTableRight !=null){
        fileTableRight.favorCard.wdgdTable.setFocus();
      }
    } else if (key == main.keyActions.keyMainPanelLeft){
      String mainPanelId = mainPanel == main.favorPathSelector.panelRight ? ".2" : ".1";
      for(GralWidget widg: main.gralMng.getWidgetsInFocus()){
        if(widg.name.contains(mainPanelId)){
          widg.setFocus();
        }
      }
    } else if (key == main.keyActions.keyMainPanelRight){
      String mainPanelId = mainPanel == main.favorPathSelector.panelLeft ? ".2" : ".3";
      for(GralWidget widg: main.gralMng.getWidgetsInFocus()){
        if(widg.name.contains(mainPanelId)){
          widg.setFocus();
        }
      }
    } else if (key == main.keyActions.keyCreateFavorite){
      main.favorPathSelector.actFavorPathInfo = favorPathInfo; //info in the line of table.
      main.favorPathSelector.windAddFavorite.widgLabel.setText("file3");
      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
      //File lastSelectedFile = panelRight.fileSelectorMain.getSelectedFile();
      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
      //windAddFavorite.widgPath.setText(pathDir);
      main.favorPathSelector.windAddFavorite.window.setWindowVisible(true);
    } else {
      ret = false;
    }//
    return ret;
  }
  
  
  @Override public String toString(){ return fileTable.toString(); }

}