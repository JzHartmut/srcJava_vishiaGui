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
  
  String sPathZbnf = "GUI";
  
  /**The time zone to present all time informations. */
  protected String sTimeZone = "GMT";
  
  /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
  String sSize;
  
  /**The own ipc-address for Interprocess-Communication with the target.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  public String sOwnIpcAddr;
  
  /**A class which is used as plugin for user specifies. It is of interface {@link PlugUser_ifc}. */
  String sPluginClass;
  

}
