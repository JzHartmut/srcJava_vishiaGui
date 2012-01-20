package org.vishia.gral.ifc;

import org.vishia.gral.widget.GralFileSelector;
import org.vishia.util.KeyCode;


/**This Interface should be implemented by any user class to receive any user actions on the graphic.
 * User actions are for example: A button is pressed,
 * a selection is done, a slider is changed or any widget is activated. 
 * The interface is a universal one. It is possible to have only one implementation method 
 * for some and different widgets with different actions. But it is possible too to implement one method only
 * for a special widget for a special action. The value {@link GralWidget#sCmd} can be used to determine 
 * the action when this method is invoked. This value can be set in the build phase
 * of the GUI to some widgets to have an adequate user action. If a script is used for build, it is the 
 * parameter 'cmd=<i>CMD</i>'.
 * <br><br>
 * If a user action should be selected by String in the GUI-Script, it should be supplied by registering 
 * before the script runs.
 * This should be done with the method {@link GralGridBuild_ifc#registerUserAction(String, UserActionGui)}.
 * <br><br>
 * Some widgets can know the same implementing instance. The differencing is done with the {@link GralWidget}
 * as parameter while calling.
 * <br><br>
 */
public abstract class GralUserAction
{
  /**Version and history:
   * <ul>
   * <li>2011-10-23 Hartmut chg: Not it is not an interface but a abstract class with the KeyCode-sensitive method.
   *   All implementations yet uses the anonymous class, therefore no changes in its usage.
   *   Typical the implementation is a simple inheritance embedded as inner class.
   *   Then an abstract class is able to use like a interface.
   *   The idea is, don't use the String determined method any more, instead the new method with KeyCode.
   *   All calling should call both methods, but the user can implement only one of them. The other returns false.
   *   So only the calling places in sources should be adapted for usage yet. 
   * </ul>
   */
  static final int version = 0x20111023;
  
  
  /**Call of users method while a widget is activated.
   * Usual intensions:
   * <ul>
   * <li>"ok": The OK-Button or enter-key
   * <li>"key": A key is pressed. Then the key code is contained as Integer in params[0].
   *            The key code follows the convention of {@link GralKey}.
   * <li>"sliderValue": A slider is changed: params contains a Integer value between 0 and 99.
   * <li>ActionMouseButton: "Button-down", "Button-up", "Button-click" 
   * <li>ActionFocused: "Focus-get", "Focus-release"
   * <li>"FileSelector-file": from {@link GralFileSelector} if a file is entered.
   * </ul>
   * @param sIntension A short string describes the intension of call, means which action is done. 
   *        This String is generated from the calling routine.
   *        It isn't able to set by the user usual, expect if it is parameterized in a specific way.
   *        Often the sIntension should not be tested in the users implementation,
   *        because the interface is used only for a specific case. But the action should be checked
   *        then to detect software errors.
   *        With this information the same action method can be used for more as one action type.
   * @param widgd The Gral-widget is given in most cases. It depends some additional static informations
   *        about the widget to control the user action.         
   * @param params Some optional values, depending on the sIntension.
   * @return true if the user action is done, false if the sIntension or any other parameter
   *        doesn't much to that implementation. It is possible to build a queue of user action invocations.
   *        The first invocation which returns true may finish the invocations.
   * @deprecated use {@link #userActionGui(int, GralWidget, Object...)}.
   */
  public boolean userActionGui(String sIntension, GralWidget widgd, Object... params){ return false; }
  
  /**Call of users method while any user action on the gui is done.
   * @param actionCode See {@link KeyCode}. Any special action is designated with 0.
   * @param widgd The Gral widget
   * @param params Some optional values, depending on special user designation. In most cases no parameter.
   *   The user may be test the type of parameter for complex usage.
   * @return true if execution is succeed. false if the users application can't deal with the actionCode or the widget. 
   */
  public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ return false; }
  
  
}
