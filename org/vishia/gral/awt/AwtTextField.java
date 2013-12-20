package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextField;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.swt.SwtMng;

public class AwtTextField extends GralTextField
{
  /*packagePrivate*/ final AwtTextFieldImpl widgetAwt;
  
  /**A possible prompt for the text field or null. */
  /*packagePrivate*/ Label promptSwt;
  
  public AwtTextField(String name, char whatis, AwtWidgetMng mng, Container parent)
  {
    super(name, whatis, mng);
    widgetAwt = new AwtTextFieldImpl();
    //widgetAwt.setForeground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("rd")));
    //widgetAwt.setBackground(mng.propertiesGuiAwt.colorAwt(GralColor.getColor("gn")));
    parent.add(widgetAwt);
  }

  @Override
  public void removeWidgetImplementation()
  {
      // TODO Auto-generated method stub
    
  }

  @Override public int getCursorPos(){ return widgetAwt.getCaretPosition(); }


  @Override
  public void setText(CharSequence text)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getWidgetImplementation()
  {
    return widgetAwt;
  }


  
  @Override public boolean setFocusGThread()
  { return AwtWidgetHelper.setFocusOfTabSwt(widgetAwt);
  }


  
  @Override public int setCursorPos(int pos){
    int oldPos = widgetAwt.getCaretPosition();
    widgetAwt.setCaretPosition(pos);
    return oldPos;
  }


  


  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  
  
public static class AwtTextFieldImpl extends TextField implements AwtWidget
  {
    Object data;
    
    AwtTextFieldImpl()
    { super("test");
      
    }
    
    @Override public Object getData(){ return data; }
  
    @Override public void setData(Object dataP){ this.data = dataP; }
    
  }


  @Override
  public void setTextStyle(GralColor color, GralFont font)
  {
    // TODO Auto-generated method stub
    
  }
  
  
  @Override public void setEditable(boolean editable){
    widgetAwt.setEditable(editable);
  }



  @Override public void repaintGthread(){
    int chg = dyda.whatIsChanged.get();
    int catastrophicalCount = 0;
    do{
      if(++catastrophicalCount > 10000) throw new RuntimeException("atomic failed");
      if((chg & ImplAccess.chgText) !=0  && dyda.displayedText !=null){ 
        widgetAwt.setText(dyda.displayedText); 
      }
      if((chg & ImplAccess.chgColorText) !=0){ widgetAwt.setForeground(((AwtWidgetMng)itsMng).getColorImpl(dyda.textColor)); }
      if((chg & ImplAccess.chgColorBack) !=0){ widgetAwt.setBackground(((AwtWidgetMng)itsMng).getColorImpl(dyda.backColor)); }
      widgetAwt.repaint();
    } while(!dyda.whatIsChanged.compareAndSet(chg, 0));
  }

  

  
}