package org.vishia.gral.swt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;

public class SwtTextBox extends GralTextBox
{
  protected Text textFieldSwt;
  
  StringBuffer newText = new StringBuffer();
  
  
  public SwtTextBox(String name, Composite parent, int style, GralWidgetMng mng)
  { super(name, 't', mng);
    textFieldSwt = new Text(parent, style);
  }

  //@Override public Widget getWidgetImplementation(){ return textFieldSwt; } 
  //@Override public boolean setFocus(){ return textFieldSwt.setFocus(); }


  @Override
  public void viewTrail()
  {
    //textAreaOutput.setCaretPosition(textAreaOutput.getLineCount());
    ScrollBar scroll = textFieldSwt.getVerticalBar();
    int maxScroll = scroll.getMaximum();
    scroll.setSelection(maxScroll);
    textFieldSwt.update();
    
  }

  @Override
  public int getNrofLines(){ return textFieldSwt.getLineCount(); }

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
  { return SwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return SwtWidgetHelper.setForegroundColor(color, textFieldSwt);
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
  


  
  @Override public void redraw(){  textFieldSwt.redraw(); textFieldSwt.update(); }


  
  @Override public void removeWidgetImplementation()
  {
    textFieldSwt.dispose();
    textFieldSwt = null;
  }

  @Override
  public void setMouseAction(GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }


  
  
}
