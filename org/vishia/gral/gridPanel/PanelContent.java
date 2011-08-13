package org.vishia.gral.gridPanel;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.ifc.WidgetDescriptor;


/**This class describes a panel with its content for managing. */
public class PanelContent
{

	/**The GUI-Widget of the panel.   
   *   (Swing:  Device guiDevice, SWT: Composite based on Control);
   */
	public Object panelComposite; 
	//public final CanvasStorePanel panelComposite;
	
	//public final Map<String, WidgetDescriptor<WidgetTYPE>> widgetIndex = new TreeMap<String, WidgetDescriptor<WidgetTYPE>>();

	/**List of all widgets which are contained in this panel.
	 * This list is used in the communication thread to update the widget's content.
	 */
	//public final List<WidgetDescriptor> widgetList = new LinkedList<WidgetDescriptor>();
	public final Queue<WidgetDescriptor> widgetList = new ConcurrentLinkedQueue<WidgetDescriptor>();

  public List<WidgetDescriptor> widgetsToResize = new LinkedList<WidgetDescriptor>();


  /**True then the content of the panel is zoomed with the actual size of panel. 
   * It means that all widgets are zoomed in position and size,  but there content isn't changed. */
  protected boolean bZoomed;
  
  /**True then the grid of the panel is zoomed with the actual size of panel. 
   * It means that all fonts are changed too.
   * */
  protected boolean bGridZoomed;
  
  
  

  
	public PanelContent(Object panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{
		this.panelComposite = panelComposite;
    int property = 0; //TODO parameter
    bZoomed = (property & GuiPanelMngBuildIfc.propZoomedPanel) !=0;
    bGridZoomed = (property & GuiPanelMngBuildIfc.propGridZoomedPanel) !=0;
	}
	
	
	protected PanelContent(){}
	
}

