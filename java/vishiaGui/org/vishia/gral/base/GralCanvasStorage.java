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
import org.vishia.gral.widget.GralCanvasArea;



/**The canvas storage is a storage, which stores orders to draw. 
 * It is also possible to say: It stores figures to draw.
 * This orders are drawn if the redraw-routine from the window-system is invoked. 
 * <br><br>
 * A canvas is organized in {@link Figure}. A figure as one position ({@link GralPos}) 
 * and only one or some {@link FigureData}. 
 * For more as one FigureData per Figure the container {@link FigureDataSet} is used.
 * <br><br>
 * {@link FigureData} and {@link FigureDataSet} can be created independent of a {@link Figure}
 * and used in more as one figure on different positions, with similar presentation: <pre>
  /**Dataset of the border of the data words in memory:
   * <pre>
   * +--+--+
   * |  |  |
   * +--+--+ </pre>
   * It is used for all Mem presentation (Master and Slave).
   * /
  final GralCanvasStorage.FigureDataSet figData_Words = new GralCanvasStorage.FigureDataSet(); { 
    GralColor color = GralColor.getColor("bk");
    this.figData_Words.addPolyline(color).point(0, 0).point(0,2);  // |
    for(int ix = 0; ix <20; ++ix) {
      this.figData_Words.addPolyline(color)
      .point(ix,2)
      .point(ix+1,2)  // --+    20 times gives rectangles for each word.
      .point(ix+1,0)  //   |
      .point(ix,0);   // --+
    }
  }
  </pre>
 * Above it is an example to define the apearance of a figure as FigureDataSet on construction inside a class.
 * To use it in two figures, the application should call in an initialization phase before establishing the graphic: <pre>
  void init() throws ParseException {
    //
    this.pos.setPosition("10-2,10+1++");
    this.canvas.addFigure("dataWordsMaster", this.pos, this.figData_Words, false);
    
    this.pos.setPosition("10-2,40+1++");
    this.canvas.addFigure("dataWordsSlave1", this.pos, this.figData_Words, false);
   </pre>
 * <b>Dynamic figures</b><br>
 * You can mark a figure as dynamic (last argument of {@link #addFigure(String, GralPos, FigureDataSet, boolean)} above).
 * If you invoke {@link GralWidget#redrawOnlyDynamics(int, int)} or {@link GralWidget#redraw(int, int, boolean)} 
 * with true as last argument then only these figures are drawn, the background of the canvas will not be deleted.
 * You can change properties of the figures before the redraw(...) invocation, especially the position and the color.
 * Then this new appearance is showing. <br>
 * Of course the given presentation of the figure should be removed before new drawing. 
 * Note that the canvas will not be cleaned, all is remained. 
 * The cleanup is organized in the implementation graphic without user programming effort:
 * <br>
 * The coordinates of the area of a figure are calculated. Before drawing the figure the canvas content is stored in the heap,
 * using a proper container such as {@link org.eclipse.swt.graphics.Image}. 
 * Before redraw the figure this canvas content is restored. 
 * This process should be well organized, meaningful if more as one figures overlap. 
 * But this is done in the implementation level of the library {@link org.vishia.gral.swt.SwtWdgCanvas} for the SWT implementation.
 
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
   * It is seen as 'draw order' for the repaint or redraw request.
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
    
    public boolean bPointsAreGralPosUnits = true;

    
    /**If set to true, this figure is dynamically. 
     * It is drawn by the dynamic refresh.
     */
    protected final boolean dynamic;
    
    /**false then don't show the figure (for the next redraw on dynamic) 
     * This flag can be changed immediately from outside. */
    public boolean bShow = true;
    
    /**True on a new position or default, then calculate points newly. */
    protected boolean bNewPos = true;
    
    /**It this rectangle is given (not a null reference),
     * then this figure is dynamically and a back image is stored in the implementing level. */
    protected GralRectangle backPositions;
    
    protected int variant;
    
    /**Used from implementing graphics on #dynamic to store the background for restore. 
     */
    public Object storageBackground;
    
    public GralRectangle pixelPos;
    
    //int dxm, dym;
    
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
    
    public void setNewPosition ( float line, float lineEndOrSize, float column, float columnEndOrSize) {
      this.pos.setPosition(this.pos, line, lineEndOrSize, column, columnEndOrSize);
      this.bNewPos = true;
    }
    
    public void setNewPosition ( String posString) throws ParseException {
      this.pos.setPosition(posString, this.pos);
      this.bNewPos = true;
    }
    
    public void setVariant(int variant) { this.variant = variant; }
    
    public int getVariant() { return this.variant; }
    
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

    @Override public String toString() { return this.name + " @" + this.pos.toString(); }
    
    
    /**To access protected members from the package of the implementing layer, use this as base class.
     */
    public static class Access {
      Figure mthis;
      protected void setFigure(Figure fig) { this.mthis = fig; }
      protected boolean dynamic() { return this.mthis.dynamic; }
      protected boolean newPos() { return this.mthis.bNewPos; }
      protected GralRectangle backPositions() { 
        if(this.mthis.backPositions == null) { this.mthis.backPositions = new GralRectangle(0,0,0,0); }
        return this.mthis.backPositions; 
      }
      protected int variant() { return this.mthis.variant; }
    }
    
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


    public PolyLine addPolyline(GralColor color, int width) {
      return addPolyline(color, width, 0xffffffff);
    }

    
    /**Adds a line with some points. Usage pattern:<pre>
     * myFigureData.addPolyline(myColor, 1).point(0,0).point(1.5, -2.5);
     * @param color
     * @param width
     * @return the line to add points.
     */
    public PolyLine addPolyline(GralColor color, int width, int variantMask) {
      PolyLine line = new GralCanvasStorage.PolyLine("" + this.listData.size(), color, width);
      line.setVariantMask(variantMask);
      this.listData.add(line);
      return line;
    }
    
    public Arcus addArcline(GralColor color, float x, float y, float dx, float dy, int gStart, int gEnd) {
      Arcus arcline = new Arcus("" + this.listData.size(), color, x, y, dx, dy, gStart, gEnd);
      this.listData.add(arcline);
      return arcline;
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

    /**Color for this simple element, foreground. */
    public GralColor color;
    
    /**Supports 32 variants, each bit for one, default: all. */
    protected int variantMask;

    /**The implementation data are graphic-platform-specific. It may be prepared data for a defined
     * size appearance to support fast redrawing. */
    private Object implData;
    
    
    public Object getImplData() { return implData; }

    public void setImplData(Object implData) { this.implData = implData; }

    
    public FigureData(String name, int paintWhat, GralColor color) {
      this.name = name;
      this.paintWhat = paintWhat;
      this.color = color;
      this.variantMask = 0xffffffff;
    }

    public FigureData(String name, int paintWhat, GralColor color, int variantMask) {
      this.name = name;
      this.paintWhat = paintWhat;
      this.color = color;
      this.variantMask = variantMask;
    }

    public void setVariantMask(int mask) { this.variantMask = mask; }
    
    /**True if the FigureData should be drawn in this variant.
     * @param variant number 0..31
     * @return true if this bit is set.
     */
    public boolean checkVariant(int variant) { return ((1<<variant) & this.variantMask) !=0; }
  }
  
  public static class SimpleLine extends FigureData
  {
    /**Coordinates. */
    public final int x1,y1,x2,y2;

    
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
    
    public final int width;
    
    public PolyLine(String name, List<GralPoint> points, GralColor color){
      super(name, paintPolyline, color);
      this.points = points;
      this.width = 1;
    }

    /**Constructor for free points add with {@link #point(float, float)}
     * Hint: use {@link GralCanvasStorage.FigureDataSet#addPolyline(GralColor)} instead this ctor.
     * @param name
     * @param color
     */
    public PolyLine(String name, GralColor color, int width){
      super(name, paintPolyline, color);
      this.points = new LinkedList<GralPoint>();
      this.width = width;
    }
    
    /**Adds a point, can be called as concatenation.
     * @param x Depending on setting on Figure it is grid unit, or relative inside the postion.
     * @param y y counts from bottom to top, as for mathematically diagrams.
     *   The point(0,0) is bottom left from a given {@link GralPos}. 
     * @return this for concatenation
     */
    public PolyLine point(float x, float y) {
      this.points.add(new GralPoint(x, y));
      return this;
    }
  }
  
  public static class Arcus extends FigureData
  {
    public final GralPoint center, dxy;
    public final int angleStart, angleEnd;

    public Arcus(String name, GralColor color, float x, float y, float dx, float dy, int gStart, int gEnd) {
      super(name, drawArg, color);
      this.center = new GralPoint(x,y);
      this.dxy = new GralPoint(dx, dy);
      this.angleStart = gStart; this.angleEnd = gEnd;
    }
    
    
  }  
  
  public static class PolyLineFloatArray extends FigureData
  {
    private final float[][] points;
    
    private final int iy;
    
    final GralCanvasArea.UserUnits userUnits;
    
    private Object implStore;
    
    /**Creates an Paint order which paints a line from array points.
     * @param color
     * @param userUnits
     * @param points The elements[...][0] contains the x-value. The elements[...][iy] contains the y-value.
     */
    public PolyLineFloatArray(String name, GralCanvasArea.UserUnits userUnits, float[][] points, int iy, GralColor color){
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
  
  public final static int drawArg = 'a';
  
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
  public void drawLine(GralColor color, GralCanvasArea.UserUnits userUnits, float[][] points, int iy){
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
