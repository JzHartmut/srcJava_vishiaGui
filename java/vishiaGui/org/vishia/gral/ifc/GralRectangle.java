package org.vishia.gral.ifc;

import java.io.IOException;

/**A GralRectangle is a commonly use-able data class to hold any x,y and size values in integer. 
 * It is used for some return data, often it is pixel units.
 * <br><br>
 * See {@link GralPoint}.
 * @author Hartmut Schorrig
 *
 */
public class GralRectangle
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

  public int x,y,dx,dy;

  public GralRectangle(int x, int y, int dx, int dy)
  { this.x = x; this.y = y; this.dx = dx; this.dy = dy;
  }
 
  
  public void set(GralRectangle src) {
    x = src.x; y = src.y; dx = src.dx; dy = src.dy; 
  }
  
  public static void toString(Appendable out, int x, int y, int dx, int dy) throws IOException {
    out.append("[bounds x,y=").append(Integer.toString(x)).append('+').append(Integer.toString(dx)).append(", ").append(Integer.toString(y)).append('+').append(Integer.toString(dy)).append(']');
    
  }
  
  
  @Override public String toString(){
    return "GralRectangle(" + x + " + " + dx + ", " + y + " + " + dy + ")";
  }
}
