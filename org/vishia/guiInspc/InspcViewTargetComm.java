package org.vishia.guiInspc;

import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

public class InspcViewTargetComm
{
  /**The window to present. */
  private final GralWindow wind;
  
  private final GralLed[] widgLedTarget = new GralLed[5];
  
  private GralColor colorIdle = GralColor.getColor("gn");
  
  private GralColor colorWaiting = GralColor.getColor("or");
  
  private GralColor colorElse = GralColor.getColor("ye");
  
  public InspcViewTargetComm(String targetId)
  {
    super();
    this.wind = new GralWindow(null, "InspcViewTargetComm", "TargetCommunication " + targetId, GralWindow_ifc.windOnTop);
    for(int ix = 0; ix < widgLedTarget.length; ++ix) {
      this.widgLedTarget[ix] = new GralLed("State " + ix);
    }
    
    
  }
  
  
  public void setToPanel(GralMng mng) {
    wind.setToPanel(mng);
    mng.setPosition(2, 4, 2, 4, 0, 'd', 1);
    for(int ix = 0; ix < widgLedTarget.length; ++ix) {
      this.widgLedTarget[ix].setToPanel(mng);;
    }
  }
  
  
  
  public boolean isVisible() { return wind.isVisible(); }
  
  
  public void step(int ixTarget, int state) {
    switch(state) {
      case 1:  { widgLedTarget[ixTarget].setColor(colorIdle, colorIdle); } break;
      case 2:  { widgLedTarget[ixTarget].setColor(colorWaiting, colorElse); } break;
      default: { widgLedTarget[ixTarget].setColor(colorElse, colorElse); }
    }
  }
  
  
  GralUserAction actionOpenWindow = new GralUserAction("InspcViewTargetComm - open window"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        wind.setVisible(true);
        return true;
      } else { 
        return false;
      }
    }
  };

}
