package org.vishia.commander;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.cmd.CmdGetterArguments;
import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.cmd.JZtxtcmdScript.JZcmdClass;
import org.vishia.cmd.JZtxtcmdScript.Subroutine;
import org.vishia.commander.target.FcmdtTarget;
import org.vishia.commander.target.FcmdtTarget_ifc;
import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.fileLocalAccessor.FileAccessorLocalJava7;
import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralArea9Panel;
//import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GuiCallingArgs;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileProperties;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralViewFileContent;
import org.vishia.jztxtcmd.JZtxtcmd;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.msgDispatch.MsgRedirectConsole;
import org.vishia.util.ApplMain;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

/**This class is the main class of the-File-commander.
 * @author Hartmut Schorrig
 *
 */
public class Fcmd //extends GuiCfg
{


  /**Version, history and license. This String is visible in the about info.
   * <ul>
   * <li>2021-02-05 Hartmut chg for FcmdView, change and write in hex. 
   * <li>2020-02-01 Hartmut sets {@link GralWindow_ifc#windMinimizeOnClose} to prevent accidentally close.
   *   Menu "Windows-Close" added.
   * <li>2017-08-27 {@link FcmdFavorPathSelector#actionDeselectDirtree} now removes all FileRemote instances of children
   *   because a selection is not necessary furthermore. This is a 'refresh'. But the 'refresh' (F5, ctrl-R) should not change selection,
   *   it must not delete this children-FileRemote. Only deselection is the key action for that.
   * <li>2016-08-28 see {@link FcmdExecuter#sVersion}, {@link FcmdSettings#sVersion}
   * <li>2015-07-18 Hartmut chg: Now the output of script errors while executing the JZcmd cmd script is visible
   *   in the output window.  
   * <li>2015-07-12 Hartmut new: Many fixes in the base classes for functionality. See all commit messages of the component.
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
  public static final String version = "2023-04-22";

  
  static class CallingArgs extends GuiCallingArgs
  {
    File fileCfgCmds;

    File fileCmdsForExt;

    File fileCfgButtonCmds;

    File fileSelectTabPaths;
    
    File dirCfg;

    Argument sDirCfg = new Argument("cfg", ":dirCfg");
    
    
    public CallingArgs() {
      super();
      super.aboutInfo = "The.file.Commander made by Hartmut Schorrig, www.vishia.org "
                      + Fcmd.version;
      super.helpInfo = "see https://www.vishia.org";
      super.addArg(this.sDirCfg);
    }

    
    
    
    //@Override 
    // not used, old concept with old arguments
    protected boolean XXXtestArgument(String arg, int nArg)
    {
      boolean bOk = true;
      if (arg.startsWith("cfg:")) {
        this.dirCfg = new File(arg.substring(4));
//      } else if (arg.startsWith("cmdcfg:")) {
//        this.fileCfgCmds = new File(arg.substring(7));
//      } else if (arg.startsWith("cmdext:")) {
//        this.fileCmdsForExt = new File(arg.substring(7));
//      } else if (arg.startsWith("cmdButton:")) {
//        this.fileCfgButtonCmds = new File(arg.substring(10));
//      } else if (arg.startsWith("sel:")) {
//        this.fileSelectTabPaths = new File(arg.substring(4));
//      } else {
        bOk = super.testArgument(arg, nArg);
      }
      return bOk;
    }

    
    @Override public boolean testConsistence(Appendable out) throws IOException {
      boolean bOk = true;
      if(this.sDirCfg.val == null) { 
        bOk = false; out.append("cmdline argument cfg:PATHCFG is missing"); 
      }
      if(bOk){
        this.dirCfg = new File(this.sDirCfg.val);
        if(!this.dirCfg.exists()){ bOk = false; out.append("cmdline argument cfg is faulty:" + this.dirCfg.getAbsolutePath());}
      }
      if(bOk){
        if(this.dirHtmlHelp ==null){ this.dirHtmlHelp = new File(this.dirCfg, "../help"); }
        if(this.fileCfgCmds ==null){ this.fileCfgCmds = new File(this.dirCfg, "cmd.cfg"); }
        if(this.fileCmdsForExt ==null){ this.fileCmdsForExt = new File(this.dirCfg, "ext.cfg"); }
        if(this.fileCfgButtonCmds ==null){ this.fileCfgButtonCmds = new File(this.dirCfg, "cmdi.cfg"); }
        if(this.fileSelectTabPaths ==null){ this.fileSelectTabPaths = new File(this.dirCfg, "path.cfg"); }
      }
      return bOk;
    }

  }


  
  
  void openExtEditor(File file){
    JZtxtcmdScript.Subroutine jzsub = buttonCmds.get("edit");
    if( jzsub == null ) {
      this.gui.writeError("internal problem - don't find 'edit' command. ");
    } else {
      String sMsg = "GralCommandSelector - put cmd;" + jzsub.toString();
      System.out.println(sMsg);
      List<DataAccess.Variable<Object>> args = new LinkedList< DataAccess.Variable<Object>>();
      DataAccess.Variable<Object> var = new DataAccess.Variable<>('O', "file1", file, true);
      args.add(var);
      //executer.cmdQueue.addCmd(jzsub, args, file.getParentFile()); // to execute.
      executer.cmdExecuter.addCmd(jzsub, args, Fcmd.this.gui.getOutputBox(), file.getParentFile());
    }
    
  }
  
  

  /**This implementation resolves the arguments for invoked commands with the capabilities of the Fcmd.
   * Especially the following argument strings are supported:
   * <ul>
   * <li>
   * </ul>
   * This instance is used only locally (private). It is designated as protected for documentation.
   */
  protected CmdGetterArguments getterFileArguments = new CmdGetterArguments()
  {
    @Override public List<DataAccess.Variable<Object>> getArguments(JZtxtcmdScript.Subroutine subRoutine) {
      FileRemote[] selFiles = getLastSelectedFiles(true, 1);  //The selected 3 files, maybe in one card too.
      List<DataAccess.Variable<Object>> args = new LinkedList<DataAccess.Variable<Object>>();
      if(subRoutine !=null) {
        if(subRoutine.formalArgs !=null) {
          for(JZtxtcmdScript.DefVariable arg :subRoutine.formalArgs){
            String name1 = arg.getVariableIdent();
            if(name1.equals("file1")){ args.add(new DataAccess.Variable<Object>('O', "file1", selFiles[0], true)); }
            else if(name1.equals("file2")){ args.add(new DataAccess.Variable<Object>('O', "file2", selFiles[1], true)); }
            else if(name1.equals("file3")){ args.add(new DataAccess.Variable<Object>('O', "file3", selFiles[2], true)); }
            else if(name1.equals("dir1")){ args.add(new DataAccess.Variable<Object>('O', "dir1", selFiles[0] ==null ? null : selFiles[0].getParentFile(), true)); }
            else if(name1.equals("dir2")){ 
              FileRemote dir = null;
              if(lastFilePanels.size() >=2){ 
                FcmdFileCard fileCard = lastFilePanels.get(1).actFileCard;
                if(fileCard !=null){ dir = fileCard.getCurrentDir(); }
              }
              args.add(new DataAccess.Variable<Object>('O', "dir2", dir, true)); 
            } 
            else if(name1.equals("dir3")){ 
              FileRemote dir = null;
              if(lastFilePanels.size() >=3){ 
                FcmdFileCard fileCard = lastFilePanels.get(2).actFileCard;
                if(fileCard !=null){ dir = fileCard.getCurrentDir(); }
              }
              args.add(new DataAccess.Variable<Object>('O', "dir3", dir, true)); 
            } 
            else if(name1.equals("listfiles1")) { 
              List<FileRemote> files = null;
              if(lastFilePanels.size() >=1){
                FcmdLeftMidRightPanel filePanel = lastFilePanels.get(0);
                FcmdFileCard fileCard = filePanel.actFileCard;
                if(fileCard !=null){
                  files = fileCard.getSelectedFiles(true, 1);   
                }
              }
              args.add(new DataAccess.Variable<Object>('L', "listfiles1", files, true)); 
            }
            else if(name1.equals("listfiles2")) { 
              List<FileRemote> files = null;
              if(lastFilePanels.size() >=2){ 
                FcmdFileCard fileCard = lastFilePanels.get(1).actFileCard;
                if(fileCard !=null){ files = fileCard.getSelectedFiles(true, 1); }
              }
              args.add(new DataAccess.Variable<Object>('L', "listfiles2", files, true)); 
            }
            else {
              System.err.println("failded argument name: " + name1);
            }
          }
        }
      } else {
      
      }
      return args;
    }
    
    @Override public final File getCurrDir(){ return Fcmd.this.currentFileCard.currentDir(); }
  };



