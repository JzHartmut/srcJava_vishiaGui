package org.vishia.mainGuiSwt;


import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;

public class SwtPrimaryWindow extends SwtSubWindow implements GralPrimaryWindow_ifc //GralWindowMng implements GralWindow_ifc
{
  //protected final Display displaySwt; 

  /** The frame of the Window in the GUI (Graphical Unit Interface)*/
  //protected Shell graphicFrame;

  /**The file menuBar is extendable. */
  private Menu menuBar;
  
  /** */
  final SwtGraphicThread graphicThreadSwt;
  
  private static class MenuEntry
  {
    String name;
    /**If it is a superior menu item, the menu below. Else null. */
    Menu menu;
    Map<String, MenuEntry> subMenu;
  }
  
  
  
  Map<String, MenuEntry> menus = new TreeMap<String, MenuEntry>();
  
  

  
  private SwtPrimaryWindow(GralWidgetMng gralMng, SwtGraphicThread graphicThread, Display displaySwt)
  { super("primaryWindow", graphicThread.windowSwt, gralMng);
    //super(gralMng, graphicThread);
    this.graphicThreadSwt = graphicThread;  //refers SWT type
  }  
  
  
  public static SwtPrimaryWindow create(LogMessage log, String sTitle, int left, int top, int xSize, int ySize)
  { SwtGraphicThread init = new SwtGraphicThread(sTitle, left, top, xSize, ySize);
    //GuiThread graphicThread = startGraphicThread(init);  

    synchronized(init){
      while(init.guiThreadId == 0){
        try{ init.wait(1000);} catch(InterruptedException exc){}
      }
    }
    //The propertiesGuiSwt needs the Display instance for Font and Color. Therefore the graphic thread with creation of Display should be executed before. 
    SwtProperties propertiesGui = new SwtProperties(init.displaySwt, 'C');
    GralWidgetMng gralMng = new SwtWidgetMng(propertiesGui, null, log);
    
    //The PrimaryWindowSwt is a derivation of the GralPrimaryWindow. It is more as only a SWT Shell.
    SwtPrimaryWindow instance = new SwtPrimaryWindow(gralMng, init, init.displaySwt);
    instance.panelComposite = init; //window.sTitle, window.xPos, window.yPos, window.xSize, window.ySize);
    gralMng.setGralDevice(init);
    gralMng.registerPanel(instance);
    
    //init.setWindow(instance);  //now the initializing of the window occurs.
    return instance;
  }
  
  

  
  
  /*
  @Override protected Object initGraphic() //String sTitle, int left, int top, int xSize, int ySize)
  { ///
    guiThreadId = Thread.currentThread().getId(); ///
    displaySwt.addFilter(SWT.Close, windowsCloseListener);
    graphicFrame = new Shell(displaySwt); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
    graphicFrame.addKeyListener(keyListener);
    
    //graphicFramePos = new Position(graphicFrame.getContentPane());
    //graphicFramePos.set(0,0,xSize,ySize);
    // main = this;
    graphicFrame.setText(sTitle);
    //graphicFrame.getContentPane().setLayout(new BorderLayout());
    //graphicFrame.addWindowListener(new WindowClosingAdapter(true));
    //graphicFrame.setSize( xSize, ySize );
    if(xSize == -1 || ySize == -1){
      graphicFrame.setFullScreen(true);
    } else {
      graphicFrame.setBounds(xPos,yPos, xSize, ySize );  //Start position.
    }
    graphicFrame.open();
    graphicFrame.setVisible( true ); 

    //graphicFrame.getContentPane().setLayout(new FlowLayout());
    graphicFrame.setLayout(null);
    graphicFrame.addShellListener(mainComponentListerner);
    
    return graphicFrame;
    
  }
  */
  
  
  /**Sets the title and size before initialization.
   * @param sTitle
   * @param xSize
   * @param ySize
  @Override public void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize)
  { this.xSize = xSize;
    this.ySize = ySize;
    this.xPos = left;
    this.yPos = top;
    if(bStarted){
      if(xSize < 0 || ySize < 0){
        graphicFrame.setFullScreen(true);
      } else {
        graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
      }  
      graphicFrame.setText(sTitle);
    } else {
      synchronized(guiThread){
        if(!bStarted){
          this.sTitle = sTitle;
          guiThread.notify();     //Run the GUI Thread
        }
      }
      synchronized(this){
        while(!bStarted){
          bWaitStart = true;
          try{ wait(10000);  } catch(InterruptedException exc){}   //Await the GUI Thread
        }
      }
    }    
  }
   */
  
  
  private static class CntSleep
  {
    long lastTime;
    long minMillisecBetweenSleep= Long.MAX_VALUE;
    long maxMillisecBetweenSleep= 0;
    double avMillisecBetweenSleep = 0.0;
    double percentActive = 0.0;
  };
  
