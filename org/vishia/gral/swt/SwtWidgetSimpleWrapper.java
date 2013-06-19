package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This class wraps a SWT widget. In this form it is able to reference in the SWT-independent GRAL
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetSimpleWrapper extends GralWidget
{
  public Control widgetSwt;

  public SwtWidgetSimpleWrapper(String name, char whatis, Control widgetSwt, GralMng mng)
  { super(name, whatis, mng);
    this.widgetSwt = widgetSwt;
  }

  
  @Override public void repaintGthread(){
    widgetSwt.redraw();
  }

  
  @Override public Object getWidgetImplementation()
  { return widgetSwt;
  }
  
  @Override public boolean setFocusGThread(){ return widgetSwt.setFocus(); }

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

  @Override public void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  

}
