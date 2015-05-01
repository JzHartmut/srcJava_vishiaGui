package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextField;

import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.swt.SwtMng;

public class AwtTextField extends GralTextField.GraphicImplAccess
{
  /*packagePrivate*/ final TextField widgetAwt;
  
  /**A possible prompt for the text field or null. */
  /*packagePrivate*/ Label promptSwt;
  
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImpl_ifc} which are delegate from this.
   */
  private final AwtWidgetHelper widgHelper;
  
  
  
  
  public AwtTextField(GralTextField widgg, AwtWidgetMng mng)
  { widgg.super(widgg, mng.mng); //NOTE: superclass is a non static inner class of GralTextField. 
    GralPos pos = widgg.pos();
    Container panelAwt = (Container)pos.panel.getWidgetImplementation();
    
    widgetAwt = new TextField();
    mng.setPosAndSize_(widgetAwt);
    //widgetAwt.setForeground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("rd")));
    //widgetAwt.setBackground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("gn")));
    panelAwt.add(widgetAwt);
    widgHelper = new AwtWidgetHelper(widgetAwt, mng);
  }

  @Override
  public void removeWidgetImplementation()
  {
      // TODO Auto-generated method stub
    
  }

  //@Override public int getCursorPos(){ return widgetAwt.getCaretPosition(); }


  @Override
  public Object getWidgetImplementation()
  {
    return widgetAwt;
  }


  
  @Override public boolean setFocusGThread()
  { return AwtWidgetHelper.setFocusOfTabSwt(widgetAwt);
  }


  
  public int setCursorPos(int pos){
    int oldPos = widgetAwt.getCaretPosition();
    widgetAwt.setCaretPosition(pos);
    return oldPos;
  }


  



  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy){ widgHelper.setBoundsPixel(x, y, dx, dy); }

  
  
  
  
  public void setEditable(boolean editable){
    widgetAwt.setEditable(editable);
  }



  @Override public void repaintGthread(){
    GralWidget.DynamicData dyda = dyda();
    int chg = dyda.whatIsChanged.get();
    int catastrophicalCount = 0;
    do{
      if(++catastrophicalCount > 10000) throw new RuntimeException("atomic failed");
      if((chg & chgText) !=0  && dyda.displayedText !=null){ 
        widgetAwt.setText(dyda.displayedText); 
      }
      if((chg & chgColorText) !=0){ widgetAwt.setForeground(widgHelper.mng.getColorImpl(dyda.textColor)); }
      if((chg & chgColorBack) !=0){ widgetAwt.setBackground(widgHelper.mng.getColorImpl(dyda.backColor)); }
      widgetAwt.repaint();
    } while(!dyda.whatIsChanged.compareAndSet(chg, 0));
  }

  @Override public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }

  

  
}