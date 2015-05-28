package org.vishia.gral.base;

import java.lang.reflect.Array;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.fileRemote.FileMark;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Assert;
import org.vishia.util.Docu_UML_simpleNotation;
import org.vishia.util.IterableIterator;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;
import org.vishia.util.SelectMask;
import org.vishia.util.MarkMask_ifc;
import org.vishia.util.TreeNodeBase;
import org.vishia.util.TreeNode_ifc;


/**This is the Gral class for a table. Its usage is independent of a graphical implementation layer.
 * A table consists of some text fields which are arranged in columns and lines. The lines can be associated
 * to any user data.
 * <br><br>
 * <b>Data, text and graphic architecture of the table</b>:<br>
 * All data which are managed in this table are contained in an Tree node {@link #rootLine} with a unlimited size. 
 * That container references instances of {@link TableLineData}. 
 * Each of that table line contains the texts which are displayed in the cells,
 * the color etc., a key for searching and a reference to any user data. In that kind a table is a container class
 * with a graphical touch.
 * <br>
 * For presenting the table in a graphic implementation some text fields are arranged in a line and column structure
 * in any panel or canvas. The number of such fields for the lines is limited, only the maximal number of viewable 
 * lines should be present (about 20 to 50), independent of the length of the table. 
 * If any part of the table will be shown, that fields
 * will be filled with the texts from the {@link TableLineData#cellTexts} of the corresponding line.
 * If the table content will be shifted in the visible area, the text content of the fields are replaced. 
 * <br>
 * Each text field refers an instance of {@link CellData} as its
 * 'user data'. The CellData refers via {@link CellData#tableItem} the line.
 * <br>
 * <pre>
 *   Graphic representation     GralTable        {@link TableLineData}
 *             |                   |<--------------<#>|           
 *    |<*------+                   |                  |<>--userData--->User's data                    
 *   TextField                     |---rootLine-----*>|
 *    |<>------>CellData           |---lineSelected-->|
 *    |            |-tableItem----------------------->|
 *                                                    |
 * 
 * </pre>
 * UML presentation, see {@link Docu_UML_simpleNotation}.
 * <br><br>
 * <b>Key handling in a table</b>: <br>
 * The table has its own {@link GraphicImplAccess#processKeys(int)} method which is called 
 * from the graphical implementation layer in an key event handler.
 * This method processes some keys for navigation and selection in the table. If the key is not
 * used for that, first the action of {@link GralWidget#setActionChange(GralUserAction)}
 * will be invoked with the key. If that method returns false,
 * the key will be offer the {@link GralMng#userMainKeyAction} for a global key usage.
 * <br>
 * Especially Mouse double click etc. can be handled in the change action.
 * 
 * <br><br>
 * <b>Focus handling in a table</b>: <br>
 * The focus events of the graphic representation layer are not used:
 * <ul>
 * <li>If a cell gets the focus by mouse click, whereby another cell of the table has lost the focus before,
 *   the focus state of the table is not changed. Only the {@link #specifyActionOnLineSelected(GralUserAction)}
 *   is called.
 * <li>If a cell gets the focus by mouse click, whereby the table has not the focus before,
 *   the {@link GralWidget#setActionFocused(GralUserAction)} is called from the focus action of the presentation graphic layer.
 * <li>If {@link #setFocus()} is called from outside, the selected cell is marked with 
 *   {@link GraphicImplAccess.CellData#bSetFocus}. The following repaint sets the focus for that cell.  
 * </ul>
 * 
 * 
 * @author Hartmut Schorrig
 *
 */
@SuppressWarnings("synthetic-access") public final class GralTable<UserData> extends GralWidget implements GralTable_ifc<UserData> {

  /**Version, history and license.
   * <ul>
   * <li>2014-12-26 Hartmut chg: {@link #colorBackSelectSomeMarked} etc.  
   * <li>2013-12-23 Hartmut chg: Rename {@link #setColorBackSelectedLine(GralColor)} instead setColorCurrLine(): 
   *   It is not the current line which is changed but the color setting for all current (= selected) lines. 
   * <li>2013-12-23 Hartmut chg: {@link #checkAndUpdateText(String, CellData)} supports update text on edit cells.
   * <li>2013-12-18 Hartmut chg: {@link CellData} is defined now public and as part of GralTable. There were a problem
   *   using the Java7-Compiler with difficult Generic parameter mismatches. The CellData is a simple data class without
   *   generic, but the compiler has interpreted a generic parameter. May the compiler wrong? The structure of a program
   *   should be simple! The only one disadvantage is, {@link CellData} has to be public, not protected,
   *   because it is not seen from the implementation class of {@link GraphicImplAccess}. Another problem was
   *   {@link GraphicImplAccess#mouseDownGral()} etc. The compiler has a problem it was named 'mouseDown'
   *   and 'super.mouseDown(ev)' was called from the derivated class. Keep it simple. It does not need the same name. 
   * <li>2013-12-06 Hartmut new: {@link #setColumnEditable(int, boolean)}, supports editing in cells. 
   * <li>2013-11-24 Hartmut chg: {@link GralTable.GraphicImplAccess#focusGained()} etc. refactored. 
   * <li>2013-11-23 Hartmut chg: use {@link KeyCode#userSelect} etc. for calling {@link #actionOnLineSelected(int, GralTableLine_ifc)}
   * <li>2013-11-16 Hartmut chg: setCurrentCell(int, int) removed because the line number without respect to a line
   *   is not able to handle. Only a line is given. New method {@link #setCurrentLine(GralTableLine_ifc, int, int)}
   *   can set the given line to any location in the visible area of table.  
   * <li>2013-11-15 Hartmut new: {@link TableLineData} and {@link GraphicImplAccess} are non-static yet
   *   because the generic type should be the same without additional effort. This is the reason.
   *   Nevertheless the non-static property was given already, but deployed by the outer aggregation 
   *   of the static one.
   * <li>2013-10-03 Hartmut new: {@link TableLineData} now extends {@link TreeNodeBase}. It is a tree.
   * <li>2013-10-03 Hartmut new: {@link GraphicImplAccess#cells}, the {@link GraphicImplAccess.CellData} are able to access
   *   elsewhere only in the implementation layer via the text widget.
   * <li>2013-11-02 Hartmut chg: {@link GralTable.TableLineData} is static now with outer reference, more transparent
   * <li>2013-11-02 Hartmut chg: {@link GralTable.GraphicImplAccess#resizeTable(GralRectangle)} moved from SWT implementation,
   *   preparing tree view. {@link GralTable.TableLineData#treeDepth}, {@link GralTable.TableLineData#childLines} for tree view.
   * <li>2013-11-02 Hartmut chg: {@link GralTable.TableLineData} is static now with outer reference, more transparent
   * <li>2013-10-06 Hartmut chg: call {@link #actionOnLineSelected(int, GralTableLine_ifc)} only if the line is changed, 
   *   not on focus without changed line.
   * <li>2013-09-15 Hartmut new: Implementation of {@link #setBackColor(GralColor, int)}  
   *   with special comments for usage of the int parameter.
   *   See {@link GralTable_ifc#setBackColor(GralColor, int)}, Adequate {@link #getBackColor(int)}. 
   * <li>2013-09-14 Hartmut chg: {@link GraphicImplAccess} implements now {@link GralWidgImpl_ifc} without any other
   *   changes (was proper) and sets {@link GralWidget#_wdgImpl}. Therefore all routines which works from the
   *   GralWidget calls the methods of the implement of the GralTable immediately without special overridden methods
   *   in this class. It is the concept.
   * <li>2013-09-14 Hartmut chg: uses the {@link GralWidget.DynamicData#backColor} etc.
   * <li>2013-08-11 Hartmut chg: {@link #getMarkedLines()} now checkes the {@link TableLineData#userData}
   *   whether that is instanceof {@link MarkMask_ifc}. If true then uses its selection state
   *   instead the selection state of the {@link TableLineData}.
   * <li>2013-06-29 Hartmut chg: refactoring. Now a GralTable<generic> can be created before the graphic is build. It is the new schema of GralWidget.
   *   The inner class {@link GraphicImplAccess} is provided as super class for the graphic implementation class,
   *   for example {@link org.vishia.gral.swt.SwtTable}.
   * <li>2013-06-12 Hartmut chg: On right mouse pressing the current line will be selected, because
   *   the context menu should have an association to that line.
   * <li>2013-06-12 Hartmut chg: A marked line is now designated with a background color. 
   *   A selected marked line has another background than a selected non marked line (to see it).
   * <li>2013-06-11 Hartmut new: Now the {@link GralTable}, the {@link GralTable.TableLineData} and this
   *   interface are marked with the generic type UserData.
   * <li>2013-05-22 Hartmut new: {@link TableLineData#lineInGraphic} not used yet, but for further usage.
   *   Maybe for refresh data only if they are in the visible range. 2013-10-03 removed, too many effort on tree view.
   *   To detect visible lines iterate over {@link GraphicImplAccess#cells}
   * <li>2013-05-11 Hartmut chg: {@link #deleteLine(GralTableLine_ifc)} was not ready, now tested
   * <li>2013-05-11 Hartmut chg: {@link TableLineData} instead TableItemWidget.
   * <li>2013-04-28 Hartmut new: {@link #specifyKeysMarkUpDn(int, int)}
   * <li>2013-04-28 Hartmut new: {@link #specifyActionOnLineMarked(MarkMask_ifc)}
   * <li>2013-04-28 Hartmut renamed: {@link #actionOnLineSelected(int, GralTableLine_ifc)}
   * <li>2013-04-21 Hartmut chg: {@link #processKeys(int)}: input of a text key starts searching for a line with this key
   *   immediately, better handling for usage.
   * <li>2012-08-22 Hartmut new {@link #setCurrentLine(int)} with int, it isn't new because it was able to set with
   *   {@link #setCurrentCell(int, int)} with -1 as second parameter.
   * <li>2012-07-15 Hartmut new: search functionality: The implementation should/may have a text field 
   *   which shows the search string. While up and down keys the that lines are selected which text in the {@link #ixColumn}
   *   starts with the search string.
   * <li>2012-03-09 Hartmut bugfix: The {@link #idxLine} was not cleared if the table was cleared.
   * <li>2012-02-19 Hartmut new: mouseWheel and double click
   * <li>2012-01-30 Hartmut new: {@link #setColorCurrLine(GralColor)}
   * <li>2012-01-15 Hartmut new: {@link #setCurrentLine(String)}, {@link #insertLine(String, int, String[], Object)}:
   *    the key is supported now. 
   * <li>2012-01-06 Hartmut new: concept of a table which is independent of the table implementation 
   *   of the table implementations in the graphic system layer or in operation system: 
   *   The capability  of a SWT-table is not sufficient, for example the color of the selection bar
   *   is not able to change. Other reason: Implementation of table in SWT, AWT, Swing is different.
   *   It seems better to have one table concept with independent features, which based on simple widgets.
   *   is not  
   * <li>2011-12-30 Hartmut chg {@link #procStandardKeys(int, GralTableLine_ifc, int)} returns true if standard keys are used. 
   * <li>2011-11-27 Hartmut new {@link #actionOnLineSelected(int, GralTableLine_ifc)}: The user action is called
   *   anytime if a line is selected by user operation. It can be show any associated content anywhere
   *   additionally. It is used for example in "The.file.Commander" to show date, time and maybe content 
   *   while the user selects any files. The graphical implementation should be call {@link #actionOnLineSelected(int, GralTableLine_ifc)}
   *   in its Selection listener. 
   * <li>2011-11-20 Hartmut new The capability of selection of lines is moved from the 
   *   {@link org.vishia.gral.widget.GralSelectList} to this class. It means any table has the capability
   *   of selection of multiple lines. This capability is supported with a extension of the
   *   {@link GralTableLine_ifc} with {@link org.vishia.util.MarkMask_ifc}. The selection of a line
   *   is notificated in the users data which are associated with the table line, not in the graphic
   *   representation of the table. This is differenced to SWT Table implementation. The capability
   *   of selection in an SWT table isn't used. The selection is done by default with shift-key up/down
   *   or with mouse selection ctrl-click, shift-click like usual.
   *   The selection is not missed if the up/down keys are pressed furthermore. More as that,
   *   more as one ranges of lines can be selected. That is a better capability than in SWT.
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
   * 
   */
  @SuppressWarnings("hiding")
  protected final static int version = 0x20131206;

  
  protected int keyMarkUp = KeyCode.shift + KeyCode.up, keyMarkDn = KeyCode.shift + KeyCode.dn;
  
