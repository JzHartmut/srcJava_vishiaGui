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

  @Override
  protected void repaintGthread() {
    // TODO Auto-generated method stub
    
  }

  
  @Override public void setSelection(String how){
    if(how.equals("|..<")){
      String sText = widgetAwt.getText();
      int zChars = sText.length();
      int pos0 = 0; //zChars - 20;
      if(pos0 < 0){ pos0 = 0; }
      //widgetAwt.setSelection(pos0, zChars);
    }
  }
  

  
}