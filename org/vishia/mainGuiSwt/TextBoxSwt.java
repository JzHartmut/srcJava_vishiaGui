package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

public class TextBoxSwt extends SwtTextFieldWrapper implements GralTextBox_ifc
{
  public TextBoxSwt(Composite parent, int style, GralPrimaryWindow_ifc mainWindow)
  { super(new Text(parent, style), mainWindow);
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
  { if(Thread.currentThread().getId() == mainWindow.getThreadIdGui()){
      textFieldSwt.append(arg0.toString());
    } else {
      newText.append(arg0);
      mainWindow.addDispatchListener(changeTextBoxTrail);    
    }
    return this;
  }

  @Override
  public Appendable append(char arg0) throws IOException
  { if(Thread.currentThread().getId() == mainWindow.getThreadIdGui()){
    String ss = "" + arg0;
    textFieldSwt.append(ss);
  } else {
    newText.append(arg0);
    mainWindow.addDispatchListener(changeTextBoxTrail);    
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

  @Override
  public void append(String src)
  {
    textFieldSwt.append(src);
    
  }

  protected GralDispatchCallbackWorker changeTextBoxTrail = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.append(newText.toString());
        viewTrail();
        newText.setLength(0);
      }
      mainWindow.removeDispatchListener(this);
    }
  };
  
  

  
  
}
