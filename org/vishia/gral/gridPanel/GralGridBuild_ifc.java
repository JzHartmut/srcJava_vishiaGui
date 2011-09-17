package org.vishia.gral.gridPanel;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.cfg.GuiCfgBuilder;
import org.vishia.gral.cfg.GuiCfgData;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GuiShellMngIfc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;



/**This is a unique interface for the GUI-panel-manager to build its content.
 * To work with the graphical application see {@link GralPanelMngWorking_ifc}. 
 * <br><br>
 * Any widget is represented by a {@link GralWidget}. Either the WidgetDescriptor
 * should be created before, and taken as parameter for the widget-creating method,
 * or the WidgetDescriptor is returned by the widget-creating method. The second form takes
 * most of the characteristics as parameters for the creating method.
 * <br><br>
 * The platform-specific Widget Object (Swing: javax.swing.JComponent, org.eclipse.swt.widgets.Control etc.)
 * is stored as Object-reference in the {@link GralWidget#widget}.   
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
 * <li>Create a panel manager which is typeof {@link GralGridMngBase} or this interface.
 *   For example create {@link org.vishia.mainGuiSwt.GuiPanelMngSwt}.
 * <li>Create a panel, for example call {@link #createGridPanel(GralColor, int, int, int, int)}
 *   and add the panel to the given     
 * <li>Before add, you can select any given panel by String-identifier, using {@link #selectPanel(String)}.
 * <li>Before add, you have to determine the position and size using 
 *   <ul><li>{@link #setPositionSize(int, int, int, int, char)} in grid units
 *     <li>{@link #setFinePositionSize(int, int, int, int, int, int, int, int, char)} 
 *     <li>{@link #setNextPositionX()}
 *   </ul>   
 * <li><b>addMethods</b>: This methods create the widget in the current selected panel.
 * </ul>
 * <br><br>
 * <b>Concept of positioning</b>: see method {@link #setPosition(float, float, float, float, char, int)}.
 * <br><br>
 *  
 * @author Hartmut Schorrig
 *
 */
public interface GralGridBuild_ifc 
{
  
  /**The version of this interface:
   * <ul>
   * <li>2011-06-18 Hartmut chg: createFileDialog() improved, 
   *     new addFileSelectField(): A Field that comprises the possibility to open a file select dialog
   *     and that are a destination for drop or paste a file mime type from clipboard. 
   * <li>2011-05-01 Hartmut new: {@link #addTextBox(GralWidget, boolean, String, char)}: 
   *     A Text box with more as one line. The TextField has only one line.
   * <li>2011-05-01 Hartmut new: {@link #createCompositeBox()}. It is a box with its own PanelMng
   *     which is located in an area of another panel. (Composite)
   * <li>2011-05-01 Hartmut new: {@link #remove(GralGridBuild_ifc)} and {@link #remove(GralWidget)}
   *     to remove widgets, for dynamic views.
   * <li>2011-05-01 Hartmut new: {@link #createWindow(String, boolean)} instead createModalWindow(String).
   *     This method should be used for any sub-windows in the application. The window position is determined
   *     inside the current window with the known {@link #setPositionSize(int, int, int, int, char)} functionality.
   *     The {@link #createWindow(int, int, int, int, VariableContainer_ifc)} with absolute coordinates
   *     may be deprecated. (Is it necessary to create a window outside the own borders? )             
   * <li>All other changes in 2010
   * </ul>
   */
  final static int version = 0x20110502;

  /**
   * 
   */
  public static final int propZoomedPanel = 0x0001;
  
  public static final int propGridZoomedPanel = 0x0002;
  
  

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
  public void registerPanel(GralPanelContent panel);
  
  
  /**Creates a panel for tabs and registers it in the GUI.
   * @param user If not null, then this user class will be notified when a tab is selected.
   *             The user should update showed values.
   * @param properties use or of constants {@link #propZoomedPanel}, {@link #propGridZoomedPanel}
   * @return The Tab-container, there the tabs can be registered.
   */
  GralTabbedPanel createTabPanel(String namePanel, GralPanelActivated_ifc user, int properties);
  
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
  
  
  
