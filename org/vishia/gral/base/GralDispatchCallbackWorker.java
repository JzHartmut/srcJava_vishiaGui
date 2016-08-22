package org.vishia.gral.base;


/**This is the base class for user classes, which contains code, that is executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.  
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralDispatchCallbackWorker 
{
  
  /**Version and history:
   * <ul>
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
   * <li>2010-06-00 Hartmut created.
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
  public final static int version = 0x20120303;
  
  /**The name of the dispatch worker, used for debug at least. */
  public final String name;
  
	private int ctDone = 0;
	
	/**It is counted only. Used for debug. Possible to set. */
	public int dbgctDone = 0;
	
	/**It is counted only. Used for debug. Possible to set. */
  public int dbgctWindup = 0;
	
	/**True if a thread waits, see {@link #awaitExecution(int, int)}. */
	private boolean reqCtDone = false;

	private boolean bAdded;
	
  /**If not 0, it is the first time to execute it. Elsewhere it should be delayed. */
  private long timeExecution;
  

  public GralDispatchCallbackWorker(String name)
  {
    this.name = name;
  }
  
	/**Handle any request in the graphic thread before the system's dispatching routine starts.
	 * @param onlyWakeup
	 */
	public abstract void doBeforeDispatching(boolean onlyWakeup);
	
	
	/**Runs the handling of delayed requests inside this.
	 * Any graphic order can have a queue of delayed requests.
	 * @param timeDelay The up to now delay in milliseconds, when the timer thread
	 *   should be waked up to handle a delayed request.
	 * @return The new delay in milliseconds maybe less than the input timeDelay
	 *   to execute the next call of this method. It is less or equal the inputed timeDelay.
	 */
	public int runTimer(int timeDelay){ return timeDelay; }
	
	
	/**Adds to the graphic thread or sets a new delay if is added already.
	 * If it is added already and the new time to execution is in range max 5 ms later, this routine does nothing.
	 * @param dst The graphic thread.
	 * @param delay time in milliseconds for delayed execution or 0.
	 */
	synchronized public void addToGraphicThread(GralGraphicThread dst, int delay){
	  long timeExecution1 = System.currentTimeMillis() + delay;
	  if(bAdded){
	    long timediff = timeExecution1 - timeExecution;
	    if(timediff >0 && timediff < 5){ //later, max 5 ms difference,
	      return;            //do nothing, it is added already.
	    }
	    //remove and add new, because its state added in queue or not may be false.
	    dst.removeDispatchListener(this);
	  }
	  //if(!bAdded){
	  //}
	  if(delay >0){
	    dbgctWindup +=1;
	    delayExecution(delay);
	  } else {
	    timeExecution = 0;  //execute at next possible time.
	  }
    dst.addDispatchOrder(this);
    bAdded = true;
	}
	
	
	
	/**Remove this from the queue of dispatch callbacks which are executed in any loop of the
	 * graphic thread.
	 * @param graphicThread it is the singleton instance refered with {@link GralMng#gralDevice}.
	 */
	synchronized public void removeFromQueue(GralGraphicThread graphicThread){
	  bAdded = false;
	  graphicThread.removeDispatchListener(this);
	}
	
	
	/**Gets the information, how many times the routine is executed.
	 * Especially it is for quest, whether it is executed 1 time if it is a single-execution-routine.
	 * Note that the method should be thread-safe, use synchronized in the implementation.
	 * @param setCtDone set the count for a new execution-counting. For example 0.
	 * @return The number of times of execution after initializing or after last call of this method.
	 */
	synchronized public int getCtDone(int setCtDone) {
		//reqCtDone = true;   //it is to notify, if this routine is called.
		int ctDone = this.ctDone;
		if(setCtDone >=0){ 
			this.ctDone = setCtDone; 
		}
		return ctDone;
	}

	protected synchronized void countExecution()
	{ dbgctDone +=1;
		ctDone +=1;
		if(reqCtDone){
			notify();
		}
		
	}
	
  /**Checks whether it should be executed.
   * @return time in milliseconds for first execution or value <0 to execute immediately.
   */
  public int timeToExecution(){ 
    return timeExecution == 0 ? -1 : (int)( timeExecution - System.currentTimeMillis()); 
  }
  
  
  /**Sets the delay to execute. It can be set newly whenever this instance isn't used to execute yet.
   * @param millisec delay.
   */
  private long delayExecution(int millisec){
    return System.currentTimeMillis() + millisec;
  }
  

	
	/**waits for execution. This method can be called in any thread, especially in that thread, 
	 * which initializes the request.
	 * @param ctDoneRequested Number of executions requested.
   * @param timeout maximal waiting time in millisec, 0 means wait for ever for execution.
   * @return true if it is executed the requested number of.
   */
  public synchronized boolean awaitExecution(int ctDoneRequested, int timeout)
  { 
  	long timeEnd = System.currentTimeMillis() + timeout; 
  	boolean bWait;
  	do {
  		if(this.ctDone < ctDoneRequested ){
	  		reqCtDone = true;
	  		long waitingTime = timeEnd - System.currentTimeMillis();
	  		if(waitingTime > 0 || timeout == 0){
	  		  try{ wait(timeout); } catch(InterruptedException exc){}
	  		  bWait = true;
	  		} else bWait = false;
  	  } else bWait = false;
  	} while(bWait);
  	return(this.ctDone >= ctDoneRequested);
  }
  

@Override public String toString(){ return name; }  


}
