package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.ifc.GralFactory_ifc;

/**The standard command-line-arguments for a graphic application are stored in an extra class. 
 * This class should be the base class for users command line argument storage.
 * In Generally the separation of command line argument helps to invoke the functionality with different calls, 
 * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT.
 * This class should be the super class of an derived application's CallingArguments class. 
 */
public class GuiCallingArgs
{
  /**Version, history and licence
   * <ul>
   * <li>2013-11-22 Hartmut new {@link #sTitle}
   * <li>2012-04-22 Hartmut chg {@link #sizeShow} instead sSize, new {@link #xLeftPixelWindow} etc.
   * <li>2011-06-00 Hartmut creation, commonly arguments for each GUI application.
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
  public final static int version = 20131122;

  
  
  /**String for title bar. */
  String sTitle;
  
  /**The graphic base factory can be detected from command line arguments
   * or set directly from the calling level. */
  GralFactory_ifc graphicFactory;
  
  /**Name of the config-file for the Gui-appearance. */
  //String sFileGui;
  
  /**The configuration file. It is created while parsing arguments.
   * The file is opened and closed while the configuration is used to build the GUI.
   * The file is used to write on menu-save action.
   */
  protected File fileGuiCfg;
  
  public String sPathZbnf = "GUI";
  
  /**The time zone to present all time informations. */
  public String sTimeZone = "GMT";
  
  /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
  public char sizeShow = 'C';
  
  public int xLeftPixelWindow = 50, yTopPixelWindow = 50, dxPixelWindow = 930, dyPixelWindow = 520;
  
  /**The own ipc-address for Interprocess-Communication with the target.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  public String sOwnIpcAddr;
  
  /**A class which is used as plugin for user specifies. It is of interface {@link PlugUser_ifc}. */
  String sPluginClass;
  
  
  /**The own ipc-address for inspector-Communication with this application.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  String sInspectorOwnPort;

}
