package org.vishia.gral.swt;


import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralRectangle;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**Class to store some graphical figures to draw it in its {@link SwtCanvas#drawBackground(GC, int, int, int, int)}-routine.
 * The figures are stored with its coordinates and it are drawn if necessary. 
 * <br><br>
 * It can contain some GUI-Elements like Button, Text, Label, Table etc from org.eclipse.swt.widgets.
 * The graphical figures are shown as background than.
 * 
 * @author Hartmut Schorrig
 *
 */
public class SwtCanvasStorePanel extends SwtPanel  //CanvasStorePanel //
{
  
  //protected SwtCanvas swtCanvas;
  
  /**The storage for the Canvas content. */
  //GralCanvasStorage store = new GralCanvasStorage();
  
  
  
  
  //class MyCanvas extends Canvas{
  
  private static final long serialVersionUID = 6448419343757106982L;
  
  protected Color XXXcurrColor;
  
  /**Constructs the instance with a SWT-Canvas Panel.
   * @param parent
   * @param style
   * @param backGround
   */
  public SwtCanvasStorePanel(GralPanelContent panelg, Composite parent, int style, Color backGround, GralMng gralMng)
  { super(panelg, (Composite)null);
    //gralPanel().panel.canvas = new GralCanvasStorage();
    SwtCanvas swtCanvas = new SwtCanvas((SwtMng)gralMng._mngImpl, this._panel.canvas, parent, style);
    super.panelSwtImpl = swtCanvas;
    swtCanvas.addControlListener(resizeItemListener);
    swtCanvas.setData(this);
    swtCanvas.setLayout(null);
    XXXcurrColor = swtCanvas.getForeground();
    swtCanvas.addPaintListener(swtCanvas.paintListener);
    swtCanvas.setBackground(backGround);
    this.wdgimpl = swtCanvas;
  }
  
  /**Constructor called in derived classes. The derived class have to be instantiate the Canvas
   * maybe with other draw routines. 
   */
  protected SwtCanvasStorePanel(GralPanelContent panelg)
  {
    super(panelg);
//    gralPanel().canvas = new GralCanvasStorage();
  }
  

  
  
  /**Implementation class for Canvas for Swt
   * This class is a org.eclipse.swt.widgets.Composite. 
   */
  protected static class SwtCanvas extends Canvas
  {
    private final GralCanvasStorage canvasStore;
    private final SwtMng swtMng;
    
    SwtCanvas(SwtMng swtMng, GralCanvasStorage canvasStore, Composite parent, int style)
    {
      super(parent, style);    //The SWT Canvas itself, it is a composite
      this.swtMng = swtMng;
      this.canvasStore = canvasStore;
    }
    
    @Override
    public void drawBackground(GC g, int x, int y, int dx, int dy) {
      //NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
      if(this.canvasStore !=null) {
        for(GralCanvasStorage.Figure order: this.canvasStore.paintOrders){
          for(GralCanvasStorage.FigureData orderData: order){
            
             switch(orderData.paintWhat){
              case GralCanvasStorage.paintLine: {
                g.setForeground(this.swtMng.getColorImpl(order.color));
                GralCanvasStorage.SimpleLine data2 = (GralCanvasStorage.SimpleLine)orderData;
                g.drawLine(data2.x1, data2.y1, data2.x2, data2.y2);
              } break;
              case GralCanvasStorage.paintImage: {
                GralCanvasStorage.PaintOrderImage orderImage = (GralCanvasStorage.PaintOrderImage) orderData;
                Image image = (Image)orderImage.image.getImage();
                //int dx1 = (int)(orderImage.zoom * order.x2);
                //int dy1 = (int)(orderImage.zoom * order.y2);
                g.drawImage(image, 0, 0, orderImage.dxImage, orderImage.dyImage, orderImage.x1, orderImage.y1, orderImage.x2, orderImage.y2);
              } break;
              case GralCanvasStorage.paintPolyline: {
                g.setForeground(this.swtMng.getColorImpl(order.color));
                if(orderData instanceof GralCanvasStorage.PolyLineFloatArray) {
                  GralCanvasStorage.PolyLineFloatArray line = (GralCanvasStorage.PolyLineFloatArray) orderData;
                  int[] points = ((GralCanvasStorage.PolyLineFloatArray) orderData).getImplStoreInt1Array();
                  g.drawPolyline(points);
                } else {
                  GralCanvasStorage.PolyLine line = (GralCanvasStorage.PolyLine) orderData;
                  SwtCanvasStorePanel.SwtPolyLine swtLine;
                  { Object oImpl = line.getImplData();
                    if(oImpl == null){
                      swtLine = new SwtCanvasStorePanel.SwtPolyLine(order, line, this.swtMng);
                      line.setImplData(swtLine);
                    } else {
                      swtLine = (SwtCanvasStorePanel.SwtPolyLine) oImpl;
                    }
                  }
                  g.drawPolyline(swtLine.points);
                }
              } break;
              case GralCanvasStorage.paintFillin: {
                Color swtColor = this.swtMng.getColorImpl(order.color);
                g.setBackground(swtColor);
                Rectangle posSwt = this.swtMng.getPixelPosInner(order.pos);
                g.fillRectangle(posSwt);
              } break;
              default: throw new IllegalArgumentException("unknown order");
            }
          }
        }
      }
    }  

    /**The listener for paint events. It is called whenever the window is shown newly. */
    protected PaintListener paintListener = new PaintListener()
    {

      @Override
      public void paintControl(PaintEvent e) {
        // TODO Auto-generated method stub
        GC gc = e.gc;
        drawBackground(e.gc, e.x, e.y, e.width, e.height);
        //stop();
      }
      
    };
    
    void stop(){}

  }
  
  @Override public Control getWidgetImplementation(){ return (Control)this.wdgimpl; } 

  @Override public boolean setFocusGThread()
  {
    if(!super.setFocusGThread()){
      return ((Control)this.wdgimpl).setFocus();
    } else return true;
  }

  
  void stop(){} //debug

  public static class SwtPolyLine // extends GralCanvasStorage.PolyLine
  {
    int[] points;
    int nrofPoints;
    Color color;
    
    SwtPolyLine(GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line, SwtMng swtMng){
      nrofPoints = line.points.size();
      points = new int[2 * nrofPoints];
      GralRectangle rr = swtMng.calcWidgetPosAndSize(order.pos, 0,0);
      int ix = -1;
      int xf, yf;
      if(line.bPointsAreGralPosUnits){
        xf = swtMng.gralMng.propertiesGui.xPixelUnit();  //1.0 is one GralPos unit
        yf = swtMng.gralMng.propertiesGui.xPixelUnit();
      } else {
        xf = rr.dx;  //0.0..1.0 is size of line.pos
        yf = rr.dy;
      }
      for(GralPoint point: line.points){
        int x = rr.x + (int)(point.x * xf + 0.5f);         // the src points counts from bottom left of the coord. to top-right
        int y = rr.y + rr.dy - (int)(point.y * yf + 0.5f); //position from button, in direction top
        points[++ix] = x;           //The pixels counts from top left to bottom-right
        points[++ix] = y;
      }
      color = swtMng.getColorImpl(order.color);
    }
  }
  

}

