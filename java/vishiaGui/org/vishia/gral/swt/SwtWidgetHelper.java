package org.vishia.gral.swt;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetHelper;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.impl_ifc.GralWidgetImpl_ifc;
import org.vishia.util.Debugutil;

/**This class is a wrapper around any widget which is a {@link org.eclipse.swt.widgets.Control}.
 * <br>
 * The basically important operation with more content is {@link #getPixelPositionSize(Control)}.
 * The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetHelper implements GralWidgetImpl_ifc
{

  /**Version and history
   * <ul>
   * <li>2011-11-18 Hartmut chg: {@link #setFocusOfTabSwt(Control)} is the implementation of all 
   *   {@link org.vishia.gral.base.GralWidget#setFocusGThread()} implementations. It regards that a tab in a TabFolder
   *   should be selected if any widget inside the tab-panel is focused. Used extensive in JavaCommander.
   * </ul>
   * 
   */
  public final static int version = 0x20111119;
  
  
  private static SwtMng mngs;
  
  public final SwtMng mng;
  
  public Control widgetSwt;



  public SwtWidgetHelper(Control widgetSwt, SwtMng mng)
  { if(SwtWidgetHelper.mngs !=null){ assert(SwtWidgetHelper.mngs == mng); }
    else {SwtWidgetHelper.mngs = mng; }
    this.widgetSwt = widgetSwt;
    this.mng = mng;
  }


  @Override public Object getImplWidget ( ) { return this.widgetSwt; }


  public static GralColor getColor(Color swtColor)
  {
    int colorValue = swtColor.getBlue() << 16 + swtColor.getGreen() << 8 + swtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static Color getColor(GralColor color){ return mngs.propertiesGuiSwt.colorSwt(color); }
  
  public static GralColor setBackgroundColor(GralColor color, Control swtWidget)
  { Color colorSwt = getColor(color);
    Color colorSwtOld = swtWidget.getBackground();
    swtWidget.setBackground(colorSwt);
    return getColor(colorSwtOld);
  }

  
  public static GralColor setForegroundColor(GralColor color, Control swtWidget)
  { 
    Color colorSwt = (Color)color.colorGuimpl;
    Color colorSwtOld = swtWidget.getForeground();
    swtWidget.setForeground(colorSwt);
    return getColor(colorSwtOld);
  }


  /**Sets the correct TabItem if any widget at this TabItem is focused. That is not done by swt graphic
   * on Control.setFocus().
   * @param control
   */
  public static boolean setFocusOfTabSwt(Control control)
  {
    List<Control> parents = new LinkedList<Control>();
    Control parent = control;
    while( parent !=null && (parent = parent.getParent())!=null){
      parents.add(parent);
    }
    for(Control parent1: parents){
      Object gralObj = parent1.getData();
      if(gralObj !=null && gralObj instanceof SwtPanel){
        SwtPanel gralPanel = (SwtPanel) gralObj;
        TabItem tabitem = null; //gralPanel.itsTabSwt;
        if(tabitem !=null){
          tabitem.getParent().setSelection(tabitem);
        }
      }
      if(parent1 instanceof TabFolder){
        TabFolder tf = (TabFolder)parent1;
        tf.setFocus();
      }
    }
    if(control == null){
      return false;         //TODO should not be.
    }
    control.forceFocus();
    return control.setFocus();

    
  }

  
  
  public void swtUpdateRedraw(){
    widgetSwt.update();
    widgetSwt.redraw();
  }
  
  
  
  public boolean setFocusGThread(){ return widgetSwt.setFocus(); }

  /**Sets the implementation widget vible or not.
   * @see org.vishia.gral.base.GralWidgImplAccess_ifc#setVisibleGThread(boolean)
   */
  public void setVisibleGThread(boolean bVisible){ 
    widgetSwt.setVisible(bVisible);
  }


  public void removeWidgetImplementation()
  {
    if(widgetSwt !=null){ 
      widgetSwt.dispose();
      widgetSwt = null;
    }
  }


  public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  
  
  /**Calculates the absolute position of the widget on the screen. 
   * It searches the whole window {@link Shell} and calculates from there through all composites.
   * @return absolute Position and size on the screen.
   */
  public GralRectangle getPixelPositionSize(){
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


  
  /**
   * @param widg
   * @return
   * @deprecated this routine is implemented in {@link SwtWidgetHelper}
   *   which can be used for all widgets (new concept).
   */
  @Deprecated
  public static GralRectangle getPixelPositionSize(Control widg){
    int posx = 0, posy = 0;
    Rectangle r = widg.getBounds();
    Composite parent;
    if(widg instanceof Composite){
      parent = (Composite) widg; //start with them, maybe the shell itself
    } else {
      parent = widg.getParent();
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
    if(parent == widg){
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
  public void specifyContextMenu(GralMenu menu)
  {
    Menu swtMenu = (Menu)menu.getMenuImpl();
    if(swtMenu == null) {
      //GralMenu._GraphicImpl implMenu = new SwtMenu(menu, widgetSwt);
      //implMenu._implMenu();
      swtMenu = (Menu)menu.getMenuImpl();
    }
    try{
      //widgetSwt.setMenu(swtMenu);
    } catch(IllegalArgumentException exc){
      Debugutil.stop();
    }
    
  }


  
  
}
