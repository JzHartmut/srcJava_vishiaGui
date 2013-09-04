package org.vishia.gral.swt;



import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

public class SwtTable  extends GralTable.GraphicImplAccess  implements GralWidgImpl_ifc 
{

  /**Version and history
   * <ul>
   * <li>2013-06-29 Hartmut chg: refactoring. Now a GralTable<generic> can be created before the graphic is build. It is the new schema of GralWidget.
   *   The inner class {@link GraphicImplAccess} is provided as super class for the graphic implementation class,
   *   for example {@link org.vishia.gral.swt.SwtTable}.
   * <li>2013-10-08 Hartmut chg: 
   * <li>2012-07-15 Hartmut new: search functionality: This implementation have a text field 
   *   which shows the search string. While up and down keys the that lines are selected which text in the {@link #ixColumn()}
   *   starts whith the search string.
   * <li>2012-01-06 Hartmut new: concept of a table which is independent of the table implementation 
   *   of the table implementations in the graphic system layer or in operation system: 
   *   The capability  of a SWT-table is not sufficient, for example the color of the selection bar
   *   is not able to change. Other reason: Implementation of table in SWT, AWT, Swing is different.
   *   It seems better to have one table concept with independent features, which based on simple widgets.
   *   is not  
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

  /**The widget manager is stored in the base class too, but here as SWT-type reference. */
  private final SwtMng mng;
  
  private final Text[][] cellsSwt;
  
  protected Text swtSearchText;
  
  private final SwtTable.Table table; 
  
  /**Set to true if the table has the focus in window.
   */
  private boolean hasFocus;
  
  private final FocusListener focusListenerTable;
  
  private final FocusListener focusListenerCell;
  
  private final MousePressedListenerTable mousePressedListener = new MousePressedListenerTable();
  
  private final MouseWheelListenerTable mouseWheelListener = new MouseWheelListenerTable();
  
  private final TableKeyListerner myKeyListener;
  
  protected long mousetime, redrawtime, mousect, redrawct;
  
  private boolean mouse1isDown, mouse2isDown, mouseDoubleClick;
  
  public SwtTable(GralTable<?> gralTable, SwtMng mng, Composite parent)
  { super(gralTable, mng);
    //super(name, mng, columnWidths);
    this.mng = mng;
    gralTable.implMethodWidget_.setWidgetImpl(this);
    this.myKeyListener = this.new TableKeyListerner(null);
    focusListenerTable = this.new FocusListenerTable(mng);
    focusListenerCell = this.new FocusListenerCell();
    setColorsSwt();    
    this.cellsSwt = new Text[zLineVisibleMax][zColumn()];
    //this.menuColumns = new SwtMenu[zColumn];
    this.table = new SwtTable.Table(parent, zColumn());
    table.addKeyListener(myKeyListener);
    //table.addSelectionListener(selectionListener);
    table.addControlListener(resizeListener);
    table.addFocusListener(focusListenerTable);
    
    table.setFont(mng.propertiesGuiSwt.stdInputFont);
    //table.setColumnSelectionAllowed(true);
    //table.setRowHeight(2 * this.propertiesGui.xPixelUnit());
    //Container widget = new Container();
    //int width=0;
    //int xPixel = mng.propertiesGui.xPixelUnit();
    //int widthPixel = width * xPixel;
    //int heightPixel = height * mng.propertiesGui.yPixelUnit();
    //table.setSize(widthPixel, heightPixel);
    resizeTable();
  }


  
  
