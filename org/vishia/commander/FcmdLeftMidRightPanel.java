package org.vishia.commander;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.SelectList;
import org.vishia.mainCmd.MainCmd;
import org.vishia.util.KeyCode;

/**Data for one panel left, middle or right.
 * It contains the tab-card for the favor folder selection.
 */
public class FcmdLeftMidRightPanel
{
  
  /**Version and history
   * <ul>
   * <li>2012-01-08 Hartmut the extra class FcmdFavorTabCard is now an inner class here because
   *   it needs some elements shared with this outer class.
   * <li>2011-11-00 Hartmut creation
   * </ul>
   */
  public static final int version = 0x20120113;
  
  final Fcmd main;
  
  /**The container for all tabs of this TabbedPanel. */
  GralTabbedPanel tabbedPanelFileCards;
  
  /**The container for the tabs for selection. */
  GralTabbedPanel tabbedPanelFavorCards;
  
  /**Table widget for the select table.*/
  FcmdFavorTabCard selectTabCard;

  /**List of all Tabs of this Panel, used and unused. This tabs are presented in the {@link FcmdFavorTabCard} table*/
  List<FcmdFileCard> listTabs = new LinkedList<FcmdFileCard>();
  
  /**The current opened file card. */
  FcmdFileCard actFileCard;
  
  //final FcmdFavorPathSelector.FavorFolder actFavorFolder;
  
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
  
  //final int[] widthSelecttableMain = new int[]{6, 20, 30};

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
    selectTabCard.setToPanel(mng, FcmdWidgetNames.tableFavoritesMain + cNr, 'A');
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
    //insert from left, from right etc. with the panels as first elements in the list:
    final FcmdLeftMidRightPanel favorFolderFrom1, favorFolderFrom2;
    final String from1, from2;
    switch(cc){
      case 'l':{ 
        favorFolderFrom1 = main.favorPathSelector.panelMid;
        favorFolderFrom2 = main.favorPathSelector.panelRight;
        from1 = "from left"; from2 = "from mid";
      } break;  
      case 'm':{ 
        favorFolderFrom1 = main.favorPathSelector.panelLeft;
        favorFolderFrom2 = main.favorPathSelector.panelRight;
        from1 = "from mid"; from2 = "from rigth";
      } break;  
      case 'r':{ 
        favorFolderFrom1 = main.favorPathSelector.panelLeft;
        favorFolderFrom2 = main.favorPathSelector.panelMid;
        from1 = "from left"; from2 = "from right";
      } break;  
      default: favorFolderFrom1 = favorFolderFrom2 = null;
      from1 = from2 = null;
    }
    final String[] cells = new String[2];
    cells[0] = "";
    cells[1] = from1;
    selectTabCard.wdgdTable.insertLine(null, -1, cells, favorFolderFrom1);
    cells[1] = from2;
    selectTabCard.wdgdTable.insertLine(null, -1, cells, favorFolderFrom2);

