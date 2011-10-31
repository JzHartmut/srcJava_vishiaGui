package org.vishia.gral.ifc;

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
   */
  public static final int version = 0x20111101;
  
  
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
