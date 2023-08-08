package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.base.GralShowMethods;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspectorTarget.Inspector;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

/**This class is the basic class for configurable GUI applications with 9-Area Main window.
 * It works without any derivation, if only simple widgets are necessary.
 * If additional capabilities are need, this class can uses as the super class.
 * Some protected methods support overriding, especially:
 * <ul>
 * <li>{@link #initMain()}: initializes the GUI
 * <li>{@link #stepMain()}: Will be invoked in the main loop.
 * <li>{@link #finishMain()}: invoked on exit.
 * </ul>
 * Hint: call super.stepMain() etc. in an overridden method if necessary.
 * <br><br>
 * The class contains a {@link #main(String[])}. It is a complete ready to run application. 
 * The content of the GUI can be controlled by a script. The command line arguments are parsed in
 * {@link GralArea9MainCmd}, the universal or basic MainCmd for gral-GUI applications.
 * <br><br>
 * The configuration is done with {@link GralCfgZbnf} in the {@link #initMain()} routine.
 * 
 * <br><br>
 * <br><br>
 * <br><br>
 * 
 * <br>
 * Registered user action can be used by script.
 * <ul>
 * <li>"cmdInvoke": The given parameter 'cmd' of the widget will be executed as command line.
 *   It is usefull to start commands from this GUI for example with a button.
 * </ul>
 * A user plugin class can be plugged which can register some more user actions etc.
 * @author Hartmut Schorrig.
 *
 */
public class GuiCfg 
{

  /**The version, history and license.
   * <ul>
   * <li>2023-08-08 Hartmut refactoring: Moved from package org.vishia.gral.area9 (deprecated) to org.vishia.gral.cfg
   *   where it is conceptual proper, reactivated as super class for {@link org.vishia.guiInspc.InspcGui}.
   * <li>2013-12-02 Hartmut new Parameter for {@link #GuiCfg(GuiCallingArgs, GralArea9MainCmd, GralPlugUser_ifc, GralPlugUser2Gral_ifc, List)}:
   *    cfgConditions.
   * <li>2012-09-17 Hartmut new: {@link #showMethods}
   * <li>2011-10-12 Hartmut chg: ctor needs a {@link GralPlugUser_ifc} which may be null: A plugin may be instantiated
   *   by reflection with String given class name. It may be possible to give it as parameter too.
   * <li>2011-10-20 Hartmut chg: ctor needs a {@link GralPlugUser2Gral_ifc} which may be null.
   *   Idea: a derived class should support it. Other Idea: either both via reflection or both maybe direct. 
   * <li>2011-10-11 Hartmut new: Switches MainCmd-output to OutputBox.
   * <li>2011-09-30 Hartmut new: menu 'Design/...' to edit fields and work with the {@link GralCfgDesigner}.
   * <li>2011-09-18 Hartmut new: The main tab panel has the name 'mainTab' and it is registered in the {@link #_gralMng} now.
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
   * 
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static String version = "2023-08-08";
  
  /**Composition of a Inspector-Target instance. This is only to visit this application for debugging.
   * Not necessary for functionality. */
  private final Inspector inspector;
  
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  public final LogMessage console; //logCfg;

