package org.vishia.gral.swt;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.msgDispatch.LogMessage;

public class SwtFactory extends GralFactory
{
  
  
  @Override protected GralGraphicThread createGraphic(GralWindow windowg, char sizeShow, int left, int top, int xSize, int ySize, LogMessage log){
    SwtGraphicThread graphicThread = new SwtGraphicThread(windowg, sizeShow, left, top, xSize, ySize, log);
    GralGraphicThread gralGraphicThread = graphicThread.gralGraphicThread();
    return gralGraphicThread;
  }


}
