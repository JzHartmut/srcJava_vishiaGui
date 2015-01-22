package org.vishia.gral.ifc;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.msgDispatch.LogMessage;



/**This is a unique interface for the GUI-panel-manager to work with it.
 * To build the graphical application see {@link org.vishia.gral.ifc.GralMngBuild_ifc}.
 * This interface supports handling with all widgets in a GUI. 
 * The widgets were selected by identifier or with their {@link GralWidget} instance
 * whereby the implementation environment of a widget may be known. That implementation environment
 * is a implementor of this interface.
 *   
 * @author Hartmut Schorrig
 *
 */
public interface GralMng_ifc 
{

  /**The version history of this interface:
   * <ul>
   * <li>2012-08-20 Hartmut new: {@link #getWidgetsPermanentlyUpdating()} created but not used yet because 
   *   {@link #refreshCurvesFromVariable(VariableContainer_ifc)} has the necessary functionality.
   * <li>2012-01-07 Hartmut new: {@link #setInfoDelayed(GralWidgetChangeRequ, int)}
   * <li>2011-05-08 Hartmut new; {@link #cmdClear} used to clear a whole swt.Table, commonly using: clear a content of widget.
   * <li>2011-05-01 Hartmut new: {@link #cmdInsert} etc now here. 
   * <li>2011-05-01 Hartmut new: {@link #setInfo(GralWidget, int, int, Object)} as adequate method
   *     to {@link #insertInfo(String, int, String) but without symbolic addressing of the widget.
   *     It calls the internal method to insert an information in a queue for the graphical thread. 
   *     It is thread-safe.
   * <li>All other changes in 2010
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120303;

  final static int cmdInsert = 0xadd;     //add
  
  final static int cmdSet = 0x5ed;     //add
  
  final static int cmdClear = 0xc1ea3;     //add
  
  final static int cmdBackColor = 0xbacc0103;     //add
  
  final static int cmdLineColor = 0x111c0103;
  
  final static int cmdTextColor = 0x1e8c0103;     //add
  
  final static int cmdColor = 0xc0103;
  
  final static int cmdRedraw = 0x3ed3a2;  //redraw
  
  //final static int cmdRedrawPart = 0x3ed3a201;  //redraw
  
  final static int cmdRemove = 0xde1e7e;  //delete
  
  final static int cmdSelect = 0x5e1ecd;
  
  final static int cmdSetWindowVisible = 0x10001;
  
  final static int cmdCloseWindow = 0x10002;
  
  //final static int cmdSetData = 0x5e1da1a;  //setdata
  
  
  GralGridProperties propertiesGui();
  
  GralGraphicThread gralDevice();
  
  LogMessage log();
  
  /**Searches a widget by name.
   * @param name The name which is assigned on build.
   * @return null if the widget is not found.
   */
  GralWidget getWidget(String name);
  
	/**Returns the list of all widgets with its informations. 
	 * The graphical representation of the widgets is unknown here.
	 * If it should be used, the graphic implementation should be known
	 * and a adequate instanceof-test and cast is necessary. 
	 */
	List<GralWidget> getListCurrWidgets();
	
	
	
  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setBackColor(String name, int ix, int colorValue);
  
  
  
  /**Sets the text into a named widget.
   * @param widget
   * @param text The text is not stored by reference, it can be changed after them.
   */
  void setText(String widget, CharSequence text);
  
  /**Sets the text into a named widget.
   * @param widget
   * @param text The text is not stored by reference, it can be changed after them.
   */
  void setValue(String widget, Object visibleInfo, Object userData);
  
