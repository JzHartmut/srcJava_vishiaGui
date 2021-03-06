package org.vishia.gral.base;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralCurveView.CommonCurve;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgWriter;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralFactory;
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
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.gral.widget.GralLabel;
import org.vishia.inspcPC.InspcReplAlias;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmdLoggingStream;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Debugutil;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.ReplaceAlias_ifc;

/**This is the Manager for the graphic. 
 * It contains the independent parts of graphic organization.
 * This class <code>GralMng</code> is a common approach to work with graphical interface simply. 
 * The inner class {@link ImplAccess} is implemented by the several graphic-system-supporting classes.
 * <ul>
 * <li>{@link org.vishia.gral.swt.SwtMng}
 * <li>{@link org.vishia.gral.awt.AwtWidgetMng}
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
  public final static String sVersion = "2015-10-30";
  
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
  
  
  
  /*package private*/ static final class PosThreadSafe
  {
    /**Position of the next widget to add. If some widgets are added one after another, 
     * it is similar like a flow-layout.
     * But the position can be set.
     * The values inside the position are positive in any case, so that the calculation of size is simple.
     */
    protected final GralPos pos; //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
    
    /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
     * calculates the next position in direction see {@link GralPos#setNextPosition()}. */
    protected boolean posUsed;
    
    final long threadId;
    
    PosThreadSafe() {
      threadId = Thread.currentThread().getId();
      pos = new GralPos();
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
  private PosThreadSafe posCurrent = new PosThreadSafe();

  private final Map<Long, PosThreadSafe> posThreadSafe = new TreeMap<Long, PosThreadSafe>();
  { //part of construction: safe the first instance.
    posThreadSafe.put(posCurrent.threadId, posCurrent);
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
  public final GralGraphicThread gralDevice = new GralGraphicThread();

  /**Properties of this Dialog Window. */
  public GralGridProperties propertiesGui;

  /**Index of all input fields to access symbolic for all panels. */
  protected final Map<String, GralWidget> indexNameWidgets = new TreeMap<String, GralWidget>();

  /**Index of all input fields to access symbolic. NOTE: The generic type of WidgetDescriptor is unknown,
   * because the set is used independently from the graphic system. */
  protected final Map<String, GralWidget> showFields = new TreeMap<String, GralWidget>();

  //private final IndexMultiTable showFieldsM;

  private final List<GralWidget> widgetsInFocus = new LinkedList<GralWidget>();
 
  /**The one instance of the primary window created in the {@link org/vishia/ifc/GralFactory#createWindow(String, String, int)} . */
  private GralWindow windPrimary;
  
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
   */
  public final LogMessage log;
  
  
  protected GralMngApplAdapter_ifc applAdapter;
  
	/**Composition of some curve view widgets, which should be filled indepentent of there visibility. */
  @Deprecated public final List<GralCurveView_ifc> curveContainer = new LinkedList<GralCurveView_ifc>();
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
  protected final GralPos XXXpos = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction see {@link GralPos#setNextPosition()}. */
  protected boolean XXXposUsed;
  

  
  
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
  
  
  /**Sets the position with a given String, see {@link GralPos#setPosition(CharSequence, GralPos)}
   * whereby the parent is the current position value.
   * @param sPosition
   * @throws ParseException
   */
  public void setPos(String sPosition) 
  throws ParseException
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(sPosition, null);
    pos.posUsed = false;
  }
  
  @Override public void setPositionSize(int line, int column, int height, int width, char direction)
  { PosThreadSafe pos = pos();
    if(line < 0){ line = pos.posUsed? GralPos.next: GralPos.same; }
    if(column < 0){ column = pos.posUsed? GralPos.next: GralPos.same; }
    setFinePosition(line, 0, height + GralPos.size, 0, column, 0, width + GralPos.size, 0, 1, direction, 0 ,0 , pos.pos);
  }

  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { setPosition(pos().pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
  }


  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(pos.pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
    pos.posUsed = false;
  }


  @Override public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction)
  { PosThreadSafe pos = pos();
    pos.pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
    pos.posUsed = false;
  }
  
  @Override public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction, float border)
  { PosThreadSafe pos = pos();
      pos.pos.setPosition(framePos, line, lineEndOrSize, column, columnEndOrSize, origin, direction, border);
      pos.posUsed = false;
  }
  
  @Override public void setFinePosition(int line, int yPosFrac, int ye, int yef
    , int column, int xPosFrac, int xe, int xef, int origin, char direction, int border, int borderFrac, GralPos frame)
  { PosThreadSafe pos = pos();
    pos.pos.setFinePosition(line, yPosFrac, ye, yef, column, xPosFrac, xe, xef, origin, direction, border, borderFrac, frame);
    pos.posUsed = false;
  }
  
  
  @Override public void setSize(int height, int ySizeFrac, int width, int xSizeFrac)
  { PosThreadSafe pos = pos();
    pos.pos.setSize(height, ySizeFrac, width, xSizeFrac);  //NOTE: setSize sets the next pos 
    pos.posUsed = false;
  }
  
  void setSize(float height, float width)
  { PosThreadSafe pos = pos();
    pos.pos.setSize(height, width, pos.pos);
    pos.posUsed = false;
  }
  
  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. TODO remove in AwtMng*/
  @Deprecated public void setNextPosition()
  { PosThreadSafe pos = pos();
    if(pos.posUsed){
      pos.pos.setNextPosition();
    }
    pos.posUsed = true;
  }  
  
  /**Not for user: Checks whether the position is used, sets the next position then, markes the position as used.
   * See @link GralPos#setNextPosition(), {@link #posUsed}. */
  public void setNextPositionUnused()
  { PosThreadSafe pos = pos();
    if(pos.posUsed){
      pos.pos.setNextPosition();
    }
    pos.posUsed = false;
  }  
  
  public void registerShowField(GralWidget widg){
    //link the widget with is information together.
    if(widgetsInFocus.size()==0 && widg.getDataPath() !=null) {
      //it has not a datapath initally. Never come here.
      widgetsInFocus.add(widg);   //add first widget.
    }
    if(widg.name !=null){
      showFields.put(widg.name, widg);
    }

  }
  
  
  @Override public GralPos getPositionInPanel(){ return pos().pos.clone(); }
  
  public GralPos getPosCheckNext(){ 
    PosThreadSafe pos = pos();
    if(pos.posUsed){
      pos.pos.setNextPosition();
      pos.posUsed = false;
    }
    pos.posUsed = true;
    return pos.pos.clone(); 
  }
  
  
  /**Used for deprecated style, without independent GralWidget. TODO remove.
   * @return Independent GralPos from the GralMng
   */
  public GralPos getPosOldPositioning(){ return getPosCheckNext(); }
	
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
  
  //private String lastClickedDatapath;
  
  //private String lastClickedVariable;
  
  @Deprecated @Override public List<GralWidget> getListCurrWidgets(){ return pos().pos.panel.widgetList; }
	
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

  public ImplAccess impl;
  
  private static GralMng singleton;
	
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

  private GralMng(LogMessage log)
  { //this.propertiesGui = props;
      this.log = log;
    //its a user action able to use in scripts.
    userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
    GralMng.singleton = this; 
  }

  
  /**Creates the singleton instance of the GralMng. If this routine is invoked more as one time, the first invocation
   * is the correct one. The more-time-invocation is supported because an application may not invoke this routine.
   * Therefore it is invoked later additional.
   * <br> See also {@link #get()}.
   * @param log The first invocation determines the log output.
   * @return true if created, false if exists already.
   * @??deprecated {@link #get()} is sufficient TODO detemine log
   */
  public static boolean create(LogMessage log){
    if(singleton !=null) return false;
    else { 
      singleton = new GralMng(log);
      return true;
    }
  }
  

  /**Returns the singleton of the GralMng. Creates it if it is not instantiated yet. 
   * On creation all logging output will be redirect to System.out.
   * To use another logging output, create the GralMng using {@link GralMng#GralMng(LogMessage)}
   * on start of application, before this routine is firstly called. */
  public static GralMng get(){ 
    if(singleton == null) { //not initialized yet, early invocation:
      singleton = new GralMng(new MainCmdLoggingStream(System.out));
    }
    return singleton; 
  }
  
  
  public void setProperties(GralGridProperties props) {
    this.propertiesGui = props;
  }
  
  public void setFirstlyThePrimaryWindow(GralWindow primaryWindow){
    if(this.windPrimary !=null)
      throw new IllegalStateException("Primary Window should set only one time.");
    this.windPrimary = primaryWindow; 
    panels.put("primaryWindow", primaryWindow);
  };
  
  
  public static void createMainWindow(GralFactory factory, GralWindow window, char sizeShow, int left, int top, int xSize, int ySize) {
    factory.createWindow(window, sizeShow, left, top, xSize, ySize);
  }
  
  
  
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
    infoHelp = GralInfoBox.createHtmlInfoBox(null, "Help", "Help", true);
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
    if(widgg instanceof GralWindow){
      GralWindow wind1 = (GralWindow)widgg;
      impl.createSubWindow(wind1);
      //registerPanel(wind1);
      //set the current position of the manager to this window, initalize it.
      //PosThreadSafe pos = pos();
      //pos.pos.panel = wind1; //it is selected.
      //pos.pos.setPosition(null, 0,0,0,0,0,'r');  //per default the whole window as position and size.

    } else {  
      impl.createImplWidget_Gthread(widgg); 
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
  
  
  @Override public ReplaceAlias_ifc getReplacerAlias(){ return replacerAlias; }
  
  

  public void setHelpBase(String path){ 
    sHelpBase = path; 
  }
  
  
  
  public void setHelpUrl(String url){ 
    String sUrl;
    if(url.startsWith("+")){
      sUrl = sHelpBase + url.substring(1);
    } else if(FileSystem.isAbsolutePath(url)) { 
      sUrl = url;  //absolute path
    } else if (sHelpBase !=null) { //it is a directory which does not end with "/"
      sUrl = sHelpBase + "/" + url;  //url is a "file.html+label"
    } else {
      sUrl = url;  //should be absolute
    }
    if(infoHelp !=null) infoHelp.setUrl(sUrl); 
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
  @Override public void selectPanel(String sName){ 
    PosThreadSafe pos = pos();
    pos.pos.panel = panels.get(sName);
    sCurrPanel = sName;
    if(pos.pos.panel == null && currTabPanel !=null) {
      //use the position of the current tab panel for the WidgetMng. Its panel is the parent.
      pos.pos.set(currTabPanel.pos());  
      pos.pos.panel = currTabPanel.addGridPanel(sName, /*"&" + */sName,1,1,10,10);
      panels.put(sName, pos.pos.panel);  //TODO unnecessay, see addGridPanel
      log.sendMsg(0, "GuiPanelMng:selectPanel: unknown panel name %s", sName);
      //Note: because the pos.panel is null, not placement will be done.
    }
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
  }
  
  
  /**Selects the given panel as current panel to build some content. */
  @Override public void selectPanel(GralPanelContent panel) {
    pos().pos.panel = panel;
    sCurrPanel = panel == null ? null: panel.name;
    setPosition(0,0,0,0,0,'d');  //set the position to default, full panel because the panel was selected newly.
  }
  
  /**Selects the primary window as current panel to build some content. */
  @Override public void selectPrimaryWindow() { selectPanel(windPrimary); } 
  
  @Override public boolean currThreadIsGraphic(){
    return Thread.currentThread().getId() == gralDevice.getThreadIdGui();
  }


  
  @Override public GralWidget getWidget(String name)
  { return indexNameWidgets.get(name);
  }
  
  
  
  public Iterable<GralWidget> getWidgetIter() {
    return indexNameWidgets.values();
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
    indexNameWidgets.put(name, widgd);
  }
  
  void removeWidget(String name) {
    indexNameWidgets.remove(name);
  }
  
  @Override public void registerWidget(GralWidget widgd)
  {
    /*
    PosThreadSafe pos = pos();
    GralPanelContent panel = widgd.pos() !=null ? widgd.pos().panel : pos.pos.panel;
    if(widgd.name != null){
      indexNameWidgets.put(widgd.name, widgd);
    }
    //only widgets with size from right TODO percent size too.
    boolean toResize = pos.pos.x.p1 < 0 || pos.pos.x.p2 <= 0 || pos.pos.y.p1< 0 || pos.pos.y.p2 <=0; 
    panel.addWidget(widgd, toResize);
    */
  }
  
  
  public void deregisterWidgetName(GralWidget widg)
  {
    if(widg.name != null){
      indexNameWidgets.remove(widg.name);
    }
    
  }
  
  /**Registers a panel to place the widgets. 
   * After registration, this panel is the current one, stored in {@link #pos()} for this thread. 
   * The panel can be selected with its name calling the {@link #selectPanel(String)} -routine
   * @param key Name of the panel.
   * @param panel The panel.
   */
  @Override public void registerPanel(GralPanelContent panel){
    GralPanelContent exist = panels.get(panel.name);
    if(exist !=null){
      if(exist == panel) System.out.println("info: unnecessary registerPanel " + panel.name);
      else System.err.println("info: faulty registerPanel " + panel.name);
    }
    panels.put(panel.name, panel);
  }
  
  
  public void setPosPanel(GralPanelContent panel) {
    GralMng.PosThreadSafe pos = pos();
    pos.pos.panel = panel;
    //initialize the position because its a new panel. The initial position is the whole panel.
    pos.pos.setFinePosition(0,0,0,0,0,0,0,0,0,'d',0,0, pos.pos);
    sCurrPanel = panel.name;
  }
  
  
  /*package private*/ void deregisterPanel(GralPanelContent panel) {
    if(sCurrPanel.equals(panel.name)) {
      sCurrPanel = windPrimary.name;
    }
    panels.remove(panel.name);
  }
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set<Map.Entry<String, GralWidget>> getShowFields()
  {
    Set<Map.Entry<String, GralWidget>> set =showFields.entrySet();
    return set; //(Set<Entry<String, WidgetDescriptor>>)set;
  }

  
  public Map<String, String> getAllValues()
  {
      Map<String, String> values = new TreeMap<String, String>();
  for(GralWidget input: indexNameWidgets.values()){
      String sValue = getValueFromWidget(input);
    values.put(input.name, sValue);
  }
  return values;
  }

  @Override public String getValue(String sName)
  { final String sValue;
      GralWidget widgetDescr = indexNameWidgets.get(sName);
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
  /*package private*/public void setTabbedPanel(GralPanelContent tabbedPanel){
    pos().pos.panel = tabbedPanel;
  }
  
  
  public GralPanelContent getPanel(String name){
    return panels.get(name);
  }
  
  
  public GralWindow getPrimaryWindow(){ return windPrimary; }
  
  
  public GralPanelContent getCurrentPanel(){ return pos().pos.panel; }
  
  public GralPanelActivated_ifc actionPanelActivate = new GralPanelActivated_ifc()
  { @Override public void panelActivatedGui(List<GralWidget> widgetsP)
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

  
  @Override public GralWidget addText(String sText, int origin, GralColor textColor, GralColor backColor)
  {
    GralLabel widgg = new GralLabel(null, sText, origin);
    widgg.setTextColor(textColor);
    widgg.setBackColor(backColor, 0);
    widgg.setToPanel(this); //Note: sets TextFont, don't call this.setToPanel
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
  @Override public GralTextField addTextField(String name, boolean editable, String prompt, String promptStylePosition){
    if(name !=null && name.charAt(0) == '$'){
      name = sCurrPanel + name.substring(1);
    }
    GralTextField widgg = new GralTextField(name);
    widgg.setPrompt(prompt, promptStylePosition);
    widgg.setEditable(editable);
    createImplWidget_Gthread(widgg);
    //SwtTextFieldWrapper.createTextField(widgg, this);   
    return widgg;
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
  GralTextBox widgg = new GralTextBox(name);
  char[] prompt1 = new char[1];
  prompt1[0] = promptStylePosition;
  widgg.setPrompt(prompt, new String(prompt1));
  widgg.setEditable(editable);
  createImplWidget_Gthread(widgg);
  //SwtTextFieldWrapper.createTextField(widgg, this);   
  return widgg;

}



@Override public GralValueBar addValueBar(
  String sName
//, String sShowMethod
, String sDataPath
)
{ 
  GralValueBar wdgg = new GralValueBar(sName);
  wdgg.setDataPath(sDataPath);
  wdgg.setToPanel(this);
  return wdgg;
}


@Override public GralButton addButton(
    String sName
  , GralUserAction action
  , String sButtonText
  )
{ return addButton(sName, action, null, null, sButtonText);
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
  GralButton widgButton = new GralButton(sName);
  //SwtButton widgButton = new SwtButton(sName, this, (Composite)pos().panel.getPanelImpl(), 0, size);
  if(action !=null)
    stop();
  widgButton.setActionChange(action);  //maybe null
  widgButton.setText(sButtonText);
  //in ctor: widgButton.setPanelMng(this);
  widgButton.sCmd = sCmd;
  widgButton.setDataPath(sDataPath);
  registerWidget(widgButton);
  createImplWidget_Gthread(widgButton);
  return widgButton;
}



@Override public GralButton addSwitchButton(
  String sName
, GralUserAction action
, String sCmd
, String sDataPath
, String sButtonText
, String sColor0
, String sColor1
  //, int height, int width
  //, String sCmd, String sUserAction, String sName)
)
{
  int ySize = (int)pos().pos.height();
  int xSize = (int)pos().pos.width();
  
  char size = ySize > 3? 'B' : 'A';
  if(sName == null){ sName = sButtonText; }
  GralButton widgButton = new GralButton(sName);
  GralColor colorOff = GralColor.getColor(sColor0);
  GralColor colorOn = GralColor.getColor(sColor1);
  widgButton.setSwitchMode(colorOff, colorOn);
  widgButton.setActionChange(action);  //maybe null
  widgButton.setText(sButtonText);
  //widgButton.setPanelMng(this);
  widgButton.sCmd = sCmd;
  widgButton.setDataPath(sDataPath);
  registerWidget(widgButton);
  createImplWidget_Gthread(widgButton);
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
  int ySize = (int)pos().pos.height();
  int xSize = (int)pos().pos.width();
  
  char size = ySize > 3? 'B' : 'A';
  GralButton widgButton = new GralButton(sName);
  widgButton.setSwitchMode(colorOff, colorOn);
  widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn);
  //in ctor: widgButton.setPanelMng(this);
  if(sName !=null){ registerWidget(widgButton); }
  createImplWidget_Gthread(widgButton);
  return widgButton;
}



public GralButton addCheckButton(
  String sName
, String sButtonTextOn
, String sButtonTextOff
, String sButtonTextDisabled
, GralColor colorOn
, GralColor colorOff
, GralColor colorDisabled
)
{
  int ySize = (int)pos().pos.height();
  int xSize = (int)pos().pos.width();
  
  char size = ySize > 3? 'B' : 'A';
  GralButton widgButton = new GralButton(sName);
  widgButton.setSwitchMode(colorOff, colorOn, colorDisabled);
  widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn, sButtonTextDisabled);
  //widgButton.setPanelMng(this);
  if(sName !=null){ registerWidget(widgButton); }
  createImplWidget_Gthread(widgButton);
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
  gralLed.setToPanel(this);
  return gralLed;
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
  @Deprecated @Override public void addLine(int colorValue, float xa, float ya, float xe, float ye){
    //if(pos().pos.panel.getPanelImpl() instanceof SwtCanvasStorePanel){
    if(pos().pos.panel.canvas !=null){
      GralColor color = propertiesGui.color(colorValue);
      int xgrid = propertiesGui.xPixelUnit();
      int ygrid = propertiesGui.yPixelUnit();
      int x1 = (int)((pos().pos.x.p1 + xa) * xgrid);
      int y1 = (int)((pos().pos.y.p1 - ya) * ygrid);
      int x2 = (int)((pos().pos.x.p1 + xe) * xgrid);
      int y2 = (int)((pos().pos.y.p1 - ye) * ygrid);
      //Any panel which is created in the SWT-implementation is a CanvasStorePanel.
      //This is because lines should be drawn.
      //((SwtCanvasStorePanel) pos().pos.panel.getPanelImpl()).store.drawLine(color, x1, y1, x2, y2);
      pos().pos.panel.canvas.drawLine(color, x1, y1, x2, y2);
      //furtherSetPosition((int)(xe + 0.99F), (int)(ye + 0.99F));
    } else {
      log.sendMsg(0, "GuiPanelMng:addLine: panel is not a CanvasStorePanel");
    }
  }
  
  
  @Override public void addLine(GralColor color, List<GralPoint> points){
    if(pos().pos.panel.canvas !=null){
      pos().pos.panel.canvas.drawLine(pos().pos, color, points);
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
  
  @Override public int getColorValue(String sColorName){ return propertiesGui.getColorValue(sColorName); }

  @Override public GralColor getColor(String sColorName){ return propertiesGui.color(getColorValue(sColorName)); }



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

  
  
  GralUserAction actionFileSelect = new GralUserAction("actionFileSelect")
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
      //GralPanelContent currPanel = 
      GralWidget widgd = getWidgetInFocus();
      if(widgd !=null){
        GralPanelContent panel = widgd.pos().panel;
        String namePanel = panel.name;
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

  
  @Override public GralInfoBox createHtmlInfoBox(String posString, String name, String title, boolean onTop)
  {
    return GralInfoBox.createHtmlInfoBox(posString, name, title, onTop);
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
  
  
  
  
  
  @Override public void repaint()
  {
    assert(false);
    //gralDevice.redraw();
  }
  
  
  
  @Override public void repaintCurrentPanel()
  {
    pos().pos.panel.repaint();
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
    { GralMng.get().gralDevice().closeMainWindow();
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
      widgg.repaint();  //maybe changed outfit on focus lost.
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
      widgg.repaint();  //maybe changed outfit on focus gained.
      String htmlHelp = widgg.getHtmlHelp();
      if(htmlHelp !=null && applAdapter !=null){
        applAdapter.setHelpUrl(htmlHelp);
      }
      if(widgg.cfg.actionFocused !=null){ widgg.cfg.actionFocused.exec(KeyCode.focusGained, widgg); }
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
  
  
  /**This class is used only for the implementation level of the graphic. It is not intent to use
   * by any application. It is public because the implementation level should accesses it.
   * It is the super class for several Graphic-Adapters (AWT/Swing, SWT etc.).
   * 
   * 
   */
  public static abstract class ImplAccess {
    public GralMng mng;
    
    
    public ImplAccess(GralMng mng, GralGridProperties props){
      this.mng = mng;
      mng.setProperties(props);
      mng.impl = this;
    }
    
    protected GralPos pos(){ return mng.pos().pos; }

    protected String sCurrPanel(){ return mng.sCurrPanel; }
    
    protected void listVisiblePanels_add(GralTabbedPanel panel){ mng.listVisiblePanels.add(panel); }
    
    public abstract Object getCurrentPanel();
    
    protected GralWidget indexNameWidgets(String name){ return mng.indexNameWidgets.get(name); }
    
    protected GralUserAction userMainKeyAction(){ return mng.userMainKeyAction; }
    
    
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
    protected abstract GralPanelContent createCompositeBox(String name);
   
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
    protected abstract GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG, int xS, int yS);

    
    public abstract boolean remove(GralPanelContent compositeBox);
    
    /**Creates the menu bar for the given window.
     * This method is invoked only in {@link GralWindow#getMenuBar()} whereby an existing
     * menu bar is stored in the {@link GralWindow#menuBarGral} association. 
     * @param windg The window
     */
    protected abstract GralMenu createMenuBar(GralWindow windg);
    
    
    @Deprecated public abstract GralWindow createWindow(String name, String title, int windProps);
    
    
    protected abstract void createSubWindow(GralWindow windowGral);
    
    public abstract GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user, int property);
    
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

  
  protected GralMenu createContextMenu(GralWidget widg){ return impl.createContextMenu(widg); }
  
  protected GralMenu createMenuBar(GralWindow windg){ return impl.createMenuBar(windg); }

  
  @Override public GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user,
      int properties)
  {
    return impl.addTabbedPanel(namePanel, user, properties);
  }

  @Override public GralWidget addSlider(String sName, GralUserAction action, String sShowMethod,
      String sDataPath)
  {
    // TODO Auto-generated method stub
    return impl.addSlider(sName, action, sShowMethod, sDataPath);
  }

  @Override @Deprecated public GralTable addTable(String sName, int height, int[] columnWidths)
  {
    // TODO Auto-generated method stub
    return impl.addTable(sName, height, columnWidths);
  }

  @Override @Deprecated public GralWidget addText(String sText, char size, int color)
  {
    // TODO Auto-generated method stub
    return impl.addText(sText, size, color);
  }

  @Override public Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  {
    // TODO Auto-generated method stub
    return impl.addImage(sName, imageStream, height, width, sCmd);
  }

  @Override public GralHtmlBox addHtmlBox(String name)
  {
    // TODO Auto-generated method stub
    return impl.addHtmlBox(name);
  }

  @Override public GralCurveView addCurveViewY(String sName, int nrofXvalues, CommonCurve common)
  {
    // TODO Auto-generated method stub
    return impl.addCurveViewY(sName, nrofXvalues, common);
  }

  @Override public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter,
      String sCmdRelease)
  {
    // TODO Auto-generated method stub
    return addFocusAction(sName, action, sCmdEnter, sCmdRelease);
  }

  @Override public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter,
      String sCmdRelease)
  {
    // TODO Auto-generated method stub
    impl.addFocusAction(widgetInfo, action, sCmdEnter, sCmdRelease);
  }

  @Override public GralPanelContent createCompositeBox(String name)
  {
    // TODO Auto-generated method stub
    return impl.createCompositeBox(name);
  }

  @Override public GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG,
      int xS, int yS)
  {
    // TODO Auto-generated method stub
    return impl.createGridPanel(namePanel, backGround, xG, yG, xS, yS);
  }

  @Override public boolean remove(GralPanelContent compositeBox)
  {
    // TODO Auto-generated method stub
    return impl.remove(compositeBox);
  }

  
  

  /**Must only invoke from the Main window close listener. */
  public static void closeGral() {
    singleton.gralDevice.bExit = true; 
  }
  


  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(java.lang.String, java.lang.String, int)
   * @deprecated use {@link GralWindow#GralWindow(String, String, String, int)} and then {@link GralWidget#createImplWidget_Gthread()}
   *   with this window.
   */
  @Override @Deprecated public GralWindow createWindow(String name, String title, int windProps)
  { GralPos pos = pos().pos;  //without clone.
    String sPos = pos.posString(); 
    GralWindow windowGral = new GralWindow(sPos, name, title, windProps);
    impl.createSubWindow(windowGral);
    return windowGral;
  }

  @Override public GralFileDialog_ifc createFileDialog()
  {
    // TODO Auto-generated method stub
    return impl.createFileDialog();
  }

  @Override public void redrawWidget(String sName)
  {
    // TODO Auto-generated method stub
    impl.redrawWidget(sName);
  }

  @Override public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
  {
    // TODO Auto-generated method stub
    impl.resizeWidget(widgd, xSizeParent, ySizeParent);
  }

  @Override public boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos)
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void setSampleCurveViewY(String sName, float[] values)
  {
    impl.setSampleCurveViewY(sName, values);
  }
	

	
}
