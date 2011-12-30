package org.vishia.gral.swt;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;

public class SwtMenu extends GralMenu
{
  
  public class Item extends GralMenu.Item
  {
    MenuItem menuItemSwt;
    
  }//class Item
  
  
  private final Menu menuSwt;
  
  

  public SwtMenu(String sName, Control parent, GralWidgetMng mng)
  {
    super(sName, mng);
    menuSwt = new Menu(parent);
    MenuItem item = new MenuItem(menuSwt,0);
    item.setText("Test");
    MenuItem item2 = new MenuItem(menuSwt,0);
    item.setText("Test2");
    
  }

  @Override
  public void addMenuItem(String nameWidg, String sMenuPath,
      GralUserAction action)
  {
    // TODO Auto-generated method stub
    
  }


  public void setVisible(){
    menuSwt.setVisible(true);
  }
  
  
}
