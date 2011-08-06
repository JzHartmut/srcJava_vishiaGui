package org.vishia.gral.widget;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.vishia.cmd.PrepareCmd;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.WidgetDescriptor;

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

  public class CmdBlock
  {
    public String name;
    private final List<PrepareCmd> listCmd = new LinkedList<PrepareCmd>();

    public PrepareCmd new_cmd(){ return new PrepareCmd(); }
    
    public void add_cmd(PrepareCmd cmd)
    { cmd.prepareListCmdReplace();
      listCmd.add(cmd); 
    }
    
  }
  
  private final List<CmdBlock> listCmd = new LinkedList<CmdBlock>();
  
  public CmdBlock new_CmdBlock(){ return new CmdBlock(); }
  
  public void add_CmdBlock(CmdBlock value){ listCmd.add(value); }
  
  
  private String syntaxCmd = "Cmds::={ <cmd> }\\e. "
    + "cmd::= <* :?name> : { <*\\n?cmd> \\n } ."; 
  
  
  String readCmdCfg(String cfgFile)
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
          if(sLine.startsWith(" ")){  //a command line
            
          } else if(sLine.startsWith("@")){
            
          } else if( (posSep = sLine.indexOf(':'))>0){
            //a new command block
            if(actBlock !=null){ add_CmdBlock(actBlock); } 
            actBlock = new_CmdBlock();
            actBlock.name = sLine.substring(0, posSep);
          }
        }
      } catch(IOException exc){ sError = "CommandSelector - cfg file error; " + cfgFile; }
    }
    return sError;
  }
  
  
  
}
