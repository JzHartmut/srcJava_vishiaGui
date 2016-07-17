package org.vishia.gral.awt;

import java.awt.event.KeyEvent;

import org.vishia.util.KeyCode;

/**Adaption from AWT-KeyCodes to universal vishia key codes.
 * @author Hartmut Schorrig
 *
 */
public class AwtGralKey extends KeyCode
{
    /**Version, history and license
   * <ul>
   * <li>2015-30-10 Hartmut created for Key handling from SwtGralkeyin the GRAL. 
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 20131116;

  /**This class isn't instantiated. Only the static method and the static definitions of KeyCode
   * are used.  */
  private AwtGralKey()
  { super(0);
  }
  
  public static int convertFromAwt(int keyCode, int stateMask, char characterKey)
  {
    final int stateKeys, key;    
    if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT){
      stateKeys = ctrl + alt + shift;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_ALT + KeyEvent.VK_CONTROL){
      stateKeys = ctrl + alt;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_CONTROL + KeyEvent.VK_SHIFT){
      stateKeys = ctrl + shift;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_ALT + KeyEvent.VK_SHIFT){
      stateKeys = alt + shift;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_CONTROL){
      stateKeys = ctrl;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_ALT){
      stateKeys = alt;
    } else if((stateMask & (KeyEvent.VK_CONTROL + KeyEvent.VK_ALT + KeyEvent.VK_SHIFT)) == KeyEvent.VK_SHIFT){
      stateKeys = shift;
    } else{
      stateKeys = 0;
    } 
    switch(keyCode){
      case KeyEvent.VK_LEFT:  key = left; break;
      case KeyEvent.VK_RIGHT: key = right; break;
      case KeyEvent.VK_UP:    key = up; break;
      case KeyEvent.VK_DOWN:  key = dn; break;
      case KeyEvent.VK_PAGE_UP:     key = pgup; break;
      case KeyEvent.VK_PAGE_DOWN:   key = pgdn; break;
      case KeyEvent.VK_F1:          key = F1; break;
      case KeyEvent.VK_F2:          key = F2; break;
      case KeyEvent.VK_F3:          key = F3; break;
      case KeyEvent.VK_F4:          key = F4; break;
      case KeyEvent.VK_F5:          key = F5; break;
      case KeyEvent.VK_F6:          key = F6; break;
      case KeyEvent.VK_F7:          key = F7; break;
      case KeyEvent.VK_F8:          key = F8; break;
      case KeyEvent.VK_F9:          key = F9; break;
      case KeyEvent.VK_F10:         key = F10; break;
      case KeyEvent.VK_F11:         key = F11; break;
      case KeyEvent.VK_F12:         key = F12; break;
      case KeyEvent.VK_HOME:        key = home; break;
      case KeyEvent.VK_END:         key = end; break;
      case KeyEvent.VK_INSERT:      key = ins; break;
      case KeyEvent.VK_DELETE:      key = del; break;
      case KeyEvent.VK_TAB:         key = tab; break;
      case 0x0d:            key = enter; break;
      case 0x08:            key = back; break;
      case 0x1b:            key = esc; break;
      default:{
        //if(characterKey == )
        key = 0;
      }
    }
    if(key == 0 && characterKey >= ' ' && characterKey != KeyEvent.CHAR_UNDEFINED) {
      if(stateKeys == shift ) {
        return characterKey;
      } else {
        return stateKeys + characterKey;
      }
    } else {
      return stateKeys + key;
    }
  }
  
  
  
  public enum MouseAction{
    down, up, upMovedOutside, doubleClick
  };
  
  
  public static int convertMouseKey(int button, MouseAction action, int stateMask){
    final int keyMouse;
    switch(action){
      case down:{
        switch(button){ 
          case 1: keyMouse = KeyCode.mouse1Down; break; 
          case 3: keyMouse = KeyCode.mouse2Down; break;
          case 2: keyMouse = KeyCode.mouse3Down; break;
          default: keyMouse = KeyCode.mouse3Down; break;  //other key
        }

      } break;
      case up:{
        switch(button){ 
          case   1: keyMouse = KeyCode.mouse1Up; break; 
          case   3: keyMouse = KeyCode.mouse2Up; break;
          case   2: keyMouse = KeyCode.mouse3Up; break;
          default: keyMouse = KeyCode.mouse3Up; break;  //other key
        }
        
      } break;
      case upMovedOutside:{
        switch(button){ 
          case   1: keyMouse = KeyCode.mouse1UpMoved; break; 
          case   3: keyMouse = KeyCode.mouse2UpMoved; break;
          case   2: keyMouse = KeyCode.mouse3Up; break;
          default: keyMouse = KeyCode.mouse3Up; break;  //other key
        }
        
      } break;
      case doubleClick:{
        switch(button){ 
          case 1: keyMouse = KeyCode.mouse1Double; break; 
          case 3: keyMouse = KeyCode.mouse2Double; break;
          case 2: keyMouse = KeyCode.mouse3Down; break;
          default: keyMouse = KeyCode.mouse3Down; break;  //other key
        }
        
      } break;
      default: keyMouse = 0;  //unused, only because compiling error
    }
    final int keyCode = AwtGralKey.convertFromAwt(keyMouse, stateMask, '\0');
    return keyCode;
  }
  
  

}
