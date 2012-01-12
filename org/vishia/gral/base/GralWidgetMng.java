package org.vishia.gral.base;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.swt.widgets.TableItem;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgWriter;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralMngApplAdapter_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.InfoBox;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;

/**This is the base class of the GuiPanelMng for several Graphic-Adapters (Swing, SWT etc.). 
 * It contains the independent parts. 
 * The GuiPanelMng is a common approach to work with graphical interface simply, 
 * it is implemented by the several graphic-system-supporting classes
 * <ul>
 * <li>{@link org.vishia.gral.swt.SwtWidgetMng}
 * <li>{@link org.vishia.gral.swt.SwtWidgetMng.GuiPanelMngSwt}
 * </ul>
 * to offer a unique interface to work with simple graphic applications.
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 * @param <WidgetTYPE> The special base type of the composed widgets in the underlying graphic adapter specialization.
 *                     (SWT: Composite)
 */
public abstract class GralWidgetMng implements GralGridBuild_ifc, GralPanelMngWorking_ifc
{
  /**Changes:
   * <ul>
   * <li>2012-01-14 Hartmut chg: {@link #registerWidget(GralWidget)}: uses {@link GralPanelContent#addWidget(GralWidget, boolean)}.
   * <li>2012-01-14 Hartmut new {@link #getValueFromWidget(GralWidget)} implementing here for non-platform depending values, especially GralTable.
   * <li>2011-12-26 Hartmut new {@link #setApplicationAdapter(GralMngApplAdapter_ifc)} to support context sensitive help by focusGained of widgets.
   * <li>2011-11-18 Hartmut new {@link #getWidgetOnMouseDown()} to get the last clicked widget in any user routine.
   *   The information about the widget can be used to capture widgets for any script.
   * <li>2011-11-17 Hartmut new addText(String) as simple variant.  
   * <li>2011-11-14 Hartmut bugfix: copy values of GralTabbedPanel.pos to this.pos instead set the reference from GralWidgetMng to GralTabbedPanel.pos 
   *   The bug effect was removed panel in GralTabbedPanel.pos because it was the same instance as GralWidgetMng.
   * <li>2011-10-01 Hartmut chg: move {@link #registerPanel(GralPanelContent)} from the SWT implementation to this.
   * <li>2011-10-01 Hartmut new: method {@link #getPanel(String)} to get a registered panel by name.    
   * <li>2011-09-30 Hartmut new: {@link #actionDesignEditField}. It is the action which is called from menu now.
   * <li>2011-09-29 Hartmut chg: {@link #calcWidgetPosAndSize(GralPos, int, int, int, int)}: calculates dy and dx one pixel less.
   * <li>2011-09-23 Hartmut chg: All implementation routines for positioning are moved to the class {@link GralPos}. This class contains only wrappers now.
   * <li>2011-09-18 Hartmut chg: Inner static class GuiChangeReq now stored in an own class {@link GralWidgetChangeRequ}.
   * <li>2011-09-18 Hartmut new: : {@link GralPos#setFinePosition(int, int, int, int, int, int, int, int, int, char, GralPos)} calculates from right or bottom with negative values.                            
   * <li>2011-09-10 Hartmut chg: Renaming this class, old name was GuiMngBase.                             
   * <li>2011-09-10 Hartmut chg: Some routines form SWT implementation moved to this base class. It doesn't depends on the underlying graphic base.                            
   * <li>2011-08-13 Hartmut chg: New routines for store and calculate the position to regard large widgets.
   * </ul>
   */
  public final static int version = 0x20111227;
  
