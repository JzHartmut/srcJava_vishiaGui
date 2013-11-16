package org.vishia.guiInspc;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.inspectorAccessor.InspcMng;
import org.vishia.inspectorAccessor.InspcPlugUser_ifc;
import org.vishia.inspectorAccessor.UserInspcPlug_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageFile;
import org.vishia.util.CompleteConstructionAndStart;
import org.vishia.util.KeyCode;

public class InspcGui implements CompleteConstructionAndStart //extends GuiCfg
{

  /**Version, history and license
   * <ul>
   * <li>2012-08-10 Hartmut A default directory for curve config files given with argument "-dirCurves=".
   * <li>2012-04-17 Hartmut new: Parameter {@link CallingArguments#bUseGetValueByIndex} for downward compatibility
   * 
   * <li>2011-04-20 Don't derive this class from {@link GuiCfg}, instead uses the inner class {@link InspcGuiCfg}.
   *   It is a problem of order of instantiation.
   * <li>2011-04-00 Hartmut creation.
   * </ul>
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
  @SuppressWarnings("hiding")
  public final static int version = 20120417;
  
  private final List<CompleteConstructionAndStart> composites = new LinkedList<CompleteConstructionAndStart>();
  
  /**The communication manager. */
  //final InspcGuiComm XXXinspcComm;
  
  private final CallingArguments cargs;
  
  GralPanelActivated_ifc panelActivated = new GralPanelActivated_ifc(){

    public void panelActivatedGui(Queue widgets)
    { panelActivated(widgets); 
    }
    /*
    @Override public void panelActivatedGui(Queue<GralWidget> widgets)
    { panelActivated(widgets); 
    }
    */
  };

  final GuiCfg guiCfg;
  
  final InspcMng inspcMng;
  
  final private Runnable callbackOnReceivedData = new Runnable(){ @Override public void run(){ callbackOnReceivedData(); } };
  
  
  LogMessage logTelg;

  GralButton btnSwitchOnLog;
  
  InspcCurveView curveA, curveB, curveC;
  
  public GralColorSelector colorSelector;
  
  private final FileCluster fileCluster = new FileCluster();

  InspcGui(CallingArguments cargs, GralArea9MainCmd cmdgui)
  {
    guiCfg = new InspcGuiCfg(cargs, cmdgui, userInspcPlug);
    

    LogMessage log = cmdgui.getLogMessageOutputConsole();
    this.cargs = cargs;  //args in the correct derived type.
    /**
    assert(user instanceof InspcPlugUser_ifc);
    if(user !=null){
      user.init(userInspcPlug, log);
    }
    */
    GralPlugUser_ifc user = guiCfg.getPluggedUser(); 
    assert(user == null || user instanceof InspcPlugUser_ifc);
    
    InspcMng variableMng = new InspcMng(cargs.sOwnIpcAddr, cargs.indexTargetIpcAddr, cargs.bUseGetValueByIndex, (InspcPlugUser_ifc)user);
    composites.add(variableMng);
    this.inspcMng = variableMng;
    variableMng.setCallbackOnReceivedData(callbackOnReceivedData);
    
    //this.XXXinspcComm = new InspcGuiComm(this, guiCfg.gralMng, cargs.indexTargetIpcAddr, (InspcPlugUser_ifc)user);
    //composites.add(XXXinspcComm);
    
    FileRemote defaultDirCfg = fileCluster.getFile(cargs.sDefaultDirCfgForCurves, null);
    FileRemote defaultDirSave = fileCluster.getFile(cargs.sDefaultDirSaveForCurves, null);
    
    curveA = new InspcCurveView("curve_A", variableMng, cmdgui.gralMng, defaultDirCfg, defaultDirSave, cargs.curveExporterClasses);
    curveB = new InspcCurveView("curve_B", variableMng, cmdgui.gralMng, defaultDirCfg, defaultDirSave, cargs.curveExporterClasses);
    curveC = new InspcCurveView("curve_C", variableMng, cmdgui.gralMng, defaultDirCfg, defaultDirSave, cargs.curveExporterClasses);

  }
  
  @Override public void completeConstruction(){
    for(CompleteConstructionAndStart composite: composites){
      composite.completeConstruction();
    }
  }
  
  @Override public void startupThreads(){
    for(CompleteConstructionAndStart composite: composites){
      composite.startupThreads();
    }
  }

  
  
