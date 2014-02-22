package org.vishia.gral.base;

import org.vishia.util.OrderForList;


/**This is the base class for user classes, which contains code, that is executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.  
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralDispatchCallbackWorker extends OrderForList
{
  
  

  public GralDispatchCallbackWorker(String name)
  { super(name);
  }
  
	

}
