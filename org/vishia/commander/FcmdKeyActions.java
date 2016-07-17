package org.vishia.commander;

import java.io.File;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;





/**This class handles all actions of common keys. Most of the keys are common.
 * 
 * Hartmut Schorrig
 */
public class FcmdKeyActions
{
  final Fcmd main;
  
  int keyExecuteInJcmd =  KeyCode.ctrl + KeyCode.enter;
  
  int keyExecuteAsJavaClass =  KeyCode.ctrl + KeyCode.alt + KeyCode.shift + KeyCode.enter;
  int keyExecuteInShellOpened = KeyCode.ctrl + KeyCode.alt + KeyCode.shift + KeyCode.enter;
  
  int keyExecuteInShell = KeyCode.ctrl + KeyCode.alt + KeyCode.enter;
  
  int keyExecuteStartProcess = KeyCode.ctrl + KeyCode.shift + KeyCode.enter;

  int keyPanelSelection = KeyCode.alt + KeyCode.dn;
  
  
  int keyCreateFavorite = KeyCode.ctrl + 'b';   //like 'Lesezeichen einfuegen'
  //---------------------------------------------------------------------------------
  //Working
  /*
  int keyFileProps = KeyCode.F2;
  
  int keyView = KeyCode.F3;
  
  int keyEdit = KeyCode.F4;
  
  int keyCopy = KeyCode.F5;
  
  int keyMkDirFile = KeyCode.F7;
  
  int keyDelete1 = KeyCode.F8;
  
  int keyDelete2 = KeyCode.del;
  
  int keyWindFullOut = KeyCode.ctrl + 'o';
  
  
  
  int keyOpenProperties = KeyCode.alt + KeyCode.enter;
  
  //---------------------------------------------------------------------------------
  //Navigation 
  int keySelectPanelLeft = KeyCode.alt + KeyCode.F1;
  int keySelectPanelMid = KeyCode.alt + KeyCode.F2;
  int keySelectPanelRight = KeyCode.alt + KeyCode.F3;
  
  */
  //int keyPanelFile = KeyCode.alt + KeyCode.ctrl + KeyCode.up;
  
  //int keyPanelLeft = KeyCode.alt + KeyCode.ctrl + KeyCode.left;
  
  //int keyPanelRight = KeyCode.alt + KeyCode.ctrl + KeyCode.right;
  
  //int keyMainPanelLeft = KeyCode.alt + KeyCode.shift + KeyCode.left;
  
  //int keyMainPanelRight = KeyCode.alt + KeyCode.shift + KeyCode.right;
  

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
  

  
  
}
