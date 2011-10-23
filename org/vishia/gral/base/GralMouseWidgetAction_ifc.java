package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

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
  /**The action.
   * @param key
   */
  void mouseAction(int key, GralWidget widgg);
  
  
  void mouse1Down();
  
  void mouse1Up();
  
  void mouse2Down();
  
  void mouse2Up();
  
  /**It is called if the mouse button is pressed, and then the mouse cursor is removed from the widget.
   * The mouse-button-up action won't be called then. Usual the user should done its action
   * while the button-up is detected, non on button down. It is an advantage for handling,
   * because on button-up the hit widget should be marked visible firstly. 
   * 
   */
  void removeMouseCursorFromWidgetWhilePressed();

}
