package org.vishia.guiCmdMenu;

import java.io.File;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainGui.GuiDialogZbnfControlled;
import org.vishia.mainGui.GuiDispatchCallbackWorker;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.TabPanel;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetCmpnifc;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.mainGuiSwt.GridPanelSwt;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

/**This class supports command line application with a graphical user interface.
 * Some Buttons can be placed by a user script. The buttons are assigned to line commands.
 * The output of any command are captured in a text. This output text can be viewed in a simple
 * edit window, or it can be prepared for list or tree views and/or fill in some variables.
 * The variables can be used for commands then.
 * <br><br>
 * If any application is provided by a command line interface, it can be completed 
 * for a proper human interface. 
 * 
 * @author hartmut schorrig.
 *
 */
public class CmdMenu 
{

private final Inspector inspector;
  
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
private final Report console;

    


/**The command-line-arguments may be stored in an extra class, which can arranged in any other class too. 
 * The separation of command line argument helps to invoke the functionality with different calls, 
 * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT 
 */
static class CallingArguments
{
  /**Name of the config-file for the Gui-appearance. */
  String sFileGui;
  
  /**Directory where sFileCfg is placed, with / on end. The current dir if sFileCfg is given without path. */
  String sParamBin;
  
  String sFileCtrlValues;
  
  String sPathZbnf = "GUI";
  
  /**The time zone to present all time informations. */
  String sTimeZone = "GMT";
  
  /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
  String sSize;
} //class CallingArguments



final CallingArguments callingArguments;




/**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
 * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
GuiDialogZbnfControlled dialogZbnfConfigurator;   












/**Organisation class for the GUI.
 */
private static class CmdLineAndGui extends MainCmdSwt
{

  
  /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
   * It is given as ctor-parameter.
   */
  final CallingArguments cargs;
  
  /**ctor called in static main.
   * @param cargs aggregation to command-line-argument data, it will be filled here.
   * @param args The command-line-calling arguments from static main
   */
  public CmdLineAndGui(CallingArguments cargs, String[] args)
  { 
    super(args);
    super.addAboutInfo("Gui");
    super.addAboutInfo("made by HSchorrig, 2010-06-07, 2011-11-13");
    //super.addStandardHelpInfo();
    this.cargs = cargs;
    super.setTitleAndSize("GUI-cfg", 50,50,800, 600); //600);  //This instruction should be written first to output syntax errors.
    //super.setStandardMenus(new File("."));
    super.setOutputArea("A3C3");        //whole area from mid to bottom
    super.startGraphicThread();
  }


  
  /*---------------------------------------------------------------------------------------------*/
  /** Tests one argument. This method is invoked from parseArgument. It is abstract in the superclass MainCmd
      and must be overwritten from the user.
      :TODO: user, test and evaluate the content of the argument string
      or test the number of the argument and evaluate the content in dependence of the number.

      @param argc String of the actual parsed argument from cmd line
      @param nArg number of the argument in order of the command line, the first argument is number 1.
      @return true is okay,
              false if the argument doesn't match. The parseArgument method in MainCmd throws an exception,
              the application should be aborted.
  */
  @Override protected boolean testArgument(String arg, int nArg)
  { boolean bOk = true;  //set to false if the argc is not passed
    try {
      if(arg.startsWith("-gui="))      
      { cargs.sFileGui = getArgument(5);  //the graphic GUI-appearance 
      }
        else if(arg.startsWith("-parambin=")) 
        { cargs.sParamBin = getArgument(10);   //an example for default output
        }
        else if(arg.startsWith("-ctrlbin=")) 
        { cargs.sFileCtrlValues = getArgument(9);   //an example for default output
        }
        else if(arg.startsWith("-timeZone=")) 
        { cargs.sTimeZone = getArgument(10);   //an example for default output
        }
        else if(arg.startsWith("-size=")) 
        { cargs.sSize = getArgument(6);   //an example for default output
        }
        else if(arg.startsWith("-_")) 
      { //accept but ignore it. Commented calling arguments.
      }
      else 
      { bOk=false;
      }
    } catch(Exception exc){
    }
    return bOk;
  }


  /** Invoked from parseArguments if no argument is given. In the default implementation a help info is written
   * and the application is terminated. The user should overwrite this method if the call without comand line arguments
   * is meaningfull.
   *
   */
  @Override protected void callWithoutArguments()
  { //overwrite with empty method - if the calling without arguments
    //having equal rights than the calling with arguments - no special action.
  }

