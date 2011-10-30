package org.vishia.gral.swt;

import org.vishia.gral.area9.GralArea9Window;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.mainGuiSwt.PrimaryWindowSwt;
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
