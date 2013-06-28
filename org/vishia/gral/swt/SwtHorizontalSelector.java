package org.vishia.gral.swt;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.widget.GralHorizontalSelector;

/**This class is a selector in one text field. You can set the cursor into the field 
 * and select between Parts which are separated with a given character sequence.
 * @author Hartmut Schorrig
 *
 */
public class SwtHorizontalSelector extends GralHorizontalSelector<?>.GraphicImplAccess implements GralWidgImpl_ifc
{
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2013-06-18 Hartmut created, new idea.
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
  public static final int version = 20130618;

  //private final GralHorizontalSelector<?> wdgGral;
  
  //public final GralWidgetAccess wdgGralAccess;
  
  protected Canvas widgetSwt;

  protected final SwtMng mng;
  

  
  private Font fontText;

  
  
  public SwtHorizontalSelector(SwtMng mng, GralHorizontalSelector<?> wdgGral)
  { wdgGral.super();
    this.mng = mng;
    super.outer.pos = mng.getPositionInPanel();
    //this.wdgGral = wdgGral;
    //this.wdgGralAccess = new GralWidgetAccess(wdgGral);
    wdgGral.implMethodWidget_.setWidgetImpl(this);
    Composite panel = (Composite)outer.pos.panel.getPanelImpl();
    widgetSwt = new Canvas(panel,0);
    widgetSwt.setData(wdgGral);
    widgetSwt.addPaintListener(paintListener);
    widgetSwt.addMouseListener(mouseListener);
    mng.setBounds_(widgetSwt);
    float ySize = outer.pos.height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
    super.execAfterCreationImplWidget();

  }

  
  
  @Override public void repaintGthread(){
    widgetSwt.redraw();
  }

  
  @Override public Object getWidgetImplementation()
  { return widgetSwt;
  }
  
  @Override public boolean setFocusGThread(){ return widgetSwt.setFocus(); }

  @Override public void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  
  @Override public boolean setVisible(boolean visible)
  { widgetSwt.setVisible(visible);
    return widgetSwt.isVisible();
  }
  
  

  
  
  @SuppressWarnings("unchecked")
  protected void paintControl(Canvas swt, PaintEvent e){
    GC gc = e.gc;
    //gc.d
    Rectangle dim = swt.getBounds();
    GralHorizontalSelector.Item<?> actItem = super.actItem();
    int nrActItem = super.nrItem();
    Color swtColorBack = mng.getColorImpl(super.outer.colorBack);
    Color swtColorText = mng.getColorImpl(super.outer.colorText);
    Color swtColorSelect = mng.getColorImpl(super.outer.colorSelect);
    gc.setBackground(swtColorBack);
    swt.drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
    gc.setFont(fontText);
    //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
    //fontData.
    gc.setForeground(swtColorBack);
    gc.fillRectangle(1,0,dim.width-1, dim.height);
    int xArrow = 20;
    super.calcLeftTab(dim.width, xArrow);
    int xText = 2;
    int yText = 0;
    int ixLeftItem = super.nrLeftTab();
    if(ixLeftItem >0){
      gc.setForeground(swtColorText);
      gc.drawString("<<", xText+4, yText); 
      xText += 20;
    }
    //
    //paint tabs
    //
    if(ixLeftItem >=0){
      int ixItem = ixLeftItem;
      int zItem = super.nrofTabs();
      do {
        GralHorizontalSelector.Item item = super.tab(ixItem); 
        if(item.xSize == 0){
          Point size = gc.stringExtent(item.text);
          if(size.x < 150 - 10){ item.xSize = size.x + 10;}
          else { item.xSize = 150; }
        }
        int xEnd = xText + item.xSize;
        if(xEnd < (dim.width - (ixItem == (zItem-1) ? xArrow +4 : 4))){
          if(ixItem == nrActItem){
            gc.setForeground(swtColorSelect);  //black
            gc.setLineWidth(2);
          } else {
            gc.setForeground(swtColorText);
            gc.setLineWidth(1);
          }
          gc.drawString(item.text, xText+4, yText);
          gc.drawLine(xText+1, 3, xText+1, dim.height);
          gc.drawLine(xText+1, 3, xText+4, 0);
          gc.drawLine(xEnd-1, 3, xEnd-1, dim.height);
          gc.drawLine(xEnd-1, 3, xEnd-4, 0);
          gc.drawLine(xEnd-4, 0, xEnd-item.xSize+4, 0);
          xText = xEnd;
          ixItem +=1;
        } else break;
      } while(ixItem < zItem);
      if(ixItem < zItem){
        gc.setForeground(swtColorText);
        gc.drawString(">>", dim.width-16, yText); 
      }
    }    
  }

  
  
  protected void mouseDown(MouseEvent e)
  {
    super.findTab(e.x);
    widgetSwt.redraw();
    
  }

  
  public void mouseUp(MouseEvent e)
  {
    Rectangle dim = widgetSwt.getBounds();
    if(e.button == 1){
      if(e.y >0 && e.y < dim.height){
        super.setDstToActItem();
      } else {
        super.clearDstItem();
      }
      widgetSwt.redraw();  //because selection is changed.
    }
    //else: content menu does the action.
  }

  
  
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent e) {
      SwtHorizontalSelector.this.paintControl(widgetSwt, e);
    }
  };
  
  
  
  MouseListener mouseListener = new MouseListener(){

    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouseDown(MouseEvent e)
    { SwtHorizontalSelector.this.mouseDown(e);
    }

    @Override
    public void mouseUp(MouseEvent e)
    { SwtHorizontalSelector.this.mouseUp(e);
    }
    
  };

  
  
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




  
}
