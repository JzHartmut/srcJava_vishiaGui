package org.vishia.guiInspc;

import org.vishia.inspectorAccessor.InspcAccessor;

public interface InspcPlugUser_ifc
{
  
  void init(InspcAccessor inspcAccessorP);

  
  
  void registerMethods(org.vishia.mainGui.GuiPanelMngBuildIfc guiMng);
  
  /**This method may be called periodically if widgets should be shown.
   * @param ident Any identification depending of the caller. It should be understand by the user algorithm.
   */
  void requData(int ident);
  
  void isSent(int seqnr);
  
}
