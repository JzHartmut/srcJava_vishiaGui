package org.vishia.gral.widget;

import java.util.List;
import java.util.Map;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;

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
 * only when {@link #setToPanel(GralMngBuild_ifc, String, int, int[], char)} is called.  
 * <pre>
 *  GralSelectList
 *        |--{@link #wdgdTable}--->GralTable         TableLineData
 *                                     |---idxLine------*>|
 *                                     |---tableLines---*>|
 * </pre>
 * Hint: The GralSelectList should not be inherited from GralTable though it seems to be possible,
 * because it is not primary a table. It is more obvious to break the inheritance.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralSelectList<UserData> extends GralWidgetBase implements Removeable //extends GralWidget
{
  /**Version and history:
   * <ul>
   * <li>2023-02-15 Hartmut new: second key set for entry/exit to left/right {@link #setLeftRightKeys(int, int, int, int)} 
   * <li>2022-12-11 Hartmut chg: because new concept of Gral setToPanel is no more called. 
   *   set the {@link #actionTable} now in the ctor which would be also proper in the past but now necessary.
   * <li>2018-10-28 Hartmut chg: {@link #createImplWidget_Gthread()} instead setToPanel(mng)
   * <li>2011-11-18 chg: This class does not inherit from GralWidget now. The GralWidget, which represents this class,
   *   is referenced with the public aggregation {@link #wdgdTable}. Only this instance is registered on a panel
   *   calling {@link #setToPanel(GralMngBuild_ifc, String, int, int[], char)}. 
   * <li>2011-10-02 chg: Uses keycodes from {@link KeyCode} now,
   * <li>2011-10-02 chg: {@link #actionOk(Object, GralTableLine_ifc)} returns boolean now, false if no action is done.
   * <li>older- TODO
   * </ul>
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  public static final String version = "2018-10-28";
  

  /**The table which is showing in the widget. */
  final public GralTable<UserData> wdgdTable;
  

  /**The keys for left and right navigation. Default it is shift + left and right arrow key.
   * 
   */
  protected int keyLeft = KeyCode.alt + KeyCode.left, 
          keyLeft2 = KeyCode.alt + KeyCode.left, 
          keyRight = KeyCode.alt + KeyCode.right, 
          keyRight2 = KeyCode.alt + KeyCode.right;
  
  
  
  /**Not used yet, register actions? */
  protected Map<String, GralUserAction> actions;
  
  protected GralSelectList(GralPos refPos, String posName, int rows, int[] columns) //String name, GralWidgetMng mng)
  { super(refPos, posName, null);
    if(posName == null){
      Debugutil.stop();
    }
    this.wdgdTable = new GralTable<UserData>(refPos, posName, rows, columns);
    this.wdgdTable.specifyActionChange(null, this.actionTable, null, GralWidget_ifc.ActionChangeWhen.onAnyKey);
    this.wdgdTable.setVisible(true);
  }

  
  /**The two left and right key codes for selection left and right can be changed.
   * The key code is a number maybe in combination with alt, ctrl, shift see {@link KeyCode}.
   * @param keyLeft Key code for outer selection
   * @param keyRight KeyCode for deeper selection
   * @param keyLeft2 second Key code for outer selection
   * @param keyRight2 second KeyCode for deeper selection
   */
  public final void setLeftRightKeys(int keyLeft, int keyRight, int keyLeft2, int keyRight2){
    this.keyLeft = keyLeft; this.keyRight = keyRight;
    this.keyLeft2 = keyLeft2; this.keyRight2 = keyRight2;
  }

  /**The left and right key codes for selection left and right can be changed.
   * The key code is a number maybe in combination with alt, ctrl, shift see {@link KeyCode}.
   * @param keyLeft Key code for outer selection
   * @param keyRight KeyCode for deeper selection
   */
  public final void setLeftRightKeys(int keyLeft, int keyRight){
    this.keyLeft = keyLeft; this.keyRight = keyRight;
    this.keyLeft2 = keyLeft; this.keyRight2 = keyRight;
  }

  //public SelectList()
  //{
  //  super('l');
  //}


  /**
   * @param panel
   * @param identArgJbat
   * @param rows
   * @param columns
   * @param size
   */
  public void XXXXsetToPanel(GralMngBuild_ifc gralMng)
  {
    //wdgdTable.setToPanel(gralMng);
    //wdgdTable.setActionChange(actionTable);
  }
  
 
  
  /**
   */
  @Override public boolean createImplWidget_Gthread() {
    boolean ret = checkImplWidgetCreation(this.wdgdTable._wdgImpl);
    if(ret) {
      wdgdTable.createImplWidget_Gthread();
      wdgdTable.setActionChange(actionTable);
    }
    return ret;
  }
  
  /**Removes the implementation widget, maybe to re-create with changed properties
   * or also if the GralWidget itself should be removed.
   * This is a internal operation not intent to use by an application. 
   * It is called from the {@link GralMng#runGraphicThread()} and hence package private.
   */
  @Override public void removeImplWidget_Gthread() {
    this.wdgdTable.removeImplWidget_Gthread();                     // recursively call of same
  }

  
  public void set(List<String[]> listData)
  {
    for(String[] data: listData){
      wdgdTable.setValue(GralMng_ifc.cmdInsert, 0, data[0]);
    }
  }
  
  
  @Override public boolean setVisible(boolean visible) { return this.wdgdTable.setVisible(visible); }
  
  
  @Override public boolean isVisible() { return this.wdgdTable.isVisible(); }
  
  @Override public boolean isInFocus() { return this.wdgdTable.isInFocus(); }
  
  
  /**Sets the focus of the associated table widget.
   * @return true if focused.
   */
  @Override public void setFocus ( ){ wdgdTable.setFocus();  }
  
  
  
  @Override public void setFocus ( int delay, int latest){ wdgdTable.setFocus(delay, latest);  }
  
  
  
  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg) {} //nothing, not for here.
  
  
  
  
  @Override public GralWidgetBase_ifc getFocusedWidget() { return null; }

  
  /**Removes all data and all widgets of this class. */
  @Override public boolean remove(){
    wdgdTable.remove();
    return true;
  }
  
  /**Action if a table line is selected and entered. Its either a double click with the mouse
   * or click of OK (Enter) button.
   * @param userData The user data stored in the line of table.
   */
  protected abstract boolean actionOk(Object userData, GralTableLine_ifc<UserData> line);
  
  /**Action if a table line is selected and ctrl-left is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionLeft(Object userData, GralTableLine_ifc<UserData> line);
  
  /**Action if a table line is selected and ctrl-right is pressed or the release button is pressed.
   * @param userData The user data stored in the line of table.
   */
  protected abstract void actionRight(Object userData, GralTableLine_ifc<UserData> line);
  
  
  /**Action if a table line is selected and any other key is pressed or the context menu is invoked.
   * @param key code or mouse code, one of constans from {@link KeyCode}.
   * @param userData The user data stored in the line of table.
   * @param line The table line.
   * @return true if is was relevant for the key.
   */
  protected abstract boolean actionUserKey(int key, Object userData, GralTableLine_ifc<UserData> line);
  
  
  /**This is the enhancement to key handling of a common table. It regards additionally keyleft and keyright,
   * which is default use with the alt key. See {@link #setLeftRightKeys(int, int)},
   * calling {@link #actionLeft(Object, GralTableLine_ifc)} and {@link #actionRight(Object, GralTableLine_ifc)}.
   * <br>
   * it supports also enter or double click which both calls {@link #actionOk(Object, GralTableLine_ifc)}
   * All other key codes calls {@link #actionUserKey(int, Object, GralTableLine_ifc)}
   * All this operations should be overridden by the implementing class.
   */
  private final GralUserAction actionTable = new GralUserAction("actionTable") {
    @Override public boolean userActionGui(int keyCode, GralWidget widgdTable, Object... params) {
      @SuppressWarnings("unchecked")
      GralTableLine_ifc<UserData> line = (GralTableLine_ifc<UserData>)params[0];
      Object data = line == null ? null : line.getUserData();
      //int keyCode = (Integer)params[1];
      boolean done = true;
      if(data !=null) {
        if(keyCode == GralSelectList.this.keyLeft || keyCode == GralSelectList.this.keyLeft2){ 
          actionLeft(data, line); 
        } else if(keyCode == GralSelectList.this.keyRight || keyCode == GralSelectList.this.keyRight2){ 
          actionRight(data, line); 
        } else if(keyCode == KeyCode.enter){ 
          done = actionOk(data, line); 
        } else if(keyCode == KeyCode.mouse1Double){ 
          done = actionOk(data, line); 
        } else { 
          done = actionUserKey(keyCode, data, line); 
        }
      } else {
        done = false;
      }
      return done;
    }
    
    
  };


  
  void stop(){}
  
}
