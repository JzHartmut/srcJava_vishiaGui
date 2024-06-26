package org.vishia.guiInspc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EventObject;

import org.vishia.byteData.ByteDataAccessSimple;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.communication.EvaluateValueDatagram;
import org.vishia.communication.InterProcessCommFactory;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.fileLocalAccessor.FileAccessorLocalJava7;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteAccessor;
import org.vishia.gral.base.GralGraphicOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.guiViewCfg.OamRcvValue;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Arguments;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;

/**This is an own application showing a Window with the {@link InspcCurveView}.
 * Inside the inspector it is also a window, but part of this application. 
 * This class shows graphics from csv, since 2024-02 also from Ethernet received data
 * with scaling and selection possibility. 
 * @author Hartmut Schorrig
 *
 */
public class InspcCurveViewApp
{
  
  /**Version, history and license. 
   * <ul>
   * <li>2024-03-02 Answer telegram if 50 telegrams are received (should be parameterizable? )
   *   Now ping-pong (requ/ackn) works with C program (Simulation). 
   * <li>2024-03-01 closes {@link FileAccessorLocalJava7} on finish, and also the receive thread.
   *   Elsewhere a process in windows is persistent which blocks the socket. 
   *   It was a serious error because it is hard to detect on run time. 
   * <li>2024-02-28 enhancement, now it can also receive data from Ethernet. 
   *   It is similar in usage as ViewCfg, difference: More simple for usage, as output for running C/++ programs for simulation.   
   * <li>2023-01-19 Some changes for file selection  
   * <li>2022-09-30 CurveViewAppl with new concept, all implGraphic removed,
   * <li>2021-12-19 now with command line arguments
   * <li>2013..2021 some adaptions only
   * <li>13-11-25 Hartmut creating as wrapper arround {@link InspcCurveView} as own application
   * </ul>
   * <br><br> 
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
   */
  //@SuppressWarnings("hiding")
  public final static String sVersion = "2024-03-02";

  
  InspcCurveView curveView;
  //GralWindow wind;
  GralMng gralMng;
  
  GralWindow windMain;
  
  /**It is a singleton.*/
  final protected InterProcessCommFactory ipcFactory;
  
  /**Reference to the used singleton fileAccessor, should be closed on end of main
   * 
   */
  final protected FileRemoteAccessor fileAccessor;
  
  final protected OamRcvValue oamRcv;
  
  final protected EvaluateValueDatagram evalValueDatagram;

  /**Counts incoming telegrams to transmit an short answer after n telg. */
  int ctTelg;
  
  
  byte[] txAnswer = new byte[32];
  
  ByteDataAccessSimple txDataAccess = new ByteDataAccessSimple(this.txAnswer, true);
  
  LogMessage log;
  
  protected final Args args;
  
  
  OamRcvValue.Plug plugReceive = new OamRcvValue.Plug() {

    @Override public boolean shouldSend () {
      return false; //ViewCfg.this.wdgbtnOnOff !=null && ViewCfg.this.wdgbtnOnOff.isOn();
    }

    @Override public void show ( byte[] binData, int nrofBytes, int from ) {
      showRcvValues(binData, nrofBytes, from);
    }
    
  };

  
  
