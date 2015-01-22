package org.vishia.gral.awt;

import java.awt.Frame;

import org.vishia.gral.base.GralGraphicThread;

public class AwtGraphicThread extends GralGraphicThread.ImplAccess
{

  Frame window;
  
  final String sTitle; final int xPos, yPos, xSize, ySize;
  
  
  AwtGraphicThread(String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  { super(sizeShow);
    this.sTitle = sTitle; this.xPos = left; this.yPos = top; this.xSize = xSize; this.ySize = ySize; 
    threadGuiDispatch.start();
    
  }

  @Override protected void initGraphic()
  {
    window = new Frame(sTitle);
    window.setBounds(xPos, yPos, xSize, ySize);
    window.setVisible(true);
    window.setLayout(null);
    
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
