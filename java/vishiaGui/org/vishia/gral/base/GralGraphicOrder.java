package org.vishia.gral.base;

import java.util.EventObject;

import org.vishia.event.EventConsumer;
import org.vishia.event.EventConsumerAwait;
import org.vishia.event.EventThread_ifc;
import org.vishia.event.TimeOrder;
import org.vishia.event.EventWithDst;
import org.vishia.event.Payload;


/**This is the base class for user classes, which contains code, that should be executed in the graphic thread.
 * <br>
 * This class is both, an EventConsumer to {@link EventConsumer#processEvent(EventObject)} for the graphic (the first task)
 * as also contains the data for the graphic event by implementation of {@link Payload}.
 * <ul>
 * <li>In this manner the activator of the event can store some data for event execution in the order, 
 * <li>then send the event to the graphic thread, the event refers this as payload.
 * <li>then process the event exact with the same instance.
 * </ul>
 * The instance of this class contains the {@link TimeOrder} which is necessary for the management of the execution time 
 * using the {@link GralMng} as {@link org.vishia.event.EventTimerThread}.
 * The time order facility can be used to send the event not immediately but with delay,
 * so that a renew of the order can be done if the time is not yet elapsed.
 * <br>
 * That are all necessities for a graphic oder.
 * <br>
 * Because this class inherits from {@link EventConsumer} the operation {@link EventConsumer#processEvent(EventObject)}
 * need to be overridden in the inherit class.. Whereby the argument event is type of the overridden type itself.
 * Hence it is not necessary to evaluate, it is this itself.
 * The 'processEvent(...)' does the necessary work.
 * <br><br>
 * The event instance itself of type 'EventWithDst<GralGraphicOrder, ?>', see {@link EventWithDst}.
 * is also referenced as composite here, hence any possible order has its specific event. 
 * It is used and activated if the source of the event becomes active. 
 * <br><br>
 * Because the event is used on the same device between threads, communication via a serial line is not necessary.
 * Hence the {@link Payload#serialize()} and ~deserialize() are dummy implementations. 
 * <br>
 *   
 * @author Hartmut Schorrig.
 *
 */
@SuppressWarnings("serial") 
public abstract class GralGraphicOrder /*extends EventConsumerAwait<GralGraphicOrder, ?>*/ implements EventConsumer, Payload
{
  
  /**Version and history.
   * <ul>
   * <li>2023-07-24 Hartmut correct concept of 2023-02-01: Because now {@link EventConsumerAwait} 
   *   needs a Payload derived from {@link org.vishia.event.PayloadBack} and this is as first type argument the GralGraphicOrder itself,
   *   the formally refactoring was not possible (two extends will be necessary, from  GralGraphicOrder extends PayloadBack extends EventConsumerAwait).
   *   But it is substantial a false concept, because this class is not a consumer of a callback event, 
   *   it is a consumer of a forwarded command event. The {@link EventConsumerAwait#awaitExecution(long, boolean)} was never used also.
   *   <br>
   *   It was a slightly refactoring to implement a simple EventConsumer instead the EventConsumerAwait for all stuff.
   * <li>2023-02-21 Hartmut chg renamed from GralGraphicEventTimeOrder
   *   and changed of concept. This order refers now the event, it is not an event. 
   *   This was also done because deviation from {@link EventConsumerAwait} for {@link #awaitExecution(long)}
   *   as essential operation. All is adapted and runs. 
   * <li>2023-02-12 Hartmut new {@link #setStackInfo()} and {@link #sInfo} possible as information from the event producer
   *   to the event dst to inform from where and why comes the event proper usable to search and improve in software.
   * <li>2023-02-08 Hartmut now because the GralMng is also the {@link org.vishia.event.EventTimerThread}
   *   the operation EnqueueInGraphicThread is no more necessary. The event can immediately executed in the graphic thread.
   *   
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
  public final static String version = "2023-02-12";

  
  final GralMng gralMng;
  
  String sInfo;
  
  
  

  
//  public static class GraphicEvent extends EventWithDst {
//    
//    final GralGraphicOrder graphicOrder;
//    
//    GraphicEvent ( String name, GralGraphicOrder to){
//      super(name, to.gralMng, to.gralMng.evSrc, to, to.gralMng);  // use the outer instance to as EventConsumer, calls the overridden operation processEvent(...)
//      this.graphicOrder = to;
//    }
//  }

  
  final EventWithDst<GralGraphicOrder, ?> ev;
  
  //final GraphicEvent ev;
  
  final TimeOrder timeOrder;

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
  protected GralGraphicOrder ( String name, GralMng gralMng) { 
    //super(gralMng);                              // gralMng is the eventThread.
    this.gralMng = gralMng;
    this.ev = new EventWithDst<GralGraphicOrder, Payload>(name, gralMng.evSrc, this, gralMng, this); //new GraphicEvent(name, this);
    this.timeOrder = new TimeOrder(name, gralMng, this.ev); //this.ev.timeOrder;
  }
  
  
  /**Set the info from where the call comes (on calling this operation):
   */
  public void setStackInfo ( ) {
    this.sInfo = org.vishia.util.ExcUtil.stackInfo(" src:", 4, 10).toString();
  }

  /**Used for debugging and messaging: gets an information from where the order was created.
   * @return
   */
  public String srcInfo ( ) { return this.sInfo; }

  
  @Override public EventThread_ifc evThread () {
    return this.gralMng;
  }

  
  
  @Override public GralGraphicOrder clean () {
    this.sInfo = "?";
    return this;
  }


  @Override public byte[] serialize () {
    return null;
  }


  @Override public boolean deserialize ( byte[] data ) {
    return false;
  }

  
}
