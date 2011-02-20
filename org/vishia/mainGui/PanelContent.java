package org.vishia.mainGui;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**This class describes the one panel with its content for managing. */
public class PanelContent<WidgetTYPE>
{

	/**The GUI-Widget of the panel.   
   *   (Swing:  Device guiDevice, SWT: Composite based on Control);
   */
	public final WidgetTYPE panelComposite; 
	//public final CanvasStorePanel panelComposite;
	
	//public final Map<String, WidgetDescriptor<WidgetTYPE>> widgetIndex = new TreeMap<String, WidgetDescriptor<WidgetTYPE>>();

	public final List<WidgetDescriptor<?>> widgetList = new LinkedList<WidgetDescriptor<?>>();

	public PanelContent(WidgetTYPE panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{
		this.panelComposite = panelComposite;
	}
}

