package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vishia.mainGui.GuiSetValueIfc;
import org.vishia.mainGui.ValueBar;

public class ValueBarSwt extends ValueBar implements GuiSetValueIfc
{

	final GuiPanelMngSwt mng;

	final public BarWidget widget;
	
	final Color black;
	//final Color colorValueOk, colorValueMinLimit, colorValueMaxLimit;
	//final Color white;
	
	private Color[] colorBorder;
	
	/**Creates a value bar.
	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
	 * @param size The size of text in button, use 'A' or 'B' for small - bold
	 */
	public ValueBarSwt(GuiPanelMngSwt mng)
	{
    super(mng);
    this.mng = mng;
		this.widget = this.new BarWidget();		
		this.widget.setBackground(mng.propertiesGui.colorBackground);
  	//Control xx = mng.currPanel.panelComposite;
		black = mng.propertiesGui.colorSwt(0);
		//white = mng.propertiesGui.color(0xffffff);
		//colorValueOk = mng.propertiesGui.color(0xff4000);
		//colorValueMinLimit = mng.propertiesGui.color(0xff4000);
		//colorValueMaxLimit = mng.propertiesGui.color(0xff4000);
		colorBorder = new Color[1];  //at least 1 color, if not parametrized
		colorBorder[0] = mng.propertiesGui.color("red");
	}

  @Override public void redraw()
  {
		this.widget.getDisplay().asyncExec(widget.redraw);
  }
	
  
  @Override public void setBorderAndColors(String[] sParam)
  {
  	super.setBorderAndColors(sParam);
  	colorBorder = new Color[sColorBorder.length];
  	int ix = -1;
  	for(String sColor: sColorBorder){
  		colorBorder[++ix] = mng.propertiesGui.color(sColor);
  	}
  }
	
	
	public class BarWidget extends Canvas
	{
		BarWidget()
		{
			super((Composite)mng.currPanel.panelComposite, 0);  //Canvas
			addPaintListener(paintListener);	
			
		}
		
		
	  final PaintListener paintListener = new PaintListener(){
			@Override
			public void paintControl(PaintEvent e) {
				// TODO Auto-generated method stub
				GC gc = e.gc;
				//gc.d
				Rectangle dim = BarWidget.this.getBounds();
				int valuePixelMax = horizontal ? dim.width -2: dim.height -2;
				if(valuePixelMax != 106)
					stop();  
				if((ValueBarSwt.this.valueMax != valuePixelMax || valPixelBorder == null)
					&& floatBorder !=null && floatBorder.length >1) {  //at least one medium border
					valPixelBorder = new int[floatBorder.length-1];
					for(int ix = 0; ix < valPixelBorder.length; ++ix){
						valPixelBorder[ix] = dim.height -1 - (int)(valueMax * ((floatBorder[ix] - minRange) / (maxRange - minRange)));
					}
				}
				ValueBarSwt.this.valueMax = valuePixelMax;
				drawBackground(e.gc, dim.x, dim.y, dim.width, dim.height);
				gc.setForeground(black);  //black
				//FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
				//fontData.
				gc.setLineWidth(1);
				gc.setForeground(black);  //black
				gc.drawRectangle(0,0,dim.width-1, dim.height-1);
				//The bar, colored:
				gc.setBackground(colorBorder[ixColor]);  //black
				//gc.fillRectangle(1,dim.height -1 - value1 ,dim.width-2, value2 - value1);
        int size = value1 - value2;
        int top = dim.height - value1;
        if(size <0){
        	size = -size;
        	top -= size;
        }
				gc.fillRectangle(1,top ,dim.width-2, size);
				//division lines for borders.
				if(valPixelBorder !=null){
					if(horizontal){
						for(int pixel : valPixelBorder){
							gc.drawLine(pixel, 0, pixel, dim.height-1);
						}
					} else {
						for(int pixel : valPixelBorder){
							gc.drawLine(0, pixel, dim.width-1, pixel);
						}
					}
				}
				//gc.drawLine(3,dim.height-2,dim.width, dim.height-2);
			}
	  };

	  final Runnable redraw = new Runnable(){
			@Override public void run()
			{ redraw();
			}
		};
		

	}  
	  
	void stop(){}
}
