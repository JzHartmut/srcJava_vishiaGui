package org.vishia.gral.awt;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

public class AwtButton extends GralButton.GraphicImplAccess
{
  /**Version, history and license.
   * <ul>
   * <li>2013-12-22 Hartmut chg: Now {@link GralButton} uses the new concept of instantiation: It is not
   *   the super class of the implementation class. But it provides {@link GralButton.GraphicImplAccess}
   *   as the super class. 
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
  //@SuppressWarnings("hiding")
  public static final int version = 20130524;

  AwtWidgetHelper helper;
  
  AwtButtonImpl widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  final AwtGralMouseListener.MouseListenerGralAction mouseListener;
  

  
  //public AwtButton(String sName, AwtWidgetMng mng, Container parent, int styleSwt, char size)
  AwtButton(GralButton widgg, AwtWidgetMng mng)
  {
    widgg.super(widgg, mng.mng);
    mouseListener = new AwtGralMouseListener.MouseListenerGralAction(mouseWidgetAction);
    float ySize = widgg.pos().height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiAwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiAwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
      }
      //Control xx = mng.pos.panel.panelComposite;
      black = mng.propertiesGuiAwt.colorAwt(GralColor.getColor("bk"));
      white = mng.propertiesGuiAwt.colorAwt(GralColor.getColor("wh"));
      Container panelSwt = mng.getCurrentPanel();
      int styleSwt = 0;
      widgetSwt = new AwtButtonImpl(panelSwt, styleSwt);
      widgetSwt.setData(widgg);
      widgetSwt.setBackground(mng.propertiesGuiAwt.colorBackground);
      widgetSwt.addMouseListener(mouseListener);
      setBoundsGraphic(mng);
      helper = new AwtWidgetHelper(widgetSwt, mng);
  }

  
  
  @Override public GralRectangle getPixelPositionSize(){ return helper.getPixelPositionSize(); }

  
  void setBoundsGraphic(AwtWidgetMng mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(widgg.pos(), widgetSwt);
    
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

  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  protected void paintRoutine(Graphics gc, Canvas canvas){
    Rectangle dim = canvas.getBounds();
    AwtButton.this.paint1();
    
    Color colorBack = helper.mng.getColorImpl(colorgback);
    canvas.setBackground(colorBack);
    //drawBackground(gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
    Color color = canvas.getForeground(); //of the widget.
    canvas.setForeground(color);  //black
    gc.setFont(fontText);
    //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
    //fontData.
    if(isPressed()){
      //gc.setLineWidth(3);
      gc.drawRect(1,1,dim.width-2, dim.height-2);
      //gc.setLineStyle(SWT.LINE_DOT);
      gc.drawRect(3,3,dim.width-6, dim.height-6);
    } else {
      //gc.setLineWidth(1);
      //setForeground(colorBack);
      gc.setColor(colorBack);
      gc.fillRect(2,2,dim.width-6, dim.height-6);
      canvas.setForeground(black);
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
      canvas.setForeground(black);
      gc.drawString(sButtonText, xText, yText);
    }
     System.out.println("Button") ;
  }
  
  
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
        paintRoutine(gc, this);
     }
   }



  @Override
  public void repaintGthread() {
    // TODO Auto-generated method stub
    
  };
    

    
  /**Sets the implementation widget vible or not.
   * @see org.vishia.gral.base.GralWidgImpl_ifc#setVisibleGThread(boolean)
   */
  @Override public void setVisibleGThread(boolean bVisible){ super.setVisibleState(bVisible); helper.setVisibleGThread(bVisible); }
  
  

  
}
