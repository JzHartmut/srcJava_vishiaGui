package org.vishia.mainGui;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Color;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.mainGui.PanelContent;
import org.vishia.mainGui.cfg.GuiCfgBuilder;
import org.vishia.mainGui.cfg.GuiCfgData;



/**This is a unique interface for the GUI-panel-manager to build its content.
 * To work with the graphical application see {@link GuiPanelMngBuildIfc}. 
 * <br><br>
 * Any widget is represented by a {@link WidgetDescriptor}. Either the WidgetDescriptor
 * should be created before, and taken as parameter for the widget-creating method,
 * or the WidgetDescriptor is returned by the widget-creating method. The second form takes
 * most of the characteristics as parameters for the creating method.
 * <br><br>
 * The platform-specific Widget Object (Swing: javax.swing.JComponent, org.eclipse.swt.widgets.Control etc.)
 * is stored as Object-reference in the {@link WidgetDescriptor#widget}.   
 * If necessary it can be casted to the expected class if some special operations 
 * using the graphic platform are need. For the most
 * simple applications, the capability of this interface and its given implementation 
 * for the platform may be sufficient, so the platform-specific widget isn't necessary to use.
 * It may be better to enhance this interface and its implementation(s) if new features are need.
 * But the definition of that enhancements should be done in a commonly form which is able to implement
 * in any known Java GUI platform.
 * <br><br>
 * Generally, the widgets are able to address with an identification String to do something with it
 * for symbolic access. 
 * <br><br>
 * To build a GUI you must use the following order of calls:
 * <ul>
 * <li>Create a panel manager which is typeof {@link GuiPanelMngBase} or this interface.
 *   For example create {@link org.vishia.mainGuiSwt.GuiPanelMngSwt}.
 * <li>Create a panel, for example call {@link #createGridPanel(ColorGui, int, int, int, int)}
 *   and add the panel to the given     
 * <li>Before add, you can select any given panel by String-identifier, using {@link #selectPanel(String)}.
 * <li>Before add, you have to determine the position and size using 
 *   <ul><li>{@link #setPosition(int, int, int, int, char)} in grid units
 *     <li>{@link #setFinePosition(int, int, int, int, int, int, int, int, char)} 
 *     <li>{@link #setNextPositionX()}
 *   </ul>   
 * <li><b>addMethods</b>: This methods create the widget in the current selected panel.
 * </ul>
 * @author Hartmut Schorrig
 *
 */
public interface GuiPanelMngBuildIfc 
{
  
  /**The version of this interface:
   * <ul>
   * <li>2011-05-01 Hartmut new: {@link #addTextBox(WidgetDescriptor, boolean, String, char)}: 
   *     A Text box with more as one line. The TextField has only one line.
   * <li>2011-05-01 Hartmut new: {@link #createCompositeBox()}. It is a box with its own PanelMng
   *     which is located in an area of another panel. (Composite)
   * <li>2011-05-01 Hartmut new: {@link #remove(GuiPanelMngBuildIfc)} and {@link #remove(WidgetDescriptor)}
   *     to remove widgets, for dynamic views.
   * <li>2011-05-01 Hartmut new: {@link #createWindow(String, boolean)} instead createModalWindow(String).
   *     This method should be used for any sub-windows in the application. The window position is determined
   *     inside the current window with the known {@link #setPosition(int, int, int, int, char)} functionality.
   *     The {@link #createWindow(int, int, int, int, VariableContainer_ifc)} with absolute coordinates
   *     may be deprecated. (Is it necessary to create a window outside the own borders? )             
   * <li>All other changes in 2010
   * </ul>
   */
  final static int version = 0x20110502;
  
	/**Returns the width (number of grid step horizontal) of the last element.
   * @return Difference between current auto-position and last pos.
   */
  //int getWidthLast();

