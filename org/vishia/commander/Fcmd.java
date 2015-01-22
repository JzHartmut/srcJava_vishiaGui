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
import org.vishia.fileLocalAccessor.FileAccessorLocalJava6;
import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralCommandSelector;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.msgDispatch.MsgRedirectConsole;
import org.vishia.util.KeyCode;

/**This class is the main class of the-File-commander.
 * @author Hartmut Schorrig
 *
 */
public class Fcmd extends GuiCfg
{


  /**Version, history and license.
   * <ul>
   * <li>2014-12-26 Hartmut new: {@link #refreshFilePanel(FileRemote)} can be called in any callback thread. 
   * <li>2011-2013 some changes, see source files.
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
  //@SuppressWarnings("hiding")
  public static final String sVersion = "2015-01-06";

  /**Version visible in about info */
  //public static final String sVersion = "Version 1.15 - 2014-02-21";
  
  static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;

    File fileCmdsForExt;

    File fileCfgButtonCmds;

    File fileSelectTabPaths;
    
    File dirCfg;

    File dirHtmlHelp;
    
    File msgConfig;
  }

  /**
   * This action is invoked for all general key pressed actions. It tests the
   * key and switches to the concretely action for the pressed key. General keys
   * are [F1] for help, [F4] for edit etc.
   */
  GralUserAction actionTest = new GralUserAction("actionTest")
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
  GralUserAction actionKey = new GralUserAction("actionKey")
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      stop();
      return true;
    }
  };

  /**Key alt-F1 to select a directory/cmd list in a list of directories for the
   * left panel. The original Norton Commander approach is to select a drive
   * letter for windows. Selection of paths instead are adequate.
   */
  GralUserAction actionReadMsgConfig = new GralUserAction("actionReadMsgConfig")
  {
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        if(cargs.msgConfig !=null && cargs.msgConfig.exists()){
          msgDisp.readConfig(cargs.msgConfig);
        }
        return true;
      } else return false;
    }
  };

  /**Key alt-F1 to select a directory/cmd list in a list of directories for the
   * left panel. The original Norton Commander approach is to select a drive
   * letter for windows. Selection of paths instead are adequate.
   */
  GralUserAction selectCardThemesLeft = new GralUserAction("selectCardThemesLeft")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        favorPathSelector.panelLeft.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key alt-F2 to select a directory/cmd list in a list of directories for the
   * middle panel.
   */
  GralUserAction selectCardThemesMiddle = new GralUserAction("selectCardThemesMiddle")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        favorPathSelector.panelMid.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key alt-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectCardThemesRight = new GralUserAction("selectCardThemesRight")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        favorPathSelector.panelRight.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F1 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardLeft = new GralUserAction("selectFileCardLeft")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        favorPathSelector.panelLeft.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F2 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardMid = new GralUserAction("selectFileCardMid")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        favorPathSelector.panelMid.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardRight = new GralUserAction("selectFileCardRight")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        favorPathSelector.panelRight.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardOther = new GralUserAction("selectFileCardOther")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(lastFilePanels.size() >=2){
          FcmdLeftMidRightPanel otherPanel = lastFilePanels.get(1);
          otherPanel.setFocus();
        }
        return true;
      } else return false;
    }
  };
  

  GralUserAction actionFocusPanelToLeft = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      FcmdLeftMidRightPanel actPanel = lastFilePanels.get(0);
      if(actPanel.cc == 'm'){ favorPathSelector.panelLeft.setFocus(); }
      else if(actPanel.cc == 'r'){ favorPathSelector.panelMid.setFocus(); }
      return true; 
    }
  };
  

  GralUserAction actionFocusPanelToRight = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      FcmdLeftMidRightPanel actPanel = lastFilePanels.get(0);
      if(actPanel.cc == 'm'){ favorPathSelector.panelRight.setFocus(); }
      else if(actPanel.cc == 'l'){ favorPathSelector.panelMid.setFocus(); }
      return true; 
    }
  };
  

  GralUserAction actionFocusFileCard = new GralUserAction("FcmdLeftMidRightPanel.actionFileCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      return true; 
    }
  };
  

  GralUserAction actionFocusThemeCard = new GralUserAction("FcmdLeftMidRightPanel.actionThemeCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      return true; 
    }
  };
  

  
  

  /**Action to focus the cmd card.
   */
  GralUserAction actionFocusCmdCard = new GralUserAction("actionFocusCmdCard")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      cmdSelector.wdgdTable.setFocus();
      return true;
      } else return false;
    }
  };
  
  
  
  GralUserAction actionFocusCardInPanelToLeft = new GralUserAction("FcmdLeftMidRightPanel.actionLeftCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      lastFilePanels.get(0).focusLeftCard();
      return true; 
    }
  };
  
  
  GralUserAction actionFocusCardInPanelToRight = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      //sets focus to right
      lastFilePanels.get(0).focusRightCard();
      return true; 
    }
  };
  

  
  
  void openExtEditor(File file){
    CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
    if (cmdBlock == null) {
      mainCmd.writeError("internal problem - don't find 'edit' command. ");
    } else {
      File[] files = new File[3]; 
      files[0] = file;
      executer.cmdQueue.addCmd(cmdBlock, files, file.getParentFile()); // to execute.
    }
    
  }
  
  

  /**
   * Key F4 for edit command. Its like Norton Commander.
   */
  GralUserAction actionEdit = new GralUserAction("actionEdit")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        CmdStore.CmdBlock cmdBlock = buttonCmds.getCmd("edit");
        if (cmdBlock == null) {
          mainCmd.writeError("internal problem - don't find 'edit' command. ");
        } else {
          File[] lastSelected = getLastSelectedFiles(false, 1); 
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
  GralUserAction actionDragFileFromStatusLine = new GralUserAction("actionDragFileFromStatusLine"){
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
  
  

  //MsgDispatchSystemOutErr msgDisp;
  
  /**Set the singleton reference before other instances are initialized which uses main. */
  { Fcmd.main = this;
  }
  
  
  final CallingArgs cargs;
  
  final MsgRedirectConsole msgDisp;
  
  final FileCluster fileCluster = new FileCluster();

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

  final GralCommandSelector cmdSelector = new GralCommandSelector("cmdSelector", 5, new int[]{0,-10}, 'A', executer.cmdQueue, gralMng);

  final FcmdSettings settings = new FcmdSettings(this);
  
  final FcmdStatus status = new FcmdStatus(this);
  
  final FcmdFileProps filePropsCmd = new FcmdFileProps(this);
  
  final FcmdView viewCmd = new FcmdView(this);
  
  final FcmdEdit editWind = new FcmdEdit(this);
  
  final FcmdCopyCmprDel copyCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.copy);
  
  final FcmdCopyCmprDel delCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.delete);
  
  final FcmdCopyCmprDel compareCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.compare);
  
  final FcmdMkDirFile mkCmd = new FcmdMkDirFile(this);

  final FcmdDelete deleteCmd = new FcmdDelete(this);

  final FcmdFilesCp filesCp = new FcmdFilesCp(this);

  final FcmdKeyActions keyActions = new FcmdKeyActions(this);

  /**
   * Note: this should be the last aggregate of Fcmd because it uses some other ones. */
  final FcmdIdents idents = new FcmdIdents(this);
  
  
  /**The current directory of the last selected file. */
  //FileRemote currentFile;

  /**The last selected files of the three panels, [0] for left .. [2] for right. */
  final File[] selectedFiles123 = new File[3];
  
  
  /**The last selected file panels in its order of selection.
   * This list contains max 3 entries. 
   * To get the last selected files: The panel knows the last used file card there. 
   * The selected file1, file2 are anytime in one and the other panel. */
  List<FcmdLeftMidRightPanel> lastFilePanels = new LinkedList<FcmdLeftMidRightPanel>();
  
  /**The last used favor card or its last used file card.
   * It is used for delete tab. */
  FcmdFavorCard lastFavorCard;
  
  FcmdFileCard currentFileCard;
  

  final Map<String, GralFileSelector> idxFileSelector = new TreeMap<String, GralFileSelector>();

  
  FcmdtTarget_ifc target;
  
  
  /**
   * The commands which are used for some buttons or menu items from the
   * JavaCommander itself.
   */
  final CmdStore buttonCmds;

  
  final List<Closeable> threadsToClose = new LinkedList<Closeable>();
  
  /**Static reference for the main instance to access from any class in the package.
   * This static instance is set firstly in constructor, all other constructors in this package
   * can be used it therefore. Use {@link #main()}*/
  private static Fcmd main;
  
  
  public Fcmd(CallingArgs cargs, GralArea9MainCmd cmdgui)
  {
    super(cargs, cmdgui, null, null, null);
    this.cargs = cargs;
    //redirect all outputs to System.out, System.err and MainCmd to System.out and System.err with timestamp.
    msgDisp = new MsgRedirectConsole(cmdgui, 0, null);
    msgDisp.setIdThreadForMsgDispatching(Thread.currentThread().getId());
    //msgDisp.msgDispatcher.setOutputRoutine(4, "MainLogFile", true, true, cmdgui.getLogMessageOutputFile());
    //msgDisp.msgDispatcher.setOutputRange(0, 100000, 4, MsgDispatcher.mAdd, 0);
    actionReadMsgConfig.exec(KeyCode.menuEntered, null);
    
    target = new FcmdtTarget();  //create the target itself, one process TODO experience with remote target.
    buttonCmds = new CmdStore();
    executer.cmdQueue.setOutput(gui.getOutputBox(), null);
  }

  
  /**Access the static instance of Fcmd.
   * This static instance is set firstly in constructor, all other constructors in this package
   * can be used it therefore.
   */
  static Fcmd main(){ return main; }
  
  
  /**
   * Initializes the areas for the panels and configure the panels. Note that
   * the window is initialized with an output area already. This is used for
   * output messages if problems occurs while build the rest of the GUI.
   */
  @Override
  protected void initGuiAreas(String sAreaMainPanel)
  {
    //panelBuildIfc.registerUserAction("KeyAction",
      //  keyActions.commanderKeyActions); // all key actions, registered central

    gui.setFrameAreaBorders(30, 65, 70, 85); // x1, x2, y1, y2
    //gui.setStandardMenusGThread(new File("."), actionFile);
    //gui.addMenuItemGThread("menuBarFavorsLeft", idents.menuBarNavigationLeft, selectCardThemesLeft);
    //gui.addMenuItemGThread("menuBarFavorsMiddle", idents.menuBarNavigationMiddle, selectCardThemesMiddle);
    //gui.addMenuItemGThread("menuBarFavorsRight", idents.menuBarNavigationRight, selectCardThemesRight);
    //gui.addMenuItemGThread("menuBarNavigatonCmd", idents.menuBarNavigatonCmd, actionFocusCmdCard);
    
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

    filesCp.buildGraphic();
    settings.buildWindow();  //F2
    status.buildWindow();  //F2
    filePropsCmd.buildWindow();  //F2
    viewCmd.buildWindowView();   //F3
    editWind.buildWindow();   //F3
    copyCmd.buildWindowConfirmCopy("confirm copy / move / compare");
    delCmd.buildWindowConfirmCopy("confirm delete");
    compareCmd.buildWindowConfirmCopy("confirm compare");
    mkCmd.buildWindowConfirmMk();
    executer.buildWindowConfirmExec();
    deleteCmd.buildWindowConfirmDelete(); //F8
    favorPathSelector.buildWindowAddFavorite();

    fButtons.initPanelButtons();
    GralMenu menu = gui.getMenuBar();
    menu.addMenuItemGthread("MenuSetWorkingDir", "&Command/Set&WorkingDir", executer.actionSetCmdWorkingDir); // /
    menu.addMenuItemGthread("MenuCommandAbort", "&Command/&Abort", executer.actionCmdAbort); // /
    // gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    menu.addMenuItemGthread("MenuCmdCfgSet", "&Command/CmdCf&g - read current file", executer.actionSetCmdCfg); // /
    menu.addMenuItemGthread("menuReadCmdiCfg", "&Command/&ExtCfg - read cfg file", executer.actionReadExtensionCmd);

    menu.addMenuItemGthread("menuHelp", idents.menuHelpBar, gui.getActionHelp());
    menu.addMenuItemGthread("menuAbout", idents.menuBarAbout, gui.getActionAbout());
    menu.addMenuItemGthread("MenuTestInfo", "&Help/&Infobox", actionTest); 
    guiW.outputBox.setActionChange(executer.actionCmdFromOutputBox);
    String sHelpUrlDir = cargs.dirHtmlHelp.getAbsolutePath();
    gui.setHelpUrl(sHelpUrlDir + "/Fcmd.html");
    lastFilePanels.clear();
    lastFilePanels.add(favorPathSelector.panelMid);
    lastFilePanels.add(favorPathSelector.panelRight);
    lastFilePanels.add(favorPathSelector.panelLeft);
    favorPathSelector.panelMid.cardFavorThemes.setFocus();
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
      sError = FcmdExecuter.readCmdCfg(cmdSelector.cmdStore, fileCfg = cargs.fileCfgCmds, console, executer.cmdQueue);
      if (sError == null) {
        sError = executer.readCmdFile(fileCfg = cargs.fileCmdsForExt);
      }
      if (sError == null) {
        sError = FcmdExecuter.readCmdCfg(buttonCmds, fileCfg = cargs.fileCfgButtonCmds, console, executer.cmdQueue);
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
    
    executer.cmdQueue.execCmds(writeStatusCmd);
    long time = System.currentTimeMillis();
    favorPathSelector.panelLeft.checkRefresh(time);
    favorPathSelector.panelMid.checkRefresh(time);
    favorPathSelector.panelRight.checkRefresh(time);
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
      FileAccessorLocalJava6.getInstance().close();
    } catch(IOException exc){
      
    }
  }
  
  
  void setLastSelectedPanel(FcmdLeftMidRightPanel panel){
    if(lastFilePanels.size() == 0){
      lastFilePanels.add(0, panel);  //first time only
    }
    else if(lastFilePanels.get(0) != panel) {  //do nothing if the panel is the first one.
      if(!lastFilePanels.remove(panel)){  //if it is in list on higher position
        System.err.println("Fcmd - setLastSelectedPanel() faulty; try remove panel=" + panel.toString());
        //The file panel should be known!
      }
      lastFilePanels.add(0, panel);
    }

  }

  /**Get the last selected files in order of selection of the file panels.
   * If the current file of any panel is marked, then all other marked files (but not directories)
   * of this panel are taken as next files. But only 3 at all.
   * It is possible to mark two files in one file panel to execute an application with them.
   * New method since 2011-12-23
   * @return array[3] of the last selected files in the file panels. It has always a length of 3
   *   but not all elements are set ( they are ==null) if no files were selected before.
   *   The returned instance is a new one, not referenced elsewhere. It can be stored
   *   and it remains the situation of selection files independently of further user actions.
   */
  FileRemote[] getLastSelectedFiles(boolean bAlsoDirs, int mask){
    FileRemote[] ret = new FileRemote[3];
    int ix = -1;
    Iterator<FcmdLeftMidRightPanel> iterPanel = lastFilePanels.iterator();
    while(ix < 2 && iterPanel.hasNext() ){
      FcmdLeftMidRightPanel panel = iterPanel.next();
      //for(FcmdLeftMidRightPanel panel: lastFilePanels){
      FcmdFileCard fileCard = panel.actFileCard;
      if(fileCard !=null){
        ret[++ix] = fileCard.currentFile();
        if(fileCard.currentFile() !=null && fileCard.currentFile().isMarked(0x1)){
          //if the current file is marked, use all marked files.
          List<FileRemote> listFiles = fileCard.getSelectedFiles(bAlsoDirs, mask);
          //NOTE: the currentFile was marked, but it was not existing after delete. In this case all is okay but listFiles is empty.
          assert(listFiles !=null); // && listFiles.size() > 0);  //at least the current file is marked.
          Iterator<FileRemote> iter= listFiles.iterator();
          while(ix < 2 && iter.hasNext()){
            FileRemote file = iter.next();
            if(file != fileCard.currentFile()){
              ret[++ix] = file;
            }
          }
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
      if(ix >=2) break;  //prevent exception because older error
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
  
  
  
  
  FileRemote currentFile(){
    if(this.currentFileCard == null) return null;
    else return this.currentFileCard.currentFile();
  }
  

  FileRemote currentDir(){
    if(this.currentFileCard == null) return null;
    else return this.currentFileCard.currentDir();
  }
  
  
  /**Refreshes the {@link FcmdFileCard} if the dir is the current one.
   * This method can be invoked in any for example callback routine in any thread, which changes the content of directories.
   * Note: It starts {@link GralFileSelector#fillIn(FileRemote, boolean)} which refreshes the files 
   * @param dir The directory of a changed file.
   */
  protected void refreshFilePanel(FileRemote dir)
  {
    for(FcmdLeftMidRightPanel panel: lastFilePanels) {
      if(panel !=null && panel.actFileCard !=null && panel.actFileCard.currentDir() == dir) {
        panel.actFileCard.fillInCurrentDir();
      }
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
      super.addAboutInfo("Version-date: " + Fcmd.sVersion);
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
      } else if (arg.startsWith("msgcfg:")) {
        cargs.msgConfig = new File(arg.substring(7));
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
      selectedFiles = getLastSelectedFiles(true, 1); //  getCurrentFileInLastPanels();
    }

    @Override
    public File getFileSelect()
    {
      return selectedFiles[0];
    }

    @Override
    public File getFile1()
    { if(selectedFiles !=null && selectedFiles.length >0) return selectedFiles[0];
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
    //new GralMng(null, cmdgui);
    
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


  
  void stop()
  {
  }

}
