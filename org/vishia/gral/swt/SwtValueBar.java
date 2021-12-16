package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;

public class SwtValueBar extends GralValueBar.GraphicImplAccess // implements GralSetValue_ifc, GralWidget_ifc
{

  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper wdgh;
  
	//final SwtMng mng;

	protected BarWidget widgetSwt;
	
	final Color black;
	//final Color colorValueOk, colorValueMinLimit, colorValueMaxLimit;
	//final Color white;
	
	private Color[] colorBorder;
	
	/**Creates a value bar.
	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
	 * @param size The size of text in button, use 'A' or 'B' for small - bold
	 */
	public SwtValueBar(GralValueBar widgg, SwtMng mng)
	{
    widgg.super(mng.mng);
    //mng.mng.setNextPosition();
    super.wdgimpl = this.wdgh = new SwtWidgetHelper(widgetSwt, mng);
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
    //widget.setPanelMng(this);
  //  widget.setShowMethod(sShowMethod);
    //widget.widget.setData(widgetInfos);
    widgetSwt.addMouseListener(mng.mouseClickForInfo);
    setBounds();
    
    mng.mng.registerWidget(widgg);
	}

  
	
	public void setBounds(){
    pixBounds = wdgh.mng.calcWidgetPosAndSizeSwt(widgg.pos(), widgetSwt.getParent(), 10, 100);
    //mng.setPosAndSize_(widgetSwt);
    widgetSwt.setBounds(pixBounds.x, pixBounds.y, pixBounds.dx, pixBounds.dy);
    horizontal = pixBounds.dx > pixBounds.dy;
	  
	}
	
	
	
  @Override public void repaintGthread(){
    widgetSwt.redraw();
  }

  
  @Override
  public void setBorderAndColorsImpl(String[] sColorLevels)
  {
  	colorBorder = new Color[sColorLevels.length];
  	int ix = -1;
  	for(String sColor: sColorLevels){
  		colorBorder[++ix] = wdgh.mng.propertiesGuiSwt.color(sColor);
  	}
  }
	
  @Override public Object getWidgetImplementation() { return widgetSwt; }
  
  
  @Override public boolean setFocusGThread(){ return widgetSwt.setFocus(); }

  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); wdgh.setVisibleGThread(bVisible); }

  
  protected void paintRoutine(BarWidget wdgs, PaintEvent e){
    // TODO Auto-generated method stub
    GC gc = e.gc;
    //gc.d
    Rectangle dim = wdgs.getBounds();
    int valuePixelMax = horizontal() ? dim.width -2: dim.height -2;
    if(valuePixelMax != 106)
      stop();  
    /*
    if((SwtValueBar.this.valueMax != valuePixelMax || pixLevel == null)
      && fLevels !=null && fLevels.length >1) {  //at least one medium border
      pixLevel = new int[fLevels.length-1];
      for(int ix = 0; ix < pixLevel.length; ++ix){
        pixLevel[ix] = dim.height -1 - (int)(valueMax * ((fLevels[ix] - minRange) / (maxRange - minRange)));
      }
    }
    SwtValueBar.this.valueMax = valuePixelMax;
    */
    wdgs.drawBackground(e.gc, dim.x, dim.y, dim.width, dim.height);
    gc.setForeground(black);  //black
    //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
    //fontData.
    gc.setLineWidth(1);
    gc.setForeground(black);  //black
    gc.drawRectangle(0,0,dim.width-1, dim.height-1);
    //The bar, colored:
    gc.setBackground(colorBorder[ixColor()]);  //black
    //gc.fillRectangle(1,dim.height -1 - value1 ,dim.width-2, value2 - value1);
    int start, size;
    if(pix0line() < pixvalue()){
      start = 1 + pix0line();
      size = pixvalue() - pix0line();  //difference start bar, end bar
    } else { //negative value
      start = pixvalue();
      size = pix0line() - pixvalue();
    }
    if(size > 150 && size < 230)
      stop();
    if(horizontal()){
      gc.fillRectangle(start,1 ,size, dim.height-2);
    } else {
      gc.fillRectangle(1,start ,dim.width-2, size);
    }
    //division lines for borders.
    if(pixLevel !=null){
      if(horizontal()){
        for(int pixel : pixLevel){
          gc.drawLine(pixel, 0, pixel, dim.height-1);
        }
      } else {
        for(int pixel : pixLevel){
          gc.drawLine(0, pixel, dim.width-1, pixel);
        }
      }
    }
    //gc.drawLine(3,dim.height-2,dim.width, dim.height-2);
  }
    
  
  
  
  public class BarWidget extends Canvas
	{
		BarWidget()
		{
			super(wdgh.mng.getCurrentPanel(), 0);  //Canvas
			wdgh.widgetSwt = this;
			addPaintListener(paintListener);	
			
		}
		
		
	  final PaintListener paintListener = new PaintListener(){
			@Override
			public void paintControl(PaintEvent e) { paintRoutine(BarWidget.this,e); }
	  };

	  final Runnable redraw = new Runnable(){
			@Override public void run()
			{ redraw();
			}
		};
		

	}  
	  
	void stop(){}


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  

  
  @Override public void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }


  @Override
  public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }


}
