package org.vishia.gral.widget;

import java.util.List;
import java.util.Map;

import org.vishia.gral.base.GralTable;
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
import org.vishia.util.Removeable;
import org.vishia.util.SelectMask_ifc;

/**The base class for lists which supports nested selections. The associated widget is a table.
 * The action listener {@link #actionTable} captures all key and mouse activities on the table-widget.
 * It is the base class for file selection and command selection.
 * The base idea is, left and right keys navigates in a tree to outer and deeper nodes. The table
 * shows only members of the current node. A text line shows the current node path.
 * It may be possible to switch to a tree presentation (TODO). But this complex widget should occupy
 * only a simple rectangle of a GUI, not some windows etc. It may be less in spread too if necessary.
 * <br><br>
 * Note: this class should not be a derived class of {@link GralTable}, because instances of derived classes
 * should be created as final compositions in the main thread before the table can be presented 
 * in the graphic thread. Therefore the aggregation {@link #wdgdTable} cannot be final. It is set 
 * only when {@link #setToPanel(GralGridBuild_ifc, String, int, int[], char)} is called.  
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class GralSelectList implements Removeable //extends GralWidget
{
  /**Version and history:
   * <ul>
   * <li>2011-11-18 chg: This class does not inherit from GralWidget now. The GralWidget, which represents this class,
   *   is referenced with the public aggregation {@link #wdgdTable}. Only this instance is registered on a panel
   *   calling {@link #setToPanel(GralGridBuild_ifc, String, int, int[], char)}. 
   * <li>2011-10-02 chg: Uses keycodes from {@link KeyCode} now,
   * <li>2011-10-02 chg: {@link #actionOk(Object, GralTableLine_ifc)} returns boolean now, false if no action is done.
   * <li>older- TODO
   * </ul>
   */
  public static final int version = 0x20111002;
  

  /**The table which is showing in the widget. */
  public GralTable wdgdTable;
  

  /**The keys for left and right navigation. Default it is shift + left and right arrow key.
   * 
   */
  private int keyLeft = KeyCode.alt + KeyCode.left, keyRight = KeyCode.alt + KeyCode.right;
  
  
  
  /**Not used yet, register actions? */
  protected Map<String, GralUserAction> actions;
  
  protected GralSelectList() //String name, GralWidgetMng mng)
  {
    //super(name, 'l', mng);
  }

  
  /**The left and right key codes for selection left and right can be changed.
   * The key code is a number maybe in combination with alt, ctrl, shift see {@link KeyCode}.
   * @param keyLeft Key code for outer selection
   * @param keyRight KeyCode for deeper selection
   */
  public final void setLeftRightKeys(int keyLeft, int keyRight){
    this.keyLeft = keyLeft; this.keyRight = keyRight;
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
  public void setToPanel(GralGridBuild_ifc gralMng, String name, int rows, int[] columns, char size)
  {
    wdgdTable = gralMng.addTable(name, rows, columns);
    wdgdTable.setActionChange(actionTable);
  }
  
 
  
  public void set(List<String[]> listData)
  {
    for(String[] data: listData){
      wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, data[0]);
    }
  }
  
  
  /**Sets the focus of the associated table widget.
   * @return true if focused.
   */
  public boolean setFocus(){ return wdgdTable.setFocus(); }
  
  /**Removes all data and all widgets of this class. */
  @Override public boolean remove(){
    wdgdTable.remove();
    return true;
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
   * @param key code or mouse code, one of constans from {@link KeyCode}.
   * @param userData The user data stored in the line of table.
   * @param line The table line.
   * @return true if is was relevant for the key.
   */
  protected abstract boolean actionUserKey(int key, Object userData, GralTableLine_ifc line);
  
  
  private GralUserAction actionTable = new GralUserAction()
  {

    @Override public boolean userActionGui(int keyCode, GralWidget widgdTable, Object... params)
    {
      //assert(sIntension.equals("table-key"));
      GralTableLine_ifc line = (GralTableLine_ifc)params[0];
      Object data = line.getUserData();
      //int keyCode = (Integer)params[1];
      boolean done = true;
      if(keyCode == keyLeft){ actionLeft(data, line); }
      else if(keyCode == keyRight){ actionRight(data, line); }
      else if(keyCode == KeyCode.enter){ done = actionOk(data, line); }
      else if(keyCode == KeyCode.mouse1Double){ done = actionOk(data, line); }
      else { done = actionUserKey(keyCode, data, line); }
      return done;
    }
    
    
  };


  
  void stop(){}
  
}
