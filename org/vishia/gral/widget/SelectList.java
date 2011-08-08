package org.vishia.gral.widget;

import java.util.List;

import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.UserActionGui;
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
  
  TableGui_ifc table;
  
  public void add(String name, GuiPanelMngBuildIfc panel, List<String[]> data, int rows, int[] columns, char size)
  {
    wdgdTable = panel.addTable(name, rows, columns);
    wdgdTable.setActionChange(actionTable);
    table = (TableGui_ifc)wdgdTable.widget;
  }
  
  
  
  public void set(List<String[]> listData)
  {
    for(String[] data: listData){
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, data[0]);
    }
  }
  
  
  UserActionGui actionTable = new UserActionGui()
  {

    @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    {
      TableGui_ifc table = (TableGui_ifc)infos.widget;
      TableLineGui_ifc line = table.getCurrentLine();
      Object data = line.getUserData();
    }
    
  };

  void stop(){}
  
}
