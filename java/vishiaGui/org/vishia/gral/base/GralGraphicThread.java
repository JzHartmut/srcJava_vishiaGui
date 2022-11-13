package org.vishia.gral.base;


import java.io.IOException;
import java.util.EventObject;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.event.EventTimerThread;
//import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;
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
 *    <ul><li>{@link GralGraphicTimeOrder#executeOrder(boolean)}.
 *    </ul> 
 *    That instance should be queued calling
 *    <ul><li>{@link #addDispatchOrder(GralGraphicTimeOrder)}. 
 *    </ul>
 *    With them the graphic thread is woken up
 *    because {@link #wakeup()} is called in the 'addDispatchOrder()'-routine. 
 *    <br><br>
 *    In the graphic thread execution loop the {@link #queueGraphicOrders} queue is checked 
 *    and all queued method are invoked. That executes the 'widget.setText(text)' or the other routines
 *    from the users programm in the {@link GralGraphicTimeOrder#executeOrder(boolean)}.
 *    <br><br>
 *    After the queue is checked the {@link #dispatchOsEvents()} is called. In SWT it calls the operation system
 *    dispatching loop. If the underlying graphic system has its own graphic dispatching thread that thread
 *    is woken up only to present the changes in the widgets. If all graphic dispatching is done, 
 *    {@link #graphicThreadSleep()} let this thread sleeping, its all done. 
 *    <br><br>
 *    The instance of {@link GralGraphicTimeOrder} will be remain in the queue. For single activities
 *    it should be queued out by itself calling its own {@link GralGraphicTimeOrder#removeFromList(GralGraphicThread)}
 *    method in its {@link GralGraphicTimeOrder#executeOrder(boolean)}-routine.
 *    Another possibility is to have instances of {@link GralGraphicTimeOrder} which are queued
 *    for any time. They are invoked whenever {@link #wakeup()} is called. 
 * <li><b>Version 2</b>: The order or commission can be instructed to the <code>setInfo(cmd, ...data)</code>-method
 *   of a {@link GralWidget}:
 *   <ul><li>{@link GralMng#setInfo(org.vishia.gral.base.GralWidget widget, int cmd, int ident, Object toshow, Object data)}
 *   </ul>
 *   This method fills a queue of the {@link GralMng}:
 *   <ul><li>{@link GralMng.WidgetChangeRequExecuter#guiChangeRequests}
 *     <li>{@link GralMng.WidgetChangeRequExecuter#executeOrder(boolean)} polls that queue.
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
public class GralGraphicThread 
{
  
  
  //protected GralPrimaryWindow window;
  
  //protected Runnable init;
  

  
  private ImplAccess impl;
  
  
  /**Constructs this class as superclass.
   * The constructor of the inheriting class has some more parameter to build the 
   * primary window. Therefore the {@link #threadGuiDispatch}.start() to start the {@link #runGraphicThread()}
   * method of this class should be invoked only in the derived constructor
   * after all parameter are saved to execute the overridden {@link #initGraphic()} method.
   * @param name Name of the thread.
   */
  public GralGraphicThread() //char size)
  { //sizeCharProperties = size;
  }
  
  
  
  
  /**This class is used only for the implementation level of the graphic. It is not intent to use
   * by any application. It is public because the implementation level should accesses it.
   */
  public static abstract class ImplAccess
  {
    
  }
  
}

