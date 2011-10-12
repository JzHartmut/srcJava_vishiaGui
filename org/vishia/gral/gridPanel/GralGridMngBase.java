package org.vishia.gral.gridPanel;

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

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralPrimaryWindow;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgWriter;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.InfoBox;
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
public abstract class GralGridMngBase implements GralGridBuild_ifc, GralPanelMngWorking_ifc
{
  /**Changes:
   * <ul>
   * <li>2011-10-01 Hartmut chg: move {@link #registerPanel(GralPanelContent)} from the SWT implementation to this.
   * <li>2011-10-01 Hartmut new: method {@link #getPanel(String)} to get a registered panel by name.    
   * <li>2011-09-30 Hartmut new: {@link #actionDesignEditField}. It is the action which is called from menu now.
   * <li>2011-09-29 Hartmut chg: {@link #calcWidgetPosAndSize(GralGridPos, int, int, int, int)}: calculates dy and dx one pixel less.
   * <li>2011-09-23 Hartmut chg: All implementation routines for positioning are moved to the class {@link GralGridPos}. This class contains only wrappers now.
   * <li>2011-09-18 Hartmut chg: Inner static class GuiChangeReq now stored in an own class {@link GralWidgetChangeRequ}.
   * <li>2011-09-18 Hartmut new: : {@link GralGridPos#setFinePosition(int, int, int, int, int, int, int, int, int, char, GralGridPos)} calculates from right or bottom with negative values.                            
   * <li>2011-09-10 Hartmut chg: Renaming this class, old name was GuiMngBase.                             
   * <li>2011-09-10 Hartmut chg: Some routines form SWT implementation moved to this base class. It doesn't depends on the underlying graphic base.                            
   * <li>2011-08-13 Hartmut chg: New routines for store and calculate the position to regard large widgets.
   * </ul>
   */
  public final static int version = 0x20111001;
  
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
  
  final GralGridMngBase parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  final protected GralPrimaryWindow gralDevice;

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
  
  protected final LogMessage log;
  
