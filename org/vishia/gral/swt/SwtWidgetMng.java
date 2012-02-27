/****************************************************************************
 * For this source the LGPL Lesser General Public License,
 * published by the Free Software Foundation is valid.
 * It means:
 * 1) You can use this source without any restriction for any desired purpose.
 * 2) You can redistribute copies of this source to everybody.
 * 3) Every user of this source, also the user of redistribute copies
 *    with or without payment, must accept this license for further using.
 * 4) But the LPGL ist not appropriate for a whole software product,
 *    if this source is only a part of them. It means, the user
 *    must publish this part of source,
 *    but don't need to publish the whole source of the own product.
 * 5) You can study and modify (improve) this source
 *    for own using or for redistribution, but you have to license the
 *    modified sources likewise under this LGPL Lesser General Public License.
 *    You mustn't delete this Copyright/Copyleft inscription in this source file.
 *
 * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
 * @version 2010-03-07  (year-month-day)
 * list of changes:
 * 2010-03-07: Hartmut new: The idea for this class comes from the necessity of some helper methods for gui-dialog.
 *
 ****************************************************************************/

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
//import org.eclipse.swt.widgets.;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidgetChangeRequ;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
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
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralCurveView;
import org.vishia.gral.widget.GralValueBar;
import org.vishia.msgDispatch.LogMessage;





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
public class SwtWidgetMng extends GralWidgetMng implements GralGridBuild_ifc, GralPanelMngWorking_ifc
//GuiShellMngIfc<Control>   
{
  private static final long serialVersionUID = -2547814076794969689L;

	/**Version, able to read as hex yyyymmdd.
	 * Changes:
	 * <ul>
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
	 * <li>2011-05-08 Hartmut new; {@link GralPanelMngWorking_ifc#cmdClear} used to clear a whole swt.Table, commonly using: clear a content of widget.
   * <li>2010-12-02 Hartmut: in method insertInfo((): call of checkAdmissibility() for some input parameter, 
	 *     elsewhere exceptions may be possible on evaluating the inserted info in doBeforeDispatching().
	 *     There the causer isn't found quickly while debugging.
	 * <li>2010-12-02 Hartmut: Up to now this version variable, its description contains the version history.
	 * </ul>
	 */
  @SuppressWarnings("hiding")
  public final static int version = 0x20111112;

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
  

  /**Properties of this Dialog Window. The {@link GralWidgetMng} contains an aggregation 
   * to the same instance, but with type {@link GralGridProperties}. Internally there are some more
   * Swt-capabilities in the derived type.
   */
  public  final SwtProperties propertiesGuiSwt;
  
  public final SwtWidgetHelper widgetHelper = new SwtWidgetHelper(this);
  
  /**This mouse-click-implementor is added to any widget,
   * which is associated to a {@link GralWidget} in its data.
   * The infos of the last clicked widget can be got with it.
   */
  SwtGralMouseListener.MouseListenerNoAction mouseClickForInfo = new SwtGralMouseListener.MouseListenerNoAction();
  
  
  SwtKeyListener swtKeyListener = new SwtKeyListener(this);
  
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
  protected SwtWidgetMng(GralGraphicThread graldevice, Device device /*, Composite graphicFrame */
  , char displaySize, VariableContainer_ifc variableContainer
	, LogMessage log)
  { //super(sTitle); 
  	this(graldevice, new SwtProperties(device, displaySize), variableContainer, log);
  	
  }

  /**Creates an instance.
   * @param guiContainer The container where the elements are stored in.
   * @param width in display-units for the window's width, the number of pixel depends from param displaySize.
   * @param height in display-units for the window's height, the number of pixel depends from param displaySize.
   * @param displaySize character 'A' to 'E' to determine the size of the content 
   *        (font size, pixel per cell). 'A' is the smallest, 'E' the largest size. Default: use 'C'.
   */
  public SwtWidgetMng(GralGraphicThread device 
    , SwtProperties propertiesGui
  	, VariableContainer_ifc variableContainer
  	, LogMessage log
  	)
  { super(device, propertiesGui, variableContainer, log);
    this.propertiesGuiSwt = propertiesGui;
    pos.x.p1 = 0; //start-position
    pos.y.p1 = 4 * propertiesGui.yPixelUnit();

    //its a user action able to use in scripts.
		userActions.put("syncVariableOnFocus", this.syncVariableOnFocus);


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

  
  
  
  @Override public GralWindow createWindow(String name, String title, int windProps)
  {
    SwtGraphicThread swtDevice = (SwtGraphicThread)gralDevice;
    SwtSubWindow window = new SwtSubWindow(name, swtDevice.displaySwt, title, windProps, this);
    GralRectangle rect = calcPositionOfWindow(window.pos);
    window.window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
    //window.window.redraw();
    //window.window.update();
    this.pos.panel = window; //it is selected.
    
    //this.pos.set(0,0,0,0,'r');
    return window;

  }
  
  
  
  
  
  @Override public boolean setWindowsVisible(GralWindow_ifc window, GralPos atPos)
  { SwtSubWindow windowSwt = (SwtSubWindow)window;
    if(atPos ==null){
      window.setWindowVisible(false); 
    } else {
      GralRectangle rect = calcPositionOfWindow(atPos);
      windowSwt.window.setBounds(rect.x, rect.y, rect.dx, rect.dy );
      window.setWindowVisible(true); 
    }
    return windowSwt.window.isVisible();
  }

  

  
  
  
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
  
  
  
  
  protected void setPosAndSize_(Control component)
  { setPosAndSizeSwt(component, 0,0);
  }  
  
  /**Set bounds. This method is package-private for SWT-implementation.
   * @param component The SWT-widget.
   * @param widthwidgetNat The natural size of the component.
   * @param heigthWidgetNat The natural size of the component.
   */
  void setPosAndSizeSwt(Control component, int widthwidgetNat, int heigthWidgetNat)
  {
    setNextPosition();
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
    rectangle = calcWidgetPosAndSize(pos, parentSize.width, parentSize.height, widthwidgetNat, heigthWidgetNat);
    component.setBounds(rectangle.x, rectangle.y, rectangle.dx, rectangle.dy );
       
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
	
  
  
  
  @Override public GralWidget addText(String sText, char size, int color)
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
    GralWidget widgd = new SwtWidgetSimpleWrapper("labelText-" + sText, 'S', widget, this);
    return widgd;
  }

  
  @Override public GralWidget addText(String sText, int origin, GralColor textColor, GralColor backColor)
  {
    int mode;
    switch(origin){
    case 1: mode = SWT.LEFT; break;
    case 2: mode = SWT.CENTER; break;
    case 3: mode = SWT.RIGHT; break;
    case 4: mode = SWT.LEFT; break;
    case 5: mode = SWT.CENTER; break;
    case 6: mode = SWT.RIGHT; break;
    case 7: mode = SWT.LEFT; break;
    case 8: mode = SWT.CENTER; break;
    case 9: mode = SWT.RIGHT; break;
    default: mode = 0;
    }
    Label widget = new Label((Composite)pos.panel.getPanelImpl(), mode);
    widget.setForeground(propertiesGuiSwt.colorSwt(textColor));
    widget.setBackground(propertiesGuiSwt.colorSwt(backColor));
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
    GralWidget widgd = new SwtWidgetSimpleWrapper("", 'S', widget, this);
    return widgd;
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
    return new SwtTextFieldWrapper(name, editable, prompt, promptStylePosition, this);
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
  public GralTextField XXXaddTextField(String name, boolean editable, String prompt, String promptStylePosition)
  { Composite panelSwt = (Composite)pos.panel.getPanelImpl();
    SwtTextFieldWrapper widgetInfo = new SwtTextFieldWrapper(name, panelSwt, editable ? 'T' : 'S', this);
    //SwtStyledTextFieldWrapper widgetInfo = new SwtStyledTextFieldWrapper(name, (Composite)pos.panel.getPanelImpl(), editable ? 'T' : 'S', this);
    //StyledText widgetSwt = widgetInfo.textFieldSwt;
    ///
    widgetInfo.setPanelMng(this);
    Text widgetSwt;
    //
    if(prompt != null && promptStylePosition.startsWith("t")){
      setNextPosition();
      final Font promptFont;
      char sizeFontPrompt;
      GralRectangle boundsAll, boundsPrompt, boundsField;
      final GralPos posPrompt = new GralPos(), posField = new GralPos();
      boundsAll = calcWidgetPosAndSize(this.pos, 800, 600, 100, 20);
      float ySize = pos.height();
      //float xSize = pos.width();
      //posPrompt from top, 
      float yPosPrompt, heightPrompt, heightText;
      //switch(promptStylePosition){
        //case 't':{
          if(ySize <= 2.5){ //it is very small for top-prompt:
            yPosPrompt = 1.0f;  //no more less than 1/2 normal line. 
            heightPrompt = 1.0f;
            heightText = ySize - 0.7f;
            if(heightText < 1.0f){ heightText = 1.0f; }
          } else if(ySize <=4.0){ //it is normally
            heightPrompt = ySize - 2.0f + (4.0f - ySize) * 0.5f; 
            if(heightPrompt < 1.0f){ heightPrompt = 1.0f; }
            yPosPrompt = ySize - heightPrompt + 0.2f;  //no more less than 1/2 normal line. 
            heightText = 2.0f;
          } else { //greater then 4.0
            yPosPrompt = ySize * 0.5f;
            heightPrompt = ySize * 0.4f;;
            heightText = ySize * 0.5f;
          }
          //from top, size of prompt
          posPrompt.setPosition(this.pos, GralPos.same - ySize + yPosPrompt, GralPos.size - heightPrompt, GralPos.same, GralPos.same, 0, '.');
          //from bottom line, size of text
          posField.setPosition(this.pos, GralPos.same, GralPos.size - heightText, GralPos.same, GralPos.same, 0, '.');
        //} break;
      //}
      promptFont = propertiesGuiSwt.getTextFontSwt(heightPrompt, GralFont.typeSansSerif, GralFont.styleNormal); //.smallPromptFont;
      boundsPrompt = calcWidgetPosAndSize(posPrompt, boundsAll.dx, boundsAll.dy, 10,100);
      boundsField = calcWidgetPosAndSize(posField, boundsAll.dx, boundsAll.dy, 10,100);
      widgetInfo.promptSwt = new SwtTransparentLabel(panelSwt, SWT.TRANSPARENT);
      widgetInfo.promptSwt.setFont(promptFont);
      widgetInfo.promptSwt.setText(prompt);
      widgetInfo.promptSwt.setBackground(null);
      Point promptSize = widgetInfo.promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      if(promptSize.x > boundsPrompt.dx){
        boundsPrompt.dx = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
      }
      widgetSwt =  new Text(panelSwt, SWT.SINGLE);
      widgetInfo.textFieldSwt = widgetSwt;
      widgetSwt.setBounds(boundsField.x, boundsField.y, boundsField.dx, boundsField.dy);
      widgetInfo.promptSwt.setBounds(boundsPrompt.x, boundsPrompt.y, boundsPrompt.dx, boundsPrompt.dy+1);
      posUsed = true;
      
    } else {
      //without prompt
      widgetSwt =  new Text(panelSwt, SWT.SINGLE);
      widgetInfo.textFieldSwt = widgetSwt;
      setPosAndSize_(widgetSwt);
    }
    widgetSwt.setFont(propertiesGuiSwt.stdInputFont);
    widgetSwt.setEditable(editable);
    if(editable)
      //widgetSwt.setDragDetect(true);
      //widgetSwt.addDragDetectListener(widgetInfo.dragListener);
      
      stop();
    widgetSwt.setBackground(propertiesGuiSwt.colorSwt(GralColor.getColor("wh")));
    widgetSwt.addFocusListener(focusListener);

    Listener[] oldMouseListener = widgetSwt.getListeners(SWT.MouseDown);
    for(Listener lst: oldMouseListener){
      widgetSwt.removeListener(SWT.MouseDown, lst);
    }
    widgetSwt.addMouseListener(mouseClickForInfo);
    
    
    if(prompt != null && promptStylePosition.startsWith("r")){
      Rectangle swtField = widgetSwt.getBounds();
      Rectangle swtPrompt = new Rectangle(swtField.x + swtField.width, swtField.y, 0, swtField.height);
      float hight = this.pos.height();
      final Font promptFont;
      if(hight <2.0){
        promptFont = propertiesGuiSwt.smallPromptFont;  
      } else { 
        promptFont = propertiesGuiSwt.stdInputFont;  
      }
      widgetInfo.promptSwt = new SwtTransparentLabel((Composite)pos.panel.getPanelImpl(), SWT.TRANSPARENT);
      widgetInfo.promptSwt.setFont(promptFont);
      widgetInfo.promptSwt.setText(prompt);
      Point promptSize = widgetInfo.promptSwt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
      swtPrompt.width = promptSize.x;
      widgetInfo.promptSwt.setBounds(swtPrompt);
      
      
    }
    //
    if(widgetInfo.name !=null && widgetInfo.name.charAt(0) == '$'){
    	widgetInfo.name = sCurrPanel + widgetInfo.name.substring(1);
    }
    //link the widget with is information together.
    widgetSwt.setData(widgetInfo);
    if(widgetInfo.name !=null){
      if(!editable){
    	  showFields.put(widgetInfo.name, widgetInfo);
    	}
    }
    registerWidget(widgetInfo);
    return widgetInfo; 
  
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
{ SwtTextBox widgetSwt = new SwtTextBox(name, (Composite)pos.panel.getPanelImpl(), SWT.MULTI|SWT.H_SCROLL|SWT.V_SCROLL, this);
  GralWidget widgetInfo = widgetSwt;
  widgetInfo.setPanelMng(this);
  //Text widgetSwt = new Text(((PanelSwt)pos.panel).getPanelImpl(), SWT.MULTI);
  widgetSwt.textFieldSwt.setFont(propertiesGuiSwt.stdInputFont);
  widgetSwt.textFieldSwt.setEditable(editable);
  if(editable)
    stop();
  widgetSwt.textFieldSwt.setBackground(propertiesGuiSwt.colorSwt(0xFFFFFF));
  widgetSwt.textFieldSwt.addMouseListener(mouseClickForInfo);
  setPosAndSize_(widgetSwt.textFieldSwt);
  if(prompt != null && promptStylePosition == 't'){
    final int yPixelField;
    final Font promptFont;
    int ySize = (int)(pos.height());
    switch(ySize){
    case 3:  promptFont = propertiesGuiSwt.smallPromptFont;
             yPixelField = propertiesGuiSwt.yPixelUnit() * 2 -3;
             break;
    case 2:  promptFont = propertiesGuiSwt.smallPromptFont;
             yPixelField = (int)(1.5F * propertiesGui.yPixelUnit());
             break;
    default: promptFont = propertiesGuiSwt.smallPromptFont;
             yPixelField = propertiesGui.yPixelUnit() * 2 -3;
    }//switch
    Rectangle boundsField = widgetSwt.textFieldSwt.getBounds();
    Rectangle boundsPrompt = new Rectangle(boundsField.x, boundsField.y-3  //occupy part of field above, only above the normal letters
      , boundsField.width, boundsField.height );
    
    if(promptStylePosition == 't'){ 
      boundsPrompt.height -= (yPixelField -4);
      boundsPrompt.y -= 1;
      
      boundsField.y += (boundsField.height - yPixelField );
      boundsField.height = yPixelField;
    }
    Label wgPrompt = new Label(((SwtPanel)pos.panel).getPanelImpl(), 0);
    //Text wgPrompt = new Text(((SwtPanel)pos.panel).getPanelImpl(), 0);
    wgPrompt.setFont(promptFont);
    wgPrompt.setText(prompt);
    Point promptSize = wgPrompt.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
    if(promptSize.x > boundsPrompt.width){
      boundsPrompt.width = promptSize.x;  //use the longer value, if the prompt text is longer as the field.
    }
    widgetSwt.textFieldSwt.setBounds(boundsField);
    wgPrompt.setBounds(boundsPrompt);
  } 
  //
  if(widgetInfo.name !=null && widgetInfo.name.charAt(0) == '$'){
    widgetInfo.name = sCurrPanel + widgetInfo.name.substring(1);
  }
  //link the widget with is information together.
  widgetSwt.textFieldSwt.setData(widgetInfo);
  if(widgetInfo.name !=null){
    if(!editable){
      showFields.put(widgetInfo.name, widgetInfo);
    }
  }
  registerWidget(widgetInfo);
  return widgetSwt; 

}

  
@Override public GralHtmlBox addHtmlBox(String name){
  return new SwtHtmlBox(name, this);
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
  public void addLine(int colorValue, float xa, float ya, float xe, float ye){
  	if(pos.panel.getPanelImpl() instanceof SwtCanvasStorePanel){
  		GralColor color = propertiesGui.color(colorValue);
  		int xgrid = propertiesGui.xPixelUnit();
  		int ygrid = propertiesGui.yPixelUnit();
  		int x1 = (int)((pos.x.p1 + xa) * xgrid);
  		int y1 = (int)((pos.y.p1 - ya) * ygrid);
  		int x2 = (int)((pos.x.p1 + xe) * xgrid);
  		int y2 = (int)((pos.y.p1 - ye) * ygrid);
  		//Any panel which is created in the SWT-implementation is a CanvasStorePanel.
  		//This is because lines should be drawn.
  		((SwtCanvasStorePanel) pos.panel.getPanelImpl()).store.drawLine(color, x1, y1, x2, y2);
  		//furtherSetPosition((int)(xe + 0.99F), (int)(ye + 0.99F));
  	} else {
  		log.sendMsg(0, "GuiPanelMng:addLine: panel is not a CanvasStorePanel");
    }
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
      canvas.store.drawImage(imageGui, rr.x, rr.y, rr.dx, rr.dy, size);
    }
    return null;
  }

  
  
  //@Override 
  public Object XXXaddImage(String sName, InputStream imageStream, int height, int width, String sCmd)
  {
    ImageData imageData = new ImageData(imageStream);
    byte[] data = imageData.data;
    Label widget = new Label(((SwtPanel)pos.panel).getPanelImpl(), 0);
    Image image = new Image((((SwtPanel)pos.panel).getPanelImpl()).getDisplay(), imageData); 
    widget.setImage(image);
    widget.setSize(propertiesGui.xPixelUnit() * width, propertiesGui.yPixelUnit() * height);
    setBounds_(widget);
    if(sCmd != null){
      widget.setData(sCmd);
    } 
    GralWidget widgd = new SwtWidgetSimpleWrapper(sName, 'i', widget, this);
    widgd.setPanelMng(this);
    registerWidget(widgd);
    return widget;
  }

  
  
  @Override public GralValueBar addValueBar(
  	String sName
  , String sShowMethod
  , String sDataPath
  )
  {
  	SwtValueBar widget = new SwtValueBar(sName, this);
  	setPosAndSize_(widget.widgetSwt);
  	Rectangle rect = widget.widgetSwt.getBounds();
  	widget.horizontal = rect.width > rect.height;
  	widget.setPanelMng(this);
    widget.setShowMethod(sShowMethod);
  	widget.setDataPath(sDataPath);
    //widget.widget.setData(widgetInfos);
    widget.widgetSwt.addMouseListener(mouseClickForInfo);
    registerWidget(widget);
    return widget;
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
   	GralWidget widgetInfos = new SwtWidgetSimpleWrapper(sName, 'V', control, this);
   	widgetInfos.setPanelMng(this);
    if(action != null){
  		SelectionListenerForSlider actionSlider = new SelectionListenerForSlider(widgetInfos, action);
  		control.addSelectionListener(actionSlider);
  	}
    widgetInfos.setDataPath(sDataPath);
    control.setData(widgetInfos);
    control.addMouseListener(mouseClickForInfo);
    return widgetInfos;
  	
  }
  
  
  
  
  @Override public GralButton addButton(
  	String sName
  , GralUserAction action
  , String sCmd
  , String sShowMethod
  , String sDataPath
  , String sButtonText
  	//, int height, int width
  	//, String sCmd, String sUserAction, String sName)
  )
  {
    int ySize = (int)pos.height();
    int xSize = (int)pos.width();
    
    char size = ySize > 3? 'B' : 'A';
    if(sName == null){ sName = sButtonText; }
    SwtButton widgButton = new SwtButton(sName, this, (Composite)pos.panel.getPanelImpl(), 0, size);
    if(action !=null)
      stop();
    widgButton.setActionChange(action);  //maybe null
  	widgButton.setText(sButtonText);
    //ButtonSwt button = new ButtonSwt(this, null, size);
  	//GralWidget widgetInfos = new WidgetSimpleWrapperSwt(sName, 'B', button);
    widgButton.setPanelMng(this);
    //button.setForeground(propertiesGuiSwt.colorSwt(0xff00));
    //button.setSize(propertiesGui.xPixelUnit() * xSize -2, propertiesGui.yPixelUnit() * ySize -2);
    //setBounds_(button);
    widgButton.sCmd = sCmd;
    widgButton.setShowMethod(sShowMethod);
    widgButton.sDataPath = sDataPath;
    //pos.panel.widgetIndex.put(sName, widgButton);
    registerWidget(widgButton);
    /*
    if(action != null){
    	widgButton.widgetSwt.addMouseListener( new MouseClickActionForUserActionSwt(this, action, null, "Button-up", null));
    } else {
      widgButton.widgetSwt.addMouseListener(mouseClickForInfo);
    }
    if(sName != null){
      indexNameWidgets.put(sName, widgButton);
    }
    */
    return widgButton;
  }
  

  
  @Override public GralButton addSwitchButton(
    String sName
  , GralUserAction action
  , String sCmd
  , String sShowMethod
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
    SwtButton widgButton = new SwtButton(sName, this, (Composite)pos.panel.getPanelImpl(), 0, size);
    GralColor colorOff = GralColor.getColor(sColor0);
    GralColor colorOn = GralColor.getColor(sColor1);
    widgButton.setSwitchMode(colorOff, colorOn);
    widgButton.setActionChange(action);  //maybe null
    widgButton.setText(sButtonText);
    widgButton.setPanelMng(this);
    widgButton.sCmd = sCmd;
    widgButton.setShowMethod(sShowMethod);
    widgButton.sDataPath = sDataPath;
    registerWidget(widgButton);
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
    SwtButton widgButton = new SwtButton(sName, this, (Composite)pos.panel.getPanelImpl(), 0, size);
    widgButton.setSwitchMode(colorOff, colorOn);
    widgButton.setSwitchMode(sButtonTextOff, sButtonTextOn);
    widgButton.setPanelMng(this);
    if(sName !=null){ registerWidget(widgButton); }
    return widgButton;
  }
  

  @Override public GralWidget addLed(
  	String sName
  , String sShowMethod
  , String sDataPath
  )
  {
    int ySize = (int)(pos.height());
    int xSize = (int)(pos.width());

    GralWidget widgetInfos = new SwtLed(sName, this);
    widgetInfos.setPanelMng(this);
    widgetInfos.sDataPath = sDataPath;
    widgetInfos.setShowMethod(sShowMethod);
    registerWidget(widgetInfos);
    return widgetInfos;
  }
  
	@Override public GralCurveView addCurveViewY(String sName, int nrofXvalues, int nrofTracks) {
    GralCurveView widgd = new SwtCurveView(sName, this, nrofXvalues, nrofTracks); //, curveView, 'c', sName, null);
		//CurveView curveView = new CurveView(((SwtPanel)pos.panel).getPanelImpl(), dxWidget, dyWidget, nrofXvalues, nrofTracks);
		testHelp.curveView = widgd; //store to inspect.
		return widgd;
	}


	
	@Override public GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    GralWidget widget = indexNameWidgets.get(sName);
  	if(widget == null || !(widget.getGraphicWidgetWrapper() instanceof Control)){
  		log.sendMsg(0, "GuiMainDialog:addClickAction: unknown widget %s", sName);
  	} else {
    	/**The class ButtonUserAction implements the general button action, which class the registered user action. */
      ((Control)(widget.getGraphicWidgetWrapper())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
      
  	}
  	return widget;
	}

	
	@Override public void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease)
	{
    ((Control)(widgetInfo.getGraphicWidgetWrapper())).addFocusListener( new SwtFocusAction(this, action, sCmdEnter, sCmdRelease));
  }

	
  @Override public GralTable addTable(String sName, int height, int[] columnWidths)
  {
    return SwtTable.addTable(this, sName, height, columnWidths);
  }
  
  
  @Override public GralMenu addPopupMenu(String sName){
    Control panelSwt = (Control)pos.panel.getPanelImpl();
    SwtMenu menu = new SwtMenu(sName, panelSwt, this);
    return menu;
  }
  
  
  @Override public GralMenu addContextMenu(GralWidget widg){
    Control widgSwt = (Control)widg.getWidgetImplementation();
    GralMenu menu = new SwtMenu(widg.name + "_menu", widgSwt, this);
    widgSwt.setMenu((Menu)menu.getMenuImpl());
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
  
  
  
  /**Sets the content of any field during operation. The GUI should be created already.
   * @param name Name of the field, corresponding with the <code>name</code>-designation of the field 
   *             in the configuration.
   * @param content The new content.
   * @return The old content of the field.
   * throws GuiDialogZbnfControlled.NotExistException if the field with the given name isn't found.
   * @deprecated it doesn't work yet. It isn't threadsafe. Use {@link #setInfo(GralWidget, int, int, Object, Object)}
   */
  public String setFieldContent(String name, String content)
  throws NotExistException
  {
    GralWidget descr = indexNameWidgets.get(name);
    if(descr == null) throw new NotExistException(name);
    assert(descr.getGraphicWidgetWrapper() instanceof Text);
    Text field = (Text)descr.getGraphicWidgetWrapper();
    String oldContent = field.getText();
    field.setText(content);
    return oldContent;
  }
  
  /**Inserts a textual information at any widget. The widget may be for example:
   * <ul>
   * <li>a Table: Than a new line will be inserted or appended. 
   *     The content associated to the cells are separated with a tab-char <code>'\t'</code>.
   *     The line number is identified by the ident. 
   * <li>a Tree: Than a new leaf is insert after the leaf, which is identified by the ident.
   * <li>a Text-edit-widget: Than a text is inserted in the field.
   * </ul>
   * The insertion is written into a queue, which is red in another thread. 
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
  public String insertInfo(String name, int ident, String content)
  {
  	GralWidget descr = indexNameWidgets.get(name);
  	if(descr == null){
  		log.sendMsg(0, "GuiMainDialog:insertInfo: unknown widget %s", name);
  	} else {
  		insertInfo(descr, ident, content);
  	}
  	return "";
  } 
  
  public String insertInfo(GralWidget descr, int ident, String content)
  {
  	return setInfo(descr, GralPanelMngWorking_ifc.cmdInsert, ident, content, null);
  }
  
  
  
  public String insertInfo(GralWidget descr, int ident, Object value)
  {
  	return setInfo(descr, GralPanelMngWorking_ifc.cmdInsert, ident, value, null);
  }
  
  
  
  
  //past: insertInfo
  @Override public String setInfo(GralWidget descr, int cmd, int ident, Object visibleInfo, Object userData)
  {
    if(currThreadIsGraphic()){
      setInfoGthread(descr, cmd, ident, visibleInfo, userData);
    } else {
    	if(descr.name !=null && descr.name.equals("writerEnergy1Sec") && cmd == GralPanelMngWorking_ifc.cmdInsert) 
    		stop();
    	//check the admissibility:
    	switch(cmd){
      case 0: checkAdmissibility(false);
    	case GralPanelMngWorking_ifc.cmdInsert: checkAdmissibility(visibleInfo != null && (visibleInfo instanceof String || visibleInfo instanceof String[])); break;
    	}
    	widgetChangeRequExecuter.addRequ(new GralWidgetChangeRequ(descr, cmd, ident, visibleInfo, userData));
    }
  	return "";
  }
  
  
  @Override protected String setInfoGthread(GralWidget_ifc widget, int cmd, int ident, Object info, Object data)
  { final Object oSwtWidget = widget.getWidgetImplementation();
    final Control swtWidget;
    String sError = null;
    boolean done = false;
    try{
      SwtSetValue_ifc widgSet = null;
      GralWidgetGthreadSet_ifc gralWidget_setifc = null;
      if(widget !=null && widget instanceof SwtSetValue_ifc){
        widgSet = (SwtSetValue_ifc)widget;
      } else if (oSwtWidget !=null && oSwtWidget instanceof SwtSetValue_ifc){
        widgSet = (SwtSetValue_ifc)oSwtWidget;
      }
      if(widgSet !=null){
        GralWindow_setifc gralWindow_setifc = widgSet.getSwtWindow_ifc();
        if(gralWindow_setifc !=null){
          //check cmd for it:
          done = true;
          switch(cmd){
          case GralPanelMngWorking_ifc.cmdSetWindowVisible: gralWindow_setifc.setWindowVisible(ident == 0 ? false : true); break;
          case GralPanelMngWorking_ifc.cmdCloseWindow: gralWindow_setifc.closeWindow(); break;
          case GralPanelMngWorking_ifc.cmdRedraw: gralWindow_setifc.repaintGthread(); break;
          default: done = false;
          }
        }
        gralWidget_setifc = widgSet.getSwtWidget_ifc();
      } else if(oSwtWidget !=null && oSwtWidget instanceof GralWidgetGthreadSet_ifc){
        gralWidget_setifc = (GralWidgetGthreadSet_ifc)oSwtWidget;
      }
      
      if(gralWidget_setifc !=null){
        //check cmd for it:
        done = true;
        switch(cmd){
        case GralPanelMngWorking_ifc.cmdClear: gralWidget_setifc.clearGthread(); break;
        case GralPanelMngWorking_ifc.cmdRedraw: gralWidget_setifc.redrawGthread(); break;
        case GralPanelMngWorking_ifc.cmdInsert: gralWidget_setifc.insertGthread(ident, info, data); break;
        case GralPanelMngWorking_ifc.cmdSet: {
          if(info instanceof String)
          gralWidget_setifc.setTextGthread((String)info, data); 
        } break;
        default: done = false;
        }//switch
      }
      else if( !done &&  widget !=null 
        && ( swtWidget = (Control)widget.getWidgetImplementation()) !=null
        ){
        //common methods for all swt.Control
        int colorValue;
        done = true;
        switch(cmd){
        case GralPanelMngWorking_ifc.cmdBackColor: {
          colorValue = ((Integer)(info)).intValue();
          Color color = propertiesGuiSwt.colorSwt(colorValue & 0xffffff);
          swtWidget.setBackground(color); 
        } break;
        case GralPanelMngWorking_ifc.cmdLineColor:{ 
          colorValue = ((Integer)(info)).intValue();
          Color color = propertiesGuiSwt.colorSwt(colorValue & 0xffffff);
          swtWidget.setForeground(color); 
        } break;
        case GralPanelMngWorking_ifc.cmdRedraw: swtWidget.redraw(); break;
        case GralPanelMngWorking_ifc.cmdRedrawPart: 
          assert(swtWidget instanceof CurveView);
          ((CurveView)(swtWidget)).redrawData(); break; //causes a partial redraw
        default:
          
        if(swtWidget instanceof Text){ 
            Text field = (Text)swtWidget;
            switch(cmd){
              case GralPanelMngWorking_ifc.cmdSet:
              case GralPanelMngWorking_ifc.cmdInsert: 
                String sInfo = (String)info;
                field.setText(sInfo); 
                //shows the end of text because the position after last char is selected.
                //field.setSelection(sInfo.length());  
                break;
            default: log.sendMsg(0, "GuiMainDialog:dispatchListener: unknown cmd: %x on widget %s", cmd, widget.getName());
            }
          } else if(widget instanceof SwtLed){ 
            SwtLed field = (SwtLed)widget;
            switch(cmd){
            case GralPanelMngWorking_ifc.cmdColor: field.setColor(ident, (Integer)info); break;
            case GralPanelMngWorking_ifc.cmdSet: {
              if(info instanceof Integer){
                int colorInner = ((Integer)info).intValue();
                field.setColor(ident, colorInner);
              } else if(info instanceof String){
                if(((String)info).charAt(0) != '0'){
                  field.setColor(0xff0000, 0xfff00);
                } else {
                  field.setColor(0x00ff00, 0x00ffff);
                }
              } else {
                stop();
              }
            } break;
            default: log.sendMsg(0, "GuiMainDialog:dispatchListener: unknown cmd: %d on widget %s", cmd, widget.getName());
            }
          } else if(widget instanceof GralButton){ 
            GralButton widgetButton = (GralButton)widget;
            widgetButton.setState(info);
          } else {
            done = false;
            //all other widgets:    
            //switch(cmd){  
            //default: log.sendMsg(0, "GuiMainDialog:dispatchListener: unknown cmd %x for widget: %s", cmd, widget.getName());
            //}
          }
        }//switch
      }//if oWidget !=null
      if(!done){
        //this is the new form. TODO only use that. TODO correct implementations for all widgets.
        GralWidgetGthreadSet_ifc setIfc = widget.getGthreadSetifc();
        switch(cmd){
          case GralPanelMngWorking_ifc.cmdClear: setIfc.clearGthread(); break;
          case GralPanelMngWorking_ifc.cmdRedraw: setIfc.redrawGthread(); break;
          case GralPanelMngWorking_ifc.cmdInsert: setIfc.insertGthread(ident, info, data); break;
          case GralPanelMngWorking_ifc.cmdSet: {
            if(info instanceof String){
              setIfc.setTextGthread((String)info, data);
            } else {
              setIfc.insertGthread(ident, info, data);
            }
          } break;
          default: 
            log.sendMsg(0, "GuiMainDialog:dispatchListener: unknown cmd %x for widget: %s", cmd, widget.getName());
          }//switch
      }
    }catch(Exception exc){
      stop();    
    }
    return sError;
  }
  

  
  
  
  /**Gets the content of any field during operation. The GUI should be created already.
   * @param name Name of the field, corresponding with the <code>name</code>-designation of the field 
   *             in the configuration.
   * @return The content of the field.
   * throws GuiDialogZbnfControlled.NotExistException if the field with the given name isn't found.
   * @deprecated it doesn't work yet. It isn't threadsafe. Use {@link #setInfo(GralWidget, int, int, Object, Object)}
   */
  public String getFieldContent(String name)
  throws NotExistException
  {
    GralWidget descr = indexNameWidgets.get(name);
    if(descr == null) throw new NotExistException(name);
    Text field = (Text)descr.getGraphicWidgetWrapper();
    String content = field.getText();
    return content;
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
  
  
  
  /**The GUI-change-listener should be called in the dispatch-loop of the GUI-(SWT)-Thread.
   * @return The instance to call run(). 
   * Hint: run() returns after checking orders and should be called any time in the loop. 
   */
  public GralDispatchCallbackWorker getTheGuiChangeWorker(){ return widgetChangeRequExecuter; }

	@Override
	public void setSampleCurveViewY(String sName, float[] values) {
		GralWidget descr = indexNameWidgets.get(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	} else if(!(descr.getGraphicWidgetWrapper() instanceof CurveView)) {
  		log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: widget %s fault type", sName);
  	} else {
  		((CurveView)descr.getGraphicWidgetWrapper()).setSample(values);
  		
  	}
  	
	}

	
	@Override public void redrawWidget(String sName)
	{
		GralWidget descr = indexNameWidgets.get(sName);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sName);
  	} else if((descr.getGraphicWidgetWrapper() instanceof CurveView)) {
  		//sends a redraw information.
  	  widgetChangeRequExecuter.addRequ(new GralWidgetChangeRequ(descr, GralPanelMngWorking_ifc.cmdRedrawPart, 0, null, null));
  	} else {
  	}
	}

	
	@Override public void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent)
	{
	  GralWidget_ifc widget = widgd.getGraphicWidgetWrapper();
	  Control swtWidget = (Control)widget.getWidgetImplementation();
	  Point size = swtWidget.getParent().getSize();
	  //Composite parent = swtWidget.
	  GralRectangle posSize = calcWidgetPosAndSize(widgd.pos, size.x, size.y, 0, 0);
	  swtWidget.setBounds(posSize.x, posSize.y, posSize.dx, posSize.dy );
	  swtWidget.redraw();
	}
	
	
	@Override
	public void setColorGridCurveViewY(String sName, int backgroundColor,
			int[] colorLines, char grid) {
		// TODO Auto-generated method stub
		
	}

	@Override
  public void setLineCurveView(String sNameView, int trackNr, String sNameLine, String sVariable, int colorValue, int style, int nullLine, float yScale, float yOffset)
	{
		GralWidget descr = indexNameWidgets.get(sNameView);
		if(descr == null){
  		//log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: unknown widget %s", sNameView);
  	} else if(!(descr.getGraphicWidgetWrapper() instanceof CurveView)) {
  		log.sendMsg(0, "GuiMainDialog:setSampleCurveViewY: widget %s fault type", sNameView);
  	} else {
      CurveView view = (CurveView)descr.getGraphicWidgetWrapper();
      view.setLine(trackNr, sNameLine, colorValue, style, nullLine, yScale, yOffset);
  	}
	}
  
	
	
	@Override public String getValueFromWidget(GralWidget widgd)
	{ String sValue;
  	sValue = super.getValueFromWidget(widgd);  //platform independent getting of value
  	if(sValue == null){
  	  GralWidget_ifc widget = widgd.getGraphicWidgetWrapper();
      Control swtWidget = (Control)widget.getWidgetImplementation();
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

	
	
	/**This userAction can be used by name (calling {@link #addFocusAction(String, GralUserAction, String, String)} 
	 * to set a variable when an input field is leaved.
	 * TODO it isn't Text
	 */
	private GralUserAction syncVariableOnFocus = new GralUserAction()
	{	/**Writes the value to the named variable on leaving the focus.
		 * The name of the variable is contained in the {@link GralWidget}.
		 * @see org.vishia.gral.ifc.GralUserAction#userActionGui(java.lang.String, org.vishia.gral.ifc.GralWidget, java.lang.Object[])
		 */
		@Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
		{
		  
			Object oWidget = infos.getGraphicWidgetWrapper();  
			final VariableAccess_ifc variable = infos.getVariableFromContentInfo(variableContainer);
			final int ixData = infos.getDataIx();
			final String sValue;
			if(variable !=null){
				if(sIntension.equals("o")){
					if(oWidget instanceof Text){ sValue = ((Text)oWidget).getText(); variable.setString(sValue, ixData); }
					else { sValue = null; }
				} else if(sIntension.equals("i")){
					if(oWidget instanceof Text){ sValue = variable.getString(ixData); ((Text)oWidget).setText(sValue == null ? "" : sValue); }
					else { sValue = null; }
				} else throw new IllegalArgumentException("GuiPanelMng.syncVariableOnFocus: unexpected intension on focus: " + sIntension); 
			} else throw new IllegalArgumentException("GuiPanelMng.syncVariableOnFocus: variable not found: " + infos.getDataPath()); 
      return true;
		}
	};
	
	
  
  /**Universal focus listener to register which widgets were in focus in its order.
   * and to set htmlHelp
   */
  class SwtMngFocusListener implements FocusListener
  {
    
    @Override public void focusLost(FocusEvent e)
    { //empty, don't register lost focus. Only the last widget in focus is registered.
    }
    
    @Override public void focusGained(FocusEvent ev)
    { GralWidget widgd = (GralWidget)ev.widget.getData();
      widgd.gralWidgetMethod.focusGained();
      /*
      widgd.getMng().notifyFocus(widgd);
      String htmlHelp = widgd.getHtmlHelp();
      if(htmlHelp !=null && applAdapter !=null){
        applAdapter.setHelpUrl(htmlHelp);
      }
      */
    }
  }
  
  /**The package private universal focus listener. */
  SwtMngFocusListener focusListener = new SwtMngFocusListener();


  /**Universal context menu listener
   */
  class SwtMngMouseMenuListener implements MenuDetectListener
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
