package org.vishia.gral.swt;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.msgDispatch.LogMessage;

public class SwtFactory extends GralFactory
{
  
  
  @Override protected GralGraphicThread createGraphic(GralWindow windowg, char sizeShow, LogMessage log){
    SwtGraphicThread graphicThread = new SwtGraphicThread(windowg, sizeShow, log);
    GralGraphicThread gralGraphicThread = graphicThread.gralGraphicThread();
    return gralGraphicThread;
  }


}
