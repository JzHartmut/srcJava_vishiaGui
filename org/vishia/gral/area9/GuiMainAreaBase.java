package org.vishia.gral.area9;

import java.io.File;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralSubWindow;
import org.vishia.gral.base.GralWidgetImplWrapper;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindowMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralFileDialog_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.InfoBox;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;

public abstract class GuiMainAreaBase implements GuiMainAreaifc
{
  public final MainCmd mainCmd;
  
  protected final GralWindowMng gralDevice;
  
  protected final GralSubWindow window;
  
  /**Area settings for output. */
  protected String outputArea;

  protected GralPanelContent outputPanel;
  
  protected GralTextBox outputBox;
  
  protected InfoBox infoHelp, infoAbout;
  
  /** Current Directory for file choosing. */
  protected File currentDirectory = null;
  
  protected GralUserAction actionFile;
  
  /**Set on call of {@link #setStandardMenus(File)} to add in in the graphic thread. */
  protected boolean bSetStandardMenus;
  
  protected Queue<String> outputTexts = new ConcurrentLinkedQueue<String>();
  
  
  /**All main components of the Display in each FrameArea. */
  protected GralPanelContent[][] componentFrameArea = new GralPanelContent[3][3];

  
  /**A little control to capture the mouse position for movement of area borders. */
  private GralWidgetImplWrapper[] yAreaMover = new GralWidgetImplWrapper[2];
  

  
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
  public void setOutputArea(String area){
    outputArea = area;
  }

  //private GuiMainAreaBase(MainCmd mainCmd){ this.mainCmd = mainCmd; }