  protected int keyOpenChild = KeyCode.right, keyCloseChild = KeyCode.left;
  
  protected String keySeparator = "/";
  
  //protected final Map<String, TableLineData> idxLine = new TreeMap<String, TableLineData>();
  
  /**This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnLineSelected;
  
  /**This action will be called any time when children of a tree node line should be refreshed. 
   * The {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnRefreshChildren;
  
  /**Width of each column in GralUnits. */
  protected int[] columnWidthsGral;
  
  protected boolean[] bColumnEditable;
  
  protected GralMenu[] menuColumns;
  

  /**The colors of each cell. It is set with the color of {@link TableItemWidget#colorBackground} and
   * {@link TableItemWidget#colorBackground} of the currently displayed line.
   */
  //private GralColor[] colorBack, colorText;
  
  
  GraphicImplAccess gi;

  
  /**The currently selected cell. */
  protected int lineSelectedixCell, colSelectedixCellC;
  
  protected int lineSelectedNewixCell;
  
  /**Array of lines which are currently associated to the cells of the implementation layer.
   * It means they are the showing lines. Note that the TableLineData referred with the {@link #rootLine}
   * are all of the lines of the table. They are not associated to any graphic widget any time.
   * Whether they are associated to a graphic widget or not is able to evaluate with this array.
   * The cellLines[0] is the TableLineData of the first visible line in any case etc.
   */
  protected TableLineData[] linesForCell;

  /**The current line. */
  protected TableLineData lineSelected;
  
  
  /**The line which is selected by mouse pressing, for right mouse menu. */
  protected TableLineData lineSelectedNew;
  
  
  /**Number of lines and columns of data. */
  protected int zLine, zColumn;
  
  /**Current number of visible lines in the view. */
  protected int zLineVisible;
  
  protected int zLineVisibleMax;
  
  /**Number of lines and number of the current line. It is -1 if this numbers should be evaluated from {@link #rootLine}
   */
  private int zLineCurr = -1, nLineFirst = -1;
  
  
  protected final StringBuilder searchChars = new StringBuilder(20);
  
  /**Any line of the table has one TableItemWidget, a long table has some more.
   * Contains content, color, selection etc. of the lines with there cells.
   * 
   */
  //protected ArrayList<TableLineData> tableLines = new ArrayList<TableLineData>();
  
  
  /**The root for all lines of the table. It may be a tree. The lines are the nodes of the tree which are shown.
   */
  NodeTableLine rootLine;
  
  
  /**True if a line or a column is marked. */
  //protected boolean[] markedLines, markedColumns;
  
  /**If true then the graphic implementation fields for cells should be filled newly with the text. */
  protected boolean bPrepareVisibleArea;
  
  
  /**Difference of changing ixLine for cells. 0: Assignment not changed.
   * It is used in {@link #redrawTableWithFocusedCell(CellData)}
   */
  protected int XXXdLineForCells;
  
  /**Check the last time of redrawing. */
  protected long timeLastRedraw;
  
  /**If set, then a next key will be processed. It is set to false if a key event is executed
   * and it is set to true in {@link #keyActionDone}. */
  protected boolean keyDone = true;
  
  /**The last key which was pressed  */
  private int lastKey;
  
  /**Number of key repetitions if a key was not used because the redraw was pending yet. */
  private int keyRepetition;
  
  

  
  /**Data of that cell which was pointered while any mouse button is pressed. */
  //protected CellData cellDataOnMousePressed;
  
  /**The color of the current line. It is the selected line */
  protected GralColor colorBackSelect;
  /**The color of that lines which are marked. */
  protected GralColor colorBackMarked;
  /**The color of the current, the selected line if it is marked. */
  protected GralColor colorBackSelectMarked;
  /**The color of that lines which's some children are marked. */
  protected GralColor colorBackSomeMarked;
  /**The color of the current selected line if some children of it are marked. */
  protected GralColor colorBackSelectSomeMarked;
  
  protected GralColor colorBackSelectNew;
  protected GralColor colorBackSelectNewMarked;
  
  protected GralColor colorTextSelect;
  protected GralColor colorTextMarked;
  
  
  protected GralColor colorSelectCharsBack;
  protected GralColor colorSelectChars;

  /**Background of VscrollBar. */
  protected GralColor colorBackVscrollbar;
  
  /**20 nuances of color for the slider of the vertical scrollBar. The nuances depends on the pixel size. */
  protected final GralColor[] colorSliderVscrollbar = new GralColor[10];
  
  
  /**This action will be called if any line is marked. It may be null, see 
   * 
   */
  protected MarkMask_ifc actionMarkOnLine;
  

  /**Constructs a table which should be positioned on {@link #setToPanel(GralMngBuild_ifc)}
   * @param pos String given position in GralPos units.
   * @param name to register for symbolic access
   * @param columnWidths positive value from left: width, negative value from right: width. 
   *   The last column with a positive width is used for sizeable. 
   */
  public GralTable(String name, int[] columnWidths) { this(null, name, columnWidths); }
  
  /**Constructs a table with position
   * @param pos String given position in GralPos units.
   * @param name to registrate
   * @param columnWidths positive value from left: width, negative value from right: width. 
   *   The last column with a positive width is used for sizeable. 
   */
  public GralTable(String pos, String name, int[] columnWidths) {
    super(pos, name, 'L');
    this.columnWidthsGral = columnWidths;
    this.zColumn = columnWidths.length;
    this.bColumnEditable = new boolean[this.zColumn];  //all false.
    
    rootLine = new NodeTableLine("", null);
    rootLine.showChildren = true;
    rootLine.treeDepth = -1; //children of rootline are level 0
    rootLine.zLineUnfolded = 0;  //count added line.
    
    linesForCell = (TableLineData[])Array.newInstance( TableLineData.class, 50 );  
    //Hint: linesForCell = new GralTable<UserData>.TableLineData[50];
    //does not work because TableLineData is generic.
    zLineVisibleMax = linesForCell.length;
    setColors();
  }

  
  
  
  public int nrofLinesVisibleMax(){ return zLineVisibleMax; }
  
  /**Sets an action which is called any time when another line is selected.
   * This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object and the following keys:
   * <ul>
   * <li>{@link KeyCode#removed} if the table was cleard.
   * <li>{@link KeyCode#defaultSelect} if the table was filled and firstly the {@link #lineSelected} is set.
   * <li>{@link KeyCode#userSelect} if the user acts with keyboard or mouse
   * </ul>
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void specifyActionOnLineSelected(GralUserAction action){
    this.actionOnLineSelected = action;
  }
  
  
  public void specifyActionOnLineMarked(MarkMask_ifc action){
    actionMarkOnLine = action;
  }
  
  /**Sets an action which is called any time when another line is selected.
   * This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void specifyActionOnRefreshChildren(GralUserAction action){
    this.actionOnRefreshChildren = action;
  }
  
  
  /**Specifies the keys to mark lines in the table.
   * Without invocation of this method the keys {@link KeyCode#shift} + {@link KeyCode#up}
   * respectively shift+dn are specified.
   * @param up Any code defined in {@link KeyCode} which marks a line and selects the next line after them.
   * @param dn Any code defined in {@link KeyCode} which marks a line and selects the previous line after them.
   */
  public void specifyKeysMarkUpDn(int up, int dn){
    keyMarkUp = up; keyMarkDn = dn;
  }
  
  
  

  
  /**Adds a context menu entry for the given column.
   * It is an abbreviation for {@link #getContextMenuColumn(int)} 
   * and {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}.
   *  
   * @param col The column, see {@link #getContextMenuColumn(int)}
   * @param identArgJbat The name of the entry, see {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}
   * @param sMenuPath same like {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}
   * @param action same like {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}
   *   Note that the {@link GralWidget_ifc}-parameter of the {@link GralUserAction#exec(int, GralWidget_ifc, Object...)}
   *   is given with the whole table, instance of this {@link GralTable}.
   */
  public void addContextMenuEntryGthread(int col, String menuname, String sMenuPath, GralUserAction action){
    GralMenu menu = getContextMenuColumn(col);
    GralWidget menuitem = null; //new GralWidget(menuname, 'M', null);
    menu.addMenuItemGthread(menuitem, menuname, sMenuPath, action);
    //menuitem.setContentInfo(this);  //the table
  }
  

  /**Returns the same context menu for all cells of the designated column. Each cells of the column gets the same context menu.
   * It means the action has not any knowledge about the cell which has used to start the context menu.
   * Only the whole table is associated. The advantage of this in comparison to {@link #getContextMenuColumnCells(int)}
   * is: Some resources are saved, because there is only one menu item instance in the implementation layer
   * of the graphic for all cells.<br>
   * See {@link #getContextMenuColumnCells(int)}. Note that the {@link GralWidget_ifc}-parameter of the 
   *   {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} methods of all added menu entries
   *   are given with the whole table, instance of this {@link GralTable}.
   *
   *  
   * @param col The column of the table, count from 0,1,...
   * @return The menu to add {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}.
   */
  public GralMenu getContextMenuColumn(int col){
    if(menuColumns == null){
      menuColumns = new GralMenu[zColumn];
    }
    if(menuColumns[col] == null){
      menuColumns[col] = gi.createColumnMenu(col); //for all cells of this column
    }
    return menuColumns[col];
  }
  
  
  /**Adds a context menu entries to all cells of the designated column. This method can't be used 
   * if either {@link #getContextMenuColumn(int)} or {@link #addContextMenuEntryGthread(int, String, String, GralUserAction)}
   * are called before. That is because either only one context menu can be added to all cells of a column
   * (saves resources) or internal an extra context menu with the same appearance can be addes to all cells
   * of the column. Using this method creates a context menu and its entry for all cells of the column
   * with an extra menu instance. The reason of the extra instance is: The menu instance associates the cell, 
   * and the cell associates the table line via {@link GralTableLine_ifc}. Because that the 
   * {@link GralWidget_ifc}-parameter of the {@link GralUserAction#exec(int, GralWidget_ifc, Object...)}
   * of the action parameter refers the cell. Use the following template for the action:
   * <pre>
   * </pre>
   *  
   * @param col The column of the table, count from 0,1,...
   * @return The menu to add {@link GralMenu#addMenuItemGthread(String, String, GralUserAction)}.
   */
  public GralMenu getContextMenuColumnCells(int col, String name, String sMenuPath, GralUserAction action){
    return null;
  }
  
  
  public void setColumnWidth(int width, int[] columnWidths){
    columnWidthsGral = columnWidths;
  }
  
  
  /**Sets the specified column to edit-able or not. This routine can be invoked before the graphic implementation
   * is created (before {@link #setToPanel(GralMngBuild_ifc)} or after them in running mode. 
   * 
   * @param column the number, starting by 0. 
   * @param val true then edit-able, false then read only.
   * @throws IndexOutOfBoundsException on faulty column.
   */
  public void setColumnEditable(int column, boolean val){
    bColumnEditable[column] = val;
    if(((GralWidget)this)._wdgImpl !=null){
      dyda.setChanged(GraphicImplAccess.chgEditableColumn);
      repaint(repaintDelay, repaintDelayMax);
    }
  }
  