    //List of favor pathes for this main panel
    for(FcmdFavorPathSelector.FavorFolder favorFolder: main.favorPathSelector.listAllFavorPathFolders){ //panel specific favorites
      int mMainTab = 0x1 << (cNr-'1');  //1, 2 or 4
      if((favorFolder.mMainPanel & mMainTab) !=0 && favorFolder.label !=null && favorFolder.label.length() >0){
        //create Panels for the file table and favor path table if not found yet, otherwise search it.
        FcmdFileCard fileTab = searchOrCreateFileCard(favorFolder.label);
          //Favor select list of the associated File table
        fileTab.favorCard.fillFavorPaths(favorFolder);
      } else {
        //The fileTable may be existend, then 
        FcmdFileCard fileTab = searchFileCard(favorFolder.label);
        if(fileTab !=null && fileTab.label.equals(favorFolder.label)){
          fileTab.favorCard.fillFavorPaths(favorFolder);
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
    
    
  class FcmdFavorTabCard extends SelectList
  {
    /**Index of all entries in the visible list. */
    Map<String, FcmdFavorPathSelector.FavorFolder> indexFavorFolders = new TreeMap<String, FcmdFavorPathSelector.FavorFolder>();
    
    final int[] widthSelecttableMain = new int[]{10, 30};

    public FcmdFavorTabCard(Fcmd main, FcmdLeftMidRightPanel panel)
    { //super(name, mng);
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
        String[] cells = new String[2];
        cells[0] = favorTabInfo.label;
        cells[1] = favorTabInfo.selectNameTab;
        GralTableLine_ifc line = wdgdTable.insertLine(null, -1, cells, favorTabInfo);
        //line.setUserData(favorTabInfo);
        //line.setCellText(favorTabInfo.label, 0);
        //line.setCellText(favorTabInfo.selectNameTab, 1);
        line.redraw();
      }
    }
    
    
    void clear()
    {
      wdgdTable.clearTable();
    }
    
    
    @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
    {
      FcmdFavorPathSelector.FavorFolder favorTabInfo = null;
      final String label;
      String currentDir = null;
      Object oLineData = line.getUserData();
      if(oLineData instanceof FcmdFavorPathSelector.FavorFolder){
        favorTabInfo = (FcmdFavorPathSelector.FavorFolder)line.getUserData();
        label = favorTabInfo.label;  //from favorite list
        //label is known in the favorite list, use it. The panel should be existing or it is created.
        //search or create the tab, representing by its fileTable:
        actFileCard = searchOrCreateFileCard(label);
        //before changing the content of this fileTable, store the current directory
        //to restore if this favor respectively selection is used ones more.
        if(actFileCard.favorPathInfo !=null){
          
          currentDir = actFileCard.getCurrentDir().getAbsolutePath();
        } else {
          //nothing selected, its a new tab
          FcmdFavorPathSelector.FavorPath favorPathInfo = favorTabInfo.listfavorPaths.get(0);
          //fill in the standard file panel, use maybe a current directory.
          actFileCard.favorPathInfo = favorPathInfo;
          if(  wdgdTable.name.startsWith(FcmdWidgetNames.tableFavoritesMain)   //use the root dir anytime if the main favor path table is used.
            || (currentDir  = indexActualDir.get(favorPathInfo.selectName)) == null){  //use the root if the entry wasn't use till now
            currentDir = favorPathInfo.path;
          }
        }
      } else {
        //it have to be a:
        final FcmdLeftMidRightPanel panel = (FcmdLeftMidRightPanel)oLineData;  //from left, from mid etc
        final FcmdFileCard fileCard = panel.actFileCard;  //the current filecard in the other panel
        label = fileCard.label;   
        actFileCard = searchOrCreateFileCard(label);     //search or create such filecard with this label here.
        actFileCard.favorPathInfo = fileCard.favorPathInfo;  //copy it, it is the same instance for all 3 panels.
        actFileCard.currentFile = fileCard.currentFile;      //select the same file.
        currentDir = fileCard.getCurrentDir().getAbsolutePath();
        if(actFileCard == null){
          MainCmd.assertion(false);
        }
        //search the proper FavorFolder for the label. 
        //Note it isn't stored in the file card yet though the file card is associated to the label.
        for(FcmdFavorPathSelector.FavorFolder folder: main.favorPathSelector.listAllFavorPathFolders){
          if(folder.label.equals(label)){
            favorTabInfo = folder;
            break;  //found.
          }
        }
        MainCmd.assertion(favorTabInfo != null);
      }
    
      //adds all favorite pathes to it newly.
      actFileCard.favorCard.fillFavorPaths(favorTabInfo);
     
      if(currentDir !=null){
        indexActualDir.put(actFileCard.favorPathInfo.selectName, currentDir);
      } 
      actFileCard.fillIn(currentDir);
      
      actFileCard.favorCard.setFocus();
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
        FcmdFileCard fileTableRight = listTabs.get(0);
        if(fileTableRight !=null){
          fileTableRight.favorCard.wdgdTable.setFocus();
        }
      } else if (key == main.keyActions.keyMainPanelLeft){
        String mainPanelId = FcmdLeftMidRightPanel.this == main.favorPathSelector.panelRight ? ".2" : ".1";
        for(GralWidget widg: main.gralMng.getWidgetsInFocus()){
          if(widg.name.contains(mainPanelId)){
            widg.setFocus();
          }
        }
      } else if (key == main.keyActions.keyMainPanelRight){
        String mainPanelId = FcmdLeftMidRightPanel.this == main.favorPathSelector.panelLeft ? ".2" : ".3";
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
  
  
  
}
