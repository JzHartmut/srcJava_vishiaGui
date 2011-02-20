package org.vishia.mainGui;

import java.util.List;


/**This interface should be implemented by any user class to call user actions when a panel is activated.
 * The activation of several panels is done initially, when the Panel becomes visible, or if a tab
 * is activated in a tab-view.
 * 
 * @author e09srrh0
 *
 */
public interface PanelActivatedGui
{

	/**If a panel is actived, the user will be notified. 
	 * @param widgets Information about all widgets in this panel, which should be updated 
	 *        with correct values for example with data of a running process.
	 * */
	void panelActivatedGui(List<WidgetDescriptor<?>> widgets);
	
}
