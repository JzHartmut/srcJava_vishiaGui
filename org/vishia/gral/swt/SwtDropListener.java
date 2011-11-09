package org.vishia.gral.swt;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

public class SwtDropListener implements DropTargetListener
{
  private FileTransfer fileTransfer;
  

  SwtDropListener(int dropType, Control control){
    DropTarget drop = new DropTarget(control, DND.DROP_COPY);
    drop.addDropListener(this);
    switch(dropType){
      case KeyCode.dropFiles:{
        fileTransfer = FileTransfer.getInstance();
        Transfer[] transfers = new Transfer[1];
        transfers[0]= fileTransfer;
        drop.setTransfer(transfers);
      } break;
      default: throw new IllegalArgumentException("unknown droptype: "+ Integer.toHexString(dropType));
    }

  }
  
  
  @Override
  public void dragEnter(DropTargetEvent event)
  {
    TransferData data = event.currentDataType;
    if(fileTransfer.isSupportedType(data)){
      event.detail = DND.DROP_COPY;
    } else {
      event.detail = DND.DROP_NONE;
    }
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
    TransferData data = event.currentDataType;
    Object oDropData = fileTransfer.nativeToJava(data);      
    String[] files = (String[])oDropData; 
    DropTarget drop = (DropTarget)event.getSource();
    Control widgetSwt = drop.getControl();
    Object oData = widgetSwt.getData();  //the associated text field, should be identical with event.getSource()
    if(oData!=null && oData instanceof GralWidget){
      GralWidget widgg = (GralWidget)oData;
      GralUserAction action = widgg.getActionDrop();
      if(action !=null){
        //call the action to apply the data from drop:
        for(int ix = 0; ix < files.length; ++ix){
          files[ix] = files[ix].replace('\\', '/');  //at user level: use slash only, on windows too!
        }
        action.userActionGui(KeyCode.dropFiles, widgg, (Object[])files);  //Note: 1 file per variable String argument
      } else throw new IllegalArgumentException("no action found for drop.");
    } else throw new IllegalArgumentException("GralWidget as getData() of swt.Control expected.");
  }

  @Override
  public void dropAccept(DropTargetEvent event)
  {
    // TODO Auto-generated method stub
    stop();
    
  }
  
  
  
  void stop(){}
  
}