	/**This class is used for a selection field for file names and pathes. */
  protected class FileSelectInfo
  {
    public List<String> listRecentFiles;
    public String sRootDir;
    public String sLocalDir;
    public String sMask;
    public String sTitle;
    public final GralFileDialog_ifc dialogFile;
    public final GralWidget dstWidgd;
    
    
    public FileSelectInfo(String sTitle, List<String> listRecentFiles, String startDirMask, GralWidget dstWidgd)
    { this.listRecentFiles = listRecentFiles;
      this.dstWidgd = dstWidgd;
      this.sTitle = sTitle;
      int posColon = startDirMask.indexOf(':',2);  //regard : after windows drive letter.
      String sLocalDir1;
      if(posColon >=0){
        this.sRootDir = startDirMask.substring(0, posColon) + "/";
        sLocalDir1 = startDirMask.substring(posColon+1);
      } else {
        this.sRootDir = "";
        sLocalDir1 = startDirMask;
      }
      int mode;
      String sMask1;
      int posSlash = sLocalDir1.lastIndexOf('/');
      if(posSlash == sLocalDir1.length()-1){ //last is slash
        mode = GralFileDialog_ifc.directory;
        sMask1 = "";
      } else {
        mode = 0;
        sMask1 = sLocalDir1.substring(posSlash+1);
        if(sMask1.indexOf('*') >=0){ //contains an asterix
          sLocalDir1 = posSlash >=0 ? sLocalDir1.substring(0, posSlash) : "";
        } else {
          sMask1 = null;  //no mask, sLocalDir is a directory.  
        }
      }
      this.sMask = sMask1;
      this.sLocalDir = sLocalDir1;
      this.dialogFile = createFileDialog();
      this.dialogFile.open(sTitle, mode);
    }
    
  }
  
  
  
  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  //GuiDialogZbnfControlled dialogZbnfConfigurator;   
  GralCfgBuilder cfgBuilder;
  
  GralCfgWriter cfgWriter;
  
  /**The designer is an aggregated part of the PanelManager, but only created if necessary. 
   * TODO check whether it should be disposed to {@link #gralDevice} .*/
  protected GralCfgDesigner designer;
  
  private GralCfgData cfgData;
  
  public boolean bDesignMode = false;
  
  /**Some actions may be processed by a user implementation. */
  //private final GuiPlugUser_ifc user;
  
  protected boolean bDesignerIsInitialized = false;
  
  final GralWidgetMng parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  public GralGraphicThread gralDevice;

  /**Properties of this Dialog Window. */
  public  final GralGridProperties propertiesGui;

  /**Index of all input fields to access symbolic for all panels. */
  protected final Map<String, GralWidget> indexNameWidgets = new TreeMap<String, GralWidget>();

  /**Index of all input fields to access symbolic. NOTE: The generic type of WidgetDescriptor is unknown,
   * because the set is used independently from the graphic system. */
  protected final Map<String, GralWidget> showFields = new TreeMap<String, GralWidget>();

  //private final IndexMultiTable showFieldsM;

  private List<GralWidget> widgetsInFocus = new LinkedList<GralWidget>();
 
  
  /**List of all panels which may be visible yet. 
   * The list can be iterated. Therefore it is lock-free multi-threading accessible.
   */
  protected final ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listVisiblePanels = new ConcurrentLinkedQueue<GralVisibleWidgets_ifc>();
  
  /**It is possible to write any message via this class to a logging system.
   * All internal methods of gral writes exceptions to that logging system instead abort the execution of the application.
   */
  public final LogMessage log;
  
  
  protected GralMngApplAdapter_ifc applAdapter;
  
