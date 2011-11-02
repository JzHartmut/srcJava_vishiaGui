package org.vishia.mainGuiSwt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.ifc.GralColor;

/**The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
class SwtWidgetHelper
{

  public static GralColor getColor(Color swtColor)
  {
    int colorValue = swtColor.getBlue() << 16 + swtColor.getGreen() << 8 + swtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static GralColor setBackgroundColor(GralColor color, Control swtWidget)
  { Color colorSwt = (Color)color.colorGuimpl;
    Color colorSwtOld = swtWidget.getBackground();
    swtWidget.setBackground(colorSwt);
    return getColor(colorSwtOld);
  }

  
  public static GralColor setForegroundColor(GralColor color, Control swtWidget)
  { Color colorSwt = (Color)color.colorGuimpl;
    Color colorSwtOld = swtWidget.getForeground();
    swtWidget.setForeground(colorSwt);
    return getColor(colorSwtOld);
  }

  
  
}
