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
public class FcmdLeftMidRightPanel
{
  final Fcmd main;
  
  /**The container for all tabs of this TabbedPanel. */
  GralTabbedPanel tabbedPanelFileCards;
  
  /**The container for the tabs for selection. */
  GralTabbedPanel tabbedPanelFavorCards;
  
  /**All entries for the select list for all favorites in order of the file. */
  List<FcmdFavorPathSelector.FavorPath> listAllFavorPaths = new LinkedList<FcmdFavorPathSelector.FavorPath>();


  /**Table widget for the select table.*/
  FcmdFavorTabCard selectTabCard;



  

  List<FcmdFileCard> listTabs = new LinkedList<FcmdFileCard>();
  
  /**Stores the current directory for all actual file panels. */
  final Map<String, String> indexActualDir = new TreeMap<String, String>();
  
  /**File panel. */
  //FcmdFileTable fileSelectorMain;
  
  //String actualDir;
  
  /**Characteristic char for the panel: l, m, r*/
  final char cc;
  
  /**Characteristic number for the panel: 1, 2, 3 */
  final char cNr;
  
  final int ixMainPanel;
  
  final int[] widthSelecttableMain = new int[]{6, 20, 30};

  final int[] widthSelecttableSub = new int[]{2, 20, 30};

  
  FcmdLeftMidRightPanel(Fcmd javaCmd, char cc, char cNr, GralWidgetMng mng){
    this.main = javaCmd;
    this.cc = cc;
    this.cNr = cNr;
    this.ixMainPanel = cNr - '1';
    selectTabCard = new FcmdFavorTabCard(main, this);
  }
  
  
  
  /**Build the initial content of one of the three tabbed panels, called in the build phase of the GUI.
   * @param which Number 1 2 3 for left, mid, right
   */
  void buildInitialTabs()
  {
    GralWidgetMng mng = main.gralMng;
    String sName = "Sel" + cNr;
    //inside the left/mid/right tabbed panel: create the panel which contains a tabbed panel for selection
    String nameGridPanel = FcmdWidgetNames.tabFavoritesLeftMidRight + cNr;
    String tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelFileCards.addGridPanel(nameGridPanel, tabLabelGridPanel,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    //A tabbed panel inside the left, middle or right tab for selection.
    String nameTabPanel = FcmdWidgetNames.panelFavoritesLeftMidRight + cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelFavorCards = mng.addTabbedPanel(nameTabPanel, null, GralGridBuild_ifc.propZoomedPanel);
    //The panel for selection from all favorites: 
    nameGridPanel = FcmdWidgetNames.tabMainFavorites + cNr;
    tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelFavorCards.addGridPanel(nameGridPanel, tabLabelGridPanel, 1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTabCard.setToPanel(mng, FcmdWidgetNames.tableFavoritesMain + cNr, 5, widthSelecttableMain, 'A');
    fillCards();  //build the rest of all tabs and panels depending on content of favorites.
    
    if(cNr == '1'){ //commands only in the left panel.
      tabbedPanelFileCards.addGridPanel("cmd", "Cm&d",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{10,10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  

  /**
   * @param which Number 1 2 3 for left, mid, right
   */
  void fillCards(){
    
    selectTabCard.clear();   //the common favor path table.
    //clear index of entries, it is a mirror of content of the GUI-visible table and prevents
    //twice adding.
    selectTabCard.indexFavorFolders.clear();  
    //clear all GUI tables of this main tab.
    for(FcmdFileCard fileTabs: listTabs){
      fileTabs.favorCard.clear();
      fileTabs.favorCard.indexFavorPaths.clear();
    }
    //List of favor pathes for this main panel
    for(FcmdFavorPathSelector.FavorFolder favorFolder: main.favorPathSelector.listAllFavorPathFolders){ //panel specific favorites
      int mMainTab = 0x1 << (cNr-'1');  //1, 2 or 4
      if((favorFolder.mMainPanel & mMainTab) !=0 && favorFolder.label !=null && favorFolder.label.length() >0){
        //create Panels for the file table and favor path table if not found yet, otherwise search it.
        FcmdFileCard fileTabs = searchOrCreateFileCard(favorFolder.label);
          //Favor select list of the associated File table
        fileTabs.fillFavorPaths(favorFolder);
      } else {
        //The fileTable may be existend, then 
        FcmdFileCard fileTab = searchFileCard(favorFolder.label);
        if(fileTab !=null && fileTab.label.equals(favorFolder.label)){
          fileTab.fillFavorPaths(favorFolder);
        }
      }
      selectTabCard.addFavorFolder(favorFolder);
      //tabSelector.initActDir(indexActualDir, info.selectName, info.path);
     
    }
  }
  
  
  
  FcmdFileCard searchOrCreateFileCard(String label){
  //search or create the tab
    FcmdFileCard fileCard = null;
    String labelTab = label + "." + cNr;
    for(FcmdFileCard item: listTabs){
      if(item.nameFilePanel.equals(labelTab)){ 
        fileCard = item; break;
      }
    } 
    if(fileCard == null){
      fileCard = new FcmdFileCard(this, label);
      listTabs.add(fileCard);
    }
    return fileCard;
  }
  
    
    
  FcmdFileCard searchFileCard(String label){
  //search or create the tab
    FcmdFileCard fileCard = null;
    String labelTab = label + "." + cNr;
    for(FcmdFileCard item: listTabs){
      if(item.nameFilePanel.equals(labelTab)){ 
        fileCard = item; break;
      }
    } 
    return fileCard;
  }
    
    
    
  
  
}
