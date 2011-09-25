package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class represents a sub window of an application.
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class GralSubWindow extends GralPanelContent implements GralWindow_ifc
{
  //GralPanelContent panelOfWindow;
  
  public GralGridPos posWindow;
  
  public GralSubWindow(String nameWindow)
  {
    super( nameWindow);
  }
  
  
  
}