  void panelActivated(Queue<GralWidget> widgets){
    for(GralWidget widget: widgets){
      
    }
  }
  
  

  
  private void callbackOnReceivedData(){
    long time = System.currentTimeMillis();
    ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listPanels = guiCfg.gralMng.getVisiblePanels();
    //GralWidget widgdRemove = null;
    try{
      for(GralVisibleWidgets_ifc panel: listPanels){
        Queue<GralWidget> widgetsVisible = panel.getWidgetsVisible();
        if(widgetsVisible !=null) for(GralWidget widget: widgetsVisible){
          try{
            String sShowMethod;
            if((sShowMethod = widget.getShowMethod()) ==null || !sShowMethod.equals("stc_cmd")){
              widget.refreshFromVariable(inspcMng);
              widget.requestNewValueForVariable(time);
            }
          }catch(Exception exc){
            System.err.println("InspcGui-receivedData-widget; " + exc.getMessage());   
            exc.printStackTrace(System.err);
          }
        }
      }
      //referesh the curve view any time if it is enabled:
      curveA.refreshCurve();
      curveB.refreshCurve();
      curveC.refreshCurve();
    } catch(Exception exc){ 
      System.err.println("InspcGui-receivedData; " + exc.getMessage()); 
      exc.printStackTrace(System.out);
    }
    
  }
  
  
  static class CallingArguments extends GuiCallingArgs
  {
    /**The target ipc-address for Interprocess-Communication with the target.
     * It is a string, which determines the kind of communication.
     * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
     */
    Map<String, String> indexTargetIpcAddr = new TreeMap<String, String>();

    /**Cohesion between file extension and exporter java class path for curve output.*/
    Map<String, String> curveExporterClasses = new TreeMap<String, String>();

    /**File with the values from the S7 to show. */
    //protected String sFileOamValues;

    boolean bUseGetValueByIndex;
    
    String sDefaultDirCfgForCurves = "C:/";

    String sDefaultDirSaveForCurves = "C:/";

  }
  
  /**Organisation class for the GUI.
   */
  private static class CmdLineAndGui extends GralArea9MainCmd
  {

    /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
     * It is given as ctor-parameter.
     */
    final protected CallingArguments cargs;
    
    
    public CmdLineAndGui(CallingArguments cargs, String[] args)
    {
      super(cargs, args);
      this.cargs = cargs;
      super.addAboutInfo("Inspc-GUI-cfg");
      super.addAboutInfo("made by HSchorrig, 2011-05-18, 2012-01-17");
    }

    @Override protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;  //set to false if the argc is not passed
      try{
        if(arg.startsWith("-targetIpc=")) 
        { String sArg = getArgument(11);
          int posSep = sArg.indexOf('@');
          if(posSep < 0){
            writeError("argument -targetIpc=KEY@ADDR: The '@' is missed.");
            bOk = false;
          } else {
            String sKey = sArg.substring(0, posSep);
            String sValue = sArg.substring(posSep+1);
            cargs.indexTargetIpcAddr.put(sKey, sValue);
          }
        }
        else if(arg.startsWith("-curve-export=")) 
        { String sArg = getArgument(14);
          int posSep = sArg.indexOf('=');
          if(posSep < 0){
            writeError("argument -curve-export=EXT=java.class.path");
            bOk = false;
          } else {
            String sKey = sArg.substring(0, posSep);
            String sValue = sArg.substring(posSep+1);
            cargs.curveExporterClasses.put(sKey, sValue);
          }
        }
        else if(arg.startsWith("-ownIpc=")) 
        { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
        }
        else if(arg.startsWith("-dirCurves=")) 
        { cargs.sDefaultDirCfgForCurves = getArgument(11);   //an example for default output
        }
        else if(arg.startsWith("-dirCurveCfg=")) 
        { cargs.sDefaultDirCfgForCurves = getArgument(13);   //an example for default output
        }
        else if(arg.startsWith("-dirCurveSave=")) 
        { cargs.sDefaultDirSaveForCurves = getArgument(14);   //an example for default output
        }
        else { bOk = super.testArgument(arg, nArg); }
      } catch(Exception exc){ bOk = false; }
      return bOk;
    }

  
  } //class CmdLineAndGui 
  

/**Overrides the GuiCfg with special initialization and methods.
 * @author Hartmut Schorrig
 *
 */
private class InspcGuiCfg extends GuiCfg
{
  
