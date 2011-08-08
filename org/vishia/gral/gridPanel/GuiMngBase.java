package org.vishia.gral.gridPanel;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.gral.base.GralDevice;
import org.vishia.mainGuiSwt.GuiPanelMngSwt.GuiChangeReq;

/**Any instance of a {@link GuiPanelMngBase} refers only one instance of this class.
 * 
 * @author Hartmut Schorrig
 * @since 2011-05-01
 */
public class GuiMngBase
{
  public final GralDevice gralDevice;

  public ConcurrentLinkedQueue<GuiChangeReq> guiChangeRequests = new ConcurrentLinkedQueue<GuiChangeReq>();
  
  public long getThreadIdGui(){ return gralDevice.guiThreadId; }

  public GuiMngBase(GralDevice gralDevice)
  {
    super();
    this.gralDevice = gralDevice;
  }
  
  
  
}
