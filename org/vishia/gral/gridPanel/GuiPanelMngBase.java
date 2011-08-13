package org.vishia.gral.gridPanel;

import java.io.File;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
  
  protected final LogMessage log;
  
	protected final VariableContainer_ifc variableContainer;
	
	
	
  /**Position of the next widget to add. If some widgets are added one after another, 
   * it is similar like a flow-layout.
   * But the position can be set.
   */
  protected GralGridPosition pos = new GralGridPosition(); //xPos, xPosFrac =0, xPosEnd, xPosEndFrac, yPos, yPosEnd, yPosFrac, yPosEndFrac =0;
  
  /**Saved last use position. After calling {@link #setPosAndSize_(Control, int, int, int, int)}
   * the xPos and yPos are setted to the next planned position.
   * But, if a new position regarded to the last given one is selected, the previous one is need.
   */
  protected int xPosPrev, xPosPrevFrac, yPosPrev, yPosPrevFrac;
  
  /**width and height for the next element. If a value */
  protected int xSize, xSizeFrac, ySize, ySizeFrac;
  
  /**'l' - left 'r'-right, 't' top 'b' bottom. */ 
  protected char xOrigin = 'l', yOrigin = 'b';
  
  protected char directionOfNextElement = 'r';
  
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
	
	
	@Override public void setPosition(int line, int column, int height, int length, char direction)
	{ setFinePosition(line, 0, column, 0, height, 0, length, 0, direction);
	}

	
  /**Sets the position for the next widget to add in the container.
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
  @Override public void setFinePosition(int line, int yPosFrac, int column, int xPosFrac, int height, int ySizeFrac, int width, int xSizeFrac, char direction)
  {
    pos.xEnd = pos.yEnd = 0;  //they are disabled.
    if("rlud".indexOf(direction)>=0 ){
      directionOfNextElement = direction;
    }
    setSize(height, ySizeFrac, width, xSizeFrac);
    if(column >=0 || pos.xFrac >0){ 
      this.pos.x = xPosPrev = column;
      this.pos.xFrac = pos.xFrac;
    } else {
      //use the same pos.x as before adding the last Component, 
      //because a new yPos is given.
      column = xPosPrev; 
      pos.xFrac = xPosPrevFrac;
    }
    if(line >=0 || yPosFrac >0){ 
      this.pos.y = yPosPrev = line;
      this.pos.yFrac = yPosFrac;
    } else {
      //use the same yPos as before adding the last Component, 
      //because a new xPos may be given.
      line = yPosPrev; 
      yPosFrac = yPosPrevFrac;
    }
    if(height <0){
      //yPosPrev = (yPos -= height);
    }
    if(width <0){
      //xPosPrev = (xPos -= width);
    }
      
    this.bBelow = false; //because yPos is set.
    this.bRigth = true;
  }
  
  
  public void setSize(int height, int ySizeFrac, int width, int xSizeFrac)
  {
    if(height !=0){
      ySize = height >0 ? height : -height;
      this.ySizeFrac = ySizeFrac;
    }
    if(width !=0){
      xSize = width >0 ? width: -width;
      this.xSizeFrac = xSizeFrac;
    }
    if(height >0){ yOrigin = 't'; }
    else if(height < 0){ yOrigin = 'b'; }
    else; //let it unchanged if height == 0
    if(width >0){ xOrigin = 'l'; }
    else if(width < 0){ xOrigin = 'r'; }
    else; //let it unchanged if width == 0
  }
  
  
  /**Positions the next widget below to the previous one. */
  public void setNextPositionX()
  { //xPos = xWidth; 
  }
  
  /**Positions the next widget on the right next to the previous one. */
  public void setNextPositionY()
  { bBelow = true;
  }
  
  /**Returns the width (number of grid step horizontal) of the last element.
   * @return Difference between current auto-position and last pos.
   */
  public int xxxgetWidthLast(){ return 0; }
  
	
  @Override public void setPositionInPanel(float line, float column, float lineEnd, float columnEnd, char direction)
  {
    xSize = Integer.MIN_VALUE+1;  //designate, use xEnd, yEnd.
    pos.y = (int)(line - (line < 0 ? 0.9f : 0));  //they are used.
    pos.yFrac = (int)(10*(line - pos.y));  
    pos.x = (int)(column - (column < 0 ? 0.9f : 0));  //may be negative 
    pos.xFrac = (int)(10*(column - pos.x));  
    
    pos.yEnd = (int)(lineEnd - (lineEnd < 0 ? 0.9f : 0));  //they are used.
    pos.yEndFrac = (int)(10*(lineEnd - pos.yEnd));  
    pos.xEnd = (int)(columnEnd - (columnEnd < 0 ? 0.9f : 0));  //may be negative 
    pos.xEndFrac = (int)(10*(columnEnd - pos.xEnd));  
    
    if("rlud".indexOf(direction)>=0 ){
      directionOfNextElement = direction;
    }
    this.bBelow = false; //because yPos is set.
    this.bRigth = true;
    
  }

	
  
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

  
  @Override public void buildCfg(GuiCfgData data, File fileCfg) //GuiCfgBuilder cfgBuilder)
  {
    this.cfgData = data;
    File currentDir = fileCfg.getParentFile();
    this.cfgBuilder = new GuiCfgBuilder(cfgData, this, currentDir);
    cfgBuilder.buildGui(log, 0);
    this.designer = new GuiCfgDesigner(cfgBuilder, this, log);  ///
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
  
  
  public void setWidgetToResize(WidgetDescriptor widgd)
  {
    if(  xSize == Integer.MIN_VALUE+1 
      && (pos.x * pos.xEnd < 0 || pos.y * pos.yEnd <0)){ //TRICKY: different sign vor x and xEnd
      widgd.pos = pos.clone();
      //widgd.pos.set(pos);
      currPanel.widgetsToResize.add(widgd);
    }
      
  }
  

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
	
	
  protected GuiRectangle getRectangleBounds(int dyDefault, int dxDefault)
  { return calcPosAndSize(pos.y, pos.yFrac, pos.x, pos.xFrac
      , this.ySize, ySizeFrac, this.xSize, xSizeFrac, dyDefault, dxDefault);
  }
  
  

	
  /**
   * @param line
   * @param yPosFrac
   * @param column
   * @param xPosFrac
   * @param dy
   * @param ySizeFrac
   * @param dx
   * @param xSizeFrac
   * @param dyPixelDefault Value used if dy == Integer.MAXVALUE 
   * @param dxPixelDefault
   * @return
   */
  protected GuiRectangle calcPosAndSize(int line, int yPosFrac, int column, int xPosFrac
    , int dy, int ySizeFrac, int dx, int xSizeFrac
    , int dyPixelDefault, int dxPixelDefault
    )
  {
    if(line == 19 && yPosFrac == 5)
      stop();
    //use values from class if parameter are non-valid.
    if(line <=0){ line = this.pos.y; yPosFrac = this.pos.yFrac; }
    if(column <=0){ column = this.pos.x; xPosFrac = this.pos.xFrac; }
    if(dy <=0){ dy = this.ySize; ySizeFrac = this.ySizeFrac; }
    if(dx <=0){ dx = this.xSize; xSizeFrac = this.xSizeFrac; }
    //
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    int xPixelSize, yPixelSize;  
    if(dx == Integer.MAX_VALUE){ xPixelSize = dxPixelDefault; }
    else if(dx == Integer.MIN_VALUE){ xPixelSize = dxPixelDefault; }
    else { xPixelSize = xPixelUnit * dx + propertiesGui.xPixelFrac(xSizeFrac) -2 ; }
    if(dy == Integer.MAX_VALUE){ yPixelSize = dyPixelDefault; }
    else if(dy == Integer.MIN_VALUE){ yPixelSize = dyPixelDefault; }
    else { yPixelSize = yPixelUnit * dy + propertiesGui.yPixelFrac(ySizeFrac) -2;}
    
    int xPixel = (int)(column * xPixelUnit) + propertiesGui.xPixelFrac(xPosFrac) +1;
    int yPixel = (int)(line * yPixelUnit) + propertiesGui.yPixelFrac(yPosFrac) +1;
    if(yOrigin == 'b'){
      yPixel -= yPixelSize +1; //line is left bottom, yPixel is left top.
    }
    if(xOrigin == 'r'){
      xPixel -= xPixelSize +1; //yPos is left bottom, yPixel is left top.
    }
    if(yPixel < 1){ yPixel = 1; }
    if(xPixel < 1){ xPixel = 1; }
    GuiRectangle rectangle = new GuiRectangle(xPixel, yPixel, xPixelSize, yPixelSize);
    xPosPrev = pos.x;    //save to support access to the last positions.
    yPosPrev = pos.y;
    //set the next planned position:
    switch(directionOfNextElement){
    case 'r': pos.x += xSize; break;
    case 'l': pos.x -= xSize; break;
    case 'u': pos.y -= ySize; break;
    case 'd': pos.y += ySize; break;
    }
    return rectangle;
  }
  
  
  /**Calculates the position and size of a widget
   * @param posWidget The position.
   * @param widthParentPixel The size of the panel, where the widget is member of
   * @param heightParentPixel The size of the panel, where the widget is member of
   * @return A rectangle for setBounds.
   */
  protected GuiRectangle calcWidgetPosAndSize(GralGridPosition posWidget, int widthParentPixel, int heightParentPixel)
  {
    int xPixelUnit = propertiesGui.xPixelUnit();
    int yPixelUnit = propertiesGui.yPixelUnit();
    //calculate pixel
    final int x1,y1, x2, y2;
    x1 = xPixelUnit * posWidget.x + propertiesGui.xPixelFrac(posWidget.xFrac)  //negative if from right
       + (posWidget.x < 0 ? widthParentPixel : 0);  //from right
    y1 = yPixelUnit * posWidget.y + propertiesGui.yPixelFrac(posWidget.yFrac)  //negative if from right
       + (posWidget.y < 0 ? heightParentPixel : 0);  //from right
    x2 = xPixelUnit * posWidget.xEnd + propertiesGui.xPixelFrac(posWidget.xEndFrac)  //negative if from right
       + (posWidget.xEnd < 0 ? widthParentPixel : 0);  //from right
    y2 = yPixelUnit * posWidget.yEnd + propertiesGui.yPixelFrac(posWidget.yEndFrac)  //negative if from right
       + (posWidget.yEnd < 0 ? heightParentPixel : 0);  //from right
    
    GuiRectangle rectangle = new GuiRectangle(x1, y1, x2-x1, y2-y1);
    return rectangle;
  }
  
  
  
  
  @Override public WidgetDescriptor addFileSelectField(String name, List<String> listRecentFiles, String startDirMask, String prompt, char promptStylePosition)
  { int xPos1 = pos.x;
    int xSize1 = xSize;
    //reduce the length of the text field:
    xSize -= ySize;
    WidgetDescriptor widgd = addTextField(name, true, prompt, promptStylePosition );
    //xPos += xSize;
    xSize = ySize;
    WidgetDescriptor widgdSelect = addButton(name + "<", actionFileSelect, "", null, null, "<");
    FileSelectInfo fileSelectInfo = new FileSelectInfo(name, listRecentFiles, startDirMask, widgd);
    widgdSelect.setContentInfo(fileSelectInfo); 
    xSize = xSize1;
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
