package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.widget.FileSelector;

/**Data for one tabbed panel
 */
public class LeftMidRightPanel
{
  final JavaCmd main;
  
  /**The container for all tabs of this TabbedPanel. */
  GralTabbedPanel tabbedPanelFileTabs;
  
  /**The container for the tabs for selection. */
  GralTabbedPanel tabbedPanelSelectionTabs;
  
  /**All entries for the select list for all favorites in order of the file. */
  List<FavoritePathSelector.SelectInfo> selectListAllFavorites = new LinkedList<FavoritePathSelector.SelectInfo>();


  /**Table widget for the select table.*/
  FavoritePathSelector.SelectTabList selectTableAll;


  

  List<FcmdFileTable> listTabs = new LinkedList<FcmdFileTable>();
  
  /**Stores the current directory for all actual file panels. */
  final Map<String, File> indexActualDir = new TreeMap<String, File>();
  
  /**File panel. */
  //FcmdFileTable fileSelectorMain;
  
  //String actualDir;
  
  /**Characteristic char for the panel: l, m, r*/
  final char cc;
  
  /**Characteristic number for the panel: 1, 2, 3 */
  final char cNr;
  
  final int[] widthSelecttable = new int[]{2, 20, 30};

  
  LeftMidRightPanel(JavaCmd javaCmd, char cc, char cNr, FavoritePathSelector tabSelector, GralWidgetMng mng){
    this.main = javaCmd;
    this.cc = cc;
    this.cNr = cNr;
    selectTableAll = tabSelector.new SelectTabList("panel-" + cNr, this, mng);
  }
  
  
  
  /**Build the initial content of one of the three tabbed panels, called in the build phase of the GUI.
   * @param tabbedPanelFileTabs The TabbbedPanel, created and assigned in the main window.
   */
  void buildInitialTabs(char which)
  {
    GralWidgetMng mng = main.panelMng;
    String sName = "Sel" + cNr;
    //inside the left/mid/right tabbed panel: create the panel which contains a tabbed panel for selection
    String nameGridPanel = WidgetNames.tabFavoritesLeftMidRight + cNr;
    String tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(2, 0, 0, 0, 1, 'd');
    tabbedPanelFileTabs.addGridPanel(nameGridPanel, tabLabelGridPanel,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    //A tabbed panel inside the left, middle or right tab for selection.
    String nameTabPanel = WidgetNames.panelFavoritesLeftMidRight + cNr;
    mng.setPosition(2, 0, 0, 0, 1, 'd');
    tabbedPanelSelectionTabs = mng.createTabPanel(nameTabPanel, null, GralGridBuild_ifc.propZoomedPanel);
    //The panel for selection from all favorites: 
    nameGridPanel = WidgetNames.tabMainFavorites + cNr;
    tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(2, 0, 0, 0, 1, 'd');
    tabbedPanelSelectionTabs.addGridPanel(nameGridPanel, tabLabelGridPanel, 1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTableAll.setToPanel(mng, WidgetNames.selectMainFavorites + cNr, 5, widthSelecttable, 'A');
    fillInAllTables(which);  //build the rest of all tabs and panels depending on content of favorites.
    
    if(cNr == '1'){ //commands only in the left panel.
      tabbedPanelFileTabs.addGridPanel("cmd", "Cm&d",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{10,10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  

  void fillInAllTables(char which){
    selectTableAll.clear();
    for(FcmdFileTable fileTabs: listTabs){
      fileTabs.selectTableForTab.clear();
    }
    for(FavoritePathSelector.SelectInfo info: selectListAllFavorites){ //panel specific favorites
      String label;
      int ixTabname = "lmr".indexOf(which);
      if(ixTabname >=0){
        label = info.tabName[ixTabname];
        if(label !=null){
          FcmdFileTable fileTabs = searchOrCreateFileTabs(label);
          fileTabs.selectTableForTab.add(cNr - '1', info);
        }
      }
      selectTableAll.add(cNr - '1', info);
      //tabSelector.initActDir(indexActualDir, info.selectName, info.path);
     
    }
    for(FavoritePathSelector.SelectInfo info: main.selectTab.selectAll){ //all favorites
      String label;
      int ixTabname = "lmr".indexOf(which);
      if(ixTabname >=0){
        label = info.tabName[ixTabname];  //depending on the instance of this
        if(label !=null){
          FcmdFileTable fileTabs = searchOrCreateFileTabs(label);
          fileTabs.selectTableForTab.add(cNr - '1', info);
        }
      }
      selectTableAll.add(cNr - '1', info);
      //tabSelector.initActDir(indexActualDir, info.selectName, info.path);
    }
  }
  
  
  
  FcmdFileTable searchOrCreateFileTabs(String label){
  //search or create the tab
    FcmdFileTable fileTab = null;
    for(FcmdFileTable item: listTabs){
      if(item.labelTab.equals(label)){
        fileTab = item; break;
      }
    } if(fileTab == null){
      fileTab = new FcmdFileTable(this, label);
      listTabs.add(fileTab);
    }
    return fileTab;
  }
  
  
  
  
  
}
