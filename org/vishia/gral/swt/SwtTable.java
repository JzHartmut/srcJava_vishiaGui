package org.vishia.gral.swt;


import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.util.KeyCode;
import org.vishia.util.SelectMask;

public class SwtTable extends GralTable
{

  
  /**Version and History:
   * <ul>
   * <li>2011-11-27 Hartmut new {@link #selectionListener}: calls {@link GralTable#selectLine(GralTableLine_ifc)}.
   * <li>2011-10-02: Hartmut chg: The key handling is improved. The old idea - a String for any key - is now obsolete.
   *   The keycodes are contained in {@link KeyCode}, that are used. The conversion routine for SWT-keys is {@link SwtGralKey}.
   * <li>older: TODO
   * </ul>
   */
  @SuppressWarnings("hiding")
  public static final int version = 0x20111001;


  
  private Table table;
  
  /**The widget manager is stored in the base class too, but here as SWT-type reference. */
  private final SwtWidgetMng mng;
  
  /**Width of each column in GralUnits. */
  private int[] columnWidths;
  
  FocusListener focusListenerTable;
  

  
  //private final int selectionColumn;
  //private final CharSequence selectionText;
  
  public SwtTable(SwtWidgetMng mng, String name, Composite parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  { super(name, mng);
    this.mng = mng;
    this.columnWidths = columnWidths;
    //this.selectionColumn = selectionColumn;
    //this.selectionText = selectionText;
    this.table = new Table(parent, SWT.FULL_SELECTION) ; //, SWT.FULL_SELECTION); //| SWT.CHECK);
    //Note: the SWT.CHECK produces a checkbox at left side of any table line. It is not usual.
    //A line would be checked by pressing space-key. But it is pressed twice, an exception is thrown (?)
    //Don't use it. It seems to be not good.
    boolean TEST = false;
    Listener[] allListeners = table.getListeners(SWT.KeyDown);
    for(Listener listener: allListeners){
      table.removeListener(SWT.Selection, listener);
    }
    table.addKeyListener(new TableKeyListerner(null));
    table.addSelectionListener(selectionListener);
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
      TableColumn tColumn = new TableColumn(table, 0, ixw);
      tColumn.setWidth(columnWidthPixel);
      //tColumn.setHeaderValue("Test"+ixw);
      tColumn.setWidth(columnWidthPixel);
    }
    int widthPixel = width * xPixel;
    int heightPixel = height * mng.propertiesGui.yPixelUnit();
    table.setSize(widthPixel, heightPixel);
    if(TEST){
      String[] sColumnTitle = { "A", "B", "C"};
      String[][] contentTest = {{ "a", "b", "c"}, { "0", "1", "2"}, { "ikkkkkkkk", "j", "k"}, { "r", "s", "t"}, { "x", "y", "z"}};
      for (int i = 0; i < contentTest.length; i++) {
        TableItem item = new TableItem(table, SWT.NONE);
        item.setText(contentTest[i]);
      }
    }
    
    /*
    for (int i = 0; i < tableInfo.length; i++) {
      TableItem item = new TableItem(table, SWT.NONE);
      item.setText(tableInfo[i]);
    }
    */
    
    /*
      JTableHeader header= table.getTableHeader();
    Dimension dimHeader = header.getPreferredSize();
    header.setBounds(0,0, dimHeader.width, dimHeader.height);
    widget.add(header);
    //Rectangle boundsHeader = header.getBounds();
    Dimension size = table.getPreferredSize();
    //not sensitive: table.setBounds(0,dimHeader.height, size.width, size.height + 200);
    JScrollPane tablePane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    tablePane.setBounds(0,dimHeader.height, widthPixel, heightPixel - dimHeader.height);
    widget.add(tablePane);
    setBounds_(widget);
    //Point diff = new Point(0, boundsHeader.height);
    //setBounds_(table, diff);
    guiContent.add(widget);
   */
   resizeTable();
  }

  
  void resizeTable()
  {
    mng.setBounds_(table);
    
  }
  
  
  
  
  public static GralTable addTable(SwtWidgetMng mng, String sName, int height, int[] columnWidths
  //, int selectionColumn, CharSequence selectionText    
  )
  {
    
    boolean TEST = false;
    final SwtTable table;
    Composite parent = (Composite)mng.pos.panel.getPanelImpl();
    table = new SwtTable(mng, sName, parent, height, columnWidths); //, selectionColumn, selectionText);
    //GralWidget widgd = new GralWidget(sName, table, 'L', sName, null);
    table.setDataPath(sName);
    table.setPanelMng(mng);
    table.table.setData(table);
    mng.registerWidget(table);
    return table;

  }
  
  
  
  
  /**Inserts a line. it is called from {@link GralPanelMngWorking_ifc#setInfo(GralWidget, int, int, Object, Object)} 
   * and from {@link #insertLine(String, int)}.
   * @param ident
   * @param visibleInfo
   * @param userData
   * @return
   */
  GralTableLine_ifc changeTable(int ident, Object visibleInfo, Object userData)
  {
    TableItem item = new TableItem(table, SWT.NONE);
    String[] sLine;
    if(visibleInfo == null){
      //do nothing
    } else if(visibleInfo instanceof String){
      sLine = ((String)visibleInfo).split("\t");
      item.setText(sLine);
    } else if (visibleInfo instanceof String[]){
      sLine = (String[])visibleInfo;
      item.setText(sLine);
    }
    GralTableLine_ifc line = new TableItemWidget(item, userData); 
    item.setData(line);
    table.showItem(item);
    //set the scrollbar downward
    ScrollBar scroll = table.getVerticalBar();
    if(scroll !=null){
      int maxScroll = scroll.getMaximum();
      //log.sendMsg(0, "TEST scroll=%d", maxScroll);
      //scroll.setSelection(maxScroll);
    }  
    //table.set
    table.redraw(); //update();
    return line;
  }
  
  
  
  
  void clearTable(int ident)
  {
    if(ident <0){ table.removeAll();}
    else { table.remove(ident); }
    table.redraw(); //update();
  }
  
  
  

  

  
  
  
  public SelectionListener selectionListener = new SelectionListener()
  {

    @Override
    public void widgetDefaultSelected(SelectionEvent ev)
    {
      stop();
      
    }

    @Override
    public void widgetSelected(SelectionEvent ev)
    {
      if(ev.item instanceof TableItem){
        TableItem item = (TableItem)ev.item;
        Object oData = item.getData();
        if(oData instanceof GralTableLine_ifc){
          selectLine((GralTableLine_ifc)oData);
        }
        //item.setBackground(mng.propertiesGui.color(0x00ffff));
      }
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  
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
          int ixRow = -99999;
      		Table table1 = (Table)source;
	        ixRow  = table1.getSelectionIndex();   //the currently selected line.
	        if(ixRow >=0){  //< 0 if nothing is selected.
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
        stop();
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
  
  
  /**An instance of this class is assigned to any TableItem.
   * It supports the access to the TableItem (it is a table line) via the SWT-independent interface.
   * The instance knows its TableSwt and therefore the supports the access to the whole table.
   *
   */
  private class TableItemWidget extends SelectMask implements GralTableLine_ifc
  {
    private TableItem item;
    
    private Object userData;
    
    public TableItemWidget(TableItem item, Object userData)
    { this.item = item;
      this.userData = userData;
      item.setData(this);
    }
    
    @Override public Widget getWidgetImplementation(){ return item; } 
    

    @Override
    public GralColor setBackgroundColor(GralColor color)
    { GralColor oldColor = SwtProperties.createColorGui(item.getBackground());
      Color colorSwt = mng.propertiesGuiSwt.colorSwt(color.getColorValue());
      item.setBackground(colorSwt);
      return oldColor;
    }
    
    @Override
    public GralColor setForegroundColor(GralColor color)
    { GralColor oldColor = SwtProperties.createColorGui(item.getForeground());
      Color colorSwt = mng.propertiesGuiSwt.colorSwt(color.getColorValue());
      item.setForeground(colorSwt);
      return oldColor;
    }
    
    
    @Override
    public String getCellText(int column)
    {
      return item.getText(column);
    }

    @Override
    public String setCellText(String text, int column)
    {
      String sOldtext = item.getText(column);
      item.setText(column, text);
      return sOldtext;
    }

    @Override public Object getUserData() { return userData; }

    @Override public void setUserData(Object data) { userData = data; }
    
    @Override public boolean setFocus()
    {
      return table.setFocus();
    }
    
    
    @Override public void redraw(){  table.redraw(); table.update(); }

    @Override public void setBoundsPixel(int x, int y, int dx, int dy)
    { table.setBounds(x,y,dx,dy);
    }
    


  }
  
  
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
      int row = table.getSelectionIndex();
      if(row >=1){
        TableItem tableLineSwt = table.getItem(row-1);
      	tableLineSwt.setGrayed(true);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x80ffff)));
      }
    }
    
    @Override public void focusGained(FocusEvent ev)
    { super.focusGained(ev);
      int row = table.getSelectionIndex();
      if(row >=0){
        TableItem tableLineSwt = table.getItem(row);
        tableLineSwt.setGrayed(false);
        //tableLineSwt.setBackground(mng.getColorImpl(mng.propertiesGui.color(0x00ff00)));
      }
    }
    
  };
  
  
  void stop(){}


  @Override public GralTableLine_ifc getCurrentLine()
  {
    int row = table.getSelectionIndex();
    if(row >=0){
      TableItem tableLineSwt = table.getItem(row);
      return (GralTableLine_ifc)tableLineSwt.getData();
    } else return null;  //nothing selected.
  }

  
  @Override public void setCurrentCell(int line, int column)
  {
    if(Thread.currentThread().getId() == itsMng.gralDevice.getThreadIdGui()){
      table.setSelection(line);
      //table.select(line);
    } else {
      itsMng.setInfo(this, GralPanelMngWorking_ifc.cmdSelect, line, null, null);
    }
    
  }
  

  @Override public GralTableLine_ifc getLine(int row)
  {
    TableItem tableLineSwt = table.getItem(row);
    return (GralTableLine_ifc)tableLineSwt.getData();
  }

  @Override public List<GralTableLine_ifc> getSelectedLines(){
    List<GralTableLine_ifc> list = new LinkedList<GralTableLine_ifc>();
    TableItem[] items = table.getItems();
    for(TableItem item: items){
      Object oData = item.getData();
      if(oData instanceof GralTableLine_ifc){ //if a line isn't used it has null.
        GralTableLine_ifc data = (GralTableLine_ifc)oData;
        if((data.getSelection() & 1) !=0){
          list.add(data);
        }
      }
    }
    return list;
  }

  

  @Override public GralTableLine_ifc getLine(String key)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override public GralTableLine_ifc insertLine(String key, int row, String[] cellTexts, Object userData)
  {
    return changeTable(row, cellTexts, userData);
  }


  @Override public void deleteLine(GralTableLine_ifc line)
  {
  }


  @Override public void clearTable()
  { clearTable(-1);
  }


  @Override
  public int searchLine(String key)
  {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override public boolean setFocus()
  { return SwtWidgetHelper.setFocusOfTabSwt(table);
  }


  
  //@Override
  public Object getWidgetImplementation()
  {
    return table;
  }


  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override public void redraw(){  table.redraw(); table.update(); }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { table.setBounds(x,y,dx,dy);
  }
  

  
  @Override public void removeWidgetImplementation()
  {
    table.dispose();
    table = null;
  }


}
