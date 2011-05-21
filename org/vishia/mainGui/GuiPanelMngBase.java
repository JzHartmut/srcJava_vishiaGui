package org.vishia.mainGui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Control;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.mainCmd.Report;
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
public abstract class GuiPanelMngBase implements GuiPanelMngBuildIfc, GuiPanelMngWorkingIfc
{
	
  final GuiPanelMngBase parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  final protected GuiMngBase mngBase;
  
  /**Properties of this Dialog Window. */
  public  final PropertiesGui propertiesGui;

  
  protected final LogMessage log;
  
	protected final VariableContainer_ifc variableContainer;
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   */
  protected int xPos, xPosFrac =0, yPos, yPosFrac =0;
  
  /**Saved last use position. After calling {@link #setPosAndSize_(Control, int, int, int, int)}
   * the xPos and yPos are setted to the next planned position.
   * But, if a new position regarded to the last given one is selected, the previous one is need.
   */
  protected int xPosPrev, xPosPrevFrac, yPosPrev, yPosPrevFrac;
  
  /**width and height for the next element. */
  protected int xIncr, xSizeFrac, yIncr, ySizeFrac;
  
  /**'l' - left 'r'-right, 't' top 'b' bottom. */ 
  protected char xOrigin = 'l', yOrigin = 'b';
  
  protected char directionOfNextElement = 'r';
  
  /**The width of the last placed element. 
   * It is used to determine a next xPos in horizontal direction. */
  //int xWidth;
  
  /**True if the next element should be placed below the last. */
  protected boolean bBelow, bRigth;

  
	
	/**Creates an nee Panel Manager in a new Window.
	 * @param graphicBaseSystem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GuiPanelMngBase createWindow(String graphicBaseSystem)
	{ Class<GuiPanelMngBase> mngClass;
		GuiPanelMngBase mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GuiPanelMngBase>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GuiPanelMngBase> ctor = mngClass.getConstructor();
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
  protected final Map<String,PanelContent> panels = new TreeMap<String,PanelContent>();
  
  public PanelContent currPanel;
  
  protected String sCurrPanel;
  
  protected WidgetDescriptor lastClickedWidgetInfo;
  

	public List<WidgetDescriptor> getListCurrWidgets(){ return currPanel.widgetList; }
	
  /**Index of all user actions, which are able to use in Button etc. 
   * The user action "showWidgetInfos" defined here is added initially.
   * Some more user-actions can be add calling {@link #registerUserAction(String, UserActionGui)}. 
   * */
  protected final Map<String, UserActionGui> userActions = new TreeMap<String, UserActionGui>();
  //private final Map<String, ButtonUserAction> userActions = new TreeMap<String, ButtonUserAction>();
  
  /**Index of all Tables, which are representable. */
  //private final Map<String, Table> userTableAccesses = new TreeMap<String, Table>();
  
	
	
  public GuiPanelMngBase(GuiPanelMngBase parent
      , PropertiesGui props
      , VariableContainer_ifc variableContainer, LogMessage log)
	{ this.parent = parent;
	  if(parent == null){
	    mngBase = new GuiMngBase();
	  } else {
	    mngBase = parent.mngBase;
	  }
	  this.propertiesGui = props;
		this.log = log;
		this.variableContainer = variableContainer;
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
	}


	public void setLastClickedWidgetInfo(WidgetDescriptor lastClickedWidgetInfo)
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
  
  
  

	UserActionGui actionShowWidgetInfos = new UserActionGui()
	{

		@Override public void userActionGui(
			String sCmd
		, WidgetDescriptor infos, Object... params
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
	
	
  protected GuiRectangle getRectangleBounds()
  { return calcPosAndSize(yPos, yPosFrac, xPos, xPosFrac, this.yIncr, ySizeFrac, this.xIncr, xSizeFrac);
  }
  
  

	
  protected GuiRectangle calcPosAndSize(int line, int yPosFrac, int column, int xPosFrac, int dy, int ySizeFrac, int dx, int xSizeFrac)
  {
    if(line == 19 && yPosFrac == 5)
      stop();
    //use values from class if parameter are non-valid.
    if(line <=0){ line = this.yPos; yPosFrac = this.yPosFrac; }
    if(column <=0){ column = this.xPos; xPosFrac = this.xPosFrac; }
    if(dy <=0){ dy = this.yIncr; ySizeFrac = this.ySizeFrac; }
    if(dx <=0){ dx = this.xIncr; xSizeFrac = this.xSizeFrac; }
    //
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    int xPixelSize, yPixelSize;  
    xPixelSize = xPixelUnit * dx + propertiesGui.xPixelFrac(xSizeFrac) -2 ;
    yPixelSize = yPixelUnit * dy + propertiesGui.yPixelFrac(ySizeFrac) -2;
    int xPixel = (int)(column * xPixelUnit) + propertiesGui.xPixelFrac(xPosFrac) +1;
    int yPixel = (int)(line * yPixelUnit) + propertiesGui.yPixelFrac(yPosFrac) +1;
    if(yOrigin == 'b'){
      yPixel -= yPixelSize +1; //line is left bottom, yPixel is left top.
    }
    if(xOrigin == 'r'){
      xPixel -= xPixelSize +1; //yPos is left bottom, yPixel is left top.
    }
    if(yPixel < 1){ yPixel = 1; }
    if(xPixel < 1){ xPixel = 1; }
    GuiRectangle rectangle = new GuiRectangle(xPixel, yPixel, xPixelSize, yPixelSize);
    xPosPrev = xPos;    //save to support access to the last positions.
    yPosPrev = yPos;
    //set the next planned position:
    switch(directionOfNextElement){
    case 'r': xPos += xIncr; break;
    case 'l': xPos -= xIncr; break;
    case 'u': yPos -= yIncr; break;
    case 'd': yPos += yIncr; break;
    }
    return rectangle;
  }
  
  void stop(){}
	

	
}
