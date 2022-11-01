package org.vishia.gral.swt;


import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralWidget;
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
  private final GralWidget.ImplAccess wdggaccess;
  
  private Map<String, SwtFigureData> swtFigures = new TreeMap<String, SwtFigureData>();
  
  SwtWdgCanvas(GralWidget.ImplAccess wdggaccess, SwtMng swtMng, GralCanvasStorage canvasStore, Composite parent, int style)
  {
    super(parent, style);    //The SWT Canvas itself, it is a composite
    this.wdggaccess = wdggaccess;
    this.swtMng = swtMng;
    this.canvasStore = canvasStore;
  }
  
  @Override
  public void drawBackground(GC g, int x, int y, int dx, int dy) {
    if(this.canvasStore !=null) {
      GralCanvasStorage store = this.canvasStore;
      for(GralCanvasStorage.Figure order: store.paintOrders) {
        Debugutil.stop();
        if(order.storageBackground !=null) {
          Image img = (Image)order.storageBackground;
          g.drawImage(img, order.backPositions.x, order.backPositions.y);
        }
        if(order.dynamic || ! this.wdggaccess.redrawOnlyDynamics()) {
          if(order.bNewPos || order.pixelPos == null) {
            order.pixelPos = this.swtMng.calcWidgetPosAndSize(order.pos, 0,0);  // base position as given in the figure
            if(order.dynamic) {
              if(order.backPositions == null) { order.backPositions = new GralRectangle(0,0,0,0); }
              order.backPositions.x = order.backPositions.y = Integer.MAX_VALUE;
              order.backPositions.dx = order.backPositions.dy = -1;       // initialize it with values to search min/max
            }
          }
          for(GralCanvasStorage.FigureData orderData: order) {
             switch(orderData.paintWhat){
              case GralCanvasStorage.paintLine: {
              } break;
              case GralCanvasStorage.paintImage: {
                GralCanvasStorage.PaintOrderImage orderImage = (GralCanvasStorage.PaintOrderImage) orderData;
              } break;
              case GralCanvasStorage.paintPolyline: {
                if(orderData instanceof GralCanvasStorage.PolyLineFloatArray) {
                  GralCanvasStorage.PolyLineFloatArray line = (GralCanvasStorage.PolyLineFloatArray) orderData;
                  int[] points = ((GralCanvasStorage.PolyLineFloatArray) orderData).getImplStoreInt1Array();
                  //g.drawPolyline(points);
                } else {
                  GralCanvasStorage.PolyLine line = (GralCanvasStorage.PolyLine) orderData;
                  preparePolyline(g, order, line);
                }
              } break;
              case GralCanvasStorage.paintFillin: {
              } break;
              default: throw new IllegalArgumentException("unknown order");
            } //switch
          } //for orderData
          if(order.dynamic && order.backPositions.dx >0) {
            if(order.storageBackground == null) { order.storageBackground = new Image(getDisplay(), order.backPositions.dx, order.backPositions.dy); }
            Image img = (Image)order.storageBackground;
            Rectangle bounds = img.getBounds();            // check, new image necessary if too less.
            if(bounds.height < order.backPositions.dy || bounds.width < order.backPositions.dx) {
              order.storageBackground = img = new Image(getDisplay(), order.backPositions.dx, order.backPositions.dy);
            }
            g.copyArea(img, order.backPositions.x, order.backPositions.y);
          }
          for(GralCanvasStorage.FigureData orderData: order) {
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
           } //switch
         } //for orderData
         
        } // dynamic
      }  //for
      Debugutil.stop();
    } //canvasStore !=null
  }  

  
  
  private void preparePolyline(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line) {
    SwtFigureData swtfig = this.swtFigures.get(order.name + line.name);
    if(swtfig == null) {
      swtfig = new SwtFigureData(line.points.size());
      this.swtFigures.put(order.name + line.name, swtfig);
    }
    int xf, yf;
    if(order.bNewPos || ! order.dynamic  ) { //swtfig.pixelPoints == null) {
      if(line.bPointsAreGralPosUnits){
        xf = this.swtMng.gralMng.propertiesGui.xPixelUnit();  //1.0 is one GralPos unit
        yf = this.swtMng.gralMng.propertiesGui.xPixelUnit();
      } else {
        xf = order.pixelPos.dx;  //0.0..1.0 is the size given in pos in both directions.
        yf = order.pixelPos.dy;
      }
      int x1 = -1, y1 = -1, x2, y2;
      int ixP = 0;
      int xmin = 77777, xmax = -1, ymin = 77777, ymax = -1; 
      for(GralPoint point: line.points){
        x2 = order.pixelPos.x + (int)(point.x * xf + 0.5f);              // the src points counts from bottom left of the coord. to top-right
        y2 = order.pixelPos.y + order.pixelPos.dy - (int)(point.y * yf + 0.5f);      //position from button, in direction top
        swtfig.pixelPoints[ixP++] = x2;
        swtfig.pixelPoints[ixP++] = y2;
        if(order.dynamic) {
          if(x2 < xmin) { xmin = x2; }
          if(x2 > xmax) { xmax = x2; }
          if(y2 < xmin) { ymin = y2; }
          if(y2 > xmax) { ymax = y2; }
        }
  //      if(x1 >=0) {
  //        g.drawLine(x1, y1, x2, y2);                        //The pixels counts from top left to bottom-right
  //      }
  //      x1 = x2; y1 = y2;
      }
      if(order.dynamic) {                                  // adjust the min/max of the positions of the Figure
        if(order.backPositions.x > xmin) { order.backPositions.x = xmin; }
        if(order.backPositions.y > ymin) { order.backPositions.y = ymin; }
        int dx = xmax - order.backPositions.x +1; int dy = ymax - order.backPositions.y +1; 
        if(order.backPositions.dx < dx) { order.backPositions.dx = dx; }
        if(order.backPositions.dy < dy) { order.backPositions.dy = dy; }
      }
    }
  }

  
  
  
  private void drawPolyline(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line) {
    SwtFigureData swtfig = this.swtFigures.get(order.name + line.name);
    if(swtfig != null) {
      g.drawPolyline(swtfig.pixelPoints);
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
  
  
  /**This are data for only one widget.
   * Note: The {@link GralCanvasStorage.FigureData} is shared between several {@link GralCanvasStorage.Figure}.
   * Thats why the {@link #pixelPoints} cannot stored inside the {@link GralCanvasStorage.PolyLine}.
   * This class is created in the implementation layer associated by the key {@link GralCanvasStorage.Figure#name}
   * and then the internal index of the {@link GralCanvasStorage.FigureDataSet#listData}.
   * 
   * 
   */
  private static class SwtFigureData {
    /**Calculated pixel points o a given position ({@link GralCanvasStorage.Figure#pos})
     * null if the position is given newly, then calculate new.
     */
    public final int[] pixelPoints;
    
    public final GralRectangle XXpixelRange;
    
    /**Implementation specific background storage. */
    public Object backgroundImageStore;
    
    SwtFigureData( int nrPoints) {
      this.pixelPoints = new int[2*nrPoints];
      this.XXpixelRange = new GralRectangle(77777, 77777, -1, -1);
    }
  }
  

  
}
