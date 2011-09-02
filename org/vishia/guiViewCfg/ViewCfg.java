package org.vishia.guiViewCfg;

import java.io.File;


import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.gral.gridPanel.GuiDialogZbnfControlled;
//import org.vishia.gral.gui.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.MainCmd_ifc;

/**Class contains main, it is able to use for a GUI without any programming in Java.*/
public class ViewCfg extends GuiCfg 
{
  
  private final OamShowValues oamShowValues;
	  
  /**Composition of a class, that reads the oam output values from the target
   * and writes into variables, which are displayed.
   */
  //private final OamOutFileReader oamOutValues;
  
  private boolean showValuesOk;
  
  private final OamRcvValue oamRcvUdpValue;
  
  /**Composition of a class, that reads the oam messages from the target
   * and writes into the dayly list and into files.
   */
  //private final MsgReceiver msgReceiver;
  
  
  /**The command-line-arguments may be stored in an extra class, which can arranged in any other class too. 
   * The separation of command line argument helps to invoke the functionality with different calls, 
   * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT 
   */
  static class CallingArguments extends GuiCallingArgs
  {
    /**Name of the config-file for the Gui-appearance. */
    String sFileGui;
  	
    /**Directory where sFileCfg is placed, with / on end. The current dir if sFileCfg is given without path. */
    String sParamBin;
    
    String sFileCtrlValues;
    
    /**File with the values from the S7 to show. */
    String sFileOamValues;
    
    /**File with the values from the S7 to show. */
    String sFileOamUcell;
    
    String sFileCfg;
    
    String sPathZbnf = "GUI";
    
    
    /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
    String sSize;
  } //class CallingArguments
  
  
  
  final CallingArguments callingArguments;

  
  
  
  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  GuiDialogZbnfControlled dialogZbnfConfigurator;   
  
  
  
  
  
  
  
  
  
  private final UserActionGui actionKeyboard = new UserActionGui()
  { public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
  		if(sCmd != null){  
  			//String sCmd1 = "TouchInputPc.exe";
  			mainCmd.executeCmdLine(widgetInfos.sCmd, 0, null, null);
  		}
    }
  };
  
  
  
  
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
      super(cargs, args, "ViewCfg");
      super.addAboutInfo("ViewCfg");
      super.addAboutInfo("made by HSchorrig, 2010-06-07, 2011-09-03");
      //super.addStandardHelpInfo();
      this.cargs = cargs;
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
        if(arg.startsWith("-parambin=")) 
	      { cargs.sParamBin = getArgument(10);   //an example for default output
	      }
	      else if(arg.startsWith("-ctrlbin=")) 
	      { cargs.sFileCtrlValues = getArgument(9);   //an example for default output
	      }
	      else if(arg.startsWith("-oambin=")) 
	      { cargs.sFileOamValues = getArgument(8);   //an example for default output
	      }
	      else { bOk = super.testArgument(arg, nArg); }
      } catch(Exception exc){
      }
      return bOk;
    }
  

    
  } //class CmdLineAndGui

  
  
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
  ViewCfg(CallingArguments cargs, GuiMainCmd cmdgui) 
  { super(cargs, cmdgui);
    this.callingArguments = cargs;
    
    oamShowValues = new OamShowValues(cmdgui, guiAccess);
    showValuesOk = oamShowValues.readVariableCfg();
    
    //oamOutValues = new OamOutFileReader(cargs.sFileOamValues, cargs.sFileOamUcell, gui, oamShowValues);
    
    oamRcvUdpValue = new OamRcvValue(oamShowValues, cmdgui);
    
    //msgReceiver = new MsgReceiver(console, dlgAccess, cargs.sTimeZone);
    
	  oamShowValues.setFieldsToShow(panelBuildIfc.getShowFields());

    //msgReceiver.test(); //use it after initGuiDialog!
    
  }
  
  
  @Override protected void initMain()
  {
    super.initMain();  //starts initializing of graphic. Do it after reading some configurations.
    //msgReceiver.start();
    oamRcvUdpValue.start();

  }
  

  @Override protected void stepMain()
  {
    try{
    	//oamOutValues.checkData();
      //msgReceiver.testAndReceive();
      oamRcvUdpValue.sendRequest();
    } catch(Exception exc){
      //tread-Problem: console.writeError("unexpected Exception", exc);
      System.out.println("unexpected Exception: " + exc.getMessage());
      exc.printStackTrace();
    }

  }
  
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] args)
  { boolean bOk = true;
    CallingArguments cargs = new CallingArguments();
    //Initializes the GUI till a output window to show informations:
    CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
    try{ cmdgui.parseArguments(); }
    catch(Exception exception)
    { cmdgui.writeError("Cmdline argument error:", exception);
      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      //gui.exit();
      bOk = false;  //not exiting, show error in GUI
    }
    
    if(bOk){
      //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
    	//try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
    	//}catch(ClassNotFoundException exc){
    	//	System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
    	//}
      //Loads the named class, and its base class InterProcessCommFactory. 
      //In that kind the calling of factory methods are regarded to socket.
    	new InterProcessCommFactorySocket();
    	
      ViewCfg main = new ViewCfg(cargs, cmdgui);
  
      main.execute();
      
      main.oamRcvUdpValue.stopThread();
    }    
    cmdgui.exit();
  }

  void stop(){} //debug helper

}
