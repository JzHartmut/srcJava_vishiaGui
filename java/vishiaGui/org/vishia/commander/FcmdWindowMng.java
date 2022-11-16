package org.vishia.commander;


import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

/**This class manages the window of the Java commander
 * @author Hartmut Schorrig
 *
 */
public class FcmdWindowMng
{
  public final Fcmd main;

  public FcmdWindowMng(Fcmd main)
  { this.main = main;
  }
  
  
  
  GralUserAction actionWindFullOut = new GralUserAction("actionWindFullOut")
  { 
    boolean isFullOut = false;
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(isFullOut){main.gui.area9.setFrameAreaBorders(30, 65, 100, 60, 80, 100); isFullOut = false; }
        else {main.gui.area9.setFrameAreaBorders(10, 55, 100, 30, 99, 100); isFullOut = true; }
        return true;
      } else return false;
    }
    
  };
  
}
