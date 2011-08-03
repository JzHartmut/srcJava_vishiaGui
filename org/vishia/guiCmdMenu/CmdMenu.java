package org.vishia.guiCmdMenu;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.GuiDialogZbnfControlled;
import org.vishia.gral.GuiDispatchCallbackWorker;
import org.vishia.gral.GuiPanelMngBase;
import org.vishia.gral.GuiPanelMngBuildIfc;
import org.vishia.gral.GuiPanelMngWorkingIfc;
import org.vishia.gral.GuiPlugUser_ifc;
import org.vishia.gral.TabPanel;
import org.vishia.gral.UserActionGui;
import org.vishia.gral.WidgetCmpnifc;
import org.vishia.gral.WidgetDescriptor;
import org.vishia.gral.cfg.GuiCfgData;
import org.vishia.gral.cfg.GuiCfgZbnf;
import org.vishia.guiInspc.InspcGuiPanelContent;
import org.vishia.guiInspc.InspcPlugUser_ifc;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

/**This class is the basic class for configurable GUI applications with 9-Area Main window.
 * It works without any derivation, if only simple widgets are necessary.
 * If additional capabilities are need, this class can uses as the super class.
 * Some protected methods support overriding.
 * 
 * @author Hartmut Schorrig.
 *
 */
public class CmdMenu 
{

  /**The version.
   * <ul>
   * <li>2011-07-31 Hartmut new: First usage as super class.
   * <li>2010-01-01 Hartmut new: The base of this class was created with some applications.
   * </ul>
   */
  public final int version = 0x20110731;
  
  /**Composition of a Inspector-Target instance. This is only to visit this application for debugging.
   * Not necessary for functionality. */

  private final Inspector inspector;
  
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  protected final Report console;

    


  /**The command-line-arguments are stored in an extra class, which can arranged in any other class too. 
   * The separation of command line argument helps to invoke the functionality with different calls, 
   * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT.
   * This class should be the super class of an derived application's CallingArguments class. 
   */
  public static class CallingArguments
  {
    /**Name of the config-file for the Gui-appearance. */
    //String sFileGui;
    
    /**The configuration file. It is created while parsing arguments.
     * The file is opened and closed while the configuration is used to build the GUI.
     * The file is used to write on menu-save action.
     */
    protected File fileGuiCfg;
    
    String sPathZbnf = "GUI";
    
    /**The time zone to present all time informations. */
    String sTimeZone = "GMT";
    
    /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
    String sSize;
    
    /**The own ipc-address for Interprocess-Communication with the target.
     * It is a string, which determines the kind of communication.
     * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
     */
    public String sOwnIpcAddr;
    
