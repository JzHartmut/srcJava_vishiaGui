package org.vishia.gral.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;
import org.vishia.util.SelectMask;
import org.vishia.util.MarkMask_ifc;

/**This is the Gral class for a table. Its usage is independent of a graphical implementation layer.
 * A table consists of some text fields which are arranged in columns and lines. 
 * <br><br>
 * <b>Data, text and graphic architecture of the table</b>:<br>
 * All data which are managed in this table are contained in an List {@link #tableLines} with a unlimited size. 
 * That container references instances of {@link TableLineData}. 
 * Each of that table line contains the texts which are displayed in the cells,
 * the color etc., a key for searching and a reference to any user data. In that kind a table is a container class
 * with a graphical touch.
 * <br>
 * For presenting the table in a graphic implementation some text fields are arranged in a line and column structure
 * in any panel or canvas. The number of such fields for the lines is limited, only the maximal number of viewable 
 * lines should be present, independent of the length of the table. If any part of the table will be shown, that fields
 * will be filled with the texts from the TableItemWidget of  {@link #tableLines}. If the table content will be shifted
 * in the visible area, the text content of the fields are shifted. The first field is shown as first anyway, but it may
 * contain the content of a further line of the table. Each text field refers an instance of {@link CellData} as its
 * 'user data'. The CellData refers via {@link CellData#tableItem} the line.
 * <br>
 * <pre>
 *   Graphic representation     GralTable
 *             |                   |
 *             |                   |--idxLine-------------->Map< key, GralTableLine_ifc>
 *             |                   |                                +------|
 *             |                   |      {@link TableLineData}     |
 *    |<*------+                   |                  |             |
 *   TextField                     |---tableLines---*>|<*-----------+
 *    |<>------>CellData                              |
 *    |            |-tableItem----------------------->|
 *                                                    |<>---userData--------------->User's data
 * 
 * </pre>
 * <br><br>
 * <b>Key handling in a table</b>: <br>
 * The table has its own {@link GraphicImplAccess#processKeys(int)} method which is called 
 * from the graphical implementation layer in an key event handler.
 * This method processes some keys for navigation and selection in the table. If the key is not
 * used for that, the key will be offer the {@link GralMng#userMainKeyAction} for a global key usage.
 * 
 * @author Hartmut Schorrig
 *
 */
public final class GralTable<UserData> extends GralWidget implements GralTable_ifc<UserData> {

  /**Version and history
   * <ul>
   * <li>2013-11-02 Hartmut chg: {@link GralTable.TableLineData} is static now with outer reference, more transparent
   * <li>2013-11-02 Hartmut chg: {@link GralTable.GraphicImplAccess#resizeTable(GralRectangle)} moved from SWT implementation,
   *   preparing tree view. {@link GralTable.TableLineData#treeDepth}, {@link GralTable.TableLineData#childLines} for tree view.
   * <li>2013-11-02 Hartmut chg: {@link GralTable.TableLineData} is static now with outer reference, more transparent
   * 
   * <li>2013-10-06 Hartmut chg: call {@link #actionOnLineSelected(GralTableLine_ifc)} only if the line is changed, not on focus without changed line.
   * <li>2013-09-15 Hartmut new: Implementation of {@link #setBackColor(GralColor, int)}  
   *   with special comments for usage of the int parameter.
   *   See {@link GralTable_ifc#setBackColor(GralColor, int)}, Adequate {@link #getBackColor(int)}. 
   * <li>2013-09-14 Hartmut chg: {@link GraphicImplAccess} implements now {@link GralWidgImpl_ifc} without any other
   *   changes (was proper) and sets {@link GralWidget#wdgImpl}. Therefore all routines which works from the
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
   *   Maybe for refresh data only if they are in the visible range. 
   * <li>2013-05-11 Hartmut chg: {@link #deleteLine(GralTableLine_ifc)} was not ready, now tested
   * <li>2013-05-11 Hartmut chg: {@link TableLineData} instead TableItemWidget.
   * <li>2013-04-28 Hartmut new: {@link #specifyKeysMarkUpDn(int, int)}
   * <li>2013-04-28 Hartmut new: {@link #specifyActionOnLineMarked(MarkMask_ifc)}
   * <li>2013-04-28 Hartmut renamed: {@link #specifyActionOnLineSelected(GralUserAction)}
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
   * <li>2011-11-27 Hartmut new {@link #specifyActionOnLineSelected(GralUserAction)}: The user action is called
   *   anytime if a line is selected by user operation. It can be show any associated content anywhere
   *   additionally. It is used for example in "The.file.Commander" to show date, time and maybe content 
   *   while the user selects any files. The graphical implementation should be call {@link #actionOnLineSelected(GralTableLine_ifc)}
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
  public final static int version = 20130521;

  
  protected int keyMarkUp = KeyCode.shift + KeyCode.up, keyMarkDn = KeyCode.shift + KeyCode.dn;
  
  
  protected final Map<String, TableLineData<UserData>> idxLine = new TreeMap<String, TableLineData<UserData>>();
  
  /**This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnLineSelected;
  
  /**Width of each column in GralUnits. */
  protected int[] columnWidthsGral;
  
