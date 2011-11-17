package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
public class SwtSubWindow extends GralWindow
{

  protected Shell window;
  
  
  
  
  SwtSubWindow(String name, Display display, String title, int windProps, GralWidgetMng gralMng)
  { super(name, gralMng, null);
    int props = 0; ////|SWT.CLOSE;
    if(title !=null){ props |= SWT.TITLE | SWT.BORDER; }
    //if(exclusive){ props |= SWT.PRIMARY_MODAL | SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL; }
    if((windProps & GralWindow.windExclusive) !=0){ 
      assert((windProps & GralWindow.windConcurrently) ==0);
      props |= SWT.APPLICATION_MODAL; 
    } else {
      assert((windProps & GralWindow.windExclusive) ==0);
    }
    if((windProps & GralWindow.windOnTop) !=0){ props |= SWT.ON_TOP; }
    window = new Shell(display, props);
    window.addShellListener(shellListener);
    window.addDisposeListener(disposeListener);
    super.panelComposite = window;
    if(title !=null){ window.setText(title); }
    
  }
  
  SwtSubWindow(String name, Shell shell, GralWidgetMng gralMng)
  { super(name, gralMng, shell);
    this.window = shell;
    
  }
  
  //@Override
  public boolean setFocus()
  { return window.setFocus();
  }

  @Override public boolean isWindowsVisible(){ return window.isVisible(); }

  @Override public void setWindowVisible(boolean visible)
  { 
    window.setVisible(visible);
    if(visible){ 
      window.setFocus();
      window.setActive();
    }
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override public void redraw(){  window.redraw(); window.update(); }

  
  public void removeWidgetImplementation()
  {
    window.dispose();
    window = null;
  }

  @Override public Composite getPanelImpl() { return window; }
  
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = window.getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { window.setBounds(x,y,dx,dy);
  }
  
  @Override public void closeWindow()
  { 
    window.close();
  }

  
  @Override public void setResizeAction(GralUserAction action){
    if(resizeAction == null){
      window.addControlListener(resizeListener);
    }
    resizeAction = action;
  }
  
  @Override public void setMouseAction(GralUserAction action){
    if(mouseAction == null){
      window.addControlListener(resizeListener);
    }
    mouseAction = action;
  }
  
  
  
  protected ShellListener shellListener = new ShellListener(){

    @Override
    public void shellActivated(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellClosed(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellDeactivated(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellDeiconified(ShellEvent e)
    {
      stop();
      
    }

    @Override
    public void shellIconified(ShellEvent e)
    {
      stop();
      
    }
    
  };
  
  
  
  DisposeListener disposeListener = new DisposeListener(){
    
    
    @Override
    public void widgetDisposed(DisposeEvent e)
    {
      stop();
    }
  };
  
  
  /**The resizeListener will be activated if {@link #setResizeAction(GralUserAction)} will be called.
   * It calls this user action on resize. */
  private ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction !=null){
        resizeAction.userActionGui(0, null);
      }
    }
  };
  
  
  /**The mouseListener will be activated if {@link #setMouseAction(GralUserAction)} will be called.
   * It calls this user action on resize. */
  MouseListener mouseListener = new MouseListener()
  {
    int captureAreaDivider;
    
    @Override public void mouseDoubleClick(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Double;  //TODO select key, alt, ctrl etc.
      mouseAction.userActionGui(key, null, pos);
    }

    @Override public void mouseDown(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Down;  //TODO select key, alt, ctrl etc.
      mouseAction.userActionGui(key, null, pos);
    }

    @Override public void mouseUp(MouseEvent e) 
    { GralRectangle pos = new GralRectangle(e.x, e.y, 0,0);
      int key = KeyCode.mouse1Up;  //TODO select key, alt, ctrl etc.
      mouseAction.userActionGui(key, null, pos);
    }
    
  };
  

  
  void stop(){}
  
  
}
