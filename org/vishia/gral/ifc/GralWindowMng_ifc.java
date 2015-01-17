package org.vishia.gral.ifc;

import org.vishia.gral.base.GralGraphicTimeOrder;

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
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120303;

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
  void addDispatchListener(GralGraphicTimeOrder listener);
  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GralGraphicTimeOrder listener);
  
  
}
