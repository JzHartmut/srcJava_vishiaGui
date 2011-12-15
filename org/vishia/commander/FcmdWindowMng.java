package org.vishia.commander;


import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

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
    main.gui.addMenuItemGThread("MenuOutputFull", "&Window/&Output", actionWindFullOut);
  }
  
  
  GralUserAction actionWindFullOut = new GralUserAction()
  { 
    boolean isFullOut = false;
    @Override public boolean userActionGui(String sIntension, GralWidget widgd, Object... params)
    { if(isFullOut){main.gui.setFrameAreaBorders(30, 65, 60, 80); isFullOut = false; }
      else {main.gui.setFrameAreaBorders(10, 55, 30, 99); isFullOut = true; }
      return true;
    }
    
  };
  
}
