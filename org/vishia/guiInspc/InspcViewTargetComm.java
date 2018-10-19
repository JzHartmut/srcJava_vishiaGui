package org.vishia.guiInspc;

import java.util.Locale;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspcPC.InspcPlugUser_ifc;
import org.vishia.inspcPC.accTarget.InspcTargetAccessor;
import org.vishia.inspcPC.mng.InspcMng;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions_C;

/**This class contains some status and control widgets for the Inspector Gui to show the status of communication.
 * The communication itself is organized in {@link org.vishia.inspcPC.accTarget.InspcTargetAccessor} in the software component javaSrc_vishiaRun.
 * @author Hartmut Schorrig
 *
 */
public class InspcViewTargetComm
{
  /**Version, history and license.
   * <ul>
   * <li>2018-10-19 new {@link #windTargetSettings} for target settings, better handling of timeout, password-setting. 
   *   Better handling of clearReq: Now it works with timeout=0 and manual clearRequest on stepbystep-debugging of the target.
   * <li>2015-05-30 Created.
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
   * 
   */
  //@SuppressWarnings("hiding")
  protected final static String sVersion = "2018-10-19";

  
  /**The window to present. */
  private final GralWindow windStateOfTarget;
  
  /**The window to input passwords. */
  private final GralWindow windTargetSettings;
  
  /**Shows the path in target to this struct. */
  //private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<InspcTargetAccessor> widgTable;


  private final GralButton wdgBtnLog, wdgBtnRetry, wdgBtnClearReq, wdgBtnPerHandle, wdgBtnTargetSettings;
  
  
  private final GralTextField wdgPwdAccess, wdgPwdChg; //, wdgPwdAccess1, wdgPwdChg1, wdgPwdAccess2, wdgPwdChg2, wdgPwdAccess3, wdgPwdChg3; 

  private final GralTextField wdgTargetIdent, wdgTimeout, wdgTimeCycle;
  
  private final GralButton wdgBtnTargetSettingsOk; 
  
  private GralColor colorInactive = GralColor.getColor("wh")
                  , colorIdle = GralColor.getColor("lgn")
                  , colorWait = GralColor.getColor("lrd")
                  , color2 = GralColor.getColor("or");

  
  final InspcGui gui;
  
  
  InspcTargetAccessor targetForSettingsWindow;
  