  /**Registers a panel to place the widgets. The panel can be selected
   * with its name calling the {@link #selectPanel(String)} -Routine
   * <br>parameter panel:
   * <ul><li>Swing: javax.swing.JPanel
   * <li>SWT: org.eclipse.swt.widgets.Composite
   * </ul>
   * @param name Name of the panel.
   * @param panel The panel. It should be from the correct type of the base-graphic-system.
   *              If it the instance is fault, a ClassCastException is thrown.
   *         
   */
  public PanelContent registerPanel(String name, Object panel);
  
  
  /**Creates a panel for tabs and registers it in the GUI.
   * @param user If not null, then this user class will be notified when a tab is selected.
   *             The user should update showed values.
   * @return The Tab-container, there the tabs can be registered.
   */
  TabPanel createTabPanel(PanelActivatedGui user);
  
  /**selects a registered panel for the next add-operations.
   * see {@link #registerPanel(String, Object)}. 
   */
  void selectPanel(String sName);

  /**Sets the position for the next widget to add in the container.
   * @param xpos x-Position in x-Units, count from left of the box.
   * @param ypos y-Position in y-Units, count from top of the box. It is the bottom line of the widget.
   *              It means ypos = 0 is not a proper value. To show a text in the first line, use ypos=2.
   */
  //void setPosition(int ypos, int xpos);
  
  
  
  /**Sets the position for the next widget to add in the container.
   * @param line y-Position in y-Units, count from top of the box. 
   *              It is either the top or bottom line of the widget, depending on height.
   *              If < 0, then the previous position is valid furthermore.
   * @param column x-Position in x-Units, count from left of the box. 
   *              If < 0, then the previous position is valid furthermore.
   * @param heigth The height of the line. If <0, then the param line respectively the current line 
   *                will be used as bottom line of the next widget, and (line-height) is the top line. 
   *                If 0 then the last value of height is taken furthermore. 
   * @param length The number of columns. If <0, then the param column is the right column, 
   *                and column-length is the left column. If 0 then the last value of length is not changed.
   * @param direction direction for a next widget, use 'r', 'l', 'u', 'd' for right, left, up, down                
   */
  public void setPosition(int line, int column, int height, int length, char direction);
  
  
  /**Same as {@link #setPosition(int, int, int, int, char)}, but the positions can be in a fine division.
   * @param line
   * @param lineFrac Number between 0..9 for fine positioning in the grid step.
   * @param column
   * @param columnFrac
   * @param height
   * @param heigthFrac
   * @param width
   * @param widthFrac
   * @param direction
   */
  public void setFinePosition(int line, int lineFrac, int column, int columnFrac, int height, int heigthFrac, int width, int widthFrac, char direction);
  
  public void setSize(int ySize, int ySizeFrac, int xSize, int xSizeFrac);
  
  
  /**Positions the next widget right to the previous one. */
  void setNextPositionX();

  
  /**Adds a button
   * @param sButtonText text in the button
   * @param height in grid-units
   * @param width in grid-unigs
   * @param sCmd The command string will be transfered to the action-method
   * @param sUserAction The user action shoult be registered before 
   *         calling {@link #registerUserAction(String, UserActionGui)}
   * @param sName
   * @return
   */
  //Object addButton(String sButtonText, int height, int width, String sCmd, String sUserAction, String sName);
  public WidgetDescriptor addButton(
  	String sName
  , UserActionGui action
  , String sCmd
  , String sShowMethod
  , String sDataPath
  , String sButtonText
  );
  
  
  /**Adds a button which saves its state, pressed or non-pressed.
   * 
   * @param sName
   * @param action
   * @param sCmd
   * @param sShowMethod
   * @param sDataPath
   * @param sButtonText
   * @return
   */
  public WidgetDescriptor addSwitchButton(
  	String sName
  , UserActionGui action
  , String sCmd
  , String sShowMethod
  , String sDataPath
  , String sButtonText
  , String color0
  , String color1
  );
  
  
  
  
  /**Adds a Led (round)
   * @param sName
   * @return
   */
  //Object addButton(String sButtonText, int height, int width, String sCmd, String sUserAction, String sName);
  WidgetDescriptor addLed(
  	String sName
  , String sShowMethod
  , String sDataPath
  );
  
