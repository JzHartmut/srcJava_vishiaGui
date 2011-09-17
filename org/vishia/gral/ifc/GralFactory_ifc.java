package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.gridPanel.GralGridProperties;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;

/**This is the interface to a factory class which allows usage of any graphical base system
 * such as Swing, SWT or other for the area9-gui and for the grid panel manager
 * @author Hartmut Schorrig
 *
 */
public interface GralFactory_ifc
{

  GuiMainAreaifc createGuiWindow(MainCmd cmdP);
  
  GralGridProperties createProperties(char sizePixel);
  
  GralGridMngBase createPanelMng(GralGridMngBase parent, int width, int height
  , GralGridProperties propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log);
      
}
