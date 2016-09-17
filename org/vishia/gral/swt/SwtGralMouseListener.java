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
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;

/**This class contains implementations of {@link MouseListener} for Gral adaption of SWT.
 * The static inner classes implements the MouseListener
 * @author Hartmut Schorrig
 *
 */
public final class SwtGralMouseListener
{
  /**Version, History and copyright
   * <ul>
   * <li>2015-09-21 Hartmut chg: The capabilities of {@link MouseListenerNoAction} is contained in {@link MouseListenerGralAction} yet.
   *   Therefore it is possible to get a mouse action in any {@link GralWidget#setActionChange(GralUserAction)}.
   * <li>2014-09-21 Hartmut chg: Using of {@link GralWidget#toString()} for double click info.
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
  private static class MouseListenerNoAction implements MouseListener
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
          String widggInfo = widgg.toString();
          guiMng.log.sendMsg(0, "Info widget: %s", widggInfo);
          //guiMng.log.sendMsg(0, "Info widget: %s / %s", widgg.name, widgg.getDataPath());
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
          if( sDataPath !=null  //no datapath given, write info! 
            && !sDataPath.equals("widgetInfo")  //don't write info if it is a widgetInfo widget itself.
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
 

  
  
  
  
  /**This class is the implementation of a SWT {@link MouseListener} and  implements methods which invokes the
   * {@link GralWidget#setActionMouse(GralMouseWidgetAction_ifc, int)} or the {@link GralWidget#setActionChange(GralUserAction)}
   * on the determined mouse clicks. 
   * @author Hartmut Schorrig
   *
   */
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
    
    
    
    /**Constructor.
     * @param mouseWidgetAction Action invoked, maybe null
     * @param mUser 0 or or-combinations of bits in {@link GralMouseWidgetAction_ifc#mUser1down} 
     *   and all other mUser... If one of this bits is set, the {@link GralWidget#setActionChange(GralUserAction)}
     *   is invoked on the appropriate mouse action after and independent of the mouseWidgetAction.
     */
    public MouseListenerGralAction()
    { super();
    }
    
    

    @Override
    public void mouseDoubleClick(MouseEvent e) {
      xMousePress = e.x;
      yMousePress = e.y;
      Control widget = (Control) e.widget;  //a widget is a Control always.
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
      try{
        String widggInfo = widgg.toString();
        GralMng.get().log.sendMsg(0, "Info widget: %s", widggInfo);
        //guiMng.log.sendMsg(0, "Info widget: %s / %s", widgg.name, widgg.getDataPath());
      } catch(Exception exc){ GralMng.get().writeLog(0, exc); }
      try{
        final int keyCode = SwtGralKey.convertMouseKey(e.button, SwtGralKey.MouseAction.doubleClick, e.stateMask);
        Control widgetSwt = (Control) e.widget;  //a widget is a Control always.
        if(widgg.cfg.mouseWidgetAction !=null){
          Point size = widgetSwt.getSize();
          widgg.cfg.mouseWidgetAction.mouse1Double(keyCode, xMousePress, yMousePress, size.x, size.y, widgg);
        } 
        boolean bStrict = (widgg.cfg.mMouseToActionChange & GralMouseWidgetAction_ifc.mUserDouble) ==0;  //search strict if no special bit is given.
        //then a strict mouse doube action wins.
        GralWidget_ifc.ActionChange action = widgg.getActionChangeStrict(GralWidget_ifc.ActionChangeWhen.onMouse1Double, bStrict); 
        if(action !=null){
          Object[] args = action.args();
          if(args == null){ action.action().exec(KeyCode.mouse1Double, widgg, new Integer(e.x), new Integer(e.y)); }
          else { 
            //additional 2 arguments: copy in one args2.
            Object[] args2 = new Object[args.length +2];
            System.arraycopy(args, 0, args2, 0, args.length);
            args2[args.length] = new Integer(e.x);
            args2[args.length+1] = new Integer(e.y);
            action.action().exec(KeyCode.mouse1Double, widgg, args2); 
          }
        }
      } catch(Exception exc){ System.err.printf("SwtGralMouseListener - any exception while mouse double; %s\n", exc.getMessage()); }
    }