  /**
   * @param gralTable
   * @param mng
   * @param sName
   * @param height
   * @param columnWidths
   * @return
   * @deprecated Create an instance of {@link GralTable} and call 
   */
  @Deprecated
  public static GralTable addTable(GralTable gralTable, SwtMng mng, String sName, int height, int[] columnWidths
      //, int selectionColumn, CharSequence selectionText    
      ) {
        
        boolean TEST = false;
        final SwtTable table;
        Composite parent = mng.getCurrentPanel();
        table = new SwtTable(gralTable, mng, parent); //, selectionColumn, selectionText);
        table.outer.setDataPath(sName);
        table.table.setData(table);
        mng.registerWidget(gralTable);
        //NOTE done in SwtTable.resize()     ((SwtMng)mng).setPosAndSize_(table.table);  
        return gralTable;

      }
      
      
  /*package private*/ static void addTable(GralTable gralTable, SwtMng mng) {
        
        boolean TEST = false;
        final SwtTable table;
        Composite parent = mng.getCurrentPanel();
        table = new SwtTable(gralTable, mng, parent); //, selectionColumn, selectionText);
        table.table.setData(table);
        mng.registerWidget(gralTable);
        //NOTE done in SwtTable.resize()     ((SwtMng)mng).setPosAndSize_(table.table);  

      }
      
      
  private void setColorsSwt(){
    //colorBackSelectSwt = mng.getColorImpl(colorBackSelect);
    //colorBackMarkedSwt = mng.getColorImpl(colorBackMarked);
    //colorBackTableSwt = mng.getColorImpl(colorBackTable);
    //colorBackSelectNonFocusedSwt = mng.getColorImpl(colorBackSelectNonFocused);
    //colorBackMarkedNonFocusedSwt = mng.getColorImpl(colorBackMarkedNonFocused);
    //colorBackTableNonFocusedSwt = mng.getColorImpl(colorBackTableNonFocused);
  }
  
  
  void resizeTable()
  {
    mng.setBounds_(table);
    
  }
  

  @Override public Object getWidgetImplementation() {
    return table;
  }

  //@Override
  public GralColor setBackgroundColor(GralColor color) {
    // TODO Auto-generated method stub
    return null;
  }

