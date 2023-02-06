package org.vishia.gral.swt;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.gral.widget.GralLabel;
import org.vishia.gral.widget.GralCanvasArea;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;





/**This class manages the building of Panels with GUI-Elements for example for a Dialog Box
 * The container can be visibilized as a part of another container of any window.
 * An example is given in {@link org.vishia.mainGuiSwt.SampleGuiSwtButtonInput}
 * <br><br>
 * The positions on the display are based on grid units. 
 * The grid-size (number of x- and y-pixel per grid unit) is able to set, so a more large
 * or a more small representation of the graphic is shown, depending on the display point size,
 * the distance to view or personal defaults.
 * <br><br>
 * A layout manager like flow-layout is not used here. The user sets fix positions in the grid units. 
 * A grid unit is a half-small-line in the vertical direction and about a small character width 
 * in the x-direction. So a closed-to-user-thinking position is able to set for any component.
 * It is like rows or lines and columns. 
 * An automatically layout managing using self-deciding positioning of any components is not done. 
 * But a next added component may be positioned right from the previous or below the previous 
 * without manually calculation and setting of the position. It simplifies positioning. 
 * <br><br>
 * This class supports setting of values of shown components with symbolic access (String-name)
 * in runtime
 * and selecting of button-actions and data-container while creating with symbolic access
 * to the action-instance or the data-container. This property is used especially 
 * by the text-script-controlled built of a dialog widget using the {@link GralCfgBuilder},
 * but it is able to simplify the access to data and actions elsewhere too.
 * <br><br>
 * <br><br>
 * <br><br>
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 */
//GralMng.ImplAccess
public class SwtMng extends GralMng.ImplAccess // implements GralMngBuild_ifc, GralMng_ifc
//GuiShellMngIfc<Control>   
{
  //private static final long serialVersionUID = -2547814076794969689L;

	/**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
	 * Changes:
	 * <ul>
   * <li>2022-10-27 new {@link #getPixelPos(GralPos)} and {@link #getPixelPosInner(GralPos)}.
   * <li>2022-09-24 new {@link #swtMng(org.vishia.gral.base.GralWidget.ImplAccess)} 
   * <li>2022-09-04 {@link #storeGralPixBounds(org.vishia.gral.base.GralWidget.ImplAccess, Control)} general usable.
   * <li>2022-08 {@link #createImplWidget_Gthread(GralWidget)} enhanced.
   * <li>2016-09-02 Hartmut chg: Some {@link GralPanelContent#GralPanelContent(String, String, char)} and {@link GralTabbedPanel#GralTabbedPanel(String, String, GralPanelActivated_ifc, int)}
   *   was invoked with "@" for the posString without any more posString information. That is false. The idea was: Set the current panel. But that does not run. 
   *   See changes on {@link GralMng} on 2016-09-02. 
   * <li>2013-12-21 Hartmut new: {@link #createImplWidget_Gthread(GralWidget)} instanciates all widget types. 
	 * <li>2012-07-13 Hartmut chg: {@link #resizeWidget(GralWidget, int, int)} can work with more as one widget implementation.
	 *   But it isn't test and used yet. Size of any implementation widget?
	 * <li>2012-03-17 Hartmut chg: some changes for {@link #setPosAndSizeSwt(Control, int, int)} etc.
	 * <li>2012-01-26 Hartmut chg: prevent some error messages which are unnecessary.
	 * <li>2012-01-01 Hartmut new: The {@link #setInfoGthread(GralWidget, int, int, Object, Object)} routine
	 *   uses the {@link SwtSetValue_ifc} capability to associate cmd to types of widgets. Yet used only for {@link SwtSubWindow}.
	 * <li>2011-12-03 Hartmut new: {@link #setInfoGthread(GralWidget, int, int, Object, Object)} catches
	 *   any exception, before: An exception causes aborting the graphic thread.
	 * <li>2011-12-03 Hartmut chg: {@link #addLed(String, String, String)} now uses {@link GralLed}.  
	 * <li>2011-12-03 Hartmut new: {@link #swtKeyListener} as base for all fields.
	 * <li>2011-11-12 Hartmut chg: {@link #calcPositionOfWindow(GralPos)} improved
	 * <li>2011-08-13 Hartmut chg: New routines for store and calculate the position to regard large widgets.
	 * <li>2011-06-17 Hartmut getValueFromWidget(): Table returns the whole selected line, cells separated with tab.
	 *     The String-return.split("\t") separates the result to the cell values.
	 * <li>2011-05-08 Hartmut new; {@link GralMng_ifc#cmdClear} used to clear a whole swt.Table, commonly using: clear a content of widget.
   * <li>2010-12-02 Hartmut: in method insertInfo((): call of checkAdmissibility() for some input parameter, 
	 *     elsewhere exceptions may be possible on evaluating the inserted info in doBeforeDispatching().
	 *     There the causer isn't found quickly while debugging.
	 * <li>2010-12-02 Hartmut: Up to now this version variable, its description contains the version history.
	 * </ul>
   * <br><br> 
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
	 */
  //@SuppressWarnings("hiding")
  public final static String version = "2022-10-27";
  
  
  /**Gets the SwtMng instance for the given implementation Widget from the {@link GralWidget#gralMng}.
   * Note: It is a quest of effort, simplicity or universality where to use only one instance for the GralMng and SwtMng
   * (Singleton) or reference it. The reference is a small effort in data. 
   * The access via reference or from singleton is approximately the same (run time effort).
   * But using a singleton prohibits a system with different graphic appearances in one running JRE environment. 
   * This should be also possible in future. Consider that many applications can run in one JRE environment. 
   * It is really a restriction using a singleton. 
   * <br>
   * Hence a simple unified way should be use to access the GralMng and the SwtMng from the GralWidget.
   * This is this operation. 
   * <br>
   * In the past the singleton was favored, but all GralWidgets have the GralMng reference {@link GralWidget#gralMng}
   * So the singleton usage can be refactored. 
   * @param widgi The implementation access, anytime accessible from the SWT implementation level. 
   * @return The reference to the GralMng is found via {@link ImplAccess#gralMng()}, 
   *   then the {@link GralMng#_mngImpl} is anytime the SwtMng if the graphic is running. 
   *   It is unconditionally downcasted here from the universal {@link GralMng.ImplAccess} to the SwtMng. 
   */
  static SwtMng swtMng(GralWidget.ImplAccess widgi) {
    return (SwtMng) widgi.gralMng()._mngImpl;
  }
  
  
  /**Returns the Swt widget which implements the given GralWidget.
   * It calls {@link GralWidget_ifc#getImplWidget()} and then checks,
   * whether the Swt impl Widget is immediately an Control, 
   * or it istype of {@link SwtWidgetHelper}. 
   * In the last case {@link SwtWidgetHelper#widgetSwt} is returned.
   * <br>
   * New since 2022-09. The approach is, only the {@link GralWidget.ImplAccess#wdgimpl} should be used
   * to store the aggregation to the implementation widget, so simple as possible. 
   *  
   * @param widgg
   * @return null if a implementation is not available.
   */
  public static Control getSwtImpl ( GralWidgetBase_ifc widgg) {
    GralWidget.ImplAccess impl = widgg.getImplAccess();
    if(impl == null) return null;                          // not instantiated or has not a specific implementation widget.
    else  return (Control)impl.getWidgetImplementation();  // maybe null on comprehensive widgets
  }

  
  
  
  public static Composite getSwtParent ( GralPos pos) {
    return (Composite)getSwtImpl(pos.parent);
  }
  
  
  
	/**The GUI may be determined by a external user file. Not all planned fields, buttons etc. 
   * may be placed in the GUI, a user can desire about the elements. 
   * But an access to a non-existing element should be detected. This Exception should be caught
   * and an alternate behavior on non-existing elements may be programmed in the application. 
   */
  public class NotExistException extends Exception
  {
    private static final long serialVersionUID = 1L;

    NotExistException(String name)
    { super("GUI-Element doesn't exist in GUI: " + name);
    }
  }
  
  /**The graphical frame. It is any part of a window (a Composite widget) or the whole window. */
  //public final Composite graphicFrame;
  
  //public final Shell theShellOfWindow;
  
  protected Rectangle XXXXXcurrPanelPos;
  

  /**Properties of this Dialog Window. The {@link GralMng} contains an aggregation 
   * to the same instance, but with type {@link GralGridProperties}. Internally there are some more
   * Swt-capabilities in the derived type.
   */
  protected SwtProperties propertiesGuiSwt;
  
  
  protected Display displaySwt;
  
 
  
  //public final SwtWidgetHelper widgetHelper = new SwtWidgetHelper(this);
  
  /**This mouse-click-implementor is added to any widget,
   * which is associated to a {@link GralWidget} in its data.
   * The infos of the last clicked widget can be got with it.
   * @deprecated use {@link SwtGralMouseListener#mouseActionStd}, it is the same, but better structured.
   */
  @Deprecated MouseListener mouseClickForInfo = new SwtGralMouseListener.MouseListenerGralAction(); //NoAction();
  
  
  /**The instance for all traverse listener applicable to all widgets. */
  static SwtTraverseListener swtTraverseListener = new SwtTraverseListener();
  
  //SwtKeyListener swtKeyListener = new SwtKeyListener(this);
  
  /**It is a marker interface. */
  protected interface XXXUserAction{}
  
  
  /**Action for user commands of buttons. 
   * An instance of this class is able to assign as button-action.
   * The actionPerformed-method is implemented here, but the procedure calls a used-defined
   * action method which is implemented in the user-space implementing the
   *  {@link GralUserAction#userActionGui(String, String, Map)}-interface. 
   */
  protected class XXXButtonUserAction implements XXXUserAction, SelectionListener
  {

    /**Reference to the users method. */
    private final GralUserAction userCmdGui;
    
    /**Constructor.
     * @param userCmdGui The users method for the action. */
    private XXXButtonUserAction(GralUserAction userCmdGui)
    {
      this.userCmdGui = userCmdGui;
    }
    

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}

    /**Implementing of the primary action method,
     * implements @see org.eclipse.swt.events#widgetSelected(SelectionEvent)
     * All values of input fields are read out and filled in a list, to support locally access
     * to the values of the dialog.
     */
		@Override
		public void widgetSelected(SelectionEvent e) {
      /**prepare all inputs: */
      //String sButtonCmd = e.getActionCommand();
      //String sParamString = e.paramString();
			Object data = null;
			//String sParam = "";
			Widget src = e.widget;
			Object widgetData = src.getData();
			GralWidget infos = widgetData instanceof GralWidget ? (GralWidget)widgetData : null; 
			if(src instanceof Button){
				Button button = (Button)src;
				data = button.getData();
				if(data != null && data instanceof String){
	    		//sParam = (String)data;
	    	}
			}
			/*
    	Map<String,String> values;
			if(sParam.endsWith("+")){
				values = getAllValues(); 
			} else {
				values = null;
			}
			*/
      userCmdGui.userActionGui("button", infos);
      
		}
    
  }
  
  
  
	/**This class is only used to store values to inspect. The Inspector is a tool which works with
	 * reflection and with it internal variable can be visited in runtime. See {@link org.vishia.inspectorTarget.Inspector}.
	 */
	@SuppressWarnings("unused")
	private static class TestHelp{
  	private GralCurveView curveView;
  } 
	TestHelp testHelp = new TestHelp(); 
  

  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  protected SwtMng(GralMng gralMng
  , char displaySize//, VariableContainer_ifc variableContainer
	, LogMessage log) { 
    super(gralMng);
    super.sizeCharProperties = displaySize;
        //pos().x.p1 = 0; //start-position
    //pos().y.p1 = 4 * propertiesGui.yPixelUnit();

		
		//displaySwt.addFilter(SWT.KeyDown, mainKeyListener);
    
    startThread();

  }

  
  /**Converts a GralPos to pixel.
   * It calls (see also) {@link #calcWidgetPosAndSize(GralPos, int, int)}.
   * Note: The size of the panel is gotten from the current size. 
   * @param src
   * @return
   */
  Rectangle getPixelPos(GralPos src) {
    GralRectangle gralPix = calcWidgetPosAndSize(src, 0, 0);
    Rectangle rect = new Rectangle(gralPix.x, gralPix.y, gralPix.dx, gralPix.dy);
    return rect;
  }

  
  /**Gets a rectangle as 'inner' coordinates for filling a drawn rectangle
   * @param src
   * @return
   */
  Rectangle getPixelPosInner(GralPos src) {
    GralRectangle gralPix = calcWidgetPosAndSize(src, 0, 0);
    Rectangle rect = new Rectangle(gralPix.x+1, gralPix.y, gralPix.dx, gralPix.dy);
    return rect;
  }

  
  
  /**The composite of the panel in SWT.
   * @see org.vishia.gral.base.GralMng.ImplAccess#getCurrentPanel()
   */
  @Override public Composite getCurrentPanel(){ 
    throw new RuntimeException("mng can not know the current panel. Use getWidgetsPanel()");
    //return ((Composite)pos().parent.getImplAccess().getWidgetImplementation()); 
  }

  public Composite getWidgetsPanel(GralWidget widg){ 
    GralPos pos = widg.pos();
    if(pos == null) { pos = pos(); } //from GralMng
    return ((Composite)pos.parent.getImplAccess().getWidgetImplementation()); 
  }

  
  private Composite getCurrentPanel(GralWidget widgg){ return ((Composite)widgg.pos().parent.getImplAccess().getWidgetImplementation()); }
  
  
  
  
  /**This is the core operation to create all implementation widgets from given {@link GralWidget}.
   * It knows all types of widgets, selects it and calls the proper swt counterpart.
   * See definition on {@link GralMng#createImplWidget_Gthread(GralWidget)}
   * @param widgg the existing GralWidget derived type
   */
  @Override public void createImplWidget_Gthread(GralWidget widgg){
    final GralWidget.ImplAccess wdga;
    if(widgg instanceof GralHtmlBox) {  //NOTE: before GralTextField because a GralTextBox is a GralTextField (derived)
      wdga = SwtHtmlBox.createHtmlBox((GralHtmlBox)widgg, this);  //This may be the best variant.
    } else if(widgg instanceof GralTextBox) {  //NOTE: before GralTextField because a GralTextBox is a GralTextField (derived)
      wdga = SwtTextFieldWrapper.createTextBox((GralTextBox)widgg, gralMng);  //This may be the best variant.
    } else if(widgg instanceof GralTextField){
      wdga = SwtTextFieldWrapper.createTextField((GralTextField)widgg, gralMng);  //This may be the best variant.
    } else if(widgg instanceof GralHorizontalSelector<?>){
      wdga = new SwtHorizontalSelector(this, (GralHorizontalSelector<?>)widgg);
    } else if(widgg instanceof GralTable<?>){
      wdga = SwtTable.createTable((GralTable<?>)widgg, this);  //This may be the best variant.
    } else if(widgg instanceof GralButton){
      wdga = new SwtButton((GralButton)widgg, this);
//      gralMng.registerWidget(widgg);
    } 
    else if(widgg instanceof GralLabel){
      wdga = new SwtLabel((GralLabel)widgg, this);
    }
    else if(widgg instanceof GralValueBar){
      wdga = new SwtValueBar((GralValueBar)widgg, this);
    }
    else if(widgg instanceof GralLed){
      wdga = new SwtLed((GralLed)widgg, this);
//      gralMng.registerWidget(widgg);
    }
    else if(widgg instanceof GralCanvasArea){
      wdga = new SwtCanvasArea((GralCanvasArea)widgg, this);
    }
//    else if(widgg instanceof GralTabbedPanel) {            // GralTabbedPanel should be checked before GralPanelContent, its derived
//      GralTabbedPanel widgp = (GralTabbedPanel)widgg;
//      GralPanelActivated_ifc user = widgp.notifyingUserInstanceWhileSelectingTab;
//      new SwtTabbedPanel(widgp, this, user, 0);
//    }
    else if(widgg instanceof GralWindow) {
      wdga = new SwtSubWindow(this, (GralWindow)widgg);
    }
    else if(widgg instanceof GralArea9Panel) {
      wdga = new SwtPanelArea9((GralArea9Panel)widgg);
    }
    else if(widgg instanceof GralPanelContent) {
      GralPanelContent widgp = (GralPanelContent)widgg;
//      SwtPanel tab;
//      if(widgp.canvas() !=null) { tab = new SwtCanvasStorePanel(widgp); }
//      else 
      wdga = new SwtGridPanel(widgp, '$', 0);   // an SwtGridPanel is always also a SwtCanvasStrorePanel
      GralWidgetBase_ifc parent = widgg.pos().parent;
//      if(parent instanceof GralTabbedPanel) {
//        SwtTabbedPanel swtParent = (SwtTabbedPanel)parent.getImplAccess();
//        swtParent.addGridPanel(widgp, widgp.getName(), 2, 2, 10, 10);
//      }
    }
    else if(widgg instanceof GralCurveView) {
      GralCurveView widgp = (GralCurveView)widgg;
      wdga = new SwtCurveView(widgp, this); 
      
    }
    else {
      throw new IllegalArgumentException("missing Widget type: " + widgg.toString());
    }
    //-------------------------------- // It should be always possible to access GralWidget from the implementation Swt-Control
    Control widgSwt = (Control)wdga.getWidgetImplementation();
    (widgSwt).setData(widgg);
    boolean bSwtVisible = widgSwt.isVisible();
    if(bSwtVisible && ! widgg.isVisible()) {
      Debugutil.stop();
    }
  }
  

  

  
  