  protected GralMenu[] menuColumns;
  

  /**The colors of each cell. It is set with the color of {@link TableItemWidget#colorBackground} and
   * {@link TableItemWidget#colorBackground} of the currently displayed line.
   */
  //private GralColor[] colorBack, colorText;
  
  
  GraphicImplAccess gi;

  /**Index (subscript) of the current line and current column. 0 is the left column or top line.
   * -1 means that nothing is selected. 
   * ixLineNew == ixLine if the selection is not changed. ixLineNew will be changed outside
   * the graphic thread, the change will be recognize in {@link #repaintGthread()}
   * and then in {@link GraphicImplAccess#setAllCellContentGthread()}. 
   * */
  protected int ixLine, ixLineNew, ixColumn;

  /**Number of lines and columns of data. */
  protected int zLine, zColumn;
  

  
  
  protected final StringBuilder searchChars = new StringBuilder(20);
  
  /**Any line of the table has one TableItemWidget, a long table has some more.
   * Contains content, color, selection etc. of the lines with there cells.
   * 
   */
  protected ArrayList<TableLineData<UserData>> tableLines = new ArrayList<TableLineData<UserData>>();
  
  /**True if a line or a column is marked. */
  //protected boolean[] markedLines, markedColumns;
  
  /**If true then the graphic implementation fields for cells should be filled newly with the text. */
  protected boolean bFillCells;
  
  /**Data of that cell which was pointered while any mouse button is pressed. */
  //protected CellData cellDataOnMousePressed;
  
  /**The colors. */
  protected GralColor colorBackSelect, colorBackMarked, colorBackSelectMarked //, colorBackTable
  //, colorBackSelectNonFocused, colorBackMarkedNonFocused, colorBackTableNonFocused
  , colorTextSelect, colorTextMarked;
  
  
  protected GralColor colorSelectCharsBack, colorSelectChars;

  
  /**This action will be called if any line is marked. It may be null, see 
   * 
   */
  protected MarkMask_ifc actionMarkOnLine;
  
  
  
  public GralTable(String name, int[] columnWidths) {
    super(name, 'L', null);
    this.columnWidthsGral = columnWidths;
    this.zColumn = columnWidths.length;
    //this.colorBack = new GralColor[zLineVisibleMax];
    //this.colorText = new GralColor[zLineVisibleMax];
    ixLineNew = ixLine = -1;

    setColors();
  }

  
  
  @Override public void setToPanel(GralMngBuild_ifc mng) throws IllegalStateException {
    if(wdgImpl !=null) throw new IllegalStateException("setToPanel faulty call - GralTable;");
    mng.add(this);
  }
  
  
  /**Sets an action which is called any time when another line is selected.
   * This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void specifyActionOnLineSelected(GralUserAction action){
    this.actionOnLineSelected = action;
  }
  
  
  public void specifyActionOnLineMarked(MarkMask_ifc action){
    actionMarkOnLine = action;
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
  
  
  
  @Override public void setBackColor(GralColor color, int ix){ 
    if(ix >=0){
      if(ix < tableLines.size()){
        tableLines.get(ix).setBackColor(color, -1);
      }
    } else {
      if(ix == kAllLines){
        for(TableLineData<UserData> line: tableLines){
          line.setBackColor(color, -1);
        }
      }
      super.setBackColor(color, ix);
    }
  }
  
  
  @Override public GralColor getBackColor(int ix){ 
    if(ix >=0 && ix < tableLines.size()){
      return tableLines.get(ix).getBackColor(-1);
    } else {
      return dyda.backColor;
    }
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
    GralWidget menuitem = new GralWidget(menuname, 'M', null);
    menu.addMenuItemGthread(menuitem, menuname, sMenuPath, action);
    menuitem.setContentInfo(this);  //the table
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
  
  @Override public boolean setCurrentLine(String key){
    GralTableLine_ifc<UserData> line = idxLine.get(key);
    if(line == null) return false;
    else {
      int nLine = line.getLineNr();
      return setCurrentCell(nLine, -1);
    }
  }
  
  @Override public boolean setCurrentLine(int nLine){ return setCurrentCell(nLine, -1); }

  
  public void setColors(){
    colorBackSelect = GralColor.getColor("lam");
    colorBackMarked = GralColor.getColor("pcy");
    colorBackSelectMarked = GralColor.getColor("lgn");
    dyda.backColor = GralColor.getColor("wh");
    //colorBackSelectNonFocused = GralColor.getColor("am");
    //colorBackMarkedNonFocused = GralColor.getColor("lrd");
    //colorBackTableNonFocused = GralColor.getColor("gr");
    dyda.textColor = GralColor.getColor("bk");
    colorSelectCharsBack = GralColor.getColor("lgr");
    colorSelectChars = GralColor.getColor("wh");
  }
  
  
  
  @Override
  public GralTableLine_ifc<UserData> getCurrentLine() {
    if(ixLine >=0 && ixLine < tableLines.size()){
      return tableLines.get(ixLine);
    }
    else return null;
  }

  
  
  
  
  @Override
  public boolean setCurrentCell(int line, int column) {
    if(line < -1 || line > zLine-1){ line = zLine -1; }
    if(column < -1 || column > zColumn-1){ column = 0; }
    if(line >=0){
      ixLineNew = line;  //forces color setting select color
    }
    if(column >=0){
      ixColumn = column;
    }
    repaint(50,200);
    return true;
  }

  
  
  /**Sets the color of the current line. 
   * @param color
   */
  public void setColorCurrLine(GralColor color){ 
    colorBackSelect = color; 
    repaint(100, 0);
  }

  

  
  @Override public GralTableLine_ifc<UserData> getLine(int row) {
    if(row >= tableLines.size()) return null;
    else return tableLines.get(row);
  }

