package org.vishia.gral.base;

import java.util.EventObject;

import org.vishia.event.EventConsumer;
import org.vishia.event.EventTimeOrder;


/**This is the base class for user classes, which contains code, that should be executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.
 * Override the method {@link #executeOrder()}!
 *   
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralGraphicTimeOrder extends EventTimeOrder
{
  
  /**Version and history.
   * <ul>
   * <li>2015-01-10 Hartmut renamed from <code>GralDispatchCallbackWorker</code>
   * <li>2012-02-14 Hartmut corr: {@link #addToGraphicThread(GralGraphicThread, int)}:
   *   For time saving: If an instance is added already and its new execution time
   *   is up to 5 ms later, nothing is done. It saves time for more as one call of this routine
   *   in a fast loop.
   * <li>2012-02-14 Hartmut corr: It was happen that an instance was designated with {@link #bAdded}==true
   *   but it wasn't added, Therefore on {@link #addToGraphicThread(GralGraphicThread, int)} it is removed
   *   from list and added newly. It is more save. 
   * <li>2012-01-26 Hartmut chg: rename removeFromGraphicThread(GralGraphicThread) 
   *   to {@link #removeFromQueue(GralGraphicThread)}. It is a better naming because it is removed from the queue
   *   in the graphic thread. This class is only used in that queue. 
   * <li>2012-01-15 Hartmut new: {@link #name} to identify, proper for debugging
   * <li>2012-01-08 Hartmut new: {@link #addToGraphicThread(GralGraphicThread, int)} and 
   *   {@link #removeFromQueue(GralGraphicThread)} as thread-safe functions which 
   *   marks the instance as added (for delayed execution, for re-using).
   * <li>2011-02-21 Hartmut created.
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
   * <li> But the LPGL is not appropriate for a whole software product,
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
  public final static String version = "2015-01-17";

  
  private static final long serialVersionUID = 1L;

  private static class EnqueueInGraphicThread implements EventConsumer {

    @Override public int processEvent(EventObject ev)
    {
      //the manager is known application global
      GralMng mng = GralMng.get();  //the singleton.
      if(mng !=null) {
        mng.gralDevice().storeEvent(ev);
      }
      return 1;
    }

    @Override public String getStateInfo()
    {
      // TODO Auto-generated method stub
      return "";
    }
    
  }
  
  

  /**Super constructor for all graphic time orders.
   * Usual a anonymous class is used for the instance: <pre>
  private final GralGraphicTimeOrder repaintRequ = new GralGraphicTimeOrder("GralWidget.repaintRequ"){
    QOverride public void executeOrder() {
      repaintGthread();
    }
    QOverride public String toString(){ return name + ":" + GralWidget.this.name; }
  };
   * </pre>  
   * @param name The name is only used for showing in debugging.
   */
  protected GralGraphicTimeOrder(String name)
  { super(name, new EnqueueInGraphicThread(), GralMng.get().gralDevice.orderList);
  }
  

  /**This package private routine is invoked only in the graphic thread. It calls {@link #doExecute()}
   * which is protected in the super class. */
  void doExecuteInGraphicThread(){ super.doExecute(); }
  
  
  /**Activates the graphic order to execute immediately as next call in the graphic thread. */
  public void activate(){
    GralMng mng = GralMng.get();  //the singleton.
    mng.gralDevice().storeEvent(this);
  }
  
}
