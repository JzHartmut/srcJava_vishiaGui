package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Composite;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.gridPanel.GuiPanelMngBase;
import org.vishia.gral.gridPanel.PropertiesGui;
import org.vishia.gral.ifc.GraphicBaseFactory_ifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

public class FactorySwt implements GraphicBaseFactory_ifc
{
  
  MainCmdSwt gui;
  
  //PropertiesGuiSwt propertiesGui;

  @Override public GuiMainAreaifc createGuiWindow(MainCmd cmdP)
  {
    // TODO Auto-generated method stub
    return gui = new MainCmdSwt(cmdP);
  }

  @Override public PropertiesGui createProperties(char sizePixel)
  {
    return new PropertiesGuiSwt(gui.getDisplay(), sizePixel);
  }
  
  
  @Override public GuiPanelMngBase createPanelMng(GuiPanelMngBase parent, int width, int height
  , PropertiesGui propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log)
  {
    return new GuiPanelMngSwt(parent, gui.getContentPane(), width, height
        , (PropertiesGuiSwt)propertiesGui, variableContainer, log);
  }
  

}
