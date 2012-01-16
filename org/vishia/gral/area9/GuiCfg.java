package org.vishia.gral.area9;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.cfg.GralCfgData;
import org.vishia.gral.cfg.GralCfgDesigner;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

/**This class is the basic class for configurable GUI applications with 9-Area Main window.
 * It works without any derivation, if only simple widgets are necessary.
 * If additional capabilities are need, this class can uses as the super class.
 * Some protected methods support overriding.
 * <br><br>
 * The class contains a {@link #main(String[])}. It is a complete ready to run application. 
 * The content of the GUI can be controlled by a script. The command line arguments are parsed in
 * {@link GralArea9MainCmd}, the universal or basic MainCmd for gral-GUI applications.
 * <br>
 * Registered user action can be used by script.
 * <ul>
 * <li>"cmdInvoke": The given parameter 'cmd' of the widget will be executed as command line.
 *   It is usefull to start commands from this GUI.
 * </ul>
 * A user plugin class can be plugged which can register some more user actions etc.
 * @author Hartmut Schorrig.
 *
 */
public class GuiCfg 
{

  /**The version.
   * <ul>
   * <li>2012-10-12 Hartmut chg: ctor needs a {@link GralPlugUser_ifc} which may be null: A plugin may be instantiated
   *   by reflection with String given class name. It may be possible to give it as parameter too.
   * <li>2011-10-20 Hartmut chg: ctor needs a {@link GralPlugUser2Gral_ifc} which may be null.
   *   Idea: a derived class should support it. Other Idea: either both via reflection or both maybe direct. 
   * <li>2011-10-11 Hartmut new: Switches MainCmd-output to OutputBox.
   * <li>2011-09-30 Hartmut new: menu 'Design/...' to edit fields and work with the {@link GralCfgDesigner}.
   * <li>2011-09-18 Hartmut new: The main tab panel has the name 'mainTab' and it is registered in the {@link #gralMng} now.
   *     Generally an application may have more as one tabbed panels.
   * <li>2011-09-10 Hartmut del: Remove dialogZbnfConfigurator, it was not used. It is the old solution.
   * <li>2011-09-08 Hartmut del: Remove the message panel. It was a special solution. 
   * <li>2011-08-08 Hartmut new: {@link #initMain()} as override-able method instead direct call of initializing.
   * <li>2011-08-07 Hartmut chg: Now {@link GuiCallingArgs} as primary class, not an inner class here.
   * <li>2011-08-07 Hartmut chg: Now {@link GralArea9MainCmd} as extra primary class.
   * <li>2011-08-04 Hartmut chg: rename and move from org/vishia/guiCmdMenu/CmdMenu.java to org/vishia/gral/area9/GuiCfg.java.
   *     It is a universal GUI which is configurable in content. Also it is a base class for some configurable GUI applications.
   * <li>2011-08-04 Hartmut chg: Use {@link GralPanelContent} instead the special InspcGuiPanelContent.     
   * <li>2011-08-04 Hartmut new: {@link #userInit()} as override-able method instead direct call of user.init(). 
   *     Advantage: User can do anything in the derived class.
   * <li>2011-08-04 Hartmut new: Use first key of argument --size: to determine the size. TODO parseArgs    
   * <li>2011-07-31 Hartmut new: First usage as super class.
   * <li>2010-01-01 Hartmut new: The base of this class was created with some applications.
   * </ul>
   */
  public final int version = 0x20110930;
  
  /**Composition of a Inspector-Target instance. This is only to visit this application for debugging.
   * Not necessary for functionality. */
  private final Inspector inspector;
  
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  public final Report console;

