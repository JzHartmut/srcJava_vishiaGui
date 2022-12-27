package org.vishia.gral.widget;

import java.io.File;
import java.util.List;

import org.vishia.cmd.CmdExecuter;
import org.vishia.cmd.CmdGetterArguments;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.DataAccess;
import org.vishia.gral.ifc.GralTableLine_ifc;

/**This class is a widget to select commands from a table list.
 * It can be used in any application in a window, which is opened on demand 
 * or which is open any time. The user can select a line with the command and click OK (Enter, Mouse).
 * Then the command is send to the instance of {@link CmdExecuter} which is given by constructor
 * and executed there in the thread of the {@link CmdExecuter}. It is possible to share the CmdQueue-instance
 * with some other application parts. The execution is queued there.
 * 
 * The commands are shown in a table. The appearance of the table can be controlled by the application
 * or maybe by the user. Only one command can be selected to execute.
 * 
 * All commands to select are contained in the public field {@link #cmdStore}. 
 * This storage of commands can be filled calling {@link CmdStore#readCmdCfg(File)} or in any other way
 * of that class. 
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralCommandSelector extends GralSelectList<JZtxtcmdScript.Subroutine>
{
  
  
  /**Version, history and license. This String is visible in the about info.
   * <ul>
   * <li>2017-01-01 Now supports only {@link JZtxtcmdScript.Subroutine}, no more CmdStore. 
   *   The CmdStore is the older concept before JZcmd was found. JZcmd contains more capabilities.
   * <li>2013-10-00 Hartmut created
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
  //@SuppressWarnings("hiding")
  public static final String version = "2016-12-27";


  /**Store of all possible commands given in the command file. 
   * See {@link CmdStore#readCmdCfg(File)} */
  //public final CmdStore cmdStore;
  
  /**Execution instance in another thread. */
  //protected final CmdQueue cmdQueue;
  protected final CmdExecuter cmdExecuter;
  
 /**
   * @since 2016-12
   */
  protected final CmdGetterArguments getterArguments;
  
  /**The currently selected command.
   * 
   */
  protected JZtxtcmdScript.Subroutine selectedCmd;
  
  
  final Appendable out;
  
  public GralCommandSelector(GralPos currPos, String name, int rows, int[] columns, char size, CmdExecuter cmdExecuter, Appendable out, CmdGetterArguments getterArguments)
  { super(currPos, name, rows, columns, size);
    this.cmdExecuter = cmdExecuter;
    this.out = out;
    //this.cmdStore = new CmdStore();
    //this.cmdQueue = cmdQueue;
    this.getterArguments = getterArguments;
    this.wdgdTable.specifyActionOnLineSelected(this.actionOnLineSelected);
   
  }
  
  
   
  
  
  public void clear() { wdgdTable.clearTable(); addJZsub2SelectTable.clear(); }
  
  
  
  public JZtxtcmdScript.AddSub2List addJZsub2SelectTable = new JZtxtcmdScript.AddSub2List() {

    int level = 1;
    GralTableLine_ifc<JZtxtcmdScript.Subroutine> parentline = null, line = null;

    @Override public void clear()
    { parentline = null; line = null;
    }

    @Override
    public void add2List(JZtxtcmdScript.JZcmdClass jzclass, int level)
    { String[] texts = new String[2]; 
      texts[0] = jzclass.cmpnName;
      texts[1] = "+";
      parentline = wdgdTable.addLine(jzclass.cmpnName, texts, null);
      
    }

    @Override
    public void add2List(JZtxtcmdScript.Subroutine jzsub, int level)
    {
      String[] texts = new String[2]; 
      texts[0] = jzsub.name;
      texts[1] = "";
      if(parentline !=null) {
        parentline.addChildLine(jzsub.name, texts, jzsub);
      } else {
        wdgdTable.addLine(jzsub.name, texts, jzsub);
      }
      
    }
    
  };
  
  
  
  
  /**Returns the currently selected command. */
  public JZtxtcmdScript.Subroutine getCurrCmd(){ return selectedCmd; }
  
  
  
  /**Executes the command which is selected currently.
   * Whether or not the selection is visible and recognized by the user, it should be defined in the application.
   */
  public void executeCurrCmdWithFiles(){
    if(selectedCmd !=null){
      actionOk(selectedCmd, null);  //Note: the argument line isn't used in the called method.
    }
  }
  
  
  
  
  
  
  /**Action on click Ok at any line of table.
   * @param userData It is the {@link GralTableLine_ifc#getUserData()}, which is the 
   *   {@link CmdStore.CmdBlock}-instance which is assigned with the line. The method casts without check.
   * @param line isn't used here. It can be null in direct invocations.  
   * @see org.vishia.gral.widget.GralSelectList#actionOk(java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
   */
  @Override protected boolean actionOk(Object userData, GralTableLine_ifc<JZtxtcmdScript.Subroutine> line)
  {
    JZtxtcmdScript.Subroutine jzsub = (JZtxtcmdScript.Subroutine)userData;
    //Map<String, DataAccess.Variable<Object>> jargs = getterArguments.getArguments(cmdBlock.)//cmdBlock.getArguments(getterFiles);
    //File currFile = getterArguments.getCurrDir();
    
    //File currDir = currFile !=null? currFile.getParentFile(): null;
    String sMsg = "GralCommandSelector - put cmd;" + jzsub.toString();
    System.out.println(sMsg);
    List<DataAccess.Variable<Object>> args = getterArguments.getArguments(jzsub);
    cmdExecuter.addCmd(jzsub, args, out, getterArguments.getCurrDir());  //to execute.
    /*
    }
    else {
      File[] files = new File[3];
      
      getterFiles.prepareFileSelection();
      files[0] = getterFiles.getFile1();
      files[1] = getterFiles.getFile2();
      files[2] = getterFiles.getFile3();
      File currDir;
      if(files[0] !=null){
        currDir = files[0].getParentFile();
      } else {
        currDir = new File("/");
      }
      File currFile = getterFiles.getFile1();
      
      String sMsg = "GralCommandSelector - put cmd;" + cmdBlock.toString();
      System.out.println(sMsg);
      cmdQueue.addCmd(cmdBlock, files, currDir);
    }
    */
    return true;
  }
  
  
  
  @Override protected void actionLeft(Object userData, GralTableLine_ifc line)
  {
  }
  
  @Override protected void actionRight(Object userData, GralTableLine_ifc line)
  {
  }
  
  
  @Override protected boolean actionUserKey(int key, Object userData, GralTableLine_ifc line)
  {
    // TODO Auto-generated method stub
    return false;
  }

  
  private final GralUserAction actionOnLineSelected = new GralUserAction("actionOnLineSelected"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      @SuppressWarnings("unchecked")
      GralTableLine_ifc<JZtxtcmdScript.Subroutine> line = (GralTableLine_ifc<JZtxtcmdScript.Subroutine>)params[0];
      if(line !=null){
        selectedCmd = line.getUserData();
      }
      return true;
    }
  };
  
  public GralUserAction actionExecCmdWithFiles = new GralUserAction("actionExecCmdWithFiles"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      executeCurrCmdWithFiles();
      return true;
    }
  };
  
  
}
