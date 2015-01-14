package org.vishia.gral.test;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

public class HelloWorld
{
  GralMng gralMng;
  
  public static void main(String[] args){
    HelloWorld main = new HelloWorld();
    main.execute();
  
  }
  
  private void execute(){
    GralFactory_ifc gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Hello World", 'C', 100, 50, 600, 400);
    gralMng = wind.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphic);
    //initGraphic.awaitExecution(1, 0);
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  GralDispatchCallbackWorker initGraphic = new GralDispatchCallbackWorker("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      gralMng.setPosition(4, -2, 2, -2, 0, 'd');
      gralMng.addText("Hello World");
      countExecution();
      //
      //GralTextField input = new GralTextField();
  } };
  
  
}
