package org.vishia.gral.widget;

import java.util.ArrayList;
import java.util.List;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

/**This class is a selector in one text field. You can set the cursor into the field 
 * and select between Parts which are separated with a given character sequence.
 * @author Hartmut Schorrig
 *
 */
public class GralHorizontalSelector<UserData> extends GralWidget
{
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2013-06-18 Hartmut created, new idea.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  @SuppressWarnings("hiding")
  public static final int version = 20130618;


  protected List<Item<UserData>> items = new ArrayList<Item<UserData>>();
  
  protected Item<UserData> actItem = null;
  
  /**The item (index in {@link items} which is the current or actual used one. */
  protected int ixActItem = 0;
  
  /**The item which is selected while the mouse button is pressed and hold, not released yet. */
  protected int ixDstItem = 0;
  
  /**The item which is shown as left in the graphic. */
  protected int ixLeftItem = 0;
  
  protected int minSize;

  public GralColor colorText, colorSelect, colorBack, colorLine;

  
  /**The constructor creates the instance but does nothing with the graphic appearance.
   * @param name
   * @param mng
   */
  public GralHorizontalSelector(String name, GralUserAction actionOnSelect, GralMng mng){
    super(name, 'n', mng);
    colorText = GralColor.getColor("bk");
    colorSelect = GralColor.getColor("rd");
    colorBack = GralColor.getColor("wh");
    colorLine = GralColor.getColor("bk");

    setActionChange(actionOnSelect);
  }
  
  
  
  
  /**Adds a item to show.
   * @param text
   * @param position
   * @param data
   */
  public void addItem(String text, int position, UserData data){
    Item<UserData> item = new Item<UserData>();
    item.text = text;
    item.xSize = 0;
    item.data = data;
    int pos1;
    if(position < 0 || position > items.size()){ pos1 = items.size(); }
    else{ pos1 = position; }
    items.add(pos1, item);
    ixActItem = ixDstItem = pos1;
    actItem = item;
  }
  
  
  
  public boolean setActItem(String name){
    int ixItem = 0;
    for(Item<UserData> item: items){
      if(item.text.equals(name)){
        actItem = item;
        ixActItem = ixItem;
        ixDstItem = ixItem;
        return true;
      }
      ixItem +=1;
    }
    return false;  //not found.
  }
  
  /**Remove a item.
   * @param text
   */
  public void removeItem(String text){
    for(Item<UserData> item: items){
      if(item.text.equals(text)){
        items.remove(item);
      }
    }
  }
  
  
  protected void setDstToActItem(){ 
    if(ixDstItem >=0){
      ixActItem = ixDstItem; 
      actItem = items.get(ixActItem);
      if(actionChanging !=null){
        actionChanging.exec(KeyCode.menuEntered, GralHorizontalSelector.this, actItem.data);
      }
    } else {
      actionChanging.exec(KeyCode.menuEntered, GralHorizontalSelector.this, (UserData)null);
    }
  }
  

  
  public static class Item<UserData>
  {
    public String text;
    public int xSize;
    protected UserData data;
    
    protected boolean removeIfNotUsed;
  }


  
  
  
  GralUserAction actionRemoveTab = new GralUserAction("actionRemoveTab"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        boolean actItemRemoved = ixDstItem == ixActItem;
        items.remove(ixDstItem);
        if(ixDstItem < ixActItem){ ixActItem -=1; }
        if(actItemRemoved){
          if(ixActItem >= items.size()){
            ixDstItem = ixActItem-1;
          }
          setDstToActItem();
        } else {
          ixDstItem = ixActItem; //unchanged
        }
        repaint(100, 300);
      }
      return true;
    }
    
  };



  
  
  /**This class is not intent to use from an application, it is a helper to access all necessary data 
   * with protected methods from the graphic implementation.
   * A derived class of this is defined in the graphic implementation class. Via this derived class
   * the graphic implementation can access this methods.
   * The methods are protected because an application should not use it. This class is public because
   * it should be visible from the graphic implementation which is located in another package. 
   */
  public class GraphicImplAccess{
    
    //public void setWidgetImpl(GralWidgImpl_ifc widg){ wdgImpl = widg; }

    public List<Item<UserData>> items(){ return items; }
    
    public Item<?> actItem(){ return actItem; }
    
    public Item<?> tab(int ix){ return items.get(ix); }
    
    public int nrItem(){ return ixDstItem; }

    public int nrofTabs(){ return items.size(); }
    
    public void calcLeftTab(int gwidth, int xArrow){
      int xBefore = 0;
      int ixItem = ixDstItem;
      //
      //search what tab should be shown left as first:
      //
      if(items.size() >0){
        ixLeftItem = 0;
        while(ixLeftItem ==0 && ixItem >=0){
          GralHorizontalSelector.Item<UserData> item = items.get(ixItem);
          if(item.xSize == 0){
            //item.xSize = 50; //TODO
          }
          if(xArrow + xBefore + item.xSize + xArrow +4 > gwidth){  //to much yet
            ixLeftItem = ixItem +1;
          } else{ 
            xBefore += item.xSize;
            ixItem -=1;
          }
        }
      } else {
        ixLeftItem = -1;  //not given
      }
    }

    
    /**Searches the tab which is shown in the xMouse region.
     * @param xMouse x-position from mouse button pressed in the implementation widget area.
     */
    protected void findTab(int xMouse){
      int ixTab = ixLeftItem;
      int xPos = (ixTab == 0) ? 2: 22;
      boolean found = false;
      int zTabs = items.size();
      GralHorizontalSelector.Item<?> tab;
      do {
        tab = tab(ixTab);
        if(xPos + tab.xSize > xMouse){
          found = true;
        } else {
          xPos += tab.xSize;
          ixTab +=1;
        }
      } while(ixTab < zTabs && !found);
      if(found){
        ixDstItem = ixTab;
        //setActItem(tab.text);
      }
    }

    /**Sets a chosen item to the current one, because the mouse was released inside this item. */
    protected void setDstToActItem(){ GralHorizontalSelector.this.setDstToActItem(); }
    
    /**Removes a different choice of a destination item, because the mouse was released 
     * outside of the area where it is pressed and outside of the widget. */
    protected void clearDstItem(){ ixDstItem = ixActItem; }
    
    protected void removeDstItem(){ 
      items.remove(ixDstItem);
      if(ixDstItem < ixActItem){ ixActItem -=1; }
      clearDstItem();
    }
    
    public void execAfterCreationImplWidget(){
      GralMenu menu = getContextMenu();
      menu.addMenuItemGthread("&Close tab", actionRemoveTab);
    }
    

    public int nrLeftTab(){ return ixLeftItem; }
    

  
  }
  
}