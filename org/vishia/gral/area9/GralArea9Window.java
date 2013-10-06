package org.vishia.gral.area9;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;

/**This class presents a Window appearance with up to 9 areas.
 * @author hartmut
 *
 */
public class GralArea9Window implements GralArea9_ifc
{
  
  /**Version and History
   * <ul>
   * <li>2012-05-17 Hartmut new: {@link #setMinMaxSizeArea(String, int, int, int, int)}: The size can be given 
   *   with gral units, as min, max or absolute (min, max are equal). The size of a area border can be changed on runtime.
   * <li>2012-04-22 Hartmut new: {@link #setFullScreen(boolean)}
   * <li>2011-12-26 Hartmut chg: The help window is a html info box now. New method {@link #setHelpUrl(String)}
   *   supports context sensitive help.
   * <li>2011-11-12 Hartmut new: The SubWindow for Help, About, InfoBox and InfoLog are created here (InfoLog: TODO).
   *   This windows can be opened in demand.
   * <li>2011-11-12 Hartmut chg: All menu items should be prepared in the users application now. The items for help and about
   *   are supported with actions by {@link #getActionAbout()} and {@link #getActionAbout()}. This actions
   *   are the standard behavior and opens the sub-windows only. But the user can install a more complex functionality
   *   for example writing a context sensitive help.
   * <li>2011-11-12 Harmut chg {@link #initGraphic} was the old initOutputArea, but it does more as only the output area. 
   *   Now creation of all necessities of Graphic in this functionality.
   *          
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
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20120303;
  
  public final MainCmd mainCmd;
  
  //protected final GralWindowMng gralDevice;
  
  protected final GralWindow window;
  
  /**Area settings for output. */
  protected String outputArea;

  protected GralPanelContent outputPanel;
  
  /**A Text box which is used for common output. It may be edit-able. 
   * This output box is created initially. It can be used to show error messages while starting the application
   * if other GUI-elements are not yet present. It can be used generally to show errors, infos and logs
   * adequate to a console output.*/
  public GralTextBox outputBox;
  
  public GralInfoBox infoHelp, infoAbout, infoBox, infoLog;
  
  /** Current Directory for file choosing. */
  protected File currentDirectory = null;
  
  protected GralUserAction actionFile;
  
  /**Set on call of {@link #setStandardMenus(File)} to add in in the graphic thread. */
  protected boolean bSetStandardMenus;
  
  protected Queue<String> outputTexts = new ConcurrentLinkedQueue<String>();
  
  
  /**All main components of the Display in each FrameArea. */
  protected GralPanelContent[][] componentFrameArea = new GralPanelContent[3][3];

  
  /**A little control to capture the mouse position for movement of area borders. */
  //private GralWidgetImplWrapper[] yAreaMover = new GralWidgetImplWrapper[2];
  

  
  /**Position of the FrameArea borders in percent. 
   * [0] is always 0, [1] and [2] are the given borders, [3] is always 100.
   * It is because lower and higher bound should be accessed always without tests.
   * Use area +1, because it is a Off-by-one problem */
  protected byte xpFrameArea[] = new byte[4],
               ypFrameArea[] = new byte[4];
  
  /**spread of each frame area in x direction.
   * It it is -1, than the area is occupied by another area.
   * If it is 0, the area is free.
   * 1..3 are the number of areas in horizontal direction.
   */
  protected byte[][][] dxyFrameArea = new byte[3][3][2]; 
  
  /**requested minimal size of each window area in GralPos units. If the minimal value is 0, 
   * the size is calculated from percent always. If the minimal value of percent calculation is lesser
   * than this value, this value is used. Note that a given minimal value of all areas cannot required
   * if the window's size is lesser. Then the middle area will 
   */
  private final short[] xMinGralSize = new short[3], yMinGralSize = new short[3]; 
  
  /**requested maximal size of each window area in GralPos units. If the maximal value is greater than 
   * the size calculated from percent always, the percent value is used. 
   * Note that a given maximal value of all areas cannot regarded
   * if the window's size is greater. Then the middle area will be presented  
   */
  private final short[] xMaxGralSize = new short[3], yMaxGralSize = new short[3];
  