  InspcGuiCfg(CallingArguments cargs, GralArea9MainCmd cmdgui, GralPlugUser2Gral_ifc plugUser2Gui)
  { super(cargs, cmdgui, null, plugUser2Gui); 
  }
  
  
  /**Initializes the areas for the panels and configure the panels.
   * This routine overrides {@link GuiCfg#initGuiAreas()} and calls its super.
   * Additional some user initialization is done.
   */
  @Override protected void initGuiAreas(String sAreaMainPanel)
  {
    super.initGuiAreas("A1C2");
    super.gralMng.selectPanel("test");
    super.gralMng.setPosition(5, GralPos.size -3, 0, GralPos.size +10 , 0, 'r');
    btnSwitchOnLog = super.gralMng.addSwitchButton("log", "log telg ?", "log telg", GralColor.getColor("wh"), GralColor.getColor("am") );
    btnSwitchOnLog.setActionChange(actionEnableLog);
    colorSelector = new GralColorSelector("colorSelector", super.gralMng);
    curveA.buildGraphic(gui.mainWindow(), colorSelector);
    curveB.buildGraphic(gui.mainWindow(), colorSelector);
    curveC.buildGraphic(gui.mainWindow(), colorSelector);
    if(user !=null){
      user.initGui(gralMng);
      user.addGuiMenu(gui.mainWindow());
    }
    gui.addMenuBarArea9ItemGThread("menuHelp", "&Help/&Help", gui.getActionHelp());
    gui.addMenuBarArea9ItemGThread("menuAbout", "&Help/&About", gui.getActionAbout());
    gui.addMenuBarArea9ItemGThread("menuAbout", "&Help/e&Xit", gui.getActionAbout());

  }

  
  
  @Override protected void initMain()
  {
    //inspcComm.openComm(cargs.sOwnIpcAddr);
    //msgReceiver.start();
    //oamRcvUdpValue.start();
    super.initMain();  //starts initializing of graphic. Do it after reading some configurations.

  }
  
  @Override protected void stepMain()
  {
    try{
      synchronized(this){ wait(100); }
      curveA.stepSaveCurve();
      curveB.stepSaveCurve();
      curveC.stepSaveCurve();
      //inspcComm.procComm();  
      //oamRcvUdpValue.sendRequest();
    } catch(Exception exc){
      //tread-Problem: console.writeError("unexpected Exception", exc);
      System.out.println("unexpected Exception: " + exc.getMessage());
      exc.printStackTrace();
    }

  }
  
  @Override protected void finishMain()
  {
    super.finishMain();
    try{ inspcMng.close(); } catch(IOException exc){}
  }
  
} //class InspcGuiCfg
  

  private final UserInspcPlug userInspcPlug = new UserInspcPlug();
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] args)
  { boolean bOk = true;
    //
    //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
    //try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
    //}catch(ClassNotFoundException exc){
    //  System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
    //}
    //Loads the named class, and its base class InterProcessCommFactory. 
    //In that kind the calling of factory methods are regarded to socket.
    new InterProcessCommFactorySocket();
    //
    CallingArguments cargs = new CallingArguments();
    //Initializes the GUI till a output window to show informations:
    CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
    bOk = cmdgui.parseArgumentsAndInitGraphic("Inspc-GUI-cfg", "3A3C");
    System.err.println("InspcGui - Test; test");
    LogMessage log = cmdgui.getLogMessageOutputConsole();
    
    
    InspcGui main = new InspcGui(cargs, cmdgui);
    
    main.completeConstruction();
    main.startupThreads();

    main.guiCfg.execute();
    
    cmdgui.exit();
  }


  
  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionEnableLog = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        GralButton widgButton = (GralButton)widgd;
        if(widgButton.isOn()){
          if(logTelg == null){
            logTelg = new LogMessageFile("telgLog", 10, 1, null, null, null);
          }
          inspcMng.inspcAccessor.setLog(logTelg, 1000);
        } else {
          if(logTelg !=null){
            logTelg.close();
          }
          inspcMng.inspcAccessor.setLog(null, 0);
        }
      }
      return true;
    }
  };
  

  private class UserInspcPlug implements UserInspcPlug_ifc, GralPlugUser2Gral_ifc 
  {
    
    UserInspcPlug(){}
    
    @Override public String replacePathPrefix(String path, String[] target)
    {
      // TODO Auto-generated method stub
      String pathRet = guiCfg.guiCfgData.XXXreplacePathPrefix(path, target);
      if(target[0] !=null){
        String targetIp = inspcMng.translateDeviceToAddrIp(target[0]);
        if(targetIp !=null){ target[0] = targetIp; }  //else let it unchanged.
      }
      return pathRet;
    }
    
  } //class UserInspcPlug


  
}
