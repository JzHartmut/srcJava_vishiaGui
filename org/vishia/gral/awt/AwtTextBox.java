package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.Label;
import java.awt.TextArea;
import java.io.IOException;

import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralUserAction;

/**
 *
 */
public class AwtTextBox extends GralTextBox
{
  protected AwtTextAreaImpl textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  /*packagePrivate*/ Label promptSwt;
  
  StringBuffer newText = new StringBuffer();
  
  
  public AwtTextBox(String name, Container parent, int style, AwtWidgetMng mng)
  { super(name, 't', mng);
    textFieldSwt = new AwtTextAreaImpl();
    parent.add(textFieldSwt);
  }

  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }


  @Override
  public void viewTrail()
  {
    //textAreaOutput.setCaretPosition(textAreaOutput.getLineCount());
    /*
    ScrollBar scroll = textFieldSwt.getVerticalBar();
    int maxScroll = scroll.getMaximum();
    scroll.setSelection(maxScroll);
    */
    textFieldSwt.repaint();
    
  }

  @Override
  public int getNrofLines(){ return 0;  }  //textFieldSwt.getLineCount();

  @Override public int getCursorPos(){ return textFieldSwt.getCaretPosition(); }


  
  @Override public void setTextInGThread(CharSequence text){ 
    textFieldSwt.setText(text.toString()); 
  }
  
  
    
  @Override public void appendTextInGThread(CharSequence text){ 
    textFieldSwt.append(text.toString()); 
  }
  
  

  
  
  @Override public String getText()
  {
    String oldText = textFieldSwt.getText();
    return oldText;
  }
   
  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
  }

  @Override public String getPromptLabelImpl(){ return promptSwt.getText(); }

  @Override public GralColor setBackgroundColor(GralColor color)
  { return AwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return AwtWidgetHelper.setForegroundColor(color, textFieldSwt);
  }
  
  



  
  protected GralDispatchCallbackWorker changeTextBoxTrail = new GralDispatchCallbackWorker("AwtTextBox.changeTextBoxTrail")
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.append(newText.toString());
        viewTrail();
        newText.setLength(0);
      }
      windowMng.removeDispatchListener(this);
    }
  };
  
  
  protected GralDispatchCallbackWorker changeText = new GralDispatchCallbackWorker("AwtTextBox.changeTextB")
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.setText(newText.toString());
        newText.setLength(0);
      }
      windowMng.removeDispatchListener(this);
    }
  };
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  


  
  @Override public void repaint(){  textFieldSwt.repaint();  }

  

  
  @Override public boolean setFocus()
  { return AwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }


  @Override public int setCursorPos(int pos){
    int oldPos = textFieldSwt.getCaretPosition();
    textFieldSwt.setCaretPosition(pos);
    return oldPos;
  }


  
  @Override public void removeWidgetImplementation()
  {
    Container parent = textFieldSwt.getParent();
    parent.remove(textFieldSwt);
    textFieldSwt = null;
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
  
  
  
public static class AwtTextAreaImpl extends TextArea implements AwtWidget
  {
    Object data;
    
    AwtTextAreaImpl()
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
  
  
  @Override public void setEditable(boolean editable){
    textFieldSwt.setEditable(editable);
  }


  @Override
  public void repaintGthread() {
    // TODO Auto-generated method stub
    
  }

  
  
}