  /**Calculated borders of areas. 
   * [0] is always 0, [1] and [2] are the given borders, [3] is always 100.
   * It is because lower and higher bound should be accessed always without tests.
   * Use area +1, because it is a Off-by-one problem */
  private final int[] xPixArea = new int[4], yPixArea = new int[4];
  
  /**Number of pixels per percent unit of size, set if {@link #validateFrameAreas()} was called. */
  protected float pixelPerXpercent = 1, pixelPerYpercent =1;  
  
  /** The interface to the application. */
  //private final MainApplicationWin_ifc application;
  
  /**The id of the thread, which created the display. 
   * It is to check whether gui-commands should be queued or not. */
  //protected long idThreadGui;
  
  final protected StringBuffer XXXsbWriteInfo = new StringBuffer(1000);  //max. 1 line!
  
  /** If it is set, the writeInfo is redirected to this.*/
  protected GralTextBox_ifc textAreaOutput = null;
  


  /**Sets the output window to a defined area. .
   * Adds the edit-menu too. 
   * @param xArea 1 to 3 for left, middle, right, See {@link #setFrameAreaBorders(int, int, int, int)}
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   */
  public void XXXsetOutputArea(String area){
    outputArea = area;
  }

  //private GuiMainAreaBase(MainCmd mainCmd){ this.mainCmd = mainCmd; }

