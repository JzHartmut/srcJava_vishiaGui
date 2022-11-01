package org.vishia.gral.base;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.ifc.GralCanvas_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.widget.GralPlotArea;



/**The canvas storage is a storage, which stores orders to draw. 
 * It is also possible to say: It stores figures to draw.
 * This orders are drawn if the redraw-routine from the window-system is invoked. 
 * An application can call for ex the {@link #drawLine(Color, int, int, int, int)}-routine to draw a line. This line
 * will be drawn immediately, if the graphical window or widget gets the redraw-request.
 * Firstly the line will be stored only. 
 * @author Hartmut Schorrig
 *
 */
public class GralCanvasStorage implements GralCanvas_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2022-10-27 Hartmut new concept, one {@link Figure} can contain more as one {@link FigureData},
   *   which allows one Object with several lines or areas with a common position. 
   * <li>2012-09-27 Hartmut new: {@link PolyLineFloatArray}
   * <li>2012-04-22 new {@link #drawLine(GralPos, GralColor, List)}, improved {@link Figure}-derivates.
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

  /**Data class to store some representation data of a figure.
   * It is seen as 'PaintOrder' for the repaint or redraw request.
   * It has either only one or a list of {@link FigureData},
   * a {@link GralPos} as position, a common {@link GralColor}
   * and a possible background storage. 
   * <br>
   * If a background storage exists, the figure can be dynamically used.
   * The same figure can be redrawn on a given new position. 
   * The background before drawing is stored. 
   * Before redraw on a new position the background is restored.
   * Hence the figure is removed from the screen. 
   * Then it can be drawn newly on a new position.
   * <br>
   * For moving care should be taken for overlapping. 
   * On overlapped moving figures of course the background can contain a dynamic figure 
   * which is drawn before. Then this content is visible too. 
   * It means on redraw of both the restore process should be done in the reverse order as drawn.
   * This is organized by the environment class with {@link GralCanvasStorage#dynamicOrder}. 
   * the 
   */
  public static class Figure implements Iterable<FigureData>
  {
    public final String name;
    
    public final GralPos pos;
    
    /**If set to true, this figure is dynamically. 
     * It is drawn by the dynamic refresh.
     */
    public final boolean dynamic;
    
    /**True on a new position, then calculate points newly. */
    public boolean bNewPos;
    
    /**It this rectangle is given (not a null reference),
     * then this figure is dynamically and a back image is stored in the implementing level. */
    public GralRectangle backPositions;
    
    /**Used from implementing graphics on #dynamic to store the background for restore. 
     */
    public Object storageBackground;
    
    public GralRectangle pixelPos;
    
    int dxm, dym;
    
    boolean hasNextOk = true;
    
    /**Either the figure consists of only one element,
     * then {@link #listData} is null.  */
    public final FigureData data;
    
    FigureDataSet dataSet;

    protected Figure(String name, GralPos pos, FigureData data, boolean isDynamic) {
      this.name = name;
      this.pos = pos == null ? null: pos.clone();
      this.data = data;
      this.dataSet = null;
      this.dynamic = isDynamic;
    }

    protected Figure(String name, GralPos pos, FigureDataSet data, boolean isDynamic) {
      this.name = name;
      this.pos = pos == null ? null: pos.clone();
      this.data = null;
      this.dataSet = data;
      this.dynamic = isDynamic;
    }

//    protected Figure(GralPos pos, GralColor color, boolean isDynamic) {
//      this.pos = pos == null ? null: pos.clone();
//      this.color = color;
//      this.data = null;
//      this.dataSet = new FigureDataSet();
//      this.dynamic = isDynamic;
//    }
    
    /**Access to the DataSet to add more for a dedicated figure.
     * Hint: First create a FigureDataSet, after them create a figure via }
     * or via {@link GralCanvasStorage#addFigure(GralPos, GralColor, boolean)}.
     * @return
     */
    public FigureDataSet data() { return this.dataSet; }
    
    public void XXXmove(int dx, int dy) {
      this.dxm += dx;
      this.dym += dy;
    }
    
    public void setNewPosition ( float line, float lineEndOrSize, float column, float columnEndOrSize) {
      this.pos.setPosition(this.pos, line, lineEndOrSize, column, columnEndOrSize);
      this.bNewPos = true;
    }
    
    public void setNewPosition ( String posString) throws ParseException {
      this.pos.setPosition(posString, this.pos);
      this.bNewPos = true;
    }
    
    
    
    Iterator<FigureData> iter = new Iterator<FigureData>() {

      @Override public boolean hasNext () {
        return Figure.this.hasNextOk;
      }

      @Override public FigureData next () {
        Figure.this.hasNextOk = false;
        return Figure.this.data;
      }
      
    };
    
    
    @Override public Iterator<FigureData> iterator () {
      if(this.dataSet !=null) {
        return this.dataSet.listData.iterator();
      }
      else {
        this.hasNextOk = (this.data !=null);
        return this.iter;
      }
    }

    @Override public String toString() { return name + " @" + pos.toString(); }
    
  }//class PaintOrder

  
  
  public static class FigureDataSet {
    
    
    
    /**Or the figure consists of more elements,
     * then {@link #data} is null. */
    public final List<FigureData> listData = new LinkedList<FigureData>();
    
    
    
    public FigureDataSet() {
    }


    public void addData(FigureData data) {
      this.listData.add(data);
    }

    
    public PolyLine addPolyline(GralColor color) {
      PolyLine line = new GralCanvasStorage.PolyLine("" + this.listData.size(), color);
      this.listData.add(line);
      return line;
    }
    
    public Fillin addFillin(GralColor color) {
      Fillin data = new GralCanvasStorage.Fillin("" + this.listData.size(), color);
      this.listData.add(data);
      return data;
    }
    
    
    @Override public String toString() { return this.listData.toString(); }
    

  }
  
  
  
  /**This is the common abstract class for all data which describes one part of a figure.
   * @author hartmut
   *
   */
  public abstract static class FigureData
  {
    public final String name;
    
    /**One of the static int of this class. Determines what to paint. 
     * See {@link GralCanvasStorage#paintLine}, {@link GralCanvasStorage#paintImage}, 
     * */
    public final int paintWhat;

    public GralColor color;

    /**The implementation data are graphic-platform-specific. It may be prepared data for a defined
     * size appearance to support fast redrawing. */
    private Object implData;
    
    
    public Object getImplData() { return implData; }

    public void setImplData(Object implData) { this.implData = implData; }

    
    public FigureData(String name, int paintWhat, GralColor color) {
      this.name = name;
      this.paintWhat = paintWhat;
      this.color = color;
    }

    
    
  }
  
  public static class SimpleLine extends FigureData
  {
    /**Coordinates. */
    public final int x1,y1,x2,y2;

    public boolean bPointsAreGralPosUnits = true;
    
    SimpleLine ( String name, int x1, int y1, int x2, int y2, GralColor color){
      super(name, paintPolyline, color);
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
    }
  }
  
  
  
  public static class PolyLine extends FigureData
  {
    public final List<GralPoint> points;
    
    public boolean bPointsAreGralPosUnits = true;
    
    public PolyLine(String name, List<GralPoint> points, GralColor color){
      super(name, paintPolyline, color);
      this.points = points;
    }

    public PolyLine(String name, GralColor color){
      super(name, paintPolyline, color);
      this.points = new LinkedList<GralPoint>();
    }
    
    public PolyLine point(float x, float y) {
      this.points.add(new GralPoint(x, y));
      return this;
    }
  }
  
  
  
  public static class PolyLineFloatArray extends FigureData
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
    public PolyLineFloatArray(String name, GralPlotArea.UserUnits userUnits, float[][] points, int iy, GralColor color){
      super(name, paintPolyline, color);
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
  
  
  public static class Fillin extends FigureData
  {
    
    public Fillin(String name, GralColor color){
      super(name, paintFillin, color);
    }
  }
  
  
  public static class PaintOrderImage extends FigureData
  {
    public final GralImageBase image;
    public final int dxImage, dyImage;
    /**Coordinates. */
    public final int x1,y1,x2,y2;
    
    PaintOrderImage(String name, GralImageBase image, int line, int column, int heigth, int width, GralRectangle pixelImage)
    { super(name, paintImage, null);
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
  public final ConcurrentLinkedQueue<Figure> paintOrders = new ConcurrentLinkedQueue<Figure>();
  
  /**If dynamic figures are available, this list contains the draw order. 
   * On restoring the static context the reverse order is used.
   * 
   */
  List<Figure> XXXdynamicOrder; 
  
  
  /**Adds a simple figure with only one FigureData for example one line.
   * @param pos
   * @param color
   * @param data
   * @param isDynamic
   */
  public Figure addFigure(String name, GralPos pos, FigureData data, boolean isDynamic) {
    Figure figure = new Figure(name, pos, data, isDynamic);
    this.paintOrders.add(figure);
    return figure;
  }
  
  /**Adds a complex figure with a FigureDataSet
   * @param pos
   * @param color
   * @param data
   * @param isDynamic
   * @return
   */
  public Figure addFigure(String name, GralPos pos, FigureDataSet data, boolean isDynamic) {
    Figure figure = new Figure(name, pos, data, isDynamic);
    this.paintOrders.add(figure);
    return figure;
  }
  
//  public Figure addFigure(GralPos pos, GralColor color, boolean isDynamic) {
//    Figure figure = new Figure(pos, color, isDynamic);
//    this.paintOrders.add(figure);
//    return figure;
//  }
  
  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   * @param x1 TODO yet it is pixel coordinates, use GralGrid coordinates.
   * @param y1
   * @param x2
   * @param y2
   */
  @Override public void drawLine(GralColor color, int x1, int y1, int x2, int y2){
    
    FigureData data = new SimpleLine("0", x1,y1,x2,y2, color);
    Figure order = new Figure("", null, data, false);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralPos pos, GralColor color, List<GralPoint> points){
    FigureData data = new PolyLine("0", points, color);
    Figure order = new Figure("", pos, data, false);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  

  /**Accepts a order to draw a line. The coordinates are stored only. 
   * This method can be called in any thread. It is thread-safe.
   * @param color
   */
  public void drawLine(GralColor color, GralPlotArea.UserUnits userUnits, float[][] points, int iy){
    PolyLineFloatArray data = new PolyLineFloatArray("0", userUnits, points, iy, color);
    Figure order = new Figure("", null, data, false);
    paintOrders.add(order);  //paint it when drawBackground is invoked.
  }
  
  
  public void drawFillin(GralPos pos, GralColor color) {
    FigureData data = new Fillin("0", color);
    Figure order = new Figure("", pos, data, false);
    this.paintOrders.add(order);  //paint it when drawBackground is invoked.
    
  }

  public void drawImage(GralImageBase image, int x, int y, int dx, int dy, GralRectangle imagePixelSize)
  {
    FigureData data = new PaintOrderImage("0", image, x, y, dx, dy, imagePixelSize);
    Figure order = new Figure("", null, data, false);
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
