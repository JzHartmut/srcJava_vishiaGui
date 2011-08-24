package org.vishia.guiCmdMenu;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.gridPanel.TabPanel;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.gral.widget.CommandSelector;
import org.vishia.gral.widget.FileSelector;
import org.vishia.mainCmd.MainCmd_ifc;


public class JavaCmd extends GuiCfg
{
  
  private static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;
  }
  
  private final CallingArgs cargs;
  
  TabPanel tabCmd, tabFile1, tabFile2;
  
  private final CommandSelector cmdSelector = new CommandSelector(mainCmd);
  
  private File[] selectedFiles;

  private final FileSelector[] fileSelector = new FileSelector[]
    { new FileSelector(mainCmd), new FileSelector(mainCmd), new FileSelector(mainCmd)};
  
  
  public JavaCmd(CallingArgs cargs, GuiMainCmd cmdgui)
  { 
    super(cargs, cmdgui);
    this.cargs = cargs;
  }
  
  
  /**Initializes the areas for the panels and configure the panels.
   * Note that the window is initialized with an output area already. This is used for output messages
   * if problems occurs while build the rest of the GUI.
   */
  @Override protected void initGuiAreas()
  {
    gui.setFrameAreaBorders(30, 65, 80, 82);  //x1, x2, y1, y2
    gui.setStandardMenusGThread(new File("."), actionFile);
    gui.addMenuItemGThread("&Command/Set&WorkingDir", actionSetCmdWorkingDir); ///
    //gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Set", actionSetCmdCfg); ///
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Check", actionSetCmdCfg); ///
    //gui.set
    
    //Creates a Tab-Panel:
    tabCmd = panelMng.tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    panelMng.tabPanel.addGridPanel("cmd", "Cm&d",1,1,10,10);
    panelMng.tabPanel.addGridPanel("file0", "File&1",1,1,10,10);
    gui.addFrameArea(1,1,1,1, panelMng.tabPanel.getGuiComponent()); //dialogPanel);
      
    tabFile1 = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    tabFile1.addGridPanel("file1", "File&2",1,1,10,10);
    gui.addFrameArea(2,1,1,1, tabFile1.getGuiComponent()); //dialogPanel);
      
    tabFile2 = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    tabFile2.addGridPanel("file2", "File&3",1,1,10,10);
    gui.addFrameArea(3,1,1,1, tabFile2.getGuiComponent()); //dialogPanel);
      
    CommandSelector.CmdBlock cmd = cmdSelector.new_CmdBlock();
    cmd.name = "test1";
    cmdSelector.add_CmdBlock(cmd);
    cmd = cmdSelector.new_CmdBlock();
    cmd.name = "test2";
    cmdSelector.add_CmdBlock(cmd);

    panelMng.selectPanel("cmd");
    panelMng.setPositionInPanel(2, 0, -2, -0.1f, '.');
    cmdSelector.setToPanel(panelMng, "cmds", 5, new int[]{10,10}, 'A');
    cmdSelector.fillIn();
    cmdSelector.setGetterFiles(getterFiles);

    panelMng.selectPanel("file0");
    panelMng.setPositionInPanel(0, 0, -2, -0.1f, '.');
    fileSelector[1].setToPanel(panelMng, "file0", 5, new int[]{2,20,5,10}, 'A');
    fileSelector[1].fillIn(new File("D:/"));
    
    panelMng.selectPanel("file1");
    panelMng.setPositionInPanel(0, 0, -2, -0.1f, '.');
    fileSelector[1].setToPanel(panelMng, "file1", 5, new int[]{2,20,5,10}, 'A');
    fileSelector[1].fillIn(new File("D:/"));

    panelMng.selectPanel("file2");
    panelMng.setPositionInPanel(0, 0, -2, -0.1f, '.');
    fileSelector[2].setToPanel(panelMng, "file2", 5, new int[]{2,20,5,10}, 'A');
    fileSelector[2].fillIn(new File("D:/"));

  }

  @Override protected final void initMain()
  { if(cargs.fileCfgCmds == null){
      mainCmd.writeError("Argument cmdcfg:CONFIGFILE should be given.");
      //mainCmd.e
    } else {
      cmdSelector.readCmdCfg(cargs.fileCfgCmds);  ///
    }
    super.initMain();  //starts initializing of graphic. Do it after config command selector!
  
  }
  
  /**Executing in the main thread loop. It handles commands.
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  @Override public void stepMain()
  {
    
    cmdSelector.executeCmds();
  }
  
  
  
  private UserActionGui actionSetCmdWorkingDir = new UserActionGui() 
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { WidgetDescriptor widgdFocus = panelMng.getWidgetInFocus();
      if(widgdFocus.name.startsWith("file")){
        int ixFilePanel = widgdFocus.name.charAt(4) - '0';
        assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);  //only such names are registered.
        FileSelector fileSel = fileSelector[ixFilePanel];
        File file = fileSel.getSelectedFile();
        cmdSelector.setWorkingDir(file); 
      }
      stop();
      if(sIntension.equals("")){
        stop();
      }
    }
    
  };
  
  
  
  private File[] getSelectedFile()
  { File file[] = new File[3];
    int ixFile = 0;
    List<WidgetDescriptor> widgdFocus = panelMng.getWidgetsInFocus();
    synchronized(widgdFocus){
      Iterator<WidgetDescriptor> iterFocus = widgdFocus.iterator();
      while(ixFile < file.length && iterFocus.hasNext()){
        WidgetDescriptor widgd = iterFocus.next();
        if(widgd.name.startsWith("file")){
          int ixFilePanel = widgd.name.charAt(4) - '0';
          assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);  //only such names are registered.
          FileSelector fileSel = fileSelector[ixFilePanel];
          file[ixFile++] = fileSel.getSelectedFile();
        }
      }
    }
    return file;
  }
  
  
  
  private UserActionGui actionSetCmdCfg = new UserActionGui() 
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { selectedFiles = getSelectedFile();
      if(selectedFiles[0] !=null){
        cmdSelector.readCmdCfg(selectedFiles[0]);
        cmdSelector.fillIn();
      }
      stop();
      if(sIntension.equals("")){
        stop();
      }
    }
    
  };
  
  
  
  
  
  
  private static class MainCmd extends GuiMainCmd
  {

    private final CallingArgs cargs;
    
    public MainCmd(CallingArgs cargs, String[] args)
    {
      super(cargs, args, "Java Commander");
      this.cargs = cargs;
    }
    
    @Override protected boolean testArgument(String arg, int nArg)
    { boolean bOk = true;
      if(arg.startsWith("cmdcfg:")){
        cargs.fileCfgCmds = new File(arg.substring(7));
      }
      else { bOk = super.testArgument(arg, nArg); }
      return bOk;
    }
    
  }
  
  
  CmdGetFileArgs_ifc getterFiles = new CmdGetFileArgs_ifc()
  { @Override public void  prepareFileSelection()
    { selectedFiles = getSelectedFile();
    }

    @Override public File getFileSelect()
    { return selectedFiles[0];
    }
    
    @Override public File getFile1() { return selectedFiles[0]; }
    
    @Override public File getFile2() { return selectedFiles[1]; }
    
    @Override public File getFile3() { return selectedFiles[2]; }
  };
  
  
  static void testT1()
  {
    float R = 10000.0f;
    float C = 0.000000001f;
    float tStep = 0.0000001f;
    float uc =0.0f;
    float ue = 1.0f;
    for(int step = 0; step < 100; ++step){
      float iR = (ue - uc) / R;
      uc = uc + iR / C * tStep;
    }
      
  }
  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] args)
  { testT1();
    boolean bOk = true;
    CallingArgs cargs = new CallingArgs();
    //Initializes the GUI till a output window to show information.
    //Uses the commonly GuiMainCmd class because here are not extra arguments.
    GuiMainCmd cmdgui = new MainCmd(cargs, args);  //implements MainCmd, parses calling arguments
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
      JavaCmd main = new JavaCmd(cargs, cmdgui);
      main.execute();
    }
    cmdgui.exit();
  }



  void stop(){}

}
