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
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.CommandSelector;
import org.vishia.gral.widget.FileSelector;
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
  
  GralTabbedPanel tabCmd, tabFile1, tabFile2;
  
  GralPanelContent panelButtons;
  
  final CmdQueue cmdQueue = new CmdQueue(mainCmd);
  
  private final SelectTab selectTab = new SelectTab(mainCmd, panelMng);
  
  private final CommandSelector cmdSelector = new CommandSelector(cmdQueue);
  
  private File[] selectedFiles;

  private final Map<String, FileSelector> idxFileSelector = new TreeMap<String, FileSelector>();
    //{ new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>()};
  
  GralWindow_ifc windConfirmCopy;
  
  GralGridPos posWindConfirmCopy;
  
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
    panelMng.selectPanel("primaryWindow");
    tabCmd = panelMng.createTabPanel("File0Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(1,1,1,1, tabCmd.getGuiComponent()); //dialogPanel);

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
      if(info.active == 'l'){ buildTabFromSelection(info, tabCmd); }
    }
    for(SelectTab.SelectInfo info: selectTab.selectAll){
      if(info.active == 'l'){ buildTabFromSelection(info, tabCmd); }
    }
    
      
    panelMng.selectPanel("primaryWindow");
    tabFile1 = panelMng.createTabPanel("File1Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(2,1,1,1, tabFile1.getGuiComponent()); //dialogPanel);
    
    tabFile1.addGridPanel("Sel1", "a-F2",1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTab.listMid.setToPanel(panelMng, "sel0", 5, widthSelecttable, 'A');
    selectTab.fillInMid();
    
    for(SelectTab.SelectInfo info: selectTab.selectMid){
      if(info.active == 'm'){ buildTabFromSelection(info, tabFile1); }
    }
    for(SelectTab.SelectInfo info: selectTab.selectAll){
      if(info.active == 'm'){ buildTabFromSelection(info, tabFile1); }
    }
    
    panelMng.selectPanel("primaryWindow");
    tabFile2 = panelMng.createTabPanel("File2Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(3,1,1,1, tabFile2.getGuiComponent()); //dialogPanel);
      
    tabFile2.addGridPanel("Sel1", "a-F2",1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd');
    selectTab.listRight.setToPanel(panelMng, "sel0", 5, widthSelecttable, 'A');
    selectTab.fillInRight();
    
    for(SelectTab.SelectInfo info: selectTab.selectRight){
      if(info.active == 'r'){ buildTabFromSelection(info, tabFile2); }
    }
    for(SelectTab.SelectInfo info: selectTab.selectAll){
      if(info.active == 'r'){ buildTabFromSelection(info, tabFile2); }
    }
    
    panelButtons = panelMng.createGridPanel("Buttons", panelMng.getColor("gr"), 1, 1, 10, 10);
    gui.addFrameArea(1,2,3,1, panelButtons); //dialogPanel);
    initPanelButtons();
    
    panelMng.selectPanel("output"); //Buttons");
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    panelMng.setPosition(-30, 0, -40, 0, 1, 'r');
    
    posWindConfirmCopy = panelMng.getPositionInPanel();
    windConfirmCopy = panelMng.createWindow("confirm copy", false);
    //windConfirmCopy.setWindowVisible(true);
    //panelMng.setPosition(-30, 0, 40, 40+GralGridPos.size, 1, 'r');
    //windConfirmCopy = panelMng.createWindow(null, false);
    //windConfirmCopy.setWindowVisible(true);
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
    panelMng.setPosition(3, 5, 0, 4, 1, 'd');
    panelMng.addText("alt", 'A', 0x0);
    panelMng.addText("ctr", 'A', 0x0);
    panelMng.addText("sh", 'A', 0x0);
    
    panelMng.setPosition(1, 3, 4, 14, 1, 'r');
    panelMng.addButton("b-help", null, "help", null, null, "help");
    panelMng.addButton("b-F2", null, "help", null, null, "F2");
    panelMng.addButton("b-help", null, "help", null, null, "view");
    panelMng.addButton("b-edit", actionEdit, "", null, null, "edit");
    panelMng.addButton("b-copy", actionCopy, "", null, null, "copy");
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
  
  
  /**Builds a tab for file or command view from a selected line of selection.
   * @param info The selection info
   */
  private void buildTabFromSelection(SelectTab.SelectInfo info, GralTabbedPanel tabPanel)
  { 
    tabPanel.addGridPanel(info.tabName, info.tabName,1,1,10,10);
    panelMng.setPosition(0, 0, 0, -0, 1, 'd'); //the whole panel.
    FileSelector fileSelector = new FileSelector(mainCmd);
    idxFileSelector.put(info.tabName, fileSelector);
    fileSelector.setToPanel(panelMng, info.tabName, 5, new int[]{2,20,5,10}, 'A');
    fileSelector.fillIn(new File(info.path));
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
  
  
  
  /**Action to set the working directory for the next command invocation.
   * The working directory is the directory in the focused file tab.
   * 
   */
  private GralUserAction actionSetCmdWorkingDir = new GralUserAction() 
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { GralWidget widgdFocus = panelMng.getWidgetInFocus();
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
  
  
  
  /**Routine to prepare up to 3 files, which were simple selected at last in the panels.
   * The order of focused file-panel-tables is used for that. The currently selected file
   * in any of the tables in order of last gotten focus is used to get the files.
   * It is the input for some command invocations.
   * @return Array of files in order of last focus
   */
  private File[] getSelectedFile()
  { File file[] = new File[3];
    int ixFile = 0;
    List<GralWidget> widgdFocus = panelMng.getWidgetsInFocus();
    synchronized(widgdFocus){
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while(ixFile < file.length && iterFocus.hasNext()){
        GralWidget widgd = iterFocus.next();
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
  
  
  
  /**Action to set the command list from file. It is called from menu.
   * 
   */
  private GralUserAction actionSetCmdCfg = new GralUserAction() 
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
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
  
  
  
  
  
  
  /**This class is instantiated in the static main routine and builds a command line interface
   * and the graphical frame. The mainly functionality is contained in the super class. 
   */
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
    
  } //class MainCmd
  
  
  /**Instance to get three selected files for some command line invocations.
   * 
   */
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

  
  /**This action is invoked for all general key pressed actions.
   * It tests the key and switches to the concretely action for the pressed key.
   * General keys are [F1] for help, [F4] for edit etc.  
   */
  GralUserAction actionKey = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { stop();
    }
  };
  


  /**Key alt-F1 to select a directory/cmd list in a list of directories for the left panel.
   * The original Norton Commander approach is to select a drive letter for windows.
   * Selection of paths instead are adequate.
   */
  GralUserAction selectPanelLeft = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { selectTab.listLeft.setFocus();
    }
  };
  

  /**Key alt-F2 to select a directory/cmd list in a list of directories for the middle panel. */
  GralUserAction selectPanelMiddle = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { selectTab.listMid.setFocus();
    }
  };
  

  /**Key alt-F3 to select a directory/cmd list in a list of directories for the right panel.
   */
  GralUserAction selectPanelRight = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { selectTab.listRight.setFocus();
    }
  };
  
  /**Key alt-F4 or ctrl-O to focus the output/text panel.
   * The original Norton Commander knows an output panel for the output of commands, 
   * which uses the whole display and is selected with ctrl-O.
   * This output/text panel is used for content output too. It is always visible. 
   */
  GralUserAction selectPanelOut = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { tabCmd.getCurrentPanel().setFocus();
    }
  };
  

  /**Key F4 for edit command. Its like Norton Commander. 
   */
  GralUserAction actionEdit = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
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
  

  /**Key F5 for copy command. Its like Norton Commander. 
   */
  GralUserAction actionCopy = new GralUserAction()
  { @Override public void userActionGui(String sIntension, GralWidget infos, Object... params)
    { selectedFiles = getSelectedFile();  
      getterFiles.prepareFileSelection();
      File[] files = new File[3];
      files[0] = getterFiles.getFile1();
      files[1] = getterFiles.getFile2();
      files[2] = getterFiles.getFile3();
      
      panelMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
      ///
    }
  };
  

  void stop(){}

}
