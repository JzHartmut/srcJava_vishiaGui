package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

/**This class should only be used for the implementation of the graphic adapter.
 * It helps to associate a GralWidget to any implementation widget.
 * @author hartmut Schorrig
 *
 */
public interface GetGralWidget_ifc {

  /**Version, history and licence
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
  public final static int version = 0x20120303;

  
  /**Returns the GralWigdet from the data of the given implementation graphic component. 
   * In Any graphic implementation a implementation widget may refer untyped user data.
   * This data may set to the associated instance of GralWidget usually. 
   * But sometimes other data should be referred and the GralWidget is existent one time for more
   * as one implementation widgets. The this interface helps. 
   * */
  GralWidget getGralWidget();
}
