package org.vishia.gral.widget;

import java.util.List;
import java.util.Map;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

/**Complex widget which contains a list what's items are able to select. 
 * It is the base class for file selection and command selection.
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class SelectList extends GralWidget
{
  /**Version and history:
   * <ul>
   * <li>2011-10-02 chg: Uses keycodes from {@link KeyCode} now,
   * <li>2011-10-02 chg: {@link #actionOk(Object, GralTableLine_ifc)} returns boolean now, false if no action is done.
   * <li>older- TODO
   * </ul>
   */
  public static final int version = 0x20111002;
  
  /**The table which is showing in the widget. */
  //protected GralWidget wdgdTable;
  
  /**The table which is showing in the widget. */
  public GralTable wdgdTable;
  
  /**Not used yet, register actions? */
  protected Map<String, GralUserAction> actions;
  
  public SelectList(String name, GralWidgetMng mng)
  {
    super(name, 'l', mng);
  }


  //public SelectList()
  //{
  //  super('l');
  //}


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
  protected abstract boolean actionOk(Object userData, GralTableLine_ifc line);
  
  /**Action if a table line is selected and ctrl-left is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionLeft(Object userData, GralTableLine_ifc line);
  
  /**Action if a table line is selected and ctrl-right is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionRight(Object userData, GralTableLine_ifc line);
  
  
  /**Action if a table line is selected and any other key is pressed or the context menu is invoked.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionUserKey(String sKey, Object userData, GralTableLine_ifc line);
  
  
  private GralUserAction actionTable = new GralUserAction()
  {

    @Override public boolean userActionGui(int keyCode, GralWidget widgdTable, Object... params)
    {
      //assert(sIntension.equals("table-key"));
      GralTableLine_ifc line = (GralTableLine_ifc)params[0];
      Object data = line.getUserData();
      //int keyCode = (Integer)params[1];
      boolean done = true;
      if(keyCode == KeyCode.shift + KeyCode.left){ actionLeft(data, line); }
      else if(keyCode == KeyCode.shift + KeyCode.right){ actionRight(data, line); }
      else if(keyCode == KeyCode.enter){ done = actionOk(data, line); }
      else { done = false; }
      return done;
    }
    
    
  };

  /**Sets the focus to this SelectList, the table-widget gets the focus. */
  @Override public boolean setFocus()
  { return wdgdTable.setFocus(); }
  
  @Override public void removeWidgetImplementation()
  {
    
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override public void redraw(){  wdgdTable.redraw(); }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { wdgdTable.setBoundsPixel(x,y,dx,dy);
  }
  

  
  void stop(){}
  
}
