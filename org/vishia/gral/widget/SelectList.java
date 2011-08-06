package org.vishia.gral.widget;

import java.util.List;

import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.WidgetDescriptor;

/**Complex widget which contains a list what's items are able to select. 
 * It is the base class for file selection and command selection.
 * 
 * @author Hartmut Schorrig
 *
 */
public class SelectList
{
  /**The table which is showing in the widget. */
  WidgetDescriptor wdgdTable;
  
  void add(String name, GuiPanelMngBuildIfc panel, List<String[]> data, int rows, int[] columns, char size)
  {
    wdgdTable = panel.addTable(name, rows, columns);
  }
  
  
  
  void set(List<String[]> listData)
  {
    for(String[] data: listData){
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, data[0]);
    }
  }
  

}
