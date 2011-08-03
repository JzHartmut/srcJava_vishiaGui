package org.vishia.gral.ifc;

import java.util.Map;

import org.vishia.gral.gridPanel.GuiDialogZbnfControlled;

/**This Interface should be implemented by any user class to call user actions when buttons are pressed,
 * a selection is done, a slider is changed or any widget is activated. Whether this interface is used,
 * it depends from the parameter 'String sUserAction' given by the call of the build-methods in the
 * {@link GuiPanelMngBuildIfc}. The user-action given by this String should be registered before
 * This should be done with the method {@link GuiPanelMngBuildIfc#registerUserAction(String, UserActionGui)}.
 * Thereby the instance implementing this interface is associated. 
 * <br><br>
 * Some widgets can know the same implementing instance. The differencing is done with the {@link WidgetDescriptor}
 * as parameter while calling.
 * <br><br>
 * The parameter may be the values of all input fields,
 * if {@link GuiDialogZbnfControlled#configureWithZbnf(String, String)} configures any input fields. The interface is used
 * to call user methods which are registered using {@link GuiDialogZbnfControlled#registerUserAction(String, UserActionGui)}.
 * The user methods are invoked by buttons etc. with the given name.
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
