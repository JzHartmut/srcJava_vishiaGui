package org.vishia.gral.widget;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralCanvasStorage;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

/**A widget which is a canvas to plot something.
 * @author hartmut Schorrig
 *
 */
public class GralPlotArea extends GralWidget
{
  /**Version, history and license.
   * <ul>
   * <li>2015-09-26 Hartmut creation.   
   * </ul>
   * <br><br>
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut@vishia.org
   * 
   */
  public static final String sVersion = "2015-09-26";

  protected final GralCanvasStorage canvasStore = new GralCanvasStorage();
  
  /**Constructs
   * @param currPos may be null, then use the singleton pos in GralMng, else basic and current position
   * @param posname
   */
  public GralPlotArea(GralPos currPos, String posname) {
    super(currPos, posname, 'P' );
  }
  
 
  public UserUnits userUnitsPerGrid(float x0, float y0, float xSize, float ySize) {
    return new UserUnits(x0, y0, xSize, ySize, true);
  }
  
  
  public void drawLine(GralColor color, UserUnits userUnits, float[][] points, int iy) {
    canvasStore.drawLine(color, userUnits, points, iy);
  }
  
  

  
  
  public class UserUnits{
    /**User units for this area to draw somewhat in user units. */
    final public float x0, y0, xmax, ymax, fx, fy;
  
    /**Sets the user units
     * @param x0 origin, x-Coordinate, bottom left
     * @param y0 origin, y-Coordinate, bottom left
     * @param x1 x-Coordinate, right
     * @param y1 y-Coordinate top right
     */
    protected UserUnits(float x0, float y0, float x1, float y1, boolean perGridPos){
      this.x0 = x0; this.y0 = y0; this.xmax = x1; this.ymax = y1;
      GralPos pos = GralPlotArea.super.pos();
      float height, width;
      if(perGridPos){
        height= width = 1; 
        fx = height / x1;
        fy = width  / y1;
      } else {
        height = pos.height();
        width = pos.width();
        fx = height / (x1 - x0);
        fy = width  / (y1 - y0);
      }
    }
  }
  
  
  /**This class contains the access to the GralWidget class. It is used only as super class for the implementation level.
   * Don't use this class from user applications! It is public only because it should be seen from the graphic implementation.
   */
  public abstract class _GraphicImplAccess_ extends GralWidget.ImplAccess {

    protected _GraphicImplAccess_(GralWidget widgg)
    {
      super(widgg);
    }

    protected GralCanvasStorage canvasStore(){ return GralPlotArea.this.canvasStore; }
    
    @Override public boolean setFocusGThread()
    {
      // TODO Auto-generated method stub
      return false;
    }

    @Override public void removeWidgetImplementation()
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void repaintGthread()
    {
      // TODO Auto-generated method stub
      
    }

    @Override public Object getWidgetImplementation()
    {
      // TODO Auto-generated method stub
      return null;
    }

    @Override public void setBoundsPixel(int x, int y, int dx, int dy)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public GralRectangle getPixelPositionSize()
    {
      // TODO Auto-generated method stub
      return null;
    }
    
  }
  
}
