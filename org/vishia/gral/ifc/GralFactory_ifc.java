package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GralArea9_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;

/**This is the interface to a factory class which allows usage of any graphical base system
 * such as Swing, SWT or other for the area9-gui and for the grid panel manager
 * @author Hartmut Schorrig
 *
 */
public interface GralFactory_ifc
{

  GralWindow createWindow(LogMessage log, String sTitle, int left, int top, int xSize, int ySize);
  
  /*
  GralGridProperties createProperties(char sizePixel);
  
  GralWidgetMng createPanelMng(GralWidgetMng parent, int width, int height
  , GralGridProperties propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log);
  */  
}
