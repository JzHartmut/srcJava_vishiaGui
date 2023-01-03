package org.vishia.gral.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.event.EventTimerThread;
//import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralCurveView.CommonCurve;
import org.vishia.gral.base.GralGraphicThread.ImplAccess;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgWriter;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralMngApplAdapter_ifc;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelectWindow;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.gral.widget.GralLabel;
import org.vishia.inspcPC.InspcReplAlias;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmdLoggingStream;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Assert;
import org.vishia.util.CheckVs;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.MinMaxTime;
import org.vishia.util.ReplaceAlias_ifc;
import org.vishia.util.TimedValues;

/**This is the Manager for the graphic. 
 * It contains the independent parts of graphic organization.
 * This class <code>GralMng</code> is a common approach to work with graphical interface simply. 
 * The inner class {@link ImplAccess} is implemented by the several graphic-system-supporting classes.
 * <ul>
 * <li>{@link org.vishia.gral.swt.SwtMng}
 * <li>{@link org.vishia.gral.awt.AwtMng}
 * </ul>
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 */
@SuppressWarnings("synthetic-access") 
public class GralMng implements GralMngBuild_ifc, GralMng_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2022-11-14 Hartmut new {@link LogMsg} with some constants for organization of log messages 
   *   with the {@link LogMessage#sendMsg(int, CharSequence, Object...)} approach. 
   *   The real logged messages can be filtered with this numbers.
   * <li>2022-11-14 Hartmut new now {@link #reportGralContent(Appendable)} for all existing windows defined here.
   * <li>2022-11-14 Hartmut new now {@link #initScript(CharSequence)} wrapped here for more simple user programming. 
   * <li>2022-11-14 Hartmut rename now {@link #refPos()} instead currPos().
   * <li>2022-10-27 Hartmut chg now the singleton concept is obsolete. Some more refactoring.
   * <li>2022-10-27 Hartmut new {@link #createGraphic(String, char, LogMessage)} is now localized here (where else!). 
   * <li>2020-02-01 Hartmut new {@link #actionClose} sets the property to close a main Window.
   * <li>2016-11-04 Hartmut chg: {@link #notifyFocus(GralWidget)}: Only widgets with datapath. It is only for the inspector etc. TODO: Is there a list necessary? Store only the last widget in focus!
   * <li>2016-09-02 Hartmut new: {@link #setPosPanel(GralPanelContent)} now invoked especially from ctor of {@link GralPanelContent}
   *   and from ctor of {@link GralPanelContent.ImplAccess}. If a new Panel was created with a given {@link GralWidget#pos()} then that panel
   *   is set as current one for the next widgets. Either the widgets are created before the implementation graphic with given position string,
   *   then the panel should have a position string too to assigned them. Or the widgets are created with creation of the implementation graphic,
   *   then a panel should be created in the same kind, and invoke {@link #setPosPanel(GralPanelContent)} in the ctor of its implementation.
   *   The contract is unchanged: A created panel determines that all following widgets are created on that panel.
   * <li>2016-09-02 Hartmut chg: {@link #registerPanel(GralPanelContent)} is only be called in the ctor of {@link GralPanelContent} 
   *   because any panel is based on GralPanelContent. 
   *   The definition of the panel in the {@link #pos()} used for the next widgets are done now in the extra routine {@link #setPosPanel(GralPanelContent)}. 
   * <li>2016-09-01 Hartmut chg: instead implements {@link ReplaceAlias_ifc} now contains {@link #getReplacerAlias()}.
   *   It is an extra class for a ReplacerAlias given independent of the graphic. 
   * <li>2016-07-20 Hartmut chg: instead setToPanel now {@link #createImplWidget_Gthread()}. It is a better name. 
   * <li>2015-10-29 Hartmut chg: Problem on {@link #pos()} with a second thread: The MainWindow- {@link GralPos#panel} was registered in another thread
   *   and therefore unknown in the new {@link #pos()} for that thread. Solution: If the thread-specific GralPos will be created,
   *   it should copy the data from the {@link #posCurrent} which is valid any case for valid initial data.   
   * <li>2015-10-26 Hartmut new: The help and info box is an integral part of the GralMng and therefore available for any small application without additional effort.
   *   Only the {@link #createHtmlInfoBoxes(MainCmd)} should be invoked in the graphic thread while initializing the application. 
   * <li>2015-07-13 Hartmut chg: {@link GralMngFocusListener#focusLostGral(GralWidget)} now invokes the {@link GralWidget#actionFocused} too.
   *   The action should distinguish between focus gained and focus lost in its action routine. That should be added to the code. Done for all vishia sources.  
   * <li>2015-07-13 Hartmut new: {@link #registerUserAction(String, GralUserAction)} now knows possibility of usage the own name with "<name>" 
   * <li>2015-07-13 Hartmut chg: Positioning: The new concept gets a position in the constructor of {@link GralWidget} via 
   *   {@link #getPosCheckNext()}. That routine sets the current position in this class to {@link PosThreadSafe#posUsed} 
   *   for further using to increment. That is correct. 
   *   The problem before was faulty old-concept usage of org.vishia.gral.swt.SwtMng#XXXsetPosAndSizeSwt
   *   which invokes {@link #setNextPosition()} which increments the position too, therefore twice. The usage of that routine
   *   is prevented for Swt implementation, TODO for awt yet now. 
   * <li>2015-05-31 Hartmut chg: {@link GralMngFocusListener}: invokes repaint() because of maybe changed outfit on focus gained
   * <li>2015-05-02 Hartmut chg: {@link #registerWidget(GralWidget)} is obsolete now, it is empty yet. 
   *   Instead {@link #registerWidget(String, GralWidget)} by given name and {@link #removeWidget(String)} by name.
   *   See description of registering on {@link GralWidget#setToPanel(GralMngBuild_ifc)}. 
   * <li>2015-04-27 Hartmut new {@link #selectPanel(GralPanelContent)} not only with String given
   * <li>2015-01-18 Hartmut chg: Now the implementation for any Grahic (SwtMng) and the GralMng are two separated instances.
   *   The SwtMng extends the {@link GralMng.ImplAccess} which accesses all private data of the GralMng.
   * <li>2013-12-21 Hartmut new: {@link #createImplWidget_Gthread(GralWidget)} for all set to panel actions. That method handles all widget types. 
   * <li>2013-03-20 Hartmut adap: {@link #actionFileSelect} with setText(...), now the file select field was filled.
   * <li>2012-08-20 Hartmut new: {@link #getWidgetsPermanentlyUpdating()} created but not used yet because 
   *   {@link #refreshCurvesFromVariable(VariableContainer_ifc)} has the necessary functionality.
   * <li>2012-06-30 Hartmut new: Composition {@link #widgetHelper} The widget helper is implemented in the graphic system
   *   for example as {@link org.vishia.gral.swt.SwtWidgetHelper} to do some widget specific things.
   * <li>2012-06-30 Hartmut new: Composition {@link #_implListener}.{@link InternalPublic#gralKeyListener}
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
   * Old History from GralGraphicThread
   * <ul>
   * <li>2022-09-04 in {@link #runGraphicThread()} set the thread ID before (!) the implementation is started. 
   * <li>2020-02-01 in {@link #runGraphicThread()}: The {@link GralWindow_ifc#windHasMenu} is not forced, it is set compatible
   *   in {@link GralArea9MainCmd#parseArgumentsAndInitGraphic(String, String, char, String)}. 
   *   <br>Usage of {@link GralWindow#GralWindow(String, String, String, int)} 
   *   should add {@link GralWindow_ifc#windResizeable} etc. additionally. 
   * <li>2016-07-16 Hartmut chg: The main window will be created with same methods like all other windows. 
   * <li>2015-01-17 Hartmut chg: Now it is an own instance able to create before the graphic is established.
   *   The graphical implementation extends the {@link ImplAccess}. 
   * <li>2012-04-20 Hartmut bugfix: If a {@link GralGraphicTimeOrder} throws an exception,
   *   it was started again because it was in the queue yet. The proplem occurs on build graphic. It
   *   was repeated till all graphic handles are consumed. Now the {@link #queueGraphicOrders} entries
   *   are deleted first, then executed. TODO use this class only for SWT, use the adequate given mechanism
   *   for AWT: java.awt.EventQueue.invokeAndWait(Runnable). use Runnable instead GralDispatchCallbackWorker. 
   * <li>2012-03-15 Hartmut chg: Message on exception.
   * <li>2011-11-08 Hartmut new: Delayed orders to dispatch in the graphic thread: 
   *   Some actions need some calculation time. 
   *   If they are called in a fast repetition cycle, a follow up effect may occur. 
   *   Therefore actions should be registered with a delayed start of execution, the start time 
   *   should be able to putting off till all repetitions (for example key repetition) are done.
   * <li>2011-11-00 Hartmut created: as own class from Swt widget manager.
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
   * <li> But the LPGL is not appropriate for a whole software product,
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
   */
  public final static String sVersion = "2022-10-27";
  
	/**This class is used for a selection field for file names and paths. */
  public static class FileSelectInfo
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
      this.dialogFile = null; //singleton.createFileDialog();      // TODO this is a problem if created not in the graphic thread.
      //this.dialogFile.open(sTitle, mode);
    }
    
  }
  
  
  
  /*package private*/ static final class PosThreadSafe
  {
    /**Position of the next widget to add. If some widgets are added one after another, 
     * it is similar like a flow-layout.
     * But the position can be set.
     * The values inside the position are positive in any case, so that the calculation of size is simple.
     */
    public final GralPos pos; //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
    
    /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
     * calculates the next position in direction see {@link GralPos#setNextPosition()}. */
    protected boolean posUsed;
    
    final long threadId;
    
    PosThreadSafe(GralMng gralMng) {
      threadId = Thread.currentThread().getId();
      pos = new GralPos(gralMng);
    }
    
    PosThreadSafe(GralPos exists) {
      threadId = Thread.currentThread().getId();
      pos = new GralPos(exists);
    }
    
    @Override public String toString(){ return "thread=" + threadId + ": " + pos.toString(); }
   
  }
  
  
  /**The current position as helper if it is the same thread.
   * Initialized firstly empty.
   */
  private PosThreadSafe posCurrent;

  private final Map<Long, PosThreadSafe> posThreadSafe;
  
  
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
  
  /**Properties of the Gui appearance. */
  public GralGridProperties.ImplAccess propertiesGui;

  /**Properties of the Gui appearance. */
  public final GralGridProperties gralProps;

  /**Index of all input fields to access symbolic for all panels. */
  protected final Map<String, GralWidget> idxNameWidgets = new TreeMap<String, GralWidget>();

  /**Index of all input fields to access symbolic. NOTE: The generic type of WidgetDescriptor is unknown,
   * because the set is used independently from the graphic system. */
  protected final Map<String, GralWidget> idxShowFields = new TreeMap<String, GralWidget>();

  //private final IndexMultiTable showFieldsM;

  //public final GralWidgetHelper widgetHelper;
  
  /**Map of all panels. A panel may be a dialog box etc. */
  protected final Map<String, GralPanel_ifc> idxPanels = new TreeMap<String,GralPanel_ifc>();

  /**Representation of the 'whole screen' as parent for a window. */
  public final GralPanel_ifc screen;
  
  /**Map of all windows. One of them is the primary one */
  protected final Map<String, GralWindow> idxWindows = new TreeMap<String,GralWindow>();

  /**The one instance of the primary window created in the {@link org/vishia/ifc/GralFactory#createWindow(String, String, int)} . */
  private GralWindow windPrimary;

  private final List<GralWidget> widgetsInFocus = new LinkedList<GralWidget>();
 
  /**Three windows as sub window for html help, info and logging created if the primary window is created. */
  public GralInfoBox infoHelp, infoBox, infoLog;
  
  private String sHelpBase;


  
  /**List of all panels which may be visible yet. 
   * The list can be iterated. Therefore it is lock-free multi-threading accessible.
   */
  protected final ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listVisiblePanels = new ConcurrentLinkedQueue<GralVisibleWidgets_ifc>();
  
  
  Queue<GralWidget> listWidgetsPermanentUpdating = new LinkedList<GralWidget>();
  
  
  /**It is possible to write any message via this class to a logging system.
   * All internal methods of gral writes exceptions to that logging system instead abort the execution of the application.
   * This is an association, can be null, should not be null while working.
   * See {@link #GralMng(LogMessage)} and #setLog
   */
  public LogMessage log;
  
  
  protected GralMngApplAdapter_ifc applAdapter;
  
	/**Composition of some curve view widgets, which should be filled indepentent of there visibility. */
  @Deprecated public final List<GralCurveView_ifc> curveContainer = new LinkedList<GralCurveView_ifc>();
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
//  protected final GralPos XXXpos = new GralPos(this); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction see {@link GralPos#setNextPosition()}. */
//  protected boolean XXXposUsed;
  

  
  
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
  
  InspcReplAlias replacerAlias = new InspcReplAlias();
  
  //public final GralWidgetHelper widgetHelper;
  
	/**Any kind of TabPanel for this PanelManager TODO make protected
   */
  
  //public GralPanel_ifc currPanel;
  
  protected String sCurrPanel;
  
  /**Last focused widget or last selected line in a table. 
   * This info can be used to get the last widget on a context menu etc. on another widget.
   * See {@link #getLastClickedWidget()}
   */
  private GralWidget_ifc lastClickedWidget;
  
  //private String lastClickedDatapath;
  
  //private String lastClickedVariable;
  
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

  
  /**The thread id of the managing thread for graphic actions. */
  protected long graphicThreadId;

  
  boolean debugPrint = false;
  
  protected boolean isWakedUpOnly;
  
  /**True if the startup of the main window is done and the main window is visible. */
  protected boolean bStarted = false; 

  /** set to true to exit the graphic thread and dispose the implementation graphic */
  protected boolean bShouldExitImplGraphic = false;
  
  protected boolean bIsExitImplGraphic = false;
  
  /**Set to true to finish the main. Set from the graphic thread. */
  protected boolean bExitMain = false;
  
  /**Set to true to finish the main. Maybe on dispose the implementation graphic. */
  protected boolean bShouldExitMain = false;
  
  /**Instance to measure execution times.
   * 
   */
  protected MinMaxTime checkTimes = new MinMaxTime();
  
  
  /**Queue of orders to execute in the graphic thread before dispatching system events. 
   * Any instance will be invoked in the dispatch-loop.
   * See {@link #addTimeOrder(Runnable)}. 
   * An order can be stayed in this queue for ever. It is invoked any time after the graphic thread 
   * is woken up and before the dispatching of graphic-system-event will be started.
   * An order may be run only one time, than it should delete itself from this queue in its run-method.
   * */
  private final ConcurrentLinkedQueue<GralGraphicTimeOrder> queueOrdersToExecute = new ConcurrentLinkedQueue<GralGraphicTimeOrder>();

  
  EventTimerThread orderList = new EventTimerThread("GraphicOrderTimeMng"); //this);

  
  /**The graphic specific implementation part of the GralMng
   * This implementation should care about the correct implementation widgets. 
   */
  public ImplAccess _mngImpl;
  
  private static GralMng XXXsingleton;
	
  /*
  public GralMng(GralGraphicThread device, LogMessage log)
  { this.gralDevice = device;
    //this.propertiesGui = props;
      this.log = log;
  //its a user action able to use in scripts.
      userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
  GralMng.singleton = this; 
  }
  */

  /**Creates the GralMng with a given logging output, 
   * 
   * @param log maybe null firstly, then use {@link #setLog(LogMessage)}
   */
  public GralMng(LogMessage log)
  { //this.propertiesGui = props;
      this.log = log;
    this.gralProps = new GralGridProperties();  
    //its a user action able to use in scripts.
    userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
    //if(GralMng.singleton ==null) { GralMng.singleton = this; } 
    this.screen = new GralScreen(this);       // this is only a fictive panel without meaning, only to have anyway a parent for GralPos
    this.idxPanels.put("screen", screen);
    this.posThreadSafe = new TreeMap<Long, PosThreadSafe>();
    this.posCurrent = new PosThreadSafe(this);
    this.posThreadSafe.put(posCurrent.threadId, posCurrent);
  }

  
  public GralMng() {
    this(new LogMessageStream(System.out));
  }
  
  
  
  /**Initialize the graphic with a script. See {@link GralCfgZbnf#configureWithZbnf(CharSequence, GralCfgData)}.
   * This operation is only wrapped.
   * @param script String given, also possible in a StringBuilder prepared.
   * @throws ParseException
   */
  public void initScript(CharSequence script) throws ParseException {
    GralCfgZbnf.configWithZbnf(script, null, this);         // does all, reads the config file, parses, creates Graphic Elements
  }
  
  
  /**Initialize the graphic with a script. See {@link GralCfgZbnf#configureWithZbnf(File, GralCfgData)}.
   * This operation is only wrapped.
   * @param script given in file
   * @throws Exception 
   */
  public void initScript(File script) throws Exception {
    GralCfgZbnf.configWithZbnf(script, null, this);         // does all, reads the config file, parses, creates Graphic Elements
  }
  
  
  /**Creates the whole graphically representation with all given widgets on Gral level.
   * This operation creates the proper Factory with {@link GralFactory#getFactory},
   * then calls {@link GralFactory#createGraphic(GralMng, char)}.
   * Only the {@link GralFactory} needs dependencies to the implementation graphic,
   * there only to the specific Factory. Whereby the specific factory can be called also via String:
   * {@link Class#forName(String)}. 
   * This class remains independent of any implementation. 
   * @param implementor "SWT" or "AWT" or (TODO) the class path of the implementor's factory class.
   * @param sizeShow 'A' .. 'G' for the size
   * @param log null if a log is already given for the GralMng,
   *   else the log from yet. A given log will be removed. 
   */
  public void createGraphic(String implementor, char sizeShow, LogMessage log) {
    if(log != null) { this.log = log; }
    this.bShouldExitImplGraphic = false;                   // important on second call
    this.bIsExitImplGraphic = false;
    this.bStarted = false;
    this.gralProps.setSizeGui(sizeShow);
    GralFactory factory = GralFactory.getFactory(implementor);
    if(factory !=null) {
      factory.createGraphic(this, sizeShow);
    } else {
      System.out.println("no Gral implementor, faulty String: " + implementor);
    }
    this.windPrimary.redraw();
  }
  
  
  /**Creates the singleton instance of the GralMng. If this routine is invoked more as one time, the first invocation
   * is the correct one. The more-time-invocation is supported because an application may not invoke this routine.
   * Therefore it is invoked later additional.
   * <br> See also {@link #get()}.
   * @param log The first invocation determines the log output.
   * @return true if created, false if exists already.
   * @??deprecated {@link #get()} is sufficient TODO detemine log
   */
  public static boolean XXXcreate(LogMessage log){
//    if(singleton !=null) return false;
//    else { 
//      singleton = new GralMng(log);
//      return true;
//    }
    return false;
  }
  

  /**Returns the singleton of the GralMng. Creates it if it is not instantiated yet. 
   * On creation all logging output will be redirect to System.out.
   * To use another logging output, create the GralMng using {@link GralMng#GralMng(LogMessage)}
   * on start of application, before this routine is firstly called. */
//  public static GralMng get(){ 
//    if(singleton == null) { //not initialized yet, early invocation:
//      singleton = new GralMng(new MainCmdLoggingStream(System.out));
//    }
//    return singleton; 
//  }
  
  
  /**Changes or sets the log output. See {@link #log()}.
   * @param log
   */
  public void setLog(LogMessage log) { this.log = log; }
  
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

  PosThreadSafe pos() 
  { long threadId1 = Thread.currentThread().getId();
    PosThreadSafe ret = posCurrent;   //thread safe: atomic access, use ret.
    if(ret.threadId != threadId1) {   //only access to map if necessary.
      Long threadId = new Long(threadId1);
      ret = posThreadSafe.get(threadId);
      if(ret == null){
        ret = new PosThreadSafe(posCurrent.pos); //copy values from the last one 
        posThreadSafe.put(threadId, ret);
      }
    }
    posCurrent = ret;                 //store for next access, it may be faster.
    return ret;
  }

  /**Sets the position in a Thread safe kind. 
   * @param pos maybe gotten from {@link #getPositionInPanel()} any slightly varied
   */
  public void setPos(GralPos pos) {
    pos().pos.set(pos);
  }

  
  /**Returns the current position of this thread to work with it.
   * @return Will be changed on ctors of GralWidget.
   *   The GralWidget does not store a reference to this, it stores a clone. 
   */
  public GralPos refPos ( ) { return this.pos().pos; }
  
  
  /**Sets the position with a given String, see {@link GralPos#setPosition(CharSequence, GralPos)}
   * whereby the parent is the current position value.
   * @param sPosition
   * @throws ParseException
   */
  public GralPos setPos ( String sPosition) 
  throws ParseException
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(sPosition, null);
    pos.posUsed = false;
    return pos.pos;
  }

  @Override public void setPositionSize ( int line, int column, int height, int width, char direction)
  { PosThreadSafe pos = pos();
    if(line < 0){ line = pos.posUsed? GralPos.next: GralPos.same; }
    if(column < 0){ column = pos.posUsed? GralPos.next: GralPos.same; }
    setFinePosition(line, 0, height + GralPos.size, 0, column, 0, width + GralPos.size, 0, 1, direction, 0 ,0 , pos.pos);
  }

  @Override public void setPosition ( float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { setPosition(pos().pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
  }

  @Override public void setPosition ( float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(pos.pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
    pos.posUsed = false;
  }

  @Override public void setPosition ( GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
    pos.posUsed = false;
  }

  @Override public void setPosition ( GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  { PosThreadSafe pos = pos();
      pos.pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
      pos.posUsed = false;
  }

  @Override public void setFinePosition ( int line, int yPosFrac, int ye, int yef
    , int column, int xPosFrac, int xe, int xef, int origin, char direction, int border, int borderFrac, GralPos frame)
  { PosThreadSafe pos = pos();
    pos.pos.setFinePosition(line, yPosFrac, ye, yef, column, xPosFrac, xe, xef, origin, direction, border, borderFrac, frame);
    pos.posUsed = false;
  }

  @Override public void setSize ( int height, int ySizeFrac, int width, int xSizeFrac)
  { PosThreadSafe pos = pos();
    pos.pos.setSize(height, ySizeFrac, width, xSizeFrac);  //NOTE: setSize sets the next pos 
    pos.posUsed = false;
  }

  /**Sets the size of the position and remark it as unused. 
   * @param height
   * @param width
   */
  public void setSize ( float height, float width)
  { PosThreadSafe pos = pos();
    pos.pos.setSize(height, width, pos.pos);
    pos.posUsed = false;
  }

  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. TODO remove in AwtMng*/
  @Deprecated public void setNextPosition()
  { PosThreadSafe pos = pos();
    pos.pos.checkSetNext();
  }

  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. */
  public void setNextPositionUnused()
  { PosThreadSafe pos = pos();
    pos.pos.checkSetNext();
  }

  public void registerShowField(GralWidget widg){
    //link the widget with is information together.
    if(widgetsInFocus.size()==0 && widg.getDataPath() !=null) {
      //it has not a datapath initally. Never come here.
      widgetsInFocus.add(widg);   //add first widget.
    }
    if(widg.name !=null){
      idxShowFields.put(widg.name, widg);
    }
  
  }

  @Override public GralPos getPositionInPanel(){ return pos().pos.clone(); }

  public GralPos getPosCheckNext(){ 
    PosThreadSafe pos = pos();
    pos.pos.checkSetNext();
    return pos.pos.clone(); 
  }

  /**Used for deprecated style, without independent GralWidget. TODO remove.
   * @return Independent GralPos from the GralMng
   */
  public GralPos getPosOldPositioning(){ return getPosCheckNext(); }

  //private String lastClickedDatapath;
  
  //private String lastClickedVariable;
  
  @Deprecated @Override public List<GralWidget> getListCurrWidgets(){ return ((GralPanelContent)pos().pos.parent).getWidgetList(); }


  public void setProperties(GralGridProperties.ImplAccess props) {
    this.propertiesGui = props;
  }
  
  /**register a window only called in the {@link GralWindow#GralWindow(GralPos, String, String, int, GralMng)}.
   * @since 2022-09 renamed from setFirstlyThePrimaryWindow(), and also register all windows.
   * @param window
   * TODO should be package private
   */
  public void registerWindow(GralWindow window){
    if(this.windPrimary ==null) {
      this.windPrimary = window; 
      idxPanels.put("primaryWindow", window.mainPanel);
    }
    this.idxWindows.put(window.name, window);
  };
  
  
//  public static void createMainWindow(GralFactory factory, GralWindow window, char sizeShow, int left, int top, int xSize, int ySize) {
//    factory.createWindow(window, sizeShow, left, top, xSize, ySize);
//  }
//  
  
  
  /**This routine should be called in the initializing routine in the graphic thread one time.
   * @param mainCmd If given the help from mainCmd will be written into the Help box.
   */
  public void createHtmlInfoBoxes(MainCmd mainCmd)
  {
    setPosition(10, 0, 10, 0, 0, 'd');
    infoBox = createTextInfoBox("infoBox", "Info");
    //
    selectPanel("primaryWindow");
    setPosition(0,40,10,0,0,'.');
    infoHelp = GralInfoBox.createHtmlInfoBox(this, refPos(), "Help", "Help", true);
    if(mainCmd !=null) {
      try{
        for(String line: mainCmd.listHelpInfo){
          infoHelp.append(line).append("\n");
        }
      } catch(Exception exc){ writeLog(0, exc); }
    }
  }
  
  
  
  /**Not that this routine must not invoked before the {@link GralFactory#createWindow(GralWindow, char, int, int, int, int)}
   * was not called.
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createImplWidget_Gthread(org.vishia.gral.base.GralWidget)
   */
  @Override public void createImplWidget_Gthread(GralWidget widgg){ 
    try {
      if(false && widgg instanceof GralWindow){
        GralWindow wind1 = (GralWindow)widgg;
        this._mngImpl.createSubWindow(wind1);
  //      for(Map.Entry<String, GralPanelContent> e: wind1.panels.entrySet()) {
  //        GralPanelContent tab = e.getValue();
  //        
  //      }
        //registerPanel(wind1);
        //set the current position of the manager to this window, initalize it.
        //PosThreadSafe pos = pos();
        //pos.pos.panel = wind1; //it is selected.
        //pos.pos.setPosition(null, 0,0,0,0,0,'r');  //per default the whole window as position and size.
  
      } else {  
        _mngImpl.createImplWidget_Gthread(widgg); 
      }
    } catch(Exception exc) {
      System.err.println(CheckVs.exceptionInfo("unexpected", exc, 0, 10));
    }
  }
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#setMainKeyAction(org.vishia.gral.ifc.GralUserAction)
   */
  @Override public GralUserAction setMainKeyAction(GralUserAction userMainKeyAction){
    GralUserAction last = this.userMainKeyAction;
    this.userMainKeyAction = userMainKeyAction;
    return last;
  }
  
  
  
  /**package private*/ GralUserAction userMainKeyAction(){ return userMainKeyAction; }
  
  
  @Override public GralGridProperties propertiesGui(){ return gralProps; }
  
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
  
  
  @Override public ReplaceAlias_ifc getReplacerAlias(){ return replacerAlias; }
  
  

  public void setHelpBase(String path){ 
    char cEnd = path.charAt(path.length()-1);
    if("/\\".indexOf(cEnd) >=0) {
      sHelpBase = path;
    } else {
      sHelpBase = path + "/";
    }
  }
  
  
  
  /**Sets the URL for the current help situation. 
   * Note: before calling the following may/should be set: <pre>
   * this.gralMng.createHtmlInfoBoxes(null);
   *  this.gralMng.setHelpBase(this.sHelpDir);
   * </pre>
   * Only then the url is effective.
   * @param urlSuffix it is usual a suffix to given helpBase. 
   *   Only if it is absolute than take it without helpbase.
   */
  public void setHelpUrl(String urlSuffix){ 
    String sUrl;
    if(urlSuffix.startsWith(":")){
      sUrl = this.sHelpBase + urlSuffix.substring(1);
    } else if(FileFunctions.isAbsolutePath(urlSuffix)) { 
      sUrl = urlSuffix;  //absolute path
    } else if (this.sHelpBase !=null) { //a directory should end with "/", possible also /path/to/file.html
      sUrl = this.sHelpBase + urlSuffix;      //url may be "file.html#label" or "#label"
    } else {
      sUrl = urlSuffix;  //taken as is absolute or relative
    }
    if(this.infoHelp !=null) this.infoHelp.setUrl(sUrl); 
  }
  

  
  public void setHtmlHelp(String url){
    setHelpUrl(url); 
    /*
    if(applAdapter !=null){
      applAdapter.setHelpUrl(url);
    }
    */
  }
  
  public void showInfo(CharSequence text) {
    if(infoBox == null) return;
    if(text !=null){ infoBox.setText(text); }
    infoBox.setFocus();
  }
  
  public void setInfo(CharSequence text) {
    if(infoBox == null) return;
    infoBox.setText(text);
  }
  

  
  public void addInfo(CharSequence info, boolean show)
  {
    if(infoBox == null) return;
    try{ infoBox.append(info); }
    catch(IOException exc){}
    if(show){
      infoBox.setFocus(); //setWindowVisible(true);
    }
  }

  
  
  /**selects a registered panel for the next add-operations. 
   */
  @Override public GralPanel_ifc selectPanel(String sName){ 
    PosThreadSafe pos = pos();
    GralPanel_ifc panel = this.idxPanels.get(sName);
    pos.pos.parent = panel;                                // the current pos in GralMng is marked with the panel as parent
    sCurrPanel = sName;
//    if(pos.pos.parent == null && XXXcurrTabPanel !=null) {
//      //use the position of the current tab panel for the WidgetMng. Its panel is the parent.
//      pos.pos.set(XXXcurrTabPanel.pos());  
//      pos.pos.parent = XXXcurrTabPanel.addGridPanel(sName, /*"&" + */sName,1,1,10,10);
//      idxPanels.put(sName, panel);  //TODO unnecessay, see addGridPanel
      log.sendMsg(0, "GuiPanelMng:selectPanel: unknown panel name %s", sName);
      //Note: because the pos.panel is null, not placement will be done.
//    }
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
    return panel;
  }
  
  
  /**Selects the given panel as current panel to build some content. */
  @Override public void selectPanel(GralPanel_ifc panel) {
    pos().pos.parent = panel;
    sCurrPanel = panel == null ? null: panel.getName();
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
  }
  
  /**Selects the primary window as current panel to build some content. */
  @Override public void selectPrimaryWindow() { selectPanel(windPrimary.mainPanel); } 
  
  @Override public boolean currThreadIsGraphic(){
    return Thread.currentThread().getId() == getThreadIdGui();
  }


  
  @Override public GralWidget getWidget(String name)
  { return idxNameWidgets.get(name);
  }
  
  
  
  public Iterable<GralWidget> getWidgetIter() {
    return idxNameWidgets.values();
  }
  
  
  @Override public void buildCfg(GralCfgData data, File fileCfg) //GuiCfgBuilder cfgBuilder)
  {
    this.cfgData = data;
    File currentDir = FileSystem.getDir(fileCfg);
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
  
  
  /**Returns that widget which was clicked by mouse at last. This method is usefully for debugging
   * and for special functionality. A widget which {@link GralWidget#setDataPath(String)} is initialized
   * with "widgetInfo" is not captured for this operation. It means, if any user action method uses
   * this method to get the last clicked widget, that widget itself have to be marked with
   * <b>setDataPath("widgetInfo");</b> to prevent getting its own widget info.  
   * @return The last clicked widget
   */
  //public String getLastClickedDatapath(){ return lastClickedWidget; }
  
  
  /**Registers all user actions, which are able to use in Button etc.
   * The name is related with the <code>userAction=</code> designation in the configuration String.
   * @param name if it contains "<name>" then that is replace by the {@link GralUserAction#name}. 
   *   You can give "<name>" only to set the action's name.
   * @param action
   */
  @Override public void registerUserAction(String name, GralUserAction action)
  { final String name1;
    if(name == null) {
      name1 = action.name;
    } else if(name.indexOf("<name>") >=0) {
      name1 = name.replace("<name>", action.name);
    } else {
      name1 = name;
    }
    userActions.put(name1, action);
  }
  
  @Override public GralUserAction getRegisteredUserAction(String name)
  {
    return userActions.get(name);
  }
  
  
  void registerWidget(String name, GralWidget widgd) {
    idxNameWidgets.put(name, widgd);
  }
  
  void removeWidget(String name) {
    idxNameWidgets.remove(name);
  }
  
  @Override public void registerWidget(GralWidget widgd)
  {
    if(widgd.name != null){
      idxNameWidgets.put(widgd.name, widgd);
    }
  }
  
  
  public void deregisterWidgetName(GralWidget widg)
  {
    if(widg.name != null){
      idxNameWidgets.remove(widg.name);
    }
    
  }
  
  /**Registers a panel to place the widgets.
   * <br>
   * For positioning of Widgets in the panel:
   * The panel should be associated with a current position which is used to hold the current value for increment.
   * For that {@link GralPos#setPosition(CharSequence, GralPos)} should be used.
   * <br>
   * Old positioning, deprecated: After registration, this panel is the current one, stored in {@link #pos()} for this thread. 
   * The panel can be selected with its name calling the {@link #selectPanel(String)} -routine
   * @param key Name of the panel.
   * @param panel The panel.
   */
  @Override public void registerPanel(GralPanel_ifc panel){
    GralPanel_ifc exist = this.idxPanels.get(panel.getName());
    if(exist !=null){
      if(exist == panel) System.out.println("info: unnecessary registerPanel " + panel.getName());
      else System.err.println("info: faulty registerPanel " + panel.getName());
    }
    this.idxPanels.put(panel.getName(), panel);
  }
  
  
  @Deprecated public void setPosPanel(GralPanel_ifc panel) {
    GralMng.PosThreadSafe pos = pos();
    pos.pos.parent = panel;
    //initialize the position because its a new panel. The initial position is the whole panel.
    pos.pos.setFinePosition(0,0,0,0,0,0,0,0,0,'d',0,0, pos.pos);
    sCurrPanel = panel.getName();
  }
  
  
  /*package private*/ 
  void deregisterPanel(GralPanel_ifc panel) {
    if(sCurrPanel.equals(panel.getName())) {
//      sCurrPanel = windPrimary.name;
    }
    idxPanels.remove(panel.getName());
  }
  
  
  void deregisterWindow(GralWindow wind) {
    this.idxWindows.remove(wind.getName());
    if(this.windPrimary == wind) {
      while(this.idxWindows.size() >0) {         // do not use for(...) because of ConcurrentModificationException
        Map.Entry<String, GralWindow> ewind2 = this.idxWindows.entrySet().iterator().next();
        GralWindow wind2 = ewind2.getValue();    // just the first entry
        wind2.remove();                          // it changes the this.idxWindows
      }
      assert(this.idxWindows.size()==0);
      this.windPrimary = null;
//      sCurrPanel = windPrimary.name;
    }
  }
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set<Map.Entry<String, GralWidget>> getShowFields()
  {
    Set<Map.Entry<String, GralWidget>> set =idxShowFields.entrySet();
    return set; //(Set<Entry<String, WidgetDescriptor>>)set;
  }

  
  public Map<String, String> getAllValues()
  {
      Map<String, String> values = new TreeMap<String, String>();
  for(GralWidget input: idxNameWidgets.values()){
      String sValue = getValueFromWidget(input);
    values.put(input.name, sValue);
  }
  return values;
  }

  @Override public String getValue(String sName)
  { final String sValue;
      GralWidget widgetDescr = idxNameWidgets.get(sName);
      if(widgetDescr !=null){
          sValue = getValueFromWidget(widgetDescr);
      } else {
          sValue = null;
      }
      return sValue;
  }
  

  
  /**It is a special routine for tabbedPanel.
   * The reason: If a new tab should add in {@link GralTabbedPanel#addGridPanel(String, String, int, int, int, int)}
   * then the mng is set to the tabbed panel.
   * @param tabbedPanel
   */
  /*package private*/public void setTabbedPanel(GralPanel_ifc tabbedPanel){
    pos().pos.parent = tabbedPanel;
  }
  
  
  public GralWidget_ifc getWindow(String name){
    return this.idxWindows.get(name);
  }
  
  
  
  public GralPanel_ifc getPanel(String name){
    return this.idxPanels.get(name);
  }
  
  

  public GralWindow getPrimaryWindow(){ return windPrimary; }
  
  
  public GralPanelContent getCurrentPanel(){ return (GralPanelContent)pos().pos.parent; }
  
  public GralPanelActivated_ifc actionPanelActivate = new GralPanelActivated_ifc()
  { @Override public void panelActivatedGui(List<GralWidget> widgetsP)
    {  //changeWidgets(widgetsP);
    }
  };

  /**Stores an event in the queue, able to invoke from any thread.
   * @param ev
   */
  /*package private*/ void storeEvent(EventObject ev){
    if(ev instanceof GralGraphicTimeOrder) { 
      queueOrdersToExecute.add((GralGraphicTimeOrder)ev);
      //System.out.println("storeEventPaint, wakeup");
      this._mngImpl.wakeup();
   } else {
      throw new IllegalArgumentException("can only store events of type GralGraphicTimeOrder");
    }
  }

  
  
  public EventTimerThread orderList(){ return orderList; }
  
  
  /**Adds the order to execute in the graphic dispatching thread.
   * It is the same like order.{@link GralGraphicTimeOrder#activate()}.
   * @param order
   */
  public void addDispatchOrder(GralGraphicTimeOrder order){ 
    order.activate();
    //orderList.addTimeOrder(order); 
  }

  //public void removeDispatchListener(GralDispatchCallbackWorker listener){ orderList.removeTimeOrder(listener); }

  

  
  public void addEvent(EventObject event) {
    assert(event instanceof GralGraphicTimeOrder);  //should be
    queueOrdersToExecute.add((GralGraphicTimeOrder)event);
    this._mngImpl.wakeup();
  }
  
  
  public long getThreadIdGui(){ return graphicThreadId; }
  
  /**This method should wake up the execution of the graphic thread because some actions are registered.. */
  public void wakeup(){ this._mngImpl.wakeup(); }


  public void waitForStart(){
    synchronized(this) {
      while(!bStarted) {
        try{ wait(1000);
        } catch(InterruptedException exc){}
      }
    }
  }
  
  public boolean isStarted(){ return this.bStarted; }
  
  /**Returns true so long the application should run (indpendent of implementation graphic)
   * @return false then exit the implementation
   */
  public boolean isRunning(){ return !this.bExitMain; }
  
  public boolean isImplementationGraphicTerminated(){ return this.bIsExitImplGraphic; }

  /**Terminates the run loop of the graphic thread*/
  public void closeImplGraphic ( ){ this.bShouldExitImplGraphic = true; }

  
  public void closeApplication ( ) { this.bShouldExitImplGraphic = true; this.bShouldExitMain = true; }

  
  /**The run method of the graphic thread. This method is started in the constructor of the derived class
   * of this, which implements the graphic system adapter. 
   * <ul>
   * <li>{@link #initGraphic()} will be called firstly. It is overridden by the graphic system implementing class
   *   and does some things necessary for the graphic system implementing level.
   * <li>The routine runs so long as {@link #bShouldExitImplGraphic} is not set to false. bExit may be set to false 
   *   in a window close listener of the graphic system level. It means, it is set to false especially 
   *   if the windows will be closed from the operation system. If the window is closed because the application
   *   is terminated by any application command the window will be closed, and the close listerer sets bReady
   *   to false then. 
   * <li>In the loop the {@link #queueGraphicOrders} will be executed.
   * <li>For SWT graphic this is the dispatch loop of graphic events. They are executed 
   *   in the abstract defined here {@link #dispatchOsEvents()} method.
   * <li>This thread should be wait if not things are to do. The wait will be executed in the here abstract defined
   *   method {@link #graphicThreadSleep()}.    
   * </ul>  
   * @see java.lang.Runnable#run()
   */
  //tag::runGraphicThread-start[]
  protected void runGraphicThread() {
    long guiThreadId1 = Thread.currentThread().getId();    // should set firstly because in createImplWidget_Gthread it is necesarry. 
    this.graphicThreadId = guiThreadId1;
    this._mngImpl.initGraphic();                           // inits the basics of the Graphic only, not the GralWidgets.
    //add important properties for the main window, the user should not thing about:
    this.windPrimary.windProps |= GralWindow.windIsMain  | GralWindow.windHasMenu;
    if((this.windPrimary.windProps & GralWindow_ifc.windMinimizeOnClose)==0) {
      //it it should not be minimized, then close, never set Invisible, because it is not possible to set visible again.
      this.windPrimary.windProps |= GralWindow.windRemoveOnClose;
    }
    for(Map.Entry<String,GralWindow> ewind: this.idxWindows.entrySet()) {
      GralWindow wind = ewind.getValue();
      //boolean bVisible = wind == this.windPrimary;
      //      
      //======>>>> 
      wind.createImplWidget_Gthread();           // creates all widgets of the window.
      //wind.setWindowVisible( bVisible ); 
    }
    //end::runGraphicThread-start[]

    
    this._mngImpl.finishInit();
    if(this.log !=null) {
      try{ this._mngImpl.reportContent(this.log);
      } catch(IOException exc) { }
    }
    //The last action, set the GuiThread
    synchronized(this){
      orderList.start();
      bStarted = true;
      this.notify();      //wakeup the waiting calling thread.
    }
    checkTimes.init();
    checkTimes.adjust();
    checkTimes.cyclTime();
    while (!bShouldExitImplGraphic) {
      step();
    }
    orderList.close();
    for(Map.Entry<String,GralWindow> ewind: this.idxWindows.entrySet()) {
      GralWindow wind = ewind.getValue();
      wind.removeImplWidget_Gthread();
    }
    this._mngImpl.closeImplGraphic();
    this._mngImpl = null;
    this.bStarted = false;
    this.bIsExitImplGraphic = true;
    this.bExitMain = this.bShouldExitMain;
    //displaySwt.dispose ();
    //bExit = true;
    //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
  }

  
  
  
  public void closeMainWindow() {
    this.windPrimary.windProps |= GralWindow_ifc.windRemoveOnClose; //if not set till now.
  }
  

  
  void step()
  {
    boolean bContinueDispatch;
    int ctOsEvents = 0;
    do{
      try{ bContinueDispatch = this._mngImpl.dispatchOsEvents();
      /*  
      try {
          Thread.sleep(10);
        } catch (InterruptedException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }*/
      }
      catch(Throwable exc){
        System.out.println(exc.getMessage());
        exc.printStackTrace(System.out);
        bContinueDispatch = true; //false;
      }
      ctOsEvents +=1;
      //isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
    } while(bContinueDispatch);
    if(debugPrint){ System.out.println("GralGraphicThread - dispatched os events, " + ctOsEvents); }
    checkTimes.calcTime();
    isWakedUpOnly = false;
    //System.out.println("dispatched");
    if(!bShouldExitImplGraphic){
      if(isWakedUpOnly){
        Assert.stop();
      }
      //it may be waked up by the operation system or by calling Display.wake().
      //if wakeUp() is called, isWakedUpOnly is set.
      checkTimes.cyclTime();
      //execute stored orders.
      GralGraphicTimeOrder order;
      boolean bSleep = true;
      int ctOrders = 0;
      while( (order = queueOrdersToExecute.poll()) !=null) {
        order.stateOfEvent = 'r';
        try{ 
          order.doExecute();  //calls EventIimeOrderBase.doExecute() with enqueue
        } catch(Throwable exc){
          CharSequence excText = Assert.exceptionInfo("GralGraphicThread - unexpected Exception; ", exc, 0, 99);
          System.err.append(excText);  //contains the stack trace in one line, up to 99 levels.
        }
        order.relinquish();
        bSleep = false;
        ctOrders +=1;
      }
      if(debugPrint){ System.out.println("GralGraphicThread - dispatched graphic orders, " + ctOrders); }
      if(bSleep){ //if any order is executed, don't sleep yet because some os events may forced therefore. Dispatch it!
        //no order executed. It sleeps. An os event which arrives in this time wakes up the graphic thread.
        this._mngImpl.graphicThreadSleep();
      }
    }    
  }
  

  
  protected void checkAdmissibility(boolean value){
    if(!value){
      throw new IllegalArgumentException("failure");
    }
  }
  
  
  private GralWidget findWidget(String name){
    GralWidget widg = idxNameWidgets.get(name);
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
  
  
  /**Add a standard window remove on close, resizeable.
   * If it is the first call, this is the main window.
   * @param posName Position at the screen or related to another window
   * @param sTitle Title bar
   * @return The GralWindow to add some more via concatenation.
   */
  public GralWindow addWindow(String posName, String sTitle) {
    return addWindow(posName, sTitle, GralWindow.windRemoveOnClose | GralWindow.windResizeable);
  }
    
  public GralWindow addWindow(String posName, String sTitle, int props) {
    GralWindow wdgg = new GralWindow(this.refPos(), posName
        , sTitle, props, this);
    return wdgg;
  }
 
  
  /**Add a panel to any other panel or window.
   * @param posName "@panel,... = name" determines to which panel, elsewhere to the last added panel.
   * @return GralPanelContent to set some more.
   */
  public GralPanelContent addPanel(String posName) {
    GralPanelContent wdgg = new GralPanelContent(this.refPos(), posName, this);
    return wdgg;
  }
  
  
  /**Add a panel to any other panel or window which contains only other panels as tab
   * @param posName "@panel,... = name" determines to which panel, elsewhere to the last added panel.
   * @return GralPanelContent to set some more.
   */
  public GralPanelContent addTabbedPanel(String posName) {
    GralPanelContent wdgg = new GralPanelContent(this.refPos(), posName, this);
    wdgg.setToTabbedPanel();
    return wdgg;
  }
  
  
  /**Add a panel as tab pabel. The parent should be a tabbed panel.
   * @param posName "@panel,... = name" determines to which panel, elsewhere to the last added panel.
   * @param tabName The string shown on the tab.
   * @return GralPanelContent to set some more.
   */
  public GralPanelContent addTabPanel(String posName, String nameTab) {
    GralPanelContent wdgg = new GralPanelContent(this.refPos(), posName, nameTab, this);
    return wdgg;
  }
  
  
  /**Add a panel with 9 areas to any other panel or window.
   * @param posName "@panel,... = name" determines to which panel, elsewhere to the last added panel.
   * @return GralArea9Panel to set some more.
   */
  public GralArea9Panel addArea9Panel(String posName) {
    GralArea9Panel wdgg = new GralArea9Panel(this.refPos(),posName, this);
    return wdgg;
  }
  
  
  /**Adds a text to a named widget.
   * @deprecated, use the widget itself to add or call {@link #getWidget(String)} to search it.
   *   It is over-engineered to offer all operations also in the GralMng. 
   */
  @Deprecated @Override public void addText(String name, CharSequence text){
    GralWidget widg;
    if( (widg = findWidget(name)) !=null){
      if(widg instanceof GralTextBox_ifc){
        try{ ((GralTextBox)widg).append(text); }
        catch(Exception exc){ System.err.println("TODO"); }
      }
      else {
        System.err.println("GralMng - addText not possible;" + name);
      }
    }
  }

  
  /**Adds a text to a named widget.
   * @deprecated, use the widget itself to add or call {@link #getWidget(String)} to search it.
   *   It is over-engineered to offer all operations also in the GralMng. 
   */
  @Override public GralWidget addText(String sText, int origin, GralColor textColor, GralColor backColor)
  {
    GralLabel widgg = new GralLabel(refPos(), null, sText, origin);
    widgg.setTextColor(textColor);
    if(backColor !=null) { widgg.setBackColor(backColor, 0); }
    return widgg;
  }

  
  /** Adds a text field for showing or editing a text value.
   * 
   * @param sName The registering name
   * @param width Number of grid units for length
   * @param editable true than edit-able, false to show content 
   * @param prompt If not null, than a description label is shown
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return
   */
  public GralTextField addTextField(String name, String prompt, String promptStylePosition, GralTextField.Type ... property){
    if(name !=null && name.charAt(0) == '$'){
      name = sCurrPanel + name.substring(1);
    }
    GralTextField widgg = new GralTextField(this.refPos(), name, property);
    widgg.setPrompt(prompt, promptStylePosition);
    //widgg.setEditable(editable);
    // createImplWidget_Gthread(widgg);
    //SwtTextFieldWrapper.createTextField(widgg, this);   
    return widgg;
  }

  
  @Override public GralTextField addTextField(String name, boolean editable, String prompt, String promptStylePosition){
    return addTextField(name, prompt, promptStylePosition, GralTextField.Type.editable);
  }
    
  public GralTextField addTextField(String name) {
    return addTextField(name, false, null, null);
  }

  public GralTextField addTextField(String name, boolean editable) {
    return addTextField(name, editable, null, null);
  }

  
  public GralTextField addTextField(String name, GralTextField.Type property) {
    return addTextField(name, null, null, property);
  }

  
/** Adds a text box for showing or editing a text in multi lines.
 * 
 * @param sName The registering name
 * @param width Number of grid units for length
 * @param editable true than edit-able, false to show content 
 * @param prompt If not null, than a description label is shown
 * @param promptStylePosition Position and size of description label:
 *   upper case letter: normal font, lower case letter: small font
 *   'l' left, 't' top (above field) 
 * @return
 */
@Override public GralTextBox addTextBox(String name, boolean editable, String prompt, char promptStylePosition)
{ 
  if(name !=null && name.charAt(0) == '$'){
    name = sCurrPanel + name.substring(1);
  }
  GralTextBox widgg = new GralTextBox(this.refPos(), name);
  char[] prompt1 = new char[1];
  prompt1[0] = promptStylePosition;
  widgg.setPrompt(prompt, new String(prompt1));
  widgg.setEditable(editable);
  // createImplWidget_Gthread(widgg);
  //SwtTextFieldWrapper.createTextField(widgg, this);   
  return widgg;

}



public GralTextBox addTextBox(String posName) {
  return new GralTextBox(refPos(), posName);
}



@Override public GralValueBar addValueBar(
  String sName
//, String sShowMethod
, String sDataPath
)
{ 
  GralValueBar wdgg = new GralValueBar(refPos(), sName);
  wdgg.setDataPath(sDataPath);
  // wdgg.setToPanel(this);
  return wdgg;
}


@Override public GralButton addButton(
    String sName
  , GralUserAction action
  , String sButtonText
  )
{ return addButton(sName, action, null, null, sButtonText);
}  




public GralButton addButton ( String sName , String sButtonText , GralUserAction action ) {
  return addButton(sName, action, null, null, sButtonText);
}  



@Override public GralButton addButton(
  String sName
, GralUserAction action
, String sCmd
//, String sShowMethod
, String sDataPath
, String sButtonText
  //, int height, int width
  //, String sCmd, String sUserAction, String sName)
)
{
  setNextPositionUnused();  //since 130523 it should be the valid one.
  float ySize = pos().pos.height();
  int xSize = (int)pos().pos.width();
  
  char size = ySize > 3? 'B' : 'A';
  if(sName == null){ sName = sButtonText; }
  GralButton widgButton = new GralButton(refPos(), sName);
  //SwtButton widgButton = new SwtButton(sName, this, (Composite)pos().panel.getPanelImpl(), 0, size);
  if(action !=null)
    stop();
  widgButton.setActionChange(action);  //maybe null
  widgButton.setText(sButtonText);
  //in ctor: widgButton.setPanelMng(this);
  widgButton.sCmd = sCmd;
  widgButton.setDataPath(sDataPath);
  registerWidget(widgButton);
//  createImplWidget_Gthread(widgButton);
  return widgButton;
}



@Override public GralButton addSwitchButton(
  String sName
, GralUserAction action
, String sCmd
, String sDataPath
, String sButtonText
, GralColor colorOff
, GralColor colorOn
  //, int height, int width
  //, String sCmd, String sUserAction, String sName)
)
{
  
  if(sName == null){ sName = sButtonText; }
  GralButton widgButton = new GralButton(refPos(), sName);
  int ySize = (int)widgButton.pos().height();
  int xSize = (int)widgButton.pos().width();
  char size = ySize > 3? 'B' : 'A';

  widgButton.setSwitchMode(colorOff, colorOn);
  widgButton.setActionChange(action);  //maybe null
  widgButton.setText(sButtonText);
  //widgButton.setPanelMng(this);
  widgButton.sCmd = sCmd;
  widgButton.setDataPath(sDataPath);
  registerWidget(widgButton);
//  createImplWidget_Gthread(widgButton);
  return widgButton;
}


@Override public GralButton addSwitchButton(
  String sName
, String sButtonTextOff
, String sButtonTextOn
, GralColor colorOff
, GralColor colorOn
  //, int height, int width
  //, String sCmd, String sUserAction, String sName)
)
{
  
  GralButton widgButton = new GralButton(refPos(), sName);
  int ySize = (int)widgButton.pos().height();
  int xSize = (int)widgButton.pos().width();
  
  char size = ySize > 3? 'B' : 'A';
  widgButton.setSwitchMode(colorOff, colorOn);
  widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn);
  //in ctor: widgButton.setPanelMng(this);
  if(sName !=null){ registerWidget(widgButton); }
  // createImplWidget_Gthread(widgButton);
  return widgButton;
}



public GralButton addCheckButton(
  String sPosName
, String sButtonTextOn
, String sButtonTextOff
, String sButtonTextDisabled
, GralColor colorOn
, GralColor colorOff
, GralColor colorDisabled
)
{
  GralButton widgButton = new GralButton(refPos(), sPosName);
  int ySize = (int)widgButton.pos().height();
  int xSize = (int)widgButton.pos().width();
  char size = ySize > 3? 'B' : 'A';

  widgButton.setSwitchMode(colorOff, colorOn, colorDisabled);
  widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn, sButtonTextDisabled);
  return widgButton;
}




@Override public GralLed addLed(
  String sName
//, String sShowMethod
, String sDataPath
)
{
  int ySize = (int)(pos().pos.height());
  int xSize = (int)(pos().pos.width());

  GralLed gralLed = new GralLed(sName);
  //SwtLed swtLed = new SwtLed(gralLed, this);
  gralLed.setDataPath(sDataPath);
  //registerWidget(gralLed);
  // gralLed.setToPanel(this);
  return gralLed;
}


  /**@deprecated, use the widget itself to add or call {@link #getWidget(String)} to search it.
   *   It is over-engineered to offer all operations also in the GralMng. 
   */
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
   * {@link XXXSwtCanvasStorePanel}. This class stores the line coordinates and conditions 
   * and draws it as background if drawing is invoked.
   * 
   * @param colorValue The value for color, 0xffffff is white, 0xff0000 is red.
   * @param xa start of line relative to current position in grid units.
   *          The start is relative to the given position! Not absolute in window! 
   * @param ya start of line relative to current position in grid units.
   * @param xe end of line relative to current position in grid units.
   * @param ye end of line relative to current position in grid units.
   */
  @Deprecated @Override public void addLine(int colorValue, float xa, float ya, float xe, float ye){
    //if(pos().pos.panel.getPanelImpl() instanceof SwtCanvasStorePanel){
    if(((GralPanelContent)pos().pos.parent).canvas() !=null){
      GralColor color = GralColor.getColor(colorValue);
      int xgrid = gralProps.xPixelUnit();
      int ygrid = gralProps.yPixelUnit();
      int x1 = (int)((pos().pos.x.p1 + xa) * xgrid);
      int y1 = (int)((pos().pos.y.p1 - ya) * ygrid);
      int x2 = (int)((pos().pos.x.p1 + xe) * xgrid);
      int y2 = (int)((pos().pos.y.p1 - ye) * ygrid);
      //Any panel which is created in the SWT-implementation is a CanvasStorePanel.
      //This is because lines should be drawn.
      //((SwtCanvasStorePanel) pos().pos.panel.getPanelImpl()).store.drawLine(color, x1, y1, x2, y2);
      ((GralPanelContent)pos().pos.parent).canvas().drawLine(color, x1, y1, x2, y2);
      //furtherSetPosition((int)(xe + 0.99F), (int)(ye + 0.99F));
    } else {
      log.sendMsg(0, "GuiPanelMng:addLine: panel is not a CanvasStorePanel");
    }
  }
  
  
  @Deprecated @Override public void addLine(GralColor color, List<GralPoint> points){
    GralPanelContent panel = (GralPanelContent)pos().pos.parent;
    if(panel.canvas() !=null){
      panel.canvas().drawLine(pos().pos, color, points);
    } else {
      log.sendMsg(0, "GralMng.addLine - panel is not a CanvasStorePanel;");
    }
  }
  

  
  @Deprecated @Override public void setLed(GralWidget widg, int colorBorder, int colorInner)
  {
    ((GralLed)widg).setColor(GralColor.getColor(colorBorder), GralColor.getColor(colorInner));
  }
  
  
  /**Reports all existing Widgets on Gral level, sorted to {@link GralWindow}.
   * It calls internally {@link GralWindow#reportAllContent(Appendable)} for each window.
   * <br>
   * See also {@link ImplAccess#reportContent(Appendable)} for the implementation level.
   * @param out any output, maybe especially {@link System#out} or {@link LogMessage}
   */
  public void reportGralContent(Appendable out) {
    try { out.append("==== GralMng.reportGralContent():\n");} 
    catch (Exception exc) { System.err.println("Error GralMng.reportGralContent() Appendable= "+ out); }
    for(Map.Entry<String, GralWindow> ewindow : idxWindows.entrySet()) {
      GralWindow window = ewindow.getValue();
      window.reportAllContent(out);
    }
  }
  

  @Override public ConcurrentLinkedQueue<GralVisibleWidgets_ifc> getVisiblePanels()
  {
    return listVisiblePanels;
  }

  @Override public GralVisibleWidgets_ifc getWidgetsPermanentlyUpdating(){
    ///
    return null;
  }

  
  
  @Deprecated @Override public void setFocus(GralWidget widgd)
  {
    widgd.setFocus();
  }
  
  @Deprecated @Override public void notifyFocus(GralWidget widgd)
  {
    if(widgd.getDataPath() !=null) {  
      //regard only widgets with datapath, all other are not used.
      //Therewith preserve the last focused widget which has a datapath.
      synchronized(widgetsInFocus){
        widgetsInFocus.remove(widgd);  //remove it anywhere inside
        widgetsInFocus.add(0, widgd);     //add at start.
      }
    }
  }
  
  @Override public GralWidget getWidgetInFocus(){ return widgetsInFocus.size() >0 ? widgetsInFocus.get(0) : null; }
  
  @Override public List<GralWidget> getWidgetsInFocus(){ return widgetsInFocus; }
  
  @Override public int getColorValue(String sColorName){ return GralColor.getColor(sColorName).getColorValue(); }

  @Override public GralColor getColor(String sColorName){ return GralColor.getColor(sColorName); }



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
  ){ return pos.calcWidgetPosAndSize(this.gralProps, widthParentPixel, heightParentPixel
                                    , widthWidgetNat, heightWidgetNat);
  }
  
  
  

  
  
  
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
    pos().pos.set(posAll);  //the saved position.
    return widgd;
  }

  
  
  public GralUserAction actionFileSelect = new GralUserAction("actionFileSelect")
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    { assert(false);
      return userActionGui(null, infos);
    }
  
    @Override public boolean userActionGui(int actionCode, GralWidget widgg, Object... params) 
    {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){  //supress both mouse up and down reaction
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
            GralWidget_ifc.ActionChange action = fileSelectInfo.dstWidgd.getActionChange(GralWidget_ifc.ActionChangeWhen.onEnter); 
            if(action !=null){
              Object[] args = action.args();
              if(args == null){ action.action().exec(KeyCode.menuEntered, fileSelectInfo.dstWidgd, fileSelect); }
              else { action.action().exec(KeyCode.menuEntered, fileSelectInfo.dstWidgd, args, fileSelect); }
            }
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
      //GralPanel_ifc currPanel = 
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        GralWidgetBase_ifc panel = widgd.pos().parent;
        String namePanel = panel.getName();
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

  
  @Override public GralInfoBox createHtmlInfoBox(String posName, String title, boolean onTop)
  {
    return GralInfoBox.createHtmlInfoBox(this, this.refPos(), posName, title, onTop);
  }

  
  /**Adds a text to the current panel at given position with standard colors, left origin.
   * The size of text is calculated using the height of positioning values.
   * see also {@link #addText(String, int, GralColor, GralColor)},
   * {@link #addTextField(String, boolean, String, String)}
   * @param text
   */
  @Override public GralWidget addText(String text)
  { //return addText(text, 0, GralColor.getColor("bk"), GralColor.getColor("wh"));
    return addText(text, 0, GralColor.getColor("bk"), null);
  }
  
  
  
  
  
  @Override public void repaint()
  {
    assert(false);
    //gralDevice.redraw();
  }
  
  
  
  @Override public void repaintCurrentPanel()
  {
    GralWidgetBase_ifc panel = pos().pos.parent;
    if(panel instanceof GralPanelContent) {
      ((GralPanelContent)panel).redraw();
    }
  }
  
  

  
  
  @Override public void refreshCurvesFromVariable(VariableContainer_ifc container){
    for(GralCurveView_ifc curve : curveContainer){
      if(curve.isActiv()){
        curve.refreshFromVariable(container);
      }
    }
  }
  
  @Override public boolean remove(GralWidget widget)
  { widget.remove();  //remove instance by Garbage collector.
    return true;
    
  }

  

  @Override public void writeLog(int msgId, Exception exc)
  {
    String sMsg = exc.toString();
    StackTraceElement[] stackTrace = exc.getStackTrace();
    String sWhere = stackTrace[0].getFileName() + ":" + stackTrace[0].getLineNumber();
    log.sendMsg(msgId, sMsg + " @" + sWhere);
  }
  
  
  private Runnable runGraphicThread = new Runnable() {
    @Override public void run () {
      GralMng.this.runGraphicThread();
    }
  };
  
  
  public final GralUserAction actionHelp = new  GralUserAction("actionHelp")
  { 
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { infoHelp.activate();
      infoHelp.setFocus(); //setWindowVisible(true);
      return true; 
  } };



  
  public final GralUserAction actionClose = new  GralUserAction("actionClose")
  { 
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { GralMng.this.closeMainWindow();
      return true; 
  } };



  
  
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
  public class GralMngFocusListener
  {
    
    /**Standard action on focus lost:
     * @param widgg
     */
    public void focusLostGral(GralWidget widgg){
      GralWidget.ImplAccess.setFocused(widgg, false);  //denotes that the GralWidget has lost the focus
      widgg.redraw();  //maybe changed outfit on focus lost.
      if(widgg.cfg.actionFocused !=null){ widgg.cfg.actionFocused.exec(KeyCode.focusLost, widgg); }
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
    public void focusGainedGral(GralWidget widgg){
      GralMng.this.notifyFocus(widgg);
      GralWidget.ImplAccess.setFocused(widgg, true);  //denotes that the GralWidget has gained the focus
      widgg.redraw();  //maybe changed outfit on focus gained.
      widgg.gralMng.log.sendMsg(GralMng.LogMsg.evFocused, "ev-focusGained: " + widgg.getName() + ":" + widgg.pos());
      String htmlHelp = widgg.getHtmlHelp();
      if(htmlHelp !=null && htmlHelp.startsWith(":FcmdNew.html")) { 
        Debugutil.stop(); }
      if(htmlHelp !=null && applAdapter !=null){
        applAdapter.setHelpUrl(htmlHelp);
      }
      if(widgg.cfg.actionFocused !=null){ 
        widgg.cfg.actionFocused.exec(KeyCode.focusGained, widgg); 
      }
      if(widgg.cfg.actionChangeSelect !=null && widgg.cfg.actionChangeSelect.onFocusGained !=null){ 
        widgg.cfg.actionChangeSelect.onFocusGained.action().exec(KeyCode.focusGained, widgg); 
      }
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
  public final InternalPublic _implListener = new InternalPublic();
  
  
  /**This class is used only for the implementation level of the graphic. It is not intent to use
   * by any application. It is public because the implementation level should accesses it.
   * It is the super class for several Graphic-Adapters (AWT/Swing, SWT etc.).
   * 
   * 
   */
  public static abstract class ImplAccess {
    public GralMng gralMng;
    
    protected char sizeCharProperties;

    /**The thread which runs all graphical activities. */
    protected final Thread threadGuiDispatch;

   
    public ImplAccess(GralMng mng){
      this.gralMng = mng;
      mng._mngImpl = this;
      this.sizeCharProperties = sizeCharProperties;
      threadGuiDispatch = new Thread(gralMng.runGraphicThread, "graphic");
   }
    
    protected GralPos pos(){ return gralMng.pos().pos; }
    
    protected Map<String, GralWindow> idxWindows ( ) { return gralMng.idxWindows; }

    protected GralPanel_ifc getPanel ( String name ) { return gralMng.idxPanels.get(name); }

    protected String sCurrPanel(){ return gralMng.sCurrPanel; }
    
    public void listVisiblePanels_add(GralVisibleWidgets_ifc panel){ gralMng.listVisiblePanels.add(panel); }
    
    /**Forbidden. The current panel depends on the widget. 
     * Use {@link org.vishia.gral.swt.SwtMng#getWidgetsPanel(GralWidget)}.
     * @return
     */
    @Deprecated public abstract Object getCurrentPanel();
    
    protected GralWidget indexNameWidgets(String name){ return gralMng.idxNameWidgets.get(name); }
    
    protected GralUserAction userMainKeyAction(){ return gralMng.userMainKeyAction; }
    
    
    /**This operation creates the proper implementation widgets due to the underlying Graphic system.
     * It is implemented by the specific GralMngXyz, see {@link org.vishia.gral.swt.SwtMng}.
     * @param widgg The given instantiated GralWidget but without implementation instacne yet.
     */
    public abstract void createImplWidget_Gthread(GralWidget widgg);
    
    
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
    
    
    /**Creates a box inside the current panel to hold some widgets.
     * 
     * @return
     * @since 2010-05-01
     */
    //protected abstract GralPanelContent createCompositeBox(String name);
   
    /**Creates an independent grid panel which is managed by this.
     * The panel can be associated to any graphic frame.
     * @param namePanel
     * @param backGround
     * @param xG
     * @param yG
     * @param xS
     * @param yS
     * @return
     */
    //protected abstract GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG, int xS, int yS);

    
    public abstract boolean remove(GralPanel_ifc compositeBox);
    
    /**Creates the menu bar for the given window.
     * This method is invoked only in {@link GralWindow#getMenuBar()} whereby an existing
     * menu bar is stored in the {@link GralWindow#menuBarGral} association. 
     * @param windg The window
     */
    protected abstract GralMenu createMenuBar(GralWindow windg);
    
    
    @Deprecated public abstract GralWindow createWindow(String name, String title, int windProps);
    
    
    @Deprecated protected abstract void createSubWindow(GralWindow windowGral) throws IOException;
    
//    @Deprecated public abstract GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user, int property);
    
    @Deprecated public abstract GralWidget addText(String sText, char size, int color);
    
    
    public abstract GralHtmlBox addHtmlBox(String name);
    
    public abstract Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd);
    
    public abstract GralWidget addSlider(
        String sName
        , GralUserAction action
        , String sShowMethod
        , String sDataPath
        );
    
    
    public abstract GralCurveView addCurveViewY(String sName, int nrofXvalues, GralCurveView.CommonCurve common);
    
    
    public abstract GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter, String sCmdRelease);

    public abstract void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease);
    
    
    @Deprecated public abstract GralTable addTable(String sName, int height, int[] columnWidths);
    
    public abstract GralFileDialog_ifc createFileDialog();
    
    protected abstract GralMenu XXXaddPopupMenu(String sName);
    

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

    @Deprecated public abstract String getValueFromWidget(GralWidget widgd);
    
    /**Gets the color of the graphic implementation (swt.Color, swing.TODO)
     * Either the implementation color instance is stored already in the GralColor,
     * or it will be created, stored in GralColor and returned here.
     * @param color The Color.
     * @return Instance of the implementation color.
     */
    public abstract Object getColorImpl(GralColor color);
    
    /**Forces the redrawing for all set samples. It should be called after { @link #setSampleCurveViewY(String, float[])}.
     * @param sName The name of the widget.
     */
    protected abstract void redrawWidget(String sName);
    
    
    
    /**Forces the resizing of the given widged. 
     * @param widgd the widget
     */
    protected abstract void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent);
    
    
    /**Adds a sampling value set.
     * @param sName The registered name
     * @param values The values.
     */
    protected abstract void setSampleCurveViewY(String sName, float[] values);
    
    /**Shows the context menu of any widget independent of the internal right-mouse invocation.
     * @param widg The widget.
     * @return true if the widget have a context menu.
     */
    protected abstract boolean showContextMenuGthread(GralWidget widg);
    
    
    /**Reports all existing Widgets on implementation level.
     * This operation is implemented in the specific implementation managers.
     * <br>
     * See also {@link GralMng#reportGralContent(Appendable)} for the Gral level.
     * @param out any output, maybe especially {@link System#out} or {@link LogMessage}
     */
    public abstract void reportContent(Appendable out) throws IOException;

    public abstract void finishInit();
    
    
    protected void startThread() {
      threadGuiDispatch.start();
      gralMng.waitForStart();
   }

    
   
    /**This method should be implemented by the graphical implementation layer. It should build the graphic main window
     * and returned when finished. This routine is called as the first routine in the Graphic thread's
     * method {@link #runGraphicThread()}. See {@link org.vishia.gral.swt.SwtGraphicThread}. */
    protected abstract void initGraphic ( );
    
    
    protected abstract void closeImplGraphic ( );
    
    /**Calls the dispatch routine of the implementation graphic.
     * @return true if dispatching should be continued.
     */
    protected abstract boolean dispatchOsEvents();
    
    
    /**Forces the graphic thread to sleep and wait for any events. Either this routine returns
     * if {@link #wakeup()} is called or this routine returns if the operation system wakes up the graphic thread. */
    protected abstract void graphicThreadSleep();
    
    /**This method should be implemented by the graphical base. It should be waked up the execution 
     * of the graphic thread because some actions are registered.. */
    public abstract void wakeup();
    
    //protected char sizeCharProperties(){ return gralGraphicThread.sizeCharProperties; }


    
    /**Sets a given and registered window visible at the given position and size or set it invisible.
     * <br>
     * A window can be created by invoking {@link org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(String, boolean)}
     * in the build phase of the gui. It can be hidden because it is not necessary to show and operate with them.
     * In a adequate phase of operate it can be shown and focused.
     * <br>
     * The position is given relative to that panel,
     * which is stored in {@link GralPos#panel}. To get a position instance,
     * you can set a position invoking 
     * <ul>
     * <li>{@link org.vishia.gral.ifc.GralMngBuild_ifc#selectPanel(String)}
     * <li>{@link org.vishia.gral.ifc.GralMngBuild_ifc#setPosition(float, float, float, float, int, char)}
     * <li>GralGridPos pos = {@link org.vishia.gral.ifc.GralMngBuild_ifc#getPositionInPanel()}.
     * </ul>
     * That can be done in the build phase of the graphic. The position can be stored. It is possible to adjust
     * the position relative to the unchanged panel by changing the values of {@link GralPos#x} etc.
     * It is possible too to change the Panel which relates to the position. Then the grid managing instance 
     * have to be known via the {@link org.vishia.gral.ifc.GralMngBuild_ifc} to select a panel.
     * The panels may be moved or resized. With the knowledge of the relative position of the window in respect to a panel
     * of the parent window, the window can be placed onto a proper position of the whole display.
     *   
     * @param window the instance of the window wrapper.
     * @param atPos If null then hide the window. If not null then show the window. 
     *        The position and size of the window is given related to any panel of any other window. 
     *         
     * @return true if it is visible.
     * @deprecated
     */
    @Deprecated
    protected abstract boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos);
    
  }
  
  
  

  void stop(){}

  
  protected GralMenu createContextMenu(GralWidget widg){ 
    return widg.getContextMenu();
  }
  
  protected GralMenu createMenuBar(GralWindow windg){ 
    return windg.getMenuBar();
  }

  

  @Override public GralWidget addSlider(String sName, GralUserAction action, String sShowMethod,
      String sDataPath)
  {
    GralSlider slider = new GralSlider(sName, this);
    slider.setActionChange(action);
    slider.setDataPath(sDataPath);
    return slider;
  }

  @Override public GralTable addTable(String sPosName, int height, int[] columnWidths)
  {
    GralTable table = new GralTable<>(this.refPos(), sPosName, height, columnWidths);
    return table;
  }

  @Override @Deprecated public GralWidget addText(String sText, char size, int color)
  {
    GralLabel label = new GralLabel(pos().pos, sText, sText, 0);
    label.setTextColor(GralColor.getColor(color));
    return label;    
  }

  @Override public Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  { //GralImageBase
    // TODO Auto-generated method stub
    return null; //_mngImpl.addImage(sName, imageStream, height, width, sCmd);
  }

  @Override public GralHtmlBox addHtmlBox(String name)
  {
    return new GralHtmlBox(pos().pos, name);
  }

  @Override public GralCurveView addCurveViewY(String sPosName, int nrofXvalues, CommonCurve common, TimedValues tracksValues)
  {
    return new GralCurveView(this.pos().pos, sPosName, nrofXvalues, common, tracksValues);
  }

  @Override public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter,
      String sCmdRelease)
  {
    return addFocusAction(sName, action, sCmdEnter, sCmdRelease);
  }

  @Override public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter,
      String sCmdRelease)
  {
    addFocusAction(widgetInfo, action, sCmdEnter, sCmdRelease);
  }

  @Override public GralPanelContent createCompositeBox(String name)
  {
    return new GralPanelContent(pos().pos, name, this);
  }

  @Override public GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG,
      int xS, int yS)
  {
    GralPanelContent panel = new GralPanelContent(pos().pos, namePanel, this);
    panel.setBackColor(backGround, 0);
    panel.setGrid(yG, xG, yS, xS, 15, 25);
    return panel;
  }

  @Override public boolean remove(GralPanel_ifc compositeBox)
  {
    if(this._mngImpl !=null) {
      return _mngImpl.remove(compositeBox);
    } else {
      return false;
    }
  }

  
  



  /**Creates only the GralWindow with position in GralMng
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(java.lang.String, java.lang.String, int)
   * @ccdeprecated use {@link GralWindow#GralWindow(String, String, String, int)} and then {@link GralWidget#createImplWidget_Gthread()}
   *   with this window.
   */
  @Override public GralWindow createWindow(String posName, String title, int windProps)
  { GralPos pos = pos().pos;  //without clone.
    //String sPos = pos.posString(); 
    GralWindow windowGral = new GralWindow(pos, posName, title, windProps, this);
//    try {
//      this._mngImpl.createSubWindow(windowGral);
//    } catch(Exception exc) {
//      CheckVs.exceptionInfo("unexpected", exc, 0, 10);
//    }
    return windowGral;
  }

  @Override public GralFileDialog_ifc createFileDialog()
  {
    GralFileSelectWindow fileSelWin = new GralFileSelectWindow("File select", this);
    return fileSelWin;
  }

  @Override public void redrawWidget(String sName)
  {
    if(this._mngImpl !=null) {
      _mngImpl.redrawWidget(sName);
    }
  }

  @Override public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
  {
    // TODO Auto-generated method stub
    if(this._mngImpl !=null) {
      _mngImpl.resizeWidget(widgd, xSizeParent, ySizeParent);
    }
  }

  @Override public boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void setSampleCurveViewY(String sName, float[] values)
  {
    //_mngImpl.setSampleCurveViewY(sName, values);
  }
	

  
  /**This class defines only static numbers for messages. 
   * The numbers should be a little bit sorted,
   * so that dispatching of messages can be done. 
   *
   */
  public static class LogMsg {
    public static int ctorWdg = 1001;
    public static int newPanel = 1010;
    
    public static int newImplTable = 1051;
    
    public static int setVisible = 1130;
    public static int setFocus = 1131;
    
    public static int gralTable_updateCells = 1201;
    public static int gralFileSelector_fillin = 1211;
    public static final int gralFileSelector_fillinFinished = 1212;
    
    public static int evFocused = 1501;
  }
	
}