  WidgetDescriptor addValueBar(
  	String sName
  , String sShowMethod
  , String sDataPath
  );
  
  
  WidgetDescriptor addSlider(
  	String sName
  , UserActionGui action
  , String sShowMethod
  , String sDataPath
  );
  
  
  /**Adds a table, which is able to scroll.
   * @param sName register name, used for {@link GuiPanelMngWorkingIfc#insertInfo(String, int, String).}
   * @param height The height in grid units for the appearance
   * @param columnWidths Array with width of the columns. 
   *        Each column has a fix default width per construction.
   *        It may or may not a fix widht, it may able to change by mouse actions,
   *        adequate to the possibilities of the used graphic base system. 
   * @return
   */
  WidgetDescriptor addTable(String sName, int height, int[] columnWidths);

  /**Adds a table which supports selection of some lines.
   * Parameter see {@link #addTable(String, int, int[])}.
   * Additional parameter:
   * @param selectionColumn Column in which the selection is written. 
   * @param selectionText Text which is written in the selection-column cell. 
   *        If the selection alternates, the text is replaced by an empty String.
   *        The String can be changed outside if this parameter refers to a StringBuilder-buffer
   *        and thats content is changed. The currently text will be used. 
   * @return
   * TODO it may be better to use a derived WidgetDescriptorSelection, which contain a method
   *      setSelectionAction(UserActionGui) or maybe using setAction() for a standard action...
   *      The action may receive the line and sends back an information to change.
   *      It should be able to applicate to a tree-leafe too! See {@link WidgetGui_ifc}.
   */
  //WidgetDescriptor addTable(String sName, int height, int[] columnWidths, int selectionColumn, CharSequence selectionText);

  /**Adds a simple text at the current position.
   * 
   * @param sText The text
   * @param size size, 'A' is small ...'E' is large.
   * @param color The color as RGB-value in 3 Byte. 0xffffff is white, 0xff0000 is red.
   * @return
   */
  WidgetDescriptor addText(String sText, char size, int color);
  
  Object addImage(String sName, InputStream imageStream, int height, int width, String sCmd);

  
  /**Adds a line.
   * <br><br>To adding a line is only possible if the current panel is of type 
   * {@link CanvasStorePanelSwt}. This class stores the line coordinates and conditions 
   * and draws it as background if drawing is invoked.
   * 
   * @param colorValue The value for color, 0xffffff is white, 0xff0000 is red.
   * @param xa start of line relative to current position in grid units.
   *          The start is relative to the given position! Not absolute in window! 
   * @param ya start of line relative to current position in grid units.
   * @param xe end of line relative to current position in grid units.
   * @param ye end of line relative to current position in grid units.
   */
  void addLine(int colorValue, float xa, float ya, float xe, float ye);
    
  
  /** Adds a field for editing or showing a text. This text can be prepared especially as number value too.
   * The field has one line. The number of chars are not limited. 
   * <br><br>
   * The current content of the edit field is able to get any time calling {@link GuiPanelMngWorkingIfc#getValue(String)}
   * with the given registering name.
   * <br><br>
   * To force a set of content or an action while getting focus of this field the method {@link #addActionFocused(String, UserActionGui, String)}
   * can be called after invoking this method (any time, able to change). The {@link UserActionGui#userActionGui(String, String, WidgetDescriptor, Map)}
   * is called in the GUI-thread before the field gets the focus.
   * <br><br>
   * To force a check of content or an action while finish editing the method {@link #addActionFocusRelease(String, UserActionGui, String)}
   * can be called after invoking this method (any time, able to change). The adequate userActionGui is called after editing the field.
   * <br><br>
   * If the {@link WidgetDescriptor#action} refers an instance of type {@link UserActionGui}, than it is the action on finish editing.
   * 
   * @param sName The registering name
   * @param widgetInfo The informations about the textfield.
   * @param editable true than edit-able, false to show content 
   * @param prompt If not null, than a description label is shown
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return
   * @deprecated
   */
  Object addTextField(WidgetDescriptor widgetInfo, boolean editable, String prompt, char promptStylePosition);
  
