package org.vishia.gral.swt;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralHorizontalSelector;
import org.vishia.gral.widget.GralLabel;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;





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
public class SwtMng extends GralMng implements GralMngBuild_ifc, GralMng_ifc
//GuiShellMngIfc<Control>   
{
  private static final long serialVersionUID = -2547814076794969689L;

	/**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
	 * Changes:
	 * <ul>
   * <li>2013-12-21 Hartmut new: {@link #setToPanel(GralWidget)} instanciates all widget types. 
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
  @SuppressWarnings("hiding")
  public final static int version = 20120317;

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
  
  protected Rectangle currPanelPos;
  

  /**Properties of this Dialog Window. The {@link GralMng} contains an aggregation 
   * to the same instance, but with type {@link GralGridProperties}. Internally there are some more
   * Swt-capabilities in the derived type.
   */
  public  final SwtProperties propertiesGuiSwt;
  
  
  final Display displaySwt;
  
  //public final SwtWidgetHelper widgetHelper = new SwtWidgetHelper(this);
  
  /**This mouse-click-implementor is added to any widget,
   * which is associated to a {@link GralWidget} in its data.
   * The infos of the last clicked widget can be got with it.
   */
  SwtGralMouseListener.MouseListenerNoAction mouseClickForInfo = new SwtGralMouseListener.MouseListenerNoAction();
  
  
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
			@SuppressWarnings("unchecked")
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
	 * reflection and with it internal variable can be visited in runtime. See {@link org.vishia.inspector.Inspector}.
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
  protected SwtMng(GralGraphicThread graldevice, Display display /*, Composite graphicFrame */
  , char displaySize//, VariableContainer_ifc variableContainer
	, LogMessage log)
  { //super(sTitle); 
  	this(graldevice, display, new SwtProperties(display, displaySize), log);
  	
  }

  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  public SwtMng(GralGraphicThread device, Display display 
    , SwtProperties propertiesGui
  	//, VariableContainer_ifc variableContainer
  	, LogMessage log
  	)
  { super(device, propertiesGui, log);
    this.propertiesGuiSwt = propertiesGui;
    pos.x.p1 = 0; //start-position
    pos.y.p1 = 4 * propertiesGui.yPixelUnit();

		
		displaySwt = display;
		//displaySwt.addFilter(SWT.KeyDown, mainKeyListener);
    

  }

  
  
  @Override public Composite getCurrentPanel(){ return (Composite)pos.panel.getPanelImpl(); }

  
  @Override public void setToPanel(GralWidget widgg){
    if(widgg instanceof GralTextBox) {  //NOTE: before GralTextField because a GralTextBox is a GralTextField (derived)
      SwtTextBox.createTextBox((GralTextBox)widgg, this);  //This may be the best variant.
    } else if(widgg instanceof GralTextField){
      SwtTextFieldWrapper.createTextField((GralTextField)widgg, this);  //This may be the best variant.
    } else if(widgg instanceof GralHorizontalSelector<?>){
      SwtHorizontalSelector swtSel = new SwtHorizontalSelector(this, (GralHorizontalSelector<?>)widgg);
      registerWidget(widgg);
    } else if(widgg instanceof GralTable<?>){
      SwtTable.createTable((GralTable<?>)widgg, this);  //This may be the best variant.
    } else if(widgg instanceof GralWindow){
      createWindow((GralWindow)widgg);
    } else if(widgg instanceof GralButton){
      new SwtButton((GralButton)widgg, this);
    } 
    else if(widgg instanceof GralLabel){
      new SwtLabel((GralLabel)widgg, this);
    }
    else if(widgg instanceof GralValueBar){
      new SwtValueBar((GralValueBar)widgg, this);
    }
  }
  

  

  
  
  @Override public GralPanelContent createCompositeBox(String name)
  {
    //Composite box = new Composite(graphicFrame, 0);
    Composite box = new Composite((Composite)pos.panel.getPanelImpl(), 0);
    setPosAndSize_(box);
    Point size = box.getSize();
    GralPanelContent panel = new SwtPanel(name, this, box);
    registerPanel(panel);
    //GuiPanelMngSwt mng = new GuiPanelMngSwt(gralDevice, size.y, size.x, propertiesGuiSwt, variableContainer, log);
    return panel;
  }

  
  @Override public GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG, int xS, int yS)
  {
    Color backColorSwt = propertiesGuiSwt.colorSwt(backGround);
    Composite panelSwt = (Composite)pos.panel.getPanelImpl();  
    SwtGridPanel panel = new SwtGridPanel(namePanel, panelSwt, 0, backColorSwt, xG, yG, xS, yS, this);
    registerPanel(panel);

    return panel;
  }
  
  
  
  @Override public boolean remove(GralPanelContent compositeBox)
  { Composite panelSwt = ((SwtPanel)compositeBox).getPanelImpl();
    panelSwt.dispose();
    return true;
  }
  
  @Override public boolean remove(GralWidget widget)
  { widget.remove();  //remove instance by Garbage collector.
    return true;
    
  }

  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(java.lang.String, java.lang.String, int)
   */
  @Deprecated
  @Override public GralWindow createWindow(String name, String title, int windProps)
  {
    GralWindow windowGral = new GralWindow(name, title, windProps, null, null);
    //SwtGraphicThread swtDevice = (SwtGraphicThread)gralDevice;
    createWindow(windowGral);
    return windowGral;

  }
  
  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralMngBuild_ifc#createWindow(org.vishia.gral.base.GralWindow)
   */
  @Override public void createWindow(GralWindow windowGral){
    SwtSubWindow windowSwt = new SwtSubWindow(this, windowGral);
    //new SwtSubWindow(name, swtDevice.displaySwt, title, windProps, this);
    GralRectangle rect = calcPositionOfWindow(windowGral.pos());
    windowSwt.window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
    //window.window.redraw();
    //window.window.update();
    this.registerPanel(windowGral);
    this.pos.panel = windowGral; //it is selected.
    this.pos.setPosition(null, 0,0,0,0,0,'r');  //per default the whole window as position and size.
    windowGral.wdgImpl = windowSwt;
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
    Control parentFrame = (Control)posWindow.panel.getPanelImpl();
    Point loc;
    Shell window = parentFrame.getShell();
    int x = 6;
    GralRectangle windowFrame = getPixelUseableAreaOfWindow(posWindow.panel);
    int dxFrame, dyFrame;  //need if posWindow has coordinates from right or in percent
    Rectangle rectParent;
    if(parentFrame == window){
      dxFrame = windowFrame.dx; dyFrame = windowFrame.dy;
    } else {
      rectParent = parentFrame.getBounds();
      dxFrame = rectParent.width; dyFrame = rectParent.height;
    }
    final GralRectangle rectangle = calcWidgetPosAndSize(posWindow, dxFrame, dyFrame, 400, 300);
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
  
  
  
  GralRectangle getPixelUseableAreaOfWindow(GralWidget widgg)
  { Object oControl = widgg.getWidgetImplementation();
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
  void setBounds_(Control component)
  { setPosAndSize_(component);
    //setBounds_(component, 0,0, 0, 0);
  }
  
  
  
  
  void setPosAndSize_(Control component)
  { setPosAndSizeSwt(component, 0,0);
  }  
  
  
  
  
  
  
  /**Set bounds of a SWT component with this {@link GralMng#pos} from the GralWidgetMng. 
   * The {@link #setNextPosition()} is called to process a used this.pos to its next. 
   * This method is package-private for SWT-implementation. It calls 
   * {@link #setNextPosition()} and {@link #setPosAndSizeSwt(GralPos, Control, int, int)}
   * with 
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   */
  void setPosAndSizeSwt(Control component, int widthwidgetNat, int heigthWidgetNat)
  { setNextPosition();
    setPosAndSizeSwt(this.pos, component, widthwidgetNat, heigthWidgetNat);
  }
  


  
  /**Set bounds of a SWT component with a given position independent of this {@link #pos}.
   * This routine is proper to use if a GralPos is calculated in any special kind, 
   * usual with this.pos as reference. 
   * This method is package-private for SWT-implementation.
   * @param posP The Position for the component.
   */
  void setPosAndSizeSwt(GralPos posP, Control component, int widthwidgetNat, int heigthWidgetNat)
  {
    GralRectangle rectangle = calcWidgetPosAndSizeSwt(posP, component, widthwidgetNat, heigthWidgetNat);
    //on SWT it invokes the resize listener if given.
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
       
  }
  


  
  
  /**Calculates the bounds of a widget with a given pos independent of this {@link #pos}.
   * This method is a part of the implementing GralMng because the GralPos is not implemented for
   * any underlying graphic system and the {@link #propertiesGuiSwt} are used.
   * It is possible to tune the bounds after calculation, for example to enhance the width if a text
   * is larger then the intended position. 
   * @param pos The position.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   * @return A rectangle with position and size.
   */
  @Override public GralRectangle calcWidgetPosAndSize(GralPos pos, int widthwidgetNat, int heigthWidgetNat){
    Composite parentComp = (Composite)pos.panel.getPanelImpl();
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
    return pos.calcWidgetPosAndSize(propertiesGui, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
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
  GralRectangle calcWidgetPosAndSizeSwt(GralPos pos, Control component, int widthwidgetNat, int heigthWidgetNat){
    Control parentComp = component.getParent();
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
    return calcWidgetPosAndSize(pos, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
  }
  
  
  


  
	@Override public GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user, int property)
	{
		SwtTabbedPanel tabMngPanel = new SwtTabbedPanel(namePanel, this, user, property);
		currTabPanel = tabMngPanel;
		//GralWidget tabFolder = currTabPanel;
		TabFolder tabFolderSwt = (TabFolder)tabMngPanel.getWidgetImplementation();
		setPosAndSize_(tabFolderSwt); //(Control)currTabPanel.getGuiComponent().getWidgetImplementation());
		listVisiblePanels.add(currTabPanel);  //TODO checkit maybe currTabPanel.getCurrentPanel()
		registerWidget(tabMngPanel);
		return currTabPanel;
	}
	
  
  
  
  @Override @Deprecated public GralWidget addText(String sText, char size, int color)
  {
    Label widget = new Label(((SwtPanel)pos.panel).getPanelImpl(), 0);
    widget.setForeground(propertiesGuiSwt.colorSwt(color));
    widget.setBackground(propertiesGuiSwt.colorBackground);
    widget.setText(sText);
    //Font font = propertiesGui.stdInputFont;
    Font font = propertiesGuiSwt.getSwtFont(pos.height());
    widget.setFont(font);
    //font.getFontData()[0].
    Point textSize = widget.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    //int width = sText.length();
    //widget.setSize(sizePixel);
    
    setPosAndSize_(widget);
    
    Point widgetSize = widget.getSize();
    if(widgetSize.x < textSize.x){
      widget.setSize(textSize);
    }
    widget.setSize(textSize);
    //guiContent.add(widget);
    GralWidget widg = new GralWidget("labelText-" + sText, 'S', this);
    SwtWidgetSimpleWrapper widgswt = new SwtWidgetSimpleWrapper(widget, this);
    //widg.implMethodWidget_.setWidgetImpl(widgswt);
    return widg;
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
    setToPanel(widgg);
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
  setToPanel(widgg);
  //SwtTextFieldWrapper.createTextField(widgg, this);   
  return widgg;

}

  
@Override public GralHtmlBox addHtmlBox(String name){
  return new SwtHtmlBox(name, this);
}

  
  /* (non-Javadoc)
   * @see org.vishia.mainGui.GuiPanelMngIfc#addImage(java.lang.String, int, int, java.lang.String, java.lang.String, java.lang.String)
   */
  @Override 
  public Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  {
    ImageData imageData = new ImageData(imageStream);
    byte[] data = imageData.data;
    Image image = new Image((((SwtPanel)pos.panel).getPanelImpl()).getDisplay(), imageData); 
    GralImageBase imageGui = new SwtImage(image);
    GralRectangle size = imageGui.getPixelSize();
    GralRectangle rr = calcWidgetPosAndSize(pos, 0, 0, size.dx, size.dy);
    if(pos.panel instanceof SwtCanvasStorePanel){
      SwtCanvasStorePanel canvas = (SwtCanvasStorePanel) pos.panel;
      //coordinates are in pixel
      canvas.canvas.drawImage(imageGui, rr.x, rr.y, rr.dx, rr.dy, size);
    }
    return null;
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
  
  
  @Override public GralWidget addSlider(
  	String sName
  , GralUserAction action
  , String sShowMethod
  , String sDataPath
  )
  {
  	Slider control = new Slider(((SwtPanel)pos.panel).getPanelImpl(), SWT.VERTICAL);
  	control.setBackground(propertiesGuiSwt.colorBackground);
  	setPosAndSize_(control);
    GralWidget widg = new GralWidget(sName, 'V', this);
    SwtWidgetSimpleWrapper widgswt = new SwtWidgetSimpleWrapper(control, this);
    //widg.implMethodWidget_.setWidgetImpl(widgswt);
    widg.setPanelMng(this);
    if(action != null){
  		SelectionListenerForSlider actionSlider = new SelectionListenerForSlider(widg, action);
  		control.addSelectionListener(actionSlider);
  	}
    widg.setDataPath(sDataPath);
    control.setData(widg);
    control.addMouseListener(mouseClickForInfo);
    return widg;
  	
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
    float ySize = pos.height();
    int xSize = (int)pos.width();
    
    char size = ySize > 3? 'B' : 'A';
    if(sName == null){ sName = sButtonText; }
    GralButton widgButton = new GralButton(sName);
    //SwtButton widgButton = new SwtButton(sName, this, (Composite)pos.panel.getPanelImpl(), 0, size);
    if(action !=null)
      stop();
    widgButton.setActionChange(action);  //maybe null
  	widgButton.setText(sButtonText);
    //in ctor: widgButton.setPanelMng(this);
    widgButton.sCmd = sCmd;
    widgButton.setDataPath(sDataPath);
    registerWidget(widgButton);
    setToPanel(widgButton);
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
    int ySize = (int)pos.height();
    int xSize = (int)pos.width();
    
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
    setToPanel(widgButton);
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
    int ySize = (int)pos.height();
    int xSize = (int)pos.width();
    
    char size = ySize > 3? 'B' : 'A';
    GralButton widgButton = new GralButton(sName);
    widgButton.setSwitchMode(colorOff, colorOn);
    widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn);
    //in ctor: widgButton.setPanelMng(this);
    if(sName !=null){ registerWidget(widgButton); }
    setToPanel(widgButton);
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
    int ySize = (int)pos.height();
    int xSize = (int)pos.width();
    
    char size = ySize > 3? 'B' : 'A';
    GralButton widgButton = new GralButton(sName);
    widgButton.setSwitchMode(colorOff, colorOn, colorDisabled);
    widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn, sButtonTextDisabled);
    //widgButton.setPanelMng(this);
    if(sName !=null){ registerWidget(widgButton); }
    setToPanel(widgButton);
    return widgButton;
  }

  
  
  
  @Override public GralLed addLed(
  	String sName
  //, String sShowMethod
  , String sDataPath
  )
  {
    int ySize = (int)(pos.height());
    int xSize = (int)(pos.width());

    GralLed widgetInfos = new SwtLed(sName, this);
    //widgetInfos.setPanelMng(this);
    widgetInfos.setDataPath(sDataPath);
    //widgetInfos.setShowMethod(sShowMethod);
    registerWidget(widgetInfos);
    return widgetInfos;
  }
  
	@Override public GralCurveView addCurveViewY(String sName, int nrofXvalues, GralCurveView.CommonCurve common) {
    //setNextPosition();
	  GralCurveView widgd = new SwtCurveView(sName, this.pos, this, nrofXvalues, common); //, curveView, 'c', sName, null);
		super.curveContainer.add(widgd);
	  //CurveView curveView = new CurveView(((SwtPanel)pos.panel).getPanelImpl(), dxWidget, dyWidget, nrofXvalues, nrofTracks);
		testHelp.curveView = widgd; //store to inspect.
		return widgd;
	}


	
	@Override public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    GralWidget widget = indexNameWidgets.get(sName);
  	if(widget == null || !(widget.getWidgetImplementation() instanceof Control)){
  		log.sendMsg(0, "GuiMainDialog:addClickAction: unknown widget %s", sName);
  	} else {
    	/**The class ButtonUserAction implements the general button action, which class the registered user action. */
      ((Control)(widget.getWidgetImplementation())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
      
  	}
  	return widget;
	}

	
	@Override public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    ((Control)(widgetInfo.getWidgetImplementation())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
  }

	
  @Override public GralTable addTable(String sName, int height, int[] columnWidths)
  {
    GralTable table = new GralTable(sName, columnWidths);
    return SwtTable.addTable(table, this, sName, height, columnWidths);

  }
  
  
  @Override protected GralMenu XXXaddPopupMenu(String sName){
    Control panelSwt = (Control)pos.panel.getPanelImpl();
    GralMenu menu = new SwtMenu(null, panelSwt, this);
    return menu;
  }
  
  
  @Override protected GralMenu createContextMenu(GralWidget widg){
    Control widgSwt = (Control)widg.getWidgetImplementation();
    GralMenu menu = new SwtMenu(widg, widgSwt, this);
    return menu;
  }
 
  
  @Override protected GralMenu createMenuBar(GralWindow windg){
    Shell windowSwt = (Shell)windg.getWidgetImplementation();
    GralMenu menu = new SwtMenu(windg, windowSwt, this);
    return menu;
  }
 
  
  
  @Override public void repaint()
  {
  	assert(false);
    //gralDevice.redraw();
  }
  
  
  
  @Override public void repaintCurrentPanel()
  {
    pos.panel.repaint();
  }
  
  
  
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set<Entry<String, GralWidget>> getShowFields()
  {
  	Set<Entry<String, GralWidget>> set = showFields.entrySet();
  	return set; //(Set<Entry<String, WidgetDescriptor>>)set;
  }

  
  @Override public GralFileDialog_ifc createFileDialog()
  {
    Composite panelSwt = (Composite)pos.panel.getPanelImpl(); //cast admissible, it should be SWT
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
		GralWidget descr = indexNameWidgets.get(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	}
  	
	}

	
	@Override public void redrawWidget(String sName)
	{
		GralWidget descr = indexNameWidgets.get(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	} else {
  	}
	}

	
	@Override public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
	{
	  //GralWidget_ifc widget = widgd.getGraphicWidgetWrapper();
	  Object owidg = widgd.getWidgetImplementation();
	  int test = 6;
	  if(owidg !=null){
	    Control swtWidget = (Control)owidg;
	    GralPanelContent panel = widgd.pos().panel;
	    GralRectangle size = panel.getPixelPositionSize(); //PixelSize();
	    GralRectangle posSize = calcWidgetPosAndSize(widgd.pos(), size.dx, size.dy, 0, 0);
  	  //Note: the swtWidget may have a resizeListener, see there.
	    swtWidget.setBounds(posSize.x, posSize.y, posSize.dx, posSize.dy );
  	  swtWidget.redraw();
	  }
	}
	
	
  
	
	
	@Override public String getValueFromWidget(GralWidget widgd)
	{ String sValue;
  	sValue = super.getValueFromWidget(widgd);  //platform independent getting of value
  	if(sValue == null){
  	  GralWidget_ifc widget = widgd;
      Control swtWidget = (Control)widgd.getWidgetImplementation();
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
    	  log.sendMsg(0, "GuiPanelMngSwt.getValueFromWidget - unknown widget type;");
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
	
	
	
	@Override public Color getColorImpl(GralColor colorGral)
	{ return propertiesGuiSwt.colorSwt(colorGral);
	}



  @Override public boolean showContextMenuGthread(GralWidget widg) {
    boolean bOk;
    Control swtWidg = (Control)widg.getWidgetImplementation();
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
  
  

	
	
  /**This routine is invoked on any key event.
   * It is possible to change keys, to disable the event handling and to call special routines.
   * Yet not used.
   */
  Listener mainKeyListener = new Listener(){
    @Override public void handleEvent(Event event) {
      // TODO Auto-generated method stub
      if(userMainKeyAction !=null 
          && (event.keyCode & 0xffff) !=0  //don't take anything on alt- etc. alone
        ){
        final int keyCode = SwtGralKey.convertFromSwt(event.keyCode, event.stateMask);
        boolean bDone = userMainKeyAction.userActionGui(keyCode, null);
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
  protected class SwtMngFocusListener extends GralMngFocusListener implements FocusListener
  {
    
    @Override public void focusLost(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(ev.widget.getData());
      super.focusLostGral(widgg);
    }
    
    @Override public void focusGained(FocusEvent ev)
    { GralWidget widgg = GralWidget.ImplAccess.gralWidgetFromImplData(ev.widget.getData());
      super.focusGainedGral(widgg);
    }
  }
  
  /**The package private universal focus listener. */
  protected SwtMngFocusListener focusListener = new SwtMngFocusListener();


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


	void stop(){}  //debug helper


}
