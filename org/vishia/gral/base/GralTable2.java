package org.vishia.gral.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;
import org.vishia.util.SelectMask;

/**
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTable2 extends GralTable{

  /**Version and history
   * <ul>
   * <li>2012-01-15 Hartmut new: {@link #setCurrentLine(String)}, {@link #insertLine(String, int, String[], Object)}:
   *    the key is supported now. 
   * <li>2012-01-06 Hartmut new: concept of a table which is independent of the table implementation 
   *   of the table implementations in the graphic system layer or in operation system: 
   *   The capability  of a SWT-table is not sufficient, for example the color of the selection bar
   *   is not able to change. Other reason: Implementation of table in SWT, AWT, Swing is different.
   *   It seems better to have one table concept with independent features, which based on simple widgets.
   *   is not  
   * </ul>
   */
  public final static int version = 0x20120113;

  
  /**Width of each column in GralUnits. */
  protected int[] columnWidthsGral;
  
  /**Start position of each column in pixel. */
  protected int[] columnPixel;

  
  /**The colors of each cell. It is set with the color of {@link TableItemWidget#colorBackground} and
   * {@link TableItemWidget#colorBackground} of the currently displayed line.
   */
  private GralColor[] colorBack, colorText;
  

  /**Pixel per line. */
  protected int linePixel;
  
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
  
  /**Texts in all lines and columns.
   * 
   */
  protected ArrayList<TableItemWidget> tableLines = new ArrayList<TableItemWidget>();;
  
  /**True if a line or a column is marked. */
  //protected boolean[] markedLines, markedColumns;
  
  /**If true then the graphic implementation fields for cells should be filled newly with the text. */
  protected boolean bFillCells;
  
  protected long timeLastRedraw;
  
  /**Set to true while {@link #table}.{@link Table#redrawGthread()} is running.
   * It prevents recursive invocation of redraw() while setFocus() is invoked. */
  protected boolean bRedrawPending;

  /**Set true if the focus is gained by mouse click. It causes color set and 
   * invocation of {@link #actionOnLineSelected(GralTableLine_ifc)}. 
   */
  protected boolean bFocused;
  

  
  /**The colors. */
  protected GralColor colorBackSelect, colorBackMarked, colorBackTable
  , colorBackSelectNonFocused, colorBackMarkedNonFocused, colorBackTableNonFocused
  , colorTextSelect, colorTextMarked, colorTextTable;
  

  public GralTable2(String name, GralWidgetMng mng, int[] columnWidths) {
    super(name, mng);
    this.columnWidthsGral = columnWidths;
    this.zColumn = columnWidths.length;
    this.colorBack = new GralColor[zLineVisibleMax];
    this.colorText = new GralColor[zLineVisibleMax];
    int xdPix = itsMng.propertiesGui.xPixelUnit();
    columnPixel = new int[columnWidthsGral.length+1];
    int xPix = 0;
    columnPixel[0] = xPix;
    for(int iCol = 0; iCol < columnWidthsGral.length; ++iCol){
      xPix += columnWidthsGral[iCol] * xdPix;
      columnPixel[iCol+1] = xPix;
    }
    int ydPix = itsMng.propertiesGui.yPixelUnit();
    linePixel = 2 * ydPix;
    ixLineNew = ixLine = -1;
    ixGlineSelectedNew = ixGlineSelected = -1;  

    setColors();
  }

  
  public void setColors(){
    colorBackSelect = GralColor.getColor("gn");
    colorBackMarked = GralColor.getColor("rd");
    colorBackTable = GralColor.getColor("wh");
    colorBackSelectNonFocused = GralColor.getColor("lgn");
    colorBackMarkedNonFocused = GralColor.getColor("lrd");
    colorBackTableNonFocused = GralColor.getColor("gr");
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


  
  @Override public GralTableLine_ifc getLine(int row) {
    if(row > tableLines.size()) return null;
    else return tableLines.get(row);
  }

  @Override
  public GralTableLine_ifc getLine(String key) {
    // TODO Auto-generated method stub
    return null;
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
    repaint();
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

  
 
  protected boolean processKeys(int keyCode){
    boolean done = true;
    long time = System.currentTimeMillis();
    if( (time - timeLastRedraw) > 50){
      switch(keyCode){
      case KeyCode.pgup: {
        if(ixLine > zLineVisible){
          ixLineNew = ixLine - zLineVisible;
          repaint();
        } else {
          ixLineNew = 0;
          repaint();
        }
      } break;
      case KeyCode.up: {
        if(ixLine > 0){
          ixLineNew = ixLine - 1;
          repaint();
        }
      } break;
      case KeyCode.dn: {
        if(ixLine < zLine -1){
          ixLineNew = ixLine + 1;
          repaint();
        }
      } break;
      case KeyCode.pgdn: {
        if(ixLine < zLine - zLineVisible){
          ixLineNew = ixLine + zLineVisible;
          repaint();
        } else {
          ixLineNew = zLine -1;
          repaint();
        }
      } break;
      default:
        done = false;
      }//switch
    }
    if(!done && ixLine >=0){
      GralTableLine_ifc lineGral = tableLines.get(ixLine);
      if(!procStandardKeys(keyCode, lineGral, ixLine)){
        if(actionChanging !=null){ 
          done = actionChanging.userActionGui(keyCode, this, lineGral);
        }
      }
    } //if(table.)
    if(!done){
      GralUserAction mainKeyAction = itsMng.getRegisteredUserAction("KeyAction");
      if(mainKeyAction !=null){
        //old form called because compatibility, if new for with int-parameter returns false.
        if(!mainKeyAction.userActionGui(keyCode, this)){
          done = mainKeyAction.userActionGui("key", this, new Integer(keyCode));
        }
      }
    }
    return done;
  }

  
  


  /**Redraws the whole table because the current line is changed or the focus is changed
   * or the content is changed and #re
   * TODO
   * {@link SwtWidgetSet_ifc#redrawGthread()}
   */
  protected void redrawTableGthread(){
    long dbgtime = System.currentTimeMillis();
    //test
    //try{ Thread.sleep(80);} catch(InterruptedException exc){}
    bRedrawPending = true;
    Assert.check(itsMng.currThreadIsGraphic());
    int iCellLine;
    /*
    if(ixLineNew != ixLine && ixGlineSelected >=0){
      iCellLine = ixLineNew - ixLine1;  //The line which is currently present and selected, before changing ixLine1:
      if(iCellLine != ixGlineSelected){ //another line to select:
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
          //cellsSwt[ixGlineSelected][iCellCol].setBackground(colorBackTableSwt);
        }
        ixGlineSelected = -1; //because selection isn't shown.
      }
    }
    */
    //calculate number of lines to show:
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
    Assert.check(ixGlineSelectedNew < zLineVisible);
    //
    //draw all table cells.
    long dbgtime1 = System.currentTimeMillis() - dbgtime;
    iCellLine = 0;
    for(int ixLine3 = ixLine1; ixLine3 <= ixLine2 && iCellLine < zLineVisibleMax; ++ixLine3){
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
    while( iCellLine < zLineVisibleMax){
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
    //}
    //if(true || ixLineNew != ixLine){
      ixLine = ixLineNew;
      //ixGlineSelectedNew = iCellLine = ixLine - ixLine1;
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
      for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
        ////Text cellSwt = cellsSwt[iCellLine][iCellCol];
        //Note: The background color isn't set yet because this routine may be called
        //in a fast key repetition (50 ms). In the next few ms the next cell may have
        //the focus then. The setBackground needs about 5 ms per cell on Linux GTK
        //with an Intel-Atom-processor. It is too much.
        //The focus is able to see because the cursor is there.
        //The color will be set in the writeContentLast.
        //
        ///don't invoke: 
        ////cellSwt.setBackground(colorBackSelectSwt);
        if(iCellCol == ixColumn){
          ////SwtWidgetHelper.setFocusOfTabSwt(cellSwt);
          //cellSwt.setFocus();  
        }
      }
    }
    ///writeContentLast.addToGraphicThread(itsMng.gralDevice, 200);
    long dbgtime4 = System.currentTimeMillis() - dbgtime;
    System.out.print("\nSwtTable2-redraw1: " + dbgtime1 + " + " + dbgtime2 + " + " + dbgtime3 + " + " + dbgtime4);
    dbgtime = System.currentTimeMillis();
    dbgtime1 = System.currentTimeMillis() - dbgtime;
    System.out.print(", redraw2: " + dbgtime1);
    
  }

  

  protected abstract void drawCellContent(int iCellLine, int iCellCol, TableItemWidget tableItem );

  protected abstract CellData drawCellInvisible(int iCellLine, int iCellCol);

  protected abstract int getVisibleLinesTableImpl();
  
  
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
    
    int nLineNr;
    
    String key;
    
    public String[] cellTexts;
    
    GralColor colorForground, colorBackground;
    
    private Object userData;
    
    //TODO GralColor colorBack, colorText;
    
    TableItemWidget(){
      cellTexts = new String[zColumn];
    }
    
    @Override public String getName(){ return name; }
    

    @Override public String getCellText(int column) { return cellTexts[column]; }

    @Override public String[] getCellTexts() { return cellTexts; }

    @Override
    public String setCellText(String text, int column) {
      String oldText = cellTexts[column];
      cellTexts[column] = text;
      GralTable2.this.repaint(100, 0);
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

    @Override
    public boolean setFocus() {
      // TODO Auto-generated method stub
      return false;
    }

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

    @Override
    public void repaint() {
      ctRepaintLine.addAndGet(1);
      GralTable2.this.repaint(); 
    }

    @Override public void repaint(int delay, int latest){
      itsMng.setInfoDelayed(this, GralPanelMngWorking_ifc.cmdRedraw, 0, null, null, delay);
    }
    

    

    
    @Override
    public void setBoundsPixel(int x, int y, int dx, int dy) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public Object getWidgetImplementation() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean remove()
    {
      // TODO Auto-generated method stub
      return false;
    }

  }
  

  /**Data for each Text widget.
   * Note: The class is visible only in the graphic implementation layer, because it is protected.
   * The elements need to set public because there are not visible elsewhere in the derived class
   * of the outer class. 
   */
  protected class CellData{
    public final int ixCellLine, ixCellColumn;
    public TableItemWidget tableItem;
    public GralColor colorBack, colorText;
    public CellData(int ixCellLine, int ixCellColumn){
      this.ixCellLine = ixCellLine; 
      this.ixCellColumn = ixCellColumn;
    }
  }
  
  

  
  
}
