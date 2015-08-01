package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.CmdQueue;
import org.vishia.cmd.CmdStore;
import org.vishia.cmd.PrepareCmd;
import org.vishia.cmd.JZcmdScript;
import org.vishia.cmd.CmdStore.CmdBlock;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.KeyCode;
import org.vishia.zcmd.JZcmd;

public class FcmdExecuter
{
  
  static class ExtCmd
  {
    final String ext;
    List<CmdBlock> listCmd = new LinkedList<CmdBlock>();
    
    
    //GralMenu menuSelectExe;
    
    /**Creates with given extension. @param name the extension. It is the name from a {@link CmdBlock}. */
    ExtCmd(String name){ this.ext = name; }
  }
  
  
  Map<String, ExtCmd> extCmds = new TreeMap<String, ExtCmd>();
  
  
  
  /**For output messages. */
  final MainCmd_ifc console;

  private final Fcmd main;
  
  GralWindow_ifc windConfirmExec;

  GralTable<CmdBlock> widgSelectExec;

  /**Store of all possible commands given in the command file. */
  final CmdStore cmdStore = new CmdStore();
  
  /**The command queue to execute */
  final CmdQueue cmdQueue;

  
  FcmdExecuter(MainCmd_ifc console, Appendable outStatus, Fcmd main)
  { this.main = main;
    this.console = console;
    this.cmdQueue = new CmdQueue(outStatus);
  }
  
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmExec()
  { 
    
    
    main._gralMng.selectPanel("primaryWindow");
    main._gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmExec = main._gralMng.createWindow("execWindow", "confirm execute", GralWindow.windConcurrently);
    //main.gralMng.setPosition(2, GralPos.size -2, 1, -1, 0, 'd');
    widgSelectExec = main._gralMng.addTable("execChoice", 3, new int[]{50});
    widgSelectExec.setActionChange(actionExecCmdAfterChoice);
    
  }  
  
  
  String readCmdFile(File cfgFileCmdsForExt)
  {
    String error = readCmdCfg(cmdStore, cfgFileCmdsForExt, console, main.executer.cmdQueue);
    if(error == null){
      //fill in the extension - cmd - assignments
      extCmds.clear();
      for(CmdBlock cmd: cmdStore.getListCmds()){
        ExtCmd extCmd = extCmds.get(cmd.name);
        if(extCmd == null){ 
          extCmd = new ExtCmd(cmd.name);   //create firstly
          extCmds.put(cmd.name, extCmd);
        }
        extCmd.listCmd.add(cmd);
      }
    }
    return error;
  }
  
  
  
  public static String readCmdCfg(CmdStore dst, File cfgFile, MainCmdLogging_ifc log, CmdQueue executerToInit)
  { String error = dst.readCmdCfgOld(cfgFile);
    if(error ==null){
      File cmdCfgJbat = new File(cfgFile.getParentFile(), cfgFile.getName() + ".jbat");
      if(cmdCfgJbat.exists()){
        try{ 
          JZcmdScript script = JZcmd.translateAndSetGenCtrl(cmdCfgJbat, new File(cmdCfgJbat.getParentFile(), cmdCfgJbat.getName() + ".check.xml"), log);
          dst.addSubOfJZcmdClass(script.scriptClass(), 1);
          executerToInit.initExecuter(script, null);  //NOTE: currdir is not determined.
          //main.cmdSelector.initExecuter(script);
        } catch(Throwable exc){
          
          log.writeError("CmdStore - JZcmdScript;", exc);
          error = "CmdStore - JZcmdScript," + exc.getMessage();
        }
      }

    }
    return error;
  }  
  

  
  
  
  
