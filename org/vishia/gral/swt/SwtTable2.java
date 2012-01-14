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
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTable2;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainCmd.MainCmd;
import org.vishia.util.KeyCode;

public class SwtTable2  extends GralTable2 {

  /**The widget manager is stored in the base class too, but here as SWT-type reference. */
  private final SwtWidgetMng mng;
  
  private Text[][] cellsSwt;
  
  private final SwtTable2.Table table; 
  
  /**Set to true while {@link #table}.{@link Table#redrawGthread()} is running.
   * It prevents recursive invocation of redraw() while setFocus() is invoked. */
  boolean bRedrawPending;

  /**Set true if the focus is gained by mouse click. It causes color set and 
   * invocation of {@link #selectLine(GralTableLine_ifc)}. 
   */
  boolean bFocused;
  
  
  private final FocusListener focusListenerTable;
  
  private final FocusListener focusListenerCell;
  
  private final TableKeyListerner myKeyListener;
  
  /**The colors. */
  private Color colorBackSelectSwt, colorBackMarkedSwt, colorBackTableSwt
  , colorBackSelectNonFocusedSwt, colorBackMarkedNonFocusedSwt, colorBackTableNonFocusedSwt
  , colorTextSelectSwt, colorTextMarkedSwt, colorTextTableSwt;

  
  
