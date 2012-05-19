package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
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
      done = true;
      FileRemote[] files = main.getLastSelectedFiles();
      if(     keyCode == main.idents.keyFileProps){ main.filePropsCmd.openDialog(main.currentFile); }  //F2
      else if(keyCode == main.idents.keyFileView){ main.viewCmd.view(null); }                  //F3
      else if(keyCode == main.idents.keyFileEdit){ main.actionEdit.userActionGui(KeyCode.menuEntered, widgd, params); }
      else if(keyCode == main.idents.keyEditIntern){ main.editWind.openEdit(null); }
      else if(keyCode == main.idents.keyFileCopy){ main.copyCmd.actionConfirmCopy.userActionGui(KeyCode.menuEntered, widgd, params); }
      else if(keyCode == main.idents.keyFileCreate){ main.mkCmd.dialogMkDirFile(files[0]); }
      else if(keyCode == main.idents.keyFileDel1 || keyCode == main.idents.keyFileDel2){ main.deleteCmd.actionConfirmDelete.userActionGui(keyCode, widgd); }
      //navigation
      else if(keyCode == main.idents.keyOriginDir){ main.favorPathSelector.actionSetDirOrigin.userActionGui(KeyCode.menuEntered, widgd, params); }
      else if(keyCode == main.idents.keyRefresh1 || keyCode == main.idents.keyRefresh2 || keyCode == main.idents.keyRefresh3){ main.favorPathSelector.actionRefreshFileTable.userActionGui(KeyCode.menuEntered, widgd, params); }
      else if(keyCode == main.idents.keyWindFullOut){ main.windMng.actionWindFullOut.userActionGui(KeyCode.menuEntered, widgd, params); }
      else if(keyCode == main.idents.keyExecCmdFile) { main.cmdSelector.executeCurrCmdWithFiles(); }
      else if(keyCode == main.idents.keyCreateFavor) { main.favorPathSelector.confirmCreateNewFavor(); }
      else if(keyCode == main.idents.keyFavorLeft) { main.selectPanelLeft.userActionGui(KeyCode.menuEntered, widgd); }
      else if(keyCode == main.idents.keyFavorMiddle) { main.selectPanelMiddle.userActionGui(KeyCode.menuEntered, widgd); }
      else if(keyCode == main.idents.keyFavorRight) { main.selectPanelRight.userActionGui(KeyCode.menuEntered, widgd); }
      else { done = false; }
      return done;
    }
    
  };

  
  
}
