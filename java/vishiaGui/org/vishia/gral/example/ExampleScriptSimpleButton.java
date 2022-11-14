package org.vishia.gral.example;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;

public class ExampleScriptSimpleButton {

  
  LogMessage log;
  
  GralMng gralMng;
  
  final String guiScript = 
      "@10+30, 20+80     =panel:  Window Example ScriptSimpleButton; \n"
    + "@panel, 2+2, 2+20 =input:  InputField(); \n"
    + "@8-3, 2+10        =button: Button(\"press me\", action = actionButtonCode);"
    + "@-10..0,0..0      =output: OutputBox();"
    ;
  
  ExampleScriptSimpleButton(String[] args)
  {
    this.log = new LogMessageStream(System.out);  // may also write to a file, use calling arguments
    this.gralMng = new GralMng(this.log);             // initialize the graphic Gral Widgets (not the implementig graphic).
  }

  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  //tag::initImplGraphic[]
  boolean init(String awtOrSwt) {
    String sError = this.gralMng.initScript(this.guiScript);
    if(sError !=null) {
      this.log.writeError(sError);
      return false;
    } else {
      //this.gui.wdgInputText.setText("any text input");
      this.gralMng.createGraphic(awtOrSwt, 'E', this.log);
      return true;
    }
  }
  //end::initImplGraphic[]

  
  /**execute routine for any other actions than the graphical actions. 
   * The application may do some things beside.
   */
  void execute()
  {
    //Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(this.gralMng.isRunning()) {
      try{ Thread.sleep(100); } catch(InterruptedException exc){}
    }
  }





  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  public static void main(String[] args)
  {
    ExampleScriptSimpleButton thiz = new ExampleScriptSimpleButton(args); // constructs the main class
    if(thiz.init("SWT")) {
      thiz.execute();
    }
  }

  /**Code snippet for the action while the button is pressed. This snippet will be executed
   * if the left mouse key is released on the button. If the left mouse is pressed and then
   * the mouse cursor is removed from the button while it is pressed, the action is not executed.
   * This is important if a touch screen is used and the accuracy of target seeking of the finger
   * is not sufficient, the action can be aborted. If the mouse is pressed respectively the button 
   * is sought, the button changes its appearance, it is marked. 
   * That actions are done by the gral implementation independing of the implementation layer.
   */
  private final GralUserAction actionButtonCode = new GralUserAction("buttonCode")
  { 
    int ctKeyStroke = 0;
    
    @Override
    public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
//        String textOfField = ExampleSimpleButton.this.gui.wdgInputText.getText();
//        try{ ExampleSimpleButton.this.gui.widgOutput.append("Button " + (++this.ctKeyStroke) + " time, text=" + textOfField + "\n");
//        } catch(IOException exc){}
      }
      return true;  
    } 
  };  
  

  
}