//  @Override public GralPanelContent createCompositeBox(String name)
//  {
//    //Composite box = new Composite(graphicFrame, 0);
//    Composite box = new Composite(getCurrentPanel(), 0);
//    setPosAndSize_(gralMng.getPosOldPositioning(), box);
//    Point size = box.getSize();
//    GralPanelContent panelg = new GralPanelContent(null, name);
//    GralPanelContent panel = (new SwtPanel(panelg, box)).gralPanel();
//    //mng.registerPanel(panel);
//    //GuiPanelMngSwt mng = new GuiPanelMngSwt(gralDevice, size.y, size.x, propertiesGuiSwt, variableContainer, log);
//    return panel;
//  }

  
//  @Override public GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG, int xS, int yS)
//  { GralPanelContent panelg = new GralPanelContent(null, namePanel);
//    panelg.setGrid(yG, xG, yS, xS, -8, -12);
//    Color backColorSwt = propertiesGuiSwt.colorSwt(backGround);
//    SwtGridPanel panel = new SwtGridPanel(panelg, 0);
//    GralPanelContent gralPanel = panel.gralPanel();
//    //mng.registerPanel(gralPanel);
//
//    return gralPanel;
//  }
  
  
  
  @Override public boolean remove(GralPanel_ifc compositeBox)
  { Composite panelSwt = ((SwtPanel)compositeBox.getImplAccess().getWidgetImplementation()).panelSwtImpl;
    panelSwt.dispose();
    return true;
  }
  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(java.lang.String, java.lang.String, int)
   */
  @Deprecated
  @Override public GralWindow createWindow(String name, String title, int windProps)
  {
    //GralWindow windowGral = new GralWindow("@", name, title, windProps);
    GralWindow windowGral = new GralWindow((GralPos)null, name, title, windProps);
    //SwtGraphicThread swtDevice = (SwtGraphicThread)gralDevice;
    try {
      createSubWindow(windowGral);
    } catch(Exception exc) {
      ExcUtil.exceptionInfo("unexpected", exc, 0, 10);
    }
    return windowGral;

  }
  
  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(org.vishia.gral.base.GralWindow)
   */
  @Deprecated @Override public void createSubWindow(GralWindow windowGral) throws IOException {
    SwtSubWindow windowSwt = new SwtSubWindow(this, windowGral);
    //GralRectangle rect = calcPositionOfWindow(windowGral.pos());
    //windowSwt.window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
//    windowGral._wdgImpl = windowSwt;
  }

  
  
  
  @Override public boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos)
  {return false;  }

  

  
  
  /**Calculates the position as absolute value on screen from a given position inside a panel.
   * @param posWindow contains any {@link GralPos#panel}. Its absolute position will be determined.
   *   from that position and size the absolute postion will be calculate, with this given grid positions
   *   inside the panel. 
   * @return Absolute pixel coordinate.
   */
  GralRectangle calcPositionOfWindow(GralPos posWindow)
  {
    final GralRectangle windowFrame;
    Control parentFrame;
    final Shell window;
    if(posWindow.parent !=null) {
      GralWidget.ImplAccess parentImpl = posWindow.parent.getImplAccess();
      if(parentImpl ==null) {                              // a primary window
        windowFrame = new GralRectangle(0,0,800,600);
        //?? windowFrame = getPixelUseableAreaOfWindow(posWindow.panel.getPanelWidget());
        parentFrame = null;
        window = null;
      } else {
        Object swtWidg = parentImpl.getWidgetImplementation();
        parentFrame = (Control)swtWidg; //((SwtPanel)(swtWidg)).panelComposite; //(Control)posWindow.panel.getPanelImpl();
        Point loc;
        window = parentFrame.getShell();
        int x = 6;
        windowFrame = getPixelUseableAreaOfWindow(((GralPanelContent)posWindow.parent).getPanelWidget());
      }
    } else {
      windowFrame = new GralRectangle(0,0,800,600);
      parentFrame = null;
      window = null;
    }
    int dxFrame, dyFrame;  //need if posWindow has coordinates from right or in percent
    Rectangle rectParent;
    if(parentFrame == window){
      dxFrame = windowFrame.dx; dyFrame = windowFrame.dy;
    } else {
      rectParent = parentFrame.getBounds();
      dxFrame = rectParent.width; dyFrame = rectParent.height;
    }
    final GralRectangle rectangle = gralMng.calcWidgetPosAndSize(posWindow, dxFrame, dyFrame, 400, 300);
    rectangle.x += windowFrame.x;
    rectangle.y += windowFrame.y;
    
    //
    while ( parentFrame != window){ //The Shell is the last parentFrame
      //the bounds are relative to its container. Get all parent container and add all positions
      //until the shell is reached.
      rectParent = parentFrame.getBounds();
      rectangle.x += rectParent.x;
      rectangle.y += rectParent.y;
      parentFrame = parentFrame.getParent();
    }
    return rectangle;
 
    /*
    Rectangle rectParent  = parentFrame instanceof Shell ? ((Shell)parentFrame).getClientArea() 
                          : parentFrame.getBounds();
    //loc = panel.getLocation();
    int xPos = rectParent.x, yPos = rectParent.y;
    while( (parentFrame = parentFrame.getParent()) !=null){
      rectParent = parentFrame.getBounds();
      loc = parentFrame.getLocation();
      xPos += rectParent.x;
      yPos += rectParent.y;
      if(parentFrame instanceof Shell){
        Shell shell = (Shell)parentFrame;
        Rectangle rectArea = shell.getClientArea(); //size of client area
        Menu menu = shell.getMenuBar();
        //Point sizeArea = shell.getSize();
        int dy = rectParent.height - rectArea.height; //The start of client area
        if(menu !=null){ dy *=2; } //Menu needs the same size as title.
        int dx = rectParent.width - rectArea.width;
        xPos += dx;
        yPos += dy;
      } else {
      }
      
    }
    rectangle.x += xPos;
    rectangle.y += yPos;
    return rectangle;
    */
  }
  
  
  /**Stores the Control#getBounds() in the {@link GralWidget.ImplAccess#pixBounds}.
   * This operation is called on 
   * @param widgg The appropriate GralWidget
   * @param widg The SWT widget which is presented with this Gral Widget.
   *   On comprehensive GralWidgets this routine is especially implemented. 
   */
  static void storeGralPixBounds(GralWidget.ImplAccess widgg, Control widg) {
    Rectangle bounds = widg.getBounds();
    widgg.pixBounds.x = bounds.x;
    widgg.pixBounds.y = bounds.y;
    widgg.pixBounds.dx = bounds.width;
    widgg.pixBounds.dy = bounds.height;
  }
  
  
  
  static void logBounds(StringBuilder sLog, Control wdg) {
    Rectangle xy = wdg.getBounds();
    sLog.append(" pixy(").append(xy.x).append(" + ").append(xy.width).append(", ").append(xy.y).append(" + ").append(xy.height).append(")");
  }
  
  GralRectangle getPixelUseableAreaOfWindow(GralWidget widgg)
  { Object oControl = widgg._wdgImpl.getWidgetImplementation();
    Control control = (Control)oControl;
    Shell window = control.getShell();
    Rectangle rectWindow = window.getBounds();
    Rectangle rectWindowArea = window.getClientArea();  //it is inclusive the menu bar.
    //Problem: the x and y of client are are 0, it may bettet that they are the left top corner
    //inside the shell window.
    //assume that the client area is on bottom of the shell. Calculate top position:
    int dxBorder = rectWindow.width - rectWindowArea.width;
    int xPos = rectWindow.x + dxBorder/2;
    int dyTitleMenu = (rectWindow.height - rectWindowArea.height) - dxBorder;  //border and title bar
    Menu menu = window.getMenuBar();
    if(menu !=null){
      //assume that the menu has the same hight as title bar, there is not a way to determine it else
      dyTitleMenu *=2;  
    }
    int yPos = rectWindow.y + dxBorder/2 + dyTitleMenu;
    GralRectangle ret = new GralRectangle(xPos, yPos, rectWindowArea.width, rectWindowArea.height - dyTitleMenu);
    return ret;
  }
  
  
  
  /**This method can be override by the user to force some actions if the dialog window is closed. It may be left empty. */
  protected void windowClosing()
  {
  	
  }
  
  
  /**Places a current component with knowledge of the current positions and the spreads of the component on graphic.
   * @param component The component to place.
   */
  GralRectangle setBounds_(GralPos pos, Control component)
  { return setPosAndSize_(pos, component);
    //setBounds_(component, 0,0, 0, 0);
  }
  
  
  
  
  GralRectangle setPosAndSize_(GralPos pos, Control component)
  { return setPosAndSizeSwt(pos, component, 0,0);
  }  
  
  
  
  
  
  
  /**Set bounds of a SWT component with this {@link GralMng#pos} from the GralWidgetMng. 
   * The {@link #setNextPosition()} is called to process a used this.pos to its next. 
   * This method is package-private for SWT-implementation. It calls 
   * {@link #setNextPosition()} and {@link #setPosAndSizeSwt(GralPos, Control, int, int)}
   * with 
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   *
   * NOTE: 2015-07-13: This method is set to unused because it uses the mng-position additional to the constructor of GralWidget.
   * This is the old concept which is in conflict with the usuage there.
   */
  void XXXsetPosAndSizeSwt(Control component, int widthwidgetNat, int heigthWidgetNat)
  { gralMng.setNextPosition();
    setPosAndSizeSwt(pos(), component, widthwidgetNat, heigthWidgetNat);
  }
  


  
  /**Set bounds of a SWT component with a given position.
   * This method is package-private for SWT-implementation.
   * @param posP The Position for the component.
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   */
  GralRectangle setPosAndSizeSwt(GralPos posP, Control component, int widthwidgetNat, int heigthWidgetNat)
  {
    GralRectangle rectangle = calcWidgetPosAndSizeSwt(posP, component.getParent(), widthwidgetNat, heigthWidgetNat);
    //on SWT it invokes the resize listener if given.
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
    return rectangle;   
  }
  


  
  
  /**Calculates the bounds of a widget with a given pos inside the implementation panel following {@link GralPos#parent}.
   * Note: The size of the panel is gotten from the current one (maybe resize has had occured). 
   * It means coordinates from right and bottom should be correct.
   * <br> 
   * This method is a part of the implementing GralMng because the GralPos is not implemented for
   * any underlying graphic system and the {@link #propertiesGuiSwt} are used.
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param widthwidgetNat The natural size of the component given if necessary.
   * @param heigthWidgetNat The natural size of the component given if necessary.
   * @return A rectangle with position and size.
   */
  @Override public GralRectangle calcWidgetPosAndSize(GralPos pos, int widthwidgetNat, int heigthWidgetNat){
    
    Object oParent = pos.parent.getImplAccess().getWidgetImplementation();
    Composite parentComp = (Composite) oParent; //((SwtPanel)(pos().panel.getWidgetImplementation())).panelComposite; //(Composite)pos().panel.getPanelImpl();
    //Rectangle pos;
    final GralRectangle rectangle;
    final Rectangle parentSize;
    if(parentComp == null){
      parentSize = new Rectangle(0,0,800, 600);
    } else if(parentComp instanceof Shell) {
      parentSize = ((Shell)parentComp).getClientArea();
    } else {
      parentSize = parentComp.getBounds();
    }
    return pos.calcWidgetPosAndSize(gralMng.gralProps, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
  }
  
  

  
  /**Calculates the bounds of a SWT component with a given position independent of {@link #pos}.
   * This method is package-private for SWT-implementation.
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   * @return A rectangle with position and size.
   * @deprecated, use {@link #calcWidgetPosAndSizeSwt(GralPos, int, int)}
   */
  GralRectangle calcWidgetPosAndSizeSwt(GralPos pos, Control parentComp, int widthwidgetNat, int heigthWidgetNat){
    //Rectangle pos;
    final GralRectangle rectangle;
    final Rectangle parentSize;
    if(parentComp == null){
      parentSize = new Rectangle(0,0,800, 600);
    } else if(parentComp instanceof Shell) {
      parentSize = ((Shell)parentComp).getClientArea();
    } else {
      parentSize = parentComp.getBounds();
    }
    return pos.calcWidgetPosAndSize(super.gralMng.gralProps, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
  }
  
  
  


  
	/**Adds a tab panel in implementation.
	 * If this routine was called, the GralMng.pos is the pos for the Tabbed panel.
	 * @see org.vishia.gral.base.GralMng.ImplAccess#addTabbedPanel(java.lang.String, org.vishia.gral.base.GralPanelActivated_ifc, int)
	 */
//	@Override public GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user, int property)
//	{ GralTabbedPanel panelg = new GralTabbedPanel(null, namePanel, user, property);
//		SwtTabbedPanel tabMngPanel = new SwtTabbedPanel(panelg, this, user, property);
//		gralMng.XXXcurrTabPanel = panelg;
//		//GralWidget tabFolder = currTabPanel;
//		TabFolder tabFolderSwt = (TabFolder)tabMngPanel.getWidgetImplementation();
//		setPosAndSize_(panelg.pos(), tabFolderSwt); //(Control)currTabPanel.getGuiComponent().getWidgetImplementation());
//		listVisiblePanels_add(gralMng.XXXcurrTabPanel);  //TODO checkit maybe currTabPanel.getCurrentPanel()
//		//mng.registerWidget(tabMngPanel);
//		return gralMng.XXXcurrTabPanel;
//	}
//	
  
  
  
  @Override @Deprecated public GralWidget addText(String sText, char size, int color)
  {
    Composite swtPanel = ((Composite)(pos().parent.getImplAccess().getWidgetImplementation()));
    Label widget = new Label(swtPanel, 0);
    widget.setForeground(propertiesGuiSwt.colorSwt(color));
    widget.setBackground(propertiesGuiSwt.colorBackground);
    widget.setText(sText);
    //Font font = propertiesGui.stdInputFont;
    Font font = propertiesGuiSwt.getSwtFont(pos().height());
    widget.setFont(font);
    //font.getFontData()[0].
    Point textSize = widget.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    //int width = sText.length();
    //widget.setSize(sizePixel);
    
    setPosAndSize_(gralMng.getPosOldPositioning(), widget);
    
    Point widgetSize = widget.getSize();
    if(widgetSize.x < textSize.x){
      widget.setSize(textSize);
    }
    widget.setSize(textSize);
    //guiContent.add(widget);
//    GralWidget widg = new GralWidget("labelText-" + sText, 'S', gralMng);
//    SwtWidgetSimpleWrapper widgswt = new SwtWidgetSimpleWrapper(widg, widget, this);
    //widg.implMethodWidget_.setWidgetImpl(widgswt);
    return null; //widg;
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
  public GralWidget XXXaddTextField(String name, boolean editable, String prompt, char promptStylePosition)
  {
    return null; //addTextField(null, name, editable, prompt, promptStylePosition);
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
  public GralWidget XXXaddTextField(GralWidget widgetInfo, boolean editable, String prompt, char promptStylePosition)
  {
    return null; //addTextField(widgetInfo, null, editable, prompt, promptStylePosition);
  }
  
  
@Override public GralHtmlBox addHtmlBox(String name) {
  GralHtmlBox box = new GralHtmlBox(null, name);
  new SwtHtmlBox(box, this);
  return box;
}


  
  
  /* (non-Javadoc)
   * @see org.vishia.mainGui.GuiPanelMngIfc#addImage(java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override 
  public Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  {
    ImageData imageData = new ImageData(imageStream);
    byte[] data = imageData.data;
    SwtPanel swtPanel = (SwtPanel)pos().parent.getImplAccess();
    Composite swtWidg = swtPanel.panelSwtImpl;
    Image image = new Image(swtWidg.getDisplay(), imageData); 
    GralImageBase imageGui = new SwtImage(image);
    GralRectangle size = imageGui.getPixelSize();
    GralRectangle rr = gralMng.calcWidgetPosAndSize(pos(), 0, 0, size.dx, size.dy);
    GralCanvasStorage canvas = ((GralPanelContent)pos().parent).canvas();  
    if(canvas !=null){
      canvas.drawImage(imageGui, rr.x, rr.y, rr.dx, rr.dy, size);
    }
    return null;
  }

  

  
  @Override public GralWidget addSlider(
  	String sName
  , GralUserAction action
  , String sShowMethod
  , String sDataPath
  )
  {
  	Slider control = new Slider(getCurrentPanel(), SWT.VERTICAL);
  	control.setBackground(propertiesGuiSwt.colorBackground);
  	setPosAndSize_(gralMng.getPosOldPositioning(), control);
//    GralWidget widg = new GralWidget(sName, 'V', gralMng);
//    SwtWidgetSimpleWrapper widgswt = new SwtWidgetSimpleWrapper(widg, control, this);
//    //widg.implMethodWidget_.setWidgetImpl(widgswt);
//    if(action != null){
//  		SelectionListenerForSlider actionSlider = new SelectionListenerForSlider(widg, action);
//  		control.addSelectionListener(actionSlider);
//  	}
//    widg.setDataPath(sDataPath);
//    control.setData(widg);
//    control.addMouseListener(mouseClickForInfo);
    return null; //widg;
  	
  }
  
  
  
	@Override public GralCurveView addCurveViewY(String sName, int nrofXvalues, GralCurveView.CommonCurve common) {
    //setNextPosition();
	  GralCurveView widgg = new GralCurveView(sName, nrofXvalues, common);
	  new SwtCurveView(widgg, this); //sName, this.pos(), this, nrofXvalues, common); //, curveView, 'c', sName, null);
		gralMng.curveContainer.add(widgg);
	  //CurveView curveView = new CurveView(((SwtPanel)pos().panel).getPanelImpl(), dxWidget, dyWidget, nrofXvalues, nrofTracks);
		testHelp.curveView = widgg; //store to inspect.
		return widgg;
	}


	
	@Override public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    GralWidget widget = indexNameWidgets(sName);
  	if(widget == null || widget._wdgImpl ==null || !(widget._wdgImpl.getWidgetImplementation() instanceof Control)){
  		gralMng.log.sendMsg(0, "GuiMainDialog:addClickAction: unknown widget %s", sName);
  	} else {
    	/**The class ButtonUserAction implements the general button action, which class the registered user action. */
      ((Control)(widget._wdgImpl.getWidgetImplementation())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
      
  	}
  	return widget;
	}

	
	@Override public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    ((Control)(widgetInfo._wdgImpl.getWidgetImplementation())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
  }

	
  @Override @Deprecated public GralTable addTable(String sName, int height, int[] columnWidths)
  {
    GralTable table = new GralTable(null, sName, 20, columnWidths);
    return SwtTable.addTable(table, this, sName, height, columnWidths);

  }
  
  
  @Override protected GralMenu XXXaddPopupMenu(String sName){
    Control panelSwt = getCurrentPanel(); //(Control)pos().panel.getPanelImpl();
    GralMenu menu = new GralMenu(null); new SwtMenu(menu, null, panelSwt);
    return menu;
  }
  
  
  @Override protected GralMenu createContextMenu(GralWidget widg){
    Control widgSwt = (Control)widg._wdgImpl.getWidgetImplementation();
    GralMenu contextMenu = widg.getContextMenu();
    new SwtMenu(contextMenu, widg, widgSwt);
    return contextMenu;
  }
 
  
  @Override protected GralMenu createMenuBar(GralWindow windg){
    Shell windowSwt = (Shell)windg._wdgImpl.getWidgetImplementation();
    GralMenu menu = new GralMenu(windg); new SwtMenu(menu, windg, windowSwt);
    return menu;
  }
 
  
  
  
  @Override public GralFileDialog_ifc createFileDialog()
  {
    Composite panelSwt = getCurrentPanel(); //(Composite)pos().panel.getPanelImpl(); //cast admissible, it should be SWT
    while(!(panelSwt instanceof Shell)){
      panelSwt = panelSwt.getParent();
    }
  	return new SwtFileDialog((Shell)panelSwt);
  }

  
  
  static class SelectionListenerForSlider implements SelectionListener
  {
  	private final GralUserAction userAction; 

  	private final GralWidget widgetInfo;
  	
  	
  	
  	public SelectionListenerForSlider(GralWidget widgetInfo, GralUserAction userAction)
		{
			this.userAction = userAction;
  		this.widgetInfo = widgetInfo;
		}

		@Override	public void widgetDefaultSelected(SelectionEvent e)
		{
			widgetSelected(e);
			
		}

		@Override	public void widgetSelected(SelectionEvent e)
		{
			// TODO Auto-generated method stub
			//String sDataPath = widgetInfo.sDataPath;
			Slider slider = (Slider)e.widget;
			int value = slider.getSelection();
			//String sParam = Integer.toString(value);
			userAction.userActionGui("sliderValue", widgetInfo, value);
		}
  	
  }
  

	@Override
	public void setSampleCurveViewY(String sName, float[] values) {
		GralWidget descr = indexNameWidgets(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	}
  	
	}

	
	@Override public void redrawWidget(String sName)
	{
		GralWidget descr = indexNameWidgets(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	} else {
  	}
	}

	
	@Override public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
	{
	  //GralWidget_ifc widget = widgd.getGraphicWidgetWrapper();
	  if(widgd._wdgImpl !=null) {
	    widgd._wdgImpl.setPosBounds();
//  	  Object owidg = widgd._wdgImpl.getWidgetImplementation();
//  	  
//  	  int test = 6;
//  	  if(owidg !=null){
//  	    Control swtWidget = (Control)owidg;
//  	    GralWidget_ifc panel = widgd.pos().parent;
//  	    GralRectangle size = panel.getImplAccess().getPixelPositionSize(); //PixelSize();
//  	    GralRectangle posSize = gralMng.calcWidgetPosAndSize(widgd.pos(), size.dx, size.dy, 0, 0);
//    	  //Note: the swtWidget may have a resizeListener, see there.
//  	    swtWidget.setBounds(posSize.x, posSize.y, posSize.dx, posSize.dy );
//    	  swtWidget.redraw();
//  	  }
	  }
	}
	
	
  
	
	
	@Override @Deprecated public String getValueFromWidget(GralWidget widgd)
	{ String sValue;
  	sValue = gralMng.getValueFromWidget(widgd);  //platform independent getting of value
  	if(sValue == null){
  	  GralWidget_ifc widget = widgd;
      Control swtWidget = (Control)widgd._wdgImpl.getWidgetImplementation();
  		if(swtWidget instanceof Text){
    	  sValue = ((Text)swtWidget).getText();
    	} else if(widgd instanceof GralButton){
    		GralButton button = (GralButton)widgd;
    		sValue = button.isOn() ? "1" : "0"; 
    	} else if(swtWidget instanceof Button){
    		sValue = "0"; //TODO input.button.isSelected() ? "1" : "0";
    	} else if(swtWidget instanceof Table){
        Table table = (Table)swtWidget;
        sValue = getValueFromTable(table);
    	} else {
    	  gralMng.log.sendMsg(0, "GuiPanelMngSwt.getValueFromWidget - unknown widget type;");
    		sValue = "";
    	}
  	}
		return sValue;
	}
	
	
	private String getValueFromTable(Table table)
	{ StringBuilder u = new StringBuilder();
    int actLine = table.getSelectionIndex();
    TableItem item = table.getItem(actLine);
    int nrofColumns = table.getColumnCount();
    for(int iCol = 0; iCol < nrofColumns; ++iCol){
      u.append(item.getText(iCol)).append('\t');
    }
	  return u.toString();
	}
	
	
	
	
	
	@Override public Color getColorImpl(GralColor colorGral)
	{ return propertiesGuiSwt.colorSwt(colorGral);
	}



  @Override public boolean showContextMenuGthread(GralWidget widg) {
    boolean bOk;
    Control swtWidg = (Control)widg._wdgImpl.getWidgetImplementation();
    Menu contextMenu = swtWidg.getMenu();
    if(contextMenu == null){
      bOk = false;
    } else {
      //Rectangle pos = swtWidg.getBounds();
      GralRectangle pos = SwtWidgetHelper.getPixelPositionSize(swtWidg);
      contextMenu.setLocation(pos.x + pos.dx, pos.y + pos.dy);
      contextMenu.setVisible(true);
      bOk = true;
    }
    return bOk;
  }
  
  
  /**Instance of windowsCloseHandler. */
  private final WindowsCloseListener windowsCloseListener = new WindowsCloseListener(); 
  
  
  
  KeyListener keyListener = new KeyListener()
  {
    @Override public void keyPressed(KeyEvent key)
    {
      // TODO Auto-generated method stub
      stop();
    }

    @Override public void keyReleased(KeyEvent e)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  /**Disables the ctrl-pgUp and ctrl-Pgdn as traversal key listener. It should be able to use
   * by the application. Only Tab and sh-Tab are usual. */
  TraverseListener XXXkeyTraverse = new TraverseListener(){
    @Override public void keyTraversed(TraverseEvent e) {
      stop();
      if(  e.detail == SWT.TRAVERSE_PAGE_NEXT //|| e.keyCode == SWT.PAGE_DOWN){
        || e.detail == SWT.TRAVERSE_PAGE_PREVIOUS
         ) {
        e.doit = true;
  } } };
  
  
  /**This interface routine is invoked on any key which is used as 'traverse' key to switch
   * between widgets, panels etc. SWT uses the ctrl-pgup and ctrl-pgdn to switch between the
   * tab cards on a TabbedPanel. This is not a standard behavior for all graphic systems.
   * That keys should be able to use in the application. Therefore they are disabled as traversal keys.
   * To switch between the tabs - it may be application specific to do it with keys - or the mouse
   * can be used. 
   * 
   */
  Listener traverseKeyFilter = new Listener(){
    @Override public void handleEvent(Event event) {
      int traverseIdent = event.detail; 
      int key = event.keyCode;
      int keyModifier = event.stateMask;
      if(  traverseIdent == SWT.TRAVERSE_PAGE_NEXT         //the pg-dn key in SWT
        || traverseIdent == SWT.TRAVERSE_PAGE_PREVIOUS   //the pg-up key in SWT
        || key == '\t' && keyModifier == SWT.CTRL
        ) {
        event.doit = false;
      }
      stop();
    }
    
  };
  
  
  ShellListener mainComponentListerner = new ShellListener()
  {

        
    @Override
    public void shellActivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellClosed(ShellEvent e) {
      Debugutil.stop();
      
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeactivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeiconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellIconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  
  //final String sTitle; 
  //final int xPos, yPos, xSize, ySize;
  
  
  
  
  
  /**Called as first operation in the Graphic thread see {@link GralMng#runGraphicThread()}
   */
  @Override protected void initGraphic(){
    this.displaySwt = new Display();
    this.propertiesGuiSwt = new SwtProperties(this.displaySwt, super.gralMng.gralProps);
    this.gralMng.setProperties(this.propertiesGuiSwt);
    this.displaySwt.addFilter(SWT.Close, this.windowsCloseListener);  //it sets bExit on close of windows for the graphic thread
    this.displaySwt.addFilter(SWT.Traverse, this.traverseKeyFilter);
    //SwtProperties propertiesGui = new SwtProperties(this.displaySwt, this.sizeCharProperties);
    //this.swtMng = new SwtMng(super.gralMng, this.displaySwt, propertiesGui, this.log);  //sets the aggregation GralMng
    
  }

  /**Called as last operation in the Graphic thread see {@link GralMng#runGraphicThread()}
   */
  @Override protected void closeImplGraphic(){
    this.displaySwt.dispose();
  }
  
  @Override public void finishInit() {
    Debugutil.stop();
  }
  
  
  @Override public void reportContent(Appendable out) throws IOException {
    for(Map.Entry<String,GralWindow> ewind: this.idxWindows().entrySet()) {
      GralWindow wind = ewind.getValue();
      
      out.append("\n==== SwtMng.reportContent: Window: ").append(wind.getName()).append("\n");
      reportContent(out, (Composite)getSwtImpl(wind), "wind: ", 0);
      out.append("\n====\n");
    }
  }
  
  static final String nl = "\n| | | | | | | | ";
  
  
  void reportContent(Appendable out, Control parent, String mark, int recursion) throws IOException {
    Rectangle pos = parent.getBounds();
    String sType = parent.getClass().getName();
    out.append(nl.substring(0, 2*recursion+1));
    boolean bVisible = parent.isVisible();
    if(bVisible) { out.append("+*-"); }
    else { out.append("+--"); }
    out.append(mark);
    out.append(sType).append(':');
    GralRectangle.toString(out, pos.x, pos.y, pos.width, pos.height);
    out.append(parent.toString());
    Object data = parent.getData();
    if(data instanceof GralWidget) {
      String sClass = data.getClass().getName();
      out.append(" ").append(sClass).append(": ");
      ((GralWidget)data).toString(out);
    } else {
      out.append("data = ").append(data == null ? "null" : data.toString());
    }
    if(parent instanceof TabFolder) {
      TabItem[] items = ((TabFolder)parent).getItems();
      for(TabItem item: items) {
        Control itemWdg = item.getControl();
        reportContent(out, itemWdg, "tab: ", recursion +1);
      }
    }
    if(parent instanceof Composite && !(data instanceof GralTable)) {
      Control[] children = ((Composite)parent).getChildren();
      for(Control child : children) {
        if(child !=null) {
          reportContent(out, child, "", recursion +1);
        }
      } }
  }
  
  
  
  
  
  

  
  @Override
  protected boolean dispatchOsEvents()
  { return this.displaySwt.readAndDispatch();
  }

  @Override
  protected void graphicThreadSleep()
  {
    displaySwt.sleep ();
  }
  
  
  /**Yet Experience: SWT knows the {@link Display#asyncExec(Runnable)}.
   * @param exec
   */
  protected void addToGraphicImplThread(Runnable exec){
    displaySwt.asyncExec(exec);
  }
  
  @Override
  public void wakeup(){
    if(displaySwt == null){
      
    }
    displaySwt.wake();
    //extEventSet.set(true);
    //isWakedUpOnly = true;
  }


	
	
  /**This routine is invoked on any key event.
   * It is possible to change keys, to disable the event handling and to call special routines.
   * Yet not used.
   */
  Listener XXXX_mainKeyListener = new Listener(){
    @Override public void handleEvent(Event event) {
      // TODO Auto-generated method stub
      if(userMainKeyAction() !=null 
          && (event.keyCode & 0xffff) !=0  //don't take anything on alt- etc. alone
        ){
        final int keyCode = SwtGralKey.convertFromSwt(event.keyCode, event.stateMask, event.character);
        boolean bDone = userMainKeyAction().userActionGui(keyCode, null);
        if(bDone){
          event.type = SWT.None;
          //event.doit = false;   //don't use this key for another action.
        }
      }
      stop();
    }
  };
  
  

	
	
	
  
  /**Universal focus listener to register which widgets were in focus in its order,
   * to set htmlHelp and to invoke the {@link GralWidget#setActionFocused(GralUserAction)}.
   */
  protected class SwtMngFocusListener implements FocusListener
  {
    GralMng.GralMngFocusListener gralFocus;
    
    SwtMngFocusListener(GralMng mng){
      gralFocus = mng.new GralMngFocusListener();
    }
    
    @Override public void focusLost(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(ev.widget.getData());
      if(widgg !=null) { gralFocus.focusLostGral(widgg); }
    }
    
    @Override public void focusGained(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(ev.widget.getData());
      if(widgg !=null) { gralFocus.focusGainedGral(widgg); }
    }
  }
  
  /**The package private universal focus listener. */
  protected SwtMngFocusListener focusListener = new SwtMngFocusListener(gralMng);


  /**Universal context menu listener
   */
  protected class SwtMngMouseMenuListener implements MenuDetectListener
  {

    @Override
    public void menuDetected(MenuDetectEvent e)
    {
      // TODO Auto-generated method stub
      
    }
  }
  
  /**The package private universal focus listener. */
  SwtMngMouseMenuListener mouseMenuListener = new SwtMngMouseMenuListener();

  
  /**The windows-closing event handler. It is used private only, but public set because documentation. 
   * The close event will be fired also when a SubWindow is closed. Therefore test the Shell instance.
   * Only if the main window is closed, bExit should be set to true.
   * */
  public final class WindowsCloseListener implements Listener{
    /**Invoked when the window is closed; it sets {@link #bShouldExitImplGraphic}, able to get with {@link #isRunning()}.
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override public void handleEvent(Event event) {
      if( event.widget == getSwtImpl(SwtMng.this.gralMng.getPrimaryWindow())) {
        //see GralMng#actionClose and GralMng.closeApplication(), shellListeners
        //SwtMng.this.gralMng.closeApplication(); //close if the main window was closed.
      }
    }
  }



	void stop(){}  //debug helper


}
