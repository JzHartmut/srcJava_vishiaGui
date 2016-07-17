package org.vishia.gral.ifc;

import java.io.Closeable;

import org.vishia.gral.base.GralMng;
import org.vishia.msgDispatch.LogMessage;



/**This interface plugs an user application to the basic GUI application. 
 * <br><br>
 * <br>Superinterface Closeable</b>: On close of the application some threads should be finished
 * or channels should be closed. 
 * */ 
public interface GralPlugUser_ifc extends Closeable
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

  //void init(LogMessage log);
  
  /**This is the first routine that will be called for the plugin. 
   * Note that the constructor is parameterless because the plugin may be instantiated by reflection access
   * This routine will be called by {@link org.vishia.gral.area9.GuiCfg#userInit()} or any of its derivation 
   * in the constructor of {@link org.vishia.gral.area9.GuiCfg#GuiCfg(org.vishia.gral.area9.GuiCallingArgs, org.vishia.gral.area9.GralArea9MainCmd, GralPlugUser2Gral_ifc)} 
   * in the main thread, not in the graphic thread.
   * @param plugUser2Gui Access from the plugin to the Gui main implementation. It is offer to use.
   * @param gralMng
   * @param log
   */
  void init(GralPlugUser2Gral_ifc plugUser2Gui, GralMng gralMng, LogMessage log);

  
  void registerMethods(org.vishia.gral.ifc.GralMngBuild_ifc guiMng);
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);

  
  /**This routine can be implemented to add some graphic elements to the gui, including special windows.
   * @param gralMng
   */
  void initGui(GralMng gralMng);
  
  /**This routine can be implemented to add some specific menu entries.
   * @param wind The main window access
   */
  void addGuiMenu(GralWindow_ifc wind);
  

}
