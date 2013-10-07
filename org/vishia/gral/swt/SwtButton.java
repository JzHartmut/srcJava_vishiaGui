package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.ifc.GralColor;

public class SwtButton extends GralButton
{
  /**The version of this interface:
   * <ul>
   * <li>2012-03-09 Hartmut new setFocus on press button
   * <li>2012-03-09 Hartmut new 3-state-Button.
   * <li>2012-03-09 Hartmut improved: appearance of the buttons.
   * <li>All other changes in 2010, 2011
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  @SuppressWarnings("hiding")
  public static final int version = 20130524;

  Canvas widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  final SwtGralMouseListener.MouseListenerUserAction mouseListener = new SwtGralMouseListener.MouseListenerUserAction(mouseWidgetAction);
  

  
  public SwtButton(String sName, SwtMng mng, Composite parent, int styleSwt, char size)
  {
    super(sName, mng);
    //Control xx = mng.pos.panel.panelComposite;
    black = mng.propertiesGuiSwt.colorSwt(0x202020);
    white = mng.propertiesGuiSwt.colorSwt(0xefefff);
    widgetSwt = new SwtButtonImpl(parent, styleSwt);
    widgetSwt.setData(this);
    widgetSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
    widgetSwt.addMouseListener(mouseListener);
    widgetSwt.addFocusListener(mng.focusListener);  //common focus listener 
    setBoundsGraphic(mng);
    float ySize = pos().height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
      
  }

  
  void setBoundsGraphic(SwtMng mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgetSwt);
    
  }
  

  
  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getWidgetImplementation()
  { return widgetSwt; }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    colorBackOff = color;
    repaint(100, 100);
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
  
  @Override public boolean setFocusGThread()
  { SwtWidgetHelper.setFocusOfTabSwt(widgetSwt);
    widgetSwt.forceFocus();
    return widgetSwt.setFocus();
  }

  
  @Override public void repaintGthread(){
    int catastrophicalCount = 0;
    int chg, chgAckn=0;
    int state1 = -1;
    do{
      chg = this.dyda.whatIsChanged.get() & ~chgAckn;  //don't handle a bit twice if re-read.
      if(++catastrophicalCount > 10000) 
        throw new RuntimeException("atomic failed");
      if((chg & ImplAccess.chgVisibleInfo) !=0 && dyda.visibleInfo !=null){ 
        chgAckn |= ImplAccess.chgVisibleInfo;
        if(dyda.visibleInfo instanceof Integer){
          state1 = ((Integer)dyda.visibleInfo).intValue();
        }
      }
      if((chg & ImplAccess.chgIntg) !=0 ){ 
        chgAckn |= ImplAccess.chgIntg;
        state1 = 0; //TODO dyda.intValue
      }
    } while(!dyda.whatIsChanged.compareAndSet(chg, 0));  //repeat if chg is changed in that time.

    switch(state1){
      case 0: switchState = State.Off;
      case 1: switchState = State.On;
      case 2: switchState = State.Disabled;
      default: ; //don't change state.
    }

    widgetSwt.redraw();
  }

  
  private class SwtButtonImpl extends Canvas
  {
    
    SwtButtonImpl(Composite parent, int style){
      super(parent, style);
      setForeground(black);
      
      addPaintListener(paintListener);  
      
    }
    
    PaintListener paintListener = new PaintListener(){
      @Override public void paintControl(PaintEvent e) {
        // TODO Auto-generated method stub
        GC gc = e.gc;
        //gc.d
        Rectangle dim = getBounds();
        if(colorBackOff == null){ 
          //it isn't initalize
          colorBackOff = GralColor.getColor("wh");  //white background
        }
        final String sButtonText;
        final GralColor colorgback, colorgline;
        if(switchState == State.On){ 
          sButtonText = sButtonTextOn != null ? sButtonTextOn : sButtonTextOff;
          colorgback = colorBackOn !=null ? colorBackOn: colorBackOff;
          colorgline = colorLineOn !=null ? colorLineOn: colorLineOff;
        } else if(switchState == State.Disabled){ 
          sButtonText = sButtonTextDisabled;
          colorgback = colorBackDisabled;
          colorgline = colorLineDisabled;
        } else { 
          sButtonText = sButtonTextOff;
          colorgback = colorBackOff;
          colorgline = colorLineOff;
        }
        
        Color colorBack = (Color)gralMng().getColorImpl(colorgback);
        Color colorLine = (Color)gralMng().getColorImpl(colorgline);
        gc.setBackground(colorBack);
        drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
        Color color = getForeground(); //of the widget.
        gc.setForeground(colorLine);  //black
        gc.setFont(fontText);
        //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
        //fontData.
        gc.setForeground(colorBack);
        gc.fillRectangle(1,1,dim.width-1, dim.height-1);
        int ypText;
        if(sButtonText !=null){
          FontMetrics fontMetrics = gc.getFontMetrics();
          int charWidth = fontMetrics.getAverageCharWidth();
          int halfWidthButtonText = charWidth * sButtonText.length() /2;
          int xText = dim.width / 2 - halfWidthButtonText;
          if(xText < 2){ xText = 2; }
          ypText = fontMetrics.getHeight();
          int halfHeightButtonText = ypText /2;
          int yText = dim.height / 2 - halfHeightButtonText;
          gc.setForeground(colorLine);
          gc.drawString(sButtonText, xText, yText);
        } else {
          ypText = 0;
        }
        if(isPressed){
          ((Canvas)e.widget).forceFocus();
          /*
          gc.setLineWidth(3);
          gc.drawRectangle(1,1,dim.width-2, dim.height-2);
          gc.setLineStyle(SWT.LINE_DOT);
          gc.drawRectangle(3,3,dim.width-6, dim.height-6);
          */
          if(ypText < dim.height -4){
            //normal button
            gc.setForeground(black);
            gc.setLineWidth(3);
            gc.drawRectangle(0,0,dim.width-1, dim.height-1);
            gc.setForeground(white); 
            gc.setLineWidth(1);
            gc.drawLine(2, dim.height-2,dim.width-1, dim.height-2);
            gc.drawLine(1, dim.height-1,dim.width-1, dim.height-1);
            gc.drawLine(0, dim.height-0,dim.width-1, dim.height-0);
            gc.drawLine(dim.width-2, 0, dim.width-2, dim.height-1);
            gc.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
            gc.drawLine(dim.width-0, 0, dim.width-0, dim.height-1);
          } else {
            //small button
            gc.setLineWidth(1);
            gc.setForeground(black);
            gc.setLineWidth(1);
            gc.drawRectangle(0,0,dim.width-1, dim.height-1);
            gc.setForeground(white); 
            gc.setLineWidth(1);
            gc.drawLine(0, dim.height-1,dim.width-1, dim.height-1);
            gc.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
          }
        } else {
          if(ypText < dim.height -4){
            //normal button
            gc.setForeground(white);
            gc.setLineWidth(3);
            gc.drawRectangle(0,0,dim.width-1, dim.height-1);
            gc.setForeground(black); 
            gc.setLineWidth(1);
            gc.drawLine(2, dim.height-2,dim.width-1, dim.height-2);
            gc.drawLine(1, dim.height-1,dim.width-1, dim.height-1);
            gc.drawLine(0, dim.height-0,dim.width-1, dim.height-0);
            gc.drawLine(dim.width-2, 0, dim.width-2, dim.height-1);
            gc.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
            gc.drawLine(dim.width-0, 0, dim.width-0, dim.height-1);
          } else {
            //small button
            gc.setLineWidth(1);
            gc.setForeground(white);
            gc.setLineWidth(1);
            gc.drawRectangle(0,0,dim.width-1, dim.height-1);
            gc.setForeground(black); 
            gc.setLineWidth(1);
            gc.drawLine(0, dim.height-1,dim.width-1, dim.height-1);
            gc.drawLine(dim.width-1, 0, dim.width-1, dim.height-1);
          }
        }
      }
    };
    

    
  }
  
  
  
}
