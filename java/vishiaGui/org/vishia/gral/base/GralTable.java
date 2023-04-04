package org.vishia.gral.base;

import java.lang.reflect.Array;
import java.util.EventObject;
import java.util.Iterator;
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
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;
import org.vishia.util.Docu_UML_simpleNotation;
import org.vishia.util.ExcUtil;
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
 * <br><br>
 * Each text field refers an instance of {@link CellData} as its
 * 'user data'. The CellData refers via {@link CellData#tableItem} the line.
 * 
 * <pre>
 *   Graphic representation     GralTable        {@link TableLineData}
 *             |                   |<--------------<#>|           
 *    |<*------+                   |                  |<>--userData--->User's data                    
 *   TextField                     |---rootLine-----*>|
 *    |<>------>CellData           |---lineSelected-->|-->cellTexts
 *                 |                                  |-->colors
 *                 |---tableItem--------------------->|
 * </pre>
 * UML presentation, see {@link Docu_UML_simpleNotation}.
 * <br><br>
 * A {@link TableLineData} refers any aggregated user data, associated with {@link #insertLine(String, int, String[], Object)}
 * or {@link NodeTableLine#addChildLine(String, String[], Object)} etc. The user data can be gotten with {@link TableLineData#getUserData()}.
 * The user data are not part of the graphical presentation by automatism. They can be used for graphical implementation
 * by the application, especially to provide the texts or maybe colors for the table line cells. 
 * <br><br>
 * <b>Keyboard handling in a table</b>: <br>
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
 * <br><br>
 * <b>Adding an context menu</b>: <br>
 * The {@link #addContextMenuEntry(int, String, String, GralUserAction)} can be invoked for the table only in the graphic thread. 
 * Use any {@link GralGraphicOrder} to do that, use a callback with this time order on the creation of the table. 
 * 
 * @author Hartmut Schorrig
 *
 */
//@SuppressWarnings("synthetic-access") 
public final class GralTable<UserData> extends GralWidget implements GralTable_ifc<UserData> {

  /**Version, history and license.
   * <ul>
   * <li>2023-04-04: bugfixing while test.
   * <li>2023-04-04: Now {@link #processKeys(int)} number of key strokes generally reduced to 100 ms min.
   *   {@link #bitLinesForCellChanged}: not active, but try to reduce effort for redraw. Has less effect, is not ready. 
   * <li>2023-02-12 There is a problem with focused cell and updating cell content. 
   *   For that after KeyCode.up and KeyCode.dn the {@link #keyActionDone} should be called immediately in the graphic thread
   *   to update the cell contents and afterwards with 50 ms delay the same because of the focus. 
   *   This is not optimized yet. If the second call is not done, an automatic created focusLost and focusGained from Swt
   *   sets the focus in the Text cell before, why it is unclarified. 
   *   TODO maybe improve only set the focused cell in the delayed call, dont update the content. 
   *   It may also be that updating forces the focus lost/gain behavior. Experience with order of that,
   *   update at last. In the version now it works but needs a little bit too much calculation time. 
   * <li>2023-02-12 {@link #colorBackMarked2} etc. for selection lines for copy in Fcmd 
   *   respectively as common approach as second selection. 
   * <li>2023-02-11 {@link #processKeys(int)} experience. Now call {@link #keyActionDone} not as event,
   *   instead immediately in the key handler. This is possible, because it is the graphic thread, of course!
   *   It is better to save calculation time for enqueu/dequeu the timer and event.
   *   The idea to show the graphic not on any key stroke to save time for fast repetition is not implemented here,
   *   and it may be not necessary. It may be implementable with a time measurement and counter.  
   * <li>2023-01-06 Hartmut new: key combination sh + enter now for activating context menu. 
   *   Is it a good choice? In MS-Windows also sh-F10 activates the context menu (sometimes). 
   *   But it is general a good idea to activate it manually per key stroke. 
   * <li>2023-01-06 Hartmut rename: {@link #addContextMenuEntry(int, String, String, GralUserAction)} instead addContextMenuEntryGthread(...).
   *   It was the faulty name meanwhile, able to add in any thread.
   * <li>2023-01-06 Hartmut new: {@link #setCurrentColumn(int)} important to focus a column,
   *   if the evaluation of click/enter should depend from the selected column.  At long last it works. 
   * <li>2023-01-02 Hartmut new: {@link #getFirstLine()} sometimes necessary, why this was not existing till now?
   *   {@link #fillVisibleArea()} has had a problem if the #lineSelected was =null, 
   *   occurring on click on a non existing line in the table. now fixed.
   * <li>2023-01-02 Hartmut new: {@link #fillinPending(boolean)} especially for color to show,
   *   now first time it is really obviously how long or whether a file panel will be filled in another thread.
   * <li>2023-01-02 Hartmut new: {@link #processKeys(int)}: More detailed association between key and action.  
   * <li>2022-12-12 Hartmut new: {@link #getColumnInFocus()}. why was this not existing till now?
   * <li>2022-12-12 Hartmut new: {@link #setColumnProportional(int[])} and resizing with this proportional values.
   *   This feature was longer plant but never implemented till now. See also changes in {@link GraphicImplAccess#resizeTable(GralRectangle)}. 
   * <li>2022-12-12 Hartmut chg: using {@link GralWidget_ifc.ActionChangeWhen#onAnyKey} instead onEnter. It is systematically. 
   * <li>2022-10-22 Hartmut chg: rename variables of {@link NodeTableLine} with prefix tbl_ to distinguish from nd_ variables from node 
   * <li>2018-10-28 Hartmut chg: Now uses argument zLineMax from {@link #GralTable(String, int, int[])} instead constant 50 for number of showed lines.
   * <li>2018-01-07 Hartmut new: {@link #getCellTextFocus()}  
   * <li>2016-09-17 Hartmut new: in {@link #mouseDouble(int, CellData)} the <code>getActionChangeStrict(ActionChangeWhen.onMouse1Double, true)</code>
   *   is checked. Therewith the new concept of {@link #specifyActionChange(String, GralUserAction, String[], org.vishia.gral.ifc.GralWidget_ifc.ActionChangeWhen...)}
   *   for specific actions is used here now. Firstly only for the mouse double click action. TODO: for all specific actions. 
   * <li>2016-09-01 Hartmut new: {@link #getIxLine(GralTableLine_ifc)} to determine the current position of a given table line.
   * <li>2016-08-28 Hartmut chg: {@link GraphicImplAccess#setBoundsCells(int, int)} now gets the zLineVisible.
   *   The {@link org.vishia.gral.swt.SwtTable#setBoundsCells(int, int)} sets the rest of texts invisible. Therewith the phenomena of ghost lines
   *   on resizing are removed. 
   * <li>2016-08-28 Hartmut chg:  Bugfix: In {@link org.vishia.gral.swt.SwtTable#resizeTable(GralRectangle)}: 
   *   The {@link GralWidget#pos()} should not be used because the Composite is resized with it already. Instead the number of lines
   *   is correctly calculated now and {@link GraphicImplAccess#setBoundsCells(int, int)} is invoked with that {@link #zLineVisible}.  
   * <li>2015-11-19 Hartmut chg: Some changes for future: The cells should be managed in the GralTable, not in SwtTable. Adaption for AwtTable 
   * <li>2015-10-29 Hartmut chg: {@link TableLineData#bChangedSet}, the content will be written in {@link org.vishia.gral.swt.SwtTable}
   *   or another graphic implementation only if the text is changed. Therewith editing a table is possible while the other cells
   *   are updated frequently. That can be used for tables which shows a current state, and a column is editable.
   *   See {@link org.vishia.guiInspc.InspcViewTargetComm}.
   * <li>2015-08-28 Hartmut chg: {@link #processKeys(int)}: Use tab and sh-tab to switch between the columns. 
   *   This keys are used as 'traverse key' normally, but they are ignored for that now by a proper TraverseListener
   *   in {@link org.vishia.gral.swt.SwtTable}. The standard traverse listener which is active for traversing between the
   *   text fields of the table does not set the correct {@link #colSelectedixCellC} which is necessary for accept the column.
   * <li>2015-08-28 Hartmut new: Search in columns:
   *   <ul><li>Use the keys without shiftDigit-designation if it is a text key. It is not possible to use elsewhere.
   *       <li>{@link #searchContent(boolean)}: If the search string starts with '*' it searches 'contains(...)'.
   *   </ul>    
   * <li>2015-07-12 Hartmut new: {@link TableLineData#setBackColor} with colorSelect etc. for content-depending table presentation. 
   * <li>2015-06-21 Hartmut chg: Implementation of {@link TableLineData#setBackColor(GralColor, int}} 
   *   now regards the ix-argument as cell index, like defined in comment.
   * <li>2015-05-31 Hartmut chg: {@link GraphicImplAccess#processKeys(int}} updates the cell text in the line firstly
   *  if it is an editing cell. It helps to get the correct text in the graphic thread.  
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
   *   preparing tree view. {@link GralTable.TableLineData#tbl_treeDepth}, {@link GralTable.TableLineData#childLines} for tree view.
   * <li>2013-11-02 Hartmut chg: {@link GralTable.TableLineData} is static now with outer reference, more transparent
   * <li>2013-10-06 Hartmut chg: call {@link #actionOnLineSelected(int, GralTableLine_ifc)} only if the line is changed, 
   *   not on focus without changed line.
   * <li>2013-09-15 Hartmut new: Implementation of {@link #setBackColor(GralColor, int)}  
   *   with special comments for usage of the int parameter.
   *   See {@link GralTable_ifc#setBackColor(GralColor, int)}, Adequate {@link #getBackColor(int)}. 
   * <li>2013-09-14 Hartmut chg: {@link GraphicImplAccess} implements now {@link GralWidgImplAccess_ifc} without any other
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
  @SuppressWarnings("hiding") protected final static String sVersion = "2023-02-12";

  
  protected int keyMarkUp = KeyCode.shift + KeyCode.up, keyMarkDn = KeyCode.shift + KeyCode.dn;
  
  protected int keyOpenChild = KeyCode.right, keyCloseChild = KeyCode.left;
  
  /**The separator for {@link TreeNodeBase#getNode(String, String)} for nested nodes.
   * This can be set for special tables. Default is null because no tree-table.
   * @since 2019-04 The older version with "/" as separator were never used. It was thought for file trees.
   */
  protected String keySeparator = null; //"/";
  
  //protected final Map<String, TableLineData> idxLine = new TreeMap<String, TableLineData>();
  
  /**This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnLineSelected;
  
  /**This action will be called any time when children of a tree node line should be refreshed. 
   * The {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnRefreshChildren;
  
  
  public long timeLastKeyUpDn;
  
  
  /**Width of each column in GralUnits. */
  protected int[] columnWidthsGral;
  
  /**Set if relative column widths are used.
   * null if only absolute.
   * Set with 
   */
  protected int[] columnWidthsPromille;
  
  protected boolean[] bColumnEditable;
  
  protected GralMenu[] contextMenuColumns;
  

  
  GraphicImplAccess gi;

  
  /**The currently selected cell. */
  protected int lineSelectedixCell, colSelectedixCellC;
  
  protected int lineSelectedixCellLast;
  
  protected int lineSelectedNewixCell, colSelectedNewixCellC;
  
  /**Array of lines which are currently associated to the cells of the implementation layer.
   * It means they are the showing lines. Note that the TableLineData referred with the {@link #rootLine}
   * are all of the lines of the table. They are not associated to any graphic widget any time.
   * Whether they are associated to a graphic widget or not is able to evaluate with this array.
   * The cellLines[0] is the TableLineData of the first visible line in any case etc.
   */
  protected TableLineData[] linesForCell, linesForCellPrev;
  long bitLinesForCellChanged;
  
  boolean bChangedLinesForCell;

  /**The current line. */
  protected TableLineData lineSelected;
  
  
  /**The line which is selected by mouse pressing, for right mouse menu. */
  protected TableLineData lineSelectedNew;
  
  /**Set to true if a fillin is pending. */
  protected boolean fillinPending;

  
  /**Number of lines and columns of data. */
  protected int zLine, zColumn;
  
  /**Current number of visible lines in the view. */
  protected int zLineVisible;
  
  protected int zLineVisibleMax;
  
  /**Number of lines and number of the current line. It is -1 if this numbers should be evaluated from {@link #rootLine}
   */
  private int zLineCurr = 0, nLineFirst = -1;
  
  
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
  protected long lastKeyTime;
  /**The last key which was pressed  */
  protected int lastKeyCode;
  
  /**If set, then a next key will be processed. It is set to false if a key event is executed
   * and it is set to true in {@link #keyActionDone}. */
  protected boolean keyDone = true;
  

  
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
  
  /**The color of that lines which are marked. */
  protected GralColor colorBackMarked2;
  /**The color of the current, the selected line if it is marked. */
  protected GralColor colorBackSelectMarked2;
  /**The color of that lines which's some children are marked. */
  protected GralColor colorBackSomeMarked2;
  /**The color of the current selected line if some children of it are marked. */
  protected GralColor colorBackSelectSomeMarked2;
  
  protected GralColor colorBackSelectNew;
  protected GralColor colorBackSelectNewMarked;
  
  protected GralColor colorBackFillPending;
  
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
   * see {@link #GralTable(String, int, int[])}, zLineMax is 50. 
   */
//  public GralTable ( String posName, int[] columnWidths) {
//    this(posName, 50, columnWidths);
//  }
  
  
  
  
  /**Constructs a table which should be positioned on {@link #setToPanel(GralMngBuild_ifc)}
   * @param currPos Position used for current, maybe null
   * @param posName String given position in GralPos units and name, see {@link GralWidget#GralWidget(GralPos, String, char)}
   * @param zLineMax maximal number of lines managed for content. It should be related to the size of viewing. 10..50 is proper.
   * @param columnWidths positive value from left: width, negative value from right: width. 
   *   The last column with a positive width is used for sizeable. 
   */
  public GralTable ( GralPos currPos, String posName, int zLineMax, int[] columnWidths) {
    super(currPos, posName, 'L');
    this.columnWidthsGral = columnWidths;
    this.zColumn = columnWidths.length;
    this.bColumnEditable = new boolean[this.zColumn];  //all false.
    
    this.rootLine = new NodeTableLine("", null);
    this.rootLine.tbl_showChildren = true;
    this.rootLine.tbl_treeDepth = -1; //children of rootline are level 0
    this.rootLine.tbl_zLineUnfolded = 0;  //count added line.
    
    @SuppressWarnings("unchecked")
    TableLineData[] linesForCell1 = (TableLineData[])Array.newInstance( TableLineData.class, zLineMax );
    this.linesForCell = linesForCell1; //Hint: linesForCell = new GralTable<UserData>.TableLineData[50]; does not work because TableLineData is generic.
    TableLineData[] linesForCell2 = (TableLineData[])Array.newInstance( TableLineData.class, zLineMax );
    this.linesForCellPrev = linesForCell2;
    this.zLineVisibleMax = this.linesForCell.length;
    setColors();
  }

  
//  public GralTable ( String posName, int zLineMax, int[] columnWidths) {
//    this(null, posName, zLineMax, columnWidths);
//  }  
  
  /**Constructs a table with position
   * @param pos String given position in GralPos units.
   * @param name to registrate
   * @param columnWidths positive value from left: width, negative value from right: width. 
   *   The last column with a positive width is used for sizeable. 
   */
//  public GralTable ( String pos, String name, int[] columnWidths) 
//  { this(pos !=null ? ((pos.startsWith("@") ? "" : "@") + pos + "=" + name) : name, columnWidths); }
  
  
  
  public int nrofLinesVisibleMax(){ return this.zLineVisibleMax; }
  
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
    this.actionMarkOnLine = action;
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
    this.keyMarkUp = up; this.keyMarkDn = dn;
  }
  
  
  

  
  /**Adds a context menu entry for the given column.
   * It is an abbreviation for {@link #getContextMenuColumn(int)} 
   * and {@link GralMenu#addMenuItem(String, String, GralUserAction)}.
   * The context menu is valid for the column of the whole table independent of the line. In the 
   * {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} method (param action) the given widget
   * is the whole table. To get the line where the mouse was pressed:
   * <ul>
   * <li>{@link #getLineMousePressed()} returns the line where the mouse was pressed to open the context menu
   * <li>{@link #getCurrentLine()} returns the line which was selected before. It is possible especially to work with both lines.
   *   The mouse button for the context menu does not change the current line of the table.
   * </ul>
   *  
   * @param col The column, see {@link #getContextMenuColumn(int)}
   * @param identArgJbat The name of the entry, see {@link GralMenu#addMenuItem(String, String, GralUserAction)}
   * @param sMenuPath same like {@link GralMenu#addMenuItem(String, String, GralUserAction)}
   * @param action the action invoked on menu entered.
   */
  public void addContextMenuEntry(int col, String menuname, String sMenuPath, GralUserAction action){
    GralMenu menu = getContextMenuColumn(col);
    menu.addMenuItem(this, menuname, sMenuPath, action);
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
   * @return The menu to add {@link GralMenu#addMenuItem(String, String, GralUserAction)}.
   */
  public GralMenu getContextMenuColumn(int col){
    if(this.contextMenuColumns == null){
      this.contextMenuColumns = new GralMenu[this.zColumn];
    }
    if(this.contextMenuColumns[col] == null){
      this.contextMenuColumns[col] = new GralMenu(this); //gi.createColumnMenu(col); //for all cells of this column
    }
    return this.contextMenuColumns[col];
  }

  
  public void setContextMenuColumn(int col, GralMenu menu){
    if(this.contextMenuColumns == null){
      this.contextMenuColumns = new GralMenu[this.zColumn];
    }
    this.contextMenuColumns[col] = menu; //gi.createColumnMenu(col); //for all cells of this column
  }
  
  /**Adds a context menu entries to all cells of the designated column. This method can't be used 
   * if either {@link #getContextMenuColumn(int)} or {@link #addContextMenuEntry(int, String, String, GralUserAction)}
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
   * @return The menu to add {@link GralMenu#addMenuItem(String, String, GralUserAction)}.
   */
  public GralMenu getContextMenuColumnCells(int col, String name, String sMenuPath, GralUserAction action){
    return null;
  }
  
  
  public void setColumnWidth(int width, int[] columnWidths){
    this.columnWidthsGral = columnWidths;
  }
  
  /**Specify a proportional width for each column. 
   * The absolute values given on construction or {@link #setColumnWidth(int, int[])} are used as minimal values.
   * Hence a value of 0 for width means, the given absolute width should be used anytime. 
   * <br>
   * The sum of all here given widths is the size of all columns on proportion to the here given column values.
   * 
   * @param widths
   */
  public void setColumnProportional(int[] widths){
    this.columnWidthsPromille = widths;
  }
  
  
  /**Sets the specified column to edit-able or not. This routine can be invoked before the graphic implementation
   * is created (before {@link #setToPanel(GralMngBuild_ifc)} or after them in running mode. 
   * 
   * @param column the number, starting by 0. 
   * @param val true then edit-able, false then read only.
   * @throws IndexOutOfBoundsException on faulty column.
   */
  public void setColumnEditable(int column, boolean val){
    this.bColumnEditable[column] = val;
    if(((GralWidget)this)._wdgImpl !=null){
      this.dyda.setChanged(GraphicImplAccess.chgEditableColumn);
      redraw(this.redrawtDelay, this.redrawDelayMax);
    }
  }
  
  @Override public boolean setCurrentLine(String key){
    Object oLine = this.rootLine.getNode(key, this.keySeparator);
    if(oLine instanceof GralTableLine_ifc) {
      @SuppressWarnings("unchecked") 
      GralTableLine_ifc<UserData> line = (GralTableLine_ifc<UserData>)oLine;
      setCurrentLine(line, this.lineSelectedixCell, -1);
      return true;
    } else return false;
  }
  
  //@Override public boolean setCurrentLine(int nLine){ return setCurrentCell(nLine, -1); }

  
  void setColors(){
    this.colorBackSelect = GralColor.getColor("lam");
    this.colorBackSelectNew = GralColor.getColor("lbl");
    
    this.colorBackMarked = GralColor.getColor("lrd");
    this.colorBackSelectMarked = GralColor.getColor("rd");
    
    this.colorBackSomeMarked = GralColor.getColor("pma");
    this.colorBackSelectSomeMarked = GralColor.getColor("lma");
    
    this.colorBackMarked2 = GralColor.getColor("lor");
    this.colorBackSelectMarked2 = GralColor.getColor("or");
    
    this.colorBackSomeMarked2 = GralColor.getColor("por");
    this.colorBackSelectSomeMarked2 = GralColor.getColor("por2");
    
    this.colorBackSelectNewMarked = GralColor.getColor("lpu");
    this.colorBackFillPending = GralColor.getColor("ma");
    this.dyda.backColor = GralColor.getColor("wh");
    this.dyda.backColorNoFocus = GralColor.getColor("pgr");
    //colorBackSelectNonFocused = GralColor.getColor("am");
    //colorBackMarkedNonFocused = GralColor.getColor("lrd");
    //colorBackTableNonFocused = GralColor.getColor("gr");
    this.dyda.textColor = GralColor.getColor("bk");
    this.colorSelectCharsBack = GralColor.getColor("lgr");
    this.colorSelectChars = GralColor.getColor("wh");
    this.colorBackVscrollbar = GralColor.getColor("lgr");
    int rd1,gn1, bl1, rd2, gn2, bl2;
    bl1 = this.colorBackVscrollbar.getColorValue();
    rd1 = (bl1 >>16) & 0xff;
    gn1 = (bl1 >>8) & 0xff;
    bl1 = bl1 & 0xff;
    this.colorSliderVscrollbar[0] = GralColor.getColor("bl");
    bl2 = this.colorSliderVscrollbar[0].getColorValue();
    rd2 = (bl1 >>16) & 0xff;
    gn2 = (bl1 >>8) & 0xff;
    bl2 = bl1 & 0xff;
    
    for(int ix=1; ix < this.colorSliderVscrollbar.length; ++ix){
      float r = 0.8f * ((float)(ix)) / (this.colorSliderVscrollbar.length);  //max. 0.8
      int rd = rd2 + (int)((rd1 - rd2) * r);
      int gn = gn2 + (int)((gn1 - gn2) * r);
      int bl = bl2 + (int)((bl1 - bl2) * r);
      this.colorSliderVscrollbar[ix] = GralColor.getColor(rd, gn, bl);
    }
  }
  
  
  
  
  /**Sets the focus of the table.
   * It sets an information that the selected cell should focused while drawing ({@link GraphicImplAccess.CellData#bSetFocus}).
   * Than it invokes the overridden super.{@link GralWidget#setFocus(int, int)}. 
   * That method asserts the visibility of the table and calls {@link GralWidgImplAccess_ifc#setFocusGThread()}
   * That method is implemented in the implementation widget layer and causes a redraw which focused
   * the correct cell. 
   * @see org.vishia.gral.base.GralWidget#setFocus(int, int)
   */
  @Override public void setFocus(int delay, int latest){
    if(this.gi !=null){
      this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true;  //will be processed on redraw
      super.setFocus(delay, latest);   // From GralWidget
    }
  }
  
  

  
  
  @Override
  public void setCurrentLine(GralTableLine_ifc<UserData> line, int ixline, int ixcolumn) {
    int ixline1 = ixline >= 0 ? ixline: this.zLineVisible + ixline;  //negative: -1 is the last line.
    if(ixline1 < 0) { ixline1 = 0;}
    if(ixline1 >= this.zLineVisible) { ixline1 = this.zLineVisible-1; }
    if(this.zLineVisible >0) {
      assert(ixcolumn < this.gi.cells[0].length);
      this.lineSelected = (TableLineData)line;
      this.nLineFirst = -1;  //get new, undefined elsewhere.
      actionOnLineSelected(KeyCode.userSelect, this.lineSelected);
      this.lineSelectedixCellLast = this.lineSelectedixCell;
      this.lineSelectedixCell = ixline1;
      if(this.lineSelectedixCell >= this.zLineVisible){
        this.lineSelectedixCell = this.zLineVisible -1;
      }
      if(ixcolumn >=0){
        this.colSelectedixCellC = ixcolumn;
      }
      this.bPrepareVisibleArea = true;
      redraw();
    } else {
      this.gralMng().log().writeWarning("table not visiable");
    }
  }

  
  public void setCurrentColumn(int col) {
    this.colSelectedixCellC = col;     // remark the focused column.
    if(this.gi !=null) {               // the flag bSetFocus sets the focus to this cell on redraw, after them set to false.
      this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true;  
    }
    redraw();
  }
  
  
  /**Sets the color of the currently selected line. 
   * @param color
   */
  public void setColorBackSelectedLine(GralColor color){ 
    this.colorBackSelect = color; 
    redraw(100, 0);
  }

  /**Quest whether the table is in a fillin pending state,
   * see {@link #fillinPending(boolean)}.
   * @return the state true if pending. 
   */
  public boolean fillinPending() { return this.fillinPending; }
  
  /**Sets the table to a fillin pending state or in a not pending state 
   * This is if another thread sets the line data, or create new lines,
   * which may need a while. 
   * @param set true: set to pending, false: set to ready.
   * @return pending state before. 
   */
  public boolean fillinPending(boolean set) {
    boolean ret = this.fillinPending;
    this.fillinPending = set;
    return ret;
  }

  
  /**Returns the current selected line. */
  @Override
  public TableLineData getCurrentLine() {
    return this.lineSelected;
  }

  
  /**Returns the temporary selected line while pressing the mouse button.
   * This method is able to use especially in mouse actions of user level.
   * On pressing any mouse button the line on mouse position is set independent of the current line.
   * The {@link #lineSelected()} is unchanged in this moment. 
   * @return The line where the mouse button is pressed.
   */
  public TableLineData getLineMousePressed(){ return this.lineSelectedNew; }
  
  
  

  @SuppressWarnings("unchecked") @Override
  public TableLineData getLine ( String key) {
    Object oRootline = this.rootLine.getNode(key, this.keySeparator);
    if(oRootline instanceof GralTable.TableLineData){ return (TableLineData) oRootline; }
    else return null;  //unexpected.
  }


  
  public GralTableLine_ifc<UserData> getFirstLine ( ) {
    return this.rootLine.firstChild();
  }


  
  /**Gets the text of any cell which is in focus. 
   * This is a special routine: If no line is selected, it is possible that a text in a cell were written which is not assiciated to a line.
   * @return
   */
  public String getCellTextFocus() {
    if(this.gi !=null) return this.gi.getCellTextFocus();
    else return null;
  }
  

  
  /**Returns the column which is in focus on an action,
   * @return -1 if implementation does not exists, else 0...
   */
  public int getColumnInFocus() {
    if(this._wdgImpl !=null) { return ((GralTable.GraphicImplAccess) this._wdgImpl).ixColumnFocus; }
    else return -1; //nothing in foucs
  }
  
  
  /**Gets the index in the visual presentation of the given line.
   * @param line any line of this table, maybe null, maybe a line from any other table
   * @return 0 for the first line, 1..., -1 if not found respectively the line is not in the visible area. 
   *  Return index of the first not defined line (current length of table) if line==null
   */
  public int getIxLine(GralTableLine_ifc<UserData> line) {
    for(int ix = 0; ix < this.linesForCell.length; ++ix) {
      TableLineData line1 = this.linesForCell[ix];
      if(line1 == line) return ix;
    }
    return -1; //not found;
  }
  
  
  
  /**Insert a new line before the current line, or as first line if the table is empty.
   * Note that the lines in the table are organized in a tree structure. 
   * The line is inserted in the node where the current line is found.
   */
  @Override public TableLineData insertLine(String lineKey, int row, String[] lineTexts, UserData userData) {
    if(row ==0){
      //insert on top
      TableLineData lineBehind = this.rootLine.firstChild();
      if(lineBehind ==null){
        return this.rootLine.addChildLine(lineKey, lineTexts, userData);
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
    
    if(row > this.zLine || row < 0){
      row = this.zLine;
    }
    if(row == 0){
      this.rootLine.addNodeFirst(line);  //addOnTop
    }
    if(this.lineSelected == null){ 
      this.lineSelected = line;
      actionOnLineSelected(KeyCode.defaultSelect, this.lineSelected);
    }
    this.zLine +=1;
    if(this.zLineCurr >= 0){ 
      this.zLineCurr +=1;
    }
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    this.bPrepareVisibleArea = true;
    redraw(100, 0);
    return line;
  }

  
  
  /**
   * @see GralTable_ifc#addLine(String, String[], Object)
   */
  @Override public TableLineData addLine(String lineKey, String[] lineTexts, UserData userData) {
    return this.rootLine.addChildLine(lineKey, lineTexts, userData);
  }
    
    
  public TableLineData XXXaddLine(String key, String[] cellTexts, UserData userData) {
    
    TableLineData line = this.new TableLineData(key, userData);
    if(this.lineSelected == null){ //// 
      this.lineSelected = line;
      actionOnLineSelected(KeyCode.defaultSelect, this.lineSelected);
      if(this.zLineCurr == -1){ 
        this.zLineCurr = 0;
        this.nLineFirst = 0;
      }
    }
    if(this.zLineCurr >= 0){
      this.zLineCurr +=1;
    }
    this.zLine += 1;
    this.rootLine.addNode(line);
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    this.bPrepareVisibleArea = true;
    redraw();
    return line;
  }

  
  
  
  @Override public void deleteLine(GralTableLine_ifc<UserData> line) {
    line.deleteLine();
  }
  
  
  /**Returns in iterable object for the lines which can used in a for-element expression
   * or in a {@link Iterator} usage.
   * Because the class {@link TableLineData} implements the {@link GralTableLine_ifc}
   * the user can write for example: <pre>
   * for(GralTableLine_ifc<MyData> line: widgTableVariables.iterLines()) {
   *    MyData data = line.getUserData();
   *    line.setBackColor(colorXY, 0);
   *  }
   * </pre>
   * @return
   */
  public IterableIterator<TableLineData> iterLines(){
    return this.rootLine.iterator();
  }
  
  
  
  @Override public int size(){ 
    if(this.zLineCurr < 0) {
      //TODO count
    }
    return this.zLine; 
  }
  

  @Override public void clearTable() {
    this.colSelectedixCellC = 0;
    this.zLine = 0;
    this.zLineCurr = 0;
    this.nLineFirst = -1;
    this.lineSelected = null;
    this.lineSelectedixCell = this.lineSelectedixCellLast = 0;
    actionOnLineSelected(KeyCode.removed, this.lineSelected);
    this.searchChars.setLength(0);
    for(int ix = 0; ix < this.linesForCell.length; ++ix){
      this.linesForCell[ix] = null;
    }
    this.bChangedLinesForCell = true;
    this.rootLine.clear();
    this.bPrepareVisibleArea = true;
    redraw(200,200);
  }

  
  
  
  
  @Override public List<GralTableLine_ifc<UserData>> getMarkedLines(int mask) {
    List<GralTableLine_ifc<UserData>> list = new LinkedList<GralTableLine_ifc<UserData>>();
    
    for(TableLineData item: this.rootLine.iterator()){
      if(item.nd_data instanceof MarkMask_ifc){
        if((((MarkMask_ifc)item.nd_data).getMark() & mask) !=0){
          list.add(item);
        }
      }
      else if((item.getMark() & mask) !=0){
        list.add(item);
      }
    }
    return list;
  }

  
  @Override public TreeNode_ifc<?, UserData> getAllLines() { return this.rootLine; }
  
  
  @Override public List<UserData> getListContent() {
    List<UserData> list = new LinkedList<UserData>();
    for(TableLineData item: this.rootLine.iterator()){
      list.add(item.getUserData());
    }
    return list;
  }

  
  @Override public GralTableLine_ifc<UserData> getFirstMarkedLine(int mask) {
    for(TableLineData item: this.rootLine.iterator()){
      if((item.getMark() & mask) !=0){
        return item;
      }
    }
    return null;
  }

  
  
  
  
  /**Fills the #linesForCell[] with the appropriate lines from the table.  
   * It uses #lineSelectixCell as preferred for the current line,
   * but this is only an estimation. 
   * The algorithm searches the real position of the line in the table backward.
   */
  protected void fillVisibleArea(){
    if(this.lineSelected ==null) {
      this.lineSelected = (TableLineData)getFirstLine();
    }
    TableLineData line = this.lineSelected;
    int ix = this.lineSelectedixCell;
    this.bitLinesForCellChanged = (1<<this.lineSelectedixCellLast);
    int ixPrev = 0;
    this.linesForCellPrev[ixPrev] = line;
    while(--ix >=0 && line !=null){                           // filles linesForCell[0..ix-1] 
      line = prevLine(line);
      if(line !=null) {
        this.linesForCellPrev[++ixPrev] = line;
      }
    }
    ix = 0;
    int nLinesBefore = ixPrev;
    while(ixPrev >=0) {
      if(this.linesForCell[ix] != this.linesForCellPrev[ixPrev]) {
        this.linesForCell[ix] = this.linesForCellPrev[ixPrev];
        this.bitLinesForCellChanged |= 1<<ix;
      }
      ix +=1; ixPrev -=1;
    }
                       
//    int nLinesBefore = this.lineSelectedixCell - ix;  //==lineStart if all lines are present.
//    if(ix > 0){
//      System.arraycopy(this.linesForCell, ix, this.linesForCell, 0, nLinesBefore);
//      this.lineSelectedixCell = nLinesBefore;
//    }
    this.bChangedLinesForCell = true;
    fillVisibleAreaBehind(this.lineSelected, nLinesBefore);
  }
  
  
  
  /**Fills the {@link #linesForCell} starting from the current line down.
   * This is called in {@link #fillVisibleArea()} but also if a line is folded/unfolded.
   * Then only the following lines are influenced.
   * @param lineStart from this line in the whole {@link #rootline} container. 
   * @param ixStart index in the graphical rows in the table.
   */
  protected void fillVisibleAreaBehind(TableLineData lineStart, int ixStart){
    int ix = ixStart;
    TableLineData line = lineStart;
    while(line !=null && ix < this.zLineVisible) {
      if(this.linesForCell[ix] != line) {
        this.bitLinesForCellChanged |= 1<<ix;
        this.linesForCell[ix] = line;
      }
      line = nextLine(line);
      ix +=1;
    } 
    while(ix < this.zLineVisible){
      this.linesForCell[ix++] = null;
    }
    this.bChangedLinesForCell = true;
    
  }
  

  
  /**Shifts the content in {@link #linesForCell} to show the appropriate part of data 
   * (e.g. lines of {@link #rootLine})in the table.
   * @param dLine >0 shift up to get next lines, <0 shift down to get previous lines
   * @return nr of lines shifted
   */
  protected int shiftVisibleArea(int dLine){
    int dLine1 = dLine;
    if(dLine >0){ //forward in lines
      TableLineData line = this.linesForCell[this.zLineVisible -1];  //the last line
      while(line !=null && dLine1 >0){
        line = nextLine(line);
        if(line !=null){
          System.arraycopy(this.linesForCell, 1, this.linesForCell, 0, this.zLineVisible-1);
          this.linesForCell[this.zLineVisible-1] = line;
          dLine1 -=1;
          this.bChangedLinesForCell = true;
        }
      }
    } else if(dLine < 0){ //backward in lines
      TableLineData line;
      line = this.linesForCell[0];  //the new start line
      if(line == null){
        line = this.rootLine.firstChild(); //outer.tableLines.size() ==0 ? null : outer.tableLines.get(0);  //on init
      }
      while(line !=null && dLine1 < 0){
        line = prevLine(line);
        if(line !=null){
          System.arraycopy(this.linesForCell, 0, this.linesForCell, 1, this.zLineVisible-1);
          this.linesForCell[0] = line;
          dLine1 +=1;
          this.bChangedLinesForCell = true;
        }
      }
    }
    int nLineShift = dLine - dLine1;
    if(nLineShift !=0){
      this.nLineFirst += nLineShift;
    }
    return nLineShift;
  }
  
  
  TableLineData nextLine(TableLineData lineP){
    TableLineData line = lineP;
    TableLineData linenext = null;
    if(line.tbl_showChildren){
      linenext = line.firstChild();         //deeper to any children
    }
    if(linenext == null){ //no children found
      linenext = line.nextSibling();  //may go to next root
    }
    while(linenext == null //no sibling found
      && line.parent() !=null  
      && !line.parentEquals(this.rootLine)) 
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
      if(!line.parentEquals(this.rootLine)){
        line = line.parent();
      } else {
        line = null;  //on root as first entry
      }
    } else {
      //if(line2 !=null){ line2 = line2.prevSibling(); } //the parent is the current parent, use previous.
      while(line2 !=null && line2.tbl_showChildren){
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
    this.lineSelectedNew = this.linesForCell[cell.ixCellLine]; 
    redraw(0,0);  //immediately, it is in the graphic thread.
  }


  /**Invoked in the graphic thread from mouse listener in {@link GraphicImplAccess#mouseUpGral(int, CellData)}
   * @param key
   * @param cell
   */
  protected void mouseUp(int key, CellData cell){
    if(key == KeyCode.mouse1Up){
      this.lineSelected = this.lineSelectedNew;
      this.nLineFirst = -1;  //get new
      this.lineSelectedNew = null;
      this.lineSelectedixCellLast = this.lineSelectedixCell;
      this.lineSelectedixCell = cell.ixCellLine;  //used for key handling.
      this.colSelectedixCellC = cell.ixCellColumn;
      actionOnLineSelected(KeyCode.userSelect, this.lineSelected);
      redraw();
    }
  }



  /**Invoked in the graphic thread from mouse listener in {@link GraphicImplAccess#mouseDoubleGral(int, CellData)}
   * @param key
   * @param cell
   */
  protected void mouseDouble(int key, CellData cell){
    ActionChange action1 = getActionChangeStrict(ActionChangeWhen.onMouse1Double, true);
    if(action1 !=null
      && action1.action().exec(KeyCode.mouse1Double, this.linesForCell[cell.ixCellLine])  //executes the action.
      ) {
        //it is sufficient.
    } else {
      processKeys(KeyCode.mouse1Double);
    }
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
    String search = this.searchChars.toString();
    boolean found = false;
    if(search != null && search.length() !=0) { // return false;
      boolean contSearch = true;
      TableLineData line2, line = this.lineSelected;
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
          this.lineSelected = line;  //show the first or last line
          this.nLineFirst = -1;  //get new, undefined elsewhere.
          found = search.length() >0;
        } else {
          line = line2;
          String sTextLo = line.getCellText(this.colSelectedixCellC).toLowerCase();
          String sText = line.getCellText(this.colSelectedixCellC);
          if( search.length() == 0 || search.equals('*')){   //always found if no searchchar is given.
            found = false;
            contSearch = false;
          } else if(  sTextLo.startsWith(search)
                   || search.charAt(0)== '*' && sText.contains(search.substring(1))
            ){  //found
            found = true;
            this.lineSelected = line;
            this.nLineFirst = -1;  //get new, undefined elsewhere.
            this.bPrepareVisibleArea = true;
            contSearch = false;                   //found
          } else {
            found = false;
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
    TableLineData line = this.linesForCell[celldata.ixCellLine];
    if(line !=null && !sText.equals(line.cellTexts[celldata.ixCellColumn])){
      line.cellTexts[celldata.ixCellColumn] = sText;
      line.bChangedEdit[celldata.ixCellColumn] = true;
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
    //TODO measure the calculation time load here with fast key repetition.
    //this is a proper test for speed of graphic presentation.
    //It writes many cells especially if shifts the table.
    if(keyCode == KeyCode.enter)
      Debugutil.stop();
    boolean done = true;
    long time = System.currentTimeMillis();
    this.lineSelectedNew = null;  //on any key, clear highlighted mouse down cell, if it is set yet.
    //NOTE: prevent to fast key action if the last redraw is yet finished.
    //The draw needs to much time in Linux-GTK with an Atom processor (Lenovo)
    long timeDiff = time - this.lastKeyTime;
    boolean bSameKey = this.lastKeyCode == keyCode;
    this.lastKeyCode = keyCode;
    if(bSameKey && timeDiff < 50) {
      //System.out.print(" " + timeDiff);
      return false;                              // ignore to high frequently key pressing
    }
    this.lastKeyTime = time;
    this.lineSelectedixCellLast = this.lineSelectedixCell;  
    //
    if( this.keyDone || keyCode == KeyCode.mouse1Double || timeDiff > -11110){  //use 50 ms for timeout if keyDone isn't set.  
      this.keyDone = false;                              // execution pending
      switch(keyCode){
      case KeyCode.tab: {                                // tab and sh-tab switches between columns
        if(this.colSelectedixCellC < this.columnWidthsGral.length-1) { 
          this.colSelectedixCellC +=1; 
        }
      } break;
      case KeyCode.shift + KeyCode.tab: {
        if(this.colSelectedixCellC >= 1) { 
          this.colSelectedixCellC -=1; 
        }
      } break;
      case KeyCode.pgup: {
        if(this.lineSelectedixCell > 2){
          this.lineSelectedixCell = 2;
        } else {
          int shifted = shiftVisibleArea(-this.zLineVisible);  //shifted = -1 if all shifted
          this.lineSelectedixCell -= this.zLineVisible + shifted;
          if(this.lineSelectedixCell <0){
            this.lineSelectedixCell = 0;  //limit it on top.
          }
        }
        this.lineSelected = this.linesForCell[this.lineSelectedixCell];
        this.nLineFirst = -1;  //get new, undefined elsewhere.
        //the table has the focus, because the key action is done only if it is so.
        //set the new cell focused, in the paint routine.
        this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true; 
        this.keyActionDone.processEvent(null);
        //keyActionDone.timeOrder.activate(100);             // activate the redraw in 100 ms to prevent too much redraw.
      } break;
      case KeyCode.mouseWheelUp:                           // cursor or mouse wheel to select lines
      case KeyCode.up: {
        if(keyCode != KeyCode.mouseWheelUp && (time - this.timeLastKeyUpDn) < 80) {
          System.out.print("ö");
          done = true;                                     // prevent too much reaction if the key repetition rate is high
        } else {
          this.timeLastKeyUpDn = time;
          if(!searchContent(true)) {
            if(this.lineSelectedixCell > 2){
              this.lineSelectedixCell -=1;
            } else {
              int shifted = shiftVisibleArea(-1);  //shifted = -1 if all shifted
              this.lineSelectedixCell -= 1 + shifted;
              if(this.lineSelectedixCell <0){
                this.lineSelectedixCell = 0;  //limit it on top.
              }
            }
            this.lineSelected = this.linesForCell[this.lineSelectedixCell];
          }
          //------------------------------------------------------------------- the table has the focus, because the key action is done only if it is so.
          this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true; // set the new cell focused, in the paint routine.
          this.keyActionDone.processEvent(null);                // if it is called immediately the last cell gets the focus return, not clarified
          //keyActionDone.timeOrder.activate(50);            // activate the redraw in 50 ms, only then the focus handling is correct, not clarified why.
          done = true;
        }
      } break;
      case KeyCode.pgdn: {
        if(this.lineSelectedixCell < this.zLineVisible -3){
          this.lineSelectedixCell = this.zLineVisible -3;
        } else {
          int shifted = shiftVisibleArea(this.zLineVisible);
          this.lineSelectedixCell += this.zLineVisible - shifted;
          if(this.lineSelectedixCell >= this.zLineVisible){
            this.lineSelectedixCell = this.zLineVisible -1;  //limit it on top.
          }
        }
        while( (this.lineSelected = this.linesForCell[this.lineSelectedixCell]) ==null
            && this.lineSelectedixCell >0    
            ){
          this.lineSelectedixCell -=1;
        }
        this.nLineFirst = -1;  //get new, undefined elsewhere.
        //the table has the focus, because the key action is done only if it is so.
        //set the new cell focused, in the paint routine.
        this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true; 
        this.keyActionDone.processEvent(null);
        //keyActionDone.timeOrder.activate(100);             // activate the redraw in 100 ms to prevent too much redraw.
      } break;
      default:
        if(keyCode == KeyCode.shift + KeyCode.enter) {     // The key combination sh+Enter is up to now (2023-01) used as right mouse:
          int col = this.getColumnInFocus();               // gets the context menu of the selected column
          GralMenu menu = this.getContextMenuColumn(col);
          if(menu == null) {
            menu = this.getContextMenu();                  // if not exists get the context menu of the table
          }
          if(menu !=null) {
            menu.setVisible();
          }
        }
        else if(keyCode == KeyCode.dn || keyCode == this.keyMarkDn || keyCode == KeyCode.mouseWheelDn
            ) {                                            // arrow down, mouse wheel down
          if(keyCode != KeyCode.mouseWheelDn && (time - this.timeLastKeyUpDn) < 80) {
            System.out.print("ö");
            done = true;                                     // prevent too much reaction if the key repetition rate is high
          } else {
            this.timeLastKeyUpDn = time;
            if(keyCode == this.keyMarkDn && this.lineSelected !=null){
              GralTableLine_ifc<?> line = this.lineSelected;    //mark the lines
              if((line.getMark() & FileMark.select)!=0){
                //it is selected yet
                line.setNonMarked(FileMark.select, line.getUserData());
              } else {
                line.setMarked(FileMark.select, line.getUserData());
              }
            }
            if(!searchContent(false)) {
              if(this.lineSelectedixCell < this.zLineVisible -3){
                this.lineSelectedixCell +=1;
              } else {
                int shifted = shiftVisibleArea(1);
                this.lineSelectedixCell += 1 - shifted;
                if(this.lineSelectedixCell >= this.zLineVisible){
                  this.lineSelectedixCell = this.zLineVisible -1;  //limit it on top.
                }
              }
              while( (this.lineSelected = this.linesForCell[this.lineSelectedixCell]) ==null
                  && this.lineSelectedixCell >0    
                  ){
                this.lineSelectedixCell -=1;
              }
              this.nLineFirst = -1;  //get new, undefined elsewhere.
            }
            //------------------------------------------------------------------- the table has the focus, because the key action is done only if it is so.
            this.gi.cells[this.lineSelectedixCell][this.colSelectedixCellC].bSetFocus = true; // set the new cell focused, in the paint routine.
            this.keyActionDone.processEvent(null);              // should be done immediately because some times content of cells are faulty.
            //keyActionDone.timeOrder.activate(50);          // activate the redraw in 100 ms to prevent too much redraw.
            done = true;
          }
        } else if(KeyCode.isTextKey(keyCode) && !this.bColumnEditable[this.colSelectedixCellC]){
          keyCode &= ~KeyCode.shiftDigit;                //The keycode is valid without shift-designation.
          this.searchChars.appendCodePoint(keyCode);
          searchContent(false);
          redraw();
        } else if(keyCode == KeyCode.esc){               // --- esc cleans the search chars 
          this.searchChars.setLength(0);
          redraw();
        } else if(keyCode == KeyCode.back && this.searchChars.length() >0){
          this.searchChars.setLength(this.searchChars.length()-1);
          redraw();
        } else if(this.lineSelected !=null && keyCode == this.keyOpenChild){
          if(this.lineSelected.tbl_lineCanHaveChildren){
            actionOnRefreshChildren(this.lineSelected);  //may get or refresh children, callback in user.
          }
          if(this.lineSelected.hasChildren()){           //only if it has children currently really.
            this.lineSelected.showChildren(true, true, true);
            //lineSelected.countChildren(true, nLineFirst);  //count the children.
            //fillVisibleAreaBehind(lineSelected, lineSelectedixCell);
            //repaint();
          }
        } else if(this.lineSelected !=null && keyCode == this.keyCloseChild){
          if(this.lineSelected !=null && this.lineSelected.tbl_showChildren){
            this.lineSelected.showChildren(false, false);
            fillVisibleAreaBehind(this.lineSelected, this.lineSelectedixCell);
            redraw();
          }
        } else {
          done = false;
        }
      }//switch
      if(done == false && keyCode == this.keyMarkDn && this.lineSelected !=null){
        GralTableLine_ifc<?> line = this.lineSelected; //tableLines.get(ixLine);
        if((line.getMark() & 1)!=0){
          //it is selected yet
          line.setNonMarked(1, line.getUserData());
        } else {
          line.setMarked(1, line.getUserData());
        }
        this.keyActionDone.processEvent(null);
        //keyActionDone.timeOrder.activate(100);             // activate the redraw in 100 ms to prevent too much redraw.
        done = true;
      }
      if(!done /*&& lineSelected !=null*/){
        GralWidget_ifc.ActionChange action = null;
        if(keyCode == KeyCode.enter) { action = getActionChange(GralWidget_ifc.ActionChangeWhen.onEnter); }
        else if(keyCode == (KeyCode.ctrl + KeyCode.enter)) { action = getActionChange(GralWidget_ifc.ActionChangeWhen.onCtrlEnter); }
        else if(keyCode == KeyCode.mouse1Double) { action = getActionChange(GralWidget_ifc.ActionChangeWhen.onMouse1Double); }
        if(action ==null) { action = getActionChange(GralWidget_ifc.ActionChangeWhen.onAnyKey); }
        //?? if(action ==null) { action = getActionChangeStrict(null, false); }  //any action for the table
        if(action !=null){
          Object[] args = action.args();
          if(args == null){ done = action.action().exec(keyCode, this, this.lineSelected); }
          else { done = action.action().exec(keyCode, this, args, this.lineSelected); }
        }
      } //if(table.)
      if(!done && this.gralMng.userMainKeyAction() !=null){
        done = this.gralMng.userMainKeyAction().exec(keyCode, getCurrentLine());
      }
      if(!done){
        GralUserAction mainKeyAction = this.gralMng.getRegisteredUserAction("KeyAction");
        if(mainKeyAction !=null){
          //old form called because compatibility, if new for with int-parameter returns false.
          if(!mainKeyAction.exec(keyCode, this)){
            done = mainKeyAction.exec(keyCode, this, new Integer(keyCode));
          }
        }
      }
    }//if not redraw pending.
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
  protected final GralGraphicOrder keyActionDone = new GralGraphicOrder("GralTableKeyDone", this.gralMng()) {
    @Override
    public int processEvent ( EventObject ev) {
      GralTable.this.gi.bFocused = true;  //to focus while repainting
      GralTable.this._wdgImpl.redrawGthread();
      actionOnLineSelected(KeyCode.userSelect, GralTable.this.lineSelected);
      GralTable.this.keyDone = true;
      return 0;
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
  protected void actionOnLineSelected(int key, GralTableLine_ifc<?> line){
    this.gralMng.setLastClickedWidget(this.lineSelected);
    if(this.actionOnLineSelected !=null){
      this.actionOnLineSelected.exec(key, this, line);
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
    if(this.actionOnRefreshChildren !=null){
      this.actionOnRefreshChildren.exec(KeyCode.userSelect, this, line);
    }
  }
  
  /**Only used on invocation of {@link GraphicImplAccess#drawCellContent}. It is not defined there but here because it is static. */
  public final static class LinePresentation
  {
    public GralColor colorBack, colorText;
    
    public GralColor[] cellsColorBack;
  }
  
  
  /**This is the super class for all implementers. It has protected access to all elements in the 
   * environment class. Via access methods the implementor class has protected access to all of it.
   * 
   */
  public abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImplAccess_ifc, Removeable
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
    
    /**Position of vScrollBar calculated from {@link GralTable#rootLine}. {@link NodeTableLine#tbl_zLineUnfolded} and {@link GralTable#nLineCurr}
     * written in {@link #determineSizeAndPosition()} used for painting vScrollBar. */
    protected int y1Scrollbar, y2Scrollbar;
    
    protected boolean bVscrollbarChanged;
    
    /**New and last index of the sliderColor. The last index is stored by the implementation and compared with the new one
     * to desire whether a new implementation color should be gotten. */
    protected int ixColorScrollbar, ixColorScrollbarLast = -1;
    
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
    
    protected int ixLineFocus, ixColumnFocus;
    
    
    /**Set to true if the table has the focus in window.
     */
    protected boolean XXXhasFocus;
    

    protected long mousetime, redrawtime, mousect, redrawct;
    
    private boolean mouseDoubleClick;
    
    

    protected GraphicImplAccess(GralTable<UserData> outer, GralMng mng){ 
      super(outer, mng);
      this.outer = outer;
      this.cells = new GralTable.CellData[GralTable.this.zLineVisibleMax][zColumn()];
      this.xPixelUnit = mng.gralProps.xPixelUnit();
      outer.gi = this; 
      int xdPix = outer.gralMng.propertiesGui().xPixelUnit();
      this.xpixelCell = new int[outer.columnWidthsGral.length+1];
      int ydPix = outer.gralMng.propertiesGui().yPixelUnit();
      this.linePixel = 2 * ydPix;
    }
    
    protected GralMng itsMng(){ return this.outer.gralMng; }
    
    protected boolean bColumnEditable(int ix){ return GralTable.this.bColumnEditable[ix]; }
    
    protected GralMenu[] getContextMenuColumns() { return GralTable.this.contextMenuColumns; }
    
    protected void checkAndUpdateText(String sText, CellData celldata){
      GralTable.this.checkAndUpdateText(sText, celldata);
    }
    
    protected int ixColumn(){ return this.outer.colSelectedixCellC; }
    
    //protected int ixLine(){ return outer.ixLine; }
    
    protected int[] columnWidthsGral(){ return this.outer.columnWidthsGral; }
    
    protected int[] columnWidthsPromille(){ return this.outer.columnWidthsPromille; }
    
    
    
    protected int zColumn(){ return this.outer.zColumn; }
    
    protected GralColor colorSelectCharsBack(){ return this.outer.colorSelectCharsBack; }
    
    protected boolean bChangedLinesForCell(){ return this.outer.bChangedLinesForCell; }
    
    protected void bChangedLinesForCell(boolean val){ this.outer.bChangedLinesForCell = val; }
    
    
    
    protected GralColor colorSelectChars(){ return this.outer.colorSelectChars; }
    
    protected GralColor colorBackTable(){ return this.outer.dyda.backColor; }
    
    protected GralColor colorBackVscrollbar(){ return this.outer.colorBackVscrollbar; }
    
    protected GralColor colorLineVscrollbar(){ return this.outer.colorSliderVscrollbar[this.ixColorScrollbar]; }
    
    protected StringBuilder searchChars(){ return this.outer.searchChars; }
    
    
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
    protected boolean processKeys(int keyCode){ 
      //GralTable widgt = (GralTable)widgg;
      if(GralTable.this.lineSelected !=null && bColumnEditable(GralTable.this.colSelectedixCellC)) {
        //update the text of the cell from the implementation layer:
        CellData cell = this.cells[GralTable.this.lineSelectedixCell][GralTable.this.colSelectedixCellC];
        String sContent = getCellText(cell);
        GralTable.this.lineSelected.cellTexts[GralTable.this.colSelectedixCellC] = sContent;
        //setCellText(cell, sContent);
      }
      return this.outer.processKeys(keyCode); 
    }
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
    
    
    /**Gets the current text of the given cell from the graphic implementation.
     * @param cell
     * @return
     */
    protected abstract String getCellText(CellData cell);
    
    
    /**Gets the cell text from the focused cell in the implementation. */
    protected abstract String getCellTextFocus();
    
    /**Updates the cell text fields which presents the content of the table.
     * After them update() of the graphic level should be called.
     * To execute the update of the cells in the graphic layer the {@link #drawCellContent(int, CellData[], TableLineData, LinePresentation)}
     * is called inside for any cell. This method is implemented in the implementation layer.
     */
    protected void updateGraphicCellContent(){
      long dbgtime = System.currentTimeMillis();
      this.bRedrawPending = true;

      Assert.check(this.outer.gralMng.currThreadIsGraphic());
      GralTable<UserData>.TableLineData/*<?>*/ line;
      
      //todo if(outer.bPrepareVisibleArea){
        this.outer.fillVisibleArea();  //show the selected line at line 3 in graphic or before 0..2
        this.outer.bPrepareVisibleArea = false;       // it is prepared
      //}
      
      //Now draw:
      //
      int ix = -1;
      while(++ix < this.outer.zLineVisible) {
        if(true || (this.outer.bitLinesForCellChanged & (1<<ix)) !=0) {  // non relevant CPU load impact
          line = this.outer.linesForCell[ix];                            // marking with bitLinesForCellChanged is not sufficient yet.
          setLineColors(line);
          drawCellContent(ix, this.cells[ix], line, this.linePresentation);
        }
      }
      if(++ix < this.outer.zLineVisibleMax){  //a half visible line
        drawCellContent(ix, this.cells[ix], null, this.linePresentation);
      }
//      outer.timeLastRedraw = System.currentTimeMillis();
      CharSequence stackInfo = ExcUtil.stackInfo(" call ", 20);
//      this.outer.gralMng.log.sendMsg(GralMng.LogMsg.gralTable_updateCells, "GralTable.updateGraphicCellContent(): " + GralTable.this.name); // + stackInfo);
    }

    
    
    /**Sets the colors of the given line depending on the state
     * <ul>
     * <li>line == {@link GralTable#lineSelected} : 
     *   <ul><li>marked: {@link TableLineData#colorBackSelectMarked} or {@link GralTable#colorBackSelectMarked}
     *   <li>childrenMarked: {@link TableLineData#colorBackSelectSomeMarked} or {@link GralTable#colorBackSelectSomeMarked}
     *   <li>not marked: {@link TableLineData#colorBackSelect} or {@link GralTable#colorBackSelect}
     *   </ul>
     * <li>selected new: {@link GralTable#colorBackSelectNewMarked} or {@link GralTable#colorBackSelectNew}
     * <li>normal non selected line:
     *   <ul><li>marked: {@link TableLineData#colorBackSelectMarked} or {@link GralTable#colorBackSelectMarked}
     *   <li>childrenMarked: {@link TableLineData#colorBackSelectSomeMarked} or {@link GralTable#colorBackSelectSomeMarked}
     *   <li>not focused: {@link GralWidget.DynamicData#backColorNoFocus} 
     *   <li>not marked: {@link TableLineData#colorBackSelect} or {@link GralTable#colorBackSelect}
     *   </ul>
     * </ul> 
     * @param line
     */
    private void setLineColors(TableLineData/*<?>*/ line){
      boolean marked = line !=null && (line.getMark() & MarkMask_ifc.select)!=0;
      boolean childrenMarked = line !=null && (line.getMark() &  MarkMask_ifc.selectParent)!=0;
      boolean marked2 = line !=null && (line.getMark() & MarkMask_ifc.select2)!=0;
      boolean childrenMarked2 = line !=null && (line.getMark() &  MarkMask_ifc.select2Parent)!=0;
      if(this.outer.fillinPending) {                            // a temporary situation 
        this.linePresentation.colorBack = this.outer.colorBackFillPending;
      } else if(line !=null && line == this.outer.lineSelected){               // color for the current line (selected)
        if(marked){
          this.linePresentation.colorBack = line.colorBackSelectMarked !=null ? line.colorBackSelectMarked : this.outer.colorBackSelectMarked;
        } else if(childrenMarked){
          this.linePresentation.colorBack = line.colorBackSelectSomeMarked !=null ? line.colorBackSelectSomeMarked : this.outer.colorBackSelectSomeMarked;
        } else if(marked2){
          this.linePresentation.colorBack = line.colorBackSelectMarked2 !=null ? line.colorBackSelectMarked2 : this.outer.colorBackSelectMarked2;
        } else if(childrenMarked2){
          this.linePresentation.colorBack = line.colorBackSelectSomeMarked2 !=null ? line.colorBackSelectSomeMarked2 : this.outer.colorBackSelectSomeMarked2;
        } else {
          this.linePresentation.colorBack = line !=null && line.colorBackSelect !=null ? line.colorBackSelect : this.outer.colorBackSelect;
        }
      } else if(line !=null && line == this.outer.lineSelectedNew){
        this.linePresentation.colorBack = marked ? this.outer.colorBackSelectNewMarked : this.outer.colorBackSelectNew;
      } else {                          //line is not selected
        if(marked){
          this.linePresentation.colorBack = line.colorBackMarked !=null ? line.colorBackMarked : this.outer.colorBackMarked;
        } else if(childrenMarked){
          this.linePresentation.colorBack = line.colorBackSomeMarked !=null ? line.colorBackSomeMarked : this.outer.colorBackSomeMarked;
        } else if(marked2){
          this.linePresentation.colorBack = line.colorBackMarked2 !=null ? line.colorBackMarked2 : this.outer.colorBackMarked2;
        } else if(childrenMarked2){
          this.linePresentation.colorBack = line.colorBackSomeMarked2 !=null ? line.colorBackSomeMarked2 : this.outer.colorBackSomeMarked2;
        } else if(!this.bFocused){
          this.linePresentation.colorBack = GralTable.this.dyda.backColorNoFocus;
        } else {
          this.linePresentation.colorBack = line !=null && line.colorBackground !=null ? line.colorBackground : this.outer.dyda.backColor;
        }
      }
      this.linePresentation.colorText = line !=null && line.colorForeground !=null ? line.colorForeground : this.outer.dyda.textColor;
      this.linePresentation.cellsColorBack = line !=null ? line.cellColorBack : null; //maybe null or can contain null elements.
    }
    

    /**This method have to be called before the vertical scroll bar is painted
     * in the {@link GralWidgImplAccess_ifc#redrawGthread()} routine..
     * It checks whether the {@link GralTable#nLineCurr} and the shown number of lines
     * in {@link GralTable#rootLine}. {@link GralTable.NodeTableLine#tbl_zLineUnfolded}
     * is given, e.g. >=0. If one of them is <0 or lines are countered
     * calling {@link NodeTableLine#countChildren(boolean, int)}. 
     * Therewith the {@link GralTable#nLineCurr} is set too. Anytime if the situation is not specified
     * the {@link GralTable#nLineCurr} can be set to -1. This forces new calculation.
     */
    protected void determineSizeAndPositionScrollbar(int yPixel)
    { if(GralTable.this.nLineFirst <0 || GralTable.this.rootLine.tbl_zLineUnfolded <0) {
        GralTable.this.rootLine.countChildren(true, 0);
      }
      int zLine = Math.max(1, GralTable.this.rootLine.tbl_zLineUnfolded);
      int y1 = yPixel * GralTable.this.nLineFirst / zLine;
      int zLineShow = Math.min(GralTable.this.zLineVisible, zLine);  //less if table is shorter than visible area
      int yd = yPixel * zLineShow / zLine;   //it is <= yPixel
      if(yd < 5){
        yd = 5;
      }
      int y2 = y1 + yd;
      if(y2 > yPixel) {
        y2 = yPixel;
        y1 = y2 - yd;
      }
      if(y1 != this.y1Scrollbar || y2 != this.y2Scrollbar){ 
        this.y1Scrollbar = y1;
        this.y2Scrollbar = y2;
        this.bVscrollbarChanged = true;
        this.ixColorScrollbar = (this.y2Scrollbar - this.y1Scrollbar) / 15;
        if(this.ixColorScrollbar >= this.outer.colorSliderVscrollbar.length){ this.ixColorScrollbar = this.outer.colorSliderVscrollbar.length -1; }
      }

    }

    /**This routine will be called inside the resize listener of the implementation graphic.
     * It calculates the width of the columns with the given width of the table's canvas.
     * Then {@link #setBoundsCells()} will be called. 
     * That is implemented in the underlying graphic layer and sets the bounds for each cell.
     * @param pixTable Size of the table area.
     */
    protected void resizeTable(GralRectangle pixTable) {
      int xPixelUnit = itsMng().propertiesGui().xPixelUnit();
      int yPixelUnit = itsMng().propertiesGui().yPixelUnit();
      this.outer.zLineVisible = (pixTable.dy +1) / yPixelUnit / 2;   // height of one cell always 2
      if(this.outer.zLineVisible > this.outer.zLineVisibleMax){ this.outer.zLineVisible = this.outer.zLineVisibleMax; }
      if(this.outer.zLineVisible < 2)
        Debugutil.stop();
      System.err.println("GralTable zLineVisible = " + this.outer.zLineVisible + " name " + GralTable.this.name);
      int xPixTable = pixTable.dx - xPixelUnit;
      int xPixTable2 = xPixTable;                          // available sum of pixel of the columns which are proportional
      this.xyVscrollbar.x = xPixTable;
      this.xyVscrollbar.dx = xPixelUnit;
      this.xyVscrollbar.y = 0;
      this.xyVscrollbar.dy = pixTable.dy;
      //
      int[] columnWidthsGral = columnWidthsGral();
      int[] xPixColumns = new int[columnWidthsGral.length];
      int[] xProportionals = columnWidthsPromille();
      float[] xFProportional = null;
      int xPixPos = 0;
      this.xpixelCell[0] = xPixPos;
      int ixPixelCell;
      int xPixColumn;
      int xPixTableMin = 0;
      //----------------------------------------------------- count sum of grid column widths for possible reducing min width
      for(ixPixelCell = 0; ixPixelCell < columnWidthsGral.length; ++ixPixelCell) {
        xPixColumn = columnWidthsGral[ixPixelCell] * xPixelUnit;   // the absolute width
        xPixColumns[ixPixelCell] = xPixColumn;
        if(xPixColumn <0) { xPixColumn = -xPixColumn; }
        else if(xPixColumn < xPixelUnit) { xPixColumn = xPixelUnit; }
        xPixTableMin += xPixColumn;
      }
      if(xPixTableMin > xPixTable) {                       // The space is too less, reduce the min width
        float f = (float)xPixTable / xPixTableMin;         // adjust pixel (min) per column
        for(ixPixelCell = 0; ixPixelCell < columnWidthsGral.length; ++ixPixelCell) {
          xPixColumn = columnWidthsGral[ixPixelCell] * xPixelUnit;   // the absolute width
          xPixColumns[ixPixelCell] = (int)(f*xPixColumn);  // also negative
        }
      }
      //----------------------------------------------------- detect the sum (size) for proportional values
      int xSumProportional = 0;                            // sum of proportional values
      int xSumProportional2 = 0;                           // sum of proportional values only from relevant columns
      if(xProportionals !=null) {
        xFProportional = new float[xProportionals.length];
        //--------------------------------------------------- calculates sum of proportional widths first without check relevant columns.
        for(ixPixelCell = 0; ixPixelCell < columnWidthsGral.length; ++ixPixelCell) {
          xSumProportional += xProportionals[ixPixelCell];
        }
        //--------------------------------------------------- second step: compare widths with proportion, which are relevant
        for(ixPixelCell = 0; ixPixelCell < columnWidthsGral.length; ++ixPixelCell) {
          float f = (float)xProportionals[ixPixelCell] / xSumProportional;
          int xPixGrid = xPixColumns[ixPixelCell];
          if(xPixGrid <0) { xPixGrid = - xPixGrid; }
          int xPixPropor = (int)(f * xPixTable2);
          if(xPixGrid < xPixPropor) {                      // only if the proportional position is greater than the minimal one
            xSumProportional2 += xProportionals[ixPixelCell]; // then count their width to the sum
            xFProportional[ixPixelCell] = f;               // and mark that columns !=0
          } else {
            xPixTable2 -= xPixGrid;                        // column with min width, do not regard on calculate the proportion.
            xFProportional[ixPixelCell] = 0;               // mark the columns = 0 which uses the minimal size
          }
        }
        //--------------------------------------------------- third step: calculate proportional factor
        for(ixPixelCell = 0; ixPixelCell < columnWidthsGral.length; ++ixPixelCell) {
          if(xFProportional[ixPixelCell] !=0) {
            float f = (float)xProportionals[ixPixelCell] / xSumProportional2;
            xFProportional[ixPixelCell] = f;
          }
        }
      }
      
      //----------------------------------------------------- Columns from left with positive width
      for(ixPixelCell = 0; ixPixelCell < xPixColumns.length && (xPixColumn = xPixColumns[ixPixelCell]) > 0; ++ixPixelCell) {
        //--------------------------------------------------- xPixColumn = absolute minimal width
        //if(columnWidthsPromille !=null && columnWidthsPromille.length > ixPixelCell) {
        if(xFProportional !=null && xFProportional[ixPixelCell] >0) {
          //int xPixColRel = (xProportionals[ixPixelCell] * xPixTable + (xSumProportional>>1))/ xSumProportional;
          int xPixColRel = (int)(xFProportional[ixPixelCell] * xPixTable2);
//          if(xPixColRel > xPixColumn) {                    // xPixColRel is the relative column width in pixel
            xPixColumn = xPixColRel;                       // use the greater value, for width, relative promille or min. 
//          }
        }
        xPixPos += xPixColumn;
        this.xpixelCell[ixPixelCell+1] = xPixPos;
      }
      this.nrofColumnTreeShift = ixPixelCell +1;
      System.out.println("GralTable - resizeTable; nrofColumnTreeShift =" + this.nrofColumnTreeShift);
      xPixPos = xPixTable;
      this.xpixelCell[columnWidthsGral().length] = xPixTable;  //right position.
      //----------------------------------------------------- Columns from right with negative width
      for(ixPixelCell = columnWidthsGral().length-1; ixPixelCell >=0  && (xPixColumn = - xPixColumns[ixPixelCell]) > 0; --ixPixelCell){
      //--------------------------------------------------- xPixColumn = absolute minimal width
        //if(columnWidthsPromille !=null && columnWidthsPromille.length > ixPixelCell) {
        if(xFProportional !=null && xFProportional[ixPixelCell] >0) {
          //int xPixColRel = (xProportionals[ixPixelCell] * xPixTable + (xSumProportional>>1))/ xSumProportional;
          int xPixColRel = (int)(xFProportional[ixPixelCell] * xPixTable2);
//          if(xPixColRel > xPixColumn) {                    // xPixColRel is the relative column width in pixel
            xPixColumn = xPixColRel;                       // use the greater value, for width, relative promille or min. 
//          }
        }
        xPixPos -= xPixColumn;
        this.xpixelCell[ixPixelCell] = xPixPos;
      }
      setBoundsCells(0, this.outer.zLineVisible);
    }
    
    
    /**Invoked in {@link #resizeTable(GralRectangle)} as action which should be implemented in the implementation layer.
     * The {@link #xpixelCell} was set before.
     * @param treeDepthBase
     */
    abstract protected void setBoundsCells(int treeDepthBase, int zLineVisible);

    @Override public boolean remove(){
      this.outer.rootLine.removeChildren();
      return true;
    }

    
    /**Sets the current cell as focused with the focus color. It causes
     * a redraw of the whole table because the cell may be shifted in table position.
     * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
     * @param cell The cell 
     */
    protected boolean redrawTableWithFocusedCell(CellData data){
      if(GralTable.this.bPrepareVisibleArea){
        fillVisibleArea();  //show the selected line at line 3 in graphic or before 0..2
        GralTable.this.bPrepareVisibleArea = false;
      }
      TableLineData line = this.outer.linesForCell[data.ixCellLine];
      if( line !=null){ //don't do any action if the cell isn't use.
        this.outer.lineSelectedNewixCell = data.ixCellLine;
        GralTable.this.lineSelectedNew = line;
        //TODO outer.selectLine = data.tableItem;
        this.outer.colSelectedixCellC = data.ixCellColumn;
        this.bFocused = true;
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
      if(this.bFocusLost){  
        //focus is lost from another cell of this table yet now, repaint is not invoked,
        this.bFocusLost = false;  //ignore focus lost.
      } else {
        //Any cell has got the focus, it means the table has gotten the focus.
        this.bFocused = true; 
        if(!this.bRedrawPending){
          GralTable.this.gralMng.gralFocusListener.focusGainedGral(GralTable.this);
//          TableLineData line = getCurrentLine();
//          actionOnLineSelected(KeyCode.focusGained, line);
          //super.focusGained();
          //System.out.println("GralTable - debugInfo; focusGained " + GralTable.this.toString() );
          redraw(50,100);       //to show all table cells with the focused background color.
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
      this.bFocusLost = true;
      redraw(50,100);  //NOTE: bFocusLost =false will be set in repaint.
    }
    

    
    
    
    
    protected void mouseDownGral(int key, CellData cell){
      this.mousetime = System.currentTimeMillis();
      this.outer.mouseDown(key, cell);
    }


    protected void mouseUpGral(int key, CellData cell){
      if(this.mouseDoubleClick){
        this.mouseDoubleClick = false;
      } else {
        this.outer.mouseUp(key, cell);
      }
    }


    protected void mouseDoubleGral(int key, CellData cell){
      this.mousetime = System.currentTimeMillis();
      this.mouseDoubleClick = true;
      this.outer.mouseDouble(key, cell);
    }


    /**
     * @param iCellLine
     * @param cellData
     * @param line
     * @param linePresentationP
     */
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

  
  /**This class is the super class of {@link TableLineData} which presents a line as a node in a tree. 
   * Therefore it implements the {@link TreeNodeBase} interface. A line as parent node can be presented folded (default)
   * or unfolded in several levels to present a tree instead a simple table. 
   * <br><br>
   * Only the {@link GralTable#rootLine} is a pure instance of this class The root line is never visible. It is the root. 
   * All other lines are instances of {@link TableLineData} derived from this class. 
   */
  public class NodeTableLine 
  extends TreeNodeBase<TableLineData, UserData, GralTableLine_ifc<UserData>>
  {
    /**Number of lines for this line and all visible children.
     * 
     */
    int tbl_zLineUnfolded;
    
    /**The deepness in the tree presentation of the data.
     * 
     */
    int tbl_treeDepth;

    /**Lines of a children, a tree structure. null for a non-treed table.
     * null it the children are unknown or there are not children.
     */
    //protected ArrayList<TableLineData> childLines;
    
    //protected TableLineData parentLine;
    
    /**True if it is known that the node has children. They may be unknown, then {@link #childLines} are null. */
    boolean tbl_lineCanHaveChildren;

    /**True if the children are shown in representation. */
    boolean tbl_showChildren;

    NodeTableLine(String key, UserData data){ super(key, data); }
    
    
    void clear() {
      GralTable.this.rootLine.removeChildren();
      GralTable.this.rootLine.tbl_zLineUnfolded = 0;
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
      this.tbl_lineCanHaveChildren = true; //hasChildren();
      TableLineData line = GralTable.this.new TableLineData(lineKey, userDataP);
      super.addNode(line);
      line.tbl_treeDepth = this.tbl_treeDepth +1;
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
      this.tbl_lineCanHaveChildren = true; //hasChildren();
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
      this.tbl_lineCanHaveChildren = true; //hasChildren();
      TableLineData line = GralTable.this.new TableLineData(lineKey, userDataP);
      super.addSiblingNext(line);
      line.prepareAddedLine(lineTexts);
      return line;

    }

    public void deleteLine() {
      GralTable.this.zLine -=1;
      GralTable.this.nLineFirst = -1;
      adjCountChildrenInParent(-1);
      final TableLineData line1 = (TableLineData)this;
      GralTable.this.bPrepareVisibleArea = true;
      if(GralTable.this.lineSelected == this){
        TableLineData line2 = prevLine(line1);
        if(line2 == null){
          GralTable.this.lineSelected = nextLine(line1); 
        } else {
          GralTable.this.lineSelected = line2;
          //nLineCurr left unchanged.
        }
      } else {
        GralTable.this.nLineFirst = -1;  //get new, undefined elsewhere.
      }
      line1.detach();
      redraw();
    }
    
    

    
    void countChildren(boolean bLeftGrandChildrenOpen, int nrParent){
      countChildren(bLeftGrandChildrenOpen, nrParent, 0);
      adjCountChildrenInParent(this.tbl_zLineUnfolded);
    }
    
    
    /**Counts the children and checks whether their childs should be shown.
     * It checks and sets {@link TableLineData#tbl_showChildren}
     * @param lineParent
     * @param bLeftGrandChildrenOpen false then set {@link TableLineData#tbl_showChildren} = false, true: count the grand children.  
     * @param recurs more than 100 - prevent it.
     */
    void countChildren(boolean bLeftGrandChildrenOpen, int nrParent, int recurs){
      if(recurs >100) return;
      this.tbl_zLineUnfolded = 0;
      int ctParent = nrParent;
      TableLineData child = this.firstChild();
      while(child !=null) {
        this.tbl_zLineUnfolded +=1;
        ctParent +=1;
        if(GralTable.this.zLineCurr >=0){ 
          GralTable.this.zLineCurr +=1; 
          if(child == GralTable.this.linesForCell[0]) {
            GralTable.this.nLineFirst = ctParent;
          }
        }
        if(child.tbl_showChildren && bLeftGrandChildrenOpen) {
          child.countChildren(true, ctParent, recurs+1);
          this.tbl_zLineUnfolded += child.tbl_zLineUnfolded;
        } else {
          child.tbl_showChildren = false; //close it.
          child.tbl_zLineUnfolded = 0;
        }
        child = child.nextSibling();
      }
    }
    
    
    
    void adjCountChildrenInParent(int nCorr) {
      ////
      NodeTableLine parent1 = parent();
      boolean showChildren1 = this.tbl_showChildren;
      while(parent1 !=null   //stop on root node. 
          && (showChildren1 &= parent1.tbl_showChildren)   //stop count zLineUnfolded on nonvisible children.
        ) {
        this.tbl_zLineUnfolded += nCorr;
        parent1 = parent1.parent();   
      }
    }

    

    
  }
  
  
  /**An instance of this class presents a line in a table or a node or leaf in a tree.
   * It is not associated with a graphical representation primary, it is a container element only.
   * The graphical representation refers this class.
   * A table line contains:
   * <ul>
   * <li>Association to user data for application usage, unused in graphic: {@link TreeNodeBase#nd_data}
   * <li>Texts for all cells of the table: {@link TableLineData#cellTexts}
   * <li>background and text color for the line (all cells) {@link TableLineData#colorBackground}, {@link TableLineData#colorForeground}
   * <li>Maybe background colors for all or some cells, null if the cell have the line back color
   * <li>Information about marking: {@link TableLineData#markMask}
   * <li>Information whether the line was changed (any cell): {@link TableLineData#bChanged}.  
   * </ul>
   */
  public final class TableLineData
  extends NodeTableLine 
  implements MarkMask_ifc, GralTableLine_ifc<UserData>
  {

    SelectMask markMask;
    
    
    /**If a repaint is necessary, it is changed by increment by 1. If the redraw is executed
     * and no other redraw is requested, it is set to 0. If another redraw is requested while the
     * redraw runs but isn't finished, it should be executed a second time. Therefore it isn't reset to 0. 
     */
    public AtomicInteger ctRepaintLine = new AtomicInteger();  //NOTE should be public to see it from derived outer class
    
    public final String[] cellTexts;

    /**If not null, then a special background color for the cell. Elsewhere the cell will be shown in the {@link #colorBackground} of the line
     * or the {@link GralTable#colorBackMarked} etc. of the table.
     * 
     */
    public GralColor[] cellColorBack;
    
    /**A line may have its datapath adequate a {@link GralWidget}, only used for
     * {@link #getDataPath()} and {@link #setDataPath(String)}.
     */
    private String sDataPath;
    
    /**True if any of a cell text was changed by manual edit. */
    protected final boolean[] bChangedEdit;
    
    /**True if any of a cell text was changed by manual edit. */
    protected final boolean[] bChangedSet;
    
    public GralColor colorForeground, colorBackground, colorBackSelect
    , colorBackSelectMarked, colorBackSelectSomeMarked, colorBackMarked, colorBackSomeMarked
    , colorBackSelectMarked2, colorBackSelectSomeMarked2, colorBackMarked2, colorBackSomeMarked2;
    
    //protected UserData userData;
    
    //TODO GralColor colorBack, colorText;
    
    TableLineData(String key, UserData data){
      super(key, data);  //key, data
      this.cellTexts = new String[GralTable.this.zColumn];
      this.bChangedEdit = new boolean[GralTable.this.zColumn];
      this.bChangedSet = new boolean[GralTable.this.zColumn];
    }
    
    
    
    
    /**Gets the table where this line is member of. 
     */
    @Override public GralTable<UserData> getTable() { return GralTable.this; }
    
    
    
    
    private void prepareAddedLine(String[] lineTexts) {
      GralTable.this.nLineFirst = -1;  //get new, undefined elsewhere.
      if(lineTexts !=null){
        for(int ixCol = 0; ixCol < lineTexts.length && ixCol < this.cellTexts.length; ++ixCol){
          this.cellTexts[ixCol] = lineTexts[ixCol];
          this.bChangedSet[ixCol] = true;
        }
      } ////
      if(GralTable.this.lineSelected == null){ //// 
        GralTable.this.lineSelected = this;
        actionOnLineSelected(KeyCode.defaultSelect, GralTable.this.lineSelected);
      }
      adjCountChildrenInParent(1);
      GralTable.this.zLine += 1;
      GralTable.this.bPrepareVisibleArea = true;
      boolean showChildren1 = this.tbl_showChildren; 
      if(this.tbl_zLineUnfolded == -1){ 
        this.tbl_zLineUnfolded = 0;
      }
      if(this.tbl_showChildren) {
        this.tbl_zLineUnfolded +=1;
        adjCountChildrenInParent(1);
      }
      if(this == GralTable.this.linesForCell[0]) {
        GralTable.this.nLineFirst = GralTable.this.rootLine.tbl_zLineUnfolded;  //store the number of current line if given.
      }
      if(showChildren1) {
        redraw();
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

    
    @Override public boolean isGraphicDisposed(){ return GralTable.this.isGraphicDisposed(); }
    
    
    @Override public boolean isChanged(boolean setUnchanged){ 
      boolean ret = false;
      for(int ix = 0; ix < this.bChangedEdit.length; ++ix) {
        ret |= this.bChangedEdit[ix];
        if(setUnchanged){
          this.bChangedEdit[ix] = false;
        }
      }
      return ret; 
    }


    public boolean wasCelltextSet(int column){
      boolean ret = this.bChangedSet[column];
      if(ret){
        this.bChangedSet[ column] = false;
      }
      return ret;
    }
    
    
    @Override public TableLineData parentNode(){ return (TableLineData)super.getParent(); }
    
    @Override public String getName(){ return GralTable.this.name; }
    
    @Override public void setCmd(String cmd){ GralTable.this.setCmd(cmd); }
    
    @Override public String getCmd(){ return GralTable.this.getCmd(); }
    

    @Override public String getCellText(int column) { 
      String text = this.cellTexts[column]; 
      return text == null ? "" : text;
    }

    @Override public String[] getCellTexts() { return this.cellTexts; }

    
    public int treeDepth(){ return this.tbl_treeDepth; }
    
    @Override
    public String setCellText(String text, int column) {
      String oldText = this.cellTexts[column];
      if(oldText ==null || !oldText.equals(text)) {
        this.cellTexts[column] = text;
        this.bChangedSet[column] = true;
        GralTable.this.redraw();
      }
      return oldText;
    }

    
    
    
    @Override public UserData getUserData() { return this.nd_data;  }

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

    //@Override public void setVisibleStateWidget(boolean bVisible) { GralTable.this.setVisibleStateWidget(bVisible); }
    
    @Override public void setFocusedWidget ( GralWidgetBase_ifc widg) { } // unreleavant
    
    @Override public GralWidgetBase_ifc getFocusedWidget() { return null; }

    
    @Override public void setFocus() { GralTable.this.setFocus(); }

    @Override public void setFocus(int delay, int latest) { GralTable.this.setFocus(delay, latest); }

    @Override public boolean isInFocus(){ return GralTable.this.isInFocus(); } 

    @Override public boolean isVisible(){ return GralTable.this.isVisible(); }
    

    @Override @Deprecated public GralColor setBackgroundColor(GralColor color) {
      GralColor ret = this.colorBackground;
      this.colorBackground = color;
      redraw(50, 50);
      return ret;
    }

    @Override @Deprecated public GralColor setForegroundColor(GralColor color) {
      GralColor ret = this.colorForeground;
      this.colorForeground = color;
      redraw(50, 50);
      return ret;
    }

    
    /**Sets the background color of the whole line or one cell.
     * @param ix -1 for the whole line, >=0 for one cell.
     */
    @Override public void setBackColor(GralColor color, int ix)
    { 
      if(color !=null && color.getColorName().equals("pma"))
        Assert.stop();
      if(ix <0){
        this.colorBackground = color;
      } else {
        if(ix >= this.cellTexts.length) throw new IndexOutOfBoundsException("faulty index " + ix);
        if(this.cellColorBack == null){ this.cellColorBack = new GralColor[this.cellTexts.length]; }
        this.cellColorBack[ix] = color;
      }
      redraw();
    }
    
    
    public void cleanSpecialColorsOfLine ( ) {
      if(this.cellColorBack !=null) {
        this.cellColorBack = null;
      }
    }
    
    @Override public GralColor getBackColor(int ix)
    { 
      if(ix <0 || this.cellColorBack == null) {
        return this.colorBackground;
      } else {
        if(ix >= this.cellTexts.length) throw new IndexOutOfBoundsException("faulty index " + ix);
        return this.cellColorBack[ix] !=null ? this.cellColorBack[ix] : this.colorBackground;
      }
    }
    
    

    /**Sets the background color for a special cell of line or for all cells of this line 
     *   which has not a special cell color for the several states of the line.
     * @param colorNormal non selected, non marked
     * @param colorSelected non marked, the selected line.
     * @param colorMarked marked, not the selected line
     * @param colorSomeMarked some marked, not the selected line
     * @param colorSelectMarked marked or some marked, the selected line.
     * @param ix -1 for the line, 0... for one cell of the line. 
     */
    public void setBackColor(GralColor colorNormal, GralColor colorSelected, GralColor colorMarked, GralColor colorSomeMarked, GralColor colorSelectMarked, int ix)
    { 
      if(ix <0){
        this.colorBackground = colorNormal;
        this.colorBackSelect = colorSelected;
        this.colorBackMarked = colorMarked;
        this.colorBackSelectSomeMarked = colorSomeMarked;
        this.colorBackSelectMarked = colorSelectMarked;
      } else {
        if(ix >= this.cellTexts.length) throw new IndexOutOfBoundsException("faulty index " + ix);
        if(this.cellColorBack == null){ this.cellColorBack = new GralColor[this.cellTexts.length]; }
        this.cellColorBack[ix] = colorNormal;
        //TODO cell colors state specific.
        this.colorBackSelect = colorSelected;
        this.colorBackMarked = colorMarked;
        this.colorBackSelectSomeMarked = colorSomeMarked;
        this.colorBackSelectMarked = colorSelectMarked;
      }
      redraw();
    }
    
    @Override public void setLineColor(GralColor color, int ix)
    { 
      this.colorForeground = color;
      redraw(50, 50);
    }

    @Override public void setTextColor(GralColor color)
    { 
      this.colorForeground = color;
      redraw(50, 50);
    }

    @Override public void setText(CharSequence text){
      throw new IllegalArgumentException("GralTable-TableLineData - setText is not implemented;");
    }
    
    
    @Override public void showChildren(boolean show, boolean bLeftGrandChildrenOpen) {
      if(show) {
        if(bLeftGrandChildrenOpen && this.tbl_showChildren) return;  //do nothing because no change
        else {
          if(this.tbl_showChildren) {  //it is shown already
          }
          this.tbl_showChildren = true;
          //children are closed yet, open and count it.
          countChildren(bLeftGrandChildrenOpen, GralTable.this.nLineFirst);
        }
      } else { //switch off children
        if(this.tbl_showChildren) {
          adjCountChildrenInParent(-this.tbl_zLineUnfolded);  //firstly subtract because children are open
          this.tbl_showChildren = false;
        }
        //else: don't change anything.
      }
    }

    
    
    
    public void showChildren(boolean show, boolean bLeftGrandChildrenOpen, boolean showReally) {
      showChildren(show, bLeftGrandChildrenOpen);
      countChildren(bLeftGrandChildrenOpen, GralTable.this.nLineFirst);  //count the children.
      fillVisibleAreaBehind(this, GralTable.this.lineSelectedixCell);
      redraw();
    }      
    
    
    @Override
    public void redraw() {
      this.ctRepaintLine.addAndGet(1);
      GralTable.this.redraw(); 
    }

    @Override public void redraw(int delay, int latest){
      this.ctRepaintLine.addAndGet(1);
      GralTable.this.redraw(delay, latest); 
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

    @Override public ActionChange getActionChange(ActionChangeWhen when){ return GralTable.this.getActionChange(when); }

    

    //@Override public Object[] getWidgetMultiImplementations(){ return null; }

    
    @Override
    public boolean remove()
    { return GralTable.this.gi.remove();
    }

    
    
    
    @Override public void setHtmlHelp(String url) { GralTable.this.setHtmlHelp(url); }
    
    
    @Override public Object getContentInfo(){ return this.nd_data; }

    
    
    @Override public int getMark(){
      if(this.nd_data instanceof MarkMask_ifc){
        return ((MarkMask_ifc)this.nd_data).getMark();
      } else if(this.markMask == null){
        return 0;
      } else {
        return this.markMask.getMark();
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
      if(this.markMask ==null) return 0;
      else return this.markMask.setNonMarked(mask, data);
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
      if(this.markMask ==null){ this.markMask = new SelectMask(); }
      return this.markMask.setMarked(mask, data);
    }

    @Override public GralMng gralMng(){ return GralTable.this.gralMng(); }
    
 
//    @Override public void createImplWidget_Gthread(){
//      throw new IllegalArgumentException("GralTableLine.setToPanel - is illegal; Use GralTable.setToPanel(...)");
//    }

    @Override public String toString(){ 
      StringBuilder u = new StringBuilder();
      for(int ii = 0; ii < this.cellTexts.length; ++ii){
        u.append(this.cellTexts[ii]).append('|');
      }
      return u.toString(); 
    }



    @Override public void setData(Object data)
    { throw new IllegalArgumentException("not supported, only final data on construction.");
    }



    @Override public UserData getData()
    { return this.nd_data;
    }




    @Override public ImplAccess getImplAccess () { return GralTable.this.getImplAccess(); }




    @Override public GralPos pos () {
      // TODO Auto-generated method stub
      return null;
    }




    @Override public Object getImplWidget () {
      // TODO Auto-generated method stub
      return null;
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
   * Note: The class should be used  only in the graphic implementation layer. It is public only to allow access from several implementations.
   * It is not public to offer it for applications.
   */
  public static class CellData
  {
    
    /**The row and column in the graphical presentation. With the information about the row, the
     * associated {@link TableLineData} can be found via the {@link GralTable#linesForCell}. */
    public final int ixCellLine, ixCellColumn;
    
    /**The color in the graphical presentation. It is the color of the text field. 
     * Note that the color of the text field is only changed if this colors and the 
     * {@link TableLineData#colorBackground} and {@link TableLineData#colorForeground} are different. 
     * It saves some calculation time if the color of the text field is set only if it is necessary. */
    public GralColor colorBack, colorText;
    
    
    /**temporary set to true to set the focus of this cell.
     * 
     */
    public boolean bSetFocus;
    
    /**The currently tree depth of this cell. Invoke setBounds if it is different of line. */
    public int treeDepth;
    
    
    /**The widget for a cell is a GralTextField. @since 2015-11-09 for AWT-adaption*/
    //public GralTextField wdgCell;
    
    public CellData(int ixCellLine, int ixCellColumn){
      this.ixCellLine = ixCellLine; 
      this.ixCellColumn = ixCellColumn;
      //this.wdgCell = new GralTextField(null);
    }
  }
  

  
  
}
