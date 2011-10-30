package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
public class SubWindowSwt extends GralSubWindow
{

  protected Shell window;
  
  
  
  
  SubWindowSwt(String name, Display display, String title, boolean exclusive, GralWidgetMng gralMng)
  { super(name, gralMng, null);
    int props = 0; ////|SWT.CLOSE;
    if(title !=null){ props |= SWT.TITLE | SWT.BORDER; }
    //if(exclusive){ props |= SWT.PRIMARY_MODAL | SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL; }
    if(exclusive){ props |= SWT.APPLICATION_MODAL; }
    window = new Shell(display, props);
    super.panelComposite = window;
    if(title !=null){ window.setText(title); }
    
  }
  
  SubWindowSwt(String name, Shell shell, GralWidgetMng gralMng)
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
    resizeAction = action;
    if(resizeAction == null){
      window.addControlListener(resizeListener);
    }
  }
  
  ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction !=null){
        resizeAction.userActionGui(0, null);
      }
    }
  };
  
  
  

  
  
}
