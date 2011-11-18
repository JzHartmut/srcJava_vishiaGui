package org.vishia.commander;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.KeyCode;





/**This class handles all actions of common keys. Most of the keys are common.
 * 
 * Hartmut Schorrig
 */
public class JavaCmdKeyActions
{
  final JavaCmd main;
  
  //---------------------------------------------------------------------------------
  //Working
  int keyEdit = KeyCode.F4;
  
  int keyCopy = KeyCode.F5;
  
  int keyDelete1 = KeyCode.F8;
  
  int keyDelete2 = KeyCode.del;
  
  int keyWindFullOut = KeyCode.ctrl + 'o';
  
  //---------------------------------------------------------------------------------
  //Navigation 
  int keySelectPanelLeft = KeyCode.alt + KeyCode.F1;
  int keySelectPanelMid = KeyCode.alt + KeyCode.F2;
  int keySelectPanelRight = KeyCode.alt + KeyCode.F3;
  int keyCreateFavorite = KeyCode.ctrl + 'b';   //like 'Lesezeichen einfuegen'
  
  int keyPanelSelection = KeyCode.alt + KeyCode.dn;
  
  int keyPanelFile = KeyCode.alt + KeyCode.up;
  
  int keyPanelLeft = KeyCode.alt + KeyCode.left;
  
  int keyPanelRight = KeyCode.alt + KeyCode.right;
  
  int keyMainPanelLeft = KeyCode.alt + KeyCode.shift + KeyCode.left;
  
  int keyMainPanelRight = KeyCode.alt + KeyCode.shift + KeyCode.right;
  

  /**Show files in the start dir of the selection. Go to the origin dir.*/
  int keyOriginDir = KeyCode.ctrl + '<';  //like total commander: jump to the root directory
  /**Show files in the current selected dir. Entry in the directory. */
  int keyEntryDir;
  /**Show files in the parent dir, like cd .. */
  int keyLeaveDir;
  
  
  //---------------------------------------------------------------------------------
  //View
  
  
  JavaCmdKeyActions(JavaCmd parent)
  {
    main = parent;
  }
  
  GralUserAction commanderKeyActions = new GralUserAction()
  { @Override public boolean userActionGui(String sIntension, GralWidget widgd, Object... params)
    { boolean done = false;
      if(sIntension.equals("key")){
        int keyCode = (Integer)params[0];
        done = true;
        switch (keyCode){
          case KeyCode.F4: main.actionEdit.userActionGui(sIntension, widgd, params); break;
          case KeyCode.F5: main.actionCopy.userActionGui(sIntension, widgd, params); break;
          case KeyCode.alt + KeyCode.F1: main.selectPanelLeft.userActionGui(sIntension, widgd, params); break;
          case KeyCode.alt + KeyCode.F2: main.selectPanelMiddle.userActionGui(sIntension, widgd, params); break;
          case KeyCode.alt + KeyCode.F3: main.selectPanelRight.userActionGui(sIntension, widgd, params); break;
          default: done = false;
        }
        if(!done){
          done = true;
          if(     keyCode == keyEdit){ main.actionEdit.userActionGui(sIntension, widgd, params); }
          else if(keyCode == keyCopy){ main.actionCopy.userActionGui(sIntension, widgd, params); }
          else if(keyCode == keyDelete1 || keyCode == keyDelete2){ main.actionDelete.userActionGui(keyCode, widgd); }
          //navigation
          else if(keyCode == keyOriginDir){ main.favorPathSelector.actionSetDirOrigin.userActionGui(sIntension, widgd, params); }
          else if(keyCode == KeyCode.enter){ main.favorPathSelector.actionSetDirOrigin.userActionGui(sIntension, widgd, params); }
          else if(keyCode == keyWindFullOut){ main.windMng.actionWindFullOut.userActionGui(sIntension, widgd, params); }
          else { done = false; }
        }
      }
      return done;
    }
    
  };

  
  
}