  InspcCurveViewApp ( Args args) {
    this.args = args;
    this.fileAccessor = FileAccessorLocalJava7.getInstance();  // for file access, should be closed.
    
    this.gralMng = new GralMng();
    
    this.reportAllContentImpl = new GralGraphicOrder("reportAllContentImpl", this.gralMng) {
      @Override public int processEvent ( EventObject ev) {
        try {
          InspcCurveViewApp.this.curveView.windCurve.reportAllContentImpl(InspcCurveViewApp.this.log);
          InspcCurveViewApp.this.log.flush();
        } catch (Exception e) {
          System.out.append("unexpected Exception " + e.getMessage());
        }
        return 0;
      }
    };
    
    if(args.sIpOwn !=null) {
      this.ipcFactory = new InterProcessCommFactorySocket();
      this.oamRcv = new OamRcvValue(this.plugReceive, this.log, args.sIpOwn, null, 0);
      this.evalValueDatagram = new EvaluateValueDatagram();

    } else {
      this.ipcFactory = null;
      this.oamRcv = null;
      this.evalValueDatagram = null;
    }
  }
  
  
  public static void main(String[] cmdArgs){
    for(String arg: cmdArgs) { System.out.println(arg); }
    Args args = new Args();
    try {
      if(  false == args.parseArgs(cmdArgs, System.err)
        || false == args.testConsistence(System.err)
        ) { 
        System.exit(1); 
      }
      InspcCurveViewApp main = new InspcCurveViewApp(args);
      main.execute();
      main.fileAccessor.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.exit(0);
  
  }
  
  
  

  
  
  
  
  private void execute(){
    try {
      this.log = new LogMessageStream(System.out, this.args.fLog, null, false, null);     // Note: Creating a window outside is necessary because:
      //GralWindow wind = gralFactory.createWindow(log, "Curve View", 'B', 100, 50, 800, 600);
      FileRemote dirCfg = FileRemote.getFile(this.args.dirCfg.getAbsolutePath(), null);
      final FileRemote dirData, fileData;
      if(this.args.dirData ==null) {
        fileData = null;
        dirData = null;
      } else {
        dirData = this.args.dirData == null ? null: FileRemote.getFile(this.args.dirData.getAbsolutePath(), null);
        dirData.refreshPropertiesAndChildren(true, null);
        fileData = this.args.fileData == null ? null: dirData.getChild(this.args.fileData);
      }
      dirCfg.refreshPropertiesAndChildren(true, null);
      FileRemote fileCfg = dirCfg.getChild(this.args.fileCfg);
      // =================================================== // Create an empty Window
      // ========== The InspcCurveView is a Sub Window on any Window-Application
      // Or it is created as main Window if it is the first one.
      VariableContainer_ifc variables = null;              // has not any variables
      this.curveView = new InspcCurveView("curves", variables, null, null, this.args.sizeBuffer, this.gralMng, true
          , dirCfg, dirData, this.args.dirHtmlHelp.getAbsolutePath(), null);
      this.curveView.windCurve.reportAllContent(this.log);
      this.log.flush();
  
      //this.curveView.windCurve = wind;
      GralFactory gralFactory = new SwtFactory();
      gralFactory.createGraphic(this.gralMng, 'C');

      this.gralMng.addDispatchOrder(this.reportAllContentImpl);
      this.curveView.readCfg(fileCfg);
      if(fileData!=null) {
        this.curveView.readCurve(fileData);
      }
    
    } catch(Exception exc) {
      System.out.println(org.vishia.util.ExcUtil.exceptionInfo("unexpected", exc, 1, 10, true));
    } finally {
    }
    if(this.oamRcv !=null) {
      this.oamRcv.start();
    }
    //initGraphic.awaitExecution(1, 0);
    while(this.gralMng.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e) { 
      }
    }
    this.oamRcv.stopThread();

    if(this.args.fLog !=null) { try { this.args.fLog.close(); } catch (IOException e) { } }
      
  }
  
  
  protected void showRcvValues ( byte[] binData, int nrofBytes, int from) {
    float[] values = this.evalValueDatagram.readFloatFromDatagram(binData, nrofBytes, from);  
    int timeShort = this.evalValueDatagram.getTimeShort();
    int timeShortAdd = 0;
    boolean clearData = this.evalValueDatagram.clearData();
    this.ctTelg +=1;
    if(clearData) {
      this.curveView.widgCurve.cleanBuffer();
    } else if(values !=null) {
      this.curveView.widgCurve.setSample(values, timeShort, timeShortAdd);
    }
    if(this.ctTelg >= 50) {
      this.txDataAccess.setIntVal(0, 4, timeShort);
      this.oamRcv.sendAnswer(this.txAnswer, this.txAnswer.length, timeShort, 0xabcd);
      this.ctTelg = 0;
    }
  }
  
  
  final GralGraphicOrder reportAllContentImpl;

  
  
  
  /**This class holds arguments.
   */
  public static class Args extends Arguments {

    public File dirCfg; String fileCfg;
    
    FileOutputStream fLog;
    
    public File dirData; String fileData;
    
    public File dirHtmlHelp;
    
    
    protected String sIpOwn;
    
    
    int sizeBuffer = 100000;
    
    Args(){
      super.aboutInfo = "CurveViewAppl 2018-09-00 - 2024-02-18";
      super.helpInfo="obligate args: nothing";  //[-w[+|-|0]]
      addArg(new Argument("-cfg", ":path to config file/dir usage $(ENV) possible", this.setCfg));
      addArg(new Argument("-data", ":path to data file/dir usage $(ENV) possible", this.setData));
      addArg(new Argument("-ip", ":127.0.0.1:44785 OP and port for receive data", this.setIp));
      addArg(new Argument("-log", ":$(ENV)/path/to/logfile.txt  /tmp/path/to/logfile.txt", this.setLog));
      addArg(new Argument("-help", ":path to help dir usage $(ENV) possible", this.setHtml));
      addArg(new Argument("", "without option marker: startpath common used usage $(ENV) possible", this.setDir));
    }
    
    Arguments.SetArgument setCfg = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      File fileCfg = new File(val).getAbsoluteFile();
      if(fileCfg.isDirectory()) { 
        Args.this.dirCfg = fileCfg; Args.this.fileCfg = null; 
      } else {
        File dir = fileCfg.getParentFile();
        if(dir.exists()) {
          Args.this.dirCfg = dir; Args.this.fileCfg = fileCfg.getName(); 
        } else {
          Args.this.dirCfg = new File("/");
        }
      }
      return true;
    }};
    
    Arguments.SetArgument setData = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      File fileData = new File(val).getAbsoluteFile();
      if(fileData.isDirectory()) { 
        Args.this.dirData = fileData; Args.this.fileData = null; 
      } else {
        File dir = fileData.getParentFile();
        if(dir.exists()) {
          Args.this.dirData = dir; Args.this.fileData = fileData.getName(); 
        } else {
          Args.this.dirData = new File("/");
        }
      }
      return true;
    }};
    
    Arguments.SetArgument setHtml = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      Args.this.dirHtmlHelp = new File(FileFunctions.getCanonicalPath(new File(val).getAbsoluteFile()));
      return true;
    }};
    
    Arguments.SetArgument setDir = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      File dir = new File(val).getAbsoluteFile();
      if(dir.isDirectory()) { dir = dir.getParentFile(); }
      Args.this.dirCfg = Args.this.dirData = Args.this.dirHtmlHelp = dir;
      return true;
    }};
    

    Arguments.SetArgument setLog = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      File flog = new File(replaceEnv(val)).getAbsoluteFile();
      try {
        Args.this.fLog = new FileOutputStream(flog);
      } catch (FileNotFoundException e) {
        return false;
      }
      return true;
    }};
    

    Arguments.SetArgument setIp = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      Args.this.sIpOwn = "UDP:" + val;
      return true;
    }};
    

    
    
    /**Here empty no further test necessary
     *
     */
    @Override public boolean testConsistence(Appendable msg) throws IOException {
      return true;
    }
  }
  
  
  
  
}
