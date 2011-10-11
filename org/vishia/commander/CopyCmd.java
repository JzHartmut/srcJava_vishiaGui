package org.vishia.commander;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralColor;
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
  
  GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralWidget widgdOverwrite, widgdOverwriteReadOnly, widgdOverwriteHidden;

  GralWidget widgdProgress;
  
  CopyCmd(JavaCmd main)
  { this.main = main;
  }
  
  
  void buildWindowConfirmCopy()
  {
    main.panelMng.selectPanel("output"); //position relative to the output panel
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.panelMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.panelMng.getPositionInPanel();
    windConfirmCopy = main.panelMng.createWindow("confirm copy", false);
    
    main.panelMng.setPosition(2, GralGridPos.size -2, 1, GralGridPos.size +45, 0, 'd');
    main.panelMng.addText("source path/file:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgdCopyFrom = main.panelMng.addTextField("copyFrom", true, null, 't');
    main.panelMng.addText("destination path/file:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgdCopyTo = main.panelMng.addTextField("copyTo", true, null, 't');
    
    main.panelMng.setPosition(GralGridPos.refer+3.5f, GralGridPos.size -3, GralGridPos.same, GralGridPos.size + 15, 0, 'r');
    widgdOverwrite = main.panelMng.addSwitchButton("copyOverwrite", null, null, null, null, "overwrite", "wh", "gn");
    widgdOverwrite = main.panelMng.addSwitchButton("copyOverwriteReadonly", null, null, null, null, "overwr readonly", "wh", "gn");
    widgdOverwrite = main.panelMng.addSwitchButton("copyOverwriteHidden", null, null, null, null, "overwr hidden", "wh", "gn");

    main.panelMng.setPosition(GralGridPos.refer+3.5f, GralGridPos.samesize, 1, 6, 0, 'r');
    main.panelMng.addButton("copyEsc", actionConfirmCopy, "esc", null, null, "esc");
    main.panelMng.setPosition(GralGridPos.same, GralGridPos.size-2, 7, -7, 0, 'r');
    widgdProgress = main.panelMng.addValueBar("copyProgress", null, null);
    main.panelMng.setPosition(GralGridPos.same, GralGridPos.size-3, -6, -1, 0, 'r');
    main.panelMng.addButton("copyOk", actionConfirmCopy, "ok", null, null, "OK");
  
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