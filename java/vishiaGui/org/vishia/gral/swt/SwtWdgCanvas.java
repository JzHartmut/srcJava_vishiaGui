package org.vishia.gral.swt;


import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
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
 * @see https://www.eclipse.org/articles/Article-SWT-graphics/SWT_graphics.html
 *
 */
public class SwtWdgCanvas  extends Canvas
{
  private final GralCanvasStorage canvasStore;
  private final SwtMng swtMng;
  private final GralWidget.ImplAccess wdggaccess;
  
  private Map<String, SwtFigureData> swtFigures = new TreeMap<String, SwtFigureData>();

  /**Inherited class only for protected access to {@link GralCanvasStorage.Figure} data. */
  static private class AccessFigure extends GralCanvasStorage.Figure.Access {
    void set(GralCanvasStorage.Figure fig) { super.setFigure(fig); }
    @Override protected boolean dynamic() { return super.dynamic(); }
    @Override protected boolean newPos() { return super.newPos(); }
    @Override protected GralRectangle backPositions() { return super.backPositions(); }
    @Override protected GralCanvasStorage.FigureDataSet dataSet() { return super.dataSet(); }
  }
  
  AccessFigure figAccess = new AccessFigure();
  
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
      boolean bOnlyDynamics = this.wdggaccess.redrawOnlyDynamics();
      if(!bOnlyDynamics) {
        super.drawBackground(g, x, y, dx, dy);             // filles the background
      }
      //================================================== // restore the backfroung of figures to draw.
      for(GralCanvasStorage.Figure figure: store.paintOrders) {
        this.figAccess.set(figure);
        //================================================ // figure
        boolean bDynamic = this.figAccess.dynamic();
        if( bDynamic && this.figAccess.hasChanged(false) && !figure.bShow) {
          if(figure.storageBackground !=null) {            // if a background was stored at last time, restore it.
            Image img = (Image)figure.storageBackground;
            g.drawImage(img, this.figAccess.backPositions().x, this.figAccess.backPositions().y);
            Debugutil.stop();
          }
        }
      }// for
      //================================================== // all back positions are restored from figures which should be changed
      //
      for(GralCanvasStorage.Figure figure: store.paintOrders) {
        this.figAccess.set(figure);
      }
      //================================================== // now paint the figure
      for(GralCanvasStorage.Figure figure: store.paintOrders) { 
        //================================================ // figure
        if(figure.name.equals("txSlave2Switch"))
          Debugutil.stop();
        this.figAccess.set(figure);
        Debugutil.stop();
        boolean bDynamic = this.figAccess.dynamic();
        //==============================================   // restore the backgorund if dynamic
        if( (bDynamic && this.figAccess.hasChanged(true) || ! bOnlyDynamics) && figure.bShow) {
          if(figure.storageBackground !=null) {            // if a background was stored at last time, restore it.
            Image img = (Image)figure.storageBackground;
            g.drawImage(img, this.figAccess.backPositions().x, this.figAccess.backPositions().y);
            Debugutil.stop();
          }
            //---------------------------------------------- // calc the position of the figure if new position
          if(this.figAccess.newPos() || figure.pixelPos == null) { 
            figure.pixelPos = this.swtMng.calcWidgetPosAndSize(figure.pos, 0,0);  // base position as given in the figure
            if(this.figAccess.dynamic()) {                 // dynamic, then calc the new backPositon
              float xf = this.swtMng.gralMng.gralProps.xPixelUnit();  //1.0 is one GralPos unit
              float yf = this.swtMng.gralMng.gralProps.yPixelUnit();
              GralCanvasStorage.FigureDataSet dataSet = this.figAccess.dataSet();
              if(dataSet !=null) {
                this.figAccess.backPositions().dx = (int)(dataSet.dx * xf + 0.5f);
                this.figAccess.backPositions().dy = (int)(dataSet.dy * xf + 0.5f);
                this.figAccess.backPositions().x = figure.pixelPos.x + (int)(dataSet.x * xf) ;
                this.figAccess.backPositions().y = figure.pixelPos.y + figure.pixelPos.dy + 1 
                    - (int)((dataSet.y + dataSet.dy)* yf) ;
              } else { // use the whole position
                this.figAccess.backPositions().x = figure.pixelPos.x ;
                this.figAccess.backPositions().y = figure.pixelPos.y ;
                this.figAccess.backPositions().dx = figure.pixelPos.dx;
                this.figAccess.backPositions().dy = figure.pixelPos.dy;
              }
            }
          }
          if( bDynamic ) {
            if(this.figAccess.backPositions().dx >0) {
              if(figure.storageBackground == null) { figure.storageBackground = new Image(getDisplay(), this.figAccess.backPositions().dx, this.figAccess.backPositions().dy); }
              Image img = (Image)figure.storageBackground;
              Rectangle bounds = img.getBounds();            // check, new image necessary if too less.
              if(bounds.height < this.figAccess.backPositions().dy || bounds.width < this.figAccess.backPositions().dx) {
                figure.storageBackground = img = new Image(getDisplay(), this.figAccess.backPositions().dx, this.figAccess.backPositions().dy);
              }
              g.copyArea(img, this.figAccess.backPositions().x, this.figAccess.backPositions().y);
            }
          }
          for(GralCanvasStorage.FigureData orderData: figure) {
            if(orderData.checkVariant(figure.getVariant())) {
              switch(orderData.paintWhat){
              case drawLine: {
                g.setForeground(this.swtMng.getColorImpl(orderData.color));
                GralCanvasStorage.SimpleLine data2 = (GralCanvasStorage.SimpleLine)orderData;
                g.drawLine(data2.x1, data2.y1, data2.x2, data2.y2);
              } break;
              case drawImage: {
                GralCanvasStorage.PaintOrderImage orderImage = (GralCanvasStorage.PaintOrderImage) orderData;
                Image image = (Image)orderImage.image.getImage();
                //int dx1 = (int)(orderImage.zoom * order.x2);
                //int dy1 = (int)(orderImage.zoom * order.y2);
                g.drawImage(image, 0, 0, orderImage.dxImage, orderImage.dyImage, orderImage.x1, orderImage.y1, orderImage.x2, orderImage.y2);
              } break;
              case drawPolyline: {
                g.setForeground(this.swtMng.getColorImpl(orderData.color));
                if(orderData instanceof GralCanvasStorage.PolyLineFloatArray) {
                  GralCanvasStorage.PolyLineFloatArray line = (GralCanvasStorage.PolyLineFloatArray) orderData;
                  int[] points = ((GralCanvasStorage.PolyLineFloatArray) orderData).getImplStoreInt1Array(this.swtMng.gralMng.gralProps);
                  g.drawPolyline(points);
                } else {
                  GralCanvasStorage.PolyLine line = (GralCanvasStorage.PolyLine) orderData;
                  preparePolyline(g, figure, line);
                  drawPolyline(g, figure, line);
                }
              } break;
              case drawArc: {
                g.setForeground(this.swtMng.getColorImpl(orderData.color));
                GralCanvasStorage.Arcus arc = (GralCanvasStorage.Arcus) orderData;
                prepareArc(g, figure, arc);
                drawArc(g, figure, arc);
              } break;
              case drawFillin: {
                if(orderData.color !=null) {
                  Color swtColor = this.swtMng.getColorImpl(orderData.color);
                  g.setBackground(swtColor);
                  Rectangle posSwt = this.swtMng.getPixelPosInner(figure.pos);
                  g.fillRectangle(posSwt);
                }
              } break;
              case drawText: {
                g.setForeground(this.swtMng.getColorImpl(orderData.color));
                GralCanvasStorage.FigureText text = (GralCanvasStorage.FigureText) orderData;
                drawText(g, figure, text);
              } break;
              default: throw new IllegalArgumentException("unknown order");
              } //switch
            }// if variant
          } //for orderData
        } // dynamic
      }  //for
      Debugutil.stop();
    } //canvasStore !=null
  }  

  
  
  private SwtFigureData getSwtFigureData ( String name, int size ) {
    SwtFigureData swtfig = this.swtFigures.get(name);
    if(swtfig == null) {
      swtfig = new SwtFigureData(size);
      this.swtFigures.put(name, swtfig);
    }
    return swtfig;
  }
  
  
  private GralPoint getPixelScaling(GralCanvasStorage.Figure order) {
    int xf, yf;
    if(order.bPointsAreGralPosUnits){
      xf = this.swtMng.gralMng.gralProps.xPixelUnit();  //1.0 is one GralPos unit
      yf = this.swtMng.gralMng.gralProps.yPixelUnit();
    } else {
      xf = order.pixelPos.dx;  //0.0..1.0 is the size given in pos in both directions.
      yf = order.pixelPos.dy;
    }
    return new GralPoint(xf, yf, 0);
  }

  
  
  private void enhanceArea(int xmin, int ymin, int dx, int dy) {
    if(this.figAccess.dynamic()) {                       // adjust the min/max of the positions of the Figure
      if(this.figAccess.backPositions().x > xmin) { this.figAccess.backPositions().x = xmin; }
      if(this.figAccess.backPositions().y > ymin) { this.figAccess.backPositions().y = ymin; }
      if(this.figAccess.backPositions().dx < dx) { this.figAccess.backPositions().dx = dx; }
      if(this.figAccess.backPositions().dy < dy) { this.figAccess.backPositions().dy = dy; }
    }
  }
  
  private void preparePolyline(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line) {
    SwtFigureData swtfig = getSwtFigureData(order.name + line.name, line.points.size());
    if(this.figAccess.newPos() || ! this.figAccess.dynamic()  ) { //swtfig.pixelPoints == null) {
      GralPoint scale = getPixelScaling(order);
      int x2, y2;
      int ixP = 0;
      int xmin = 77777, xmax = -1, ymin = 77777, ymax = -1; 
      for(GralPoint point: line.points){
        x2 = order.pixelPos.x + (int)(point.x * scale.x + 0.5f);              // the src points counts from bottom left of the coord. to top-right
        y2 = order.pixelPos.y + order.pixelPos.dy - (int)(point.y * scale.y + 0.5f);      //position from button, in direction top
        swtfig.pixelPoints[ixP++] = x2;
        swtfig.pixelPoints[ixP++] = y2;
        if(this.figAccess.dynamic()) {
          if(x2 < xmin) { xmin = x2; }
          if(x2 > xmax) { xmax = x2; }
          if(y2 < ymin) { ymin = y2; }
          if(y2 > ymax) { ymax = y2; }
        }
      }                                                    //The pixels counts from top left to bottom-right
      if(this.figAccess.dynamic()) {                       // adjust the min/max of the positions of the Figure
        int dx = xmax - this.figAccess.backPositions().x +1; int dy = ymax - this.figAccess.backPositions().y +1; 
        //enhanceArea(xmin, ymin, dx, dy);
      }
    }
  }

  
  private void drawPolyline(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.PolyLine line) {
    g.setLineWidth(line.width);
    SwtFigureData swtfig = this.swtFigures.get(order.name + line.name);
    if(swtfig != null) {
      g.drawPolyline(swtfig.pixelPoints);
    }
  }
  
  
  private void prepareArc(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.Arcus arc) {
    SwtFigureData swtfig = getSwtFigureData(order.name + arc.name, 6);
    if(this.figAccess.newPos() || ! this.figAccess.dynamic()  ) {
      GralPoint scale = getPixelScaling(order);
      int x,y, dx, dy;
      swtfig.pixelPoints[2] = (dx = (int)(2 * arc.dxy.x * scale.x + 0.5f));
      swtfig.pixelPoints[3] = (dy = (int)(2 * arc.dxy.y * scale.y + 0.5f));
      swtfig.pixelPoints[0] = x = order.pixelPos.x + (int)((arc.center.x - arc.dxy.x) * scale.x + 0.5f);
      swtfig.pixelPoints[1] = y = order.pixelPos.y + order.pixelPos.dy - (int)((arc.center.y + arc.dxy.y) * scale.y + 0.5f);
      if(this.figAccess.dynamic()) {
        //enhanceArea(x-dx-1, y-dy-1, 2*dx +2, 2*dy +2);
      }
    }
  }
  
  
  
  private void drawArc(GC g, GralCanvasStorage.Figure order, GralCanvasStorage.Arcus arc) {
    SwtFigureData swtfig = this.swtFigures.get(order.name + arc.name);
    if(swtfig != null) {
      g.drawArc(swtfig.pixelPoints[0], swtfig.pixelPoints[1], swtfig.pixelPoints[2], swtfig.pixelPoints[3], arc.angleStart, arc.angleEnd );
    }
  }
  
  
  private void drawText(GC g, GralCanvasStorage.Figure figure, GralCanvasStorage.FigureText text) {
    Font font = this.swtMng.propertiesGuiSwt.fontSwt(text.font);
    g.setFont(font);
    FontMetrics fontMetric = g.getFontMetrics();
    int ytext = fontMetric.getHeight();
    GralPoint scale = getPixelScaling(figure);
    int x = (int)(text.x * scale.x + 0.5f) + figure.pixelPos.x;
    int y = (int)(text.y * scale.y + 0.5f) + figure.pixelPos.y - ytext;  //oriented to the bottom line of the first line
    if(text.text.indexOf('\n') >=0) {
      int flags = SWT.DRAW_TRANSPARENT | SWT.DRAW_DELIMITER;   //delimiter necessary, elsewhere \n is not regarded. 
      g.drawText(text.text, x, y, flags);
    } else {
      //SWT docu says: drawString is faster. But it does not support a \n character.
      g.drawString(text.text, x, y, true);
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
