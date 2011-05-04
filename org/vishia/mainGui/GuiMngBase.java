package org.vishia.mainGui;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.mainGuiSwt.GuiPanelMngSwt.GuiChangeReq;

/**Any instance of a {@link GuiPanelMngBase} refers only one instance of this class.
 * 
 * @author Hartmut Schorrig
 * @since 2011-05-01
 */
public class GuiMngBase
{

  public ConcurrentLinkedQueue<GuiChangeReq> guiChangeRequests = new ConcurrentLinkedQueue<GuiChangeReq>();
  

  
}
