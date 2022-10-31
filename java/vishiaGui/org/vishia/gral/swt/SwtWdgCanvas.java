package org.vishia.gral.swt;


import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.util.Debugutil;

/**This class enhances the swt widget Canvas with some operations,
 * especially the drawBackground from {@link GralCanvasStorage}.
 * @author Hartmut Schorrig
 *
 */
public class SwtWdgCanvas  extends Canvas
{
  private final GralCanvasStorage canvasStore;
  private final SwtMng swtMng;
  
  SwtWdgCanvas(SwtMng swtMng, GralCanvasStorage canvasStore, Composite parent, int style)
  {
    super(parent, style);    //The SWT Canvas itself, it is a composite
    this.swtMng = swtMng;
    this.canvasStore = canvasStore;
  }
  
  @Override
  public void drawBackground(GC g, int x, int y, int dx, int dy) {
    if(this.canvasStore !=null) {
      GralCanvasStorage store = this.canvasStore;
      for(GralCanvasStorage.Figure order: store.paintOrders){
        Debugutil.stop();
        for(GralCanvasStorage.FigureData orderData: order){
           switch(orderData.paintWhat){
            case GralCanvasStorage.paintLine: {
              g.setForeground(this.swtMng.getColorImpl(orderData.color));
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
              g.setForeground(this.swtMng.getColorImpl(orderData.color));
              if(orderData instanceof GralCanvasStorage.PolyLineFloatArray) {
                GralCanvasStorage.PolyLineFloatArray line = (GralCanvasStorage.PolyLineFloatArray) orderData;
                int[] points = ((GralCanvasStorage.PolyLineFloatArray) orderData).getImplStoreInt1Array();
                g.drawPolyline(points);
              } else {
                GralCanvasStorage.PolyLine line = (GralCanvasStorage.PolyLine) orderData;
                drawPolyline(g, order, line);
              }
            } break;
            case GralCanvasStorage.paintFillin: {
              Color swtColor = this.swtMng.getColorImpl(orderData.color);
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

  
  
  private void drawPolyline(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line) {
    GralRectangle rr = this.swtMng.calcWidgetPosAndSize(order.pos, 0,0);  // base position as given in the figure
    int xf, yf;
    if(line.bPointsAreGralPosUnits){
      xf = this.swtMng.gralMng.propertiesGui.xPixelUnit();  //1.0 is one GralPos unit
      yf = this.swtMng.gralMng.propertiesGui.xPixelUnit();
    } else {
      xf = rr.dx;  //0.0..1.0 is the size given in pos in both directions.
      yf = rr.dy;
    }
    int x1 = -1, y1 = -1, x2, y2;
    for(GralPoint point: line.points){
      x2 = rr.x + (int)(point.x * xf + 0.5f);              // the src points counts from bottom left of the coord. to top-right
      y2 = rr.y + rr.dy - (int)(point.y * yf + 0.5f);      //position from button, in direction top
      if(x1 >=0) {
        g.drawLine(x1, y1, x2, y2);                        //The pixels counts from top left to bottom-right
      }
      x1 = x2; y1 = y2;
    }
  }
  
  
  
  /**The listener for paint events. It is called whenever the window is shown newly. 
   * It is used as PaintListener in the using SwtAccessGral classes to inform this SWT widget
   * */
  protected PaintListener paintListener = new PaintListener() {    
    @Override public void paintControl(PaintEvent e) {
      drawBackground(e.gc, e.x, e.y, e.width, e.height);
    }
  };
  
  
  

  
}
