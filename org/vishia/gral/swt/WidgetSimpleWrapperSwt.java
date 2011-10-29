package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This class wraps a SWT widget. In this form it is able to reference in the SWT-independent GRAL
 * @author Hartmut Schorrig
 *
 */
public class WidgetSimpleWrapperSwt extends GralWidget
{
  public Control widgetSwt;

  public WidgetSimpleWrapperSwt(String name, char whatis, Control widgetSwt, GralWidgetMng mng)
  { super(name, whatis, mng);
    this.widgetSwt = widgetSwt;
  }

  @Override public Object getWidgetImplementation()
  { return widgetSwt;
  }
  
  @Override public boolean setFocus(){ return widgetSwt.setFocus(); }

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

  @Override protected void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }

  @Override public void redraw(){  widgetSwt.redraw(); }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
}