  /*---------------------------------------------------------------------------------------------*/
  /**Checks the cmdline arguments relation together.
     If there is an inconsistents, a message should be written. It may be also a warning.
     :TODO: the user only should determine the specific checks, this is a sample.
     @return true if successfull, false if failed.
  */
  @Override protected boolean checkArguments()
  { boolean bOk = true;
    return bOk;
  
  }
  
} //class CmdLineAndGui

private final MainCmdSwt gui;


private GuiPanelMngSwt panelMng;

/**Panel-Management-interface for the panels. */
private GuiPanelMngBuildIfc panelBuildIfc;

private GuiPanelMngWorkingIfc dlgAccess;

/**Code snippet for initializing the GUI area (panel). This snippet will be executed
 * in the GUI-Thread if the GUI is created. 
 */
GuiDispatchCallbackWorker initGuiDialog = new GuiDispatchCallbackWorker()
{
  @Override public void doBeforeDispatching(boolean onlyWakeup)
  {
    gui.setFrameAreaBorders(20, 80, 60, 85);

      
    //Creates a Tab-Panel:
    TabPanel tabPanel = panelMng.createTabPanel(null);
    tabPanel.addGridPanel("operation", "&Operation",1,1,10,10);
    tabPanel.addGridPanel("panel2", "&Panel_" +
            "2",1,1,10,10);
      
    gui.addFrameArea(1,1,3,1, tabPanel.getGuiComponent()); //dialogPanel);
    //##
    WidgetCmpnifc msgPanel = panelMng.createGridPanel(  
        panelMng.propertiesGui.colorBackground_
        , panelMng.propertiesGui.xPixelUnit(), panelMng.propertiesGui.yPixelUnit(), 5, 5);
    panelMng.registerPanel("msg", msgPanel);
    gui.addFrameArea(1,2,3,1, msgPanel); //dialogPanel);
    
    gui.removeDispatchListener(this);    
    countExecution();
  }
  
  
};


/**Code snippet to run the ZBNF-configurator (text controlled GUI)
 * 
 */
GuiDispatchCallbackWorker configGuiWithZbnf = new GuiDispatchCallbackWorker()
{
  
  @Override public void doBeforeDispatching(boolean onlyWakeup){
    char sizeArg = callingArguments.sSize == null ? 'A' : callingArguments.sSize.charAt(0);
      switch(sizeArg){
      case 'F':   gui.setTitleAndSize("GUI", 0, 0, -1, -1); break;
      case 'A': gui.setTitleAndSize("GUI", 500, 100, 800, 600); break;
      case 'a': gui.setTitleAndSize("GUI", 50, 100, 512, 396); break;
      case 'b': gui.setTitleAndSize("GUI", 50, 100, 640, 480); break;
      case 'c': gui.setTitleAndSize("GUI", 50, 100, 800, 600); break;
      case 'D': gui.setTitleAndSize("GUI", 50, 100, 1024, 768); break;
      case 'E': gui.setTitleAndSize("GUI", 50, 100, 1200, 1050); break;
      default: gui.setTitleAndSize("GUI", 500, 100, -1, 800); break;
    }
    try { 
      File fileGui = new File(callingArguments.sFileGui);
      
      dialogZbnfConfigurator.configureWithZbnf("Sample Gui", fileGui, panelBuildIfc);
    
    }  
    catch(Exception exception)
    { //catch the last level of error. No error is reported direct on command line!
      gui.writeError("Uncatched Exception on main level:", exception);
      gui.writeStackTrace(exception);
      gui.setExitErrorLevel(MainCmd_ifc.exitWithErrors);
    }
      gui.removeDispatchListener(this);    
      
      countExecution();
      
  }
////
};



/**ctor for the main class of the application. 
 * The main class can be created in some other kinds as done in static main too.
 * But it needs the {@link MainCmdWin}.
 * <br><br>
 * The ctor checks whether a gUI-configuration file is given. If not then the default configuration is used.
 * It is especially for the Sample.
 * <br><br>
 * The the GUI will be completed with the content of the GUI-configuration file.  
 *   
 * @param cargs The given calling arguments.
 * @param gui The GUI-organization.
 */
CmdMenu(CallingArguments cargs, MainCmdSwt gui) 
{ this.gui = gui;
  boolean bOk = true;
  this.callingArguments = cargs;
  this.console = gui;  
  
  inspector = new Inspector("UDP:127.0.0.1:60088");
  inspector.start(this);
  
  //Creates a panel manager to work with grid units and symbolic access.
    //Its properties:  //##
  final char sizePixel;
  char sizeArg = callingArguments.sSize == null ? 'A' : callingArguments.sSize.charAt(0);
      switch(sizeArg){
      case 'F':   sizePixel = 'D'; break;
      case 'A': sizePixel = 'D'; break;
      case 'a': sizePixel = 'A'; break;
      case 'b': sizePixel = 'B'; break;
      case 'c': sizePixel = 'C'; break;
      case 'D': sizePixel = 'D'; break;
      case 'E': sizePixel = 'E'; break;
      default: sizePixel = 'D'; break;
  }
  PropertiesGuiSwt propertiesGui = new PropertiesGuiSwt(gui.getDisplay(), sizePixel);
      LogMessage log = gui.getLogMessageOutputConsole();
  panelMng = new GuiPanelMngSwt(null, gui.getContentPane(), 120,80, propertiesGui, null, log);
  panelBuildIfc = panelMng;
  dlgAccess = panelMng;
  
  
  //create the basic appearance of the GUI. The execution sets dlgAccess:
  gui.addDispatchListener(initGuiDialog);
  if(!initGuiDialog.awaitExecution(1, 10000)) throw new RuntimeException("unexpected fail of execution initGuiDialog");
      
  //fileHandlerUcell = new FileViewer(panelMng);
  
      
      
  /**Creates the dialog elements while reading a config-file. */
  //
      //Register any user action. This should be done before the GUI-configuration is read.
  panelBuildIfc.registerUserAction("cmdInvoke", cmdInvoke);
  //dialogCellMng.registerTableAccess("msgOfDay", msgReceiver.msgOfDayAccessGui);
  //panelBuildIfc.registerTableAccess("msgOfDay", msgReceiver.msgOfDay;
  
  //dialogVellMng.re
  boolean bConfigDone = false;
  if(cargs.sFileGui != null){
      //configGuiWithZbnf.ctDone(0);  //counter for done initialized.
      File fileSyntax = new File(cargs.sPathZbnf + "/dialog.zbnf");
      dialogZbnfConfigurator = new GuiDialogZbnfControlled((MainCmd_ifc)gui, fileSyntax);
      gui.addDispatchListener(configGuiWithZbnf);
      bConfigDone = configGuiWithZbnf.awaitExecution(1, 10000);
  }    
  //assigns the fields which are visible to the oamOutValues-Manager to fill it with the values.
  if(!bConfigDone){
      console.writeError("No configuration");
  } else {
      try{ Thread.sleep(10);} catch(InterruptedException exc){}
      //The GUI-dispatch-loop should know the change worker of the panel manager. Connect both:
      gui.addDispatchListener(panelBuildIfc.getTheGuiChangeWorker());
      try{ Thread.sleep(10);} catch(InterruptedException exc){}
      //gets all prepared fields to show informations.
      //oamShowValues.setFieldsToShow(panelBuildIfc.getShowFields());
  }  

  //msgReceiver.test(); //use it after initGuiDialog!
  
}



void execute()
{
  dlgAccess.insertInfo("msgOfDay", Integer.MAX_VALUE, "Test\tMsg");
  //msgReceiver.start();
  while(gui.isRunning())
  { try{ Thread.sleep(100);} 
    catch (InterruptedException e)
    { dialogZbnfConfigurator.terminate();
    }
  }

}




/**The command-line-invocation (primary command-line-call. 
 * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
 */
public static void main(String[] args)
{ boolean bOk = true;
  CallingArguments cargs = new CallingArguments();
  //Initializes the GUI till a output window to show informations:
  CmdLineAndGui gui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
  try{ gui.parseArguments(); }
  catch(Exception exception)
  { gui.writeError("Cmdline argument error:", exception);
    gui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
    //gui.exit();
    bOk = false;  //not exiting, show error in GUI
  }
  
  //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
  //try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
  //}catch(ClassNotFoundException exc){
  //  System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
  //}
  //Loads the named class, and its base class InterProcessCommFactory. 
  //In that kind the calling of factory methods are regarded to socket.
  new InterProcessCommFactorySocket();
  
  CmdMenu main = new CmdMenu(cargs, gui);

  main.execute();
  
  gui.exit();
}

private final UserActionGui cmdInvoke = new UserActionGui()
{ 
  ProcessBuilder processBuilder = new ProcessBuilder("pwd");
  
  StringBuilder output = new StringBuilder();
  StringBuilder error = new StringBuilder();
   
  public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
  {
    if(sCmd != null){
      output.setLength(0);
      error.setLength(0);
      gui.executeCmdLine(processBuilder, widgetInfos.sCmd, null, Report.info, output, error);
      stop();
      dlgAccess.insertInfo("output", 0, output.toString());
      //gui.executeCmdLine(widgetInfos.sCmd, 0, null, null);
    }
  }
};


void stop(){} //debug helper

	
	
	
}
