package org.vishia.gral.gridPanel;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Control;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.base.GralDevice;
import org.vishia.gral.cfg.GuiCfgBuilder;
import org.vishia.gral.cfg.GuiCfgData;
import org.vishia.gral.cfg.GuiCfgDesigner;
import org.vishia.gral.cfg.GuiCfgWriter;
import org.vishia.gral.ifc.ColorGui;
import org.vishia.gral.ifc.FileDialogIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.GuiRectangle;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;

/**This is the base class of the GuiPanelMng for several Graphic-Adapters (Swing, SWT etc.). 
 * It contains the independent parts. 
 * The GuiPanelMng is a common approach to work with graphical interface simply, 
 * it is implemented by the several graphic-system-supporting classes
 * <ul>
 * <li>{@link org.vishia.mainGuiSwt.GuiPanelMngSwt}
 * <li>{@link org.vishia.mainGuiSwing.GuiPanelMngSwt}
 * </ul>
 * to offer a unique interface to work with simple graphic applications.
 * <br><br>
 * 
 * @author Hartmut Schorrig
 *
 * @param <WidgetTYPE> The special base type of the composed widgets in the underlying graphic adapter specialization.
 *                     (SWT: Composite)
 */
public abstract class GuiPanelMngBase implements GuiPanelMngBuildIfc, GuiPanelMngWorkingIfc
{
  /**Changes:
   * <ul>
   * <li>2011-08-13 Hartmut chg: New routines for store and calculate the position to regard large widgets.
   * </ul>
   */
  public final static int version = 0x20110813;
  
	/**This class is used for a selection field for file names and pathes. */
  protected class FileSelectInfo
  {
    public List<String> listRecentFiles;
    public String sRootDir;
    public String sLocalDir;
    public String sMask;
    public String sTitle;
    public final FileDialogIfc dialogFile;
    public final WidgetDescriptor dstWidgd;
    
    
    public FileSelectInfo(String sTitle, List<String> listRecentFiles, String startDirMask, WidgetDescriptor dstWidgd)
    { this.listRecentFiles = listRecentFiles;
      this.dstWidgd = dstWidgd;
      this.sTitle = sTitle;
      int posColon = startDirMask.indexOf(':',2);  //regard : after windows drive letter.
      String sLocalDir1;
      if(posColon >=0){
        this.sRootDir = startDirMask.substring(0, posColon) + "/";
        sLocalDir1 = startDirMask.substring(posColon+1);
      } else {
        this.sRootDir = "";
        sLocalDir1 = startDirMask;
      }
      int mode;
      String sMask1;
      int posSlash = sLocalDir1.lastIndexOf('/');
      if(posSlash == sLocalDir1.length()-1){ //last is slash
        mode = FileDialogIfc.directory;
        sMask1 = "";
      } else {
        mode = 0;
        sMask1 = sLocalDir1.substring(posSlash+1);
        if(sMask1.indexOf('*') >=0){ //contains an asterix
          sLocalDir1 = posSlash >=0 ? sLocalDir1.substring(0, posSlash) : "";
        } else {
          sMask1 = null;  //no mask, sLocalDir is a directory.  
        }
      }
      this.sMask = sMask1;
      this.sLocalDir = sLocalDir1;
      this.dialogFile = createFileDialog();
      this.dialogFile.open(sTitle, mode);
    }
    
  }
  
  /**This class holds the informations for 1 widget, which things should be changed.
   * An instance of this is used temporary in a queue.
   */
  public static class GuiChangeReq
  {
    /**The widget where the change should be done. */
    public final WidgetDescriptor widgetDescr;
    
    /**The command which should be done to change. It is one of the static definitions cmd... of this class. */
    public final int cmd;
    
    /**Numeric value describes the position of widget where the change should be done.
     * For example, if the widget is a table, it is either the table line or it is
     * Integer.MAX_VALUE or 0 to designate top or end.
     */
    public final int ident;
    
    /**The textual information which were to be changed or add. */
    public final Object visibleInfo;
    
    public final Object userData;
    
