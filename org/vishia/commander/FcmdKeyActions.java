package org.vishia.commander;

import java.io.File;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.util.FileRemote;
import org.vishia.util.KeyCode;





/**This class handles all actions of common keys. Most of the keys are common.
 * 
 * Hartmut Schorrig
 */
public class FcmdKeyActions
{
  final Fcmd main;
  
  //---------------------------------------------------------------------------------
  //Working
  int keyFileProps = KeyCode.F2;
  
  int keyView = KeyCode.F3;
  
  int keyEdit = KeyCode.F4;
  
  int keyCopy = KeyCode.F5;
  
  int keyMkDirFile = KeyCode.F7;
  
  int keyDelete1 = KeyCode.F8;
  
  int keyDelete2 = KeyCode.del;
  
  int keyWindFullOut = KeyCode.ctrl + 'o';
  
  int keyExecuteInShellOpened = KeyCode.ctrl + KeyCode.alt + KeyCode.shift + KeyCode.enter;
  
  int keyExecuteInShell = KeyCode.ctrl + KeyCode.alt + KeyCode.enter;
  
  int keyExecuteStartProcess = KeyCode.ctrl + KeyCode.shift + KeyCode.enter;
  
  int keyExecuteInJcmd =  KeyCode.ctrl + KeyCode.enter;
  
  int keyExecuteAsJavaClass =  KeyCode.ctrl + KeyCode.alt + KeyCode.shift + KeyCode.enter;
  
  int keyOpenProperties = KeyCode.alt + KeyCode.enter;
  
  //---------------------------------------------------------------------------------
  //Navigation 
  int keySelectPanelLeft = KeyCode.alt + KeyCode.F1;
  int keySelectPanelMid = KeyCode.alt + KeyCode.F2;
  int keySelectPanelRight = KeyCode.alt + KeyCode.F3;
  int keyCreateFavorite = KeyCode.ctrl + 'b';   //like 'Lesezeichen einfuegen'
  
  int keyPanelSelection = KeyCode.alt + KeyCode.dn;
  
  int keyPanelFile = KeyCode.alt + KeyCode.ctrl + KeyCode.up;
  
  int keyPanelLeft = KeyCode.alt + KeyCode.ctrl + KeyCode.left;
  
  int keyPanelRight = KeyCode.alt + KeyCode.ctrl + KeyCode.right;
  
  int keyMainPanelLeft = KeyCode.alt + KeyCode.shift + KeyCode.left;
  
  int keyMainPanelRight = KeyCode.alt + KeyCode.shift + KeyCode.right;
  

  /**Show files in the current selected dir. Entry in the directory. */
  int keyEntryDir;
  /**Show files in the parent dir, like cd .. */
  int keyLeaveDir;
  
  
  //---------------------------------------------------------------------------------
  //View
  
  
  FcmdKeyActions(Fcmd parent)
  {
    main = parent;
  }
  
  GralUserAction commanderKeyActions = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget widgd, Object... params)
    { boolean done = false;
      //if(sIntension.equals("key")){
        //int keyCode = (Integer)params[0];
      String sIntension = "key";
        done = true;
        switch (keyCode){
          //case KeyCode.F4: main.actionEdit.userActionGui(sIntension, widgd, params); break;
          //case KeyCode.F5: main.copyCmd.actionCopy.userActionGui(keyCode, widgd, params); break;
          case KeyCode.alt + KeyCode.F1: main.selectPanelLeft.userActionGui(sIntension, widgd, params); break;
          case KeyCode.alt + KeyCode.F2: main.selectPanelMiddle.userActionGui(sIntension, widgd, params); break;
          case KeyCode.alt + KeyCode.F3: main.selectPanelRight.userActionGui(sIntension, widgd, params); break;
          default: done = false;
        }
        if(!done){
          done = true;
          FileRemote[] files = main.getLastSelectedFiles();
          if(     keyCode == keyFileProps){ main.filePropsCmd.openDialog(null); }  //F2
          else if(keyCode == keyView){ main.viewCmd.view(null); }                  //F3
          else if(keyCode == keyEdit){ main.actionEdit.userActionGui(sIntension, widgd, params); }
          else if(keyCode == keyCopy){ main.copyCmd.actionConfirmCopy.userActionGui(keyCode, widgd, params); }
          else if(keyCode == keyMkDirFile){ main.mkCmd.dialogMkDirFile(files[0]); }
          else if(keyCode == keyDelete1 || keyCode == keyDelete2){ main.deleteCmd.actionConfirmDelete.userActionGui(keyCode, widgd); }
          //navigation
          else if(keyCode == main.idents.keyOriginDir){ main.favorPathSelector.actionSetDirOrigin.userActionGui(KeyCode.menuEntered, widgd, params); }
          else if(keyCode == main.idents.keyRefresh1 || keyCode == main.idents.keyRefresh2){ main.favorPathSelector.actionRefreshFileTable.userActionGui(KeyCode.menuEntered, widgd, params); }
          else if(keyCode == keyWindFullOut){ main.windMng.actionWindFullOut.userActionGui(sIntension, widgd, params); }
          else if(keyCode == KeyCode.ctrl + KeyCode.enter) { main.cmdSelector.executeCurrCmdWithFiles(); }
          else { done = false; }
        }
      //}
      return done;
    }
    
  };

  
  
}
