package org.vishia.gral.base;

/**The main key listener.
 * <b>Processing of keys</b>: (2012-06-17)<br>
 * Key events are produced by the underlying graphic operation system. Usual they are applied to the widgets of the
 * operation system graphic. For example, key left and right are used to navigate in a text field.
 * <br><br>
 * Any widget can have a key listener. Then the keys are applied to it, the listener determines what to do with keys.
 * But if main keys should be used, keys which have the same reaction independent of the current focus of widgets,
 * all widgets have to be catch this keys in there key listener and forwarding to the main handler.
 * That is not optimal. Some widgets would not do so, and the keys does not take an effect fretfully. The user should
 * implement a key listener to all widgets.
 * <br><br>
 * It is better to have a main key listerner which is invoked in any case. That is realized in 
 *   {@link GralWidgetMng#setMainKeyAction(org.vishia.gral.ifc.GralUserAction)} and the adequate SWT-Implementation.
 * 
 * @author Hartmut
 *
 */
public class GralKeyListener
{
  /**Version and history
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
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20111203;
 
}