  /**Appends the text into a named widget. The widget can be especially a text area.
   * @param widget
   * @param text The text is not stored by reference, it can be changed after them.
   */
  void addText(String widget, CharSequence text);
  
  
  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   * @deprecated use {@link GralWidget#setBackColor(GralColor, int)
   */
  @Deprecated
  void setBackColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
  /**Sets the color of line of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   * @deprecated use {@link GralWidget#setLineColor(GralColor, int)
   */
  @Deprecated
  void setLineColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
  /**Sets the color of text of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   * @deprecated use {@link GralWidget#setTextColor(GralColor)
   */
  @Deprecated
  void setTextColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
  /**
   * @param widgetDescr
   * @param colorBorder
   * @param colorInner
   * @deprecated use {@link org.vishia.gral.base.GralLed#setColor(GralColor, GralColor)}.
   */
  @Deprecated
  void setLed(GralWidget widgetDescr, int colorBorder, int colorInner);
  
  /**Adds a sampling value set.
   * @param sName The registered name
   * @param values The values.
   */
  void setSampleCurveViewY(String sName, float[] values);
  
  /**Forces the redrawing for all set samples. It should be called after { @link #setSampleCurveViewY(String, float[])}.
   * @param sName The name of the widget.
   */
  void redrawWidget(String sName);
  
  
  
  /**Forces the resizing of the given widged. 
   * @param widgd the widget
   */
  void resizeWidget(GralWidget widgd, int xSizeParent, int ySizeParent);
  
  
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
  boolean XXXsetWindowsVisible(GralWindow_ifc window, GralPos atPos);
  
  
	/**Gets the value to the named color. It is a method of the graphic.
	 * @param sName supported: red, green, blue, yellow
	 * @return 3 bytes intensity: bit23..16 blue, bit15..8: green, bit 7..0 red. 
	 */
	int getColorValue(String sName);
	
  /**Gets the value from a widget.
   * @param sName The name of the widget. The widget will be searched per name.
   * @return null if the named widget isn't found or the widget is not able to input.
   */
  String getValue(String sName);

	/**Gets the String value from a widget with given descriptor
	 * <ul>
	 * <li>Text field: The content written into, with all spaces.
	 * <li>Table: content of the selected line, all cells separated with tabulator char.
	 * </ul> 
	 * @param widgetDescr The widget.
	 * @return The content.
	 */
	String getValueFromWidget(GralWidget widgetDescr);

	
	/**Sets the focus to the designated widget.
	 * @param widgd
	 * @return true if the focus is set. False if it isn't able to set the focus.
	 */
	void setFocus(GralWidget widgd);
	
	/**Notifies that this widget has the focus gotten.
	 * Note: not all widgets notifies this. The focus can be used to detect which widget is active
	 * while a menu command or any button is pressed.
	 * @param widgd The widget descriptor
	 */
	void notifyFocus(GralWidget widgd);
	
	
  GralWidget getWidgetInFocus();
  
  /**Returns a list of the last widgets in focus in there focus order.
   * On access to the list, usual with an iterator, the returned list should be used
   * with mutex. Use 'synchronized(focusList){....}'. But don't block the access for a longer time!
   * The graphical thread will be wait on mutex if any other widget will be focused.
   *  
   * @return The list.
   */
  List<GralWidget> getWidgetsInFocus();
  
  
  /**Gets the list of all panels which are visible yet and should be updated with values therefore. 
   * @return The list of widget list.
   */
  ConcurrentLinkedQueue<GralVisibleWidgets_ifc> getVisiblePanels();
	
  
  /**refresh all curve views. */
  void refreshCurvesFromVariable(VariableContainer_ifc container);
  
  /**Gets a list of widgets which should be updated permanently because they store data in any time.
   * @return List of widgets which are permanently to update 
   */
  GralVisibleWidgets_ifc getWidgetsPermanentlyUpdating();
  
  
  /**Returns true if the current thread is the graphical thread.
   * This routine is used internally firstly. The graphical thread is that thread, which dispatches
   * all orders for the graphic, see {@link org.vishia.gral.base.GralGraphicThread#run()}.
   * @return true if this routine is called from the same thread as the graphical thread.
   */
  boolean currThreadIsGraphic();
  
  /**Writes a log message instead throwing an exception or writing on standard output.
   * The log message contains a timestamp and can be dispatched to any destination. 
   * All internal methods of gral writes exceptions to that logging system instead abort the execution of the application.
   * @param msgId The ident number for dispatching and evaluation.
   * @param exc The catched exception.
   */
  void writeLog(int msgId, Exception exc);

}