  @Override
  public GralTableLine_ifc<UserData> getLine(String key) {
    // TODO Auto-generated method stub
    return idxLine.get(key);
  }

  @Override public GralTableLine_ifc<UserData> insertLine(String key, int row, String[] cellTexts, UserData userData) {
    TableLineData<UserData> line = new TableLineData<UserData>(this);
    zLine = tableLines.size();
    if(row > zLine || row < 0){
      row = zLine;
    }
    line.nLineNr = row;
    line.key = key;
    tableLines.add(row, line);
    zLine +=1;
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    line.userData = userData;
    bFillCells = true;
    if(key !=null){
      idxLine.put(key, line);
    }
    for(int ii=row+1; ii < zLine; ++ii){
      TableLineData<UserData> line2 = tableLines.get(ii);
      line2.nLineNr = ii;
    }
    repaint(100, 0);
    return line;
  }

  @Override public void deleteLine(GralTableLine_ifc<UserData> line) {
    int linenr = line.getLineNr();
    TableLineData<UserData> tline = (TableLineData<UserData>)line;
    if(tline.key !=null){ 
      TableLineData<UserData> kline = idxLine.remove(tline.key);
      if(kline != tline){
        idxLine.put(kline.key, kline);
      }
    }
    tableLines.remove(linenr);
    for(int ii=linenr; ii < tableLines.size(); ++ii){
      TableLineData<UserData> line2 = tableLines.get(ii);
      line2.nLineNr = ii;
    }
    zLine -=1;
    if(ixLine == linenr){  //TODO correct???
      ixLineNew = ixLine = -1;
      gi.ixGlineSelectedNew = -1;  //deselects ixGlineSelected on redraw!
    }
    repaint(200,200);
  }
  
  
  
  @Override public void replaceLineKey(GralTableLine_ifc<UserData> line, String keyNew){
    TableLineData<UserData> lineData = (TableLineData<UserData>)line;
    String keyOld = lineData.key;
    if(keyOld !=null){
      TableLineData<UserData> lineOld = idxLine.remove(keyOld);
      if(lineOld != line){ //multiple keys, it was another line:
        idxLine.put(keyOld, lineOld);  //put it back again, don't remove.
      }
    }
    if(keyNew !=null){
      assert(line instanceof TableLineData<?>);
      idxLine.put(keyNew, (TableLineData<UserData>)line);
    }
    lineData.key = keyNew;  //maybe null 
  }
  
  
  
  
  @Override public int size(){ return zLine = tableLines.size(); }
  

  @Override public void clearTable() {
    gi.ixLine1 = gi.ixLine2 = 0;
    ixColumn = 0;
    zLine = 0;
    tableLines.clear();
    ixLineNew = ixLine = -1;
    gi.ixGlineSelectedNew = -1;  //deselects ixGlineSelected on redraw!
    idxLine.clear();
    searchChars.setLength(0);
    repaint(200,200);
  }

  @Override public List<GralTableLine_ifc<UserData>> getMarkedLines(int mask) {
    List<GralTableLine_ifc<UserData>> list = new LinkedList<GralTableLine_ifc<UserData>>();
    for(TableLineData<UserData> item: tableLines){
      if(item.userData instanceof MarkMask_ifc){
        if((((MarkMask_ifc)item.userData).getMark() & mask) !=0){
          list.add(item);
        }
      }
      else if((item.getMark() & mask) !=0){
        list.add(item);
      }
    }
    return list;
  }

  
  @Override public List<? extends GralTableLine_ifc<UserData>> getAllLines() { return tableLines; }
  
  
  @Override public List<UserData> getListContent() {
    List<UserData> list = new LinkedList<UserData>();
    for(TableLineData<UserData> item: tableLines){
      list.add(item.getUserData());
    }
    return list;
  }

  
  @Override public GralTableLine_ifc<UserData> getFirstMarkedLine(int mask) {
    for(TableLineData<UserData> item: tableLines){
      if((item.getMark() & mask) !=0){
        return item;
      }
    }
    return null;
  }

  
  
