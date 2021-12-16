package org.vishia.gral.awt;

import java.awt.Color;
import java.awt.Component;
import java.util.LinkedList;
import java.util.List;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Rectangle;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

/**The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
public class AwtWidgetHelper
{
  
  protected AwtWidgetMng mng;
  
  protected Component widga;  
  
  protected AwtWidgetHelper(Component widga, AwtWidgetMng mng)
  { this.widga = widga;
    this.mng = mng;
  }
  

  public void setBoundsPixel(int x, int y, int dx, int dy)
  { widga.setBounds(x,y,dx,dy);
  }
  
  

  public GralRectangle getPixelPositionSize(){
    return getPixelPositionSize(widga);
  }
  
  
  public static GralRectangle getPixelPositionSize(Component widga){
    int posx = 0, posy = 0;
    Rectangle r = widga.getBounds();
    Container parent;
    if(widga instanceof Container){
      parent = (Container) widga; //start with them, maybe the shell itself
    } else {
      parent = widga.getParent();
    }
    Rectangle pos;
    while( !( parent instanceof Frame ) ){
      pos = parent.getBounds();
      posx += pos.x; posy += pos.y;
      parent = parent.getParent();
    }
    assert(parent instanceof Frame);
    Rectangle s = parent.getBounds();
    pos = parent.getBounds();
    int dframe = (pos.width - s.width) /2;   //width of the frame line.
    posx += r.x + dframe;               //absolute position of the client area!
    posy += r.y + (pos.height - s.height) - dframe;
    int dx, dy;
    if(parent == widga){
      dx = s.width;
      dy = s.height;
    } else {
      dx = r.width;
      dy = r.height;
    }
    GralRectangle posSize = new GralRectangle(posx, posy, dx, dy);
    return posSize;
  }




  
  
  public static GralColor getColor(Color awtColor)
  {
    int colorValue = awtColor.getBlue() << 16 + awtColor.getGreen() << 8 + awtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static GralColor setBackgroundColor(GralColor color, Component awtWidget)
  { Color colorAwt = (Color)color.colorGuimpl;
    Color colorAwtOld = awtWidget.getBackground();
    awtWidget.setBackground(colorAwt);
    return getColor(colorAwtOld);
  }

  
  public static GralColor setForegroundColor(GralColor color, Component awtWidget)
  { Color colorAwt = (Color)color.colorGuimpl;
    Color colorAwtOld = awtWidget.getForeground();
    awtWidget.setForeground(colorAwt);
    return getColor(colorAwtOld);
  }


  
  /**Sets the correct TabItem if any widget at this TabItem is focused. That is not done by swt graphic
   * on Control.setFocus().
   * @param control
   */
  static boolean setFocusOfTabSwt(Component control)
  {
    List<Component> parents = new LinkedList<Component>();
    Component parent = control;
    while( (parent = parent.getParent())!=null){
      parents.add(parent);
    }
    for(Component parent1: parents){
    }
    return false; //TODOcontrol.setf();

    
  }
  
  
  public boolean setFocusGThread(){ widga.setFocusable(true); return widga.isFocusOwner(); }

  public void setVisibleGThread(boolean bVisible){ widga.setVisible(bVisible); }


  public void removeWidgetImplementation()
  {
    if(widga !=null){ 
      widga.removeNotify();
      widga = null;
    }
  }


  
}
