package org.vishia.guiInspc;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Menu;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.guiViewCfg.OamRcvValue;
import org.vishia.guiViewCfg.OamShowValues;
import org.vishia.guiViewCfg.ViewCfg;
import org.vishia.inspector.Inspector;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.mainGui.GuiDialogZbnfControlled;
import org.vishia.mainGui.GuiDispatchCallbackWorker;
import org.vishia.mainGui.GuiPanelMngBase;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.TabPanel;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetCmpnifc;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.mainGui.cfg.GuiCfgBuilder;
import org.vishia.mainGui.cfg.GuiCfgData;
import org.vishia.mainGui.cfg.GuiCfgZbnf;
import org.vishia.mainGuiSwt.GuiPanelMngSwt;
import org.vishia.mainGuiSwt.MainCmdSwt;
import org.vishia.mainGuiSwt.PropertiesGuiSwt;
import org.vishia.msgDispatch.LogMessage;

public class InspcGuiCfg
{
  /**Composition of a Inspector-Target instance. This is only to visit this application for debugging.
   * Not necessary for functionality. */
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
    
    /**File with the values from the S7 to show. */
    String sFileOamValues;

    String sPathZbnf = "GUI";
    
    /**The time zone to present all time informations. */
    String sTimeZone = "GMT";
    
    /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
    String sSize;
    
    /**The own ipc-address for Interprocess-Communication with the target.
     * It is a string, which determines the kind of communication.
     * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
     */
    String sOwnIpcAddr;
    
