package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
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
  //protected GralPrimaryWindow window;
  
  //protected Runnable init;
  
  /**The thread which runs all graphical activities. */
  protected Thread thread;

  /**The thread id of the managing thread for GUI actions. */
  public long guiThreadId;

  /**List of all requests to change the graphical presentation of values. The list can be filled in some Threads.
   * It is processed in the {@link #dispatch()} routine.  */
  protected ConcurrentLinkedQueue<GralWidgetChangeRequ> guiChangeRequests = new ConcurrentLinkedQueue<GralWidgetChangeRequ>();
  
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
  
  
  /**Adds any change request of the graphic appearance in any other thread.
   * The graphic thread will be poll it.
   * @param requ
   */
  public void addChangeRequest(GralWidgetChangeRequ requ)
  {
    guiChangeRequests.add(requ);
    synchronized(guiChangeRequests){ guiChangeRequests.notify(); }  //to wake up waiting on guiChangeRequests.
    wakeup();

  }
  

  /**Polls one change request. This method should be called in the graphic thread from any class,
   * which knows details about the graphic. That class is {@link org.vishia.gral.base.GralWidgetMng}.
   * @return null if the queue is empty.
   */
  public GralWidgetChangeRequ pollChangeRequest()
  {
    return guiChangeRequests.poll();
  }
  

  
  /** Adds a method which will be called in anytime in the dispatch loop until the listener will remove itself.
   * @see org.vishia.gral.ifc.GralWindowMng_ifc#addDispatchListener(org.vishia.gral.ifc.GralDispatchCallbackWorker)
   * @param listener
   */
  public void addDispatchListener(GralDispatchCallbackWorker listener)
  { dispatchListeners.add(listener);
    //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
    //therefore:
    extEventSet.getAndSet(true);
    if(bStarted){
      
      wakeup();  //to wake up the GUI-thread, to run the listener at least one time.
    }
  }
  
  

  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { dispatchListeners.remove(listener);
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

  

}

