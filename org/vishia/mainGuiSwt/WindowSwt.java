package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.widget.WidgetCmpnifc;

public class WindowSwt extends GralPanelContent implements WidgetCmpnifc
{

  final Shell window;
  
  
  WindowSwt(Display display, String title, boolean exclusive)
  {
    int props = 0;
    if(exclusive){ props |= SWT.PRIMARY_MODAL; }
    if(title !=null){ props |= SWT.TITLE; }
    window = new Shell(display, props);
    if(title !=null){ window.setText(title); }
    
  }
  
  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }

}
