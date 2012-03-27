package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextField;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
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
  protected void removeWidgetImplementation()
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

  @Override public String getPromptLabelImpl(){ return promptSwt.getText(); }


  
  @Override public boolean setFocus()
  { return AwtWidgetHelper.setFocusOfTabSwt(widgetAwt);
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
  public void setMouseAction(GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setTextStyle(GralColor color, GralFont font)
  {
    // TODO Auto-generated method stub
    
  }

  @Override protected void repaintGthread(){
    int chg = this.whatIsChanged.get();
    int catastrophicalCount = 0;
    do{
      if(++catastrophicalCount > 10000) throw new RuntimeException("atomic failed");
      if((chg & chgText) !=0){ 
        widgetAwt.setText(text); 
      }
      if((chg & chgColorText) !=0){ widgetAwt.setForeground(((AwtWidgetMng)itsMng).getColorImpl(colorText)); }
      if((chg & chgColorBack) !=0){ widgetAwt.setBackground(((AwtWidgetMng)itsMng).getColorImpl(colorBack)); }
      widgetAwt.repaint();
    } while(!whatIsChanged.compareAndSet(chg, 0));
  }

  

  
}