  /**Sets the position and size for the next widget to add in the container.
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
   * @deprecated. Use {@link #setPosition(float, float, float, float, char, int)}.                
   */
  public void setPositionSize(int line, int column, int height, int length, char direction);
  
  
  /**Sets the position.
   * <b>Concept of positioning</b>:<br>
   * The position are never given as pixel positions. They are user-oriented positions. The calculation
   * of pixel units are done from the implementing graphic layer depending on the graphic device properties
   * and the requested size appearance.
   * <br><br>
   * A normal text with a font in the standard proper read-able size is presented by 2 units of the Gral-positions
   * in vertical direction (lines) and approximately 1 position per character in horizontal direction.
   * Of course the horizontal character size depends on the font properties, it is only a standard value.
   * A text can be presented in a smaller font. A very small font is presented by 1 vertical gral-unit.
   * Such a text can be used as short title for text input fields (prompt) or adequate.
   * <br>
   * A button is able to present with 3 or 2 vertical gral units. A small check box may be presented 
   * with 1 x 1 gral unit.
   * <br><br>
   * A gral unit should be have the same distance in vertical as in horizontal direction. It depends on the 
   * graphical implementation. One gral unit may have approximately 6 to 30 pixel, 
   * depending from the requested size of appearance and the display pixel size. Any graphic can be shown
   * with several sizes of appearance, depending from parameters.
   * <br><br>
   * <b>Fine positions</b>:
   * Either the positions are given with 2 integer values or with a float value. The fine position is given
   * as one digit from 0 to 9. It is one fractional part digit of the float or the frac int argument.
   * The fine position divides one gral position into 5 or into 6 fine positions. 
   * The odd numbers divide into 6 positions. In this kind a gral position is able to divide by 2, 3 and 6:
   * <ul>
   * <li>1: 1/6 = 0.1333
   * <li>3: 1/3 = 0.3333
   * <li>5: 1/2 = 0.5
   * <li>7: 2/3 = 0.6667
   * <li>9: 5/6 = 0.8667
   * </ul>
   * The even numbers divide into 5 positions: 
   * <ul>
   * <li>2: 1/5 = 0.2
   * <li>4: 2/5 = 0.4
   * <li>6: 3/5 = 0.6
   * <li>8: 4/5 = 0.8
   * </ul>
   * The fine positioning enables a fine positioning of widgets in respect to the fundamental positions.
   * <br><br>
   * Positions may be given in the following forms: <ul>
   * <li>Positive number in range 0...about 100..200: Gral Unit from left or top.
   * <li>Negative number in range 0 or -1...about -200..-200: Gral Unit from right or bottom.
   * <li>0 for lineEnd or columnEnd means the right or bottom. 
   * <li>Positive Number added with {@link GralGridPos#size} applied at lineEnd or columnEnd: 
   *   The size. In this case the line and column is the left top corner. 
   *   All further related positions has the same left or top line.
   * <li>Negative Number added with {@link GralGridPos#size} applied at lineEnd or columnEnd: 
   *   The absolute value is the size. It is negative because the size is measured from right to left
   *   respectively bottom to top. It means the line and column is the right or the bottom line. 
   *   All further related positions has the same right or bottom line. Especially the bottom line is used
   *   usual if more as one widgets are placed in one line after another, with a common bottom line.
   * <li>TODO {@link GralGridPos#same}
   * <li> {@link GralGridPos#next} and {@link GralGridPos#nextBlock}   
   * <li>as width or height or as percent value from the panel size.
   * </ul>
   * Fine positions are given always from left or top of the fundamental positions. 
   * For example a value -1.3 means, the widget is placed 1 unit from right, and then 1/3 inside this unit.
   * This is 2/3 unit from right. A value for example -0.3 is not admissible, because -0 is not defined. 
   * <br>
   * 
   *        
   * @param framePos The given frame.
   * @param line The line
   * @param lineEndOrSize
   * @param column
   * @param columnEndOrSize
   */
  public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , int origin, char direction );
  
  
  /**Sets the position in relation to a given position.
   * @param framePos The given frame.
   * @param line The line
   * @param lineEnd
   * @param column
   * @param columnEnd
   * @deprecated. 
   */
  public void setPosition(GralGridPos framePos, float line, float lineEnd, float column, float columnEnd
      , int origin, char direction);
  
  
  
  /**Same as {@link #setPositionSize(int, int, int, int, char)}, but the positions can be in a fine division.
   * @param y The line. 
   * @param yFrac Number between 0..9 for fine positioning in the grid step.
   * @param yEnd
   * @param yEndFrac Number between 0..9 for fine positioning in the grid step.
   * @param x
   * @param xFrac Number between 0..9 for fine positioning in the grid step.
   * @param xEnd
   * @param xEndFrac Number between 0..9 for fine positioning in the grid step.
   * @param direction Direction of the next position if that is not given than or {@link GralGridPos#next} is given than.
   *        A value other then r, l, u, d let the direction unchanged from previous call.
   * @param origin Origin of inner widgets or next widgets. Use:
   *        <pre>
   *        1    4    7
   *        2    5    8
   *        3    6    9
   *        </pre>
   *        for the origin points. (origin-1) %3 is the horizontal origin, (origin-1 /3) is the vertical one.
   *        A value 0 let the origin unchanged from previous call.
   */
  public void setFinePosition(int y, int yFrac, int yEnd, int yEndFrac
      , int x, int xFrac, int xEnd, int xEndFrac
      , int origin, char direction, GralGridPos frame);
  
  /**Sets the next position if the position is used, but change the size.
   * @param ySize
   * @param ySizeFrac
   * @param xSize
   * @param xSizeFrac
   */
  public void setSize(int ySize, int ySizeFrac, int xSize, int xSizeFrac);

  
  /**Set the position for the next widget especially for widgets, which should be placed relativ in the whole panel. 
   * It is adequate {@link #setPositionSize(int, int, int, int, char)}, but here the size isn't given, instead the end line and column.
   * That approach allows to set positions for widgets, which are placed relative to the actual size of the window.
   * @param line Line in the grid. Fine positions in 1 fractional part digit can be used. Negative value counts from bottom.
   * @param lineEnd The line position after the widget. Fine positions in 1 fractional part digit can be used. Negative value counts from bottom.
   * @param column Column in the grid. Fine positions in 1 fractional part digit can be used. Negative value counts from rigth.
   * @param columnEnd. Fine positions in 1 fractional part digit can be used. Negative value counts from bottom.
   * @param direction
   */
  public void xxxsetPositionInPanel(float line, float column, float lineEnd, float columnEnd, char direction);
  
  
  
  GralGridPos getPositionInPanel();
  
  /**Positions the next widget right to the previous one. */
  void setNextPositionX();

  
  
  
  
  
  /**Adds a button
   * @param sButtonText text in the button
   * @param height in grid-units
   * @param width in grid-unigs
   * @param sCmd The command string will be transfered to the action-method
   * @param sUserAction The user action shoult be registered before 
   *         calling {@link #registerUserAction(String, GralUserAction)}
   * @param sName
   * @return
   */
  //Object addButton(String sButtonText, int height, int width, String sCmd, String sUserAction, String sName);
  public GralWidget addButton(
  	String sName
  , GralUserAction action
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
  public GralWidget addSwitchButton(
  	String sName
  , GralUserAction action
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
  GralWidget addLed(
  	String sName
  , String sShowMethod
  , String sDataPath
  );
  
  GralWidget addValueBar(
  	String sName
  , String sShowMethod
  , String sDataPath
  );
  
  
  GralWidget addSlider(
  	String sName
  , GralUserAction action
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
  GralWidget addTable(String sName, int height, int[] columnWidths);

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
  GralWidget addText(String sText, char size, int color);
 
  
  /**Adds a simple text at the current position.
   * @param sText
   * @param origin Origin, use char 1..9 for 1 top-left, 2 top-middle, ... 5 middle, 9 bottom-right.  
   * @param textColor
   * @param BackColor
   * @return
   */
  GralWidget addText(String sText, int origin, GralColor textColor, GralColor BackColor);
  
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
   * The current content of the edit field is able to get any time calling {@link GralPanelMngWorking_ifc#getValue(String)}
   * with the given registering name.
   * <br><br>
   * To force a set of content or an action while getting focus of this field the method {@link #addActionFocused(String, GralUserAction, String)}
   * can be called after invoking this method (any time, able to change). The {@link GralUserAction#userActionGui(String, String, GralWidget, Map)}
   * is called in the GUI-thread before the field gets the focus.
   * <br><br>
   * To force a check of content or an action while finish editing the method {@link #addActionFocusRelease(String, GralUserAction, String)}
   * can be called after invoking this method (any time, able to change). The adequate userActionGui is called after editing the field.
   * <br><br>
   * If the {@link GralWidget#action} refers an instance of type {@link GralUserAction}, than it is the action on finish editing.
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
  Object addTextField(GralWidget widgetInfo, boolean editable, String prompt, char promptStylePosition);
  
  /**Adds a text field at the current position.
   * @param name The registering name to get the value from outside or set the content.
   * @param editable true then an input field, false only for show
   * @param prompt If not null, then this prompt text is shown above, left, rigth
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return The WidgetDescriptor. An action, tooltip, color etc. can be set there later.
   */
  GralWidget addTextField(String name, boolean editable, String prompt, char promptStylePosition);
  
  /** Adds a box for editing or showing a text.
   * <br><br>
   * The current content of the edit field is able to get anytime calling {@link GralPanelMngWorking_ifc#getValue(String)}
   * with the given registering name.
   * <br><br>
   * To force a set of content or an action while getting focus of this field the method {@link #addActionFocused(String, GralUserAction, String)}
   * can be called after invoking this method (any time, able to change). The {@link GralUserAction#userActionGui(String, String, GralWidget, Map)}
   * is called in the GUI-thread before the field gets the focus.
   * <br><br>
   * To force a check of content or an action while finish editing the method {@link #addActionFocusRelease(String, GralUserAction, String)}
   * can be called after invoking this method (any time, able to change). The adequate userActionGui is called after editing the field.
   * <br><br>
   * If the {@link GralWidget#action} refers an instance of type {@link GralUserAction}, than it is the action on finish editing.
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
  Object addTextBox(GralWidget widgetInfo, boolean editable, String prompt, char promptStylePosition);
  
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
   *        in this string. See {@link GralFileDialog_ifc}. 
   *        If the last or only one char is '/' then a directory should be selected.
   *        For example "D:/MyDir:*.txt" shows only .txt-files to select in the dialog starting from d:/MyDir. 
   * @param prompt Prompt for the text field.
   * @param promptStylePosition
   * @return
   */
  GralWidget addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, char promptStylePosition);
  
  Object addMouseButtonAction(String sName, GralUserAction action, String sCmdPress, String sCmdRelease, String sCmdDoubleClick);

  /**Adds the given Focus action to the named widget.
   * @param sName The name of the widget. It should be registered calling any add... method.
   * @param action
   * @param sCmdEnter
   * @param sCmdRelease
   * @return
   */
  GralWidget addFocusAction(String sName, GralUserAction action, String sCmdEnter, String sCmdRelease);

  /**Adds the given Focus action to the known widget.
   * @param widgetInfo
   * @param action
   * @param sCmdEnter
   * @param sCmdRelease
   */
  void addFocusAction(GralWidget widgetInfo, GralUserAction action, String sCmdEnter, String sCmdRelease);

  
  /**Register all widgets, which are created in its own classes, not add here.
   * The widgets are stored in the index of names.
   * @param widgd
   */
  void registerWidget(GralWidget widgd);
  
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
  

  /**Gets the named color. 
   * @param sName supported: red, green, blue, yellow
   */
  GralColor getColor(String sName);
  

  /**Forces a newly paint of the GUI-container. 
   * This method should only be called in the graphic thread (SWT).*/
  void repaint();
  
  /**Registered any user action. A registered user action is used especially for a script build GUI.
   * Action can be given by this register name. 
   * <br><br>
   * The registering of user actions should be done at startup of the application, before the 
   * {@link GuiCfgBuilder#buildGui(org.vishia.msgDispatch.LogMessage, int)} is invoked.
   * The user actions can be called in any specialized context.
   * <br><br>
   * Some user actions can be invoked from the GUI itself:
   * <ul>
   * <li> "KeyAction": Action for all common keys.
   * </ul>
   * @param name Name of the action
   * @param action what to do.
   */
  void registerUserAction(String name, GralUserAction action);
  
  GralUserAction getRegisteredUserAction(String name);
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set< Map.Entry <String, GralWidget>> getShowFields();

  
  /**The GUI-change-listener should be called in the dispatch-loop of the GUI-(SWT)-Thread.
   * @return The instance to call run(). 
   * Hint: run() returns after checking orders and should be called any time in the loop. 
   */
  public GralDispatchCallbackWorker getTheGuiChangeWorker();
  
  
  /**Creates a box inside the current panel to hold some widgets.
   * 
   * @return
   * @since 2010-05-01
   */
  GralGridBuild_ifc createCompositeBox();
  
  
  
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
  GralPanelContent createGridPanel(String namePanel, GralColor backGround, int xG, int yG, int xS, int yS);
  
  
  /**Removes a composite box from the graphic representation.
   * @param compositeBox
   * @return true if removed.
   */
  boolean remove(GralGridBuild_ifc compositeBox);
  
  boolean remove(GralWidget widget);
  
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
	 * {@link GralWindow_ifc#setWindowVisible(boolean)} or 
	 * {@link GralPanelMngWorking_ifc#setWindowsVisible(GralWindow_ifc, GralGridPos)}. 
	 * The position and size of the window is set with the adequate strategy like all other widget: 
	 * using {@link #setPositionSize(int, int, int, int, char)}. 
	 * @param title Title of the window, may be null, then without title bar.
	 * @param exclusive true then non-modal.
	 * @return
	 */
	GralWindow_ifc createWindow(String title, boolean exclusive);
  
	
	GuiShellMngBuildIfc createWindowOld(String title, boolean exclusive);
	
	
	
  GralWindow_ifc createInfoBox(String title, String[] lines, boolean todo);

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
	GralFileDialog_ifc createFileDialog();
	
	GuiShellMngIfc getShellMngIfc();

}
