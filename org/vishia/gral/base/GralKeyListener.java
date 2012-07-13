package org.vishia.gral.base;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

/**The main key listener.
 * <b>Processing of keys</b>: (2012-06-17)<br>
 * Key events are produced by the underlying graphic operation system. Usual they are applied to the widgets of the
 * operation system graphic. For example, key left and right are used to navigate in a text field.
 * <br><br>
 * On system graphic level any widget can have its key listener. Then the keys are applied to it, the listener determines 
 * what to do with keys. Usual the key is applied to the standard behavior of the widget after them. 
 * But the key event may be set to 'do not do it' in the key listener. Then the standard behavior of the key in that widget is prevent.
 * <br><br>
 * The action for some keys may be determined for a special widget or widget type in an graphic system independent way.
 * The {@link GralWidget#setActionChange(org.vishia.gral.ifc.GralUserAction)} method can be given for any widget.
 * The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be invoked with the {@link KeyCode} 
 * and the widget reference. But firstly the {@link #specialKeysOfWidgetType(int, GralWidget)} is called. 
 * This method may be overridden for the widget type. If it returns true, the key code is not applied to any user action,
 * it is a widget-special key. 
 * <br><br>
 * If that method returns false or a {@link GralWidget#getActionChange()} action is not given for this widget,
 * the key will be applied to the {@link GralMng#setMainKeyAction(org.vishia.gral.ifc.GralUserAction)}.
 * In that way a application-global usage of some keys is organized.
 * <br><br>
 * If any of the both widget specific or global {@link GralUserAction#userActionGui(int, GralWidget, Object...)} method 
 * returns true, the key is not used for the widget in the graphic system. In that kind some keys can be blocked
 * for standard behavior.
 * <br><br>
 * <b>class diagramm of usage for example in SWT: </b>
 * <pre>
 *                                                                 GralWidget <------------|
 *                                                                                         |
 *    SwtSpecialWidgetKeyListener ----|> SwtKeyListner ----|> swt.KeyListener <------- SwtWidget
 *    + specialKeysOfWidgetType()                  |
 *                                           |<----| 
 *                                           |
 *    GralSpecialWidgetKeyListener ---|> GralKeyListener
 *    +specialKeysOfWidgetType()             * keyPressed(key, gralWidg)
 *                                                * gralWidg.getActionChange().userActionGui(...)
 *                                                * mng.userMainKeyAction().userActionGui(...)
 *  
 *  </pre>
 *  (See {@link org.vishia.util.Docu_UML_simpleNotation})
 *  <br><br>
 *  The implementing graphic system aggregates an instance of this class.
 *  An underived instance is created in the {@link GralMng.InternalPublic#gralKeyListener}.
 *  This instance can be used as aggregation in all implementing system listeners, if the
 *  {@link #specialKeysOfWidgetType(int, GralWidget)} should not overridden.
 *  
 * @author Hartmut Schorrig
 *
 */
public class GralKeyListener implements GralKeySpecial_ifc
{
  /**Version, history and license
   * <ul>
   * <li>2011-12-03 Hartmut created. Any widget may have the same key listener. It is the baseclass of it
   *   and empty yet. 
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
   * 
   */
  public final static int version = 20120609;
 
  
  protected final GralMng mng;
  
  
  public GralKeyListener(GralMng mng){
    this.mng = mng;
  }
  
  
  
  public boolean keyPressed(int keyCode, GralWidget widgetDescr){
    boolean actionDone;
    final GralUserAction action = widgetDescr.getActionChange();
    final GralMng mng = widgetDescr.itsMng;
    try{
      actionDone = specialKeysOfWidgetType(keyCode, widgetDescr);
      if(!actionDone && action !=null){ 
          actionDone = action.userActionGui(keyCode, widgetDescr);
      } //if(table.)
      if(!actionDone && mng.userMainKeyAction() !=null){
        actionDone = mng.userMainKeyAction().userActionGui(keyCode, widgetDescr);
      }
      if(!actionDone){
        GralUserAction mainKeyAction = mng.getRegisteredUserAction("KeyAction");
        if(mainKeyAction !=null){
          //old form called because compatibility, if new for with int-parameter returns false.
          if(!mainKeyAction.userActionGui(keyCode, widgetDescr)){
            mainKeyAction.userActionGui("key", widgetDescr, new Integer(keyCode));
          }
        }
      }
    } catch(Exception exc){
      mng.log.sendMsg(0, "KeyListener - UsercallException; key=%8x; %s;", keyCode, exc.getLocalizedMessage());
    }

    return true;
  }
  
  
  @Override public boolean specialKeysOfWidgetType(int key, GralWidget widgg){ return false; }

  
}