  public InspcViewTargetComm(InspcGui gui)
  { //inspcMng.addUserOrder(this);  //invoke run in any communication step.
    this.gui = gui;
    this.windStateOfTarget = new GralWindow("@primaryWindow,-21..0,-50..0", "InspcCtrlStatusWind", "State of targets", GralWindow_ifc.windOnTop | GralWindow_ifc.windResizeable);
    this.widgTable = new GralTable<InspcTargetAccessor>("@InspcCtrlStatusWind,0..-3,0..0=TargetTable", new int[]{3, 0,-6,-6});
    this.widgTable.setColumnEditable(2,  true);
    this.widgTable.setColumnEditable(3,  true);
    //this.widgTable.setColumnEditable(2, true);
    this.widgTable.setHtmlHelp("HelpInspc.html#Topic.HelpInspc.ctrlStatus.");
    this.wdgBtnLog = new GralButton("@InspcCtrlStatusWind,-2..0,0..8=BtnLog", "Enable Log", null);
    this.wdgBtnLog.setSwitchMode("? Log", "Log ?off");
    this.wdgBtnLog.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("am"));
    this.wdgBtnLog.specifyActionChange("switch log telg", gui.actionEnableLog, null);
    this.wdgBtnRetry = new GralButton("@InspcCtrlStatusWind,-2..0,9..17=BtnRetry", "Retry", null);
    this.wdgBtnRetry.setSwitchMode("? Retry", "Retry ?off");
    this.wdgBtnRetry.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("am"));
    this.wdgBtnRetry.specifyActionChange("retry variables", gui.actionSetRetryDisabledVariable, null);
    this.wdgBtnClearReq = new GralButton("@InspcCtrlStatusWind,-2..0,18..27=BtnClearReq", "ClearReq", null);
    this.wdgBtnClearReq.specifyActionChange("retry variables", actionClearReq, null);
    this.wdgBtnPerHandle = new GralButton("@InspcCtrlStatusWind,-2..0,28..37=BtnLog", "Enable Log", null);
    this.wdgBtnPerHandle.setSwitchMode("? use Handle", "use Handle ?off");
    this.wdgBtnPerHandle.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("gn"));
    this.wdgBtnPerHandle.specifyActionChange("use handle", gui.actionUseGetValueByHandle, null);
    this.wdgBtnTargetSettings = new GralButton("@InspcCtrlStatusWind,-2..0,38..47=BtnTargetSettings", "@Settings", null);
    this.wdgBtnTargetSettings.specifyActionChange("openPwd", actionOpenWindowTargetSettings, null);
  
    this.windTargetSettings = new  GralWindow("@primaryWindow,-36..-18,-30..0", "InspcTargetPwdWind", "Passwords for Target", GralWindow_ifc.windOnTop);
    
    this.wdgTargetIdent = new GralTextField("@InspcTargetPwdWind, 2-2,1+20=targetident");
    this.wdgTimeCycle = new GralTextField("@InspcTargetPwdWind, 6-4,5+10=timeCycle", GralTextField.Type.editable);
    this.wdgTimeout =    new GralTextField("@InspcTargetPwdWind, 6-4,17+10=timeout",   GralTextField.Type.editable);
    this.wdgTimeCycle.setPrompt("cycle [s]", "t");
    this.wdgTimeout.setPrompt("timeout [s] 0-debug", "t");
    
    this.wdgPwdAccess = new GralTextField("@InspcTargetPwdWind, 10-4,5+10=PwdAccess0", GralTextField.Type.editable);
    this.wdgPwdChg =    new GralTextField("@InspcTargetPwdWind, 10-4,17+10=PwdChg0",   GralTextField.Type.editable);
    this.wdgPwdAccess.setPrompt("pwd access", "t");
    this.wdgPwdChg.setPrompt("pwd change", "t");
    //    this.wdgPwdAccess1 = new GralTextField("@InspcTargetPwdWind, 6-2,5+10=PwdAccess1", GralTextField.Type.editable);
