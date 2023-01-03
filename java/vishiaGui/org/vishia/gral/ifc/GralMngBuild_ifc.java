package org.vishia.gral.ifc;

import java.io.File;
import java.io.InputStream;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.cfg.GralCfgBuilder;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.util.KeyCode;
import org.vishia.util.ReplaceAlias_ifc;
import org.vishia.util.TimedValues;



/**This is a unique interface for the GUI-panel-manager to build its content.
 * To work with the graphical application see {@link GralMng_ifc}. 
 * <br><br>
 * <br><br>
 * For the most
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
 * <li>Create a graphic manager which is typeof {@link GralMng} or this interface.
 *   For example create {@link org.vishia.gral.swt.SwtMng}.
 * <li>Create a panel, for example call {@link #createGridPanel(GralColor, int, int, int, int)}
 *   and add the panel to the given     
 * <li>Before add, you can select any given panel by String-identifier, using {@link #selectPanel(String)}.
 * <li>Before add, you have to determine the position and size using 
 *   <ul><li>{@link #setPositionSize(int, int, int, int, char)} in grid units
 *     <li>{@link #setFinePositionSize(int, int, int, int, int, int, int, int, char)} 
 *     <li>{@link #setNextPositionX()}
 *   </ul>   
 * <li>The call the <b>add method</b>: This methods create the graphic widget in the current selected panel.
 * </ul>
 * <br><br>
 * <b>Concept of positioning</b>: see method {@link #setPosition(float, float, float, float, char, int)}
 * and {@link GralWidget_ifc}.
 * <br><br>
 * <br><br>
 * <b>Concept for widgets</b>: see {@link GralWidget}
 *  
 * @author Hartmut Schorrig
 *
 */
public interface GralMngBuild_ifc 
{
  
  /**The version of this interface:
   * <ul>
   * <li>2016-09-01 Hartmut chg: instead extends {@link ReplaceAlias_ifc} now contains {@link #getReplacerAlias()}.
   *   It is an extra class for a ReplacerAlias given independent of the graphic. 
   * <li>2016-07-20 Hartmut chg: instead setToPanel now {@link #createImplWidget_Gthread()}. It is a better name. 
   * <li>2012-04-01 Hartmut new: {@link #addDataReplace(Map)}, {@link #replaceDataPathPrefix(String)}.
   *   using alias in the {@link GralWidget#setDataPath(String)}. The resolving of the alias is done
   *   only if the datapath is used. 
   * <li>2012-03-10 Hartmut chg: Rename this class to GralMngBuild_ifc
   * <li>2012-03-09 Hartmut new: {@link #addCheckButton(String, String, String, String, GralColor, GralColor, GralColor)}
   * <li>2012-02-11 Hartmut new: {@link #setContextMenu(GralWidget, GralMenu)}. 
   * <li>2011-11-16 Hartmut new: {@link #addText(String)} simple with standards.
   * <li>2011-11-17 Hartmut chg: {@link #createWindow(String, String, int)} now not only exclusive or not
   *   but with some control bits {@link GralWindow#windExclusive} etc.
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


  /**
   * 
   */
  public static final int propZoomedPanel = 0x0001;
  
  public static final int propGridZoomedPanel = 0x0002;
  
  
  
  /**Sets the action for main keys. Main keys are used in an application independent of the focused widget.
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} is invoked on any key down event,
   * exclusive some graphic implementation specific keys. The user should return false in this method
   * if the key is not used and should not be blocked for widget specific key listeners. 
   * @param userKeyAction The user action to process main keys.
   * @return the last set user action.
   */
  GralUserAction setMainKeyAction(GralUserAction userKeyAction);

  ReplaceAlias_ifc getReplacerAlias();

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
  public void registerPanel(GralPanel_ifc panel);
  
  
  /**Adds a panel for tabs as child of the current panel.
   * @param user If not null, then this user class will be notified when a tab is selected.
   *             The user should update showed values.
   * @param properties use or of constants {@link #propZoomedPanel}, {@link #propGridZoomedPanel}
   * @return The tab-container, there the tabs can be registered.
   */
//  GralTabbedPanel addTabbedPanel(String namePanel, GralPanelActivated_ifc user, int properties);
  
  /**selects a registered panel for the next add-operations.
   * see {@link #registerPanel(String, Object)}. 
   */
  GralPanel_ifc selectPanel(String sName);

  /**Selects the given panel as current panel to build some content. */
  void selectPanel(GralPanel_ifc panel);
  
