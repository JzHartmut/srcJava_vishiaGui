package org.vishia.gral.ifc;

public class GralFont
{
  /**The font-instance for the implementation can be set from the implementation with the necessary type
   * to minimize the effort in dynamic instances.  */
  public Object fontImpl;

  public static String fontMonospacedSansSerif = "monospacedSansSerif";
  
  public static String fontMonospacedSmall = "MonospacedSmall";
  
  public static String fontMonospacedSerif = "monospacedSerif";
  
  public static String fontSansSerif = "sansSerif";
  
  public static String fontSerif = "serif";
  
  
  public String fontName;
  
  /**The size of the font in points.*/
  public int size;
  
  /**The style of the font. 
   * <ul>
   * <li>b bold
   * <li>i italic
   * <li>
   * </ul>
   */
  public char style;

  public GralFont(String fontName, int size, char style)
  { this.fontName = fontName;
    this.size = size;
    this.style = style;
  }
  
  
  
  
}
