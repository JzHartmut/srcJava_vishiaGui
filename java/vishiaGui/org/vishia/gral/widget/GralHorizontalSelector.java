package org.vishia.gral.widget;

import java.util.ArrayList;
import java.util.List;

import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidgImplAccess_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

/**This widget is a selector with tabs. It can be placed like a text field. 
 * Selecting a tab the {@link GralWidget#setActionChange(GralUserAction)} is called with the associated
 * UserData of the tab is third parameter of {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} 
 * with {@link KeyCode#activated}.
 * That can focus, show or select some widgets, panels etc. With them a tab-panel is able to build,
 * but in a more possibilities than usual implementations of such tab-panels.
 * <br><br>
 * The tabs are shift to left and right if the spaces is to less. 
 * <br><br>
 * Tabs can be closed with right-mouse context menu. Then the {@link GralUserAction#exec(int, GralWidget_ifc, Object...)} 
 * will be called a last time for the barely known UserData the last time with {@link KeyCode#removed}.
 * If the current selected tab will be removed, the tab left of them or the next if it was the first
 * will be activated with calling of {@link GralUserAction#exec(int, GralWidget_ifc, Object...)}
 * for the yet current selected tab if there is any one yet. 
 * <br><br>
 * @author Hartmut Schorrig
 *
 */
public class GralHorizontalSelector<UserData> extends GralWidget
{
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2023-01-20 remove of tabs improved. Used for {@link org.vishia.gral.base.GralPanelContent}.
   * <li>2023-01-20 callback operations gets the other tab also as 2th arg   
   * <li>2023-01-14 Hartmut remove tabs works, used for {@link GralFileSelector}.
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
  public static final String version = "2023-01-20";


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
   * @param gralMng
   */
  public GralHorizontalSelector(GralPos currPos, String name, GralUserAction actionOnSelect){
    super(currPos, name, 'n');
    colorText = GralColor.getColor("bk");
    colorSelect = GralColor.getColor("rd");
    colorBack = GralColor.getColor("wh");
    colorLine = GralColor.getColor("bk");

    setActionChange(actionOnSelect);
  }
  
  
  /**Adds a item to show.
   * @param text The text of the tab.
   * @param position insert position, -1: on end
   * @param data The data of type <UserData>, usual panels, widgets, ...
   * @param removable only if true this item is removable.
   */
  public void addItem(String text, int position, UserData data, boolean removable){
    Item<UserData> item = new Item<UserData>(text, data, removable);
    int pos1;
    if(position < 0 || position > this.items.size()){ pos1 = this.items.size(); }
    else{ pos1 = position; }
    this.items.add(pos1, item);
    this.ixActItem = this.ixDstItem = pos1;
    this.actItem = item;
  }
  
  /**Quest the current selected tab independent of its visibility. 
   * @return the data set with {@link #addItem(String, int, Object)}
   *   for the current selected item or null.
   */
  public Item<UserData> getActItem() {
    return this.actItem;
  }
  
  /**Quest the current selected tab independent of its visibility. 
   * @return the data set with {@link #addItem(String, int, Object)}
   *   for the current selected item or null.
   */
  public UserData getCurrItem ( ) {
    return this.actItem !=null ? this.actItem.data : null;
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
  
  
  /**Sets the #ixActItem to the #ixDstItem, the new current item.
   * @param lastItem the last selected.
   */
  protected void setDstToActItem(Item<UserData> lastItem){ 
    if(this.ixDstItem >=0){
      this.ixActItem = this.ixDstItem; 
      this.actItem = this.items.get(this.ixActItem);
      GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onEnter);
      if(action !=null){
        Object[] args = action.args();
        if(args == null){ action.action().exec(KeyCode.activated, GralHorizontalSelector.this, this.actItem.data, lastItem.data); }
        else { action.action().exec(KeyCode.activated, GralHorizontalSelector.this, args, this.actItem.data, lastItem.data); }
      }
    }
  }
  

  /**This is called on changing the tab, the #ixActItem is the current (last) tab. 
   * 
   */
  protected void setDstToActItem ( ) {
    Item<UserData> lastItem = this.items.get(this.ixActItem);
    setDstToActItem(lastItem);
  }  
  
