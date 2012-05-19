package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget_ifc;

/**This class holds change requests for widgets. It is used only internally, it should not be invoked
 * by a user's application. The class is public because it should be known by the graphical implementation level,
 * which may be resident in another package.
 * <br><br>
 * The ObjectModelDiagram may shown the relations:
 * <img src="../../../../img/GralWidgetGthreadSet_ifc_gral.png"><br>
 * This class is referred from the {@link GralWidgetMng.WidgetChangeRequExecuter}. A change request 
 * may be delayed or to execute immediately, therefore two queues exist.
 * <br>
 * The evaluation of the data of this class are done in the overridden method 
 * {@link GralWidgetMng#setInfoGthread(GralWidget_ifc, int, int, Object, Object)} of the graphical
 * implementation layer. Any change request is determined by its {@link #cmd} code, which is defined
 * in {@link org.vishia.gral.ifc.GralMng_ifc}. A switch-case calls the proper methods
 * from the {@link GralWidgetGthreadSet_ifc}.
 * <br> 
 * @author Hartmut Schorrig
 *
 */
public class GralWidgetChangeRequ
{
  
  /**Version and history:
   * <ul>
   * <li>2012-01-08 Hartmut new fields und methods {@link #delayExecution(int)} etc.
   * <li>2010-06-00 Hartmut created.
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
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 0x20120108;
  
  /**The widget where the change should be done. */
  public final GralWidget_ifc widg;
  
  /**The command which should be done to change. It is one of the static definitions cmd... 
   * of the class n {@link org.vishia.gral.ifc.GralMng_ifc}. */
  public final int cmd;
  
  /**Numeric value describes any identification, maybe the position of widget where the change should be done.
   * For example, if the widget is a table, it is either the table line or it is
   * -1 or 0 to designate top or end.
   */
  public final int ident;
  
  /**If not 0, it is the first time to execute it. Elsewhere it should be delayed. */
  private long timeExecution;
  
  /**The information which should be changed or added on the widget. The type of information depends
   * on the type of the widget and maybe the cmd. */
  public final Object visibleInfo;
  
  /**Data, which should be assigned as user data if the change of content is done.
   * See {@link org.vishia.gral.base.GralWidget#getContentInfo()}
   */
  public final Object userData;
  
  /**Creates an instance with all final data. The delay should be set after them.
   * @param widg
   * @param cmd
   * @param indent
   * @param visibleInfo
   * @param userData
   */
  public GralWidgetChangeRequ(GralWidget_ifc widg, int cmd, int indent, Object visibleInfo, Object userData) 
  { this.widg = widg;
    this.cmd = cmd;
    this.ident = indent;
    this.visibleInfo = visibleInfo;
    this.userData = userData;
  }
  
  
  /**Checks whether it should be executed.
   * @return time in milliseconds for first execution or value <0 to execute immediately.
   */
  public int timeToExecution(){ 
    return timeExecution == 0 ? -1 : (int)( timeExecution - System.currentTimeMillis()); 
  }
  
  
  /**Sets the delay to execute. It can be set newly whenever this instance isn't used to execute yet.
   * @param millisec delay.
   */
  public void delayExecution(int millisec){
    timeExecution = System.currentTimeMillis() + millisec;
  }
  
}
