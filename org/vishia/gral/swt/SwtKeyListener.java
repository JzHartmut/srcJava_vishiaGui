package org.vishia.gral.swt;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralKeyListener;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

/**A common key listener implementation for SWT. It is applied to all widgets.
 * Derived forms exists for special SWT-widgets.
 * This class is instanciated one time in the {@link SwtWidgetMng}.
 * @author Hartmut Schorrig
 *
 */
public class SwtKeyListener extends GralKeyListener
{
  
  /**Version and history
   * <ul>
   * <li>2011-12-03 Hartmut created. Any widget may have the same key listener.
   * </ul>
   * 
   */
  public final static int version = 0x20111203;
  
  final SwtWidgetMng swtMng;
  
  final KeyListener swtListener;
  
  public SwtKeyListener(SwtWidgetMng swtMng)
  {
    super();
    this.swtMng = swtMng;
    swtListener = new TextKeyListener();
  }

  /**This method can be overridden by specialized classes of this.
   * This method is called firstly. 
   * Note if a special key should be handled in the {@link GralWidget#getActionChange()}
   * then this special handling should be programmed in the derived class.
   * If this method returns true, the key is not applied to the action and not applied to the common handling.
   * @return true if the key is processed.
   * 
   * 
   * */
  protected boolean specialKeysOfWidgetType(int key, GralWidget widgg){ return false; }
  


  /**A Widget can be completed with this key listener. On all keys 
   * the {@link GralUserAction} given in the {@link GralWidget#getActionChange()} is called
   * <ul>
   * </ul> 
   * If the method returns false, the central key action given in {@link GralWidgetMng#getRegisteredUserAction(String)}
   * for "keyAction" is tried to get and then invoked with cmd = "key" and the key code in params[0].
   * This central keyAction may be used for application centralized keys without association to the table itself.
   */
  class TextKeyListener implements KeyListener
  {
        
    
    public TextKeyListener()
    {
    }

    @Override
    public void keyPressed(KeyEvent keyEv)
    {
      final GralWidget widgetDescr;
      final GralUserAction action;
      //System.out.println("" + keyEv.character + Integer.toHexString(keyEv.keyCode));
      
      final Object source = keyEv.getSource();
      final Control swtControl;
      if(source instanceof Control){
        swtControl = ((Control)source);
        Object oData = swtControl.getData();
        if(oData instanceof GralWidget){
          widgetDescr = (GralWidget)oData;
          action = widgetDescr.getActionChange();
        } else { widgetDescr = null; action = null; }
      } else { 
        widgetDescr = null; action = null;
        swtControl = null;
      }
      boolean actionDone = false;
      if((keyEv.keyCode & 0xffff) !=0){
        final int keyCode = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
        try{
          actionDone = specialKeysOfWidgetType(keyCode, widgetDescr);
          if(!actionDone && action !=null){ 
              actionDone = action.userActionGui(keyCode, widgetDescr);
          } //if(table.)
          if(!actionDone){
            GralUserAction mainKeyAction = widgetDescr.itsMng.getRegisteredUserAction("KeyAction");
            if(mainKeyAction !=null){
              int gralKey = SwtGralKey.convertFromSwt(keyEv.keyCode, keyEv.stateMask);
              //old form called because compatibility, if new for with int-parameter returns false.
              if(!mainKeyAction.userActionGui(gralKey, widgetDescr)){
                mainKeyAction.userActionGui("key", widgetDescr, new Integer(gralKey));
              }
            }
          }
        } catch(Exception exc){
          swtMng.log.sendMsg(0, "KeyListener - UsercallException; key=%8.8x; %s;", keyCode, exc.getLocalizedMessage());
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
    
  };
  
  

  

}
