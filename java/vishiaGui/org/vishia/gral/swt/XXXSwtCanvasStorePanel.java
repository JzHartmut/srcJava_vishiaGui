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

/**Class to store some graphical figures to draw it in its {@link SwtWdgCanvas#drawBackground(GC, int, int, int, int)}-routine.
 * The figures are stored with its coordinates and it are drawn if necessary. 
 * <br><br>
 * It can contain some GUI-Elements like Button, Text, Label, Table etc from org.eclipse.swt.widgets.
 * The graphical figures are shown as background than.
 * 
 * @author Hartmut Schorrig
 *
 */
public class XXXSwtCanvasStorePanel extends SwtPanel  //CanvasStorePanel //
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
  public XXXSwtCanvasStorePanel(GralPanelContent panelg, Composite parent, int style, Color backGround, GralMng gralMng)
  { super(panelg, (Composite)null);
    //gralPanel().panel.canvas = new GralCanvasStorage();
    SwtWdgCanvas swtCanvas = new SwtWdgCanvas((SwtMng)gralMng._mngImpl, this._panel.canvas, parent, style);
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
  protected XXXSwtCanvasStorePanel(GralPanelContent panelg)
  {
    super(panelg);
//    gralPanel().canvas = new GralCanvasStorage();
  }
  

  
  
  /**Implementation class for Canvas for Swt
   * This class is a org.eclipse.swt.widgets.Composite. 
   */
 
  @Override public Control getWidgetImplementation(){ return (Control)this.wdgimpl; } 

  @Override public boolean setFocusGThread()
  {
    if(!super.setFocusGThread()){
      return ((Control)this.wdgimpl).setFocus();
    } else return true;
  }

  
  void stop(){} //debug


}

