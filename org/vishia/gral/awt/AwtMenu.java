package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Composite;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralUserAction;

public class AwtMenu extends GralMenu
{
  
  private final Menu menuAwt;
  
  

  public AwtMenu(String sName, Component parent, GralWidgetMng mng)
  {
    super(sName, mng);
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


  public void setVisible(){
    //menuAwt.setVisible(true);
  }
  
  
  @Override public Menu getMenuImpl(){ return menuAwt; }
  
  
  
}