  @Override public boolean setCurrentLine(String key){
    Object oLine = rootLine.getNode(key, keySeparator);
    if(oLine instanceof GralTableLine_ifc) {
      @SuppressWarnings("unchecked") 
      GralTableLine_ifc<UserData> line = (GralTableLine_ifc<UserData>)oLine;
      setCurrentLine(line, lineSelectedixCell, -1);
      return true;
    } else return false;
  }
  
  //@Override public boolean setCurrentLine(int nLine){ return setCurrentCell(nLine, -1); }

  
  void setColors(){
    colorBackSelect = GralColor.getColor("lam");
    colorBackSelectNew = GralColor.getColor("lbl");
    
    colorBackMarked = GralColor.getColor("prd");
    colorBackSelectMarked = GralColor.getColor("lrd");
    
    colorBackSomeMarked = GralColor.getColor("pma");
    colorBackSelectSomeMarked = GralColor.getColor("lor");
    
    colorBackSelectNewMarked = GralColor.getColor("lpu");
    dyda.backColor = GralColor.getColor("wh");
    dyda.backColorNoFocus = GralColor.getColor("pgr");
    //colorBackSelectNonFocused = GralColor.getColor("am");
    //colorBackMarkedNonFocused = GralColor.getColor("lrd");
    //colorBackTableNonFocused = GralColor.getColor("gr");
    dyda.textColor = GralColor.getColor("bk");
    colorSelectCharsBack = GralColor.getColor("lgr");
    colorSelectChars = GralColor.getColor("wh");
    colorBackVscrollbar = GralColor.getColor("lgr");
    int rd1,gn1, bl1, rd2, gn2, bl2;
    bl1 = colorBackVscrollbar.getColorValue();
    rd1 = (bl1 >>16) & 0xff;
    gn1 = (bl1 >>8) & 0xff;
    bl1 = bl1 & 0xff;
    colorSliderVscrollbar[0] = GralColor.getColor("bl");
    bl2 = colorSliderVscrollbar[0].getColorValue();
    rd2 = (bl1 >>16) & 0xff;
    gn2 = (bl1 >>8) & 0xff;
    bl2 = bl1 & 0xff;
    
    for(int ix=1; ix < colorSliderVscrollbar.length; ++ix){
      float r = 0.8f * ((float)(ix)) / (colorSliderVscrollbar.length);  //max. 0.8
      int rd = rd2 + (int)((rd1 - rd2) * r);
      int gn = gn2 + (int)((gn1 - gn2) * r);
      int bl = bl2 + (int)((bl1 - bl2) * r);
      colorSliderVscrollbar[ix] = GralColor.getColor(rd, gn, bl);
    }
  }
  
  
  
  
  /**Sets the focus of the table.
   * It sets an information that the selected cell should focused while drawing ({@link GraphicImplAccess.CellData#bSetFocus}).
   * Than it invokes the overridden super.{@link GralWidget#setFocus(int, int)}. 
   * That method asserts the visibility of the table and calls {@link GralWidgImpl_ifc#setFocusGThread()}
   * That method is implemented in the implementation widget layer and causes a redraw which focused
   * the correct cell. 
   * @see org.vishia.gral.base.GralWidget#setFocus(int, int)
   */
  @Override public void setFocus(int delay, int latest){
    if(gi !=null){
      gi.cells[lineSelectedixCell][colSelectedixCellC].bSetFocus = true;  //will be processed on redraw
      super.setFocus(delay, latest);
    }
  }
  
  

  
  
  @Override
  public void setCurrentLine(GralTableLine_ifc<UserData> line, int ixline, int ixcolumn) {
    assert(ixline > - zLineVisible && ixline < zLineVisible);
    assert(ixcolumn < gi.cells[0].length);
    lineSelected = (TableLineData)line;
    nLineFirst = -1;  //get new, undefined elsewhere.
    actionOnLineSelected(KeyCode.userSelect, lineSelected);
    if(ixline < 0){
      lineSelectedixCell = zLineVisible + ixline;  //-1 is the last.
      if(lineSelectedixCell < 0){
        lineSelectedixCell = 0;
      }
    } else {
      lineSelectedixCell = ixline;
      if(lineSelectedixCell >= zLineVisible){
        lineSelectedixCell = zLineVisible -1;
      }
    }
    if(ixcolumn >=0){
      this.colSelectedixCellC = ixcolumn;
    }
    bPrepareVisibleArea = true;
    /*
    if(line < -1 || line > zLine-1){ line = zLine -1; }
    if(column < -1 || column > zColumn-1){ column = 0; }
    if(line >=0){
      ixLineNew = line;  //forces color setting select color
    }
    if(column >=0){
      ixColumn = column;
    }
    */
    repaint(50,200);
  }

  
  
  /**Sets the color of the currently selected line. 
   * @param color
   */
  public void setColorBackSelectedLine(GralColor color){ 
    colorBackSelect = color; 
    repaint(100, 0);
  }

  

  
  /**Returns the current selected line. */
  @Override
  public TableLineData getCurrentLine() {
    return lineSelected;
  }

  
  /**Returns the temporary selected line while pressing the mouse button.
   * This method is able to use especially in mouse actions of user level.
   * On pressing any mouse button the line on mouse position is set independent of the current line.
   * The {@link #lineSelected()} is unchanged in this moment. 
   * @return The line where the mouse button is pressed.
   */
  public TableLineData getLineMousePressed(){ return lineSelectedNew; }
  
  
  

  @SuppressWarnings("unchecked") @Override
  public GralTableLine_ifc<UserData> getLine(String key) {
    Object oRootline = rootLine.getNode(key, keySeparator);
    if(oRootline instanceof GralTableLine_ifc){ return (GralTableLine_ifc<UserData>) oRootline; }
    else return null;
  }

  
  
  @Override public TableLineData insertLine(String lineKey, int row, String[] lineTexts, UserData userData) {
    if(row ==0){
      //insert on top
      TableLineData lineBehind = rootLine.firstChild();
      if(lineBehind ==null){
        return rootLine.addChildLine(lineKey, lineTexts, userData);
      } else {
        return lineBehind.addPrevLine(lineKey, lineTexts, userData);
      }
    } else if(row < 0) {
      return addLine(lineKey, lineTexts, userData);
    } else {
      throw new IllegalArgumentException("GralTable.insertLine - row not supported,row= " + row);
    }
  }
  
  
  
  public TableLineData XXXinsertLine(String key, int row, String[] cellTexts, UserData userData) {
    TableLineData line = this.new TableLineData(key, userData);
    
    if(row > zLine || row < 0){
      row = zLine;
    }
    if(row == 0){
      rootLine.addNodeFirst(line);  //addOnTop
    }
    if(lineSelected == null){ 
      lineSelected = line;
      actionOnLineSelected(KeyCode.defaultSelect, lineSelected);
    }
    zLine +=1;
    if(zLineCurr >= 0){ 
      zLineCurr +=1;
    }
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    bPrepareVisibleArea = true;
    repaint(100, 0);
    return line;
  }

  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.ifc.GralTable_ifc#addLine(java.lang.String, java.lang.String[], java.lang.Object)
   */
  @Override public TableLineData addLine(String lineKey, String[] lineTexts, UserData userData) {
    return rootLine.addChildLine(lineKey, lineTexts, userData);
  }
    
    
  public TableLineData XXXaddLine(String key, String[] cellTexts, UserData userData) {
    
    TableLineData line = this.new TableLineData(key, userData);
    if(lineSelected == null){ //// 
      lineSelected = line;
      actionOnLineSelected(KeyCode.defaultSelect, lineSelected);
      if(zLineCurr == -1){ 
        zLineCurr = 0;
        nLineFirst = 0;
      }
    }
    if(zLineCurr >= 0){
      zLineCurr +=1;
    }
    zLine += 1;
    rootLine.addNode(line);
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    bPrepareVisibleArea = true;
    repaint();
    return line;
  }

  
  
  
  @Override public void deleteLine(GralTableLine_ifc<UserData> line) {
    line.deleteLine();
  }
  
  
  public IterableIterator<TableLineData> iterLines(){
    return rootLine.iterator();
  }
  
  
  
  @Override public int size(){ 
    if(zLineCurr < 0) {
      //TODO count
    }
    return zLine; 
  }
  

  @Override public void clearTable() {
    colSelectedixCellC = 0;
    zLine = 0;
    zLineCurr = 0;
    nLineFirst = -1;
    lineSelected = null;
    actionOnLineSelected(KeyCode.removed, lineSelected);
    searchChars.setLength(0);
    for(int ix = 0; ix < linesForCell.length; ++ix){
      linesForCell[ix] = null;
    }
    rootLine.clear();
    bPrepareVisibleArea = true;
    bPrepareVisibleArea = true;
    repaint(200,200);
  }

  @Override public List<GralTableLine_ifc<UserData>> getMarkedLines(int mask) {
    List<GralTableLine_ifc<UserData>> list = new LinkedList<GralTableLine_ifc<UserData>>();
    
    for(TableLineData item: rootLine.iterator()){
      if(item.data instanceof MarkMask_ifc){
        if((((MarkMask_ifc)item.data).getMark() & mask) !=0){
          list.add(item);
        }
      }
      else if((item.getMark() & mask) !=0){
        list.add(item);
      }
    }
    return list;
  }

  
  @Override public TreeNode_ifc<?, UserData> getAllLines() { return rootLine; }
  
  
  @Override public List<UserData> getListContent() {
    List<UserData> list = new LinkedList<UserData>();
    for(TableLineData item: rootLine.iterator()){
      list.add(item.getUserData());
    }
    return list;
  }

  
  @Override public GralTableLine_ifc<UserData> getFirstMarkedLine(int mask) {
    for(TableLineData item: rootLine.iterator()){
      if((item.getMark() & mask) !=0){
        return item;
      }
    }
    return null;
  }

  
  
  
  
