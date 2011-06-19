package org.vishia.guiInspc;

import java.util.Map;

import org.vishia.inspectorAccessor.InspcAccessor;
import org.vishia.msgDispatch.LogMessage;

public interface InspcPlugUser_ifc
{
  
  void init(UserInspcPlug_ifc inspc, LogMessage log);

  void setInspcComm(InspcGuiComm inspcCommP);
  
  void registerMethods(org.vishia.mainGui.GuiPanelMngBuildIfc guiMng);
  
  /**This method is called periodically on start of requesting data all widgets in visible windows.
   * It is possible to get some special data here.
   * @param ident Any identification depending of the caller. It should be understand by the user algorithm.
   */
  void requData(int ident);
  
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);
  
  void isSent(int seqnr);
  
}
