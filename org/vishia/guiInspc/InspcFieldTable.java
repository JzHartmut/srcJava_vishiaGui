package org.vishia.guiInspc;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class presents a window with one table and some buttons to view and edit all fields in one instance
 * in a target system.
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcFieldTable
{
  /**Version, history and license.
   * <ul>
   * <li>2013-12-18 Created.
   * </ul>
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
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  //@SuppressWarnings("hiding")
  protected final static int version = 0x20131218;

  
  
  /**The window to present. */
  GralWindow wind;
  
  /**Table of fields, type and value. */
  GralTable<Object> widgTable;

  public InspcFieldTable()
  {
    super();
    this.wind = new GralWindow("InspcFieldTableWind", "Fields of ...", GralWindow_ifc.windOnTop);
    this.widgTable = new GralTable<Object>("InspcFieldTable", new int[]{20, 0, -10});
  }
  
  
  public void setToPanel(GralMng mng){
    wind.setToPanel(mng);
    mng.setPosition(2, -2, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
  }
  
  
  GralUserAction actionOpenWindow = new GralUserAction("InspcFieldTable - open window"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      wind.setVisible(true);
      return true;
    }
  };
  
  
}
