package org.vishia.commander;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class contains all capabilities to execute copy and move for TheCommander.
 * @author Hartmut Schorrig
 *
 */
public class CopyCmd
{
  protected final JavaCmd main;

  
  GralWindow_ifc windConfirmCopy;
  
  GralGridPos posWindConfirmCopy;
  

  CopyCmd(JavaCmd main)
  { this.main = main;
  }
  
  
  void buildWindowConfirmCopy()
  {
    posWindConfirmCopy = main.panelMng.getPositionInPanel();
    windConfirmCopy = main.panelMng.createWindow("confirm copy", false);
    main.panelMng.setPositionSize(2, 2, 3, 5, 'r');
    main.panelMng.addButton("copyEsc", actionConfirmCopy, "esc", null, null, "esc");
    main.panelMng.addButton("copyOk", actionConfirmCopy, "ok", null, null, "OK");
    //windConfirmCopy.setWindowVisible(true);
    //panelMng.setPosition(-30, 0, 40, 40+GralGridPos.size, 1, 'r');
    //windConfirmCopy = panelMng.createWindow(null, false);
    //windConfirmCopy.setWindowVisible(true);
  
  }
  
  
  
  void confirmCopy()
  {
    main.panelMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);

  }
  
  
  
  GralUserAction actionConfirmCopy = new GralUserAction()
  { @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params)
    { if(sIntension.equals("ok")){
      //copy
      }
      main.panelMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
      return true;
    }
  };
  
  
  
}
