package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

//public class SubWindowSwt extends GralPanelContent implements WidgetCmpnifc
public class SubWindowSwt extends GralSubWindow
{

  protected Shell window;
  
  
  SubWindowSwt(Display display, String title, boolean exclusive, GralGridMngBase gralMng)
  { super("sub", gralMng);
    int props = 0; ////|SWT.CLOSE;
    if(title !=null){ props |= SWT.TITLE | SWT.BORDER; }
    //if(exclusive){ props |= SWT.PRIMARY_MODAL | SWT.SYSTEM_MODAL | SWT.APPLICATION_MODAL; }
    if(exclusive){ props |= SWT.APPLICATION_MODAL; }
    window = new Shell(display, props);
    super.panelComposite = window;
    if(title !=null){ window.setText(title); }
    
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

}
