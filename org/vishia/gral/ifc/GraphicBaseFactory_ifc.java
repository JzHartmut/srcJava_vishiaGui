package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.gridPanel.GuiPanelMngBase;
import org.vishia.gral.gridPanel.PropertiesGui;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;

/**This is the interface to a factory class which allows usage of any graphical base system
 * such as Swing, SWT or other for the area9-gui and for the grid panel manager
 * @author Hartmut Schorrig
 *
 */
public interface GraphicBaseFactory_ifc
{

  GuiMainAreaifc createGuiWindow(MainCmd cmdP);
  
  PropertiesGui createProperties(char sizePixel);
  
  GuiPanelMngBase createPanelMng(GuiPanelMngBase parent, int width, int height
  , PropertiesGui propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log);
      
}
