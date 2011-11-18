package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.ifc.GralColor;

/**The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
public class AwtWidgetHelper
{
  public static GralColor getColor(Color awtColor)
  {
    int colorValue = awtColor.getBlue() << 16 + awtColor.getGreen() << 8 + awtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static GralColor setBackgroundColor(GralColor color, Component awtWidget)
  { Color colorAwt = (Color)color.colorGuimpl;
    Color colorAwtOld = awtWidget.getBackground();
    awtWidget.setBackground(colorAwt);
    return getColor(colorAwtOld);
  }

  
  public static GralColor setForegroundColor(GralColor color, Component awtWidget)
  { Color colorAwt = (Color)color.colorGuimpl;
    Color colorAwtOld = awtWidget.getForeground();
    awtWidget.setForeground(colorAwt);
    return getColor(colorAwtOld);
  }


  
  /**Sets the correct TabItem if any widget at this TabItem is focused. That is not done by swt graphic
   * on Control.setFocus().
   * @param control
   */
  static boolean setFocusOfTabSwt(Component control)
  {
    List<Component> parents = new LinkedList<Component>();
    Component parent = control;
    while( (parent = parent.getParent())!=null){
      parents.add(parent);
    }
    for(Component parent1: parents){
    }
    return false; //TODOcontrol.setf();

    
  }
  
  

  
}
