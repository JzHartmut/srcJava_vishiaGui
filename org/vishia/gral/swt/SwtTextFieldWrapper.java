package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;

public class SwtTextFieldWrapper extends GralTextField
{
  protected Text textFieldSwt;
  
  /**A possible prompt for the text field or null. */
  Label promptSwt;
  
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
   

  @Override public int getCursorPos(){ return textFieldSwt.getCaretPosition(); }


  
  protected GralDispatchCallbackWorker changeText = new GralDispatchCallbackWorker()
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { if(newText.length() >0){
        textFieldSwt.setText(newText.toString());
        newText.setLength(0);
      }
      windowMng.removeDispatchListener(this);
    }
  };


  /**yet it is the same actionChanged of the widget. is called if the write-able text field was changed and the focus is released.
   * The distinction between both is the key code in {@link GralUserAction#userActionGui(int, GralWidget, Object...)}.
   * @param action If null then the action isn't changed. Only the mouse button listener is installed.
   *   The action should be given calling {@link GralWidget#setActionChange(GralUserAction)} then.
   *   If no action is given, The mouse listener has no effect.

   * @see org.vishia.gral.base.GralTextField#setMouseAction(org.vishia.gral.ifc.GralUserAction)
   */
  @Override public void setMouseAction(GralUserAction action)
  {
    if(action !=null){ setActionChange(action); }
    GralMouseWidgetAction_ifc actionMouse = null;  //TODO it should be the action.
    SwtGralMouseListener.MouseListenerUserAction mouseListener = new SwtGralMouseListener.MouseListenerUserAction(actionMouse);
    textFieldSwt.addMouseListener(mouseListener);  
  }
  
  @Override public Text getWidgetImplementation()
  { return textFieldSwt;
  }

  
  @Override public Label getPromptLabelImpl(){ return promptSwt; }


  @Override public GralColor setBackgroundColor(GralColor color)
  { return SwtWidgetHelper.setBackgroundColor(color, textFieldSwt);
  }
  

  @Override public GralColor setForegroundColor(GralColor color)
  { return SwtWidgetHelper.setForegroundColor(color, textFieldSwt);
  }
  
  
  @Override public void redraw(){  textFieldSwt.redraw(); textFieldSwt.update(); }

  @Override public boolean setFocus()
  { return SwtWidgetHelper.setFocusOfTabSwt(textFieldSwt);
  }


  @Override public void removeWidgetImplementation()
  {
    if(textFieldSwt !=null){
      textFieldSwt.dispose();
    } else {
      stop();
    }
    textFieldSwt = null;
    if(promptSwt !=null){
      promptSwt.dispose();
      promptSwt = null;
    }
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
