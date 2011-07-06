package org.vishia.guiInspc;

import java.util.Map;

import org.vishia.gral.GuiPlugUser_ifc;
import org.vishia.inspectorAccessor.InspcAccessor;
import org.vishia.msgDispatch.LogMessage;

public interface InspcPlugUser_ifc extends GuiPlugUser_ifc
{
  
  void init(UserInspcPlug_ifc inspc, LogMessage log);

  void setInspcComm(InspcGuiComm inspcCommP);
  
  /**This method is called periodically on start of requesting data all widgets in visible windows.
   * It is possible to get some special data here.
   * @param ident Any identification depending of the caller. It should be understand by the user algorithm.
   */
  void requData(int ident);
  
  
  
  void isSent(int seqnr);
  
}
