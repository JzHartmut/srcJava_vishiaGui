package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Composite;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GralArea9Window;
import org.vishia.gral.area9.GralArea9_ifc;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainGuiSwt.SwtWidgetMng;
import org.vishia.mainGuiSwt.PrimaryWindowSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

public class FactorySwt implements GralFactory_ifc
{
  
  GralArea9Window gui;
  

  @Override public GralWindow createWindow(LogMessage log, String sTitle, int left, int top, int xSize, int ySize)
  {
    PrimaryWindowSwt swtWindow = PrimaryWindowSwt.create(log, sTitle, left, top, xSize, ySize);
    return swtWindow;
  }
  

}