  private CntSleep cntSleep;
  
  
  
  //public boolean isWakedUpOnly(){ return graphicThread.isWakedUpOnly; }
  
  public void terminate()
  {
    if(!graphicThreadSwt.bExit && !graphicThreadSwt.displaySwt.isDisposed()){ 
      graphicThreadSwt.displaySwt.dispose();
    }  

  }
  
  @Override public boolean isWindowsVisible()
  { return graphicThreadSwt.windowSwt.isVisible();
  }


  @Override
  public void setWindowVisible(boolean visible)
  {
    graphicThreadSwt.windowSwt.setVisible(visible);
  }
  
  
  
  
  class ActionUserMenuItem implements SelectionListener
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
      action.userActionGui(KeyCode.menuEntered, widgg);
    }
  }
  
  

  
  void stop()
  { //to set breakpoint
  }


  @Override public void addMenuItemGThread(String nameWidg, String sMenuPath, GralUserAction action)
  {
    addMenuItemGThread(nameWidg, sMenuPath, action, this.new ActionUserMenuItem(action));
  }
  
  
  /**Adds a menu item with a SWT-specific {@link SelectionListener}. This method can be invoked
   * from any derived SWT-specific class. {@link MainCmdSwt} do so. 
   * @param namePath
   * @param action
   */
  protected void addMenuItemGThread(String nameWidg, String sMenuPath, GralUserAction gralAction, SelectionListener action)
  {
    String[] names = sMenuPath.split("/");
    if(menuBar == null){
      menuBar = new Menu(graphicThreadSwt.windowSwt, SWT.BAR);
      graphicThreadSwt.windowSwt.setMenuBar(menuBar);
    }
    Menu parentMenu = menuBar;
    Map<String, MenuEntry> menustore = menus;
    int ii;
    for(ii=0; ii<names.length-1; ++ii){
      //search all pre-menu entries before /. It may be existing, otherwise create it.
      String name = names[ii];
      final char cAccelerator;
      final int posAccelerator = name.indexOf('?');
      if(posAccelerator >=0){
        cAccelerator = Character.toUpperCase(name.charAt(posAccelerator));
        name = name.replace("&", "");
      } else {
        cAccelerator = 0;
      }
      MenuEntry menuEntry = menustore.get(name);
      if(menuEntry == null){
        //create it.
        menuEntry = new MenuEntry();
        menustore.put(name, menuEntry);
        menuEntry.name = name;
        menuEntry.subMenu = new TreeMap<String, MenuEntry>();
        MenuItem item = new MenuItem(parentMenu, SWT.CASCADE);
        item.setText(name);
        if(cAccelerator !=0){
          item.setAccelerator(SWT.CONTROL | cAccelerator);
        }
        menuEntry.menu = new Menu(graphicThreadSwt.windowSwt, SWT.DROP_DOWN);
        item.setMenu(menuEntry.menu);
      }
      menustore = menuEntry.subMenu;
      parentMenu = menuEntry.menu;
    }
    String name = names[ii];
    MenuItem item = new MenuItem(parentMenu, SWT.None); 
    GralWidget widgMenu = new SwtWidgetMenu(nameWidg, item, sMenuPath, gralMng);
    item.setText(name);
    //item.setAccelerator(SWT.CONTROL | 'S');
    item.addSelectionListener(action);
    ///
  }
  
  
  



  @Override
  public MainCmd_ifc getMainCmd()
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Shell getitsGraphicFrame()
  {
    // TODO Auto-generated method stub
    return (graphicThreadSwt).windowSwt;
  }



  @Override
  public void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile)
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }



  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void redraw(){  graphicThreadSwt.windowSwt.redraw(); graphicThreadSwt.windowSwt.update(); }

  
  public void removeWidgetImplementation()
  {
    graphicThreadSwt.windowSwt.dispose();
    graphicThreadSwt.windowSwt = null;
    menuBar = null;
  }


  @Override public Composite getPanelImpl() { return graphicThreadSwt.windowSwt; }

  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = graphicThreadSwt.windowSwt.getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { graphicThreadSwt.windowSwt.setBounds(x,y,dx,dy);
  }
  

  @Override public void closeWindow()
  { 
    terminate();
  }
  
  
  ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { if(resizeAction !=null){
        resizeAction.userActionGui(0, null);
      }
    }
  };
  
  

}
