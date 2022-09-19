package org.vishia.gral.awt;

import java.awt.Frame;
import java.io.IOException;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralWindow;
import org.vishia.msgDispatch.LogMessage;

public class AwtGraphicThread extends GralGraphicThread.ImplAccess
{

  /**Version, history and license.
   * <ul>
   * <li>2016-07-16 Hartmut chg: The main window will be created with same methods like all other windows. 
   * <li>2015-05-01 Hartmut chg: gral: some gardening, improving AWT
   * <li>2011-10-23 Hartmut created. 
   * </ul>
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  //@SuppressWarnings("hiding")
  public final static String version = "2016-07-16";

  Frame window;
  
  //final String sTitle; 
  //final int xPos, yPos, xSize, ySize;
  
  AwtWidgetMng awtMng;
  
  /**Starts the Graphic thread see {@link GralGraphicThread#run()} 
   * @param windowGral
   * @param sizeShow
   * @param log
   */
  AwtGraphicThread(GralWindow windowGral, char sizeShow, LogMessage log)
  { super(sizeShow, windowGral, log);
    //this.xPos = left; this.yPos = top; this.xSize = xSize; this.ySize = ySize; 
    //threadGuiDispatch.start();  //invokes initGraphic()
    startThread();
  }

  @Override protected void initGraphic()
  {
    AwtProperties propertiesGui = new AwtProperties(sizeCharProperties);
    //AwtSubWindow awtWindow = new AwtSubWindow(mainWindow, true);
    awtMng = new AwtWidgetMng(propertiesGui, log);
    
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

  @Override public void reportContent ( Appendable out )
      throws IOException {
    // TODO Auto-generated method stub
    
  }

  @Override public void finishInit () {
    // TODO Auto-generated method stub
    
  }
  
}
