package org.vishia.mainGuiSwt;


import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.base.GralDevice;
import org.vishia.gral.ifc.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.GuiWindowMng_ifc;
import org.vishia.util.MinMaxTime;

public class PrimaryWindowSwt extends GralDevice implements GuiWindowMng_ifc
{
  protected Display guiDevice; 

  /** The frame of the Window in the GUI (Graphical Unit Interface)*/
  protected Shell graphicFrame;

  /**Size of display window. */
  int xSize,ySize;
  /**left and top edge of display-window. */
  int xPos, yPos;
  
  /**Title of display window. */
  String sTitle;
  


  
  /**This thread can (not have to be) used for all graphic actions. If the user calls
   * 
   * 
   */
  Thread guiThread = new Thread("GUI-SWT")
  {
    @Override public void run()
    {
      initGraphic(sTitle, 0, 0, xSize, ySize);
      dispatch();
      //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
    }
  };
  

  
  /**True if the startup is done. */
  boolean bStarted = false; 

  boolean bWaitStart = false;
  
  /**If true, than the application is to be terminate. */
  protected boolean bTerminated = false;

  private boolean isWakedUpOnly;
  

  
  /** set to true to exit in main*/
  boolean bExit = false;
  
  /**Queue of dispatchListeners. Any instance will be invoked in the dispatch-loop.
   * See {@link #addDispatchListener(Runnable)}. 
   * Hint: A dispatch-listener can be run only one time, than it should delete itself in its run-method.*/
  ConcurrentLinkedQueue<GuiDispatchCallbackWorker> dispatchListeners = new ConcurrentLinkedQueue<GuiDispatchCallbackWorker>();
  
  /**Set if any external event is set. Then the dispatcher shouldn't sleep after finishing dispatching. 
   * This is important if the external event occurs while the GUI is busy in the operation-system-dispatching loop.
   */
  AtomicBoolean extEventSet = new AtomicBoolean(false);

  /**Queue of orders for building the graphic. Any instance will be invoked 
   * after initializing the graphic and before entry in the dispatch-loop.
   * See {@link #addGuiBuildOrder(Runnable)}. */
  ConcurrentLinkedQueue<Runnable> buildOrders = new ConcurrentLinkedQueue<Runnable>();
  

  
  
  
  PrimaryWindowSwt()
  {
  }  
  
  /**Starts the execution of the graphic initializing in a own thread.
   * The following sets should be called already:
   * <pre>
   * setTitleAndSize("SES_GUI", 800, 600);  //This instruction should be written first to output syntax errors.
     setStandardMenus(new File("."));
     setOutputArea("A3C3");        //whole area from mid to bottom
     setFrameAreaBorders(20, 80, 75, 80);
     </pre>
   * They sets only some values, no initializing of the graphic is executed till now.
   * <br><br>
   * The graphic thread inside this class {@link #guiThread} initializes the graphic.
   * All other GUI-actions should be done only in the graphic-thread. 
   * The method {@link #addDispatchListener(Runnable)} is to be used therefore.
   * <br><br>
   * The method to use an own thread for the GUI is not prescribed. The initialization
   * and all actions can be done in the users thread too. But then, the user thread
   * have to be called {@link #dispatch()} to dispatch the graphic events. It is busy with it than.   
   * @return true if started
   */
  public boolean startGraphicThread(){
    if(bStarted) throw new IllegalStateException("it is started already.");
    guiThread.start();
    
    synchronized(guiThread){ 
      while(!bStarted){
        bWaitStart = true;
        try{guiThread.wait(5000);} catch(InterruptedException exc){}
      }
      bWaitStart =false;
    }
    return bStarted;
  }
  
  

  
  
  void initGraphic(String sTitle, int left, int top, int xSize, int ySize)
  {
    guiThreadId = Thread.currentThread().getId();
    guiDevice = new Display ();
    guiDevice.addFilter(SWT.Close, windowsCloseListener);
    graphicFrame = new Shell(guiDevice); //, SWT.ON_TOP | SWT.MAX | SWT.TITLE);
    graphicFrame.addKeyListener(keyListener);
    graphicFrame.open();
    
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
      graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
    }
    graphicFrame.setVisible( true ); 

    //graphicFrame.getContentPane().setLayout(new FlowLayout());
    graphicFrame.setLayout(null);
    graphicFrame.addShellListener(mainComponentListerner);
    
    for(Runnable build: buildOrders){
      build.run();
    }
    synchronized(guiThread){
      if(bWaitStart){
        guiThread.notify(); 
      }
      bStarted = true;
    }

    
    
  }

  
  
  /**Sets the title and size before initialization.
   * @param sTitle
   * @param xSize
   * @param ySize
   */
  public void setTitleAndSize(String sTitle, int left, int top, int xSize, int ySize)
  { this.xSize = xSize;
    this.ySize = ySize;
    this.xPos = left;
    this.yPos = top;
    this.sTitle = sTitle;
    if(bStarted){
      if(xSize < 0 || ySize < 0){
        graphicFrame.setFullScreen(true);
      } else {
        graphicFrame.setBounds(left,top, xSize, ySize );  //Start position.
      }  
      graphicFrame.setText(sTitle);
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
  public void removeDispatchListener(GuiDispatchCallbackWorker listener)
  { dispatchListeners.remove(listener);
  }
  
  

  
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  public void addDispatchListener(GuiDispatchCallbackWorker listener)
  { dispatchListeners.add(listener);
    //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
    //therefore:
    extEventSet.getAndSet(true);
    if(guiDevice !=null){
      guiDevice.wake();  //to wake up the GUI-thread, to run the listener at least one time.
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
    guiDevice.wake();
    isWakedUpOnly = true;
  }
  
  public boolean isWakedUpOnly(){ return isWakedUpOnly; }
  
  
  public void dispatch()
  {
    checkTimes.init();
    checkTimes.adjust();
    checkTimes.cyclTime();
    while (! (bExit = graphicFrame.isDisposed ())) {
      while (guiDevice.readAndDispatch ()){
        //isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
      }
      checkTimes.calcTime();
      isWakedUpOnly = false;
      //System.out.println("dispatched");
      if(!extEventSet.get()) {
        guiDevice.sleep ();
      }
      if(!bExit){
        extEventSet.set(false); //the list will be tested!
        if(isWakedUpOnly)
          stop();
        //it may be waked up by the operation system or by calling Display.wake().
        //if wakeUp() is called, isWakedUpOnly is set.
        checkTimes.cyclTime();
        for(GuiDispatchCallbackWorker listener: dispatchListeners){
          //use isWakedUpOnly for run as parameter?
          ///System.out.println("BeforeDispatch");
          listener.doBeforeDispatching(isWakedUpOnly);  
          ///System.out.println("BeforeDispatch-ready");
        }
      } 
    }
    guiDevice.dispose ();
    bExit = true;
  }
  
  
  
  
  public boolean isRunning(){ return !bExit; }
  
  

  @Override public boolean isWindowsVisible()
  { return graphicFrame.isVisible();
  }


  @Override
  public void setWindowVisible(boolean visible)
  {
    graphicFrame.setVisible(visible);
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

  
}
