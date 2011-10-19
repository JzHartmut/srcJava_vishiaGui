package org.vishia.guiBzr;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainAreaifc;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.gridPanel.GralGridProperties;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.FileSystem;

public class BzrGui extends GuiCfg
{
  /**Version, able to read as hex yyyymmdd.
   * Changes:
   * <ul>
   * <li>2011-05-17 new association panelOutput to the output panel for any outputs. It is used yet
   *     for the diff output. 
   * <li>2011-05-01 Hartmut: Created
   * </ul>
   */
  public final static int version = 0x20110617;

  /**The command-line-arguments may be stored in an extra class, which can arranged in any other class too. 
   * The separation of command line argument helps to invoke the functionality with different calls, 
   * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT 
   */
  static class CallingArguments extends GuiCallingArgs
  {
    /**This file contains all bzr commands and the pathes of the users sandboxes. */
    File fileCfg;
  } //class CallingArguments



  final CallingArguments cargs;

  final MainData mainData;


  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  //GuiDialogZbnfControlled dialogZbnfConfigurator;   



  /**Panel-Management-interface for the panels. */
  //GuiPanelMngBuildIfc panelBuildifc;









  //private GuiPanelMngBase panelMng;

  
  final GuiStatusPanel guiStatusPanel;

  final GuiCommitPanel guiCommitPanel;
  
  final PanelOutput panelOutput;
  
  final GuiFilesDiffPanel guiFilesDiffPanel;
  
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
  BzrGui(CallingArguments cargs, GuiMainCmd cmdgui) 
  { super(cargs, cmdgui, null);  //builds all graphic panels
    this.cargs = cargs;  //args in the correct derived type.
    boolean bOk = true;
    
    
    mainData = new MainData(cmdgui);
    String sError = mainData.cfg.readConfig(cargs.fileCfg);
    if(sError !=null){
      cmdgui.writeError(sError);
    }
    
    mainData.panelAccess = panelMng;
  
    mainData.guifc = this.gui;
    
    //creates the panel classes in constructor, don't initialize the GUI yet.
    guiStatusPanel = new GuiStatusPanel(mainData, panelBuildIfc);
    guiCommitPanel = new GuiCommitPanel(mainData, panelBuildIfc);
    guiFilesDiffPanel = new GuiFilesDiffPanel(mainData, panelBuildIfc);
    
    panelOutput = new PanelOutput(mainData, panelBuildIfc);
    
    mainData.mainAction = new MainAction(mainData, guiStatusPanel, guiCommitPanel, guiFilesDiffPanel, panelOutput);
    

    //gui.addDispatchListener(configGui);

    
    
  }


 
  //GuiDispatchCallbackWorker configGui = new GuiDispatchCallbackWorker()
  //{

    //@Override public void doBeforeDispatching(boolean onlyWakeup){
  /**Initializes the areas for the panels and configure the panels.
   * This routine can be overridden if other areas are need.
   */
  protected void initGuiAreas()
  {
    gui.setFrameAreaBorders(20, 80, 60, 85);
    gui.setStandardMenusGThread(new File("."), actionFile);

    
    //Creates a Tab-Panel:
    //panelMng.tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate, 0);
    //panelMng.tabPanel.addGridPanel("operation", "&Operation",1,1,10,10);
      
    gui.addFrameArea(1,1,3,2, mainTabPanel.getGuiComponent()); //dialogPanel);

    try { 
        mainTabPanel.addGridPanel("Select", "&Select",1,1,10,10);
        mainTabPanel.addGridPanel("Commit", "&Commit",1,1,10,10);
        mainTabPanel.addGridPanel("Log", "&Log",1,1,10,10);
        mainTabPanel.addGridPanel("FilesDiff", "&Files && Diff",1,1,10,10);
        mainTabPanel.addGridPanel("Output", "&Output",1,1,10,10);

        guiStatusPanel.initGui();
        guiCommitPanel.initGui();
        guiFilesDiffPanel.initGui();
        panelOutput.initGui();  
      }  
      catch(Exception exception)
      { //catch the last level of error. No error is reported direct on command line!
        mainCmd.writeError("Uncatched Exception on main level:", exception);
        mainCmd.writeStackTrace(exception);
        mainCmd.setExitErrorLevel(MainCmd_ifc.exitWithErrors);
      }
      //gui.removeDispatchListener(this);    

      //countExecution();

    }
    
  //};


  protected void stepMain()
  {
    Runnable order = mainData.awaitOrderBackground(1000);
    if(order !=null){
      order.run();
    }
  }


  
  void stop(){} //debug helper



  /**Organisation class for the GUI.
   */
  private static class CmdLineAndGui extends GuiMainCmd
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
      super(cargs, args, "Bazaar-Gui", "3A3C");
      this.cargs = cargs;
      addAboutInfo("Bazaar-Gui");
      addAboutInfo("made by HSchorrig, 2011-04-30, 2011-05-01");
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
        if(arg.startsWith("-cfg=")){      
          String sPath = FileSystem.absolutePath(getArgument(5), null);
          cargs.fileCfg = new File(sPath);
        }  
        else if(arg.startsWith("-_")) 
        { //accept but ignore it. Commented calling arguments.
        }
        else { bOk = super.testArgument(arg, nArg); }
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
    
    /**Initializes the Graphic. 
     * Note: This functionality should be a part of the implementing class, because some of the methods
     * are designated as protected. The initializing is done only one time.
     * Advantage of protected methods: don't call it without the correct context. 
     * This method is called from outside.
     */
    void initGraphic()
    {
      gui.setStandardMenusGThread(null, null);
    }

  } //class CmdLineAndGui

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
  
    BzrGui main = new BzrGui(cargs, gui);
  
    main.execute();
  
    gui.exit();
  }


}
