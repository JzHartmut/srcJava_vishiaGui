package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.FileSelector;

import org.vishia.util.FileRemote;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This is one file table in the Java commander. Each main panel (left, middle, right)
 * has maybe more as one tabs, each tab has exactly one file table. The file table is reused
 * for the several tabs of the main panel, and they are reused too if the directory is changed.
 * @author Hartmut Schorrig
 *
 */
public class FcmdFileCard extends FileSelector
{
  /**Table widget for the select table of the file tab.*/
  FcmdFavorCard favorCard;

  /**The component */
  final Fcmd main;
  
  /**The left, mid or right main panel where this tabbed file table is associated. */
  final FcmdLeftMidRightPanel mainPanel;
  
  /**The organization unit for this FileSelector. */
  //final LeftMidRightPanel.FileTabs fileTabs;
  
  /**The search-name of the tabbed file panel where this Table is placed on. 
   * It is the visible label of the tab, following by ".1" till ".3" for the three panels. */
  final String nameFilePanel;
  
  /**The label which is written in the line of favor file after l:label m:label r:label
   * It is the label on the tab.
   */
  final String label;
  
  /**Association to the current used favor path selection.
   * Note that this instance is re-used for more as one selection.
   */
  FcmdFavorPathSelector.FavorPath favorPathInfo;
  
  FileRemote currentFile;
  
  DateFormat formatDateInfo = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
  
  /**Creates an instance and creates the Panel and List for the files and for the path (favorite)
   * selection.
   * @param tabbedPanelP The outer class. (Access is more broadly than using an non-static class)
   * @param label The label of the tab, it builds the name of all widgets.
   */
  FcmdFileCard(FcmdLeftMidRightPanel mainPanelP, String label){
    super();
    this.label = label;
    this.main = mainPanelP.main;
    this.mainPanel = mainPanelP;
    this.nameFilePanel = label+ "." + mainPanelP.cNr;
    String namePanelFile = FcmdWidgetNames.tableFile + nameFilePanel;
    main.idxFileSelector.put(namePanelFile, this); //it is WidgetNames.tableFile + label +.123, see super(...) 
    GralWidgetMng mng = main.gralMng;
    ///
    ///
    favorCard = new FcmdFavorCard(main, this, mainPanel);
    String nameTableSelection = FcmdWidgetNames.tableFavorites + nameFilePanel;
    mainPanel.tabbedPanelFavorCards.addGridPanel(FcmdWidgetNames.tabFavorites + nameFilePanel, label,1,1,10,10);
    mng.setPosition(2, 0, 0, -0, 1, 'd');  ///p
    favorCard.setToPanel(mng, nameTableSelection, 5, mainPanel.widthSelecttableSub, 'A');
    favorCard.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.favorpath.favorSelect.");
    //mng.selectPanel(WidgetNames.panelFavoritesLeftMidRight +mainPanel.cNr);
    //String sLabelTab = "file&"+cNr;
    //The grid panel contains this widget. The grid panel is a tab of mainPanel.tabbedPanel
    mainPanel.tabbedPanelFileCards.addGridPanel(FcmdWidgetNames.tabFile + nameFilePanel, label,1,1,10,10);
    //to show the properties of the selected file in the info line:
    //
    //sets this Widget to the selected panel, it is the grid panel which was created even yet.
    setToPanel(mng, namePanelFile, 5, new int[]{2,20,5,10}, 'A');
    super.selectList.wdgdTable.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.fileSelect.");
    selectList.wdgdTable.setActionOnLineSelected(actionFileSelected);
  }

 
  /**Add all favor paths from the SelectTab newly
   * @param favorTabInfo
   */
  void fillFavorPaths(FcmdFavorPathSelector.FavorFolder favorTabInfo)
  {
    favorCard.clear();
    for( FcmdFavorPathSelector.FavorPath favorPathInfo: favorTabInfo.listfavorPaths){
      favorCard.add(favorPathInfo);
    }

  }
  
  
  
  
  
  @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
  { boolean ret = true;
    FileRemote data = (FileRemote)oData;
    switch(keyCode){
    case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{data}, "ordersBackground"); break;
    default: ret = false;
    }
    if (keyCode == main.keyActions.keyCreateFavorite){
      main.favorPathSelector.windAddFavorite.panelInvocation = mainPanel;
      main.favorPathSelector.windAddFavorite.widgLabel.setText(nameFilePanel);
      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
      FileRemote lastSelectedFile = getSelectedFile();
      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
      main.favorPathSelector.windAddFavorite.widgPath.setText(lastSelectedFile.getParent());
      main.favorPathSelector.windAddFavorite.window.setWindowVisible(true);
    } else if (keyCode == main.keyActions.keyPanelSelection){
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
      String mainPanelId = mainPanel == main.favorPathSelector.panelRight ? ".2" : ".1";
      for(GralWidget widg: main.gralMng.getWidgetsInFocus()){
        if(widg.name.contains(mainPanelId)){
          widg.setFocus();
        }
      }
    } else if (keyCode == main.keyActions.keyMainPanelRight){
      String mainPanelId = mainPanel == main.favorPathSelector.panelLeft ? ".2" : ".3";
      for(GralWidget widg: main.gralMng.getWidgetsInFocus()){
        if(widg.name.contains(mainPanelId)){
          widg.setFocus();
        }
      }
    } else if (main.executer.actionExecuteUserKey(keyCode, data)){
      ret = true;
    } else {
      ret = false;
    }
    return ret;
  }

  
  
  private void actionFileSelected(FileRemote file){
    //note the file, able to use for some actions.
    currentFile = file;
    main.currentFile = file;
    main.selectedFiles123[mainPanel.ixMainPanel] = file;
    //note the file card in order of usage.
    if(main.lastFileCards.size() == 0 || main.lastFileCards.get(0) != this){
      main.lastFileCards.remove(this);  //if it is in list on higher position
      main.lastFileCards.add(0, this);
    }
    String sDate = formatDateInfo.format(new Date(file.lastModified()));
    String sLenShort = //String.format("", file.length)
      file.length() >= 1000000 ? String.format("%2.1f MByte", file.length()/1000000.0) :
      file.length() >=    1000 ? String.format("%2.1f kByte", file.length()/1000.0) :
      String.format("%3d Byte", file.length());  
    String info = sDate + " # " + sLenShort + " >" + file.getName() + "<";        
    main.widgFileInfo.setText(info);
    
  }
  
  
  
  
  
  /**Action to show the file properties in the info line. This action is called anytime if a line
   * was changed in the file view table. */
  GralUserAction actionFileSelected = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object[] params) {
      if(actionCode == KeyCode.tableLineSelect){
        GralTableLine_ifc line = (GralTableLine_ifc) params[0];
        Object oData = line.getUserData();
        if(oData instanceof FileRemote){
          actionFileSelected((FileRemote)oData);
        }
      }
      return true;
    }
  };
  
  
  
  @Override public String toString(){ return label + "/" + nameFilePanel; }
  
  
}
