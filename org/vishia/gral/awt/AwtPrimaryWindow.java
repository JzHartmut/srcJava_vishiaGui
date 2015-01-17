package org.vishia.gral.awt;

import java.awt.Frame;
import java.io.File;

import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;

public class AwtPrimaryWindow extends AwtSubWindow implements GralPrimaryWindow_ifc
{
  /**It is the same instance as {@link GralMng#gralDevice} but refer the SWT type.
   * 
   */
  final AwtGraphicThread graphicThreadAwt;

  
  AwtPrimaryWindow(GralMng gralMng, String sTitle, AwtGraphicThread graphicThread)
  { super("primaryWindow", graphicThread.window, "title", GralWindow.windHasMenu | GralWindow.windConcurrently, gralMng);
    //super(gralMng, graphicThread);
    this.graphicThreadAwt = graphicThread;  //refers SWT type
  }  
  
  public static AwtPrimaryWindow create(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize)
  { AwtGraphicThread implGraphicThread = new AwtGraphicThread(sTitle, sizeShow, left, top, xSize, ySize);
    GralGraphicThread gralGraphicThread = implGraphicThread.gralGraphicThread();
    //GuiThread graphicThread = startGraphicThread(init);  

    synchronized(gralGraphicThread){
      while(gralGraphicThread.getThreadIdGui() == 0){
        try{ gralGraphicThread.wait(1000);} catch(InterruptedException exc){}
      }
    }
   //The propertiesGuiSwt needs the Display instance for Font and Color. Therefore the graphic thread with creation of Display should be executed before. 
    AwtProperties propertiesGui = new AwtProperties('C');
    //GralMng gralMng = new AwtWidgetMng(gralGraphicThread, implGraphicThread.window, propertiesGui, log);
    //TODO
    GralMng gralMng = null; //implGraphicThread.
    //The PrimaryWindowSwt is a derivation of the GralPrimaryWindow. It is more as only a SWT Shell.
    AwtPrimaryWindow instance = new AwtPrimaryWindow(gralMng, sTitle, implGraphicThread);
    instance.panelComposite = implGraphicThread; //window.sTitle, window.xPos, window.yPos, window.xSize, window.ySize);
    //gralMng.setGralDevice(init);
    gralMng.registerPanel(instance);
    
    //init.setWindow(instance);  //now the initializing of the window occurs.
    return instance;
  }
  

  
  
  @Override
  public MainCmd_ifc getMainCmd()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object getitsGraphicFrame()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void closeWindow()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean isWindowsVisible()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void repaint()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setWindowVisible(boolean visible)
  {
    // TODO Auto-generated method stub
    
  }
  
}