  //@Override
  public GralColor setForegroundColor(GralColor color) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy) {
    // TODO Auto-generated method stub
    
  }
  
  //@Override 
  public boolean setVisible(boolean visible){
    boolean ret = table.isVisible();
    table.setVisible(visible);
    outer.implMethodWidget_.setVisibleState(visible);
    return ret;
  }
  


  @Override public void repaintGthread(){
    if(!table.isDisposed()){
      setAllCellContentGthread();
      Color colorSelectBack =  mng.getColorImpl(super.colorSelectCharsBack());
      Color colorSelect =  mng.getColorImpl(super.colorSelectChars());
      if(super.searchChars().length() >0){
        swtSearchText.setBackground(colorSelectBack);
        swtSearchText.setForeground(colorSelect);
        //swtSearchText.
        swtSearchText.setText(searchChars().toString());
        Point tabSize = table.getSize();
        swtSearchText.setBounds(xpixelCell[ixColumn()] + 10, tabSize.y - linePixel, xpixelCell[ixColumn()+1] - xpixelCell[ixColumn()] - 10, linePixel);
        swtSearchText.setVisible(true);
        
      } else {
        swtSearchText.setVisible(false);
      }
      //System.out.println("swtTable redrawed");
      table.superRedraw();  //this is the core-redraw
      redrawtime = System.currentTimeMillis();
      //System.out.println("test SwtTable redraw " + ++redrawct);
    } else {
      System.out.println("test SwtTable redraw disposed" + ++redrawct);
    }
    bRedrawPending = false;

  }

  
  /**Sets the focus to the current cell of the tab
   * @see org.vishia.gral.base.GralWidget#setFocusGThread()
   * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
   */
  @Override public boolean setFocusGThread()
  { if(ixGlineSelectedNew >=0 && ixColumn() >=0){
      //System.out.println("test SwtTable.setFocus-1");
      redrawTableWithFocusedCell(cellsSwt[ixGlineSelectedNew][ixColumn()]);
      return true;
    } else {
      //System.out.println("test SwtTable.setFocus-2");
      correctIxLineColumn();
      bFocused = true;
      table.redraw();
      table.setFocus();
      return true;
    }
    /*
    if(ixLineNew >=0){ ///
      return SwtWidgetHelper.setFocusOfTabSwt(cellsSwt[ixLineNew][0]);
    } else {
      return SwtWidgetHelper.setFocusOfTabSwt(table);
    }
    */
  }



  /**Removes the graphical widgets.
   * @see org.vishia.gral.base.GralWidget#removeWidgetImplementation()
   */
  @Override public void removeWidgetImplementation() {
    // TODO Auto-generated method stub
    if(!table.isDisposed()){
      for(int iRow = 0; iRow < zLineVisibleMax; ++iRow){
        for(int iCol = 0; iCol < zColumn(); ++iCol){
          Text cell = cellsSwt[iRow][iCol];
          cell.dispose();
          cellsSwt[iRow][iCol] = null;  
        }
      }
      table.dispose();  //it may be sufficient to dispose table only becaust it is the container....
    }
  }

  @Override public boolean remove(){
    super.remove(); //removes the widget implementation.
    return true;
  }
  
  
  /**Sets the current cell as focused with the focus color. It causes
   * a redraw of the whole table because the cell may be shifted in table position.
   * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
   * @param cell The cell 
   */
  private void redrawTableWithFocusedCell(Widget cell){
    CellData data = (CellData)cell.getData();
    if(super.redrawTableWithFocusedCell(data)){
      table.redraw();
    }
  }


  
  @Override protected int getVisibleLinesTableImpl(){
    Rectangle size = table.getBounds();  
    return size.height / linePixel;
  }  
  
  
  /**This routine implements all things to set the content of any table cell to show it.
   * @see org.vishia.gral.base.GralTable#drawCellContent(int, int, org.vishia.gral.base.GralTable.TableLineData)
   */
  @Override protected void drawCellContent(int iCellLine, int iCellCol, GralTable.TableLineData tableItem ){
    Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
    CellData cellData = (CellData)cellSwt.getData();
    cellData.tableItem = tableItem;
    //
    String text = tableItem.cellTexts[iCellCol];
    if(text == null){ text = ""; }
    //
    cellSwt.setText(text);
    GralColor colorBack;
    if(isCurrentLine(tableItem.nLineNr)) { //the current line, but only if ixLineNew <0
    //if(ixGlineSelectedNew() == iCellLine){
      colorBack = (tableItem.getMark() & 1)!=0 ? colorBackSelectMarked() : colorBackSelect();
    } else if(tableItem.colorBackground !=null){
      colorBack = tableItem.colorBackground;
    } else {
      colorBack = colorBackTable();
    }
    if(cellData.colorBack != colorBack){
      Color colorSwt =  mng.getColorImpl(colorBack);
      cellSwt.setBackground(colorSwt);
      cellData.colorBack = colorBack;
    }
    GralColor colorText = tableItem.colorForground !=null ? tableItem.colorForground : colorTextTable();
    if(colorText != cellData.colorText){
      cellSwt.setForeground(mng.getColorImpl(colorText));
      cellData.colorText = colorText;
    }
    if(ixGlineSelectedNew == iCellLine && iCellCol == ixColumn() && bFocused){
      SwtWidgetHelper.setFocusOfTabSwt(cellSwt);
    }

    cellSwt.setVisible(true);
    cellSwt.redraw();
  }

  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return table; }
  
  
  
  @Override protected CellData drawCellInvisible(int iCellLine, int iCellCol){
    Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
    CellData cellData = (CellData)cellSwt.getData();
    cellData.tableItem = null;
    cellSwt.setText("");
    if(cellData.colorBack != colorBackTable()){
      Color colorSwt =  mng.getColorImpl(colorBackTable());
      cellSwt.setBackground(colorSwt);
      cellData.colorBack = colorBackTable();
    }
    cellSwt.setVisible(true);
    return (cellData);
  }

  
  @Override protected GralMenu createColumnMenu(int column){
    GralMenu menuColumn = new SwtMenu(outer, table, itsMng());
    for(int iRow = 0; iRow < zLineVisibleMax; ++iRow){
      cellsSwt[iRow][column].setMenu((Menu)menuColumn.getMenuImpl());
    }    
    return menuColumn;
  }
  
  
  /**Called internal from mouse event only.
   * @param ev
   */
  protected void mouseDown(MouseEvent ev){
    Text widgSwt = (Text)ev.widget;  //it is only associated to a cell.
    redrawTableWithFocusedCell(widgSwt);
    //cellDataOnMousePressed = (CellData)widgSwt.getData();
    //TableLineData line = cellDataOnMousePressed.tableItem;
    //ixLineNew = line.nLineNr;  //select this line.
    
    if(true || !hasFocus){
      outer.implMethodWidget_.focusGained();  //from GralWidget.
      hasFocus = true;
      //System.out.println("focusTable");
    }
    //redrawTableWithFocusedCell(ev.widget);
    mousetime = System.currentTimeMillis();
    
    if((ev.button & SWT.BUTTON1)!=0){ mouse1isDown = true; }
    else if((ev.button & SWT.BUTTON2)!=0){ 
      mouse2isDown = true; 
    }
    //System.out.println("SwtTable-mouse dn end");
    
    
  }
  

  protected void setBoundsCells(){
    Rectangle parentBounds = table.getParent().getBounds();
    GralRectangle pixTable = outer.pos().calcWidgetPosAndSize(itsMng().propertiesGui(), parentBounds.width, parentBounds.height, 0, 0);
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
    int yPix = 0;
    
    for(Text[] row: cellsSwt){
      int ixColumn = 0;
      for(Text cell: row){
        cell.setBounds(xpixelCell[ixColumn], yPix, xpixelCell[ixColumn+1] - xpixelCell[ixColumn], linePixel);
        ixColumn +=1;
      }
      yPix += linePixel;
    }
      
  }
  
  
  private class Table extends Composite implements GralWidgetGthreadSet_ifc {

    public Table(Composite parent, int zColumns) {
      super(parent, 0);
      int yPix = 0;
      Font font = mng.propertiesGuiSwt.getTextFontSwt(2, outer.whatIs, outer.whatIs);
      Color colorBackTableSwt = mng.getColorImpl(colorBackTable());
      for(int iCol = 0; iCol < zColumns; ++iCol){
        //menuColumns[iCol] = new SwtMenu(name + "_menu" + iCol, this, itsMng());
      }
      //NOTE: only if the swtSelectText is created first, it will drawn in in the foreground of the other cells.
      swtSearchText = new Text(this, SWT.LEFT | SWT.SINGLE | SWT.READ_ONLY);
      swtSearchText.setFont(font);
      swtSearchText.setVisible(false);
      for(int iRow = 0; iRow < zLineVisibleMax; ++iRow){
        for(int iCol = 0; iCol < zColumns; ++iCol){
          Text cell = new Text(this, SWT.LEFT | SWT.SINGLE | SWT.READ_ONLY);
          cell.setFont(font);
          cell.addKeyListener(myKeyListener);
          cell.addFocusListener(focusListenerCell);
          cell.addMouseListener(mousePressedListener);
          cell.addMouseWheelListener(mouseWheelListener);
          CellData cellData = new CellData(iRow, iCol);
          cell.setData(cellData);
          int xdPixCol = SwtTable.super.columnPixel[iCol+1] - columnPixel[iCol];
          cell.setBounds(columnPixel[iCol], yPix, xdPixCol, linePixel);
          cell.setBackground(colorBackTableSwt);
          //cell.setMenu((Menu)menuColumns[iCol].getMenuImpl());
          cellsSwt[iRow][iCol] = cell;
        }
        yPix += linePixel;
      }
      //The text field for selection string

      
    }

    
    //@Override public void drawBackground (GC gc, int x, int y, int width, int height, int offsetX, int offsetY) {
    //  redrawGthread();
    //}
    
    
    /**Prepares the cells, then redraw. It overrides the super method,
     * and calls super.redraw() internally. This method is only called
     * from the graphic system itself in the graphic thread.
     * It can't be called from the user (in any other thread)
     * because this class and the built composition with it is private.
     * @see org.eclipse.swt.widgets.Control#redraw()
     */
    @Override public void redraw(){
      redrawGthread();
    }
    
    
    /**Does call the super redraw method called inside {@link #redrawGthread()} from the overridden
     * {@link #redraw()} method. */
    private void superRedraw(){
      super.update(); 
      super.redraw();
      //if(searchChars.length() >0){
        //swtSelectText.update();
        //swtSelectText.redraw();
      //}
    }
    
    
    /**Redraws the whole table because the current line is changed or the focus is changed
     * or the content is changed and #re
     * TODO
     * {@link GralWidgetGthreadSet_ifc#redrawGthread()}
     */
    @Override public void redrawGthread(){ 
      SwtTable.this.repaintGthread();
    }
    
    
    @Override
    public void setBackGroundColorGthread(GralColor color) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void setForeGroundColorGthread(GralColor color) {
      // TODO Auto-generated method stub
      
    }


    @Override
    public void setTextGthread(String text, Object data) {
      // TODO Auto-generated method stub
      
    }


    @Override public void insertGthread(int pos, Object visibleInfo, Object data) {
      if(visibleInfo instanceof String[]){
        //outer.insertLine(null, pos, (String[])visibleInfo, data);
      } else if(visibleInfo instanceof String){
        String[] text = ((String)visibleInfo).split("\t");
        //String[] text = new String[1];
        //text[0] = (String)visibleInfo;
        //outer.insertLine(null, pos, text, data);
      }
    }


    @Override public void clearGthread() {
      outer.clearTable();  
    }
    

  }
  

  
  /**A Table is completed with a special key listener. On all keys 
   * the {@link GralUserAction} given in the {@link GralWidget#getActionChange()} is called
   * <ul>
   * <li> with given command "table-key".
   * <li>params[0] is the selected line referenced with {@link GralTableLine_ifc}
   * <li>params[1] is the key code described in {@link KeyCode}
   * </ul> 
   * If the method returns false, the central key action given in {@link GralMng#getRegisteredUserAction(String)}
   * for "keyAction" is tried to get and then invoked with cmd = "key" and the key code in params[0].
   * This central keyAction may be used for application centralized keys without association to the table itself.
   */
  class TableKeyListerner implements KeyListener
  {
    final KeyListener basicListener;

    
    
    public TableKeyListerner(KeyListener basicListener)
    {
      this.basicListener = basicListener;
    }

    @Override
    public void keyPressed(KeyEvent keyEv)
    {
      try{
        if((keyEv.keyCode & 0xffff) !=0){
          final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
          processKeys(keyCode);
        }
      } catch(Exception exc){
        mng.log.sendMsg(0, "Exception in SwtTable-KeyEvent; %s", exc.getLocalizedMessage());
        exc.printStackTrace(System.out);
      }
    }

    @Override
    public void keyReleased(KeyEvent arg0)
    {
      //basicListener.keyReleased(arg0);
      
    }
    
  };
  
  ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
      stop();
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      stop();
      Rectangle boundsTable = ((Control)e.widget).getBounds();
      int borderTable = ((Control)e.widget).getBorderWidth();
      //it calls repaint with delay.
      SwtTable.this.resizeTable(boundsTable.width, boundsTable.height);
      SwtTable.this.setBoundsCells();
      
    }
    
  };
  
  
  private final class MousePressedListenerTable implements MouseListener{

    protected MousePressedListenerTable(){}
    
    
    @Override
    public void mouseDoubleClick(MouseEvent e)
    {
      //System.out.println("SwtTable-mouse-double");
      mousetime = System.currentTimeMillis();
      mouseDoubleClick = true;
      processKeys(KeyCode.mouse1Double);
    }

    @Override
    public void mouseDown(MouseEvent ev)
    {
      SwtTable.this.mouseDown(ev);
      //System.out.println("SwtTable-mouse dn start" + ++mousect);
    }

    @Override
    public void mouseUp(MouseEvent ev)
    {
      long time = System.currentTimeMillis();
      if(mouseDoubleClick)  //mouse up event after double click.
      { mouseDoubleClick = false; 
        //System.out.println("mouse double-up");
      
        if((time -mousetime)<3000){
          return; 
        }
      }
      //System.out.println("mouse up");
      if((ev.button & SWT.BUTTON1 | SWT.BUTTON2)!=0){ 
        //while mouseUp 1 button is down:
        //processKeys(KeyCode.mouse1Double);
      }
      mousetime = time;
      //System.out.println("SwtTable-mouse up");
    }
    
  }
  
 
  class MouseWheelListenerTable implements MouseWheelListener{

    @Override
    public void mouseScrolled(MouseEvent e) {
      //System.out.println("SwtTable mouseWheel " + e.count);
      if(e.count >0){
        processKeys(KeyCode.mouseWheelUp);
      } else {
        processKeys(KeyCode.mouseWheelDn);
      }
    }
    
  }
  

  /**Debug: this focus listener is not invoked any time. It is associated to a swt.Composite.
   *
   */
  private class FocusListenerTable implements FocusListener //extends SwtWidgetMng.SwtMngFocusListener
  {
    FocusListenerTable(SwtMng mng){
      //mng.super();    
    }
    
    @Override public void focusLost(FocusEvent e){ 
      //System.out.println("table focus lost. ");
      //assert(false);
      hasFocus = false;
      int row = 1; //table.getSelectionIndex();
      if(row >=1){
        //TableItem tableLineSwt = table.getItem(row-1);
        //tableLineSwt.setGrayed(true);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x80ffff)));
      }
    }
    
    @Override public void focusGained(FocusEvent ev)
    { //super.focusGained(ev);
      outer.implMethodWidget_.focusGained();
      //System.out.println("table focus gained. ");
      //assert(false);
      int row = 1; //table.getSelectionIndex();
      if(row >=0){
        //TableItem tableLineSwt = table.getItem(row);
        //tableLineSwt.setGrayed(false);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x00ff00)));
      }
    }
    
  };
  
  
  /**An instance of this is associated to any cell of the table. 
   * The focus gained method is used to set the current cell of the table and to invoke redraw
   * to show the new selection. That focus event is invoked if the mouse button is pressed on that cell.
   */
  private class FocusListenerCell implements FocusListener
  {
    
    /**This method does nothing. A lost focus color showing may be forced from redraw of another 
     * table. Think about: maybe any action?
     * Old: This routine is invoked whenever the focus of any Text field of the table will be lost
     * the focus. Before that occurs, the field is the selected line, because it has had the focus.
     * Therefore the {@link GralTable#colorBackSelectNonFocused} is set.
     * @deprecated unnecessary yet.
     */
    @Deprecated
    @Override public void focusLost(FocusEvent ev){ 
      //System.out.println("Cell focus lost");
      if(!bRedrawPending){
        CellData data = (CellData)ev.widget.getData();
        Control widgSwt = (Control)ev.widget;
        //widgSwt.setBackground(colorBackSelectNonFocusedSwt); 
        int iCellLine = data.ixCellLine; //ixLineNew - ixLine1;
        for(int iCellCol = 0; iCellCol < zColumn(); ++iCellCol){
          Text cellSwt = cellsSwt[iCellLine][iCellCol];
    
          //cellSwt.setBackground(colorBackSelectNonFocusedSwt);
        }
      }
    }
    
    /**This routine is invoked especially if the mouse is landing 
     * on a Text-field with click. Then this table line and column
     * should be selected as currently. <br>
     * This routine is invoked on setFocus()-call too. In this case
     * it should not done anything. The variable #bRedrawPending guards it.
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override public void focusGained(FocusEvent ev) { 
      try{
        if(!bRedrawPending){ 
          //The focusGained for the table invokes the GralWidget.focusAction for this table.
          if(true || !hasFocus){
            outer.implMethodWidget_.focusGained();  //from GralWidget.
            hasFocus = true;
            //System.out.println("focusTable");
          }
          redrawTableWithFocusedCell(ev.widget);
          //System.out.println("focusCell");
        }
      } catch(Exception exc){
        itsMng().log().sendMsg(0, "Exception in SwtTable.focusGained");
      }
    }
    
  };
  
  
  
  /**This class wraps the {@link GralUserAction} for a menu action for the table.
   */
  static class ActionUserMenuItem implements SelectionListener
  { 
    final GralUserAction action;
    
    public ActionUserMenuItem(GralUserAction action)
    { this.action = action;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    public void widgetSelected(SelectionEvent e)
    { Object oWidgSwt = e.getSource();
      final GralWidget widgg;
      if(oWidgSwt instanceof Widget){
        Widget widgSwt = (Widget)oWidgSwt;
        Object oGralWidg = widgSwt.getData();
        if(oGralWidg instanceof GralWidget){
          widgg = (GralWidget)oGralWidg;
        } else { widgg = null; }
      } else { widgg = null; }
      action.userActionGui(KeyCode.menuEntered, widgg);
    }
  }
  

  

  
  
  void stop(){}




}