  private void fillVisibleArea(){
    TableLineData line = lineSelected;
    int ix = lineSelectedixCell;
    while(ix >0 && line !=null){
      line = prevLine(line);
      if(line !=null){
        linesForCell[--ix] = line;
      }
    }
    int nLinesBefore = lineSelectedixCell - ix;  //==lineStart if all lines are present.
    if(ix > 0){
      System.arraycopy(linesForCell, ix, linesForCell, 0, nLinesBefore);
      lineSelectedixCell = nLinesBefore;
    }
    
    fillVisibleAreaBehind(lineSelected, nLinesBefore);
  }
  
  
  
  private void fillVisibleAreaBehind(TableLineData lineStart, int ixStart){
    int ix = ixStart;
    TableLineData line = lineStart;
    while(line !=null && ix < zLineVisible) {
      linesForCell[ix] = line;
      line = nextLine(line);
      ix +=1;
    } 
    while(ix < zLineVisible){
      linesForCell[ix++] = null;
    }
  }
  

  
  /**Shifts the content in {@link #linesForCell} to show the appropriate part of data 
   * (e.g. lines of {@link #rootLine})in the table.
   * @param dLine >0 shift up to get next lines, <0 shift down to get previous lines
   * @return nr of lines shifted
   */
  protected int shiftVisibleArea(int dLine){
    int dLine1 = dLine;
    if(dLine >0){ //forward in lines
      TableLineData line = linesForCell[zLineVisible -1];  //the last line
      while(line !=null && dLine1 >0){
        line = nextLine(line);
        if(line !=null){
          System.arraycopy(linesForCell, 1, linesForCell, 0, zLineVisible-1);
          linesForCell[zLineVisible-1] = line;
          dLine1 -=1;
        }
      }
    } else if(dLine < 0){ //backward in lines
      TableLineData line;
      line = linesForCell[0];  //the new start line
      if(line == null){
        line = rootLine.firstChild(); //outer.tableLines.size() ==0 ? null : outer.tableLines.get(0);  //on init
      }
      while(line !=null && dLine1 < 0){
        line = prevLine(line);
        if(line !=null){
          System.arraycopy(linesForCell, 0, linesForCell, 1, zLineVisible-1);
          linesForCell[0] = line;
          dLine1 +=1;
        }
      }
    }
    int nLineShift = dLine - dLine1;
    if(nLineShift !=0){
      nLineFirst += nLineShift;
    }
    return nLineShift;
  }
  
  
  TableLineData nextLine(TableLineData lineP){
    TableLineData line = lineP;
    TableLineData linenext = null;
    if(line.showChildren){
      linenext = line.firstChild();         //deeper to any children
    }
    if(linenext == null){ //no children found
      linenext = line.nextSibling();  //may go to next root
    }
    while(linenext == null //no sibling found
      && line.parent() !=null  
      && !line.parentEquals(rootLine)) 
    {
      line = line.parent();
      if(line !=null){
        linenext = line.nextSibling();  //may null, then repeat for next parent
      }
    }
    return linenext;  //maybe null
  }
  
  
  TableLineData prevLine(TableLineData lineP){
    TableLineData line2 = lineP, line = lineP;
    line2 = line.prevSibling();
    if(line2 == null){
      //check parent
      if(!line.parentEquals(rootLine)){
        line = line.parent();
      } else {
        line = null;  //on root as first entry
      }
    } else {
      //if(line2 !=null){ line2 = line2.prevSibling(); } //the parent is the current parent, use previous.
      while(line2 !=null && line2.showChildren){
        //it has children
        line2 = line2.lastChild();  //last of all showing levels
      }
      line = line2;
    }
    return line;
  }  
  
  
  /**Invoked in the graphic thread from mouse listener in {@link GraphicImplAccess#mouseDownGral(int, CellData)}
   * @param key
   * @param cell
   */
  protected void mouseDown(int key, CellData cell){
    lineSelectedNew = linesForCell[cell.ixCellLine]; 
    repaint(0,0);  //immediately, it is in the graphic thread.
  }


  /**Invoked in the graphic thread from mouse listener in {@link GraphicImplAccess#mouseUpGral(int, CellData)}
   * @param key
   * @param cell
   */
  protected void mouseUp(int key, CellData cell){
    if(key == KeyCode.mouse1Up){
      lineSelected = lineSelectedNew;
      nLineFirst = -1;  //get new
      lineSelectedNew = null;
      lineSelectedixCell = cell.ixCellLine;  //used for key handling.
      colSelectedixCellC = cell.ixCellColumn;
      actionOnLineSelected(KeyCode.userSelect, lineSelected);
      repaint();
    }
  }



  /**Invoked in the graphic thread from mouse listener in {@link GraphicImplAccess#mouseDoubleGral(int, CellData)}
   * @param key
   * @param cell
   */
  protected void mouseDouble(int key, CellData cell){
    processKeys(KeyCode.mouse1Double);
  }



  
  
  /**Increments or decrements ixLineNew in [up] or [dn] situation until the {@link #searchChars} are found
   * or one time if searchChars are not given.
   * If the searchChars are not found, either the first or last line will be selected. It is because
   * any key operation should have an effect for the user. Paradigm: Not the search and found is prior,
   * the response of operation is prior. The user may see whether anything is found. 
   * @param bUp direction
   * @return true if found
   */
  protected boolean searchContent(boolean bUp){
    String search = searchChars.toString();
    boolean found = false;
    if(search != null && search.length() !=0) { // return false;
    boolean contSearch = true;
    TableLineData line2, line = lineSelected;
    do{
      if(bUp && line !=null){        //up
        line2 = prevLine(line);
      } else if(!bUp && line !=null){ //down
        line2 = nextLine(line);
      } else {
        //ixLineNew = ixLineNewAct;
        line2 = null;                 //eand reached
      }
      if(line2 ==null){ //end of search
        contSearch = false;  //without change the lineSelected
        lineSelected = line;  //show the first or last line
        nLineFirst = -1;  //get new, undefined elsewhere.
        found = search.length() >0;
      } else {
        line = line2;
        String sText = line.getCellText(colSelectedixCellC).toLowerCase();
        if(search.length() > 0 && searchChars.charAt(0) == '*'){
          contSearch = false;  //TODO search content contains
        } else {
          if( search.length() == 0){   //always found if no searchchar is given.
            found = false;
            contSearch = false;
          } else if(!sText.startsWith(search)){  //found
            found = false;
          } else {
            found = true;
            lineSelected = line;
            nLineFirst = -1;  //get new, undefined elsewhere.
            bPrepareVisibleArea = true;
            contSearch = false;                   //found
          }
        }
      }
    } while(contSearch);
    }
    return found;
  }
  
  
  
  
  /**Invoked from focus lost of any table cell implementation, checks whether the line is changed.
   * @param sText The text in the cell from the implementation widget.
   * @param celldata The cell, the line is gotten from {@link #linesForCell}[{@link CellData#ixCellLine}].
   */
  protected void checkAndUpdateText(String sText, CellData celldata){
    TableLineData line = linesForCell[celldata.ixCellLine];
    if(line !=null && !sText.equals(line.cellTexts[celldata.ixCellColumn])){
      line.cellTexts[celldata.ixCellColumn] = sText;
      line.bChanged = true;
    }
  }  
  
