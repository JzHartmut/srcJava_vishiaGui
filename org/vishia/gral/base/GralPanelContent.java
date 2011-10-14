package org.vishia.gral.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;


/**This class describes a panel with its content for managing. */
public abstract class GralPanelContent implements GralWidget_ifc
{

  public final String namePanel;

  /**The GUI-Widget of the panel.   
   *   (Swing:  Device guiDevice, SWT: Composite based on Control);
   * Note: can't be final because it may be unknown on calling constructor  
   */
  public Object panelComposite; 
  
  /**A possible tab in a TabFolder. Especially for SWT.   
   * Note: can't be final because it may be unknown on calling constructor  
   */
  public Object itsTabSwt; 
  
	
	public GralPrimaryWindow_ifc mainWindow;
	
	//public final CanvasStorePanel panelComposite;
	
	//public final Map<String, WidgetDescriptor<WidgetTYPE>> widgetIndex = new TreeMap<String, WidgetDescriptor<WidgetTYPE>>();

	/**List of all widgets which are contained in this panel.
	 * This list is used in the communication thread to update the widget's content.
	 */
	public Queue<GralWidget> widgetList = new ConcurrentLinkedQueue<GralWidget>();

  public List<GralWidget> widgetsToResize = new LinkedList<GralWidget>();


  /**True then the content of the panel is zoomed with the actual size of panel. 
   * It means that all widgets are zoomed in position and size,  but there content isn't changed. */
  protected boolean bZoomed;
  
  /**True then the grid of the panel is zoomed with the actual size of panel. 
   * It means that all fonts are changed too.
   * */
  protected boolean bGridZoomed;
  
  
  

  
	public GralPanelContent(String namePanel, Object panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{
	  this.namePanel = namePanel;
		this.panelComposite = panelComposite;
    int property = 0; //TODO parameter
    bZoomed = (property & GralGridBuild_ifc.propZoomedPanel) !=0;
    bGridZoomed = (property & GralGridBuild_ifc.propGridZoomedPanel) !=0;
	}
	
	
	
	protected GralPanelContent(String namePanel, GralPrimaryWindow_ifc mainWindow)
	{ this.namePanel = namePanel; this.mainWindow = mainWindow;
  }

	

  @Override public Object getWidgetImplementation()
  { return panelComposite;
  }
	
	
  @Override public String toString(){ return "GralPanel:" + namePanel; }
  
}

