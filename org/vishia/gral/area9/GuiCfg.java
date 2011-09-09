package org.vishia.gral.area9;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.cfg.GuiCfgData;
import org.vishia.gral.cfg.GuiCfgZbnf;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.gridPanel.GralGridProperties;
import org.vishia.gral.ifc.GraphicBaseFactory_ifc;
import org.vishia.gral.ifc.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GuiPlugUser_ifc;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.gral.swt.FactorySwt;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd;
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
public class GuiCfg 
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

  //protected final GralPanelContent panelContent;

    


  
  


/**The calling arguments of this class. It may be filled by command line invocation 
 * but maybe given in a direct way too while calling this class in a Java environment. */
final GuiCallingArgs cargs;

/**The configuration data for graphical appearance. */
protected final GuiCfgData guiCfgData = new GuiCfgData();




/**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
 * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
//GuiDialogZbnfControlled dialogZbnfConfigurator;   



/**Some actions may be processed by a user implementation. */
protected GuiPlugUser_ifc user;








protected final GuiMainAreaifc gui;

protected final MainCmd_ifc mainCmd;

protected GralGridMngBase panelMng;

/**Panel-Management-interface for the panels. */
protected GralGridBuild_ifc panelBuildIfc;

protected GralPanelMngWorking_ifc guiAccess;

protected GralTabbedPanel mainTabPanel;


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
public GuiCfg(GuiCallingArgs cargs, GuiMainCmd cmdGui) 
{ this.mainCmd = cmdGui;
  this.gui = cmdGui.gui;
  this.cargs = cargs;
  this.console = gui.getMainCmd();  

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
  GralGridProperties propertiesGui = cargs.graphicFactory.createProperties(sizePixel);
  LogMessage log = console.getLogMessageOutputConsole();
  //panelMng = new GuiPanelMngSwt(null, gui.getContentPane(), 120,80, propertiesGui, null, log);
  panelMng = cargs.graphicFactory.createPanelMng(null, 120,80, propertiesGui, null, log);
  panelBuildIfc = panelMng;
  guiAccess = panelMng;
  
  //panelContent = new PanelContent(user);
  if(user !=null){
    user.registerMethods(panelBuildIfc);
  }

  //Register any user action. This should be done before the GUI-configuration is read.
  panelBuildIfc.registerUserAction("cmdInvoke", cmdInvoke);
  
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
    mainTabPanel = panelMng.createTabPanel(null, 0);
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





/**Initializes the areas for the panels and configure the panels.
 * This routine can be overridden if other areas are need.
 */
protected void initGuiAreas()
{
  gui.setFrameAreaBorders(20, 80, 60, 85);
  gui.setStandardMenusGThread(new File("."), actionFile);
    
  gui.addFrameArea(1,1,3,1, mainTabPanel.getGuiComponent()); //dialogPanel);
 
}

protected void initMain()
{
  //create the basic appearance of the GUI. The execution sets dlgAccess:
  gui.addDispatchListener(initGuiDialog);
  if(!initGuiDialog.awaitExecution(1, 60000)) throw new RuntimeException("unexpected fail of execution initGuiDialog");
      
      
  /**Creates the dialog elements while reading a config-file. */
  //
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
        }  
      }
    } else {
      console.writeError("Config file not found: " + cargs.fileGuiCfg.getAbsolutePath());
    }
  }    
  try{ Thread.sleep(10);} catch(InterruptedException exc){}
  //The GUI-dispatch-loop should know the change worker of the panel manager. Connect both:
  gui.addDispatchListener(panelBuildIfc.getTheGuiChangeWorker());
  try{ Thread.sleep(10);} catch(InterruptedException exc){}
  //gets all prepared fields to show informations.
  //oamShowValues.setFieldsToShow(panelBuildIfc.getShowFields());
  
}

protected void stepMain(){}


public final void execute()
{
  initMain();
  //guiAccess.insertInfo("msgOfDay", Integer.MAX_VALUE, "Test\tMsg");
  //msgReceiver.start();
  while(gui.isRunning())
  { stepMain();
    try{ Thread.sleep(100);} 
    catch (InterruptedException e)
    { //dialogZbnfConfigurator.terminate();
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
      mainCmd.executeCmdLine(processBuilder, widgetInfos.sCmd, null, Report.info, output, error);
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


/**The command-line-invocation (primary command-line-call. 
 * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
 */
public static void main(String[] args)
{ boolean bOk = true;
  GuiCallingArgs cargs = new GuiCallingArgs();
  //Initializes the GUI till a output window to show information.
  //Uses the commonly GuiMainCmd class because here are not extra arguments.
  GuiMainCmd cmdGui = new GuiMainCmd(cargs, args, "GUI-cfg");  //implements MainCmd, parses calling arguments
  try{ cmdGui.parseArguments(); }
  catch(Exception exception)
  { cmdGui.writeError("Cmdline argument error:", exception);
    cmdGui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
    //gui.exit();
    bOk = false;  //not exiting, show error in GUI
  }
  
  if(bOk){
    //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
    //try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
    //}catch(ClassNotFoundException exc){
    //  System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
    //}
    //Loads the named class, and its base class InterProcessCommFactory. 
    //In that kind the calling of factory methods are regarded to socket.
    
    new InterProcessCommFactorySocket();
    
    GuiCfg main = new GuiCfg(cargs, cmdGui);
  
    main.execute();
  }    
  cmdGui.exit();
}



void stop(){} //debug helper

	
	
	
}
