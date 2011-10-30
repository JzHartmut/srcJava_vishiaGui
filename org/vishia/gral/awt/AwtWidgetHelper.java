package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Component;

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

  
  
}
