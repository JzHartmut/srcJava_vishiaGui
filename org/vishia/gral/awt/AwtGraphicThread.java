package org.vishia.gral.awt;

import java.awt.Frame;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWindow;
import org.vishia.msgDispatch.LogMessage;

public class AwtGraphicThread extends GralGraphicThread.ImplAccess
{

  Frame window;
  
  //final String sTitle; 
  //final int xPos, yPos, xSize, ySize;
  
  AwtWidgetMng awtMng;
  
  AwtGraphicThread(GralWindow windowGral, char sizeShow, LogMessage log)
  { super(sizeShow, windowGral, log);
    //this.xPos = left; this.yPos = top; this.xSize = xSize; this.ySize = ySize; 
    threadGuiDispatch.start();  //invokes initGraphic()
    
  }

  @Override protected void initGraphic()
  {
    AwtProperties propertiesGui = new AwtProperties(sizeCharProperties);
    AwtSubWindow awtWindow = new AwtSubWindow(mainWindow, true);
    awtMng = new AwtWidgetMng(awtWindow.window, propertiesGui, log);
    
  }

  
  
  @Override
  protected boolean dispatchOsEvents()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  protected void graphicThreadSleep()
  {
    synchronized(this){
      try{ wait(100);} catch(InterruptedException exc){}
    }
  }


  @Override
  public void wakeup()
  {
    // TODO Auto-generated method stub
    
  }
  
}
