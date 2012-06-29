package org.vishia.gral.swt;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetHelper;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

/**The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetHelper implements GralWidgetHelper
{

  /**Version and history
   * <ul>
   * <li>2011-11-18 Hartmut chg: {@link #setFocusOfTabSwt(Control)} is the implementation of all 
   *   {@link org.vishia.gral.base.GralWidget#setFocus()} implementations. It regards that a tab in a TabFolder
   *   should be selected if any widget inside the tab-panel is focused. Used extensive in JavaCommander.
   * </ul>
   * 
   */
  public final static int version = 0x20111119;
  
  
  private static SwtMng mng;
  
  
  @Override public void setMng(GralWidgetMng mng)
  { this.mng = (SwtMng)mng;
  }


  public SwtWidgetHelper()
  { 
    
  }


  public static GralColor getColor(Color swtColor)
  {
    int colorValue = swtColor.getBlue() << 16 + swtColor.getGreen() << 8 + swtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static Color getColor(GralColor color){ return mng.propertiesGuiSwt.colorSwt(color); }
  
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
    while( (parent = parent.getParent())!=null){
      parents.add(parent);
    }
    for(Control parent1: parents){
      Object gralObj = parent1.getData();
      if(gralObj !=null && gralObj instanceof SwtPanel){
        SwtPanel gralPanel = (SwtPanel) gralObj;
        TabItem tabitem = gralPanel.itsTabSwt;
        if(tabitem !=null){
          tabitem.getParent().setSelection(tabitem);
        }
      }
      if(parent1 instanceof TabFolder){
        TabFolder tf = (TabFolder)parent1;
        tf.setFocus();
      }
    }
    control.forceFocus();
    return control.setFocus();

    
  }

  
  @Override public GralRectangle getAbsoluteBoundsOf(GralWidget widg){
    Control swtWidg = (Control)widg.getWidgetImplementation();
    Rectangle pos = swtWidg.getBounds();
    GralRectangle rect = new GralRectangle(pos.x, pos.y, pos.width, pos.height);
    Control parent = swtWidg;
    do{ 
      parent = parent.getParent();
      if(parent !=null){
        Point posParent = parent.getLocation();
        rect.x += posParent.x;
        rect.y += posParent.y;
      }
      if(parent instanceof Scrollable){
        Rectangle area = ((Scrollable)parent).getClientArea();
        rect.x += area.x;
        rect.y += area.y;
      }
    } while(!(parent instanceof Shell));
    rect.y += 30;  //size of title and menu, where to find in SWT???
    return rect;
  }
  

  @Override
  public boolean showContextMenu(GralWidget widg) {
    boolean bOk;
    Control swtWidg = (Control)widg.getWidgetImplementation();
    Menu contextMenu = swtWidg.getMenu();
    if(contextMenu == null){
      bOk = false;
    } else {
      //Rectangle pos = swtWidg.getBounds();
      GralRectangle pos = getAbsoluteBoundsOf(widg);
      contextMenu.setLocation(pos.x + pos.dx, pos.y + pos.dy);
      contextMenu.setVisible(true);
      bOk = true;
    }
    return bOk;
  }
  
  
  
  
}
