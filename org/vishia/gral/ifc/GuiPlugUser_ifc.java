package org.vishia.gral.ifc;

import org.vishia.msgDispatch.LogMessage;



/**This interface plugs an user application to the basic GUI application. */ 
public interface GuiPlugUser_ifc
{

  //void init(LogMessage log);
  
  void init(UserPlugGral_ifc inspc, LogMessage log);

  
  void registerMethods(org.vishia.gral.ifc.GuiPanelMngBuildIfc guiMng);
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);

}
