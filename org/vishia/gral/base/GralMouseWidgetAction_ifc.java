package org.vishia.gral.base;


/**This interface is implemented in the Widget adaption for the Gral level.
 * It should not be known by the user, except the user creates new widget types.
 * The interface is the connection from Mouse listeners in the graphic implementation layer (swt, swing)
 * to the gral widgets.
 * 
 * @author Hartmut Schorrig
 *
 */
public interface GralMouseWidgetAction_ifc
{
  /**Version, history and licence
   * 
   * <ul>
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

  
  /**The action.
   * @param key
   */
  void mouseAction(int key, int xMousePixel, int yMousePixel, GralWidget widgg);
  
  
  /**Called from the graphic implementation layer if the standard left mouse button is pressed.
   * @param xMousePixel
   * @param yMousePixel
   */
  void mouse1Down(int key, int xMousePixel, int yMousePixel, GralWidget widgg);
  
  void mouse1Up(int key, int xMousePixel, int yMousePixel, GralWidget widgg);
  
  void mouse2Down(int key, int xMousePixel, int yMousePixel, GralWidget widgg);
  
  void mouse2Up(int key, int xMousePixel, int yMousePixel, GralWidget widgg);
  
  /**It is called if the mouse button is pressed, and then the mouse cursor is removed from the widget.
   * The mouse-button-up action won't be called then. Usual the user should done its action
   * while the button-up is detected, non on button down. It is an advantage for handling,
   * because on button-up the hit widget should be marked visible firstly. 
   * 
   */
  void removeMouseCursorFromWidgetWhilePressed();

}
