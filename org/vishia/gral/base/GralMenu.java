package org.vishia.gral.base;

import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public abstract class GralMenu //extends GralWidget
{

  /**Version, history and licence
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
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20120303;
  
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
