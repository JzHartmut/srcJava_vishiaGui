package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralCanvasArea;

/**Implementation of GralPlotArea to SET
 * @author Hartmut Schorrig
 *
 */
public class SwtCanvasArea extends GralCanvasArea._GraphicImplAccess_
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

  /**This is the Swt widget of this GralWidget. It is a Composite. */
  private final SwtWdgCanvas swtCanvas;
  
  private final SwtMng mng;
  
  protected SwtCanvasArea(GralCanvasArea gralPlotArea, SwtMng mng)
  {
    gralPlotArea.super(gralPlotArea, mng);
    this.mng = mng;
    GralWidget_ifc panel = gralPlotArea.pos().parent;
    Object swtPanel = panel.getImplAccess().getWidgetImplementation();
    Composite panelSwt = (Composite) swtPanel; //mng.getCurrentPanel();
    this.swtCanvas = new SwtWdgCanvas(this, mng, gralPlotArea.getCanvasStore(0) , panelSwt, SWT.NO_BACKGROUND);
    super.wdgimpl = this.swtCanvas;
    this.swtCanvas.setBackground(mng.getColorImpl(GralColor.getColor("white")));
    mng.setPosAndSizeSwt( gralPlotArea.pos(), this.swtCanvas, 800, 600);
    this.swtCanvas.addPaintListener(this.paintListener);
  }
  
  
  
  
  @Override public void repaintGthread ( ) {
    this.swtCanvas.redraw();
  }


  
  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); swtCanvas.setVisible(bVisible); }

  
  @SuppressWarnings("synthetic-access") 
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent ev) {
      SwtCanvasArea.this.swtCanvas.drawBackground(ev.gc, ev.x, ev.y, ev.width, ev.height);
      //SwtPlotArea.this.paintRoutine( ev);
    }
  };

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }
  

}
