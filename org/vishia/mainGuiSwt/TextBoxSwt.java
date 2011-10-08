package org.vishia.mainGuiSwt;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.widget.TextBoxGuifc;

public class TextBoxSwt implements TextBoxGuifc
{
  final Text text;
  
  final GralPrimaryWindow_ifc mainWindow;
  
  StringBuffer newText = new StringBuffer();
  
  public TextBoxSwt(Composite parent, int style, GralPrimaryWindow_ifc mainWindow)
  { text = new Text(parent, style);
    this.mainWindow = mainWindow;
  }

  @Override public Widget getWidget(){ return text; } 
  @Override public boolean setFocus(){ return text.setFocus(); }


  @Override
  public void viewTrail()
  {
    //textAreaOutput.setCaretPosition(textAreaOutput.getLineCount());
    ScrollBar scroll = text.getVerticalBar();
    int maxScroll = scroll.getMaximum();
    scroll.setSelection(maxScroll);
    text.update();
    
  }

  @Override
  public int getNrofLines(){ return text.getLineCount(); }

  @Override
  public Appendable append(CharSequence arg0) throws IOException
  { if(Thread.currentThread().getId() == mainWindow.getThreadIdGui()){
      text.append(arg0.toString());
    } else {
      newText.append(arg0);
      mainWindow.addDispatchListener(changeGui);    
    }
    return this;
  }

  @Override
  public Appendable append(char arg0) throws IOException
  { if(Thread.currentThread().getId() == mainWindow.getThreadIdGui()){
    String ss = "" + arg0;
    text.append(ss);
  } else {
    newText.append(arg0);
    mainWindow.addDispatchListener(changeGui);    
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
  public String setText(String arg)
  {
    String oldText = text.getText();
    text.setText(arg);
    return oldText;
  }

  @Override
  public void append(String src)
  {
    text.append(src);
    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  
  GralDispatchCallbackWorker changeGui = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        text.append(newText.toString());
        viewTrail();
        newText.setLength(0);
      }
      mainWindow.removeDispatchListener(this);
    }
  };
  
  
}
