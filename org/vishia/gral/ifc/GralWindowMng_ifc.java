package org.vishia.gral.ifc;

import org.vishia.gral.base.GralDispatchCallbackWorker;

/**This interface supports to deal with the graphical implementation, especially the graphic thread.
 * The graphic thread is associated with a graphic device usually. The graphic device handles
 * all window appearances and events.
 * <br><br>
 * Events usual calls methods from the user because the methods are registered to the events. 
 * Therefore some actions in user space are done in the graphic thread. If there are not additional actions,
 * all operations with a GUI like Button press, mouse move, input in fields act in the graphic thread.
 * This actions may block the execution of the application. Therefore actions which waits for something etc
 * should run in extra threads. The adaption of a graphic device supports a queue mechanism
 * to send events to the graphic from any other thread.
 * <br><br>
 * In the eclipse.SWT graphic the actions with SWT-widgets tests the thread and causes an exception on error.
 * In the Swing graphic some actions are possible to call in another thread, but they are not threadsafe.
 * The gral adaption helps to handle that problems.
 * 
 * @author Hartmut Schorrig
 *
 */
public interface GralWindowMng_ifc
{
  /**Starts the execution of the graphic initializing and handling in a own thread.
   * The following sets should be called already:
   * <pre>
   * setTitleAndSize("Title bar text", 800, 600);  //This instruction should be written first to output syntax errors.
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
  //boolean buildMainWindow(String sTitle, int x, int y, int dx, int dy);

  
  /**Returns the identification number of the already started graphic thread. This number can be compared
   * with the own thread id (java.lang.Thread.getId()). If there are equal, the current thread is the graphic thread.
   * 
   * @return The id of the graphic thread or 0 if it isn't started.
   */
  long getThreadIdGui();
  
  /**Returns true if the graphical application runs. 
   * @return
   */
  boolean isRunning();
  
  void exit();
  
  /**Adds a order for building the gui. The order will be execute one time after intializing the Graphic
   * and before entry in the dispatch loop. After usage the orders will be removed,
   * to force garbage collection for the orders.
   * @param order
   */
  //public void addGuiBuildOrder(Runnable order);

  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  void addDispatchListener(GralDispatchCallbackWorker listener);
  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener);
  
  
}
