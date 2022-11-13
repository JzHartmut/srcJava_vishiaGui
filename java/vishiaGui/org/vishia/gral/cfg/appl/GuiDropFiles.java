package org.vishia.gral.cfg.appl;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.vishia.cmd.CmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.cfg.GralCfgWindow;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.mainCmd.MainCmdLoggingStream;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.FileSystem;


/**This is a common GUI application which can be controlled by scripts
 * to receive files pre drag and drop and execute something with the file paths
 * via a JTtxtcmd script subroutine.
 * It means, either special Java operations can be invoked, depending on the JZtxtcmd script,
 * or command line processes can be invoked in the script.
 * 
 * <br><br>
 * This application needs a GUI configuration file, see ...TODO.
 * Usual the config file contains some text fields with the capability of receive droped files
 * respectively capability to select files. Furthermore it should contains and exec button:<pre>
@primaryWindow, 3-2,2+5: Text(helloLabel, "Drag & drop test");
@6-2,2+50: InputFile(file1, dropFiles="dropFilePath");
@10-2,2+50: InputFile(file2, dropFiles="dropFilePath");

@14-3,15+12: Button(TestButton, "execute", action=exec);

@15+20, 0+80: OutputBox(msgOut);  //need to have the name msgOut!
 * </pre>
 * The Script should contain one subroutine <pre>
 * sub exec(String file1, String file2) {
 * } 
 * </pre>
 * The arguments should be the same as the name of the text fields in the GUI-cfg-scripts.
 * Their content are delivered as value.
 * What this subroutine does, is free.
 * 
 *   
 * @author Hartmut Schorrig, LPGL-license, 
 * @since 2020-03-14
 *
 */
public class GuiDropFiles {

  private static class Args extends GuiCallingArgs {

    File fjzTc;
    
    @Override final protected boolean testArgument(String arg, int nArg) {
      boolean bOk = true;  //set to false if the argc is not passed
      String value;
      if( (value = checkArgVal("-jzTc", arg)) !=null) {       
        this.fjzTc = new File(value);  //the graphic GUI-appearance
      }
      else 
      { super.testArgument(arg, nArg);
      }
      return bOk;
    }
  }
  
  
  /**The command line arguments */
  final Args args;
  
  
  /**The only one window */
  private GralWindow window;
  
  private GralMng gralMng;
  
  
  //private JZtxtcmdExecuter jzTcExec;
  
  /**The command queue to execute */
  final CmdExecuter cmdExecuter;

  
  /**The translated script. */
  private JZtxtcmdScript jzTcScript;
  
  //private JZtxtcmdScript.Subroutine subExec;
  final Map<String, JZtxtcmdScript.Subroutine> jzSubroutines = new TreeMap<String, JZtxtcmdScript.Subroutine>();
  
  private File fcurrdir;
  
  GralTextBox outTextbox;
  
  MainCmdLogging_ifc logTextbox;
  
  /**Temporary used output text */
  StringBuilder outText = new StringBuilder();
  
  Map<String, String> systemButtons = new TreeMap<String, String>();
    
  
  static String script = //possible to give the script as loaded text from a text file!
      "@primaryWindow, 3-2,2+5: Text(helloLabel, \"Hello World\"); \n"
    + "@7-3,10+12: Button(TestButton, \"press me\", action=actionTestButton); \n";

  
  public static void main(String[] cmdArgs){
    int errlev = smain(cmdArgs);
    System.exit(errlev);
  } 
  
  
  
  public GuiDropFiles(Args args) {
    super();
    this.args = args;
    this.fcurrdir = new File(".");
    this.cmdExecuter = new CmdExecuter();
    this.systemButtons.put("abortCmd","abortCmd");
    this.systemButtons.put("clearOutput","clearOutput");
    this.systemButtons.put("readJZtc","readJZtc");
  }

  
  
  public static int smain(String[] cmdArgs){
    final Args args = new Args();
    try {
      args.parseArgs(cmdArgs);
    } catch (Exception exc) {
      System.err.println("cmdline arg exception: " + exc.getMessage());
      return 255;
    }
    GuiDropFiles thiz = new GuiDropFiles(args);
    try{ 
      thiz.createGraphic();
    } catch(Exception exc) {
      System.err.println("cannot create window because error in config file: " + exc.getMessage());
    }
    thiz.backProcess();
    return 0;
  }

  
  
