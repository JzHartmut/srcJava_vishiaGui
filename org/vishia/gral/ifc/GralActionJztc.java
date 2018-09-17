package org.vishia.gral.ifc;

import org.vishia.cmd.JZtxtcmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.cmd.JZtxtcmdThreadQueue;
import org.vishia.gral.base.GralMng;

/**This class should be instantiated by a JZtxtcmd script.
 * It is the common container for all actions which are deployed by sub routines of jzcmd.
 * To add an action invoke in JZtxtcmd:<pre>
 * Obj gralActions = java new org.vishia.gral.ifc.GralActionJztc(jztc, out);
 * gralActions.add(jztc.sub("buttonConvert"));
 * ....
 * sub buttonConvert(){
 *   .....do something with graphical content or other
 * }  
 * </pre>
 * @author hartmut Schorrig
 *
 */
public class GralActionJztc
{
  
  /**The version, history and license.
   * <ul>
   * <li>2018-09-17 created
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
   * 
   */
  public final static String version = "2018-09-17";


  
  private class Action extends GralUserAction {

    final JZtxtcmdScript.Subroutine jzsub;

    Action(JZtxtcmdScript.Subroutine jzsub){
      super(jzsub.name);
      this.jzsub = jzsub;
    }
    
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params) {
      thread.add(jzsub);
      return true;
    }
    
  }
  
  final JZtxtcmdThreadQueue thread;
  
  //final JZtxtcmdExecuter cmdExecuter;

  final Appendable out;
  
  /**Constructs and stores JZtxtcmd aggregation.
   * @param jztc The main data to access script level
   * @param out Any output used in the sub routine.
   */
  public GralActionJztc(JZtxtcmdExecuter.JzTcMain jztc, Appendable out){
    this.thread = new JZtxtcmdThreadQueue("jzTc-GUI", jztc);
    //this.cmdExecuter = cmdExecuter;
    this.out = out;
  }
  
  
  
  /**Adds a JZtxtcmd-subroutine as execution of a {@link GralUserAction}.
   * to the registered actions, able to get with {@link GralMng#getRegisteredUserAction(String)}
   * The name of the action is the name of the sub routine.
   * The user is responsible to unique names in the graphic application.
   * @param jzsub
   */
  public void add(JZtxtcmdScript.Subroutine jzsub) {
    Action action = new Action(jzsub);  //will be registered
    GralMng.get().registerUserAction(jzsub.name, action);
  }
  
  
}
