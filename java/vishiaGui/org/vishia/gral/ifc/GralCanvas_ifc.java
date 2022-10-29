package org.vishia.gral.ifc;

import org.vishia.gral.base.GralCanvasStorage;

/**This interface is used to free-draw on a canvas area. It is implemented by the 
 * {@link GralCanvasStorage} and by implementation classes.
 * @author Hartmut Schorrig
 *
 */
public interface GralCanvas_ifc
{

  
  /**Version and history:
   * <ul>
   * <li>2011-11-01 Created. It is necessary to build common use-able widgets on gral level,
   *   it may be usefully to draw and paint on user level. Therefore this universal interface is need.
   * </ul>
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
  public static final int version = 20111101;
  
  
  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   * @param x1 TODO yet it is pixel coordinates, use GralGrid coordinates.
   * @param y1
   * @param x2
   * @param y2
   */
  public void drawLine(GralColor color, int x1, int y1, int x2, int y2);

  
  public void drawImage(GralImageBase image, int x, int y, int dx, int dy, GralRectangle imagePixelSize);
  
  
  public void setTextStyle(GralColor color, GralFont font, int origin);
  
  public void drawText(String text);

  
}
