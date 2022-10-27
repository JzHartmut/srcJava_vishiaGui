package org.vishia.guiViewCfg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.ParseException;

import org.vishia.byteData.VariableAccessArray_ifc;
import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InterProcessComm;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.guiInspc.InspcCurveView;
import org.vishia.mainCmd.MainCmdLoggingStream;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.CheckVs;
import org.vishia.util.FileFunctions;
import org.vishia.util.TimedValues;

/**This class can be used as Operation and Monitoring for process values. 
 * The GUI design is controlled by a script, see {@link GralCfgZbnf}.
 * It means the GUI can be configured without any programming in Java.
 * Especially one {@link GralCurveView} is supported to show currently values.
 * The class contains a {@link InterProcessComm}, usual Socket Ethernet communication. 
 * The communication protocol, UDP, is adequate the {@link InspcDataExchangeAccess}.
 * The association between positions in the datagram and the GUI is also script controlled.
 * See {@link OamOutFileReader}
 * */
public class ViewCfg //extends GuiCfg 
{
  
  /**Version and history
   * <ul>
   * <li>2022-09-26 Hartmut more refactored. New concepts with docu.
   * <li>2022-08-26 Hartmut little bit refactored because of newly usage, prevent warnings.
   * <li>2012-20-22 Hartmut chg now works yet.
   * <li>2010-06-00 Hartmut created
   * </ul>
   */
  public static final int versionViewCfg = 0x20220926;
  
  private final OamShowValues oamShowValues;
	  
  /**Composition of a class, that reads the oam output values from the target
   * and writes into variables, which are displayed.
   */
  //private final OamOutFileReader oamOutValues;
  
  @SuppressWarnings("unused")
  private boolean showValuesOk;
  
  private final OamRcvValue oamRcvUdpValue;
  
  /**Composition of a class, that reads the oam messages from the target
   * and writes into the dayly list and into files.
   */
  //private final MsgReceiver msgReceiver;
  
  public final GralMng gralMng = new GralMng(null);
  
  public final GralWindow window;
  
  final InspcCurveView curveView;
  
  final GralButton wdgbtnOnOff; 
  
  public final LogMessage logCfg;
  
  public final GralTextBox outTextbox;
  
  public final MainCmdLogging_ifc logTextbox;
  
  
  /**The command-line-arguments are stored in an extra class, which can arranged in any other class too. 
   * The separation of command line argument helps to invoke the functionality with different calls, 
   * for example calling in a GUI, calling in a command-line-batch-process or JZtxtcmd. Hence the class and arguments are public.
   */
  public static class CallingArguments extends GuiCallingArgs
  {
    /**Name of the config-file for the Gui-appearance. */
    public final Argument sFileGui = new Argument("-gui", ":path/to/config.gui  The gui configuration file. Syntax see ...");
  	
    /**Directory where sFileCfg is placed, with / on end. The current dir if sFileCfg is given without path. */
    public String sParamBin;
    
    public String sFileCtrlValues;
    
    /**File with values to show alternatively to UDP input. */
    public final Argument sFileOamValues = new Argument("-oamFile", ":path/to/val.bin  File with values for oam");
    
    /**File to configure the Oam variables. */
    public final Argument sFileOamVariables = new Argument("-oamCfg", ":path/to/oam.cfg  file with oam variables");
    
    /**File to configure the Oam variables. */
    public final Argument argClassEvalRcvValues = new Argument("-oamEval", ":package.path.EvalClass  a class implenting");
    
    /**UDP:0.0.0.0:60000 IP and port to listen for incomming UDP telegrams.
     * If not given, do not listen. 
     * Values back are sent to the sender.
     */
    public final Argument targetIpc = new Argument("-targetIpc", ":UDP:192.168.1.77:41234 IP for commands to the target should be given if Ethernet is used.");
    
    public CallingArguments() {
      super();
      super.aboutInfo = "Configurable Gui, made by Hartmut Schorrig, 2010, 2022-09-23";
      super.helpInfo = "see https://www.vishia.org/gral/index.html";
      super.addArg(this.sFileOamValues);
      super.addArg(this.sFileOamVariables);
      super.addArg(this.targetIpc);
      super.addArg(this.sFileGui);
      super.addArg(this.argClassEvalRcvValues);
    }

    
  } //class CallingArguments
  
  
  
