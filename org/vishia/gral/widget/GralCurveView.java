package org.vishia.gral.widget;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;

public class GralCurveView extends GralWidget
{

  public GralCurveView(String sName, GralWidgetMng mng)
  {
    super(sName, 'c', mng);
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  
  @Override public void redraw(){   }


  
  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
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
  
  
  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { //widgetSwt.setBounds(x,y,dx,dy);
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }
  

}