    @Override public void mouseDown(MouseEvent ev) {
      super.mouseDown(ev);
      isPressed = true;
      xMousePress = ev.x;
      yMousePress = ev.y;
      Control widget = (Control) ev.widget;  //a widget is a Control always.
      widget.addMouseMoveListener(mouseMoveListener);
      Object owdgg = widget.getData();
      GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(owdgg);
      GralMng guiMng = widgg.gralMng();
      try{
        String sDataPath = widgg.getDataPath();
        if( sDataPath !=null  //no datapath given, write info! 
          && !sDataPath.equals("widgetInfo")  //don't write info if it is a widgetInfo widget itself.
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
      try{ 
        final int keyCode = SwtGralKey.convertMouseKey(ev.button, SwtGralKey.MouseAction.down, ev.stateMask);
        GralWidget_ifc.ActionChangeWhen whenAction;
        final int mUser1;
        switch(ev.button){
          case 1: mUser1 = GralMouseWidgetAction_ifc.mUser1down; whenAction = GralWidget_ifc.ActionChangeWhen.onMouse1Dn; break;
          case 3: mUser1 = GralMouseWidgetAction_ifc.mUser2down; whenAction = null; break;  //the usual right button is 3 in SWT!
          case 2: mUser1 = GralMouseWidgetAction_ifc.mUser3down; whenAction = null; break;  //the usual middle button is 2 in SWT!
          default: mUser1 = 0; whenAction = null; break;
        }//switch:
        if(widgg.cfg.mouseWidgetAction !=null){
          Point size = widget.getSize();
          switch(ev.button){ 
            case 1: 
              widgg.cfg.mouseWidgetAction.mouse1Down(keyCode, xMousePress, yMousePress, size.x, size.y, widgg); 
              break;
            case 3: 
              widgg.cfg.mouseWidgetAction.mouse2Down(keyCode, xMousePress, yMousePress, size.x, size.y, widgg); 
              break;
          }  
        }
        GralWidget_ifc.ActionChange action = widgg.getActionChangeStrict(whenAction, true); 
        if(action !=null){
          Object[] args = action.args();
          if(args == null){ action.action().exec(keyCode, widgg, new Integer(ev.x), new Integer(ev.y)); }
          else { 
            //additional 2 arguments: copy in one args2.
            Object[] args2 = new Object[args.length +2];
            System.arraycopy(args, 0, args2, 0, args.length);
            args2[args.length] = new Integer(ev.x);
            args2[args.length+1] = new Integer(ev.y);
            action.action().exec(keyCode, widgg, args2); 
          }
        } 
      } catch(Exception exc){ System.err.printf("SwtGralMouseListener - any exception while mouse down; %s\n", exc.getMessage()); }
    }

    
    
    
    @Override public void mouseUp(MouseEvent ev) {
      //set the background color to the originally value again if it was changed.
      super.mouseUp(ev);
      if(isPressed){  //prevent any action of mouse up if the mouse is not designated as pressed
        //especially if the mouse was removed from the widget.
        Control widget = (Control)ev.widget;
        widget.removeMouseMoveListener(mouseMoveListener);
        isPressed = false;
        Point size = widget.getSize();
        GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
        final int mUser1;
        GralWidget_ifc.ActionChangeWhen whenAction;
        switch(ev.button){
          case 1: mUser1 = GralMouseWidgetAction_ifc.mUser1up; whenAction = GralWidget_ifc.ActionChangeWhen.onMouse1Up; break;
          case 3: mUser1 = GralMouseWidgetAction_ifc.mUser2up; whenAction = GralWidget_ifc.ActionChangeWhen.onMouse2Up; break;  //the usual right button is 3 in SWT!
          case 2: mUser1 = GralMouseWidgetAction_ifc.mUser3up; whenAction = null; break;  //the usual middle button is 2 in SWT!
          default: mUser1 = 0; whenAction = null; break;
        }//switch:
        try{ 
          //int dx = e.x - xMousePress, dy = e.y - yMousePress;
          boolean moved = ev.x < 0 || ev.x > size.x || ev.y < 0 || ev.y > size.y;
          SwtGralKey.MouseAction mouseAction = moved ? SwtGralKey.MouseAction.upMovedOutside : SwtGralKey.MouseAction.up;
          final int keyCode = SwtGralKey.convertMouseKey(ev.button, mouseAction, ev.stateMask);
                  GralMng guiMng = widgg.gralMng();
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

          if(widgg.cfg.mouseWidgetAction !=null){
            switch(ev.button){ 
              case 1: 
                widgg.cfg.mouseWidgetAction.mouse1Up(keyCode, ev.x, ev.y, size.x, size.y, widgg); 
                break;
              case 3:
                widgg.cfg.mouseWidgetAction.mouse2Up(keyCode, ev.x, ev.y, size.x, size.y, widgg); 
                break;
            }  
          } 
          GralWidget_ifc.ActionChange action = widgg.getActionChangeStrict(whenAction, (widgg.cfg.mMouseToActionChange & mUser1) !=0); 
          if(action !=null){
            Object[] args = action.args();
            if(args == null){ action.action().exec(keyCode, widgg, new Integer(ev.x), new Integer(ev.y)); }
            else { 
              //additional 2 arguments: copy in one args2.
              Object[] args2 = new Object[args.length +2];
              System.arraycopy(args, 0, args2, 0, args.length);
              args2[args.length] = new Integer(ev.x);
              args2[args.length+1] = new Integer(ev.y);
              action.action().exec(keyCode, widgg, args2); 
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
          GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(widget.getData());
          Point size = widget.getSize();
          if(widgg.cfg.mouseWidgetAction !=null){
            if(!widgg.cfg.mouseWidgetAction.mouseMoved(e.x, e.y, size.x, size.y)){
              isPressed = false;
              widget.removeMouseMoveListener(mouseMoveListener);
            }
            //mouseWidgetAction.removeMouseCursorFromWidgetWhilePressed();
          }
        } 
      }//method mouseMove
    };
    

  }



  /**The only one instance can used for all widgets because the working data are given with the mouse action methods.
   * The associated GralWidget data are accessed via the data field (the GralWidget is known in all Widget implementations).
   */
  protected static MouseListener mouseActionStd = new MouseListenerGralAction();
  
}
