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
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;
import org.vishia.util.KeyCode;

/**Implementation of the GralTable for Swt graphic.
 * The following schema shows the aggregation relations, see {@link org.vishia.util.Docu_UML_simpleNotation}:
 * <pre>
 *                      |--widgg----------------->GralWidget<|-------+
 *                      |                             |              |
 *            +--|>GralWidget.ImplAccess<--widgImpl---|              |
 *            |                                                      |
 *            |                                                      |
 *    +-|>GralTable.GraphicImplAccess                                |
 *    |                   |                                          |
 * SwtTable               |                                     GralTable
 *    |                   |                                          |
 *    |                   |<-------------------------------------gi--|
 *    |                   |                                          |
 *    |                   |<&>-------------------------------------&>|
 *    |                        access to all protected methods and fields of GralTable
 *    |
 *    |
 *    |
 *    |---swtWidg----->SwtWidgetHelper
 *    |                       |
 *    |                       |
 *    |                       |--widgetSwt--->Control<|---Composite<|---SwtTable.Table
 *    |                                                      |               |
 *    |---cellsSwt--------------*>Text<*---------------------|            some Listener
 *    |
 *    |
 * </pre>
 * <ul>
 * <li>The {@link GralTable} contains some implementation-non-specific things, most of table structure.
 * <li>To access from this class to the GralTable, the inner class {@link GralTable.GraphicImplAccess}
 *   is used as super class of SwtTable with protected access.
 * <li>The GralTable knows this class via the interface {@link GralWidgImplAccess_ifc} in its superclass
 *   association {@link GralWidget#_wdgImpl}.
 * <li>The GralTable knows this class via {@link GralTable#gi} association with the proper type.
 * <li>The {@link GralTable.GraphicImplAccess} defines some abstract methods which are implemented here.
 * <li>But some implementations of {@link GralWidgImplAccess_ifc} is found in {@link SwtWidgetHelper}.
 *   That are unique implementations, reuse it!
 * <li>Therefore for reused implementations this class delegates the interface to {@link #swtWidgHelper}.
 * <li>The Swt widget core implementation is {@link Table} which is derived from
 *   {@link org.eclipse.swt.widgets.Composite}. That Composite contains some text fields
 *   which are the visible rows and columns of the table.
 * <li>The text fields are created as children of the swt.widgets.Composite but associated
 *   in this class: {@link #cellsSwt}. Therefore this class can access the visible rows and columns
 *   directly, using as {@link org.eclipse.swt.widgets.Text}.
 * <li>The inner class {@link Table} is necessary only as implementor of {@link GralWidgetGthreadSet_ifc}.
 *   But that is an {@link Deprecated} interface. It is possible to replace it later with an simple
 *   instance of Composite.
 * <li>The content of the table itself is stored in an array of {@link GralTable#tableLines},
 *   see description of {@link GralTable}.
 * </ul>
 * @author Hartmut Schorrig
 *
 */
public class SwtTable  extends GralTable<?>.GraphicImplAccess implements GralWidgImplAccess_ifc

