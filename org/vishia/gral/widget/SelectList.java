package org.vishia.gral.widget;

import java.util.List;
import java.util.Map;

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
public abstract class SelectList
{
  /**The table which is showing in the widget. */
  protected WidgetDescriptor wdgdTable;
  
  /**The table which is showing in the widget. */
  protected TableGui_ifc table;
  
  /**Not used yet, register actions? */
  protected Map<String, UserActionGui> actions;
  
  /**
   * @param panel
   * @param name
   * @param rows
   * @param columns
   * @param size
   */
  public void setToPanel(GuiPanelMngBuildIfc panel, String name, int rows, int[] columns, char size)
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
  
  
  /**Action if a table line is selected and entered. Its either a double click with the mouse
   * or click of OK (Enter) button.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionOk(Object userData, TableLineGui_ifc line);
  
  /**Action if a table line is selected and ctrl-left is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionLeft(Object userData, TableLineGui_ifc line);
  
  /**Action if a table line is selected and ctrl-right is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionRight(Object userData, TableLineGui_ifc line);
  
  
  /**Action if a table line is selected and any other key is pressed or the context menu is invoked.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionUserKey(String sKey, Object userData, TableLineGui_ifc line);
  
  
  private UserActionGui actionTable = new UserActionGui()
  {

    @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    {
      TableGui_ifc table = (TableGui_ifc)infos.widget;
      TableLineGui_ifc line = table.getCurrentLine();
      Object data = line.getUserData();
      if(sIntension.equals("ok")){ actionOk(data, line); }
      else if(sIntension.equals("s-left")){ actionLeft(data, line); }
      else if(sIntension.equals("s-right")){ actionRight(data, line); }
      else { actionUserKey(sIntension, data, line); }
    }
    
  };

  void stop(){}
  
}