  //MsgDispatchSystemOutErr msgDisp;
  
  /**Set the singleton reference before other instances are initialized which uses main. */
  { Fcmd.main = this;
  }
  
  
  public JZtxtcmdScript.AddSub2List addButtonCmd = new JZtxtcmdScript.AddSub2List() {


    @Override public void clear() { buttonCmds.clear(); }

    @Override public void add2List(JZcmdClass jzclass, int level){} //not used
  
    @Override public void add2List(Subroutine jzsub, int level)
    {
      buttonCmds.put(jzsub.name, jzsub);
    }
    
  };
  

  
  final CallingArgs cargs;
  
  final FcmdActions fcmdActions = new FcmdActions(this);
  
  final FcmdGui gui = new FcmdGui();
  
  LogMessage log = this.gui.gralMng.log;         // initial use System.out, later changed to outputBox
  
  //final MsgRedirectConsole msgDisp;
  
  final FileCluster fileCluster = FileRemote.clusterOfApplication;

  // GralTabbedPanel tabbedPanelsLeft, tabbedPanelsMid, tabbedPanelsRight;

  final FcmdIdents idents = new FcmdIdents(this);

  final FcmdWindowMng windMng = new FcmdWindowMng(this);

  final FcmdButtons fButtons = new FcmdButtons(this);
  
