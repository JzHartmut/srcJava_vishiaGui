package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;

/**This is the representation of a main window in the Graphic Adalption Layer.
 * A main window has its own thread, which runs the dispatch loop of window handling.
 * <ul>
 * <li>
 * In the SWT implementation a main window has its own org.eclipse.swt.widgets.Display
 * and an associated org.eclipse.swt.widgets.Shell.
 * <li>
 * In the swing implementation a main window has its javax.swing.JFrame.
 * </ul> 
 * 
 * @author Hartmut Schorrig
 * @since 2011-05-01
 */
public abstract class GralPrimaryWindow extends GralSubWindow implements GralPrimaryWindow_ifc
{

  /**Version of this class:
   * <ul>
   * <li>2011-09-10, Hartmut: This class has had the name GralDevice, because it refers the Display-Thread of SWT.
   *   But the really function is: represent a primary window with its event dispatch loop.
   *   Therefore rename, extend. 
   * </ul>
   * 
   */
  public static final int version = 0x20110911; 
  
  
  /**The thread id of the managing thread for GUI actions. */
  public long guiThreadId;

  /**List of all requests to change the graphical presentation of values. The list can be filled in some Threads.
   * It is processed in the 
   * 
   */
  public ConcurrentLinkedQueue<GralWidgetChangeRequ> guiChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>();
  
  /**Size of display window. */
  protected int xSize,ySize;
  /**left and top edge of display-window. */
  protected int xPos, yPos;
  
  /**Title of display window. */
  protected String sTitle;
  


  
  /**This thread can (not have to be) used for all graphic actions. If the user calls
   * 
   * 
   */
  protected static class GuiThread implements Runnable
  {
    private GralPrimaryWindow window;
    
    protected Runnable init;
    
    private Thread thread;
    
    GuiThread(String name, Runnable init){ thread = new Thread(this, name); this.init = init; }
    
    public Thread getThread(){ return thread; }
    
    void start(){ thread.start(); }
    
    @Override public void run()
    { init.run();
      synchronized(this){
        init = null;
        notify();
      }
      synchronized(this){
          while(window == null || window.sTitle == null){
          try{ wait(1000); } catch(InterruptedException exc){ }
        }
      }
      window.panelComposite = window.initGraphic(); //window.sTitle, window.xPos, window.yPos, window.xSize, window.ySize);
      window.gralMng.setGralDevice(window);
      window.gralMng.registerPanel(window);
      
      
      for(Runnable build: window.buildOrders){
        build.run();
      }
      
      synchronized(window){
        window.bStarted = true;   ///
        if(window.bWaitStart){ 
          window.notify();
        }
      }
      window.dispatch();
      //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
    }

    public void setWindow(GralPrimaryWindow window){
      synchronized(this){
        this.window = window;
        notify();
      }
      
    }
  
  }
  
  
  protected final Thread guiThread;

  
  /**True if the startup is done. */
  protected boolean bStarted = false; 

  protected boolean bWaitStart = false;
  
  /**If true, than the application is to be terminate. */
  protected boolean bTerminated = false;

  protected boolean isWakedUpOnly;
  

  
  /** set to true to exit in main*/
  protected boolean bExit = false;
  
  /**Queue of dispatchListeners. Any instance will be invoked in the dispatch-loop.
   * See {@link #addDispatchListener(Runnable)}. 
   * Hint: A dispatch-listener can be run only one time, than it should delete itself in its run-method.*/
  protected ConcurrentLinkedQueue<GralDispatchCallbackWorker> dispatchListeners = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  /**Set if any external event is set. Then the dispatcher shouldn't sleep after finishing dispatching. 
   * This is important if the external event occurs while the GUI is busy in the operation-system-dispatching loop.
   */
  protected AtomicBoolean extEventSet = new AtomicBoolean(false);

  /**Queue of orders for building the graphic. Any instance will be invoked 
   * after initializing the graphic and before entry in the dispatch-loop.
   * See {@link #addGuiBuildOrder(Runnable)}. */
  protected ConcurrentLinkedQueue<Runnable> buildOrders = new ConcurrentLinkedQueue<Runnable>();
  
  
  //-----------------------------------------------------------------------------------------------------
  //Abstract method definitions:

  public abstract void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize);

  protected abstract void dispatch();
    
  
  public GralPrimaryWindow(GralGridMngBase gralMng, Thread graphicThread)
  { super("primaryWindow", gralMng);
    this.guiThread = graphicThread;
    guiThreadId = graphicThread.getId();
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
  public boolean XXXcontinueGraphicThread(String sTitle){
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
  
  
  /**This method should be called from the implementing class on creation of the main window.
   * @param init run-Method to initialize the graphic.
   * @return The tread to deal and store in the instance
   */
  protected static GuiThread startGraphicThread(Runnable init)
  {
    GuiThread thread = new GuiThread("GUI", init);
    thread.start();
    synchronized(thread){
      while(thread.init != null){
        try{ thread.wait(100);} catch(InterruptedException exc){}
      }
    }
    return thread;
  }
  
  /**Should be implemented by the underlying graphic adapter. 
   * <ul>
   * <li>Creation of the graphic device
   * <li>Initialization of the primary window
   * <li>Creation of the properties for GralMng, it may be depending on the underlaying graphic (Fonts etc.)
   * <li>Creation of the GralMng for the underlying graphic (derived class)
   * </ul>
   * 
   * @return The instance of the underlying graphic which represents the composite class of the primary window-panel.
   *   For SWT it is a Shell. For Swing it is a JFrame. It is stored in this' superclass {@link GralPanelContent#panelComposite}
   * */
  abstract protected Object initGraphic();
  
  /** Adds a method which will be called in anytime in the dispatch loop until the listener will remove itself.
   * @see org.vishia.gral.ifc.GralWindowMng_ifc#addDispatchListener(org.vishia.gral.ifc.GralDispatchCallbackWorker)
   */
  public abstract void addDispatchListener(GralDispatchCallbackWorker listener);

  
  //------------------------------------------------------------------------------------------
  //get methods
  
  @Override public long getThreadIdGui(){ return guiThreadId; }

  
  
}
