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

import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This is one file table in the Java commander
 * @author hartmut
 *
 */
public class FcmdFileTable extends FileSelector
{
  /**Table widget for the select table of the file tab.*/
  FcmdFavorTable favorTable;

  /**The component */
  final JavaCmd main;
  
  /**The left, mid or right main panel where this tabbed file table is associated. */
  final LeftMidRightPanel mainPanel;
  
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
  FcmdFavorPathSelector.SelectInfo selectInfo;
  
  DateFormat formatDateInfo = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss");
  
  /**Creates an instance and creates the Panel and List for the files and for the path (favorite)
   * selection.
   * @param tabbedPanelP The outer class. (Access is more broadly than using an non-static class)
   * @param label The label of the tab, it builds the name of all widgets.
   */
  FcmdFileTable(LeftMidRightPanel mainPanelP, String label){
    super();
    this.label = label;
    this.main = mainPanelP.main;
    this.mainPanel = mainPanelP;
    this.nameFilePanel = label+ "." + mainPanelP.cNr;
    String namePanelFile = WidgetNames.tableFile + nameFilePanel;
    main.idxFileSelector.put(namePanelFile, this); //it is WidgetNames.tableFile + label +.123, see super(...) 
    GralWidgetMng mng = main.gralMng;
    ///
    ///
    favorTable = new FcmdFavorTable(main, mainPanel);
    String nameTableSelection = WidgetNames.tableFavorites + nameFilePanel;
    mainPanel.tabbedPanelSelectionTabs.addGridPanel(WidgetNames.tabFavorites + nameFilePanel, label,1,1,10,10);
    mng.setPosition(2, 0, 0, -0, 1, 'd');  ///p
    favorTable.setToPanel(mng, nameTableSelection, 5, mainPanel.widthSelecttableSub, 'A');
    //mng.selectPanel(WidgetNames.panelFavoritesLeftMidRight +mainPanel.cNr);
    //String sLabelTab = "file&"+cNr;
    //The grid panel contains this widget. The grid panel is a tab of mainPanel.tabbedPanel
    mainPanel.tabbedPanelFileTabs.addGridPanel(WidgetNames.tabFile + nameFilePanel, label,1,1,10,10);
    setActionOnEnterFile(mainPanel.main.executer.actionExecute);
    //to show the properties of the selected file in the info line:
    //
    //sets this Widget to the selected panel, it is the grid panel which was created even yet.
    setToPanel(mng, namePanelFile, 5, new int[]{2,20,5,10}, 'A');
    selectList.wdgdTable.setActionOnLineSelected(actionFileSelected);
  }

  
  
  @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
  { boolean ret = true;
    FileAndName data = (FileAndName)oData;
    switch(keyCode){
    case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{data.file}, "ordersBackground"); break;
    default: ret = false;
    }
    if (keyCode == main.keyActions.keyCreateFavorite){
      main.favorPathSelector.windAddFavorite.panelInvocation = mainPanel;
      main.favorPathSelector.windAddFavorite.widgTab.setText(nameFilePanel);
      main.favorPathSelector.windAddFavorite.widgShortName.setText("alias");
      FileAndName lastSelectedFile = getSelectedFile();
      //String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
      main.favorPathSelector.windAddFavorite.widgPath.setText(lastSelectedFile.path);
      main.favorPathSelector.windAddFavorite.window.setWindowVisible(true);
    } else if (keyCode == main.keyActions.keyPanelSelection){
      //focuses the panel which is the selection panel for this file table.
      GralWidget tableSelection = main.gralMng.getWidget(WidgetNames.tableFavorites + nameFilePanel);
      tableSelection.setFocus();
    } else if (keyCode == main.keyActions.keyPanelLeft){
      //sets focus to left
      FcmdFileTable fileTableLeft = null;
      boolean found = false;
      for(FcmdFileTable fileTable: mainPanel.listTabs){
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
      FcmdFileTable fileTableRight = null;
      boolean found = false; //(mainPanel.selectTableAll == this);
      for(FcmdFileTable fileTable: mainPanel.listTabs){
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
    }
    return ret;
  }

  
  
  /**Action to show the file properties in the info line. */
  GralUserAction actionFileSelected = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object[] params) {
      if(actionCode == KeyCode.tableLineSelect){
        GralTableLine_ifc line = (GralTableLine_ifc) params[0];
        Object oData = line.getUserData();
        if(oData instanceof FileAndName){
          FileAndName file = (FileAndName)oData;
          main.currentFile = file;
          main.selectedFiles123[mainPanel.ixMainPanel] = file;
          String sDate = formatDateInfo.format(new Date(file.date));
          String sLenShort = //String.format("", file.length)
            file.length >= 1000000 ? String.format("%2.1f MByte", file.length/1000000.0) :
            file.length >=    1000 ? String.format("%2.1f kByte", file.length/1000.0) :
            String.format("%3d Byte", file.length);  
          String info = sDate + " # " + sLenShort + " >" + file.name + "<";        
          main.widgInfo.setText(info);
        }
      }
      return true;
    }
  };
  
  
}
