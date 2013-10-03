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
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.widget.GralHorizontalSelector;

/**This class is a selector in one line. You can set the cursor into the field.
 * @author Hartmut Schorrig
 *
 */
public class SwtHorizontalSelector extends GralHorizontalSelector.GraphicImplAccess implements GralWidgImpl_ifc
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
  
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  SwtWidgetSimpleWrapper swtWidgWrapper;

  
  //protected Canvas widgetSwt;

  //protected final SwtMng mng;
  

  
  private Font fontText;

  
  
  public SwtHorizontalSelector(SwtMng mng, GralHorizontalSelector<?> wdgGral)
  { super(wdgGral, mng);  //Invoke constructor of the super class, with knowledge of its outer class.
    //this.mng = mng;
    Composite panel = (Composite)outer.pos().panel.getPanelImpl();
    //widgetSwt = new Canvas(panel,0);
    this.swtWidgWrapper = new SwtWidgetSimpleWrapper(new Canvas(panel,0), mng);
    swtWidgWrapper.widgetSwt.setData(wdgGral);
    swtWidgWrapper.widgetSwt.addPaintListener(paintListener);
    swtWidgWrapper.widgetSwt.addMouseListener(mouseListener);
    mng.setBounds_(swtWidgWrapper.widgetSwt);
    float ySize = outer.pos().height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
    super.execAfterCreationImplWidget();

  }

  
  
  @Override public void repaintGthread(){ swtWidgWrapper.repaintGthread(); }

  
  @Override public Object getWidgetImplementation(){ return swtWidgWrapper.getWidgetImplementation(); }
  
  @Override public boolean setFocusGThread(){ return swtWidgWrapper.setFocusGThread(); }

  @Override public void removeWidgetImplementation(){ swtWidgWrapper.removeWidgetImplementation(); }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy){ swtWidgWrapper.setBoundsPixel(x, y, dx, dy); }

  
  @Override public GralRectangle getPixelPositionSize(){ return swtWidgWrapper.getPixelPositionSize(); }

  
  @Override public boolean setVisible(boolean visible){ return swtWidgWrapper.setVisible(visible); }
  
  

  
  
  @SuppressWarnings("unchecked")
  protected void paintControl(Canvas swt, PaintEvent e){
    GC gc = e.gc;
    //gc.d
    Rectangle dim = swt.getBounds();
    GralHorizontalSelector.Item<?> actItem = super.actItem();
    int nrActItem = super.nrItem();
    Color swtColorBack = swtWidgWrapper.mng.getColorImpl(super.outer.colorBack);
    Color swtColorText = swtWidgWrapper.mng.getColorImpl(super.outer.colorText);
    Color swtColorSelect = swtWidgWrapper.mng.getColorImpl(super.outer.colorSelect);
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
      if(ixItem >= zItem){ 
        ixItem = zItem -1; 
      }
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
    swtWidgWrapper.widgetSwt.redraw();
    
  }

  
  public void mouseUp(MouseEvent e)
  {
    Rectangle dim = swtWidgWrapper.widgetSwt.getBounds();
    if(e.button == 1){
      if(e.y >0 && e.y < dim.height){
        super.setDstToActItem();
      } else {
        super.clearDstItem();
      }
      swtWidgWrapper.widgetSwt.redraw();  //because selection is changed.
    }
    //else: content menu does the action.
  }

  
  
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent e) {
      SwtHorizontalSelector.this.paintControl((Canvas)swtWidgWrapper.widgetSwt, e);
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