  final CallingArguments callingArguments;

  
  
  
  /**This instance helps to create the Dialog Widget as part of the whole window. It is used only in the constructor.
   * Therewith it may be defined stack-locally. But it is better to show and explain if it is access-able at class level. */
  //GuiDialogZbnfControlled dialogZbnfConfigurator;   
  
  
  
  
  
  
  
  
  
  
  
  
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
//	      else if(arg.startsWith("-oambin=")) 
//	      { this.cargs.sFileOamValues = getArgument(8);   //an example for default output
//	      }
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
  public ViewCfg(CallingArguments cargs) { 
    this.callingArguments = cargs;
    Appendable fLogFile = null;
    if(cargs.sFileLogCfg.val !=null) {
      try {
        File fileLogCfg = new File(cargs.sFileLogCfg.val);
        FileFunctions.mkDirPath(fileLogCfg);
        FileWriter flogCfg = new FileWriter(fileLogCfg);
        fLogFile = flogCfg;
      } catch (Exception exc) {
        System.err.println("not possible to write to: " + cargs.sFileLogCfg.val + " : " + exc.getMessage());
      }
    }
    this.logCfg = new LogMessageStream(System.out, null, fLogFile, true, null);
    this.gralMng.setLog(this.logCfg);;
    //super(cargs, cmdgui, null, null, null);

    
    if(cargs.fileGuiCfg !=null) {
//      gralMng.registerUserAction(null, this.action_dropFilePath);
//      gralMng.registerUserAction(null, this.action_exec);
//      gralMng.registerUserAction(null, this.action_abortCmd);
//      gralMng.registerUserAction(null, this.action_clearOutput);
//      gralMng.registerUserAction(null, this.action_readJZtc);
//        this.window = GralCfgWindow.createWindow("ViewCfg", " View cfg ", 'C', sCfg, null, null);
      
      try {
        File fCfg = /*new File*/(cargs.fileGuiCfg);
        this.window = GralCfgZbnf.configWithZbnf(fCfg, this.gralMng);         // does all, reads the config file, parses, creates Graphic Elements
      } catch (Exception e) {
        throw new IllegalArgumentException("Graphic confic error: " + e.getMessage());
      }                  
    } else {
      throw new IllegalArgumentException("argument -gui:path/to/gui.cfg is mandatory. ");
    }
    this.logCfg.flush();
    //=========================================  // now all Gui elements are presented.
    //                                           // The OamShowValues needs some of the elements maybe presented in gui.cfg:
    this.oamShowValues = new OamShowValues(this.logCfg, this.gralMng, cargs.argClassEvalRcvValues);
    //oamOutValues = new OamOutFileReader(cargs.sFileOamValues, cargs.sFileOamUcell, gui, oamShowValues);
    
    GralWidget btnCurveView = gralMng.getWidget("curveWindow");
    if(btnCurveView !=null && btnCurveView instanceof GralButton) {
      GralWidget gralwidgetCurveView = gralMng.getWidget("userCurves");
      final GralCurveView.CommonCurve commonCurve; 
      final TimedValues tracksValues;
      if(gralwidgetCurveView !=null) {
        GralCurveView widgetCurveView = (GralCurveView)gralwidgetCurveView;
        commonCurve = widgetCurveView.common;
        tracksValues = widgetCurveView.tracksValue;
      } else { commonCurve = null; tracksValues = null; }
      
      FileRemote dirCfg = FileRemote.fromFile(new File("T:/"));
      FileRemote dirSave = FileRemote.fromFile(new File("T:/"));
      this.curveView = new InspcCurveView("curveView", this.oamShowValues.accessOamVariable, commonCurve, tracksValues, this.gralMng, dirCfg, dirSave, ".", null);
      //((GralButton)btnCurveView).set
      btnCurveView.specifyActionChange(null, this.actionShowCurveWindow, null);
    } else {
      this.curveView = null;
    }
    GralWidget wdgbtnOnOff = gralMng.getWidget("btnOnOff");
    this.wdgbtnOnOff = wdgbtnOnOff instanceof GralButton ? (GralButton) wdgbtnOnOff : null;
    this.window.mainPanel.reportAllContent(this.logCfg);
    this.logCfg.flush();
    //
    //==================================================== // create implementation graphic
    if(cargs.graphicFactory ==null) {
      cargs.graphicFactory = new SwtFactory();         // default use SWT.
    }
    cargs.graphicFactory.createGraphic(this.gralMng, cargs.sizeShow);
    this.logCfg.flush();
    //
    GralTextBox msgOut = (GralTextBox)this.gralMng.getWidget("msgOut");
    this.outTextbox = msgOut;
    //
    //
    this.logTextbox = new MainCmdLoggingStream("mm-dd-hh:mm:ss", this.outTextbox);
    if(this.callingArguments.sOwnIpcAddr !=null) {
      this.oamRcvUdpValue = new OamRcvValue(this.oamShowValues, this.logTextbox, this.callingArguments, this.wdgbtnOnOff);
    } else { 
      this.oamRcvUdpValue = null;
    }
    
  
  
    //msgReceiver = new MsgReceiver(console, dlgAccess, cargs.sTimeZone);
    
  
    //msgReceiver.test(); //use it after initGuiDialog!
    
  }
  
  
  //@Override 
  protected void initViewCfg ( CallingArguments cargs ) throws IOException { 
      
    if(   this.callingArguments.sFileOamVariables.val !=null
      &&! this.oamShowValues.readVariableCfg(this.callingArguments)) {         // read cfg oam Variables
      this.logCfg.sendMsg(99, "error oamShowValues.readVariableCfg()");
      System.err.println("error oamShowValues.readVariableCfg()");
    }
    
    GralWidget curveView = this.gralMng.getWidget("userCurves");
    if(curveView !=null && curveView instanceof GralCurveView) {
      this.oamShowValues.setCurveView((GralCurveView)curveView);
    }
    this.oamShowValues.setFieldsToShow(this.gralMng.getShowFields(), this.logCfg);
    final VariableAccess_ifc varTimeShort = this.oamShowValues.accessOamVariable.getVariable("timeShort");
    if(varTimeShort !=null) {
      
    }
    
    if(this.oamRcvUdpValue !=null) {
      this.oamRcvUdpValue.start();
    }
  }
  
  

