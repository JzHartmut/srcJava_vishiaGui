package org.vishia.commander;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.SelectList;
import org.vishia.util.KeyCode;

public class FcmdFavorTabCard extends SelectList
{
  /**The component */
  final Fcmd main;

  /**The tabbed panel where this class is member of. */
  final FcmdLeftMidRightPanel mainPanel;
  
  /**Index of all entries in the visible list. */
  Map<String, FcmdFavorPathSelector.FavorFolder> indexFavorFolders = new TreeMap<String, FcmdFavorPathSelector.FavorFolder>();
  
  final int[] widthSelecttableMain = new int[]{10, 30};

  public FcmdFavorTabCard(Fcmd main, FcmdLeftMidRightPanel panel)
  { //super(name, mng);
    this.main = main;
    this.mainPanel = panel;
  }

  
  
  public void setToPanel(GralGridBuild_ifc panel, String name, char size){
    super.setToPanel(panel, name, 20, widthSelecttableMain, size);
    wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.tabSelect.");
  }

  /**Adds a line to this table.
   * @param ix Show which index is used for a local table, 0..2 for left, mid, right,
   *   than show the label in the left cell (column)
   * @param favorTabInfo The favorite info
   */
  void addFavorFolder(FcmdFavorPathSelector.FavorFolder favorTabInfo)
  {
    if(indexFavorFolders.get(favorTabInfo.label) == null){
      indexFavorFolders.put(favorTabInfo.label, favorTabInfo);
      GralTableLine_ifc line = wdgdTable.insertLine(null, 0);
      line.setUserData(favorTabInfo);
      line.setCellText(favorTabInfo.label, 0);
      line.setCellText(favorTabInfo.selectNameTab, 1);
    }
  }
  
  
  void clear()
  {
    wdgdTable.clearTable();
  }
  
  
  @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
  {
    FcmdFavorPathSelector.FavorFolder favorTabInfo = (FcmdFavorPathSelector.FavorFolder)line.getUserData();
    //main.favorPathSelector.actSelectInfo = info; //The last used selection (independent of tab left, middle, right)
    int ixtabName = mainPanel.cNr - '1';
    
    String tabName = favorTabInfo.label;  //The label of file tab.
    GralWidget widgd;
      //a new path is selected:
      //save the path of the current selection
  
    String label = favorTabInfo.label;  //from favorite list
    //label is known in the favorite list, use it. The panel should be existing or it is created.
    //search or create the tab, representing by its fileTable:
    final FcmdFileCard fileCard = mainPanel.searchOrCreateFileCard(label);
    //adds all favorite pathes to it newly.
    fileCard.fillFavorPaths(favorTabInfo);
   
    //before changing the content of this fileTable, store the current directory
    //to restore if this favor respectively selection is used ones more.
    String currentDir;
    if(fileCard.favorPathInfo !=null){
      currentDir = fileCard.getCurrentDir();
      if(currentDir !=null){
        mainPanel.indexActualDir.put(fileCard.favorPathInfo.selectName, currentDir);
      } 
    } else {
      //nothing selected, its a new tab
      FcmdFavorPathSelector.FavorPath favorPathInfo = favorTabInfo.listfavorPaths.get(0);
      //fill in the standard file panel, use maybe a current directory.
      fileCard.favorPathInfo = favorPathInfo;
      if(  wdgdTable.name.startsWith(FcmdWidgetNames.tableFavoritesMain)   //use the root dir anytime if the main favor path table is used.
        || (currentDir  = mainPanel.indexActualDir.get(favorPathInfo.selectName)) == null){  //use the root if the entry wasn't use till now
        currentDir = favorPathInfo.path;
      }
      fileCard.fillIn(currentDir);
    }
    
    fileCard.favorCard.setFocus();
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
    FcmdFavorPathSelector.FavorFolder favorTabInfo = (FcmdFavorPathSelector.FavorFolder)userData;
    //TODO not used no more
    if (key ==KeyCode.shift + KeyCode.F5){
      //reread the configuration file.
      main.favorPathSelector.readCfg(main.favorPathSelector.fileCfg);
      main.favorPathSelector.panelLeft.fillCards();
      
    } else if (key == main.keyActions.keyPanelRight){
      //sets focus to right
      FcmdFileCard fileTableRight = mainPanel.listTabs.get(0);
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
    } else {
      ret = false;
    }//
    return ret;
  }

}