      /**Handle all standard keys of table. 
     * It should call in the key event handler from the implementation class.
     * <br>Keys:
     * <ul>
     * <li>pgup: 
     * <li>up
     * <li>dn
     * <li>pgdn
     * <li>{@link #keyMarkUp}
     * <li>{@link #keyMarkDn}
     * <li>calls {@link GralMng#getRegisteredUserAction(String what)} with what="KeyAction"
     *   and invokes the returned action method. With them all standard key actions of this application
     *   may be done if a {@link GralUserAction} is registered for that.  
     * </ul>
     * @param keyCode Encoding see {@link KeyCode}.
     * @return true if the key is processed, false if the key is not processed here. Maybe processed after them.
     */
    protected boolean processKeys(int keyCode){
      boolean done = true;
      long time = System.currentTimeMillis();
      lineSelectedNew = null;  //on any key, clear highlighted mouse down cell, if it is set yet.
      if(lastKey == keyCode){ keyRepetition +=1;  //same key
      } else {
        keyRepetition = 1; //other key.
      }
      //NOTE: prevent to fast key action if the last redraw is yet finished.
      //The draw needs to much time in Linux-GTK with an Atom processor (Lenovo)
      long timeDiff = time - timeLastRedraw;
      if( keyDone || keyCode == KeyCode.mouse1Double || timeDiff > -11110){  //use 50 ms for timeout if keyDone isn't set.  
        keyDone = false;
        switch(keyCode){
        case KeyCode.pgup: {
          if(lineSelectedixCell > 2){
            lineSelectedixCell = 2;
          } else {
            int shifted = shiftVisibleArea(-zLineVisible);  //shifted = -1 if all shifted
            lineSelectedixCell -= zLineVisible + shifted;
            if(lineSelectedixCell <0){
              lineSelectedixCell = 0;  //limit it on top.
            }
          }
          lineSelected = linesForCell[lineSelectedixCell];
          nLineFirst = -1;  //get new, undefined elsewhere.
          //the table has the focus, because the key action is done only if it is so.
          //set the new cell focused, in the paint routine.
          gi.cells[lineSelectedixCell][colSelectedixCellC].bSetFocus = true; 
          actionOnLineSelected(KeyCode.userSelect, lineSelected);
          keyActionDone.activate();
        } break;
        case KeyCode.mouseWheelUp:
        case KeyCode.up: {
          if(!searchContent(true)) {
            if(lineSelectedixCell > 2){
              lineSelectedixCell -=1;
            } else {
              int shifted = shiftVisibleArea(-1);  //shifted = -1 if all shifted
              lineSelectedixCell -= 1 + shifted;
              if(lineSelectedixCell <0){
                lineSelectedixCell = 0;  //limit it on top.
              }
            }
            lineSelected = linesForCell[lineSelectedixCell];
            
          }
          //the table has the focus, because the key action is done only if it is so.
          //set the new cell focused, in the paint routine.
          gi.cells[lineSelectedixCell][colSelectedixCellC].bSetFocus = true; 
          actionOnLineSelected(KeyCode.userSelect, lineSelected);
          keyActionDone.activate();
        } break;
        case KeyCode.pgdn: {
          if(lineSelectedixCell < zLineVisible -3){
            lineSelectedixCell = zLineVisible -3;
          } else {
            int shifted = shiftVisibleArea(zLineVisible);
            lineSelectedixCell += zLineVisible - shifted;
            if(lineSelectedixCell >= zLineVisible){
              lineSelectedixCell = zLineVisible -1;  //limit it on top.
            }
          }
          while( (lineSelected = linesForCell[lineSelectedixCell]) ==null
               && lineSelectedixCell >0    
            ){
            lineSelectedixCell -=1;
          }
          nLineFirst = -1;  //get new, undefined elsewhere.
          //the table has the focus, because the key action is done only if it is so.
          //set the new cell focused, in the paint routine.
          gi.cells[lineSelectedixCell][colSelectedixCellC].bSetFocus = true; 
          actionOnLineSelected(KeyCode.userSelect, lineSelected);
          keyActionDone.activate();
        } break;
        default:
          if(keyCode == KeyCode.dn || keyCode == keyMarkDn || keyCode == KeyCode.mouseWheelDn
            ) {
            if(keyCode == keyMarkDn && lineSelected !=null){
              GralTableLine_ifc<?> line = lineSelected; //tableLines.get(ixLine);
              if((line.getMark() & FileMark.select)!=0){
                //it is selected yet
                line.setNonMarked(FileMark.select, line.getUserData());
              } else {
                line.setMarked(FileMark.select, line.getUserData());
              }
            }
            if(!searchContent(false)) {
              if(lineSelectedixCell < zLineVisible -3){
                lineSelectedixCell +=1;
              } else {
                int shifted = shiftVisibleArea(1);
                lineSelectedixCell += 1 - shifted;
                if(lineSelectedixCell >= zLineVisible){
                  lineSelectedixCell = zLineVisible -1;  //limit it on top.
                }
              }
              while( (lineSelected = linesForCell[lineSelectedixCell]) ==null
                   && lineSelectedixCell >0    
                ){
                lineSelectedixCell -=1;
              }
              nLineFirst = -1;  //get new, undefined elsewhere.
            }
            //the table has the focus, because the key action is done only if it is so.
            //set the new cell focused, in the paint routine.
            gi.cells[lineSelectedixCell][colSelectedixCellC].bSetFocus = true; 
            actionOnLineSelected(KeyCode.userSelect, lineSelected);
            
            keyActionDone.activate();
          } else if(KeyCode.isTextKey(keyCode) && !bColumnEditable[colSelectedixCellC]){
            searchChars.appendCodePoint(keyCode);
            searchContent(false);
            repaint();
          } else if(keyCode == KeyCode.esc){
            searchChars.setLength(0);
            repaint();
          } else if(keyCode == KeyCode.back && searchChars.length() >0){
            searchChars.setLength(searchChars.length()-1);
            repaint();
          } else if(keyCode == keyOpenChild){
            if(lineSelected.lineCanHaveChildren){
              actionOnRefreshChildren(lineSelected);  //may get or refresh children, callback in user.
            }
            if(lineSelected.hasChildren()){           //only if it has children currently really.
              lineSelected.showChildren(true, true);
              lineSelected.countChildren(true, nLineFirst);  //count the children.
              fillVisibleAreaBehind(lineSelected, lineSelectedixCell);
              repaint();
            }
          } else if(keyCode == keyCloseChild){
            if(lineSelected.showChildren){
              lineSelected.showChildren(false, false);
              fillVisibleAreaBehind(lineSelected, lineSelectedixCell);
              repaint();
            }
          } else {
            done = false;
          }
        }//switch
        if(done == false && keyCode == keyMarkDn && lineSelected !=null){
          GralTableLine_ifc<?> line = lineSelected; //tableLines.get(ixLine);
          if((line.getMark() & 1)!=0){
            //it is selected yet
            line.setNonMarked(1, line.getUserData());
          } else {
            line.setMarked(1, line.getUserData());
          }
          
          keyActionDone.activate();
          done = true;
        }
        if(!done && lineSelected !=null){
          if(actionChanging !=null){
            //all other keys: call actionChanging.
            done = actionChanging.exec(keyCode, this, lineSelected);
          }
        } //if(table.)
        if(!done && itsMng.userMainKeyAction() !=null){
          done = itsMng.userMainKeyAction().exec(keyCode, getCurrentLine());
        }
        if(!done){
          //if actionChanging.userAction() returns false 
          GralUserAction mainKeyAction = itsMng.getRegisteredUserAction("KeyAction");
          if(mainKeyAction !=null){
            //old form called because compatibility, if new for with int-parameter returns false.
            if(!mainKeyAction.exec(keyCode, this)){
              done = mainKeyAction.exec(keyCode, this, new Integer(keyCode));
            }
          }
        }
        keyRepetition = 0;  //because it was done.
      }//if not redraw pending.
      lastKey = keyCode;
      return done;
    }

  
  
  
  
  /**This callback is need because the paint of a table needs more time if a slow processor is used
   * and the key repeat rate is higher than the calculation time. After finishing all paint requests
   * in the graphic thread this action was called. It sets {@link #keyDone} = true, then the next key
   * is processed immediately. Elsewhere, if the graphic thread is busy in the graphic os dispatching
   * and a new key event is received there, the key action won't be executed.
   * <br>
   * It helps for a run-after effect if the key are released already but some key events are stored.
   * If a navigation key is released, the table navigation should be stopped immediately.
   * 
   */
  protected final GralGraphicTimeOrder keyActionDone = new GralGraphicTimeOrder("GralTableKeyDone") {
    @Override
    public void executeOrder() {
      gi.bFocused = true;  //to focus while repainting
      repaintGthread();
      keyDone = true;
      //System.out.println("Key done");
    }
  };

