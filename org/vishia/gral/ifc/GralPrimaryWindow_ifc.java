package org.vishia.gral.ifc;

import java.io.File;

import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.mainCmd.MainCmd_ifc;


/**This interface represents a basic main window with title bar in the gral (graphical adaption layer).
 *
 * @author Hartmut Schorrig
 *
 */
public interface GralPrimaryWindow_ifc //extends GralWindow_setifc, GralWindow_getifc //, GralWindowMng_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2012-03-10 Hartmut chg Now the both GralWindow_set- and -getifc are base,
   *   The GralWindow_ifc is independent of this.
   * <li>2011-06-00 Hartmut created
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
  public static final int version = 20120303;

  /**Sets the title and the pixel size for the whole window. */
  //void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize);
  
  
  /**Activates the menu bar with the standard entries, especially the file open entry.
   * Note: This method should only be called in the same thread where the graphic runs.
   * 
   * @param openStandardDirectory If given, then this is the default directory for file open.
   * @param actionFile Action on file menu entries.
   */
  void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile);
  
  
  
  MainCmd_ifc getMainCmd();
  
  
  /**Returns the Frame class of the underlying graphic.
   * SWT: Shell, swing: TODO
   * @return
   */
  Object getitsGraphicFrame();
  
  



}
