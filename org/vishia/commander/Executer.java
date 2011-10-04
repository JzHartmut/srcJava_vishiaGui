package org.vishia.commander;

import java.io.File;

import org.vishia.cmd.CmdStore;
import org.vishia.cmd.CmdStore.CmdBlock;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.mainCmd.MainCmd_ifc;

public class Executer
{
  /**For output messages. */
  final MainCmd_ifc console;

  private final JavaCmd main;
  

  /**Store of all possible commands given in the command file. */
  final CmdStore cmdStore = new CmdStore();
  

  
  Executer(MainCmd_ifc console, JavaCmd main)
  { this.main = main;
    this.console = console;
  }
  
  String readCmdFile(File cfgFileCmdsForExt)
  {
    return cmdStore.readCmdCfg(cfgFileCmdsForExt);
  }
  
  
  GralUserAction actionExecute = new GralUserAction(){
    @Override public boolean userActionGui(String sIntension, GralWidget widgd, Object... params)
    { assert(sIntension.equals("FileSelector-file"));
      File file = (File)params[0];
      if(false && file.canExecute()){
        main.cmdQueue.addCmd(null, null, null);
      }
      String name = file.getName();
      String ext;
      int posDot = name.lastIndexOf('.');
      if(posDot > 0){
        ext = name.substring(posDot+1);
      } else {
        ext = "exe";
      }
      CmdBlock cmd = cmdStore.getCmd(ext);
      if(cmd ==null && file.canExecute()){
         cmd = cmdStore.getCmd("exe"); 
      } 
      if(cmd !=null){
        File[] files = new File[1];
        files[0] = file;
        File currentDir = file.getParentFile();
        main.cmdQueue.addCmd(cmd, files, currentDir);
      } else {
        return false;
      }
      // TODO Auto-generated method stub
      return false;
  } };
  
}
