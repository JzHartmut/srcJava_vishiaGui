package org.vishia.gral.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;

public class AwtButton extends GralButton
{

  AwtButtonImpl widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  final AwtGralMouseListener.MouseListenerUserAction mouseListener = new AwtGralMouseListener.MouseListenerUserAction(mouseWidgetAction);
  

  
  public AwtButton(String sName, AwtWidgetMng mng, Container parent, int styleSwt, char size)
  {
    super(sName, mng);
    switch(size){ 
      case 'A': fontText = mng.propertiesGuiAwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiAwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
      }
      //Control xx = mng.pos.panel.panelComposite;
      black = mng.propertiesGuiAwt.colorAwt(GralColor.getColor("bk"));
      white = mng.propertiesGuiAwt.colorAwt(GralColor.getColor("wh"));
      widgetSwt = new AwtButtonImpl(parent, styleSwt);
      widgetSwt.setData(this);
      widgetSwt.setBackground(mng.propertiesGuiAwt.colorBackground);
      widgetSwt.addMouseListener(mouseListener);
      setBoundsGraphic(mng);
  }

  
  void setBoundsGraphic(AwtWidgetMng mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgetSwt);
    
  }
  
  

  
  @Override public boolean setFocusGThread()
  { return AwtWidgetHelper.setFocusOfTabSwt(widgetSwt);
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
  
  
  
  private class AwtButtonImpl extends Canvas implements AwtWidget
  {
    
    private Object data;
    

    
    AwtButtonImpl(Container parent, int style){
      super();
      setForeground(black);
      parent.add(this);      
      //addPaintListener(paintListener);  
      
    }
    
    public Object getData(){ return data; }
    
    public void setData(Object dataP){ data = dataP; }
    
    //PaintListener paintListener = new PaintListener(){
      @Override public void paint(Graphics gc) {
        // TODO Auto-generated method stub
        //GC gc = e.gc;
        //gc.d
        Rectangle dim = getBounds();
        if(colorBackOff == null){ 
          //it isn't initalize
          colorBackOff = GralColor.getColor("wh");  //white background
        }
        GralColor colorgback = switchState == State.On && colorBackOn != null ? colorBackOn : colorBackOff;
        
        Color colorBack = (Color)getMng().getColorImpl(colorgback);
        setBackground(colorBack);
        //drawBackground(gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
        Color color = getForeground(); //of the widget.
        setForeground(color);  //black
        gc.setFont(fontText);
        //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
        //fontData.
        final String sButtonText = switchState == State.On && sButtonTextOn != null ? sButtonTextOn : sButtonTextOff;
        if(isPressed){
          //gc.setLineWidth(3);
          gc.drawRect(1,1,dim.width-2, dim.height-2);
          //gc.setLineStyle(SWT.LINE_DOT);
          gc.drawRect(3,3,dim.width-6, dim.height-6);
        } else {
          //gc.setLineWidth(1);
          //setForeground(colorBack);
          gc.setColor(colorBack);
          gc.fillRect(2,2,dim.width-6, dim.height-6);
          setForeground(black);
          gc.drawRect(1,1,dim.width-5, dim.height-5);
          //setForeground(white); 
          gc.setColor(black);
          //gc.setLineWidth(3);
          gc.drawLine(0, dim.height-2,dim.width, dim.height-2);
          gc.drawLine(dim.width-1, 0, dim.width-1, dim.height);
        }
        if(sButtonText !=null){
          FontMetrics fontMetrics = gc.getFontMetrics();
          int charWidth = 8; //fontMetrics.getAverageCharWidth();
          int halfWidthButtonText = charWidth * sButtonText.length() /2;
          int xText = dim.width / 2 - halfWidthButtonText;
          if(xText < 2){ xText = 2; }
          int halfHeightButtonText = fontMetrics.getHeight() /2;
          int yText = dim.height / 2 + halfHeightButtonText;
          setForeground(black);
          gc.drawString(sButtonText, xText, yText);
        }
         System.out.println("Button") ;
      }
    }



  @Override
  public void repaintGthread() {
    // TODO Auto-generated method stub
    
  };
    

    
  
  
  

  
}
