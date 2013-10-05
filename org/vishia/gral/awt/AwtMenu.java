package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Composite;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralUserAction;

public class AwtMenu extends GralMenu
{
  
  private final Menu menuAwt;
  
  

  public AwtMenu(GralWidget widgg, Component parent, GralMng mng)
  {
    super(widgg, mng);
    menuAwt = new PopupMenu("test");
    MenuItem item = new MenuItem("Test1");
    menuAwt.add(item);
    MenuItem item2 = new MenuItem("Test2");
    menuAwt.add(item2);
    
  }

  @Override public void addMenuItemGthread(String nameWidg, String sMenuPath, GralUserAction action)
  {
    // TODO Auto-generated method stub
  }
  
  @Override public void addMenuItemGthread(GralWidget widggMenu, String nameWidg, String sMenuPath, GralUserAction gralAction)
  {
    // TODO Auto-generated method stub
    return;
  }


  @Override public void addMenuItemGthread(String sMenuPath,
      GralUserAction gralAction)
  {
    // TODO Auto-generated method stub
    
  }
  

  @Override
  public void setVisible(){
    //menuAwt.setVisible(true);
  }
  
  
  @Override public Menu getMenuImpl(){ return menuAwt; }

  
  
}
