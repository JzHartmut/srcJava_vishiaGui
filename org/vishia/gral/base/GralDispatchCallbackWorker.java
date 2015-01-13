package org.vishia.gral.base;

import java.util.EventObject;

import org.vishia.event.EventConsumer;
import org.vishia.event.TimeOrderBase;


/**This is the base class for user classes, which contains code, that is executed in the graphic thread,
 * any-time when any graphic dispatching occurs. Especially it is used for SWT.  
 * @author Hartmut Schorrig.
 *
 */
public abstract class GralDispatchCallbackWorker extends TimeOrderBase
{
  
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
  
  

  public GralDispatchCallbackWorker(String name)
  { super(name, new EnqueueInGraphicThread());
  }
  

}
