package org.vishia.commander;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.cmd.CmdQueue;
import org.vishia.cmd.CmdStore;
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
import org.vishia.gral.widget.WidgetCmpnifc;
import org.vishia.mainCmd.MainCmd_ifc;


public class JavaCmd extends GuiCfg
{
  
  private static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;
    
    File fileCfgButtonCmds;
    
    File fileSelectTabPaths;
  }
  
  private final CallingArgs cargs;
  
  TabPanel tabCmd, tabFile1, tabFile2;
  
  WidgetCmpnifc panelButtons;
  
  final CmdQueue cmdQueue = new CmdQueue(mainCmd);
  
  private final SelectTab selectTab = new SelectTab(mainCmd, panelMng);
  
  private final CommandSelector cmdSelector = new CommandSelector(cmdQueue);
  
  private File[] selectedFiles;

  private final Map<String, FileSelector> idxFileSelector = new TreeMap<String, FileSelector>();
    //{ new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>()};
  
  
  
  
  /**The commands which are used for some buttons or menu items from the JavaCommander itself. */
  final CmdStore buttonCmds;
  
  public JavaCmd(CallingArgs cargs, GuiMainCmd cmdgui)
  { 
    super(cargs, cmdgui);
    this.cargs = cargs;
    buttonCmds = new CmdStore();
  }
  
  
  /**Initializes the areas for the panels and configure the panels.
   * Note that the window is initialized with an output area already. This is used for output messages
   * if problems occurs while build the rest of the GUI.
   */
  @Override protected void initGuiAreas()
  {
    gui.setFrameAreaBorders(30, 65, 70, 85);  //x1, x2, y1, y2
    gui.setStandardMenusGThread(new File("."), actionFile);
    gui.addMenuItemGThread("&Command/Set&WorkingDir", actionSetCmdWorkingDir); ///
    //gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Set", actionSetCmdCfg); ///
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Check", actionSetCmdCfg); ///
    //gui.set
    
    //Creates tab-Panels for the file lists and command lists.
    tabCmd = panelMng.tabPanel = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    gui.addFrameArea(1,1,1,1, panelMng.tabPanel.getGuiComponent()); //dialogPanel);

    int[] widthSelecttable = new int[]{2, 20, 30};
    
    tabCmd.addGridPanel("Sel0", "a-F1",1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTab.listLeft.setToPanel(panelMng, "sel0", 5, widthSelecttable, 'A');
    selectTab.fillInLeft();
    
    tabCmd.addGridPanel("cmd", "Cm&d",1,1,10,10);
    panelMng.setPosition(2, -2, 0, -0, 1, 'd');
    cmdSelector.setToPanel(panelMng, "cmds", 5, new int[]{10,10}, 'A');
    cmdSelector.fillIn();
    cmdSelector.setGetterFiles(getterFiles);

    for(SelectTab.SelectInfo info: selectTab.selectLeft){
      if(info.active == 'l'){
        tabCmd.addGridPanel(info.tabName, info.tabName,1,1,10,10);
        panelMng.setPosition(0, -2, 0, -0, 1, 'd');
        FileSelector fileSelector = new FileSelector(mainCmd);
        idxFileSelector.put(info.tabName, fileSelector);
        fileSelector.setToPanel(panelMng, info.tabName, 5, new int[]{2,20,5,10}, 'A');
        fileSelector.fillIn(new File(info.path));
      }
    }
    
      
    tabFile1 = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    gui.addFrameArea(2,1,1,1, tabFile1.getGuiComponent()); //dialogPanel);
    
    tabFile1.addGridPanel("Sel1", "a-F2",1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTab.listMid.setToPanel(panelMng, "sel0", 5, widthSelecttable, 'A');
    selectTab.fillInMid();
    
    tabFile1.addGridPanel("file1", "File&2",1,1,10,10);
    panelMng.setPosition(0, -2, 0, -0, 1, 'd');
    { FileSelector fileSelector = new FileSelector(mainCmd);
      idxFileSelector.put("file1", fileSelector);
      fileSelector.setToPanel(panelMng, "file1", 5, new int[]{2,20,5,10}, 'A');
      fileSelector.fillIn(new File("/"));
    }
    tabFile2 = panelMng.createTabPanel(panelContent.actionPanelActivate, GuiPanelMngBuildIfc.propZoomedPanel);
    gui.addFrameArea(3,1,1,1, tabFile2.getGuiComponent()); //dialogPanel);
      
    tabFile2.addGridPanel("file2", "File&3",1,1,10,10);

    tabFile2.addGridPanel("Sel1", "a-F2",1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTab.listRight.setToPanel(panelMng, "sel0", 5, widthSelecttable, 'A');
    selectTab.fillInRight();
    
    panelButtons = panelMng.createGridPanel("Buttons", panelMng.getColor("gr"), 1, 1, 10, 10);
    gui.addFrameArea(1,2,3,1, panelButtons); //dialogPanel);
    initPanelButtons();
    
    
    panelMng.selectPanel("file2");
    panelMng.setPosition(0, -2, 0, -0, 1, 'd');
    { FileSelector fileSelector = new FileSelector(mainCmd);
      idxFileSelector.put("file2", fileSelector);
      fileSelector.setToPanel(panelMng, "file2", 5, new int[]{2,20,5,10}, 'A');
      fileSelector.fillIn(new File("/"));
    }
    panelMng.selectPanel("file1");
    panelMng.setPosition(0, -0, 0, -0, 1, 'd');
    //panelMng.createWindow("selectTab", "select", true);
    
  }

  private void initPanelButtons()
  {
    panelMng.selectPanel("Buttons");
    panelMng.setPosition(0, 1, 10, 20, 1, 'r');
    panelMng.addText("F1", 'A', 0x0);
    panelMng.addText("F2", 'A', 0x0);
    panelMng.addText("F3", 'A', 0x0);
    panelMng.addText("F4", 'A', 0x0);
    panelMng.addText("F5", 'A', 0x0);
    panelMng.addText("F6", 'A', 0x0);
    panelMng.addText("F7", 'A', 0x0);
    panelMng.addText("F8", 'A', 0x0);
    panelMng.addText("F9", 'A', 0x0);
    panelMng.addText("F10", 'A', 0x0);
    panelMng.setPosition(1, 3, 0, 4, 1, 'd');
    panelMng.addText("alt", 'A', 0x0);
    panelMng.addText("ctr", 'A', 0x0);
    panelMng.addText("sh", 'A', 0x0);
    
    panelMng.setPosition(1, 3, 4, 14, 1, 'r');
    panelMng.addButton("b-help", null, "help", null, null, "help");
    panelMng.addButton("b-F2", null, "help", null, null, "F2");
    panelMng.addButton("b-help", null, "help", null, null, "view");
    panelMng.addButton("b-edit", actionEdit, "", null, null, "edit");
    panelMng.addButton("b-help", null, "help", null, null, "copy");
    panelMng.addButton("b-help", null, "help", null, null, "move");
    panelMng.addButton("b-help", null, "help", null, null, "mkdir");
    panelMng.addButton("b-help", null, "help", null, null, "del");
    panelMng.addButton("b-help", null, "help", null, null, "cmd");
    panelMng.addButton("b-help", null, "help", null, null, "F10");
    panelMng.setPosition(3, 5, 4, 14, 1, 'r');
    panelMng.addButton("selectLeft", selectPanelLeft, "selectLeft", null, null, "left");
    panelMng.addButton("selectMiddle", selectPanelMiddle, "help", null, null, "middle");
    panelMng.addButton("selectRight", selectPanelRight, "", null, null, "right");
    panelMng.addButton("selectCmd", selectPanelOut, "", null, null, "cmd");
    panelMng.addButton("b-help", null, "help", null, null, "zip");
    panelMng.addButton("b-help", null, "help", null, null, "link");
    panelMng.addButton("b-help", null, "help", null, null, "find");
    panelMng.addButton("b-help", null, "help", null, null, "a-F8");
    panelMng.addButton("b-help", null, "help", null, null, "a-F9");
    panelMng.addButton("b-help", null, "help", null, null, "a-F10");
    panelMng.setPosition(5, 7, 4, 14, 1, 'r');
    panelMng.addButton("b-help", null, "help", null, null, "brief");
    panelMng.addButton("b-F2", null, "help", null, null, "full");
    panelMng.addButton("b-help", null, "help", null, null, "name");
    panelMng.addButton("b-help", null, "help", null, null, "ext");
    panelMng.addButton("b-help", null, "help", null, null, "time");
    panelMng.addButton("b-help", null, "help", null, null, "size");
    panelMng.addButton("b-help", null, "help", null, null, "nat");
    panelMng.addButton("b-help", null, "help", null, null, "tree");
    panelMng.addButton("b-help", null, "help", null, null, "c-F9");
    panelMng.addButton("b-help", null, "help", null, null, "c-F10");
  }
  
  
  
  
  @Override protected final void initMain()
  { if(cargs.fileCfgCmds == null){
      mainCmd.writeError("Argument cmdcfg:CONFIGFILE should be given.");
      //mainCmd.e
  } else if(cargs.fileCfgButtonCmds == null){
    mainCmd.writeError("Argument cmdButton:CONFIGFILE should be given.");
    //mainCmd.e
  } else if(cargs.fileSelectTabPaths == null){
    mainCmd.writeError("Argument sel:SELECTFILE should be given.");
    //mainCmd.e
  } else {
      String sError;
      File fileCfg;
      sError = cmdSelector.cmdStore.readCmdCfg(fileCfg = cargs.fileCfgCmds); 
      if(sError == null){ sError = buttonCmds.readCmdCfg(fileCfg = cargs.fileCfgButtonCmds); }
      if(sError == null){ sError = selectTab.readCfg(fileCfg = cargs.fileSelectTabPaths); }
      if(sError !=null){
        mainCmd.writeError("Error reading " + fileCfg.getAbsolutePath() + ": " + sError);
      }
    }
    super.initMain();  //starts initializing of graphic. Do it after config command selector!
  
  }
  
  /**Executing in the main thread loop. It handles commands.
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  @Override public void stepMain()
  {
    
    cmdQueue.execCmds();
  }
  
  
  
  private UserActionGui actionSetCmdWorkingDir = new UserActionGui() 
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { WidgetDescriptor widgdFocus = panelMng.getWidgetInFocus();
      FileSelector fileSel = idxFileSelector.get(widgdFocus.name);
      if(fileSel !=null){ //is a FileSelector focused yet?
      //if(widgdFocus.name.startsWith("file")){
        //int ixFilePanel = widgdFocus.name.charAt(4) - '0';
        //assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);  //only such names are registered.
        //FileSelector fileSel = fileSelector[ixFilePanel];
        File file = fileSel.getSelectedFile();
        cmdQueue.setWorkingDir(file); 
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
        FileSelector fileSel = idxFileSelector.get(widgd.name);
        if(fileSel !=null){ //is a FileSelector focused yet?
        //if(widgd.name.startsWith("file")){
          //int ixFilePanel = widgd.name.charAt(4) - '0';
          //assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);  //only such names are registered.
          //FileSelector fileSel = fileSelector[ixFilePanel];
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
        cmdSelector.cmdStore.readCmdCfg(selectedFiles[0]);
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
      else if(arg.startsWith("cmdButton:")){
        cargs.fileCfgButtonCmds = new File(arg.substring(10));
      }
      else if(arg.startsWith("sel:")){
        cargs.fileSelectTabPaths = new File(arg.substring(4));
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


  UserActionGui selectPanelLeft = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { tabCmd.getCurrentPanel().setFocus();
    }
  };
  

  UserActionGui selectPanelMiddle = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { tabFile1.getCurrentPanel().setFocus();
    }
  };
  

  UserActionGui selectPanelRight = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { //fileSelector[2].setFocus();
      //tabFile2.getCurrentPanel().setFocus();
    }
  };
  
  UserActionGui selectPanelOut = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { tabCmd.getCurrentPanel().setFocus();
    }
  };
  

  UserActionGui actionEdit = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor infos, Object... params)
    { CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
      if(cmdBlock == null){ mainCmd.writeError("internal problem - don't find 'edit' command. "); }
      else {
        selectedFiles = getSelectedFile();  
        getterFiles.prepareFileSelection();
        File[] files = new File[3];
        files[0] = getterFiles.getFile1();
        files[1] = getterFiles.getFile2();
        files[2] = getterFiles.getFile3();
        cmdQueue.addCmd(cmdBlock, files);  //to execute.
      }
      ///
    }
  };
  

  void stop(){}

}
