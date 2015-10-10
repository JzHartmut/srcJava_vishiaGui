package org.vishia.gral.ifc;

/**This class should not be used by an application. It is intent to adjust the internal colors.
 * @author Hartmut Schorrig
 *
 */
public class _GralChgColor
{
  public static void setColorValue(GralColor color, int rgb) {
    color.rgb = rgb;
    color.red = (rgb>>16) & 0xff;
    color.green = (rgb>>8) & 0xff;
    color.blue = (rgb) & 0xff;
  }

  
  public static void setColorUsualNames(GralColor color, String usualNames) {
    color.usualNames = usualNames;
  }
}
