package org.vishia.guiInspc;

import java.io.File;

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

public class InspcCurveViewApp
{
  
  InspcCurveView curveView;
  //GralWindow wind;
  GralMng gralMng;
  
  public static void main(String[] args){
    InspcCurveViewApp main = new InspcCurveViewApp();
    File dir;
    if(args.length >=1) {
      dir = new File(args[0]);
      if(dir.exists() && !dir.isDirectory()) {
        dir = dir.getParentFile();
      } else if(!dir.exists()) {
        dir = new File("d:/");
      }
    } else {
      dir = new File("d:/");
    }
    main.execute(dir.getAbsolutePath());
  
  }
  
  private void execute(String sDir){
    GralFactory gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Curve View", 'B', 100, 50, 800, 600);
    FileCluster fileCluster = FileRemote.clusterOfApplication;
    FileRemote dirCfg = fileCluster.getDir(sDir);
    FileRemote dirSave = fileCluster.getDir(sDir);
    curveView = new InspcCurveView("curves", null, wind.gralMng(), dirCfg, dirSave, null);
    curveView.windCurve = wind;
    gralMng = wind.gralMng();
    gralMng.gralDevice.addDispatchOrder(initGraphic);
    //initGraphic.awaitExecution(1, 0);
    while(gralMng.gralDevice.isRunning()){
      try{ Thread.sleep(100);} 
      catch (InterruptedException e)
      { //dialogZbnfConfigurator.terminate();
      }
    }
      
  }
  
  
  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("GralArea9Window.initGraphic"){
    @Override public void executeOrder()
    {
      curveView.buildGraphicInCurveWindow(null);
      //
  } };
  
  
}
