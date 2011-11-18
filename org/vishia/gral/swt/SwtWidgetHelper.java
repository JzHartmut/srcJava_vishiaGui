package org.vishia.gral.swt;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.gral.ifc.GralColor;

/**The static methods of this class are called in some situations, where same functionality is need in some classes.
 * @author Hartmut Schorrig
 *
 */
public class SwtWidgetHelper
{

  /**Version and history
   * <ul>
   * <li>2011-11-18 Hartmut chg: {@link #setFocusOfTabSwt(Control)} is the implementation of all 
   *   {@link org.vishia.gral.ifc.GralWidget#setFocus()} implementations. It regards that a tab in a TabFolder
   *   should be selected if any widget inside the tab-panel is focused. Used extensive in JavaCommander.
   * </ul>
   * 
   */
  public final static int version = 0x20111119;
  
  public static GralColor getColor(Color swtColor)
  {
    int colorValue = swtColor.getBlue() << 16 + swtColor.getGreen() << 8 + swtColor.getRed();
    return GralColor.getColor(colorValue);
  }
  
  
  public static GralColor setBackgroundColor(GralColor color, Control swtWidget)
  { Color colorSwt = (Color)color.colorGuimpl;
    Color colorSwtOld = swtWidget.getBackground();
    swtWidget.setBackground(colorSwt);
    return getColor(colorSwtOld);
  }

  
  public static GralColor setForegroundColor(GralColor color, Control swtWidget)
  { Color colorSwt = (Color)color.colorGuimpl;
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
  
  
  
  
}
