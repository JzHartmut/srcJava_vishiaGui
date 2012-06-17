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
  
  
  void initMenuWindMng()
  {
    main.gui.addMenuItemGThread("MenuOutputFull", main.idents.menuWindowOutputBar, actionWindFullOut);
  }
  
  
  GralUserAction actionWindFullOut = new GralUserAction()
  { 
    boolean isFullOut = false;
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(isFullOut){main.gui.setFrameAreaBorders(30, 65, 60, 80); isFullOut = false; }
        else {main.gui.setFrameAreaBorders(10, 55, 30, 99); isFullOut = true; }
        return true;
      } else return false;
    }
    
  };
  
}
