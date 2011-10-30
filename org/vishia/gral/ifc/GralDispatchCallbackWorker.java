package org.vishia.gral.ifc;

/**This is the base class for user classes, which contains code, that is executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.  
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralDispatchCallbackWorker 
{
	private int ctDone = 0;
	private boolean reqCtDone = false;

	public abstract void doBeforeDispatching(boolean onlyWakeup);
	
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
	{
		ctDone +=1;
		if(reqCtDone){
			notify();
		}
		
	}
	
	
	
	/**waits for execution. This method can be called in any thread, especially in that thread, 
	 * which initializes the request.
	 */
  /**
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