  /**
   * @param mainCmd Aggregation to a MainCmd implemenation.
   * @param guiDevice The window manager
   * @param window The window itself. It doesn't be the primary window but a sub window too.
   */
  public GralArea9Window(MainCmd mainCmd, GralWindow window)
  {
    super();
    this.mainCmd = mainCmd;
    //this.gralDevice = guiDevice;
    this.window = window;
    for(int ix = 0; ix < 3; ++ix) for(int iy = 0; iy < 3; ++iy){
      xMinGralSize[ix] = 4;
      yMinGralSize[iy] = 4;
      xMaxGralSize[ix] = Short.MAX_VALUE;
      yMaxGralSize[iy] = Short.MAX_VALUE;
    }
  }
  
  
  /**Initializes the output area, can be called after construction immediately.
   * @param sOutputArea String for example "A3C3". The letter character is the column. The digit is the row.
   * It is fromTo. The example means the whole bottom area.
   */
  @Override public void initGraphic(String sOutputArea){
    this.outputArea = sOutputArea;
    window.gralMng().gralDevice.addDispatchOrder(initGraphic); 
    initGraphic.awaitExecution(1, 0);
    window.gralMng().gralDevice.addDispatchOrder(writeOutputTextDirectly);
    
  }
  

  
  GralDispatchCallbackWorker initGraphic = new GralDispatchCallbackWorker("GralArea9Window.initGraphic"){
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      window.setResizeAction(resizeAction);   //sets the resize action from this instead a standard window for the primaryWindow.
      setFrameAreaBorders(30,70,30,70);
      if(bSetStandardMenus){
        //setStandardMenusGThread(currentDirectory, actionFile);
      }
      window.gralMng().setPosition(-40, 0, 10, 0, 0, 'd');
      infoBox = window.gralMng().createTextInfoBox("infoBox", "Info");
      //
      window.gralMng().selectPanel("primaryWindow");
      window.gralMng().setPosition(0,40,10,0,0,'.');
      infoHelp = window.gralMng().createHtmlInfoBox("Help", "Help", true);
      try{
        for(String line: mainCmd.listHelpInfo){
          infoHelp.append(line).append("\n");
        }
      } catch(Exception exc){ window.gralMng().writeLog(0, exc); }
      //
      window.gralMng().selectPanel("primaryWindow");
      window.gralMng().setPosition(0,20,15,GralPos.size + 50,0,'.');
      infoAbout = window.gralMng().createTextInfoBox("About", "About");
      try{
        for(String line: mainCmd.listAboutInfo){
          infoAbout.append(line).append("\n");
        }
      } catch(Exception exc){ window.gralMng().writeLog(0, exc); }
      //
      
      
      if(outputArea != null){
        outputPanel = addOutputFrameArea(outputArea);
        window.gralMng().registerPanel(outputPanel);
      }
      window.gralMng().gralDevice.removeDispatchListener(this);
      countExecution();
    }
  };

  
  
  @Override public MainCmd_ifc getMainCmd(){ return mainCmd; }
  
  @Override public GralWindow_ifc mainWindow(){ return window; }
  
  @Override public GralPanelContent getOutputPanel(){ return outputPanel; } ///
  
  @Override public GralTextBox getOutputBox(){ return outputBox; }

  

  
  /**Sets the divisions of the frame. The frame is divide into 9 parts,
   * where two horizontal and two vertical lines built them:
   * <pre>
   * +=======+===============+===========+
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +=======+===============+===========+
   * </pre>
   * 
   * @param x1p percent from left for first vertical divide line.
   * @param x2p percent from left for second vertical divide line.
   * @param y1p percent from left for first horizontal divide line.
   * @param y2p percent from left for first horizontal divide line.
   */
  @Override public void setFrameAreaBorders(int x1p, int x2p, int y1p, int y2p)
  { xpFrameArea[0] = 0;
    xpFrameArea[1] = (byte)x1p;
    xpFrameArea[2] = (byte)x2p;
    xpFrameArea[3] = 100;
    ypFrameArea[0] = 0;
    ypFrameArea[1] = (byte)y1p;
    ypFrameArea[2] = (byte)y2p;
    ypFrameArea[3] = 100;
    validateFrameAreas();
  }
  
  

  
  @Override public void addFrameArea(String sArea, GralPanelContent component)
  throws IndexOutOfBoundsException {
    GralRectangle r = convertArea(sArea);
    addFrameArea(r.x, r.y, r.dx, r.dy, component);
  }
 
  
  /**Sets a Component into a defined area. See {@link #setFrameAreaBorders(int, int, int, int)}.
   * It should be called only in the GUI-Thread.
   * @param xArea 1 to 3 for left, middle, right
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   * @param component The component.
   * @throws IndexOutOfBoundsException if the arguments are false or the area is occupied already.
   */
  @Override public final void addFrameArea(int xArea, int yArea, int dxArea, int dyArea, GralPanelContent component)
  throws IndexOutOfBoundsException
  { //int idxArea = (x -1) + 3 * (y -1);
    //Composite component = new Composite(swtWindow.graphicThreadSwt, SWT.NONE);
    if(  xArea <1 || xArea > componentFrameArea[0].length
      || dxArea < 1
      || xArea+dxArea-2 > componentFrameArea[0].length
      || yArea <1 || yArea > componentFrameArea.length
      || dyArea < 1
      || yArea+dyArea-1 > componentFrameArea.length
      ) 
      throw new IndexOutOfBoundsException("failed argument");
    for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
    { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
      { if(dxyFrameArea[idyArea][idxArea][0] != 0) throw new IndexOutOfBoundsException("area occupied already");
      }
    }
    for(int idxArea = xArea-1; idxArea <= xArea + dxArea -2; idxArea++)
    { for(int idyArea = yArea-1; idyArea <= yArea + dyArea -2; idyArea++)
      { dxyFrameArea[idyArea][idxArea][0] = -1; //ocuupy it.
      }
    }
    dxyFrameArea[yArea-1][xArea-1][0] = (byte)dxArea;
    dxyFrameArea[yArea-1][xArea-1][1] = (byte)dyArea;
    //JScrollPane scrollPane = new JScrollPane(component);
    //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    //scrollPane.setViewportView(component);
    componentFrameArea[yArea-1][xArea-1] = component; //scrollPane;
    //setBoundsForFrameArea(xArea-1, yArea-1);
    //scrollPane.validate();
    //swtWindow.graphicThreadSwt.add(component); //scrollPane);
    /*TODO
      ((Control)component.getWidgetImplementation()).addMouseListener(mouseListener);
      if(yAreaMover[1] == null){
        yAreaMover[1] = new Canvas(swtWindow.graphicThreadSwt.windowSwt, SWT.None);
        yAreaMover[1].setSize(10,10);
        yAreaMover[1].setBackground(swtWindow.graphicThreadSwt.displaySwt.getSystemColor(SWT.COLOR_GREEN));
      }
    */
    validateFrameAreas();
    //return component;
  }

  
  
  @Override public void setMinMaxSizeArea(String sArea, int yMinUnit, int yMaxUnit, int xMinUnit, int xMaxUnit)
  throws IndexOutOfBoundsException {
    GralRectangle r = convertArea(sArea);
    yMinGralSize[r.y -1] = (short)yMinUnit;
    yMaxGralSize[r.y -1] = yMaxUnit <=0 ? Short.MAX_VALUE : (short)yMaxUnit;
    xMinGralSize[r.x -1] = (short)xMinUnit;
    xMaxGralSize[r.x -1] = xMaxUnit <=0 ? Short.MAX_VALUE : (short)xMaxUnit;
    
    validateFrameAreas();
  }

  
  
  
  /**Set the bounds of all areas.
   * Calls {@link GralWindow#getPixelPositionSize()} to get the size of working area of the whole window
   * 
   */
  protected void validateFrameAreas()
  {
    GralRectangle posSize = window.getPixelPositionSize();
    //Point size = swtWindow.graphicThreadSwt.windowSwt.getSize();
    int xWidth = posSize.dx; // -6; //swtWindow.graphicThreadSwt.getWidth();
    int yWidth = posSize.dy; //  -50; //swtWindow.graphicThreadSwt.getHeight() - 50;  //height of title and menu TODO calculate correctly
    //Control content = swtWindow.graphicThreadSwt.getContentPane();
    //xWidth = content.getWidth();
    //yWidth = content.getHeight();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    
    int xPixUnit = window.gralMng().propertiesGui().xPixelUnit();
    int yPixUnit = window.gralMng().propertiesGui().yPixelUnit();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    
    calcPixSize(xPixArea, xpFrameArea, xMinGralSize, xMaxGralSize, xPixUnit, xWidth, pixelPerXpercent);
    calcPixSize(yPixArea, ypFrameArea, yMinGralSize, yMaxGralSize, yPixUnit, yWidth, pixelPerYpercent);

    for(int idxArea = 0; idxArea <= 2; idxArea++)
    { for(int idyArea = 0; idyArea <= 2; idyArea++)
      { GralPanelContent component = componentFrameArea[idyArea][idxArea];
        if(component !=null)
        { //setBoundsForFrameArea(idxArea, idyArea);
          int xp = xPixArea[idxArea];
          int dxf = dxyFrameArea[idyArea][idxArea][0]; //nr of occupied areas
          int xf2 = xPixArea[idxArea + dxf]; //percent right
          int dxp =  xf2 - xp;
          
          int yp = yPixArea[idyArea];
          int dyf = dxyFrameArea[idyArea][idxArea][1];
          int yf2 = yPixArea[idyArea + dyf];
          int dyp =  yf2 - yp;
          GralPanelContent area = componentFrameArea[idyArea][idxArea];
          area.setBoundsPixel(xp, yp, dxp, dyp);
          area.repaint();
    } } }
    /*
    for(int ixMover = 0; ixMover < yAreaMover.length; ++ixMover){
      if(yAreaMover[ixMover] != null){
        int yp = (int)(ypFrameArea[ixMover+1] * pixelPerYpercent);
        //TODO it doesn't work yet, not visible, why?:
        /*
        yAreaMover[ixMover].setBounds(10,yp,10,10);
        yAreaMover[ixMover].update();     
        yAreaMover[ixMover].redraw();
        * /     
      }
    }
    */
    //swtWindow.graphicThreadSwt.update();
    //swtWindow.graphicThreadSwt.redraw();
    //swtWindow.graphicThreadSwt.update();
    
  }

  
  
  private void calcPixSize(int[] zPixArea, byte[] zpFrameArea, short[] zMinGralSize, short[] zMaxGralSize
    , int zPixUnit, int zSize, float pixelPerZpercent){
    int zMinWin = zMinGralSize[0] + zMinGralSize[1] + zMinGralSize[2];  
    zPixArea[0] = 0;
    if(zSize >= (zPixUnit * zMinWin)){
      int zPix = 0;
      for(int iz=1; iz < 4; ++iz){
        int zPix2 = (int)(zpFrameArea[iz] * pixelPerZpercent); 
        if((zPix2 - zPix) > (zMaxGralSize[iz-1] * zPixUnit)){
          zPix2 = zPix + zMaxGralSize[iz-1] * zPixUnit;
        }
        else if((zPix2 - zPix) < (zMinGralSize[iz-1] * zPixUnit)){
          zPix2 = zPix + zMinGralSize[iz-1] * zPixUnit;
        }
        zPixArea[iz] = zPix = zPix2;
      }
      if(zPixArea[3] < zSize){
        zPixArea[3] = zPix = zSize;
        for(int iz=2; iz >=0; --iz){
          if(zPix - zPixArea[iz] > zMaxGralSize[iz] * zPixUnit){
            zPixArea[iz] = zPix - zMaxGralSize[iz] * zPixUnit;
          }
          zPix = zPixArea[iz];
        }
      } else if(zPixArea[3] > zSize){
        zPixArea[3] = zPix = zSize;
        for(int iz=2; iz >=0; --iz){
          if(zPix - zPixArea[iz] < zMinGralSize[iz] * zPixUnit){
            zPixArea[iz] = zPix - zMinGralSize[iz] * zPixUnit;
          }
          zPix = zPixArea[iz];
        }
      }
    } else { //the window is less than the minimal area sizes:
      //then the percent are valid, lesser as planned
      for(int iz=0; iz < 3; ++iz){
        zPixArea[iz] = (int)(zpFrameArea[iz] * pixelPerZpercent); 
      }
    }

  }
  
  
  
  
  
  
  
  /**Sets the bounds for the component, which is localized at the given area.
   * @param idxArea
   * @param idyArea
   */
  private void setBoundsForFrameArea(int idxArea, int idyArea)
  { //Point size = swtWindow.graphicThreadSwt.windowSwt.getSize();
    GralRectangle posSize = window.getPixelPositionSize();
    int xWidth = posSize.dx -6; //swtWindow.graphicThreadSwt.getWidth();
    int yWidth = posSize.dy -53; //swtWindow.graphicThreadSwt.getHeight() - 50;  //height of title and menu TODO calculate correctly
    //Control content = swtWindow.graphicThreadSwt.getContentPane();
    //xWidth = content.getWidth();
    //yWidth = content.getHeight();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    int xf1 = xpFrameArea[idxArea];  //percent left
    int yf1 = ypFrameArea[idyArea];
    int dxf = dxyFrameArea[idyArea][idxArea][0]; //nr of occupied areas
    int dyf = dxyFrameArea[idyArea][idxArea][1];
    int xf2 = xpFrameArea[idxArea + dxf]; //percent right
    int yf2 = ypFrameArea[idyArea + dyf];
    
    //calculate pixel size for the component:
    int xp = (int)(xf1  * pixelPerXpercent);
    int yp = (int)(yf1  * pixelPerYpercent);
    int dxp = (int) ((xf2-xf1) * pixelPerXpercent);
    int dyp = (int) ((yf2-yf1) * pixelPerYpercent);
    int xPixUnit = window.gralMng().propertiesGui().xPixelUnit();
    int yPixUnit = window.gralMng().propertiesGui().yPixelUnit();
    int xMax = xPixUnit * xMaxGralSize[idxArea];
    int xMin = xPixUnit * xMinGralSize[idxArea];
    int yMax = xPixUnit * yMaxGralSize[idyArea];
    int yMin = xPixUnit * yMinGralSize[idyArea];
    GralPanelContent area = componentFrameArea[idyArea][idxArea];
    area.setBoundsPixel(xp, yp, dxp, dyp);
    area.repaint();
  }
  
  

  

  
  /**Converts a string given are designation to indices.
   * @param area A, B , C for column, 1, 2, 3 for row.
   * @return
   */
  protected GralRectangle convertArea(String area)
  { int x1,x2,y1,y2;
    x1 = "ABC".indexOf(area.charAt(0));
    if(x1 < 0){x1 = "ABC".indexOf(area.charAt(1));}
    y1 = "123".indexOf(area.charAt(0));
    if(y1 < 0){y1 = "123".indexOf(area.charAt(1));}
    if(area.length() >=2){
      x2 = "ABC".indexOf(area.charAt(2));
      if(x2 < 0){x2 = "ABC".indexOf(area.charAt(3));}
      y2 = "123".indexOf(area.charAt(2));
      if(y2 < 0){y2 = "123".indexOf(area.charAt(3));}
    } else {
      x2 = x1; y2 = y1;
    }
    GralRectangle ret = new GralRectangle(x1+1, y1+1, x2-x1+1, y2-y1+1);
    return ret;
  }

  
  

  public final void setStandardMenus(File openStandardDirectory, GralUserAction actionFile)
  { this.currentDirectory = openStandardDirectory;
    this.actionFile = actionFile;
    this.bSetStandardMenus = true;
    
  }
  

  
  
  public final void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile)
  { this.currentDirectory = openStandardDirectory;
    this.actionFile = actionFile;
    //if(window instanceof GralPrimaryWindow_ifc)
    { //GralPrimaryWindow_ifc pWindow = (GralPrimaryWindow_ifc) window;
      //create the menue
      window.addMenuBarItemGThread("menuFileOpen", "&File/&Open", this.new GralActionFileOpen());
      //swtWindow.addMenuItemGThread("&File/&Close", this.new ActionFileClose());
      window.addMenuBarItemGThread("menuFileSave", "&File/&Save", actionFile);
      //swtWindow.addMenuItemGThread("&File/E&xit", this.new ActionFileOpen());
      //swtWindow.graphicThreadSwt.setJMenuBar(menuBar);
      //swtWindow.graphicThreadSwt.setVisible( true );
      window.repaint();
    //} else {
    //  throw new IllegalArgumentException("Error: can't apply menus in a sub window");
      //window.getMng().writeLog(0, "Error: can't apply menus in a sub window");
    }
    
  }
  
  
  @Deprecated @Override public void addMenuBarArea9ItemGThread(String name, String sMenuPath, GralUserAction action)
  { //if(window instanceof GralPrimaryWindow_ifc)
    { //GralPrimaryWindow_ifc pWindow = (GralPrimaryWindow_ifc) window;
      window.addMenuBarItemGThread(name, sMenuPath, action);
    }
  }
  
  
  @Override public GralMenu getMenuBar(){ return window.getMenuBar(); }

  
  protected GralPanelContent addOutputFrameArea(String area)
  {
    GralRectangle areaR = convertArea(area);
    window.gralMng().selectPanel("primaryWindow");
    GralPanelContent outputPanel = window.gralMng().createCompositeBox("outputArea");
    window.gralMng().setPosition(0,0,0,0,0,'b');
    //NOTE: it is a edit-able box. It may be usefully to edit the content by user sometimes. 
    outputBox = window.gralMng().addTextBox("output", true, null, '.');  
    try{ outputBox.append("output...\n"); } catch(IOException exc){}
    addFrameArea(areaR.x, areaR.y, areaR.dx, areaR.dy, outputPanel);
    return outputPanel;
  }
  
  
  
  private final GralDispatchCallbackWorker writeOutputTextDirectly = new GralDispatchCallbackWorker("GralArea9Window.writeOutputTextDirectly")
  { @Override public void doBeforeDispatching(boolean onlyWakeup)
    { String line;
      while((line = outputTexts.poll())!=null){
        writeDirectly(line, MainCmd.kInfoln_writeInfoDirectly);
      }
    }
  };
  
  
  
  
  
  /** Overloads MainCmd.writeDirectly. This method may be overloaded by the user
   * if it has a better way to show infos.*/
  protected void writeDirectly(String sInfo, short kind)  //##a
  { if(textAreaOutput != null){
      if(Thread.currentThread().getId() == window.gralMng().gralDevice.getThreadIdGui()){
        try{
          if((kind & MainCmd.mNewln_writeInfoDirectly) != 0)
          { textAreaOutput.append("\n");
          }
          textAreaOutput.append(sInfo);
          int nrofLines = textAreaOutput.getNrofLines();
          textAreaOutput.viewTrail();
          //textAreaOutput.setCaretPosition(nrofLines-1);
          textAreaOutput.repaint(100, 500);
        } catch(IOException exc){ getGralMng().writeLog(0, exc); }
      } else {  
        //queue the text
        outputTexts.add(sInfo);
        window.gralMng().gralDevice.wakeup();
      }
    }  
    else mainCmd.writeDirectly(sInfo, kind);     
  }
  
  

  
  
  





  @Override public void setHelpUrl(String url){ infoHelp.setUrl(url); }
  



  //@Override public void repaint(){  window.repaint(); }



  
  /** Gets the graphical frame. */
  //@Override public Object getitsGraphicFrame(){ return window.getWidgetImplementation(); }



  
  @Override public GralMng getGralMng()
  { return window.gralMng();
  }
  



  
  


  private final GralUserAction mouseAction = new GralUserAction("mouseAction")
  { int captureAreaDivider;
  
  
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { GralRectangle mousePos = (GralRectangle)params[0]; 
      int yf1 = ypFrameArea[1];
      int yf2 = ypFrameArea[2];
      int xf1 = xpFrameArea[1]; //percent right
      int xf2 = xpFrameArea[2]; //percent right
      if(mousePos.x < 20){
        //calculate pixel size for the component:
        int y1 = (int)(yf1  * pixelPerYpercent);
        int y2 = (int)(yf1  * pixelPerYpercent);
        if(mousePos.y > y1-20 && mousePos.y < y1 + 20){
          captureAreaDivider = 1;     
      }
    }
return true;
  } };
  
  
  private final GralUserAction resizeAction = new GralUserAction("resizeAction")
  { @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { validateFrameAreas();  //calculates the size of the areas newly and redraw.
      return true;
  } };
  
  
  /**TODO actionFile in user space?
   *
   */
  protected class GralActionFileOpen extends GralUserAction
  { private final GralFileDialog_ifc fileDialog;
  
    public GralActionFileOpen(){
      fileDialog = window.gralMng().createFileDialog();
      fileDialog.open("FileDialog", 0);
    }
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { fileDialog.show("d:/vishia", "", "*.*", "select");
      return true; 
  } }


  private final GralUserAction actionHelp = new  GralUserAction("actionHelp")
  { 
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { infoHelp.activate();
      infoHelp.setWindowVisible(true);
      return true; 
  } };


  private final GralUserAction actionAbout = new  GralUserAction("actionAbout")
  { //final InfoBox infoHelp;
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { try{
        for(String line: mainCmd.listAboutInfo){
          //infoAbout.append(line).append("\n");
        }
      } catch(Exception exc){ window.gralMng().writeLog(0, exc); }
      infoAbout.setWindowVisible(true);
      return true; 
  } };


  /**Returns the standard behavior: opens the help window with its constant text.
   * @see org.vishia.gral.area9.GralArea9_ifc#getActionAbout()
   */
  @Override public GralUserAction getActionAbout()
  { return actionAbout;
  }

  /**Returns the standard behavior: opens the about window with its constant text.
   * @see org.vishia.gral.area9.GralArea9_ifc#getActionAbout()
   */
  @Override public GralUserAction getActionHelp()
  { return actionHelp;
  }



}
