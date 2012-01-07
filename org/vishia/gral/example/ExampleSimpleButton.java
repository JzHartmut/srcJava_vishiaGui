package org.vishia.gral.example;

import java.io.IOException;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.swt.FactorySwt;
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
  
  private final GuiElements gui;
  
  
  ExampleSimpleButton(GralWidgetMng gralMng)
  {
    gui = new GuiElements(gralMng);
    
  }
  
  
  /**Initializes the graphical user interface.
   * 
   */
  void initGui()
  {
    //The code to initialize the GUI appearance should be run in the graphic thread.
    //Therefore the code snippet which contains the functionality to set the graphic is applied to the graphic thread.
    //It is defined in this application class locally.
    gui.gralMng.gralDevice.addDispatchOrder(initGuiCode);
    initGuiCode.awaitExecution(1, 0);  //waits for finishing
    
  }
  
  
  /**Main execute method for any other actions than the graphical actions. 
   * The application may do some things beside.
   */
  void execute()
  {
    //Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(gui.gralMng.gralDevice.isRunning()){
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
  private GralUserAction actionButtonCode = new GralUserAction()
  { 
    int ctKeyStroke = 0;
    
    public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up){
        String textOfField = gui.widgInput.getText();
        try{ gui.widgOutput.append("Button " + (++ctKeyStroke) + " time, text=" + textOfField + "\n");
        } catch(IOException exc){}
      }
      return true;  
    } 
  };
  
  
  
  
  private static class GuiElements
  {
    final GralWidgetMng gralMng;

    GralTextField widgInput;
    
    GralButton widgButton;
    
    GralTextBox widgOutput;
  
    /**Constructor with given widget manager.
     * @param gralMng
     */
    GuiElements(GralWidgetMng gralMng)
    {
      this.gralMng = gralMng;
    }
  }
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. It is an anonymous inner class. 
   */
  private GralDispatchCallbackWorker initGuiCode = new GralDispatchCallbackWorker()
  {
    /**This routine is called in the graphic thread if it was added.
     * @see org.vishia.gral.ifc.GralDispatchCallbackWorker#doBeforeDispatching(boolean)
     */
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      //we have only one panel. But if there are more as one, select which.
      gui.gralMng.selectPanel("primaryWindow");
      //
      //Sets the positions in grid line and columns. line 5 to 8, column 2 to 15
      gui.gralMng.setPosition(5, 8, 2, 15, 0, '.');
      //Adds a text input field.
      //NOTE: the element gui is arranged in the outer class because it may be accessed later.
      gui.widgInput = gui.gralMng.addTextField("input", true, null, "t");
      //Sets the position of the next widget, the button, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      gui.gralMng.setPosition(GralPos.same+5, GralPos.size +3, 2, GralPos.size +10, 0, '.');
      gui.widgButton = gui.gralMng.addButton("button", actionButtonCode, "test", null, null, "Hello");
      //
      //The button can be presented with colors. Use named colors 'Pastel GreeN' and 'Pastel YEllow'.
      //The button is a switching button then. 
      gui.widgButton.setSwitchMode(GralColor.getColor("pgn"), GralColor.getColor("pye"));
      //
      //Sets the position of the next widget, the textbox, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      //The columns are dedicated with 0 (left) and 0 (from right). It means the full window width.
      gui.gralMng.setPosition(-10, 0, 0, 0, 0, '.');
      gui.widgOutput = gui.gralMng.addTextBox("outputText", true, null, '.');
      //counts its execution because await...() is called for it.
      countExecution();
      //remove itself, it should be executed only one time.
      gui.gralMng.gralDevice.removeDispatchListener(this);
    }
  };


  
  
  
  
  
  
  
  
  public static void main(String[] args)
  {
    boolean bOk = true;
    //
    //choose a factory, recomment one of the following:
    //
    GralFactory_ifc graphicFactory = new AwtFactory();
    //GralFactory_ifc graphicFactory = new FactorySwt();
    //
    //A logger is a final thing what is need. This logger writes to the console.
    //A complexer application may write to a graphic output window.
    LogMessage log = new LogMessageStream(System.out);
    //
    //create the window, use the factory.
    GralWindow primaryWindow = graphicFactory.createWindow(log, "Example Simple Button", 50,50,400, 300);
    //
    //The widget manager is created with the primary window. Use it.
    GralWidgetMng gralMng = primaryWindow.gralMng;
    //
    //An empty graphic window is present now. It is time to create this application class now. 
    //In an complexer application the graphic window can contain an output window, so information
    //while building the application class can be shown for the user. 
    //
    //The gralMng is the main access to the graphic. It is indpenendent of the graphical implementation layer.
    ExampleSimpleButton mainData = new ExampleSimpleButton(gralMng);
    //
    //Now the appearance of the graphic should be initialized:
    mainData.initGui();
    //
    //Now executes the application code which may be independent of the graphic execution.
    mainData.execute();
  }
  
  
  
}
