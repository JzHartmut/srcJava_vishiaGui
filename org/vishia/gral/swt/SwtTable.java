package org.vishia.gral.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;

public class SwtTable  extends GralTable {

  /**The widget manager is stored in the base class too, but here as SWT-type reference. */
  private final SwtWidgetMng mng;
  
  private Text[][] cellsSwt;
  
  private final SwtTable.Table table; 
  
  
  private final FocusListener focusListenerTable;
  
  private final FocusListener focusListenerCell;
  
  private final TableKeyListerner myKeyListener;
  
  /**The colors. */
  private Color colorBackSelectSwt, colorBackMarkedSwt, colorBackTableSwt
  , colorBackSelectNonFocusedSwt, colorBackMarkedNonFocusedSwt, colorBackTableNonFocusedSwt
  , colorTextSelectSwt, colorTextMarkedSwt, colorTextTableSwt;

  
  
  public SwtTable(SwtWidgetMng mng, String name, Composite parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  { super(name, mng, columnWidths);
    this.mng = mng;
    this.myKeyListener = this.new TableKeyListerner(null);
    focusListenerTable = this.new FocusListenerTable(mng);
    focusListenerCell = this.new FocusListenerCell();
    setColorsSwt();    
    this.cellsSwt = new Text[zLineVisibleMax][zColumn];
    this.table = new SwtTable.Table(parent, zColumn);
    table.addKeyListener(myKeyListener);
    //table.addSelectionListener(selectionListener);
    table.addControlListener(resizeListener);
    table.addFocusListener(focusListenerTable);
    
    table.setFont(mng.propertiesGuiSwt.stdInputFont);
    //table.setColumnSelectionAllowed(true);
    //table.setRowHeight(2 * this.propertiesGui.xPixelUnit());
    //Container widget = new Container();
    int width=0;
    int xPixel = mng.propertiesGui.xPixelUnit();
    for(int ixw=0; ixw<columnWidths.length; ++ixw){
      int columnWidth = columnWidths[ixw];
      width += columnWidth;
      int columnWidthPixel = columnWidth * xPixel;
      //TableColumn tColumn = new TableColumn(table, 0, ixw);
      //tColumn.setWidth(columnWidthPixel);
      //tColumn.setHeaderValue("Test"+ixw);
      //tColumn.setWidth(columnWidthPixel);
    }
    int widthPixel = width * xPixel;
    int heightPixel = height * mng.propertiesGui.yPixelUnit();
    table.setSize(widthPixel, heightPixel);
    resizeTable();
  }


  
  
  public static GralTable addTable(SwtWidgetMng mng, String sName, int height, int[] columnWidths
  //, int selectionColumn, CharSequence selectionText    
  ) {
    
    boolean TEST = false;
    final SwtTable table;
    Composite parent = (Composite)mng.pos.panel.getPanelImpl();
    table = new SwtTable(mng, sName, parent, height, columnWidths); //, selectionColumn, selectionText);
    table.setDataPath(sName);
    table.setPanelMng(mng);
    table.table.setData(table);
    mng.registerWidget(table);
    return table;

  }
  
  
  private void setColorsSwt(){
    colorBackSelectSwt = mng.getColorImpl(colorBackSelect);
    colorBackMarkedSwt = mng.getColorImpl(colorBackMarked);
    colorBackTableSwt = mng.getColorImpl(colorBackTable);
    colorBackSelectNonFocusedSwt = mng.getColorImpl(colorBackSelectNonFocused);
    colorBackMarkedNonFocusedSwt = mng.getColorImpl(colorBackMarkedNonFocused);
    colorBackTableNonFocusedSwt = mng.getColorImpl(colorBackTableNonFocused);
  }
  
  
  void resizeTable()
  {
    mng.setBounds_(table);
    
  }
  


  @Override public Object getWidgetImplementation() {
    return table;
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
  public void setBoundsPixel(int x, int y, int dx, int dy) {
    // TODO Auto-generated method stub
    
  }


  @Override protected void repaintGthread(){
    redrawTableGthread();
    table.superRedraw();
    bRedrawPending = false;

  }

  
  /**Sets the focus to the current cell of the tab
   * @see org.vishia.gral.ifc.GralWidget#setFocus()
   * TODO this method must call in the graphic thread yet, queue it with {@link GralWidgetMng#setInfo(GralWidget, int, int, Object, Object)}.
   */
  @Override public boolean setFocus()
  { if(ixGlineSelectedNew >=0 && ixColumn >=0){
      redrawTableWithFocusedCell(cellsSwt[ixGlineSelectedNew][ixColumn]);
      return true;
    } else {
      if(ixColumn < 0){ ixColumn = 0;}
      if(ixLine < 0 && zLine >0){ ixLineNew = 0;}
      bFocused = true;
      table.redraw();
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
   * @see org.vishia.gral.ifc.GralWidget#removeWidgetImplementation()
   */
  @Override protected void removeWidgetImplementation() {
    // TODO Auto-generated method stub
    if(!table.isDisposed()){
      for(int iRow = 0; iRow < zLineVisibleMax; ++iRow){
        for(int iCol = 0; iCol < zColumn; ++iCol){
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
    tableLines.clear();
    return true;
  }
  
  
  /**Sets the current cell as focused with the focus color. It causes
   * a redraw of the whole table because the cell may be shifted in table position.
   * TODO this method must call in the graphic thread yet, queue it with {@link GralWidgetMng#setInfo(GralWidget, int, int, Object, Object)}.
   * @param cell The cell 
   */
  private void redrawTableWithFocusedCell(Widget cell){
    CellData data = (CellData)cell.getData();
    if(data.tableItem !=null){ //don't do any action if the cell isn't use.
      ixLineNew = data.ixCellLine + ixLine1;
      Assert.check(ixLineNew < zLine);
      ixColumn = data.ixCellColumn;
      bFocused = true;
      table.redraw();
    }
  }


  
  @Override protected int getVisibleLinesTableImpl(){
    Rectangle size = table.getBounds();  
    return size.height / linePixel;
  }  
  
  
  /**This routine implements all things to set the content of any table cell to show it.
   * @see org.vishia.gral.base.GralTable#drawCellContent(int, int, org.vishia.gral.base.GralTable.TableItemWidget)
   */
  @Override protected void drawCellContent(int iCellLine, int iCellCol, TableItemWidget tableItem ){
    Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
    CellData data = (CellData)cellSwt.getData();
    data.tableItem = tableItem;
    //
    String text = tableItem.cellTexts[iCellCol];
    if(text == null){ text = ""; }
    //
    cellSwt.setText(text);
    GralColor colorBack;
    if(ixGlineSelectedNew == iCellLine){
      colorBack = colorBackSelect;
    } else if(tableItem.colorBackground !=null){
      colorBack = tableItem.colorBackground;
    } else {
      colorBack = colorBackTable;
    }
    if(data.colorBack != colorBack){
      Color colorSwt =  mng.getColorImpl(colorBack);
      cellSwt.setBackground(colorSwt);
      data.colorBack = colorBack;
    }
    if(tableItem.colorForground !=null && data.colorText != tableItem.colorForground){
      cellSwt.setForeground(mng.getColorImpl(tableItem.colorForground));
      data.colorText = tableItem.colorForground;
    }
    if(ixGlineSelectedNew == iCellLine && iCellCol == ixColumn){
      SwtWidgetHelper.setFocusOfTabSwt(cellSwt);
      //cellSwt.setFocus();  
    }

    cellSwt.setVisible(true);
    cellSwt.redraw();
  }

  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return table; }
  
  
  
  @Override protected CellData drawCellInvisible(int iCellLine, int iCellCol){
    Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
    cellSwt.setText("");
    cellSwt.setVisible(false);
    return ((CellData)cellSwt.getData());
  }

  /**This routine is called in the graphic thread after a last {@link #redrawGthread()} invocation with delay.
   * It sets the background color to the focused cell and sets the focus for the panel.
   * @deprecated
   */
  GralDispatchCallbackWorker writeContentLast = new GralDispatchCallbackWorker("SwtTable2.writeContentLast"){
    @Override public void doBeforeDispatching(boolean onlyWakeup) {
      ///
      bRedrawPending = true;
      if(ixGlineSelected >=0 && ixGlineSelectedNew != ixGlineSelected){
        //set background color for non-selected line.
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
          Text cellSwt = cellsSwt[ixGlineSelected][iCellCol];
          cellSwt.setBackground(colorBackTableSwt);
        }
      }
      if((ixGlineSelectedNew != ixGlineSelected || bFocused) && ixGlineSelectedNew >= 0 ){
        //set background color for selected line.
        ixGlineSelected = ixGlineSelectedNew; //Note is equal already if bFocused only
        if(ixGlineSelectedNew >=0){ //only if anything is selected:
          for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
            Text cellSwt = cellsSwt[ixGlineSelected][iCellCol];
            cellSwt.setBackground(colorBackSelectSwt);
            if(iCellCol == ixColumn){
              SwtWidgetHelper.setFocusOfTabSwt(cellSwt);
              //cellSwt.setFocus(); 
              System.out.print("\nSwtTable2.writeContentLast: " + SwtTable.this.name);
            }
          }
        }
        bFocused = false;
      }
      bRedrawPending = false;
      countExecution();
      removeFromGraphicThread(itsMng.gralDevice);
    }
  };


  private class Table extends Composite implements GralWidgetGthreadSet_ifc {

    public Table(Composite parent, int zColumns) {
      super(parent, 0);
      int yPix = 0;
      Font font = mng.propertiesGuiSwt.getTextFontSwt(2, whatIs, whatIs);
      for(int iRow = 0; iRow < zLineVisibleMax; ++iRow){
        for(int iCol = 0; iCol < zColumns; ++iCol){
          Text cell = new Text(this, SWT.LEFT | SWT.SINGLE | SWT.READ_ONLY);
          cell.setFont(font);
          cell.addKeyListener(myKeyListener);
          cell.addFocusListener(focusListenerCell);
          CellData cellData = new CellData(iRow, iCol);
          cell.setData(cellData);
          int xdPixCol = columnPixel[iCol+1] - columnPixel[iCol];
          cell.setBounds(columnPixel[iCol], yPix, xdPixCol, linePixel);
          cell.setBackground(colorBackTableSwt);
          cellsSwt[iRow][iCol] = cell;
        }
        yPix += linePixel;
      }
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
    }
    
    
    /**Redraws the whole table because the current line is changed or the focus is changed
     * or the content is changed and #re
     * TODO
     * {@link GralWidgetGthreadSet_ifc#redrawGthread()}
     */
    @Override public void redrawGthread(){ 
      redrawTableGthread(); 
      superRedraw();
      bRedrawPending = false;
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
        insertLine(null, pos, (String[])visibleInfo, data);
      } else if(visibleInfo instanceof String){
        String[] text = ((String)visibleInfo).split("\t");
        //String[] text = new String[1];
        //text[0] = (String)visibleInfo;
        insertLine(null, pos, text, data);
      }
    }


    @Override public void clearGthread() {
      clearTable();  
    }
    

  }
  

  
  /**A Table is completed with a special key listener. On all keys 
   * the {@link GralUserAction} given in the {@link GralWidget#getActionChange()} is called
   * <ul>
   * <li> with given command "table-key".
   * <li>params[0] is the selected line referenced with {@link GralTableLine_ifc}
   * <li>params[1] is the key code described in {@link KeyCode}
   * </ul> 
   * If the method returns false, the central key action given in {@link GralWidgetMng#getRegisteredUserAction(String)}
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
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  

  private class FocusListenerTable implements FocusListener //extends SwtWidgetMng.SwtMngFocusListener
  {
    FocusListenerTable(SwtWidgetMng mng){
      //mng.super();    
    }
    
    @Override public void focusLost(FocusEvent e){ 
      int row = 1; //table.getSelectionIndex();
      if(row >=1){
        //TableItem tableLineSwt = table.getItem(row-1);
        //tableLineSwt.setGrayed(true);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x80ffff)));
      }
    }
    
    @Override public void focusGained(FocusEvent ev)
    { //super.focusGained(ev);
      SwtTable.this.implMethod.focusGained();
      int row = 1; //table.getSelectionIndex();
      if(row >=0){
        //TableItem tableLineSwt = table.getItem(row);
        //tableLineSwt.setGrayed(false);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x00ff00)));
      }
    }
    
  };
  
  
  private class FocusListenerCell implements FocusListener
  {
    
    /**This routine is invoked whenever the focus of any Text field of the table will be lost
     * the focus. Before that occurs, the field is the selected line, because it has had the focus.
     * Therefore the {@link GralTable#colorBackSelectNonFocused} is set.
     * 
     */
    @Override public void focusLost(FocusEvent ev){ 
      if(!bRedrawPending){
        CellData data = (CellData)ev.widget.getData();
        Control widgSwt = (Control)ev.widget;
        //widgSwt.setBackground(colorBackSelectNonFocusedSwt); 
        int iCellLine = data.ixCellLine; //ixLineNew - ixLine1;
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
          Text cellSwt = cellsSwt[iCellLine][iCellCol];
          cellSwt.setBackground(colorBackSelectNonFocusedSwt);
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
      SwtTable.this.implMethod.focusGained();  //from GralWidget.
      if(!bRedrawPending){ 
        redrawTableWithFocusedCell(ev.widget);
      } 
    }
    
  };
  
  
  void stop(){}




}
