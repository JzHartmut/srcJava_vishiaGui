package org.vishia.guiInspc;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralArea9Panel;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralShowMethods;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.cfg.GralCfgZbnf;
import org.vishia.gral.cfg.GuiCfg;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.inspcPC.InspcAccess_ifc;
import org.vishia.inspcPC.InspcPlugUser_ifc;
import org.vishia.inspcPC.InspcTargetAccessData;
import org.vishia.inspcPC.UserInspcPlug_ifc;
import org.vishia.inspcPC.accTarget.InspcTargetAccessor;
import org.vishia.inspcPC.mng.InspcMng;
import org.vishia.inspectorTarget.Inspector;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageFile;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.Assert;
import org.vishia.util.CompleteConstructionAndStart;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class InspcGui extends GuiCfg implements CompleteConstructionAndStart
{

  /**Version, history and license
   * <ul>
   * <li>2016-12-30 Hartmut chg: The buttons for log, retry, get by handle are not part of {@link InspcViewTargetComm} 
   * <li>2016-01-24 Hartmut new: cmd line argument -cycle= for cycletime 
   * <li>2015-01-27 Hartmut new: Test {@link #actionGetValueByHandleIntern}
   * <li>2015-01-27 Hartmut new: Now initialized the {@link GralShowMethods} for usage on edit fields. An edit field
   *   can use the {@link GralShowMethods#syncVariableOnFocus} if the text was changed. 
   *   It invokes {@link VariableAccess_ifc#setString(String)} which changed the content of the variable on their target.
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
  public final static String version = "2023-08-08";
  
  
  
  
  private final List<CompleteConstructionAndStart> composites = new LinkedList<CompleteConstructionAndStart>();
  

  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionSetRetryDisabledVariable = new GralUserAction("InspcGui - setRetryDisabledVariable"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        GralButton widgButton = (GralButton)widgd;
        inspcMng.setmodeRetryDisabledVariables(widgButton.isOn());
      }
      return true;
    }
  };
  

  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionUseGetValueByHandle = new GralUserAction("InspcGui - UseGetValueByIndex"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        GralButton widgButton = (GralButton)widgd;
        inspcMng.setmodeGetValueByIndex(widgButton.isOn());
      }
      return true;
    }
  };
  

  
  
  
  private final CallingArguments cargs;
  
  GralPanelActivated_ifc panelActivated = new GralPanelActivated_ifc(){

    public void panelActivatedGui(List widgets)
    { panelActivated(widgets); 
    }
    /*
    @Override public void panelActivatedGui(Queue<GralWidget> widgets)
    { panelActivated(widgets); 
    }
    */
  };

  //final GuiCfg guiCfg;
  
  final InspcMng inspcMng;
  
  final private Runnable callbackOnReceivedData = new Runnable(){ @Override public void run(){ callbackOnReceivedData(); } };
  
  
  static final GralColor colorRefreshed = GralColor.getColor("wh");
  static final GralColor colorOldValue = GralColor.getColor("lgr");
  
  InspcCurveView curveA, curveB, curveC;
  
  InspcFieldTable fieldsA, fieldsB;
  
  InspcViewTargetComm viewTargetComm;
  
  /**This plugged application for the inspector manager. */  
  InspcPlugUser inspcMngUser;
  
  public GralColorSelector colorSelector;
  
  private final FileCluster fileCluster = FileRemote.clusterOfApplication;

  
  
  InspcGui(CallingArguments cargs, GralPlugUser_ifc user/*, GralArea9MainCmd cmdgui*/) {
    super(cargs, user, null, null);
    this.cargs = cargs;  //args in the correct derived type.
    ButtonInspcCmd.registerUserAction(this.gralMng);
    this.viewTargetComm = new InspcViewTargetComm(this);   // small window to monitor target communication for each target.
//    guiCfg = new InspcGuiCfg(cargs, cmdgui, userInspcPlug);
//    GralMng.get().registerUserAction("<name>", actionGetValueByHandleIntern);
    for(Map.Entry<String, String> entry: cargs.indexTargetIpcAddr.entrySet()){
      this.viewTargetComm.addTarget(entry.getKey(), entry.getValue(), 0.2f, 5);  // target get visible / parameterizable
    }
    

//    LogMessage log = cmdgui.getLogMessageOutputConsole();
    /**
    assert(user instanceof InspcPlugUser_ifc);
    if(user !=null){
      user.init(userInspcPlug, log);
    }
    */
    assert(user == null || user instanceof InspcPlugUser_ifc);
    this.inspcMngUser = new InspcPlugUser((InspcPlugUser_ifc)user);
    if(cargs.sOwnIpcAddr ==null){
      System.err.println("arg ownIpc missing");
      System.exit(255);
    }
    InspcMng variableMng = new InspcMng(cargs.sOwnIpcAddr, cargs.indexTargetIpcAddr, cargs.cycletime, cargs.bUseGetValueByIndex, this.inspcMngUser);
    this.composites.add(variableMng);
    this.inspcMng = variableMng;
    (new GralShowMethods(variableMng)).registerShowMethods(this.gralMng);
    variableMng.setCallbackOnReceivedData(this.callbackOnReceivedData);
    
    if(cargs.sDefaultDirCfgForCurves !=null) {
      CharSequence sDefaultDirCfgForCurves = FileFunctions.normalizePath(new File(cargs.sDefaultDirCfgForCurves).getAbsolutePath());
      FileRemote defaultDirCfg = FileRemote.getDir(sDefaultDirCfgForCurves);   //fileCluster.getFile(sDefaultDirCfgForCurves, null);
      FileRemote defaultDirSave;
      if(cargs.sDefaultDirSaveForCurves !=null) {
        CharSequence sDefaultDirSaveForCurves = FileSystem.normalizePath(new File(cargs.sDefaultDirSaveForCurves).getAbsolutePath());
        defaultDirSave = FileRemote.getDir(sDefaultDirSaveForCurves);
      } else {
        defaultDirSave = null;
      }
      curveA = new InspcCurveView("curve_A", variableMng, null, null, this.gralMng, false, defaultDirCfg, defaultDirSave, null, cargs.curveExporterClasses);
      curveB = new InspcCurveView("curve_B", variableMng, null, null, this.gralMng, false, defaultDirCfg, defaultDirSave, null, cargs.curveExporterClasses);
      curveC = new InspcCurveView("curve_C", variableMng, null, null, this.gralMng, false, defaultDirCfg, defaultDirSave, null, cargs.curveExporterClasses);
    }
    fieldsA = new InspcFieldTable(this.gralMng, variableMng);
    fieldsB = new InspcFieldTable(this.gralMng, variableMng);
    //
    //
    super.menuBar.addMenuItem("menuBarFieldsA", "&Window/open Fields &1", this.fieldsA.actionOpenWindow);
    super.menuBar.addMenuItem("menuBarFieldsB", "&Window/open Fields &2", this.fieldsB.actionOpenWindow);
    super.menuBar.addMenuItem("&Window/open Curve &A ", this.curveA.actionOpenWindow);
    super.menuBar.addMenuItem("&Window/open Curve &B ", this.curveB.actionOpenWindow);
    super.menuBar.addMenuItem("&Window/open Curve &C ", this.curveC.actionOpenWindow);
    super.menuBar.addMenuItem("menuBarViewTargetComm", "&Window/view &TargetComm", viewTargetComm.setVisible);
    //
//    if(user !=null){
//      user.initGui(_gralMng);
//      user.addGuiMenu(gui.mainWindow());
//    }
//    menuBar.addMenuItem("menuHelp", "&Help/&Help", this.gralMng.getActionHelp());
//    menuBar.addMenuItem("menuAbout", "&Help/&About", this.gralMng.getActionAbout());
//    gui.addMenuBarArea9ItemGThread("menuAbout", "&Help/e&Xit", gui.getActionAbout());

  }
  
  @Override public void completeConstruction(){
    this.inspcMng.complete_ReplaceAlias_ifc(this.gralMng.getReplacerAlias());

    for(CompleteConstructionAndStart composite: composites){
      composite.completeConstruction();
    }
  }
  
  @Override public void startupThreads(){
    for(CompleteConstructionAndStart composite: composites){
      composite.startupThreads();
    }
  }

  
  @Override protected void initMain() {
    completeConstruction();
    startupThreads();
    
    this.gralMng.createGraphic("SWT", 'D', this.gralMng.log);
  }
  
  protected void stepMain () {
    super.stepMain();
  }

  
  void panelActivated(List<GralWidget> widgets){
    for(GralWidget widget: widgets){
      
    }
  }
  
  

  
  /**This method is invoked by callback if a receive cycle is finished.
   * Shows values.
   */
  private void callbackOnReceivedData(){
    long time = System.currentTimeMillis();
    GralPanelContent primaryWindow = gralMng.getPrimaryWindow().mainPanel; 
    long timeAtleast = System.currentTimeMillis() - 5000;
    checkWidgetsToRefresh(primaryWindow, time, timeAtleast, 0);
    
    //ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listPanels = guiCfg._gralMng.getVisiblePanels();
    //GralWidget widgdRemove = null;
    try{
      /*
      for(GralVisibleWidgets_ifc panel: listPanels){
        List<GralWidget> widgetsVisible = panel.getWidgetsVisible();
        if(widgetsVisible !=null) for(GralWidget widget: widgetsVisible){
          try{
            String sShowMethod;
            if((sShowMethod = widget.getShowMethod()) ==null || !sShowMethod.equals("stc_cmd")){
              widget.refreshFromVariable(inspcMng, timeAtleast, colorRefreshed, colorOldValue);
              widget.requestNewValueForVariable(time);
            }
          }catch(Exception exc){
            System.err.println("InspcGui-receivedData-widget; " + exc.getMessage());   
            exc.printStackTrace(System.err);
          }
        }
      }
      */
      //referesh the curve view any time if it is enabled:
      curveA.refreshCurve();
      curveB.refreshCurve();
      curveC.refreshCurve();
    } catch(Exception exc){ 
      System.err.println("InspcGui-receivedData; " + exc.getMessage()); 
      exc.printStackTrace(System.out);
    }
    
  }
  
  
  
  void checkWidgetsToRefresh(GralPanelContent panel, long time, long timeAtleast, int recursiveCnt)
  { if(recursiveCnt > 10) { System.err.println("InspcGui: to many recursions"); assert(false); return; }
    for(GralWidgetBase widget1: panel.getWidgetList()){
      if(widget1.isVisible() && widget1 instanceof GralWidget) {
        GralWidget widget = (GralWidget)widget1;
        if(widget instanceof GralPanelContent) {
          checkWidgetsToRefresh((GralPanelContent) widget, time, timeAtleast, recursiveCnt +1);
        } else {
          try{
            if(widget.getDataPath() !=null) {
              String sShowMethod;
              if((sShowMethod = widget.getShowMethod()) ==null || !sShowMethod.equals("stc_cmd")){
                widget.refreshFromVariable(inspcMng, timeAtleast, colorRefreshed, colorOldValue);
                widget.requestNewValueForVariable(time);
              }
            }
          }catch(Exception exc){
            System.err.println("InspcGui-receivedData-widget; " + exc.getMessage());   
            exc.printStackTrace(System.err);
          }
        }
      }
    }
  
  }
  
  
  
  
  
  /**The command-line-arguments are stored in an extra class, which can arranged in any other class too. 
   * The separation of command line argument helps to invoke the functionality with different calls, 
   * for example calling in a GUI, calling in a command-line-batch-process or JZtxtcmd. Hence the class and arguments are public.
   */
  public static class CallingArguments extends GuiCallingArgs {
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
    
    public int cycletime = 100;
    
    String sDefaultDirCfgForCurves = "C:/";

    String sDefaultDirSaveForCurves = "C:/";


    CallingArguments (){
      super("InspcGui made by Hartmut Schorrig, 2010, " + InspcGui.version);
    }
    
    @Override protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;  //set to false if the argc is not passed
      CallingArguments cargs = this;
      String value;
      try{
        if((value = checkArgVal("-targetIpc", arg))!=null) 
        { int posSep = value.indexOf('@');
          if(posSep < 0){
            writeError("argument -targetIpc=KEY@ADDR: The '@' is missed.");
            bOk = false;
          } else {
            String sKey = value.substring(0, posSep);
            String sValue = value.substring(posSep+1);
            this.indexTargetIpcAddr.put(sKey, sValue);
          }
        }
        else if((value = checkArgVal("-pluginCfg", arg))!=null) 
        { cargs.sPluginCfg = value;
        }
        else if((value = checkArgVal("-curve-export", arg))!=null) 
        { int posSep = value.indexOf('=');
          if(posSep < 0){
            writeError("argument -curve-export=EXT=java.class.path");
            bOk = false;
          } else {
            String sKey = value.substring(0, posSep);
            String sValue = value.substring(posSep+1);
            this.curveExporterClasses.put(sKey, sValue);
          }
        }
        else if((value = checkArgVal("-cycle", arg))!=null) 
        { try{ this.cycletime = Integer.parseInt(value); }
          catch(NumberFormatException exc){ bOk = false; writeError("argument \"-cycle=\" should be an integer, read: " + value); }
        }
        else if((value = checkArgVal("-targetbyIndex", arg))!=null) 
        { this.bUseGetValueByIndex = true;   //an example for default output
        }
        else if((value = checkArgVal("-ownIpc", arg))!=null) 
        { cargs.sOwnIpcAddr = value;   //an example for default output
        }
        else if((value = checkArgVal("-dirCurves", arg))!=null) 
        { cargs.sDefaultDirCfgForCurves = value;   //an example for default output
        }
        else if((value = checkArgVal("-dirCurveCfg", arg))!=null) 
        { cargs.sDefaultDirCfgForCurves = value;   //an example for default output
        }
        else if((value = checkArgVal("-dirCurveSave", arg))!=null) 
        { cargs.sDefaultDirSaveForCurves = value;   //an example for default output
        }
        else { bOk = super.testArgument(arg, nArg); }
      } catch(Exception exc){ bOk = false; }
      return bOk;
    }

    
    void writeError(String sError) {
      System.err.println(sError);
    }
  
  } //class CmdLineAndGui 
  