	protected final VariableContainer_ifc variableContainer;
	
	
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
  public GralGridPos pos = new GralGridPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction of {@link #pos.dirNext}. */
  protected boolean posUsed;
  
  /**Position for the next widget to store.
   * The Position values may be negative which means measurement from right or bottom.
   */
  protected GralGridPos posWidget = new GralGridPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  
  
  
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
	public static GralGridMngBase createWindow(String graphicBaseSystem)
	{ Class<GralGridMngBase> mngClass;
		GralGridMngBase mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GralGridMngBase>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GralGridMngBase> ctor = mngClass.getConstructor();
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
  { if(line < 0){ line = posUsed? GralGridPos.next: GralGridPos.same; }
    if(column < 0){ column = posUsed? GralGridPos.next: GralGridPos.same; }
    setFinePosition(line, 0, height + GralGridPos.size, 0, column, 0, width + GralGridPos.size, 0, 1, direction, pos);
  }

  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { setPosition(pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
  }


  @Override public void setPosition(GralGridPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  {
      pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
      posUsed = false;
  }
  
  @Override public void setFinePosition(int line, int yPosFrac, int ye, int yef
    , int column, int xPosFrac, int xe, int xef, int origin, char direction, GralGridPos frame)
  {
    pos.setFinePosition(line, yPosFrac, ye, yef, column, xPosFrac, xe, xef, origin, direction, frame);
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
  
  @Override public GralGridPos getPositionInPanel(){ return pos.clone(); }
	
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
  
	
	
  public GralGridMngBase(GralPrimaryWindow device, GralGridMngBase parent
      , GralGridProperties props
      , VariableContainer_ifc variableContainer, LogMessage log)
	{ this.parent = parent;
	  if(parent == null){
	    gralDevice = device;
	  } else {
	    gralDevice = parent.gralDevice;
	  }
	  this.propertiesGui = props;
		this.log = log;
		this.variableContainer = variableContainer;
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
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
  

  
  
	public void setLastClickedWidgetInfo(GralWidget lastClickedWidgetInfo)
	{
		this.lastClickedWidgetInfo = lastClickedWidgetInfo;
	}
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
    if(pos.x < 0 || pos.xEnd <= 0 || pos.y< 0 || pos.yEnd <=0){ 
      //only widgets with size from right TODO percent size too.
      widgd.pos = pos.clone();
      //widgd.pos.set(pos);
      pos.panel.widgetsToResize.add(widgd);
    }
    indexNameWidgets.put(widgd.name, widgd);
    pos.panel.widgetList.add(widgd);
    
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
    return widgd.widget.setFocus();
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
					, GralGridMngBase.this.lastClickedWidgetInfo.name
					, GralGridMngBase.this.lastClickedWidgetInfo.getDataPath());
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
  protected GralRectangle calcWidgetPosAndSize(GralGridPos posWidget, 
      int widthParentPixel, int heightParentPixel,
      int widthWidgetNat, int heightWidgetNat)
  {
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    final int x1,y1, x2, y2;
    x1 = xPixelUnit * posWidget.x + propertiesGui.xPixelFrac(posWidget.xFrac)  //negative if from right
       + (posWidget.x < 0 ? widthParentPixel : 0);  //from right
    y1 = yPixelUnit * posWidget.y + propertiesGui.yPixelFrac(posWidget.yFrac)  //negative if from right
       + (posWidget.y < 0 ? heightParentPixel : 0);  //from right
    if(posWidget.xEnd == GralGridPos.useNatSize){
      x2 = x1 + widthWidgetNat; 
    } else {
      x2 = xPixelUnit * posWidget.xEnd + propertiesGui.xPixelFrac(posWidget.xEndFrac)  //negative if from right
         + (posWidget.xEnd < 0 || posWidget.xEnd == 0 && posWidget.xEndFrac == 0 ? widthParentPixel : 0);  //from right
    }
    if(posWidget.xEnd == GralGridPos.useNatSize){
      y2 = y1 + heightWidgetNat; 
    } else {
      y2 = yPixelUnit * posWidget.yEnd + propertiesGui.yPixelFrac(posWidget.yEndFrac)  //negative if from right
         + (posWidget.yEnd < 0  || posWidget.yEnd == 0 && posWidget.yEndFrac == 0 ? heightParentPixel : 0);  //from right
    }
    GralRectangle rectangle = new GralRectangle(x1, y1, x2-x1-1, y2-y1-1);
    return rectangle;
  }
  
  
  
  
  @Override public GralWidget addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, char promptStylePosition)
  { //int xSize1 = xSize;
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralGridPos posAll = getPositionInPanel(); //saved whole position.
    //reduce the length of the text field:
    setPosition(GralGridPos.same, GralGridPos.same, GralGridPos.same, GralGridPos.same -2.0F, 1, 'r');
    
    //xSize -= ySize;
    GralWidget widgd = addTextField(name, true, prompt, promptStylePosition );
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
    {
      FileSelectInfo fileSelectInfo = (FileSelectInfo)infos.getContentInfo();
      if(fileSelectInfo.listRecentFiles !=null){
        stop();
      } else {
        fileSelectInfo.dialogFile.show(fileSelectInfo.sRootDir, fileSelectInfo.sLocalDir
          , fileSelectInfo.sMask, fileSelectInfo.sTitle);
        String fileSelect = fileSelectInfo.dialogFile.getSelection(); 
        if(fileSelect !=null){
          fileSelectInfo.dstWidgd.setValue(cmdSet, 0, fileSelect);
        }
      }
      return true;
      
    }
    
  };
  
  

  /**Action to edit the properties of one widget in the graphic. */
  public GralUserAction actionDesignEditField = new GralUserAction()
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
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
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        designer.editFieldProperties(widgd, null);
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
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void pressedRightMouseDownForDesign(GralWidget widgd, GralRectangle xy)
  { designer.editFieldProperties(widgd, xy);
  }
  
  
  @Override public InfoBox createInfoBox(String name, String title)
  {
    return InfoBox.create(this, name, title);
  }

  
  
  void stop(){}
	

	
}
