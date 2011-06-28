package org.vishia.gral;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;


/**This class describes the one panel with its content for managing. */
public class PanelContent
{

	/**The GUI-Widget of the panel.   
   *   (Swing:  Device guiDevice, SWT: Composite based on Control);
   */
	public final Object panelComposite; 
	//public final CanvasStorePanel panelComposite;
	
	//public final Map<String, WidgetDescriptor<WidgetTYPE>> widgetIndex = new TreeMap<String, WidgetDescriptor<WidgetTYPE>>();

	/**List of all widgets which are contained in this panel.
	 * This list is used in the communication thread to update the widget's content.
	 */
	//public final List<WidgetDescriptor> widgetList = new LinkedList<WidgetDescriptor>();
	public final Queue<WidgetDescriptor> widgetList = new ConcurrentLinkedQueue<WidgetDescriptor>();

	public PanelContent(Object panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{
		this.panelComposite = panelComposite;
	}
}

