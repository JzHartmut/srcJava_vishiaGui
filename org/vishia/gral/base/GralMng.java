package org.vishia.gral.base;

import java.io.File;
import java.io.IOException;
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
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgWriter;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMngApplAdapter_ifc;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;

/**This is the base class of the GuiPanelMng for several Graphic-Adapters (Swing, SWT etc.). 
 * It contains the independent parts. 
 * This class <code>GralMng</code> is a common approach to work with graphical interface simply, 
 * it is implemented by the several graphic-system-supporting classes
 * <ul>
 * <li>{@link org.vishia.gral.swt.SwtMng}
 * </ul>
 * to offer a unique interface to work with simple graphic applications.
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 */
public abstract class GralMng implements GralMngBuild_ifc, GralMng_ifc
{
  /**Changes:
   * <ul>
   * <li>2013-12-21 Hartmut new: {@link #setToPanel(GralWidget)} for all set to panel actions. That method handles all widget types. 
   * <li>2013-03-20 Hartmut adap: {@link #actionFileSelect} with setText(...), now the file select field was filled.
   * <li>2012-08-20 Hartmut new: {@link #getWidgetsPermanentlyUpdating()} created but not used yet because 
   *   {@link #refreshCurvesFromVariable(VariableContainer_ifc)} has the necessary functionality.
   * <li>2012-06-30 Hartmut new: Composition {@link #widgetHelper} The widget helper is implemented in the graphic system
   *   for example as {@link org.vishia.gral.swt.SwtWidgetHelper} to do some widget specific things.
   * <li>2012-06-30 Hartmut new: Composition {@link #_impl}.{@link InternalPublic#gralKeyListener}
   * <li>2012-04-22 Hartmut new: {@link #addLine(GralColor, List)} to add in a {@link GralCanvasStorage}.
   * <li>2012-04-01 Hartmut new: {@link #addDataReplace(Map)}, {@link #replaceDataPathPrefix(String)}.
   *   using alias in the {@link GralWidget#setDataPath(String)}. The resolving of the alias is done
   *   only if the datapath is used.   * <li>2012-03-17 Hartmut new: {@link #calcWidgetPosAndSize(GralPos, int, int)} as abstract method.
   * <li>2012-03-10 Hartmut chg: {@link #addText(String)} now uses the background color {@link GralGridProperties#colorBackground_}.
   * <li>2012-01-14 Hartmut chg: {@link #registerWidget(GralWidget)}: uses {@link GralPanelContent#addWidget(GralWidget, boolean)}.
   * <li>2012-01-14 Hartmut new {@link #getValueFromWidget(GralWidget)} implementing here for non-platform depending values, especially GralTable.
   * <li>2011-12-26 Hartmut new {@link #setApplicationAdapter(GralMngApplAdapter_ifc)} to support context sensitive help by focusGained of widgets.
   * <li>2011-11-18 Hartmut new {@link #getLastClickedWidget()} to get the last clicked widget in any user routine.
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
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 20120422;
  
	/**This class is used for a selection field for file names and paths. */
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
  
  //final GralMng parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  public final GralGraphicThread gralDevice;

  /**Properties of this Dialog Window. */
  public  final GralGridProperties propertiesGui;

  /**Index of all input fields to access symbolic for all panels. */
  protected final Map<String, GralWidget> indexNameWidgets = new TreeMap<String, GralWidget>();

  /**Index of all input fields to access symbolic. NOTE: The generic type of WidgetDescriptor is unknown,
   * because the set is used independently from the graphic system. */
  protected final Map<String, GralWidget> showFields = new TreeMap<String, GralWidget>();

  //private final IndexMultiTable showFieldsM;

