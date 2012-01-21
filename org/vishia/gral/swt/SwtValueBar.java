package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralValueBar;

public class SwtValueBar extends GralValueBar implements GralSetValue_ifc, GralWidget_ifc
{

	final SwtWidgetMng mng;

	protected BarWidget widgetSwt;
	
	final Color black;
	//final Color colorValueOk, colorValueMinLimit, colorValueMaxLimit;
	//final Color white;
	
	private Color[] colorBorder;
	
	/**Creates a value bar.
	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
	 * @param size The size of text in button, use 'A' or 'B' for small - bold
	 */
	public SwtValueBar(String name, SwtWidgetMng mng)
	{
    super(name, mng);
    this.mng = mng;
		this.widgetSwt = this.new BarWidget();
		this.widgetSwt.setData(this);
		this.widgetSwt.setBackground(mng.propertiesGuiSwt.colorBackgroundSwt());
  	//Control xx = mng.currPanel.panelComposite;
		black = mng.propertiesGuiSwt.colorSwt(0);
		//white = mng.propertiesGui.color(0xffffff);
		//colorValueOk = mng.propertiesGui.color(0xff4000);
		//colorValueMinLimit = mng.propertiesGui.color(0xff4000);
		//colorValueMaxLimit = mng.propertiesGui.color(0xff4000);
		colorBorder = new Color[1];  //at least 1 color, if not parametrized
		colorBorder[0] = mng.propertiesGuiSwt.color("red");
	}

  @Override public void repaint()
  {
    if(itsMng.currThreadIsGraphic()){
      widgetSwt.redraw();
    } else {
		  this.widgetSwt.getDisplay().asyncExec(widgetSwt.redraw);
    }
  }
	
  
  @Override protected void repaintGthread(){
    widgetSwt.redraw();
  }

  
  @Override public void setBorderAndColors(String[] sParam)
  {
  	super.setBorderAndColors(sParam);
  	colorBorder = new Color[sColorBorder.length];
  	int ix = -1;
  	for(String sColor: sColorBorder){
  		colorBorder[++ix] = mng.propertiesGuiSwt.color(sColor);
  	}
  }
	
  @Override public Object getWidgetImplementation() { return widgetSwt; }
  @Override public boolean setFocus(){ return widgetSwt.setFocus(); }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    /**This method is used to set the position of the bar.
     * @see org.vishia.gral.base.GralWidgetGthreadSet_ifc#insertGthread(int, java.lang.Object, java.lang.Object)
     */
    @Override public void insertGthread(int pos, Object visibleInfo, Object data){ 
      if(visibleInfo == null){
        setValue(pos);
        widgetSwt.redraw();
      }
      if(data !=null) { setContentInfo(data); }
    }

    @Override public void redrawGthread(){ widgetSwt.redraw(); }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  
public class BarWidget extends Canvas
	{
		BarWidget()
		{
			super((Composite)mng.pos.panel.getPanelImpl(), 0);  //Canvas
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
				if((SwtValueBar.this.valueMax != valuePixelMax || valPixelBorder == null)
					&& floatBorder !=null && floatBorder.length >1) {  //at least one medium border
					valPixelBorder = new int[floatBorder.length-1];
					for(int ix = 0; ix < valPixelBorder.length; ++ix){
						valPixelBorder[ix] = dim.height -1 - (int)(valueMax * ((floatBorder[ix] - minRange) / (maxRange - minRange)));
					}
				}
				SwtValueBar.this.valueMax = valuePixelMax;
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
        int start, size;
        if(value1 < value2){
          start = 1 + value1;
          size = value2 - value1;  //difference start bar, end bar
        } else {
          start = value2;
          size = value1 - value2;
        }
        if(size > 150 && size < 230)
          stop();
        if(horizontal){
          gc.fillRectangle(start,1 ,size, dim.height-2);
        } else {
  				gc.fillRectangle(1,start ,dim.width-2, size);
        }
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

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  

  
  @Override public void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }


}
