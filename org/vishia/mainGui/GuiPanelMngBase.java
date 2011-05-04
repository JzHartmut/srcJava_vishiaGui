package org.vishia.mainGui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.msgDispatch.LogMessage;

/**This is the base class of the GuiPanelMng for several Graphic-Adapters (Swing, SWT etc.). 
 * It contains the independent parts. 
 * The GuiPanelMng is a common approach to work with graphical interface simply, 
 * it is implemented by the several graphic-system-supporting classes
 * <ul>
 * <li>{@link org.vishia.mainGuiSwt.GuiPanelMngSwt}
 * <li>{@link org.vishia.mainGuiSwing.GuiPanelMngSwt}
 * </ul>
 * to offer a unique interface to work with simple graphic applications.
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 * @param <WidgetTYPE> The special base type of the composed widgets in the underlying graphic adapter specialization.
 *                     (SWT: Composite)
 */
public abstract class GuiPanelMngBase<WidgetTYPE> implements GuiPanelMngBuildIfc, GuiPanelMngWorkingIfc
{
	
  final GuiPanelMngBase<?> parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  final protected GuiMngBase mngBase;
  
  protected final LogMessage log;
  
	protected final VariableContainer_ifc variableContainer;
	
	
	/**Creates an nee Panel Manager in a new Window.
	 * @param graphicBaseSystem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GuiPanelMngBase<?> createWindow(String graphicBaseSystem)
	{ Class<GuiPanelMngBase<?>> mngClass;
		GuiPanelMngBase<?> mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GuiPanelMngBase<?>>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GuiPanelMngBase<?>> ctor = mngClass.getConstructor();
			mng = ctor.newInstance();
			//mng = mngClass.newInstance();
		
		} catch(IllegalAccessException exc){ throw new IllegalArgumentException("Graphic base system access error: " + sGraphicBaseSystem);
		} catch(InstantiationException exc){ throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (SecurityException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (NoSuchMethodException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (IllegalArgumentException exc) {throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (InvocationTargetException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		}
		return mng;
	}
	
	
	@Override public void setPosition(int line, int column, int height, int length, char direction)
	{ setFinePosition(line, 0, column, 0, height, 0, length, 0, direction);
	}

	
	
  /**Map of all panels. A panel may be a dialog box etc. */
  protected final Map<String,PanelContent<WidgetTYPE>> panels = new TreeMap<String,PanelContent<WidgetTYPE>>();
  
  public PanelContent<WidgetTYPE> currPanel;
  
  protected Rectangle currPanelPos;
  
  protected String sCurrPanel;
  
  protected WidgetDescriptor<?> lastClickedWidgetInfo;
  

	public List<WidgetDescriptor<?>> getListCurrWidgets(){ return currPanel.widgetList; }
	
  /**Index of all user actions, which are able to use in Button etc. 
   * The user action "showWidgetInfos" defined here is added initially.
   * Some more user-actions can be add calling {@link #registerUserAction(String, UserActionGui)}. 
   * */
  protected final Map<String, UserActionGui> userActions = new TreeMap<String, UserActionGui>();
  //private final Map<String, ButtonUserAction> userActions = new TreeMap<String, ButtonUserAction>();
  
  /**Index of all Tables, which are representable. */
  //private final Map<String, Table> userTableAccesses = new TreeMap<String, Table>();
  
	
	
  public GuiPanelMngBase(GuiPanelMngBase<?> parent, VariableContainer_ifc variableContainer, LogMessage log)
	{ this.parent = parent;
	  if(parent == null){
	    mngBase = new GuiMngBase();
	  } else {
	    mngBase = parent.mngBase;
	  }
		this.log = log;
		this.variableContainer = variableContainer;
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
	}


	public void setLastClickedWidgetInfo(WidgetDescriptor<?> lastClickedWidgetInfo)
	{
		this.lastClickedWidgetInfo = lastClickedWidgetInfo;
	}
  /**Registers all user actions, which are able to use in Button etc.
   * The name is related with the <code>userAction=</code> designation in the configuration String.
   * @param name
   * @param action
   */
  @Override public void registerUserAction(String name, UserActionGui action)
  {
    userActions.put(name, action);
  }
  
  @Override public UserActionGui getRegisteredUserAction(String name)
  {
    return userActions.get(name);
  }
  
  
  
  /**Registers all Tables, which are representable. */
  public void xxxregisterTableAccess(String name, Table table)
  {
    //userTableAccesses.put(name, table);
  }
    
  

	UserActionGui actionShowWidgetInfos = new UserActionGui()
	{

		@Override public void userActionGui(
			String sCmd
		, WidgetDescriptor<?> infos, Object... params
		)
		{ 
			if(lastClickedWidgetInfo !=null){
				log.sendMsg(Report.info, "widget %s, datapath=%s"
					, GuiPanelMngBase.this.lastClickedWidgetInfo.name
					, GuiPanelMngBase.this.lastClickedWidgetInfo.getDataPath());
			} else {
				log.sendMsg(0, "widgetInfo - no widget selected");
			}
		}
		
	};
	
	

	
}
