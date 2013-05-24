package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.GralSelectList;
import org.vishia.mainCmd.MainCmd;
import org.vishia.util.Assert;
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
  FcmdFavorThemeCard cardFavorThemes;

  /**List of all Tabs of this Panel, used and unused. This tabs are presented in the {@link FcmdFavorThemeCard} table*/
  List<FcmdFileCard> listTabs = new LinkedList<FcmdFileCard>();
  
  /**The current opened file card. */
  FcmdFileCard actFileCard;
  
  /**If the favor card is in foreground, it contains true. If the file card is in foreground, it contains false.
   * The information will be set in the select line methods of the table.
   */
  boolean bFavorCardHasFocus = true;
  
  boolean bFavorThemeCardHasFocus= true;
  
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
  
  /**Index of panel 0, 1, 2. */
  final int ixMainPanel;
  
  
  /**The order number of the panel in order of usage.
   * 1 = actual focused panel, 2 = last focused, 3 = third focused, 0 = no file panel. */
  int orderMainPanel;
  
  //final int[] widthSelecttableMain = new int[]{6, 20, 30};

  final int[] widthSelecttableSub = new int[]{2, 20, 30};

  
  FcmdLeftMidRightPanel(Fcmd javaCmd, char cc, char cNr, GralMng mng){
    this.main = javaCmd;
    this.cc = cc;
    this.cNr = cNr;
    this.ixMainPanel = cNr - '1';
    cardFavorThemes = new FcmdFavorThemeCard(main, this);
    
  }
  
  
  
  /**Build the initial content of one of the three tabbed panels, called in the build phase of the GUI.
   * @param which Number 1 2 3 for left, mid, right
   */
  void buildInitialTabs()
  {
    GralMng mng = main.gralMng;
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
    tabbedPanelFavorCards = mng.addTabbedPanel(nameTabPanel, null, GralMngBuild_ifc.propZoomedPanel);
    //The panel for selection from all favorites: 
    nameGridPanel = FcmdWidgetNames.tabMainFavorites + cNr;
    tabLabelGridPanel = "a-F"+cNr;
    mng.setPosition(0, 0, 0, 0, 1, 'd');
    tabbedPanelFavorCards.addGridPanel(nameGridPanel, tabLabelGridPanel, 1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    cardFavorThemes.setToPanel(mng, FcmdWidgetNames.tableFavoritesMain + cNr, 'A');
    fillCards();  //build the rest of all tabs and panels depending on content of favorites.
    
    if(cNr == '1'){ //commands only in the left panel.
      tabbedPanelFileCards.addGridPanel("cmd", "Cmd",1,1,10,10);
      mng.setPosition(2, -2, 0, -0, 1, 'd');
      main.cmdSelector.setToPanel(mng, "cmds", 5, new int[]{0,-10}, 'A');
      main.cmdSelector.fillIn();
      main.cmdSelector.setGetterFiles(main.getterFiles);
    }
    
  }
  

  void setFocus(){
    main.setLastSelectedPanel(this);
    if(actFileCard !=null){
      actFileCard.setFocusFavorOrFile();
    } else {
      cardFavorThemes.setFocus();
    }
  }
  

  
  /**
   * @param which Number 1 2 3 for left, mid, right
   */
  void fillCards(){
    
    cardFavorThemes.clear();   //the common favor path table.
    //clear index of entries, it is a mirror of content of the GUI-visible table and prevents
    //twice adding.
    cardFavorThemes.indexFavorFolders.clear();  
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
        from1 = "from mid"; from2 = "from right";
      } break;  
      case 'm':{ 
        favorFolderFrom1 = main.favorPathSelector.panelLeft;
        favorFolderFrom2 = main.favorPathSelector.panelRight;
        from1 = "from left"; from2 = "from rigth";
      } break;  
      case 'r':{ 
        favorFolderFrom1 = main.favorPathSelector.panelLeft;
        favorFolderFrom2 = main.favorPathSelector.panelMid;
        from1 = "from left"; from2 = "from mid";
      } break;  
      default: favorFolderFrom1 = favorFolderFrom2 = null;
      from1 = from2 = null;
    }
    final String[] cells = new String[2];
    cells[0] = "";
    cells[1] = from1;
    cardFavorThemes.wdgdTable.insertLine(null, -1, cells, favorFolderFrom1);
    cells[1] = from2;
    cardFavorThemes.wdgdTable.insertLine(null, -1, cells, favorFolderFrom2);

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
      cardFavorThemes.addFavorFolder(favorFolder);
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
    
  
  void checkRefresh(long since){
    if(actFileCard !=null) actFileCard.checkRefresh(since);
  }
  
  
  @Override public String toString(){ return "panel " + cc; }
  
  
  boolean focusLeftCard(){ 
    FcmdFileCard fileTableLeft = null;
    boolean found = false;
    for(FcmdFileCard fileTable: listTabs){
      if(fileTable == actFileCard){ found = true;  break;}
      fileTableLeft = fileTable;  //save this table as table left, use if found.
    }
    if(found){
      if(fileTableLeft !=null){
        actFileCard = fileTableLeft;
        fileTableLeft.setFocusFavorOrFile(); 
      } else {  //left from first is the selectAllTable of this panel.
        cardFavorThemes.setFocus();
      }
    }
    return true; 
  }
  
  
  boolean focusRightCard(){ 
    //sets focus to right
    FcmdFileCard fileTableRight = null;
    boolean found = bFavorThemeCardHasFocus; //true if the left card is focused.
    for(FcmdFileCard fileTable: listTabs){
      if(found){ fileTableRight = fileTable; break; }  //use this next table if found before.
      if(fileTable == actFileCard) { found = true; }
    }
    if(fileTableRight !=null){
      actFileCard = fileTableRight;
      fileTableRight.setFocusFavorOrFile(); 
    } else {
      //remain the last
    }
    return true; 
  }
  
  
    
  static class FcmdFavorThemeCard extends GralSelectList
  {
    
    private final FcmdLeftMidRightPanel mainPanel;
    
    /**Index of all entries in the visible list. */
    Map<String, FcmdFavorPathSelector.FavorFolder> indexFavorFolders = new TreeMap<String, FcmdFavorPathSelector.FavorFolder>();
    
    final int[] widthSelecttableMain = new int[]{10, 30};

    public FcmdFavorThemeCard(Fcmd main, FcmdLeftMidRightPanel panel)
    { //super(name, mng);
      mainPanel = panel;
    }

    
    
    public void setToPanel(GralMngBuild_ifc panel, String name, char size){
      super.setToPanel(panel, name, 20, widthSelecttableMain, size);
      wdgdTable.specifyActionOnLineSelected(actionFavorThemeLineSelected);
      wdgdTable.setHtmlHelp(mainPanel.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.tabSelect.");
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
        line.repaint(100, 0);
      }
    }
    
    
    @Override public boolean setFocus(){
      mainPanel.bFavorThemeCardHasFocus = true;
      return super.setFocus();
    }
    
    void clear()
    {
      wdgdTable.clearTable();
    }
    
    
    @Override protected boolean actionOk(Object userData, GralTableLine_ifc line)
    {
      FcmdFavorPathSelector.FavorFolder favorTabInfo = null;
      final String label;
      final FileRemote currentDir;
      Object oLineData = line.getUserData();
      if(oLineData instanceof FcmdFavorPathSelector.FavorFolder){
        favorTabInfo = (FcmdFavorPathSelector.FavorFolder)line.getUserData();
        label = favorTabInfo.label;  //from favorite list
        //label is known in the favorite list, use it. The panel should be existing or it is created.
        //search or create the tab, representing by its fileTable:
        mainPanel.actFileCard = mainPanel.searchOrCreateFileCard(label);
        //before changing the content of this fileTable, store the current directory
        //to restore if this favor respectively selection is used ones more.
        if(mainPanel.actFileCard.favorPathInfo !=null){
          currentDir = mainPanel.actFileCard.getCurrentDir(); //.getAbsolutePath();
        } else {
          //nothing selected, its a new tab. Don't show files.
          currentDir = null;
          //FcmdFavorPathSelector.FavorPath favorPathInfo = favorTabInfo.listfavorPaths.get(0);
          //actFileCard.favorPathInfo = favorPathInfo;
          //currentDir = favorPathInfo.getOriginDir();
        }
      } else {
        //it have to be a:
        final FcmdLeftMidRightPanel panel = (FcmdLeftMidRightPanel)oLineData;  //from left, from mid etc
        final FcmdFileCard fileCard = panel.actFileCard;  //the current filecard in the other panel
        label = fileCard.label;   
        mainPanel.actFileCard = mainPanel.searchOrCreateFileCard(label);     //search or create such filecard with this label here.
        mainPanel.actFileCard.favorPathInfo = fileCard.favorPathInfo;  //copy it, it is the same instance for all 3 panels.
        mainPanel.actFileCard.currentFile = fileCard.currentFile;      //select the same file.
        currentDir = fileCard.getCurrentDir(); //.getAbsolutePath();
        if(mainPanel.actFileCard == null){
          Assert.check(false);
        }
        //search the proper FavorFolder for the label. 
        //Note it isn't stored in the file card yet though the file card is associated to the label.
        for(FcmdFavorPathSelector.FavorFolder folder: mainPanel.main.favorPathSelector.listAllFavorPathFolders){
          if(folder.label.equals(label)){
            favorTabInfo = folder;
            break;  //found.
          }
        }
        Assert.check(favorTabInfo != null);
      }
    
      //adds all favorite pathes to it newly.
      mainPanel.actFileCard.favorCard.fillFavorPaths(favorTabInfo);
      if(currentDir !=null){
        mainPanel.indexActualDir.put(mainPanel.actFileCard.favorPathInfo.selectName, currentDir.getPath());
        mainPanel.actFileCard.fillIn(currentDir, false);
        mainPanel.actFileCard.setFocus();
      } else { 
        mainPanel.actFileCard.favorCard.setFocus();
      }
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
     * @see org.vishia.gral.widget.GralSelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override protected boolean actionUserKey(int key, Object userData, GralTableLine_ifc line)
    { boolean ret = true;
      ret = false;
      return ret;
    }

    /**Action is called any time if a line was focused in the favor theme table. */
    GralUserAction actionFavorThemeLineSelected = new GralUserAction(){
      @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
        if(actionCode == KeyCode.tableLineSelect){
          mainPanel.bFavorThemeCardHasFocus = true;
          GralTableLine_ifc line = (GralTableLine_ifc) params[0];
          Object oData = line.getUserData();
          //System.out.println("FcmdFavorCard.actionFavorSelected: " + fileTable.label);
        }
        return true;
      }
    };
    

    
  }
  
  
  
}
