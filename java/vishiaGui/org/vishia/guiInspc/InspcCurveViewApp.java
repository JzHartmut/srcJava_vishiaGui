package org.vishia.guiInspc;

import java.io.File;
import java.io.IOException;

import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.msgDispatch.MsgRedirectConsole;
import org.vishia.util.Arguments;
import org.vishia.util.FileFunctions;

public class InspcCurveViewApp
{
  
  InspcCurveView curveView;
  //GralWindow wind;
  GralMng gralMng;
  
  Args argData = new Args();
  
  public static void main(String[] args){
    InspcCurveViewApp main = new InspcCurveViewApp();
    File dir;
    for(String arg: args) { System.out.println(arg); }
    try {
      if(  false == main.argData.parseArgs(args, System.err)
        || false == main.argData.testArgs(System.err)
        ) { 
        System.exit(1); 
      }
      main.execute();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
      System.exit(1);
    }
      
  
  }
  
  
  private void execute(){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);     // Note: Creating a window outside is necessary because:
    GralWindow wind = gralFactory.createWindow(log, "Curve View", 'B', 100, 50, 800, 600);
    FileCluster fileCluster = FileRemote.clusterOfApplication;
    FileRemote fileCfg = fileCluster.getFile(this.argData.dirCfg.getAbsolutePath(), this.argData.fileCfg);
    FileRemote fileData = fileCluster.getFile(this.argData.dirData.getAbsolutePath(), this.argData.fileData);
    //                                                     // The InspcCurveView is a widget on any Window-Application
    this.curveView = new InspcCurveView("curves", null, wind.gralMng(), fileCfg, fileData, this.argData.dirHtmlHelp.getAbsolutePath(), null);
    this.curveView.windCurve = wind;
    this.gralMng = wind.gralMng();
    this.gralMng.gralDevice.addDispatchOrder(this.initGraphic);
    //initGraphic.awaitExecution(1, 0);
    while(this.gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  
  /**This class holds arguments.
   */
  public static class Args extends Arguments {

    public File dirCfg; String fileCfg;
    
    public File dirData; String fileData;
    
    public File dirHtmlHelp;
    
    boolean foundOptionArg = false;
    
    
    Args(){
      super.aboutInfo = "CurveViewAppl 2018-09-00 - 2021-12-19";
      super.helpInfo="obligate args: nothing";  //[-w[+|-|0]]
      addArg(new Argument("-cfg", ":path to config file/dir usage $(ENV) possible", this.setCfg));
      addArg(new Argument("-data", ":path to data file/dir usage $(ENV) possible", this.setData));
      addArg(new Argument("-help", ":path to help dir usage $(ENV) possible", this.setHtml));
      addArg(new Argument("", "without option marker: startpath common used usage $(ENV) possible", this.setDir));
    }
    
    Arguments.SetArgument setCfg = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      Args.this.foundOptionArg = true;
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
      Args.this.foundOptionArg = true;
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
      Args.this.foundOptionArg = true;
      Args.this.dirHtmlHelp = new File(FileFunctions.getCanonicalPath(new File(val).getAbsoluteFile()));
      return true;
    }};
    
    Arguments.SetArgument setDir = new Arguments.SetArgument(){ @Override public boolean setArgument(String val){ 
      if(Args.this.foundOptionArg) {
        return false;                  // option-less argument only admissible as alone one.
      }
      File dir = new File(val).getAbsoluteFile();
      if(dir.isDirectory()) { dir = dir.getParentFile(); }
      Args.this.dirCfg = Args.this.dirData = Args.this.dirHtmlHelp = dir;
      return true;
    }};
    

    
    
    /**Here empty no further test necessary
     *
     */
    @Override
    public boolean testArgs(Appendable msg) throws IOException {
      return true;
    }
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      curveView.buildGraphicInCurveWindow(null);
      //
  } };
  
  
}