  /**Selects the primary window as current panel to build some content. */
  void selectPrimaryWindow();
  
  
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
   * 
   *        
   * @param framePos The given frame.
   * @param line The line. Positive: from top, negative: from end. 
   * @param lineEndOrSize The position of end of widget. negative or 0: from end. 0: the end position of Panel.
   *   see {@link GralPos#same} etc.
   * @param column
   * @param columnEndOrSize
   */
  public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
    , char direction );
  
  
  /**Sets the position in relation to a given position.
   * @param framePos The given frame.
   * @param line The line
   * @param lineEnd
   * @param column
   * @param columnEnd
   * @deprecated. 
   */
  public void setPosition(GralPos framePos, float line, float lineEnd, float column, float columnEnd
    , char direction);


  public void setPosition(GralPos framePos, float line, float lineEnd, float column, float columnEnd
    , char direction, float border);


  /**Sets the position with fine position given as float value. Only one digit after the float point is regarded,
   * see definition for the fine position as description of {@link GralPos}.
   * @param line either position or combinded with {@link GralPos#refer} etc.
   * @param lineEnd
   * @param column
   * @param columnEnd
   * @param direction
   * @param border
   */
  public void setPosition(float line, float lineEnd, float column, float columnEnd
    , char direction, float border);


  
  /**Same as {@link #setPositionSize(int, int, int, int, char)}, but the positions can be in a fine division.
   * @param y The line. 
   * @param yFrac Number between 0..9 for fine positioning in the grid step.
   * @param yEnd
   * @param yEndFrac Number between 0..9 for fine positioning in the grid step.
   * @param x
   * @param xFrac Number between 0..9 for fine positioning in the grid step.
   * @param xEnd
   * @param xEndFrac Number between 0..9 for fine positioning in the grid step.
   * @param direction Direction of the next position if that is not given than or {@link GralPos#next} is given than.
   *        A value other then r, l, u, d let the direction unchanged from previous call.
   */
  public void setFinePosition(int y, int yFrac, int yEnd, int yEndFrac
      , int x, int xFrac, int xEnd, int xEndFrac
      , char direction, int border, int borderFrac, GralPos frame);
  
  
  
  /**Gets the current position in the panel to store anywhere other. Usual the position is stored in the widget itself.
   * This operation returns an independent instance of GralGridPos. 
   * Note that the {@link GralMng#pos} is reused there. Therefore the implementation of the method
   * returns a cloned instance.   
   * @return An independent instance with current data of {@link GralMng#pos}.
   * @deprecated see {@link GralWidget.ImplAccess}. 
   */
  @Deprecated
  GralPos getPositionInPanel();
  
  
  
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
  public GralButton addButton(
    String sName
  , GralUserAction action
  , String sCmd
  //, String sShowMethod
  , String sDataPath
  , String sButtonText
  );
  
  
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
  public GralButton addButton(
    String sName
  , GralUserAction action
  , String sButtonText
  );
  
  
  //public void add(GralHorizontalSelector<?> sel);

  
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
  public GralButton addSwitchButton(
    String sName
  , GralUserAction action
  , String sCmd
  //, String sShowMethod
  , String sDataPath
  , String sButtonText
  , GralColor colorOff
  , GralColor colorOn
  );
  
  
  
  /**Adds a button which is switching on or off. The state is shown with 2 colors and 2 different texts
   * inside the button. The state is able to retrieve calling {@link GralButton#isOn()} 
   * or {@link GralButton#getValue()}.
   * @param sName
   * @param sButtonText0
   * @param sButtonText1
   * @param color0
   * @param color1
   * @return
   */
  public GralButton addSwitchButton(
    String sName
  , String sButtonTextOff
  , String sButtonTextOn
  , GralColor colorOff
  , GralColor colorOn
  );
  
  
  
  /**Adds a button which is switching on or off. The state is shown with 2 colors and 2 different texts
   * inside the button. The state is able to retrieve calling {@link GralButton#isOn()} 
   * or {@link GralButton#getValue()}.
   * @param sName
   * @param sButtonText0
   * @param sButtonText1
   * @param color0
   * @param color1
   * @return
   */
  public GralButton addCheckButton(
    String sName
  , String sButtonTextOn
  , String sButtonTextOff
  , String sButtonTextDisabled
  , GralColor colorOn
  , GralColor colorOff
  , GralColor colorDisabled
  );
  
  
  
  
  /**Adds a Led (round)
   * @param sName
   * @return
   */
  //Object addButton(String sButtonText, int height, int width, String sCmd, String sUserAction, String sName);
  GralLed addLed(
  	String sName
  //, String sShowMethod
  , String sDataPath
  );
  
