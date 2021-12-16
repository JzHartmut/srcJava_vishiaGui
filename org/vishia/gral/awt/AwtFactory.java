package org.vishia.gral.awt;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.msgDispatch.LogMessage;



public class AwtFactory  extends GralFactory
{

  
  @Override protected GralGraphicThread createGraphic(GralWindow windowg, char sizeShow, LogMessage log){
    AwtGraphicThread graphicThread = new AwtGraphicThread(windowg, sizeShow, windowg.gralMng().log);
    GralGraphicThread gralGraphicThread = graphicThread.gralGraphicThread();
    return gralGraphicThread;
  }

  
}
