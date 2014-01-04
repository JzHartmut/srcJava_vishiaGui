package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.Assert;
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
   * <li>2013-05-13 Hartmut chg: All methods of {@link GralMouseWidgetAction_ifc} changed, parameter key, position.
   * <li>2012-10-10 Hartmut the keycode for mouse pressed user actions contains pressing ctrl, alt, sh too.
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
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(oInfo);
      if(widgg !=null){
        GralMng guiMng = widgg.gralMng();
        try{
          guiMng.log.sendMsg(0, "Info widget: %s / %s", widgg.name, widgg.getDataPath());
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
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(oInfo);
      if(widgg !=null){
        GralMng guiMng = widgg.gralMng();
        try{
          String sDataPath = widgg.getDataPath();
          if( sDataPath ==null  //no datapath given, write info! 
            || !sDataPath.equals("widgetInfo")  //don't write info if it is a widgetInfo widget itself.
            ){
            guiMng.setLastClickedWidget(widgg );
          }
          if(guiMng.bDesignMode){
            GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
            if(ev.button == 1){ //left
              xDown = ev.x; yDown = ev.y;
              guiMng.pressedLeftMouseDownForDesign(widgg, rr);  
            } else if(ev.button == 3){ //right
              //guiMng.pressedRightMouseDownForDesign(widgetInfo, rr);
            }
          }
        } catch(Exception exc){ guiMng.writeLog(0, exc); }

    }
      
    }

    /**The default behavior for mouse up is used for design mode. */
    @Override public void mouseUp(MouseEvent ev)
  {   Widget widget = ev.widget;
      Object oInfo = widget.getData();
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(oInfo);
      if(widgg !=null){
        GralMng guiMng = widgg.gralMng();
        try{
          int dx = ev.x - xDown, dy = ev.y - yDown;
          if(guiMng.bDesignMode && ev.button == 1){
            if((ev.stateMask & SWT.ALT)!=0){
              boolean bCopy = (ev.stateMask & org.eclipse.swt.SWT.CTRL) !=0;
              GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
              guiMng.releaseLeftMouseForDesign(widgg, rr, bCopy);  
            } else {
              guiMng.markWidgetForDesign(widgg);
            }
          } 
          //widgd.redraw();
        } catch(Exception exc){ guiMng.writeLog(0, exc); }
        
      }
      
    }
    
    
    private final MouseMoveListener mouseMoveListenerDesignMode = new MouseMoveListener()
    {
      
      @Override public void mouseMove(MouseEvent e)
      {
        //xMouse = e.x;
        //yMouse = e.y;
      }//method mouseMove
    };
      

    
  }
 

  
  
  
  
  public static class MouseListenerGralAction extends MouseListenerNoAction
  implements MouseListener
  {
    
    /**Positions saved on mouse press, to detect whether the mouse-release occurs in the pressed area.
     * If the mouse-position is shifted outside the area of the widget, the mouse-release-user-action
     * is not executed.
     */
    private int xMousePress, yMousePress;
    
    /**Used in the implementation level for the paint routine. Therefore it is package private.
     */
    protected boolean isPressed;
    
    
    
    /**A standard action for a specific widget for example button, which is executed
     * independently and additional to the user action. */
    protected final GralMouseWidgetAction_ifc mouseWidgetAction;
    
    
    /**Bits see {@link GralMouseWidgetAction_ifc#mUser1down} etc. */
    final protected int mUser;
    
    /**Constructor.
     * @param mouseWidgetAction Action invoked, maybe null
     * @param mUser 0 or or-combinations of bits in {@link GralMouseWidgetAction_ifc#mUser1down} 
     *   and all other mUser... If one of this bits is set, the {@link GralWidget#setActionChange(GralUserAction)}
     *   is invoked on the appropriate mouse action after and independent of the mouseWidgetAction.
     */
    public MouseListenerGralAction(GralMouseWidgetAction_ifc mouseWidgetAction, int mUser)
    { super();
      this.mouseWidgetAction = mouseWidgetAction;
      this.mUser = mUser;
    }
    
    

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      xMousePress = e.x;
      yMousePress = e.y;
      Control widget = (Control) e.widget;  //a widget is a Control always.
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
      try{
        final int keyCode = SwtGralKey.convertMouseKey(e.button, SwtGralKey.MouseAction.doubleClick, e.stateMask);
        Control widgetSwt = (Control) e.widget;  //a widget is a Control always.
        if(mouseWidgetAction !=null){
          Point size = widgetSwt.getSize();
          mouseWidgetAction.mouse1Double(keyCode, xMousePress, yMousePress, size.x, size.y, widgg);
        } 
        if( (mUser & GralMouseWidgetAction_ifc.mUserDouble) !=0) {
          GralUserAction action = widgg ==null ? null : widgg.getActionChange();
          if(action !=null){
            action.exec(keyCode, widgg, new Integer(e.x), new Integer(e.y));
          }
        }
      } catch(Exception exc){ System.err.printf("SwtGralMouseListener - any exception while mouse double; %s\n", exc.getMessage()); }
    }

    @Override public void mouseDown(MouseEvent e) {
      super.mouseDown(e);
      isPressed = true;
      xMousePress = e.x;
      yMousePress = e.y;
      Control widget = (Control) e.widget;  //a widget is a Control always.
      widget.addMouseMoveListener(mouseMoveListener);
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
      try{ 
        final int keyCode = SwtGralKey.convertMouseKey(e.button, SwtGralKey.MouseAction.down, e.stateMask);
        int mUser1 = 0;
        if(mouseWidgetAction !=null){
          Point size = widget.getSize();
          switch(e.button){ 
            case 1: 
              mUser1 = GralMouseWidgetAction_ifc.mUser1down; 
              mouseWidgetAction.mouse1Down(keyCode, xMousePress, yMousePress, size.x, size.y, widgg); 
              break;
            case 2: 
              mUser1 = GralMouseWidgetAction_ifc.mUser1down; 
              mouseWidgetAction.mouse2Down(keyCode, xMousePress, yMousePress, size.x, size.y, widgg); 
              break;
          }  
        } 
        if( (mUser & mUser1) !=0) {
          GralUserAction action = widgg ==null ? null : widgg.getActionChange();
          if(action !=null){
            action.exec(keyCode, widgg, new Integer(e.x), new Integer(e.y));
          }
        }
      } catch(Exception exc){ System.err.printf("SwtGralMouseListener - any exception while mouse down; %s\n", exc.getMessage()); }
    }

    
    
    
    @Override public void mouseUp(MouseEvent e) {
      //set the background color to the originally value again if it was changed.
      super.mouseUp(e);
      if(isPressed){  //prevent any action of mouse up if the mouse is not designated as pressed
        //especially if the mouse was removed from the widget.
        Control widget = (Control)e.widget;
        widget.removeMouseMoveListener(mouseMoveListener);
        isPressed = false;
        Point size = widget.getSize();
        GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
        try{ 
          //int dx = e.x - xMousePress, dy = e.y - yMousePress;
          boolean moved = e.x < 0 || e.x > size.x || e.y < 0 || e.y > size.y;
          SwtGralKey.MouseAction mouseAction = moved ? SwtGralKey.MouseAction.upMovedOutside : SwtGralKey.MouseAction.up;
          final int keyCode = SwtGralKey.convertMouseKey(e.button, mouseAction, e.stateMask);
          int mUser1 = 0;
          if(mouseWidgetAction !=null){
            switch(e.button){ 
              case 1: 
                mUser1 = GralMouseWidgetAction_ifc.mUser1up; 
                mouseWidgetAction.mouse1Up(keyCode, e.x, e.y, size.x, size.y, widgg); 
                break;
              case 2:
                mUser1 = GralMouseWidgetAction_ifc.mUser2up; 
                mouseWidgetAction.mouse2Up(keyCode, e.x, e.y, size.x, size.y, widgg); 
                break;
            }  
          } 
          if( (mUser & mUser1) !=0) {
            GralUserAction action = widgg ==null ? null : widgg.getActionChange();
            if(action !=null){
              action.exec(keyCode, widgg);
            }
          }
        } catch(Exception exc){ 
          CharSequence text = Assert.exceptionInfo("SwtGralMouseListener - any exception while mouse down;", exc, 0, 20);
          //System.err.append(text).append('\n'); 
          System.err.printf(text.toString()); 
        }
      }
    }

    
    
    
    /**TODO invoke user action if mouse releases the area
     * 
     */
    protected MouseMoveListener mouseMoveListener = new MouseMoveListener()
    {

      @Override public void mouseMove(MouseEvent e)
      {
        if(e.widget instanceof Control){
          Control widget = (Control)e.widget;
          Point size = widget.getSize();
          if(mouseWidgetAction !=null){
            if(!mouseWidgetAction.mouseMoved(e.x, e.y, size.x, size.y)){
              isPressed = false;
              widget.removeMouseMoveListener(mouseMoveListener);
            }
            //mouseWidgetAction.removeMouseCursorFromWidgetWhilePressed();
          }
        } 
      }//method mouseMove
    };
    

  }


  
}