  /**Adds a text field at the current position.
   * @param name The registering name to get the value from outside or set the content.
   * @param editable true then an input field, false only for show
   * @param prompt If not null, then this prompt text is shown above, left, rigth
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return The WidgetDescriptor. An action, tooltip, color etc. can be set there later.
   */
  WidgetDescriptor addTextField(String name, boolean editable, String prompt, char promptStylePosition);
  
  /** Adds a box for editing or showing a text.
   * <br><br>
   * The current content of the edit field is able to get anytime calling {@link GuiPanelMngWorkingIfc#getValue(String)}
   * with the given registering name.
   * <br><br>
   * To force a set of content or an action while getting focus of this field the method {@link #addActionFocused(String, UserActionGui, String)}
   * can be called after invoking this method (any time, able to change). The {@link UserActionGui#userActionGui(String, String, WidgetDescriptor, Map)}
   * is called in the GUI-thread before the field gets the focus.
   * <br><br>
   * To force a check of content or an action while finish editing the method {@link #addActionFocusRelease(String, UserActionGui, String)}
   * can be called after invoking this method (any time, able to change). The adequate userActionGui is called after editing the field.
   * <br><br>
   * If the {@link WidgetDescriptor#action} refers an instance of type {@link UserActionGui}, than it is the action on finish editing.
   * 
   * @param sName The registering name
   * @param widgetInfo The informations about the textfield.
   * @param editable true than edit-able, false to show content 
   * @param prompt If not null, than a description label is shown
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return
   */
  Object addTextBox(WidgetDescriptor widgetInfo, boolean editable, String prompt, char promptStylePosition);
  
  /**Adds a curve view for displaying values with ordinary x-coordinate.
   * The scaling of the curve view is set to -100..100 per default. 
   * @param sName Its registered name
   * @param dyGrid height in grid-units
   * @param dxGrid width in grid-units
   * @param nrofXvalues depth of the buffer for x-values. It should be 6..20 times of dx.
   * @param nrofTracks number of curves (tracks).
   * @return The Canvas Object.
   */
  Object addCurveViewY(String sName, int nrofXvalues, int nrofTracks);
  