  private boolean actionExecuteUserKey(int keyCode, FileRemote file)
  {
    
    final char kindOfExecution = checkKeyOfExecution(keyCode);
    
    if(kindOfExecution !=0){
      String ext;
      String name = file.getName();
      int posDot = name.lastIndexOf('.');
      if(posDot > 0){
        ext = name.substring(posDot+1);
      } else if(file.canExecute()){
        ext = ">";
      } else {
        ext = "???";
      }
      ExtCmd extensionCmd = extCmds.get(ext);
      if(extensionCmd !=null){
        widgSelectExec.clearTable();
        for(CmdBlock block: extensionCmd.listCmd){
          GralTableLine_ifc line = widgSelectExec.insertLine(block.name, 0, null, block);
          line.setCellText(block.title, 0);
        }
        windConfirmExec.setWindowVisible(true);
      /*  
      CmdBlock cmd = cmdStore.getCmd(ext);
      if(cmd !=null){
        PrepareCmd cmdp = cmd.getCmds().get(0);
        if(cmd.name.startsWith(">")){
          main.cmdQueue.addCmd(name, null, file.getParentFile(), kindOfExecution);
        } else {
          //the extension determines the command.
          File[] files = new File[1];
          files[0] = file;
          File currentDir = file.getParentFile();
          main.cmdQueue.addCmd(cmd, files, currentDir);
        }
        */
      } else {
        console.writeError("no association found for extension ." + ext);
      }
      
    } else{ 
    }
    return kindOfExecution != 0;
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
      GralWidget widgdFocus = main._gralMng.getWidgetInFocus();
      //FileSelector fileSel = idxFileSelector.get(widgdFocus.name);
      if (main.currentDir() != null) { // is a FileSelector focused yet?
        // if(widgdFocus.name.startsWith("file")){
        // int ixFilePanel = widgdFocus.name.charAt(4) - '0';
        // assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length); //only
        // such names are registered.
        // FileSelector fileSel = fileSelector[ixFilePanel];
        //FileRemote file = fileSel.getSelectedFile();
        cmdQueue.setWorkingDir(main.currentDir());
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
          readCmdCfg(main.cmdSelector.cmdStore, main.currentFile(), main.console, main.executer.cmdQueue);
          main.cmdSelector.fillIn();
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
        String sError = readCmdCfg(main.cmdSelector.cmdStore, main.cargs.fileCfgCmds, console, main.executer.cmdQueue);
      
        if(sError ==null){
          main.cmdSelector.fillIn();
        } else {
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


  
  
  /**User action to re-read the configuration file for extension assignments.
   */
  GralUserAction actionReadExtensionCmd = new GralUserAction("actionReadExtensionCmd")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { readCmdFile(main.cargs.fileCmdsForExt);
      return true;
    }
  };
  

  
  private void executeFileByExtension(File file){
    String ext;
    if(file !=null){
      String name = file.getName();
      int posDot = name.lastIndexOf('.');
      if(posDot > 0){
        ext = name.substring(posDot+1);
      } else if(file.canExecute()){
        ext = ">";
      } else {
        ext = "???";
      }
      ExtCmd extensionCmd = extCmds.get(ext);
      if(extensionCmd !=null){
        widgSelectExec.clearTable();
        for(CmdBlock block: extensionCmd.listCmd){
          GralTable.TableLineData line = widgSelectExec.insertLine(block.name, 0, null, block);
          line.setCellText(block.title, 0);
        }
        windConfirmExec.setWindowVisible(true);
      } else {
        console.writeError("no association found for extension ." + ext);
      }
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
  
  

  /**Instance to execute any command selected in the 'ConfirmExec' sub window.
   * The class's method is invoked if any line was selected.
   */
  GralUserAction actionExecCmdAfterChoice = new GralUserAction("actionExecCmdAfterChoice")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { 
      if(key == KeyCode.enter || key == KeyCode.mouse1Up || key == KeyCode.mouse1Double){
        if(params.length > 0 && params[0] instanceof GralTableLine_ifc){
          GralTableLine_ifc line = (GralTableLine_ifc)params[0];
          CmdBlock cmd = (CmdBlock)line.getUserData();
          ;
          if(cmd !=null){
            //PrepareCmd cmdp = cmd.getCmds().get(0);
            if(cmd.name.startsWith(">")){
              console.writeInfoln("FcmdExecuter - add cmd by extension;" + cmd.name);
              cmdQueue.addCmd(cmd.name, null, main.currentDir(), '>');
            } else {
              //the extension determines the command.
              console.writeInfoln("FcmdExecuter - add cmd by extension;" + cmd.name);
              File[] files = main.getLastSelectedFiles(true, 1);
              if(files[0] !=null) {
                File currentDir = files[0].getParentFile();
                cmdQueue.addCmd(cmd, files, currentDir);
              } else {
                System.err.println("FcmdExecute - no directory selected as current dir");
              }
            }
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
              else { files[++ix] = new File(fileName.getParent() + "/" + fileName.getName()); }
            }
          }
        } else { files = null; }
        cmdQueue.addCmd(sCmd, files, currentDir, kindOfExecution);
        return true;
      } else return false;
    }
  };
  
  
  /**User action to abort a running command.
   * 
   */
  GralUserAction actionCmdAbort = new GralUserAction("actionCmdAbort")
  { @Override public boolean userActionGui(int key, GralWidget widgd, Object... params)
    { cmdQueue.abortCmd();
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