	protected final VariableContainer_ifc variableContainer;
	
	
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
  public GralPos pos = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction of {@link #pos.dirNext}. */
  protected boolean posUsed;
  
  /**Position for the next widget to store.
   * The Position values may be negative which means measurement from right or bottom.
   */
  protected GralPos posWidget = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  
  
  
  /**Saved last use position. After calling {@link #setPosAndSize_(Control, int, int, int, int)}
   * the xPos and yPos are setted to the next planned position.
   * But, if a new position regarded to the last given one is selected, the previous one is need.
   */
  //protected int xPosPrev, xPosPrevFrac, yPosPrev, yPosPrevFrac;
  
  /**width and height for the next element. If a value */
  //protected int xSize, xSizeFrac, ySize, ySizeFrac;
  
  /**'l' - left 'r'-right, 't' top 'b' bottom. */ 
  //protected char xOrigin = 'l', yOrigin = 'b';
  
  //protected char pos.dirNext = 'r';
  
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
	public static GralWidgetMng createWindow(String graphicBaseSystem)
	{ Class<GralWidgetMng> mngClass;
		GralWidgetMng mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GralWidgetMng>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GralWidgetMng> ctor = mngClass.getConstructor();
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
	
  @Override public void setPositionSize(int line, int column, int height, int width, char direction)
  { if(line < 0){ line = posUsed? GralPos.next: GralPos.same; }
    if(column < 0){ column = posUsed? GralPos.next: GralPos.same; }
    setFinePosition(line, 0, height + GralPos.size, 0, column, 0, width + GralPos.size, 0, 1, direction, 0 ,0 , pos);
  }

  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { setPosition(pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
  }


  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  {
    pos.setPosition(pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
    posUsed = false;
  }


  @Override public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  {
      pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
      posUsed = false;
  }
  
  @Override public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  {
      pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
      posUsed = false;
  }
  
  @Override public void setFinePosition(int line, int yPosFrac, int ye, int yef
    , int column, int xPosFrac, int xe, int xef, int origin, char direction, int border, int borderFrac, GralPos frame)
  {
    pos.setFinePosition(line, yPosFrac, ye, yef, column, xPosFrac, xe, xef, origin, direction, border, borderFrac, frame);
    posUsed = false;
  }
  
  
  @Override public void setSize(int height, int ySizeFrac, int width, int xSizeFrac)
  {
    pos.setSize(height, ySizeFrac, width, xSizeFrac);  //NOTE: setSize sets the next pos 
    posUsed = false;
  }
  
  void setSize(float height, float width)
  { pos.setSize(height, width, pos);
    posUsed = false;
  }
  
  /**Sets the position to the next adequate the {@link #pos.dirNext}. */
  public void setNextPosition()
  { pos.setNextPosition();
  }  
  
  @Override public GralPos getPositionInPanel(){ return pos.clone(); }
	
  /**Map of all panels. A panel may be a dialog box etc. */
  protected final Map<String,GralPanelContent> panels = new TreeMap<String,GralPanelContent>();
  
  /**Any kind of TabPanel for this PanelManager TODO make protected
   */
  public GralTabbedPanel currTabPanel;
  
  //public GralPanelContent currPanel;
  
  protected String sCurrPanel;
  
  protected GralWidget lastClickedWidgetInfo;
  

	@Override public Queue<GralWidget> getListCurrWidgets(){ return pos.panel.widgetList; }
	
  /**Index of all user actions, which are able to use in Button etc. 
   * The user action "showWidgetInfos" defined here is added initially.
   * Some more user-actions can be add calling {@link #registerUserAction(String, GralUserAction)}. 
   * */
  protected final Map<String, GralUserAction> userActions = new TreeMap<String, GralUserAction>();
  //private final Map<String, ButtonUserAction> userActions = new TreeMap<String, ButtonUserAction>();
  
  /**Index of all Tables, which are representable. */
  //private final Map<String, Table> userTableAccesses = new TreeMap<String, Table>();
  
	
	
  public GralWidgetMng(GralGridProperties props, VariableContainer_ifc variableContainer, LogMessage log)
	{ this.parent = null;
	  this.propertiesGui = props;
		this.log = log;
		this.variableContainer = variableContainer;
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
	}
  
  
  
  /**Returns null if the widget value can only be gotten platform-depending.
   * The platform widget manager should override this method too and invoke super.getValueFromWidget()
   * to call this method. If it returns a value, then it is ok.
   * A user invocation calls the overridden platform depending method automatically.
   * <br>
   * See {@link org.vishia.gral.swt.SwtWidgetMng}.   
   */
  @Override public String getValueFromWidget(GralWidget widgd)
  { String sValue = null;
    if(widgd instanceof GralTable){
      StringBuilder u = new StringBuilder();
      GralTableLine_ifc line = ((GralTable)widgd).getCurrentLine();
      String[] texts = line.getCellTexts();
      for(int iCol = 0; iCol < texts.length; ++iCol){
        u.append(texts[iCol]).append('\t');
      }
      sValue = u.toString();
    }
    return sValue;
  }

  public void setApplicationAdapter(GralMngApplAdapter_ifc adapter){ this.applAdapter = adapter; }
  

  public void setGralDevice(GralGraphicThread device)
  {
    this.gralDevice = device;
  }
  
 
  
  @Override public String setInfoDelayed(GralWidget_ifc widgd, int cmd, int ident, Object visibleInfo, Object userData, int delay){
    GralWidgetChangeRequ requ = new GralWidgetChangeRequ(widgd, cmd, ident, visibleInfo, userData);
    return setInfoDelayed(requ, delay);
  }
  
  @Override public String setInfoDelayed(GralWidgetChangeRequ changeRequ, int delay){
    //TODO check admissibility
    changeRequ.delayExecution(delay);
    gralDevice.addChangeRequest(changeRequ);
    return null;
  }
  


  
  
  /**selects a registered panel for the next add-operations. 
   */
  @Override public void selectPanel(String sName){
    pos.panel = panels.get(sName);
    sCurrPanel = sName;
    if(pos.panel == null && currTabPanel !=null) {
      //use the position of the current tab panel for the WidgetMng. Its panel is the parent.
      pos.set(currTabPanel.pos);  
      pos.panel = currTabPanel.addGridPanel(sName, /*"&" + */sName,1,1,10,10);
      panels.put(sName, pos.panel);  //TODO unnecessay, see addGridPanel
      log.sendMsg(0, "GuiPanelMng:selectPanel: unknown panel name %s", sName);
      //Note: because the pos.panel is null, not placement will be done.
    }
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
  }
  
  

  
  @Override public GralWidget getWidget(String name)
  { return indexNameWidgets.get(name);
  }
  
  
  
  @Override public void buildCfg(GralCfgData data, File fileCfg) //GuiCfgBuilder cfgBuilder)
  {
    this.cfgData = data;
    File currentDir = fileCfg.getParentFile();
    this.cfgBuilder = new GralCfgBuilder(cfgData, this, currentDir);
    cfgBuilder.buildGui(log, 0);
    this.designer = new GralCfgDesigner(cfgBuilder, this, log);  
    this.bDesignMode = true;
  }

  /**Sets or resets the design mode. The design mode allows to change the content.
   * @param mode
   */
  @Override public void setDesignMode(boolean mode){ this.bDesignMode = mode; }
  
  /**Saves the given configuration.
   * @param dest
   * @return
   */
  @Override public String saveCfg(Writer dest)
  { cfgWriter = new GralCfgWriter(log);
    String sError = cfgWriter.saveCfg(dest, cfgData);
    return sError;
  }
  

  
  
	/**This method is called whenever the left mouse is pressed on a widget, whiches 
	 * @param lastClickedWidgetInfo
	 */
	public void setLastClickedWidgetInfo(GralWidget lastClickedWidgetInfo)
	{
		this.lastClickedWidgetInfo = lastClickedWidgetInfo;
	}
	
	
	/**Returns that widget which was clicked by mouse at last. This method is usefully for debugging
	 * and for special functionality. A widget which {@link GralWidget#setDataPath(String)} is initialized
	 * with "widgetInfo" is not captured for this operation. It means, if any user action method uses
	 * this method to get the last clicked widget, that widget itself have to be marked with
	 * <b>setDataPath("widgetInfo");</b> to prevent getting its own widget info.  
	 * @return The last clicked widget
	 */
	public GralWidget getWidgetOnMouseDown(){ return lastClickedWidgetInfo; }
	
	
  /**Registers all user actions, which are able to use in Button etc.
   * The name is related with the <code>userAction=</code> designation in the configuration String.
   * @param name
   * @param action
   */
  @Override public void registerUserAction(String name, GralUserAction action)
  {
    userActions.put(name, action);
  }
  
  @Override public GralUserAction getRegisteredUserAction(String name)
  {
    return userActions.get(name);
  }
  
  
  @Override public void registerWidget(GralWidget widgd)
  {
    GralPanelContent panel = widgd.pos !=null ? widgd.pos.panel : this.pos.panel;
    if(widgd.name != null){
      indexNameWidgets.put(widgd.name, widgd);
    }
    //only widgets with size from right TODO percent size too.
    boolean toResize = pos.x.p1 < 0 || pos.x.p2 <= 0 || pos.y.p1< 0 || pos.y.p2 <=0; 
    panel.addWidget(widgd, toResize);
    
  }
  
  /**Registers a panel to place the widgets. 
   * After registration, the panel can be selected
   * with its name calling the {@link #selectPanel(String)} -routine
   * @param name Name of the panel.
   * @param panel The panel.
   */
  @Override public void registerPanel(GralPanelContent panel){
    panels.put(panel.namePanel, panel);
    pos.panel = panel;
    //initialize the position because its a new panel. The initial position is the whole panel.
    pos.setFinePosition(0,0,0,0,0,0,0,0,0,'d',0,0,pos);
    sCurrPanel = panel.namePanel;
  }
  
  
  public GralPanelContent getPanel(String name){
    return panels.get(name);
  }
  
  
  public GralPanelActivated_ifc actionPanelActivate = new GralPanelActivated_ifc()
  { @Override public void panelActivatedGui(Queue<GralWidget> widgetsP)
    {  //changeWidgets(widgetsP);
    }
  };


  
  protected void checkAdmissibility(boolean value){
    if(!value){
      throw new IllegalArgumentException("failure");
    }
  }
  
  
  /**Sets the background color of any widget. The widget may be for example:
   * <ul>
   * <li>a Table: Then a new line will be colored. 
   * <li>a Tree: Then a new leaf is colored.
   * <li>a Text-edit-widget: Then the field background color is set.
   * </ul>
   * The color is written into a queue, which is red in another thread. 
   * It may be possible too, that the GUI is realized in another module, maybe remote.
   * It means, that a few milliseconds should be planned before the change appears.
   * If the thread doesn't run or the remote receiver isn't present, 
   * than the queue may be overflowed or the request may be lost.
   *    
   * @param name The name of the widget, which was given by the add...()-Operation
   * @param ident A identifying number. It meaning depends on the kind of widget.
   *        0 means, insert on top.  Integer.MAXVALUE means, insert after the last element (append).
   * @param content The content to insert.
   * @return
   */
  public void setBackColor(String name, int ix, int color)
  {
    GralWidget descr = indexNameWidgets.get(name);
    if(descr == null){
      log.sendMsg(0, "GuiMainDialog:setBackColor: unknown widget %s", name);
    } else {
      setBackColor(descr, ix, color);
    }
  } 
  
  
  /**Sets the background color of any widget. The widget may be for example:
   * <ul>
   * <li>a Table: Then a new line will be colored. 
   * <li>a Tree: Then a new leaf is colored.
   * <li>a Text-edit-widget: Then the field background color is set.
   * </ul>
   * The color is written into a queue, which is red in another thread. 
   * It may be possible too, that the GUI is realized in another module, maybe remote.
   * It means, that a few milliseconds should be planned before the change appears.
   * If the thread doesn't run or the remote receiver isn't present, 
   * than the queue may be overflowed or the request may be lost.
   *    
   * @param name The name of the widget, which was given by the add...()-Operation
   * @param ident A identifying number. It meaning depends on the kind of widget.
   *        0 means, insert on top.  Integer.MAXVALUE means, insert after the last element (append).
   * @param content The content to insert.
   * @return
   */
  @Override public void setBackColor(GralWidget descr1, int ix, int color)
  { @SuppressWarnings("unchecked") //casting from common to specialized: only one type of graphic system is used.
    GralWidget descr = (GralWidget) descr1;
    setInfo(descr, GralPanelMngWorking_ifc.cmdBackColor, ix, color, null);
  } 
  
  
  @Override public void setLineColor(GralWidget descr1, int ix, int color)
  { @SuppressWarnings("unchecked") //casting from common to specialized: only one type of graphic system is used.
    GralWidget descr = (GralWidget) descr1;
    setInfo(descr, GralPanelMngWorking_ifc.cmdLineColor, ix, color, null);
  } 
  
  
  @Override public void setTextColor(GralWidget descr1, int ix, int color)
  { @SuppressWarnings("unchecked") //casting from common to specialized: only one type of graphic system is used.
    GralWidget descr = (GralWidget) descr1;
    setInfo(descr, GralPanelMngWorking_ifc.cmdTextColor, ix, color, null);
  } 
  
  
  @Override public void setLed(GralWidget widgetDescr, int colorBorder, int colorInner)
  {
    @SuppressWarnings("unchecked") //casting from common to specialized: only one type of graphic system is used.
    GralWidget descr = (GralWidget) widgetDescr;
    setInfo(descr, GralPanelMngWorking_ifc.cmdColor, colorBorder, colorInner, null);
    
  }
  

  @Override public ConcurrentLinkedQueue<GralVisibleWidgets_ifc> getVisiblePanels()
  {
    return listVisiblePanels;
  }


  
  
  @Override public boolean setFocus(GralWidget widgd)
  {
    return widgd.setFocus();
  }
  
  @Override public void notifyFocus(GralWidget widgd)
  {
    synchronized(widgetsInFocus){
      widgetsInFocus.remove(widgd);  //remove it anywhere inside
      widgetsInFocus.add(0, widgd);     //add at start.
    }
  }
  
  @Override public GralWidget getWidgetInFocus(){ return widgetsInFocus.size() >0 ? widgetsInFocus.get(0) : null; }
  
  @Override public List<GralWidget> getWidgetsInFocus(){ return widgetsInFocus; }
  
  @Override public int getColorValue(String sColorName){ return propertiesGui.getColorValue(sColorName); }

  @Override public GralColor getColor(String sColorName){ return propertiesGui.color(getColorValue(sColorName)); }



  

	GralUserAction actionShowWidgetInfos = new GralUserAction()
	{

		@Override public boolean userActionGui(
			String sCmd
		, GralWidget infos, Object... params
		)
		{ 
			if(lastClickedWidgetInfo !=null){
				log.sendMsg(Report.info, "widget %s, datapath=%s"
					, GralWidgetMng.this.lastClickedWidgetInfo.name
					, GralWidgetMng.this.lastClickedWidgetInfo.getDataPath());
	      return true;
			} else {
				log.sendMsg(0, "widgetInfo - no widget selected");
			}
      return false;
		}
		
	};
	
	
  
  

	
  
  /**Calculates the position and size of a widget
   * @param posWidget The position.
   * @param widthParentPixel The size of the panel, where the widget is member of
   * @param heightParentPixel The size of the panel, where the widget is member of
   * @return A rectangle for setBounds.
   */
  protected GralRectangle calcWidgetPosAndSize(GralPos posWidget, 
      int widthParentPixel, int heightParentPixel,
      int widthWidgetNat, int heightWidgetNat)
  {
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    final int x1,y1, x2, y2;
    x1 = xPixelUnit * posWidget.x.p1 + propertiesGui.xPixelFrac(posWidget.x.p1Frac)  //negative if from right
       + (posWidget.x.p1 < 0 ? widthParentPixel : 0);  //from right
    y1 = yPixelUnit * posWidget.y.p1 + propertiesGui.yPixelFrac(posWidget.y.p1Frac)  //negative if from right
       + (posWidget.y.p1 < 0 ? heightParentPixel : 0);  //from right
    if(posWidget.x.p2 == GralPos.useNatSize){
      x2 = x1 + widthWidgetNat; 
    } else {
      x2 = xPixelUnit * posWidget.x.p2 + propertiesGui.xPixelFrac(posWidget.x.p2Frac)  //negative if from right
         + (posWidget.x.p2 < 0 || posWidget.x.p2 == 0 && posWidget.x.p2Frac == 0 ? widthParentPixel : 0);  //from right
    }
    if(posWidget.x.p2 == GralPos.useNatSize){
      y2 = y1 + heightWidgetNat; 
    } else {
      y2 = yPixelUnit * posWidget.y.p2 + propertiesGui.yPixelFrac(posWidget.y.p2Frac)  //negative if from right
         + (posWidget.y.p2 < 0  || posWidget.y.p2 == 0 && posWidget.y.p2Frac == 0 ? heightParentPixel : 0);  //from right
    }
    GralRectangle rectangle = new GralRectangle(x1, y1, x2-x1-1, y2-y1-1);
    return rectangle;
  }
  
  
  
  
  @Override public GralTextField addFileSelectField(String name, List<String> listRecentFiles
    , String startDirMask, String prompt, String promptStylePosition)
  { //int xSize1 = xSize;
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralPos posAll = getPositionInPanel(); //saved whole position.
    //reduce the length of the text field:
    setPosition(GralPos.same, GralPos.same, GralPos.same, GralPos.same -2.0F, 1, 'r');
    
    //xSize -= ySize;
    GralTextField widgd = addTextField(name, true, prompt, promptStylePosition );
    setSize(posAll.height(), 2.0F);
    //xPos += xSize;
    //xSize = ySize;
    GralWidget widgdSelect = addButton(name + "<", actionFileSelect, "", null, null, "<");
    FileSelectInfo fileSelectInfo = new FileSelectInfo(name, listRecentFiles, startDirMask, widgd);
    widgdSelect.setContentInfo(fileSelectInfo); 
    //xSize = xSize1;
    pos = posAll;  //the saved position.
    return widgd;
  }

  
  
  GralUserAction actionFileSelect = new GralUserAction()
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    { assert(false);
      return userActionGui(null, infos);
    }
  
    @Override public boolean userActionGui(int actionCode, GralWidget widgg, Object... params) 
    {
      FileSelectInfo fileSelectInfo = (FileSelectInfo)widgg.getContentInfo();
      if(fileSelectInfo.listRecentFiles !=null){
        stop();
      } else {
        fileSelectInfo.dialogFile.show(fileSelectInfo.sRootDir, fileSelectInfo.sLocalDir
          , fileSelectInfo.sMask, fileSelectInfo.sTitle);
        String fileSelect = fileSelectInfo.dialogFile.getSelection(); 
        if(fileSelect !=null){
          fileSelectInfo.dstWidgd.setValue(cmdSet, 0, fileSelect);
          GralUserAction actionSelect = fileSelectInfo.dstWidgd.getActionChange();
          if(actionSelect !=null){
            actionSelect.userActionGui(KeyCode.menuEntered, fileSelectInfo.dstWidgd, fileSelect);
          }
        }
      }
      return true;      
    }; 
    
  };
  
  

  /**Action to edit the properties of one widget in the graphic. */
  public GralUserAction actionDesignEditField = new GralUserAction()
  { 
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        designer.editFieldProperties(widgd, null);
      }
      return true;
    }
    
    @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    {
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        designer.editFieldProperties(widgd, null);
      }
      return true;
    }
  };


  

