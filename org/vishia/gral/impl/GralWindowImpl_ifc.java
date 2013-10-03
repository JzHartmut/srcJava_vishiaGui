package org.vishia.gral.impl;

import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.ifc.GralUserAction;

public interface GralWindowImpl_ifc extends GralWidgImpl_ifc
{
  /**Adds a menu item
   * @param nameMenu Name of the menu item to search with TODO
   * @param sMenuPath textual path of the menu
   * @param gralAction
   */
  void addMenuItemGThread(String nameMenu, String sMenuPath, GralUserAction gralAction);
}
