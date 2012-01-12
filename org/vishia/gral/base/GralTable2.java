package org.vishia.gral.base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;
import org.vishia.util.SelectMask;

/**
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTable2 extends GralTable{

  /**Version and history
   * <ul>
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

  /**Pixel per line. */
  protected int linePixel;
  
  /**Index (subscript) of {@link #texts} of the current line and current color. 0 means: nothing is selected. 
   * 1 is the first line and the left column.
   * 
   */
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
  
  /**The colors. */
  protected GralColor colorBackSelect, colorBackMarked, colorBackTable
  , colorBackSelectNonFocused, colorBackMarkedNonFocused, colorBackTableNonFocused
  , colorTextSelect, colorTextMarked, colorTextTable;
  

  public GralTable2(String name, GralWidgetMng mng, int[] columnWidths) {
    super(name, mng);
    this.columnWidthsGral = columnWidths;
    this.zColumn = columnWidths.length;
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
  public void setCurrentCell(int line, int column) {
    if(line < 0 || line > zLine-1){ line = zLine -1; }
    if(column < 0 || column > zColumn-1){ column = zColumn -1; }
    ixLineNew = line;  //forces color setting select color
    ixColumn = column;
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
    tableLines.add(row, line);
    zLine = tableLines.size();
    if(cellTexts !=null){
      for(int ixCol = 0; ixCol < cellTexts.length && ixCol < line.cellTexts.length; ++ixCol){
        line.cellTexts[ixCol] = cellTexts[ixCol];
      }
    }
    line.userData = userData;
    bFillCells = true;
    //redraw();
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
    redraw();
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
    switch(keyCode){
    case KeyCode.pgup: {
      if(ixLine > zLineVisible){
        ixLineNew = ixLine - zLineVisible;
        redraw();
      } else {
        ixLineNew = 0;
        redraw();
      }
    } break;
    case KeyCode.up: {
      if(ixLine > 0){
        ixLineNew = ixLine - 1;
        redraw();
      }
    } break;
    case KeyCode.dn: {
      if(ixLine < zLine -1){
        ixLineNew = ixLine + 1;
        redraw();
      }
    } break;
    case KeyCode.pgdn: {
      if(ixLine < zLine - zLineVisible){
        ixLineNew = ixLine + zLineVisible;
        redraw();
      } else {
        ixLineNew = zLine -1;
        redraw();
      }
    } break;
    default:
      done = false;
    }//switch
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

  
  
  /**An instance of this class is assigned to any TableItem.
   * It supports the access to the TableItem (it is a table line) via the SWT-independent interface.
   * The instance knows its TableSwt and therefore the supports the access to the whole table.
   *
   */
  protected class TableItemWidget extends SelectMask implements GralTableLine_ifc
  {

    public AtomicInteger redraw = new AtomicInteger();
    
    public String[] cellTexts;
    
    private Object userData;
    
    TableItemWidget(){
      cellTexts = new String[zColumn];
    }
    
    
    @Override
    public Object getWidgetImplementation() {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public boolean setFocus() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public GralColor setBackgroundColor(GralColor color) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public GralColor setForegroundColor(GralColor color) {
      // TODO Auto-generated method stub
      return null;
    }

    @Override
    public void redraw() {
      redraw.addAndGet(1);
      GralTable2.this.redraw(); 
    }

    @Override
    public void setBoundsPixel(int x, int y, int dx, int dy) {
      // TODO Auto-generated method stub
      
    }

    @Override public String getCellText(int column) { return cellTexts[column]; }

    @Override
    public String setCellText(String text, int column) {
      String oldText = cellTexts[column];
      cellTexts[column] = text;
      //redraw();
      return oldText;
    }

    @Override public void setUserData(Object data) {this.userData = data; }

    @Override public Object getUserData() { return userData;  }


    @Override
    public boolean remove()
    {
      // TODO Auto-generated method stub
      return false;
    }
    
  }
  


  
  
}
