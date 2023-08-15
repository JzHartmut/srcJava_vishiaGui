package org.vishia.gral.ifc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.script.ScriptException;

import org.vishia.cmd.JZtxtcmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.cmd.JZtxtcmdScript.DefVariable;
import org.vishia.cmd.JZtxtcmdThreadQueue;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.cfg.GuiCfg;
import org.vishia.util.CalculatorExpr;
import org.vishia.util.DataAccess;

/**This class is used inside GuiCfg.
 * Old meaning, but maybe no more possible: should be instantiated by a JZtxtcmd script.
 * New: now GuiCfg is the frame for action sub routines.
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
   * <li>2023-08-15 many renewed using for {@link GuiCfg}
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

    JZtxtcmdScript.Subroutine jzsub;

    Action(JZtxtcmdScript.Subroutine jzsub){
      super(jzsub.name);
      this.jzsub = jzsub;
    }
    
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params) {
      List<DataAccess.Variable<Object>> args = new LinkedList<DataAccess.Variable<Object>>();
      for(DefVariable defv: this.jzsub.formalArgs) {
        String name = defv.getVariableIdent();             // name of the formal argument;
        if(name.equals("gui")) {                           // reference to the GuiCfg class resp. its derived
          
        } 
        else if(name.equals("gralMng")) {                  // reference to the GuiCfg class resp. its derived
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('O', "gralMng", GralActionJztc.this.gui.gralMng, false);
          args.add(actarg);
        }
        else if(name.equals("wdg")) {                  // reference to the GuiCfg class resp. its derived
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('O', "wdg", widgd, false);
          args.add(actarg);
        }
        else if(name.equals("param0") && params[0] !=null) {                  // reference to the GuiCfg class resp. its derived
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('O', "param0", params[0], false);
          args.add(actarg);
        }
        else if(name.equals("param1") && params[0] !=null) {                  // reference to the GuiCfg class resp. its derived
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('O', "param1", params[1], false);
          args.add(actarg);
        }
        else if(name.equals("param2") && params[0] !=null) {                  // reference to the GuiCfg class resp. its derived
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('O', "param2", params[2], false);
          args.add(actarg);
        }
        else if(name.equals("key")) {                  // reference to the GuiCfg class resp. its derived
          CalculatorExpr.Value val = new CalculatorExpr.Value(key);
          DataAccess.Variable<Object> actarg = new DataAccess.Variable<Object>('K', "key", val, true);
          args.add(actarg);
        }
      }
      GralActionJztc.this.thread.add(this.jzsub, args);
      return true;
    }
    
  }
  
  
  private class ActionRereadScript extends GralUserAction {


    public ActionRereadScript()
    { super("GralActionJztc-reread script");
      GralActionJztc.this.gui.gralMng.registerUserAction("GralActionJztc_reread", this);
    }
    
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params) {
      if(GralActionJztc.this.scriptFile !=null) {
        GralActionJztc.this.setScript(GralActionJztc.this.scriptFile);
      }
      return true;
    }
    
  }
  

  class ActionArgs {
    int key;
    GralWidget_ifc widgd; 
    Object[] params;
  }
  
  
  Map<String, Action> actions = new TreeMap<String, Action>();
  
  
  
  File scriptFile;
  
  final JZtxtcmdThreadQueue thread;
  
  //final JZtxtcmdExecuter cmdExecuter;

  /**The java prepared script which contains subroutines. */
  JZtxtcmdScript jzTcScript;

  public final JZtxtcmdExecuter jzTcExec;

  public final Appendable out;
  
  public final GuiCfg gui;
  
//  final MainCmdLogging_ifc log;
  
  /**Constructs and stores JZtxtcmd aggregation.
   * @param jztc The main data to access script level
   * @param out Any output used in the sub routine.
   */
  public GralActionJztc(JZtxtcmdExecuter jzTcExec, JZtxtcmdScript script, Appendable out, GuiCfg gui){
    this.gui = gui;
    this.jzTcExec = jzTcExec;
    this.jzTcScript = script;
    this.thread = new JZtxtcmdThreadQueue("jzTc-GUI", jzTcExec);
    //this.cmdExecuter = cmdExecuter;
    this.out = out; 
//    this.log = log;
  }
  
  
  
  public void setScript(File scriptfile) {
    if(this.scriptFile == null) { //first invocation
      new ActionRereadScript();   //registeres it.
    }
    this.scriptFile = scriptfile;
    try{ 
      this.jzTcScript = JZtxtcmdScript.createScriptFromFile(scriptfile, this.gui.gralMng.log, null);
      List<Object> listSubs = this.jzTcScript.scriptClass().listClassesAndSubroutines();
      for(Object e: listSubs) {
        if(e instanceof JZtxtcmdScript.Subroutine) {
          JZtxtcmdScript.Subroutine sub = (JZtxtcmdScript.Subroutine)e;
          if(sub.formalArgs !=null && sub.formalArgs.size()==1) {
            DataAccess arg = sub.formalArgs.get(0).defVariable;   //has a defVariable always.
            if(arg.datapath().get(0).ident().equals("widget")) {  //datapath only simple, 'widget' aus key
              //subroutine to register
              add(sub);  //adds or replaces
            }
          }
        }
      }
    } catch (ScriptException exc) {
      this.gui.gralMng.log.writeError("Error GralUserAction.setScript", exc);
    }
  }
  
  
  
  public final JZtxtcmdScript getScript () { return this.jzTcScript; }
  
  
  /**Adds a JZtxtcmd-subroutine as execution of a {@link GralUserAction}.
   * to the registered actions, able to get with {@link GralMng#getRegisteredUserAction(String)}
   * The name of the action is the name of the sub routine.
   * The user is responsible to unique names in the graphic application.
   * @param jzsub
   */
  public void add(JZtxtcmdScript.Subroutine jzsub) {
    String name = jzsub.name;
    Action action = this.actions.get(name);
    if(action != null) {
      action.jzsub = jzsub;      //replace action on reread script.
    } else {
      action = new Action(jzsub);
      this.actions.put(name, action);
      this.gui.gralMng.registerUserAction(name, action);
    }
  }
  
  
}