  /**Adds a special text field to select a file. On the right side a small button [<] is arranged
   * to open the standard file select dialog. 
   * The text field is a receiver of file objects for drag % drop or paste the clipboard. 
   * @param name Name of the widget
   * @param listRecentFiles maybe null, a list which stores and offers selected files.
   * @param defaultDir The start directory on open the dialog.
   * @param startDirMask The start dir and selection mask. Both are separated with a ':' character
   *        in this string. See {@link FileDialogIfc}. 
   *        If the last or only one char is '/' then a directory should be selected.
   *        For example "D:/MyDir:*.txt" shows only .txt-files to select in the dialog starting from d:/MyDir. 
   * @param prompt Prompt for the text field.
   * @param promptStylePosition
   * @return
   */
  WidgetDescriptor addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, char promptStylePosition);
  
  Object addMouseButtonAction(String sName, UserActionGui action, String sCmdPress, String sCmdRelease, String sCmdDoubleClick);

  /**Adds the given Focus action to the named widget.
   * @param sName The name of the widget. It should be registered calling any add... method.
   * @param action
   * @param sCmdEnter
   * @param sCmdRelease
   * @return
   */
  WidgetDescriptor addFocusAction(String sName, UserActionGui action, String sCmdEnter, String sCmdRelease);

  /**Adds the given Focus action to the known widget.
   * @param widgetInfo
   * @param action
   * @param sCmdEnter
   * @param sCmdRelease
   */
  void addFocusAction(WidgetDescriptor widgetInfo, UserActionGui action, String sCmdEnter, String sCmdRelease);

  
  /**Sets the values for a line
   * @param sName The registered name
   * @param yNull array of values for all tracks in percent from 0..100.0, where its 0-line is shown.
   * @param yOffset This value will be subtract from the input values before scale. 
   *         It is the 0-line-reference of the input values. 
   * @param yScale This value is that value, which is shown in a 10%-difference in the output window.
   */
  void setLineCurveView(String sNameView, int trackNr, String sNameLine, String sVariable, int colorValue, int style, int y0Line, float yScale, float yOffset);
  
  
  /**Sets the appearance of the graphic respectively color and grid.
   * @param sName The registered name
   * @param backgroundColor The color of the background
   * @param colorLines Array of color values for the lines. Any value should be given 
   *        as red-green-blue value in 24 bit. Additionally 
   *        <ul>
   *        <li>bit24,25, is the thickness of the line.
   *        </ul> 
   * @param grid character to determine how the grid is shown. '0' no grid, 'a'..'f' weak to strong.
   * 
   */
  void setColorGridCurveViewY(String sName, int backgroundColor, int[] colorLines, char grid);
  
	/**Gets the value to the named color. It is a method of the graphic.
	 * @param sName supported: red, green, blue, yellow
	 * @return 3 bytes intensity: bit23..16 blue, bit15..8: green, bit 7..0 red. 
	 */
	int getColorValue(String sName);
	

  /**Forces a newly paint of the GUI-container. 
   * This method should only be called in the graphic thread (SWT).*/
  void repaint();
  
  /**Registered a user action for a button. The register of the action should be done
   * before it is used.
   * @param name Name of the action
   * @param action what to do.
   */
  void registerUserAction(String name, UserActionGui action);
  
  UserActionGui getRegisteredUserAction(String name);
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set< Map.Entry <String, WidgetDescriptor>> getShowFields();

  
  /**The GUI-change-listener should be called in the dispatch-loop of the GUI-(SWT)-Thread.
   * @return The instance to call run(). 
   * Hint: run() returns after checking orders and should be called any time in the loop. 
   */
  public GuiDispatchCallbackWorker getTheGuiChangeWorker();
  
  
  /**Creates a box inside the current panel to hold some widgets.
   * 
   * @return
   * @since 2010-05-01
   */
  GuiPanelMngBuildIfc createCompositeBox();
  
  
  WidgetCmpnifc createGridPanel(ColorGui backGround, int xG, int yG, int xS, int yS);
  
  /**Removes a composite box from the graphic representation.
   * @param compositeBox
   * @return true if removed.
   */
  boolean remove(GuiPanelMngBuildIfc compositeBox);
  
  boolean remove(WidgetDescriptor widget);
  
	/**Creates a new window additional to a given window with Panel Manager.
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param variableContainer A Container for variables
	 * @return
	 * @deprecated
	 */
	GuiShellMngBuildIfc createWindow(int left, int top, int width, int height, VariableContainer_ifc variableContainer);
	
	/**Creates a Window for a modal or non modal dialog. The window is described by the returned interface. 
	 * It can be filled with elements. The dialog is able to show and hide calling 
	 * {@link GuiShellMngIfc#setWindowVisible(boolean)}. The interface therefore can be get calling
	 * {@link GuiShellMngBuildIfc#getShellMngIfc()}.
	 * The position and size of the window is set with the adequate strategy like all other widget: 
	 * using {@link #setPosition(int, int, int, int, char)}. 
	 * @param title Title of the window, may be null, then without title bar.
	 * @param exclusive true then non-modal.
	 * @return
	 */
	GuiShellMngBuildIfc createWindow(String title, boolean exclusive);
  
	
  GuiWindowMng_ifc createInfoBox(String title, String[] lines, boolean todo);

  /**Sets the builder for content configuration.
   * @param cfgBuilder
   */
  void buildCfg(GuiCfgData data, File fileCfg);
	
  /**Sets or resets the design mode. The design mode allows to change the content.
   * @param mode
   */
  void setDesignMode(boolean mode);
  
  /**Saves the given configuration.
   * @param dest
   * @return
   */
  String saveCfg(Writer dest);
  
  /**Creates a file or directory dialog. The dialog can be activated (showed) any time. 
   * The dialog is showed in a own window, maybe modal or not.
   * @return Interface to deal with the dialog.
   */
	FileDialogIfc createFileDialog(String sTitle, int mode);
	
	GuiShellMngIfc getShellMngIfc();

}
