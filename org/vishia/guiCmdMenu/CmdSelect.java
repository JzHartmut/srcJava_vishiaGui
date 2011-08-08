package org.vishia.guiCmdMenu;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.gral.gridPanel.TabPanel;
import org.vishia.gral.widget.CommandSelector;
import org.vishia.mainCmd.MainCmd_ifc;

public class CmdSelect extends GuiCfg
{
  
  TabPanel tabCmd, tabFile1, tabFile2;
  
  CommandSelector cmdSelector;

  public CmdSelect(GuiCallingArgs cargs, GuiMainCmd cmdgui)
  { 
    super(cargs, cmdgui);
    // TODO Auto-generated constructor stub
  }
  
  
  /**Initializes the areas for the panels and configure the panels.
   * This routine can be overridden if other areas are need.
   */
  @Override protected void initGuiAreas()
  {
    gui.setFrameAreaBorders(30, 65, 80, 90);
    //gui.setStandardMenusGThread(new File("."), actionFile);

    
    //Creates a Tab-Panel:
    tabCmd = panelMng.tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate);
    panelMng.tabPanel.addGridPanel("cmd", "&A",1,1,10,10);
    gui.addFrameArea(1,1,1,1, panelMng.tabPanel.getGuiComponent()); //dialogPanel);
      
    tabFile1 = panelMng.createTabPanel(panelContent.actionPanelActivate);
    tabFile1.addGridPanel("file1", "&File1",1,1,10,10);
    gui.addFrameArea(2,1,1,1, tabFile1.getGuiComponent()); //dialogPanel);
      
    tabFile2 = panelMng.createTabPanel(panelContent.actionPanelActivate);
    tabFile2.addGridPanel("file2", "File&2",1,1,10,10);
    gui.addFrameArea(3,1,1,1, tabFile2.getGuiComponent()); //dialogPanel);
      
    cmdSelector = new CommandSelector();
    CommandSelector.CmdBlock cmd = cmdSelector.new_CmdBlock();
    cmd.name = "test1";
    cmdSelector.add_CmdBlock(cmd);
    cmd = cmdSelector.new_CmdBlock();
    cmd.name = "test2";
    cmdSelector.add_CmdBlock(cmd);
    panelMng.selectPanel("cmd");
    panelMng.setPosition(0, 0, 10, 21, 'r');
    cmdSelector.add("cmds", panelMng, null, 5, new int[]{10,10}, 'A');
    cmdSelector.fillIn();
   
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
