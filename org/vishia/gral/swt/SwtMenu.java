package org.vishia.gral.swt;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

/**This class describes either the menu bar of a window or a context menu of any widget.
 * It wraps the SWT Menu. All sub menus are contained in a tree parallel to the menu tree
 * of the implementation platform. 
 * @author Hartmut Schorrig
 *
 */
public class SwtMenu extends GralMenu
{
  

  
  /**This class wraps the {@link GralUserAction} for a menu action in Swt.
   */
  static class ActionUserMenuItem implements SelectionListener
  { 
    final GralUserAction action;
    
    public ActionUserMenuItem(GralUserAction action)
    { this.action = action;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    public void widgetSelected(SelectionEvent e)
    { Object oWidgSwt = e.getSource();
      final GralWidget widgg;
      if(oWidgSwt instanceof Widget){
        Widget widgSwt = (Widget)oWidgSwt;
        Object oGralWidg = widgSwt.getData();
        if(oGralWidg instanceof GralWidget){
          widgg = (GralWidget)oGralWidg;
        } else { widgg = null; }
      } else { widgg = null; }
      try{
        action.exec(KeyCode.menuEntered, widgg);
      } catch(Exception exc){
        System.err.printf("GralMenu - unexpected exception; %s\n", exc.getMessage());
      }
    }
  }
  

  
  protected final Shell window;

  
  /**It is the first level of menu hierarchy.
   * 
   */
  private final Menu menuSwt;
  
  

  /**Constructor of a context menu of any widget.
   * @param sName
   * @param parent
   * @param mng
   */
  protected SwtMenu(GralWidget widgg, Control parent, GralMng mng)
  {
    super(widgg, mng);
    this.window = parent.getShell();
    this.menuSwt = new Menu(parent);
    parent.setMenu(menuSwt);
  }


  /**Constructor for the menu bar of a window. It creates the window's menu if it isn't existing yet.
   * @param sName
   * @param window
   * @param mng
   */
  protected SwtMenu(GralWidget widgg, Shell window, GralMng mng)
  {
    super(widgg, mng);
    this.window = window;
    Menu menuWindow = window.getMenuBar();
    if(menuWindow == null){
      menuWindow = new Menu(window, SWT.BAR);
      window.setMenuBar(menuWindow);
    }
    this.menuSwt = menuWindow;
    
  }

  
  /**
   * @see org.vishia.gral.base.GralMenu#addMenuItemGthread(java.lang.String, java.lang.String, org.vishia.gral.ifc.GralUserAction)
   */
  @Override public void addMenuItemGthread(String nameWidg, String sMenuPath, GralUserAction gralAction)
  {
    SelectionListener action = new ActionUserMenuItem(gralAction);
    addMenuItemGthread(widgg, nameWidg, sMenuPath, action);
  }  
  
  
  /* (non-Javadoc)
   * @see org.vishia.gral.base.GralMenu#addMenuItemGthread(org.vishia.gral.base.GralWidget, java.lang.String, java.lang.String, org.vishia.gral.ifc.GralUserAction)
   */
  @Override public void addMenuItemGthread(GralWidget widggMenu, String nameWidg, String sMenuPath, GralUserAction gralAction)
  {
    SelectionListener action = new ActionUserMenuItem(gralAction);
    addMenuItemGthread(widggMenu, nameWidg, sMenuPath, action);
  }  
  
  
  @Override public void addMenuItemGthread(String sMenuPath, GralUserAction gralAction)
  {
    SelectionListener action = new ActionUserMenuItem(gralAction);
    addMenuItemGthread(widgg, null, sMenuPath, action);
  }  
  
  
  
  
  
  
  /**Adds an action with the menu path to this menu.
   * This method is package private. It is used by {@link SwtTable} to add a context menu with a special
   * SelectionListener.
   * @param nameWidg
   * @param sMenuPath
   * @param action
   */
  /*package private*/ private void addMenuItemGthread(GralWidget widggP, String nameWidg, 
      String sMenuPath, SelectionListener action)
  {
    String[] names = sMenuPath.split("/");
    Map<String, MenuEntry> menustore = menus;
    int ii;
    Menu parentMenu = menuSwt;  //set initial, it will be a child then
    for(ii=0; ii<names.length-1; ++ii){
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
      MenuEntry menuEntry = menustore.get(name);
      if(menuEntry == null){
        //create it.
        menuEntry = new MenuEntry();
        menustore.put(name, menuEntry);
        menuEntry.name = name;
        menuEntry.subMenu = new TreeMap<String, MenuEntry>();
        MenuItem item = new MenuItem(parentMenu, SWT.CASCADE);
        item.setText(name);
        if(cAccelerator !=0){
          item.setAccelerator(SWT.CONTROL | cAccelerator);
        }
        Menu menu = new Menu(window, SWT.DROP_DOWN);
        item.setMenu(menu);
        menuEntry.menuImpl = menu;
      }
      menustore = menuEntry.subMenu;
      parentMenu = (Menu)menuEntry.menuImpl;
    }
    String name = names[ii];
    MenuItem item = new MenuItem(parentMenu, SWT.None);
    if(widgg != null){
      //An associated GralWidget
      item.setData(widggP);
    } else {
      item.setData(widgg);
    }
    item.setText(name);
    //item.setAccelerator(SWT.CONTROL | 'S');
    item.addSelectionListener(action);
  }


  @Override
  public void setVisible(){
    menuSwt.setVisible(true);
  }
  
  
  @Override public Menu getMenuImpl(){ return menuSwt; }
  
  
}
