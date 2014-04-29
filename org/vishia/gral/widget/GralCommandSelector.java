package org.vishia.gral.widget;

import java.io.File;
import java.util.Map;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.cmd.CmdQueue;
import org.vishia.cmd.CmdStore;
import org.vishia.cmd.JZcmdScript;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.util.DataAccess;

/**This class is a widget to select commands from a table list.
 * It can be used in any application in a window, which is opened on demand 
 * or which is open any time. The user can select a line with the command and click OK (Enter, Mouse).
 * Then the command is send to the instance of {@link CmdQueue} which is given by constructor
 * and executed there in the thread of the {@link CmdQueue}. It is possible to share the CmdQueue-instance
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
public class GralCommandSelector extends GralSelectList<CmdStore.CmdBlock>
{

  /**Store of all possible commands given in the command file. 
   * See {@link CmdStore#readCmdCfg(File)} */
  public final CmdStore cmdStore;
  
  /**Execution instance in another thread. */
  protected final CmdQueue cmdQueue;
  
  /**This is an agent which gets some files in the application, controlled by this class.
   * Depending on the commmand several files should be provided.
   */
  protected CmdGetFileArgs_ifc getterFiles;
  
  
  
  /**The currently selected command.
   * 
   */
  protected CmdStore.CmdBlock selectedCmd;
  
  
  public GralCommandSelector(String name, int rows, int[] columns, char size, CmdQueue cmdQueue, GralMng mng)
  { super(name, rows, columns, size);
    this.cmdStore = new CmdStore();
    this.cmdQueue = cmdQueue;
  }
  
  
  @Override
  public void setToPanel(GralMngBuild_ifc gralMng){
    super.setToPanel(gralMng);
    wdgdTable.specifyActionOnLineSelected(actionOnLineSelected);
  }

  
  /**Sets the interface, which is able to get file arguments.
   * The interface depends on the application, which may provide some file arguments 
   * for a command to execute. The file arguments may depend on the state of the application
   * (currently selected files etc. ). 
   * This interface is valid for the next commands which will be invoke. It can be changed anytime.
   * 
   * @param getterFiles Implementation of the named interface in the application.
   */
  public void setGetterFiles(CmdGetFileArgs_ifc getterFiles)
  {
    this.getterFiles = getterFiles;
  }
  
  
  public void fillIn()
  {
    wdgdTable.clearTable();
    int level = 1;
    GralTableLine_ifc<CmdStore.CmdBlock> parentline = null, line = null;
    for(CmdStore.CmdBlock data: cmdStore.getListCmds()){
      if(data.level > level){
        parentline = line;
        level = data.level;
      }
      if(data.level == 1 || line == null){
        line = wdgdTable.addLine(data.name, null, data);
        line.setCellText(data.name, 0);
        line.setCellText(data.title, 1);
      } else if(data.level < level){
        level = data.level;
        GralTableLine_ifc<CmdStore.CmdBlock> parent = line.parentNode();
        if(parent !=null){ line = parent;}
        line = wdgdTable.addLine(data.name, null, data);
        line.setCellText(data.name, 0);
        line.setCellText(data.title, 1);
      } else {  //level >=2
        line = parentline.addChildLine(data.name, null, data);
        line.setCellText(data.name, 0);
        line.setCellText(data.title, 1);
      }
    }
    wdgdTable.repaint();
  }
  
  
  
  
  /**Returns the currently selected command. */
  public CmdStore.CmdBlock getCurrCmd(){ return selectedCmd; }
  
  
  
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
  @Override protected boolean actionOk(Object userData, GralTableLine_ifc<CmdStore.CmdBlock> line)
  {
    CmdStore.CmdBlock cmdBlock = (CmdStore.CmdBlock)userData;
    Map<String, DataAccess.Variable<Object>> jargs = cmdBlock.getArguments(getterFiles);
    if(jargs != null){ 
      File currFile = getterFiles.getFile1();
      
      File currDir = currFile !=null? currFile.getParentFile(): null;
      String sMsg = "GralCommandSelector - put cmd;" + cmdBlock.toString();
      System.out.println(sMsg);
      cmdQueue.addCmd(cmdBlock, jargs, currDir);  //to execute.
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
      GralTableLine_ifc line = (GralTableLine_ifc)params[0];
      if(line !=null){
        selectedCmd = (CmdStore.CmdBlock)line.getUserData();
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
