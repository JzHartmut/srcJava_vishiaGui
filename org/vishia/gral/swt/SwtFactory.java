package org.vishia.gral.swt;

import org.vishia.gral.area9.GralArea9Window;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;

public class SwtFactory implements GralFactory_ifc
{
  
  GralArea9Window gui;
  

  @Override public GralWindow createWindow(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  {
    GralMng.create(log);
    int windProps = GralWindow_ifc.windResizeable;
    GralWindow window = new GralWindow("!", "primaryWindow", sTitle, windProps, null, null);
    SwtGraphicThread graphicThread = new SwtGraphicThread(window, sizeShow, left, top, xSize, ySize, log);
    GralGraphicThread gralGraphicThread = graphicThread.gralGraphicThread();
    //The graphicthread creates the Swt Window.
    //SwtPrimaryWindow swtWindow = SwtPrimaryWindow.create(log, sTitle, sizeShow, left, top, xSize, ySize);
    synchronized(gralGraphicThread){
      while(gralGraphicThread.getThreadIdGui() == 0){
        try{ gralGraphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
    return window;
  }
  

  @Override public void createWindow(GralWindow windowg, char sizeShow, int left, int top, int xSize, int ySize)
  {
    SwtGraphicThread graphicThread = new SwtGraphicThread(windowg, sizeShow, left, top, xSize, ySize, windowg.gralMng().log);
    GralGraphicThread gralGraphicThread = graphicThread.gralGraphicThread();
    //The graphicthread creates the Swt Window.
    //SwtPrimaryWindow swtWindow = SwtPrimaryWindow.create(log, sTitle, sizeShow, left, top, xSize, ySize);
    synchronized(gralGraphicThread){
      while(gralGraphicThread.getThreadIdGui() == 0){
        try{ gralGraphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
  }
  

}
