package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Composite;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindowMng;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainGuiSwt.SwtWidgetMng;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PrimaryWindowSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

public class FactorySwt implements GralFactory_ifc
{
  
  MainCmdSwt gui;
  
  /**One instance per factory class for possible more as one windows.
   */
  GralWindowMng gralDevice;
  
  //PropertiesGuiSwt propertiesGui;

  @Override public GuiMainAreaifc createGuiWindow(MainCmd cmdP, String sTitle, int left, int top, int xSize, int ySize, String sOutputArea)
  {
    GralWindowMng swtWindow = PrimaryWindowSwt.create(cmdP.getLogMessageOutputConsole(), sTitle, left, top, xSize, ySize);

    gui = new MainCmdSwt(cmdP, swtWindow, sOutputArea);
    gralDevice = gui.getPrimaryWindow();
    if(gralDevice ==null){
      //gralDevice = new GralDeviceSwt();
    }
    return gui; // = new MainCmdSwt(cmdP, gralDevice);
  }

  @Override public GralGridProperties createProperties(char sizePixel)
  {
    return new PropertiesGuiSwt(gui.getDisplay(), sizePixel);
  }
  
  
  @Override public GralWidgetMng createPanelMng(GralWidgetMng parent, int width, int height
  , GralGridProperties propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log)
  {
    return null; //new GuiPanelMngSwt(gralDevice, width, height , (PropertiesGuiSwt)propertiesGui, variableContainer, log);
  }
  

}
