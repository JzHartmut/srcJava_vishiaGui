package org.vishia.gral.base;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;


/**This class is the common base class for Tabbed-Panels.
 * A TabbbedPanel is the container for Tabs. It doesn't contain other widgets than Tabs.
 * It is a {@link GralPanelContent} because it can be used as one of the areas 
 * in {@link org.vishia.gral.area9.GralArea9Window}.
 * It is a {@link GralWidget} because it is used as one member inside another Panel.
 * The Implementation of this class should create a swt.TabFolder or adequate
 * A Tab inside the TabPanel is a Panel.
 * <br><br>
 * Concepts in base graphic:
 * <ul>
 * <li>SWT: TabFolder, TabItem
 * <li>Swing: TabbedPane
 * </ul>
 * @author Hartmut Schorrig
 *
 */
public class GralTabbedPanel extends GralPanelContent /*extends GralWidget*/ implements GralVisibleWidgets_ifc
{
  /**The version and history:
   * <ul>
   * <li>2013-10-22 Hartmut bugfix: {@link #setMngToTabbedPanel()} necessary because a new Tab should refer to this parent.
   *   On {@link #addCanvasPanel(String, String)} or {@link #addGridPanel(String, String, int, int, int, int)} the mng
   *   should refer this as {@link GralMng#pos}.parent. 
   * <li>2012-01-08 Hartmut new: {@link #removePanel(String)}
   * <li>2011-11-07 Hartmut chg: a TabbedPanel is a GralWidget too, 
   * <li>2011-10-01 Hartmut new: abstract method {@link #selectTab(String)}.
   * <li>2011-09-10 Hartmut chg: move this class from gral/gridPanel/TabPanel to gral/base/GralTabbedPanel
   *     Reason: it isn't a concept of the grid panel  but a basic concept of gral. It hasn't any dependencies to gridPanel. 
   * <li>2011-09-08 Hartmut chg: The {@link #widgetsVisible} were stored in the GuiPanelMngBase before.
   *     But an application can have more as one tabbed panel, and any of them can change the visible widgets while changing the tab.
   *     The visible widgets are not an concpet of the grid panel manager but a topic of the panel content. 
   *     It is not a question of one PanelContent but a question of the TabbedPanel which contains some panels.
   * <li>2011-08-31 Hartmut chg: The {@link #focusedTab} and {@link #getFocusedTab()} is moved from GuiPanelMngBase to here.
   *     Reason: One application can have more as one tabbed panel.    
   * <li>2011-05-26 Hartmut chg: {@link #addCanvasPanel(String, String)} now returns {@link GralPanelContent} instead an untyped Object.    
   * <li>2010-05-00 Hartmut created   
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  @SuppressWarnings("hiding")
  public static final int version = 0x20111001;
  
	final public GralPanelActivated_ifc notifyingUserInstanceWhileSelectingTab;
	
	final protected Map<String, GralPanelContent> panels = new TreeMap<String, GralPanelContent>();

	/**The currently selected tab. */
	public GralPanelContent focusedTab;
	
  /**The actual widgets in the visible panel. It may a sub-panel or changed content. The list can be changed. */
  public Queue<GralWidget> widgetsVisible;

  /**A new list of actual widgets, set while select another tab etc. The reference may be set 
   * in the GUI-Thread (GUI-listener). The communication-manager thread reads whether it isn't null,
   * processes it and sets this reference to null if it is processed. */
  public Queue<GralWidget> newWidgetsVisible;
  


	/**The constructor can only be invoked from a implementing class.
	 * @param user
	 */
	public GralTabbedPanel(String posString, String sName, GralPanelActivated_ifc user, int property)
	{ super(posString, sName);
	  //super(sName, '@', mng);
	  this.notifyingUserInstanceWhileSelectingTab = user;
	}
	
	
	//protected void setMngToTabbedPanel(){ itsMng.setTabbedPanel(this); }
	
	//abstract public GralPanelContent getGuiComponent();
	
	public GralPanelContent getFocusedTab(){ return focusedTab; }
	
	   /**Adds a grid-panel in the TabPanel. The panel will be registered in the GuiPanelMng,
     * so the access to the panel can be done with its name.
     * The position of the widget manager is set to full area of this panel.
     * 
     * @param sName The name, used in 
     * @param sLabel to designate the tab for view. A "&" left from a character determines the hot-key
     *               to select the tab.
     * @param yGrid   number of units per grid line vertical. It may be 1. 
     * @param xGrid   number of units per grid line horizontal. It may be 1. 
     * @param yGrid2  Number of grid lines vertical per wider ranges for lines
     * @param xGrid2  Number of grid lines horizontal per wider ranges for lines
     * @return
     */
    public GralPanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2)
    {
      GralPanelContent panelg = new GralPanelContent("@", sName);
      ((ImplAccess)wdgImpl).addGridPanel(panelg, sLabel, yGrid, xGrid, yGrid2, xGrid2);
      return panelg;
    }
    
    public GralPanelContent addCanvasPanel(String sName, String sLabel){
      GralPanelContent panelg = new GralPanelContent("@", sName);
      ((ImplAccess)wdgImpl).addCanvasPanel(panelg, sLabel);
      return panelg;
      
    }
 
	
    public GralPanelContent selectTab(String name){
      return ((ImplAccess)wdgImpl).selectTab(name);
    }
    
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
  

  boolean removePanel(GralPanelContent panel){
    return removePanel(panel.name);
  }
  
  /**Removes the named panel from this panel container.
   * @param namePanel The name which was given by {@link #addCanvasPanel(String, String)} or 
   *   {@link #addGridPanel(String, String, int, int, int, int)} and which is stored in 
   *   {@link GralPanelContent#namePanel}.
   * @return true if removed, false if the namePanel isn't ok and therefore nothing is removed.
   */
  public boolean removePanel(String namePanel){
    boolean bOk = true;
    GralPanelContent panel = panels.get(namePanel);
    if(panel !=null){
      panels.remove(namePanel);
      panel.remove();
    } else {
      bOk = false;
    }
    return bOk;
  }
  
  
  public abstract static class ImplAccess extends GralPanelContent.ImplAccess
  {

    protected final GralTabbedPanel widgg;
    
    protected ImplAccess(GralTabbedPanel widgg)
    {
      super(widgg);
      this.widgg = widgg;
    }
    
    /**Adds a grid-panel in the TabPanel. The panel will be registered in the GuiPanelMng,
     * so the access to the panel can be done with its name.
     * The position of the widget manager is set to full area of this panel.
     * 
     * @param sName The name, used in 
     * @param sLabel to designate the tab for view. A "&" left from a character determines the hot-key
     *               to select the tab.
     * @param yGrid   number of units per grid line vertical. It may be 1. 
     * @param xGrid   number of units per grid line horizontal. It may be 1. 
     * @param yGrid2  Number of grid lines vertical per wider ranges for lines
     * @param xGrid2  Number of grid lines horizontal per wider ranges for lines
     * @return
     */
    abstract public GralPanelContent addGridPanel(GralPanelContent panelg, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2);
    
    abstract public GralPanelContent addCanvasPanel(GralPanelContent panelg, String sLabel);
    /**The named tab should be focused.
     * TODO which widget should be focused? It may be a first widget in any GralPanelContent?
     * In SWT it works if any widget in a tab is focused. The the tab is focused then automatically.
     * It means this method is not necessary. If selectTab is called, the whole tab has the focus
     * which means it content is visible. That is able to use.
     * @param name
     * @return
     * @deprecated use {@link GralWidget#setFocus()}
     */
    @Deprecated
    abstract public GralPanelContent selectTab(String name);
    
  }

}
