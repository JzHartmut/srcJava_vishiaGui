package org.vishia.gral.swt;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GetGralWidget_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class SwtDragListener extends DragSourceAdapter
{
  /**This instance helps to check whether a Transfer can be done. */
  private final FileTransfer fileTransfer;
  
  private final TextTransfer textTransfer;
  
  SwtDragListener(int dragType, Control control){
    DragSource drag = new DragSource(control, DND.DROP_COPY);
    drag.addDragListener(this);
    switch(dragType){
      case KeyCode.dragFiles:{
        fileTransfer = FileTransfer.getInstance();
        textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[2];
        transfers[0]= fileTransfer;
        transfers[1]= textTransfer;
        drag.setTransfer(transfers);
      } break;
      case KeyCode.dragText:{
        fileTransfer = FileTransfer.getInstance();
        textTransfer = TextTransfer.getInstance();
        Transfer[] transfers = new Transfer[1];
        transfers[0]= textTransfer;
        drag.setTransfer(transfers);
      } break;
      default: throw new IllegalArgumentException("unknown dragtype: "+ Integer.toHexString(dragType));
    }

  }


  @Override
  public void dragFinished(DragSourceEvent event)
  {
    // TODO Auto-generated method stub
    stop();   
    System.out.println("drag finished\n");
  }


  /**This method is called if the mouse button is released over the target.
   * The possible data types of the receiver are known therefore: The receiver may accept
   * a text transfer if it is a text editor, or a file transfer if it is a file browser etc.
   * The type of transfer can be tested with event.dataType.
   * @see org.eclipse.swt.dnd.DragSourceAdapter#dragSetData(org.eclipse.swt.dnd.DragSourceEvent)
   */
  @Override public void dragSetData(DragSourceEvent event)
  {
    DragSource drag = (DragSource)event.getSource();
    Control widgetSwt = drag.getControl();
    //the associated text field, should be identical with event.getSource()
    GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widgetSwt.getData());
    if(widgg!=null){
      GralUserAction action = widgg.getActionDrag();
      if(action !=null){
        //call the action to get the data from drag
        //ret is a array which references the necessary String[] for answer.
        String[][] ret = new String[1][];
        boolean bOk = action.userActionGui(KeyCode.dragFiles, widgg, (Object)ret);  //Note: 1 file per variable String argument
        if(bOk && ret[0] !=null){
          String[] data = ret[0];
          TransferData transferData = event.dataType;
          if(textTransfer.isSupportedType(transferData)){
            //the call of fileTransfer.javaToNative(data, transferData) will be done       
            event.data = data[0]; //transferData;
            event.doit = true;
          } else if(fileTransfer.isSupportedType(transferData)){
            //the call of fileTransfer.javaToNative(data, transferData) will be done       
            data[0] = data[0].replace('/', '\\');
            event.data = data; //transferData;
            event.doit = true;
          } else {
            //the destination does not support a file transfer.
            event.doit = false;
          }
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