  /**It is called whenever another line is selected, if the focus is gotten by mouse click
   * or a navigation key is pressed. 
   * This method calls the {@link GralUserAction#userActionGui(int, GralWidget, Object...)}.
   * The {@link #specifyActionOnLineSelected(GralUserAction)} can be set with any user instantiation
   * of that interface. The params[0] of that routine are filled with the {@link GralTableLine_ifc}
   * of the selected line.
   *  
   * @param line
   */
  private void actionOnLineSelected(int key, GralTableLine_ifc<?> line){
    itsMng.setLastClickedWidget(lineSelected);
    if(actionOnLineSelected !=null){
      actionOnLineSelected.exec(key, this, line);
    }
  }
  
  
  /**It is called whenever another line is selected, if the focus is gotten by mouse click
   * or a navigation key is pressed. 
   * This method calls the {@link GralUserAction#userActionGui(int, GralWidget, Object...)}.
   * The {@link #specifyActionOnLineSelected(GralUserAction)} can be set with any user instantiation
   * of that interface. The params[0] of that routine are filled with the {@link GralTableLine_ifc}
   * of the selected line.
   *  
   * @param line
   */
  protected void actionOnRefreshChildren(GralTableLine_ifc<?> line){
    if(actionOnRefreshChildren !=null){
      actionOnRefreshChildren.exec(KeyCode.userSelect, this, line);
    }
  }
  
  
  public final static class LinePresentation
  {
    public GralColor colorBack, colorText;
  }
  
  
  /**This is the super class for all implementers. It has protected access to all elements in the 
   * environment class. Via access methods the implementor class has protected access to all of it.
   * 
   */
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc, Removeable
  {
    
    public static final int chgEditableColumn = 0x00100000;

    /**Only used on invocation of {@link #drawCellContent}
     * 
     */
    private final LinePresentation linePresentation = new LinePresentation();
    
    protected final GralTable<UserData> outer;
    
    //protected GralWidgImpl_ifc implWidgWrapper;

    
    protected CellData[][] cells;
    
    
    
    /**Set to true while {@link #table}.{@link Table#redrawGthread()} is running.
     * It prevents recursive invocation of redraw() while setFocus() is invoked. */
    protected boolean bRedrawPending;

    
    
    
    /**Pixel per line. */
    protected int linePixel;
    
    /**Column position for the cells in pixel from 0 (left) calculated from the pixel size of the table. 
     * This values are calculated on setToPanel and after resize with {@link #resizeTable(GralRectangle)}. 
     */
    protected final int[] xpixelCell;

    /**Pixel position of the vertical scroll bar. */
    protected final GralRectangle xyVscrollbar = new GralRectangle(0,0,0,0);
    
    /**Position of vScrollBar calculated from {@link GralTable#rootLine}. {@link NodeTableLine#zLineUnfolded} and {@link GralTable#nLineCurr}
     * written in {@link #determineSizeAndPosition()} used for painting vScrollBar. */
    protected int y1Scrollbar, y2Scrollbar;
    
    protected boolean bVscrollbarChanged;
    
    /**New and last index of the sliderColor. The last index is stored by the implementation and compared with the new one
     * to desire whether a new implementation color should be gotten. */
    protected int ixColorScrollbar, ixColorScrollbarLast = -1;
    
    /**Start position of each column in pixel. 
     * @deprecated only used in org.vishia.gral.swt.SwtTable#initSwtTable(...) */
    protected int[] columnPixel;
    
    protected final int xPixelUnit;
        
    /**number of columns which should be shift to rigth on tree indent. 0. don't shift. Usual 1 */
    protected int nrofColumnTreeShift;

    /**Index (subscript) of the graphical line (not lines of the table)
     * which has the selection color and which should be gotten the selection color.
     */
    //protected int ixGlineSelected = -1, ixGlineSelectedNew = -1;
    
    /**Set true if the focus is gained by mouse click. It causes color set and 
     * invocation of {@link #actionOnLineSelected(GralTableLine_ifc)}. 
     */
    protected boolean bFocused;
    
    
    /**Sets temporary between focus lost and redraw. It is reset if the focus is gained for another cell
     * of the same table. */
    protected boolean bFocusLost;
    
    /**Set to true if the table has the focus in window.
     */
    protected boolean XXXhasFocus;
    

    protected long mousetime, redrawtime, mousect, redrawct;
    
    private boolean mouseDoubleClick;
    
    

    protected GraphicImplAccess(GralTable<UserData> outer, GralMng mng){ 
      super(outer, mng);
      xPixelUnit = mng.propertiesGui.xPixelUnit();
      this.outer = outer;
      outer.gi = this; 
      int xdPix = outer.itsMng.propertiesGui().xPixelUnit();
      columnPixel = new int[outer.columnWidthsGral.length+1];
      int xPix = 0;
      columnPixel[0] = xPix;
      for(int iCol = 0; iCol < outer.columnWidthsGral.length; ++iCol){
        xPix += outer.columnWidthsGral[iCol] * xdPix;
        columnPixel[iCol+1] = xPix;
      }

      this.xpixelCell = new int[outer.columnWidthsGral.length+1];
      int ydPix = outer.itsMng.propertiesGui().yPixelUnit();
      linePixel = 2 * ydPix;
    }
    
    protected GralMng itsMng(){ return outer.itsMng; }
    
    protected boolean bColumnEditable(int ix){ return GralTable.this.bColumnEditable[ix]; }
    
    
    protected void checkAndUpdateText(String sText, CellData celldata){
      GralTable.this.checkAndUpdateText(sText, celldata);
    }
    
    protected int ixColumn(){ return outer.colSelectedixCellC; }
    
    //protected int ixLine(){ return outer.ixLine; }
    
    protected int[] columnWidthsGral(){ return outer.columnWidthsGral; }
    
    
    
    protected int zColumn(){ return outer.zColumn; }
    
    protected GralColor colorSelectCharsBack(){ return outer.colorSelectCharsBack; }
    
    protected GralColor colorSelectChars(){ return outer.colorSelectChars; }
    
    protected GralColor colorBackTable(){ return outer.dyda.backColor; }
    
    protected GralColor colorBackVscrollbar(){ return outer.colorBackVscrollbar; }
    
    protected GralColor colorLineVscrollbar(){ return outer.colorSliderVscrollbar[ixColorScrollbar]; }
    
    protected StringBuilder searchChars(){ return outer.searchChars; }
    
    
    /**Returns the line on that any mouse button was pressed. It is either the left, mid or right button.
     * Able to use for context menu (right button).
     * @return
     */
    //public TableLineData getLineOnMousePressed(){ return cellDataOnMousePressed.tableItem; }
    

    
    /**Handle all standard keys of table. 
     * It should call in the key event handler from the implementation class.
     * <br>Keys:
     * <ul>
     * <li>pgup: 
     * <li>up
     * <li>dn
     * <li>pgdn
     * <li>{@link #keyMarkUp}
     * <li>{@link #keyMarkDn}
     * <li>calls {@link GralMng#getRegisteredUserAction(String what)} with what="KeyAction"
     *   and invokes the returned action method. With them all standard key actions of this application
     *   may be done if a {@link GralUserAction} is registered for that.  
     * </ul>
     * @param keyCode Encoding see {@link KeyCode}.
     * @return true if the key is processed, false if the key is not processed here. Maybe processed after them.
     */
    protected boolean processKeys(int keyCode){ return outer.processKeys(keyCode); }
/*
    boolean done = true;
      long time = System.currentTimeMillis();
      if(lastKey == keyCode){ keyRepetition +=1;  //same key
      } else {
        keyRepetition = 1; //other key.
      }
      //NOTE: prevent to fast key action if the last redraw is yet finished.
      //The draw needs to much time in Linux-GTK with an Atom processor (Lenovo)
      if( keyDone || keyCode == KeyCode.mouse1Double || (time - timeLastRedraw) > 350){  //use 350 ms for timeout if keyDone isn't set.  
        keyDone = false;
        switch(keyCode){
        case KeyCode.pgup: {
          if(outer.ixLine > zLineVisible){
            outer.ixLineNew = outer.ixLine - zLineVisible;
          } else {
            outer.ixLineNew = 0;
          }
          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
        } break;
        case KeyCode.mouseWheelUp:
        case KeyCode.up: {
          if(outer.ixLine > 0){
            //ixLineNew = ixLine - keyRepetition;
            outer.searchContent(true);
            if(outer.ixLineNew <0){ outer.ixLineNew = 0; }
          }
          
          if(outer.ixCellLineSelect > 3){
            outer.ixCellLineSelect -=1;
          } else {
            dLineForCells = -1;  
          }

          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
        } break;
        case KeyCode.mouseWheelDn:
        case KeyCode.dn: {
          if(outer.ixLine < outer.zLine -1){
            //ixLineNew = ixLine + keyRepetition;
            outer.searchContent(false);
            if(outer.ixLineNew >= outer.zLine ){ outer.ixLineNew = outer.zLine -1; }
          }
          
          if(outer.ixCellLineSelect < zLineVisible -3){
            outer.ixCellLineSelect +=1;
            outer.selectLine = outer.cellLines[outer.ixCellLineSelect];
          } else {
            dLineForCells =1;  
          }
          
          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
        } break;
        case KeyCode.pgdn: {
          if(outer.ixLine < outer.zLine - zLineVisible){
            outer.ixLineNew = outer.ixLine + zLineVisible;
          } else {
            outer.ixLineNew = outer.zLine -1;
          }
          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
        } break;
        default:
          if(KeyCode.isTextKey(keyCode)){
            outer.searchChars.appendCodePoint(keyCode);
            outer.searchContent(false);
            outer.repaint();
          } else if(keyCode == KeyCode.esc){
            outer.searchChars.setLength(0);
            outer.repaint();
          } else if(keyCode == KeyCode.back && outer.searchChars.length() >0){
            outer.searchChars.setLength(outer.searchChars.length()-1);
            outer.repaint();
          } else {
            done = false;
          }
        }//switch
        if(done == false && keyCode == outer.keyMarkDn && outer.ixLine >=0){
          GralTableLine_ifc<?> line = outer.tableLines.get(outer.ixLine);
          if((line.getMark() & 1)!=0){
            //it is selected yet
            line.setNonMarked(1, line.getUserData());
          } else {
            line.setMarked(1, line.getUserData());
          }
          if(outer.ixLine < outer.zLine -1){
            outer.ixLineNew = outer.ixLine + 1;
          }
          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
          done = true;
        }
        if(!done && outer.ixLine >=0){
          GralTableLine_ifc<?> lineGral = outer.tableLines.get(outer.ixLine);
          if(outer.actionChanging !=null){
            //all other keys: call actionChanging.
            done = outer.actionChanging.exec(keyCode, outer, lineGral);
          }
        } //if(table.)
        if(!done && outer.itsMng.userMainKeyAction() !=null){
          done = outer.itsMng.userMainKeyAction().exec(keyCode, outer.getCurrentLine());
        }
        if(!done){
          //if actionChanging.userAction() returns false 
          GralUserAction mainKeyAction = outer.itsMng.getRegisteredUserAction("KeyAction");
          if(mainKeyAction !=null){
            //old form called because compatibility, if new for with int-parameter returns false.
            if(!mainKeyAction.exec(keyCode, outer)){
              done = mainKeyAction.exec(keyCode, outer, new Integer(keyCode));
            }
          }
        }
        keyRepetition = 0;  //because it was done.
      }//if not redraw pending.
      lastKey = keyCode;
      return done;
    }
*/
    
    
    
    protected void setFocusCellMousePressed(){
     
    }

    
    /**Invoked on editing a text field
     * @param cell
     * @param text
     */
    protected void setCellText(CellData cell, String text){
      TableLineData line = GralTable.this.linesForCell[cell.ixCellLine];
      if(line != null){
        line.setCellText(text, cell.ixCellColumn);
      }
    }
    
    /**Updates the cell text fields which presents the content of the table.
     * After them update() of the graphic level should be called.
     * To execute the update of the cells in the graphic layer the {@link #drawCellContent(int, CellData[], TableLineData, LinePresentation)}
     * is called inside for any cell. This method is implemented in the implementation layer.
     */
    protected void updateGraphicCellContent(){
      long dbgtime = System.currentTimeMillis();
      bRedrawPending = true;

      Assert.check(outer.itsMng.currThreadIsGraphic());
      GralTable<UserData>.TableLineData/*<?>*/ line;
      
      if(outer.bPrepareVisibleArea){
        outer.fillVisibleArea();  //show the selected line at line 3 in graphic or before 0..2
        outer.bPrepareVisibleArea = false;
      }
      
      //Now draw:
      //
      int ix = -1;
      while(++ix < outer.zLineVisible) {
        line = outer.linesForCell[ix];
        setLinePresentation(line);
        drawCellContent(ix, cells[ix], line, linePresentation);
      }
      if(++ix < outer.zLineVisibleMax){  //a half visible line
        drawCellContent(ix, cells[ix], null, linePresentation);
      }
      outer.timeLastRedraw = System.currentTimeMillis();
      //System.out.println("GralTable - redraw;" + timeLastRedraw - dbgTime);
    }

    
    
    private void setLinePresentation(TableLineData/*<?>*/ line){
      boolean marked = line !=null && (line.getMark() & 1)!=0;
      boolean childrenMarked = line !=null && (line.getMark() & 2)!=0;
      if(line == outer.lineSelected){
        linePresentation.colorBack = marked ? outer.colorBackSelectMarked : childrenMarked ? outer.colorBackSelectSomeMarked : outer.colorBackSelect;
      } else if(line == outer.lineSelectedNew){
        linePresentation.colorBack = marked ? outer.colorBackSelectNewMarked : outer.colorBackSelectNew;
      } else {
        if(marked){
          linePresentation.colorBack = outer.colorBackMarked;
        } else if(childrenMarked){
          linePresentation.colorBack = outer.colorBackSomeMarked;
        } else if(!bFocused){
          linePresentation.colorBack = dyda.backColorNoFocus;
        } else {
          linePresentation.colorBack = line !=null && line.colorBackground !=null ? line.colorBackground : outer.dyda.backColor;
        }
      }
      linePresentation.colorText = line !=null && line.colorForground !=null ? line.colorForground : outer.dyda.textColor;
      
    }
    

    /**This method have to be called before the vertical scroll bar is painted
     * in the {@link GralWidgImpl_ifc#repaintGthread()} routine..
     * It checks whether the {@link GralTable#nLineCurr} and the shown number of lines
     * in {@link GralTable#rootLine}. {@link GralTable.NodeTableLine#zLineUnfolded}
     * is given, e.g. >=0. If one of them is <0 or lines are countered
     * calling {@link NodeTableLine#countChildren(boolean, int)}. 
     * Therewith the {@link GralTable#nLineCurr} is set too. Anytime if the situation is not specified
     * the {@link GralTable#nLineCurr} can be set to -1. This forces new calculation.
     */
    protected void determineSizeAndPositionScrollbar(int yPixel)
    { if(GralTable.this.nLineFirst <0 || GralTable.this.rootLine.zLineUnfolded <0) {
        GralTable.this.rootLine.countChildren(true, 0);
      }
      int zLine = Math.max(1, rootLine.zLineUnfolded);
      int y1 = yPixel * nLineFirst / zLine;
      int zLineShow = Math.min(zLineVisible, zLine);  //less if table is shorter than visible area
      int yd = yPixel * zLineShow / zLine;   //it is <= yPixel
      if(yd < 5){
        yd = 5;
      }
      int y2 = y1 + yd;
      if(y2 > yPixel) {
        y2 = yPixel;
        y1 = y2 - yd;
      }
      if(y1 != y1Scrollbar || y2 != y2Scrollbar){ 
        y1Scrollbar = y1;
        y2Scrollbar = y2;
        bVscrollbarChanged = true;
        ixColorScrollbar = (y2Scrollbar - y1Scrollbar) / 15;
        if(ixColorScrollbar >= outer.colorSliderVscrollbar.length){ ixColorScrollbar = outer.colorSliderVscrollbar.length -1; }
      }

    }

    /**This routine will be called inside a resize listener of the implementation graphic.
     * It calculates the width of the columns with the given width of the table's canvas.
     * Then {@link #setBoundsCells()} will be called. 
     * That is implemented in the underlying graphic layer and sets the bounds for each cell.
     * @param pixTable Size of the table area.
     */
    protected void resizeTable(GralRectangle pixTable) {
      int xPixelUnit = itsMng().propertiesGui().xPixelUnit();
      int yPixelUnit = itsMng().propertiesGui().yPixelUnit();
      outer.zLineVisible = pixTable.dy / yPixelUnit / 2;
      if(outer.zLineVisible > outer.zLineVisibleMax){ outer.zLineVisible = outer.zLineVisibleMax; }
      xyVscrollbar.x = pixTable.dx - xPixelUnit;
      xyVscrollbar.dx = xPixelUnit;
      xyVscrollbar.y = 0;
      xyVscrollbar.dy = pixTable.dy;
      int xPixel1 = 0;
      xpixelCell[0] = xPixel1;
      int ixPixelCell;
      int xPos;
      //Columns from left with positive width
      for(ixPixelCell = 0; ixPixelCell < columnWidthsGral().length && (xPos = columnWidthsGral()[ixPixelCell]) > 0; ++ixPixelCell){
        xPixel1 += xPos * xPixelUnit;
        xpixelCell[ixPixelCell+1] = xPixel1;
      }
      nrofColumnTreeShift = ixPixelCell +1;
      System.out.println("GralTable - resizeTable; nrofColumnTreeShift =" + nrofColumnTreeShift);
      xPixel1 = pixTable.dx - xPixelUnit;
      xpixelCell[columnWidthsGral().length] = xPixel1;  //right position.
      for(ixPixelCell = columnWidthsGral().length-1; ixPixelCell >=0  && (xPos = columnWidthsGral()[ixPixelCell]) < 0; --ixPixelCell){
        xPixel1 += xPos * xPixelUnit;
        xpixelCell[ixPixelCell] = xPixel1;
      }
      setBoundsCells(0);

    }
    
    
    /**Invoked in {@link #resizeTable(GralRectangle)} as action which should be implemented in the implementation layer.
     * The {@link #xpixelCell} was set before.
     * @param treeDepthBase
     */
    abstract protected void setBoundsCells(int treeDepthBase);

    @Override public boolean remove(){
      outer.rootLine.removeChildren();
      return true;
    }

    
    /**Sets the current cell as focused with the focus color. It causes
     * a redraw of the whole table because the cell may be shifted in table position.
     * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
     * @param cell The cell 
     */
    protected boolean redrawTableWithFocusedCell(CellData data){
      if(bPrepareVisibleArea){
        fillVisibleArea();  //show the selected line at line 3 in graphic or before 0..2
        bPrepareVisibleArea = false;
      }
      TableLineData line = outer.linesForCell[data.ixCellLine];
      if( line !=null){ //don't do any action if the cell isn't use.
        outer.lineSelectedNewixCell = data.ixCellLine;
        lineSelectedNew = line;
        //TODO outer.selectLine = data.tableItem;
        outer.colSelectedixCellC = data.ixCellColumn;
        bFocused = true;
        return true;
      }
      else return false;
    }


    
    /**This routine is invoked if any cell of the table gets the focus. It is if the window gets the focus
     * from view of the whole operation system, if the mouse is landing on a cell Text-field with click
     * or if setFocus() for the graphic presentation layer was called. 
     * <ul>
     * <li>If {@link #bFocusLost} is true, another cell of this table has lost the focus
     *   in the last 50 ms before the repaint() was invoked. Then the super.{@link GralWidget#setFocus()}
     *   will not be called.
     * <li>If {@link #bRedrawPending} is set, it means that the focus gained is invoked by a setFocus()-call
     *   of the graphic representation layer. Then the {@link GralWidget#setFocus()} will not be called.  
     * <li>If both conditions are false, it is a focusGained either by mouse click, whereby this table 
     *   has not have the focus before, or by any other action of focusing. 
     *   Then {@link GralWidget#setFocus()} is called to designate, that the table is focused.   
     * </ul>
     */
    public void focusGainedTable(){ 
      if(bFocusLost){  
        //focus is lost from another cell of this table yet now, repaint is not invoked,
        bFocusLost = false;  //ignore focus lost.
      } else {
        //Any cell has got the focus, it means the table has gotten the focus.
        bFocused = true; 
        if(!bRedrawPending){
          itsMng.gralFocusListener.focusGainedGral(GralTable.this);
          //super.focusGained();
          System.out.println("GralTable - debugInfo; focusGained " + GralTable.this.toString() );
          repaint(50,100);
        }
      }
    }
    
    /**Invoked if any cell has lost the focus.
     * If the table has lost the focus, the repaint will established this state after 50 milliseconds
     * because the flag {@link #bFocused} = false.
     * But if another cell of the table has gotten the focus in this time, the bFocus = true is set. 
     * 
     */
    protected void focusLostTable(){ 
      bFocusLost = true;
      repaint(50,100);  //NOTE: bFocusLost =false will be set in repaint.
    }
    

    
    
    
    
    protected void mouseDownGral(int key, CellData cell){
      mousetime = System.currentTimeMillis();
      outer.mouseDown(key, cell);
    }


    protected void mouseUpGral(int key, CellData cell){
      if(mouseDoubleClick){
        mouseDoubleClick = false;
      } else {
        outer.mouseUp(key, cell);
      }
    }


    protected void mouseDoubleGral(int key, CellData cell){
      mousetime = System.currentTimeMillis();
      mouseDoubleClick = true;
      outer.mouseDouble(key, cell);
    }


    protected abstract void drawCellContent(int iCellLine, CellData[] cellData, GralTable<?>.TableLineData line, LinePresentation linePresentationP);

    protected abstract CellData drawCellInvisible(int iCellLine, int iCellCol);

    protected abstract int getVisibleLinesTableImpl();
    
    /**Should be implemented by the implementation class.
     * @param column The column
     * @return The menu.
     */
    protected abstract GralMenu createColumnMenu(int column);
    
    
    //protected void setCellDataOnMousePressed(CellData data){ cellDataOnMousePressed = data; } 
    

    

 
  }

  
  /**This class is only a definition of the typed TreeNodeBase without additonal features. */
  public class NodeTableLine 
  extends TreeNodeBase<TableLineData, UserData, GralTableLine_ifc<UserData>>
  {
    /**Number of lines for this line and all visible children.
     * 
     */
    int zLineUnfolded;
    
    /**The deepness in the tree presentation of the data.
     * 
     */
    int treeDepth;

    /**Lines of a children, a tree structure. null for a non-treed table.
     * null it the children are unknown or there are not children.
     */
    //protected ArrayList<TableLineData> childLines;
    
    //protected TableLineData parentLine;
    
    /**True if it is known that the node has children. They may be unknown, then {@link #childLines} are null. */
    boolean lineCanHaveChildren;

    /**True if the children are shown in representation. */
    boolean showChildren;

    NodeTableLine(String key, UserData data){ super(key, data); }
    
    
    void clear() {
      rootLine.removeChildren();
      rootLine.zLineUnfolded = 0;
    }
    
    
    /**Inserts a line as child of this. This method should be used if no child line exists yet.
     * It adds as last line of children. If child lines are existent, one can use {@link #addPrevLine(String, String[], Object)}
     * or {@link #addNextLine(String, String[], Object)} in respect to a given child line too.
     * @param lineKey The key for the line
     * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
     * @param userDataP
     * @return the added line.
     */
    public TableLineData addChildLine(String lineKey, String[] lineTexts, UserData userDataP){
      lineCanHaveChildren = true; //hasChildren();
      TableLineData line = GralTable.this.new TableLineData(lineKey, userDataP);
      super.addNode(line);
      line.treeDepth = this.treeDepth +1;
      line.prepareAddedLine(lineTexts);
      return line;

    }
    
    
    /**Inserts a line before the current line.
     * @param lineKey The key for the line
     * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
     * @param userDataP
     * @return the added line.
     */
    public TableLineData addPrevLine(String lineKey, String[] lineTexts, UserData userDataP){
      lineCanHaveChildren = true; //hasChildren();
      TableLineData line = GralTable.this.new TableLineData(lineKey, userDataP);
      super.addSiblingPrev(line);  
      line.prepareAddedLine(lineTexts);
      return line;

    }
    
    
    
    /**Inserts a line behind the current line.
     * @param lineKey The key for the line
     * @param lineTexts maybe null, elsewhere texts for the cells (column texts) of the line.
     * @param userDataP
     * @return the added line.
     */
    public TableLineData addNextLine(String lineKey, String[] lineTexts, UserData userDataP){
      lineCanHaveChildren = true; //hasChildren();
      TableLineData line = GralTable.this.new TableLineData(lineKey, userDataP);
      super.addSiblingNext(line);
      line.prepareAddedLine(lineTexts);
      return line;

    }

    public void deleteLine() {
      zLine -=1;
      nLineFirst = -1;
      adjCountChildrenInParent(-1);
      final TableLineData line1 = (TableLineData)this;
      bPrepareVisibleArea = true;
      if(lineSelected == this){
        TableLineData line2 = prevLine(line1);
        if(line2 == null){
          lineSelected = nextLine(line1); 
        } else {
          lineSelected = line2;
          //nLineCurr left unchanged.
        }
      } else {
        nLineFirst = -1;  //get new, undefined elsewhere.
      }
      line1.detach();
      repaint();
    }
    
    

    
    void countChildren(boolean bLeftGrandChildrenOpen, int nrParent){
      countChildren(bLeftGrandChildrenOpen, nrParent, 0);
      adjCountChildrenInParent(this.zLineUnfolded);
    }
    
    
    /**Counts the children and checks whether their childs should be shown.
     * It checks and sets {@link TableLineData#showChildren}
     * @param lineParent
     * @param bLeftGrandChildrenOpen false then set {@link TableLineData#showChildren} = false, true: count the grand children.  
     * @param recurs more than 100 - prevent it.
     */
    void countChildren(boolean bLeftGrandChildrenOpen, int nrParent, int recurs){
      if(recurs >100) return;
      this.zLineUnfolded = 0;
      int ctParent = nrParent;
      TableLineData child = this.firstChild();
      while(child !=null) {
        this.zLineUnfolded +=1;
        ctParent +=1;
        if(zLineCurr >=0){ 
          zLineCurr +=1; 
          if(child == linesForCell[0]) {
            nLineFirst = ctParent;
          }
        }
        if(child.showChildren && bLeftGrandChildrenOpen) {
          child.countChildren(true, ctParent, recurs+1);
          this.zLineUnfolded += child.zLineUnfolded;
        } else {
          child.showChildren = false; //close it.
          child.zLineUnfolded = 0;
        }
        child = child.nextSibling();
      }
    }
    
    
    
    void adjCountChildrenInParent(int nCorr) {
      ////
      NodeTableLine parent1 = parent();
      boolean showChildren1 = showChildren;
      while(parent1 !=null   //stop on root node. 
          && (showChildren1 &= parent1.showChildren)   //stop count zLineUnfolded on nonvisible children.
        ) {
        zLineUnfolded += nCorr;
        parent1 = parent1.parent();   
      }
    }

    

    
  }
  
  
  /**An instance of this class is assigned to any TableItem.
   * It supports the access to the TableItem (it is a table line) via the SWT-independent interface.
   * The instance knows its TableSwt and therefore the supports the access to the whole table.
   *
   */
  public final class TableLineData
  extends NodeTableLine 
  implements MarkMask_ifc, GralTableLine_ifc<UserData>
  {

    SelectMask markMask;
    
    
    /**Lines of a children, a tree structure. null for a non-treed table.
     * null it the children are unknown or there are not children.
     */
    //protected ArrayList<TableLineData> childLines;
    
    //protected TableLineData parentLine;

    /**If a repaint is necessary, it is changed by increment by 1. If the redraw is executed
     * and no other redraw is requested, it is set to 0. If another redraw is requested while the
     * redraw runs but isn't finished, it should be executed a second time. Therefore it isn't reset to 0. 
     */
    public AtomicInteger ctRepaintLine = new AtomicInteger();  //NOTE should be public to see it from derived outer class
    
    public String[] cellTexts;
    
    /**A line may have its datapath adequate a {@link GralWidget}, only used for
     * {@link #getDataPath()} and {@link #setDataPath(String)}.
     */
    private String sDataPath;
    
    /**True if any of a cell text was changed. */
    protected boolean bChanged;
    
    public GralColor colorForground, colorBackground;
    
    //protected UserData userData;
    
    //TODO GralColor colorBack, colorText;
    
    TableLineData(String key, UserData data){
      super(key, data);  //key, data
      cellTexts = new String[GralTable.this.zColumn];
    }
    
    
    
    private void prepareAddedLine(String[] lineTexts) {
      nLineFirst = -1;  //get new, undefined elsewhere.
      if(lineTexts !=null){
        for(int ixCol = 0; ixCol < lineTexts.length && ixCol < cellTexts.length; ++ixCol){
          cellTexts[ixCol] = lineTexts[ixCol];
        }
      } ////
      if(lineSelected == null){ //// 
        lineSelected = this;
        actionOnLineSelected(KeyCode.defaultSelect, lineSelected);
      }
      adjCountChildrenInParent(1);
      zLine += 1;
      GralTable.this.bPrepareVisibleArea = true;
      boolean showChildren1 = this.showChildren; 
      if(zLineUnfolded == -1){ 
        zLineUnfolded = 0;
      }
      if(showChildren) {
        zLineUnfolded +=1;
        adjCountChildrenInParent(1);
      }
      if(this == linesForCell[0]) {
        nLineFirst = rootLine.zLineUnfolded;  //store the number of current line if given.
      }
      if(showChildren1) {
        repaint();
      }
    }
    

    
    
    /*
    @Override
    public void removeChildren(){
      if(childLines !=null){
        childLines = null;
      }
    }
    */
    
    
    @Override public void setEditable(boolean editable){
      throw new IllegalArgumentException("a table line can't be set edit able");
      //TODO set a table line to able to edit?
    }

    /**Query whether the table line is able to edit: Return from the whole table.
     * @see org.vishia.gral.ifc.GralWidget_ifc#isEditable()
     */
    @Override public boolean isEditable(){ 
      return GralTable.this.bEditable; 
    }
    
    
    @Override public boolean isNotEditableOrShouldInitialize(){ return GralTable.this.isNotEditableOrShouldInitialize(); }

    
    
    
    @Override public boolean isChanged(boolean setUnchanged){ 
      boolean ret = bChanged;
      if(setUnchanged){
        bChanged = false;
      }
      return ret; 
    }


    
    @Override public TableLineData parentNode(){ return (TableLineData)super.getParent(); }
    
    @Override public String getName(){ return GralTable.this.name; }
    

    @Override public String getCellText(int column) { 
      String text = cellTexts[column]; 
      return text == null ? "" : text;
    }

    @Override public String[] getCellTexts() { return cellTexts; }

    
    public int treeDepth(){ return treeDepth; }
    
    @Override
    public String setCellText(String text, int column) {
      String oldText = cellTexts[column];
      cellTexts[column] = text;
      GralTable.this.repaint();
      return oldText;
    }

    
    
    
    @Override public UserData getUserData() { return data;  }

    //@Override public void setUserData(UserData data) {this.userData = data; }

    @Override public long setContentIdent(long date){ long last = GralTable.this.dateUser; GralTable.this.dateUser = date; return last; }
    
    @Override public long getContentIdent(){ return GralTable.this.dateUser; }

    @Override
    public int getSelectedColumn()
    { return GralTable.this.colSelectedixCellC;
    }

    @Override public boolean setVisible(boolean visible) 
    { return false;  //TODO line visible. 
    }

    
    
    @Override public void setFocus() { GralTable.this.setFocus(); }

    @Override public void setFocus(int delay, int latest) { GralTable.this.setFocus(delay, latest); }

    @Override public boolean isInFocus(){ return GralTable.this.isInFocus(); } 

    @Override public boolean isVisible(){ return GralTable.this.isVisible(); }
    

    @Override public GralColor setBackgroundColor(GralColor color) {
      GralColor ret = colorBackground;
      colorBackground = color;
      repaint(50, 50);
      return ret;
    }

    @Override public GralColor setForegroundColor(GralColor color) {
      GralColor ret = colorForground;
      colorForground = color;
      repaint(50, 50);
      return ret;
    }

    
    /**Sets the background color of the whole line or one cell.
     * @param ix -1 for the whole line, >=0 for one cell. (TODO)
     */
    @Override public void setBackColor(GralColor color, int ix)
    { 
      if(color.getColorName().equals("pma"))
        Assert.stop();
      colorBackground = color;
      repaint(50, 50);
    }
    
    @Override public GralColor getBackColor(int ix)
    { 
      return colorBackground;
    }
    
    

    @Override public void setLineColor(GralColor color, int ix)
    { 
      colorForground = color;
      repaint(50, 50);
    }

    @Override public void setTextColor(GralColor color)
    { 
      colorForground = color;
      repaint(50, 50);
    }

    @Override public void setText(CharSequence text){
      throw new IllegalArgumentException("GralTable-TableLineData - setText is not implemented;");
    }
    
    
    @Override public void showChildren(boolean show, boolean bLeftGrandChildrenOpen) {
      if(show) {
        if(bLeftGrandChildrenOpen && showChildren) return;  //do nothing because no change
        else {
          if(showChildren) {  //it is shown already
          }
          showChildren = true;
          //children are closed yet, open and count it.
          countChildren(bLeftGrandChildrenOpen, nLineFirst);
        }
      } else { //switch off children
        if(showChildren) {
          adjCountChildrenInParent(-zLineUnfolded);  //firstly subtract because children are open
          showChildren = false;
        }
        //else: don't change anything.
      }
    }

    
    
    
    @Override
    public void repaint() {
      ctRepaintLine.addAndGet(1);
      GralTable.this.repaint(); 
    }

    @Override public void repaint(int delay, int latest){
      ctRepaintLine.addAndGet(1);
      GralTable.this.repaint(delay, latest); 
      //itsMng.setInfoDelayed(this, GralPanelMngWorking_ifc.cmdRedraw, 0, null, null, delay);
    }
    

    

    
    @Override
    public void setBoundsPixel(int x, int y, int dx, int dy) {
      // TODO Auto-generated method stub
      
    }

    @Override public void refreshFromVariable(VariableContainer_ifc container){
      GralTable.this.refreshFromVariable(container);
    }

    @Override public void refreshFromVariable(VariableContainer_ifc container, long timeAtleast, GralColor colorRefreshed, GralColor colorOld){
      GralTable.this.refreshFromVariable(container, timeAtleast, colorRefreshed, colorOld);
    }


    
    @Override public void setDataPath(String sDataPath){
      this.sDataPath = sDataPath;
    }

    
    
    @Override public String getDataPath(){ return this.sDataPath; }

    @Override public GralUserAction getActionChange(){ return GralTable.this.getActionChange(); }

    

    //@Override public Object[] getWidgetMultiImplementations(){ return null; }

    
    @Override
    public boolean remove()
    { return GralTable.this.gi.remove();
    }

    
    
    
    @Override public void setHtmlHelp(String url) { GralTable.this.setHtmlHelp(url); }
    
    
    @Override public Object getContentInfo(){ return data; }

    
    
    @Override public int getMark(){
      if(data instanceof MarkMask_ifc){
        return ((MarkMask_ifc)data).getMark();
      } else if(markMask == null){
        return 0;
      } else {
        return markMask.getMark();
      }

    }
    

    /**Sets the mark status of the line.
     * It invokes the method given with {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * with the given {@link #setUserData(Object)} of this line. It means a selection with the user data
     * may be done too. This method will be called especially on pressing the mark key specified with  
     * {@link GralTable#specifyKeysMarkUpDn(int, int)}
     * @param mask This bits of the {@link SelectMask#selectMask} will be reseted.
     * @param data if null then don't invoke {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * @see org.vishia.util.SelectMask#setNonMarked(int, java.lang.Object)
     */
    @Override public int setNonMarked(int mask, Object data)
    { if(GralTable.this.actionMarkOnLine !=null && data !=null){ GralTable.this.actionMarkOnLine.setNonMarked(mask, data); }
      if(data instanceof MarkMask_ifc){
        ((MarkMask_ifc)data).setNonMarked(mask, data);
      }
      if(markMask ==null) return 0;
      else return markMask.setNonMarked(mask, data);
    }
    
    /**Sets the mark status of the line.
     * It invokes the method given with {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * with the given {@link #getUserData()} of this line. It means a selection with the user data
     * may be done too. This method will be called especially on pressing the mark key specified with  
     * {@link GralTable#specifyKeysMarkUpDn(int, int)}
     * @param mask This bits of the {@link SelectMask#selectMask} will be set.
     * @param data if null then don't invoke {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * @see org.vishia.util.SelectMask#setNonMarked(int, java.lang.Object)
     */
    @Override public int setMarked(int mask, Object data)
    { if(GralTable.this.actionMarkOnLine !=null && data !=null){ GralTable.this.actionMarkOnLine.setMarked(mask, data); }
      if(data instanceof MarkMask_ifc){
        ((MarkMask_ifc)data).setMarked(mask, data);
      }
      if(markMask ==null){ markMask = new SelectMask(); }
      return markMask.setMarked(mask, data);
    }

    @Override public GralMng gralMng(){ return GralTable.this.gralMng(); }
    
    @Override public void setToPanel(GralMngBuild_ifc mng){
      throw new IllegalArgumentException("GralTableLine.setToPanel - is illegal; Use GralTable.setToPanel(...)");
    }

    @Override public String toString(){ 
      StringBuilder u = new StringBuilder();
      for(int ii = 0; ii < cellTexts.length; ++ii){
        u.append(cellTexts[ii]).append('|');
      }
      return u.toString(); 
    }
  }
  
