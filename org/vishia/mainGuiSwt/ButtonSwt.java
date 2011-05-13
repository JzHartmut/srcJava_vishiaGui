package org.vishia.mainGuiSwt;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

public class ButtonSwt extends Canvas
{
  final WidgetDescriptor widgd;

	String sButtonText;
	
	final GuiPanelMngSwt mng;
	
	final Color black;
	final Color white;
	
	final Font fontText;
  
	
	boolean isActivated;
	
	
	
	/**Creates a Button.
	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
	 * @param size The size of text in button, use 'A' or 'B' for small - bold
	 */
	public ButtonSwt(GuiPanelMngSwt mng, WidgetDescriptor widgd, char size)
	{
		super((Composite)mng.currPanel.panelComposite, 0);
		this.widgd = widgd;
		//super((Composite)mng.currPanel.panelComposite, 0);  //Canvas
		switch(size){ 
		case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
		case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
		default: throw new IllegalArgumentException("param size must be A or B");
		}
		//Control xx = mng.currPanel.panelComposite;
		this.mng = mng;
		black = mng.propertiesGuiSwt.colorSwt(0);
		white = mng.propertiesGuiSwt.colorSwt(0xffffff);
		
	  addPaintListener(paintListener);	
	}

	public void setText(String sButtonText){ this.sButtonText = sButtonText; }
	
	/**Show the button in an activated state. This method is called especially 
	 * in its mouse press and release events. 
	 * In the activated state the button looks like pressed.*/
	public void setActivated(boolean value){ 
		isActivated = value;
		redraw();
	}
	
	
  PaintListener paintListener = new PaintListener(){
		@Override
		public void paintControl(PaintEvent e) {
			// TODO Auto-generated method stub
			GC gc = e.gc;
			//gc.d
			Rectangle dim = ButtonSwt.this.getBounds();
			drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
			gc.setForeground(black);  //black
			gc.setFont(fontText);
			//FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
			//fontData.
			if(sButtonText !=null){
				FontMetrics fontMetrics = gc.getFontMetrics();
				int charWidth = fontMetrics.getAverageCharWidth();
				int halfWidthButtonText = charWidth * sButtonText.length() /2;
				int xText = dim.width / 2 - halfWidthButtonText;
				if(xText < 2){ xText = 2; }
				int halfHeightButtonText = fontMetrics.getHeight() /2;
				int yText = dim.height / 2 - halfHeightButtonText;
				gc.drawString(sButtonText, xText, yText);
			}
			if(isActivated){
				gc.setLineWidth(3);
				gc.drawRectangle(1,1,dim.width-2, dim.height-2);
				gc.setLineStyle(SWT.LINE_DOT);
				gc.drawRectangle(3,3,dim.width-6, dim.height-6);
			} else {
				gc.setLineWidth(1);
				gc.setForeground(black);
				gc.drawRectangle(1,1,dim.width-5, dim.height-5);
				gc.setForeground(white); 
				gc.setLineWidth(3);
				gc.drawLine(0, dim.height-2,dim.width, dim.height-2);
				gc.drawLine(dim.width-1, 0, dim.width-1, dim.height);
			}
		}
  };
  
	
	
	
}
