package org.vishia.gral.gridPanel;

import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.ifc.WidgetDescriptor;

/**Base class for all panels of the PanelMng
 * @author Hartmut Schorrig
 *
 */
public abstract class GralPanel
{
  /**True then the content of the panel is zoomed with the actual size of panel. 
   * It means that all widgets are zoomed in position and size,  but there content isn't changed. */
  protected boolean bZoomed;
  
  /**True then the grid of the panel is zoomed with the actual size of panel. 
   * It means that all fonts are changed too.
   * */
  protected boolean bGridZoomed;
  
  //see gridPanel.PanelContent
  //List<WidgetDescriptor> listResizeWidgets = new LinkedList<WidgetDescriptor>();
  
  protected GralPanel(int property)
  {
    bZoomed = (property & GuiPanelMngBuildIfc.propZoomedPanel) !=0;
    bGridZoomed = (property & GuiPanelMngBuildIfc.propGridZoomedPanel) !=0;
  }
}
