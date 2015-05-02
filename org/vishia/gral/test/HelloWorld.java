package org.vishia.gral.test;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

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
    main.openWindow1();
    waitForClosePrimaryWindow();
  }
  
  
  public static void openWindow(){
    HelloWorld main = new HelloWorld();
    main.openWindow1();
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
  
  
  private void openWindow1(){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    window = gralFactory.createWindow(log, "Hello World", 'C', 100, 50, 600, 400);
    gralMng = window.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphic);
    //initGraphic.awaitExecution(1, 0);
      
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    { gralMng.selectPanel(window);
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      gralMng.addText("Hello World");
      //
      //GralTextField input = new GralTextField();
  } };
  
  
}
