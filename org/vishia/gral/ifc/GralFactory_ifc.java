package org.vishia.gral.ifc;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.area9.GralArea9_ifc;
import org.vishia.gral.base.GralWindow;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;

/**This is the interface to a factory class which allows usage of any graphical base system
 * such as Swing, SWT or other for the area9-gui and for the grid panel manager
 * @author Hartmut Schorrig
 *
 */
public interface GralFactory_ifc
{

  /**Version, history and license.
   * <ul>
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

  GralWindow createWindow(LogMessage log, String sTitle, char sizeShow, int left, int top, int xSize, int ySize);

  void createWindow(GralWindow gralWindow, char sizeShow, int left, int top, int xSize, int ySize);
  
  /*
  GralGridProperties createProperties(char sizePixel);
  
  GralWidgetMng createPanelMng(GralWidgetMng parent, int width, int height
  , GralGridProperties propertiesGui
  , VariableContainer_ifc variableContainer, LogMessage log);
  */  
}
