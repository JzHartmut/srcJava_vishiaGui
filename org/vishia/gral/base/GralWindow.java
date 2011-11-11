package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class represents a sub window of an application.
 * The {@link GralGridPos#pos} of the baseclass is the position of the window derived from any other 
 * Position.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralWindow extends GralPanelContent implements GralWindow_ifc
{
  //GralPanelContent panelOfWindow;
  
  //public GralGridPos posWindow;
  
  /**See {@link GralWindow_ifc#setResizeAction(GralUserAction)}. */
  protected GralUserAction resizeAction;
  
  /**See {@link GralWindow_ifc#setMouseAction(GralUserAction)}. */
  protected GralUserAction mouseAction;
  
  public GralWindow(String nameWindow, GralWidgetMng mng, Object panelComposite)
  {
    super( nameWindow, mng, panelComposite);
  }
  
  /**Sets an action which is invoked if the whole window is resized by user handling on the window borders.
   * @param action The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   *   without parameter.
   */
  public abstract void setResizeAction(GralUserAction action);
  
  /**Sets an action which is invoked if any mouse button is pressed in the windows area on the screen.
   * @param action The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   *   with parameter key: The mouse key. params[0]: Instance of {@link GralRectangle} with mouse coordinates.
   */
  abstract public void setMouseAction(GralUserAction action);
  

  
}
