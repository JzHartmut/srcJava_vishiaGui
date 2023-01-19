package org.vishia.gral.impl_ifc;

import org.vishia.gral.base.GralMenu;

/**This interface should be implemented by an associated (usual inner non static) class of the widget implementation. 
 * It allows access to the implementation widgets especially from the implementation level
 * but organized as common interface.
 * @author hartmut
 *
 */
public interface GralWidgetImpl_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2022-08 new {@link #getImplWidget()} used in the implementation level. 
   * <li>2016-11-13 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String sVersion = "2022-09-04";

  void specifyContextMenu(GralMenu menu);

  /**This returns the graphic element of the implementation.
   * The usage of this interface should be done also in the implementation level,
   * so a cast to the expected graphic element can be done.
   * @return either a basic graphic widget or the panel implementation on comprehensive widgets.
   */
  Object getImplWidget();
  
}
