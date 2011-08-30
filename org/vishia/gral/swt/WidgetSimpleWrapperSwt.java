package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Control;
import org.vishia.gral.widget.Widgetifc;

public class WidgetSimpleWrapperSwt implements Widgetifc
{
  public final Control widgetSwt;

  public WidgetSimpleWrapperSwt(Control widgetSwt)
  { this.widgetSwt = widgetSwt;
  }

  @Override public Object getWidget()
  { return widgetSwt;
  }
  
  @Override public boolean setFocus(){ return widgetSwt.setFocus(); }

  
}
