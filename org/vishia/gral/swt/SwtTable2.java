package org.vishia.gral.swt;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTable2;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;

public class SwtTable2  extends GralTable2{

  /**The widget manager is stored in the base class too, but here as SWT-type reference. */
  private final SwtWidgetMng mng;
  
  private Text[][] cellsSwt;
  
  private final SwtTable2.Table table; 

  final FocusListener focusListenerTable;
  
  final TableKeyListerner myKeyListener;
  
  /**The colors. */
  private Color colorBackSelectSwt, colorBackMarkedSwt, colorBackTableSwt
  , colorBackSelectNonFocusedSwt, colorBackMarkedNonFocusedSwt, colorBackTableNonFocusedSwt
  , colorTextSelectSwt, colorTextMarkedSwt, colorTextTableSwt;

  
  
  public SwtTable2(SwtWidgetMng mng, String name, Composite parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  { super(name, mng, columnWidths);
    this.mng = mng;
    this.myKeyListener = this.new TableKeyListerner(null);
    this.cellsSwt = new Text[zLineVisible][zCol];
    this.table = new SwtTable2.Table(parent, zCol);
    table.addKeyListener(myKeyListener);
    //table.addSelectionListener(selectionListener);
    table.addControlListener(resizeListener);
    focusListenerTable = this.new FocusListenerTable(mng);
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
    setColorsSwt();    
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


  @Override public void redraw() {
    itsMng.setInfo(this, itsMng.cmdRedraw, 0, null, null);
  }

  
  @Override public boolean setFocus()
  { if(ixLine >=0){
      return SwtWidgetHelper.setFocusOfTabSwt(cellsSwt[ixLine][0]);
    } else {
      return SwtWidgetHelper.setFocusOfTabSwt(table);
    }
  }



  @Override
  protected void removeWidgetImplementation() {
    // TODO Auto-generated method stub
    
  }

  
  
  
  class Table extends Composite implements SwtWidgetSet_ifc {

    public Table(Composite parent, int zColumns) {
      super(parent, 0);
      int yPix = 0;
      for(int iRow = 0; iRow < zLineVisible; ++iRow){
        for(int iCol = 0; iCol < zColumns; ++iCol){
          Text cell = new Text(this, SWT.LEFT | SWT.SINGLE | SWT.READ_ONLY);
          cell.addKeyListener(myKeyListener);
          
          int xdPixCol = columnPixel[iCol+1] - columnPixel[iCol];
          cell.setBounds(columnPixel[iCol], yPix, xdPixCol, linePixel);
          cellsSwt[iRow][iCol] = cell;
        }
        yPix += linePixel;
      }
    }

    
    //@Override public void drawBackground (GC gc, int x, int y, int width, int height, int offsetX, int offsetY) {
    //  redrawGthread();
    //}
    
    
    @Override public void redraw(){
      redrawGthread();
    }
    
    @Override public void redrawGthread(){
      int iCellLine = 0;
      Rectangle size = table.getBounds();
      int zLinesVisible = size.height / linePixel;
      ixLine2 = ixLine1 + zLinesVisible -1;
      if(ixLine2 >= tableLines.size() ){
        ixLine2 = tableLines.size()-1;
      }
      for(int ixLine3 = ixLine1; ixLine3 <= ixLine2 && iCellLine < zLine; ++ixLine3){
        TableItemWidget line = tableLines.get(ixLine3);
        int ctredraw = line.redraw.get();
        if(ctredraw > 0 || true){
          for(int iCellCol = 0; iCellCol < zCol; ++iCellCol){
            String text = line.cellTexts[iCellCol];
            if(text == null){ text = ""; }
            cellsSwt[iCellLine][iCellCol].setText(text);
          }
          iCellLine +=1;
          //Thread safety: set to 0 only if it isn't changed between quest and here.
          //Only then the text isn't changed.
          line.redraw.compareAndSet(ctredraw, 0);  
        }
      }
      while( iCellLine < zLineVisible){
        for(int iCellCol = 0; iCellCol < zCol; ++iCellCol){
          cellsSwt[iCellLine][iCellCol].setText("");
        }
        iCellLine +=1;
      }
      //mark current line
      if(ixLineNew != ixLine){
        for(int iCellCol = 0; iCellCol < zCol; ++iCellCol){
          cellsSwt[ixLine][iCellCol].setBackground(colorBackTableSwt);
        }
        ixLine = ixLineNew;
        for(int iCellCol = 0; iCellCol < zCol; ++iCellCol){
          cellsSwt[ixLine][iCellCol].setBackground(colorBackSelectSwt);
        }
      
      }
      super.update();
      super.redraw();
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
  

  class FocusListenerTable extends SwtWidgetMng.SwtMngFocusListener
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
  
  
  void stop(){}



}
