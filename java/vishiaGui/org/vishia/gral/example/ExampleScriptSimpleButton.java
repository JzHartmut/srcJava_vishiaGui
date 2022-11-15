package org.vishia.gral.example;

import java.io.IOException;
import java.text.ParseException;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;

//tag::classAndScript[]
public class ExampleScriptSimpleButton {

  
  LogMessage log;

  final String guiScript = 
      "@10+30, 20+80     =mainWin:Window Example ScriptSimpleButton; \n"
    + "@main, 2+2, 2+20  =input:  InputField(); \n"
    + "@8-3, 2+10        =button: Button(\"press me\", action = actionButtonCode);"
    + "@-10..0,0..0      =output: OutputBox();"
    ;
  //end::classAndScript[]
  
  //tag::GuiElements[]
  protected class GuiElements {
    
    final GralMng gralMng;
    
    final GralTextField wdgInputText;
    
    final GralTextBox wdgOutput;
    
    
    GuiElements(CharSequence script, LogMessage log) throws ParseException {
      this.gralMng = new GralMng(log);             // The GralMng should know the user actions used in the script.
      this.gralMng.registerUserAction("actionButtonCode", ExampleScriptSimpleButton.this.actionButtonCode);
      //
      this.gralMng.initScript(script);             // initialize the graphic Gral Widgets (not the implementig graphic).
      //
      this.wdgInputText = (GralTextField)this.gralMng.getWidget("input");
      this.wdgOutput = (GralTextBox)this.gralMng.getWidget("output");
    }
  }
  //end::GuiElements[]
  
  //tag::fieldsCtor[]
  final GuiElements gui;
  
  int ctKeyStroke;
  
  
  ExampleScriptSimpleButton(String[] args) throws ParseException
  {
    this.log = new LogMessageStream(System.out);  // may also write to a file, use calling arguments
    this.gui = new GuiElements(this.guiScript, log);
  }
  //end::fieldsCtor[]
  

  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  //tag::initImplGraphic[]
  boolean init(String awtOrSwt) {
    this.gui.gralMng.reportGralContent(log);
    //                                           // check whether the widgets are existing
    if(this.gui.wdgInputText == null) { throw new IllegalArgumentException("missing widget \"input\""); }
    if(this.gui.wdgOutput == null) { throw new IllegalArgumentException("missing widget \"output\""); }
    this.gui.wdgInputText.setText("any text input");
    this.gui.gralMng.createGraphic(awtOrSwt, 'E', this.log);
    return true;
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
    while(this.gui.gralMng.isRunning()) {
      try{ Thread.sleep(100); } catch(InterruptedException exc){}
    }
  }




  //tag::main[]
  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  public static void main(String[] args)
  {
    try {
      ExampleScriptSimpleButton thiz = new ExampleScriptSimpleButton(args); // constructs the main class
      thiz.init("SWT");
      thiz.execute();
    } catch (Exception exc) {
      System.err.println("Exception: " + exc.getMessage());
      exc.printStackTrace(System.err);
    }
  }
  //end::main[]
  

  /**Code snippet for the action while the button is pressed. This snippet will be executed
   * if the left mouse key is released on the button. If the left mouse is pressed and then
   * the mouse cursor is removed from the button while it is pressed, the action is not executed.
   * This is important if a touch screen is used and the accuracy of target seeking of the finger
   * is not sufficient, the action can be aborted. If the mouse is pressed respectively the button 
   * is sought, the button changes its appearance, it is marked. 
   * That actions are done by the gral implementation independing of the implementation layer.
   */
  void buttonOperation() {
    String textOfField = this.gui.wdgInputText.getText();
    try{ 
      this.gui.wdgOutput.append("Button " + (++this.ctKeyStroke) + " time, text=" + textOfField + "\n");
    } catch(IOException exc){}
  }
  
  private final GralUserAction actionButtonCode = new GralUserAction("buttonCode") { 
    
    @Override
    public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        ExampleScriptSimpleButton.this.buttonOperation();
      }
      return true;  
    } 
  };  


  
}