  /**
   * @param mainCmd Aggregation to a MainCmd implemenation.
   * @param guiDevice The window manager
   * @param window The window itself. It doesn't be the primary window but a sub window too.
   */
  public GuiMainAreaBase(MainCmd mainCmd, GralWindowMng guiDevice, GralSubWindow window, String sOutputArea)
  {
    super();
    this.mainCmd = mainCmd;
    this.gralDevice = guiDevice;
    this.window = window;
    this.outputArea = sOutputArea;
    guiDevice.addDispatchListener(initOutputArea); 
    guiDevice.addDispatchListener(writeOutputTextDirectly);

  }

  
  GralDispatchCallbackWorker initOutputArea = new GralDispatchCallbackWorker(){
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      window.setResizeAction(resizeAction);
      //swtWindow.graphicThreadSwt.windowSwt.addControlListener(resizeListener);
      setFrameAreaBorders(30,70,30,70);
      if(bSetStandardMenus){
        setStandardMenusGThread(currentDirectory, actionFile);
      }
      if(outputArea != null){
        GralRectangle area = convertArea(outputArea);
        /*
        int xArea = outputArea.charAt(0) - 'A' +1;
        int yArea = outputArea.charAt(1) - '0';
        int dxArea = outputArea.charAt(2) - 'A' +1 - xArea +1;
        int dyArea = outputArea.charAt(3) - '0' - yArea +1;
        */
        outputPanel = addOutputFrameArea(area.x, area.y, area.dx, area.dy);
        gralDevice.gralMng.registerPanel(outputPanel);
        gralDevice.removeDispatchListener(this);
        countExecution();
      }
    }
  };

  

  
  @Override public MainCmd_ifc getMainCmd(){ return mainCmd; }
  
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
    setBoundsForFrameArea(xArea-1, yArea-1);
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

  
  
  
  protected void validateFrameAreas()
  {
    GralRectangle posSize = window.getPixelPositionSize();
    //Point size = swtWindow.graphicThreadSwt.windowSwt.getSize();
    int xWidth = posSize.dx -6; //swtWindow.graphicThreadSwt.getWidth();
    int yWidth = posSize.dy -53; //swtWindow.graphicThreadSwt.getHeight() - 50;  //height of title and menu TODO calculate correctly
    //Control content = swtWindow.graphicThreadSwt.getContentPane();
    //xWidth = content.getWidth();
    //yWidth = content.getHeight();
    pixelPerXpercent = xWidth / 100.0F;
    pixelPerYpercent = yWidth / 100.0F;
    

    for(int idxArea = 0; idxArea <= 2; idxArea++)
    { for(int idyArea = 0; idyArea <= 2; idyArea++)
      { GralPanelContent component = componentFrameArea[idyArea][idxArea];
        if(component !=null)
        { //Control control = (Control)component.getWidgetImplementation();
          setBoundsForFrameArea(idxArea, idyArea);
          //control.update();
          //Rectangle bounds = control.getBounds();
          //control.redraw(bounds.x, bounds.y, bounds.width, bounds.height, true);
    } } }
    for(int ixMover = 0; ixMover < yAreaMover.length; ++ixMover){
      if(yAreaMover[ixMover] != null){
        int yp = (int)(ypFrameArea[ixMover+1] * pixelPerYpercent);
        //TODO it doesn't work yet, not visible, why?:
        /*
        yAreaMover[ixMover].setBounds(10,yp,10,10);
        yAreaMover[ixMover].update();     
        yAreaMover[ixMover].redraw();
        */     
      }
    }
    //swtWindow.graphicThreadSwt.update();
    //swtWindow.graphicThreadSwt.redraw();
    //swtWindow.graphicThreadSwt.update();
    
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
    GralPanelContent area = componentFrameArea[idyArea][idxArea];
    area.setBoundsPixel(xp, yp, dxp, dyp);
    area.redraw();
  }
  
  

  

  
  protected GralRectangle convertArea(String area)
  { int x1,x2,y1,y2;
    x1 = "ABC".indexOf(area.charAt(0));
    if(x1 < 0){x1 = "ABC".indexOf(area.charAt(1));}
    x2 = "ABC".indexOf(area.charAt(2));
    if(x2 < 0){x2 = "ABC".indexOf(area.charAt(3));}
    y1 = "123".indexOf(area.charAt(0));
    if(y1 < 0){y1 = "123".indexOf(area.charAt(1));}
    y2 = "123".indexOf(area.charAt(2));
    if(y2 < 0){y2 = "123".indexOf(area.charAt(3));}
    
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
    if(window instanceof GralPrimaryWindow_ifc)
    { GralPrimaryWindow_ifc pWindow = (GralPrimaryWindow_ifc) window;
      //create the menue
      pWindow.addMenuItemGThread("&File/&Open", this.new GralActionFileOpen());
      //swtWindow.addMenuItemGThread("&File/&Close", this.new ActionFileClose());
      pWindow.addMenuItemGThread("&File/&Save", actionFile);
      //swtWindow.addMenuItemGThread("&File/E&xit", this.new ActionFileOpen());
      pWindow.addMenuItemGThread("&Help/&Help", this.new GralActionHelp());
      pWindow.addMenuItemGThread("&Help/&About", this.new GralActionAbout());
      //swtWindow.graphicThreadSwt.setJMenuBar(menuBar);
      //swtWindow.graphicThreadSwt.setVisible( true );
      pWindow.redraw();
    } else {
      throw new IllegalArgumentException("Error: can't apply menus in a sub window");
      //window.gralMng.writeLog(0, "Error: can't apply menus in a sub window");
    }
    
  }
  
  
  @Override public void addMenuItemGThread(String namePath, GralUserAction action)
  { if(window instanceof GralPrimaryWindow_ifc)
    { GralPrimaryWindow_ifc pWindow = (GralPrimaryWindow_ifc) window;
      pWindow.addMenuItemGThread(namePath, action);
    }
  }
  

  
  protected GralPanelContent addOutputFrameArea(int xArea, int yArea, int dxArea, int dyArea)
  {
    GralPanelContent outputArea = window.gralMng.createCompositeBox("outputArea");
    addFrameArea(xArea, yArea, dxArea, dyArea, outputArea);
    
    window.gralMng.setPosition(0,0,0,0,0,'b');
    outputBox = window.gralMng.addTextBox("output", false, null, '.');
    try{ outputBox.append("output...\nA\nb\nc\nd\ne\nf\ng\nA\nA\n"); } catch(IOException exc){}
    
    return outputArea;
  }
  
  
  
  GralDispatchCallbackWorker writeOutputTextDirectly = new GralDispatchCallbackWorker()
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
      if(Thread.currentThread().getId() == gralDevice.graphicThread.guiThreadId){
        try{
          if((kind & MainCmd.mNewln_writeInfoDirectly) != 0)
          { textAreaOutput.append("\n");
          }
          textAreaOutput.append(sInfo);
          int nrofLines = textAreaOutput.getNrofLines();
          textAreaOutput.viewTrail();
          //textAreaOutput.setCaretPosition(nrofLines-1);
          textAreaOutput.redraw();
        } catch(IOException exc){ getGralMng().writeLog(0, exc); }
      } else {  
        //queue the text
        outputTexts.add(sInfo);
        gralDevice.wakeup();
      }
    }  
    else mainCmd.writeDirectly(sInfo, kind);     
  }
  
  

  
  
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  @Override public void addDispatchListener(GralDispatchCallbackWorker listener)
  { gralDevice.addDispatchListener(listener);
  }
  
  
  
  @Override public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { gralDevice.removeDispatchListener(listener);
  }
  
  


  @Override
  public boolean isWindowsVisible()
  { return window.isWindowsVisible();
  }




  @Override  public void setWindowVisible(boolean visible)
  { window.setWindowVisible(visible);
  }


  @Override public boolean isRunning(){ return gralDevice.isRunning(); }

  @Override public void redraw(){  window.redraw(); }



  
  /** Gets the graphical frame. */
  @Override public Object getitsGraphicFrame(){ return window.getWidgetImplementation(); }



  @Override public long getThreadIdGui()
  { return gralDevice.getThreadIdGui();
  }

  
  @Override public GralWidgetMng getGralMng()
  { return gralDevice.gralMng;
  }
  
  @Override public GralRectangle getPixelPositionSize(){ return window.getPixelPositionSize(); }

  
  @Override public void closeWindow()
  { 
    window.closeWindow();
  }



  @Override public void exit()
  { closeWindow(); 
  }

  
  
  @Override public void setResizeAction(GralUserAction action){
    throw new IllegalArgumentException("this instance has its resizeListener already.");
  }
  


  private GralUserAction resizeAction = new GralUserAction()
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
      fileDialog = window.gralMng.createFileDialog();
      fileDialog.open("FileDialog", 0);
    }
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { fileDialog.show("d:/vishia", "", "*.*", "select");
      return true; 
  } }


  protected class GralActionHelp extends GralUserAction
  { //final InfoBox infoHelp;
    public GralActionHelp(){
      InfoBox infoHelp1 = null;
      try{
        window.gralMng.selectPanel("output");
        window.gralMng.setPosition(-40,0,0,0,0,'.');
        infoHelp1 = InfoBox.create(window.gralMng, "Help", "Help");
        for(String line: mainCmd.listHelpInfo){
          infoHelp1.append(line).append("\n");
          //sHelpText[ix++] = line;
        }
      } catch(Exception exc){ window.gralMng.writeLog(0, exc); }
      infoHelp = infoHelp1;
    }

    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { infoHelp.setWindowVisible(true);
      return true; 
  } }


  protected class GralActionAbout extends GralUserAction
  { //final InfoBox infoHelp;
    public GralActionAbout(){
      //InfoBox infoHelp1 = null;
      try{
        window.gralMng.selectPanel("output");
        window.gralMng.setPosition(-20,0,-40,0,0,'.');
        infoAbout = InfoBox.create(getGralMng(), "about", "about");
        for(String line: mainCmd.listAboutInfo){
          infoAbout.append(line).append("\n");
          //sHelpText[ix++] = line;
        }
      } catch(Exception exc){ window.gralMng.writeLog(0, exc); }
      //infoAbout = infoHelp1;
    }

    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { infoAbout.setWindowVisible(true);
      return true; 
  } }


}