/**Overrides the GuiCfg with special initialization and methods.
 * @author Hartmut Schorrig
 *
 */
//private class InspcGuiCfg extends GuiCfg {
//  
//  /**Initializes the areas for the panels and configure the panels.
//   * This routine overrides {@link GuiCfg#initGuiAreas()} and calls its super.
//   * Additional some user initialization is done.
//   */
//  InspcGuiCfg(CallingArguments cargs, GralPlugUser2Gral_ifc plugUser2Gui) {
//    //super(cargs, cmdgui, null, plugUser2Gui, null); 
//    
//    
//    
//    
//    
//    super.initGuiAreas("A1C2");
//    super._gralMng.selectPanel("test");
//    super._gralMng.setPosition(5, GralPos.size -3, 0, GralPos.size +18 , 0, 'd',1);
//    //btnSwitchOnLog = super._gralMng.addSwitchButton("log", "log telg ?", "log telg", GralColor.getColor("wh"), GralColor.getColor("am") );
//    //btnSwitchOnLog.setActionChange(actionEnableLog);
//    colorSelector = new GralColorSelector("colorSelector", super._gralMng);
//    curveA.buildGraphic(gui.mainWindow(), colorSelector, null, null);
//    curveB.buildGraphic(gui.mainWindow(), colorSelector, curveA.widgCurve.getCommonData(), null);
//    curveC.buildGraphic(gui.mainWindow(), colorSelector, curveA.widgCurve.getCommonData(), null);
//    //
//    _gralMng.selectPanel("primaryWindow");
//    _gralMng.setPosition(14, 84, 4, 64, 0, '.');
//    fieldsA.setToPanel(_gralMng);
//    _gralMng.selectPanel("primaryWindow");
//    _gralMng.setPosition(24, 94, 14, 74, 0, '.');
//    fieldsB.setToPanel(_gralMng);
//    _gralMng.selectPanel("primaryWindow");
//    _gralMng.setPosition(10, 30, 50, 74, 0, '.');
//    viewTargetComm.setToPanel();
//    GralMenu menu = super.guiW.getMenuBar();
//    menu.addMenuItem("menuBarFieldsA", "&Window/open Fields &A", fieldsA.actionOpenWindow);
//    menu.addMenuItem("menuBarFieldsB", "&Window/open Fields &B", fieldsB.actionOpenWindow);
//    menu.addMenuItem("menuBarViewTargetComm", "&Window/view &TargetComm", viewTargetComm.setVisible);
//    //
//    if(user !=null){
//      user.initGui(_gralMng);
//      user.addGuiMenu(gui.mainWindow());
//    }
//    menu.addMenuItem("menuHelp", "&Help/&Help", gui.getActionHelp());
//    menu.addMenuItem("menuAbout", "&Help/&About", gui.getActionAbout());
//    gui.addMenuBarArea9ItemGThread("menuAbout", "&Help/e&Xit", gui.getActionAbout());
//
//  }
//
//  
//  
//  @Override protected void initMain()
//  {
//    //inspcComm.openComm(cargs.sOwnIpcAddr);
//    //msgReceiver.start();
//    //oamRcvUdpValue.start();
//    super.initMain();  //starts initializing of graphic. Do it after reading some configurations.
//
//  }
//  
//  @Override protected void stepMain()
//  {
//    try{
//      synchronized(this){ wait(100); }
//      curveA.stepSaveCurve();
//      curveB.stepSaveCurve();
//      curveC.stepSaveCurve();
//      //inspcComm.procComm();  
//      //oamRcvUdpValue.sendRequest();
//    } catch(Exception exc){
//      System.out.println(Assert.exceptionInfo("InspcGui - unexpected Exception; ", exc, 0, 7));
//      exc.printStackTrace();
//    }
//
//  }
//  
//  @Override protected void finishMain()
//  {
//    super.finishMain();
//    try{ inspcMng.close(); } catch(IOException exc){}
//  }
//  
//} //class InspcGuiCfg
  

  private final UserInspcPlug userInspcPlug = new UserInspcPlug();
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] cmdArgs)
  { 
    CallingArguments cargs = new CallingArguments();
    boolean bOk = cargs.parseArgs(cmdArgs, System.err);
    
    if(bOk) {
      if(cargs.sTitle ==null) {    // title= not determined
        cargs.sTitle = "InspcGui: " + cargs.fileGuiCfg.getAbsolutePath();
      }
      InspcGui main = new InspcGui(cargs, null);
      main.execute();
      System.exit(0);
    } else {
      System.exit(255);
    }
  }


  

  private class UserInspcPlug implements UserInspcPlug_ifc, GralPlugUser2Gral_ifc 
  {
    
    UserInspcPlug(){}
    
    
    @Override public InspcTargetAccessData getTargetFromPath(String sDataPath){
      return InspcGui.this.inspcMng.getTargetAccessFromPath(sDataPath, true);
    }

    
    
  } //class UserInspcPlug


  
  
  private class InspcPlugUser implements InspcPlugUser_ifc
  {

    /**Cascaded user. */
    final InspcPlugUser_ifc user1;
    
    InspcPlugUser(InspcPlugUser_ifc user){
      user1 = user;
    }
    
    @Override public void showStateInfo(String key, TargetState state, int count, int accLevels, float[] cycle_timeout){
      InspcGui.this.viewTargetComm.setStateInfo(key,state, count, accLevels, cycle_timeout);
    }  

    
    @Override public void setInspcComm(InspcAccess_ifc inspcMng)
    { if(user1 !=null) { user1.setInspcComm(inspcMng); }
    }

    @Override public void requData(int ident)
    { if(user1 !=null) { user1.requData(ident); }
    }

    @Override public void isSent(int seqnr)
    { if(user1 !=null) { user1.isSent(seqnr); }
    }

    @Override
    public void registerTarget(String name, String sAddr, InspcTargetAccessor targetAcc)
    {
      viewTargetComm.registerTarget(name, sAddr, targetAcc);
      
    }
    
  }
  
  
  
  GralUserAction actionGetValueByHandleIntern = new GralUserAction("getValueByHandleIntern"){
    int handle = 0;
    public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      System.out.println("hello");
      Inspector inspector = Inspector.get();
      if(handle == 0){
        handle = inspector.classContent.registerHandle("this$0.inspcMng.threadEvent.timeSleep", null);
      }
      if(handle !=-1){
        int value = inspector.classContent.getIntValueByHandle(handle);
        System.out.println("value =" + value);
      }
      return true;
    }
  };
  
  
}