  /**Action to edit the properties of one widget in the graphic. */
  public GralUserAction actionReadPanelCfg = new GralUserAction()
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    {
      //GralPanelContent currPanel = 
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        GralPanelContent panel = widgd.pos.panel;
        String namePanel = panel.namePanel;
        cfgBuilder.buildGui(log, 0);
        //designer.editFieldProperties(widgd, null);
      }
      return true;
    }
  };


  

  
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void pressedLeftMouseDownForDesign(GralWidget widgd, GralRectangle xy)
  { designer.pressedLeftMouseDownForDesign(widgd, xy);
  }
  
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void releaseLeftMouseForDesign(GralWidget widgd, GralRectangle xy, boolean bCopy)
  { designer.releaseLeftMouseForDesign(widgd, xy, bCopy);
  }
  
  public void markWidgetForDesign(GralWidget widgg){
    designer.markWidgetForDesign(widgg);
  }
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void XXXpressedRightMouseDownForDesign(GralWidget widgd, GralRectangle xy)
  { designer.editFieldProperties(widgd, xy);
  }
  
  
  @Override public InfoBox createTextInfoBox(String name, String title)
  {
    return InfoBox.createTextInfoBox(this, name, title);
  }

  
  @Override public InfoBox createHtmlInfoBox(String name, String title)
  {
    return InfoBox.createHtmlInfoBox(this, name, title);
  }

  
  /**Adds a text to the current panel at given position with standard colors, left origin.
   * The size of text is calculated using the height of positioning values.
   * @param text
   */
  @Override public GralWidget addText(String text)
  { return addText(text, 0, GralColor.getColor("bk"), GralColor.getColor("wh"));
  }
  
  
  @Override public void writeLog(int msgId, Exception exc)
  {
    String sMsg = exc.toString();
    StackTraceElement[] stackTrace = exc.getStackTrace();
    String sWhere = stackTrace[0].getFileName() + ":" + stackTrace[0].getLineNumber();
    log.sendMsg(msgId, sMsg + " @" + sWhere);
  }
  
  
  void stop(){}
	

	
}
