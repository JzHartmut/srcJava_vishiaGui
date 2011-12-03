package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;

/**This class represents a LED, which is able to show a state with its color. 
 * The LED may have a inner light and a border light. In that case to state,
 * which may have any association, can presented.
 */
public class SwtLed extends GralLed{

  /**Version and history
   * <ul>
   * <li>2011-12-03 chg now it is the implementation class for the new class {@link GralLed}.
   *   It is the concept of specialized {@link GralWidget}.
   *   The ctor creates the swt Control.
   *   The content before is the inner class {@link SwtLedImpl} up to now. 
   * <li>2010-05-00 created.
   * </ul>
   * 
   */
  private SwtLedImpl widgSwt;
  
  SwtLed(String name, SwtWidgetMng mng){
    super(name, mng);
    widgSwt = new SwtLedImpl(mng, 'r');
    widgSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
    widgSwt.addFocusListener(mng.focusListener);
    widgSwt.setForeground(mng.propertiesGuiSwt.colorSwt(0xff00));
    //widget.setSize(propertiesGui.xPixelUnit() * xSize -2, propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgSwt);
    widgSwt.setData(this);
    widgSwt.addMouseListener(mng.mouseClickForInfo);

  }

  
  public void setColor(int nBorderColor, int nInnerColor){
    widgSwt.setColor(nBorderColor, nInnerColor);
  }
  
  
  private class SwtLedImpl extends Canvas
  {
  	final SwtWidgetMng mng;
  
  	boolean round;
  	
  	Color borderColor, innerColor;
  	
  	/**Creates a LED.
  	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
  	 * @param kind Use 'r' or 'q' for a round or a square LED.
  	 */
  	public SwtLedImpl(SwtWidgetMng mng, char kind)
  	{
  		
  		super(((SwtPanel)mng.pos.panel).getPanelImpl(), 0);
  		switch(kind){ 
  		case 'r': round = true; break;
  		case 'q': round = false; break;
  		default: throw new IllegalArgumentException("param size must be r or q");
  		}
  		borderColor = mng.propertiesGuiSwt.color("yellow");
  		innerColor = mng.propertiesGuiSwt.color("green");
  		this.mng = mng;
  		
  	  addPaintListener(paintListener);	
  	}
  
    PaintListener paintListener = new PaintListener(){
  		@Override
  		public void paintControl(PaintEvent e) {
  			// TODO Auto-generated method stub
  			GC gc = e.gc;
  			//gc.d
  			//drawBackground(e.gc, e.x, e.y, e.width, e.height);
  			gc.setBackground(innerColor); 
  			gc.fillOval(3,3, e.width-4, e.height-4);
  			gc.setForeground(borderColor); 
  			gc.setLineWidth(3);
  			gc.drawOval(2,2,e.width-3,e.height-3);
  		}
    };
    
  	
    void setBorderColor(int color)
    {
      borderColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
    void setInnerColor(int color)
    {
      innerColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
    void setColor(int color)
    {
      borderColor = innerColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
  	void setColor(String sColor)
  	{
  		borderColor = innerColor = mng.propertiesGuiSwt.color(sColor);
  		redraw();
  	}
  	
  	
  	void setColor(int nBorderColor, int nInnerColor)
  	{
  		borderColor = mng.propertiesGuiSwt.colorSwt(nBorderColor);
  		innerColor = mng.propertiesGuiSwt.colorSwt(nInnerColor);
  		redraw();
  	}
  	
  	
  }

  @Override
  protected void removeWidgetImplementation()
  { if(widgSwt !=null){
      widgSwt.dispose();
      widgSwt = null;
    }
  }

  @Override
  public boolean setFocus()
  { return widgSwt.setFocus();
  }

  @Override
  public Object getWidgetImplementation()
  {
    return widgSwt;
  }

  @Override
  public void redraw()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return SwtWidgetHelper.setBackgroundColor(color, widgSwt);
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
}