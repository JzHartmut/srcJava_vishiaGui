package org.vishia.guiInspc;

import java.util.Locale;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspcPC.InspcPlugUser_ifc;
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
  protected final static String sVersion = "2015-05-30";

  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  //private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<Object> widgTable;


  private final GralButton wdgBtnLog;

  private GralColor colorInactive = GralColor.getColor("wh")
                  , colorIdle = GralColor.getColor("lgn")
                  , colorWait = GralColor.getColor("lrd")
                  , color2 = GralColor.getColor("or");

  
  public InspcViewTargetComm(InspcGui gui)
  { //inspcMng.addUserOrder(this);  //invoke run in any communication step.
    this.wind = new GralWindow("@primaryWindow,-21..0,-50..0", "InspcCtrlStatusWind", "State of targets", GralWindow_ifc.windOnTop | GralWindow_ifc.windResizeable);
    this.widgTable = new GralTable<Object>("@InspcCtrlStatusWind,3..-3,0..0=TargetTable", new int[]{3, 0,-6,-6});
    this.widgTable.setColumnEditable(2,  true);
    this.widgTable.setColumnEditable(3,  true);
    //this.widgTable.setColumnEditable(2, true);
    this.widgTable.setHtmlHelp("HelpInspc.html#Topic.HelpInspc.ctrlStatus.");
    this.wdgBtnLog = new GralButton("@InspcCtrlStatusWind,-2..0,0..10=BtnLog", "Enable Log", null);
    this.wdgBtnLog.setSwitchMode("? Log", "Log ?off");
    this.wdgBtnLog.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("am"));
    this.wdgBtnLog.specifyActionChange("switch log telg", gui.actionEnableLog, null);
  }
  
  
  /**Invoked in the graphic thread.
   */
  public void setToPanel(){
    GralMng mng = GralMng.get();
    wind.setToPanel(mng);
    //mng.setPosition(0, 2, 0, 3, 0, 'd');
    //mng.setPosition(0, 2, 3, 0, 0, 'd');
    //mng.setPosition(2, -4, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
    //mng.setPosition(-2, 0, 0, 7, 0, 'd');
  }
  

  GralUserAction setVisible = new GralUserAction("")
  { public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      wind.setFocus(); //Visible(true);   
      return true;
    }
  };
  
  
  public void addTarget(String key, String info, float cycle, float timeout){
    GralTableLine_ifc<Object> line = widgTable.addLine(key, null, null);
    line.setCellText(key + "@" + info, 1);
    line.setCellText(String.format(Locale.US, "%1.1f", cycle), 2);  //use a 0.2 and not 0,2 in a german installation
    line.setCellText(String.format(Locale.US, "%3.1f", timeout), 3);
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
    GralTableLine_ifc<Object> line = widgTable.getLine(key);
    if(line !=null) {
      line.setCellText(Integer.toHexString(count & 0xff), 0);
      line.setBackColor(color, 0);
      if(cycle_timeout !=null) { 
        String sLine = line.getCellText(3);
        float timeout = StringFunctions_C.parseFloat(sLine, 0, -1, null);
        if(timeout > 0){
          cycle_timeout[1] = timeout;
        }
      }
    } else {
      System.err.println("InspcViewTargetComm - unknown target, "+ key);
    }
  }
  
  
  public boolean isLogOn() { return wdgBtnLog.getState()== GralButton.State.On; }
  
  float getTimeout(String target){
    float timeout; 
    GralTableLine_ifc<Object> line = widgTable.getLine(target);
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
  
  

}
