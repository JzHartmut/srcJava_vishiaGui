package org.vishia.gral.ifc;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.widget.GralPlotArea;



/**The canvas storage is a storage, which stores orders to paint. This orders are painted
 * if the paint-routine from the windows-system is invoked. An application can call
 * forex the {@link #drawLine(Color, int, int, int, int)}-routine to draw a line. This line
 * will be drawn immediately, if the graphical window or widget gets the redraw-request.
 * Firstly the line will be stored only. 
 * @author Hartmut Schorrig
 *
 */
public class GralCanvasStorage implements GralCanvas_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2012-04-22 Hartmut new: {@link PolyLineFloatArray}
   * <li>2012-04-22 new {@link #drawLine(GralPos, GralColor, List)}, improved {@link PaintOrder}-derivates.
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
  public static final String version = "2015-09-26";

	/**Data class to store an order.
	 */
	public static class PaintOrder
	{
		/**One of the static int of this class. Determines what to paint. 
		 * See {@link GralCanvasStorage#paintLine}, {@link GralCanvasStorage#paintImage}, 
		 * */
		public final int paintWhat;
		
		/**Coordinates. */
		public final int x1,y1,x2,y2;
		
		public final GralPos pos;
		
		public final GralColor color;

		/**The implementation data are graphic-platform-specific. It may be prepared data for a defined
		 * size appearance to support fast redrawing. */
		private Object implData;
		
		public Object getImplData() { return implData; }

    public void setImplData(Object implData) { this.implData = implData; }

    PaintOrder(int paintWhat, int x1, int y1, int x2, int y2, GralColor color) {
      this.paintWhat = paintWhat;
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.color = color;
      this.pos = null;
    }

    PaintOrder(int paintWhat, GralPos pos, GralColor color) {
      this.paintWhat = paintWhat;
      this.pos = pos == null ? null: pos.clone();
      this.x1 = -1;
      this.y1 = -1;
      this.x2 = -1;
      this.y2 = -1;
      this.color = color;
    }

	}//class PaintOrder

	
  public static class PolyLine extends PaintOrder
  {
    public final List<GralPoint> points;
    
    public boolean bPointsAreGralPosUnits = true;
    
    PolyLine(GralPos pos, GralColor color, List<GralPoint> points){
      super(paintPolyline, pos, color);
      this.points = points;
    }
  }
  
  
  
  public static class PolyLineFloatArray extends PaintOrder
  {
    private final float[][] points;
    
    final GralPlotArea.UserUnits userUnits;
    
    private Object implStore;
    
    public PolyLineFloatArray(GralColor color, GralPlotArea.UserUnits userUnits, float[][] points){
      super(paintPolyline, null, color);
      this.userUnits = userUnits;
      this.points = points;
    }
    
    /**Sets any instance which stores implementation specific data. This method should only called by the implementation layer. */
    public void setImplStore(Object store){ implStore = store; }
    
    /**Gets the implementation specific instance. */
    public Object getImplStore(){ return implStore; }

    /**Gets the implementation specific instance. */
    public int[] getImplStoreInt1Array(){ 
      if(implStore == null) {
        int[] store = new int[2*points.length];
        implStore = store;
        int ixd = -1;
        GralGridProperties props = GralMng.get().propertiesGui;
        int fxp = props.xPixelUnit();
        int fyp = props.yPixelUnit();
        for(float[] point: points){
          float x = point[0], y = point[1];
          store[++ixd] = (int)(userUnits.fx * fxp * (x - userUnits.x0) + 0.5f);
          store[++ixd] = (int)(userUnits.fy * fyp * (y - userUnits.y0) + 0.5f);
        }
      }
      return (int[])implStore; 
    }
  }
  
  
  
	public static class PaintOrderImage extends PaintOrder
	{
	  public final GralImageBase image;
	  public final int dxImage, dyImage;
	  PaintOrderImage(GralImageBase image, int line, int column, int heigth, int width, GralRectangle pixelImage)
	  { super(paintImage, line, column, heigth, width, null);
	    this.image = image;
      this.dxImage = pixelImage.dx;
      this.dyImage = pixelImage.dy;
	  }
	}//class PaintOrderImage
	
	public final static int paintLine = 0xee, paintImage = 0x1ae;
	
	public final static int paintPolyline = 'y';
	
	/**List of all orders to paint in {@link #drawBackground(GC, int, int, int, int)}.
	 * 
	 */
	public final ConcurrentLinkedQueue<PaintOrder> paintOrders = new ConcurrentLinkedQueue<PaintOrder>();
	
	
	
  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   * @param x1 TODO yet it is pixel coordinates, use GralGrid coordinates.
   * @param y1
   * @param x2
   * @param y2
   */
  public void drawLine(GralColor color, int x1, int y1, int x2, int y2){
    PaintOrder order = new PaintOrder(paintLine, x1,y1,x2,y2, color);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralPos pos, GralColor color, List<GralPoint> points){
    PaintOrder order = new PolyLine(pos, color, points);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralColor color, GralPlotArea.UserUnits userUnits, float[][] points){
    PolyLineFloatArray order = new PolyLineFloatArray(color, userUnits, points);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

	public void drawImage(GralImageBase image, int x, int y, int dx, int dy, GralRectangle imagePixelSize)
	{
    PaintOrder order = new PaintOrderImage(image, x, y, dx, dy, imagePixelSize);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
	}


  @Override
  public void drawText(String text)
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void setTextStyle(GralColor color, GralFont font, int origin)
  {
    // TODO Auto-generated method stub
    
  }
	
	
	//public abstract void redraw();
	
}
