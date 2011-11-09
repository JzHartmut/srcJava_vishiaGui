package org.vishia.gral.swt;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

public class SwtDragListener implements DragSourceListener
{
  private FileTransfer fileTransfer;
  

  SwtDragListener(int dropType, Control control){
    DragSource drag = new DragSource(control, DND.DROP_COPY);
    drag.addDragListener(this);
    switch(dropType){
      case KeyCode.dragFiles:{
        fileTransfer = FileTransfer.getInstance();
        Transfer[] transfers = new Transfer[1];
        transfers[0]= fileTransfer;
        drag.setTransfer(transfers);
      } break;
      default: throw new IllegalArgumentException("unknown dragtype: "+ Integer.toHexString(dropType));
    }

  }


  @Override
  public void dragFinished(DragSourceEvent event)
  {
    // TODO Auto-generated method stub
    stop();    
  }


  @Override public void dragSetData(DragSourceEvent event)
  {
    DragSource drag = (DragSource)event.getSource();
    Control widgetSwt = drag.getControl();
    Object oData = widgetSwt.getData();  //the associated text field, should be identical with event.getSource()
    if(oData!=null && oData instanceof GralWidget){
      GralWidget widgg = (GralWidget)oData;
      GralUserAction action = widgg.getActionDrag();
      if(action !=null){
        //call the action to get the data from drag
        String[][] ret = new String[1][];
        boolean bOk = action.userActionGui(KeyCode.dragFiles, widgg, (Object)ret);  //Note: 1 file per variable String argument
        if(bOk && ret[0] !=null){
          TransferData transferData = event.dataType;
          fileTransfer.javaToNative(ret[0], transferData);      
          event.data = transferData;
          event.doit = true;
        } else {
          //action will be prevent drag, no data:
          event.doit = false;
        }
      } else throw new IllegalArgumentException("no action found for drop.");
    } else throw new IllegalArgumentException("GralWidget as getData() of swt.Control expected.");
    
  }


  @Override
  public void dragStart(DragSourceEvent event)
  {
    // TODO Auto-generated method stub
    stop();
    event.doit = true;
  }
  
  void stop(){}
  
}
