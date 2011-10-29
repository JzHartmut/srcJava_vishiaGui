package org.vishia.gral.ifc;

import org.vishia.msgDispatch.LogMessage;



/**This interface plugs an user application to the basic GUI application. */ 
public interface GralPlugUser_ifc
{

  //void init(LogMessage log);
  
  void init(GralPlugUser2Gral_ifc plugUser2Gui, LogMessage log);

  
  void registerMethods(org.vishia.gral.ifc.GralGridBuild_ifc guiMng);
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);

}
