package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

public class AwtGralMouseListener
{
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
    
    /**The mouse-down action save some informations about the widget.
     * It may be overridden by an derived class, then this method should be invoked within.
     */
    @Override public void mousePressed(MouseEvent ev)
    {
      AwtWidget widget = (AwtWidget)ev.getComponent();
      Object oInfo = widget.getData();
      if(oInfo instanceof GralWidget){
        GralWidget widgetInfo = (GralWidget)oInfo;
        GralWidgetMng guiMng = widgetInfo.getMng();
        try{
          if(widgetInfo ==null || widgetInfo.sDataPath ==null || !widgetInfo.sDataPath.equals("widgetInfo")){
            guiMng.setLastClickedWidgetInfo(widgetInfo );
          }
          if(guiMng.bDesignMode){
            GralRectangle rr = new GralRectangle(ev.getX(), ev.getY(), 0, 0);
            if(ev.getButton() == 1){ //left
              xDown = ev.getX(); yDown = ev.getY();
              guiMng.pressedLeftMouseDownForDesign(widgetInfo, rr);  
            } else if(ev.getButton() == 3){ //right
              //guiMng.pressedRightMouseDownForDesign(widgetInfo, rr);
            }
          }
        } catch(Exception exc){ guiMng.writeLog(0, exc); }

    }
      
    }

    /**The mouse up is left empty. It may be overridden by an derived class. */
    @Override public void mouseReleased(MouseEvent ev)
    { AwtWidget widget = (AwtWidget)ev.getComponent();
      Object oInfo = widget.getData();
      if(oInfo instanceof GralWidget){
        GralWidget widgetInfo = (GralWidget)oInfo;
        GralWidgetMng guiMng = widgetInfo.getMng();
        try{
          GralWidget widgd = (GralWidget)oInfo;
          int dx = ev.getX() - xDown, dy = ev.getY() - yDown;
          if(dx < 10 && dx > -10 && dy < 10 && dy > -10){
            GralUserAction action = widgd.getActionChange();
            if(action !=null){
              action.userActionGui("lu", widgd);
            }
          } else if(guiMng.bDesignMode && ev.getButton() == 1){
            boolean bCopy = (ev.getModifiers() & 99999) !=0;
            GralRectangle rr = new GralRectangle(ev.getX(), ev.getY(), 0, 0);
            guiMng.releaseLeftMouseForDesign(widgd, rr, bCopy);  
          }
          //widgd.redraw();
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
        
      }
      
    }
    
    

    /**The mouse doubleclick is left empty. It may be overridden by an derived class. */
       @Override
    public void mouseClicked(MouseEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
      // TODO Auto-generated method stub
      
    }
      

    
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
    public void mouseClicked(MouseEvent ev) {
      xMousePress = ev.getX();
      yMousePress = ev.getY();
      AwtWidget widget = (AwtWidget)ev.getComponent();
      GralWidget widgg = (GralWidget)widget.getData();
      GralUserAction action = widgg ==null ? null : widgg.getActionChange();
      if(action !=null){
        action.userActionGui(KeyCode.mouse1Double, widgg);
      }
    }

    @Override public void mousePressed(MouseEvent e) {
      super.mousePressed(e);
      isPressed = true;
      xMousePress = e.getX();
      yMousePress = e.getY();
      Component widget = e.getComponent();
      AwtWidget widgetAwt = (AwtWidget)widget;
      widget.addMouseMotionListener(mouseMoveListener);
      GralWidget widgg = (GralWidget)widgetAwt.getData();
      GralWidgetMng guiMng = widgg.getMng();
      try{ 
        if(mouseWidgetAction !=null){
          switch(e.getButton()){ 
            case 1: mouseWidgetAction.mouse1Down();
            case 2: mouseWidgetAction.mouse2Down();
          }  
        }
        GralUserAction action = widgg ==null ? null : widgg.getActionChange();
        if(action !=null){
          final int keyCode;
          switch(e.getButton()){ 
            case 1: keyCode = KeyCode.mouse1Down; break; 
            case 2: keyCode = KeyCode.mouse2Down; break;
            case 3: keyCode = KeyCode.mouse3Down; break;
            default: keyCode = KeyCode.mouse3Down; break;  //other key
          }
          action.userActionGui(keyCode, widgg);
        }
      } catch(Exception exc){ guiMng.writeLog(0, exc); }
    }

    
    
    
    @Override public void mouseReleased(MouseEvent e) {
      //set the background color to the originally value again if it was changed.
      super.mouseReleased(e);
      if(isPressed){
        Component widget = e.getComponent();
        AwtWidget widgetAwt = (AwtWidget)widget;
        widget.removeMouseMotionListener(mouseMoveListener);
        isPressed = false;
        if(mouseWidgetAction !=null){
          switch(e.getButton()){ 
            case 1: mouseWidgetAction.mouse1Up(); break;
            case 2: mouseWidgetAction.mouse2Up(); break;
          }  
        }
        backgroundWhilePressed = null;
        GralWidget widgg = (GralWidget)widgetAwt.getData();
        GralWidgetMng guiMng = widgg.getMng();
        try{ 
          GralUserAction action = widgg ==null ? null : widgg.getActionChange();
          if(action !=null){
            final int keyCode;
            switch(e.getButton()){ 
              case 1: keyCode = KeyCode.mouse1Up; break; 
              case 2: keyCode = KeyCode.mouse2Up; break;
              case 3: keyCode = KeyCode.mouse3Up; break;
              default: keyCode = KeyCode.mouse3Up; break;  //other key
            }
            action.userActionGui(keyCode, widgg);
          }
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
        widgg.redraw();
      }
    }

    protected MouseMotionListener mouseMoveListener = new MouseMotionListener()
    {

      @Override public void mouseMoved(MouseEvent e)
      {
        if(e.getComponent() instanceof AwtWidget){
          Component widget = e.getComponent();
          AwtWidget widgetAwt = (AwtWidget)widget;
          Rectangle size = widget.getBounds();
          //xSize = size.x; ySize = size.y;
          if(  e.getX() < 0 || e.getX() > size.width
            || e.getY() < 0 || e.getY() > size.height
            ){
            isPressed = false;
            widget.removeMouseMotionListener(mouseMoveListener);
            if(mouseWidgetAction !=null){
              mouseWidgetAction.removeMouseCursorFromWidgetWhilePressed();
            }
          }
        } 
      }//method mouseMove

      @Override
      public void mouseDragged(MouseEvent e)
      {
        // TODO Auto-generated method stub
        
      }
    };
    

  }


  
  
}
