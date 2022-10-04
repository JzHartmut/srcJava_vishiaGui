package org.vishia.gral.test;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralLabel;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;


/*Test with jzcmd: call jzcmd with this java file with its full path:
D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/test/HelloWorld.java
==JZcmd==
java org.vishia.gral.test.HelloWorld.main(null);                 
==endJZcmd==
 */


public class HelloWorld
{
  String[] helloText = { "hello user", "hello world"};
  
  GralUserAction actionTestButton = new GralUserAction("TestButton") { 
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object...args) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)) {
        widgHelloText.setText("hello again");
      }
      return true;
    }
  };  
  
  /**The GralWindow have to be initialized firstly, after them the widgets in the window. Therewith the assignment of the widgets to this window
   * is determined. The widgets are created with the window then, see {@link GralPanelContent#createImplWidget_Gthread()}
   */
  GralWindow window = new GralWindow("50+30, 50+50", "HelloWorldWind", "Simple Hello World application", GralWindow.windResizeable + GralWindow.windHasMenu);
  GralLabel widgHelloText = new GralLabel("3-2,2+5", "HelloLabel", helloText[0], 0);
  GralButton widgButton = new GralButton("7-3,10+12", "TestButton", "press me", actionTestButton);
  
  
  public static void main(String[] args){
    HelloWorld main = new HelloWorld();
    String sTypeOfImplementation = "AWT";  //default
    if(args.length >=1){
      sTypeOfImplementation = args[0];
    }
    LogMessage log = new LogMessageStream(System.out);  //a logging system.
    main.window.create(sTypeOfImplementation, 'C', log, null);  //creates the primary window, starts the whole graphic engine.
    //wait, a parallel thread to the grahic.
    main.doSomethinginMainthreadTillClosePrimaryWindow();
  }
  
  
  
  
  public void doSomethinginMainthreadTillClosePrimaryWindow()
  { int ix = 0;
    while(GralMng.get().isRunning()){
      try{ Thread.sleep(2000);} 
      catch (InterruptedException e) { }
      if(++ix >= helloText.length) { ix = 0; }
      widgHelloText.setText(helloText[ix]);
    }
    
  }
  

}
