package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;

/**This class defines the methods which are able to invoke from a widget change request. 
 * It is used only internally, it should not be invoked by a user's application. 
 * The class is public because it should be known by the graphical implementation level,
 * which may be resident in another package. The methods must not invoke in another thread than
 * the graphic thread. The implementation of this interface will be done in the graphical implementation layer.
 * <br><br>
 * The ObjectModelDiagram may shown the relations:
 * <img src="../../../../img/GralWidgetGthreadSet_ifc_gral.png"><br>
 * This interface should be implemented in any widget deployment. It is provide by the 
 * {@link org.vishia.gral.ifc.GralWidget_ifc#getGthreadSetifc()} method.
 * It is used in the overridden method 
 * {@link GralWidgetMng#setInfoGthread(GralWidget_ifc, int, int, Object, Object)} of the graphical
 * implementation layer. Any change request is determined by its {@link #cmd} code, which is defined
 * in {@link org.vishia.gral.ifc.GralPanelMngWorking_ifc}. A switch-case calls the proper methods
 * from this interface.
 * <br> 
 * @author Hartmut Schorrig
 *
 */
public interface GralWidgetGthreadSet_ifc {
  
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

  
  void clearGthread();
  
  void redrawGthread();
  
  void setTextGthread(String text, Object data);
  
  void insertGthread(int pos, Object visibleInfo, Object data);
  
  void setBackGroundColorGthread(GralColor color);

  void setForeGroundColorGthread(GralColor color);
}
