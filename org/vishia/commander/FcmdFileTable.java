package org.vishia.commander;

import java.io.File;

import org.vishia.commander.FavoritePathSelector.SelectTabList;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralTableLine_ifc;
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
  FavoritePathSelector.SelectTabList selectTableForTab;

  final JavaCmd main;
  
  final LeftMidRightPanel mainPanel;
  
  /**The organization unit for this FileSelector. */
  //final LeftMidRightPanel.FileTabs fileTabs;
  
  final String labelTab;
  
  String actualDir;
  
  /**Creates an instance and creates the Panel and List for the files and for the path (favorite)
   * selection.
   * @param tabbedPanelP The outer class. (Access is more broadly than using an non-static class)
   * @param label The label of the tab, it builds the name of all widgets.
   */
  FcmdFileTable(LeftMidRightPanel mainPanelP, String label){
    super(WidgetNames.tableFile + label, (GralWidgetMng)mainPanelP.main.panelMng);
    this.main = mainPanelP.main;
    this.mainPanel = mainPanelP;
    this.labelTab = label;
    main.idxFileSelector.put(this.name, this); //it is WidgetNames.tableFile + label, see super(...) 
    GralWidgetMng mng = main.panelMng;
    ///
    ///
    selectTableForTab = main.selectTab.new SelectTabList(WidgetNames.tableFavorites + label, mainPanel, mng);
    mainPanel.tabbedPanelSelectionTabs.addGridPanel(WidgetNames.tabFavorites + label, label,1,1,10,10);
    mng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTableForTab.setToPanel(mng, label+"l", 5, mainPanel.widthSelecttable, 'A');
    //mng.selectPanel(WidgetNames.panelFavoritesLeftMidRight +mainPanel.cNr);
    //String sLabelTab = "file&"+cNr;
    //The grid panel contains this widget. The grid panel is a tab of mainPanel.tabbedPanel
    mainPanel.tabbedPanelFileTabs.addGridPanel(WidgetNames.tabFile + label, label,1,1,10,10);
    setActionOnEnterFile(mainPanel.main.executer.actionExecute);
    //
    //sets this Widget to the selected panel, it is the grid panel which was created even yet.
    String nameTab = WidgetNames.tableFile + label;
    setToPanel(mng, nameTab, 5, new int[]{2,20,5,10}, 'A');
  }

  
  
  @Override public boolean actionUserKey(int keyCode, Object data, GralTableLine_ifc line)
  { boolean ret = true;
    File file = (File)(data);
    switch(keyCode){
    case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{file}, "ordersBackground"); break;
    default: ret = false;
    }
    if (keyCode ==main.keyActions.keyCreateFavorite){
      main.selectTab.windAddFavorite.panelInvocation = mainPanel;
      main.selectTab.windAddFavorite.widgTab.setText(labelTab);
      main.selectTab.windAddFavorite.widgShortName.setText("alias");
      File lastSelectedFile = getSelectedFile();
      String pathDir = FileSystem.getCanonicalPath(lastSelectedFile.getParentFile());
      main.selectTab.windAddFavorite.widgPath.setText(pathDir);
      main.selectTab.windAddFavorite.window.setWindowVisible(true);
    }
    return ret;
  }


  
  
}
