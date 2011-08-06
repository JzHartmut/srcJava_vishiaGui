package org.vishia.guiCmdMenu;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.mainCmd.MainCmd_ifc;

public class CmdSelect extends GuiCfg
{

  public CmdSelect(GuiCallingArgs cargs, GuiMainCmd cmdgui)
  { 
    super(cargs, cmdgui);
    // TODO Auto-generated constructor stub
  }
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] args)
  { boolean bOk = true;
    GuiCallingArgs cargs = new GuiCallingArgs();
    //Initializes the GUI till a output window to show information.
    //Uses the commonly GuiMainCmd class because here are not extra arguments.
    GuiMainCmd cmdgui = new GuiMainCmd(cargs, args, "Java-Commander");  //implements MainCmd, parses calling arguments
    try{ cmdgui.parseArguments(); }
    catch(Exception exception)
    { cmdgui.writeError("Cmdline argument error:", exception);
      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      bOk = false;  //not exiting, show error in GUI
    }
    if(bOk){
      //Uses socket communication for InterprocessComm, therefore load the factory.
      new InterProcessCommFactorySocket();
      //
      //Initialize this main class and execute.
      CmdSelect main = new CmdSelect(cargs, cmdgui);
      main.execute();
    }
    cmdgui.exit();
  }




}