    /**The target ipc-address for Interprocess-Communication with the target.
     * It is a string, which determines the kind of communication.
     * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
     */
    Map<String, String> indexTargetIpcAddr = new TreeMap<String, String>();
    
  } //class CallingArguments
  
  
  
  final CallingArguments callingArguments;

  
  File fileGui;
  
  final GuiCfgData guiCfgData = new GuiCfgData();
  
  final InspcGuiComm inspcComm;
  
  
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
      super.addAboutInfo("InspcGuiCfg");
      super.addAboutInfo("made by HSchorrig, 2011-05-18, 2011-05-19");
      //super.addStandardHelpInfo();
      this.cargs = cargs;
      super.setTitleAndSize("Inspc-GUI-cfg", 50,50,900, 600); //600);  //This instruction should be written first to output syntax errors.
      
      super.setOutputArea("B3C3");        //whole area from mid to bottom
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
        else if(arg.startsWith("-targetIpc=")) 
        { String sArg = getArgument(11);
          int posSep = sArg.indexOf('@');
          if(posSep < 0){
            writeError("argument -targetIpc=KEY@ADDR: The '@' is missed.");
            bOk = false;
          } else {
            String sKey = sArg.substring(0, posSep);
            String sValue = sArg.substring(posSep+1);
            cargs.indexTargetIpcAddr.put(sKey, sValue);
          }
        }
        else if(arg.startsWith("-ownIpc=")) 
        { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
        }
        else if(arg.startsWith("-oambin=")) 
        { cargs.sFileOamValues = getArgument(8);   //an example for default output
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
  
  
  private GuiPanelMngBase panelMng;
  
  /**Panel-Management-interface for the panels. */
  private GuiPanelMngBuildIfc panelBuildIfc;
  
  private GuiPanelMngWorkingIfc guiAccess;
  
  /**Code snippet for initializing the GUI area (panel). This snippet will be executed
   * in the GUI-Thread if the GUI is created. 
   */
  GuiDispatchCallbackWorker initGuiDialog = new GuiDispatchCallbackWorker()
  {
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      gui.setStandardMenusGThread(new File("."), actionFile);
      gui.setFrameAreaBorders(20, 80, 60, 85);

      
      //Creates a Tab-Panel:
      InspcGuiPanelContent panelContent = new InspcGuiPanelContent();
      inspcComm.addPanel(panelContent);
      //create a Tab panel, on activation of any tab the panelContent will be notified.
      TabPanel tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate);
      tabPanel.addGridPanel("operation", "&Operation",1,1,10,10);
      tabPanel.addGridPanel("panel2", "&Values",1,1,10,10);
      
      gui.addFrameArea(2,1,2,2, tabPanel.getGuiComponent()); //dialogPanel);
      //##
      WidgetCmpnifc treePanel = panelMng.createGridPanel(  
          panelMng.propertiesGui.colorBackground_
          , panelMng.propertiesGui.xPixelUnit(), panelMng.propertiesGui.yPixelUnit(), 5, 5);
      panelMng.registerPanel("tree", treePanel);
      gui.addFrameArea(1,1,1,3, treePanel); //dialogPanel);
      
      gui.removeDispatchListener(this);    
      countExecution();
    }
    
    
  };
  
  
  /**Code snippet to run the ZBNF-configurator (text controlled GUI)
   * 
   */
  GuiDispatchCallbackWorker configGuiWithZbnf = new GuiDispatchCallbackWorker(){
    
    @Override public void doBeforeDispatching(boolean onlyWakeup){
      /*
      char sizeArg = callingArguments.sSize == null ? 'A' : callingArguments.sSize.charAt(0);
      switch(sizeArg){
      case 'F': gui.setTitleAndSize("GUI", 0, 0, -1, -1); break;
      case 'A': gui.setTitleAndSize("GUI", 500, 100, 800, 600); break;
      case 'a': gui.setTitleAndSize("GUI", 50, 100, 512, 396); break;
      case 'b': gui.setTitleAndSize("GUI", 50, 100, 640, 480); break;
      case 'c': gui.setTitleAndSize("GUI", 50, 100, 800, 600); break;
      case 'D': gui.setTitleAndSize("GUI", 50, 100, 1024, 768); break;
      case 'E': gui.setTitleAndSize("GUI", 50, 100, 1200, 1050); break;
      default: gui.setTitleAndSize("GUI", 500, 100, -1, 800); break;
      }
      */
      gui.setTitleAndSize("GUI", 50, 100, 1200, 900);
      panelBuildIfc.buildCfg(guiCfgData, fileGui);
      
      gui.removeDispatchListener(this);    
      
      countExecution();
      
    }

  };
  
  
  
  /**ctor for the main class of the application. 
   * The main class can be created in some other kinds as done in static main too.
   * But it needs the {@link MainCmdWin}.
   * <br><br>
   * The ctor checks whether a gUI-configuration file is given. If not then the default configuratin is used.
   * It is especially for the Sample.
   * <br><br>
   * The the GUI will be completed with the content of the GUI-configuration file.  
   *   
   * @param cargs The given calling arguments.
   * @param gui The GUI-organization.
   */
  InspcGuiCfg(CallingArguments cargs, MainCmdSwt gui) 
  { this.gui = gui;
    boolean bOk = true;
    this.callingArguments = cargs;
    this.console = gui;  
    this.inspcComm = new InspcGuiComm(console, cargs.indexTargetIpcAddr);
    
    inspector = new Inspector("UDP:127.0.0.1:60088");
    inspector.start(this);
    
    //Creates a panel manager to work with grid units and symbolic access.
    //Its properties:  //##
    final char sizePixel;
    char sizeArg = callingArguments.sSize == null ? 'A' : callingArguments.sSize.charAt(0);
    switch(sizeArg){
    case 'F': sizePixel = 'D'; break;
    case 'A': sizePixel = 'D'; break;
    case 'a': sizePixel = 'A'; break;
    case 'b': sizePixel = 'B'; break;
    case 'c': sizePixel = 'C'; break;
    case 'D': sizePixel = 'D'; break;
    case 'E': sizePixel = 'E'; break;
    default: sizePixel = 'D'; break;
    }
    PropertiesGuiSwt propertiesGui = new PropertiesGuiSwt(gui.getDisplay(), sizeArg);
    LogMessage log = gui.getLogMessageOutputConsole();
    //Menu menuItem = super.addMenu("Edit", 'E');
    panelMng = new GuiPanelMngSwt(null, gui.getContentPane(), 120,80, propertiesGui, null, log);
    panelBuildIfc = panelMng;
    guiAccess = panelMng;
    
    
    //oamShowValues = new OamShowValues(gui, dlgAccess);
    //showValuesOk = oamShowValues.readVariableCfg();
    
    
    //oamRcvUdpValue = new OamRcvValue(oamShowValues, gui);
    
    
    //create the basic appearance of the GUI. The execution sets dlgAccess:
    gui.addDispatchListener(initGuiDialog);
    if(!initGuiDialog.awaitExecution(1, 10000)) throw new RuntimeException("unexpected fail of execution initGuiDialog");
    
    //fileHandlerUcell = new FileViewer(panelMng);
    
    
    
    /**Creates the dialog elements while reading a config-file. */
    //
    //Register any user action. This should be done before the GUI-configuration is read.
    //panelBuildIfc.registerUserAction("fileHandlerUcell", fileHandlerUcell.getAction());
    //dialogCellMng.registerTableAccess("msgOfDay", msgReceiver.msgOfDayAccessGui);
    //panelBuildIfc.registerTableAccess("msgOfDay", msgReceiver.msgOfDay;
    
    //dialogVellMng.re
    boolean bConfigDone = false;
    if(cargs.sFileGui != null){
      //configGuiWithZbnf.ctDone(0);  //counter for done initialized.
      fileGui = new File(callingArguments.sFileGui);
      if(fileGui.exists())
      {
        File fileSyntax = new File(cargs.sPathZbnf + "/dialog.zbnf");
        GuiCfgZbnf cfgZbnf = new GuiCfgZbnf(console, fileSyntax);
        
        String sError = cfgZbnf.configureWithZbnf(fileGui, guiCfgData);
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
        console.writeError("Config file not found: " + fileGui.getAbsolutePath());
      }
    }    
    //assigns the fields which are visible to the oamOutValues-Manager to fill it with the values.

    //msgReceiver.test(); //use it after initGuiDialog!
    
  }
  
  void execute()
  {
    inspcComm.openComm(callingArguments.sOwnIpcAddr);
    //msgReceiver.start();
    //oamRcvUdpValue.start();
    while(gui.isRunning())
    { try{
        inspcComm.procComm();  
      //oamRcvUdpValue.sendRequest();
      } catch(Exception exc){
        //tread-Problem: console.writeError("unexpected Exception", exc);
        System.out.println("unexpected Exception: " + exc.getMessage());
        exc.printStackTrace();
      }
      try{ Thread.sleep(50);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }

  }
  
  
  UserActionGui actionFile = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    {
      if(sIntension.equals("save")){
        String sError = null;
        try{
          Writer writer = new FileWriter("save.cfg");
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
    CallingArguments cargs = new CallingArguments();
    //Initializes the GUI till a output window to show informations:
    CmdLineAndGui gui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
    try{ gui.parseArguments(); }
    catch(Exception exception)
    { System.out.append("Cmdline argument error:" +  exception.getMessage() + "\n");
      //gui.writeError("Cmdline argument error:", exception);
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
    
    InspcGuiCfg main = new InspcGuiCfg(cargs, gui);

    main.execute();
    
    //main.oamRcvUdpValue.stopThread();
    
    gui.exit();
  }

  void stop(){} //debug helper




}
