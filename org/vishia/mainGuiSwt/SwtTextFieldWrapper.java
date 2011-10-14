package org.vishia.mainGuiSwt;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.swt.WidgetSimpleWrapperSwt;

public class SwtTextFieldWrapper extends GralTextField
{
  protected Text textFieldSwt;
  
  final GralPrimaryWindow_ifc mainWindow;
  
  StringBuffer newText = new StringBuffer();
  
  
  public SwtTextFieldWrapper(String name, Text widgetSwt, char whatis, GralPrimaryWindow_ifc mainWindow)
  { super(name, whatis);
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


  @Override public Object getWidgetImplementation()
  { return textFieldSwt;
  }


  @Override public GralColor setBackgroundColor(GralColor color)
  { return SwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return SwtWidgetHelper.setForegroundColor(color, textFieldSwt);
  }
  
  
  @Override public void removeWidgetImplementation()
  {
    textFieldSwt.dispose();
    textFieldSwt = null;
  }


  
}
