package org.vishia.commander;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.CmdGetFileArgs_ifc;
import org.vishia.cmd.CmdQueue;
import org.vishia.cmd.CmdStore;
import org.vishia.commander.target.FcmdtTarget;
import org.vishia.commander.target.FcmdtTarget_ifc;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.CommandSelector;
import org.vishia.gral.widget.FileSelector;
import org.vishia.util.FileRemote;
import org.vishia.util.FileRemoteAccessorLocalFile;

public class Fcmd extends GuiCfg
{

  static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;

    File fileCmdsForExt;

    File fileCfgButtonCmds;

    File fileSelectTabPaths;
    
    File dirCfg;

    File dirHtmlHelp;
  }

  final CallingArgs cargs;

  // GralTabbedPanel tabbedPanelsLeft, tabbedPanelsMid, tabbedPanelsRight;

  final FcmdWindowMng windMng = new FcmdWindowMng(this);

  final FcmdButtons fButtons = new FcmdButtons();
  
  GralTextField widgFileInfo, widgFilePath, widgRunInfo;
  
  final String nameTextFieldInfo = "file-info";
  
  final String nameTextFieldFilePath = "file-path";
  
  final String nameTextFieldRunInfo = "run-info";
  
  GralPanelContent panelButtons;

  final CmdQueue cmdQueue = new CmdQueue(mainCmd);

  final FcmdFavorPathSelector favorPathSelector = new FcmdFavorPathSelector(mainCmd, this);

  final FcmdExecuter executer = new FcmdExecuter(mainCmd, this);

  final CommandSelector cmdSelector = new CommandSelector("cmdSelector", cmdQueue, gralMng);

  final FcmdIdents idents = new FcmdIdents();
  
  final FcmdFileProps filePropsCmd = new FcmdFileProps(this);
  
  final FcmdView viewCmd = new FcmdView(this);
  
  final FcmdCopyCmd copyCmd = new FcmdCopyCmd(this);
  
  final FcmdMkDirFile mkCmd = new FcmdMkDirFile(this);

  final FcmdDelete deleteCmd = new FcmdDelete(this);

  final FcmdFilesCp filesCp = new FcmdFilesCp(this);

  final FcmdKeyActions keyActions = new FcmdKeyActions(this);

  
  /**The current directory of the last selected file. */
  FileRemote currentFile;

  /**The last selected files of the three panels, [0] for left .. [2] for right. */
  final FileRemote[] selectedFiles123 = new FileRemote[3];
  
  
  List<FcmdFileCard> lastFileCards = new LinkedList<FcmdFileCard>();
  
  /**The last used favor card or its last used file card.
   * It is used for delete tab. */
  FcmdFavorCard lastFavorCard;
  

  /**
   * @deprecated
   */
  FileSelector[] lastFocusedFileTables = new FileSelector[3];
  
  final Map<String, FileSelector> idxFileSelector = new TreeMap<String, FileSelector>();
  // { new TreeMap<String, FileSelector>(), new TreeMap<String, FileSelector>(),
  // new TreeMap<String, FileSelector>()};

  
  FcmdtTarget_ifc target;
  
  
  /**
   * The commands which are used for some buttons or menu items from the
   * JavaCommander itself.
   */
  final CmdStore buttonCmds;

  
  final List<Closeable> threadsToClose = new LinkedList<Closeable>();
  
  public Fcmd(CallingArgs cargs, GralArea9MainCmd cmdgui)
  {
    super(cargs, cmdgui, null, null);
    this.cargs = cargs;
    target = new FcmdtTarget();  //create the target itself, one process TODO experience with remote target.
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
    //gui.setStandardMenusGThread(new File("."), actionFile);
    gui.addMenuItemGThread("menuSaveFavoriteSel", idents.menuSaveFavoriteSel, favorPathSelector.actionSaveFavoritePathes); // /

    this.windMng.initMenuWindMng();
    // gui.set

    // Creates tab-Panels for the file lists and command lists.
    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelLeft.tabbedPanelFileCards = gralMng.addTabbedPanel("File0Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(1, 1, 1, 1, favorPathSelector.panelLeft.tabbedPanelFileCards); // dialogPanel);

    favorPathSelector.panelLeft.buildInitialTabs();
    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelMid.tabbedPanelFileCards = gralMng.addTabbedPanel("File1Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(2, 1, 1, 1, favorPathSelector.panelMid.tabbedPanelFileCards); // dialogPanel);
    favorPathSelector.panelMid.buildInitialTabs();

    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelRight.tabbedPanelFileCards = gralMng.addTabbedPanel("File2Tab", null, GralGridBuild_ifc.propZoomedPanel);
    gui.addFrameArea(3, 1, 1, 1, favorPathSelector.panelRight.tabbedPanelFileCards); // dialogPanel);
    favorPathSelector.panelRight.buildInitialTabs();

    gralMng.selectPanel("primaryWindow");
    panelButtons = gralMng.createGridPanel("Buttons", gralMng.getColor("gr"),
        1, 1, 10, 10);
    gui.addFrameArea(1, 3, 3, 1, panelButtons); // dialogPanel);
    initPanelButtons();

    filesCp.buildGraphic();
    filePropsCmd.buildWindowConfirmMk();  //F2
    viewCmd.buildWindowView();   //F3
    copyCmd.buildWindowConfirmCopy();
    mkCmd.buildWindowConfirmMk();
    executer.buildWindowConfirmExec();
    deleteCmd.buildWindowConfirmDelete(); //F8
    favorPathSelector.buildWindowAddFavorite();
    gui.addMenuItemGThread("menuHelp", "&Help/&Help", gui.getActionHelp());
    gui.addMenuItemGThread("menuAbout", "&Help/&About", gui.getActionAbout());
    gui.addMenuItemGThread("MenuTestInfo", "&Help/&Infobox", actionTest); 
    guiW.outputBox.setActionChange(executer.actionCmdFromOutputBox);
    
    gui.setHelpUrl("/home/hartmut/vishia/JavaCommander/sf/JavaCommander/help/Fcmd.html");
  }

  private void initPanelButtons()
  {
    gralMng.selectPanel("Buttons");
    gralMng.setPosition(0, 2, 0, 0, 1, 'r');
    widgFilePath = gralMng.addTextField(nameTextFieldFilePath, false, null, null);
    gralMng.setPosition(2, 4, 0, 9.8f, 1, 'r');
    widgRunInfo = gralMng.addTextField(nameTextFieldRunInfo, false, null, null);
    gralMng.setPosition(2, 4, 10, 0, 1, 'r');
    widgFileInfo = gralMng.addTextField(nameTextFieldInfo, false, null, null);
    
    gralMng.setPosition(4, GralPos.size + 1, 10, 20, 1, 'r');
    gralMng.addText("F1", 'A', 0x0);
    gralMng.addText("F2", 'A', 0x0);
    gralMng.addText("F3", 'A', 0x0);
    gralMng.addText("F4", 'A', 0x0);
    gralMng.addText("F5", 'A', 0x0);
    gralMng.addText("F6", 'A', 0x0);
    gralMng.addText("F7", 'A', 0x0);
    gralMng.addText("F8", 'A', 0x0);
    gralMng.addText("F9", 'A', 0x0);
    gralMng.addText("F10", 'A', 0x0);
    gralMng.setPosition(5, 7, 0, 4, 1, 'd');
    gralMng.addText("alt -", 'A', 0x0);
    gralMng.addText("ctr -", 'A', 0x0);
    gralMng.addText("sh  -", 'A', 0x0);

    gralMng.setPosition(5, 7, 4, 14, 1, 'r');
    gralMng.addButton("b-help", null, "help", null, null, "help");
    gralMng.addButton("b-F2", null, "help", null, null, "F2");
    gralMng.addButton("b-help", null, "help", null, null, "view");
    gralMng.addButton("b-edit", actionEdit, "", null, null, "edit");
    gralMng.addButton("b-copy", copyCmd.actionConfirmCopy, "", null, null, "copy");
    gralMng.addButton("b-help", null, "help", null, null, "move");
    gralMng.addButton("b-help", null, "help", null, null, "mkdir");
    fButtons.buttonDel = gralMng.addButton("b-delete", deleteCmd.actionConfirmDelete, "del", null, null, "del");
    gralMng.addButton("b-help", null, "help", null, null, "cmd");
    gralMng.addButton("b-help", null, "help", null, null, "F10");
    gralMng.setPosition(7, 9, 4, 14, 1, 'r');
    gralMng.addButton("selectLeft", selectPanelLeft, "selectLeft", null, null, "left");
    gralMng.addButton("selectMiddle", selectPanelMiddle, "help", null, null, "middle");
    gralMng.addButton("selectRight", selectPanelRight, "", null, null, "right");
    gralMng.addButton("b-help", null, "help", null, null, "zip");
    gralMng.addButton("selectCmd", actionFocusCmdCard, "", null, null, "cmd");
    gralMng.addButton("b-help", null, "help", null, null, "link");
    gralMng.addButton("b-help", null, "help", null, null, "find");
    gralMng.addButton("b-help", null, "help", null, null, "a-F8");
    gralMng.addButton("b-help", null, "help", null, null, "a-F9");
    gralMng.addButton("b-help", null, "help", null, null, "a-F10");
    gralMng.setPosition(9, 11, 4, 14, 1, 'r');
    gralMng.addButton("b-help", null, "help", null, null, "brief");
    gralMng.addButton("b-F2", null, "help", null, null, "full");
    gralMng.addButton("b-help", null, "help", null, null, "name");
    gralMng.addButton("b-help", null, "help", null, null, "ext");
    gralMng.addButton("b-help", null, "help", null, null, "time");
    gralMng.addButton("b-help", null, "help", null, null, "size");
    gralMng.addButton("b-help", null, "help", null, null, "nat");
    gralMng.addButton("b-help", null, "help", null, null, "tree");
    gralMng.addButton("b-help", null, "help", null, null, "c-F9");
    gralMng.addButton("b-help", null, "help", null, null, "c-F10");
    gralMng.setPosition(11, 13, 4, 14, 1, 'r');
    gralMng.addButton("b-help", null, "help", null, null, "brief");
    gralMng.addButton("b-F2", null,   "help", null, null, "full");
    gralMng.addButton("b-help", null, "help", null, null, "name");
    gralMng.addButton("b-help", null, "help", null, null, "ext");
    gralMng.addButton("b-help", null, "help", null, null, "time");
    gralMng.addButton("b-help", null, "help", null, null, "size");
    gralMng.addButton("b-help", null, "help", null, null, "nat");
    gralMng.addButton("b-help", null, "help", null, null, "tree");
    gralMng.addButton("b-help", null, "help", null, null, "c-F9");
    gralMng.addButton("b-help", null, "help", null, null, "c-F10");
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
        sError = favorPathSelector.readCfg(fileCfg = cargs.fileSelectTabPaths);
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
  @Override public void stepMain()
  {
    Appendable writeStatusCmd = new Appendable(){

      @Override public Appendable append(CharSequence csq) throws IOException
      { widgRunInfo.setText(csq);
        return this;
      }

      @Override public Appendable append(char c) throws IOException
      { if(c == '\0') widgRunInfo.setText(" ");
        else widgRunInfo.setText("" + c);
        return this;
      }

      @Override public Appendable append(CharSequence csq, int start, int end) throws IOException
      { widgRunInfo.setText(csq.subSequence(start, end));
        return null;
      }
      
    };
    
    cmdQueue.execCmds(writeStatusCmd, null);
  }
  
  
  
  /**
   * Executing in the main thread loop. It handles commands.
   * 
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  @Override public void finishMain()
  { 
    try{
      cmdQueue.close();  //finishes threads.
      target.close();
      FileRemoteAccessorLocalFile.getInstance().close();
    } catch(IOException exc){
      
    }
  }
  
  
  

  /**Routine to prepare up to 3 files, which were simple selected at last in the
   * panels. The order of focused file-panel-tables is used for that. The
   * currently selected file in any of the tables in order of last gotten focus
   * is used to get the files. It is the input for some command invocations.
   * @deprecated use {@link #lastFileCards} or {@link #getLastSelectedFiles()} instead.
   * @return Array of files in order of last focus
   */
  FileRemote[] getCurrentFileInLastPanels()
  { findLastFocusedFileTables();
    FileRemote file[] = new FileRemote[3];
    int ixFile = -1;
    for(FileSelector fileTable: lastFocusedFileTables){
      if(fileTable !=null){
        FileRemote fileItem = fileTable.getSelectedFile();
        if(fileItem !=null){
          file[++ixFile] = fileItem;
        }
      }
    }
    return file;
  }


  /**Routine to find out the last focused file tables in order of focus.
   * 
   * @set lastFocusedFileTables
   * @deprecated
   */
  void findLastFocusedFileTables(){
    int ixFile = 0;
    List<GralWidget> widgdFocus = gralMng.getWidgetsInFocus();
    synchronized (widgdFocus) {
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while (ixFile < lastFocusedFileTables.length && iterFocus.hasNext()) {
        GralWidget widgd = iterFocus.next();
        FileSelector fileSel = idxFileSelector.get(widgd.name);
        if (fileSel != null) { // is a FileSelector focused yet?
          // if(widgd.name.startsWith("file")){
          // int ixFilePanel = widgd.name.charAt(4) - '0';
          // assert(ixFilePanel >=0 && ixFilePanel < fileSelector.length);
          // //only such names are registered.
          // FileSelector fileSel = fileSelector[ixFilePanel];
          lastFocusedFileTables[ixFile++] = fileSel;
        }
      }
    }
  }
  
  
  
  
  /**Returns all selected files in the last actual file table.
   * @deprecated
   * @return Array of files which are selected, The array has the length 1 if only one file is selected.
   */
  private List<String> getSelectedFilesInLastPanel()
  {
    List<GralWidget> widgdFocus = gralMng.getWidgetsInFocus();
    FileSelector fileSel = null;
    synchronized (widgdFocus) {
      Iterator<GralWidget> iterFocus = widgdFocus.iterator();
      while (fileSel == null && iterFocus.hasNext()) {
        GralWidget widgd = iterFocus.next();
        fileSel = idxFileSelector.get(widgd.name);
      }
    }
    if(fileSel !=null){
      return fileSel.getSelectedFiles();
    } else return null;
  }

  
  
  /**Get the last selected files in order of selection.
   * New method since 2011-12-23
   * @return array of the last selected files. It hast length ==0 of nothing was selected before. 
   */
  FileRemote[] getLastSelectedFiles(){
    FileRemote[] ret = new FileRemote[lastFileCards.size()];
    int ix = -1;
    for(FcmdFileCard card: lastFileCards){
      ret[++ix] = card.currentFile;
    }
    return ret;
  }
  

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

    @Override protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;
      if (arg.startsWith("cfg:")) {
        cargs.dirCfg = new File(arg.substring(4));
      } else if (arg.startsWith("help:")) {
        cargs.dirHtmlHelp = new File(arg.substring(5));
      } else if (arg.startsWith("cmdcfg:")) {
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
    
    @Override protected boolean checkArguments(){
      boolean bOk = true;
      if(cargs.dirCfg == null) { bOk = false; writeError("cmdline argument cfg:PATHCFG is missing"); }
      if(bOk){
        if(!cargs.dirCfg.exists()){ bOk = false; writeError("cmdline argument cfg is faulty:" + cargs.dirCfg.getAbsolutePath());}
      }
      if(bOk){
        if(cargs.dirHtmlHelp ==null){ cargs.dirHtmlHelp = new File(cargs.dirCfg, "../help"); }
        if(cargs.fileCfgCmds ==null){ cargs.fileCfgCmds = new File(cargs.dirCfg, "cmd.cfg"); }
        if(cargs.fileCmdsForExt ==null){ cargs.fileCmdsForExt = new File(cargs.dirCfg, "ext.cfg"); }
        if(cargs.fileCfgButtonCmds ==null){ cargs.fileCfgButtonCmds = new File(cargs.dirCfg, "cmdi.cfg"); }
        if(cargs.fileSelectTabPaths ==null){ cargs.fileSelectTabPaths = new File(cargs.dirCfg, "path.cfg"); }
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
    /**
     * 
     */
    File[] selectedFiles;

    @Override
    public void prepareFileSelection()
    {
      selectedFiles = getLastSelectedFiles(); //  getCurrentFileInLastPanels();
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
    bOk = cmdgui.parseArgumentsAndInitGraphic("The.file.Commander", "2A2C");

    /*
    try {
      cmdgui.parseArguments();
    } catch (Exception exception) {
      cmdgui.writeError("Cmdline argument error:", exception);
      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      bOk = false; // not exiting, show error in GUI
    }
    */
    if (bOk) {
      // Uses socket communication for InterprocessComm, therefore load the
      // factory.
      new InterProcessCommFactorySocket();
      //
      // Initialize this main class and execute.
      Fcmd main = new Fcmd(cargs, cmdgui);
      main.execute();
    }
    cmdgui.exit();
  }

  /**
   * This action is invoked for all general key pressed actions. It tests the
   * key and switches to the concretely action for the pressed key. General keys
   * are [F1] for help, [F4] for edit etc.
   */
  GralUserAction actionTest = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      try{ guiW.infoBox.append("Test\n"); }
      catch(IOException exc){}
      guiW.infoBox.setWindowVisible(true);
      return true;
    }
  };

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
      favorPathSelector.panelLeft.selectTabCard.wdgdTable.setFocus();
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
      favorPathSelector.panelMid.selectTabCard.wdgdTable.setFocus();
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
      favorPathSelector.panelRight.selectTabCard.wdgdTable.setFocus();
      return true;
    }
  };

  /**Action to focus the cmd card.
   */
  GralUserAction actionFocusCmdCard = new GralUserAction()
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      favorPathSelector.panelMid.tabbedPanelFileCards.getFocusedTab().setFocus();
      return true;
    }
  };

  /**
   * Key F4 for edit command. Its like Norton Commander.
   */
  GralUserAction actionEdit = new GralUserAction()
  {
    @Override public boolean userActionGui(String sIntension, GralWidget infos, Object... params) {
      CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
      if (cmdBlock == null) {
        mainCmd.writeError("internal problem - don't find 'edit' command. ");
      } else {
        FileRemote[] lastSelected = getLastSelectedFiles(); 
        //create a new instance of array of files because the selection may be changed
        //till the command is invoked. The files are stored in a queue and executed in another thread. 
        File[] files = new File[1];
        if(lastSelected.length >0){
          files[0] = lastSelected[0];
        }
        cmdQueue.addCmd(cmdBlock, files, null); // to execute.
      }
      return true;
    }
  };


  
  void stop()
  {
  }

}
