package org.vishia.gral.base;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public abstract class GralMenu //extends GralWidget
{

  public abstract class Item
  {
  
  }
  
  
  
  
  public GralMenu(String sName, GralWidgetMng mng)
  {
    //super(sName, 'M', mng);
    // TODO Auto-generated constructor stub
  }




  /**Adds any menu item
   * @param name name of the menu, it is used as widget name.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  public abstract void addMenuItem(String nameWidg, String sMenuPath, GralUserAction action);
  
  public abstract void setVisible();
  
}