  /**Called on mouse action in context menu.
   * 
   */
  protected void removeTab(){
    boolean currItemRemoved = this.ixDstItem == this.ixActItem;
    Item<UserData> remove = this.items.get(this.ixDstItem);
    if(remove.removable) {
      Item<UserData> removed = this.items.remove(this.ixDstItem);
      if(this.ixDstItem < this.ixActItem){ this.ixActItem -=1; }
      if(currItemRemoved){                         // remove the current item
        if(this.ixActItem >= this.items.size()){
          this.ixDstItem = this.ixActItem-1;                  // select the item before.
        }
        setDstToActItem(removed);  //calls activation of the yet actual item.
      } else {                                     // remove another item, not selected yet.
        if(this.ixActItem > this.ixDstItem) {
          this.ixActItem -=1;                      // ixActItem -1 because one is removed between
        }
        this.ixDstItem = this.ixActItem; //unchanged
      }
      GralWidget_ifc.ActionChange action = getActionChange(GralWidget_ifc.ActionChangeWhen.onEnter);
      if(action !=null){                                     // call the user action for remove
        UserData currTab = getCurrItem();
        Object[] args = action.args();
        if(args == null){ action.action().exec(KeyCode.removed, GralHorizontalSelector.this, removed.data, currTab); }
        else { action.action().exec(KeyCode.removed, GralHorizontalSelector.this, args, removed.data, currTab); }
      }
      redraw(100, 300);
    } else {
      // cannot remove this item, do nothing. 
    }
  }
  
  

  
  public static class Item<UserData>
  {
    final public String text;
    public int xSize;
    final protected UserData data;
    
    final protected boolean removable;
    
    protected Item ( String text, UserData data, boolean removable ) {
      this.text = text;
      this.xSize = 0;
      this.data = data;
      this.removable = removable;
    }
    
  }


  GralUserAction actionRemoveTab = new GralUserAction("actionRemoveTab"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        removeTab(); 
      }
      return true;
    }
    
  };



  
  
  /**This class is not intent to use from an application, it is the super class for the implementation layer
   * to access all necessary data and methods with protected access rights.
   * The methods are protected because an application should not use it. This class is public because
   * it should be visible from the graphic implementation which is located in another package. 
   */
  public static abstract class GraphicImplAccess<UserData> 
  extends GralWidget.ImplAccess //access to GralWidget
  implements GralWidgImplAccess_ifc
  {
    
    //public void setWidgetImpl(GralWidgImpl_ifc widg){ wdgImpl = widg; }

    protected final GralHorizontalSelector<UserData> outer;
    
    protected GraphicImplAccess(GralHorizontalSelector<UserData> widgg, GralMng mng){
      super(widgg, mng);
      outer = widgg;
    }
    
    protected List<Item<UserData>> items(){ return outer.items; }
    
    protected Item<?> actItem(){ return outer.actItem; }
    
    protected Item<?> tab(int ix){ return outer.items.get(ix); }
    
    protected int nrItem(){ return outer.ixDstItem; }

    protected int nrofTabs(){ return outer.items.size(); }
    
    protected void calcLeftTab(int gwidth, int xArrow){
      int xBefore = 0;
      int ixItem = outer.ixDstItem;
      //
      //search what tab should be shown left as first:
      //
      if(outer.items.size() >0){
        outer.ixLeftItem = 0;
        while(outer.ixLeftItem ==0 && ixItem >=0){
          GralHorizontalSelector.Item<UserData> item = outer.items.get(ixItem);
          if(item.xSize == 0){
            //item.xSize = 50; //TODO
          }
          if(xArrow + xBefore + item.xSize + xArrow +4 > gwidth){  //to much yet
            outer.ixLeftItem = ixItem +1;
          } else{ 
            xBefore += item.xSize;
            ixItem -=1;
          }
        }
      } else {
        outer.ixLeftItem = -1;  //not given
      }
    }

    
    /**Searches the tab which is shown in the xMouse region.
     * @param xMouse x-position from mouse button pressed in the implementation widget area.
     */
    protected void findTab(int xMouse){
      int ixTab = outer.ixLeftItem;
      int xPos = (ixTab == 0) ? 2: 22;
      boolean found = false;
      int zTabs = outer.items.size();
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
        outer.ixDstItem = ixTab;
        //setActItem(tab.text);
      }
    }

    /**Sets a chosen item to the current one, because the mouse was released inside this item. */
    protected void setDstToActItem(){ outer.setDstToActItem(); }
    
    /**Removes a different choice of a destination item, because the mouse was released 
     * outside of the area where it is pressed and outside of the widget. */
    protected void clearDstItem(){ outer.ixDstItem = outer.ixActItem; }
    
    protected void removeDstItem(){ 
      outer.items.remove(outer.ixDstItem);
      if(outer.ixDstItem < outer.ixActItem){ outer.ixActItem -=1; }
      clearDstItem();
    }
    
    protected void execAfterCreationImplWidget(){
      GralMenu menu = outer.getContextMenu();
      menu.addMenuItem("&Close tab", outer.actionRemoveTab);
    }
    

    protected int nrLeftTab(){ return outer.ixLeftItem; }
    

  
  }
  
}
