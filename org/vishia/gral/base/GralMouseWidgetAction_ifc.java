package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget_ifc;

/**This interface is implemented in the Widget adaption for the Gral level.
 * It should not be known by the user, except the user creates new widget types.
 * The interface is the connection from Mouse listeners in the graphic implementation layer (swt, swing)
 * to the gral widgets.
 * See {@link org.vishia.gral.swt.SwtGralMouseListener} for an usage in graphic implementation layer.
 * 
 * 
 * @author Hartmut Schorrig
 *
 */
public interface GralMouseWidgetAction_ifc
{
  /**Version, history and licence
   * 
   * <ul>
   * <li>2013-10-13 Hartmut new: {@link #mouse1Double(int, int, int, int, int, GralWidget)}.
   *   #new 
   * <li>2013-05-13 Hartmut chg: All methods changed, parameter key, position. 
   * </ul>
   * 
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
   * 
   */
  public final static int version = 0x20120303;

  
  /**Bit mask to determine which mouse action should invoke the {@link GralWidget#getActionChange()}.
   * It is the second parameter of {@link GralWidget#setActionMouse(GralMouseWidgetAction_ifc, int)}. 
   * Use an | of this conditions. mUserAll includes any key of mouse.
   */
  public final static int mUser1down = 1, mUser2down = 0x2, mUser3down = 0x4, mUserAlldown = 0xf
    , mUser1up = 0x10, mUser2up = 0x20, mUser3up = 0x40, mUserAllup = 0xf
    , mUserDouble = 0x100
    , mUserAll = 0xffff; 
  
  /**Called from the graphic implementation layer if the standard left mouse button is pressed.
   * @param xMousePixel
   * @param yMousePixel
   */
  void mouse1Down(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg);
  
  void mouse1Up(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg);
  
  /**Called from the graphic implementation layer if the standard right mouse button is pressed.
   * @param xMousePixel
   * @param yMousePixel
   */
  void mouse2Down(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg);
  
  void mouse2Up(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg);
  

  void mouse1Double(int key, int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel, GralWidget widgg);

  
  
  /**It is called if the mouse button is pressed, and then the mouse cursor is removed from the widget.
   * The mouse-button-up action won't be called then. Usual the user should done its action
   * while the button-up is detected, non on button down. It is an advantage for handling,
   * because on button-up the hit widget should be marked visible firstly. 
   * 
   */
  //void removeMouseCursorFromWidgetWhilePressed();

  
  
  /**This routine is called only if a mouse button is pressed while moving the mouse cursor.
   * @param xMousePixel The current mouse cursor x value
   * @param yMousePixel The current mouse cursor y value
   * @param xWidgetSizePixel Width of the associated widget.
   * @param yWidgetSizePixel Height of the associated widget.
   * @return true if the mouse should be still accepted as pressed, 
   *   false if the mouse button up action should be prevented. The mouse is not accepted as pressed any more. 
   */
  boolean mouseMoved(int xMousePixel, int yMousePixel, int xWidgetSizePixel, int yWidgetSizePixel);
  
  
  
  /**Invoke the proper action for mouse events.
   * This routine is used inside the graphic implementing mouse handlers.
   * The action with the 'when' designation is searched. If found it is invoked with its own arguments
   * and then with the mouse position in line and column grid values of the panel and a possible delta position.
   * @param widgg The widget where the mouse handling is associated to
   * @param keyCode The possible additional modifying key (ctrl, alt, sh) and the mouse key
   * @param when which mouse action should be used.
   * @param mouse_x The pixel position of the mouse inside the panel
   * @param mouse_y
   * @param dx An additional delta-position in pixel.
   * @param dy
   * @throws any exception which is catched in the mouse listener.
   * NOTE: yet unused. Write a class with gral mouse adaption!
  public static void executeActionForMouse(GralWidget widgg, int keyCode, GralWidget_ifc.ActionChangeWhen when, int mouse_x, int mouse_y, int dx, int dy) {
    GralWidget_ifc.ActionChange action = widgg.getActionChangeStrict(when, true); 
    if(action !=null){
      //TODO: calc grid positions from pixel!
      Object[] args = action.args();
      if(args == null){ action.action().exec(keyCode, widgg, new Integer(mouse_x), new Integer(mouse_y), new Integer(dx), new Integer(dy)); }
      else { 
        //additional 2 arguments: copy in one args2.
        Object[] args2 = new Object[args.length +4];
        System.arraycopy(args, 0, args2, 0, args.length);
        args2[args.length] = new Integer(mouse_y);
        args2[args.length+1] = new Integer(mouse_x);
        args2[args.length+2] = new Integer(dy);
        args2[args.length+3] = new Integer(dx);
        action.action().exec(keyCode, widgg, args2); 
      }
    }
  }
   */
  
  
}