  void testChgVariable() {
    VariableAccess_ifc var1 = this.oamShowValues.getVariable("var1", null);
    float val1 = var1.getFloat();
    val1 +=0.01;
    if(val1 >1.0f) {val1 = -1.0f;}
    var1.setFloat(val1);
    this.oamShowValues.writeValuesOfTab();
  }
  
  

  //@Override 
  protected void stepMain()
  {
    try{
    	//oamOutValues.checkData();
      //msgReceiver.testAndReceive();
      //
      if(this.oamRcvUdpValue !=null) {
        this.oamRcvUdpValue.sendRequest();
      } else {
        testChgVariable();
      }
    } catch(Exception exc){
      //tread-Problem: console.writeError("unexpected Exception", exc);
      System.out.println(CheckVs.exceptionInfo("ViewCfg - unexpected Exception; ", exc, 0, 7));
      exc.printStackTrace();
    }

  }
  
  public void doSomethinginMainthreadTillClosePrimaryWindow()
  { while(gralMng.isRunning()){
      try{ Thread.sleep(20);} 
      catch (InterruptedException e) { }
      stepMain();
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
  public static int smain ( String[] cmdArgs) { 
    int error = 0;
    //old: CallingArguments cargs = new CallingArguments();
    CallingArguments cargs = new CallingArguments();           // Standard arguments for graphic application, here sufficient
    try {
      cargs.parseArgs(cmdArgs);
    } catch (Exception exc) {
      System.err.println("cmdline arg exception: " + exc.getMessage());
      error = 255;
    }
    
    //Initializes the GUI till a output window to show informations:
    //old: CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
    
    
    //Initializes the graphic window and parse the parameter of args (command line parameter).
    //Parameter errors will be output in the graphic window in its given output area.
    //old: bOk = cmdgui.parseArgumentsAndInitGraphic("ViewCfg", "3A3C");
    try {
      
      new InterProcessCommFactorySocket();
      //
      //======>>>>
      ViewCfg main = new ViewCfg(cargs);                   // reads all config files, creates Graphic
      main.initViewCfg(cargs);
      main.logCfg.close();                                 // close the configuration log, it is done.
      //
      //main.execute();
      main.doSomethinginMainthreadTillClosePrimaryWindow();
      if(main.oamRcvUdpValue !=null) {  
        main.oamRcvUdpValue.stopThread();
      }
      main.gralMng.closeGral();
    } catch(Exception exc) {
      System.err.println("Unexpected exception: " + exc.getMessage());
      exc.printStackTrace(System.err);
    }
    return 0;
  }

  public static void main(String[] cmdArgs){
    int errlev = smain(cmdArgs);
    System.exit(errlev);
  } 

  
  GralUserAction actionShowCurveWindow = new GralUserAction("showCurveWindow") {
    public boolean exec ( int actionCode, GralWidget_ifc widgd, Object... params ) {
      ViewCfg.this.curveView.showWindow(true);
      return true;
    }
  };  
  
}