  final FcmdStatusLine statusLine = new FcmdStatusLine(this);
  
  final String nameTextFieldInfo = "file-info";
  
  final String nameTextFieldFilePath = "file-path";
  
  final String nameTextFieldRunInfo = "run-info";
  
  GralPanelContent panelButtons;

  
  /**This instance contains the three panels.
   * 
   */
  final FcmdFavorPathSelector favorPathSelector = new FcmdFavorPathSelector(this.gui.log(), this);

  FcmdExecuter executer = new FcmdExecuter(this.gui.log(), this.gui.getOutputBox(), this);

  final FcmdSettings settings = new FcmdSettings(this);
  
  final FcmdStatus status = new FcmdStatus(this);
  
  final GralFileProperties fileProps = GralFileProperties.createWindow(this.gui.refPos, "@8..38, 30..70=fileProps", "Fcmd File properties");
  
  final GralViewFileContent fileViewer = new GralViewFileContent(this.gui.gralMng.refPos(), "@50..100, 50..100=fileViewer" + ".view");
  
  final FcmdEdit editWind = new FcmdEdit(this);
  
  final FcmdCopyCmprDel copyCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.copy);
  
  final FcmdCopyCmprDel delCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.delete);
  
  final FcmdCopyCmprDel compareCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.compare);
  
  final FcmdCopyCmprDel searchCmd = new FcmdCopyCmprDel(this, FcmdCopyCmprDel.Ecmd.search);
  
  final FcmdMkDirFile mkCmd = new FcmdMkDirFile(this);

  final FcmdDelete deleteCmd = new FcmdDelete(this);

  final FcmdFilesCp filesCp = new FcmdFilesCp(this);

  final FcmdKeyActions keyActions = new FcmdKeyActions(this);

  /**
   * Note: this should be the last aggregate of Fcmd because it uses some other ones. */