//public class SwtTable  implements GralWidgImpl_ifc
{

  /**Version and history
   * <ul>
   * <li>2018-01-07 Hartmut new: {@link #getCellTextFocus()}
   * <li>2015-08-29 Hartmut chg: It has a {@link #traverseListenerTable} now to accept 'tab' and 'sh-tab' as normal key
   *   instead usage to traverse between the text fields of a table. TODO: What traversing functions are missing yet?
   *   They should not any traverse function between the cells.
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
  public final static String version = "2015-08-29";

  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWidgHelper;

  /**Some SWT Text Control fields. There are the visible cells of the table. They are children
   * of the Composite in {@link SwtWidgetHelper#widgetSwt}. */
  private final Text[][] cellsSwt;

  private Canvas vScrollBar;

  private Text cellInFocus;

  Color colorBackVscrollbar, colorSliderVscrollbar;

  /**SWT Text Control which contains a search text. It is only visible if need. */
  protected Text swtSearchText;

  //private final SwtTable.Table table;

  //private final FocusListener focusListenerTable;

  //private final FocusListener focusListenerCell;

  private final MousePressedListenerTable mousePressedListener = new MousePressedListenerTable();

  private final MouseWheelListenerTable mouseWheelListener = new MouseWheelListenerTable();

  private final TableKeyListerner myKeyListener;

  public SwtTable(GralTable<?> gralTable, SwtMng mng, Composite parent) {
    gralTable.super(gralTable, mng.gralMng);
    //super(name, mng, columnWidths);
    StringBuilder sLog = new StringBuilder(120);
    this.myKeyListener = this.new TableKeyListerner(null);
    //this.focusListenerTable = this.new FocusListenerTable(mng);
    //focusListenerCell = this;
    setColorsSwt();
    int zLineVisibleMax = gralTable.nrofLinesVisibleMax();
    this.cellsSwt = new Text[zLineVisibleMax][zColumn()];
    int zColumn = zColumn();                               // tehe tables cells are assembled in an own Composite
    Composite swtTable = new SwtTable.Table(parent, zColumn, mng);       // it is not a panel.
    //The background of the panel, which does not contain cells of Text:
    swtTable.setBackground(mng.getColorImpl(GralColor.getColor("pgr")));
    GralMenu[] contextMenuColumns = super.getContextMenuColumns();
    GralMenu contextMenu = gralTable.getContextMenu();
    initSwtTable(swtTable, zColumn, mng, contextMenu, contextMenuColumns);
    this.vScrollBar = new Vscrollbar(swtTable);

    swtTable.setVisible(gralTable.isVisible());            // sets the initial visible state of the composite of all cells and scrollbar.
    super.wdgimpl = this.swtWidgHelper = new SwtWidgetHelper(swtTable, mng);
    //gralTable.implMethodWidget_.setWidgetImpl(this);
    //this.menuColumns = new SwtMenu[zColumn];
    swtTable.addKeyListener(this.myKeyListener);
    //table.addSelectionListener(selectionListener);
    //swtTable.addControlListener(this.resizeListener);
    //swtTable.addFocusListener(this.focusListenerTable);

    swtTable.setFont(mng.propertiesGuiSwt.stdInputFont);
    GralRectangle pixTable = this.swtWidgHelper.mng.setBounds_(this.widgg.pos(), swtTable);
    SwtTable.this.resizeTable(pixTable);
    mng.gralMng.log.sendMsg(GralMng.LogMsg.newImplTable, sLog);
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
        table.swtWidgHelper.widgetSwt.setData(table);
        mng.gralMng.registerWidget(gralTable);
        //NOTE done in SwtTable.resize()     ((SwtMng)mng).setPosAndSize_(table.table);
        return gralTable;

      }


  /*package private*/
  static GralWidget.ImplAccess createTable(GralTable<?> gralTable, SwtMng mng) {
    GralWidgetBase_ifc panelg = gralTable.pos().parent;
//    if(panelg.getName().equals("tabFavorsAll1")) {
//      Debugutil.stop(); }

    Composite parent = mng.getWidgetsPanel(gralTable);
    boolean parentVisible = parent.isVisible();
    @SuppressWarnings("unchecked")
    final SwtTable table = new SwtTable(gralTable, mng, parent); //, selectionColumn, selectionText);
    table.swtWidgHelper.widgetSwt.setData(table);
    table.redrawGthread();
    //mng.gralMng.registerWidget(gralTable);
    //NOTE done in SwtTable.resize()     ((SwtMng)mng).setPosAndSize_(table.table);
    return table;
  }


  private void setColorsSwt(){
    //colorBackSelectSwt = mng.getColorImpl(colorBackSelect);
    //colorBackMarkedSwt = mng.getColorImpl(colorBackMarked);
    //colorBackTableSwt = mng.getColorImpl(colorBackTable);
    //colorBackSelectNonFocusedSwt = mng.getColorImpl(colorBackSelectNonFocused);
    //colorBackMarkedNonFocusedSwt = mng.getColorImpl(colorBackMarkedNonFocused);
    //colorBackTableNonFocusedSwt = mng.getColorImpl(colorBackTableNonFocused);
  }




  @Override public Object getWidgetImplementation() {
    return this.swtWidgHelper.widgetSwt;
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
    SwtTable.this.swtWidgHelper.widgetSwt.setBounds(x, y, dx, dy);

  }



  @Override public GralRectangle getPixelPositionSize(){ return this.swtWidgHelper.getPixelPositionSize(); }





  /** TODO implement in {@link GralTable.GraphicImplAccess}
   * @see org.vishia.gral.base.GralWidgImplAccess_ifc#redrawGthread()
   */
  @Override public void redrawGthread(){
    if(this.bFocusLost){
      //this is about 50 ms after focus lost, the focus has lost really.
      this.bFocused = false;
      this.bFocusLost = false;
      this.widgg.setFocused(false);
    }
    if(this.swtWidgHelper.widgetSwt !=null && !this.swtWidgHelper.widgetSwt.isDisposed()){
      int chg = getChanged();
      int acknChg = 0;
      if((chg & chgVisible)!=0){
        acknChg |= chgVisible;
        setVisibleState(true);
        if(!this.swtWidgHelper.widgetSwt.isVisible()) {
          this.swtWidgHelper.widgetSwt.setVisible(true);  //the composite.
        }
        //for(Text[] lines: cellsSwt){
        //  for(Text cell: lines){ cell.setVisible(true); }
        //}
      }
      if((chg & chgInvisible)!=0){
        acknChg |= chgInvisible;
        setVisibleState(false);
        if(this.swtWidgHelper.widgetSwt.isVisible()) {
          this.swtWidgHelper.widgetSwt.setVisible(false);  //the composite.
        }
        //for(Text[] lines: cellsSwt){
        //  for(Text cell: lines){ cell.setVisible(false); }
        //}
      }
      acknChanged(acknChg);
      if(this.widgg.isVisible()){
        //
        updateGraphicCellContent();  //invokes drawCellContent(...) with the correct lines.
        //^^^^^^^^^^^^^^^^^
        //setAllCellContentGthread();
        Color colorSelectBack =  this.swtWidgHelper.mng.getColorImpl(super.colorSelectCharsBack());
        Color colorSelect =  this.swtWidgHelper.mng.getColorImpl(super.colorSelectChars());
        if(super.searchChars().length() >0){
          this.swtSearchText.setBackground(colorSelectBack);
          this.swtSearchText.setForeground(colorSelect);
          //swtSearchText.
          this.swtSearchText.setText(searchChars().toString());
          Point tabSize = this.swtWidgHelper.widgetSwt.getSize();
          this.swtSearchText.setBounds(this.xpixelCell[ixColumn()] + 10, tabSize.y - this.linePixel, this.xpixelCell[ixColumn()+1] - this.xpixelCell[ixColumn()] - 10, this.linePixel);
          this.swtSearchText.setVisible(true);

        } else {
          this.swtSearchText.setVisible(false);
        }
        //System.out.println("swtTable redrawed");
        //see redrawChildren. This redraw don't influences the vScrollbar:
        //swtWidgHelper.widgetSwt.redraw();
        //swtWidgHelper.widgetSwt.update();  //this is the core-redraw
        //redraw command for the:
        if(this.bVscrollbarChanged){
          this.vScrollBar.update();
          this.vScrollBar.redraw();
        }
      }
      //((Table)swtWidg.widgetSwt).super.redraw();
      this.redrawtime = System.currentTimeMillis();
      super.bChangedLinesForCell(false);
      //System.out.println("test SwtTable redraw " + ++redrawct);
    } else {
      //System.out.println("test SwtTable redraw disposed" + ++this.redrawct);
    }
    this.bRedrawPending = false;

  }


  /**Sets the focus to the current cell of the tab
   * @see org.vishia.gral.base.GralWidget#setFocusGThread()
   * TODO this method must call in the graphic thread yet, queue it with {@link GralMng#setInfo(GralWidget, int, int, Object, Object)}.
   */
  @Override public boolean setFocusGThread()
  { redrawGthread();  //to set the focus of the cell
    return true;
  }


  @Override public void setVisibleGThread(boolean bVisible) {
    if(this.swtWidgHelper.widgetSwt.isVisible() != bVisible) { 
      this.swtWidgHelper.widgetSwt.setVisible(bVisible);
    }
    if(bVisible)
      Debugutil.stop();
    super.setVisibleState(bVisible);
    for(Text[] cell1 : this.cellsSwt) for(Text cell: cell1){
      if(cell !=null) {
        if(cell.isVisible() != bVisible) {
          cell.setVisible(bVisible);
        }
      }
    }
    redrawGthread();
  }


  @Override
  protected void setDragEnable(int dragType)
  { throw new IllegalArgumentException("drag not supported for this widget type");
  }


  /**Removes the graphical widgets.
   * @see org.vishia.gral.base.GralWidget#removeWidgetImplementation()
   */
  @Override public void removeWidgetImplementation() {
    // TODO Auto-generated method stub
    if(!this.swtWidgHelper.widgetSwt.isDisposed()){
      for(int iRow = 0; iRow < this.cellsSwt.length; ++iRow){
        for(int iCol = 0; iCol < zColumn(); ++iCol){
          Text cell = this.cellsSwt[iRow][iCol];
          cell.dispose();
          this.cellsSwt[iRow][iCol] = null;
        }
      }
      this.swtWidgHelper.widgetSwt.dispose();  //it may be sufficient to dispose table only becaust it is the container....
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
    GralTable.CellData data = (GralTable.CellData)cell.getData();
    data.bSetFocus = true;
    if(super.redrawTableWithFocusedCell(data)){
      redrawGthread();
      //((Table)swtWidgWrapper.widgetSwt).redrawGthread();
    }
  }



  @Override protected int getVisibleLinesTableImpl(){
    Rectangle size = this.swtWidgHelper.widgetSwt.getBounds();
    return (size.height+1) / this.linePixel;
  }


  /**This routine implements all things to set the content of any table cell to show it.
   * @see org.vishia.gral.base.GralTable#drawCellContent(int, int, org.vishia.gral.base.GralTable.TableLineData)
     * @param iCellLine index in the line (visible Text fields)
     * @param cellData data for the Text[] of this line: color, etc. see {@link GralTable.CellData}
     * @param line the line in the container of the table. Note: it is a node structure
     * @param linePresentationP colors of the line
   */
  @Override protected void drawCellContent(int iCellLine, GralTable.CellData[] cellLine, GralTable<?>.TableLineData line, GralTable.LinePresentation linePresentationP){
    Text[] textlineSwt = this.cellsSwt[iCellLine];         // The Text[] ioof the iCellLine
    int treeDepth = -1;
    if(line !=null && line.treeDepth() != cellLine[0].treeDepth){
      treeDepth =  cellLine[0].treeDepth = line.treeDepth();
    }
    if(line !=null){
      for(int col=0; col < this.cells[0].length; ++col){
        Text cellSwt = textlineSwt[col];
        GralTable.CellData cellData = cellLine[col]; //(GralTable.CellData)cellSwt.getData();
        if(treeDepth >=0 && col < super.nrofColumnTreeShift){
          int xleft = this.xpixelCell[col] + treeDepth * this.xPixelUnit;
          int xRight = this.xpixelCell[col+1] + (col < (super.nrofColumnTreeShift-1) ?  treeDepth * this.xPixelUnit : 0);
          int yTop = cellData.ixCellLine * this.linePixel;
          cellSwt.setBounds(xleft, yTop, xRight - xleft, this.linePixel);
        }
        //
        if(line.wasCelltextSet(col) || bChangedLinesForCell()) {
          String text = line.cellTexts[col];
          if(text == null){ text = ""; }
          //
          cellSwt.setText(text);
        }
        if(cellSwt.isFocusControl()) {
          //System.out.printf("SwtTable cell in focus: %s, %d\n", line.toString(), col);
          this.widgg.setFocused(true);
        }
        if(cellData.bSetFocus){
          boolean bFocusOk = cellSwt.forceFocus();
          cellData.bSetFocus = false;
          //System.out.printf("SwtTable cell set focus: %b %s, %d\n", bFocusOk, line.toString(), col);
          //this.widgg.gralMng.log().sendMsg(GralMng.LogMsg.setFocus, "set Focus %d %d %s", iCellLine, col, bFocusOk ? "ok" : "no");
        }
        GralColor colorBackCell = linePresentationP.cellsColorBack !=null && linePresentationP.cellsColorBack[col] !=null
            ? linePresentationP.cellsColorBack[col] : linePresentationP.colorBack;
        if(cellData.colorBack != colorBackCell){
          //only change color of the SWT Text field if it is necessary, comparison set color with cellData.color.
          Color colorSwt =  this.swtWidgHelper.mng.getColorImpl(colorBackCell);
          cellSwt.setBackground(colorSwt);
          cellData.colorBack = colorBackCell;  //for the visible cell swt widget, not for the table line!
        }
        if(cellData.colorText != linePresentationP.colorText){
          //only change color of the SWT Text field if it is necessary, comparison set color with cellData.color.
          cellSwt.setForeground(this.swtWidgHelper.mng.getColorImpl(linePresentationP.colorText));
          cellData.colorText = linePresentationP.colorText;
        }
        if(!cellSwt.isVisible()) {
          cellSwt.setVisible(true);
        }
        cellSwt.redraw();
      }
    } else {
      //empty line:
      GralColor colorBack = linePresentationP.colorBack;  //colorBackTable();
      for(int col=0; col < this.cells[0].length; ++col){
        Text cellSwt = textlineSwt[col];
        GralTable.CellData cellData = cellLine[col]; //(GralTable.CellData)cellSwt.getData();
        cellSwt.setText("");
        if(cellData.colorBack != colorBack){
          Color colorSwt =  this.swtWidgHelper.mng.getColorImpl(colorBack);
          cellSwt.setBackground(colorSwt);
          cellData.colorBack = colorBack;  //for the visible cell swt widget, not for the table line!
        }
      }
    }
  }





  @Override protected GralTable.CellData drawCellInvisible(int iCellLine, int iCellCol){
    Text cellSwt = this.cellsSwt[iCellLine][iCellCol];
    GralTable.CellData cellData = (GralTable.CellData)cellSwt.getData();
    cellSwt.setText("");
    if(cellData.colorBack != colorBackTable()){
      Color colorSwt =  this.swtWidgHelper.mng.getColorImpl(colorBackTable());
      cellSwt.setBackground(colorSwt);
      cellData.colorBack = colorBackTable();
    }
    cellSwt.setVisible(true);
    return (cellData);
  }




  private void paintVscrollbar(GC gc, Canvas canvas)
  {

    if(this.ixColorScrollbar != this.ixColorScrollbarLast) {
      //only get a new color if necessary.
      this.colorBackVscrollbar = this.swtWidgHelper.mng.getColorImpl(colorBackVscrollbar());
      this.colorSliderVscrollbar = this.swtWidgHelper.mng.getColorImpl(colorLineVscrollbar());
      this.ixColorScrollbarLast = this.ixColorScrollbar;
    }
    Rectangle dim = canvas.getBounds();
    determineSizeAndPositionScrollbar(dim.height);
    gc.setForeground(this.colorBackVscrollbar);
    gc.fillRectangle(1, dim.y, dim.width, dim.height);
    gc.setForeground(this.colorSliderVscrollbar);
    gc.setBackground(this.colorSliderVscrollbar);
    //Note: relative coordinates inside the canvas area:
    gc.fillRectangle(1, this.y1Scrollbar, dim.width-1, this.y2Scrollbar - this.y1Scrollbar);
    //gc.drawLine(1, y1Scrollbar+1, dim.width-1, y2Scrollbar); // - y1Scrollbar-1);

    //gc.drawLine(1,1, dim.width-1, dim.height-1);

  }




  @Override protected GralMenu createColumnMenu(int column){
    //GralMenu menuColumn = new SwtMenu(outer, swtWidgWrapper.widgetSwt, itsMng());
    GralTable<?> widgg = (GralTable<?>)super.widgg;
    GralMenu menuColumn = new GralMenu(widgg);
    new SwtMenu(menuColumn, null, this.cellsSwt[0][column]);
    for(int iRow = 1; iRow < this.cellsSwt.length; ++iRow){
      //uses the same menu instance in all cells of the column.
      this.cellsSwt[iRow][column].setMenu((Menu)menuColumn.getMenuImpl());
    }
    return menuColumn;
  }


  /**Called internal from mouse event only.
   * @param ev
   */
  protected void mouseDown(MouseEvent ev){
    Text widgSwt = (Text)ev.widget;  //it is only associated to a cell.
    GralTable.CellData cell = (GralTable.CellData)widgSwt.getData();
    int key = SwtGralKey.convertMouseKey(ev.button, SwtGralKey.MouseAction.down, ev.stateMask);
    super.mouseDownGral(key, cell);  //To independent GRAL layer
  }



  /**Called internal from mouse event only.
   * @param ev
   */
  protected void mouseUp(MouseEvent ev){
    Text widgSwt = (Text)ev.widget;  //it is only associated to a cell.
    GralTable.CellData cell = (GralTable.CellData)widgSwt.getData();
    int key = SwtGralKey.convertMouseKey(ev.button, SwtGralKey.MouseAction.up, ev.stateMask);
    super.mouseUpGral(key, cell);    //To independent GRAL layer
  }



  /**Called internal from mouse event only.
   * @param ev
   */
  protected void mouseDouble(MouseEvent ev){
    Text widgSwt = (Text)ev.widget;  //it is only associated to a cell.
    GralTable.CellData cell = (GralTable.CellData)widgSwt.getData();
    int key = SwtGralKey.convertMouseKey(ev.button, SwtGralKey.MouseAction.down, ev.stateMask);
    super.mouseDoubleGral(key, cell);    //To independent GRAL layer
  }



  @Override protected void setBoundsCells(int treeDepthBase, int zLineVisible){
    int yPix = 0;
    //int xPixelUnit = swtWidgHelper.mng.mng.propertiesGui.xPixelUnit();
    int ixrow = 0;
    for(Text[] row: this.cellsSwt){
      if(ixrow < zLineVisible) {
        int ixColumn = 0;
        for(Text cell: row){
          ///cell.setVisible(true);
          int xleft = 0;
          cell.setBounds(xleft + this.xpixelCell[ixColumn], yPix, this.xpixelCell[ixColumn+1] - this.xpixelCell[ixColumn], this.linePixel);
          ixColumn +=1;
        }
        yPix += this.linePixel;
      } else {
        //invisible cells:
        for(Text cell: row){
          cell.setVisible(false);
        }
      }
      ixrow +=1;
    }
    this.vScrollBar.setBounds(this.xyVscrollbar.x, this.xyVscrollbar.y, this.xyVscrollbar.dx, this.xyVscrollbar.dy);
  }

  /**Invoked on {@link #swtKeyListener} if enter. */
  void setTextToLine(Text widgSwt){
    String text = widgSwt.getText();
    GralTable.CellData data = (GralTable.CellData)widgSwt.getData();
    SwtTable.this.setCellText(data, text);
  }


  protected String getCellText(GralTable.CellData cell){
    return this.cellsSwt[cell.ixCellLine][cell.ixCellColumn].getText();
  }


  @Override protected String getCellTextFocus(){
    if(this.cellInFocus == null) return null;
    else return this.cellInFocus.getText();
  }




  FocusListener focusListenerCells = new FocusListener() { //for the cells

    /**Focus listener implementation for all cells.
     * This routine is invoked whenever the focus of any Text field of the table will be lost the focus.
     * It invokes {@link GralTable.GraphicImplAccess#focusLostTable()} but only if
     * {@link GralTable.GraphicImplAccess#bRedrawPending} is not set. That prevents invocation while
     * {@link GralTable.GraphicImplAccess#updateGraphicCellContent()} sets the focus while updating the graphic cells.
     *
     */
    @Override public void focusLost(FocusEvent ev){
      //System.out.println("Cell focus lost");
      String textLost = "---" + ((Text)ev.widget).getText();
      if(!SwtTable.this.bRedrawPending && System.currentTimeMillis() - (((GralTable)SwtTable.this.widgg).timeLastKeyUpDn) > 300){
        SwtTable.this.focusLostTable();         // do not call during redrawpending, supress unnecessary redraw action.
        //System.out.println("SwtTable - cell focus lost;" + (SwtTable.this).outer.toString());
        GralTable.CellData celldata = (GralTable.CellData)ev.widget.getData();
        Text widgSwt = (Text)ev.widget;
        String sText = widgSwt.getText();
        textLost = sText;
        if(SwtTable.this.bColumnEditable(celldata.ixCellColumn)){
          SwtTable.this.checkAndUpdateText(sText, celldata);
        }
        SwtTable.this.widgg.setFocused(false);                 // notes the focus lost to the GralWidget and its parents.
      }
      //System.out.printf("SwtTableCell - focus lost %s\n", textLost);
    }

    /**Focus listener implementation for all cells.
     * This routine is invoked especially if the mouse is landing
     * on a Text-field with click. Then this table line and column
     * should be selected as currently. <br>
     * This routine is invoked on setFocus()-call too. In this case
     * it should not done anything. The variable #bRedrawPending guards it.
     * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
     */
    @Override public void focusGained(FocusEvent ev) {
      String sText = "---" + ((Text)ev.widget).getText();
      if(sText.equals("---Download")) {
        Debugutil.stop();
      }
      if(!SwtTable.this.bRedrawPending && System.currentTimeMillis() - (((GralTable)SwtTable.this.widgg).timeLastKeyUpDn) > 300){
        Text widgSwt = (Text)ev.widget;
        sText = widgSwt.getText();
        GralTable.CellData cell = (GralTable.CellData)((Text)ev.getSource()).getData();
        SwtTable.this.ixLineFocus = cell.ixCellLine;
        SwtTable.this.ixColumnFocus = cell.ixCellColumn;
        SwtTable.this.focusGainedTable();
        //setFocused(SwtTable.this.widgg, true);
        SwtTable.this.cellInFocus = widgSwt; //(Text)ev.getSource();
        SwtTable.this.widgg.setFocused(true);                 // notes the focus gained to the GralWidget and its parents.
      }
      //System.out.printf("SwtTableCell - focus gained %s\n", sText);
    }
  };




  /**It creates {@link Text} for each cell of the table,
   * and assigns the {@link #swtKeyListener}, {@link #focusListenerCells}, {@link #mousePressedListener},
   * {@link #mouseWheelListener}, {@link #traverseListenerTable} to all cells.
   * <br>
   * Each cell gets {@link GralTable.CellData} as {@link Control#setData(Object)}.
   * @param swtTable
   * @param zColumns
   * @param mng
   */
  protected void initSwtTable(Composite swtTable, int zColumns, SwtMng mng, GralMenu contextMenu, GralMenu[] contextMenuColumn ){
    int yPix = 0;
    GralTable wdgg = (GralTable)super.widgg;
    Font font = mng.propertiesGuiSwt.stdInputFont; //mng.propertiesGuiSwt.getTextFontSwt(2, 'n', 'n');
    Color colorBackTableSwt = mng.getColorImpl(colorBackTable());
    if(contextMenuColumn !=null || contextMenu !=null) {
      Debugutil.stop();
    }

    for(int iCol = 0; iCol < zColumns; ++iCol){
      //menuColumns[iCol] = new SwtMenu(name + "_menu" + iCol, this, itsMng());
    }
    //NOTE: only if the swtSelectText is created first, it will drawn in in the foreground of the other cells.
    this.swtSearchText = new Text(swtTable, SWT.LEFT | SWT.SINGLE | SWT.READ_ONLY);
    this.swtSearchText.setFont(font);
    this.swtSearchText.setVisible(false);
    for(int iRow = 0; iRow < this.cellsSwt.length; ++iRow){
      for(int iCol = 0; iCol < zColumns; ++iCol){
        boolean editable = super.bColumnEditable(iCol);
        Text cell = new Text(swtTable, SWT.LEFT | SWT.SINGLE | (editable  ? 0 : SWT.READ_ONLY));
        cell.setText("cell" + iRow + "," + iCol);
        if(editable){
          cell.addKeyListener(this.swtKeyListener);
        }
        cell.setFont(font);
        cell.addKeyListener(this.myKeyListener);
        cell.addFocusListener(this.focusListenerCells);
        cell.addMouseListener(this.mousePressedListener);
        cell.addMouseWheelListener(this.mouseWheelListener);
        cell.addTraverseListener(this.traverseListenerTable);
        GralTable.CellData cellData = new GralTable.CellData(iRow, iCol);
        cell.setData(cellData);
        super.cells[iRow][iCol] = cellData;                // in GralTable.GraphicImplAccess
        cell.setBackground(colorBackTableSwt);
        if(contextMenu !=null) {
          if(!contextMenu.hasImplementation()){            // create a implementation with the first cell and the GralTable as widget.
            new SwtMenu(contextMenu, wdgg, cell);          // aggregates SwtMenu in contectMenu
          }
          cell.setMenu((Menu)contextMenu.getMenuImpl());
        }
        if(contextMenuColumn !=null && contextMenuColumn[iCol] !=null) {
          if(!contextMenuColumn[iCol].hasImplementation()){   // create a implementation with the first cell and the GralTable as widget.
            new SwtMenu(contextMenuColumn[iCol], wdgg, cell); // aggregates SwtMenu in contectMenu
          }
          cell.setMenu((Menu)contextMenuColumn[iCol].getMenuImpl());
        }
        this.cellsSwt[iRow][iCol] = cell;                       // The array of swt.Txt
      }
      yPix += this.linePixel;
    }
    //The text field for selection string



  }




  /**The widget for the vertical scroll bar is a canvas with a special paint routine.
   * In the paint routine {@link SwtTable#paintVscrollbar(PaintEvent, Canvas)} is called.
   */
  @SuppressWarnings("synthetic-access")
  private class Vscrollbar extends Canvas
  {
    Vscrollbar(Composite parent) {
      super(parent, 0);
      addPaintListener(this.vScrollbarPainter);

    }

    @Override
    public void drawBackground(GC g, int x, int y, int dx, int dy) {
      System.out.println("VScrollbar - draw");
      //SwtTable.this.paintVscrollbar(g, Vscrollbar.this);
    }

    private PaintListener vScrollbarPainter = new PaintListener(){
      @Override public void paintControl(PaintEvent e) {
        //System.out.println("VScrollbar - paintlistener");
        SwtTable.this.paintVscrollbar(e.gc, Vscrollbar.this);
      }
    };

  }



  /**The SWT-Composite for the cell texts and the scroll bar. */
  static class Table extends Composite {

    final SwtMng mng;
    public Table(Composite parent, int zColumns, SwtMng mng) {
      super(parent, 0);
      this.mng = mng;
    }


    //@Override public void drawBackground (GC gc, int x, int y, int width, int height, int offsetX, int offsetY) {
    //  redrawGthread();
    //}


    @Override
    public boolean setFocus () {
      this.mng.gralMng.log.sendMsg(GralMng.LogMsg.newImplTable, "set Focus of SwtTable.Table");

      return true;
    }


  }

  
  protected void keyPressed(KeyEvent keyEv){
    try{
      if((keyEv.keyCode & 0xffff) !=0){
        final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask, keyEv.character);
        processKeys(keyCode);
      }
    } catch(Exception exc){
      String txt = ExcUtil.exceptionInfo("SwtTable - keyPressed Exception", exc, 0, 20, true).toString();
      this.swtWidgHelper.mng.gralMng.log.sendMsg(0, txt);
      //CharSequence stackInfo = Assert.exceptionInfo("Gral - SwtTable;", exc, 1, 5);
      //System.err.append(stackInfo);
      //exc.printStackTrace(System.out);
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
    { SwtTable.this.keyPressed(keyEv);
    }

    @Override
    public void keyReleased(KeyEvent arg0)
    {
      //basicListener.keyReleased(arg0);

    }

  };

  /**The Resize listener for the whole table (a swt.Composite).
   * The controlResized(...) operation calls {@link #resizeTable(GralRectangle)}
   * which calculates and sets the bounds of all swt.Text fields.
   */
  ControlListener XXXresizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e)
    { //do nothing if moved.
      stop();
    }

    /**The Composite for the whole table is resized already. This routine should determine which Text fields are visible and used.
     * @see org.eclipse.swt.events.ControlListener#controlResized(org.eclipse.swt.events.ControlEvent)
     */
    @Override public void controlResized(ControlEvent e)
    {
      stop();
      Rectangle boundsTable = ((Control)e.widget).getBounds();
      int borderTable = ((Control)e.widget).getBorderWidth();
      //the pixTable is given already with the Composite of the whole table.
      GralRectangle pixTable = new GralRectangle(boundsTable.x, boundsTable.y, boundsTable.width, boundsTable.height);
      //it calls repaint with delay.
      //Composite parent = ((Control)e.widget).getParent();
      //Rectangle parentBounds = parent.getBounds();
      //GralRectangle pixTable = outer.pos().calcWidgetPosAndSize(itsMng().propertiesGui(), parentBounds.width, parentBounds.height, 0, 0);
      //GralRectangle pixTable = outer.pos().calcWidgetPosAndSize(itsMng().propertiesGui(), boundsTable.width, boundsTable.height, 0, 0);
      SwtTable.this.resizeTable(pixTable);
    }

  };


  private final class MousePressedListenerTable implements MouseListener{

    protected MousePressedListenerTable(){}


    @Override
    public void mouseDoubleClick(MouseEvent ev)
    { //Note: redirect to environment class to prevent usage of synthetic accessor
      SwtTable.this.mouseDouble(ev);
      //System.out.println("SwtTable-mouse-double");

    }

    @Override
    public void mouseDown(MouseEvent ev)
    { //Note: redirect to environment class to prevent usage of synthetic accessor
      SwtTable.this.mouseDown(ev);
    }

    @Override
    public void mouseUp(MouseEvent ev)
    { //Note: redirect to environment class to prevent usage of synthetic accessor
      SwtTable.this.mouseUp(ev);
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
  private class XXXFocusListenerTable implements FocusListener //extends SwtWidgetMng.SwtMngFocusListener
  {
    XXXFocusListenerTable(SwtMng mng){
      //mng.super();
    }

    @Override public void focusLost(FocusEvent e){
      System.out.println("SwtTable - debug;table composite focus lost. ");
    }

    @Override public void focusGained(FocusEvent ev) {
      System.out.println("SwtTable - debug;table composite focus gained. ");
    }

  };

  TraverseListener traverseListenerTable = new TraverseListener() {

    @Override public void keyTraversed(TraverseEvent ev)
    {
      Debugutil.stop();
      final int keyCode = SwtGralKey.convertFromSwt(ev.keyCode, ev.stateMask, ev.character);
      if(keyCode == KeyCode.tab) { // || keyCode == KeyCode.ctrl+KeyCode.tab) {
      //if(e.character == '\t'){
        //only the tab key should be handled because up and down are received by the key handler
        SwtTable.this.keyPressed(ev);
      }
    }
  };



  /**An instance of this is associated to any cell of the table.
   * The focus gained method is used to set the current cell of the table and to invoke redraw
   * to show the new selection. That focus event is invoked if the mouse button is pressed on that cell.
   */
  private class XXXFocusListenerCell implements FocusListener
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
      SwtTable.this.focusLostTable();
      if(!SwtTable.this.bRedrawPending){
        //System.out.println("SwtTable - cell focus lost;" + (SwtTable.this).outer.toString());
        GralTable.CellData data = (GralTable.CellData)ev.widget.getData();
        Control widgSwt = (Control)ev.widget;
        //widgSwt.setBackground(colorBackSelectNonFocusedSwt);
        //System.out.println("SwtTableCell - focus lost;");
        int iCellLine = data.ixCellLine; //ixLineNew - ixLine1;
        for(int iCellCol = 0; iCellCol < zColumn(); ++iCellCol){
          Text cellSwt = SwtTable.this.cellsSwt[iCellLine][iCellCol];

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
      SwtTable.this.focusGainedTable();
      //System.out.println("SwtTableCell - focus gained;");
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
      this.action.userActionGui(KeyCode.menuEntered, widgg);
    }
  }



  protected SwtKeyListener swtKeyListener = new SwtKeyListener(this.widgg.gralMng()._implListener.gralKeyListener)
  {

    @Override public final boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){
      boolean bDone = true;
      if(KeyCode.isWritingKey(key)){
        //bTextChanged = true;
      }
      if(key == KeyCode.enter) { // && KeyCode.isWritingOrTextNavigationKey(key)){
        bDone = true;
        assert(widgImpl instanceof Text);
        Text widgSwt = (Text)widgImpl;
        setTextToLine(widgSwt);
      } else {
        /*
        boolean bUserOk;
        if(user !=null){
          Point selection = textFieldSwt.getSelection();
          bUserOk = user.userKey(key
              , textFieldSwt.getText()
              , textFieldSwt.getCaretPosition()
              , selection.x, selection.y);
        } else bUserOk = false;
        if(!bUserOk ){
          switch(key){
            case KeyCode.ctrl + 'a': {
              textFieldSwt.selectAll();
            } break;
            default: bDone = false;
          }
        }
        */
      }
      return bDone;
    }
  };



  void stop(){}




  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub

  }




}
