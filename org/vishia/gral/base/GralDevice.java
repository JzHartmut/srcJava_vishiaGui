package org.vishia.gral.base;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.gridPanel.GralGridMngBase.GuiChangeReq;

/**Any instance of a {@link GralGridMngBase} refers only one instance of this class.
 * 
 * @author Hartmut Schorrig
 * @since 2011-05-01
 */
public class GralDevice
{
  /**The thread id of the managing thread for GUI actions. */
  public long guiThreadId;

  public ConcurrentLinkedQueue<GuiChangeReq> guiChangeRequests = new ConcurrentLinkedQueue<GuiChangeReq>();
  
  public long getThreadIdGui(){ return guiThreadId; }

  public GralDevice()
  {
    guiThreadId = Thread.currentThread().getId();
  }
  
  
  
}