    public GuiChangeReq(WidgetDescriptor widgetDescr, int cmd, int indent, Object visibleInfo, Object userData) 
    { this.widgetDescr = widgetDescr;
      this.cmd = cmd;
      this.ident = indent;
      this.visibleInfo = visibleInfo;
      this.userData = userData;
    }
    
  }
  
  
  
  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  //GuiDialogZbnfControlled dialogZbnfConfigurator;   
  GuiCfgBuilder cfgBuilder;
  
  GuiCfgWriter cfgWriter;
  
  /**The designer is an aggregated part of the PanelManager, but only created if necessary. 
   * TODO check whether it should be disposed to {@link #mngBase} .*/
  protected GuiCfgDesigner designer;
  
  private GuiCfgData cfgData;
  
  public boolean bDesignMode = false;
  

  
  protected boolean bDesignerIsInitialized = false;
  
  final GuiPanelMngBase parent;
  
  /**Base class for managing all panels and related windows.
   * This base class contains all common resources to manage panels and windows.
   */
  final protected GuiMngBase mngBase;

  /**Properties of this Dialog Window. */
  public  final PropertiesGui propertiesGui;

  /**Any kind of TabPanel for this PanelManager TODO make protected
   */
  public TabPanel tabPanel;
  
  /**Index of all input fields to access symbolic for all panels. */
  protected final Map<String, WidgetDescriptor> indexNameWidgets = new TreeMap<String, WidgetDescriptor>();

  /**Index of all input fields to access symbolic. NOTE: The generic type of WidgetDescriptor is unknown,
   * because the set is used independently from the graphic system. */
  protected final Map<String, WidgetDescriptor> showFields = new TreeMap<String, WidgetDescriptor>();

  //private final IndexMultiTable showFieldsM;

  private List<WidgetDescriptor> widgetsInFocus = new LinkedList<WidgetDescriptor>();
  
  protected final LogMessage log;
  
