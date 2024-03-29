package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.vishia.cmd.JZtxtcmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.ifc.GralActionJztc;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspectorTarget.Inspector;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;
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
   * <li>2023-08-15 Hartmut now a JZtxtcmd script is possible for actions sub routines, beside cfg for the widgets. 
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
   * <li>2011-08-04 Hartmut new: Use first key of argument --size: to determine the size. 
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
  public final static String version = "2023-08-15";
  
  /**Composition of a Inspector-Target instance. This is only to visit this application for debugging.
   * Not necessary for functionality. 
   * The instance is only created if {@link GuiCallingArgs#sInspectorOwnPort} is given as argument. 
   * It means it depends on the arguments of the user, or maybe set this argurment by the inheriting application. */
  private final Inspector inspector;
  
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  public final LogMessage console; //logCfg;

  //protected final GralPanelContent panelContent;

    


  protected final JZtxtcmd jzcmd;
  
  protected final  GralActionJztc jzTcActions;
    
  //protected final JZtxtcmdScript jzTcScript;
  

  


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
  protected final GralPos refPos; 
  
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
  
  /**ctor for the main class of a GUI application. 
   * <br><br>
   * The ctor checks whether a GUI-configuration file is given. If given, then reads the configuration to determine the graphic.
   * <br><br>
   * If not given, then only an empty main Window will be created. Whereas name, position and title of the window 
   * can be gotten from {@link GuiCallingArgs#sTitle}, should be given by command line arguments or set before construction. 
   * <br><br>
   * The GUI can be completed after construction with some more GUI elements, also independent sub windows, panels etc.
   * This can be done in the constructor of an inherit class or also after construction. 
   * Note that build of the implementation graphic is done in {@link #init()} which can be overridden.
   * <br><br>
   * The the GUI will be completed with the content of the GUI-configuration file
   * or also alternatively by programmed GUI elements. For that the ctor of the inherit class can create widgets.
   * After finish this (super) constructor the {@link #refPos} refers the last created panel,
   * so that panel can immediately used for widgets, or the window can be selected etc. pp.  
   *   
   * @param cargs The given calling arguments.
   *   If {@link GuiCallingArgs#sTitle} is set then this is used as title for the window.  
   * @param cmdGui The GUI-organization.
   * @param plugUser2Gui maybe possible a instances which plugs the user instance to the GUI.
   *   This instance may be defined in the context which calls this constructor.
   *   Note: A user instance may be instantiated with the cmd line calling argument "-plugin=JAVACLASSPATH"  
   */
  public GuiCfg(GuiCallingArgs cargs
      , GralPlugUser_ifc plugUser, GralPlugUser2Gral_ifc plugUser2Gui
      , List<String> cfgConditions
    ) { 
    //
    new InterProcessCommFactorySocket();
    this.cargs = cargs;
    this.plugUser2Gui = plugUser2Gui;
    this.console = new LogMessageStream(System.out, null, null, true, null);  
    this.gralMng = new GralMng(this.console);
    this.refPos = new GralPos(this.gralMng);
    
    if(cargs.fileJzTc !=null) {                              // if a JzTxtCmd file is given,  
      this.jzcmd = new JZtxtcmd();
      this.jzTcActions = processJzTc(this.jzcmd, cargs.fileJzTc, this);
    } else {
      this.jzcmd = null;
      this.jzTcActions = null;
    }
    this.guiCfgData = new GralCfgData(cfgConditions);
    if(cargs.fileGuiCfg !=null) {
      readConfig(cargs.fileGuiCfg);                          // first read the GUI configuration file. may contain info about window.
    }
    String sWinTitle = cargs.sTitle !=null ? cargs.sTitle 
                     : this.guiCfgData.firstWindow.title !=null ? this.guiCfgData.firstWindow.title
                     : cargs.fileGuiCfg !=null ? "GuiCfg: " + cargs.fileGuiCfg.getName()
                     : "GuiCfg";
    int winProps = GralWindow_ifc.windResizeable;
    boolean bMinimizeOnClose = false;
    if(this.guiCfgData.firstWindow !=null && this.guiCfgData.firstWindow.data !=null || cargs.aboutInfo() !=null) {
      bMinimizeOnClose = true;
    }
    if(bMinimizeOnClose || this.guiCfgData.firstWindow !=null && this.guiCfgData.firstWindow.help !=null) {
      winProps |= GralWindow_ifc.windMinimizeOnClose;        // then menues will be created, hence also the exit menu.
    }
    this.window = new GralWindow(this.refPos, "@screen,16+80, 20+120=mainWin", sWinTitle
        , winProps);
  
    this.menuBar = this.window.getMenuBar();
    if((winProps & GralWindow_ifc.windMinimizeOnClose) !=0) {
      this.menuBar.addMenuItem("menuFClose", "&File/e&Xit", this.gralMng.actionClose);
      this.menuBar.addMenuItem("menuWClose", "&Window/e&Xit", this.gralMng.actionClose);
      this.menuBar.addMenuItem("menuHClose", "&Help/e&Xit", this.gralMng.actionClose);
    }
    if(this.guiCfgData.firstWindow !=null && this.guiCfgData.firstWindow.data !=null) {
    }
    
    this.area9 = new GralArea9Panel(this.refPos, "area9");
  
    this.outputBox = new GralTextBox(this.refPos, "@area9,A3C3=outputBox", true, null, '\0');
  
    /**The panel for all tabs from configuration.*/
    this.tabPanel = new GralPanelContent(this.refPos, "@area9,A1C2=tabs");
  
    if(cargs.fileGuiCfg != null) {
      this.guiCfgBuilder = buildGuiCfg(cargs, this.guiCfgData, this.tabPanel);
    } else {
      this.guiCfgBuilder = null;
    }
    
    if(plugUser !=null){
      this.user = plugUser;
    } else  if(cargs.sPluginClass !=null){
      try{
        Class<?> pluginClass = Class.forName(cargs.sPluginClass);
        Object oUser = pluginClass.newInstance();
        if(oUser instanceof GralPlugUser_ifc){
          this.user = (GralPlugUser_ifc) oUser;
        } else {
          this.console.writeError("user-plugin - fault type: " + cargs.sPluginClass 
            + "; it should be type of GuiPlugUser_ifc");
        }
      } catch (Exception exc){
        this.user = null;
        this.console.writeError("user-plugin - cannot instantiate: " + cargs.sPluginClass + "; "
          + exc.getMessage());
      }
    }
    
    if(cargs.sInspectorOwnPort !=null){
      this.inspector = new Inspector(cargs.sInspectorOwnPort);
      this.inspector.start(this);
    } else {
      this.inspector = null; //don't use.
    }
  
    if(this.jzTcActions !=null) {
      initJzTc();
    }
    
    userInit();
    //panelContent = new PanelContent(user);
    
    if(this.user !=null){
      this.user.registerMethods(this.gralMng);
    }
  
    //Register any user action. This should be done before the GUI-configuration is read.
    this.gralMng.registerUserAction("cmdInvoke", this.cmdInvoke);
  }

  
  
  private static GralActionJztc processJzTc ( JZtxtcmd jzcmd, File fileJzTc, GuiCfg gui) {
    JZtxtcmdScript script = null;
    JZtxtcmdExecuter jzTcExec = null;
    GralActionJztc jzTcActions = null;;
    try {
      script = jzcmd.compile(fileJzTc, null);
    } catch( ScriptException exc) {
      System.err.println("jzTc=... Script error" + exc.getMessage());
    }
    if(script !=null) {
      try {
        jzTcExec = new JZtxtcmdExecuter(gui.gralMng.log()); // a yet non initialized executer instance
        jzTcActions = new GralActionJztc(jzTcExec, script, gui.gralMng.log(), gui);
        for(Map.Entry<String, JZtxtcmdScript.Subroutine> esub : script.iterSubroutines()) {
          String name = esub.getKey();
          if(name.startsWith("action")) {
            JZtxtcmdScript.Subroutine sub = esub.getValue();
            jzTcActions.add(sub);
          }
        }
      } catch( Exception exc) {
        CharSequence msg = ExcUtil.exceptionInfo("Exception while evaluating the jzTc=...", exc, 0, 20);
        System.err.println(msg);
      }
    }
    return jzTcActions;                // to set to the this.jzTcActions, null or set
  }

  
  
  /**Build the graphic with given textual configuration data.
   * @param cargs arguments
   * @param guiCfgData destination for the config data
   * @param panel panel for widgets.
   * @return final instance of GralCfgBuilder able to use for edit with {@link GralCfgDesigner}.
   */
  private static GralCfgBuilder buildGuiCfg ( GuiCallingArgs cargs, GralCfgData guiCfgData, GralPanelContent panel ) {
    final GralCfgBuilder guiCfgBuilder = new GralCfgBuilder(guiCfgData, panel.gralMng, cargs.fileGuiCfg.getParentFile());
    guiCfgBuilder.buildGui(guiCfgData, panel); // builds only the Gral instances without implementation graphic
    return guiCfgBuilder;
  }
  
  
  private void initJzTc () {
    try {
      Map<String, DataAccess.Variable<Object>> envVar = new TreeMap<String, DataAccess.Variable<Object>>();
      envVar.put("gralMng", new DataAccess.Variable<Object>('O', "gralMng", this.gralMng));
      this.jzTcActions.jzTcExec.initialize(this.jzTcActions.getScript(), false, null, envVar, null);
      //jzTcExec.executeScriptLevel(script, fileJzTc.getParent());
    } catch( Exception exc) {
      CharSequence msg = ExcUtil.exceptionInfo("Exception while evaluating the jzTc=...", exc, 0, 20);
      System.err.println(msg);
    }

  }
  
  
  
  public GralPlugUser_ifc getPluggedUser(){ return this.user; }



  /**Will be overridden... TODO InspcGui
   * 
   */
  protected void userInit()
  {
    if(this.user !=null){
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

protected void initMain() {
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
    if(this.tabPanel !=null) {                             // on first start, it is not yet prepared.
      this.guiCfgBuilder.buildGui(this.guiCfgData, this.tabPanel); // builds only the Gral instances without implementation graphic
    }
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
  if(this.user !=null){ try{ this.user.close(); } catch(IOException exc ){} }
}


/**This operation should be called in main(). It waits for closing graphic,
 * executes {@link #stepMain()} with 20 ms wait time,
 * and closes all necessities.
 * An unexpected exception is written to System.err. 
 */
public final void execute()
{
  try {
    this.initMain();
//    this.completeConstruction();
//    this.startupThreads();
    while(this.gralMng.isRunning()){
      try{ Thread.sleep(20);} 
      catch (InterruptedException e) { }
      try { this.stepMain();}
      catch (Exception exc) { 
        CharSequence sMsg = ExcUtil.exceptionInfo("Unexpected exception while stepMain: ", exc, 0, 20);
        System.err.println(sMsg);
      }
    }
    this.gralMng.closeApplication();
    if(this.inspector !=null) { 
      this.inspector.shutdown(); 
    }
    finishMain();
  } catch(Exception exc) {
    CharSequence sMsg = ExcUtil.exceptionInfo("Unexpected exception: ", exc, 0, 20);
    System.err.println(sMsg);
  } 

}



public void showInfoBox(CharSequence text) {
  this.outputBox.setFocus();
  if(text !=null) {
    this.outputBox.setText(text);
  }
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
        Writer writer = new FileWriter(GuiCfg.this.cargs.fileGuiCfg); //"save.cfg");
        sError = GuiCfg.this.gralMng.saveCfg(writer);
        writer.close();
      } catch(java.io.IOException exc){
        sError = "Problem open file ";
      }
      if(sError !=null){
        GuiCfg.this.console.writeError(sError);
      }
    }
    return true;
  }
};



/**The command-line-invocation (primary command-line-call). 
 * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
 */
public static void main(String[] cmdArgs) { 
  //
  int error = smain(cmdArgs);
  System.exit(error);
}


/**Possible to start from another program, not as main.
 * @param cmdArgs given arguments
 * @param cargs
 * @return
 */
public static int smain(String[] cmdArgs) {
  //
  GuiCallingArgs cargs = new GuiCallingArgs("GuiCfg configuarable GUI made by Hartmut Schorrig, 2010, " + GuiCfg.version);
  boolean bOk = cargs.parseArgs(cmdArgs, System.err);
  //Initializes the GUI till a output window to show informations:
//  CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
  
  if(bOk) {
    GuiCfg thiz = new GuiCfg(cargs, null, null, null);
    thiz.execute();
    return 0;
  } else {
    return 255;
  }
}


void stop(){} //debug helper

	
	
	
}
