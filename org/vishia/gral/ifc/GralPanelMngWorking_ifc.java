package org.vishia.gral.ifc;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



/**This is a unique interface for the GUI-panel-manager to work with it.
 * To build the graphical application see {@link org.vishia.gral.ifc.GralGridBuild_ifc}.
 * This interface supports handling with all widgets in a GUI. 
 * The widgets were selected by identifier or with their {@link GralWidget} instance
 * whereby the implementation environment of a widget may be known. That implementation environment
 * is a implementor of this interface.
 *   
 * @author Hartmut Schorrig
 *
 */
public interface GralPanelMngWorking_ifc 
{

  /**The version history of this interface:
   * <ul>
   * <li>2011-05-08 Hartmut new; {@link #cmdClear} used to clear a whole swt.Table, commonly using: clear a content of widget.
   * <li>2011-05-01 Hartmut new: {@link #cmdInsert} etc now here. 
   * <li>2011-05-01 Hartmut new: {@link #setInfo(GralWidget, int, int, Object)} as adequate method
   *     to {@link #insertInfo(String, int, String) but without symbolic addressing of the widget.
   *     It calls the internal method to insert an information in a queue for the graphical thread. 
   *     It is thread-safe.
   * <li>All other changes in 2010
   * </ul>
   */
  final static int version = 0x20110502;

  final static int cmdInsert = 0xadd;     //add
  
  final static int cmdSet = 0x5ed;     //add
  
  final static int cmdClear = 0xc1ea3;     //add
  
  final static int cmdBackColor = 0xbacc0103;     //add
  
  final static int cmdLineColor = 0x111c0103;
  
  final static int cmdTextColor = 0x1e8c0103;     //add
  
  final static int cmdColor = 0xc0103;
  
  final static int cmdRedraw = 0x3ed3a2;  //redraw
  
  final static int cmdRedrawPart = 0x3ed3a201;  //redraw
  
  final static int cmdRemove = 0xde1e7e;  //delete
  
  //final static int cmdSetData = 0x5e1da1a;  //setdata
  

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
	Queue<GralWidget> getListCurrWidgets();
	
  /**Inserts a textual information to any widget. Calls {@link #setInfo(GralWidget, int, int, Object, Object)}
   * with {@link #cmdInsert}.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @param ident A identifying number. It meaning depends on the kind of widget.
   *        0 means, insert on top.  Integer.MAXVALUE means, insert after the last element (append).
   * @param content The content to insert.
   * @return
   */
  String insertInfo(String name, int ident, String content);

  
  /**Sets any information to the given widget.
   * The widget may be for example:
   * <ul>
   * <li>a Table: Than a new line will be inserted or appended. 
   *     The content associated to the cells are separated with a tab-char <code>'\t'</code>.
   *     The line number is identified by the ident. 
   * <li>a Tree: Than a new leaf may be inserted after the leaf, which is identified by the ident.
   * <li>a Text-edit-widget: Than a text can be set or inserted to the field.
   * </ul>
   * The insertion is written into a queue, which is red in another thread. 
   * It may be possible too, that the GUI is realized in another module, maybe remote.
   * It means, that a few milliseconds should be planned before the change appears.
   * If the thread doesn't run or the remote receiver isn't present, 
   * than the queue may be overflowed or the request may be lost.
   *    
   * @param widgd The widget
   * @param cmd See {@link #cmdBackColor} etc.
   * @param ident A value, widget-specific.
   * @param visibleInfo The data which should be shown. It is a String in most of cases.
   * @param userData Any user data. If the widget consist of elements like a table or a tree,
   *   this data are referenced from the element of the widget (the table line, the leaf in the tree). 
   *   Which element it is, it is given by param ident.
   * @return
   */
  String setInfo(GralWidget widgd, int cmd, int ident, Object visibleInfo, Object userData);
  
  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setBackColor(String name, int ix, int colorValue);
  
  /**Sets the color of background of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setBackColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
  /**Sets the color of line of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setLineColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
  /**Sets the color of text of the widget, if possible.
   * @param name The name of the widget, which was given by the add...()-Operation
   * @ix may be a line number of table or an position identifier. 0 if unused for the kind of widget. 
   * @param colorValue blue, green and red in the bits 23..16, 15..8 and 7..0. 
   *        opaque in bits 31..24 if possible. 
   */
  void setTextColor(GralWidget widgetDescr, int ix, int colorValue);
  
  
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
   * A window can be created by invoking {@link org.vishia.gral.ifc.GralGridBuild_ifc#createWindow(String, boolean)}
   * in the build phase of the gui. It can be hidden because it is not necessary to show and operate with them.
   * In a adequate phase of operate it can be shown and focused.
   * <br>
   * The position is given relative to that panel,
   * which is stored in {@link GralPos#panel}. To get a position instance,
   * you can set a position invoking 
   * <ul>
   * <li>{@link org.vishia.gral.ifc.GralGridBuild_ifc#selectPanel(String)}
   * <li>{@link org.vishia.gral.ifc.GralGridBuild_ifc#setPosition(float, float, float, float, int, char)}
   * <li>GralGridPos pos = {@link org.vishia.gral.ifc.GralGridBuild_ifc#getPositionInPanel()}.
   * </ul>
   * That can be done in the build phase of the graphic. The position can be stored. It is possible to adjust
   * the position relative to the unchanged panel by changing the values of {@link GralPos#x} etc.
   * It is possible too to change the Panel which relates to the position. Then the grid managing instance 
   * have to be known via the {@link org.vishia.gral.ifc.GralGridBuild_ifc} to select a panel.
   * The panels may be moved or resized. With the knowledge of the relative position of the window in respect to a panel
   * of the parent window, the window can be placed onto a proper position of the whole display.
   *   
   * @param window the instance of the window wrapper.
   * @param atPos If null then hide the window. If not null then show the window. 
   *        The position and size of the window is given related to any panel of any other window. 
   *         
   * @return true if it is visible.
   */
  boolean setWindowsVisible(GralWindow_ifc window, GralPos atPos);
  
  
	/**Gets the value to the named color. It is a method of the graphic.
	 * @param sName supported: red, green, blue, yellow
	 * @return 3 bytes intensity: bit23..16 blue, bit15..8: green, bit 7..0 red. 
	 */
	int getColorValue(String sName);
	
	/**Gets the color of the graphic implementation (swt.Color, swing.TODO)
	 * Either the implementation color instance is stored already in the GralColor,
	 * or it will be created, stored in GralColor and returned here.
	 * @param color The Color.
	 * @return Instance of the implementation color.
	 */
	Object getColorImpl(GralColor color);
	
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
	boolean setFocus(GralWidget widgd);
	
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
   * @return The list.
   */
  ConcurrentLinkedQueue<GralVisibleWidgets_ifc> getVisiblePanels();
	
  
  /**Writes a log message instead throwing an exception or writing on standard output.
   * The log message contains a timestamp and can be dispatched to any destination. 
   * All internal methods of gral writes exceptions to that logging system instead abort the execution of the application.
   * @param msgId The ident number for dispatching and evaluation.
   * @param exc The catched exception.
   */
  void writeLog(int msgId, Exception exc);

}
