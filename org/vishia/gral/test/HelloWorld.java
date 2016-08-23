package org.vishia.gral.test;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralGraphicThread;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

import com.sun.xml.internal.ws.addressing.model.ActionNotSupportedException;

/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/HelloWorld.java
==JZcmd==
java org.vishia.gral.test.HelloWorld.main(null);                 
==endJZcmd==
 */


public class HelloWorld
{
  GralMng gralMng;
  
  String[] helloText = { "hello user", "hello world"};
  
  GralUserAction actionTestButton = new GralUserAction("TestButton") { 
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object...args) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)) {
        widgHelloText.setText("hello again");
      }
      return true;
    }
  };  
  
  GralWindow window = new GralWindow("0+30, 0+50", "HelloWorldWind", "Simple Hello World application", GralWindow.windResizeable + GralWindow.windHasMenu);
  GralLabel widgHelloText = new GralLabel("3-2,2+5", "HelloLabel", helloText[0], 0);
  GralButton widgButton = new GralButton("7-3,10+12", "TestButton", "press me", actionTestButton);
  
  
  public static void main(String[] args){
    HelloWorld main = new HelloWorld();
    String sTypeOfImplementation = "AWT";  //default
    if(args.length >=1){
      sTypeOfImplementation = args[0];
    }
    main.createWindow(sTypeOfImplementation);
    main.doSomethinginMainthreadTillClosePrimaryWindow();
  }
  
  private void createWindow(String awtOrSwt){
    LogMessage log = new LogMessageStream(System.out);
    GralGraphicThread gthread = GralFactory.createGraphic(window, 'C', log, awtOrSwt);
/* ???
    String sPosition = "0+30, 0+50";
    //String sPosition = "0..-1, 0..-1";
    
    window = new GralWindow(sPosition, "HelloWorldWind", "Simple Hello World application", GralWindow.windResizeable);
    GralGraphicThread gthread = GralFactory.createGraphic(window, 'C', log, awtOrSwt);
    gthread.addDispatchOrder(initGraphic);
    initGraphic.awaitExecution(1, 0);
>>>>>>> hist1 */
  }
  
  
  
  public void doSomethinginMainthreadTillClosePrimaryWindow()
  { int ix = 0;
    while(GralMng.get().gralDevice.isRunning()){
      try{ Thread.sleep(1000);} 
      catch (InterruptedException e) { }
      if(++ix >= helloText.length) { ix = 0; }
      widgHelloText.setText(helloText[ix]);
    }
    
  }
  

}
