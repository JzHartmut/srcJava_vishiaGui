package org.vishia.guiInspc;

import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralGraphicTimeOrder;
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
    FileRemote dirCfg = fileCluster.getDir("D:/");
    FileRemote dirSave = fileCluster.getDir("D:/");
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
