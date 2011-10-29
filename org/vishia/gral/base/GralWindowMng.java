package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralWidgetChangeRequ;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;

/**This is the manager of the graphic and the representation of the primary window in the Graphic Adaption Layer.
 * A primary window has its own thread, which runs the dispatch loop of window handling.
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
public abstract class GralWindowMng extends GralSubWindow implements GralPrimaryWindow_ifc
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
  
  public final GralGraphicThread graphicThread;
  

  
  /**This class should contain the basic graphic implementation (main window, graphic device etc.)
   * and should be enhanced by the implementation (swing, SWT).
   * The here called run mehtod is the graphic thread. It can (not have to be) used for all graphic actions. 
   * If the user calls any action which deals with graphical widgets, SWT expects that it is done in the graphic thread.
   * Swing isn't threadsafe, so it should be done in the graphic thread. To support calling from any other thread,
   * some mechanism of queuing and notifying are existing in the gral adaption.
   * 
   * 
   */
  
  //protected final Thread guiThread;

  
  
  //-----------------------------------------------------------------------------------------------------
  //Abstract method definitions:

  //public abstract void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize);

  //protected abstract void dispatch();
    
  
  public GralWindowMng(GralWidgetMng gralMng, GralGraphicThread graphicThread)
  { super("primaryWindow", gralMng);
    this.graphicThread = graphicThread;
  }
  
  /**This method should be called from the implementing class on creation of the main window.
   * @param init run-Method to initialize the graphic.
   * @return The tread to deal and store in the instance
  protected static GuiThread startGraphicThread(GuiThread thread)
  {
    //GuiThread thread = new GuiThread("GUI", init);
    thread.start();
    synchronized(thread){
      while(thread.init != null){
        try{ thread.wait(100);} catch(InterruptedException exc){}
      }
    }
    return thread;
  }
   */
  
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
  //abstract protected Object initGraphic();
  

  
  //------------------------------------------------------------------------------------------
  //get methods
  
  @Override public long getThreadIdGui(){ return graphicThread.guiThreadId; }


  
  /**Adds a order for building the gui. The order will be execute one time after intializing the Graphic
   * and before entry in the dispatch loop. After usage the orders will be removed,
   * to force garbage collection for the orders.
   * @param order
  public void addGuiBuildOrder(Runnable order)
  { graphicThread.buildOrders.add(order);
  }
   */
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GralDispatchCallbackWorker listener)
  { graphicThread.dispatchListeners.remove(listener);
  }
  
  

  
  
  /** Adds a method which will be called in anytime in the dispatch loop until the listener will remove itself.
   * @see org.vishia.gral.ifc.GralWindowMng_ifc#addDispatchListener(org.vishia.gral.ifc.GralDispatchCallbackWorker)
   * @param listener
   */
  public void addDispatchListener(GralDispatchCallbackWorker listener)
  { graphicThread.dispatchListeners.add(listener);
    //it is possible that the GUI is busy with dispatching and doesn't sleep yet.
    //therefore:
    graphicThread.extEventSet.getAndSet(true);
    if(graphicThread.bStarted){
      
      graphicThread.wakeup();  //to wake up the GUI-thread, to run the listener at least one time.
    }
  }
  
  
  public void wakeup(){
    graphicThread.extEventSet.set(true);
    graphicThread.isWakedUpOnly = true;
    graphicThread.wakeup();
  }
  

  
  void processBuildOrders()
  {
  }
  
  public boolean isStarted(){ return graphicThread.bStarted; }
  
  public boolean isRunning(){ return graphicThread.bStarted && !graphicThread.bExit; }
  
  public boolean isTerminated(){ return graphicThread.bStarted && graphicThread.bExit; }


  
  
}
