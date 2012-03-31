package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

/**This class contains the functionality of mouse button and move listening for Gral adaption of SWT.
 * The static inner classes implements the MouseListener
 * @author Hartmut Schorrig
 *
 */
public class SwtGralMouseListener
{
  /**Version, History and copyright
   * <ul>
   * <li>2012-03-09 Hartmut The methods {@link GralMouseWidgetAction_ifc#mouse1Up()} etc. are not called
   *   if the cursor was removed from the widget.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120309;


  /**This class implements a MouseListener which does not call a user method.
   * Only the information about the clicked widget are stored in the GralMng
   * and the Gral designer is supported.
   * 
   */
  public static class MouseListenerNoAction implements MouseListener
  {

    
    public MouseListenerNoAction()
    {
    }

    int xDown, yDown;
    
    /**The mouse doubleclick is left empty. It may be overridden by an derived class. */
    @Override public void mouseDoubleClick(MouseEvent arg0)
    {
      Widget widget = arg0.widget;
      Object oInfo = widget.getData();
      if(oInfo instanceof GralWidget){
        GralWidget widgetInfo = (GralWidget)oInfo;
        GralWidgetMng guiMng = widgetInfo.getMng();
        try{
          guiMng.log.sendMsg(0, "Info widget: %s / %s", widgetInfo.name, widgetInfo.getDataPath());
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
          
      }
      
    }

    /**The mouse-down action save some informations about the widget.
     * It may be overridden by an derived class, then this method should be invoked within.
     */
    @Override public void mouseDown(MouseEvent ev)
    {
      Widget widget = ev.widget;
      Object oInfo = widget.getData();
      if(oInfo instanceof GralWidget){
        GralWidget widgetInfo = (GralWidget)oInfo;
        GralWidgetMng guiMng = widgetInfo.getMng();
        try{
          String sDataPath = widgetInfo.getDataPath();
          if( sDataPath ==null  //no datapath given, write info! 
            || !sDataPath.equals("widgetInfo")  //don't write info if it is a widgetInfo widget itself.
            ){
            guiMng.setLastClickedWidgetInfo(widgetInfo );
          }
          if(guiMng.bDesignMode){
            GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
            if(ev.button == 1){ //left
              xDown = ev.x; yDown = ev.y;
              guiMng.pressedLeftMouseDownForDesign(widgetInfo, rr);  
            } else if(ev.button == 3){ //right
              //guiMng.pressedRightMouseDownForDesign(widgetInfo, rr);
            }
          }
        } catch(Exception exc){ guiMng.writeLog(0, exc); }

    }
      
    }

    /**The default behaviour for mouse up is used for design mode. */
    @Override public void mouseUp(MouseEvent ev)
  {   Widget widget = ev.widget;
      Object oInfo = widget.getData();
      if(oInfo instanceof GralWidget){
        GralWidget widgetInfo = (GralWidget)oInfo;
        GralWidgetMng guiMng = widgetInfo.getMng();
        try{
          GralWidget widgd = (GralWidget)oInfo;
          int dx = ev.x - xDown, dy = ev.y - yDown;
          if(guiMng.bDesignMode && ev.button == 1){
            if((ev.stateMask & SWT.ALT)!=0){
              boolean bCopy = (ev.stateMask & org.eclipse.swt.SWT.CTRL) !=0;
              GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
              guiMng.releaseLeftMouseForDesign(widgd, rr, bCopy);  
            } else {
              guiMng.markWidgetForDesign(widgd);
            }
          } 
          //widgd.redraw();
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
        
      }
      
    }
    
    
    private MouseMoveListener mouseMoveListenerDesignMode = new MouseMoveListener()
    {
      
      @Override public void mouseMove(MouseEvent e)
      {
        //xMouse = e.x;
        //yMouse = e.y;
      }//method mouseMove
    };
      

    
  }
 

  
  
  
  
  public static class MouseListenerUserAction extends MouseListenerNoAction
  implements MouseListener
  {
    
    /**Positions saved on mouse press, to detect whether the mouse-release occurs in the pressed area.
     * If the mouse-position is shifted outside the area of the widget, the mouse-release-user-action
     * is not executed.
     */
    private int xMousePress, yMousePress;
    
    private Color backgroundWhilePressed;
    
    /**Used in the implementation level for the paint routine. Therefore it is package private.
     */
    boolean isPressed;
    
    
    
    /**A standard action for a specific widget for example button, which is executed
     * independently and additional to the user action. */
    private final GralMouseWidgetAction_ifc mouseWidgetAction;
    
    
    /**Constructor.
     * @param guiMng The Gui-manager
     * @param userCmdGui The users method for the action. 
     * @param sCmdPress command string provided as first parameter on mouse button press.
     * @param sCmdRelease
     * @param sCmdDoubleClick
     */
    public MouseListenerUserAction(GralMouseWidgetAction_ifc mouseWidgetAction)
    { super();
      this.mouseWidgetAction = mouseWidgetAction;
    }
    
    

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      xMousePress = e.x;
      yMousePress = e.y;
      Control widget = (Control) e.widget;  //a widget is a Control always.
      GralWidget widgg = (GralWidget)widget.getData();
      GralUserAction action = widgg ==null ? null : widgg.getActionChange();
      if(action !=null){
        action.userActionGui(KeyCode.mouse1Double, widgg);
      }
    }

    @Override public void mouseDown(MouseEvent e) {
      super.mouseDown(e);
      isPressed = true;
      xMousePress = e.x;
      yMousePress = e.y;
      Control widget = (Control) e.widget;  //a widget is a Control always.
      widget.addMouseMoveListener(mouseMoveListener);
      GralWidget widgg = (GralWidget)widget.getData();
      GralWidgetMng guiMng = widgg.getMng();
      try{ 
        if(mouseWidgetAction !=null){
          switch(e.button){ 
            case 1: mouseWidgetAction.mouse1Down(); break;
            case 2: mouseWidgetAction.mouse2Down(); break;
          }  
        }
        GralUserAction action = widgg ==null ? null : widgg.getActionChange();
        if(action !=null){
          final int keyCode;
          switch(e.button){ 
            case 1: keyCode = KeyCode.mouse1Down; break; 
            case 2: keyCode = KeyCode.mouse2Down; break;
            case 3: keyCode = KeyCode.mouse3Down; break;
            default: keyCode = KeyCode.mouse3Down; break;  //other key
          }
          action.userActionGui(keyCode, widgg);
        }
      } catch(Exception exc){ guiMng.writeLog(0, exc); }
    }

    
    
    
    @Override public void mouseUp(MouseEvent e) {
      //set the background color to the originally value again if it was changed.
      super.mouseUp(e);
      //if(isPressed){
        Control widget = (Control)e.widget;
        widget.removeMouseMoveListener(mouseMoveListener);
        isPressed = false;
        backgroundWhilePressed = null;
        GralWidget widgg = (GralWidget)widget.getData();
        GralWidgetMng guiMng = widgg.getMng();
        try{ 
          int dx = e.x - xMousePress, dy = e.y - yMousePress;
          final int keyCode;
          int moved = (dx < 10 && dx > -10 && dy < 10 && dy > -10) ? 0: 100;
          switch(e.button + moved){ 
            case   1: keyCode = KeyCode.mouse1Up; break; 
            case 101: keyCode = KeyCode.mouse1UpMoved; break; 
            case   2: keyCode = KeyCode.mouse2Up; break;
            case 102: keyCode = KeyCode.mouse2UpMoved; break;
            case   3: keyCode = KeyCode.mouse3Up; break;
            case 103: keyCode = KeyCode.mouse3Up; break;
            default: keyCode = KeyCode.mouse3Up; break;  //other key
          }
          //TODO add capture ctrl, alt, sh 
          if(mouseWidgetAction !=null){
            switch(e.button + moved){ 
              case 1: mouseWidgetAction.mouse1Up(); break;
              case 2: mouseWidgetAction.mouse2Up(); break;
            }  
          }
          GralUserAction action = widgg ==null ? null : widgg.getActionChange();
          if(action !=null){
              action.userActionGui(keyCode, widgg);
          }
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
        widgg.repaint();
      //}
    }

    protected MouseMoveListener mouseMoveListener = new MouseMoveListener()
    {

      @Override public void mouseMove(MouseEvent e)
      {
        if(e.widget instanceof Control){
          Control widget = (Control)e.widget;
          Point size = widget.getSize();
          //xSize = size.x; ySize = size.y;
          if(  e.x < 0 || e.x > size.x
            || e.y < 0 || e.y > size.y
            ){
            isPressed = false;
            widget.removeMouseMoveListener(mouseMoveListener);
            if(mouseWidgetAction !=null){
              mouseWidgetAction.removeMouseCursorFromWidgetWhilePressed();
            }
          }
        } 
      }//method mouseMove
    };
    

  }


  
}
