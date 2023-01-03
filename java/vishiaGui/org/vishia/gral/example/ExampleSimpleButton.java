package org.vishia.gral.example;

import java.io.IOException;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;

/**This class contains a simple example with a switching button, an input text field and a output
 * text field. When the button is pressed, the text of the input field will be read and an output will be written.
 * @author Hartmut Schorrig
 *
 */
//tag::classHead[]
public class ExampleSimpleButton
{
  /**A log mechanism nice to have and necessary for GRAL built,
   * writes firstly to the console out on built of the graphic. 
   * Changeable later of the graphic runs. */
  protected LogMessage log;
  //end::classHead[]
  
  /**Version, history and license.
   * <ul>
   * <li>2022-01-26 Hartmut improved a little bit
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20220126;

  //tag::guiClass[]
  /**Extra inner class (for more data structuring) for all Gui elements.
   */
  protected class GuiElements
  {
    /**The central Gral management instance:*/
    final GralMng gralMng = new GralMng(ExampleSimpleButton.this.log);
  
    /**Intermediate Position instance as helper for positioning. */
    GralPos refPos = new GralPos(this.gralMng);            // use an own reference position to build
    
    /**The Window of the application. */
    final GralWindow window = new GralWindow(this.refPos, "@screen, 10+30,20+80=mainWin"
                            , "ExampleSimpleTextButton"
                            , GralWindow.windRemoveOnClose | GralWindow.windResizeable);
    /**A text field.*/
    final GralTextField wdgInputText = new GralTextField(this.refPos, "@main, 2+2, 2+20=input"
                                     , GralTextField.Type.editable);
    /*A button. */
    final GralButton wdgButton1 = new GralButton(this.refPos, "@8-3, 2+10++2.5 =button1"
        , "press me", ExampleSimpleButton.this.actionButton); //Position string: next to right with 2 units space

    final GralButton wdgButton2 = new GralButton(this.refPos, "button2"
        , "Button 2", null); //without action,              //without position string, automatic right side

    /**Textbox for output texts, can be also used for log. */
    GralTextBox widgOutput = new GralTextBox(this.refPos, "@-10..0,0..0=output");
    
    /**Empty ctor, formally. */
    GuiElements() { }                                      // empty ctor, only formally
  }
  //end::guiClass[]



  //tag::fieldsCtor[]
  /**Instance of inner class contains the graphical elements.*/
  protected final GuiElements gui;
  
  int ctKeyStroke1 = 0, ctKeyStroke2 = 0;
  
  ExampleSimpleButton ( String[] args )
  {
    this.log = new LogMessageStream(System.out);  // may also write to a file, use calling arguments
    //----------------------------------   initialize the graphic Gral Widgets (not the implementing graphic).
    this.gui = new GuiElements();       // because the log is set, GuiElements construction uses it. 
  }
  //end::fieldsCtor[]
  
  
  
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  //tag::initImplGraphic[]
  void init ( String awtOrSwt) {
    this.gui.wdgInputText.setText("any text input");
    this.gui.gralMng.createGraphic(awtOrSwt, 'E', this.log);
  }
  //end::initImplGraphic[]

  
  //tag::execute[]
  /**execute routine for any other actions than the graphical actions. 
   * The application may do some things beside.
   */
  void execute ( )
  {
    //Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(this.gui.gralMng.isRunning()) {
      try {
        if(this.gui.wdgButton2.wasReleased()) {
          String textOfField = this.gui.wdgInputText.getText();
          this.gui.widgOutput.append("Button2 " + (++this.ctKeyStroke2) + " time, text=" + textOfField + "\n");
          throw new Exception("test");
        }
        Thread.sleep(100); 
      } catch(Exception exc){
        CharSequence sText = org.vishia.util.ExcUtil.exceptionInfo("unexpected: ", exc, 1, 10);
        this.log.sendMsg(9999, sText);
      }
      
    }
  }
  //end::execute[]





  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  //tag::main[]
  public static void main ( String[] args)
  {
    try {
      ExampleSimpleButton thiz = new ExampleSimpleButton(args); // constructs the main class
      thiz.init("SWT");
      thiz.execute();
    } catch (Exception exc) {
      System.err.println("Exception: " + exc.getMessage());
      exc.printStackTrace(System.err);
    }
  }
  //end::main[]



  //tag::action[]
  /**Operation on button pressed, on the application level.
   * It uses the known references to the GralWidget. 
   * Immediately access to implementation widgets is not necessary.  
   * This operation is executed in the Graphic thread. 
   * Be carefully, do not program longer or hanging stuff such as synchronized or sleep.
   */
  void actionButton ( ) throws IOException {
    String textOfField = this.gui.wdgInputText.getText();
    this.gui.widgOutput.append("Button1 " + (++this.ctKeyStroke1) + " time, text=" + textOfField + "\n");
  }
  
  
  /**Action operation is called in the event handler of the appropriate widget. */
  private final GralUserAction actionButton = new GralUserAction("buttonCode")
  { 
    @Override
    public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try{ 
          ExampleSimpleButton.this.actionButton();         // defined of class level of the main (environment) class.
        } catch(Exception exc){                            // Exceptions should catch anyway. but not expected.
          ExampleSimpleButton.this.log.writeError("Unexpected", exc);
        }                           
      }
      return true;  
    } 
  };  
  //end::action[]
   
}
