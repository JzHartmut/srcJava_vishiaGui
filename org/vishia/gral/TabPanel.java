package org.vishia.gral;


/**This class is the common base class for Tab-Panels.
 * <ul>
 * <li>SWT: TabFolder, TabItem
 * <li>Swing: TabbedPane
 * @author Hartmut Schorrig
 *
 */
public abstract class TabPanel
{
	final protected PanelActivatedGui notifyingUserInstanceWhileSelectingTab;
	

	/**The constructor can only be invoked from a implementing class.
	 * @param user
	 */
	protected TabPanel(PanelActivatedGui user)
	{
		this.notifyingUserInstanceWhileSelectingTab = user;
	}
	
	/**Adds a grid-panel in the TabPanel. The panel will be registered in the GuiPanelMng,
	 * so the access to the panel can be done with its name.
	 * @param sName The name, used in 
	 * @param sLabel to designate the tab for view. A "&" left from a character determines the hot-key
	 *               to select the tab.
	 * @param yGrid   number of units per grid line vertical. It may be 1. 
	 * @param xGrid   number of units per grid line horizontal. It may be 1. 
	 * @param yGrid2  Number of grid lines vertical per wider ranges for lines
	 * @param xGrid2  Number of grid lines horizontal per wider ranges for lines
	 * @return
	 */
	abstract public PanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2);
	
	abstract public PanelContent addCanvasPanel(String sName, String sLabel);
	
	
	abstract public WidgetCmpnifc getGuiComponent();
	
}
