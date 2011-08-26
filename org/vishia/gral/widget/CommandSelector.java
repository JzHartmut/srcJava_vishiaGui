package org.vishia.gral.widget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.cmd.PrepareCmd;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

/**This class is a widget to select and view commands.
 * It can be used in any application in a window, which is opened on demand 
 * or which is open any time. 
 * 
 * The commands are shown in a table. The appearance of the table can be controlled by the application
 * or maybe by the user. Only one command can be selected to execute.
 * 
 * @author Hartmut Schorrig
 *
 */
public class CommandSelector extends SelectList
{

  /**Gets in grsphical thread!
   * 
   */
  private CmdGetFileArgs_ifc getterFiles;
  
  /**Description of one command.
   */
  public class CmdBlock
  {
    /**The identification for user in the selection list. */
    public String name;
    
    /**Some commands of this block. */
    private final List<PrepareCmd> listCmd = new LinkedList<PrepareCmd>();

    /**Possible call from {@link org.vishia.zbnf.ZbnfJavaOutput}. Creates an instance of one command */
    public PrepareCmd new_cmd(){ return new PrepareCmd(); }
    
    /**Possible call from {@link org.vishia.zbnf.ZbnfJavaOutput}. Adds the instance of command */
    public void add_cmd(PrepareCmd cmd)
    { cmd.prepareListCmdReplace();
      listCmd.add(cmd); 
    }
    
  }
  
  
  
  
  private static class PendingCmd implements CmdGetFileArgs_ifc
  {
    final CmdBlock cmdBlock;
    final File[] files;
    
    public PendingCmd(CmdBlock cmdBlock, File[] files)
    { this.cmdBlock = cmdBlock;
      this.files = files;
    }

    @Override public void  prepareFileSelection()
    { }

    @Override public File getFileSelect()
    { return files[0];
    }
    
    @Override public File getFile1() { return files[0]; }
    
    @Override public File getFile2() { return files[1]; }
    
    @Override public File getFile3() { return files[2]; }

  }
  
  
  
  /**Contains all commands read from the configuration file. */
  private final List<CmdBlock> listCmd = new LinkedList<CmdBlock>();
  
  private String syntaxCmd = "Cmds::={ <cmd> }\\e. "
    + "cmd::= <* :?name> : { <*\\n?cmd> \\n } ."; 
  
  private final MainCmd_ifc mainCmd;

  private final ConcurrentLinkedQueue<PendingCmd> pendingCmds = new ConcurrentLinkedQueue<PendingCmd>();
  
  private final ProcessBuilder processBuilder = new ProcessBuilder();
  
  private final StringBuilder cmdOutput = new StringBuilder(4000);
  
  private final StringBuilder cmdError = new StringBuilder(1000);
  
  
  public CommandSelector(MainCmd_ifc mainCmd)
  {
    this.mainCmd = mainCmd;
  }
  
  public void setGetterFiles(CmdGetFileArgs_ifc getterFiles)
  {
    this.getterFiles = getterFiles;
  }
  
  
  /**Possible call from {@link org.vishia.zbnf.ZbnfJavaOutput}. Creates an instance of one command block */
  public CmdBlock new_CmdBlock(){ return new CmdBlock(); }
  
  /**Possible call from {@link org.vishia.zbnf.ZbnfJavaOutput}. Adds the instance of command block. */
  public void add_CmdBlock(CmdBlock value){ listCmd.add(value); }
  
  
  public String readCmdCfg(File cfgFile)
  { String sError = null;
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(cfgFile));
    } catch(FileNotFoundException exc){ sError = "CommandSelector - cfg file not found; " + cfgFile; }
    if(reader !=null){
      CmdBlock actBlock = null;
      listCmd.clear();
      try{
        String sLine;
        int posSep;
        while( (sLine = reader.readLine()) !=null){
          if( sLine.startsWith("==")){
            posSep = sLine.indexOf("==", 2);  
            //a new command block
            if(actBlock !=null){ add_CmdBlock(actBlock); } 
            actBlock = new_CmdBlock();
            actBlock.name = sLine.substring(2, posSep);
          } else if(sLine.startsWith("@")){
              
          } else  if(sLine.startsWith(" ")){  //a command line
            PrepareCmd cmd = actBlock.new_cmd();
            cmd.cmd = sLine.trim();
            cmd.prepareListCmdReplace();
            actBlock.add_cmd(cmd);
          }      
        }
        if(actBlock !=null){ add_CmdBlock(actBlock); } 
      } catch(IOException exc){ sError = "CommandSelector - cfg file error; " + cfgFile; }
    }
    return sError;
  }
  
  public void fillIn()
  {
    wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdClear, -1, null, null);
    for(CmdBlock data: listCmd){
      
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, data.name, data);
    }

  }
  
  
  public void setWorkingDir(File file)
  { final File dir;
    if(!file.isDirectory()){
      dir = file.getParentFile();
    } else {
      dir = file;
    }
    processBuilder.directory(dir);
  }
  
  
  @Override public void actionOk(Object userData, TableLineGui_ifc line)
  {
    CmdBlock cmdBlock = (CmdBlock)userData;
    getterFiles.prepareFileSelection();
    File[] files = new File[3];
    files[0] = getterFiles.getFile1();
    files[1] = getterFiles.getFile2();
    files[2] = getterFiles.getFile3();
    pendingCmds.add(new PendingCmd(cmdBlock, files));  //to execute.
  }
  
  
  
  @Override public void actionLeft(Object userData, TableLineGui_ifc line)
  {
  }
  
  @Override public void actionRight(Object userData, TableLineGui_ifc line)
  {
  }
  
  
  /**Execute the pending commands.
   * This method should be called in a specified user thread.
   * 
   */
  public final void executeCmds()
  {
    CmdBlock block;
    PendingCmd cmd1;
    while( (cmd1 = pendingCmds.poll())!=null){
      for(PrepareCmd cmd: cmd1.cmdBlock.listCmd){
        String sCmd = cmd.prepareCmd(cmd1);
        if(sCmd.startsWith("@")){
          
        } else {
          //a operation system command:
          mainCmd.executeCmdLine(processBuilder, sCmd, null, Report.anytime, this.cmdOutput, cmdError);
          System.out.append(cmdOutput);
          System.out.append(cmdError);
        }
      }
      System.out.println(cmd1.cmdBlock.name);
    }
  }

  @Override
  void actionUserKey(String sKey, Object userData, TableLineGui_ifc line)
  {
    // TODO Auto-generated method stub
    
  }
  
  
  
}
