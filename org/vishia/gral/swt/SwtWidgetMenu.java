package org.vishia.gral.swt;

import org.eclipse.swt.widgets.MenuItem;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;

public class SwtWidgetMenu extends GralWidget
{
  
  private final MenuItem widgSwt;
  
  SwtWidgetMenu(String sName, MenuItem widgSwt, String menuPath, GralWidgetMng mng){
    super(sName, 'M', mng);
    this.widgSwt = widgSwt;
    super.setDataPath("menu-" + menuPath);
    widgSwt.setData(this);
  }

  @Override
  protected void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  @Override protected void repaintGthread(){
    //NOTE: do nothing, a Menu isn't able to redraw, it isn't a Control.
    //widgSwt.redraw();
  }


  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }
}
