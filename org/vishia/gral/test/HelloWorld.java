package org.vishia.gral.test;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;

/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/HelloWorld.java
==JZcmd==
java org.vishia.gral.test.HelloWorld.main(null);                 
==endJZcmd==
 */


public class HelloWorld
{
  GralMng gralMng;
  
  GralWindow window;
  
  public static void main(String[] args){
    HelloWorld main = new HelloWorld();
    String sTypeOfImplementation = "AWT";  //default
    if(args.length >=1){
      sTypeOfImplementation = args[0];
    }
    main.createWindow(sTypeOfImplementation);
    waitForClosePrimaryWindow();
  }
  
  
  public static void openWindow(){
    HelloWorld main = new HelloWorld();
    main.createWindow("AWT");
  }

  
  public static void waitForClosePrimaryWindow()
  {
    while(GralMng.get().gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
    
  }
  
  
  private void createWindow(String awtOrSwt){
    //GralFactory gralFactory = new AwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    String sPosition = "0+30, 0+50";
    //String sPosition = "0..-1, 0..-1";
    
    window = new GralWindow(sPosition, "HelloWorldWind", "Simple Hello World application", GralWindow.windResizeable);
    GralGraphicThread gthread = GralFactory.createGraphic(window, 'C', log, awtOrSwt);
    gthread.addDispatchOrder(initGraphic);
    initGraphic.awaitExecution(1, 0);
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    { GralMng gralMng = GralMng.get();
      gralMng.selectPanel(window);
      gralMng.setPosition(4, 6, 2, -2, 0, 'd');
      gralMng.addText("Hello World");
      //
      //GralTextField input = new GralTextField();
  } };
  
  
}