  private final List<GralWidget> widgetsInFocus = new LinkedList<GralWidget>();
 
  
  /**List of all panels which may be visible yet. 
   * The list can be iterated. Therefore it is lock-free multi-threading accessible.
   */
  protected final ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listVisiblePanels = new ConcurrentLinkedQueue<GralVisibleWidgets_ifc>();
  
  
  Queue<GralWidget> listWidgetsPermanentUpdating = new LinkedList<GralWidget>();
  
  
  /**It is possible to write any message via this class to a logging system.
   * All internal methods of gral writes exceptions to that logging system instead abort the execution of the application.
   */
  public final LogMessage log;
  
  
  protected GralMngApplAdapter_ifc applAdapter;
  
	/**Composition of some curve view widgets, which should be filled indepentent of there visibility. */
	protected final List<GralCurveView_ifc> curveContainer = new LinkedList<GralCurveView_ifc>();
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
  protected GralPos pos = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction see {@link GralPos#setNextPosition()}. */
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
  
  
  /**
   * 
   */
  protected GralUserAction userMainKeyAction;
  
  
  //public final GralWidgetHelper widgetHelper;
  
	/**Creates an nee Panel Manager in a new Window.
	 * @param graphicBaseSystem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GralMng createWindow(String graphicBaseSystem)
	{ Class<GralMng> mngClass;
		GralMng mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GralMng>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GralMng> ctor = mngClass.getConstructor();
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
  
  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. */
  public void setNextPosition()
  { if(posUsed){
      pos.setNextPosition();
    }
    posUsed = true;
  }  
  
  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. */
  public void setNextPositionUnused()
  { if(posUsed){
      pos.setNextPosition();
    }
    posUsed = false;
  }  
  
  public void registerShowField(GralWidget widg){
    //link the widget with is information together.
    if(widg.name !=null){
      showFields.put(widg.name, widg);
    }

  }
  
  
  @Override public GralPos getPositionInPanel(){ return pos.clone(); }
  
  public GralPos getPosCheckNext(){ 
    if(posUsed){
      pos.setNextPosition();
      posUsed = false;
    }
    //posUsed = true;
    return pos.clone(); 
  }
	
  /**Map of all panels. A panel may be a dialog box etc. */
  protected final Map<String,GralPanelContent> panels = new TreeMap<String,GralPanelContent>();
  
  /**Any kind of TabPanel for this PanelManager TODO make protected
   */
  public GralTabbedPanel currTabPanel;
  
  //public GralPanelContent currPanel;
  
  protected String sCurrPanel;
  
  /**Last focused widget or last selected line in a table. 
   * This info can be used to get the last widget on a context menu etc. on another widget.
   * See {@link #getLastClickedWidget()}
   */
  private GralWidget_ifc lastClickedWidget;
  

	@Override public Queue<GralWidget> getListCurrWidgets(){ return pos.panel.widgetList; }
	
  /**Index of all user actions, which are able to use in Button etc. 
   * The user action "showWidgetInfos" defined here is added initially.
   * Some more user-actions can be add calling {@link #registerUserAction(String, GralUserAction)}. 
   * */
  protected final Map<String, GralUserAction> userActions = new TreeMap<String, GralUserAction>();
  //private final Map<String, ButtonUserAction> userActions = new TreeMap<String, ButtonUserAction>();
  
  /**Index of all Tables, which are representable. */
  //private final Map<String, Table> userTableAccesses = new TreeMap<String, Table>();
  
  /**Map of replacements of paths to data. Filled from ZBNF: DataReplace::= <$?key> = <$-/\.?string> */
  private final Map<String, String> dataReplace = new TreeMap<String,String>();


  private static GralMng singleton;
	
  public GralMng(GralGraphicThread device, GralGridProperties props, LogMessage log)
	{ this.gralDevice = device;
	  //this.widgetHelper = widgetHelper;
	  //if(widgetHelper !=null) { widgetHelper.setMng(this); }
    //this.parent = null;
	  this.propertiesGui = props;
		this.log = log;
    //its a user action able to use in scripts.
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
    GralMng.singleton = this; 
	}
  
  
  /**Returns the singleton or null if the GralMng is not instantiated yet.*/
  public static GralMng get(){ return singleton; }
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#setMainKeyAction(org.vishia.gral.ifc.GralUserAction)
   */
  @Override public GralUserAction setMainKeyAction(GralUserAction userMainKeyAction){
    GralUserAction last = this.userMainKeyAction;
    this.userMainKeyAction = userMainKeyAction;
    return last;
  }
  
  
  
  /**package private*/ GralUserAction userMainKeyAction(){ return userMainKeyAction; }
  
  
  @Override public GralGridProperties propertiesGui(){ return propertiesGui; }
  
  @Override public GralGraphicThread gralDevice(){ return gralDevice; }
  
  @Override public LogMessage log(){ return log; }


  
  /**Returns null if the widget value can only be gotten platform-depending.
   * The platform widget manager should override this method too and invoke super.getValueFromWidget()
   * to call this method. If it returns a value, then it is ok.
   * A user invocation calls the overridden platform depending method automatically.
   * <br>
   * See {@link org.vishia.gral.swt.SwtMng}.   
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
  

  public GralMngApplAdapter_ifc getApplicationAdapter(){ return applAdapter; } 
  
  
  /**It supports usage of an alias in the data path. See {@link #replaceDataPathPrefix(String)}.
   * @param src this map will added to the existing one.
   */
  @Override public void addDataReplace(final Map<String, String> src){
    dataReplace.putAll(src);    
  }
  
  /**It supports usage of an alias in the data path. See {@link #replaceDataPathPrefix(String)}.
   * @param alias Any shorter alias
   * @param value The complete value.
   */
  @Override public void addDataReplace(String alias, String value){
    dataReplace.put(alias, value);    
  }
  
  /**It supports usage of an alias in the data path.
   * @param path may contain "alias:restOfPath"
   * @return if "alias" is found in {@link #addDataReplace(String, String)} the it is replaced
   *   inclusively ":". If alias is not found, it is not replaced.
   *   Note that another meaning of "prefix:restOfPath" is possible.
   */
  @Override public String replaceDataPathPrefix(final String path)
  {
    String pathRet = path;
    int posSep = path.indexOf(':');
    if(posSep >=0){
      String sRepl = dataReplace.get(path.substring(0, posSep));
      if(sRepl !=null){
        pathRet = sRepl + path.substring(posSep+1);  //Note: sRepl may contain a ':', its the device.
      }
    }
    return pathRet;
  }
  

  
  
  public void setHtmlHelp(String url){
    if(applAdapter !=null){
      applAdapter.setHelpUrl(url);
    }
  }
  


  
  
  /**selects a registered panel for the next add-operations. 
   */
  @Override public void selectPanel(String sName){
    pos.panel = panels.get(sName);
    sCurrPanel = sName;
    if(pos.panel == null && currTabPanel !=null) {
      //use the position of the current tab panel for the WidgetMng. Its panel is the parent.
      pos.set(currTabPanel.pos());  
      pos.panel = currTabPanel.addGridPanel(sName, /*"&" + */sName,1,1,10,10);
      panels.put(sName, pos.panel);  //TODO unnecessay, see addGridPanel
      log.sendMsg(0, "GuiPanelMng:selectPanel: unknown panel name %s", sName);
      //Note: because the pos.panel is null, not placement will be done.
    }
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
  }
  
  
  @Override public boolean currThreadIsGraphic(){
    return Thread.currentThread().getId() == gralDevice.getThreadIdGui();
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
	public void setLastClickedWidget(GralWidget_ifc lastClickedWidgetInfo)
	{
		this.lastClickedWidget = lastClickedWidgetInfo;
	}
	
	
	/**Returns that widget which was clicked by mouse at last. This method is usefully for debugging
	 * and for special functionality. A widget which {@link GralWidget#setDataPath(String)} is initialized
	 * with "widgetInfo" is not captured for this operation. It means, if any user action method uses
	 * this method to get the last clicked widget, that widget itself have to be marked with
	 * <b>setDataPath("widgetInfo");</b> to prevent getting its own widget info.  
	 * @return The last clicked widget
	 */
	public GralWidget_ifc getLastClickedWidget(){ return lastClickedWidget; }
	
	
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
    GralPanelContent panel = widgd.pos() !=null ? widgd.pos().panel : this.pos.panel;
    if(widgd.name != null){
      indexNameWidgets.put(widgd.name, widgd);
    }
    //only widgets with size from right TODO percent size too.
    boolean toResize = pos.x.p1 < 0 || pos.x.p2 <= 0 || pos.y.p1< 0 || pos.y.p2 <=0; 
    panel.addWidget(widgd, toResize);
    
  }
  
  /**Registers a panel to place the widgets. 
   * After registration, this panel is the current one, stored in {@link #pos}. the panel can be selected
   * with its name calling the {@link #selectPanel(String)} -routine
   * @param key Name of the panel.
   * @param panel The panel.
   */
  @Override public void registerPanel(GralPanelContent panel){
    panels.put(panel.namePanel, panel);
    pos.panel = panel;
    //initialize the position because its a new panel. The initial position is the whole panel.
    pos.setFinePosition(0,0,0,0,0,0,0,0,0,'d',0,0,pos);
    sCurrPanel = panel.namePanel;
  }
  
  
  /**It is a special routine for tabbedPanel.
   * The reason: If a new tab should add in {@link GralTabbedPanel#addGridPanel(String, String, int, int, int, int)}
   * then the mng is set to the tabbed panel.
   * @param tabbedPanel
   */
  /*package private*/ void setTabbedPanel(GralPanelContent tabbedPanel){
    pos.panel = tabbedPanel;
  }
  
  
  public GralPanelContent getPanel(String name){
    return panels.get(name);
  }
  
  
  public abstract Object getCurrentPanel();
  
  
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
  
  
  private GralWidget findWidget(String name){
    GralWidget widg = indexNameWidgets.get(name);
    if(widg == null){
      log.sendMsg(0, "GuiMainDialog:setBackColor: unknown widget %s", name);
    }
    return widg;
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
  @Override public void setBackColor(String name, int ix, int color)
  {
    GralWidget widg;
    if( (widg = findWidget(name)) !=null){
      widg.setBackColor(GralColor.getColor(color), ix);
    }
  } 
  
  
  @Override public void setText(String name, CharSequence text){
    GralWidget widg;
    if( (widg = findWidget(name)) !=null){
      widg.setText(text);
    }
  }

  
  
  @Override public void setValue(String widget, Object visibleInfo, Object userData){
    GralWidget widg;
    if( (widg = findWidget(widget)) !=null){
      widg.setValue(0,0, visibleInfo, userData);
    }
    
  }
  
  @Override public void addText(String name, CharSequence text){
    GralWidget widg;
    if( (widg = findWidget(name)) !=null){
      if(widg instanceof GralTextBox_ifc){
        try{ ((GralTextBox)widg).append(text); }
        catch(IOException exc){ System.err.println("TODO"); }
      }
      else {
        System.err.println("GralMng - addText not possible;" + name);
      }
    }
  }

  
  @Deprecated @Override public void setBackColor(GralWidget widg, int ix, int color)
  { widg.setBackColor(GralColor.getColor(color), ix);
  } 
  
  
  @Deprecated @Override public void setLineColor(GralWidget widg, int ix, int color)
  { widg.setLineColor(GralColor.getColor(color), ix);
  } 
  
  
  @Deprecated @Override public void setTextColor(GralWidget widg, int ix, int color)
  { widg.setTextColor(GralColor.getColor(color));
  } 
  
  
  
  /**Adds a line.
   * <br><br>To adding a line is only possible if the current panel is of type 
   * {@link SwtCanvasStorePanel}. This class stores the line coordinates and conditions 
   * and draws it as background if drawing is invoked.
   * 
   * @param colorValue The value for color, 0xffffff is white, 0xff0000 is red.
   * @param xa start of line relative to current position in grid units.
   *          The start is relative to the given position! Not absolute in window! 
   * @param ya start of line relative to current position in grid units.
   * @param xe end of line relative to current position in grid units.
   * @param ye end of line relative to current position in grid units.
   */
  @Override public void addLine(int colorValue, float xa, float ya, float xe, float ye){
    //if(pos.panel.getPanelImpl() instanceof SwtCanvasStorePanel){
    if(pos.panel.canvas !=null){
      GralColor color = propertiesGui.color(colorValue);
      int xgrid = propertiesGui.xPixelUnit();
      int ygrid = propertiesGui.yPixelUnit();
      int x1 = (int)((pos.x.p1 + xa) * xgrid);
      int y1 = (int)((pos.y.p1 - ya) * ygrid);
      int x2 = (int)((pos.x.p1 + xe) * xgrid);
      int y2 = (int)((pos.y.p1 - ye) * ygrid);
      //Any panel which is created in the SWT-implementation is a CanvasStorePanel.
      //This is because lines should be drawn.
      //((SwtCanvasStorePanel) pos.panel.getPanelImpl()).store.drawLine(color, x1, y1, x2, y2);
      pos.panel.canvas.drawLine(color, x1, y1, x2, y2);
      //furtherSetPosition((int)(xe + 0.99F), (int)(ye + 0.99F));
    } else {
      log.sendMsg(0, "GuiPanelMng:addLine: panel is not a CanvasStorePanel");
    }
  }
  
  
  @Override public void addLine(GralColor color, List<GralPoint> points){
    if(pos.panel.canvas !=null){
      pos.panel.canvas.drawLine(pos, color, points);
    } else {
      log.sendMsg(0, "GralMng.addLine - panel is not a CanvasStorePanel;");
    }
  }
  

  
  @Override public void setLed(GralWidget widg, int colorBorder, int colorInner)
  {
    ((GralLed)widg).setColor(GralColor.getColor(colorBorder), GralColor.getColor(colorInner));
  }
  

  @Override public ConcurrentLinkedQueue<GralVisibleWidgets_ifc> getVisiblePanels()
  {
    return listVisiblePanels;
  }

  @Override public GralVisibleWidgets_ifc getWidgetsPermanentlyUpdating(){
    ///
    return null;
  }

  
  
  @Override public void setFocus(GralWidget widgd)
  {
    widgd.setFocus();
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


  /**Creates the context menu for the given widget for right-mouse pressing.
   * This method is invoked only in {@link GralWidget#getContextMenu()} whereby an existing
   * context menu is stored in the {@link GralWidget#contextMenu} association. 
   * The widget have to be set to panel already, an implementation widget have to be existing.
   * It means {@link GralWidget#getWidgetImplementation()} should be return that instance
   * where the menu is to be added.
   * This method is package protected because it should only be called internally.
   * @param widg The widget
   */
  protected abstract GralMenu createContextMenu(GralWidget widgg);
  
  
  /**Creates the menu bar for the given window.
   * This method is invoked only in {@link GralWindow#getMenuBar()} whereby an existing
   * menu bar is stored in the {@link GralWindow#menuBarGral} association. 
   * @param windg The window
   */
  protected abstract GralMenu createMenuBar(GralWindow windg);
  
  
  protected abstract GralMenu XXXaddPopupMenu(String sName);
  


	GralUserAction actionShowWidgetInfos = new GralUserAction("actionShowWidgetInfos")
	{

		@Override public boolean userActionGui(
			String sCmd
		, GralWidget infos, Object... params
		)
		{ 
			if(lastClickedWidget !=null){
				log.sendMsg(Report.info, "widget %s, datapath=%s"
					, GralMng.this.lastClickedWidget.getName()
					, GralMng.this.lastClickedWidget.getDataPath());
	      return true;
			} else {
				log.sendMsg(0, "widgetInfo - no widget selected");
			}
      return false;
		}
		
	};
	
	
	
	
  
  
  
  

	
	
	
  
  /**Calculates the pixel position and size with a given GralPos for the given size of display appearance.
   * @param pos Given position
   * @param widthParentPixel width of the container. This value will be used if the position is given 
   *   from right with negative numbers.
   * @param heightParentPixel height of the container. This value will be used if the position is given 
   *   from bottom with negative numbers.
   * @param widthWidgetNat natural width of the component which will be positioning. 
   *   This value is used only if the pos parameter contains {@link GralPos#useNatSize} for the xe-value
   * @param heightWidgetNat natural height of the component which will be positioning. 
   *   This value is used only if the pos parameter contains {@link GralPos#useNatSize} for the ye-value
   * @return The position and size relative in the container. 
   * @deprecated, use {@link #calcWidgetPosAndSizeSwt(GralPos, int, int)} because the parent is known in pos.
   */
  public GralRectangle calcWidgetPosAndSize(GralPos pos,
    int widthParentPixel, int heightParentPixel,
    int widthWidgetNat, int heightWidgetNat
  ){ return pos.calcWidgetPosAndSize(propertiesGui, widthParentPixel, heightParentPixel
                                    , widthWidgetNat, heightWidgetNat);
  }
  
  
  
  
  
  
  
  /**Calculates the bounds of a widget with a given pos independent of this {@link #pos}.
   * This method is a part of the implementing GralMng because the GralPos is not implemented for
   * any underlying graphic system and the {@link #propertiesGuiSwt} are used.
   * This method is not intent to use from an application, only for implementing methods of Gral.
   * Therefore it isn't a member of the {@link GralWindowMng_ifc} and {@link GralMngBuild_ifc}
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   * @return A rectangle with position and size.
   */
  public abstract GralRectangle calcWidgetPosAndSize(GralPos pos, int widthwidgetNat, int heigthWidgetNat);


  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#addFileSelectField(java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String)
   */
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
    GralWidget widgdSelect = addButton(name + "<", actionFileSelect,  "<");
    FileSelectInfo fileSelectInfo = new FileSelectInfo(name, listRecentFiles, startDirMask, widgd);
    widgdSelect.setContentInfo(fileSelectInfo); 
    //xSize = xSize1;
    pos = posAll;  //the saved position.
    return widgd;
  }

  
  
  GralUserAction actionFileSelect = new GralUserAction("actionFileSelect")
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
          fileSelectInfo.dstWidgd.setText(fileSelect);
          //fileSelectInfo.dstWidgd.setValue(cmdSet, 0, fileSelect);
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
  public GralUserAction actionDesignEditField = new GralUserAction("actionDesignEditField")
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
  public GralUserAction actionReadPanelCfg = new GralUserAction("actionReadPanelCfg")
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    {
      //GralPanelContent currPanel = 
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        GralPanelContent panel = widgd.pos().panel;
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
  
  
  
  public void initCfgDesigner(){
    designer.setToPanel();
  }
  
  @Override public GralInfoBox createTextInfoBox(String name, String title)
  {
    return GralInfoBox.createTextInfoBox(this, name, title);
  }

  
  @Override public GralInfoBox createHtmlInfoBox(String name, String title, boolean onTop)
  {
    return GralInfoBox.createHtmlInfoBox(this, name, title, onTop);
  }

  
  /**Adds a text to the current panel at given position with standard colors, left origin.
   * The size of text is calculated using the height of positioning values.
   * see also {@link #addText(String, int, GralColor, GralColor)},
   * {@link #addTextField(String, boolean, String, String)}
   * @param text
   */
  @Override public GralWidget addText(String text)
  { //return addText(text, 0, GralColor.getColor("bk"), GralColor.getColor("wh"));
    return addText(text, 0, GralColor.getColor("bk"), propertiesGui.colorBackground_);
  }
  
  
  @Override public void refreshCurvesFromVariable(VariableContainer_ifc container){
    for(GralCurveView_ifc curve : curveContainer){
      if(curve.isActiv()){
        curve.refreshFromVariable(container);
      }
    }
  }
  
  
  @Override public void writeLog(int msgId, Exception exc)
  {
    String sMsg = exc.toString();
    StackTraceElement[] stackTrace = exc.getStackTrace();
    String sWhere = stackTrace[0].getFileName() + ":" + stackTrace[0].getLineNumber();
    log.sendMsg(msgId, sMsg + " @" + sWhere);
  }
  
  
  
  /**This standard Gral focus listener is the base class for the common implementation layer focus listener.
   * The both methods {@link #focusGainedGral(GralWidget)} and {@link #focusLostGral(GralWidget)} will be invoked
   * with that GralWidget, which is referred by the implementation layer widgets data.
   * <br><br>
   * The GralMng implementation classes should offer a focus listener for common usage, see 
   * {@link org.vishia.gral.swt.SwtMng.SwtMngFocusListener} and its instance {@link org.vishia.gral.swt.SwtMng.focusListener}. 
   * That reference and class is protected and therewith package visible because only swt implementations needs it. 
   * An implementation widget class can use this instance of SwtMng.focusListener immediately for standard behavior.
   * The standard behavior is realized in this class, see {@link GralMngFocusListener#focusGainedGral(GralWidget)}.
   * <br><br>
   * For enhanced functionality of a focus listener the implementation layer SwtFocusListener class can be enhanced.
   * That is realized for example in {@link org.vishia.gral.swt.SwtTextFieldWrapper}. That's focus listener should 
   * update the text in the widget with the gral text store {@link GralWidget.DynamicData#displayedText}
   * on focus gained, and overtake a changed content on focus lost. Adequate it is on all edit-able widgets.
   * The SwtTextFieldWrapper.TextFieldFocusListener.focusGained(...) and focusLost(...) methods executes the special
   * functionality. After them 'super.focusGained/Lost(ev);' is in called to execute the standard behavior.
   */
  protected class GralMngFocusListener
  {
    
    /**Standard action on focus lost:
     * @param widgg
     */
    protected void focusLostGral(GralWidget widgg){
      GralWidget.ImplAccess.setFocused(widgg, false);
      //CharSequence text = Assert.stackInfo("", 1, 5);
      //System.out.println("GralMng - widget focus lost;" + widgg.name + text);
    }

    /**Standard action on focus gained:
     * <ul>
     * <li>Sets the html help into the {@link GralMng#setApplicationAdapter(GralMngApplAdapter_ifc)} if given.
     * <li> invokes the {@link GralWidget#setActionFocused(GralUserAction)} if given.
     * <li>invokes {@link GralMng#notifyFocus(GralWidget)} to detect the {@link GralMng#getWidgetInFocus()}.
     * </ul>
     * @param widgg
     */
    protected void focusGainedGral(GralWidget widgg){
      GralMng.this.notifyFocus(widgg);
      GralWidget.ImplAccess.setFocused(widgg, true);
      String htmlHelp = widgg.getHtmlHelp();
      if(htmlHelp !=null && applAdapter !=null){
        applAdapter.setHelpUrl(htmlHelp);
      }
      if(widgg.actionFocused !=null){ widgg.actionFocused.exec(KeyCode.focusGained, widgg); }
    }
  }
  
  
  /**This instance can be used if any other focus listener is necessary for any implementation widget.
   * The standard behavior for GralWidget is supported using this aggregate.
   * In Opposite a full ready to use focus listener based on this class in the implementation layer,
   * see {@link org.vishia.gral.swt.SwtMng.SwtMngFocusListener}.
   */
  public GralMngFocusListener gralFocusListener = new GralMngFocusListener();
  
  /**This inner class is public only because the implementation uses it. It is not public for applications.
   *
   *
   */
  public class InternalPublic{
    public GralKeyListener gralKeyListener = new GralKeyListener(GralMng.this);
  }
  
  /**Implementation specific fields.
   * 
   */
  public final InternalPublic _impl = new InternalPublic();
  
  

  void stop(){}
	

	
}
