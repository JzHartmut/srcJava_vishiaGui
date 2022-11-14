package org.vishia.gral.example;

import java.io.IOException;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.KeyCode;

/**This class contains a simple example with a switching button, an input text field and a output
 * text field. When the button is pressed, the text of the input field will be read and an output will be written.
 * @author Hartmut Schorrig
 *
 */
public class ExampleSimpleButton
{
  
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

  //tag::elements[]
  /**Extra inner class (for more data structuring) for all Gui elements.
   */
  protected class GuiElements
  {
  
    final GralMng gralMng = new GralMng(null);             // on Gral widget structuring no log necessary. 
  
    GralPos refPos = new GralPos(this.gralMng);            // use an own reference position to build
    
    final GralWindow window = new GralWindow(this.refPos, "@10+30,20+80=panelWin"
                            , "ExampleSimpleTextButton"
                            , GralWindow.windRemoveOnClose | GralWindow.windResizeable);
    
    final GralTextField wdgInputText = new GralTextField(this.refPos, "@panel, 2+2, 2+20=input"
                                     , GralTextField.Type.editable);
    
    final GralButton wdgButton = new GralButton(this.refPos, "@8-3, 2+10=button"
                               , "press me", ExampleSimpleButton.this.actionButtonCode);
  
    GralTextBox widgOutput = new GralTextBox(this.refPos, "@-10..0,0..0=output");
  
    GuiElements() { }                                      // empty ctor, only formally
  }





  /**Instance of inner class contains the graphical elements.
   * 
   */
  protected final GuiElements gui;
  
  protected final LogMessage log;
  
  
  ExampleSimpleButton(String[] args)
  {
    this.log = new LogMessageStream(System.out);  // may also write to a file, use calling arguments
    this.gui = new GuiElements();                 // initialize the graphic Gral Widgets (not the implementig graphic).
  }
  
  
  
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  //tag::initImplGraphic[]
  void init(String awtOrSwt) {
    this.gui.wdgInputText.setText("any text input");
    this.gui.gralMng.createGraphic(awtOrSwt, 'E', this.log);
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





  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  public static void main(String[] args)
  {
    ExampleSimpleButton thiz = new ExampleSimpleButton(args); // constructs the main class
    thiz.init("SWT");
    thiz.execute();
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
        String textOfField = ExampleSimpleButton.this.gui.wdgInputText.getText();
        try{ ExampleSimpleButton.this.gui.widgOutput.append("Button " + (++this.ctKeyStroke) + " time, text=" + textOfField + "\n");
        } catch(IOException exc){}
      }
      return true;  
    } 
  };  
  
  
  
  
  
  
 
  
  
  
}
