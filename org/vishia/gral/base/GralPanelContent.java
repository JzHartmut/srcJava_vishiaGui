package org.vishia.gral.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;


/**This class describes a panel with its content for managing. */
public abstract class GralPanelContent extends GralWidget implements GralWidget_ifc
{

  /**Version history:
   * <ul>
   * <li>2012-01-08 Hartmut new: {@link #remove()}
   * <li>2011-11-19 Hartmut chg: The 'itsTabSwt' is moved to {@link org.vishia.gral.swt.SwtPanel} now.
   * <li>2011-11-12 Hartmut new: {@link #getPixelPositionSize()}.
   * </ul>
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 0x20111119;

  public final String namePanel;

  /**The GUI-Widget of the panel.   
   *   (Swing:  Device guiDevice, SWT: Composite based on Control);
   * Note: can't be final because it may be unknown on calling constructor  
   */
  protected Object panelComposite; 
  
	
	//public GralPrimaryWindow_ifc mainWindow;
	
	public final GralWidgetMng gralMng;
	
	//public final CanvasStorePanel panelComposite;
	
	//public final Map<String, WidgetDescriptor<WidgetTYPE>> widgetIndex = new TreeMap<String, WidgetDescriptor<WidgetTYPE>>();

	/**List of all widgets which are contained in this panel.
	 * This list is used in the communication thread to update the content of all widgets in the panel.
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
  
  
  

  
	public GralPanelContent(String namePanel, GralWidgetMng mng, Object panelComposite)
	//public PanelContent(CanvasStorePanel panelComposite)
	{ super(namePanel, '$', mng);
	  this.namePanel = namePanel;
		this.panelComposite = panelComposite;
		this.gralMng = mng;
		if(mng !=null){
		  mng.registerPanel(this);
		}
    int property = 0; //TODO parameter
    bZoomed = (property & GralGridBuild_ifc.propZoomedPanel) !=0;
    bGridZoomed = (property & GralGridBuild_ifc.propGridZoomedPanel) !=0;
	}
	
	/*
  private GralPanelContent(String namePanel, GralPrimaryWindow_ifc mainWindow)
  { super(namePanel, '$', null);
    this.namePanel = namePanel; this.gralMng = null; //mainWindow.;
  }

  
  private GralPanelContent(String namePanel, GralWidgetMng mng)
  { super(namePanel, '$', mng);
    this.namePanel = namePanel; this.gralMng = mng;
  }
  */
  
	
	public abstract   GralRectangle getPixelPositionSize();
	
  /**Removes this widget from the lists in this panel. This method is not intent to invoke
   * by an application. It is only used in {@link GralWidget#remove()}. Use the last one method
   * to remove a widget includint is disposition and remove from the panel.
   * @param widg The widget.
   */
  public void removeWidget(GralWidget widg)
  {
    widgetList.remove(widg);
    widgetsToResize.remove(widg);
  }
  
  /**This overridden form of {@link GralWidget_ifc#remove()} removes all widgets of this panel.
   * It includes the disposition  of the widgets in the graphic. It is done by invocation
   * {@link GralWidget#remove()}.
   * @return true because it is done.
   */
  @Override public boolean remove(){
    super.remove();
    for(GralWidget widg: widgetList){
      widg.remove();
    }
    widgetList.clear();      //the lists may be cleared already 
    widgetsToResize.clear(); //because widg.remove() removes the widget from the panel.
    return true;
  }
  

  @Override public Object getWidgetImplementation()
  { return panelComposite;
  }
	

  /**Returns the container instance of the panel of the implementing graphic.
   * @return The container.
   */
  public abstract Object getPanelImpl();
  
  @Override public String toString(){ return "GralPanel:" + namePanel; }
  
}

