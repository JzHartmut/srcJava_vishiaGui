package org.vishia.gral.ifc;

import java.util.Iterator;
import java.util.LinkedList;
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
   * <li>2022-10-27 Hartmut new concept, one {@link PaintOrder} can contain more as one {@link PaintOrderData},
   *   which allows one Object with several lines or areas with a common position. 
   * <li>2012-09-27 Hartmut new: {@link PolyLineFloatArray}
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
  public static class PaintOrder implements Iterable<PaintOrderData>
  {
    public final GralPos pos;
    
    public final GralColor color;
    
    public final PaintOrderData data;
    
    int dxm, dym;
    
    boolean hasNextOk = true;
    
    public final List<PaintOrderData> listData;
    
    

    PaintOrder(GralPos pos, GralColor color, PaintOrderData data) {
      this.pos = pos == null ? null: pos.clone();
      this.color = color;
      this.data = data;
      this.listData = null;
    }

    PaintOrder(GralPos pos, GralColor color, List<PaintOrderData> data) {
      this.pos = pos == null ? null: pos.clone();
      this.color = color;
      this.data = null;
      this.listData = data;
    }

    public PaintOrder(GralPos pos, GralColor color) {
      this.pos = pos == null ? null: pos.clone();
      this.color = color;
      this.data = null;
      this.listData = new LinkedList<PaintOrderData>();
    }
    
    public void addData(PaintOrderData data) {
      this.listData.add(data);
    }

    public void move(int dx, int dy) {
      this.dxm += dx;
      this.dym += dy;
    }
    
    
    Iterator<PaintOrderData> iter = new Iterator<PaintOrderData>() {

      @Override public boolean hasNext () {
        // TODO Auto-generated method stub
        return PaintOrder.this.hasNextOk;
      }

      @Override public PaintOrderData next () {
        // TODO Auto-generated method stub
        PaintOrder.this.hasNextOk = false;
        return PaintOrder.this.data;
      }
      
    };
    
    
    @Override public Iterator<PaintOrderData> iterator () {
      if(this.listData !=null) {
        return this.listData.iterator();
      }
      else {
        this.hasNextOk = (this.data !=null);
        return this.iter;
      }
    }

  }//class PaintOrder

  public abstract static class PaintOrderData
  {
    /**One of the static int of this class. Determines what to paint. 
     * See {@link GralCanvasStorage#paintLine}, {@link GralCanvasStorage#paintImage}, 
     * */
    public final int paintWhat;

    /**The implementation data are graphic-platform-specific. It may be prepared data for a defined
     * size appearance to support fast redrawing. */
    private Object implData;
    
    public Object getImplData() { return implData; }

    public void setImplData(Object implData) { this.implData = implData; }

    
    public PaintOrderData(int paintWhat) {
      this.paintWhat = paintWhat;
    }

    
    
  }
  
  public static class SimpleLine extends PaintOrderData
  {
    /**Coordinates. */
    public final int x1,y1,x2,y2;

    public boolean bPointsAreGralPosUnits = true;
    
    SimpleLine ( int x1, int y1, int x2, int y2){
      super(paintPolyline);
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
  }
  
  
  
  public static class PolyLine extends PaintOrderData
  {
    public final List<GralPoint> points;
    
    public boolean bPointsAreGralPosUnits = true;
    
    public PolyLine(List<GralPoint> points){
      super(paintPolyline);
      this.points = points;
    }
  }
  
  
  
  public static class PolyLineFloatArray extends PaintOrderData
  {
    private final float[][] points;
    
    private final int iy;
    
    final GralPlotArea.UserUnits userUnits;
    
    private Object implStore;
    
    /**Creates an Paint order which paints a line from array points.
     * @param color
     * @param userUnits
     * @param points The elements[...][0] contains the x-value. The elements[...][iy] contains the y-value.
     */
    public PolyLineFloatArray(GralPlotArea.UserUnits userUnits, float[][] points, int iy){
      super(paintPolyline);
      this.userUnits = userUnits;
      this.points = points;
      this.iy = iy;
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
          float x = point[0], y = point[iy];
          store[++ixd] = (int)(userUnits.fx * fxp * (x - userUnits.x0) + 0.5f);
          store[++ixd] = (int)(userUnits.fy * fyp * (y - userUnits.y0) + 0.5f);
        }
      }
      return (int[])implStore; 
    }
  }
  
  
  public static class Fillin extends PaintOrderData
  {
    
    Fillin(){
      super(paintFillin);
    }
  }
  
  
  public static class PaintOrderImage extends PaintOrderData
  {
    public final GralImageBase image;
    public final int dxImage, dyImage;
    /**Coordinates. */
    public final int x1,y1,x2,y2;
    
    PaintOrderImage(GralImageBase image, int line, int column, int heigth, int width, GralRectangle pixelImage)
    { super(paintImage);
      y1 = line; y2 = heigth; x1 = column; x2 = width;  
      this.image = image;
      this.dxImage = pixelImage.dx;
      this.dyImage = pixelImage.dy;
    }
  }//class PaintOrderImage
  
  public final static int paintLine = 0xee, paintImage = 0x1ae;
  
  public final static int paintFillin = 'f';
  
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
  @Override public void drawLine(GralColor color, int x1, int y1, int x2, int y2){
    
    PaintOrderData data = new SimpleLine(x1,y1,x2,y2);
    PaintOrder order = new PaintOrder(null, color, data);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralPos pos, GralColor color, List<GralPoint> points){
    PaintOrderData data = new PolyLine(points);
    PaintOrder order = new PaintOrder(pos, color, data);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralColor color, GralPlotArea.UserUnits userUnits, float[][] points, int iy){
    PolyLineFloatArray data = new PolyLineFloatArray(userUnits, points, iy);
    PaintOrder order = new PaintOrder(null, color, data);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  
  
  public void drawFillin(GralPos pos, GralColor color) {
    PaintOrderData data = new Fillin();
    PaintOrder order = new PaintOrder(pos, color, data);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
    
  }

  public void drawImage(GralImageBase image, int x, int y, int dx, int dy, GralRectangle imagePixelSize)
  {
    PaintOrderData data = new PaintOrderImage(image, x, y, dx, dy, imagePixelSize);
    PaintOrder order = new PaintOrder(null, null, data);
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