  private void createGraphic() throws ParseException {
    String sCfg = script;
    if(this.args.fileGuiCfg !=null) {
      sCfg = FileSystem.readFile(this.args.fileGuiCfg);
    }
    this.gralMng = new GralMng(new LogMessageStream(System.out));
    this.gralMng.registerUserAction(null, this.action_dropFilePath);
    this.gralMng.registerUserAction(null, this.action_exec);
    this.gralMng.registerUserAction(null, this.action_abortCmd);
    this.gralMng.registerUserAction(null, this.action_clearOutput);
    this.gralMng.registerUserAction(null, this.action_readJZtc);
    this.window = GralCfgZbnf.configWithZbnf(sCfg, this.gralMng);
    //this.window = GralCfgWindow.createWindow("Guidropfiles", "Gui drop files and execute via JzTxtCmd", 'C', sCfg, null, null);
    GralTextBox msgOut = (GralTextBox)this.gralMng.getWidget("msgOut");
    this.outTextbox = msgOut;
    this.logTextbox = new MainCmdLoggingStream("mm-dd-hh:mm:ss", this.outTextbox);
    this.gralMng.createGraphic("SWT", 'C', this.gralMng.log);
    readJZtcScript();
    
  }
  
  
  
  private void readJZtcScript() {
    try{
      this.jzTcScript = JZtxtcmd.translateAndSetGenCtrl(this.args.fjzTc, this.logTextbox);
    } catch(ScriptException exc) {
      logTextbox(exc.getMessage());
    }
    //assign button to subroutines
    for(GralWidget wdg: gralMng.getWidgetIter()) {
      if(wdg instanceof GralButton) {
        String name = wdg.getName();
        if(this.systemButtons.get(name) == null && !name.endsWith("<")) {
          JZtxtcmdScript.Subroutine jzSub = this.jzTcScript.getSubroutine(name);
          if(jzSub ==null) {
            logTextbox("subroutine to button not found: " + name + "\n");
          } else {
            this.jzSubroutines.put(name, jzSub);
          }
        }
      }
    }
    logTextbox("jzTc-Script read: " + this.args.fjzTc.getAbsolutePath());
    try {
      this.cmdExecuter.initJZcmdExecuter(this.jzTcScript, this.fcurrdir.getAbsolutePath(), this.logTextbox);
    } catch (Throwable e) {
      logTextbox("error initializing cmdExecuter with script:\n");
      logTextbox(e.getMessage());
    }
    
  }
  
  
  
  private void backProcess() {
    while(this.window !=null && !this.window.isGraphicDisposed()) {
      try {
        Thread.sleep(100);
        this.cmdExecuter.executeCmdQueue(false);
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

  }
  
  
  private Appendable logTextbox(CharSequence msg) {
    try {
      this.outTextbox.append(msg);
    } catch (IOException e) {
      System.err.println("unexected: cannot append to msgOut");
    }
    return this.outTextbox;
  }
  
  
  
  void exec(String name) {
    JZtxtcmdScript.Subroutine jzSub = this.jzSubroutines.get(name);
    if(jzSub == null) {
      return;  //do nothing, error message is shown already.
    }
    List<DataAccess.Variable<Object>> args = new LinkedList<DataAccess.Variable<Object>>();
    if(jzSub.formalArgs !=null) {
      for(JZtxtcmdScript.DefVariable arg :jzSub.formalArgs){
        String name1 = arg.getVariableIdent();
        GralWidget wdg = this.gralMng.getWidget(name1);
        String val = "??";
        if(wdg ==null) {
          this.logTextbox("error arg "+ name1 + "not found as textfield");
        } else {
          val = wdg.getText();
        }
        DataAccess.Variable<Object> var = new DataAccess.Variable<Object>('S', name1, val, true);
        args.add(var);
      }
    }
    try {
      this.outText.setLength(0);
      this.cmdExecuter.addCmd(jzSub, args, outText, this.fcurrdir, this.execFinish);
    } catch (Exception e) {
      this.logTextbox("error: "); logTextbox(e.getMessage()); 
    }
    
  }
  
  
  
  GralUserAction action_dropFilePath = new GralUserAction("dropFilePath") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      return true;
    }
  };
  
  GralUserAction action_exec = new GralUserAction("exec") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      String name = widgd.getName();
      GuiDropFiles.this.exec(name);
      return true;
    }
  };
  

  GralUserAction action_abortCmd = new GralUserAction("abortCmd") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      GuiDropFiles.this.cmdExecuter.abortAllCmds();
      return true;
    }
  };
  

  GralUserAction action_readJZtc = new GralUserAction("readJZtc") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      readJZtcScript();
      return true;
    }
  };
  

  
  GralUserAction action_clearOutput = new GralUserAction("clearOutput") {
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      GuiDropFiles.this.outTextbox.setText("");
      return true;
    }
  };
  

  
  CmdExecuter.ExecuteAfterFinish execFinish = new CmdExecuter.ExecuteAfterFinish() {

    @Override
    public void exec(int errorcode, Appendable out, Appendable err) {
      GuiDropFiles.this.logTextbox(outText);
    }
    
  };
  
  
}
