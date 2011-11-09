package org.vishia.gral.swt;


import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralCanvasStorage;
import org.vishia.gral.ifc.GralColor;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
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
	
  protected SwtCanvas swtCanvas;
	
	/**The storage for the Canvas content. */
	GralCanvasStorage store = new GralCanvasStorage();
	
	
	
	
	//class MyCanvas extends Canvas{
	
	private static final long serialVersionUID = 6448419343757106982L;
	
  protected Color currColor;
	
  /**Constructs the instance with a SWT-Canvas Panel.
   * @param parent
   * @param style
   * @param backGround
   */
  public SwtCanvasStorePanel(String namePanel, Composite parent, int style, Color backGround, GralWidgetMng gralMng)
  { super(namePanel, gralMng, null);
    swtCanvas = new SwtCanvas(this,parent, style);
    super.panelComposite = swtCanvas;
    swtCanvas.addControlListener(resizeItemListener);
    swtCanvas.setData(this);
    swtCanvas.setLayout(null);
    currColor = swtCanvas.getForeground();
    swtCanvas.addPaintListener(swtCanvas.paintListener);
    swtCanvas.setBackground(backGround);
  }
  
  /**Constructor called in derived classes. The derived class have to be instantiate the Canvas
   * maybe with other draw routines. 
   */
  protected SwtCanvasStorePanel(String namePanel, GralWidgetMng gralMng)
  {
    super(namePanel, gralMng, null);
  }
  

  public void xxxsetForeground(Color color){
    currColor = color;		
	}
	
	
	
	/**Implementation class for Canvas for Swt
	 * This class is a org.eclipse.swt.widgets.Composite. 
	 */
	protected static class SwtCanvas extends Canvas
	{
	  private final SwtCanvasStorePanel storeMng;
	  
	  SwtCanvas(SwtCanvasStorePanel storeMng, Composite parent, int style)
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
      			g.setForeground(((SwtWidgetMng)storeMng.gralMng).getColorImpl(order.color));
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
    
	}
	
  @Override public Control getWidgetImplementation(){ return swtCanvas; } 

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

