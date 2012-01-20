package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.util.MinMaxTime;

/**This class is the base for implementation of graphic threading. It is implemented for SWT and Swing yet.
 * <br><br>
 * <b>The functionality and necessity of a graphic thread</b>: <br>
 * Swing is not thread safe but it works if widgets are changed from other threads. 
 * SWT causes an exception if a widget is changed in another thread than the graphic thread.
 * The graphic thread in SWT calls the operation system routine to dispatch events. 
 * If any event call-back method is invoked from the graphic system, it runs in the graphic thread.
 * Therefore all other widgets can changed in this thread. For example a button is pressed, and therefore
 * some widgets are changed in appearance (setText(), setColor()) or new widgets are created and other are deleted.
 * <br><br>
 * <b>Necessity of other threads as the graphic thread, divide of functionality in some threads</b>:<br> 
 * To get the data from the application some actions should be done which may need time or/and may wait of something
 * for example network communication transfer. It that action are done in the graphic thread immediately, 
 * the graphic seems as frozen. It doesn't execute other activities which are need for example to resize a window,
 * hide and focus it etc. This phenomena is known by some graphic applications. It is bothering. 
 * Therefore long running or waiting actions should be organized in another thread. The GUI can be show any hint
 * that an action is executing. Other actions of the GUI runs well.
 * <br><br>
 * To organize such working the action on the GUI (calling back method, event) should notify and inform the other thread
 * which does the work. If that thread is finished, its result should be shown on the graphic. 
 * But because the graphic isn't thread safe the graphic can't be changed in this other thread direct.
 * The information which should be changed are queued calling {@link #addChangeRequest(GralWidgetChangeRequ)}
 * from this class. The execution of the change requests can't be done with the information contained in this class
 * because it is the common and simple graphic thread implementation which doesn't know details about widgets.
 * The proper class for that action is the {@link org.vishia.gral.base.GralWidgetMng} 
 * and its derivation for the graphic implementation. That class calls {@link #pollChangeRequest()}.   
 *  
 * @author Hartmut Schorrig
 *
 */
public abstract class GralGraphicThread implements Runnable
{
  
  /**Version and history:
   * <ul>
   * <li>2012-11-08 Hartmut new: Delayed orders to dispatch in the graphic thread: 
   *   Some actions need some calculation time. 
   *   If they are called in a fast repetition cycle, a follow up effect may occur. 
   *   Therefore actions should be registered with a delayed start of execution, the start time 
   *   should be able to putting off till all repetitions (for example key repetition) are done.
   * <li>2011-11-00 Hartmut created: as own class from Swt widget manager.
   * </ul>
   * 
   */
  public static final int version = 0x20120108;
  
  //protected GralPrimaryWindow window;
  
  //protected Runnable init;
  
  /**The thread which runs all graphical activities. */
  protected Thread threadGuiDispatch;

  /**The thread which executes delayed wake up. */
  protected Thread threadTimer;

  /**The thread id of the managing thread for GUI actions. */
  public long guiThreadId;

  /**Queue of orders to execute in the graphic thread before dispatching system events. 
   * Any instance will be invoked in the dispatch-loop.
   * See {@link #addDispatchOrder(Runnable)}. 
   * An order can be stayed in this queue for ever. It is invoked any time after the graphic thread 
   * is woken up and before the dispatching of graphic-system-event will be started.
   * An order may be run only one time, than it should delete itself from this queue in its run-method.
   * */
  protected ConcurrentLinkedQueue<GralDispatchCallbackWorker> graphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  protected ConcurrentLinkedQueue<GralDispatchCallbackWorker> delayedGraphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  protected ConcurrentLinkedQueue<GralDispatchCallbackWorker> delayedTempGraphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  /**List of all requests to change the graphical presentation of values. The list can be filled in some Threads.
   * It is processed in the {@link #dispatch()} routine.  */
  protected ConcurrentLinkedQueue<GralWidgetChangeRequ> guiChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>();
  
