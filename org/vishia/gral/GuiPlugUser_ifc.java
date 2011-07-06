package org.vishia.gral;

import org.vishia.msgDispatch.LogMessage;

public interface GuiPlugUser_ifc
{

  void init(LogMessage log);
  
  
  void registerMethods(org.vishia.gral.GuiPanelMngBuildIfc guiMng);
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);

}
