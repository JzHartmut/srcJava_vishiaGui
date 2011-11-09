package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;

public class SwtTextFieldWrapper extends GralTextField
{
  protected Text textFieldSwt;
  
  private DropTarget drop;
  
  StringBuffer newText = new StringBuffer();
  
  public SwtTextFieldWrapper(String name, Composite parent, char whatis, GralWidgetMng mng)
  { super(name, whatis, mng);
    textFieldSwt = new Text(parent, SWT.SINGLE);
  }

  
  protected void setDropEnable(int dropType)
  {
    new SwtDropListener(dropType, textFieldSwt); //associated with textFieldSwt.
  }
  
  
  protected void setDragEnable(int dragType)
  {
    new SwtDragListener(dragType, textFieldSwt); //associated with textFieldSwt.
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
   


  
  protected GralDispatchCallbackWorker changeText = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.setText(newText.toString());
        newText.setLength(0);
      }
      windowMng.removeDispatchListener(this);
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
  
  
  @Override public void redraw(){  textFieldSwt.redraw(); textFieldSwt.update(); }

  
  @Override public void removeWidgetImplementation()
  {
    textFieldSwt.dispose();
    textFieldSwt = null;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { textFieldSwt.setBounds(x,y,dx,dy);
  }
  
  
  DragDetectListener dragListener = new DragDetectListener()
  { @Override public void dragDetected(DragDetectEvent e)
    {
      // TODO Auto-generated method stub
      stop();
    }
  };

    
  
  void stop(){}
  
}
