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
    color.colorGuimpl = null; //create a new one.
  }


  public static void addColorUsualNames(GralColor color, String usualNames) {
    color.usualNames = usualNames;
  }


  public static void setColorUsualNames(GralColor color, String usualNames) {
    if(false && usualNames !=null && color.usualNames !=null && color.usualNames.length() >0){
      color.usualNames += ", " + usualNames;
    } else {
      color.usualNames = usualNames;
    }
  }
}
