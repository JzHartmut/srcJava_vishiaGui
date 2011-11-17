package org.vishia.gral.ifc;

import org.vishia.gral.base.GralWidgetMng;
import org.vishia.msgDispatch.LogMessage;



/**This interface plugs an user application to the basic GUI application. */ 
public interface GralPlugUser_ifc
{

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
  void init(GralPlugUser2Gral_ifc plugUser2Gui, GralWidgetMng gralMng, LogMessage log);

  
  void registerMethods(org.vishia.gral.ifc.GralGridBuild_ifc guiMng);
  
  /**This method is called if the view is changed. 
   * @param sTitle title of a window or panel.
   * @param cmd any command. TODO what.
   */
  void changedView(String sTitle, int cmd);

  
  /**This routine can be implemented to add some graphic elements to the gui, including special windows.
   * @param gralMng
   */
  void initGui(GralWidgetMng gralMng);
  
  /**This routine can be implemented to add some specific menu entries.
   * @param wind The main window access
   */
  void addGuiMenu(GralPrimaryWindow_ifc wind);
  

}
