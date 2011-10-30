package org.vishia.gral.example;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralFactory_ifc;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.swt.FactorySwt;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

/**This class contains a simple example with a switching button, an input text field and a output
 * text field. When the button is pressed, the text of the input field will be read and an output will be written.
 * @author Hartmut Schorrig
 *
 */
public class ExampleSimpleButton
{
  private final GralWidgetMng gralMng;
  
  ExampleSimpleButton(GralWidgetMng gralMng)
  {
    this.gralMng = gralMng;
  }
  
  
  /**Code snippet for initializing the GUI. This snippet will be executed
   * in the graphic thread. An instance of this class will be created only temporary. 
   */
  private static class InitGui extends GralDispatchCallbackWorker
  {
    
    private final GralWidgetMng gralMng;
    
    /**Constructor with given widget manager.
     * @param gralMng
     */
    InitGui(GralWidgetMng gralMng)
    {
      this.gralMng = gralMng;
    }
    
    /**This routine is called in the graphic thread if it was added.
     * @see org.vishia.gral.ifc.GralDispatchCallbackWorker#doBeforeDispatching(boolean)
     */
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      //we have only one panel. But if there are more as one, select which.
      gralMng.selectPanel("primaryWindow");
      //
      //Sets the positions in grid line and columns. line 5 to 8, column 2 to 15
      gralMng.setPosition(5, 8, 2, 15, 0, '.');
      //Adds a text input field.
      gralMng.addTextField("input", true, null, 't');
      //Sets the position of the next widget, the button, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      gralMng.setPosition(GralGridPos.same+5, GralGridPos.size +3, 2, GralGridPos.size +10, 0, '.');
      GralButton widgButton = gralMng.addButton("button", null, "test", null, null, "Hello");
      //
      //The button can be presented with colors. Use named colors 'Pastel GreeN' and 'Pastel YEllow'.
      //The button is a switching button then. 
      widgButton.setSwitchMode(GralColor.getColor("pgn"), GralColor.getColor("pye"));
      //
      //Sets the position of the next widget, the textbox, relative to the last one, 5 lines deeper.
      //Use size instead an line position. 
      //The columns are dedicated with 0 (left) and 0 (from right). It means the full window width.
      gralMng.setPosition(-10, 0, 0, 0, 0, '.');
      GralTextBox widgOutput = gralMng.addTextBox("outputText", true, null, '.');
      //counts its execution because await...() is called for it.
      countExecution();
      //remove itself, it should be executed only one time.
      gralMng.gralDevice.removeDispatchListener(this);
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
    //The code to initialize the GUI appearance should be run in the graphic thread.
    //Therefore an instance which contains the functionality to set the graphic is created.
    //It is defined in this application class locally.
    InitGui initCode = new InitGui(gralMng);
    gralMng.gralDevice.addDispatchListener(initCode);
    initCode.awaitExecution(1, 0);  //waits for finishing
    //
    //The graphic is present. Now do nothing because all actions are done in the graphic thread.
    //A more complex application can handle some actions in its main thread simultaneously and independent of the graphic thread.
    //
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100); } catch(InterruptedException exc){}
    }
  }
  
  
  
}