  /**Increments or decrements ixLineNew in [up] or [dn] situation until the {@link #searchChars} are found
   * or one time if searchChars are not given.
   * If the searchChars are not found, either the first or last line will be selected. It is because
   * any key operation should have an effect for the user. Paradigm: Not the search and found is prior,
   * the response of operation is prior. The user may see wheter anything is found. 
   * @param bUp direction
   * @return
   */
  protected void searchContent(boolean bUp){
    if(searchChars.length() >0){
      String search = searchChars.toString();
      int ixLineNewAct = ixLineNew;
      boolean contSearch = true;
      do{
        if(bUp && ixLineNew > 0){        //up
          ixLineNew -=1;
        } else if(!bUp && ixLineNew < zLine-1){ //down
          ixLineNew +=1;
        } else {
          //ixLineNew = ixLineNewAct;
          contSearch = false;                 //eand reached
        }
        if(contSearch){
          GralTableLine_ifc<?> lineGral = tableLines.get(ixLineNew);
          String sText = lineGral.getCellText(ixColumn).toLowerCase();
          if(searchChars.charAt(0) == '*'){
            contSearch = false;  //TODO
          } else {
            if(!sText.startsWith(search)){
            } else {
              contSearch = false;                   //found
            }
          }
        }
      } while(contSearch);
      
    } else {
      if(bUp && ixLineNew > 0){        //up
        ixLineNew -=1;
      } else if(!bUp && ixLineNew < zLine-1){ //down
        ixLineNew +=1;
      }
    }
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
  protected final GralDispatchCallbackWorker keyActionDone = new GralDispatchCallbackWorker("GralTableKeyDone") {
    @Override
    public void doBeforeDispatching(boolean onlyWakeup) {
      gi.bFocused = true;  //to focus while repainting
      repaintGthread();
      gi.keyDone = true;
      //System.out.println("Key done");
      removeFromQueue(itsMng.gralDevice());
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
  protected void actionOnLineSelected(GralTableLine_ifc<?> line){
    if(actionOnLineSelected !=null){
      actionOnLineSelected.exec(KeyCode.tableLineSelect, this, line);
    }
  }
  
  
  
  /**This is the super class for all implementers. It has protected access to all elements in the 
   * environment class. Via access methods the implementor class has protected access to all of it.
   * 
   */
  public static abstract class GraphicImplAccess extends GralWidget.ImplAccess
  implements GralWidgImpl_ifc, Removeable
  {
    protected final GralTable<?> outer;
    
    /**Set to true while {@link #table}.{@link Table#redrawGthread()} is running.
     * It prevents recursive invocation of redraw() while setFocus() is invoked. */
    protected boolean bRedrawPending;

    
    
    
    /**Pixel per line. */
    protected int linePixel;
    
    protected final int[] xpixelCell;

    /**Start position of each column in pixel. */
    protected int[] columnPixel;

    /**Index of {@link #texts} of the cell left top for presentation. */
    protected int ixLine1, ixCol1;
    
    /**Index of {@link #texts} of the cell right bottom for presentation. It depends of the size of presentation. */
    protected int ixLine2, ixCol2;
    
    
    /**Current number of visible lines in the view. */
    protected int zLineVisible;
    
    /**maximum number of visible lines. it is the static amount of line-cells for displaying. */
    protected int zLineVisibleMax = 20;  
    
    /**Index (subscript) of the graphical line (not lines of the table)
     * which has the selection color and which should be gotten the selection color.
     */
    protected int ixGlineSelected = -1, ixGlineSelectedNew = -1;
    
    /**Check the last time of redrawing. */
    protected long timeLastRedraw;
    
    /**If set, then a next key will be processed. It is set to false if a key event is executed
     * and it is set to true in {@link #keyActionDone}. */
    protected boolean keyDone = true;
    
    /**The last key which was pressed  */
    private int lastKey;
    
    /**Number of key repetitions if a key was not used because the redraw was pending yet. */
    private int keyRepetition;
    
    
    /**Set true if the focus is gained by mouse click. It causes color set and 
     * invocation of {@link #actionOnLineSelected(GralTableLine_ifc)}. 
     */
    protected boolean bFocused;
    

    

    protected GraphicImplAccess(GralTable<?> outer, GralMng mng){ 
      super(outer, mng);
      this.outer = outer;
      //outer.wdgImpl = this;
      //outer.setPanelMng(mng);
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
      ixGlineSelectedNew = ixGlineSelected = -1;  
    }
    
    protected GralMng itsMng(){ return outer.itsMng; }
    
    protected int ixColumn(){ return outer.ixColumn; }
    
    protected int ixLine(){ return outer.ixLine; }
    
    protected int[] columnWidthsGral(){ return outer.columnWidthsGral; }
    
    
    
    protected int zColumn(){ return outer.zColumn; }
    
    protected GralColor colorSelectCharsBack(){ return outer.colorSelectCharsBack; }
    
    protected GralColor colorSelectChars(){ return outer.colorSelectChars; }
    
    protected GralColor colorBackTable(){ return outer.dyda.backColor; }
    
    protected GralColor colorBackSelect(){ return outer.colorBackSelect; }
    
    protected GralColor colorBackMarked(){ return outer.colorBackMarked; }
    
    protected GralColor colorBackSelectMarked(){ return outer.colorBackSelectMarked; }
    
    protected GralColor colorTextTable(){ return outer.dyda.textColor; }
    
    protected GralColor colorTextSelect(){ return outer.colorTextSelect; }
    
    protected GralColor colorTextMarked(){ return outer.colorTextMarked; }
    
    protected StringBuilder searchChars(){ return outer.searchChars; }
    
    
    protected void correctIxLineColumn(){
    
      if(outer.ixColumn < 0){ outer.ixColumn = 0;}
      if(outer.ixLine < 0 && outer.zLine >0){ outer.ixLineNew = 0;}
    }
    
    
    protected boolean isCurrentLine(int nLineNr){
      return outer.ixLineNew >=0 ? nLineNr == outer.ixLineNew  //a new line 
          : nLineNr == outer.ixLine;
    }
    
    
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
          outer.keyActionDone.addToGraphicThread(outer.itsMng.gralDevice(), 0);
        } break;
        case KeyCode.mouseWheelDn:
        case KeyCode.dn: {
          if(outer.ixLine < outer.zLine -1){
            //ixLineNew = ixLine + keyRepetition;
            outer.searchContent(false);
            if(outer.ixLineNew >= outer.zLine ){ outer.ixLineNew = outer.zLine -1; }
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

    
    
    
    protected void setFocusCellMousePressed(){
      
    }
    

    /**Redraws the whole table because the current line is changed or the focus is changed
     * or the content is changed and #re
     * TODO
     * {@link GralWidgetGthreadSet_ifc#redrawGthread()}
     */
    protected void setAllCellContentGthread(){
      long dbgtime = System.currentTimeMillis();
      bRedrawPending = true;

      Assert.check(outer.itsMng.currThreadIsGraphic());
      int iCellLine;
      //calculate number of lines to show:
      if(outer.tableLines.size() != outer.zLine){
        //any multithread problem? this routine detects it firstly.
        outer.zLine = outer.tableLines.size();
        if(ixLine1 >= outer.zLine){ 
          ixLine1 = outer.zLine -1;
        }
      }
      zLineVisible = getVisibleLinesTableImpl();
      if(zLineVisible > zLineVisibleMax){ 
        zLineVisible = zLineVisibleMax;   //no more lines existing.
      }
      ixLine2 = ixLine1 + zLineVisible -1;
      final int dLine;  //Hint: defined outside to see in debugging.
      if(outer.ixLineNew < 2){
        ixLine1 = 0; ixLine2 = zLineVisible -1;
        dLine = 0;
      } else if(outer.ixLineNew > ixLine2 -2){
        dLine = outer.ixLineNew - (ixLine2 -2);
        ixLine1 += dLine; ixLine2 += dLine;
      } else if (outer.ixLineNew < ixLine1 +2){
        dLine = outer.ixLineNew - (ixLine1 +2);  //negative
        ixLine1 += dLine; ixLine2 += dLine;
      } else {
        dLine = 99998;  //unused, debug
      }
      if(ixLine2 >= outer.zLine ){
        ixLine2 = outer.zLine-1;
        ixLine1 = ixLine2 - zLineVisible +1;
        if(ixLine1 < 0){ ixLine1 = 0; }
      }
      ixGlineSelectedNew = outer.ixLineNew - ixLine1;
      
      Assert.check(ixLine2 < outer.zLine && outer.ixLineNew < outer.zLine);
      Assert.check(dLine < 99999);
      //Assert.check(ixGlineSelectedNew < zLineVisible);
      //
      //draw all table cells.
      long dbgtime1 = System.currentTimeMillis() - dbgtime;
      int ixLine3;
      for(ixLine3 = 0; ixLine3 < ixLine1; ++ixLine3){ //mark invsible lines with -1
        GralTable.TableLineData<?> line = outer.tableLines.get(ixLine3);
        line.lineInGraphic = -1;
      }
      iCellLine = 0;
      for(ixLine3 = ixLine1; ixLine3 <= ixLine2 && iCellLine < zLineVisibleMax; ++ixLine3){
        //cells with content
        GralTable.TableLineData<?> line = outer.tableLines.get(ixLine3);
        line.lineInGraphic = iCellLine;
        int ctredraw = line.ctRepaintLine.get();
        if(ctredraw > 0 || true){
          for(int iCellCol = 0; iCellCol < outer.zColumn; ++iCellCol){
            //
            //draw the content of the cell in the graphic implementation.
            //CellData widgiData = 
              drawCellContent(iCellLine, iCellCol, line);
            //widgiData.tableItem = line;  //The widgiData are assigned to the implementation graphic cell.
          }
          iCellLine +=1;
          //Thread safety: set to 0 only if it isn't changed between quest and here.
          //Only then the text isn't changed.
          line.ctRepaintLine.compareAndSet(ctredraw, 0);  
        }
      }
      while(ixLine3 < outer.zLine){
        GralTable.TableLineData<?> line = outer.tableLines.get(ixLine3);
        line.lineInGraphic = -1;
        ixLine3 +=1;
      }
      long dbgtime2 = System.currentTimeMillis() - dbgtime;
      while( iCellLine < zLineVisible) { //Max){
        for(int iCellCol = 0; iCellCol < outer.zColumn; ++iCellCol){
          CellData widgiData = drawCellInvisible(iCellLine, iCellCol);
          widgiData.tableItem = null;  //The widgiData are assigned to the implementation graphic cell.
        }
        iCellLine +=1;
      }
      long dbgtime3 = System.currentTimeMillis() - dbgtime;
      
      //mark current line
      if(outer.ixLineNew >=0 && (outer.ixLineNew != outer.ixLine || bFocused)){
        if(outer.ixLine != outer.ixLineNew){
          outer.actionOnLineSelected(outer.tableLines.get(outer.ixLineNew));
          outer.ixLine = outer.ixLineNew;
        }
        if(ixGlineSelectedNew != ixGlineSelected){ //note: if the table scrolls, the same cell is used as current.
          //set background color for non-selected line.
          if(ixGlineSelected >=0){
            Assert.check(ixGlineSelected < zLineVisibleMax);
            for(int iCellCol = 0; iCellCol < outer.zColumn; ++iCellCol){
              ////Text cellSwt = cellsSwt[ixGlineSelected][iCellCol];
              ////cellSwt.setBackground(colorBackTableSwt);
            }
          }
          ixGlineSelected = ixGlineSelectedNew;
        }
      }
      //long dbgtime4 = System.currentTimeMillis() - dbgtime;
      //System.out.print("\nSwtTable2-redraw1: " + dbgtime1 + " + " + dbgtime2 + " + " + dbgtime3 + " + " + dbgtime4);
      timeLastRedraw = System.currentTimeMillis();
      dbgtime = timeLastRedraw;
      dbgtime1 = System.currentTimeMillis() - dbgtime;
      //System.out.print(", redraw2: " + dbgtime1);
      //System.out.println("GralTable.repaint; " + name);
      //Thread.dumpStack();
      bFocused = false;
      //??? outer.ixLineNew = -1;   
    }

    

    /**This routine will be called inside a resize listener of the implementation graphic.
     * It calculates the width of the columns with the given width of the table's canvas.
     * Then {@link #setBoundsCells()} will be called. 
     * That is implemented in the underlying graphic layer and sets the bounds for each cell.
     * @param pixTable Size of the table area.
     */
    protected void resizeTable(GralRectangle pixTable) {
      int xPixelUnit = itsMng().propertiesGui().xPixelUnit();
      int xPixel1 = 0;
      xpixelCell[0] = xPixel1;
      int ixPixelCell;
      int xPos;
      //Columns from left with positive width
      for(ixPixelCell = 0; ixPixelCell < columnWidthsGral().length && (xPos = columnWidthsGral()[ixPixelCell]) > 0; ++ixPixelCell){
        xPixel1 += xPos * xPixelUnit;
        xpixelCell[ixPixelCell+1] = xPixel1;
      }
      xPixel1 = pixTable.dx;
      xpixelCell[columnWidthsGral().length] = xPixel1;  //right position.
      for(ixPixelCell = columnWidthsGral().length-1; ixPixelCell >=0  && (xPos = columnWidthsGral()[ixPixelCell]) < 0; --ixPixelCell){
        xPixel1 += xPos * xPixelUnit;
        xpixelCell[ixPixelCell] = xPixel1;
      }
      setBoundsCells(0);

    }
    
    
    abstract protected void setBoundsCells(int treeDepthBase);

    @Override public boolean remove(){
      outer.tableLines.clear();
      return true;
    }

    
    /**Sets the current cell as focused with the focus color. It causes
     * a redraw of the whole table because the cell may be shifted in table position.
     * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
     * @param cell The cell 
     */
    protected boolean redrawTableWithFocusedCell(CellData data){
      if(data.tableItem !=null){ //don't do any action if the cell isn't use.
        outer.ixLineNew = data.tableItem.nLineNr; //data.ixCellLine + ixLine1;
        if(outer.ixLineNew >= outer.zLine){ //files may be deleted 
          outer.ixLineNew = outer.zLine >0 ? 0 : -1;  //select the first line or select nothing.
        }
        outer.ixColumn = data.ixCellColumn;
        bFocused = true;
        return true;
      }
      else return false;
    }


    


    protected abstract void drawCellContent(int iCellLine, int iCellCol, GralTable.TableLineData<?> tableItem );

    protected abstract CellData drawCellInvisible(int iCellLine, int iCellCol);

    protected abstract int getVisibleLinesTableImpl();
    
    protected abstract GralMenu createColumnMenu(int column);
    
    
    //protected void setCellDataOnMousePressed(CellData data){ cellDataOnMousePressed = data; } 
    
    /**Data for each Text widget of the graphical implementation layer.
     * An instance is created on creating the text field for the cell in the implementation layer.
     * The instance is referenced only by the text field,
     * It refers the data of the {@link GralTable#tableLines}.
     * <pre>
     * swt.Canvas
     *      |--*>swt.Text
     *               |--data-->CellData
     *                           |---tableItem-->TableLineData
     * 
     * </pre>
     * 
     * Note: The class is visible only in the graphic implementation layer, because it is protected.
     * The elements need to set public because there are not visible elsewhere in the derived class
     * of the outer class. 
     */
    protected static class CellData{
      
      /**The row and column in the graphical presentation. */
      public final int ixCellLine, ixCellColumn;
      
      /**The line data which are presented in this cell. */
      public GralTable.TableLineData<?> tableItem;
      
      /**The color in the graphical presentation. It is the color of the text field. 
       * Note that the color of the text field is only changed if this colors and the 
       * {@link TableLineData#colorBackground} and {@link TableLineData#colorForground} are different. 
       * It saves some calculation time if the color of the text field is set only if it is necessary. */
      public GralColor colorBack, colorText;
      
      public CellData(int ixCellLine, int ixCellColumn){
        this.ixCellLine = ixCellLine; 
        this.ixCellColumn = ixCellColumn;
      }
    }
    
    


 
  }

  
  /**An instance of this class is assigned to any TableItem.
   * It supports the access to the TableItem (it is a table line) via the SWT-independent interface.
   * The instance knows its TableSwt and therefore the supports the access to the whole table.
   *
   */
  public final static class TableLineData<UserData> extends SelectMask implements GralTableLine_ifc<UserData>
  {

    /**The aggregated table. */
    final GralTable<UserData> outer;
    
    
    /**Lines of a children, a tree structure. null for a non-treed table.
     * null it the children are unknown or there are not children.
     */
    protected ArrayList<TableLineData<UserData>> childLines;

    /**True if it is known that the node has children. They may be unknown, then {@link #childLines} are null. */
    boolean hasChildren;
    
    /**True if the children are shown in representation. */
    boolean showChildren;
    
    /**If a repaint is necessary, it is changed by increment by 1. If the redraw is executed
     * and no other redraw is requested, it is set to 0. If another redraw is requested while the
     * redraw runs but isn't finished, it should be executed a second time. Therefore it isn't reset to 0. 
     */
    public AtomicInteger ctRepaintLine = new AtomicInteger();  //NOTE should be public to see it from derived outer class
    
    /**The line visible in graphic. -1 if not visible.*/
    int lineInGraphic = -1;
    
    /**The deepness in the tree presentation of the data.
     * 
     */
    public int treeDepth;
    
    /**The index number in the container {@link GralTable#tableLines}. It is necessary to find out
     * the line in the container if the line is given. Conclusion from line to tableLines.get(line.nLineNr). */
    public int nLineNr;
    
    /**The index value in {@link GralTable#idxLine}.*/
    String key;
    
    public String[] cellTexts;
    
    public GralColor colorForground, colorBackground;
    
    protected UserData userData;
    
    //TODO GralColor colorBack, colorText;
    
    TableLineData(GralTable<UserData> outer){
      this.outer = outer;
      cellTexts = new String[outer.zColumn];
    }
    
    
    public GralTableLine_ifc<UserData> insertChildLine(String childKey, int rowP, String[] childTexts, UserData data){
      if(childLines == null){ 
        hasChildren = true;
        childLines = new ArrayList<TableLineData<UserData>>(); 
      }
      TableLineData<UserData> line = new TableLineData<UserData>(outer);
      line.treeDepth = this.treeDepth +1;
      int zLine = childLines.size();
      int row =  rowP > zLine || rowP < 0 ? zLine : rowP;
      line.nLineNr = row;
      line.key = childKey;
      childLines.add(row, line);
      if(cellTexts !=null){
        for(int ixCol = 0; ixCol < childTexts.length && ixCol < line.cellTexts.length; ++ixCol){
          line.cellTexts[ixCol] = childTexts[ixCol];
        }
      }
      line.userData = userData;
      outer.bFillCells = true;
      if(key !=null){
        //outer.idxLine.put(this.key + '.' + key, line);
      }
      for(int ii=row+1; ii < zLine; ++ii){
        TableLineData<UserData> line2 = childLines.get(ii);
        line2.nLineNr = ii;
      }
      if(this.showChildren){
        repaint(100, 0);
      }
      return line;

    }
    
    
    public void removeChildren(){
      if(childLines !=null){
        childLines = null;
      }
    }
    
    
    @Override public void setEditable(boolean editable){
      throw new IllegalArgumentException("a table line can't be set edit able");
      //TODO set a table line to able to edit?
    }

    /**Query whether the table line is able to edit: Return from the whole table.
     * @see org.vishia.gral.ifc.GralWidget_ifc#isEditable()
     */
    @Override public boolean isEditable(){ 
      return outer.bEditable; 
    }
    
    
    @Override public boolean isNotEditableOrShouldInitialize(){ return outer.isNotEditableOrShouldInitialize(); }

    
    
    
    @Override public boolean isChanged(boolean setUnchanged){ 
      return false; 
    }


    
    @Override public String getName(){ return outer.name; }
    

    @Override public String getCellText(int column) { 
      String text = cellTexts[column]; 
      return text == null ? "" : text;
    }

    @Override public String[] getCellTexts() { return cellTexts; }

    @Override
    public String setCellText(String text, int column) {
      String oldText = cellTexts[column];
      cellTexts[column] = text;
      outer.repaint(100, 0);
      return oldText;
    }

    @Override public UserData getUserData() { return userData;  }

    @Override public void setUserData(UserData data) {this.userData = data; }

    @Override public long setContentIdent(long date){ long last = outer.dateUser; outer.dateUser = date; return last; }
    
    @Override public long getContentIdent(){ return outer.dateUser; }

    @Override public int getLineNr()
    { return nLineNr;
    }

    @Override
    public int getSelectedColumn()
    { return outer.ixColumn;
    }

    @Override public void setFocus() { outer.setFocus(); }

    @Override public boolean setVisible(boolean visible) 
    { return false;  //TODO line visible. 
    }

    
    
    @Override public void setFocus(int delay, int latest) { outer.setFocus(delay, latest); }

    @Override public boolean isVisible(){ return outer.isVisible(); }
    

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
    
    @Override
    public void repaint() {
      ctRepaintLine.addAndGet(1);
      outer.repaint(); 
    }

    @Override public void repaint(int delay, int latest){
      ctRepaintLine.addAndGet(1);
      outer.repaint(delay, latest); 
      //itsMng.setInfoDelayed(this, GralPanelMngWorking_ifc.cmdRedraw, 0, null, null, delay);
    }
    

    

    
    @Override
    public void setBoundsPixel(int x, int y, int dx, int dy) {
      // TODO Auto-generated method stub
      
    }

    @Override public void refreshFromVariable(VariableContainer_ifc container){
      outer.refreshFromVariable(container);
    }

    
    @Override public void setDataPath(String sDataPath){
      outer.setDataPath(sDataPath);
    }

    
    

    //@Override public Object[] getWidgetMultiImplementations(){ return null; }

    
    @Override
    public boolean remove()
    { return outer.gi.remove();
    }

    
    
    
    @Override public void setHtmlHelp(String url) { outer.setHtmlHelp(url); }
    
    
    @Override public Object getContentInfo(){ return userData; }

    
    
    @Override public int getMark(){
      if(userData instanceof MarkMask_ifc){
        return ((MarkMask_ifc)userData).getMark();
      } else {
        return super.getMark();
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
    { if(outer.actionMarkOnLine !=null && data !=null){ outer.actionMarkOnLine.setNonMarked(mask, data); }
      if(userData instanceof MarkMask_ifc){
        ((MarkMask_ifc)userData).setNonMarked(mask, data);
      }
      return super.setNonMarked(mask, data);
    }
    
    /**Sets the mark status of the line.
     * It invokes the method given with {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * with the given {@link #setUserData(Object)} of this line. It means a selection with the user data
     * may be done too. This method will be called especially on pressing the mark key specified with  
     * {@link GralTable#specifyKeysMarkUpDn(int, int)}
     * @param mask This bits of the {@link SelectMask#selectMask} will be set.
     * @param data if null then don't invoke {@link GralTable#specifyActionOnLineMarked(MarkMask_ifc)}
     * @see org.vishia.util.SelectMask#setNonMarked(int, java.lang.Object)
     */
    @Override public int setMarked(int mask, Object data)
    { if(outer.actionMarkOnLine !=null && data !=null){ outer.actionMarkOnLine.setMarked(mask, data); }
      if(userData instanceof MarkMask_ifc){
        ((MarkMask_ifc)userData).setMarked(mask, data);
      }
      return super.setMarked(mask, data);
    }

    @Override public GralMng gralMng(){ return outer.gralMng(); }
    
    @Override public void setToPanel(GralMngBuild_ifc mng){
      throw new IllegalArgumentException("GralTableLine.setToPanel - is illegal; Use GralTable.setToPanel(...)");
    }

    
  }
  

  
  
}
