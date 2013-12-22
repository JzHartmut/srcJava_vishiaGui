package org.vishia.guiInspc;

import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralFactory_ifc;
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
    main.execute();
  
  }
  
  private void execute(){
    GralFactory_ifc gralFactory = new SwtFactory();
    LogMessage log = new LogMessageStream(System.out);
    GralWindow wind = gralFactory.createWindow(log, "Curve View", 'C', 100, 50, 800, 600);
    FileCluster fileCluster = new FileCluster();
    FileRemote dirCfg = fileCluster.getDir("D:/SFC/SBOX/EDIT-XRPT/data/XRPT/ReflexAccess/curves");
    FileRemote dirSave = fileCluster.getDir("D:/SFC/Docu/Messungen/130417_KBtest");
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
  
  
  GralDispatchCallbackWorker initGraphic = new GralDispatchCallbackWorker("GralArea9Window.initGraphic"){
    @Override public void doBeforeDispatching(boolean onlyWakeup)
    {
      curveView.buildGraphicInCurveWindow();
  } };
  
  
}
