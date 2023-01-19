package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralLed.GraphicImplAccess;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

/**This class represents a LED, which is able to show a state with its color. 
 * The LED may have a inner light and a border light. In that case to state,
 * which may have any association, can presented.
 */
public class SwtLed extends GralLed.GraphicImplAccess
{

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

  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper wdgh;

  
  Color borderColor, innerColor;

  final SwtMng mng;
  
  boolean round;
  
  
  SwtLed(String name, SwtMng mng){
    this(new GralLed(name), mng);
  }
  
  SwtLed(GralLed widgg, SwtMng mng){
    widgg.super(widgg, mng.gralMng);
    //super(name, mng);
    this.mng = mng;
    switch('r'){ 
    case 'r': round = true; break;
    case 'q': round = false; break;
    default: throw new IllegalArgumentException("param size must be r or q");
    }
    Composite panel = mng.getCurrentPanel();
    widgSwt = new SwtLedImpl(panel);
    super.wdgimpl = this.wdgh = new SwtWidgetHelper(widgSwt, mng);
    widgSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
    widgSwt.addFocusListener(mng.focusListener);
    widgSwt.setForeground(mng.propertiesGuiSwt.colorSwt(0xff00));
    //widget.setSize(propertiesGui.xPixelUnit() * xSize -2, propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgg.pos(), widgSwt);
    widgSwt.setData(this);
    widgSwt.addMouseListener(mng.mouseClickForInfo);

  }

  

  
  /**Called in the paint routine, corrects the colors for SWT depending on {@link GralWidget.DynamicData#backColor}
   * and {@link GralWidget.DynamicData#lineColor}.
   * <ul>
   * <li>lineColor is the border color
   * <li>backColor is the inner color.
   * </ul>
   */
  private void setColors(){
    int changedAckn = 0;
    GralWidget.DynamicData dyda = dyda(); //GralLed.GraphicImplAccess.this.dyda();
    int chg = dyda.getChanged();
    if(dyda.backColor !=null 
      && ( (chg & GralWidget.ImplAccess.chgColorBack)!=0
         ||innerColor == null   //uninitialized: start with dyda.backColor
      )  ){ 
      innerColor = mng.getColorImpl(dyda.backColor);
      changedAckn |= GralWidget.ImplAccess.chgColorBack; 
    }
    if(dyda.lineColor !=null){  //use backColor if lineColor is not set! 
      if( (chg & GralWidget.ImplAccess.chgColorLine)!=0
        || borderColor == null
        ){
        borderColor = mng.getColorImpl(dyda.lineColor); 
        changedAckn |= GralWidget.ImplAccess.chgColorLine; 
      }
    } else {
      borderColor = innerColor;
    }
    if(changedAckn !=0) {
      dyda.acknChanged(changedAckn);
    }
  }
  
  
  
  
  
private class SwtLedImpl extends Canvas
  {
  		/**Creates a LED.
  	 * @param gralMng The Gui-panel-manager contains information about the graphic frame and properties.
  	 * @param kind Use 'r' or 'q' for a round or a square LED.
  	 */
  	public SwtLedImpl(Composite panel)
  	{
  		
      super(panel, 0);
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
        gc.setBackground(borderColor); 
        gc.fillOval(2,2,e.width-3,e.height-3);
  			gc.setBackground(innerColor); 
  			int dx = (e.width+1) / 2;  //size of inner point, calculate 1 if width = 1,2
  			int dy = (e.height+1) / 2;  //size of inner point, calculate 1 if height = 1,2
        int x1 = (e.width - dx) /2 +1;
        int y1 = (e.height - dy) /2 +1;
        gc.fillOval(x1,y1, dx, dy);
  			//gc.setForeground(borderColor); 
  			//gc.setLineWidth(3);
  			//gc.drawOval(2,2,e.width-3,e.height-3);
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
  		borderColor = innerColor = mng.getColorImpl(GralColor.getColor(sColor));
  		redraw();
  	}
  	
  	
  	void XXXsetColor(int nBorderColor, int nInnerColor)
  	{
  		borderColor = mng.propertiesGuiSwt.colorSwt(nBorderColor);
  		innerColor = mng.propertiesGuiSwt.colorSwt(nInnerColor);
  		redraw();
  	}
  	
  	
  }

  @Override public GralRectangle getPixelPositionSize(){ return wdgh.getPixelPositionSize(); }


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

  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); wdgh.setVisibleGThread(bVisible); }

  
  @Override public void redrawGthread(){
    widgSwt.redraw();
  }


  @Override
  public Object getWidgetImplementation()
  {
    return widgSwt;
  }



  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  //@Override
  public GralColor XXXsetForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }
}