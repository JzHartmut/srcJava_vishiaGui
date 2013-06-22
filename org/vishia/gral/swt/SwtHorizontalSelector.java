package org.vishia.gral.swt;

import java.util.List;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.util.KeyCode;

/**This class is a selector in one text field. You can set the cursor into the field 
 * and select between Parts which are separated with a given character sequence.
 * @author Hartmut Schorrig
 *
 */
public class SwtHorizontalSelector extends SwtWidgetSimpleWrapper implements GralWidgImpl_ifc
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
  @SuppressWarnings("hiding")
  public static final int version = 20130618;

  private final GralHorizontalSelector<?> wdgGral;
  
  public final GralWidgetAccess wdgGralAccess;
  

  
  private Font fontText;

  
  GralColor colorText, colorSelect, colorBack, colorLine;
  
  public SwtHorizontalSelector(SwtMng mng, GralHorizontalSelector<?> wdgGral)
  { super(null, mng);
    this.wdgGral = wdgGral;
    this.wdgGralAccess = new GralWidgetAccess(wdgGral);
    wdgGral.implMethodWidget_.setWidgetImpl(this);
    colorText = GralColor.getColor("bk");
    colorSelect = GralColor.getColor("lbl");
    colorBack = GralColor.getColor("wh");
    colorLine = GralColor.getColor("bk");

    Composite panel = (Composite)mng.pos.panel.getPanelImpl();
    widgetSwt = new Canvas(panel,0);
    widgetSwt.setData(wdgGral);
    widgetSwt.addPaintListener(paintListener);
    widgetSwt.addMouseListener(mouseListener);
    mng.setBounds_(widgetSwt);
    float ySize = mng.pos.height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
    wdgGralAccess.execAfterCreationImplWidget();

  }

  
  
  @SuppressWarnings("unchecked")
  protected void paintControl(Canvas swt, PaintEvent e){
    GC gc = e.gc;
    //gc.d
    Rectangle dim = swt.getBounds();
    GralHorizontalSelector.Item<?> actItem = wdgGralAccess.actItem();
    int nrActItem = wdgGralAccess.nrItem();
    Color swtColorBack = mng.getColorImpl(colorBack);
    Color swtColorText = mng.getColorImpl(colorText);
    Color swtColorSelect = mng.getColorImpl(colorSelect);
    gc.setBackground(swtColorBack);
    swt.drawBackground(e.gc, dim.x+1, dim.y+1, dim.width-1, dim.height-1);
    gc.setFont(fontText);
    //FontData fontData = mng.propertiesGui.stdButtonFont.getFontData();
    //fontData.
    gc.setForeground(swtColorBack);
    gc.fillRectangle(1,0,dim.width-1, dim.height);
    int xArrow = 20;
    wdgGralAccess.calcLeftTab(dim.width, xArrow);
    int xText = 2;
    int yText = 0;
    int ixLeftItem = wdgGralAccess.nrLeftTab();
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
      List items = wdgGralAccess.items();
      int zItem = wdgGralAccess.nrofTabs();
      do {
        GralHorizontalSelector.Item item = wdgGralAccess.tab(ixItem); //(GralHorizontalSelector.Item)items.get(ixItem);
        if(item.xSize == 0){
          item.xSize = 50; //TODO
        }
        int xEnd = xText + item.xSize;
        if(xEnd < (dim.width - (ixItem == (zItem-1) ? xArrow +4 : 4))){
          if(ixItem == nrActItem){
            gc.setForeground(swtColorSelect);  //black
          } else {
            gc.setForeground(swtColorText);
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

  
  
  
  PaintListener paintListener = new PaintListener(){
    @Override public void paintControl(PaintEvent e) {
      SwtHorizontalSelector.this.paintControl((Canvas)widgetSwt, e);
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
    {
      wdgGralAccess.findTab(e.x);
      widgetSwt.redraw();
      
    }

    @Override
    public void mouseUp(MouseEvent e)
    {
      Rectangle dim = widgetSwt.getBounds();
      if(e.button == 1){
        if(e.y >0 && e.y < dim.height){
          wdgGralAccess.setDstToActItem();
        } else {
          wdgGralAccess.clearDstItem();
        }
        widgetSwt.redraw();  //because selection is changed.
      }
      //else: contect menu does the action.
    }
    
  };

  
  
  private static class GralWidgetAccess extends GralHorizontalSelector<?>.GraphicImplAccess{

    GralWidgetAccess(GralHorizontalSelector<?> wdgGral)
    {
      wdgGral.super();
    }
    
    @Override protected void findTab(int xMouse){ super.findTab(xMouse); }
    
    @Override protected void setDstToActItem(){ super.setDstToActItem(); }

    @Override protected void clearDstItem(){ super.clearDstItem(); }

    @Override protected void removeDstItem(){ super.removeDstItem(); }

  }
  
  

  
}
