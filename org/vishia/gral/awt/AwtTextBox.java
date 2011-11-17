package org.vishia.gral.awt;

import java.awt.Container;
import java.awt.TextArea;
import java.io.IOException;

import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;

/**
 *
 */
public class AwtTextBox extends GralTextBox
{
  protected AwtTextAreaImpl textFieldSwt;
  
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

  @Override
  public Appendable append(CharSequence arg0) throws IOException
  { if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      textFieldSwt.append(arg0.toString());
    } else {
      newText.append(arg0);
      windowMng.addDispatchListener(changeTextBoxTrail);    
    }
    return this;
  }

  @Override
  public Appendable append(char arg0) throws IOException
  { if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
    String ss = "" + arg0;
    textFieldSwt.append(ss);
  } else {
    newText.append(arg0);
    windowMng.addDispatchListener(changeTextBoxTrail);    
  }
  return this;
}

  @Override
  public Appendable append(CharSequence arg0, int arg1, int arg2)
      throws IOException
  {
    append(arg0.subSequence(arg1, arg2).toString());
    return this;
  }

  @Override public void setText(String arg)
  {
    if(Thread.currentThread().getId() == windowMng.getThreadIdGui()){
      textFieldSwt.setText(arg);
    } else {
      newText.setLength(0);
      newText.append(arg);
      windowMng.addDispatchListener(changeText);    
    }
  }
  
  @Override public String getText()
  {
    String oldText = textFieldSwt.getText();
    return oldText;
  }
   
  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
  }


  @Override public GralColor setBackgroundColor(GralColor color)
  { return AwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return AwtWidgetHelper.setForegroundColor(color, textFieldSwt);
  }
  
  



  
  protected GralDispatchCallbackWorker changeTextBoxTrail = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.append(newText.toString());
        viewTrail();
        newText.setLength(0);
      }
      windowMng.removeDispatchListener(this);
    }
  };
  
  
  protected GralDispatchCallbackWorker changeText = new GralDispatchCallbackWorker()
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
  


  
  @Override public void redraw(){  textFieldSwt.repaint();  }


  
  @Override public void removeWidgetImplementation()
  {
    Container parent = textFieldSwt.getParent();
    parent.remove(textFieldSwt);
    textFieldSwt = null;
  }


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

  
}
