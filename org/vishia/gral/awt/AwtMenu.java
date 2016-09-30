package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Composite;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralUserAction;

public class AwtMenu extends GralMenu._GraphicImpl
{
  
  private final Menu menuAwt;
  
  private final MenuBar menuBar;

  public AwtMenu(GralWidget widgg, Component parent, GralMng mng)
  {
    new GralMenu().super(widgg);
    menuBar = null;
    menuAwt = new PopupMenu("test");
    MenuItem item = new MenuItem("Test1");
    menuAwt.add(item);
    MenuItem item2 = new MenuItem("Test2");
    menuAwt.add(item2);
  }

  public AwtMenu(GralWidget widgg, Frame window, GralMng mng)
  {
    new GralMenu().super(widgg);
    menuAwt = null;
    menuBar = new MenuBar();
    window.setMenuBar(menuBar);   
  }

  
    
  /**Creates the implementation for a menu node or entry.
   * @param oParentMenu return value of {@link #getMenuImpl()} or the {@link GralMenu.MenuEntry#menuImpl}, that is a menu node. 
   * @param gralEntry The entry in the menu tree
   */
  @Override public void _implMenuItem(Object oParentMenu, GralMenu.MenuEntry gralEntry)
  { assert(gralEntry.menuImpl ==null);
    Menu parentMenu = (Menu) oParentMenu;
    //TODO
  }


  

  @Override
  public void setVisible(){
    //menuAwt.setVisible(true);
  }
  
  
  @Override public Menu getMenuImpl(){ return menuAwt; }

  
  
}
