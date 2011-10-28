package org.vishia.gral.ifc;

import java.io.File;

import org.vishia.mainCmd.MainCmd_ifc;


/**This interface represents a basic main window with title bar in the gral (graphical adaption layer).
 *
 * @author Hartmut Schorrig
 *
 */
public interface GralPrimaryWindow_ifc extends GralWindow_ifc, GralWindowMng_ifc
{
  /**Sets the title and the pixel size for the whole window. */
  //void buildMainWindow(String sTitle, int left, int top, int xSize, int ySize);
  
  
  /**Activates the menu bar with the standard entries, especially the file open entry.
   * Note: This method should only be called in the same thread where the graphic runs.
   * 
   * @param openStandardDirectory If given, then this is the default directory for file open.
   * @param actionFile Action on file menu entries.
   */
  void setStandardMenusGThread(File openStandardDirectory, GralUserAction actionFile);
  
  
  /**Adds any menu item
   * @param name Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   * @param action called on menu activation.
   */
  void addMenuItemGThread(String name, GralUserAction action);
  
  
  
  MainCmd_ifc getMainCmd();
  
  
  /**Returns the Frame class of the underlying graphic.
   * SWT: Shell, swing: TODO
   * @return
   */
  Object getitsGraphicFrame();
  
  



}
