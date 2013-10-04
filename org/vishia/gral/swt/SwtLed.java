package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
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

  
  Color borderColor, innerColor;

  final SwtMng mng;
  
  boolean round;
  
  

  SwtLed(String name, SwtMng mng){
    super(name, mng);
    this.mng = mng;
    switch('r'){ 
    case 'r': round = true; break;
    case 'q': round = false; break;
    default: throw new IllegalArgumentException("param size must be r or q");
    }
    widgSwt = new SwtLedImpl();
    widgSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
    widgSwt.addFocusListener(mng.focusListener);
    widgSwt.setForeground(mng.propertiesGuiSwt.colorSwt(0xff00));
    //widget.setSize(propertiesGui.xPixelUnit() * xSize -2, propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgSwt);
    widgSwt.setData(this);
    widgSwt.addMouseListener(mng.mouseClickForInfo);

  }

  

  
  private void setColors(){
    int changedAckn = 0;
    if(dyda.backColor !=null && (dyda.whatIsChanged.get() & ImplAccess.chgColorBack)!=0){ 
      innerColor = mng.getColorImpl(dyda.backColor);
      changedAckn |= ImplAccess.chgColorBack; 
    }
    if(dyda.lineColor !=null){ 
      if((dyda.whatIsChanged.get() & ImplAccess.chgColorLine)!=0){
        borderColor = mng.getColorImpl(dyda.lineColor); 
        changedAckn |= ImplAccess.chgColorLine; 
      }
    } else {
      borderColor = innerColor;
    }
    if(changedAckn !=0){
      dyda.setChanged(changedAckn);
    }
  }
  
  
  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

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
  
  
  
private class SwtLedImpl extends Canvas
  {
  		/**Creates a LED.
  	 * @param mng The Gui-panel-manager contains information about the graphic frame and properties.
  	 * @param kind Use 'r' or 'q' for a round or a square LED.
  	 */
  	public SwtLedImpl()
  	{
  		
  		super(((SwtPanel)pos().panel).getPanelImpl(), 0);
  		borderColor = mng.propertiesGuiSwt.color("yellow");
  		innerColor = mng.propertiesGuiSwt.color("green");
  		
  	  addPaintListener(paintListener);	
  	}
  
    PaintListener paintListener = new PaintListener(){
  		@Override
  		public void paintControl(PaintEvent e) {
  			// TODO Auto-generated method stub
  			GC gc = e.gc;
  			//gc.d
  			//drawBackground(e.gc, e.x, e.y, e.width, e.height);
  			setColors();
        gc.setBackground(innerColor); 
  			gc.fillOval(3,3, e.width-4, e.height-4);
  			gc.setForeground(borderColor); 
  			gc.setLineWidth(3);
  			gc.drawOval(2,2,e.width-3,e.height-3);
  		}
    };
    
  	
    void XXXsetBorderColor(int color)
    {
      borderColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
    void XXXsetInnerColor(int color)
    {
      innerColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
    void XXXsetColor(int color)
    {
      borderColor = innerColor = mng.propertiesGuiSwt.colorSwt(color);
      redraw();
    }
    
  	void XXXsetColor(String sColor)
  	{
  		borderColor = innerColor = mng.propertiesGuiSwt.color(sColor);
  		redraw();
  	}
  	
  	
  	void XXXsetColor(int nBorderColor, int nInnerColor)
  	{
  		borderColor = mng.propertiesGuiSwt.colorSwt(nBorderColor);
  		innerColor = mng.propertiesGuiSwt.colorSwt(nInnerColor);
  		redraw();
  	}
  	
  	
  }

  @Override
  public void removeWidgetImplementation()
  { if(widgSwt !=null){
      widgSwt.dispose();
      widgSwt = null;
    }
  }

  @Override
  public boolean setFocusGThread()
  { return widgSwt.setFocus();
  }

  
  @Override public void repaintGthread(){
    widgSwt.redraw();
  }


  @Override
  public Object getWidgetImplementation()
  {
    return widgSwt;
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