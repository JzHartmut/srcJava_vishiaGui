package org.vishia.guiInspc;

import org.vishia.inspectorAccessor.InspcAccessor;

public interface InspcPlugUser_ifc
{
  
  void init(InspcAccessor inspcAccessorP);

  
  
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
