package org.vishia.gral.ifc;

import java.util.Map;
import java.util.TreeMap;

public final class GralFont
{
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120303;

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

  
  private static Map<String, GralFont> fonts = new TreeMap<String, GralFont>();
  
  public GralFont(String fontName, int size, char style)
  { this.fontName = fontName;
    this.size = size;
    this.style = style;
  }
  
  
  
  /**Not yet ready. Therefore protected yet.
   * @param fontName
   * @param size Use the grid size!
   * @param style
   * @return
   */
  protected static GralFont getFont(String fontName, int size, char style){
    String key = fontName + "." + style + size;
    GralFont ret = fonts.get(key);
    if(ret == null){
      ret = new GralFont(fontName, size, style);
      fonts.put(key, ret);
    }
    return ret;
  }
  
  
}
