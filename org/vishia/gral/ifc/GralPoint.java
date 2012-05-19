package org.vishia.gral.ifc;

import org.vishia.gral.base.GralPos;

/**A GralPoint contains 2 or 3 float values for a point.
 * The unit of float is 1 grid unit like {@link GralPos}. It means, a figure described with this points
 * is shown in the correct size. Fractional parts of float determines fine positions.
 * The third dimension can be used for 3-dimensional figures.
 * @author Hartmut Schorrig
 *
 */
public class GralPoint
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
  public static final int version = 20120422;

  public final float x,y,z;

  public GralPoint(float x, float y)
  { this.x = x; this.y = y; this.z = Float.NaN;
  }
  
  public GralPoint(float x, float y, float z)
  { this.x = x; this.y = y; this.z = z;
  }
  
  @Override public String toString(){
    if(z == Float.NaN) return "Point(" + x + ":" + y + ")";
    else return "Point(" + x + ":" + y  + ":" + z + ")";
  }
  
}
