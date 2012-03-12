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
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;

public class SwtButton extends GralButton
{
  /**The version of this interface:
   * <ul>
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
  public static final int version = 20120309;

  Canvas widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  final SwtGralMouseListener.MouseListenerUserAction mouseListener = new SwtGralMouseListener.MouseListenerUserAction(mouseWidgetAction);
  

  
  public SwtButton(String sName, SwtWidgetMng mng, Composite parent, int styleSwt, char size)
  {
    super(sName, mng);
    switch(size){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
      }
      //Control xx = mng.pos.panel.panelComposite;
      black = mng.propertiesGuiSwt.colorSwt(0x202020);
      white = mng.propertiesGuiSwt.colorSwt(0xefefff);
      widgetSwt = new SwtButtonImpl(parent, styleSwt);
      widgetSwt.setData(this);
      widgetSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
      widgetSwt.addMouseListener(mouseListener);
      setBoundsGraphic(mng);
  }

  
  void setBoundsGraphic(SwtWidgetMng mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgetSwt);
    
  }
  

  
  
  @Override
  protected void removeWidgetImplementation()
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
  
  @Override public boolean setFocus()
  { SwtWidgetHelper.setFocusOfTabSwt(widgetSwt);
    widgetSwt.forceFocus();
    return widgetSwt.setFocus();
  }

  
  @Override protected void repaintGthread(){
    widgetSwt.redraw();
  }

  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override
    public void clearGthread()
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void insertGthread(int pos, Object visibleInfo, Object data)
    {
      // TODO Auto-generated method stub
      
    }

    @Override public void redrawGthread()
    { widgetSwt.redraw(); }

    @Override public void setBackGroundColorGthread(GralColor color)
    { widgetSwt.setBackground(((SwtWidgetMng)itsMng).getColorImpl(color)); }

    @Override public void setForeGroundColorGthread(GralColor color)
    { widgetSwt.setForeground(((SwtWidgetMng)itsMng).getColorImpl(color)); }

    @Override
    public void setTextGthread(String text, Object data)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
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
        if(colorOff == null){ 
          //it isn't initalize
          colorOff = GralColor.getColor("wh");  //white background
        }
        final String sButtonText;
        final GralColor colorgback;
        if(switchState == kOn){ 
          sButtonText = sButtonTextOn != null ? sButtonTextOn : sButtonTextOff;
          colorgback = colorOn !=null ? colorOn: colorOff;
        } else if(switchState == kDisabled){ 
          sButtonText = sButtonTextDisabled;
          colorgback = colorDisabled;
        } else { 
          sButtonText = sButtonTextOff;
          colorgback = colorOff;
        }
        
        Color colorBack = (Color)getMng().getColorImpl(colorgback);
        gc.setBackground(colorBack);
        drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
        Color color = getForeground(); //of the widget.
        gc.setForeground(color);  //black
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
          gc.setForeground(black);
          gc.drawString(sButtonText, xText, yText);
        } else {
          ypText = 0;
        }
        if(isPressed){
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
