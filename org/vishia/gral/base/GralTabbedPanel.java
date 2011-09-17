package org.vishia.gral.base;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralWidget;


/**This class is the common base class for Tabbed-Panels.
 * A TabPanel is the container for Tabs. It doesn't contain other widgets than Tabs.
 * A Tab inside the TabPanel is a usability Panel.
 * <ul>
 * <li>SWT: TabFolder, TabItem
 * <li>Swing: TabbedPane
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTabbedPanel implements GralVisibleWidgets_ifc
{
	final protected GralPanelActivated_ifc notifyingUserInstanceWhileSelectingTab;
	
	final protected Map<String, GralPanelContent> panels = new TreeMap<String, GralPanelContent>();

	protected GralPanelContent currentPanel;
	
  /**The actual widgets in the visible panel. It may a sub-panel or changed content. The list can be changed. */
  public Queue<GralWidget> widgetsVisible;

  /**A new list of actual widgets, set while select another tab etc. The reference may be set 
   * in the GUI-Thread (GUI-listener). The communication-manager thread reads whether it isn't null,
   * processes it and sets this reference to null if it is processed. */
  public Queue<GralWidget> newWidgetsVisible;
  


	/**The constructor can only be invoked from a implementing class.
	 * @param user
	 */
	protected GralTabbedPanel(GralPanelActivated_ifc user, int property)
	{ this.notifyingUserInstanceWhileSelectingTab = user;
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
	abstract public GralPanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2);
	
	abstract public GralPanelContent addCanvasPanel(String sName, String sLabel);
	
	
	
	abstract public GralPanelContent getGuiComponent();
	
	public GralPanelContent getCurrentPanel(){ return currentPanel; }
	
  @Override public Queue<GralWidget> getWidgetsVisible()
  {
    if(newWidgetsVisible !=null){
      //if(panel.widgetList !=null){
        //remove communication request for actual widgets.
      //}
      widgetsVisible = newWidgetsVisible;
      newWidgetsVisible = null;
    }
    
    return widgetsVisible;
  }

  
	
}
