package org.vishia.gral.base;

import java.util.Queue;

import org.vishia.gral.ifc.GralWidget;


/**This interface should be implemented by any user class to call user actions when a panel is activated.
 * The activation of several panels is done initially, when the Panel becomes visible, or if a tab
 * is activated in a tab-view.
 * 
 * @author e09srrh0
 *
 */
public interface GralPanelActivated_ifc
{

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

  
  /**If a panel is actived, the user will be notified. 
	 * @param widgets Information about all widgets in this panel, which should be updated 
	 *        with correct values for example with data of a running process.
	 * */
	void panelActivatedGui(Queue<GralWidget> widgets);
	
}
