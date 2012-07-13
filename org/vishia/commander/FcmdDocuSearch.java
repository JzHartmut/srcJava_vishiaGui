package org.vishia.commander;

import org.vishia.gral.base.GralWidgetMng;

/**
 * <b>File search, current files</b>:
 * <br>
 * <ul>
 * <li>{@link Fcmd#getLastSelectedFiles()}
 * </ul>
 * @author Hartmut Schorrig
 *
 */

public interface FcmdDocuSearch {
  
  
  
  /**The {@link GralWidgetMng#setMainKeyAction(org.vishia.gral.ifc.GralUserAction)} knows a central processing of keys,
   * which are invoked from all widgets. 
   * The {@link FcmdButtons#actionMainKeys} is registered for that. 
   * The method {@link FcmdButtons#processKey(int)} processes the common keys with the table
   * {@link FcmdButtons#keys} and its associated {@link FcmdButtons#keyAction}.
   * That arrays are filled by registering the actions in FcmdButtons#addButton(...) 
   * called from {@link FcmdButtons#initPanelButtons()}. 
   * The information which keys and methods are contained in the {@link FcmdIdents} 
   * which are referenced in {@link Fcmd#idents}.
   * All key settings in {@link FcmdIdents} are variables which are initialized and which can be changed from a script
   * before they are used. In that kind all keys and texts are flexible. 
   * 
   */
  void keyActions();
  

}
