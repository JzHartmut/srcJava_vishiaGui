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
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.ifc.GralGridBuild_ifc;
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

    File fileCmdsForExt;

    File fileCfgButtonCmds;

    File fileSelectTabPaths;
  }

  private final CallingArgs cargs;

  // GralTabbedPanel tabbedPanelsLeft, tabbedPanelsMid, tabbedPanelsRight;

  final WindowMng windMng = new WindowMng(this);

  GralPanelContent panelButtons;

  final CmdQueue cmdQueue = new CmdQueue(mainCmd);

  final SelectTab selectTab = new SelectTab(mainCmd, this);

  final Executer executer = new Executer(mainCmd, this);

  final CommandSelector cmdSelector = new CommandSelector("cmdSelector", cmdQueue, panelMng);

  final CopyCmd copyCmd = new CopyCmd(this);

  private final JavaCmdKeyActions keyActions = new JavaCmdKeyActions(this);

  private File[] selectedFiles;

  final Map<String, FileSelector> idxFileSelector = new TreeMap<String, FileSelector>();
  // { new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>(),
  // new TreeMap<String, FileSelector>()};

  /**
   * The commands which are used for some buttons or menu items from the
   * JavaCommander itself.
   */
  final CmdStore buttonCmds;

  public JavaCmd(CallingArgs cargs, GralArea9MainCmd cmdgui)
  {
    super(cargs, cmdgui, null);
    this.cargs = cargs;
    buttonCmds = new CmdStore();
    cmdQueue.setOutput(gui.getOutputBox(), null);
  }

  /**
   * Initializes the areas for the panels and configure the panels. Note that
   * the window is initialized with an output area already. This is used for
   * output messages if problems occurs while build the rest of the GUI.
   */
  @Override
  protected void initGuiAreas()
  {
    panelBuildIfc.registerUserAction("KeyAction",
        keyActions.commanderKeyActions); // all key actions, registered central

    gui.setFrameAreaBorders(30, 65, 70, 85); // x1, x2, y1, y2
    gui.setStandardMenusGThread(new File("."), actionFile);
    gui.addMenuItemGThread("&Command/Set&WorkingDir", actionSetCmdWorkingDir); // /
    gui.addMenuItemGThread("&Command/&Abort", executer.actionCmdAbort); // /
    // gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Set", actionSetCmdCfg); // /
    gui.addMenuItemGThread("&Command/CmdCf&gFile/&Check", actionSetCmdCfg); // /
    this.windMng.initMenuWindMng();
    // gui.set

    // Creates tab-Panels for the file lists and command lists.
    panelMng.selectPanel("primaryWindow");
    selectTab.panelLeft.tabbedPanel = panelMng.createTabPanel("File0Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(1, 1, 1, 1, selectTab.panelLeft.tabbedPanel.getGuiComponent()); // dialogPanel);

    selectTab.buildInitialTabs(selectTab.panelLeft);
    panelMng.selectPanel("primaryWindow");
    selectTab.panelMid.tabbedPanel = panelMng.createTabPanel("File1Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(2, 1, 1, 1, selectTab.panelMid.tabbedPanel.getGuiComponent()); // dialogPanel);
    selectTab.buildInitialTabs(selectTab.panelMid);

    panelMng.selectPanel("primaryWindow");
    selectTab.panelRight.tabbedPanel = panelMng.createTabPanel("File2Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(3, 1, 1, 1, selectTab.panelRight.tabbedPanel.getGuiComponent()); // dialogPanel);
    selectTab.buildInitialTabs(selectTab.panelRight);

    panelMng.selectPanel("primaryWindow");
    panelButtons = panelMng.createGridPanel("Buttons", panelMng.getColor("gr"),
        1, 1, 10, 10);
    gui.addFrameArea(1, 3, 3, 1, panelButtons); // dialogPanel);
    initPanelButtons();

    copyCmd.buildWindowConfirmCopy();
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
    panelMng.addButton("selectLeft", selectPanelLeft, "selectLeft", null, null,
        "left");
    panelMng.addButton("selectMiddle", selectPanelMiddle, "help", null, null,
        "middle");
    panelMng
        .addButton("selectRight", selectPanelRight, "", null, null, "right");
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

  @Override
  protected final void initMain()
  {
    if (cargs.fileCfgCmds == null) {
      mainCmd.writeError("Argument cmdcfg:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileCmdsForExt == null) {
      mainCmd.writeError("Argument cmdext:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileCfgButtonCmds == null) {
      mainCmd.writeError("Argument cmdButton:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileSelectTabPaths == null) {
      mainCmd.writeError("Argument sel:SELECTFILE should be given.");
      // mainCmd.e
    } else {
      String sError;
      File fileCfg;
      sError = cmdSelector.cmdStore.readCmdCfg(fileCfg = cargs.fileCfgCmds);
      if (sError == null) {
        sError = executer.readCmdFile(fileCfg = cargs.fileCmdsForExt);
      }
      if (sError == null) {
        sError = buttonCmds.readCmdCfg(fileCfg = cargs.fileCfgButtonCmds);
      }
      if (sError == null) {
        sError = selectTab.readCfg(fileCfg = cargs.fileSelectTabPaths);
      }
      if (sError != null) {
        mainCmd.writeError("Error reading " + fileCfg.getAbsolutePath() + ": "
            + sError);
      }
    }
    super.initMain(); // starts initializing of graphic. Do it after config
                      // command selector!

  }

  /**
   * Executing in the main thread loop. It handles commands.
   * 
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  @Override
  public void stepMain()
  {

    cmdQueue.execCmds();
  }

  /**
   * Action to set the working directory for the next command invocation. The
   * working directory is the directory in the focused file tab.
   * 
   */
  private GralUserAction actionSetCmdWorkingDir = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      GralWidget widgdFocus = panelMng.getWidgetInFocus();
      FileSelector fileSel = idxFileSelector.get(widgdFocus.name);
      if (fileSel != null) { // is a FileSelector focused yet?
        // if(widgdFocus.name.startsWith("file")){
        // int ixFilePanel = widgdFocus.name.charAt(4) - '0';
        // assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length); //only
        // such names are registered.
        // FileSelector fileSel = fileSelector[ixFilePanel];
        File file = fileSel.getSelectedFile();
        cmdQueue.setWorkingDir(file);
      }
      stop();
      if (sIntension.equals("")) {
        stop();
      }
      return true;
    }

  };

  /**
   * Routine to prepare up to 3 files, which were simple selected at last in the
   * panels. The order of focused file-panel-tables is used for that. The
   * currently selected file in any of the tables in order of last gotten focus
   * is used to get the files. It is the input for some command invocations.
   * 
   * @return Array of files in order of last focus
   */
  private File[] getSelectedFile()
  {
    File file[] = new File[3];
    int ixFile = 0;
    List<GralWidget> widgdFocus = panelMng.getWidgetsInFocus();
    synchronized (widgdFocus) {
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while (ixFile < file.length && iterFocus.hasNext()) {
        GralWidget widgd = iterFocus.next();
        FileSelector fileSel = idxFileSelector.get(widgd.name);
        if (fileSel != null) { // is a FileSelector focused yet?
          // if(widgd.name.startsWith("file")){
          // int ixFilePanel = widgd.name.charAt(4) - '0';
          // assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);
          // //only such names are registered.
          // FileSelector fileSel = fileSelector[ixFilePanel];
          file[ixFile++] = fileSel.getSelectedFile();
        }
      }
    }
    return file;
  }

  /**
   * Action to set the command list from file. It is called from menu.
   * 
   */
  private GralUserAction actionSetCmdCfg = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectedFiles = getSelectedFile();
      if (selectedFiles[0] != null) {
        cmdSelector.cmdStore.readCmdCfg(selectedFiles[0]);
        cmdSelector.fillIn();
      }
      stop();
      if (sIntension.equals("")) {
        stop();
      }
      return true;
    }

  };

  /**
   * This class is instantiated in the static main routine and builds a command
   * line interface and the graphical frame. The mainly functionality is
   * contained in the super class.
   */
  private static class MainCmd extends GralArea9MainCmd
  {

    private final CallingArgs cargs;

    public MainCmd(CallingArgs cargs, String[] args)
    {
      super(cargs, args);
      this.cargs = cargs;
    }

    @Override
    protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;
      if (arg.startsWith("cmdcfg:")) {
        cargs.fileCfgCmds = new File(arg.substring(7));
      } else if (arg.startsWith("cmdext:")) {
        cargs.fileCmdsForExt = new File(arg.substring(7));
      } else if (arg.startsWith("cmdButton:")) {
        cargs.fileCfgButtonCmds = new File(arg.substring(10));
      } else if (arg.startsWith("sel:")) {
        cargs.fileSelectTabPaths = new File(arg.substring(4));
      } else {
        bOk = super.testArgument(arg, nArg);
      }
      return bOk;
    }

  } // class MainCmd

  /**
   * Instance to get three selected files for some command line invocations.
   * 
   */
  CmdGetFileArgs_ifc getterFiles = new CmdGetFileArgs_ifc()
  {
    @Override
    public void prepareFileSelection()
    {
      selectedFiles = getSelectedFile();
    }

    @Override
    public File getFileSelect()
    {
      return selectedFiles[0];
    }

    @Override
    public File getFile1()
    {
      return selectedFiles[0];
    }

    @Override
    public File getFile2()
    {
      return selectedFiles[1];
    }

    @Override
    public File getFile3()
    {
      return selectedFiles[2];
    }
  };

  static void testT1()
  {
    float R = 10000.0f;
    float C = 0.000000001f;
    float tStep = 0.0000001f;
    float uc = 0.0f;
    float ue = 1.0f;
    for (int step = 0; step < 100; ++step) {
      float iR = (ue - uc) / R;
      uc = uc + iR / C * tStep;
    }

  }

  /**
   * The command-line-invocation (primary command-line-call.
   * 
   * @param args
   *          Some calling arguments are taken. This is the GUI-configuration
   *          especially.
   */
  public static void main(String[] args)
  {
    testT1();
    boolean bOk = true;
    CallingArgs cargs = new CallingArgs();
    // Initializes the GUI till a output window to show information.
    // Uses the commonly GuiMainCmd class because here are not extra arguments.
    GralArea9MainCmd cmdgui = new MainCmd(cargs, args); // implements MainCmd, parses
                                                  // calling arguments
    bOk = cmdgui.parseArgumentsAndInitGraphic("The.file.Commander", "2B2C");

    try {
      cmdgui.parseArguments();
    } catch (Exception exception) {
      cmdgui.writeError("Cmdline argument error:", exception);
      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      bOk = false; // not exiting, show error in GUI
    }
    if (bOk) {
      // Uses socket communication for InterprocessComm, therefore load the
      // factory.
      new InterProcessCommFactorySocket();
      //
      // Initialize this main class and execute.
      JavaCmd main = new JavaCmd(cargs, cmdgui);
      main.execute();
    }
    cmdgui.exit();
  }

  /**
   * This action is invoked for all general key pressed actions. It tests the
   * key and switches to the concretely action for the pressed key. General keys
   * are [F1] for help, [F4] for edit etc.
   */
  GralUserAction actionKey = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      stop();
      return true;
    }
  };

  /**
   * Key alt-F1 to select a directory/cmd list in a list of directories for the
   * left panel. The original Norton Commander approach is to select a drive
   * letter for windows. Selection of paths instead are adequate.
   */
  GralUserAction selectPanelLeft = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectTab.panelLeft.selectTable.setFocus();
      return true;
    }
  };

  /**
   * Key alt-F2 to select a directory/cmd list in a list of directories for the
   * middle panel.
   */
  GralUserAction selectPanelMiddle = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectTab.panelMid.selectTable.setFocus();
      return true;
    }
  };

  /**
   * Key alt-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectPanelRight = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectTab.panelRight.selectTable.setFocus();
      return true;
    }
  };

  /**
   * Key alt-F4 or ctrl-O to focus the output/text panel. The original Norton
   * Commander knows an output panel for the output of commands, which uses the
   * whole display and is selected with ctrl-O. This output/text panel is used
   * for content output too. It is always visible.
   */
  GralUserAction selectPanelOut = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectTab.panelMid.tabbedPanel.getCurrentPanel().setFocus();
      return true;
    }
  };

  /**
   * Key F4 for edit command. Its like Norton Commander.
   */
  GralUserAction actionEdit = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
      if (cmdBlock == null) {
        mainCmd.writeError("internal problem - don't find 'edit' command. ");
      } else {
        selectedFiles = getSelectedFile();
        getterFiles.prepareFileSelection();
        File[] files = new File[3];
        files[0] = getterFiles.getFile1();
        files[1] = getterFiles.getFile2();
        files[2] = getterFiles.getFile3();
        cmdQueue.addCmd(cmdBlock, files, null); // to execute.
      }
      return true;
    }
  };

  /**
   * Key F5 for copy command. Its like Norton Commander.
   */
  GralUserAction actionCopy = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      selectedFiles = getSelectedFile();
      getterFiles.prepareFileSelection();
      //File[] files = new File[3];
      File fileSrc = getterFiles.getFile1();
      File fileDst = getterFiles.getFile2();
      //files[2] = getterFiles.getFile3();
      copyCmd.confirmCopy(fileDst, fileSrc);
      return true;
      // /
    }
  };

  void stop()
  {
  }

}
