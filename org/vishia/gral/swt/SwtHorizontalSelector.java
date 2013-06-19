package org.vishia.gral.swt;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;

/**This class is a selector in one text field. You can set the cursor into the field 
 * and select between Parts which are separated with a given character sequence.
 * @author Hartmut Schorrig
 *
 */
public class SwtHorizontalSelector implements GralWidgImpl_ifc
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
  
  protected final SwtMng mng;
  
  private final Canvas wdgSwt;
  
  private Font fontText;

  
  GralColor colorText, colorSelect, colorBack, colorLine;
  
  public SwtHorizontalSelector(SwtMng mng, GralHorizontalSelector<?> wdgGral)
  {
    //super(name, mng);
    this.wdgGral = wdgGral;
    wdgGral.implMethodWidget_.setWidgetImpl(this);
    this.mng = mng;
    colorText = GralColor.getColor("bk");
    colorSelect = GralColor.getColor("lbl");
    colorBack = GralColor.getColor("wh");
    colorLine = GralColor.getColor("bk");

    Composite panel = (Composite)mng.pos.panel.getPanelImpl();
    wdgSwt = new SwtImpl(panel);
    mng.setBounds_(wdgSwt);
    float ySize = mng.pos.height();
    char size1 = ySize > 3? 'B' : 'A';
    switch(size1){ 
      case 'A': fontText = mng.propertiesGuiSwt.stdInputFont; break;
      case 'B': fontText = mng.propertiesGuiSwt.stdButtonFont; break;
      default: throw new IllegalArgumentException("param size must be A or B");
    }
  }

  
  @SuppressWarnings("unchecked")
  protected void paintControl(Canvas swt, PaintEvent e){
    GC gc = e.gc;
    //gc.d
    Rectangle dim = swt.getBounds();
    Object actItem = wdgGral.guiImplAccess.items().get(1);
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
    int xText = 2;
    int yText = 0;
    for(Object item1: wdgGral.guiImplAccess.items()){
      //@SuppressWarnings("unchecked")
      GralHorizontalSelector.Item item = (GralHorizontalSelector.Item)item1;
      if(item.xSize == 0){
        item.xSize = 50; //TODO
      }
      if(item1 == actItem){
        gc.setForeground(swtColorSelect);  //black
      } else {
        gc.setForeground(swtColorText);  //black
      }
      gc.drawString(item.text, xText+4, yText);
      gc.drawLine(xText+1, 3, xText+1, dim.height);
      gc.drawLine(xText+1, 3, xText+4, 0);
      xText += item.xSize;
      gc.drawLine(xText-1, 3, xText-1, dim.height);
      gc.drawLine(xText-1, 3, xText-4, 0);
      gc.drawLine(xText-4, 0, xText-item.xSize+4, 0);
      
    }
    
  }

  
  private class SwtImpl extends Canvas
  {
    
    SwtImpl(Composite parent){
      super(parent, 0);
      //setForeground(black);
      
      addPaintListener(paintListener);  
      
    }
    
    PaintListener paintListener = new PaintListener(){
      @Override public void paintControl(PaintEvent e) {
        SwtHorizontalSelector.this.paintControl(SwtImpl.this, e);
      }
    };
    
  }


  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public void repaintGthread()
  {
    // TODO Auto-generated method stub
    
  }


  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }



  
}
