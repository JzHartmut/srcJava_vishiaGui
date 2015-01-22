package org.vishia.gral.awt;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;

public class AwtFactory  implements GralFactory_ifc
{
  @Override public GralWindow createWindow(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  {
    GralMng.create(log);
    AwtPrimaryWindow window = AwtPrimaryWindow.create(log, sTitle, sizeShow, left, top, xSize, ySize);
    return window;
  }
 
  
  @Override public void createWindow(GralWindow windowg, char sizeShow, int left, int top, int xSize, int ySize)
  {
    AwtPrimaryWindow window = AwtPrimaryWindow.create(GralMng.get().log, "sTitle", sizeShow, left, top, xSize, ySize);
  }
  

  
}