  /**Data for each Text widget of the graphical implementation layer.
   * An instance is created on creating the text field for the cell in the implementation layer.
   * The instance is referenced only by the text field,
   * It refers the data of the {@link GralTable#tableLines}.
   * <pre>
   * swt.Canvas
   *      |--*>swt.Text
   *               |--data-->CellData
   *                           -ixCellLine
   *                           -ixCellColumn
   * 
   * </pre>
   * 
   * Note: The class is visible only in the graphic implementation layer, because it is protected.
   * The elements need to set public because there are not visible elsewhere in the derived class
   * of the outer class. 
   */
  public static class CellData{
    
    /**The row and column in the graphical presentation. With the information about the row, the
     * associated {@link TableLineData} can be found via the {@link GralTable#linesForCell}. */
    public final int ixCellLine, ixCellColumn;
    
    /**The color in the graphical presentation. It is the color of the text field. 
     * Note that the color of the text field is only changed if this colors and the 
     * {@link TableLineData#colorBackground} and {@link TableLineData#colorForground} are different. 
     * It saves some calculation time if the color of the text field is set only if it is necessary. */
    public GralColor colorBack, colorText;
    
    
    /**temporary set to true to set the focus of this cell.
     * 
     */
    public boolean bSetFocus;
    
    /**The currently tree depth of this cell. Invoke setBounds if it is different of line. */
    public int treeDepth;
    
    public CellData(int ixCellLine, int ixCellColumn){
      this.ixCellLine = ixCellLine; 
      this.ixCellColumn = ixCellColumn;
    }
  }
  

  
  
}
