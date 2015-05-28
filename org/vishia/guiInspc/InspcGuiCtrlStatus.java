package org.vishia.guiInspc;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class contains some status and control widgets for the Inspector Gui
 * @author Hartmut Schorrig
 *
 */
public class InspcGuiCtrlStatus
{
  /**Version, history and license.
   * <ul>
   * <li>2015-05-30 Created.
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
  protected final static String sVersion = "2015-05-30";

  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  //private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<Object> widgTable;


  public InspcGuiCtrlStatus()
  { //inspcMng.addUserOrder(this);  //invoke run in any communication step.
    this.wind = new GralWindow("@primaryWindow,-21..0,-40..0", "InspcCtrlStatusWind", "Fields of ...", GralWindow_ifc.windOnTop | GralWindow_ifc.windResizeable);
    this.widgTable = new GralTable<Object>("@InspcCtrlStatusWind,2..0,0..0", "InspcFieldTable", new int[]{2, 0});
    //this.widgTable.setColumnEditable(2, true);
    //this.widgTable.setActionChange(this.actionChgTable);
    //this.widgTable.specifyActionOnLineSelected(actionLineSelected);
    this.widgTable.setHtmlHelp("HelpInspc.html#Topic.HelpInspc.ctrlStatus.");
  }
  
  
  /**Invoked in the graphic thread.
   */
  public void setToPanel(){
    GralMng mng = GralMng.get();
    wind.setToPanel(mng);
    //mng.setPosition(0, 2, 0, 3, 0, 'd');
    //mng.setPosition(0, 2, 3, 0, 0, 'd');
    //mng.setPosition(2, -4, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
    //mng.setPosition(-2, 0, 0, 7, 0, 'd');
  }
  

  GralUserAction setVisible = new GralUserAction("")
  { public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
      wind.setVisible(true);   
      return true;
    }
  };
  
}
