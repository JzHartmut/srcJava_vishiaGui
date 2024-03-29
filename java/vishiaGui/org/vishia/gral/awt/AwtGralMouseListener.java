package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

/**Common used {@link MouseListener} for all gral widgets.
 * 
 * @author Hartmut Schorrig
 *
 */
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
        GralMng guiMng = widgetInfo.gralMng();
        try{
          if(widgetInfo ==null || widgetInfo.getDataPath() ==null || !widgetInfo.getDataPath().equals("widgetInfo")){
            guiMng.setLastClickedWidget(widgetInfo );
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
        GralMng guiMng = widgetInfo.gralMng();
        try{
          GralWidget widgd = (GralWidget)oInfo;
          int dx = ev.getX() - xDown, dy = ev.getY() - yDown;
          if(dx < 10 && dx > -10 && dy < 10 && dy > -10){
            GralWidget_ifc.ActionChange action = widgd.getActionChange(GralWidget_ifc.ActionChangeWhen.onMouse1Up); 
            if(action !=null){
              Object[] args = action.args();
              if(args == null){ action.action().exec(KeyCode.mouse1Up, widgd, new Integer(ev.getX()), new Integer(ev.getY())); }
              else { 
                //additional 2 arguments: copy in one args2.
                Object[] args2 = new Object[args.length +2];
                System.arraycopy(args, 0, args2, 0, args.length);
                args2[args.length] = new Integer(ev.getX());
                args2[args.length+1] = new Integer(ev.getY());
                action.action().exec(KeyCode.mouse1Up, widgd, args2); 
              }
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
 

  
  
  
  
  public static class MouseListenerGralAction extends MouseListenerNoAction
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
    protected boolean isPressed;
    
    
    
    protected final GralMouseWidgetAction_ifc mouseWidgetAction;
    
    
    /**Constructor.
     * @param guiMng The Gui-manager
     * @param userCmdGui The users method for the action. 
     * @param sCmdPress command string provided as first parameter on mouse button press.
     * @param sCmdRelease
     * @param sCmdDoubleClick
     */
    public MouseListenerGralAction(GralMouseWidgetAction_ifc mouseWidgetAction)
    { super();
      this.mouseWidgetAction = mouseWidgetAction;
    }
    
    

    @Override
    public void mouseClicked(MouseEvent ev) {
      xMousePress = ev.getX();
      yMousePress = ev.getY();
      AwtWidget widget = (AwtWidget)ev.getComponent();
      GralWidget widgg = (GralWidget)widget.getData();
      GralWidget_ifc.ActionChange action = widgg.getActionChange(GralWidget_ifc.ActionChangeWhen.onMouse1Double); 
      if(action !=null){
        Object[] args = action.args();
        if(args == null){ action.action().exec(KeyCode.mouse1Double, widgg, new Integer(ev.getX()), new Integer(ev.getY())); }
        else { 
          //additional 2 arguments: copy in one args2.
          Object[] args2 = new Object[args.length +2];
          System.arraycopy(args, 0, args2, 0, args.length);
          args2[args.length] = new Integer(ev.getX());
          args2[args.length+1] = new Integer(ev.getY());
          action.action().exec(KeyCode.mouse1Double, widgg, args2); 
        }
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
      Object oData = widgetAwt.getData();
      GralWidget widgg = (GralWidget)oData;  //maybe null
      Dimension size = widget.getSize();
      try{ 
        final int keyCode;
        switch(e.getButton()){ 
          case 1: keyCode = KeyCode.mouse1Down; break; 
          case 2: keyCode = KeyCode.mouse2Down; break;
          case 3: keyCode = KeyCode.mouse3Down; break;
          default: keyCode = KeyCode.mouse3Down; break;  //other key
        }
        if(mouseWidgetAction !=null){
          switch(e.getButton()){ 
            case 1: mouseWidgetAction.mouse1Down(keyCode, xMousePress, yMousePress, size.width, size.height, widgg); break;
            case 2: mouseWidgetAction.mouse2Down(keyCode, xMousePress, yMousePress, size.width, size.height, widgg); break;
          }  
        }
        GralWidget_ifc.ActionChange action = widgg.getActionChange(null); 
        if(action !=null){
          Object[] args = action.args();
          if(args == null){ action.action().exec(keyCode, widgg, new Integer(e.getX()), new Integer(e.getY())); }
          else { 
            //additional 2 arguments: copy in one args2.
            Object[] args2 = new Object[args.length +2];
            System.arraycopy(args, 0, args2, 0, args.length);
            args2[args.length] = new Integer(e.getX());
            args2[args.length+1] = new Integer(e.getY());
            action.action().exec(keyCode, widgg, args2); 
          }
        }
      } catch(Exception exc){ System.err.printf("AwtGralMouseListener - any exception while mouse down; %s\n", exc.getMessage()); }
    }

    
    
    
    @Override public void mouseReleased(MouseEvent e) {
      //set the background color to the originally value again if it was changed.
      super.mouseReleased(e);
      if(isPressed){
        Component widget = e.getComponent();
        AwtWidget widgetAwt = (AwtWidget)widget;
        Dimension size = widget.getSize();
        GralWidget widgg = (GralWidget)widgetAwt.getData();
        widget.removeMouseMotionListener(mouseMoveListener);
        isPressed = false;
        final int keyCode;
        GralWidget_ifc.ActionChangeWhen whenAction;
        switch(e.getButton()){ 
          case 1: keyCode = KeyCode.mouse1Up; whenAction = GralWidget_ifc.ActionChangeWhen.onMouse1Up; break; 
          case 2: keyCode = KeyCode.mouse2Up; whenAction = GralWidget_ifc.ActionChangeWhen.onMouse2Up; break;
          case 3: keyCode = KeyCode.mouse3Up; whenAction = null; break;
          default: keyCode = KeyCode.mouse3Up; whenAction = null; break;  //other key
        }
        if(mouseWidgetAction !=null){
          switch(e.getButton()){ 
            case 1: mouseWidgetAction.mouse1Up(keyCode, e.getX(), e.getY(), size.width, size.height, widgg); break;
            case 2: mouseWidgetAction.mouse2Up(keyCode, e.getX(), e.getY(), size.width, size.height, widgg); break;
          }  
        }
        backgroundWhilePressed = null;
        try{ 
          GralWidget_ifc.ActionChange action = widgg.getActionChange(whenAction); 
          if(action !=null){
            Object[] args = action.args();
            if(args == null){ action.action().exec(keyCode, widgg); }
            else { action.action().exec(keyCode, widgg, args); }
          }
        } catch(Exception exc){ System.err.printf("SwtGralMouseListener - any exception while mouse down; %s\n", exc.getMessage()); }
        //widgg.repaint();
      }
    }

    protected MouseMotionListener mouseMoveListener = new MouseMotionListener()
    {

      @Override public void mouseMoved(MouseEvent e)
      {
      }//method mouseMoved

      @Override
      public void mouseDragged(MouseEvent e)
      {
        if(e.getComponent() instanceof AwtWidget){
          Component widget = e.getComponent();
          AwtWidget widgetAwt = (AwtWidget)widget;
          Rectangle size = widget.getBounds();
          if(mouseWidgetAction !=null){
            if(!mouseWidgetAction.mouseMoved(e.getX(), e.getY(), size.x, size.y)){
              isPressed = false;
              widget.removeMouseMotionListener(mouseMoveListener);
            }
            mouseWidgetAction.mouseMoved(e.getX(), e.getY(), size.x, size.y);
            //mouseWidgetAction.removeMouseCursorFromWidgetWhilePressed();
          }
        } 
      }//method mouseDragged
    };
    

  }


  
  
}
