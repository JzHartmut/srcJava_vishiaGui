package org.vishia.mainGuiSwt;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.swt.WidgetSimpleWrapperSwt;

public class SwtTextFieldWrapper extends GralTextField
{
  protected Text textFieldSwt;
  
  final DropTarget drop;
  
  StringBuffer newText = new StringBuffer();
  
  final FileTransfer fileTransfer;
  
  public SwtTextFieldWrapper(String name, Text widgetSwt, char whatis, GralWidgetMng mng)
  { super(name, whatis, mng);
    textFieldSwt = widgetSwt;
    drop = new DropTarget(textFieldSwt, DND.DROP_COPY);
    drop.addDropListener(dropListener);
    fileTransfer = FileTransfer.getInstance();
    Transfer[] transfers = new Transfer[1];
    transfers[0]= fileTransfer;
    drop.setTransfer(transfers);
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

  
  DropTargetListener dropListener = new DropTargetListener(){

    @Override
    public void dragEnter(DropTargetEvent event)
    {
      TransferData data = event.currentDataType;
      Object oData = fileTransfer.nativeToJava(data);      
      String[] files = (String[])oData; 
      // TODO Auto-generated method stub
      textFieldSwt.setText(files[0]);
      stop();
      
    }

    @Override
    public void dragLeave(DropTargetEvent event)
    {
      // TODO Auto-generated method stub
      stop();
      
    }

    @Override
    public void dragOperationChanged(DropTargetEvent event)
    {
      // TODO Auto-generated method stub
      stop();
      
    }

    @Override
    public void dragOver(DropTargetEvent event)
    {
      // TODO Auto-generated method stub
      stop();
      
    }

    @Override
    public void drop(DropTargetEvent event)
    {
      // TODO Auto-generated method stub
      stop();
      
    }

    @Override
    public void dropAccept(DropTargetEvent event)
    {
      // TODO Auto-generated method stub
      stop();
      
    }
    
  };
  
  
  void stop(){}
  
}
