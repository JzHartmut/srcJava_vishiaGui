package org.vishia.gral.swt;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.base.GralKeySpecial_ifc;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;

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
    final GralWidget_ifc widgetDescr;
    //System.out.println("" + keyEv.character + Integer.toHexString(keyEv.keyCode));
    
    final Object source = keyEv.getSource();
    final Control swtControl;
    final GralWidgImplAccess_ifc wdgImpl; 
    if(source instanceof Control){
      swtControl = ((Control)source);
      Object oData = swtControl.getData();
      wdgImpl = oData instanceof GralWidgImplAccess_ifc ? (GralWidgImplAccess_ifc) oData : null;
      if(oData instanceof GralTextField.GraphicImplAccess){
        GralTextField.GraphicImplAccess widgi = (GralTextField.GraphicImplAccess) oData;
        widgetDescr = widgi.widgg;
      } else if(oData instanceof GralWidget_ifc){
        widgetDescr = (GralWidget_ifc)oData;
      } else { widgetDescr = null;  }
    } else { 
      widgetDescr = null; 
      swtControl = null;
      wdgImpl = null;
    }
    if((keyEv.keyCode & 0xffff) !=0){
      final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask, keyEv.character);
      if(! specialKeysOfWidgetType(keyCode, widgetDescr, swtControl)){
        this.keyAction.keyPressed(keyCode, widgetDescr, wdgImpl);  //should take the SWT impl, not the control itself.
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
  
  @Override public boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl){ return false; }


}