    /**A class which is used as plugin for user specifies. It is of interface {@link InspcPlugUser_ifc}. */
    String sPluginClass;
    
  } //class CallingArguments
  
  


/**The calling arguments of this class. It may be filled by command line invocation 
 * but maybe given in a direct way too while calling this class in a Java environment. */
final CallingArguments cargs;

/**The configuration data for graphical appearance. */
protected final GuiCfgData guiCfgData = new GuiCfgData();




/**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
 * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
GuiDialogZbnfControlled dialogZbnfConfigurator;   



/**Some actions may be processed by a user implementation. */
protected GuiPlugUser_ifc user;





protected final InspcGuiPanelContent panelContent;





protected final MainCmdSwt gui;


protected GuiPanelMngBase panelMng;

/**Panel-Management-interface for the panels. */
private GuiPanelMngBuildIfc panelBuildIfc;

private GuiPanelMngWorkingIfc guiAccess;

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
public CmdMenu(CallingArguments cargs, MainCmdSwt gui) 
{ this.gui = gui;
  boolean bOk = true;
  this.cargs = cargs;
  this.console = gui;  

  if(cargs.sPluginClass !=null){
    try{
      Class<?> pluginClass = Class.forName(cargs.sPluginClass);
      Object oUser = pluginClass.newInstance();
      if(oUser instanceof GuiPlugUser_ifc){
        user = (GuiPlugUser_ifc) oUser;
      } else {
        console.writeError("user-plugin - fault type: " + cargs.sPluginClass 
          + "; it should be type of GuiPlugUser_ifc");
      }
    } catch (Exception exc){
      user = null;
      console.writeError("user-plugin - cannot instantiate: " + cargs.sPluginClass + "; "
        + exc.getMessage());
    }
  }
  
  userInit();
  inspector = new Inspector("UDP:127.0.0.1:60088");
  inspector.start(this);
  
  //Creates a panel manager to work with grid units and symbolic access.
    //Its properties:  //##
  final char sizePixel = cargs.sSize == null ? 'C' : cargs.sSize.charAt(0);
  //for test situation
  PropertiesGuiSwt propertiesGui = new PropertiesGuiSwt(gui.getDisplay(), sizePixel);
      LogMessage log = gui.getLogMessageOutputConsole();
  panelMng = new GuiPanelMngSwt(null, gui.getContentPane(), 120,80, propertiesGui, null, log);
  panelBuildIfc = panelMng;
  guiAccess = panelMng;
  
  if(user !=null){
    user.registerMethods(panelBuildIfc);
  }
  panelContent = new InspcGuiPanelContent(user);
  

  //create the basic appearance of the GUI. The execution sets dlgAccess:
  gui.addDispatchListener(initGuiDialog);
  if(!initGuiDialog.awaitExecution(1, 10000)) throw new RuntimeException("unexpected fail of execution initGuiDialog");
      
      
  /**Creates the dialog elements while reading a config-file. */
  //
      //Register any user action. This should be done before the GUI-configuration is read.
  panelBuildIfc.registerUserAction("cmdInvoke", cmdInvoke);
  //dialogCellMng.registerTableAccess("msgOfDay", msgReceiver.msgOfDayAccessGui);
  //panelBuildIfc.registerTableAccess("msgOfDay", msgReceiver.msgOfDay;
  
  //dialogVellMng.re
  boolean bConfigDone = false;
  if(cargs.fileGuiCfg != null){
    //configGuiWithZbnf.ctDone(0);  //counter for done initialized.
    if(cargs.fileGuiCfg.exists())
    {
      File fileSyntax = new File(cargs.sPathZbnf + "/dialog.zbnf");
      GuiCfgZbnf cfgZbnf = new GuiCfgZbnf(console, fileSyntax);
      
      String sError = cfgZbnf.configureWithZbnf(cargs.fileGuiCfg, guiCfgData);
      if(sError !=null){
        console.writeError(sError);
      } else {
        //dialogZbnfConfigurator = new GuiDialogZbnfControlled((MainCmd_ifc)gui, fileSyntax);
        //cfgBuilder = new GuiCfgBuilder(guiCfgData, panelBuildIfc, fileGui.getParentFile());
        //panelBuildIfc.setCfgBuilder(cfgBuilder);
        gui.addDispatchListener(configGuiWithZbnf);
        bConfigDone = configGuiWithZbnf.awaitExecution(1, 10000);
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
      }
    } else {
      console.writeError("Config file not found: " + cargs.fileGuiCfg.getAbsolutePath());
    }
  }    
  
}



/**Will be overridden... TODO InspcGui
 * 
 */
protected void userInit()
{
  if(user !=null){
    user.init(null, console.getLogMessageOutputConsole());
  }
  
  
}



/**Code snippet for initializing the GUI area (panel). This snippet will be executed
 * in the GUI-Thread if the GUI is created. 
 */
GuiDispatchCallbackWorker initGuiDialog = new GuiDispatchCallbackWorker()
{
  @Override public void doBeforeDispatching(boolean onlyWakeup)
  {
    initGuiAreas();
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
    gui.setTitleAndSize("GUI", 50, 100, 1200, 900);
    panelBuildIfc.buildCfg(guiCfgData, cargs.fileGuiCfg);
    
    gui.removeDispatchListener(this);    
    
    countExecution();
      
  }
////
};





/**Initializes the areas for the configuration panels.
 * This routine can be overridden if other areas are need.
 */
protected void initGuiAreas()
{
  gui.setFrameAreaBorders(20, 80, 60, 85);
  gui.setStandardMenusGThread(new File("."), actionFile);

  
  //Creates a Tab-Panel:
  panelMng.tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate);
  panelMng.tabPanel.addGridPanel("operation", "&Operation",1,1,10,10);
    
