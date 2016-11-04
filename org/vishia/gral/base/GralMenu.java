package org.vishia.gral.base;

import java.util.Map;

import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.Assert;
import org.vishia.util.IndexMultiTable;

/**Super class of Menu root wrappers of the graphic implementation layer independent of the implementation graphic.
 * This class is the super class either for the menu bar of a window
 * or for the context menu for any widget.
 * <br><br>
 * It refers the window or the widget. The graphical implementation layer associates
 * an event listener to any menu item created with {@link #addMenuItem(String, String, GralUserAction)}.
 * That event listener invokes the {@link GralUserAction} given as parameter.  
 * <ul>
 * <li>To add a menu bar to a window call {@link GralWindow#addMenuBarItemGThread(String, String, GralUserAction)}.
 * <li>To get or create a context menu to a widget call {@link GralWidget#getContextMenu()}
 * <li>To add a menu item to the context menu or any menu item call
 *   {@link #addMenuItem(String, String, GralUserAction)}.
 * </ul>
 * One should call this methods only in the graphic thread in the initalizing phase.
 * <br><br>  
 * The basic idea for menus is: A path is used instead a tree of menus.
 * To add any new menu item to a window's pull-down menu bar, use
 * {@link GralWindow#addMenuBarItemGThread(String, String, GralUserAction)}.
 * You needn't have knowledge about the tree structure of the menu.
 * The String sMenuPath should consist of the parts of the menu tree,
 * for example "&File/&Check/rest&Ore". For this example a menu bar entry "File" is created
 * or the yet existing is used. Then the sub menu entry "Check" is created or the existing is used.
 * There the entry "restOre" is created with 'O' as hot-key.
 * 
 * 
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralMenu //extends GralWidget
{

  /**Version, history and license.
   * <ul>
   * <li>2014-06-23 Hartmut redesign: Now the GralMenu is an implementation-independent class. 
   *   It contains references to the implementation menu instances and supports creation of them if the graphic implementation is present.
   *   {@link _GraphicImpl} is now the base class for the {@link org.vishia.gral.swt.SwtMenu}.
   * <li>2014-06-23 Hartmut new: Now the {@link #widgg} is stored here. It is used for all sub menus
   *  to refer it for calling {@link GralUserAction#exec(int, org.vishia.gral.ifc.GralWidget_ifc, Object...)}.
   *  The {@link #addMenuItem(String, String, GralUserAction)} is now deprecated, the String is not used.
   *  But only that method creates an instance of {@link org.vishia.gral.swt.SwtWidgetMenu} which is deprecated too
   *  and only used to held a reference to the GralWidget. 
   *  The {@link #addMenuItem(String, GralUserAction)} is the new one to use. 
   *   
   * <li>2012-03-17 Hartmut new: {@link #addMenuItem(String, String, GralUserAction)} returns now
   *   a {@link GralWidget}. It is necessary to add some information to the menu-widget, which can be used
   *   if the {@link GralUserAction#userActionGui(int, GralWidget, Object...)} for this menu is called.
   *   The second param of this method is that menu-GralWidget.
   * <li>2011-11-00 Hartmut created, menus are present not only in the main window. Context menu etc.
   * <ul>
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
  public final static int version = 20120317;
  
  /**This class wraps a menu entry of the implementation. It knows all sub menu entries
   * in a implementation-independent way. So searching entries with given name is possible.
   */
  public static class MenuEntry
  {
    public String name;
    
    public char cAccelerator;
    
    /**If it is a superior menu item, the menu below. Else null. */
    public Object menuImpl;
    
    /**All menu entries of this menu item. */
    public Map<String, MenuEntry> subMenu;
    
    public GralWidget widgg;
    
    public GralUserAction action;
    
    public MenuEntry(){}
  }
  
  protected Map<String, MenuEntry> menus = new IndexMultiTable<String, MenuEntry>(IndexMultiTable.providerString);
  
  
  _GraphicImpl _impl;
  

  public GralMenu() {
  }
  
  
  /**Adds any menu item
   * @param name name of the menu, it is used as widget name.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  public final void addMenuItem(String name, String sMenuPath, GralUserAction action) {
    addMenuItem(null, name, sMenuPath, action);
    //if(_impl !=null) { _impl.addMenuItemGthread(name, sMenuPath, action); } 
  }
  
  
  
  /**Adds with a given widget.
   * Note the widget is in difference with the {@link #widgg} of the constructor. 
   * Check what is happen. Therefore:
   * @param widggMenu the widget can be used to add any {@link GralWidget#setContentInfo(Object)} etc. 
   *   It is provided in the action method.
   * @param name
   * @param sMenuPath
   * @param action
   */
  public final void addMenuItem(GralWidget widggP, String nameWidgg, String sMenuPath, GralUserAction action){
    String[] names = sMenuPath.split("/");
    Map<String, GralMenu.MenuEntry> menustore = this.menus;
    int ii;
    MenuEntry menuEntry = null;
    for(ii=0; ii<names.length; ++ii){
      //search all pre-menu entries before /. It may be existing, otherwise create it.
      String name = names[ii];
      final char cAccelerator;
      final int posAccelerator = name.indexOf('?');
      if(posAccelerator >=0){
        cAccelerator = Character.toUpperCase(name.charAt(posAccelerator));
        name = name.replace("&", "");
      } else {
        cAccelerator = 0;
      }
      menuEntry = menustore.get(name);
      if(menuEntry == null){
        //create it.
        menuEntry = new MenuEntry();
        menustore.put(name, menuEntry);
        menuEntry.name = name;
        menuEntry.cAccelerator = cAccelerator;
        if(ii < names.length -1) {
          menuEntry.subMenu = new IndexMultiTable<String, MenuEntry>(IndexMultiTable.providerString);
        }
      }
      menustore = menuEntry.subMenu;
    }
    assert(menuEntry !=null);  //null if sMenuPath will be empty.
    menuEntry.action = action;  //store to the last one.
    menuEntry.widgg = widggP;
    if(_impl !=null) { _impl._implMenu(); } 
    
  }
  
  
  
  
  
  /**Adds any menu item.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  public final void addMenuItem(String sMenuPath, GralUserAction gralAction){
    //if(_impl !=null) { _impl.addMenuItemGthread(sMenuPath, gralAction); } 
    addMenuItem(null, null, sMenuPath, gralAction);;
  }
  
  public final void setVisible(){
    if(_impl !=null) { _impl.setVisible(); } 
  }
  
  
  public boolean hasImplementation(){ return _impl !=null; }
  
  /**Returns the implementation instance for the menu. */
  public final Object getMenuImpl(){
    if(_impl !=null) { return _impl.getMenuImpl(); }
    else return null; 
  }





  public abstract class _GraphicImpl
  {

    protected final GralWidget widgg;
    

  
  /**Creates a new menu wrapper. This is called as super(widgg, mng) in the derived class. 
   * @param widgg The gral widget which is the parent. If it is a context menu it is that
   *   widget where {@link GralWidget#getContextMenu()} was called. 
   *   If it is is a menu bar, the window is used.
   * @param mng The mng
   */
    protected _GraphicImpl(GralWidget widgg)
  {
      this.widgg = widgg;
      GralMenu.this._impl = this;
  }



    /**Creates all necessary new implementation instances for this GralMenu. */
    public final void _implMenu() {
      _implMenu(getMenuImpl(), menus);
    }
    
    
    private final void _implMenu(Object parentMenu, Map<String, MenuEntry> menusP) {
      for(Map.Entry<String, MenuEntry> e: menusP.entrySet()) {
        MenuEntry child = e.getValue();
        if(child.menuImpl == null) {
          _implMenuItem(parentMenu, child);
        }
        if(child.subMenu !=null) {
          _implMenu(child.menuImpl, child.subMenu);
        }
      }
    }
    
    
    
    /**This method should be implemented in the implementation layer.
     * @param oParentMenu 
     * @param gralEntry
     */
    protected abstract void _implMenuItem(Object oParentMenu, GralMenu.MenuEntry gralEntry);

  /**Adds any menu item
   * @param name name of the menu, it is used as widget name.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  //public abstract void addMenuItemGthread(String name, String sMenuPath, GralUserAction action);
  
  
  /**Adds with a given widget.
   * Note the widget is in difference with the {@link #widgg} of the constructor. 
   * Check what is happen. Therefore:
   * @deprecated The {@link GralWidget#getContextMenu()} creates a menu for the graphic implementation layer widget
   *   and assigns it to the widget as GralMenu. The GralMenu has the association {@link #widgg} to the widget already.
   *   In the adequate case {@link GralWindow#getMenuBar()} adds the menu to the window and stores the window's reference 
   *   in {@link #widgg}. That is proper to use. This method was create before the {@link #widgg} are set
   *   in this constructor.  
   * @param widggMenu the widget can be used to add any {@link GralWidget#setContentInfo(Object)} etc. 
   *   It is provided in the action method.
   * @param name
   * @param sMenuPath
   * @param action
   */
  @Deprecated
  //public abstract void addMenuItemGthread(GralWidget widggMenu, String name, String sMenuPath, GralUserAction action);
  
  
  
  
  
  /**Adds any menu item.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  //public abstract void addMenuItemGthread(String sMenuPath, GralUserAction gralAction);
  
  public abstract void setVisible();
  
  /**Returns the implementation instance for the menu. */
  public abstract Object getMenuImpl();
  }
}