  /**List of all requests to change the graphical presentation of values. The list can be filled in some Threads.
   * It is processed in the {@link #dispatch()} routine.  */
  protected ConcurrentLinkedQueue<GralWidgetChangeRequ> delayedChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>()
        , delayedTempChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>();
  
  
  private boolean bTimeIsWaiting;
  
  /**True if the startup of the main window is done and the main window is visible. */
  protected boolean bStarted = false; 

  //protected boolean bWaitStart = false;
  
  /**If true, than the application is to be terminate. */
  //protected boolean bTerminated = false;

  protected boolean isWakedUpOnly;
  

  
  /** set to true to exit in main*/
  protected boolean bExit = false;
  
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
  

  
  /**Constructs this class as superclass.
   * The constructor of the inheriting class has some more parameter to build the 
   * primary window. Therefore the {@link #threadGuiDispatch}.start() to start the {@link #run()}
   * method of this class should be invoked only in the derived constructor
   * after all parameter are saved to execute the overridden {@link #initGraphic()} method.
   * @param name Name of the thread.
   */
  protected GralGraphicThread()
  { threadGuiDispatch = new Thread(this, "graphic");
    threadTimer = new Thread(runTimer, "graphictime");
    threadTimer.start();
  }
  
  
  /**Adds any change request of the graphic appearance in any other thread.
   * The graphic thread will be poll it.
   * @param requ
   */
  public void addChangeRequest(GralWidgetChangeRequ requ)
  {
    if(requ.timeToExecution() >=0){
      delayedChangeRequests.offer(requ);
      synchronized(runTimer){
        if(bTimeIsWaiting){
          runTimer.notify();  
        }
      }
    } else {
      guiChangeRequests.add(requ);
      synchronized(guiChangeRequests){ 
        guiChangeRequests.notify();   //to wake up waiting on guiChangeRequests.
      }
      wakeup();
    }
  }
  

  /**Polls one change request. This method should be called in the graphic thread from any class,
   * which knows details about the graphic. That class is {@link org.vishia.gral.base.GralWidgetMng}.
   * 
   * Hint: The method is public only because it will be invoked from the graphical implementation.
   * @return null if the queue is empty.
   */
  public GralWidgetChangeRequ pollChangeRequest()
  {
    GralWidgetChangeRequ changeReq = guiChangeRequests.poll();
    while (changeReq != null){
      int timeToExecution = changeReq.timeToExecution();
      if(timeToExecution >=0){
        //not yet to proceed
        delayedChangeRequests.offer(changeReq);
        synchronized(runTimer){
          if(bTimeIsWaiting){
            runTimer.notify();  
          }
        }
        changeReq = guiChangeRequests.poll();  //check if there is another.
      } else {
        return changeReq;  //take this
      }
    }
    return null;  //nothing found.
  }
  

  
  /** Adds a method which will be called in anytime in the dispatch loop until the listener will remove itself.
   * @deprecated: This method sholdn't be called by user, see {@link GralDispatchCallbackWorker#addToGraphicThread(GralGraphicThread, int)}. 
   * @see org.vishia.gral.ifc.GralWindowMng_ifc#addDispatchListener(org.vishia.gral.ifc.GralDispatchCallbackWorker)
   * @param order
   */
  public void addDispatchOrder(GralDispatchCallbackWorker order){ 
    if(order.timeToExecution() >=0){
      delayedGraphicOrders.offer(order);
      synchronized(runTimer){
        if(bTimeIsWaiting){
          runTimer.notify();  
        }
      }
    } else {
      graphicOrders.add(order);
      //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
      //therefore:
      extEventSet.getAndSet(true);
      if(bStarted){
        
        wakeup();  //to wake up the GUI-thread, to run the listener at least one time.
      }
  
    }
  }
  
  

  
  