	protected final VariableContainer_ifc variableContainer;
	
	
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   * The values inside the position are positive in any case, so that the calculation of size is simple.
   */
  protected GralPos pos = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**False if the position is given newly. True if it is used. Then the next add-widget invocation 
   * calculates the next position in direction of {@link #pos.dirNext}. */
  protected boolean posUsed;
  
  /**Position for the next widget to store.
   * The Position values may be negative which means measurement from right or bottom.
   */
  protected GralPos posWidget = new GralPos(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  
  
  
  /**Saved last use position. After calling {@link #setPosAndSize_(Control, int, int, int, int)}
   * the xPos and yPos are setted to the next planned position.
   * But, if a new position regarded to the last given one is selected, the previous one is need.
   */
  //protected int xPosPrev, xPosPrevFrac, yPosPrev, yPosPrevFrac;
  
  /**width and height for the next element. If a value */
  //protected int xSize, xSizeFrac, ySize, ySizeFrac;
  
  /**'l' - left 'r'-right, 't' top 'b' bottom. */ 
  //protected char xOrigin = 'l', yOrigin = 'b';
  
  //protected char pos.dirNext = 'r';
  
  /**The width of the last placed element. 
   * It is used to determine a next xPos in horizontal direction. */
  //int xWidth;
  
  /**True if the next element should be placed below the last. */
  protected boolean bBelow, bRigth;

	/**Creates an nee Panel Manager in a new Window.
	 * @param graphicBaseSystem
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static GuiPanelMngBase createWindow(String graphicBaseSystem)
	{ Class<GuiPanelMngBase> mngClass;
		GuiPanelMngBase mng = null;
		String sGraphicBaseSystem = "org.vishia.mainGuiSwt.GuiPanelMngSwt";
		try{ 
			mngClass = (Class<GuiPanelMngBase>) Class.forName(sGraphicBaseSystem);
		} catch(ClassNotFoundException exc){ mngClass = null; }
		
		if(mngClass == null) throw new IllegalArgumentException("Graphic base system not found: " + sGraphicBaseSystem);
		try{ 
			Constructor<GuiPanelMngBase> ctor = mngClass.getConstructor();
			mng = ctor.newInstance();
			//mng = mngClass.newInstance();
		
		} catch(IllegalAccessException exc){ throw new IllegalArgumentException("Graphic base system access error: " + sGraphicBaseSystem);
		} catch(InstantiationException exc){ throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (SecurityException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (NoSuchMethodException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (IllegalArgumentException exc) {throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		} catch (InvocationTargetException exc) { throw new IllegalArgumentException("Graphic base system not able to instanciate: " + sGraphicBaseSystem);
		}
		return mng;
	}
	
	
	/* (non-Javadoc)
	 * @see org.vishia.gral.gridPanel.GuiPanelMngBuildIfc#setPosition(int, int, int, int, char)
	 */
	@Override public void setPositionSize(int line, int column, int height, int width, char direction)
	{ if(line < 0){ line = posUsed? GralPos.next: GralPos.same; }
	  if(column < 0){ column = posUsed? GralPos.next: GralPos.same; }
	  setFinePosition(line, 0, height + GralPos.size, 0, column, 0, width + GralPos.size, 0, 1, direction, pos);
	}

	
  @Override public void setPosition(float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction)
  { setPosition(pos, line, lineEndOrSize, column, columnEndOrSize, origin, direction);
  }

  
  
  
  @Override public void setPosition(GralPos framePos, float line, float lineEndOrSize, float column, float columnEndOrSize
      , int origin, char direction)
  {
    int y1 = (int)(line);
    int y1f = y1 >=0 ? (int)((line - y1)* 10.001F) : (int)((line - y1)* -10.001F);  
    int y2 = (int)(lineEndOrSize);
    int y2f = y2 >=0 ? (int)((lineEndOrSize - y2)* 10.001F) : (int)((lineEndOrSize - y2)* -10.001F);  
    int x1 = (int)(column);
    int x1f = x1 >=0 ? (int)((column - x1)* 10.001F) : (int)((column - x1)* -10.001F);  
    int x2 = (int)(columnEndOrSize);
    int x2f = x2 >=0 ? (int)((columnEndOrSize - x2)* 10.001F) : (int)((columnEndOrSize - x2)* -10.001F); 
    setFinePosition(y1, y1f, y2, y2f, x1, x1f, x2, x2f, origin, direction, framePos);
  }
  
  

	
  /**Sets the position for the next widget to add in the container.
   * Implementation note: This is the core function to calculate positions. It is called from all other ones.
   * @param line y-Position in y-Units, count from top of the box. It is the bottom line of the widget.
   *              It means ypos = 0 is not a proper value. To show a text in the first line, use y=2.
   *              If <0, then the previous position is valid still.
   * @param column x-Position in x-Units, count from left of the box. 
   *              If <0, then the previous position is valid still.
   * @param heigth: The height of the line. If <0, then the param line is the buttom line, 
   *                and (line-height) is the top line. If 0 then the last value of height is not changed. 
   * @param length: The number of columns. If <0, then the param column is the right column, 
   *                and column-length is the left column. If 0 then the last value of length is not changed.
   * @param direction: direction for a next widget, use 'r', 'l', 'u', 'd' for right, left, up, down                
   */
  @Override public void setFinePosition(int line, int yPosFrac, int ye, int yef
      , int column, int xPosFrac, int xe, int xef, int origin, char direction, GralPos frame)
  {
    //Inner class to calculate for x and y.   
    class Calc{
      int p, pf, pe, pef;
      float pd;
      int pDir;  //0 for up or left, 1 for down or right, -1 else 
      int pOrigin;
      
      void calc(int z, int zf, int ze, int zef)
      {
        final boolean bxSizeNeg;
        final boolean bxSize =  ze > (GralPos.size - GralPos.sizeRange_)
                            && ze < (GralPos.size + GralPos.sizeRange_);
        if(bxSize){ 
          ze -= GralPos.size; //may be negative!
          bxSizeNeg = ze < 0;
          if(bxSizeNeg){ 
            ze =  -ze;
            if(pOrigin <0){ pOrigin = 2; }
          } else {
            if(pOrigin <0){ pOrigin = 0; }
          }
        } else { bxSizeNeg = false; }
        final boolean bxSame = z > (GralPos.same - GralPos.sizeRange_)
                            && z < (GralPos.same + GralPos.sizeRange_);
        if(bxSame){ 
          z -= GralPos.same; //may be negative!
        }  
        final boolean bxSameEnd = ze > (GralPos.same - GralPos.sizeRange_)
                            && ze < (GralPos.same + GralPos.sizeRange_);
        if(bxSameEnd){ 
          ze -= GralPos.same; //may be negative!
        }  
        final boolean bColumnFromRight = z < 0;
        if(bColumnFromRight){ z = - z; } //use positive values anytime
        posUsed = false;
        if(bxSame){
          //don't change column
          if(bxSameEnd){
            pe += ze; pef += zef;
            if(pef >=10){ pef -=10; pe+=1; }
          } else if(bxSize){
            //The size is changed but the same column
            if(bxSizeNeg){
              p = pe - ze; pf = pef - zef;
              if(pf <0){ p -=1; pf +=10; }
            } else {
              pe = p + ze; pef = pf + zef;
              if(pef >=10){ pe +=1; pef -=10; }
            }
          } else { //an end position is given
            pe = ze; pef = zef;
          }
          p += z; pf += zf; 
          if(pf >=10){ pf -=10; p+=1; }
        } else if(z == GralPos.next && pDir==1 || z == GralPos.nextBlock){
          //calculate next x
          if(bxSameEnd || ze == GralPos.next || ze == GralPos.nextBlock){ 
            //calculate next position, don't change end column
            int xd = (int)pd;
            zef = (int)((pd - xd)* 10.001F) + pef;
            
            p = pe; pf = pef;
            if(zef >= 10){ zef -=10; xd +=1; }
            pe = p + xd;
            pef = zef;
          } else if(bxSize){
            //calculate next position, size is given:
            p = pe; pf = pef;  //set start to previous end.
            zef += pef; if(zef >=10){ zef -=10; ze +=1; }
            pe = p + ze; pef = zef;  //set end to newstart + size
          } else { 
            //calculate next position, end position is given:
            p = pe; pf = pef;
            pe = ze; pef = zef;
          }
        } else { //position z is given:
          if(z == GralPos.next){  //next is given, but not for this coordinate: 
            if(bxSizeNeg){
              z = pe; zf = pef;     //use the actual value.
            } else {
              z = p; zf = pf;       //use the actual value.
            }
          }
          //position is given or next is set but not in this direction:
          if(bxSameEnd || ze == GralPos.next || ze == GralPos.nextBlock){ 
            //don't change end position
            p = z; pf = zf;
          } else if(bxSize){
            if(bxSizeNeg){ //the given position is the right or button one:
              p = z - ze; pf = zf - zef;
              if(pf < 0){ pf +=10; p -=1; }
              pe = z; pef = zf;  //the end position is the given one.
            } else { //the given position is the left one:
              p = z; pf = zf; 
              pe = z + ze; pef = zf + zef;
              if(pef >=10){ pef -=10; pe +=1; }
            } 
          } else { //column and end column is given:
            p = z; pf = zf;      //Note: values may be negative then calculate pixel from right or bottom border. 
            pe = ze; pef = zef;
          }
        }
        if(pOrigin <0){ pOrigin = 0; } //set default if not determined. 
      }
    }
    if(frame == null){ frame = pos; }
    Calc calc = new Calc();
    if(origin >0 && origin <=9){
      int yOrigin = (origin-1) /3;
      int xOrigin = origin - yOrigin -1; //0..2
      pos.xOrigin = "lmr".charAt(xOrigin);
      pos.yOrigin = "tmb".charAt(yOrigin);
    }
    //calculate y
    calc.p = frame.y; calc.pe = frame.yEnd; calc.pf = frame.yFrac; calc.pef = frame.yEndFrac;
    calc.pd = frame.height(); calc.pDir = "ud".indexOf(frame.dirNext); calc.pOrigin = "tmb".indexOf(frame.yOrigin);
    calc.calc(line, yPosFrac, ye, yef);
    pos.y = calc.p; pos.yEnd = calc.pe; pos.yFrac = calc.pf; pos.yEndFrac = calc.pef;
    pos.yOrigin = "tmb".charAt(calc.pOrigin); if(calc.pDir >=0){ pos.dirNext = calc.pDir == 0 ? 'u': 'd'; }
    //calculate x
    calc.p = frame.x; calc.pe = frame.xEnd; calc.pf = frame.xFrac; calc.pef = frame.xEndFrac;
    calc.pd = frame.width(); calc.pDir = "lr".indexOf(frame.dirNext); calc.pOrigin = "lmr".indexOf(frame.xOrigin);
    calc.calc(column, xPosFrac, xe, xef);
    pos.x = calc.p; pos.xEnd = calc.pe; pos.xFrac = calc.pf; pos.xEndFrac = calc.pef;
    pos.xOrigin = "lmr".charAt(calc.pOrigin); if(calc.pDir >=0){ pos.dirNext = calc.pDir == 0 ? 'l': 'r'; }
    
    /*
    if(column >=0){ 
      this.pos.x = column;
      this.pos.xFrac = xPosFrac;
      this.pos.xEnd = (column + width);
    } else {
      //use the same pos.x as before adding the last Component, 
      //because a new yPos is given.
      if(pos.dirNext == 'r') { 
        int xSize = pos.xEnd - pos.x;
        pos.x = pos.xEnd;
        pos.xEnd = pos.x + xSize;
      }
    }
    if(line >=0){ 
      if(height >=0){
        this.pos.y = (line); pos.yFrac = yPosFrac;
        int yEndFrac = yPosFrac + ySizeFrac;
        if(yEndFrac >=10){ yEndFrac -=10; height +=1; }
        this.pos.yEnd = (line + height);
        pos.yEndFrac = yEndFrac;
        pos.yOrigin = 't';
      } else {
        this.pos.yEnd = (line); pos.yEndFrac = yPosFrac;
        int yFrac = yPosFrac - ySizeFrac;
        if(yFrac <0){ yFrac +=10; height -=1; }
        this.pos.y = (line + height);  //less then yEnd because height is negative.
        pos.yFrac = yFrac;
        pos.yOrigin = 'b';
      }
        
    } else {
      //use the same yPos as before adding the last Component, 
      //because a new xPos may be given.
      if(pos.dirNext == 'd') { 
        int ySize = pos.yEnd - pos.y;
        pos.y = pos.yEnd;
        pos.yEnd = pos.y + ySize;
      }
    }
    if(height <0){
      //yPosPrev = (yPos -= height);
    }
    if(width <0){
      //xPosPrev = (xPos -= width);
    }
    */
    if("rlud".indexOf(direction)>=0 ){
      pos.dirNext = direction;
    }
  }
  
  
  
  
  public void setSize(int height, int ySizeFrac, int width, int xSizeFrac)
  {
    posUsed = false;
    if(height !=0){
      //ySize = height >0 ? height : -height;
      //this.ySizeFrac = ySizeFrac;
    }
    if(width !=0){
      //xSize = width >0 ? width: -width;
      //this.xSizeFrac = xSizeFrac;
    }
    if(height >0){ pos.yOrigin = 't'; }
    else if(height < 0){ pos.yOrigin = 'b'; }
    else; //let it unchanged if height == 0
    if(width >0){ pos.xOrigin = 'l'; }
    else if(width < 0){ pos.xOrigin = 'r'; }
    else; //let it unchanged if width == 0
  }
  
  
  void setSize(float height, float width)
  { 
    int y2 = (int)(height);
    int y2f = y2 >=0 ? (int)((height - y2)* 10.001F) : (int)((height - y2)* -10.001F);  
    int x2 = (int)(width);
    int x2f = x2 >=0 ? (int)((width - x2)* 10.001F) : (int)((width - x2)* -10.001F); 
    setFinePosition(GralPos.next, 0,  y2 + GralPos.size, y2f, GralPos.next, 0, x2 + GralPos.size, x2f, 0, pos.dirNext, pos);
  }
  
  
  
  /**Positions the next widget below to the previous one. */
  public void setNextPositionX()
  { //xPos = xWidth; 
  }
  
  /**Positions the next widget on the right next to the previous one. */
  public void setNextPositionY()
  { bBelow = true;
  }
  
  /**Sets the position to the next adequate the {@link #pos.dirNext}. */
  public void setNextPosition()
  {
    float dx3 = pos.width();
    float dy3 = pos.height();
    int dx = (int)dx3;
    int dxf = (int)((dx3 - dx) * 10.001F) + pos.xEndFrac;
    if(dxf >= 10){ dxf -=10; dx +=1; }
    int dy = (int)dy3;
    int dyf = (int)((dy3 - dy) * 10.001F) + pos.yEndFrac;
    if(dyf >= 10){ dyf -=10; dy +=1; }
    switch(pos.dirNext){
    case 'r': pos.x = pos.xEnd; pos.xFrac = pos.xEndFrac; pos.xEnd = pos.x + dx; pos.xEndFrac = dxf; break;
    case 'd': pos.y = pos.yEnd; pos.yFrac = pos.yEndFrac; pos.yEnd = pos.y + dy; pos.yEndFrac = dyf; break;
    }
  }
  
  
  /**Returns the width (number of grid step horizontal) of the last element.
   * @return Difference between current auto-position and last pos.
   */
  public int xxxgetWidthLast(){ return 0; }
  
	
  @Override public void xxxsetPositionInPanel(float line, float column, float lineEnd, float columnEnd, char direction)
  {
    posUsed = false;
    int y1 = (int)(line);
    int y1f = y1 >=0 ? (int)((line - y1)* 10.001F) : (int)((line - y1)* -10.001F);  
    int y2 = (int)(lineEnd);
    int y2f = y2 >=0 ? (int)((lineEnd - y2)* 10.001F) : (int)((lineEnd - y2)* -10.001F);  
    int x1 = (int)(column);
    int x1f = x1 >=0 ? (int)((column - x1)* 10.001F) : (int)((column - x1)* -10.001F);  
    int x2 = (int)(columnEnd);
    int x2f = x2 >=0 ? (int)((columnEnd - x2)* 10.001F) : (int)((columnEnd - x2)* -10.001F); 
    pos.y = y1;
    pos.yFrac = y1f;
    pos.yEnd = y2;
    pos.yEndFrac = y2f;
    pos.x = x1;
    pos.xFrac = x1f;
    pos.xEnd = x2;
    pos.xEndFrac = x2f;
    
    if("rlud".indexOf(direction)>=0 ){
      pos.dirNext = direction;
    }
  }

	
  @Override public GralPos getPositionInPanel(){ return pos; }
  
  
  void setSizeFromPositionInPanel()
  {
    
  }
  
  
  
	
  /**Map of all panels. A panel may be a dialog box etc. */
  protected final Map<String,PanelContent> panels = new TreeMap<String,PanelContent>();
  
  public PanelContent currPanel;
  
  protected String sCurrPanel;
  
  protected WidgetDescriptor lastClickedWidgetInfo;
  

	@Override public Queue<WidgetDescriptor> getListCurrWidgets(){ return currPanel.widgetList; }
	
  /**Index of all user actions, which are able to use in Button etc. 
   * The user action "showWidgetInfos" defined here is added initially.
   * Some more user-actions can be add calling {@link #registerUserAction(String, UserActionGui)}. 
   * */
  protected final Map<String, UserActionGui> userActions = new TreeMap<String, UserActionGui>();
  //private final Map<String, ButtonUserAction> userActions = new TreeMap<String, ButtonUserAction>();
  
  /**Index of all Tables, which are representable. */
  //private final Map<String, Table> userTableAccesses = new TreeMap<String, Table>();
  
	
	
  public GuiPanelMngBase(GralDevice gralDevice, GuiPanelMngBase parent
      , PropertiesGui props
      , VariableContainer_ifc variableContainer, LogMessage log)
	{ this.parent = parent;
	  if(parent == null){
	    mngBase = new GuiMngBase(gralDevice);
	  } else {
	    mngBase = parent.mngBase;
	  }
	  this.propertiesGui = props;
		this.log = log;
		this.variableContainer = variableContainer;
		userActions.put("showWidgetInfos", this.actionShowWidgetInfos);
	}

  
  @Override public WidgetDescriptor getWidget(String name)
  { return indexNameWidgets.get(name);
  }
  
  
  
  @Override public void buildCfg(GuiCfgData data, File fileCfg) //GuiCfgBuilder cfgBuilder)
  {
    this.cfgData = data;
    File currentDir = fileCfg.getParentFile();
    this.cfgBuilder = new GuiCfgBuilder(cfgData, this, currentDir);
    cfgBuilder.buildGui(log, 0);
    this.designer = new GuiCfgDesigner(cfgBuilder, this, log);  
    this.bDesignMode = true;
  }

  /**Sets or resets the design mode. The design mode allows to change the content.
   * @param mode
   */
  @Override public void setDesignMode(boolean mode){ this.bDesignMode = mode; }
  
  /**Saves the given configuration.
   * @param dest
   * @return
   */
  @Override public String saveCfg(Writer dest)
  { cfgWriter = new GuiCfgWriter(log);
    String sError = cfgWriter.saveCfg(dest, cfgData);
    return sError;
  }
  

  
  
	public void setLastClickedWidgetInfo(WidgetDescriptor lastClickedWidgetInfo)
	{
		this.lastClickedWidgetInfo = lastClickedWidgetInfo;
	}
  /**Registers all user actions, which are able to use in Button etc.
   * The name is related with the <code>userAction=</code> designation in the configuration String.
   * @param name
   * @param action
   */
  @Override public void registerUserAction(String name, UserActionGui action)
  {
    userActions.put(name, action);
  }
  
  @Override public UserActionGui getRegisteredUserAction(String name)
  {
    return userActions.get(name);
  }
  
  
  @Override public void registerWidget(WidgetDescriptor widgd)
  {
    if(pos.x < 0 || pos.xEnd <= 0 || pos.y< 0 || pos.yEnd <=0){ 
      //only widgets with size from right TODO percent size too.
      widgd.pos = pos.clone();
      //widgd.pos.set(pos);
      currPanel.widgetsToResize.add(widgd);
    }
    indexNameWidgets.put(widgd.name, widgd);
      
  }
  
  @Override public boolean setFocus(WidgetDescriptor widgd)
  {
    return widgd.widget.setFocus();
  }
  

  @Override public void notifyFocus(WidgetDescriptor widgd)
  {
    synchronized(widgetsInFocus){
      widgetsInFocus.remove(widgd);  //remove it anywhere inside
      widgetsInFocus.add(0, widgd);     //add at start.
    }
  }
  
  @Override public WidgetDescriptor getWidgetInFocus(){ return widgetsInFocus.get(0); }
  
  @Override public List<WidgetDescriptor> getWidgetsInFocus(){ return widgetsInFocus; }
  
  @Override public int getColorValue(String sColorName){ return propertiesGui.getColorValue(sColorName); }

  @Override public ColorGui getColor(String sColorName){ return propertiesGui.color(getColorValue(sColorName)); }



  

	UserActionGui actionShowWidgetInfos = new UserActionGui()
	{

		@Override public void userActionGui(
			String sCmd
		, WidgetDescriptor infos, Object... params
		)
		{ 
			if(lastClickedWidgetInfo !=null){
				log.sendMsg(Report.info, "widget %s, datapath=%s"
					, GuiPanelMngBase.this.lastClickedWidgetInfo.name
					, GuiPanelMngBase.this.lastClickedWidgetInfo.getDataPath());
			} else {
				log.sendMsg(0, "widgetInfo - no widget selected");
			}
		}
		
	};
	
	
  
  

	
  
  /**Calculates the position and size of a widget
   * @param posWidget The position.
   * @param widthParentPixel The size of the panel, where the widget is member of
   * @param heightParentPixel The size of the panel, where the widget is member of
   * @return A rectangle for setBounds.
   */
  protected GuiRectangle calcWidgetPosAndSize(GralPos posWidget, 
      int widthParentPixel, int heightParentPixel,
      int widthWidgetNat, int heightWidgetNat)
  {
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    final int x1,y1, x2, y2;
    x1 = xPixelUnit * posWidget.x + propertiesGui.xPixelFrac(posWidget.xFrac)  //negative if from right
       + (posWidget.x < 0 ? widthParentPixel : 0);  //from right
    y1 = yPixelUnit * posWidget.y + propertiesGui.yPixelFrac(posWidget.yFrac)  //negative if from right
       + (posWidget.y < 0 ? heightParentPixel : 0);  //from right
    if(posWidget.xEnd == GralPos.useNatSize){
      x2 = x1 + widthWidgetNat; 
    } else {
      x2 = xPixelUnit * posWidget.xEnd + propertiesGui.xPixelFrac(posWidget.xEndFrac)  //negative if from right
         + (posWidget.xEnd < 0 || posWidget.xEnd == 0 && posWidget.xEndFrac == 0 ? widthParentPixel : 0);  //from right
    }
    if(posWidget.xEnd == GralPos.useNatSize){
      y2 = y1 + heightWidgetNat; 
    } else {
      y2 = yPixelUnit * posWidget.yEnd + propertiesGui.yPixelFrac(posWidget.yEndFrac)  //negative if from right
         + (posWidget.yEnd < 0  || posWidget.yEnd == 0 && posWidget.yEndFrac == 0 ? heightParentPixel : 0);  //from right
    }
    GuiRectangle rectangle = new GuiRectangle(x1, y1, x2-x1, y2-y1);
    return rectangle;
  }
  
  
  
  
  @Override public WidgetDescriptor addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, char promptStylePosition)
  { //int xSize1 = xSize;
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralPos posAll = getPositionInPanel().clone(); //saved whole position.
    //reduce the length of the text field:
    setPosition(GralPos.same, GralPos.same, GralPos.same, GralPos.same -2.0F, 1, 'r');
    
    //xSize -= ySize;
    WidgetDescriptor widgd = addTextField(name, true, prompt, promptStylePosition );
    setSize(posAll.height(), 2.0F);
    //xPos += xSize;
    //xSize = ySize;
    WidgetDescriptor widgdSelect = addButton(name + "<", actionFileSelect, "", null, null, "<");
    FileSelectInfo fileSelectInfo = new FileSelectInfo(name, listRecentFiles, startDirMask, widgd);
    widgdSelect.setContentInfo(fileSelectInfo); 
    //xSize = xSize1;
    pos = posAll;  //the saved position.
    return widgd;
  }

  
  
  UserActionGui actionFileSelect = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    {
      FileSelectInfo fileSelectInfo = (FileSelectInfo)infos.getContentInfo();
      if(fileSelectInfo.listRecentFiles !=null){
        stop();
      } else {
        fileSelectInfo.dialogFile.show(fileSelectInfo.sRootDir, fileSelectInfo.sLocalDir
          , fileSelectInfo.sMask, fileSelectInfo.sTitle);
        String fileSelect = fileSelectInfo.dialogFile.getSelection(); 
        if(fileSelect !=null){
          fileSelectInfo.dstWidgd.setValue(cmdSet, 0, fileSelect);
        }
      }
      
    }
    
  };
  
  
  

  
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void pressedLeftMouseDownForDesign(WidgetDescriptor widgd, GuiRectangle xy)
  { designer.pressedLeftMouseDownForDesign(widgd, xy);
  }
  
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void releaseLeftMouseForDesign(WidgetDescriptor widgd, GuiRectangle xy, boolean bCopy)
  { designer.releaseLeftMouseForDesign(widgd, xy, bCopy);
  }
  
  /**It will be called only at the GUI-implementation level. TODO protected and delegation.
   * @param widgd
   * @param xy
   */
  public void pressedRightMouseDownForDesign(WidgetDescriptor widgd, GuiRectangle xy)
  { designer.pressedRightMouseDownForDesign(widgd, xy);
  }
  
  
  
  void stop(){}
	

	
}