  //protected final GralPanelContent panelContent;

    


  
  


/**The calling arguments of this class. It may be filled by command line invocation 
 * but maybe given in a direct way too while calling this class in a Java environment. */
final GuiCallingArgs cargs;

/**The configuration data for graphical appearance. */
public final GralCfgData guiCfgData = new GralCfgData();




/**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
 * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
//GuiDialogZbnfControlled dialogZbnfConfigurator;   



/**Some actions may be processed by a user implementation. */
protected GralPlugUser_ifc user;

protected final GralPlugUser2Gral_ifc plugUser2Gui;






public final GralArea9_ifc gui;

public final GralArea9Window guiW;

public final MainCmd_ifc mainCmd;

public GralWidgetMng gralMng;

/**Panel-Management-interface for the panels. */
public GralGridBuild_ifc panelBuildIfc;

public GralPanelMngWorking_ifc guiAccess;

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
 * @param cmdGui The GUI-organization.
 * @param plugUser2Gui maybe possible a instances which plugs the user instance to the GUI.
 *   This instance may be defined in the context which calls this constructor.
 *   Note: A user instance may be instantiated with the cmd line calling argument "-plugin=JAVACLASSPATH"  
 */
public GuiCfg(GuiCallingArgs cargs, GralArea9MainCmd cmdGui, GralPlugUser_ifc plugUser, GralPlugUser2Gral_ifc plugUser2Gui) 
{ this.mainCmd = cmdGui;
  this.gui = cmdGui.gui;
  guiW = (GralArea9Window)gui;
  this.cargs = cargs;
  this.plugUser2Gui = plugUser2Gui;
  this.console = gui.getMainCmd();  

  if(plugUser !=null){
    this.user = plugUser;
  } else  if(cargs.sPluginClass !=null){
    try{
      Class<?> pluginClass = Class.forName(cargs.sPluginClass);
      Object oUser = pluginClass.newInstance();
      if(oUser instanceof GralPlugUser_ifc){
        user = (GralPlugUser_ifc) oUser;
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
  
  inspector = new Inspector("UDP:127.0.0.1:60088");
  inspector.start(this);
  
  //Creates a panel manager to work with grid units and symbolic access.
    //Its properties:  //##
  final char sizePixel = cargs.sSize == null ? 'C' : cargs.sSize.charAt(0);
  //GralGridProperties propertiesGui = cargs.graphicFactory.createProperties(sizePixel);
  //LogMessage log = console.getLogMessageOutputConsole();
  gralMng = cmdGui.gralMng; //cargs.graphicFactory.createPanelMng(null, 120,80, propertiesGui, null, log);
  panelBuildIfc = gralMng;
  guiAccess = gralMng;
  userInit();
  //panelContent = new PanelContent(user);
  if(user !=null){
    user.registerMethods(panelBuildIfc);
  }

  //Register any user action. This should be done before the GUI-configuration is read.
  panelBuildIfc.registerUserAction("cmdInvoke", cmdInvoke);
  
}


public GralPlugUser_ifc getPluggedUser(){ return user; }



/**Will be overridden... TODO InspcGui
 * 
 */
protected void userInit()
{
  if(user !=null){
    user.init(plugUser2Gui, gralMng, console.getLogMessageOutputConsole());
  }
  
  
}



/**Code snippet for initializing the GUI area (panel). This snippet will be executed
 * in the GUI-Thread if the GUI is created. 
 */
GralDispatchCallbackWorker initGraphic = new GralDispatchCallbackWorker("GuiCfg.initGraphic")
{
  @Override public void doBeforeDispatching(boolean onlyWakeup)
  {
    gralMng.selectPanel("primaryWindow");
    gralMng.setPosition(10, 16,5,20,0,'.');
    initGuiAreas();
    gralMng.gralDevice.removeDispatchListener(this);    
    countExecution();
  }
};


/**Code snippet to run the ZBNF-configurator (text controlled GUI)
 * 
 */
GralDispatchCallbackWorker configGuiWithZbnf = new GralDispatchCallbackWorker("GuiCfg.configGuiWithZbnf")
{
  
  @Override public void doBeforeDispatching(boolean onlyWakeup){
    panelBuildIfc.buildCfg(guiCfgData, cargs.fileGuiCfg);
    
    gralMng.gralDevice.removeDispatchListener(this);    
    
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
  initMenuGralDesigner();
  gralMng.selectPanel("primaryWindow");
  mainTabPanel = gralMng.addTabbedPanel("mainTab", null, 0);
  gui.addFrameArea(1,1,3,1, mainTabPanel); //dialogPanel);
  Appendable out = gui.getOutputBox();
  mainCmd.setOutputChannels(out, out);
 
}


protected void initMenuGralDesigner()
{
  gui.addMenuItemGThread("GralDesignEnable", "&Design/e&Nable", gralMng.actionDesignEditField);  
  gui.addMenuItemGThread("GralDesignEditField", "&Design/Edit &field", gralMng.actionDesignEditField);  
  gui.addMenuItemGThread("GralDesignUpdatePanel", "&Design/update &Panel from cfg-file", gralMng.actionReadPanelCfg);  
  
}



protected void initMain()
{
  //create the basic appearance of the GUI. The execution sets dlgAccess:
  gralMng.gralDevice.addDispatchOrder(initGraphic);
  
  if(!initGraphic.awaitExecution(1, 0)) throw new RuntimeException("unexpected fail of execution initGuiDialog");
      
      
  /**Creates the dialog elements while reading a config-file. */
  //
  //dialogVellMng.re
  boolean bConfigDone = false;
  if(cargs.fileGuiCfg != null){
    //configGuiWithZbnf.ctDone(0);  //counter for done initialized.
    if(cargs.fileGuiCfg.exists())
    {
      File fileSyntax = new File(cargs.sPathZbnf + "/dialog.zbnf");
      GralCfgZbnf cfgZbnf = new GralCfgZbnf(console, fileSyntax);
      
      String sError = cfgZbnf.configureWithZbnf(cargs.fileGuiCfg, guiCfgData);
      if(sError !=null){
        console.writeError(sError);
      } else {
        //dialogZbnfConfigurator = new GuiDialogZbnfControlled((MainCmd_ifc)gui, fileSyntax);
        //cfgBuilder = new GuiCfgBuilder(guiCfgData, panelBuildIfc, fileGui.getParentFile());
        //panelBuildIfc.setCfgBuilder(cfgBuilder);
        gralMng.gralDevice.addDispatchOrder(configGuiWithZbnf);
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
  gralMng.gralDevice.addDispatchOrder(panelBuildIfc.getTheGuiChangeWorker());
  try{ Thread.sleep(10);} catch(InterruptedException exc){}
  //gets all prepared fields to show informations.
  //oamShowValues.setFieldsToShow(panelBuildIfc.getShowFields());
  
}

protected void stepMain(){}

/**This routine is called on end of main-execution. This default implementation calls 
 * {@link GralPlugUser_ifc#close()}.
 * 
 */
protected void finishMain(){
  if(user !=null){ try{ user.close(); } catch(IOException exc ){} }
}


public final void execute()
{
  initMain();
  //guiAccess.insertInfo("msgOfDay", Integer.MAX_VALUE, "Test\tMsg");
  //msgReceiver.start();
  while(gralMng.gralDevice.isRunning())
  { stepMain();
    try{ Thread.sleep(100);} 
    catch (InterruptedException e)
    { //dialogZbnfConfigurator.terminate();
    }
  }

  inspector.shutdown();
  finishMain();
}




/**Registered as user action.
 * 
 */
private final GralUserAction cmdInvoke = new GralUserAction()
{ 
  ProcessBuilder processBuilder = new ProcessBuilder("pwd");
  
  StringBuilder output = new StringBuilder();
  StringBuilder error = new StringBuilder();
   
  public boolean userActionGui(String sCmd, GralWidget widgetInfos, Object... values)
  {
    if(sCmd != null){
      output.setLength(0);
      error.setLength(0);
      mainCmd.executeCmdLine(processBuilder, widgetInfos.sCmd, null, Report.info, output, error);
      stop();
      guiAccess.insertInfo("output", 0, output.toString());
      //gui.executeCmdLine(widgetInfos.sCmd, 0, null, null);
      return true;
    } else return false;
  }
};


protected GralUserAction actionFile = new GralUserAction()
{ @Override public boolean userActionGui(int key, GralWidget widg, Object... params)
  {
    if(widg.name.equals("menuFileSave")){
      String sError = null;
      try{
        Writer writer = new FileWriter(cargs.fileGuiCfg); //"save.cfg");
        sError = gralMng.saveCfg(writer);
        writer.close();
      } catch(java.io.IOException exc){
        sError = "Problem open file ";
      }
      if(sError !=null){
        console.writeError(sError);
      }
    }
    return true;
  }
};



/**The command-line-invocation (primary command-line-call). 
 * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
 */
public static void main(String[] args){ 
  boolean bOk = true;
  //
  //Uses the commonly GuiCallingArgs class because here are not extra arguments.
  GuiCallingArgs cargs = new GuiCallingArgs();
  //Initializes the GUI till a output window to show information.
  GralArea9MainCmd cmdGui = new GralArea9MainCmd(cargs, args);  //implements MainCmd, parses calling arguments
  //Initializes the graphic window and parse the parameter of args (command line parameter).
  //Parameter errors will be output in the graphic window in its given output area.
  bOk = cmdGui.parseArgumentsAndInitGraphic("Gui-Cfg", "3A3C");
  
  if(bOk){
    //loads the named class, so it is existent as factory.
    new InterProcessCommFactorySocket();
    //
    //the third parameter may be a plugin, use it in your application if necessary.
    GuiCfg main = new GuiCfg(cargs, cmdGui, null, null);
    //
    //starts execution.
    main.execute();
  }    
  cmdGui.exit();
}



void stop(){} //debug helper

	
	
	
}
