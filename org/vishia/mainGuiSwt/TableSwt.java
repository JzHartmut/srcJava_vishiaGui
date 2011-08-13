package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.ColorGui;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.gral.widget.TableGui_ifc;
import org.vishia.gral.widget.TableLineGui_ifc;
import org.vishia.gral.widget.WidgetGui_ifc;

public class TableSwt implements TableGui_ifc
{

  private final Table table;
  
  private final GuiPanelMngSwt mng;
  
  private int[] columnWidths;
  
  //private final int selectionColumn;
  //private final CharSequence selectionText;
  
  public TableSwt(GuiPanelMngSwt mng, Composite parent,  int height
      , int[] columnWidths) //, int selectionColumn, CharSequence selectionText)
  {
    this.mng = mng;
    this.columnWidths = columnWidths;
    //this.selectionColumn = selectionColumn;
    //this.selectionText = selectionText;
    this.table = new Table(parent, 0) ; //, SWT.FULL_SELECTION); //| SWT.CHECK);
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
  
  
  
  
  public static WidgetDescriptor addTable(GuiPanelMngSwt mng, String sName, int height, int[] columnWidths
  //, int selectionColumn, CharSequence selectionText    
  )
  {
    
    boolean TEST = false;
    final TableSwt table;
    Composite parent = (Composite)mng.currPanel.panelComposite;
    table = new TableSwt(mng, parent, height, columnWidths); //, selectionColumn, selectionText);
    WidgetDescriptor widgd = new WidgetDescriptor(sName, table, 'L', sName, null);
    widgd.setPanelMng(mng);
    table.table.setData(widgd);
    mng.setWidgetToResize(widgd);
    mng.indexNameWidgets.put(sName, widgd);
    return widgd;

  }
  
  
  
  
  void changeTable(int ident, Object visibleInfo, Object userData)
  {
    TableItem item = new TableItem(table, SWT.NONE);
    
    if(visibleInfo instanceof String){
      String[] sLine = ((String)visibleInfo).split("\t");
      item.setText(sLine);
    }
    item.setData(new TableItemWidget(item, userData));
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
        //item.setBackground(mng.propertiesGui.color(0x00ffff));
      }
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  
  
  /**A Table is completed with a special key listener. On some keys and situation 
   * the {@link UserActionGui} given in the WidgetDescriptor is called. 
   * The following keys are detected:
   * <ul><li>Enter: "ok" Selection of a line or cell in the table
   * <li>KeyUp on the first line: "upleave": Leave the table.
   * <ul>
   * The 
   *
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
      final WidgetDescriptor widgetDescr;
      final UserActionGui action;
      final Object source = keyEv.getSource();
      if(source instanceof Control){
        Object oData = ((Control)source).getData();
        if(oData instanceof WidgetDescriptor){
          widgetDescr = (WidgetDescriptor)oData;
          action = widgetDescr.getActionChange();
        } else { widgetDescr = null; action = null; }
        } else { widgetDescr = null; action = null;
      }
      if(action !=null){
      	int ixRow = -99999;
      	try{
	      	Table table1 = (Table)source;
	        ixRow  = table1.getSelectionIndex();
	        if(ixRow >=0){  //< 0 if nothing is selected.
		        TableItem line = table1.getItem(ixRow);
		        TableLineGui_ifc lineGui = (TableLineGui_ifc)line.getData();
		        if(lineGui == null){
		          lineGui = new TableItemWidget(line, null);
		          line.setData(lineGui);
		        }
		        if(keyEv.keyCode == 0x0d){ //Enter-key pressed:
		          TableItem[] tableItem = table1.getSelection();
		          int zColumns = tableItem.length;
		          String[] content = new String[zColumns];
		          for(int ii=0; ii < zColumns; ++ii){
		            String text= tableItem[ii].getText();  //first column? TODO
		            String text2= tableItem[ii].getText(0);
		            content[ii] = text;  //content of the table item
		          }
		          action.userActionGui("ok", widgetDescr, content);    
		        } else if(keyEv.keyCode == SWT.KeyUp 
		                 && source instanceof Table && ixRow == 0){
		          action.userActionGui("upleave", widgetDescr, (Object)null);    
		        } else if(keyEv.character == ' '){
		          action.userActionGui("mark", widgetDescr, lineGui);    
		            
		          /*
		          && selectionColumn >=0){
		          String selected = line.getText(selectionColumn);
		          if(selected.length() >0) {
		            line.setText(selectionColumn, "");
		            line.setBackground(mng.propertiesGui.color(0xffffff));
		          }
		          else { 
		            line.setText(selectionColumn, selectionText.toString());
		            line.setBackground(mng.propertiesGui.color(0x00ff00));
		          }
		          //table.setSelection(select);
		          stop();
		          */
		        }
	        } //if(table.)
	      } catch(Exception exc){
      		stop();  //ignore it
      	}
      }
      stop();
      //basicListener.keyPressed(arg0);
      
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
  private class TableItemWidget implements TableLineGui_ifc
  {
    private final TableItem item;
    
    private Object userData;
    
    public TableItemWidget(TableItem item, Object userData)
    { this.item = item;
      this.userData = userData;
      item.setData(this);
    }
    
    @Override public Widget getWidget(){ return item; } 
    

    @Override
    public ColorGui setBackgroundColor(ColorGui color)
    { ColorGui oldColor = PropertiesGuiSwt.createColorGui(item.getBackground());
      Color colorSwt = mng.propertiesGuiSwt.colorSwt(color.getColorValue());
      item.setBackground(colorSwt);
      return oldColor;
    }
    
    @Override
    public ColorGui setForegroundColor(ColorGui color)
    { ColorGui oldColor = PropertiesGuiSwt.createColorGui(item.getForeground());
      Color colorSwt = mng.propertiesGuiSwt.colorSwt(color.getColorValue());
      item.setForeground(colorSwt);
      return oldColor;
    }
    
    
    @Override
    public String getText()
    {
      // TODO Auto-generated method stub
      return null;
    }
    @Override
    public String setText(String text)
    {
      // TODO Auto-generated method stub
      return null;
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
  

  
  void stop(){}


  @Override public TableLineGui_ifc getCurrentLine()
  {
    int row = table.getSelectionIndex();
    if(row >=0){
      TableItem tableLineSwt = table.getItem(row);
      return (TableLineGui_ifc)tableLineSwt.getData();
    } else return null;  //nothing selected.
  }


  @Override public TableLineGui_ifc getLine(int row)
  {
    TableItem tableLineSwt = table.getItem(row);
    return (TableLineGui_ifc)tableLineSwt.getData();
  }


  @Override public TableLineGui_ifc getLine(String key)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override public int insertLine(String key, TableLineGui_ifc line, int row)
  {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override
  public int searchLine(String key)
  {
    // TODO Auto-generated method stub
    return 0;
  }


  @Override
  public String getText()
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public ColorGui setBackgroundColor(ColorGui color)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public ColorGui setForegroundColor(ColorGui color)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public String setText(String text)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public Object getWidget()
  {
    return table;
  }
}
