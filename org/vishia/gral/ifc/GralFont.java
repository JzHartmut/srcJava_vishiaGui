package org.vishia.gral.ifc;

public class GralFont
{
  /**The font-instance for the implementation can be set from the implementation with the necessary type
   * to minimize the effort in dynamic instances.  */
  public Object fontImpl;

  public static final String fontMonospacedSansSerif = "monospacedSansSerif";
  
  public static final String fontMonospacedSmall = "MonospacedSmall";
  
  public static final String fontMonospacedSerif = "monospacedSerif";
  
  public static final String fontSansSerif = "sansSerif";
  
  public static final String fontSerif = "serif";
  
  /**Some types of fonts.*/
  public static final char typeSmallMonospaced = 'm', typeMonospaced = 'M',
    typeSansSerif = 'a', typeSerif = 't';
  
  public static final char styleNormal = 'n', styleItalic = 'i', styleBoldItalic = 'I', styleBold = 'b';
  
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