  gui.addFrameArea(1,1,3,1, panelMng.tabPanel.getGuiComponent()); //dialogPanel);
  //##
  /*
  WidgetCmpnifc msgPanel = panelMng.createGridPanel(  
      panelMng.propertiesGui.colorBackground_
      , panelMng.propertiesGui.xPixelUnit(), panelMng.propertiesGui.yPixelUnit(), 5, 5);
  panelMng.registerPanel("msg", msgPanel);
  gui.addFrameArea(1,2,3,1, msgPanel); //dialogPanel);
  */
 
}

protected void initMain(){}

protected void stepMain(){}


public void execute()
{
  initMain();
  //guiAccess.insertInfo("msgOfDay", Integer.MAX_VALUE, "Test\tMsg");
  //msgReceiver.start();
  while(gui.isRunning())
  { stepMain();
    try{ Thread.sleep(100);} 
    catch (InterruptedException e)
    { dialogZbnfConfigurator.terminate();
    }
  }

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
      guiAccess.insertInfo("output", 0, output.toString());
      //gui.executeCmdLine(widgetInfos.sCmd, 0, null, null);
    }
  }
};


protected UserActionGui actionFile = new UserActionGui()
{ @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
  {
    if(sIntension.equals("save")){
      String sError = null;
      try{
        Writer writer = new FileWriter(cargs.fileGuiCfg); //"save.cfg");
        sError = panelMng.saveCfg(writer);
        writer.close();
      } catch(java.io.IOException exc){
        sError = "Problem open file ";
      }
      if(sError !=null){
        console.writeError(sError);
      }
    }
  }
  
};

/**Organisation class for the GUI.
 */
protected static class CmdLineAndGui extends MainCmdSwt
{

  
  /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
   * It is given as ctor-parameter.
   */
  final protected CallingArguments cargs;
  
  /**ctor called in static main.
   * @param cargs aggregation to command-line-argument data, it will be filled here.
   * @param args The command-line-calling arguments from static main
   */
  public CmdLineAndGui(CallingArguments cargs, String[] args, String sTitle)
  { 
    super(args);
    super.addAboutInfo("Gui");
    super.addAboutInfo("made by HSchorrig, 2010-06-07, 2011-11-13");
    //super.addStandardHelpInfo();
    this.cargs = cargs;
    super.setTitleAndSize(sTitle, 50,50,900, 600); //600);  //This instruction should be written first to output syntax errors.
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
      { cargs.fileGuiCfg = new File(getArgument(5));  //the graphic GUI-appearance
      
      }
      else if(arg.startsWith("-ownIpc=")) 
      { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
      }
      else if(arg.startsWith("-timeZone=")) 
      { cargs.sTimeZone = getArgument(10);   //an example for default output
      }
      else if(arg.startsWith("-size=")) 
      { cargs.sSize = getArgument(6);   //an example for default output
      }
      else if(arg.startsWith("-plugin=")) 
      { cargs.sPluginClass = getArgument(8);   //an example for default output
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

/**The command-line-invocation (primary command-line-call. 
 * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
 */
public static void main(String[] args)
{ boolean bOk = true;
  CallingArguments cargs = new CallingArguments();
  //Initializes the GUI till a output window to show informations:
  CmdLineAndGui gui = new CmdLineAndGui(cargs, args, "GUI-cfg");  //implements MainCmd, parses calling arguments
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



void stop(){} //debug helper

	
	
	
}