  public SwtTable2(SwtWidgetMng mng, String name, Composite parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  { super(name, mng, columnWidths);
    this.mng = mng;
    this.myKeyListener = this.new TableKeyListerner(null);
    focusListenerTable = this.new FocusListenerTable(mng);
    focusListenerCell = this.new FocusListenerCell(mng);
    setColorsSwt();    
    this.cellsSwt = new Text[zLineVisibleMax][zColumn];
    this.table = new SwtTable2.Table(parent, zColumn);
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
    final SwtTable2 table;
    Composite parent = (Composite)mng.pos.panel.getPanelImpl();
    table = new SwtTable2(mng, sName, parent, height, columnWidths); //, selectionColumn, selectionText);
    table.setDataPath(sName);
    table.setPanelMng(mng);
    table.table.setData(table);
    mng.registerWidget(table);
    return table;

  }
  
  
  protected void setColorsSwt(){
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
    table.redrawGthread();
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
  
  
  private class Table extends Composite implements SwtWidgetSet_ifc {

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
    
    /**Redraws the whole table because the current line is changed or the focus is changed.
     * TODO
     * {@link SwtWidgetSet_ifc#redrawGthread()}
     */
    @Override public void redrawGthread(){
      long dbgtime = System.currentTimeMillis();
      bRedrawPending = true;
      int iCellLine;
      if(ixLineNew != ixLine && ixGlineSelected >=0){
        iCellLine = ixLineNew - ixLine1;  //The line which is currently present and selected, before changing ixLine1:
        if(iCellLine != ixGlineSelected){ //another line to select:
          for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
            cellsSwt[ixGlineSelected][iCellCol].setBackground(colorBackTableSwt);
          }
          ixGlineSelected = -1; //because selection isn't shown.
        }
      }
      //calculate number of lines to show:
      Rectangle size = table.getBounds();  
      zLineVisible = size.height / linePixel;
      if(zLineVisible > zLineVisibleMax){ 
        zLineVisible = zLineVisibleMax;   //no more lines existing.
      }
      ixLine2 = ixLine1 + zLineVisible -1;
      if(ixLineNew < 2){
        ixLine1 = 0; ixLine2 = zLineVisible -1;
      } else if(ixLineNew > ixLine2 -2){
        int dLine = ixLineNew - (ixLine2 -2);
        ixLine1 += dLine; ixLine2 += dLine;
      } else if (ixLineNew < ixLine1 +2){
        int dLine = ixLine1 +2 - ixLineNew;
        ixLine1 -= dLine; ixLine2 -= dLine;
      }
      if(ixLine2 >= zLine ){
        ixLine2 = zLine-1;
      }
      MainCmd.assertion(ixLine2 < zLine && ixLineNew < zLine);
      long dbgtime1 = System.currentTimeMillis() - dbgtime;
      iCellLine = 0;
      for(int ixLine3 = ixLine1; ixLine3 <= ixLine2 && iCellLine < zLineVisibleMax; ++ixLine3){
        TableItemWidget line = tableLines.get(ixLine3);
        int ctredraw = line.redraw.get();
        if(ctredraw > 0 || true){
          for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
            String text = line.cellTexts[iCellCol];
            if(text == null){ text = ""; }
            Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
            cellSwt.setText(text);
            ((CellData)cellSwt.getData()).tableItem = line;
            cellSwt.setVisible(true);
          }
          iCellLine +=1;
          //Thread safety: set to 0 only if it isn't changed between quest and here.
          //Only then the text isn't changed.
          line.redraw.compareAndSet(ctredraw, 0);  
        }
      }
      long dbgtime2 = System.currentTimeMillis() - dbgtime;
      while( iCellLine < zLineVisibleMax){
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
          Text cellSwt = cellsSwt[iCellLine][iCellCol]; 
          cellSwt.setText("");
          ((CellData)cellSwt.getData()).tableItem = null;
          cellSwt.setVisible(false);
        }
        iCellLine +=1;
      }
      long dbgtime3 = System.currentTimeMillis() - dbgtime;
      
      //mark current line
      if(ixLineNew >=0 && (ixLineNew != ixLine || bFocused)){
        selectLine(tableLines.get(ixLineNew));
        ixLine = ixLineNew;
      //}
      //if(true || ixLineNew != ixLine){
        ixLine = ixLineNew;
        ixGlineSelectedNew = iCellLine = ixLine - ixLine1;
        MainCmd.assertion(ixGlineSelectedNew < zLineVisible);
        for(int iCellCol = 0; iCellCol < zColumn; ++iCellCol){
          Text cellSwt = cellsSwt[iCellLine][iCellCol];
          //Note: The background color isn't set yet because this routine may be called
          //in a fast key repetition (50 ms). In the next few ms the next cell may have
          //the focus then. The setBackground needs about 5 ms per cell on Linux GTK
          //with an Intel-Atom-processor. It is too much.
          //The focus is able to see because the cursor is there.
          //The color will be set in the writeContentLast.
          //
          //don't invoke: cellSwt.setBackground(colorBackSelectSwt);
          if(iCellCol == ixColumn){
            cellSwt.setFocus();  
          }
        }
      }
      writeContentLast.addToGraphicThread(itsMng.gralDevice, 200);
      long dbgtime4 = System.currentTimeMillis() - dbgtime;
      System.out.print("\nSwtTable2-redraw1: " + dbgtime1 + " + " + dbgtime2 + " + " + dbgtime3 + " + " + dbgtime4);
      dbgtime = System.currentTimeMillis();
      super.update();
      super.redraw();
      bRedrawPending = false;
      dbgtime1 = System.currentTimeMillis() - dbgtime;
      System.out.print(", redraw2: " + dbgtime1);
      
    }
    
    
    /**This routine is called if some time is delayed after a last {@link #redrawGthread()}
     * invocation. It sets the background color to the focused cell and sets the focus
     * for the panel.
     * 
     */
    GralDispatchCallbackWorker writeContentLast = new GralDispatchCallbackWorker(){
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
                System.out.print("\nSwtTable2.writeContentLast: " + SwtTable2.this.name);
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
  

  
  /**Data for each Text widget.
   */
  private class CellData{
    final int ixCellLine, ixCellColumn;
    TableItemWidget tableItem;
    
    CellData(int ixCellLine, int ixCellColumn){
      this.ixCellLine = ixCellLine; 
      this.ixCellColumn = ixCellColumn;
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
        final GralWidget widgetDescr;
        final GralUserAction action;
        //System.out.println("" + keyEv.character + Integer.toHexString(keyEv.keyCode));
        
        final Object source = keyEv.getSource();
        final Control swtControl;
        if(source instanceof Control){
          swtControl = ((Control)source);
          Object oData = swtControl.getData();
          if(oData instanceof GralWidget){
            widgetDescr = (GralWidget)oData;
            action = widgetDescr.getActionChange();
          } else { widgetDescr = null; action = null; }
        } else { 
          widgetDescr = null; action = null;
          swtControl = null;
        }
        boolean actionDone = false;
        if((keyEv.keyCode & 0xffff) !=0){
          final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
          actionDone = processKeys(keyCode);
          int ixRow = -99999;
          Table table1 = (Table)source;
          ixRow  = 1; //table1.getSelectionIndex();   //the currently selected line.
          if(ixRow >=0){  //< 0 if nothing is selected.
            /*
            TableItem tableLineSwt = table1.getItem(ixRow);   // the SWT TableItem which presents the line.
            //The SWT-TableItem contains data, which implements the gral TableLineGui_ifc to get data from the line.
            //firstly: Build the instance and associate to the TableItem.
            //later: Re-Use the instance.
            GralTableLine_ifc lineGral = (GralTableLine_ifc)tableLineSwt.getData();
            if(lineGral == null){
              lineGral = new TableItemWidget(tableLineSwt, null);
              tableLineSwt.setData(lineGral);  //Set the data for usage later.
            }
            if(!procStandardKeys(keyCode, lineGral, ixRow)){
              if(action !=null){ 
                actionDone = action.userActionGui(keyCode, widgetDescr, lineGral);
              }
            }
            */
          } //if(table.)
          if(action !=null && !actionDone){
            GralUserAction mainKeyAction = mng.getRegisteredUserAction("KeyAction");
            if(mainKeyAction !=null){
              int gralKey = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
              //old form called because compatibility, if new for with int-parameter returns false.
              if(!mainKeyAction.userActionGui(gralKey, widgetDescr)){
                mainKeyAction.userActionGui("key", widgetDescr, new Integer(gralKey));
              }
            }
          }
        }
        if(basicListener !=null){
          basicListener.keyPressed(keyEv);
        }
        if(swtControl !=null){
          Control parent = swtControl.getParent();
          if(parent !=null){
            //KeyListener parentListener = parent.getListener(SWT.KEY_MASK);
            //parent.
          }
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
  

  private class FocusListenerTable extends SwtWidgetMng.SwtMngFocusListener
  {
    FocusListenerTable(SwtWidgetMng mng){
      mng.super();    
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
    { super.focusGained(ev);
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
    FocusListenerCell(SwtWidgetMng mng){
      //mng.super();    
    }
    
    /**This routine is invoked whenever the focus of any Text field of the table will be lost
     * the focus. Before that occurs, the field is the selected line, because it has had the focus.
     * Therefore the {@link GralTable2#colorBackSelectNonFocused} is set.
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
      if(!bRedrawPending){ 
        redrawTableWithFocusedCell(ev.widget);
      } 
    }
    
  };
  
  
  /**Sets the current cell as focused with the focus color. It causes
   * a redraw of the whole table because the cell may be shifted in table position.
   * TODO this method must call in the graphic thread yet, queue it with {@link GralWidgetMng#setInfo(GralWidget, int, int, Object, Object)}.
   * @param cell The cell 
   */
  private void redrawTableWithFocusedCell(Widget cell){
    CellData data = (CellData)cell.getData();
    if(data.tableItem !=null){ //don't do any action if the cell isn't use.
      ixLineNew = data.ixCellLine + ixLine1;
      MainCmd.assertion(ixLineNew < zLine);
      ixColumn = data.ixCellColumn;
      bFocused = true;
      table.redraw();
    }
  }
  
  
  
  void stop(){}



}
