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
import org.vishia.cmd.CmdStore;
import org.vishia.commander.target.FcmdtTarget;
import org.vishia.commander.target.FcmdtTarget_ifc;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.GralCommandSelector;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.msgDispatch.MsgDispatchSystemOutErr;
import org.vishia.util.FileRemoteAccessorLocalFile;
import org.vishia.util.KeyCode;

public class Fcmd extends GuiCfg
{


  /**Version, history and license.
   * <ul>
   * <li>1011-2012 some changes, see source files.
   * <li>2011-10-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  @SuppressWarnings("hiding")
  public static final int version = 20120617;

  /**Version visible in about info */
  public static final String sVersion = "Version 1.03 - 2012-06-17";
  
  static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;

    File fileCmdsForExt;

    File fileCfgButtonCmds;

    File fileSelectTabPaths;
    
    File dirCfg;

    File dirHtmlHelp;
  }

  //MsgDispatchSystemOutErr msgDisp;
  
  final CallingArgs cargs;

  // GralTabbedPanel tabbedPanelsLeft, tabbedPanelsMid, tabbedPanelsRight;

  final FcmdWindowMng windMng = new FcmdWindowMng(this);

  final FcmdButtons fButtons = new FcmdButtons(this);
  
  final FcmdStatusLine statusLine = new FcmdStatusLine(this);
  
  final String nameTextFieldInfo = "file-info";
  
  final String nameTextFieldFilePath = "file-path";
  
  final String nameTextFieldRunInfo = "run-info";
  
  GralPanelContent panelButtons;

  
  final FcmdFavorPathSelector favorPathSelector = new FcmdFavorPathSelector(mainCmd, this);

  final FcmdExecuter executer = new FcmdExecuter(mainCmd, this);

  final GralCommandSelector cmdSelector = new GralCommandSelector("cmdSelector", executer.cmdQueue, gralMng);

  final FcmdIdents idents = new FcmdIdents();
  
  final FcmdFileProps filePropsCmd = new FcmdFileProps(this);
  
  final FcmdView viewCmd = new FcmdView(this);
  
  final FcmdEdit editWind = new FcmdEdit(this);
  
  final FcmdCopyCmd copyCmd = new FcmdCopyCmd(this);
  
  final FcmdMkDirFile mkCmd = new FcmdMkDirFile(this);

  final FcmdDelete deleteCmd = new FcmdDelete(this);

  final FcmdFilesCp filesCp = new FcmdFilesCp(this);

  final FcmdKeyActions keyActions = new FcmdKeyActions(this);

  
  /**The current directory of the last selected file. */
  File currentFile;

  /**The last selected files of the three panels, [0] for left .. [2] for right. */
  final File[] selectedFiles123 = new File[3];
  
  
  /**The last selected file panels in its order of selection. The panel knows the last used file card there. 
   * The selected file1, file2 are anytime in one and the other panel. */
  List<FcmdLeftMidRightPanel> lastFilePanels = new LinkedList<FcmdLeftMidRightPanel>();
  
  /**The last used favor card or its last used file card.
   * It is used for delete tab. */
  FcmdFavorCard lastFavorCard;
  

  final Map<String, GralFileSelector> idxFileSelector = new TreeMap<String, GralFileSelector>();

  
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
    executer.cmdQueue.setOutput(gui.getOutputBox(), null);
  }

  /**
   * Initializes the areas for the panels and configure the panels. Note that
   * the window is initialized with an output area already. This is used for
   * output messages if problems occurs while build the rest of the GUI.
   */
  @Override
  protected void initGuiAreas(String sAreaMainPanel)
  {
    panelBuildIfc.registerUserAction("KeyAction",
        keyActions.commanderKeyActions); // all key actions, registered central

    gui.setFrameAreaBorders(30, 65, 70, 85); // x1, x2, y1, y2
    //gui.setStandardMenusGThread(new File("."), actionFile);
    gui.addMenuItemGThread("menuBarFavorsLeft", idents.menuBarNavigationLeft, selectPanelLeft);
    gui.addMenuItemGThread("menuBarFavorsMiddle", idents.menuBarNavigationMiddle, selectPanelMiddle);
    gui.addMenuItemGThread("menuBarFavorsRight", idents.menuBarNavigationRight, selectPanelRight);
    gui.addMenuItemGThread("menuBarNavigatonCmd", idents.menuBarNavigatonCmd, actionFocusCmdCard);
    
    // gui.set

    // Creates tab-Panels for the file lists and command lists.
    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelLeft.tabbedPanelFileCards = gralMng.addTabbedPanel("File0Tab", null, GralMngBuild_ifc.propZoomedPanel);
    gui.addFrameArea("A1A1", favorPathSelector.panelLeft.tabbedPanelFileCards); // dialogPanel);

    favorPathSelector.panelLeft.buildInitialTabs();
    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelMid.tabbedPanelFileCards = gralMng.addTabbedPanel("File1Tab", null, GralMngBuild_ifc.propZoomedPanel);
    gui.addFrameArea("B1B1", favorPathSelector.panelMid.tabbedPanelFileCards); // dialogPanel);
    favorPathSelector.panelMid.buildInitialTabs();

    gralMng.selectPanel("primaryWindow");
    favorPathSelector.panelRight.tabbedPanelFileCards = gralMng.addTabbedPanel("File2Tab", null, GralMngBuild_ifc.propZoomedPanel);
    gui.addFrameArea("C1C1", favorPathSelector.panelRight.tabbedPanelFileCards); // dialogPanel);
    favorPathSelector.panelRight.buildInitialTabs();

    gralMng.selectPanel("primaryWindow");
    panelButtons = gralMng.createGridPanel("Buttons", gralMng.getColor("gr"), 1, 1, 10, 10);
    gui.addFrameArea("A3C3", panelButtons); // dialogPanel);
    fButtons.initPanelButtons();

    filesCp.buildGraphic();
    filePropsCmd.buildWindowConfirmMk();  //F2
    viewCmd.buildWindowView();   //F3
    editWind.buildWindow();   //F3
    copyCmd.buildWindowConfirmCopy();
    mkCmd.buildWindowConfirmMk();
    executer.buildWindowConfirmExec();
    deleteCmd.buildWindowConfirmDelete(); //F8
    favorPathSelector.buildWindowAddFavorite();
    
    gui.addMenuItemGThread("MenuSetWorkingDir", "&Command/Set&WorkingDir", executer.actionSetCmdWorkingDir); // /
    gui.addMenuItemGThread("MenuCommandAbort", "&Command/&Abort", executer.actionCmdAbort); // /
    // gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    gui.addMenuItemGThread("MenuCmdCfgSet", "&Command/CmdCf&g - read current file", executer.actionSetCmdCfg); // /
    gui.addMenuItemGThread("menuReadCmdiCfg", "&Command/&ExtCfg - read cfg file", executer.actionReadExtensionCmd);

    this.windMng.initMenuWindMng();
    gui.addMenuItemGThread("menuHelp", idents.menuHelpBar, gui.getActionHelp());
    gui.addMenuItemGThread("menuAbout", idents.menuBarAbout, gui.getActionAbout());
    gui.addMenuItemGThread("MenuTestInfo", "&Help/&Infobox", actionTest); 
    guiW.outputBox.setActionChange(executer.actionCmdFromOutputBox);
    String sHelpUrlDir = cargs.dirHtmlHelp.getAbsolutePath();
    gui.setHelpUrl(sHelpUrlDir + "/Fcmd.html");
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
      { statusLine.widgRunInfo.setText(csq);
        return this;
      }

      @Override public Appendable append(char c) throws IOException
      { if(c == '\0') statusLine.widgRunInfo.setText(" ");
        else statusLine.widgRunInfo.setText("" + c);
        return this;
      }

      @Override public Appendable append(CharSequence csq, int start, int end) throws IOException
      { statusLine.widgRunInfo.setText(csq.subSequence(start, end));
        return null;
      }
      
    };
    
    executer.cmdQueue.execCmds(writeStatusCmd, null);
  }
  
  
  
  /**
   * Executing in the main thread loop. It handles commands.
   * 
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  @Override public void finishMain()
  { 
    try{
      executer.cmdQueue.close();  //finishes threads.
      target.close();
      FileRemoteAccessorLocalFile.getInstance().close();
    } catch(IOException exc){
      
    }
  }
  
  
  

  /**Get the last selected files in order of selection of the file panels.
   * New method since 2011-12-23
   * @return array[3] of the last selected files in the file panels. It has always a length of 3
   *   but not all elements are set ( they are ==null) if no files were selected before.
   *   The returned instance is a new one, not referenced elsewhere. It can be stored
   *   and it remains the situation of selection files independently of further user actions.
   */
  File[] getLastSelectedFiles(){
    File[] ret = new File[3];
    int ix = -1;
    Iterator<FcmdLeftMidRightPanel> iterPanel = lastFilePanels.iterator();
    while(ix < 2 && iterPanel.hasNext() ){
      FcmdLeftMidRightPanel panel = iterPanel.next();
      //for(FcmdLeftMidRightPanel panel: lastFilePanels){
      FcmdFileCard fileCard = panel.actFileCard;
      if(fileCard !=null){
        List<File> listFiles = fileCard.getSelectedFiles();
        if(listFiles !=null && listFiles.size() > 0){
          Iterator<File> iter= listFiles.iterator();
          while(ix < 2 && iter.hasNext()){
            ret[++ix] = iter.next();
          }
        } else {
          ret[++ix] = fileCard.currentFile;
        }
      } else {
        //ret[++ix] = null;  //the panel hasn't a file selected now.
      }
    }
    //Note: There may be less than 3 file panels, rest of files are null.
    return ret;
  }
  

  /**Get the last selected file cards in order of selection of the file panels.
   * @return array[3] of the last selected file cards in the file panels. It has always a length of 3
   *   but not all elements are set ( they are ==null) if the file cards are not opened before
   *   in the panel.
   *   The returned instance is a new one, not referenced elsewhere. It can be stored
   *   and it remains the situation of selection files independently of further user actions.
   */
  FcmdFileCard[] getLastSelectedFileCards(){
    FcmdFileCard[] ret = new FcmdFileCard[3];
    int ix = -1;
    for(FcmdLeftMidRightPanel panel: lastFilePanels){
      FcmdFileCard fileCard = panel.actFileCard;
      if(fileCard !=null){
        ret[++ix] = fileCard;
      } else {
        ret[++ix] = null;  //the panel hasn't a file selected now.
      }
    }
    //Note: There may be less than 3 file panels, rest of files are null.
    return ret;
  }
  
  
  
  
  
  /**Get the last selected file card in order of selection of the file panels.
   * @return The file card reference.   */
  FcmdFileCard getLastSelectedFileCard(){
    
    if(lastFavorCard!=null){
      return lastFavorCard.fileTable;
    } else {
      return null;
    }
  }
  
  
  
  
  
  

  /**
   * This class is instantiated in the static main routine and builds a command
   * line interface and the graphical frame. The mainly functionality is
   * contained in the super class.
   */
  private static class FcmdMainCmd extends GralArea9MainCmd
  {

    private final CallingArgs cargs;

    public FcmdMainCmd(CallingArgs cargs, String[] args)
    {
      super(cargs, args);
      this.cargs = cargs;
      super.addAboutInfo("The.file.Commander");
      super.addAboutInfo("made by Hartmut Schorrig, www.vishia.org, hartmut.schorrig@vishia.de");
      super.addAboutInfo(sVersion);
    }

    @Override protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;
      if (arg.startsWith("cfg:")) {
        cargs.dirCfg = new File(arg.substring(4));
      } else if (arg.startsWith("help:")) {
        File file1 = new File(arg.substring(5));
        String sPathHelpAbs = file1.getAbsolutePath();
        cargs.dirHtmlHelp = new File(sPathHelpAbs);  //should be absolute because browser.
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
    { if(selectedFiles.length >0) return selectedFiles[0];
      else return null;
    }

    @Override
    public File getFile2()
    { if(selectedFiles.length >1) return selectedFiles[1];
      else return null;
    }

    @Override
    public File getFile3()
    { if(selectedFiles.length >2) return selectedFiles[2];
      else return null;
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
    GralArea9MainCmd cmdgui = new FcmdMainCmd(cargs, args); // implements MainCmd, parses
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
      //main.msgDisp = MsgDispatchSystemOutErr.create();
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
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        favorPathSelector.panelLeft.selectTabCard.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**
   * Key alt-F2 to select a directory/cmd list in a list of directories for the
   * middle panel.
   */
  GralUserAction selectPanelMiddle = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        favorPathSelector.panelMid.selectTabCard.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**
   * Key alt-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectPanelRight = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        favorPathSelector.panelRight.selectTabCard.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Action to focus the cmd card.
   */
  GralUserAction actionFocusCmdCard = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      cmdSelector.wdgdTable.setFocus();
      return true;
      } else return false;
    }
  };

  /**
   * Key F4 for edit command. Its like Norton Commander.
   */
  GralUserAction actionEdit = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
        if (cmdBlock == null) {
          mainCmd.writeError("internal problem - don't find 'edit' command. ");
        } else {
          File[] lastSelected = getLastSelectedFiles(); 
          //create a new instance of array of files because the selection may be changed
          //till the command is invoked. The files are stored in a queue and executed in another thread. 
          File[] files = new File[1];
          if(lastSelected.length >0){
            files[0] = lastSelected[0];
          }
          File currentDir = files[0].getParentFile();
          executer.cmdQueue.addCmd(cmdBlock, files, currentDir); // to execute.
        }
      }
      return true;
    }
  };

  
  
  /**This callback will be invoked in the drag event while the mouse is released in the destination. 
   */
  GralUserAction actionDragFileFromStatusLine = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params) {
      if(key == KeyCode.dragFiles){
        String path = widgd.getValue();
        String[][] sFiles = (String[][])params[0];  //sFiles has lenght 1
        sFiles[0] = new String[1]; 
        sFiles[0][0] = path;
        stop();
      }
      return true;
    }
  };
  
  


  
  void stop()
  {
  }

}
