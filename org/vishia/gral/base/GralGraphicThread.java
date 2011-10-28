package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
import org.vishia.util.MinMaxTime;

public abstract class GralGraphicThread implements Runnable
{
  //protected GralPrimaryWindow window;
  
  //protected Runnable init;
  
  /**The thread which runs all graphical activities. */
  protected Thread thread;

  /**The thread id of the managing thread for GUI actions. */
  public long guiThreadId;

  /**List of all requests to change the graphical presentation of values. The list can be filled in some Threads.
   * It is processed in the {@link #dispatch()} routine.  */
  public ConcurrentLinkedQueue<GralWidgetChangeRequ> guiChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>();
  
  /**True if the startup of the main window is done and the main window is visible. */
  protected boolean bStarted = false; 

  //protected boolean bWaitStart = false;
  
  /**If true, than the application is to be terminate. */
  //protected boolean bTerminated = false;

  protected boolean isWakedUpOnly;
  

  
  /** set to true to exit in main*/
  public boolean bExit = false;
  
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
  //ConcurrentLinkedQueue<Runnable> buildOrders = new ConcurrentLinkedQueue<Runnable>();
  
  /**Size of display window. */
  protected int xSize,ySize;
  /**left and top edge of display-window. */
  protected int xPos, yPos;
  
  /**Title of display window. */
  protected String sTitle;
  

  protected MinMaxTime checkTimes = new MinMaxTime();
  

  
  /**The constructor of the implementing class can be get some more parameter to build the primary window.
   * That constructor should call {@link #thread}.start() to start the {@link #run()} method of this class
   * after all parameter are saved to execute the overridden {@link #initGraphic()} method.
   * @param name Name of the thread.
   */
  protected GralGraphicThread(String name)
  { thread = new Thread(this, name);
  }
  
  /**This method should be implemented by the graphical base. It should build the graphic main window
   * and returned when finished. This routine is called as the first routine in the Graphic thread's
   * method {@link #run()}. */
  protected abstract void initGraphic();
  
  /**Calls the dispatch routine of the implementation graphic.
   * @return true if dispatching should be continued.
   */
  protected abstract boolean dispatchOsEvents();
  
  
  /**Forces the graphic thread to sleep and wait for any events. Either this routine returns
   * if {@link #wakeup()} is called or this routine returns if the operation system wakes up the graphic thread. */
  protected abstract void graphicThreadSleep();
  
  /**This method should be implemented by the graphical base. It should be waked up the execution 
   * of the graphic thread because some actions are registered.. */
  protected abstract void wakeup();
  
  
  
  /**The run method of the graphic thread. This method will be invoked automatically in the constructor
   * of the derived class.
   * It will be executed so long as the application runs. 
   * @see java.lang.Runnable#run()
   */
  @Override public void run()
  { initGraphic();
    //The last action, set the GuiThread
    long guiThreadId1 = Thread.currentThread().getId(); ///
    synchronized(this){
      this.guiThreadId = guiThreadId1;
      bStarted = true;
      notify();      //wakeup the waiting calling thread.
    }
    /*
    synchronized(this){
        while(window == null){
        try{ wait(1000); } catch(InterruptedException exc){ }
      }
    }
    for(Runnable build: buildOrders){
      build.run();
    }
    */
    //dispatch();
    checkTimes.init();
    checkTimes.adjust();
    checkTimes.cyclTime();
    while (!bExit) {
      while (dispatchOsEvents()){
        //isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
      }
      checkTimes.calcTime();
      isWakedUpOnly = false;
      //System.out.println("dispatched");
      if(!extEventSet.get()) {
        graphicThreadSleep();
      }
      if(!bExit){
        extEventSet.set(false); //the list will be tested!
        if(isWakedUpOnly)
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
    //displaySwt.dispose ();
    //bExit = true;
    //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
  }

  //public Thread getThread(){ return thread; }

}

