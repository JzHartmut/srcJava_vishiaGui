package org.vishia.gral.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This class wraps a SWT widget. In this form it is able to reference in the SWT-independent GRAL
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetSimpleWrapper implements GralWidgImpl_ifc
{
  protected Control widgetSwt;

  protected final SwtMng mng;
  
  public SwtWidgetSimpleWrapper(Control widgetSwt, SwtMng mng)
  { this.mng = mng;
    this.widgetSwt = widgetSwt;
  }

  
  @Override public void repaintGthread(){
    widgetSwt.redraw();
  }

  
  public void swtUpdateRedraw(){
    widgetSwt.update();
    widgetSwt.redraw();
  }
  
  
  
  @Override public Object getWidgetImplementation()
  { return widgetSwt;
  }
  
  @Override public boolean setFocusGThread(){ return widgetSwt.setFocus(); }

  @Override public void removeWidgetImplementation()
  {
    widgetSwt.dispose();
    widgetSwt = null;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  @Override public boolean setVisible(boolean visible)
  { widgetSwt.setVisible(visible);
    return widgetSwt.isVisible();
  }
  

  
  @Override public GralRectangle getPixelPositionSize(){
    int posx = 0, posy = 0;
    Rectangle r = widgetSwt.getBounds();
    Composite parent;
    if(widgetSwt instanceof Composite){
      parent = (Composite) widgetSwt; //start with them, maybe the shell itself
    } else {
      parent = widgetSwt.getParent();
    }
    Rectangle pos;
    while( !( parent instanceof Shell ) ){
      pos = parent.getBounds();
      posx += pos.x; posy += pos.y;
      parent = parent.getParent();
    }
    assert(parent instanceof Shell);
    Rectangle s = parent.getClientArea();
    pos = parent.getBounds();
    int dframe = (pos.width - s.width) /2;   //width of the frame line.
    posx += r.x + dframe;               //absolute position of the client area!
    posy += r.y + (pos.height - s.height) - dframe;
    int dx, dy;
    if(parent == widgetSwt){
      dx = s.width;
      dy = s.height;
    } else {
      dx = r.width;
      dy = r.height;
    }
    GralRectangle posSize = new GralRectangle(posx, posy, dx, dy);
    return posSize;
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
