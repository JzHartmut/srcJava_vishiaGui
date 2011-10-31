package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.widget.FileSelector;

/**Data for one tabbed panel
 */
public class TabbedPanelData
{
  /**Instance which works with all Tabs. */
  final SelectTab tabSelector;
  
  /**The container for all tabs of one TabbedPanel. */
  GralTabbedPanel tabbedPanel;
  
  /**All entries for the left select list in order of the file. */
  List<SelectTab.SelectInfo> selectList = new LinkedList<SelectTab.SelectInfo>();

  /**Table widget for the three select tables.*/
  SelectTab.SelectTabList selectTable;

  final Map<String, File> indexActualDir = new TreeMap<String, File>();
  
  FileSelector fileSelectorMain;
  
  String actualDir;
  
  final char cc;
  
  final char cNr;
  
  TabbedPanelData(char cc, char cNr, SelectTab tabSelector, GralWidgetMng mng){
    this.cc = cc;
    this.cNr = cNr;
    this.tabSelector = tabSelector;
    selectTable = tabSelector.new SelectTabList("panel-" + cNr, this, mng);
  }
  
  /**Fills the table list left. 
   * It have to be called in the graphic thread.
   */
  void fillInTable()
  { selectTable.clear();
    for(SelectTab.SelectInfo info: selectList){
      selectTable.add(info, cc);
      tabSelector.initActDir(indexActualDir, info.selectName, info.path);
    }
    for(SelectTab.SelectInfo info: tabSelector.selectAll){
      selectTable.add(info, cc);
      tabSelector.initActDir(indexActualDir, info.selectName, info.path);
    }
  }
  
  
  
}
