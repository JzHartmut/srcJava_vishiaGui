package org.vishia.gral.swt;

import org.eclipse.swt.SWT;
import org.vishia.util.KeyCode;

/**Adaption from SWT-KeyCodes to universal vishia key codes.
 * @author Hartmut Schorrig
 *
 */
public class SwtGralKey extends KeyCode
{
  
  /**This class isn't instantiated. Only the static method and the static definitions of KeyCode
   * are used.  */
  private SwtGralKey()
  { super(0);
  }
  
  public static int convertFromSwt(int keyCode, int stateMask)
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
      case SWT.F6:          key = 6; break;
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
      case 0x0d:            key = enter; break;
      default: key = keyCode;
    }
    return stateKeys + key;
  }
  
  
}
