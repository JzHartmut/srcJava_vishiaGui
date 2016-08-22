package org.vishia.gral.base;

import org.vishia.gral.ifc.GralRectangle;


public interface GralWidgetHelper {

  /**Version, history and license.
   * <ul>
   * <li>2012-07-13 Hartmut removed: getAbsoluteBoundsOf() This is the same as {@link GralPanelContent#getPixelPositionSize()}.
   * <li>2012-06-00 Hartmut created. The class {@link org.vishia.gral.swt.SwtWidgetHelper} does exist with static method yet,
   *   but it should contain common methods. Adequate classes for all implementations may be needed. 
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
  public static final int version = 20120713;

  void setMng(GralWidgetMng mng);
  
  //GralRectangle getAbsoluteBoundsOf(GralWidget widg);
    
  
  boolean showContextMenu(GralWidget widg);

}
