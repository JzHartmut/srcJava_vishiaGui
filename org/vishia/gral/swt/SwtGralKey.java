package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.vishia.util.KeyCode;

/**Adaption from SWT-KeyCodes to universal vishia key codes.
 * @author Hartmut Schorrig
 *
 */
public class SwtGralKey extends KeyCode
{
  
  /**Version, history and license
   * <ul>
   * <li>2015-08-29 Hartmut chg: Mouse handling: mouse buttons: The usual right mouse button is the Button2. The middle button is number 3.
   * <li>2015-08-29 Hartmut chg: {@link #convertFromSwt(int, int, char)} now uses the character key information of the event too.
   *   Elsewhere it is not possible to use the keyboard layout from the operation system to detect keys with sh. 
   *   All keys with shift, which are character keys, are returned without {@link KeyCode#shift} designation now.
   * <li>2013-11-16 Hartmut new {@link #convertMouseKey(int, MouseAction, int)}
   * <li>2011-10-02 Hartmut created for Key handling in the GRAL. 
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
  private SwtGralKey()
  { super(0);
  }
  
  public static int convertFromSwt(int keyCode, int stateMask, char characterKey)
  {
    final int stateKeys, key;    
    if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.CONTROL + SWT.ALT + SWT.SHIFT){
      stateKeys = ctrl + alt + shift;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.ALT + SWT.CONTROL){
      stateKeys = ctrl + alt;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.CONTROL + SWT.SHIFT){
      stateKeys = ctrl + shift;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.ALT + SWT.SHIFT){
      stateKeys = alt + shift;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.CONTROL){
      stateKeys = ctrl;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.ALT){
      stateKeys = alt;
    } else if((stateMask & (SWT.CONTROL + SWT.ALT + SWT.SHIFT)) == SWT.SHIFT){
      stateKeys = shift;
    } else{
      stateKeys = 0;
    } 
    switch(keyCode){
      case SWT.ARROW_LEFT:  key = left; break;
      case SWT.ARROW_RIGHT: key = right; break;
      case SWT.ARROW_UP:    key = up; break;
      case SWT.ARROW_DOWN:  key = dn; break;
      case SWT.PAGE_UP:     key = pgup; break;
      case SWT.PAGE_DOWN:   key = pgdn; break;
      case SWT.F1:          key = F1; break;
      case SWT.F2:          key = F2; break;
      case SWT.F3:          key = F3; break;
      case SWT.F4:          key = F4; break;
      case SWT.F5:          key = F5; break;
      case SWT.F6:          key = F6; break;
      case SWT.F7:          key = F7; break;
      case SWT.F8:          key = F8; break;
      case SWT.F9:          key = F9; break;
      case SWT.F10:         key = F10; break;
      case SWT.F11:         key = F11; break;
      case SWT.F12:         key = F12; break;
      case SWT.HOME:        key = home; break;
      case SWT.END:         key = end; break;
      case SWT.INSERT:      key = ins; break;
      case SWT.DEL:         key = del; break;
      case SWT.TAB:         key = tab; break;
      case 0x0d:            key = enter; break;
      case 0x08:            key = back; break;
      case 0x1b:            key = esc; break;
      default:              key = keyCode;
    }
    if(stateKeys == shift && characterKey >= ' ') {
      return characterKey;
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
    final int keyCode = SwtGralKey.convertFromSwt(keyMouse, stateMask, '\0');
    return keyCode;
  }
  
  
}
