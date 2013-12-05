package org.vishia.gral.swt;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralKeySpecial_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;

/**A common key listener implementation for SWT. It is applied to all widgets.
 * Derived forms exists for special SWT-widgets.
 * This class is instanciated one time in the {@link SwtMng}.
 * A Widget can be completed with this key listener. On all keys 
   * the {@link GralUserAction} given in the {@link GralWidget#getActionChange()} is called
   * <ul>
   * </ul> 
   * If the method returns false, the central key action given in {@link GralMng#getRegisteredUserAction(String)}
   * for "keyAction" is tried to get and then invoked with cmd = "key" and the key code in params[0].
   * This central keyAction may be used for application centralized keys without association to the table itself.
 * @see GralKeyListener
 * 
 * @author Hartmut Schorrig
 *
 */
public class SwtKeyListener implements GralKeySpecial_ifc, KeyListener// extends GralKeyListener
{
  
  /**Version and history
   * <ul>
   * <li>2011-12-03 Hartmut created. Any widget may have the same key listener.
   * </ul>
   * 
   */
  //@SuppressWarnings("hiding")
  public final static int version = 20120630;
  
  //final SwtMng swtMng;
  
  private final GralKeyListener keyAction;
  //final KeyListener swtListener;
  
  public SwtKeyListener(GralKeyListener keyAction)
  {
    this.keyAction = keyAction;
  }

  @Override
  public void keyPressed(KeyEvent keyEv)
  {
    final GralWidget widgetDescr;
    //System.out.println("" + keyEv.character + Integer.toHexString(keyEv.keyCode));
    
    final Object source = keyEv.getSource();
    final Control swtControl;
    if(source instanceof Control){
      swtControl = ((Control)source);
      Object oData = swtControl.getData();
      if(oData instanceof GralWidget){
        widgetDescr = (GralWidget)oData;
      } else { widgetDescr = null;  }
    } else { 
      widgetDescr = null; 
      swtControl = null;
    }
    if((keyEv.keyCode & 0xffff) !=0){
      final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
      if(! specialKeysOfWidgetType(keyCode, widgetDescr, swtControl)){
        keyAction.keyPressed(keyCode, widgetDescr, swtControl);
      }
    }
    if(swtControl !=null){
      Control parent = swtControl.getParent();
      if(parent !=null){
        //KeyListener parentListener = parent.getListener(SWT.KEY_MASK);
        //parent.
      }
    }
  }

  @Override
  public void keyReleased(KeyEvent arg0)
  {
    //basicListener.keyReleased(arg0);
    
  }
  
  @Override public boolean specialKeysOfWidgetType(int key, GralWidget widgg, Object widgImpl){ return false; }


}
