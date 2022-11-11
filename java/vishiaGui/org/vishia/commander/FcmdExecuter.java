package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.CmdExecuter;
import org.vishia.cmd.CmdStore;
import org.vishia.cmd.JZtxtcmdExecuter;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.cmd.PrepareCmd;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralCommandSelector;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.DataAccess;
import org.vishia.util.KeyCode;

public class FcmdExecuter
{
  
  /**Version, history and license. This String is visible in the about info.
   * <ul>
   * <li>2017-01-01 Hartmut uses {@link JZtxtcmdExecuter} and {@link CmdExecuter}. CmdQueue and CmdStore are obsolete. 
   * <li>2016-08-28 Hartmut chg: The cfg file for jzcmd commands are named cmdjz.cfg anyway, in the directory given with cmdcfg:path/to/cmd.cfg. 
   * <li>2011-10-00 Hartmut created
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


  
  
  /**For output messages. */
  final MainCmd_ifc console;

  private final Fcmd main;
  

  
  GralWindow windConfirmExec = new GralWindow("-19..0,-47..0","execWindow", "confirm execute", GralWindow.windConcurrently);

 // GralTable<CmdBlock> widgSelectExec = new GralTable<>("0..0,0..0", "execChoice", new int[]{47});

  GralTable<JZtxtcmdScript.Subroutine> widgSelectJzExt = new GralTable<>("0..0,0..0", "execChoice", new int[]{47});

  /**Store of all possible commands given in the command file. */
  //final CmdStore cmdStore = new CmdStore();
  
  final Map<String, List<JZtxtcmdScript.Subroutine>> mapCmdExt = new TreeMap<String, List<JZtxtcmdScript.Subroutine>>();
  
  /**The command queue to execute */
  
  final CmdExecuter cmdExecuter;

  /**Table contains some commands, can be selected and executed.
   * <ul>
   * <li>The table is filled in {@link #readCmdCfgSelectList(CmdStore, File, MainCmdLogging_ifc)}. 
   * <li>Commands can be either operation system commands or c {@link JZtxtcmdScript.Subroutine} entry point. 
   * They are stored in the list in user data of type {@link CmdBlock}.
   * <li>Execution of a command: see {@link GralCommandSelector#actionExecCmdWithFiles} respectively GralCommandSelector#actionOk(...)
   * <li>The arguments of a command are read from {@link CmdStore.CmdBlock#getArguments(org.vishia.cmd.CmdGetFileArgs_ifc)}. 
   *   That deals with {@link Fcmd#getterFiles} because it is initialized with {@link GralCommandSelector#setGetterFiles(org.vishia.cmd.CmdGetFileArgs_ifc)}
   *   
   * </ul> 
   */
  final GralCommandSelector cmdSelector;


  
  FcmdExecuter(MainCmd_ifc console, Appendable outStatus, Fcmd main)
  { this.main = main;
    this.console = console;
    //this.cmdQueue = new CmdQueue(outStatus);
    this.cmdExecuter = new CmdExecuter();
    this.cmdExecuter.setEchoCmdOut(main.guiW.outputBox);
    cmdSelector = new GralCommandSelector("cmdSelector", 50, new int[]{0,-10}, 'A', cmdExecuter, main.gui.getOutputBox(), main.getterFileArguments);
  }
  
  
  
