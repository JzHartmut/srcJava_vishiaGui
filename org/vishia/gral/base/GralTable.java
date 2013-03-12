package org.vishia.gral.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;
import org.vishia.util.SelectMask;

/**
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTable extends GralWidget implements GralTable_ifc {

  /**Version and history
   * <ul>
   * <li>2012-08-22 Hartmut new {@link #setCurrentLine(int)} with int, it isn't new because it was able to set with
   *   {@link #setCurrentCell(int, int)} with -1 as second parameter.
   * <li>2012-07-15 Hartmut new: search functionality: The implementation should/may have a text field 
   *   which shows the search string. While up and down keys the that lines are selected which text in the {@link #ixColumn}
   *   starts whith the search string.
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
   * <li>2011-11-27 Hartmut new {@link #setActionOnLineSelected(GralUserAction)}: The user action is called
   *   anytime if a line is selected by user operation. It can be show any associated content anywhere
   *   additionally. It is used for example in "The.file.Commander" to show date, time and maybe content 
   *   while the user selects any files. The graphical implementation should be call {@link #actionOnLineSelected(GralTableLine_ifc)}
   *   in its Selection listener. 
   * <li>2011-11-20 Hartmut new The capability of selection of lines is moved from the 
   *   {@link org.vishia.gral.widget.GralSelectList} to this class. It means any table has the capability
   *   of selection of multiple lines. This capability is supported with a extension of the
   *   {@link GralTableLine_ifc} with {@link org.vishia.util.SelectMask_ifc}. The selection of a line
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
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 20120715;

  
  protected int keyMarkUp = KeyCode.shift + KeyCode.up, keyMarkDn = KeyCode.shift + KeyCode.dn;
  
  
  protected final Map<String, GralTableLine_ifc> idxLine = new TreeMap<String, GralTableLine_ifc>();
  
  /**This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object. */
  GralUserAction actionOnLineSelected;
  
  /**Width of each column in GralUnits. */
  protected int[] columnWidthsGral;
  
  /**Start position of each column in pixel. */
  protected int[] columnPixel;

  protected GralMenu[] menuColumns;
  

  /**The colors of each cell. It is set with the color of {@link TableItemWidget#colorBackground} and
   * {@link TableItemWidget#colorBackground} of the currently displayed line.
   */
  //private GralColor[] colorBack, colorText;
  

  /**Pixel per line. */
  protected int linePixel;
  
  protected final int[] xpixelCell;

  
  /**Index (subscript) of the current line and current column. 0 is the left column or top line.
   * -1 means that nothing is selected. */
  protected int ixLine, ixLineNew, ixColumn;

  /**Index of {@link #texts} of the cell left top for presentation. */
  protected int ixLine1, ixCol1;
  
  /**Index of {@link #texts} of the cell right bottom for presentation. It depends of the size of presentation. */
  protected int ixLine2, ixCol2;
  
  
  /**Number of lines and columns of data. */
  protected int zLine, zColumn;
  
  /**Current number of visible lines in the view. */
  protected int zLineVisible;
  
  /**maximum number of visible lines. it is the static amount of line-cells for displaying. */
  protected int zLineVisibleMax = 20;  
  
  /**Index (subscript) of the graphical line (not lines of the table)
   * which has the selection color and which should be gotten the selection color.
   */
  protected int ixGlineSelected = -1, ixGlineSelectedNew = -1;
  
  
  protected final StringBuilder searchChars = new StringBuilder(20);
  
  /**Any line of the table has one TableItemWidget, a long table has some more.
   * Contains content, color, selection etc. of the lines with there columns.
   * 
   */
  protected ArrayList<TableItemWidget> tableLines = new ArrayList<TableItemWidget>();
  
  /**True if a line or a column is marked. */
  //protected boolean[] markedLines, markedColumns;
  
  /**If true then the graphic implementation fields for cells should be filled newly with the text. */
  protected boolean bFillCells;
  
  /**Check the last time of redrawing. */
  protected long timeLastRedraw;
  
  /**If set, then a next key will be processed. It is set to false if a key event is executed
   * and it is set to true in {@link #keyActionDone}. */
  private boolean keyDone = true;
  
  /**The last key which was pressed  */
  private int lastKey;
  
  /**Number of key repetitions if a key was not used because the redraw was pending yet. */
  private int keyRepetition;
  
  
  /**Set to true while {@link #table}.{@link Table#redrawGthread()} is running.
   * It prevents recursive invocation of redraw() while setFocus() is invoked. */
  protected boolean bRedrawPending;

  /**Set true if the focus is gained by mouse click. It causes color set and 
   * invocation of {@link #actionOnLineSelected(GralTableLine_ifc)}. 
   */
  protected boolean bFocused;
  

  
  /**The colors. */
  protected GralColor colorBackSelect, colorBackMarked, colorBackTable
  //, colorBackSelectNonFocused, colorBackMarkedNonFocused, colorBackTableNonFocused
  , colorTextSelect, colorTextMarked, colorTextTable;
  
  
  protected GralColor colorSelectCharsBack, colorSelectChars;

  public GralTable(String name, GralMng mng, int[] columnWidths) {
    super(name, 'L', mng);
    this.columnWidthsGral = columnWidths;
    this.xpixelCell = new int[columnWidthsGral.length+1];
    this.zColumn = columnWidths.length;
    //this.colorBack = new GralColor[zLineVisibleMax];
    //this.colorText = new GralColor[zLineVisibleMax];
    int xdPix = itsMng.propertiesGui().xPixelUnit();
    columnPixel = new int[columnWidthsGral.length+1];
    int xPix = 0;
    columnPixel[0] = xPix;
    for(int iCol = 0; iCol < columnWidthsGral.length; ++iCol){
      xPix += columnWidthsGral[iCol] * xdPix;
      columnPixel[iCol+1] = xPix;
    }
    int ydPix = itsMng.propertiesGui().yPixelUnit();
    linePixel = 2 * ydPix;
    ixLineNew = ixLine = -1;
    ixGlineSelectedNew = ixGlineSelected = -1;  

    setColors();
  }

  /**Sets an action which is called any time when another line is selected.
   * This action will be called any time when the selection of the current line is changed. 
   * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   * with the line as Object.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void setActionOnLineSelected(GralUserAction actionOnLineSelected){
    this.actionOnLineSelected = actionOnLineSelected;
  }
  
  
  public void addContextMenuEntryGthread(int col, String name, String sMenuPath, GralUserAction action){
    menuColumns[col].addMenuItemGthread(name, sMenuPath, action);
  }
  
  
  public void setColumnWidth(int width, int[] columnWidths){
    columnWidthsGral = columnWidths;
  }
  
  @Override public boolean setCurrentLine(String key){
    GralTableLine_ifc line = idxLine.get(key);
    if(line == null) return false;
    else {
      int nLine = line.getLineNr();
      return setCurrentCell(nLine, -1);
    }
  }
  
  @Override public boolean setCurrentLine(int nLine){ return setCurrentCell(nLine, -1); }

  
  public void setColors(){
    colorBackSelect = GralColor.getColor("am");
    colorBackMarked = GralColor.getColor("rd");
    colorBackTable = GralColor.getColor("wh");
    //colorBackSelectNonFocused = GralColor.getColor("am");
    //colorBackMarkedNonFocused = GralColor.getColor("lrd");
    //colorBackTableNonFocused = GralColor.getColor("gr");
    colorTextTable = GralColor.getColor("bk");
    colorSelectCharsBack = GralColor.getColor("lgr");
    colorSelectChars = GralColor.getColor("wh");
  }
  
  
  
  @Override
  public GralTableLine_ifc getCurrentLine() {
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

  
  @Override public GralTableLine_ifc getLine(int row) {
    if(row > tableLines.size()) return null;
    else return tableLines.get(row);
  }

  @Override
  public GralTableLine_ifc getLine(String key) {
    // TODO Auto-generated method stub
    return idxLine.get(key);
  }

  @Override public GralTableLine_ifc insertLine(String key, int row, String[] cellTexts, Object userData) {
    TableItemWidget line = new TableItemWidget();
    if(row > zLine || row < 0){
      row = zLine;
    }
    line.nLineNr = row;
    line.key = key;
    tableLines.add(row, line);
    zLine = tableLines.size();
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
    repaint(100, 0);
    return line;
  }

  @Override
  public void deleteLine(GralTableLine_ifc line) {
    // TODO Auto-generated method stub
    
  }

  @Override public void clearTable() {
    ixLine1 = ixLine2 = 0;
    ixColumn = 0;
    zLine = 0;
    tableLines.clear();
    ixLineNew = ixLine = -1;
    ixGlineSelectedNew = -1;  //deselects ixGlineSelected on redraw!
    idxLine.clear();
    searchChars.setLength(0);
    repaint(200,200);
  }

  @Override public List<GralTableLine_ifc> getSelectedLines() {
    List<GralTableLine_ifc> list = new LinkedList<GralTableLine_ifc>();
    for(TableItemWidget item: tableLines){
      if((item.getSelection() & 1) !=0){
          list.add(item);
      }
    }
    return list;
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
        if(ixLine > zLineVisible){
          ixLineNew = ixLine - zLineVisible;
        } else {
          ixLineNew = 0;
        }
        keyActionDone.addToGraphicThread(itsMng.gralDevice(), 0);
      } break;
      case KeyCode.mouseWheelUp:
      case KeyCode.up: {
        if(ixLine > 0){
          //ixLineNew = ixLine - keyRepetition;
          searchContent(true);
          if(ixLineNew <0){ ixLineNew = 0; }
        }
        keyActionDone.addToGraphicThread(itsMng.gralDevice(), 0);
      } break;
      case KeyCode.mouseWheelDn:
      case KeyCode.dn: {
        if(ixLine < zLine -1){
          //ixLineNew = ixLine + keyRepetition;
          searchContent(false);
          if(ixLineNew >= zLine ){ ixLineNew = zLine -1; }
        }
        keyActionDone.addToGraphicThread(itsMng.gralDevice(), 0);
      } break;
      case KeyCode.pgdn: {
        if(ixLine < zLine - zLineVisible){
          ixLineNew = ixLine + zLineVisible;
        } else {
          ixLineNew = zLine -1;
        }
        keyActionDone.addToGraphicThread(itsMng.gralDevice(), 0);
      } break;
      default:
        if(KeyCode.isTextKey(keyCode)){
          searchChars.appendCodePoint(keyCode);
          repaint();
        } else if(keyCode == KeyCode.esc){
          searchChars.setLength(0);
          repaint();
        } else if(keyCode == KeyCode.back && searchChars.length() >0){
          searchChars.setLength(searchChars.length()-1);
          repaint();
        } else {
          done = false;
        }
      }//switch
      if(done == false && keyCode == keyMarkDn && ixLine >=0){
        GralTableLine_ifc line = tableLines.get(ixLine);
        if((line.getSelection() & 1)!=0){
          //it is selected yet
          line.setForegroundColor(GralColor.getColor("bk"));
          line.setDeselect(1);
        } else {
          line.setForegroundColor(GralColor.getColor("rd"));
          line.setSelect(1);
        }
        if(ixLine < zLine -1){
          ixLineNew = ixLine + 1;
        }
        keyActionDone.addToGraphicThread(itsMng.gralDevice(), 0);
        done = true;
      }
      if(!done && ixLine >=0){
        GralTableLine_ifc lineGral = tableLines.get(ixLine);
        if(actionChanging !=null){
          //all other keys: call actionChanging.
          done = actionChanging.userActionGui(keyCode, this, lineGral);
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
          if(!mainKeyAction.userActionGui(keyCode, this)){
            done = mainKeyAction.userActionGui("key", this, new Integer(keyCode));
          }
        }
      }
      keyRepetition = 0;  //because it was done.
    }//if not redraw pending.
    lastKey = keyCode;
    return done;
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
          GralTableLine_ifc lineGral = tableLines.get(ixLineNew);
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
  private final GralDispatchCallbackWorker keyActionDone = new GralDispatchCallbackWorker("GralTableKeyDone") {
    @Override
    public void doBeforeDispatching(boolean onlyWakeup) {
      bFocused = true;  //to focus while repainting
      repaintGthread();
      keyDone = true;
      //System.out.println("Key done");
      removeFromQueue(itsMng.gralDevice());
    }
  };


  /**Redraws the whole table because the current line is changed or the focus is changed
   * or the content is changed and #re
   * TODO
   * {@link GralWidgetGthreadSet_ifc#redrawGthread()}
   */
  protected void setAllCellContentGthread(){
    long dbgtime = System.currentTimeMillis();
    bRedrawPending = true;
    Assert.check(itsMng.currThreadIsGraphic());
    int iCellLine;
    //calculate number of lines to show:
    if(tableLines.size() != zLine){
      //any multithread problem? this routine detects it firstly.
      zLine = tableLines.size();
      if(ixLine1 >= zLine){ 
        ixLine1 = zLine -1;
      }
    }
    zLineVisible = getVisibleLinesTableImpl();
    if(zLineVisible > zLineVisibleMax){ 
      zLineVisible = zLineVisibleMax;   //no more lines existing.
    }
    ixLine2 = ixLine1 + zLineVisible -1;
    final int dLine;  //Hint: defined outside to see in debugging.
    if(ixLineNew < 2){
      ixLine1 = 0; ixLine2 = zLineVisible -1;
      dLine = 0;
    } else if(ixLineNew > ixLine2 -2){
      dLine = ixLineNew - (ixLine2 -2);
      ixLine1 += dLine; ixLine2 += dLine;
    } else if (ixLineNew < ixLine1 +2){
      dLine = ixLineNew - (ixLine1 +2);  //negative
      ixLine1 += dLine; ixLine2 += dLine;
    }
    if(ixLine2 >= zLine ){
      ixLine2 = zLine-1;
    }
    ixGlineSelectedNew = ixLineNew - ixLine1;
    
    Assert.check(ixLine2 < zLine && ixLineNew < zLine);
    //Assert.check(ixGlineSelectedNew < zLineVisible);
    //
    //draw all table cells.
    long dbgtime1 = System.currentTimeMillis() - dbgtime;
    iCellLine = 0;
    for(int ixLine3 = ixLine1; ixLine3 <= ixLine2 && iCellLine < zLineVisibleMax; ++ixLine3){
      //cells with content
      TableItemWidget line = tableLines.get(ixLine3);
      int ctredraw = line.ctRepaintLine.get();
      if(ctredraw > 0 || true){
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
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
    long dbgtime2 = System.currentTimeMillis() - dbgtime;
    while( iCellLine < zLineVisible) { //Max){
      for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
        CellData widgiData = drawCellInvisible(iCellLine, iCellCol);
        widgiData.tableItem = null;  //The widgiData are assigned to the implementation graphic cell.
      }
      iCellLine +=1;
    }
    long dbgtime3 = System.currentTimeMillis() - dbgtime;
    
    //mark current line
    if(ixLineNew >=0 && (ixLineNew != ixLine || bFocused)){
      actionOnLineSelected(tableLines.get(ixLineNew));
      ixLine = ixLineNew;
      ixLine = ixLineNew;
      if(ixGlineSelectedNew != ixGlineSelected){ //note: if the table scrolls, the same cell is used as current.
        //set background color for non-selected line.
        if(ixGlineSelected >=0){
          Assert.check(ixGlineSelected < zLineVisibleMax);
          for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
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
    if(name.equals("tableSelect-doc.3"))
      dbgtime = 0;
    //System.out.println("GralTable.repaint; " + name);
    //Thread.dumpStack();
    bFocused = false;
       
  }

  
  protected void resizeTable(int xpixel, int ypixel) {
    
  }
  

  protected abstract void drawCellContent(int iCellLine, int iCellCol, TableItemWidget tableItem );

  protected abstract CellData drawCellInvisible(int iCellLine, int iCellCol);

  protected abstract int getVisibleLinesTableImpl();
  
  
  
  /**It is called whenever another line is selected, if the focus is gotten by mouse click
   * or a navigation key is pressed. 
   * This method calls the {@link GralUserAction#userActionGui(int, GralWidget, Object...)}.
   * The {@link #setActionOnLineSelected(GralUserAction)} can be set with any user instantiation
   * of that interface. The params[0] of that routine are filled with the {@link GralTableLine_ifc}
   * of the selected line.
   *  
   * @param line
   */
  protected void actionOnLineSelected(GralTableLine_ifc line){
    if(actionOnLineSelected !=null){
      actionOnLineSelected.userActionGui(KeyCode.tableLineSelect, this, line);
    }
  }
  
  
  
  
  

  
  /**An instance of this class is assigned to any TableItem.
   * It supports the access to the TableItem (it is a table line) via the SWT-independent interface.
   * The instance knows its TableSwt and therefore the supports the access to the whole table.
   *
   */
  protected class TableItemWidget extends SelectMask implements GralTableLine_ifc
  {

    /**If a repaint is necessary, it is changed by increment by 1. If the redraw is executed
     * and no other redraw is requested, it is set to 0. If another redraw is requested while the
     * redraw runs but isn't finished, it should be executed a second time. Therefore it isn't reset to 0. 
     */
    public AtomicInteger ctRepaintLine = new AtomicInteger();  //NOTE should be public to see it from derived outer class
    
    public int nLineNr;
    
    String key;
    
    public String[] cellTexts;
    
    public GralColor colorForground, colorBackground;
    
    private Object userData;
    
    //TODO GralColor colorBack, colorText;
    
    TableItemWidget(){
      cellTexts = new String[zColumn];
    }
    
    
    @Override public void setEditable(boolean editable){
      throw new IllegalArgumentException("a table line can't be set edit able");
      //TODO set a table line to able to edit?
    }

    /**Query whether the table line is able to edit: Return from the whole table.
     * @see org.vishia.gral.ifc.GralWidget_ifc#isEditable()
     */
    @Override public boolean isEditable(){ 
      return (GralTable.this).bEditable; 
    }
    
    
    @Override public boolean isNotEditableOrShouldInitialize(){ return (GralTable.this).isNotEditableOrShouldInitialize(); }

    
    
    
    @Override public boolean isChanged(boolean setUnchanged){ 
      return false; 
    }


    
    @Override public String getName(){ return name; }
    

    @Override public String getCellText(int column) { 
      String text = cellTexts[column]; 
      return text == null ? "" : text;
    }

    @Override public String[] getCellTexts() { return cellTexts; }

    @Override
    public String setCellText(String text, int column) {
      String oldText = cellTexts[column];
      cellTexts[column] = text;
      GralTable.this.repaint(100, 0);
      return oldText;
    }

    @Override public Object getUserData() { return userData;  }

    @Override public void setUserData(Object data) {this.userData = data; }

    @Override public int getLineNr()
    { return nLineNr;
    }

    @Override
    public int getSelectedColumn()
    { return ixColumn;
    }

    @Override public void setFocus() { GralTable.this.setFocus(); }

    @Override public void setFocus(int delay, int latest) { GralTable.this.setFocus(delay, latest); }

    @Override public boolean isVisible(){ return GralTable.this.isVisible(); }
    

    @Override
    public GralColor setBackgroundColor(GralColor color) {
      GralColor ret = colorBackground;
      colorBackground = color;
      repaint(50, 50);
      return color;
    }

    @Override
    public GralColor setForegroundColor(GralColor color) {
      GralColor ret = colorForground;
      colorForground = color;
      repaint(50, 50);
      return color;
    }

    
    @Override public void setBackColor(GralColor color, int ix)
    { 
      colorBackground = color;
      repaint(50, 50);
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

    
    @Override public void setDataPath(String sDataPath){
      GralTable.this.setDataPath(sDataPath);
    }

    
    
    @Override
    public Object getWidgetImplementation() {
      // TODO Auto-generated method stub
      return null;
    }

    //@Override public Object[] getWidgetMultiImplementations(){ return null; }

    
    @Override
    public boolean remove()
    {
      // TODO Auto-generated method stub
      return false;
    }

    
    @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

    /**Implementation of the graphic thread widget set interface. */
    GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

      @Override public void clearGthread()
      { // TODO Auto-generated method stub
      }

      @Override public void insertGthread(int pos, Object visibleInfo, Object data)
      { // TODO Auto-generated method stub
      }

      @Override public void redrawGthread()
      { // TODO Auto-generated method stub
      }

      @Override public void setBackGroundColorGthread(GralColor color)
      { // TODO Auto-generated method stub
      }

      @Override public void setForeGroundColorGthread(GralColor color)
      { // TODO Auto-generated method stub
      }

      @Override public void setTextGthread(String text, Object data)
      { // TODO Auto-generated method stub
      }
    };

    
    
    
    @Override public void setHtmlHelp(String url) { GralTable.this.setHtmlHelp(url); }
    
    
    

    
    
  }
  

  /**Data for each Text widget.
   * Note: The class is visible only in the graphic implementation layer, because it is protected.
   * The elements need to set public because there are not visible elsewhere in the derived class
   * of the outer class. 
   */
  protected static class CellData{
    public final int ixCellLine, ixCellColumn;
    public TableItemWidget tableItem;
    public GralColor colorBack, colorText;
    public CellData(int ixCellLine, int ixCellColumn){
      this.ixCellLine = ixCellLine; 
      this.ixCellColumn = ixCellColumn;
    }
  }
  
  

  
  
}
