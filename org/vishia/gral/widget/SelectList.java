package org.vishia.gral.widget;

import java.util.List;
import java.util.Map;

import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

/**Complex widget which contains a list what's items are able to select. 
 * It is the base class for file selection and command selection.
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class SelectList
{
  /**Version and history:
   * <ul>
   * <li>2011-10-02 chg: Uses keycodes from {@link KeyCode} now,
   * <li>2011-10-02 chg: {@link #actionOk(Object, TableLineGui_ifc)} returns boolean now, false if no action is done.
   * <li>older- TODO
   * </ul>
   */
  public static final int version = 0x20111002;
  
  /**The table which is showing in the widget. */
  protected GralWidget wdgdTable;
  
  /**The table which is showing in the widget. */
  protected TableGui_ifc table;
  
  /**Not used yet, register actions? */
  protected Map<String, GralUserAction> actions;
  
  /**
   * @param panel
   * @param name
   * @param rows
   * @param columns
   * @param size
   */
  public void setToPanel(GralGridBuild_ifc panel, String name, int rows, int[] columns, char size)
  {
    wdgdTable = panel.addTable(name, rows, columns);
    wdgdTable.setActionChange(actionTable);
    table = (TableGui_ifc)wdgdTable.widget;
  }
  
  
  
  public void set(List<String[]> listData)
  {
    for(String[] data: listData){
      wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, data[0]);
    }
  }
  
  
  /**Action if a table line is selected and entered. Its either a double click with the mouse
   * or click of OK (Enter) button.
   * @param userData The user data stored in the line of table.
   */
  protected abstract boolean actionOk(Object userData, TableLineGui_ifc line);
  
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
  
  
  private GralUserAction actionTable = new GralUserAction()
  {

    @Override public boolean userActionGui(String sIntension, GralWidget widgdTable, Object... params)
    {
      assert(sIntension.equals("table-key"));
      TableLineGui_ifc line = (TableLineGui_ifc)params[0];
      Object data = line.getUserData();
      int keyCode = (Integer)params[1];
      boolean done = true;
      if(keyCode == KeyCode.shift + KeyCode.left){ actionLeft(data, line); }
      else if(keyCode == KeyCode.shift + KeyCode.right){ actionRight(data, line); }
      else if(keyCode == KeyCode.enter){ done = actionOk(data, line); }
      else { done = false; }
      return done;
    }
    
  };

  /**Sets the focus to this SelectList, the table-widget gets the focus. */
  public void setFocus()
  { table.setFocus(); }
  
  void stop(){}
  
}