  /**Builds the content of the confirm-execute window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmExec()
  { 
    
    
    windConfirmExec.createImplWidget_Gthread();
   // widgSelectExec.specifyActionChange("exec", actionExecCmdAfterChoice, null, GralWidget_ifc.ActionChangeWhen.onEnter);
    widgSelectJzExt.specifyActionChange("exec", actionJZextAfterChoice, null, GralWidget_ifc.ActionChangeWhen.onEnter);
  }  
  
  
  
  
  String readCfgExt(File cfgFileCmdsForExt)
  { String error = null;
    JZtxtcmdScript script = null;
    try{ 
      script = JZtxtcmd.translateAndSetGenCtrl(cfgFileCmdsForExt, null, console);
    } catch(Throwable exc){
      
      console.writeError("CmdStore - JZcmdScript;", exc);
      error = "CmdStore - JZcmdScript," + exc.getMessage();
    }
    if(error ==null) {
      for(JZtxtcmdScript.JZcmdClass jzclass: script.scriptClass().classes()) { // = e.getValue();
        List<JZtxtcmdScript.Subroutine> routines = new LinkedList<JZtxtcmdScript.Subroutine>();
        String ext = jzclass.cmpnName;
        if(ext.endsWith("__")) { 
          ext = ext.substring(0, ext.length()-2); }  //especially for cmd__, cannot be a identifier.
        mapCmdExt.put(ext, routines);
        for(Object classOrSub: jzclass.listClassesAndSubroutines()) {
          if(classOrSub instanceof JZtxtcmdScript.Subroutine) {
            JZtxtcmdScript.Subroutine subRoutine = (JZtxtcmdScript.Subroutine) classOrSub;
            if(!subRoutine.name.startsWith("_")) { //ignore internal subroutines!
              routines.add(subRoutine); 
            }
          } 
        }
      }
    }
    return error;
  }
  
  
  public String readCmdCfgSelectList(JZtxtcmdScript.AddSub2List dst, File cfgFile, MainCmdLogging_ifc log)
  { File cmdCfgJbat = new File(cfgFile.getParentFile(), "cmdjz.cfg");
    return JZtxtcmd.readJZcmdCfg(dst, cmdCfgJbat, log, cmdExecuter);
  }  

  
  /**
   * Action to set the working directory for the next command invocation. The
   * working directory is the directory in the focused file tab.
   * 
   */
  GralUserAction actionSetCmdWorkingDir = new GralUserAction("actionSetCmdWorkingDir")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      //GralWidget widgdFocus = main._gralMng.getWidgetInFocus();
      //FileSelector fileSel = idxFileSelector.get(widgdFocus.name);
      if (main.currentDir() != null) { // is a FileSelector focused yet?
        // if(widgdFocus.name.startsWith("file")){
        // int ixFilePanel = widgdFocus.name.charAt(4) - '0';
        // assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length); //only
        // such names are registered.
        // FileSelector fileSel = fileSelector[ixFilePanel];
        //FileRemote file = fileSel.getSelectedFile();
        //cmdQueue.setWorkingDir(main.currentDir());
        cmdExecuter.setCurrentDir(main.currentDir());
      }
      return true;
    }

  };

  
  
  
  /**
   * Action to set the command list from file. It is called from menu.
   * 
   */
  GralUserAction actionSetCmdCfg = new GralUserAction("actionSetCmdCfg") { 
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if (main.currentFile() != null) {
          cmdSelector.clear();
          String sError = readCmdCfgSelectList(cmdSelector.addJZsub2SelectTable, main.currentFile(), main.console);
          if(sError !=null) {
            System.err.println("readCmdCfg; from file " + main.cargs.fileCfgButtonCmds.getAbsolutePath()+ "; error: " + sError);
          }
        }
      }
      return true;
    }

  };


  
  /**Action to set the command list from actual file. It is called from menu.
   */
  GralUserAction actionSetCmdCfgAct = new GralUserAction("actionSetCmdCfgAct") { 
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        cmdSelector.clear();
        String sError = readCmdCfgSelectList(cmdSelector.addJZsub2SelectTable, main.cargs.fileCfgCmds, console);
        if(sError !=null){
          System.err.println("readCmdCfg; from file " + main.cargs.fileCfgButtonCmds.getAbsolutePath()+ "; error: " + sError);
        }
      }
      return true;
    }

  };


  /**Action to open an editor for the command list from actual file. It is called from menu.
   */
  GralUserAction actionEditCmdCfgAct = new GralUserAction("actionEditCmdCfgAct") { 
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        main.openExtEditor(main.cargs.fileCfgCmds);
      }
      return true;
    }

  };



  
  private void executeFileByExtension(File file){
    String ext;
    if(file !=null){
      String name = file.getName();
      int posDot = name.lastIndexOf('.');
      if(posDot >= 0){
        ext = name.substring(posDot+1);
      } else if(file.canExecute()){
        ext = ">";
      } else {
        ext = "???";
      }
      List<JZtxtcmdScript.Subroutine> listSub = mapCmdExt.get(ext.toLowerCase());
      if(listSub !=null){
        widgSelectJzExt.clearTable();
        GralTable<JZtxtcmdScript.Subroutine>.TableLineData firstLine = null;
        for(JZtxtcmdScript.Subroutine jzsub: listSub) {
          GralTable<JZtxtcmdScript.Subroutine>.TableLineData line = widgSelectJzExt.insertLine(jzsub.name, -1, null, jzsub);
          if(firstLine == null) { firstLine = line; }
          line.setCellText(jzsub.name, 0);
          
        }
        widgSelectJzExt.setCurrentLine(firstLine, 0, 0); //set the first line to position 0.
        //windConfirmExec.setWindowVisible(true);
        windConfirmExec.setFocus();
        
      }
      
      
      
      
      /*
      ExtCmd extensionCmd = extCmds.get(ext);
      if(extensionCmd !=null){
        widgSelectExec.clearTable();
        GralTable<CmdBlock>.TableLineData firstLine = null;
        for(CmdBlock block: extensionCmd.listCmd){
          GralTable<CmdBlock>.TableLineData line = widgSelectExec.insertLine(block.name, -1, null, block);
          if(firstLine == null) { firstLine = line; }
          line.setCellText(block.title, 0);
        }
        widgSelectExec.setCurrentLine(firstLine, 0, 0); //set the first line to position 0.
        //windConfirmExec.setWindowVisible(true);
        windConfirmExec.setFocus();
        
      } else {
        console.writeError("no association found for extension ." + ext);
      }
      */
    } else{ System.err.println("FcmdExecuter.executeFileByExtension - no file selected;"); }
  }
  
  
  
  GralUserAction actionExecuteFileByExtension = new GralUserAction("actionExecuteFileByExtension")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        File file = main.currentFile();
        executeFileByExtension(file);  
        return true;
      } else return false;
  } };  
  
  /**This action is associated to any {@link FcmdFileCard} as action on enter file.
   * The file is given as parameter then.
   */
  GralUserAction actionOnEnterFile = new GralUserAction("actionOnEnterFile")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { FileRemote file = (FileRemote)params[0]; 
      executeFileByExtension(file);  
      return true;
    }
  };
  
  

  
  
  
  GralUserAction actionJZextAfterChoice = new GralUserAction("actionExecCmdAfterChoice")
  { @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params)
  { 
    if(key == KeyCode.enter || key == KeyCode.mouse1Up || key == KeyCode.mouse1Double){
      if(params.length > 0 && params[0] instanceof GralTableLine_ifc){
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<JZtxtcmdScript.Subroutine> line = (GralTableLine_ifc<JZtxtcmdScript.Subroutine>)params[0];
        JZtxtcmdScript.Subroutine jzsub = line.getUserData();
        if(jzsub !=null){
          //PrepareCmd cmdp = cmd.getCmds().get(0);
          File[] files = main.getLastSelectedFiles(true, 1);
          List<DataAccess.Variable<Object>> args = new LinkedList<DataAccess.Variable<Object>>();
          DataAccess.Variable<Object> var = new DataAccess.Variable<Object>('F', "file1", files[0], true);
          args.add(var);
          cmdExecuter.addCmd(jzsub, args, main.gui.getOutputBox(), main.currentDir());
        } else {
          console.writeError("FcmdExecuter - cmd not found; ");
        }
      }
      windConfirmExec.setWindowVisible(false);
      return true;
    } else return false;
  }
};


  
  
  
  
  GralUserAction actionCmdFromOutputBox = new GralUserAction("actionCmdFromOutputBox")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { final char kindOfExecution = checkKeyOfExecution(key);
      if(kindOfExecution !=0){
        GralTextBox widgg = (GralTextBox)widgd;
        String text = widgg.getText();
        int cursorPos = widgg.getCursorPos();
        int start1 = text.lastIndexOf("\n>", cursorPos);
        int start2 = text.lastIndexOf("\n&", cursorPos);
        int start = start2 > start1 ? start2+1 : start1+1;  //note: on -1 it is 0, start of box
        String sCmd = text.substring(start+1, cursorPos);
        final File currentDir;
        if(main.currentDir() !=null){
          //note: executes it local:
          currentDir = main.currentDir();
        } else {
          currentDir = null;  //use any standard
        }
        final File[] files;
        if(sCmd.indexOf("<*")>=0){ //a file may need
          files= new File[3];
          int ix = -1;
          for(File fileName: main.selectedFiles123){
            if(fileName !=null){
              if(fileName !=null ){ files[++ix] = fileName; }
             // else { files[++ix] = new File(fileName.getParent() + "/" + fileName.getName()); }
            }
          }
        } else { files = null; }
        //cmdQueue.addCmd(sCmd, files, currentDir, kindOfExecution);
        cmdExecuter.addCmd(sCmd, null, main.gui.getOutputBox(), currentDir, null);
        return true;
      } else return false;
    }
  };
  
  
  /**User action to abort a running command.
   * 
   */
  GralUserAction actionCmdAbort = new GralUserAction("actionCmdAbort")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { //cmdQueue.abortCmd();
      cmdExecuter.abortAllCmds();
      return true;
    }
  };
  

  

  

  private char checkKeyOfExecution(int key){
    final char kindOfExecution;
    if(key == main.keyActions.keyExecuteInJcmd) kindOfExecution = PrepareCmd.executeLocalPipes;
    else if(key == main.keyActions.keyExecuteStartProcess) kindOfExecution = PrepareCmd.executeStartProcess;
    else if(key == main.keyActions.keyExecuteInShell) kindOfExecution = PrepareCmd.executeInShellClose;
    else if(key == main.keyActions.keyExecuteInShellOpened) kindOfExecution = PrepareCmd.executeInShellOpened;
    else if(key == main.keyActions.keyExecuteAsJavaClass) kindOfExecution = PrepareCmd.executeJavaMain;
    else kindOfExecution = 0;
    return kindOfExecution;
  }
  

  
  
  
  
}
