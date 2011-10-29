package org.vishia.mainGuiSwt;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralCanvasStorage;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;

/**Class to store some graphical figures to draw it in its {@link #drawBackground(GC, int, int, int, int)}-routine.
 * The figures are stored with its coordinates and it are drawn if necessary. 
 * <br><br>
 * This class is a org.eclipse.swt.widgets.Composite. 
 * It can contain some GUI-Elements like Button, Text, Label, Table etc from org.eclipse.swt.widgets.
 * The graphical figures are shown as background than.
 * 
 * @author Hartmut Schorrig
 *
 */
public class CanvasStorePanelSwt extends PanelSwt  //CanvasStorePanel //
{
	
  protected SwtCanvas swtCanvas;
	
	/**The storage for the Canvas content. */
	GralCanvasStorage store = new GralCanvasStorage(){

		@Override public void redraw()
		{
			// TODO Auto-generated method stub
			
		}
		
	};
	
	
	Color colorSwt(GralColor colorGui)
	{
	  if(colorGui.colorGuimpl == null){
	    colorGui.colorGuimpl = new Color(swtCanvas.getDisplay(), colorGui.red, colorGui.green, colorGui.blue);
	  } else if(!(colorGui.colorGuimpl instanceof Color)){
	    throw new IllegalArgumentException("unauthorized change");
	  };
	  return (Color)colorGui.colorGuimpl;  
	}
	
	//@Override 
	public Composite xxxgetGuiContainer(){
		return swtCanvas;
	}

	
	
	
	
	//class MyCanvas extends Canvas{
	
  /**The listener for paint events. It is called whenever the window is shown newly. */
  protected PaintListener paintListener = new PaintListener()
  {

		@Override
		public void paintControl(PaintEvent e) {
			// TODO Auto-generated method stub
			GC gc = e.gc;
			swtCanvas.drawBackground(e.gc, e.x, e.y, e.width, e.height);
			stop();
		}
  	
  };
	
	private static final long serialVersionUID = 6448419343757106982L;
	
  protected Color currColor;
	
  /**Constructs the instance with a SWT-Canvas Panel.
   * @param parent
   * @param style
   * @param backGround
   */
  public CanvasStorePanelSwt(String namePanel, Composite parent, int style, Color backGround, GralWidgetMng gralMng)
  { super(namePanel, gralMng);
    swtCanvas = new SwtCanvas(this,parent, style);
    super.panelComposite = swtCanvas;
    swtCanvas.setData(this);
    swtCanvas.setLayout(null);
    currColor = swtCanvas.getForeground();
    swtCanvas.addPaintListener(paintListener);
    swtCanvas.setBackground(backGround);
    swtCanvas.addControlListener(resizeItemListener);
  }
  
  /**Constructor called in derived classes. The derived class have to be instantiate the Canvas
   * maybe with other draw routines. 
   */
  protected CanvasStorePanelSwt(String namePanel, GralWidgetMng gralMng)
  {
    super(namePanel, gralMng);
  }
  

  public void xxxsetForeground(Color color){
    currColor = color;		
	}
	
	
	
	protected static class SwtCanvas extends Canvas
	{
	  private final CanvasStorePanelSwt storeMng;
	  
	  SwtCanvas(CanvasStorePanelSwt storeMng, Composite parent, int style)
	  {
	    super(parent, style);
	    this.storeMng = storeMng;
	  }
	  
    @Override
    public void drawBackground(GC g, int x, int y, int dx, int dy) {
    	//NOTE: forces stack overflow because calling of this routine recursively: super.paint(g);
    	
    	for(GralCanvasStorage.PaintOrder order: storeMng.store.paintOrders){
    		switch(order.paintWhat){
      		case GralCanvasStorage.paintLine: {
      			g.setForeground(storeMng.colorSwt(order.color));
      	  	g.drawLine(order.x1, order.y1, order.x2, order.y2);
      	  } break;
      		case GralCanvasStorage.paintImage: {
      		  GralCanvasStorage.PaintOrderImage orderImage = (GralCanvasStorage.PaintOrderImage) order;
      		  Image image = (Image)orderImage.image.getImage();
      		  //int dx1 = (int)(orderImage.zoom * order.x2);
      		  //int dy1 = (int)(orderImage.zoom * order.y2);
            g.drawImage(image, 0, 0, orderImage.dxImage, orderImage.dyImage, order.x1, order.y1, order.x2, order.y2);
      		} break;
      		default: throw new IllegalArgumentException("unknown order");
    		}
    	}
    }	
	}
	
  @Override public Control getWidgetImplementation(){ return swtCanvas; } 

  protected ControlListener resizeItemListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
      stop();
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      Widget wparent = e.widget; //it is the SwtCanvas because this method is assigned only there.
      //Control parent = wparent;
      for(GralWidget widgd: widgetsToResize){
        widgd.getMng().resizeWidget(widgd, 0, 0);
      }
      stop();
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  
  @Override public boolean setFocus()
  {
    return swtCanvas.setFocus();
  }

  
  void stop(){} //debug

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}

