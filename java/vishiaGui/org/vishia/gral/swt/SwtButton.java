package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

public class SwtButton extends GralButton.GraphicImplAccess
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

  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWidgHelper;
  
  Canvas widgetSwt;
  
  final Color black;
  final Color white;
  
  final Font fontText;
  
  
  final SwtGralMouseListener.MouseListenerGralAction mouseListener;
  

  
  //public SwtButton(String sName, SwtMng mng, Composite parent, int styleSwt, char size)
  SwtButton(GralButton widgg, SwtMng mng)
  {
    widgg.super(widgg, mng.gralMng);
    widgg.setActionMouse(mouseWidgetAction, 0);
    mouseListener = new SwtGralMouseListener.MouseListenerGralAction();
    //Control xx = mng.pos.panel.panelComposite;
    black = mng.propertiesGuiSwt.colorSwt(0x202020);
    white = mng.propertiesGuiSwt.colorSwt(0xffffff);
    Composite panelSwt = mng.getWidgetsPanel(widgg);
    int styleSwt = 0;
    widgetSwt = new SwtButtonImpl(panelSwt, styleSwt);
    widgetSwt.setData(this);
    widgetSwt.setBackground(mng.propertiesGuiSwt.colorBackground);
    widgetSwt.addMouseListener(mouseListener);
    widgetSwt.addFocusListener(mng.focusListener);  //common focus listener 
    widgetSwt.addKeyListener(new KeyListener(mng.gralMng._implListener.gralKeyListener));
    widgetSwt.addTraverseListener(SwtMng.swtTraverseListener);
    setBoundsGraphic(widgg.pos(), mng);
    float ySize = widgg.pos().height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
    super.wdgimpl = swtWidgHelper = new SwtWidgetHelper(widgetSwt, mng);  
  }


  
  @Override public GralRectangle getPixelPositionSize(){ return swtWidgHelper.getPixelPositionSize(); }

  
  void setBoundsGraphic(GralPos pos, SwtMng mng)
  {
    //widgetSwt.setSize(mng.propertiesGui.xPixelUnit() * xSize -2, mng.propertiesGui.yPixelUnit() * ySize -2);
    mng.setBounds_(pos, widgetSwt);
    
  }
  

  
  
  @Override public void removeWidgetImplementation()
  { swtWidgHelper.removeWidgetImplementation();
    widgetSwt = null;
  }

  @Override
  public Object getWidgetImplementation()
  { return widgetSwt; }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  @Override public boolean setFocusGThread()
  { SwtWidgetHelper.setFocusOfTabSwt(widgetSwt);
    widgetSwt.forceFocus();
    return widgetSwt.setFocus();
  }


  /**Sets the implementation widget vible or not.
   * @see org.vishia.gral.base.GralWidgImplAccess_ifc#setVisibleGThread(boolean)
   */
  @Override public void setVisibleGThread(boolean bVisible) { 
    super.setVisibleState(bVisible); 
    swtWidgHelper.setVisibleGThread(bVisible);
  }
  
  @Override public void redrawGthread(){
    super.prepareWidget();
    //super.redrawGthread(); from GralWidget.ImplAccess
    GralWidget.DynamicData dyda = dyda();
    int chg = dyda.getChanged();
    if((chg & chgText) !=0 && dyda.displayedText !=null){ 
      //textFieldSwt.setText(dyda.displayedText);
    }
    if((chg & chgColorText)!=0){
      acknChanged(chgColorText);
      SwtProperties props = swtWidgHelper.mng.propertiesGuiSwt;
      if(dyda.textColor !=null){
        this.widgetSwt.setForeground(props.colorSwt(dyda.textColor));
      }
      if(dyda.backColor !=null){
        this.widgetSwt.setBackground(props.colorSwt(dyda.backColor));
      }
      if(dyda.textFont !=null){
        this.widgetSwt.setFont(props.fontSwt(dyda.textFont));
      }
    }
    if((chg & chgVisible) !=0) {
      acknChanged(chgVisible);
      this.widgetSwt.setVisible(true);
      //widgetSwt.getShell().setVisible(true);
    }
    if((chg & chgFocus) !=0) {
      acknChanged(chgFocus);
      this.widgetSwt.setFocus();
      //widgetSwt.getShell().setVisible(true);
    }
    if((chg & chgInvisible) !=0) {
      acknChanged(chgInvisible);
      this.widgetSwt.setVisible(false);
      //widgetSwt.getShell().setVisible(false);
    }
    if((chg & chgColorText) !=0){ acknChanged(chgColorText); widgetSwt.setForeground(swtWidgHelper.mng.getColorImpl(dyda().textColor)); }
    if((chg & chgColorBack) !=0){ acknChanged(chgColorBack); widgetSwt.setBackground(swtWidgHelper.mng.getColorImpl(dyda().backColor)); }
    this.widgetSwt.redraw();
  }

  
  protected void paintRoutine(PaintEvent e, Canvas canvas){
    GC gc = e.gc;
    //gc.d
    Rectangle dim = canvas.getBounds();
    SwtButton.this.paint1();
    boolean bFocus = canvas.isFocusControl();
    Color colorBack = swtWidgHelper.mng.getColorImpl(colorgback);
    Color colorLine = swtWidgHelper.mng.getColorImpl(colorgline);
    if(bFocus) {
      colorLine = swtWidgHelper.mng.getColorImpl(GralColor.getColor("rd"));
    }
    gc.setBackground(colorBack);
    canvas.drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
    //Color color = canvas.getForeground(); //of the widget.
    gc.setForeground(colorLine);  //black
    gc.setFont(fontText);
    //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
    //fontData.
    gc.setForeground(colorBack);
    gc.fillRectangle(1,1,dim.width-1, dim.height-1);
    int ypText;
    if(sButtonText !=null){
//      if(sButtonText.equals("help")) {   //support to debug a specific button, set breakpoint here
//        Debugutil.stop();
//      }
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
    if(isPressed()){
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
  
  
  
  
  private class SwtButtonImpl extends Canvas
  {
    
    SwtButtonImpl(Composite parent, int style){
      super(parent, style);
      setForeground(black);
      
      addPaintListener(paintListener);  
      
    }
    
    PaintListener paintListener = new PaintListener(){
      @Override public void paintControl(PaintEvent e) {
        SwtButton.this.paintRoutine(e, SwtButtonImpl.this);
      }
    };
    

    
  }
  
  
  protected class KeyListener extends SwtKeyListener
  {

    public KeyListener(GralKeyListener keyAction)
    { super(keyAction);
    }

    @SuppressWarnings("synthetic-access") 
    @Override 
    public final boolean specialKeysOfWidgetType(int key, GralWidget_ifc widggArg, Object widgImpl){ 
      boolean bDone = true;
      if(key == KeyCode.enter) {
        widgg.activate();
      }
      return bDone; 
    }
  }


  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  };

  
  
  
  
}