//  final FcmdIdents idents = new FcmdIdents(this);
  
  
  /**The current directory of the last selected file. */
  //FileRemote currentFile;

  /**The last selected files of the three panels, [0] for left .. [2] for right. */
  final File[] selectedFiles123 = new File[3];
  
  
  /**The last selected file panels in its order of selection.
   * This list contains max 3 entries. 
   * To get the last selected files: The panel knows the last used file card there. 
   */
  List<FcmdLeftMidRightPanel> lastFilePanels = new LinkedList<FcmdLeftMidRightPanel>();
  
  /**The last used favor card or its last used file card.
   * It is used for delete tab. */
  //FcmdFavorCard lastFavorCard;
  
  FcmdFileCard currentFileCard;
  

  final Map<String, GralFileSelector> idxFileSelector = new TreeMap<String, GralFileSelector>();

  
  FcmdtTarget_ifc target;
  
  
  /**
   * The commands which are used for some buttons or menu items from the
   * JavaCommander itself.
   */
  final Map<String, JZtxtcmdScript.Subroutine> buttonCmds;

  
  final List<Closeable> threadsToClose = new LinkedList<Closeable>();

  
  /**Static reference for the main instance to access from any class in the package.
   * This static instance is set firstly in constructor, all other constructors in this package
   * can be used it therefore. Use {@link #main()}*/
  private static Fcmd main;
  
  
  
  public Fcmd ( CallingArgs cargs) //, GralArea9MainCmd cmdgui)
  {
    //super(cargs, cmdgui, null, null, null);
    this.cargs = cargs;
    //redirect all outputs to System.out, System.err and MainCmd to System.out and System.err with timestamp.
    //msgDisp = new MsgRedirectConsole(cmdgui, 0, null);
    //msgDisp.setIdThreadForMsgDispatching(Thread.currentThread().getId());
    //msgDisp.msgDispatcher.setOutputRoutine(4, "MainLogFile", true, true, cmdgui.getLogMessageOutputFile());
    //msgDisp.msgDispatcher.setOutputRange(0, 100000, 4, MsgDispatcher.mAdd, 0);
    this.fcmdActions.actionReadMsgConfig.exec(KeyCode.menuEntered, null);
    
    this.idents.buildDependings();
    
    target = new FcmdtTarget();  //create the target itself, one process TODO experience with remote target.
    buttonCmds = new TreeMap<String, JZtxtcmdScript.Subroutine>();
    //executer.cmdQueue.setOutput(gui.getOutputBox(), null);
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
  //@Override
  protected void initGuiAreas ( ) {
    //panelBuildIfc.registerUserAction("KeyAction",
      //  keyActions.commanderKeyActions); // all key actions, registered central

    this.gui.setFrameAreaBorders(30, 65, 100, 70, 85, 100); // x1, x2, y1, y2
    //gui.setStandardMenusGThread(new File("."), actionFile);
    //gui.addMenuItemGThread("menuBarFavorsLeft", idents.menuBarNavigationLeft, selectCardThemesLeft);
    //gui.addMenuItemGThread("menuBarFavorsMiddle", idents.menuBarNavigationMiddle, selectCardThemesMiddle);
    //gui.addMenuItemGThread("menuBarFavorsRight", idents.menuBarNavigationRight, selectCardThemesRight);
    //gui.addMenuItemGThread("menuBarNavigatonCmd", idents.menuBarNavigatonCmd, actionFocusCmdCard);
    
    // gui.set

    // Creates tab-Panels for the file lists and command lists.
    this.gui.gralMng.selectPanel("primaryWindow");
    this.favorPathSelector.panelLeft.tabbedPanelFileCards = this.gui.gralMng.addTabbedPanel("@area9,A1A1=File0Tab");
    //this.gui.area9.addFrameArea("A1A1", this.favorPathSelector.panelLeft.tabbedPanelFileCards); // dialogPanel);
    this.favorPathSelector.panelLeft.buildInitialTabs();

    this.gui.gralMng.selectPanel("primaryWindow");
    this.favorPathSelector.panelMid.tabbedPanelFileCards = this.gui.gralMng.addTabbedPanel("@area9,B1B1=File1Tab");
    //gui.addFrameArea("B1B1", this.favorPathSelector.panelMid.tabbedPanelFileCards); // dialogPanel);
    this.favorPathSelector.panelMid.buildInitialTabs();

    this.gui.gralMng.selectPanel("primaryWindow");
    this.favorPathSelector.panelRight.tabbedPanelFileCards = this.gui.gralMng.addTabbedPanel("@area9,C1C1=File2Tab");
    //gui.addFrameArea("C1C1", this.favorPathSelector.panelRight.tabbedPanelFileCards); // dialogPanel);
    this.favorPathSelector.panelRight.buildInitialTabs();

    this.executer.buildGui();

    this.gui.gralMng.selectPanel("primaryWindow");
    panelButtons = this.gui.gralMng.createGridPanel("@area9,A3C3=Buttons", this.gui.gralMng.getColor("gr"), 1, 1, 10, 10);
    //gui.addFrameArea("A3C3", panelButtons); // dialogPanel);

//    filesCp.buildGraphic();
//    settings.buildWindow();  //F2
//    status.buildWindow();  //F2
//    filePropsCmd.buildWindow();  //F2
//    viewCmd.buildWindowView();   //F3
//    editWind.buildWindow();   //F3
//    copyCmd.buildWindowConfirmCopy("confirm copy / move / compare");
//    delCmd.buildWindowConfirmCopy("confirm delete");
//    searchCmd.buildWindowConfirmCopy("confirm search");
//    compareCmd.buildWindowConfirmCopy("confirm compare");
//    mkCmd.buildWindowConfirmMk();
//    executer.buildWindowConfirmExec();
//    deleteCmd.buildWindowConfirmDelete(); //F8
//    this.favorPathSelector.buildWindowAddFavorite();

    fButtons.initPanelButtons();
    GralMenu menu = this.gui.fcmdWindow.getMenuBar();
    menu.addMenuItem("menuHelp", this.idents.menuHelpBar, this.gui.gralMng.actionHelp);
    menu.addMenuItem("menuClose", this.idents.menuCloseBar, this.gui.gralMng.actionClose);
    
    menu.addMenuItem("MenuSetWorkingDir", "&Command/Set&WorkingDir", this.executer.actionSetCmdWorkingDir); // /
    menu.addMenuItem("MenuCommandAbort", "&Command/&Abort", this.executer.actionCmdAbort); // /
    // gui.addMenuItemGThread("&Command/E&xecute", actionSetCmdCurrentDir); ///
    menu.addMenuItem("MenuCmdCfgSet", "&Command/CmdCf&g - read current file", this.executer.actionSetCmdCfg); // /
    menu.addMenuItem("menuBarFolderSyncMidRight", main.idents.menuBarFolderSyncMidRight, this.favorPathSelector.actionSyncMidRight); // /
    menu.addMenuItem("test", main.idents.menuExecuteBar, main.executer.actionExecuteFileByExtension);
    
    //TODO menu.addMenuItem("menuAbout", this.idents.menuBarAbout, this.gui.gralMng.actionAbout);
    menu.addMenuItem("MenuTestInfo", "&Help/&Infobox", this.fcmdActions.actionTest); 
    String sHelpUrlDir = this.cargs.dirHtmlHelp.getAbsolutePath();
    this.gui.gralMng.setHelpUrl(sHelpUrlDir + "/Fcmd.html");
    this.gui.outputBox.specifyActionChange(null, this.executer.actionCmdFromOutputBox, null);
    this.gui.outputBox.setHtmlHelp(":FcmdNew.html#cmdOutput");
    this.lastFilePanels.clear();                                     // The order of used file panels
//    this.lastFilePanels.add(this.favorPathSelector.panelMid);        // default: mid, right, left
//    this.lastFilePanels.add(this.favorPathSelector.panelRight);
//    this.lastFilePanels.add(this.favorPathSelector.panelLeft);
//    this.favorPathSelector.panelMid.cardFavorThemes.setFocus();
    this.gui.outputBox.setText("Test Outbox");
    this.fileProps.setActionRefresh(this.favorPathSelector.actionRefreshFileTable);
  }

  //@Override
  protected final void initMain()
  {
    if (cargs.fileCfgCmds == null) {
      this.gui.writeError("Argument cmdcfg:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileCmdsForExt == null) {
      this.gui.writeError("Argument cmdext:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileCfgButtonCmds == null) {
      this.gui.writeError("Argument cmdButton:CONFIGFILE should be given.");
      // mainCmd.e
    } else if (cargs.fileSelectTabPaths == null) {
      this.gui.writeError("Argument sel:SELECTFILE should be given.");
      // mainCmd.e
    } else {
      String sError = null;
      File fileCfg = cargs.fileCfgCmds;
      if (sError == null) {
        //sError = executer.readCmdFile(fileCfg = cargs.fileCmdsForExt);
        sError = executer.readCfgExt(new File(cargs.dirCfg, "extjz.cfg"));
      }
      if (sError == null) {
        sError = JZtxtcmd.readJZcmdCfg(addButtonCmd, fileCfg = cargs.fileCfgButtonCmds, this.gui.gralMng.log, executer.cmdExecuter);
      }
      if (sError == null) {
        sError = this.favorPathSelector.readCfg(fileCfg = cargs.fileSelectTabPaths);
      }
      if (sError != null) {
        this.gui.writeError("Error reading " + fileCfg.getAbsolutePath() + ": "
            + sError);
      }
      initGuiAreas();
      sError = this.executer.readCmdCfgSelectList(executer.cmdSelector.addJZsub2SelectTable, fileCfg, this.gui.log());
      if(sError !=null) {
        this.gui.outputBox.setText(sError);   
      } else {
        this.gui.outputBox.append("read config cmd");   
      }
      
      this.gui.gralMng.reportGralContent(this.log);
      this.gui.gralMng.createGraphic("SWT", this.cargs.sizeShow, null);
    //old: super.initMain(); // starts initializing of graphic. Do it after config
                      // command selector!
    }

  }

  /**
   * Executing in the main thread loop. It handles commands.
   * 
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  public void stepMain()
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
    
    //executer.cmdQueue.execCmds(writeStatusCmd);
    executer.cmdExecuter.executeCmdQueue(false);
    long time = System.currentTimeMillis();
    this.favorPathSelector.panelLeft.checkRefresh(time);
    this.favorPathSelector.panelMid.checkRefresh(time);
    this.favorPathSelector.panelRight.checkRefresh(time);
    try { Thread.sleep(50); } catch (InterruptedException e) { }
    
  }
  
  
  
  /**
   * Executing in the main thread loop. It handles commands.
   * 
   * @see org.vishia.gral.area9.GuiCfg#stepMain()
   */
  public void finishMain()
  { 
    try{
      executer.cmdExecuter.close();  //finishes threads.
      target.close();
      FileAccessorLocalJava7.getInstance().close();
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
    String sPanels = "";
    for(FcmdLeftMidRightPanel panel2: lastFilePanels) {
      sPanels += panel2.cc;
    }
    this.gui.gralMng.log().sendMsg(555, "changed act panel: %s", sPanels);
    this.statusLine.widgFilePath.setText(panel.toString());
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
  
  
  
  /**Get the last selected file cards in order of selection of the file panels.
   * @return array[3] of the last selected file cards in the file panels. It has always a length of 3
   *   but not all elements are set ( they are ==null) if the file cards are not opened before
   *   in the panel.
   *   The returned instance is a new one, not referenced elsewhere. It can be stored
   *   and it remains the situation of selection files independently of further user actions.
   */
  List<File>[] getLastSelectedFilesPerCard(){
    @SuppressWarnings("unchecked")
    List<File>[] ret = new List[3];
    int ix = -1;
    for(FcmdLeftMidRightPanel panel: lastFilePanels){
      FcmdFileCard fileCard = panel.actFileCard;
      if(fileCard !=null){
        List<FileRemote> list = fileCard.getSelectedFiles(true, 0x1);
        ret[++ix] = new ArrayList<File>();
        for(File file: list) { ret[ix].add(file); }
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
    return this.currentFileCard;
//    if(lastFavorCard!=null){
//      return lastFavorCard.fileTable;
//    } else {
//      return null;
//    }
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
   * @param fileOrDir The changed file or its directory. 
   *   it is checked whether the directory is shown currently in one of the three file panels, then the panel will be refreshed.
   *   If the directory is not shown yet then nothing is done. 
   *   Note that a directory will be refreshed whenever it will be activated in a panel. 
   */
  protected void refreshFilePanel(FileRemote fileOrDir)
  {
    FileRemote dir = fileOrDir.isDirectory() ? fileOrDir: fileOrDir.getParentFile();
    for(FcmdLeftMidRightPanel panel: lastFilePanels) {
      if(panel !=null && panel.actFileCard !=null && panel.actFileCard.currentDir() == dir) {
        panel.actFileCard.fillInCurrentDir();
      }
    }
  }
  
  
  
  
  


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
    try {
      if(  !cargs.parseArgs(args, System.err)
        || !cargs.testConsistence(System.err)
        ) {
        System.exit(cargs.exitCodeArgError);
      }
    } catch (Exception exc) {
      System.err.println("cmdline arg exception: " + exc.getMessage());
      System.exit(9);
    }

    // Initializes the GUI till a output window to show information.
    // Uses the commonly GuiMainCmd class because here are not extra arguments.
    //GralArea9MainCmd cmdgui = new FcmdMainCmd(cargs, args); // implements MainCmd, parses
//    MainCmd cmdgui = new FcmdMainCmd(cargs, args); // implements MainCmd, parses
    //new GralMng(null, cmdgui);
    
    // calling arguments
//    int windProps = GralWindow_ifc.windMinimizeOnClose | GralWindow_ifc.windHasMenu | GralWindow_ifc.windResizeable;
    //bOk = cmdgui.parseArgumentsAndInitGraphic("The.file.Commander", "2A2C", '.', "20+70,20+250", windProps);
//    try {
//      cmdgui.parseArguments();
//    } catch (Exception exception) {
//      cmdgui.writeError("Cmdline argument error:", exception);
//      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
//      bOk = false; // not exiting, show error in GUI
//    }
//    if (bOk) {
      // Uses socket communication for InterprocessComm, therefore load the
      // factory.
      new InterProcessCommFactorySocket();
      //
      // Initialize this main class and execute.
      Fcmd thiz = new Fcmd(cargs); //, cmdgui);
      thiz.initMain();
      //main.msgDisp = MsgDispatchSystemOutErr.create();
      while(thiz.gui.gralMng.isRunning() && !thiz.gui.gralMng.isImplementationGraphicTerminated()) {
        thiz.stepMain();
      }
//      main.execute();
//    }
//    cmdgui.exit();
      thiz.finishMain();
  }

  /**This class defines only static numbers for messages. 
   * The numbers should be a little bit sorted,
   * so that dispatching of messages can be done. 
   *
   */
  public static class LogMsg {
    
    public static final int fmcdFileCard_selectFile = 2101;
    public static final int fmcdFileCard_setCurrFilePanel = 2102;

  }  

}
