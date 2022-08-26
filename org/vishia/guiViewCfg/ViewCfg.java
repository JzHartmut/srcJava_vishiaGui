package org.vishia.guiViewCfg;

import java.io.File;


import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.base.GralWidget;
//import org.vishia.gral.gui.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.CheckVs;

/**Class contains main, it is able to use for a GUI without any programming in Java.*/
public class ViewCfg extends GuiCfg 
{
  
  /**Version and history
   * <ul>
   * <li>2022-08-26 Hartmut little bit refactored because of newly usage, prevent warnings.
   * <li>2012-20-22 Hartmut chg now works yet.
   * <li>2010-06-00 Hartmut created
   * </ul>
   */
  public static final int versionViewCfg = 0x20120222;
  
  private final OamShowValues oamShowValues;
	  
  /**Composition of a class, that reads the oam output values from the target
   * and writes into variables, which are displayed.
   */
  //private final OamOutFileReader oamOutValues;
  
  @SuppressWarnings("unused")
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
	      { this.cargs.sParamBin = getArgument(10);   //an example for default output
	      }
	      else if(arg.startsWith("-ctrlbin=")) 
	      { this.cargs.sFileCtrlValues = getArgument(9);   //an example for default output
	      }
	      else if(arg.startsWith("-oambin=")) 
	      { this.cargs.sFileOamValues = getArgument(8);   //an example for default output
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
    
    this.oamShowValues = new OamShowValues(cmdgui, this.guiAccess);
    this.showValuesOk = this.oamShowValues.readVariableCfg();
    
    //oamOutValues = new OamOutFileReader(cargs.sFileOamValues, cargs.sFileOamUcell, gui, oamShowValues);
    
    this.oamRcvUdpValue = new OamRcvValue(this.oamShowValues, cmdgui);
    
    //msgReceiver = new MsgReceiver(console, dlgAccess, cargs.sTimeZone);
    
	  this.oamShowValues.setFieldsToShow(this.panelBuildIfc.getShowFields());

    //msgReceiver.test(); //use it after initGuiDialog!
    
  }
  
  
  @Override protected void initMain()
  {
    super.initMain();  //starts initializing of graphic. Do it after reading some configurations.
    //msgReceiver.start();
    this.oamRcvUdpValue.start();

  }
  

  @Override protected void stepMain()
  {
    try{
    	//oamOutValues.checkData();
      //msgReceiver.testAndReceive();
      this.oamRcvUdpValue.sendRequest();
    } catch(Exception exc){
      //tread-Problem: console.writeError("unexpected Exception", exc);
      System.out.println(CheckVs.exceptionInfo("ViewCfg - unexpected Exception; ", exc, 0, 7));
      exc.printStackTrace();
    }

  }
  
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   * <pre>
-gui=guiCfg/gui.cfg
-size=B
-timeZone=GMT
--report:T:\tmp\GUI.log
--rlevel:334
   * </pre>
   * possible in a file called with argument <code>--@guiCfg/gui.args</code>
   * The gui.cfg contains widgets to show, especially a curveView, for example: <pre>
size(500,120);
//@ 2,1:Text(GUI SES-Parameter);
@msg, 16-16,0+90:Table(uMin) : size(14+6+2+70 x 16), name="msgOfDay";

//===================opeation-curves: ===============================
@operation, 30-30,0+50: Curveview(userCurves, 30000):
  line(xway, color=006000, nullLine=50, offset = 0.0, scale=5000.0, var=xway),
  line(wway, color=600000, nullLine=50, scale=5000.0, var=wway),
  line(target, color=00ff00, nullLine=50, scale=5000.0, var=target),
  line(dway, color=red, nullLine=50, offset = 0.0, scale=1000.0, var=dway),
  line(output, color=blue, nullLine=50, scale=5000.0, var=output)
;
//explanations on diagram:
@6.5-1.3++, 50+3: Text("2.0 m", dgn);//dgn is dark green
@12.5, 50+3: Text("1.5");
@18.5, 50+3: Text("1.0", dgn);
@24.5, 50+3: Text("0.5", dgn);
   * </pre>
   * The Application expects a UDP datagram with data on port 60082 on localhost, to test (TODO use parameter).
   * see {@link OamRcvValue#ownAddr}
   * The diagram should have a payload with:
   * <ul>
   * <li>0x00: Head of inspector datagram, not evaluated, but space is used
   * <li>0x10...0x17 Head of a Inspector datagram item, length should be set only (bytes @0x18..19)
   * <li>0x18 a long timestamp milliseconds after 1970
   * <li> ... some more telegram data 
   * </ul>
   * The position of the data in the UDP should be defined with a file on <code>GUI/oamVar.cfg</code>
   * with the following content (example proper to CurveView):
<pre>
==OamVariables==
time_milliseconds1970: J @0;
xway: S @8;  
wway: S @12;
target: S @14;
dway: S @16;
output: S @18;
stateSetValueGen: S @20;
ctController: B @22;
ctSetValue: B @23;
</pre>
The positions are related to the start of the Inspector item @ 0x18, first with the timestamp. 
   * Use the directory inside the cmpnJava_vishiaGui: src/appl/ViewCfg/GUI
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


}
