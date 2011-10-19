package org.vishia.mainGuiSwt;


import java.io.File;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralPrimaryWindow;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.MinMaxTime;

public class PrimaryWindowSwt extends GralPrimaryWindow implements GralWindow_ifc
{
  protected final Display displaySwt; 

  /** The frame of the Window in the GUI (Graphical Unit Interface)*/
  protected Shell graphicFrame;

  /**The file menuBar is extendable. */
  private Menu menuBar;
  

  private static class MenuEntry
  {
    String name;
    /**If it is a superior menu item, the menu below. Else null. */
    Menu menu;
    Map<String, MenuEntry> subMenu;
  }
  
  
  /**Class to instantiate in the static routine {@link #create(LogMessage)}
   * which contains the inital run method to build the gui device.
   */
  private static class Init implements Runnable
  {
    Display displaySwt;
    
    @Override public void run(){
      displaySwt = new Display();
    }
    
  }
  
  Map<String, MenuEntry> menus = new TreeMap<String, MenuEntry>();
  
  

  
  private PrimaryWindowSwt(GralGridMngBase gralMng, Thread graphicThread, Display displaySwt)
  { super(gralMng, graphicThread);
    this.displaySwt = displaySwt; 
  }  
  
  
  static PrimaryWindowSwt create(LogMessage log)
  { Init init = new Init();
    GuiThread graphicThread = startGraphicThread(init);  

    PropertiesGuiSwt propertiesGui = new PropertiesGuiSwt(init.displaySwt, 'C');
    GralGridMngBase gralMng = new GuiPanelMngSwt(propertiesGui, null, log);
    

    PrimaryWindowSwt instance = new PrimaryWindowSwt(gralMng, graphicThread.getThread(), init.displaySwt);
    graphicThread.setWindow(instance);  //now the initializing of the window occurs.
    return instance;
  }
  
  

  
  
  
  @Override protected Object initGraphic() //String sTitle, int left, int top, int xSize, int ySize)
  {
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

  
  
  /**Sets the title and size before initialization.
   * @param sTitle
   * @param xSize
   * @param ySize
   */
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
  

  
  /**Adds a order for building the gui. The order will be execute one time after intializing the Graphic
   * and before entry in the dispatch loop. After usage the orders will be removed,
   * to force garbage collection for the orders.
   * @param order
   */
  public void addGuiBuildOrder(Runnable order)
  { buildOrders.add(order);
  }
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { dispatchListeners.remove(listener);
  }
  
  

  
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  public void addDispatchListener(GralDispatchCallbackWorker listener)
  { dispatchListeners.add(listener);
    //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
    //therefore:
    extEventSet.getAndSet(true);
    if(displaySwt !=null){
      displaySwt.wake();  //to wake up the GUI-thread, to run the listener at least one time.
    }
  }
  
  
  void processBuildOrders()
  {
  }
  
  
  
  private static class CntSleep
  {
    long lastTime;
    long minMillisecBetweenSleep= Long.MAX_VALUE;
    long maxMillisecBetweenSleep= 0;
    double avMillisecBetweenSleep = 0.0;
    double percentActive = 0.0;
  };
  
  private CntSleep cntSleep;
  
  
  MinMaxTime checkTimes = new MinMaxTime();
  
  
  public void wakeup(){
    displaySwt.wake();
    extEventSet.set(true);
    isWakedUpOnly = true;
  }
  
  public boolean isWakedUpOnly(){ return isWakedUpOnly; }
  
  
  @Override protected void dispatch()
  {
    checkTimes.init();
    checkTimes.adjust();
    checkTimes.cyclTime();
    while (! (bExit = graphicFrame.isDisposed ())) {
      while (displaySwt.readAndDispatch ()){
        //isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
      }
      checkTimes.calcTime();
      isWakedUpOnly = false;
      //System.out.println("dispatched");
      if(!extEventSet.get()) {
        displaySwt.sleep ();
      }
      if(!bExit){
        extEventSet.set(false); //the list will be tested!
        if(isWakedUpOnly)
          stop();
        //it may be waked up by the operation system or by calling Display.wake().
        //if wakeUp() is called, isWakedUpOnly is set.
        checkTimes.cyclTime();
        for(GralDispatchCallbackWorker listener: dispatchListeners){
          //use isWakedUpOnly for run as parameter?
          ///System.out.println("BeforeDispatch");
          listener.doBeforeDispatching(isWakedUpOnly);  
          ///System.out.println("BeforeDispatch-ready");
        }
      } 
    }
    displaySwt.dispose ();
    bExit = true;
  }
  
  
  public void terminate()
  {
    if(!bExit && !displaySwt.isDisposed()){ 
      displaySwt.dispose();
    }  

  }
  
  
  public boolean isStarted(){ return bStarted; }
  
  public boolean isRunning(){ return bStarted && !bExit; }
  
  public boolean isTerminated(){ return bStarted && bExit; }

  @Override public boolean isWindowsVisible()
  { return graphicFrame.isVisible();
  }


  @Override
  public void setWindowVisible(boolean visible)
  {
    graphicFrame.setVisible(visible);
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
    { action.userActionGui("", null, (Object[])null);
    }
  }
  
  

  
  /**The windows-closing event handler. It is used private only, but public set because documentation. */
  public final class WindowsCloseListener implements Listener{
    /**Invoked when the window is closed; it sets {@link #bExit}, able to get with {@link #isRunning()}.
     * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
     */
    @Override public void handleEvent(Event event) {
      bExit = true;
    }
  }

  /**Instance of windowsCloseHandler. */
  private final WindowsCloseListener windowsCloseListener = new WindowsCloseListener(); 
  
  
  KeyListener keyListener = new KeyListener()
  {
    @Override public void keyPressed(KeyEvent key)
    {
      // TODO Auto-generated method stub
      stop();
    }

    @Override public void keyReleased(KeyEvent e)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  ShellListener mainComponentListerner = new ShellListener()
  {

        
    @Override
    public void shellActivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellClosed(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeactivated(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellDeiconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void shellIconified(ShellEvent e) {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  
  void stop()
  { //to set breakpoint
  }


  @Override public void addMenuItemGThread(String namePath, GralUserAction action)
  {
    addMenuItemGThread(namePath, this.new ActionUserMenuItem(action));
  }
  
  
  /**Adds a menu item with a SWT-specific {@link SelectionListener}. This method can be invoked
   * from any derived SWT-specific class. {@link MainCmdSwt} do so. 
   * @param namePath
   * @param action
   */
  protected void addMenuItemGThread(String namePath, SelectionListener action)
  {
    String[] names = namePath.split("/");
    if(menuBar == null){
      menuBar = new Menu(graphicFrame, SWT.BAR);
      graphicFrame.setMenuBar(menuBar);
    }
    Menu parentMenu = menuBar;
    Map<String, MenuEntry> menustore = menus;
    int ii;
    for(ii=0; ii<names.length-1; ++ii){
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
        menuEntry = new MenuEntry();
        menustore.put(name, menuEntry);
        menuEntry.name = name;
        menuEntry.subMenu = new TreeMap<String, MenuEntry>();
        MenuItem item = new MenuItem(parentMenu, SWT.CASCADE);
        item.setText(name);
        if(cAccelerator !=0){
          item.setAccelerator(SWT.CONTROL | cAccelerator);
        }
        menuEntry.menu = new Menu(graphicFrame, SWT.DROP_DOWN);
        item.setMenu(menuEntry.menu);
      }
      menustore = menuEntry.subMenu;
      parentMenu = menuEntry.menu;
    }
    String name = names[ii];
    MenuItem item = new MenuItem(parentMenu, SWT.None); 
    item.setText(name);
    //item.setAccelerator(SWT.CONTROL | 'S');
    item.addSelectionListener(action);
    ///
  }
  
  
  


  @Override
  public void exit()
  {
    // TODO Auto-generated method stub
    
  }



  @Override
  public MainCmd_ifc getMainCmd()
  {
    // TODO Auto-generated method stub
    return null;
  }



  @Override
  public Object getitsGraphicFrame()
  {
    // TODO Auto-generated method stub
    return null;
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

  @Override public void redraw(){  graphicFrame.redraw(); graphicFrame.update(); }


  
  public void removeWidgetImplementation()
  {
    graphicFrame.dispose();
    graphicFrame = null;
    menuBar = null;
  }


  
}
