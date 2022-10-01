package org.vishia.gral.swt;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.ifc.GralRectangle;

/**This class wraps a SWT widget. In this form it is able to reference in the SWT-independent GRAL
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetSimpleWrapper implements GralWidgImplAccess_ifc
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

  @Override public void setVisibleGThread(boolean bVisible) { widgetSwt.setVisible(bVisible); }


  @Override public void removeWidgetImplementation()
  {
    if(widgetSwt !=null){ 
      widgetSwt.dispose();
      widgetSwt = null;
    }
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
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


  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }


  
  

}
