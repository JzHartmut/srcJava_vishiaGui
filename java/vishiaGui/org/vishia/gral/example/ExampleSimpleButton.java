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

  /**Instance of inner class contains the graphical elements.
   * 
   */
  protected final GuiElements gui;
  
  /**Instance to initialize the graphic. */
  private GralGraphicTimeOrder initGuiCode;
  
  ExampleSimpleButton(GralMng gralMng)
  {
    //this.initGuiCode = new InitGuiCode();
    this.gui = new GuiElements(gralMng);
    
  }
  
  protected void setInitGuiCode(GralGraphicTimeOrder initGuiCode)
  {
    this.initGuiCode = initGuiCode;
  }
  
  
  /**Initializes the graphical user interface.
   * 
   */
  void initGui()
  {
    //The code to initialize the GUI appearance should be run in the graphic thread.
    //Therefore the code snippet which contains the functionality to set the graphic is applied to the graphic thread.
    //It is defined in this application class locally.
    this.gui.gralMng.addDispatchOrder(this.initGuiCode);
    this.initGuiCode.awaitExecution(1, 0);  //waits for finishing
    
  }
  
  
  /**Main execute method for any other actions than the graphical actions. 
   * The application may do some things beside.
   */
  void execute()
  {
    //Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(this.gui.gralMng.isRunning()){
      try{ Thread.sleep(100); } catch(InterruptedException exc){}
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
        String textOfField = ExampleSimpleButton.this.gui.widgInput.getText();
        try{ ExampleSimpleButton.this.gui.widgOutput.append("Button " + (++this.ctKeyStroke) + " time, text=" + textOfField + "\n");
        } catch(IOException exc){}
      }
      return true;  
    } 
  };
  
  
  
  
  protected static class GuiElements
  {
    final GralMng gralMng;

    GralTextField widgInput;
    
    GralButton widgButton;
    
    GralTextBox widgOutput;
  
    /**Constructor with given widget manager.
     * @param gralMng
     */
    GuiElements(GralMng gralMng)
    {
      this.gralMng = gralMng;
    }
  }
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  @SuppressWarnings("serial")
  protected class InitGuiCodeSimpleButton extends GralGraphicTimeOrder
  {
    InitGuiCodeSimpleButton(){
      super("ExampleSimpleButton.initGuiCode");
    }
    
    /**This routine is called in the graphic thread if it was added.
     * @see org.vishia.gral.base.GralGraphicTimeOrder#executeOrder(boolean)
     */
    @Override public void executeOrder()
    {
      //we have only one panel. But if there are more as one, select which.
      ExampleSimpleButton.this.gui.gralMng.selectPanel("primaryWindow");
      //
      //Sets the positions in grid line and columns. line 5 to 8, column 2 to 15
      ExampleSimpleButton.this.gui.gralMng.setPosition(5, 8, 2, 15, 0, '.');
      //Adds a text input field.
      //NOTE: the element gui is arranged in the outer class because it may be accessed later.
      ExampleSimpleButton.this.gui.widgInput = ExampleSimpleButton.this.gui.gralMng.addTextField("input", true, null, "t");
      //Sets the position of the next widget, the button, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      ExampleSimpleButton.this.gui.gralMng.setPosition(GralPos.same+5, GralPos.size +3, 2, GralPos.size +10, 0, '.');
      ExampleSimpleButton.this.gui.widgButton = ExampleSimpleButton.this.gui.gralMng.addButton("button", ExampleSimpleButton.this.actionButtonCode, "test", null, "Hello");
      //
      //The button can be presented with colors. Use named colors 'Pastel GreeN' and 'Pastel YEllow'.
      //The button is a switching button then. 
      ExampleSimpleButton.this.gui.widgButton.setSwitchMode(GralColor.getColor("pgn"), GralColor.getColor("pye"));
      //
      //Sets the position of the next widget, the textbox, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      //The columns are dedicated with 0 (left) and 0 (from right). It means the full window width.
      //ExampleSimpleButton.this.gui.widgOutput = new GralTextBox("outputText");
      ExampleSimpleButton.this.gui.gralMng.setPosition(-10, 0, 0, 0, 0, '.');
      //ExampleSimpleButton.this.gui.gralMng.registerWidget(ExampleSimpleButton.this.gui.widgOutput);
      //alternatively:
      ExampleSimpleButton.this.gui.widgOutput = ExampleSimpleButton.this.gui.gralMng.addTextBox("outputText", true, null, '.');
    }
  };


  
  /**The main routine. It creates the factory of this class
   * and then calls {@link #main(String[], Factory)}.
   * With that pattern a derived class may have a simple main routine too.
   * @param args command line arguments.
   */
  public static void main(String[] args)
  {
    main(args, new Factory());
  
  }  
  
  
  
  
  /**Main routine with a factory class. That allows to use the same main routine for a derived class
   * for further more complex examples.
   * @param args command line arguments.
   * @param factoryExample The factory to create the current class which should be derived from this.
   */
  protected static void main(String[] args, Factory factoryExample)
  {
    //boolean bOk = true;
    //
    //choose a factory, recomment one of the following:
    //
    //depr GralFactory graphicFactory = new SwtFactory();   //Awt SwtFactory
    //GralFactory_ifc graphicFactory = new FactorySwt();
    //
    //A logger is a final thing what is need. This logger writes to the console.
    //A complexer application may write to a graphic output window.
    LogMessage log = new LogMessageStream(System.out);
    //
    //create the window, use the factory.
    GralWindow primaryWindow = new GralWindow("10..50,20..90", "ExampleSimpleButton", "ExampleSimpleButton", GralWindow_ifc.windIsMain);
    GralFactory.createGraphic(primaryWindow, 'C', log, "SWT");
    
    //depr: GralWindow primaryWindow = graphicFactory.createWindow(log, "Example Simple Button", 'C', 50,50,400, 300);
    //
    //The widget manager is created with the primary window. Use it.
    GralMng gralMng = primaryWindow.gralMng();
    //
    //An empty graphic window is present now. It is time to create this application class now. 
    //In an complexer application the graphic window can contain an output window, so information
    //while building the application class can be shown for the user. 
    //
    //The gralMng is the main access to the graphic. It is independent of the graphical implementation layer.
    ExampleSimpleButton mainData = factoryExample.create(gralMng);
    //
    //Now the appearance of the graphic should be initialized:
    mainData.initGui();
    //
    //Now executes the application code which may be independent of the graphic execution.
    mainData.execute();
  }
  
  
  
 
  /**This inner class creates this class with given parameter.
   */
  static class Factory {
    ExampleSimpleButton create(GralMng gralMng){
      ExampleSimpleButton obj = new ExampleSimpleButton(gralMng);
      obj.setInitGuiCode(obj.new InitGuiCodeSimpleButton());
      return obj;
    }
  }
  
  
  
}
