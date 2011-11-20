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
  List<FcmdFavorPathSelector.SelectInfo> selectListAllFavorites = new LinkedList<FcmdFavorPathSelector.SelectInfo>();


  /**Table widget for the select table.*/
  FcmdFavorPathSelector.SelectTabList selectTableAll;


  

  List<FcmdFileTable> listTabs = new LinkedList<FcmdFileTable>();
  
  /**Stores the current directory for all actual file panels. */
  final Map<String, String> indexActualDir = new TreeMap<String, String>();
  
  /**File panel. */
  //FcmdFileTable fileSelectorMain;
  
  //String actualDir;
  
  /**Characteristic char for the panel: l, m, r*/
  final char cc;
  
  /**Characteristic number for the panel: 1, 2, 3 */
  final char cNr;
  
  final int[] widthSelecttable = new int[]{2, 20, 30};

  
  LeftMidRightPanel(JavaCmd javaCmd, char cc, char cNr, FcmdFavorPathSelector tabSelector, GralWidgetMng mng){
    this.main = javaCmd;
    this.cc = cc;
    this.cNr = cNr;
    selectTableAll = tabSelector.new SelectTabList(this);
  }
  
  
  
  /**Build the initial content of one of the three tabbed panels, called in the build phase of the GUI.
   * @param which Number 1 2 3 for left, mid, right
   */
  void buildInitialTabs(int which)
  {
    GralWidgetMng mng = main.gralMng;
    String sName = "Sel" + cNr;
    //inside the left/mid/right tabbed panel: create the panel which contains a tabbed panel for selection
    String nameGridPanel = WidgetNames.tabFavoritesLeftMidRight + cNr;
    String tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelFileTabs.addGridPanel(nameGridPanel, tabLabelGridPanel,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    //A tabbed panel inside the left, middle or right tab for selection.
    String nameTabPanel = WidgetNames.panelFavoritesLeftMidRight + cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelSelectionTabs = mng.addTabbedPanel(nameTabPanel, null, GralGridBuild_ifc.propZoomedPanel);
    //The panel for selection from all favorites: 
    nameGridPanel = WidgetNames.tabMainFavorites + cNr;
    tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelSelectionTabs.addGridPanel(nameGridPanel, tabLabelGridPanel, 1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTableAll.setToPanel(mng, WidgetNames.selectMainFavorites + cNr, 5, widthSelecttable, 'A');
    fillInTables(which);  //build the rest of all tabs and panels depending on content of favorites.
    
    if(cNr == '1'){ //commands only in the left panel.
      tabbedPanelFileTabs.addGridPanel("cmd", "Cm&d",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{10,10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  

  /**
   * @param which Number 1 2 3 for left, mid, right
   */
  void fillInTables(int which){
    selectTableAll.clear();
    for(FcmdFileTable fileTabs: listTabs){
      fileTabs.selectTableForTab.clear();
    }
    //List of favor pathes for this main panel
    for(FcmdFavorPathSelector.SelectInfo info: selectListAllFavorites){ //panel specific favorites
      int mMainTab = 1 << (which-1);  //1, 2 or 4
      if((info.mMainPanel & mMainTab) !=0 && info.label !=null && info.label.length() >0){
        //create Panels for the file table and favor path table if not found yet, otherwise search it.
        FcmdFileTable fileTabs = searchOrCreateFileTabs(info.label);
          //Favor select list of the associated File table
        fileTabs.selectTableForTab.add(info);
      }
      selectTableAll.add(info);
      //tabSelector.initActDir(indexActualDir, info.selectName, info.path);
     
    }
    //
    //List of all favor pathes (for all main panels)
    for(FcmdFavorPathSelector.SelectInfo info: main.favorPathSelector.selectAll){ //all favorites
      int mMainTab = 1 << (which-1);  //1, 2 or 4
      if((info.mMainPanel & mMainTab) !=0 && info.label !=null && info.label.length() >0){
        //create Panels for the file table and favor path table if not found yet, otherwise search it.
        FcmdFileTable fileTabs = searchOrCreateFileTabs(info.label);
          //Favor select list of the associated File table
        fileTabs.selectTableForTab.add(info);
      }
      selectTableAll.add(info);
      //tabSelector.initActDir(indexActualDir, info.selectName, info.path);
    }
  }
  
  
  
  FcmdFileTable searchOrCreateFileTabs(String label){
  //search or create the tab
    FcmdFileTable fileTab = null;
    String labelTab = label + "." + cNr;
    for(FcmdFileTable item: listTabs){
      if(item.nameFilePanel.equals(labelTab)){ 
        fileTab = item; break;
      }
    } if(fileTab == null){
      fileTab = new FcmdFileTable(this, label);
      listTabs.add(fileTab);
    }
    return fileTab;
  }
  
  
  
  
  
}
