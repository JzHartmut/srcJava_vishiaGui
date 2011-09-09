package org.vishia.gral.ifc;


/**This Interface should be implemented by any user class to call user actions when buttons are pressed,
 * a selection is done, a slider is changed or any widget is activated. Whether this interface is used,
 * it depends from the parameter 'String sUserAction' given by the call of the build-methods in the
 * {@link GralGridBuild_ifc}. The user-action given by this String should be registered before
 * This should be done with the method {@link GralGridBuild_ifc#registerUserAction(String, UserActionGui)}.
 * Thereby the instance implementing this interface is associated. 
 * <br><br>
 * Some widgets can know the same implementing instance. The differencing is done with the {@link WidgetDescriptor}
 * as parameter while calling.
 * <br><br>
 */
public interface UserActionGui
{
  /**Call of users method while a widget is activated.
   * @param sIntension A short string describes the intension of call, means which action is done. 
   *        This String is generated from the calling routine.
   *        It isn't able to set by the user:
   *        <ul>
   *        <li>ActionMouseButton: "Button-down", "Button-up", "Button-click" 
   *        <li>ActionFocused: "Focus-get", "Focus-release"
   *        </li>
   *        With this information the same action method can be used for more as one action type. 
   * @param params Some optional params, depending on the implementation and the sIntension.
   */
  void userActionGui(String sIntension, WidgetDescriptor infos, Object... params);
}