  GralValueBar addValueBar(
  	String sName
  //, String sShowMethod
  , String sDataPath
  );
  
  
  GralWidget addSlider(
  	String sName
  , GralUserAction action
  , String sShowMethod
  , String sDataPath
  );
  
  
  /**Adds a table, which is able to scroll.
   * A user action on the GUI with the table invokes the {@link GralWidget#setActionChange(GralUserAction)}
   * with 
   * <ul>
   * <li> with given command "table-key".
   * <li>values[0] is the selected line referenced with {@link GralTableLine_ifc}
   * <li>values[1] is the key code described in {@link KeyCode}
   * </ul> 
   * If the method isn't given or returns false, the central key action given in {@link GralMng#getRegisteredUserAction(String)}
   * for "keyAction" is tried to get and then invoked with cmd = "key" and the key code in values[0].
   * This central keyAction may be used for application centralized keys without association to the table itself.
   * 
   * @param sName register name, used for {@link GuiPanelMngWorkingIfc#insertInfo(String, int, String).}
   * @param height The height in grid units for the appearance
   * @param columnWidths Array with width of the columns. 
   *        Each column has a fix default width per construction.
   *        It may or may not a fix widht, it may able to change by mouse actions,
   *        adequate to the possibilities of the used graphic base system. 
   * @return
   * @deprecated Create an instance of {@link GralTable} and call {@link GralTable#setToPanel(GralMngBuild_ifc)} 
   */
  @Deprecated
  GralTable addTable(String sName, int height, int[] columnWidths);

  //void add(GralTable<?> table);
  
  
  
  /**This routine is called from {@link GralWidget#setToPanel(GralMngBuild_ifc)}.
   * It is not intent to call from the application immediately. Use {@link GralWidget#setToPanel(GralMngBuild_ifc)} instead.
   * The implementation of this routine checks the type of the widget and invokes the proper routine
   * for the implementation graphic in the implementation level of the {@link GralMng}.
   * @param widgg The widget should be initialized already.
   */
  void createImplWidget_Gthread(GralWidget widgg);

  /**Adds a text to the current panel at given position with standard colors, left origin.
   * The size of text is calculated using the height of positioning values.
   * @param text
   */
  GralWidget addText(String sText);
  
  /**Adds a simple text at the current position.
   * 
   * @param sText The text
   * @param size size, 'A' is small ...'E' is large.
   * @param color The color as RGB-value in 3 Byte. 0xffffff is white, 0xff0000 is red.
   * @return
   * @deprecated
   */
  @Deprecated
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
   * {@link XXXSwtCanvasStorePanel}. This class stores the line coordinates and conditions 
   * and draws it as background if drawing is invoked.
   * 
   * @param colorValue The value for color, 0xffffff is white, 0xff0000 is red.
   * @param xa start of line relative to current position in grid units.
   *          The start is relative to the given position! Not absolute in window! 
   * @param ya start of line relative to current position in grid units.
   * @param xe end of line relative to current position in grid units.
   * @param ye end of line relative to current position in grid units.
   * @deprecated it is the old form before a {@link org.vishia.gral.widget.GralCanvasArea} was created. Use that.
   */
  @Deprecated void addLine(int colorValue, float xa, float ya, float xe, float ye);
    
  
  /**Adds a line.
   * <br><br>To adding a line is only possible if the current panel is of type 
   * {@link XXXSwtCanvasStorePanel}. This class stores the line coordinates and conditions 
   * and draws it as background if drawing is invoked.
   * 
   * @param color
   * @param points
   */
  void addLine(GralColor color, List<GralPoint> points);
      
    

    
  
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
  //Object addTextField(GralWidget widgetInfo, boolean editable, String prompt, char promptStylePosition);
  
  /**Adds a text field at the current position.
   * @param name The registering name to get the value from outside or set the content.
   * @param editable true then an input field, false only for show
   * @param prompt If not null, then this prompt text is shown above, left, rigth
   * @param promptStylePosition Position and size of description label:
   *   upper case letter: normal font, lower case letter: small font
   *   'l' left, 't' top (above field) 
   * @return The WidgetDescriptor. An action, tooltip, color etc. can be set there later.
   */
  GralTextField addTextField(String name, boolean editable, String prompt, String promptStylePosition);
  
