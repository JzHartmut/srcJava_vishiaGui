package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextField;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;

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

  @Override
  public String getText()
  { return widgetAwt.getText();
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override public Label getPromptLabelImpl(){ return promptSwt; }

  @Override
  public void redraw()
  {
    // TODO Auto-generated method stub
    
  }
  
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

}