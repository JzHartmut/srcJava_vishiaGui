package org.vishia.guiBzr;

import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainGui.GuiMainAreaifc;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;

public class MainData
{
  
  final MainCmd_ifc mainCmdifc;

  GuiPanelMngWorkingIfc panelAccess;
  
  GuiMainAreaifc guifc;
  
  /**The current selected software project. */
  DataProject currPrj;
  
  /**The current selected component. */
  DataCmpn currCmpn;
  
  final BzrGetStatus getterStatus;
  
  /**Only one command invocation should be active in one time. */
  final ProcessBuilder cmdMng = new ProcessBuilder("");
  
  

  /**Data of the currently selected component.
   * 
   */
  DataCmpn selectedCmpn;
  
  
  MainData(MainCmd_ifc mainCmd)
  {
    this.mainCmdifc = mainCmd;
    getterStatus = new BzrGetStatus(mainCmd, this);

  }
  
  
}
