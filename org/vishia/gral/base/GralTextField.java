package org.vishia.gral.base;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralWidget;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTextField extends GralWidget implements GralTextField_ifc
{
  /**Version and history
   * <ul>
   * <li>2011-11-18 Hartmut new {@link #setMouseAction(GralUserAction)}. This method should be 
   * an abstract method of all {@link GralWidget} but it is used yet only here.
   * <li>2011-09-00 Hartmut Creation to build a platform-indenpenden representation of text field. 
   * </ul>
   */
  public final static int version = 0x20111118;
  
  protected final GralGraphicThread windowMng;
  
  public GralTextField(String name, char whatis, GralWidgetMng mng){
    super(name, whatis, mng);
    this.windowMng = mng.gralDevice;
  }
  
  /**Sets the action which is invoked while a mouse button is pressed or release on this widget.
   * Implementation hint: It installs a mouse listener.
   * TODO: use GralMouseWidgetAction_ifc instead GralUserAction, use another action for mouse than change.
   */
  abstract public void setMouseAction(GralUserAction action);
  
  /**Returns the Label implementation for a prompt or null if there isn't used a prompt
   */
  abstract public Object getPromptLabelImpl();
  
}