  /**Removes a order, which was called in the dispatch loop.
   * Hint: Use {@link GralDispatchCallbackWorker#removeFromGraphicThread(GralGraphicThread)}
   * to remove thread safe with signification. Don't call this routine yourself.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { graphicOrders.remove(listener);
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
  public abstract void wakeup();
  
  public long getThreadIdGui(){ return guiThreadId; }
  
  
  public boolean isStarted(){ return bStarted; }
  
  public boolean isRunning(){ return bStarted && !bExit; }
  
  public boolean isTerminated(){ return bStarted && bExit; }


  
  /**The run method of the graphic thread. This method is started in the constructor of the derived class
   * of this, which implements the graphic system adapter. 
   * <ul>
   * <li>{@link #initGraphic()} will be called firstly. It is overridden by the graphic system implementing class
   *   and does some things necessary for the graphic system implementing level.
   * <li>The routine runs so long as {@link #bExit} is not set to false. bExit may be set to false 
   *   in a window close listener of the graphic system level. It means, it is set to false especially 
   *   if the windows will be closed from the operation system. If the window is closed because the application
   *   is terminated by any application command the window will be closed, and the close listerer sets bReady
   *   to false then. 
   * <li>In the loop the {@link #graphicOrders} will be executed.
   * <li>For SWT graphic this is the dispatch loop of graphic events. They are executed 
   *   in the abstract defined here {@link #dispatchOsEvents()} method.
   * <li>This thread should be wait if not things are to do. The wait will be executed in the here abstract defined
   *   method {@link #graphicThreadSleep()}.    
   * </ul>  
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
      boolean bContinueDispatch;
      do{
        try{ bContinueDispatch = dispatchOsEvents();}
        catch(Exception exc){
          System.out.println(exc.getLocalizedMessage());
          exc.printStackTrace(System.out);
          bContinueDispatch = true; //false;
        }
        //isWakedUpOnly = false;  //after 1 event, it may be wakeUp, set if false.
      } while(bContinueDispatch);
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
        for(GralDispatchCallbackWorker listener: graphicOrders){
          //use isWakedUpOnly for run as parameter?
          try{
            listener.doBeforeDispatching(isWakedUpOnly);
          } catch(Exception exc){
            System.err.println("Exception in GralDispatchCallbackWorker:");
            exc.printStackTrace();
          }
        }
      } 
    }
    //displaySwt.dispose ();
    //bExit = true;
    //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
  }

  
  
  
  
  
  
  Runnable runTimer = new Runnable(){
    @Override public void run(){
      while(!bExit){
        int timeWait = 1000;
        boolean bWake = false;
        { GralWidgetChangeRequ requ;
          while( (requ = delayedChangeRequests.poll()) !=null){
            int timeToExecution = requ.timeToExecution();
            if(timeToExecution >=0){
              //not yet to proceed
              if(timeWait > timeToExecution){ timeWait = timeToExecution; }
              delayedTempChangeRequests.offer(requ);
            } else {
              guiChangeRequests.offer(requ);
              bWake = true;
            }
          }
          //delayedChangeRequest is tested and empty now.
          //offer the requ back from the temp queue
          while( (requ = delayedTempChangeRequests.poll()) !=null){
            delayedChangeRequests.offer(requ); 
          }
        }
        { GralDispatchCallbackWorker order;
          while( (order = delayedGraphicOrders.poll()) !=null){
            int timeToExecution = order.timeToExecution();
            if(timeToExecution >=0){
              //not yet to proceed
              if(timeWait > timeToExecution){ timeWait = timeToExecution; }
              delayedTempGraphicOrders.offer(order);
            } else {
              graphicOrders.offer(order);
              bWake = true;
            }
          }
          //delayedChangeRequest is tested and empty now.
          //offer the requ back from the temp queue
          while( (order = delayedTempGraphicOrders.poll()) !=null){
            delayedGraphicOrders.offer(order); 
          }
        }
        if(bWake){
          wakeup(); //process changeRequests in the graphic thread.
        }
        synchronized(this){
          bTimeIsWaiting = true;
          if(timeWait < 10){
            timeWait = 10; //at least 10 ms, especially prevent usage of 0 and negative values.
          }
          try{ wait(timeWait);} catch(InterruptedException exc){}
          bTimeIsWaiting = false;
        }
      }
    }
  };

}

