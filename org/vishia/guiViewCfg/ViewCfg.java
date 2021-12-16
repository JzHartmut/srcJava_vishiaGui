package org.vishia.guiViewCfg;

import java.io.File;


import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.base.GralWidget;
//import org.vishia.gral.gui.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.Assert;

/**Class contains main, it is able to use for a GUI without any programming in Java.*/
public class ViewCfg extends GuiCfg 
{
  
  /**Version and history
   * <ul>
   * <li>2012-20-22 Hartmut chg now works yet.
   * <li>2010-06-00 Hartmut created
   * </ul>
   */
  public static final int version = 0x20120222;
  
  private final OamShowValues oamShowValues;
	  
  /**Composition of a class, that reads the oam output values from the target
   * and writes into variables, which are displayed.
   */
  //private final OamOutFileReader oamOutValues;
  
  private final boolean showValuesOk;
  
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
    
  } //class CallingArguments
  
  
  
  final CallingArguments callingArguments;

  
  
  
  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  //GuiDialogZbnfControlled dialogZbnfConfigurator;   
  
  
  
  
  
  
  
  
  
  private final GralUserAction actionKeyboard = new GralUserAction()
  { @Override
  public boolean userActionGui(String sCmd, GralWidget widgetInfos, Object... values)
    {
  		if(sCmd != null){  
  			//String sCmd1 = "TouchInputPc.exe";
  			mainCmd.executeCmdLine(widgetInfos.sCmd, 0, null, null);
        return true;
  		} else return false;
    }
  };
  
  
  
  
  /**Organisation class for the GUI.
   */
  private static class CmdLineAndGui extends GralArea9MainCmd
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
      super(cargs, args);
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
  ViewCfg(CallingArguments cargs, GralArea9MainCmd cmdgui) 
  { super(cargs, cmdgui, null, null, null);
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
      System.out.println(Assert.exceptionInfo("ViewCfg - unexpected Exception; ", exc, 0, 7));
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
    //Initializes the graphic window and parse the parameter of args (command line parameter).
    //Parameter errors will be output in the graphic window in its given output area.
    bOk = cmdgui.parseArgumentsAndInitGraphic("ViewCfg", "3A3C");
    
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
