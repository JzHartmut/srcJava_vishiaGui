package org.vishia.mainGuiSwt;

import org.eclipse.swt.widgets.Text;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.swt.WidgetSimpleWrapperSwt;

public class SwtTextFieldWrapper extends WidgetSimpleWrapperSwt implements GralTextField_ifc
{
  final Text textFieldSwt;
  
  final GralPrimaryWindow_ifc mainWindow;
  
  StringBuffer newText = new StringBuffer();
  
  
  public SwtTextFieldWrapper(Text widgetSwt, GralPrimaryWindow_ifc mainWindow)
  { super(widgetSwt);
    textFieldSwt = widgetSwt;
    this.mainWindow = mainWindow;
  }

  
  @Override public void setText(String arg)
  {
    if(Thread.currentThread().getId() == mainWindow.getThreadIdGui()){
      textFieldSwt.setText(arg);
    } else {
      newText.setLength(0);
      newText.append(arg);
      mainWindow.addDispatchListener(changeText);    
    }
  }
  
  @Override public String getText()
  {
    String oldText = textFieldSwt.getText();
    return oldText;
  }
   


  
  protected GralDispatchCallbackWorker changeText = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.setText(newText.toString());
        newText.setLength(0);
      }
      mainWindow.removeDispatchListener(this);
    }
  };
  
  

  
}
