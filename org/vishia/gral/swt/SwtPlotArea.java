package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralCanvasStorage;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.GralPlotArea;

/**Implementation of GralPlotArea to SET
 * @author Hartmut Schorrig
 *
 */
public class SwtPlotArea extends GralPlotArea.ImplAccess
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

  private final Canvas swtCanvas;
  
  private final SwtMng mng;
  
  protected SwtPlotArea(GralPlotArea gralPlotArea, SwtMng mng)
  {
    gralPlotArea.super(gralPlotArea);
    this.mng = mng;
    GralPanelContent panel = gralPlotArea.pos().panel;
    Object swtPanel = panel._wdgImpl.getWidgetImplementation();
    Composite panelSwt = (Composite) swtPanel; //mng.getCurrentPanel();
    swtCanvas = new Canvas(panelSwt, 0);
    swtCanvas.setBackground(mng.getColorImpl(GralColor.getColor("white")));
    mng.setPosAndSizeSwt( gralPlotArea.pos(), swtCanvas, 800, 600);
    swtCanvas.addPaintListener(paintListener);
  }
  
  
  private void paintRoutine(PaintEvent ev) {
    GC g = ev.gc;  
    for(GralCanvasStorage.PaintOrder order: super.canvasStore().paintOrders){
        switch(order.paintWhat){
          case GralCanvasStorage.paintLine: {
            g.setForeground(mng.getColorImpl(order.color));
            g.drawLine(order.x1, order.y1, order.x2, order.y2);
          
          } break;
          case GralCanvasStorage.paintImage: {
            GralCanvasStorage.PaintOrderImage orderImage = (GralCanvasStorage.PaintOrderImage) order;
            Image image = (Image)orderImage.image.getImage();
            //int dx1 = (int)(orderImage.zoom * order.x2);
            //int dy1 = (int)(orderImage.zoom * order.y2);
            g.drawImage(image, 0, 0, orderImage.dxImage, orderImage.dyImage, order.x1, order.y1, order.x2, order.y2);
          } break;
          case GralCanvasStorage.paintPolyline: {
            if(order instanceof GralCanvasStorage.PolyLineFloatArray) {
              GralCanvasStorage.PolyLineFloatArray line = (GralCanvasStorage.PolyLineFloatArray) order;
              int[] points = ((GralCanvasStorage.PolyLineFloatArray) order).getImplStoreInt1Array();
              g.setForeground(mng.getColorImpl(order.color));
              g.drawPolyline(points);
            } else {
              GralCanvasStorage.PolyLine line = (GralCanvasStorage.PolyLine) order;
              SwtCanvasStorePanel.SwtPolyLine swtLine;
              { Object oImpl = line.getImplData();
                if(oImpl == null){
                  swtLine = new SwtCanvasStorePanel.SwtPolyLine(line, mng);
                  line.setImplData(swtLine);
                } else {
                  swtLine = (SwtCanvasStorePanel.SwtPolyLine) oImpl;
                }
              }
              g.drawPolyline(swtLine.points);
            }
          } break;
          default: throw new IllegalArgumentException("unknown order");
        }
      }
  
  }
  
  
  
  @SuppressWarnings("synthetic-access") 
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent ev) {
      SwtPlotArea.this.paintRoutine( ev);
    }
  };
  

}
