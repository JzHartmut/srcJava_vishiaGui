package org.vishia.gral.base;

import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public abstract class GralMenu //extends GralWidget
{

  
  /**This class wraps a menu entry of the implementation. It knows all sub menu entries
   * in a implementation-independent way. So searching entries with given name is possible.
   */
  protected static class MenuEntry
  {
    public String name;
    
    /**If it is a superior menu item, the menu below. Else null. */
    public Object menuImpl;
    
    /**All menu entries of this menu item. */
    public Map<String, MenuEntry> subMenu;
    
    public MenuEntry(){
      
    }
  }
  
  protected final GralWidgetMng gralMng;
  
  protected Map<String, MenuEntry> menus = new TreeMap<String, MenuEntry>();
  

  
  public GralMenu(String sName, GralWidgetMng mng)
  {
    //super(sName, 'M', mng);
    this.gralMng = mng;
  }




  /**Adds any menu item
   * @param name name of the menu, it is used as widget name.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  public abstract void addMenuItemGthread(String name, String sMenuPath, GralUserAction action);
  
  public abstract void setVisible();
  
  public abstract Object getMenuImpl();
  
}
