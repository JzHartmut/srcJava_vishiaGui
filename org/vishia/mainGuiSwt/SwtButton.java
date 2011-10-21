package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
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
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindowMng_ifc;

public class SwtButton extends GralButton
{

  Canvas widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  
  

  
  public SwtButton(String sName, GuiPanelMngSwt mng, Composite parent, int styleSwt, char size)
  {
    super(sName, mng);
    switch(size){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
      }
      //Control xx = mng.pos.panel.panelComposite;
      black = mng.propertiesGuiSwt.colorSwt(0);
      white = mng.propertiesGuiSwt.colorSwt(0xffffff);
      widgetSwt = new SwtButtonImpl(parent, styleSwt);
      widgetSwt.setData(this);
      widgetSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
      MouseClickButton mouseClickButton = new MouseClickButton();
      widgetSwt.addMouseListener(mouseClickButton);
      setBoundsGraphic(mng);
  }

  
  void setBoundsGraphic(GuiPanelMngSwt mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgetSwt);
    
  }
  
  public void setText(String sButtonText){ this.sButtonText = sButtonText; }
  
  /**Show the button in an activated state. This method is called especially 
   * in its mouse press and release events. 
   * In the activated state the button looks like pressed.*/
  public void setActivated(boolean value){ 
    isActivated = value;
    widgetSwt.redraw();
  }
  
  

  @Override public void redraw(){  widgetSwt.redraw(); widgetSwt.update(); }
  
  
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
        drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
        Color color = getForeground(); //of the widget.
        gc.setForeground(color);  //black
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
  
  
  private class MouseClickButton extends MouseClickActionForUserActionSwt
  {
    /**Constructor.
     * @param guiMng The Gui-manager
     * @param userCmdGui The users method for the action. 
     * @param sCmdPress command string provided as first parameter on mouse button press.
     * @param sCmdRelease
     * @param sCmdDoubleClick
     */
    public MouseClickButton()
    { super(null, "Button-down", "Button-up", "Button-click");
    }

    
    @Override public void mouseDown(MouseEvent e) {
      super.mouseDown(e);
      setActivated(true);
    }

    @Override public void mouseUp(MouseEvent e) {
      super.mouseUp(e);
      setActivated(false);
    }

    
    
  }
  
  
}
