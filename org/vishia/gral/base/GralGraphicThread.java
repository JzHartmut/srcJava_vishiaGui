package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

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
 * <b>Necessity of other threads than the graphic thread, divide of functionality in some threads</b>:<br> 
 * To get the data from the application some actions should be done which may need time or/and may wait of something
 * for example network communication transfer. If that action are done in the graphic thread immediately, 
 * the graphic seems as frozen. It doesn't execute other activities which are need for example to resize a window,
 * hide and focus it etc. This phenomena is known by some graphic applications. Often, firstly
 * a pilot application works well because the actions are done in less 100 milliseconds. That's okay. But if the
 * environment will be more and more complex and the functionality increases in real applications, the waiting
 * time increases from 100 to ... 1 second and more, and the graphic hangs in some situations. It is bothering.    
 * <b>Therefore long running or waiting actions should be organized in another thread.</b> The GUI can be show any hint
 * that some action is executing. Other actions can be done in this time, especially abort a non-response action. 
 * The GUI itself runs well.
 * <br><br>
 * To organize such working, actions on the GUI should notify and inform only the other threads. The other threads
 * executes the necessary activity. If any activity is finished, its result should be shown on the graphic. 
 * If any activity is progressed, it may be shown too, for example in steps of 300 milliseconds. 
 * <br><br>
 * The graphic content should be changed from some other thread. 
 * But because the graphic isn't thread safe. The graphic can't be changed in the other thread directly.
 * The information which should be presented should be queued. That is the mechanism:
 * <ul>
 * <li><b>Version 1:</b> An execution sequence for the graphic thread is written in a derived instance of
 *    <ul><li>{@link GralDispatchCallbackWorker#doBeforeDispatching(boolean)}.
 *    </ul> 
 *    That instance should be queued calling
 *    <ul><li>{@link #addDispatchOrder(GralDispatchCallbackWorker)}. 
 *    </ul>
 *    With them the graphic thread is woken up
 *    because {@link #wakeup()} is called in the 'addDispatchOrder()'-routine. 
 *    <br><br>
 *    In the graphic thread execution loop the {@link #queueGraphicOrders} queue is checked 
 *    and all queued method are invoked. That executes the 'widget.setText(text)' or the other routines
 *    from the users programm in the {@link GralDispatchCallbackWorker#doBeforeDispatching(boolean)}.
 *    <br><br>
 *    After the queue is checked the {@link #dispatchOsEvents()} is called. In SWT it calls the operation system
 *    dispatching loop. If the underlying graphic system has its own graphic dispatching thread that thread
 *    is woken up only to present the changes in the widgets. If all graphic dispatching is done, 
 *    {@link #graphicThreadSleep()} let this thread sleeping, its all done. 
 *    <br><br>
 *    The instance of {@link GralDispatchCallbackWorker} will be remain in the queue. For single activities
 *    it should be queued out by itself calling its own {@link GralDispatchCallbackWorker#removeFromQueue(GralGraphicThread)}
 *    method in its {@link GralDispatchCallbackWorker#doBeforeDispatching(boolean)}-routine.
 *    Another possibility is to have instances of {@link GralDispatchCallbackWorker} which are queued
 *    for any time. They are invoked whenever {@link #wakeup()} is called. 
 * <li><b>Version 2</b>: The order or commission can be instructed to the <code>setInfo(cmd, ...data)</code>-method
 *   of a {@link GralWidget}:
 *   <ul><li>{@link GralMng#setInfo(org.vishia.gral.base.GralWidget widget, int cmd, int ident, Object toshow, Object data)}
 *   </ul>
 *   This method fills a queue of the {@link GralMng}:
 *   <ul><li>{@link GralMng.WidgetChangeRequExecuter#guiChangeRequests}
 *     <li>{@link GralMng.WidgetChangeRequExecuter#doBeforeDispatching(boolean)} polls that queue.
 *     <li>{@link GralMng#widgetChangeRequExecuter}: The instance in the GralWidgetManager.
 *   </ul>
 *   The instance is a permanent member of the {@link #queueGraphicOrders} queue, it is executed 
 *   any time when a {@link #wakeup()} will be invoked. 
 *   <br>
 *   The commission for changing any widget
 *   is given in that way with a specific command and data, which is realized in the method:
 *   <ul><li>{@link GralMng#setInfoGthread(org.vishia.gral.ifc.GralWidget_ifc, int, int, Object, Object)}
 *   </ul>
 *   That method assigns some commands to the implementation level
 *   standard invocation methods to change widget contents such as setText() or setColor(). To support this action
 *   independently of the graphic implementation two interfaces are defined: 
 *   <ul><li>{@link GralWidgetGthreadSet_ifc} and
 *     <li> {@link GralWindow_setifc}
 *   </ul>  
 *   That interfaces defines the universal changed methods for widgets and windows.
 *   <br><br>
 *   The second version of instructing widget changing requires less programming effort, but it supports only standard operations.
 * <li><b>Version 3</b>: All data of a widget are stored in graphic-independent fields of the derived instance of the widget.
 *   That can be done in any thread without mutex mechanisms. If there are more as one thread 
 *   which changes the same widget, the last wins. That may be a non-usual case. If one thread changes a text
 *   and another thread changes a color, it may be okay. There is no necessity to make it thread-safe. 
 *   But if it may be necessity, the user can wrap the access with specific synchronized methods.
 *   After any changing of content, {@link GralWidget#repaint(int, int)} should be invoked with a suitable
 *   delay. That queues the repaint instance
 *   <ul><li>{@link GralWidget#repaintRequ} (it is private) in the queue
 *   <li>{@link #queueDelayedGraphicOrders}
 *   </ul>
 *   In that way the repaint is executed in the graphic thread and fills the graphical widgets.
 * </ul>  
 * Secondary a system of delayed execution is given. All commissions to the graphic thread can be instruct with a delay.
 * There are a queues for delayed execution and an extra time thread {@link #runTimer}. The starting time
 * can be shelved if there are queued and re-instruct further time. This is necessary because some 
 * graphic appearance changing requests may be given in any thread one after another, and the graphic thread
 * should be invoked not for any one request, but only if all requests are given. It saves thread switch activity.
 * Especially if some data are changed and a {@link GralWidget#repaint()} request only applies the data 
 * to the widgets, that 'repaint()' should be invoked only if all data are given. But any data changing
 * don't may know whether it is the last one. Therefore {@link GralWidget#repaint(int, int)} can be called
 * after any data changing with shelving the repaint time. The repaint is executed not till the activity
 * of data changing is finished.
 *  
 * @author Hartmut Schorrig
 *
 */
public abstract class GralGraphicThread implements Runnable
{
  
  /**Version and history:
   * <ul>
   * <li>
   * <li>2012-04-20 Hartmut bugfix: If a {@link GralDispatchCallbackWorker} throws an exception,
   *   it was started again because it was in the queue yet. The proplem occurs on build graphic. It
   *   was repeated till all graphic handles are consumed. Now the {@link #queueGraphicOrders} entries
   *   are deleted first, then executed. TODO use this class only for SWT, use the adequate given mechanism
   *   for AWT: java.awt.EventQueue.invokeAndWait(Runnable). use Runnable instead GralDispatchCallbackWorker. 
   * <li>2012-03-15 Hartmut chg: Message on exception.
   * <li>2011-11-08 Hartmut new: Delayed orders to dispatch in the graphic thread: 
   *   Some actions need some calculation time. 
   *   If they are called in a fast repetition cycle, a follow up effect may occur. 
   *   Therefore actions should be registered with a delayed start of execution, the start time 
   *   should be able to putting off till all repetitions (for example key repetition) are done.
   * <li>2011-11-00 Hartmut created: as own class from Swt widget manager.
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
  public final static int version = 20120422;
  
  //protected GralPrimaryWindow window;
  
  //protected Runnable init;
  
  /**The thread which runs all graphical activities. */
  protected Thread threadGuiDispatch;

  /**The thread which executes delayed wake up. */
  protected Thread threadTimer;

  /**The thread id of the managing thread for graphic actions. */
  protected long graphicThreadId;

  /**Queue of orders to execute in the graphic thread before dispatching system events. 
   * Any instance will be invoked in the dispatch-loop.
   * See {@link #addDispatchOrder(Runnable)}. 
   * An order can be stayed in this queue for ever. It is invoked any time after the graphic thread 
   * is woken up and before the dispatching of graphic-system-event will be started.
   * An order may be run only one time, than it should delete itself from this queue in its run-method.
   * */
  private final ConcurrentLinkedQueue<GralDispatchCallbackWorker> queueGraphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  /**Queue of orders which are executed with delay yet. */
  private final ConcurrentLinkedQueue<GralDispatchCallbackWorker> queueDelayedGraphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  /**Temporary used instance of delayed orders while {@link #runTimer} organizes the delayed orders.
   * This queue is empty outside running one step of runTimer(). */
  private final ConcurrentLinkedQueue<GralDispatchCallbackWorker> queueDelayedTempGraphicOrders = new ConcurrentLinkedQueue<GralDispatchCallbackWorker>();
  
  /**Mutex mechanism: This variable is set true under mutex while the timer waits. Then it should
   * be notified in {@link #addDispatchOrder(GralDispatchCallbackWorker)} with delayed order. */
  private boolean bTimeIsWaiting;
  
  /**True if the startup of the main window is done and the main window is visible. */
  protected boolean bStarted = false; 

  
  protected boolean isWakedUpOnly;
  
  protected final char sizeCharProperties;
  
  /** set to true to exit in main*/
  protected boolean bExit = false;
  
  /**Set if any external event is set. Then the dispatcher shouldn't sleep after finishing dispatching. 
   * This is important if the external event occurs while the GUI is busy in the operation-system-dispatching loop.
   */
  protected AtomicBoolean extEventSet = new AtomicBoolean(false);


  /**Instance to measure execution times.
   * 
   */
  protected MinMaxTime checkTimes = new MinMaxTime();
  

  /**Constructs this class as superclass.
   * The constructor of the inheriting class has some more parameter to build the 
   * primary window. Therefore the {@link #threadGuiDispatch}.start() to start the {@link #run()}
   * method of this class should be invoked only in the derived constructor
   * after all parameter are saved to execute the overridden {@link #initGraphic()} method.
   * @param name Name of the thread.
   */
  protected GralGraphicThread(char size)
  { sizeCharProperties = size;
    threadGuiDispatch = new Thread(this, "graphic");
    threadTimer = new Thread(runTimer, "graphictime");
    threadTimer.start();
  }
  
  
  /** Adds a method which will be called in anytime in the dispatch loop until the listener will remove itself.
   * @deprecated: This method sholdn't be called by user, see {@link GralDispatchCallbackWorker#addToGraphicThread(GralGraphicThread, int)}. 
   * @see org.vishia.gral.ifc.GralWindowMng_ifc#addDispatchListener(org.vishia.gral.base.GralDispatchCallbackWorker)
   * @param order
   */
  public void addDispatchOrder(GralDispatchCallbackWorker order){ 
    if(order.timeToExecution() >=0){
      queueDelayedGraphicOrders.offer(order);
      synchronized(runTimer){
        if(bTimeIsWaiting){
          runTimer.notify();  
        }
      }
    } else {
      queueGraphicOrders.add(order);
      //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
      //therefore:
      extEventSet.getAndSet(true);
      if(bStarted){
        
        wakeup();  //to wake up the GUI-thread, to run the listener at least one time.
      }
  
    }
  }
  
  
  /**Removes a order, which was called in the dispatch loop.
   * Hint: Use {@link GralDispatchCallbackWorker#removeFromQueue(GralGraphicThread)}
   * to remove thread safe with signification. Don't call this routine yourself.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { queueGraphicOrders.remove(listener);
    queueDelayedGraphicOrders.remove(listener);
  }
  
  

  
  /**This method should be implemented by the graphical implementation layer. It should build the graphic main window
   * and returned when finished. This routine is called as the first routine in the Graphic thread's
   * method {@link #run()}. See {@link org.vishia.gral.swt.SwtGraphicThread}. */
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
  
  public long getThreadIdGui(){ return graphicThreadId; }
  
  
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
   * <li>In the loop the {@link #queueGraphicOrders} will be executed.
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
      this.graphicThreadId = guiThreadId1;
      bStarted = true;
      notify();      //wakeup the waiting calling thread.
    }
    checkTimes.init();
    checkTimes.adjust();
    checkTimes.cyclTime();
    while (!bExit) {
      boolean bContinueDispatch;
      do{
        try{ bContinueDispatch = dispatchOsEvents();}
        catch(Throwable exc){
          System.out.println(exc.getMessage());
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
        
        GralDispatchCallbackWorker listener;
        while( (listener = queueGraphicOrders.poll()) !=null){
        //for(GralDispatchCallbackWorker listener: queueGraphicOrders){
              //use isWakedUpOnly for run as parameter?
          try{
            listener.doBeforeDispatching(isWakedUpOnly);
          } catch(Exception exc){
            System.err.println("GralGraphicThread-" + exc.getMessage());
            exc.printStackTrace();
          }
        }
      } 
    }
    //displaySwt.dispose ();
    //bExit = true;
    //synchronized(this){ notify(); }  //to weak up waiting on configGrafic().
  }

  
  
  
  /**Wakes up the {@link #runTimer} queue to execute delayed requests.
   * 
   */
  public void notifyTimer(){
    synchronized(runTimer){
      if(bTimeIsWaiting){
        runTimer.notify();  
      }
    }
  }
  
  
  Runnable runTimer = new Runnable(){
    @Override public void run(){
      while(!bExit){
        int timeWait = 1000;
        boolean bWake = false;
        { GralDispatchCallbackWorker order;
          while( (order = queueDelayedGraphicOrders.poll()) !=null){
            int timeToExecution = order.timeToExecution();
            if(timeToExecution >=0){
              //not yet to proceed
              if(timeWait > timeToExecution){ timeWait = timeToExecution; }
              queueDelayedTempGraphicOrders.offer(order);
            } else {
              queueGraphicOrders.offer(order);
              bWake = true;
            }
          }
          //delayedChangeRequest is tested and empty now.
          //offer the requ back from the temp queue
          while( (order = queueDelayedTempGraphicOrders.poll()) !=null){
            queueDelayedGraphicOrders.offer(order); 
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

