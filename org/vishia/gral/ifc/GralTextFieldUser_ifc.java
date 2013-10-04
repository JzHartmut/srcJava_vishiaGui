package org.vishia.gral.ifc;

import java.util.Map;

import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralPanelContent;


/**This interface can be implemented by any user classes which are associated to 
 * a {@link org.vishia.gral.base.GralTextBox} or a {@link org.vishia.gral.base.GralTextField} calling 
 * @author Hartmut Schorrig
 *
 */
public interface GralTextFieldUser_ifc {
  
  
  /**Version, history and license:
   * <ul>
   * <li>2012-04-15 Hartmut created: A text box may have some special key features. 
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
  public final static int version = 20120415;


  boolean userKey(int keyCode, String content, int cursorPos, int selectStart, int selectEnd);
  
}
