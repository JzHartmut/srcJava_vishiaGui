package org.vishia.gral.widget;

import java.util.ArrayList;
import java.util.List;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWidgImpl_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;

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
  
  
  protected int minSize;

  public GuiImplAccess guiImplAccess = new GuiImplAccess();
  
  /**The constructor creates the instance but does nothing with the graphic appearance.
   * @param name
   * @param mng
   */
  public GralHorizontalSelector(String name, GralMng mng){
    super(name, 'n', mng);
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
  
  
  
  public static class Item<UserData>
  {
    public String text;
    public int xSize;
    protected UserData data;
  }


  
  
  public class GuiImplAccess{
    //public void setWidgetImpl(GralWidgImpl_ifc widg){ wdgImpl = widg; }
    public List<?> items(){ return items; }
  }
  

  @Override
  public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }




  @Override
  public void repaintGthread()
  {
    // TODO Auto-generated method stub
    
  }




  @Override
  public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }




  @Override
  public GralWidgetGthreadSet_ifc getGthreadSetifc()
  {
    // TODO Auto-generated method stub
    return null;
  }




  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }




  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }




  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }




  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