  /** Adds a box for editing or showing a text.
   * <br><br>
   * The current content of the edit field is able to get anytime calling {@link GralMng_ifc#getValue(String)}
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
  //GralTextBox_ifc 
  GralTextBox addTextBox(String name,/*GralWidget widgetInfo, */boolean editable, String prompt, char promptStylePosition);
  
  /** Adds a box which presents html content.
   * 
   * @param sName The registering name
   * @return The instance arranged in the current panel at current position of the GralWidgetMng
   */
  GralHtmlBox addHtmlBox(String name);
  
  /**Adds a curve view for displaying values with ordinary x-coordinate.
   * The scaling of the curve view is set to -100..100 per default. 
   * @param sName Its registered name
   * @param dyGrid height in grid-units
   * @param dxGrid width in grid-units
   * @param nrofXvalues depth of the buffer for x-values. It should be 6..20 times of dx.
   * @param nrofTracks number of curves (tracks).
   * @return The Canvas Object.
   */
  GralCurveView addCurveViewY(String sPosName, int nrofXvalues, GralCurveView.CommonCurve common, TimedValues tracksValues);
  
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
  GralTextField addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, String promptStylePosition);
  
  
  /**Adds a panel that is not bound yet.
   * @param panel
   */
  //void addPanel(GralPanel_ifc panel);
  
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
  //void setLineCurveView(String sNameView, int trackNr, String sNameLine, String sVariable, int colorValue, int style, int y0Line, float yScale, float yOffset);
  
  
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
  //void setColorGridCurveViewY(String sName, int backgroundColor, int[] colorLines, char grid);
  
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
  
  
  /**Forces a newly paint of the current panel. 
   * This method should only be called in the graphic thread. It should be used after changing content. */
  void repaintCurrentPanel();
  
  /**Registered any user action. A registered user action is used especially for a script build GUI.
   * Action can be given by this register name. 
   * <br><br>
   * The registering of user actions should be done at startup of the application, before the 
   * {@link GralCfgBuilder#buildGui(org.vishia.msgDispatch.LogMessage, int)} is invoked.
   * The user actions can be called in any specialized context.
   * <br><br>
   * @param name Name of the action
   * @param action what to do.
   */
  void registerUserAction(String name, GralUserAction action);
  
  GralUserAction getRegisteredUserAction(String name);
  
  
  /**Returns a Set of all fields, which are created to show.
   * @return the set, never null, possible an empty set.
   */
  public Set< Map.Entry <String, GralWidget>> getShowFields();

  
  
  /**Creates a box inside the current panel to hold some widgets.
   * 
   * @return
   * @since 2010-05-01
   */
  GralPanelContent createCompositeBox(String name);
  
  
  
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
  
  
  /**Creates an empty context menu.
   * @return
   */
  //GralMenu createContextMenu(String name, GralWidget widg);
  
  /**Removes a composite box from the graphic representation.
   * @param compositeBox
   * @return true if removed.
   */
  boolean remove(GralPanel_ifc compositeBox);
  
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
	//GuiShellMngBuildIfc createWindow(int left, int top, int width, int height, VariableContainer_ifc variableContainer);
	
	/**Creates a Window for a modal or non modal dialog. The window is described by the returned interface. 
	 * It can be filled with elements. The dialog is able to show and hide calling 
	 * {@link GralWindow_ifc#setWindowVisible(boolean)} or 
	 * {@link GralMng_ifc#setWindowsVisible(GralWindow_ifc, GralPos)}. 
	 * The position and size of the window is set with the adequate strategy like all other widget: 
	 * using {@link #setPositionSize(int, int, int, int, char)}. 
	 * @param title Title of the window, may be null, then without title bar.
	 * @param windProps Or of the static variables {@link GralWindow#windExclusive} etc. 
	 * @return
	 * @xxdeprecated use {@link GralWindow#GralWindow(String, String, String, int)} and then {@link GralWidget#createImplWidget_Gthread()}
	 *   with this window.
	 */
	GralWindow createWindow(String name, String title, int windProps);
  
	
  GralInfoBox createTextInfoBox(String name, String title);

  GralInfoBox createHtmlInfoBox(String posName, String title, boolean onTop);

  /**Sets the builder for content configuration.
   * @param cfgBuilder
   */
  void buildCfg(GralCfgData data, File fileCfg);
	
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
	
	//GuiShellMngIfc getShellMngIfc();

}