  //protected final GralPanelContent panelContent;

    


  
  


/**The calling arguments of this class. It may be filled by command line invocation 
 * but maybe given in a direct way too while calling this class in a Java environment. */
final GuiCallingArgs cargs;

/**The configuration data for graphical appearance. */
public final GralCfgData guiCfgData;

public final GralCfgBuilder guiCfgBuilder;


/**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
 * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
//GuiDialogZbnfControlled dialogZbnfConfigurator;   



/**Some actions may be processed by a user implementation. */
protected GralPlugUser_ifc user;

protected final GralPlugUser2Gral_ifc plugUser2Gui;






//public final GralArea9_ifc gui;
//
//public final GralArea9Window guiW;

//public final MainCmd_ifc mainCmd;

/**Especially for debug access to the singleton instance, start with _ to present on top of variables. */
public final GralMng gralMng;

/**The meaning of this GralPos is, it contains the reference to this.gralMng.
 * It can be used especially for new Windows, but also for content in windows.
 * It is changed while using with the sPosName of the created widget.
 */
final GralPos refPos; 

public final GralWindow window;

protected final GralMenu menuBar;

final GralArea9Panel area9;

/**The panel for all tabs from configuration.
 * It is assembled as middle area of the area9*/
final GralPanelContent tabPanel;

/**An output box assembled as bottom area of the area9. */
final GralTextBox outputBox;



/**Panel-Management-interface for the panels. */
//public GralMngBuild_ifc panelBuildIfc;

//public GralMng_ifc guiAccess;
//
//protected GralTabbedPanel mainTabPanel;


//private static GuiCfg singleton;

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
public GuiCfg(GuiCallingArgs cargs
    , GralPlugUser_ifc plugUser, GralPlugUser2Gral_ifc plugUser2Gui
    , List<String> cfgConditions) { 
//  this.mainCmd = cmdGui;
//  this.gui = cmdGui.gui;
//  guiW = (GralArea9Window)gui;
  this.cargs = cargs;
  if(cargs.fileGuiCfg == null) {
    throw new IllegalArgumentException("GuiCfg should have a config file. argument -gui=path");
  }
  this.plugUser2Gui = plugUser2Gui;
  this.console = new LogMessageStream(System.out, null, null, true, null);  
  this.gralMng = new GralMng(this.console);
  this.refPos = new GralPos(this.gralMng);

  this.window = new GralWindow(this.refPos, "@screen,16+80, 20+120=mainWin", "The.inspector"
      , GralWindow_ifc.windMinimizeOnClose | GralWindow_ifc.windResizeable);

  this.menuBar = this.window.getMenuBar();

  this.area9 = new GralArea9Panel(this.refPos, "area9");

  this.outputBox = new GralTextBox(this.refPos, "@area9,A3C3=outputBox", true, null, '\0');

  /**The panel for all tabs from configuration.*/
  this.tabPanel = new GralPanelContent(this.refPos, "@area9,A1C2=tabs");


  
  this.guiCfgData = new GralCfgData(cfgConditions);
  
  this.guiCfgBuilder = new GralCfgBuilder(this.guiCfgData, this.gralMng, cargs.fileGuiCfg.getParentFile());
  
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
  
  if(cargs.sInspectorOwnPort !=null){
    inspector = new Inspector(cargs.sInspectorOwnPort);
    inspector.start(this);
  } else {
    inspector = null; //don't use.
  }
  if(cargs.fileGuiCfg !=null) {
    readConfig(cargs.fileGuiCfg);
  }

  userInit();
  //panelContent = new PanelContent(user);
  
  if(user !=null){
    user.registerMethods(this.gralMng);
  }

  //Register any user action. This should be done before the GUI-configuration is read.
  gralMng.registerUserAction("cmdInvoke", cmdInvoke);
}


public GralPlugUser_ifc getPluggedUser(){ return user; }



/**Will be overridden... TODO InspcGui
 * 
 */
protected void userInit()
{
  if(user !=null){
    //user.init(plugUser2Gui, _gralMng, guiCfgData.dataReplace, this.cargs, console.getLogMessageOutputConsole());
  }
  
  
}





/**Code snippet to run the ZBNF-configurator (text controlled GUI)
 * 
 */
//GralGraphicTimeOrder configGuiWithZbnf = new GralGraphicTimeOrder("GuiCfg.configGuiWithZbnf", this._gralMng)
//{
//  
//  @Override public void executeOrder(){
//    panelBuildIfc.buildCfg(guiCfgData, cargs.fileGuiCfg);
//    gralMng.initCfgDesigner();
//
//    //gralMng.gralDevice.removeDispatchListener(this);    
//    
//    //countExecution();
//      
//  }
//////
//};



/**Initializes the areas for the panels and configure the panels.
 * This routine can be overridden if other areas are need.
 */
//protected void initGuiAreas(String sMainArea)
//{
//  gui.setFrameAreaBorders(20, 80, 60, 85);
//  gui.setStandardMenusGThread(new File("."), actionFile);
//  initMenuGralDesigner();
//  _gralMng.selectPanel("primaryWindow");
//  mainTabPanel = _gralMng.addTabbedPanel("mainTab", null, 0);
//  gui.addFrameArea(sMainArea, mainTabPanel); //dialogPanel);
//  Appendable out = gui.getOutputBox();
//  mainCmd.setOutputChannels(out, out);
// 
//}


protected void initMenuGralDesigner() {
  this.menuBar.addMenuItem("GralDesignEnable", "&Design/e&Nable", this.gralMng.actionDesignEditField);
  this.menuBar.addMenuItem("GralDesignEditField", "&Design/Edit &field", this.gralMng.actionDesignEditField);  
  this.menuBar.addMenuItem("GralDesignUpdatePanel", "&Design/update &Panel from cfg-file", this.gralMng.actionReadPanelCfg);  
//  
}


//protected void initMain()
//{
//  //create the basic appearance of the GUI. The execution sets dlgAccess:
//  System.out.println("GuiCfg.initMain() - addDispatchOrder initGraphic, wait for execution;");
//  this._gralMng.addDispatchOrder(initGraphic);
//  
//  if(!initGraphic.awaitExecution(1, 0)){
//    System.out.println("GuiCfg.initMain() - initGraphic does not respond;");
//    throw new RuntimeException("unexpected fail of execution initGuiDialog");
//  }
//  System.out.println("GuiCfg.initMain() - await initGraphic ok;");
//      
//      
//  /**Creates the dialog elements while reading a config-file. */
//  //
//  //dialogVellMng.re
//  boolean bConfigDone = false;
//  if(cargs.fileGuiCfg != null){
//    //configGuiWithZbnf.ctDone(0);  //counter for done initialized.
//    if(cargs.fileGuiCfg.exists())
//    {
//      File fileSyntax = new File(cargs.sPathZbnf + "/dialog.zbnf");
//      GralCfgZbnf cfgZbnf = new GralCfgZbnf(console, fileSyntax, GralMng.get());
//      System.out.println("GuiCfg - start parsing cfg file; " + cargs.fileGuiCfg.getAbsolutePath());
//      String sError = "todo"; //cfgZbnf.configureWithZbnf(cargs.fileGuiCfg, guiCfgData);
//      System.out.println("GuiCfg - finish parsing cfg file; ");
//      if(sError !=null){
//        console.writeError(sError);
//      } else {
//        //dialogZbnfConfigurator = new GuiDialogZbnfControlled((MainCmd_ifc)gui, fileSyntax);
//        //cfgBuilder = new GuiCfgBuilder(guiCfgData, panelBuildIfc, fileGui.getParentFile());
//        //panelBuildIfc.setCfgBuilder(cfgBuilder);
//        this._gralMng.addDispatchOrder(configGuiWithZbnf);
//        bConfigDone = configGuiWithZbnf.awaitExecution(1, 10000);
//        if(!bConfigDone){
//          console.writeError("No configuration");
//        }  
//      }
//    } else {
//      console.writeError("Config file not found: " + cargs.fileGuiCfg.getAbsolutePath());
//    }
//  }    
//  try{ Thread.sleep(10);} catch(InterruptedException exc){}
//  //The GUI-dispatch-loop should know the change worker of the panel manager. Connect both:
//  try{ Thread.sleep(10);} catch(InterruptedException exc){}
//  //gets all prepared fields to show informations.
//  //oamShowValues.setFieldsToShow(panelBuildIfc.getShowFields());
//  
//}

private final void init() {
  this.gralMng.createGraphic("SWT", 'D', this.gralMng.log);
}


/**Reads a new configuration from the given file. 
 * @param fileCfg
 */
protected void readConfig(File fileCfg) {
  try {
    this.guiCfgData.clear();
    GralCfgZbnf zbnfCfgParser = new GralCfgZbnf(this.gralMng);  // temporary instance of this
    zbnfCfgParser.configureWithZbnf(fileCfg, this.guiCfgData);
    this.guiCfgBuilder.buildGui(this.guiCfgData, this.tabPanel);                               // builds only the Gral instances without implementation graphic
  } catch(Exception exc) {
    System.err.println(exc.getMessage());
  }

}



protected void stepMain () {
}



/**This routine is called on end of main-execution. This default implementation calls 
 * {@link GralPlugUser_ifc#close()}.
 * 
 */
protected void finishMain(){
  if(user !=null){ try{ user.close(); } catch(IOException exc ){} }
}


public final void execute()
{
  try {
    this.init();
//    this.completeConstruction();
//    this.startupThreads();
    while(this.gralMng.isRunning()){
      try{ Thread.sleep(20);} 
      catch (InterruptedException e) { }
      this.stepMain();
    }
    this.gralMng.closeApplication();
  } catch(Exception exc) {
    System.err.println("Unexpected exception: " + exc.getMessage());
    exc.printStackTrace(System.err);
  }

  if(inspector !=null) { 
    inspector.shutdown(); 
  }
  finishMain();
}



public void showInfoBox(CharSequence text) {
  this.outputBox.setFocus();
}

public void setTextInfoBox(CharSequence text) {
  this.outputBox.setText(text);
}

public void appendTextInfoBox(CharSequence text) {
  this.outputBox.append(text);
}




/**Registered as user action.
 * 
 */
private final GralUserAction cmdInvoke = new GralUserAction("cmdInvoke")
{ 
  ProcessBuilder processBuilder = new ProcessBuilder("pwd");
  
  StringBuilder output = new StringBuilder();
  StringBuilder error = new StringBuilder();
   
  @Override
  public boolean exec(int cmd, GralWidget_ifc wdgi, Object... values)
  {
    if(KeyCode.isControlFunctionMouseUpOrMenu(cmd)){
      GralWidget wdg = (GralWidget)wdgi;
      output.setLength(0);
      error.setLength(0);
//      thisCmd.executeCmdLine(processBuilder, wdg.sCmd, null, Report.info, output, error);
      stop();
//      guiAccess.addText("output", output);  //adds the result to any widget with name "output"
      //gui.executeCmdLine(widgetInfos.sCmd, 0, null, null);
      return true;
    } else return false;
  }
};


protected GralUserAction actionFile = new GralUserAction("actionFile")
{ @Override public boolean userActionGui(int key, GralWidget widg, Object... params)
  {
    if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
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
public static void main(String[] cmdArgs) { 
  boolean bOk = true;
  //
  new InterProcessCommFactorySocket();
  //
  GuiCallingArgs cargs = new GuiCallingArgs();
  int error = 0;
  try {
    cargs.parseArgs(cmdArgs);
  } catch (Exception exc) {
    System.err.println("cmdline arg exception: " + exc.getMessage());
    error = 255;
  }
  //Initializes the GUI till a output window to show informations:
//  CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
  
  if(error ==0) {
    try {
      GuiCfg main = new GuiCfg(cargs, null, null, null);
      main.init();
//      main.completeConstruction();
//      main.startupThreads();
      while(main.gralMng.isRunning()){
        try{ Thread.sleep(20);} 
        catch (InterruptedException e) { }
        main.stepMain();
      }
      main.gralMng.closeApplication();
    } catch(Exception exc) {
      System.err.println("Unexpected exception: " + exc.getMessage());
      exc.printStackTrace(System.err);
    }
  }
  System.exit(error);
}



void stop(){} //debug helper

	
	
	
}
