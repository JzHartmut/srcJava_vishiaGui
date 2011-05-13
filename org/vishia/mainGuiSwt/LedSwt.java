package org.vishia.mainGuiSwt;

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

/**This class represents a LED, which is able to show a state with its color. 
 * The LED may have a inner light and a border light. In that case to state,
 * which may have any association, can presented.
 */
public class LedSwt extends Canvas
{
	final GuiPanelMngSwt mng;

	boolean round;
	
	Color borderColor, innerColor;
	
	/**Creates a LED.
	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
	 * @param kind Use 'r' or 'q' for a round or a square LED.
	 */
	public LedSwt(GuiPanelMngSwt mng, char kind)
	{
		
		super((Composite)mng.currPanel.panelComposite, 0);
		switch(kind){ 
		case 'r': round = true; break;
		case 'q': round = false; break;
		default: throw new IllegalArgumentException("param size must be r or q");
		}
		borderColor = mng.propertiesGui.color("yellow");
		innerColor = mng.propertiesGui.color("green");
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
  
	
	void setColor(int color)
	{
		borderColor = innerColor = mng.propertiesGui.colorSwt(color);
		redraw();
	}
	
	void setColor(String sColor)
	{
		borderColor = innerColor = mng.propertiesGui.color(sColor);
		redraw();
	}
	
	
	void setColor(int nBorderColor, int nInnerColor)
	{
		borderColor = mng.propertiesGui.colorSwt(nBorderColor);
		innerColor = mng.propertiesGui.colorSwt(nInnerColor);
		redraw();
	}
	
	
}
