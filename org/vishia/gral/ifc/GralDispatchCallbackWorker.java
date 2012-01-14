package org.vishia.gral.ifc;

import org.vishia.gral.base.GralGraphicThread;

/**This is the base class for user classes, which contains code, that is executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.  
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralDispatchCallbackWorker 
{
  
  /**Version and history:
   * <ul>
   * <li>2012-01-08 Hartmut new: {@link #addToGraphicThread(GralGraphicThread, int)} and 
   *   {@link #removeFromGraphicThread(GralGraphicThread)} as thread-safe functions which 
   *   marks the instance as added (for delayed execution, for re-using).
   * <li>2010-06-00 Hartmut created.
   * </ul> */
  private final static int version = 0x20120108;
  
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
  

	public abstract void doBeforeDispatching(boolean onlyWakeup);
	
	
	/**Adds to the graphic thread or sets a new delay it is added already.
	 * @param dst The graphic thread.
	 * @param delay time in milliseconds for delayed execution or 0.
	 */
	synchronized public void addToGraphicThread(GralGraphicThread dst, int delay){
	  if(!bAdded){
	    dst.addDispatchOrder(this);
	    bAdded = true;
	  }
	  if(delay >0){
	    dbgctWindup +=1;
	    delayExecution(delay);
	  } else {
	    timeExecution = 0;
	  }
	}
	
	
	
	synchronized public void removeFromGraphicThread(GralGraphicThread dst){
	  bAdded = false;
	  dst.removeDispatchListener(this);
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
  public void delayExecution(int millisec){
    timeExecution = System.currentTimeMillis() + millisec;
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
  



}