//    this.wdgPwdChg1 =    new GralTextField("@InspcTargetPwdWind, 6-2,17+10=PwdChg1",   GralTextField.Type.editable);
//    this.wdgPwdAccess2 = new GralTextField("@InspcTargetPwdWind, 8-2,5+10=PwdAccess2", GralTextField.Type.editable);
//    this.wdgPwdChg2 =    new GralTextField("@InspcTargetPwdWind, 8-2,17+10=PwdChg2",   GralTextField.Type.editable);
//    this.wdgPwdAccess3 = new GralTextField("@InspcTargetPwdWind, 10-2,5+10=PwdAccess3", GralTextField.Type.editable);
//    this.wdgPwdChg3 =    new GralTextField("@InspcTargetPwdWind, 10-2,17+10=PwdChg3",   GralTextField.Type.editable);
    this.wdgBtnTargetSettingsOk = new GralButton("@InspcTargetPwdWind, 12-2,22+5=BtnSettingsOk", "ok", null);
    this.wdgBtnTargetSettingsOk.specifyActionChange("openPwd", actionSetTargetSettings, null);
  
  
  }
  
  
  /**Invoked in the graphic thread.
   */
  public void setToPanel(){
    GralMng mng = GralMng.get();
    windStateOfTarget.setToPanel(mng);
    windTargetSettings.createImplWidget_Gthread();
    //mng.setPosition(0, 2, 0, 3, 0, 'd');
    //mng.setPosition(0, 2, 3, 0, 0, 'd');
    //mng.setPosition(2, -4, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
    //mng.setPosition(-2, 0, 0, 7, 0, 'd');
  }
  

  GralUserAction setVisible = new GralUserAction("")
  { public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      windStateOfTarget.setFocus(); //Visible(true);   
      return true;
    }
  };
  
  
  public void addTarget(String key, String info, float cycle, float timeout){
//    GralTableLine_ifc<InspcTargetAccessor> line = widgTable.addLine(key, null, null);
//    line.setCellText(key + "@" + info, 1);
//    line.setCellText(String.format(Locale.US, "%1.1f", cycle), 2);  //use a 0.2 and not 0,2 in a german installation
//    line.setCellText(String.format(Locale.US, "%3.1f", timeout), 3);
  }

  
  
  void registerTarget(String name, String sAddr, InspcTargetAccessor targetAcc) {
    GralTableLine_ifc<InspcTargetAccessor> line = widgTable.addLine(name, null, targetAcc);
    line.setCellText(name + "@" + sAddr, 1);
    line.setCellText(String.format(Locale.US, "%1.1f", targetAcc.cycle_timeout[0]), 2);  //use a 0.2 and not 0,2 in a german installation
    line.setCellText(String.format(Locale.US, "%3.1f", targetAcc.cycle_timeout[1]), 3);
  }
  
  
  public void setStateInfo(String key, InspcPlugUser_ifc.TargetState state, int count, float[] cycle_timeout){
    GralColor color;
    switch(state) {
      case idle: color = colorIdle; break;
      case inactive: color = colorInactive; break;
      case waitReceive: color = colorWait; break;
      case receive: color = color2; break;
      default: color = color2;
    }
    GralTableLine_ifc<InspcTargetAccessor> line = widgTable.getLine(key);
    if(line !=null) {
      line.setCellText(Integer.toHexString(count & 0xff), 0);
      line.setBackColor(color, 0);
      line.setCellText(Float.toString(cycle_timeout[0]), 2);
      line.setCellText(Float.toString(cycle_timeout[1]), 3);
//      if(cycle_timeout !=null) { 
//        String sLine = line.getCellText(3);
//        float timeout = StringFunctions_C.parseFloat(sLine, 0, -1, null);
//        if(timeout > 0){
//          //cycle_timeout[1] = timeout;
//        }
//      }
    } else {
      System.err.println("InspcViewTargetComm - unknown target, "+ key);
    }
  }
  
  
  public boolean isLogOn() { return wdgBtnLog.getState()== GralButton.State.On; }
  
  float getTimeout(String target){
    float timeout; 
    GralTableLine_ifc<InspcTargetAccessor> line = widgTable.getLine(target);
     if(line !=null) {
       String sLine = line.getCellText(3);
       timeout = StringFunctions_C.parseFloat(sLine, 0, -1, null);
       if(timeout <= 0){
         timeout = 1.0f;
       }
     } else {
       System.err.println("InspcViewTargetComm - unknown target, "+ target);
       timeout = 5.0f;
     }
     return timeout;
   }
  
  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionOpenWindowTargetSettings = new GralUserAction("InspcGui - OpenPwd"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        targetForSettingsWindow = widgTable.getCurrentLine().data;
        windTargetSettings.setVisible(true);
        wdgTargetIdent.setText(targetForSettingsWindow.name);
        wdgTimeCycle.setText("" + targetForSettingsWindow.cycle_timeout[0]);
        wdgTimeout.setText("" + targetForSettingsWindow.cycle_timeout[1]);
      }
      return true;
    }
  };
  


  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionSetTargetSettings = new GralUserAction("InspcGui - OpenPwd"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        String pwdAccess = wdgPwdAccess.getText().trim();
        String pwdChange = wdgPwdChg.getText().trim();
        String sTimeCycle = wdgTimeCycle.getText();
        float timeCycle = StringFunctions_C.parseFloat(sTimeCycle, 0, -1, null);
        String sTimeout = wdgTimeout.getText();
        float timeout = StringFunctions_C.parseFloat(sTimeout, 0, -1, null);
        
        targetForSettingsWindow.setPwdCycle(pwdAccess, pwdChange, timeCycle, timeout);
      }
      return true;
    }
  };
  

  /**Action for button log. It switches on or off the logging functionality to log the telegram traffic
   * for debugging. */
  GralUserAction actionClearReq = new GralUserAction("InspcGui - clearReq"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        gui.inspcMng.clearRequestedVariables();
        InspcTargetAccessor target = widgTable.getCurrentLine().getUserData();
        target.setReady();
        
      }
      return true;
    }
  };
  



}
