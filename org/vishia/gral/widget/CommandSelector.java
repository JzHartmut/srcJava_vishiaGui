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
  
  /**Contains all commands read from the configuration file. */
  private final List<CmdBlock> listCmd = new LinkedList<CmdBlock>();
  
  private String syntaxCmd = "Cmds::={ <cmd> }\\e. "
    + "cmd::= <* :?name> : { <*\\n?cmd> \\n } ."; 
  
  private final MainCmd_ifc mainCmd;

  private final ConcurrentLinkedQueue<CmdBlock> pendingCmds = new ConcurrentLinkedQueue<CmdBlock>();
  
  private final ProcessBuilder processBuilder = new ProcessBuilder();
  
  private final StringBuilder cmdOutput = new StringBuilder(4000);
  
  private final StringBuilder cmdError = new StringBuilder(1000);
  
  
  public CommandSelector(MainCmd_ifc mainCmd)
  {
    this.mainCmd = mainCmd;
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
    for(CmdBlock data: listCmd){
      
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, data.name, data);
    }

  }
  
  
  @Override public void actionOk(Object userData)
  {
    CmdBlock cmdBlock = (CmdBlock)userData;
    pendingCmds.add(cmdBlock);  //to execute.
  }
  
  
  /**Execute the pending commands.
   * This method should be called in a specified user thread.
   * 
   */
  public final void executeCmds()
  {
    CmdBlock block;
    while( (block = pendingCmds.poll())!=null){
      for(PrepareCmd cmd: block.listCmd){
        String sCmd = cmd.prepareCmd(new File("."));
        if(sCmd.startsWith("@")){
          
        } else {
          //a operation system command:
          mainCmd.executeCmdLine(processBuilder, sCmd, null, Report.anytime, this.cmdOutput, cmdError);
        }
      }
      System.out.println(block.name);
    }
  }
  
  
  
}
