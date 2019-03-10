package org.vishia.gitGui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vishia.cmd.CmdExecuter;
import org.vishia.cmd.JZtxtcmdFilepath;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.FileList;
import org.vishia.util.FilePath;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringPartAppend;


/**A Graphical User Interface for basic working in git with remote repository and preserving time stamp of the files.
 * <ul>
 * <li>Show a working tree with remote repository (option -git-dir on any git command call).
 * <li>Lists all changed files from working tree and between any revisions.
 * <li>supports commit: via a text file for the commit text (edit with any standard editor) and commit button.
 *   On any commit creates a new "_filelist.lst" via {@link FileList} which contains the time stamp of all files.
 * <li>restore any file from any older version: select version, select file, right mouse "restore". (git checkout)
 *   with restoring the original time stamp.
 * </ul>
 * @author Hartmut Schorrig
 *
 */
public class GitGui
{



  /**Version, history and license
   * <ul>
   * <li>2018-10-28 Hartmut new textfield for cmd, shows the last automatically cmd, enables assembled cmd for git and common cmd. 
   * <li>2018-10-27 Hartmut new {@link #openNewFileSelector(String, RevisionEntry)} not ready yet. 
   * <li>2018-10-27 Hartmut chg uses <code>diff --name-status</diff> instead <code>diff --name-only</diff> 
   *   to build the input for the #wdgTableFiles to show different files. The table has an additional left row for the kind of difference
   *   adequate the output of <code>diff --name-status</diff>. 
   * <li>2018-10-10 Hartmut new {@link #guiRepository(JZtxtcmdFilepath)} as start operation. It checks whether "name.gitRepository" is given
   *  and copies "name.gitignore" to ".gitignore" and uses "name.filelist" as filelist. It supports more as one component on one working dir.
   * <li>2018-10-10 Hartmut new {@link #getFilePathRepository(File)}. It is invoked in jzTc to get the opened repository by this GUI for add command.
   * <li>2017-05-10 Hartmut bugfix {@link CmdExecuter#setCharsetForOutput(String)} to UTF-8 because git outputs cmd output in UTF-8
   * <li>2016-12-02 Hartmut chg GitGui some improvements.
   * <li>2016-09-23 Hartmut GitGui: ContextMenu in file table
   * <li>2016-08-18 Hartmut this version is able to use to view the repository versions, the changed files per version, the changed file to the working tree.
   *   It supports view diff with invocation of an external tool. It is the first productive version. But yet with some specific settings yet now.
   *   TODO: read a config. Document it. Show the git command line for any action.
   * <li>2016-08-24 Hartmut created
   * </ul>
   * 
   * 
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final String sVersion = "2018-10-28";  


  static class RevisionEntry
  {
    final String revisionHash;
    String treeHash;
    String parentHash;
    String author;
    Date dateAuthor;
    String committer;
    Date dateCommit;
    String commitTitle;
    StringBuilder commitText = new StringBuilder(200);
    
    RevisionEntry(String hash) { revisionHash = hash; }
    
  }



  static class Settings
  {
    
    File dirTemp1 = new File("t:/git_tmp1");
    File dirTemp2 = new File("t:/git_tmp2");
  }


  /**Action for open the commit text. 
   * 
   */
  GralUserAction actionOpenCommitText = new GralUserAction("actionTableOpenCommitText")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      String[] args ={"cmd.exe", "/C", "edit", ".gitCommit"};
      wdgCmd.setText("cmd.exe /C edit .gitCommit");
      gitCmd.addCmd(args, null, listOut, null, workingDir, null);
      wdgCommit.setText("do commit");
      return true;
  } };


  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_CommitDone = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { if(errorcode ==0) {
        FileSystem.writeFile("\n", sWorkingDir + "/.gitcommit");
        wdgCommit.setText("commit done");
      } else {
        wdgCommit.setText("commit error");
      }
    }
  }; 



  /**Action for do commi. 
   * 
   */
  GralUserAction actionCommit = new GralUserAction("actionCommit")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      File gitCommit = new File(sWorkingDir, ".gitCommit");
      if(gitCommit.length() > 3) {
        try{ FileList.list(sWorkingDir, "*", GitGui.this.sFileList);
        } catch(Exception exc){
          wdgInfo.setText("_filelist.lst problem: " + exc.getMessage());
        }
        String sGitCmd = "git";
        if(! sGitDir.startsWith(sWorkingDir)) {
          sGitCmd += " '--git-dir=" + sGitDir + "'";
        }
        sGitCmd += " commit -a -F .gitcommit";
        wdgCmd.setText(sGitCmd);
        String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
        gitCmd.addCmd(args, null, listOut, null, workingDir, exec_CommitDone);
      } else {
        wdgCommit.setText("do commit text?");
      }
      return true;
  } };


  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionRefresh = new GralUserAction("actionRefresh")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        startLog(null); return true;
      } //if;
      return false;
  } };




  GralUserAction actionTableLineVersion = new GralUserAction("actionTablelineVersion")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(actionCode == KeyCode.userSelect) {
        @SuppressWarnings("unchecked")
        GralTable<RevisionEntry>.TableLineData line = (GralTable<RevisionEntry>.TableLineData) params[0];
        
        showLog_startRevisionDiff4FileTable(line);
      }  
      return true;
    }
  };


  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_fillRevisionTable = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { fillRevisionTable();
    }
  };


  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_fillFileTable4Revision = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { fillFileTable4Revision();
    }
  };


  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_ShowStatus = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { wdgInfo.setText(gitOut);  //The status info
      gitOut.buffer().setLength(0);  //prepare for the next command.
      gitOut.assign(gitOut.buffer());   //to reset positions to the changed gitOut.buffer()
    }
  };


  /**Action for mouse double to start view diff. 
   * 
   */
  GralUserAction actionTableFile = new GralUserAction("actionTableFile")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry>.TableLineData line = (GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
       
      String sFile = line.getKey(); //"org/vishia/inspcPC/InspCmd.java";
      switch(actionCode) {
      case (KeyCode.ctrl | KeyCode.enter):
      case KeyCode.mouse1Double: startDiffView(sFile, GitGui.this.currentEntry, GitGui.this.cmpEntry); return true;
      case (KeyCode.ctrl | 's'): startLog(sFile); return true;
      default: return false;
      } //switch;
      
  } };


  /**Action for mouse double to start view diff. 
   * 
   */
  GralUserAction actionRestore = new GralUserAction("actionRestore")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry> table = (GralTable<RevisionEntry>)widgd;  //it is the table line.
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line = table.getLineMousePressed(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
        String sFile = line.getKey(); 
        restoreFile(sFile); 
        return true;
      } //if;
      return false;
  } };


  /**Action for mouse double to start view diff. 
   * 
   */
  GralUserAction actionTableFileDiffView = new GralUserAction("actionTableFileDiffView")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry> table = (GralTable<RevisionEntry>)widgd;  //it is the table line.
      
      GralTable<RevisionEntry>.TableLineData lineCurr = table.getCurrentLine(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      GralTable<RevisionEntry>.TableLineData line = table.getLineMousePressed(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String sFile = line.getKey(); 
        startDiffView(sFile, GitGui.this.currentEntry, GitGui.this.cmpEntry); return true;
      } //if;
      return false;
  } };


  GralUserAction actionTableFileRenMove = new GralUserAction("actionTableFileRenMove")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry> table = (GralTable<RevisionEntry>)widgd;  //it is the table line.
      
      GralTable<RevisionEntry>.TableLineData lineCurr = table.getCurrentLine(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      GralTable<RevisionEntry>.TableLineData line = table.getLineMousePressed(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String sFile = line.getKey(); 
        openNewFileSelector(sFile, GitGui.this.currentEntry); return true;
      } //if;
      return false;
  } };


  /**Action for diff view of the current file to the workspace.*/
  GralUserAction actionDiffCurrWork = new GralUserAction("actionCurrFileDiffView")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line = wdgTableVersion.getCurrentLine();
        RevisionEntry revision = line.getData();
        startDiffView(sLocalFile, null, revision); return true;
      } //if;
      return false;
  } };


  /**Action for diff view of the current file between revisions.*/
  GralUserAction actionFileDiffRev = new GralUserAction("actionFileDiffRev")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line = wdgTableVersion.getCurrentLine();
        RevisionEntry revision = line.getData();
        startDiffView(sLocalFile, null, revision); return true;
      } //if;
      return false;
  } };


  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionTableFileLog = new GralUserAction("actionTableFileLog")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry> table = (GralTable<RevisionEntry>)widgd;  //it is the table line.
      GralTable<RevisionEntry>.TableLineData lineCurr = table.getCurrentLine(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      GralTable<RevisionEntry>.TableLineData line = table.getLineMousePressed(); //(GralTable<RevisionEntry>.TableLineData)params[0];  //it is the table line.
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String sFile = line.getKey();   //working tree has key "*"
        startLog(sFile); return true;
      } //if;
      return false;
  } };


  GralUserAction actionExecCmd = new GralUserAction("actionExecCmd")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = wdgCmd.getText();
        File cmdDir = workingDir;
        int posDir = cmd.indexOf('>');
        if(posDir >0) {
          cmdDir = new File(cmd.substring(0, posDir));
          cmd = cmd.substring(posDir+1);
        }
        String[] args = cmd.split(" ");
        if(args[0].equals("git")) {
          String[] args2 = args;
          args = new String[4];
          args[0] = exepath.gitsh_exe;
          args[1] = "-x";
          args[2] = "-c";
          args[3] = cmd;
        }
        gitOut.clear();
        gitCmd.addCmd(args, null, listOut, null, cmdDir, execShowListOut);

      } //if;
      return false;
  } };

  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionAdd = new GralUserAction("actionAdd")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        addFileFromSelection(); return true;
      } //if;
      return false;
  } };



  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionMove = new GralUserAction("actionMove")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        moveFileListToSelection(); return true;
      } //if;
      return false;
  } };




  CmdExecuter.ExecuteAfterFinish execShowListOut = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { //if(errorcode ==0) {
      wdgInfo.setText("cmd ouptut:\n");
      try { wdgInfo.append(gitOut);
      } catch (IOException e) { }
  } };


  /**The paths to the executables. */
  GitGuiPaths exepath;
  
  /**The only one opened git repository. null if nothing is open or more as one is open.
   * 
   */
  static File filePathRepository;
  
  Settings settings = new Settings();

  String sTypeOfImplementation = "SWT";  //default
  

  GralWindow window = new GralWindow("0+60, 0+90", "GitGui", "Git vishia", GralWindow_ifc.windResizeable | GralWindow_ifc.windRemoveOnClose);

  GralTextField wdgCmd = new GralTextField("@2-2,0..-7=cmd", GralTextField.Type.editable);
  
  GralButton wdgBtnCmd = new GralButton("@0..2, -6..0 = cmdExec", "exec", this.actionExecCmd);

  
  GralTable<RevisionEntry> wdgTableVersion = new GralTable<>("@3..-30,0..-40=git-versions", new int[] {2, 10, 0, -10});
  
  GralTable<String> wdgTableFiles = new GralTable<>("@3..-30,-40..0=git-files", new int[] {3,20,0});
  
  GralTextBox wdgInfo = new GralTextBox("@-30..0, 0..-20=info");

  GralButton wdgBtnDiffCurrWork = new GralButton("@-29..-27, -18..-2 = diffCurrWork", "diff current file to workspace", this.actionDiffCurrWork);

  GralButton wdgBtnDiffCurrFile = new GralButton("@-26..-24, -18..-2 = diffCurrFile", "diff current file", this.actionFileDiffRev);

  GralButton wdgBtnAdd = new GralButton("@-18+2, -18..-8 = add", "add", this.actionAdd);

  GralButton wdgBtnMove = new GralButton("@-15+2, -18..-8 = move", "mv", this.actionMove);

  GralButton wdgRefresh = new GralButton("@-12+2, -18..-8 = refresh", "refresh", this.actionRefresh);

  GralButton wdgCommitText = new GralButton("@-9+2, -18..-2 = commitText", "commit-text", this.actionOpenCommitText);

  GralButton wdgCommit = new GralButton("@-6+2, -18..-2 = commit", "do commit", this.actionCommit);

  /**If set to true, the {@link #cmdThread} should be aborted.
   * 
   */
  boolean bCmdThreadClose;

  CmdExecuter gitCmd = new CmdExecuter();


  /**Destination for output of all command line invocations.
   * This buffer will be cleared and filled with the git command, and then parsed to present the result. 
   */
  StringPartAppend gitOut = new StringPartAppend();
  
  /**The {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * needs a list of appendable, that is it.*/
  List<Appendable> listOut = new LinkedList<Appendable>();
  { listOut.add(gitOut); }
  
  /**Stored arguments from {@link #startLog(String, String, String)}. */
  String sGitDir, sWorkingDir; //, sLocalFile;

  File workingDir;
  
  String sFileList = "_filelist.lst";

  /**If given the file which's log and diff should be shown.
   * Elsewhere null. set in {@link #startLog(String)}
   */
  String sLocalFile;


  /**The presentation of the time stamps. */
  SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");



  /**The current line and the line before, to the earlier commit. The Predecessor is not the parent in any case, not on branch and merge points. */
  //GralTable<RevisionEntry>.TableLineData currentLine, cmpLine;
  GralTableLine_ifc<RevisionEntry> currentLine, cmpLine;
  
  /**The current entry and the entry before, to the earlier commit. The Predecessor is not the parent in any case, not on branch and merge points. */
  RevisionEntry currentEntry, cmpEntry;
  

  public static void main(String[] args){
    GitGui main = new GitGui(args);
    //only test
    main.startLog("D:/GitArchive/D/vishia/srcJava_vishiaBase/.git", "D:/GitArchive/D/vishia/srcJava_vishiaBase", "org/vishia/util/CalculatorExpr.java");
    main.doSomethinginMainthreadTillCloseWindow();
  }
  

  public GitGui(String[] args) {
    if(args !=null && args.length >=1){
      sTypeOfImplementation = args[0];
    }
    initializeCmd();
    if(!settings.dirTemp1.exists()) { settings.dirTemp1.mkdirs(); }
    if(!settings.dirTemp2.exists()) { settings.dirTemp2.mkdirs(); }
    wdgTableVersion.specifyActionOnLineSelected(actionTableLineVersion);
    wdgTableFiles.specifyActionChange("actionTableFile", actionTableFile, null);
    window.specifyActionOnCloseWindow(actionOnCloseWindow);
    window.create(sTypeOfImplementation, 'B', null, initGraphic);
    cmdThread.start();
  }


  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("init")
  { @Override protected void executeOrder() {
      wdgTableFiles.addContextMenuEntryGthread(1, "diffView", "Diff view [Mouse double], [ctrl-Enter]", actionTableFileDiffView);
      wdgTableFiles.addContextMenuEntryGthread(1, "rename/move", "rename/move", actionTableFileRenMove);
      wdgTableFiles.addContextMenuEntryGthread(1, "restore", "Restore this file", actionRestore);
      wdgTableFiles.addContextMenuEntryGthread(1, "show Log for File", "Show log for this file [ctrl-s]", actionTableFileLog);
    }
  };




  GralUserAction actionOnCloseWindow = new GralUserAction("")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      GitGui.this.gitCmd.close();
      bCmdThreadClose = true;
      return true;
    }
  };


  @Override public void finalize()
  {
  }

  void initializeCmd() {
    gitCmd.setCharsetForOutput("UTF-8");  //git outputs in UTF-8
    Map<String, String> env = gitCmd.environment();
    env.put("HOMEPATH", "\\vishia\\HOME");
    env.put("HOMEDRIVE", "D:");
    String sPath = env.get("PATH");
    if(sPath ==null) { sPath = env.get("Path"); }
    if(sPath ==null) { sPath = env.get("path"); }
    sPath = "D:\\Programs\\Gitcmd\\bin;" + sPath;
    env.put("PATH", sPath);
  }


  public void doSomethinginMainthreadTillCloseWindow()
  { int ix = 0;
    while(! window.isGraphicDisposed()){
      try{ Thread.sleep(1000);} 
      catch (InterruptedException e) { }
    }
    
  }





  /**Search the base dir and the repository for a given path/to/file.
   * @param startFile Any file path. Especially a ".git" directory or ".gitRepository*" file. In that  case the local file path will be set to null,
   *   because the whole repository is given.
   *   A ".gitRepository*" file contains the path to the extern .git directory.
   * @param dst maybe null, elsewhere new String[2]. 
   *   dst[0] will be filled with the absolute path of the .git directory. 
   *   dst[1] will be filled with the absolute path to the basic directory for the working tree where .git or .gitRepository were found.
   *   dst[2] will be filled with the local file path or null if the .git or .gitRepositiory is the startFile.
   * @param dstMap maybe null, if not null it is a container maybe from JZcmd variables or an empty Map.
   * @param nameRepository if dstMap !=null, the name of the key (variable) for the repository path.
   * @param nameBasedir if dstMap !=null, the name of the key (variable) for the base directory.
   * @param nameLocalFile  if dstMap !=null, the name of the key (variable) for the local file.
   *   That variables will be set. nameLocalFile will be set to null if .git or .gitRepository is the startFile.
   * @return null on success or an error message 
   */
  public static String searchRepository(File startFile, String[] dst, Map<String, DataAccess.Variable<Object>> dstMap
  , String nameRepository, String nameBasedir, String nameLocalFile) 
  { File fRepo = null;
    File currDir;
    String ret = null;
    if(startFile.isDirectory()) {
      currDir = startFile;
      fRepo = new File(startFile, ".git"); //only to check whether it exists
      if(!fRepo.exists()){
        fRepo = new File(startFile, ".gitRepository");
        if(!fRepo.exists()) {  //if startFile as currdir contains .git or .gitRepository, it's okay
          currDir = null;  //not okay, startFile is any directory maybe inside a working tree
          fRepo = null;
        }
      }
    } else { //startFile is a file.
      String fName = startFile.getName();
      if(fName.equals(".git") || fName.endsWith(".gitRepository")) {
        fRepo = startFile;
        currDir = startFile.getParentFile();
      } else {
        currDir = null; //startFile is not a directory, search in parents.
      }
    }
    if(currDir == null) { //startFile is not a directory, or it is a directory which does not contain .git or .gitRepository 
      currDir = startFile.getParentFile();
      //search whether a .git or .gitRepository exists and change to parent dir till it is found.
      do{
        fRepo = new File(currDir, ".git"); //only to check whether it exists
        if(!fRepo.exists()){
          fRepo = new File(currDir, ".gitRepository");
          if(!fRepo.exists()){
            fRepo = null;
            try{
              currDir = FileSystem.getDirectory(currDir);  //NOTE: currDir.getParent() is not successfully on relative dir "."
            } catch(FileNotFoundException exc){ currDir = null;}
          }
        }
      } while(fRepo ==null && currDir !=null);
    }
    //
    //currdir and fRepo should be set, or not:
    if(fRepo == null){
      ret = "searchRepository - .git or .gitRepository not found ;" + startFile.getAbsolutePath();
    } else {
      assert(currDir !=null); //set together with fRepo
      String sBaseDir = FileSystem.normalizePath(currDir).toString();
      //dst.put(bzrdir, sBzrDir);
      String sRepository;
      if(fRepo.getName().endsWith(".gitRepository")){   //File with link to repository
        filePathRepository = fRepo;
        sRepository = FileSystem.readFile(fRepo).trim();
      } else {
        sRepository = sBaseDir;  
      }
      CharSequence sFilePath = FileSystem.normalizePath(startFile);
      String sLocalFilePath;
      if(currDir == startFile) {
        sLocalFilePath = null;
      } else {
        sLocalFilePath = sFilePath.subSequence(sBaseDir.length()+1, sFilePath.length()).toString();
        if(sLocalFilePath.length() == 0 || sLocalFilePath.equals(".git") || sLocalFilePath.endsWith(".gitRepository")){
          sLocalFilePath = null;  //no local file.
        }
      }
      if(dst !=null) { 
        dst[0] = sRepository; 
        dst[1] = sBaseDir; 
        dst[2] = sLocalFilePath; 
      }
      if(dstMap !=null) {
        try {
        DataAccess.createOrReplaceVariable(dstMap, nameBasedir, 'S', sBaseDir, true);
        DataAccess.createOrReplaceVariable(dstMap, nameRepository, 'S', sRepository, true);
        DataAccess.createOrReplaceVariable(dstMap, nameLocalFile, 'S', sLocalFilePath, true);
        } catch(Exception exc) {
          ret = "searchRepository - repository found, but write problems for dst Map,";
        }
      }
    }
    return ret;
  }



  /**Returns the opened repository or repoository linking file or searches the next .git or .gitRepository file
   * in the parent dir. It is to return the correct repository linking file for the opened GUI
   * to add something with Fcmd and jzTc
   * @param dir The dir where a file should be handled.
   * @return The opened filePathRepository only if the dir is in the same file tree.
   *   Elsewhere it searches .git or .gitRepository in this dir and parents.
   */
  public static File getFilePathRepository(File startFileSearchRepository) {
    if(filePathRepository !=null) {
      String sFilePathRepository = FileSystem.getCanonicalPath(filePathRepository.getParentFile());
      String sstartFileSearchRepository = FileSystem.getCanonicalPath(startFileSearchRepository);
      if(sstartFileSearchRepository.startsWith(sFilePathRepository)) {
        return filePathRepository;  //same or sub dir as filePathRepository: Proper.
      }
    }
    //else:
    return FileSystem.searchInParent(startFileSearchRepository, ".gitRepository", ".git");  //search either .gitRepository or .git
  }
  
  


  /**Searches the git repository and the root of the working tree and opens the window for the git gui. 
   * This routine can be invoked from any Java program without additional conditions. 
   * @param srcFile Any file to start searching the root of the working tree and the git repository inside this tree.
   *   If it is a directory and that directory contains either ".git" or ".gitRepository", it is the root of the working tree.
   *   Elsewhere the root will be searched backward to the root of the file system. 
   *   It means you can start this routine with any srcFile inside the working tree.
   *   This file will be used as additional argument for example to show the history of that file. 
   */
  public static void guiRepository(GitGuiPaths exepath, JZtxtcmdFilepath repoFile)
  {
    File srcFile = null;
    try {
      //copy to the working version of .gitignore   
      File fIgnore = new File(repoFile.absname() + ".gitignore");
      if(fIgnore.exists()) { 
        File fIgnore2 = new File(repoFile.absdir() + "/.gitignore");
        FileSystem.copyFile(fIgnore, fIgnore2);
      }
      srcFile = new File(repoFile.absfile().toString());
    } catch (NoSuchFieldException | IOException e) { System.err.println(e.getMessage());}
    String[] paths = new String[3];
    String error = searchRepository(srcFile, paths, null, null, null, null);
    if(error !=null) { System.err.println(error); }
    else {
      GitGui main = new GitGui(null);
      main.exepath = exepath;
      main.sFileList = repoFile.name() + ".filelist";
      main.startLog(paths[0], paths[1], paths[2]);
    }
  }


  /**Searches the git repository and the root of the working tree and opens the window for the git gui. 
   * This routine can be invoked from any Java program without additional conditions. 
   * @param srcFile Any file to start searching the root of the working tree and the git repository inside this tree.
   *   If it is a directory and that directory contains either ".git" or ".gitRepository", it is the root of the working tree.
   *   Elsewhere the root will be searched backward to the root of the file system. 
   *   It means you can start this routine with any srcFile inside the working tree.
   *   This file will be used as additional argument for example to show the history of that file. 
   */
  public static void showLog(File srcFile)
  {
    String[] paths = new String[3];
    String error = searchRepository(srcFile, paths, null, null, null, null);
    if(error !=null) { System.err.println(error); }
    else {
      GitGui main = new GitGui(null);
      main.startLog(paths[0], paths[1], paths[2]);
    }
  }




  public void startLog(String sGitDir, String sWorkingDir, String sLocalFile) {
    this.sGitDir = sGitDir; 
    this.sWorkingDir = sWorkingDir;
    this.workingDir = new File(sWorkingDir);
    startLog(sLocalFile);
  }
  
  /**
   * @param sLocalFile "*" for all files, else "path/in/loacal/tree/file.ext"
   */
  void startLog(String sLocalFile) {
    //this.sLocalFile = sLocalFile;
    String sPathShow;
    this.sLocalFile = sLocalFile;
    if(sLocalFile !=null) {
      sPathShow = sGitDir + " : " + sLocalFile;
      wdgBtnDiffCurrFile.setVisible(true);
      wdgBtnDiffCurrWork.setVisible(true);
    } else {
      sPathShow = sGitDir;
      wdgBtnDiffCurrFile.setVisible(false);
      wdgBtnDiffCurrWork.setVisible(false);
    }
    window.setTitle("Git " + sPathShow);
    
    wdgTableVersion.clearTable();
    wdgTableVersion.addLine("*", new String[] {"", "", "wait for prepairing log", ""}, null);
    gitOut.buffer().setLength(0);
    gitOut.assign(gitOut.buffer());   //to reset positions to the changed gitOut.buffer()
    String sGitCmd = "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    sGitCmd += " log --date=iso '--pretty=raw'";
    if(sLocalFile !=null && sLocalFile.length() >0) {
      sGitCmd +=  " -- '" + sLocalFile + "'";
    }
    wdgCmd.setText(sGitCmd);
    String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
    gitCmd.clearCmdQueue();
    gitCmd.abortCmd();
    gitOut.buffer().setLength(0);
    gitOut.assign(gitOut.buffer());   //to reset positions to the changed gitOut.buffer()
    gitCmd.addCmd(args, null, listOut, null, workingDir, exec_fillRevisionTable);
    synchronized(cmdThread) { cmdThread.notify(); }
    //int error = cmd.execute(args, null,  gitOut, null);
    //fillRevisionTable();
  }




  /**Fills the {@link #wdgTableVersion} with the gotten output after git log command. This routine is invoked as {@link #execAfterCmdLog}.
   * 
   */
  void fillRevisionTable() {
    gitOut.firstlineMaxpart();
    String lineTexts[] = new String[4];
    boolean contCommits = true;
    wdgTableVersion.clearTable();
    lineTexts[0] = "";
    lineTexts[1] = "*";
    lineTexts[2] = "--working area --";
    lineTexts[3] = "";
    wdgTableVersion.addLine("*", lineTexts, null);  
    RevisionEntry entryLast = null;
    do {
      if(gitOut.scanStart().scan("commit ").scanOk()) {
        contCommits = false;  //set to true on the next "commit " line.
        String hash = gitOut.getCurrentPart().toString();
        RevisionEntry entry = new RevisionEntry(hash);
        try {
          boolean cont = true;
          while (cont && gitOut.nextlineMaxpart().found()) {
            if(gitOut.scanStart().scan("tree ").scanOk()) {
              entry.treeHash = gitOut.getCurrentPart().toString();
            } 
            else if(gitOut.scanStart().scan("parent ").scanOk()) {
              entry.parentHash = gitOut.getCurrentPart().toString();
            }
            else if(gitOut.scanStart().scan("author ").scanOk()) {
              gitOut.lento('<');
              entry.author = gitOut.getCurrentPart().toString();
              gitOut.fromEnd().seekPosBack(16).lento(' ');
              if(gitOut.scan().scanInteger().scanOk()) { 
                entry.dateAuthor = new Date(1000*gitOut.scan().getLastScannedIntegerNumber());
              }
            } else if(gitOut.scanStart().scan("committer ").scanOk()) {
              gitOut.lento('<');
              entry.committer = gitOut.getCurrentPart().toString();
              gitOut.fromEnd().seekPosBack(16).lento(' ');
              if(gitOut.scan().scanInteger().scanOk()) { 
                entry.dateCommit = new Date(1000*gitOut.scan().getLastScannedIntegerNumber());
              }
            } else {
              do {
                if(gitOut.scanStart().scan("commit ").scanOk()) {
                  gitOut.seekBegin();
                  cont = false;
                  contCommits = true;
                  break;
                } else {
                  CharSequence commitTextline = gitOut.getCurrentPart();
                  if(entry.commitTitle == null && commitTextline.length() >6){
                    entry.commitTitle = commitTextline.toString();  //first line with at least 6 character: Use as title.
                  }
                  entry.commitText.append(commitTextline).append('\n');
                }
              } while(cont && (gitOut.nextlineMaxpart().found()));
            }
          }//while lines of one commit 
        }catch(Exception exc) {
          Debugutil.stop();
        }
        if(entry.dateAuthor !=null) {
          String cL = "A";
          if(entryLast !=null && entryLast.parentHash !=null && entryLast.parentHash.equals(entry.revisionHash)){ cL = "B"; }
          lineTexts[0] = cL;
          lineTexts[1] = dateFormat.format(entry.dateAuthor);
          lineTexts[2] = entry.commitTitle;
          lineTexts[3] = entry.author;
          GralTableLine_ifc<RevisionEntry> line = wdgTableVersion.addLine(hash, lineTexts, entry);  
          line.repaint();
        }
        entryLast = entry;
      } //
      else {
        contCommits = gitOut.nextlineMaxpart().found();
      }
    } while(contCommits);
    //wdgTableVersion.
    wdgTableVersion.repaint();
  }


  /**Starts the command 'git diff' to fill the {@link #wdgTableFiles} for the selected revision.
   * 
   * @param line
   */
  void showLog_startRevisionDiff4FileTable(GralTable<RevisionEntry>.TableLineData line) {
    if(line == null){
       return;
    }
    //
    //abort the current cmd for diff view (or any other)
    gitCmd.clearCmdQueue();
    gitCmd.abortCmd();
    gitOut.buffer().setLength(0);
    gitOut.assign(gitOut.buffer());   //to reset positions to the changed gitOut.buffer()
    //
    //
    GitGui.this.currentLine = line;
    GralTable<RevisionEntry> table = line.getTable();
    List<GralTableLine_ifc<RevisionEntry>> markedLines = table.getMarkedLines(1);
    GitGui.this.cmpLine = line.nextSibling();
    if(markedLines !=null && markedLines.size() >0) {
      if(markedLines.size() ==1) {
        cmpLine = markedLines.get(0);
      }
      else {
        System.err.println("more as one line marked");
      }
    }
    GitGui.this.currentEntry = line.data;
    GitGui.this.cmpEntry = cmpLine == null ? null : cmpLine.getUserData();
    String sGitCmd = "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    if(currentEntry ==null) {
      String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd + " status"};
      gitCmd.addCmd(args, null, listOut, null, workingDir, exec_ShowStatus);
      wdgInfo.setText("(working area)");
      try{
        wdgInfo.append("\n");
      } catch(IOException exc){}
      sGitCmd += " diff --name-status HEAD";
    } else {
      wdgInfo.setText(currentEntry.revisionHash);
      try{
        wdgInfo.append("=>").append(currentEntry.parentHash).append(" @").append(currentEntry.treeHash).append("\n");
        if(cmpEntry !=null) {
          wdgInfo.append(cmpEntry.revisionHash).append("=>").append(cmpEntry.parentHash).append(" @").append(cmpEntry.treeHash).append("\n");
        }
        wdgInfo.append(currentEntry.commitText);
      } catch(IOException exc){}
      sGitCmd += " diff --name-status " + currentEntry.parentHash + ".." + currentEntry.revisionHash;
    }
    wdgCmd.setText(sGitCmd);
    String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
    //
    gitCmd.addCmd(args, null, listOut, null, workingDir, exec_fillFileTable4Revision);
    synchronized(cmdThread) { cmdThread.notify(); }
  }
  
  

  void restoreFile(String sFile) {
    String sGitCmd2 = "git '--git-dir=" + sGitDir + "' checkout " + cmpEntry.revisionHash + " -- " + this.sFileList; ///
    wdgCmd.setText(sGitCmd2);
    String[] args2 ={exepath.gitsh_exe, "-x", "-c", sGitCmd2};
    gitCmd.addCmd(args2, null, listOut, null, workingDir, null);

    String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + cmpEntry.revisionHash + " -- " + sFile;
    String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
    gitCmd.addCmd(args, null, listOut, null, workingDir, 
      new CmdExecuter.ExecuteAfterFinish()
        { @Override
          public void exec(int errorcode, Appendable out, Appendable err)
          { try {FileList.touch(sWorkingDir, GitGui.this.sFileList, sFile, null);
            } catch (IOException exc) {
              System.err.println("IOexception for File.touch()");
            }
          }
        }
     );   



    //
    
    //
  }


  
  void addFileFromSelection() {
    int pos = wdgInfo.getCursorPos();
    String text = wdgInfo.getText();
  
    String sGitCmd = "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    sGitCmd += " add ";
    wdgCmd.setText(sGitCmd);
  
  }


  void moveFileListToSelection() {
    int pos = wdgInfo.getCursorPos();
    String text = wdgInfo.getText();
    
    
    
    
    GralTable<String>.TableLineData line = wdgTableFiles.getCurrentLine();
    int posSep = sGitDir.lastIndexOf('/');
    assert(sGitDir.substring(posSep).equals("/.git"));
    String sGitCmd = sGitDir.substring(0, posSep) + ">git mv ";
    sGitCmd += line.getCellText(2) + "/" + line.getCellText(1) + " ";
    
    wdgCmd.setText(sGitCmd);


  
  
  }


  /**Gets the file from repository and writes to tmp directory, starts diff tool
   * @param sFile The local path of the file
   * @param currRev maybe null, then compare with working tree, elsewhere a selected revision
   * @param cmpRev the selected revision to compare.
   */
  void startDiffView(String sFile,  RevisionEntry currRev, RevisionEntry cmpRev) {
    String sFile1 = settings.dirTemp1.getAbsolutePath() + "/" + sFile;
    String sFile2 = settings.dirTemp2.getAbsolutePath() + "/" + sFile;
    if(currRev == null) {
      sFile1 = sWorkingDir + "/" + sFile;
    } else  { 
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + currRev.revisionHash + " -- " + sFile;
      String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
      gitCmd.addCmd(args, null, listOut, null, settings.dirTemp1, null);
    }
    if(cmpRev == null) {
      sFile2 = sFile1;
    }
    else { 
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + cmpRev.revisionHash + " -- " + sFile;
      wdgCmd.setText(settings.dirTemp2 + ">" + sGitCmd);
      String[] args ={exepath.gitsh_exe, "-x", "-c", sGitCmd};
      gitCmd.addCmd(args, null, listOut, null, settings.dirTemp2, null);
    }
    sFile1 = sFile1.replace('/', '\\');
    sFile2 = sFile2.replace('/', '\\');
    String sCmdDiff = "cmd.exe /C start " + exepath.diff_exe + " " + sFile1 + " " + sFile2;
    String[] cmdDiffView = CmdExecuter.splitArgs(sCmdDiff); 
    gitCmd.addCmd(cmdDiffView, null, listOut, null, null, null);
    synchronized(cmdThread) { cmdThread.notify(); }
  }



  
  void openNewFileSelector(String sFile,  RevisionEntry currRev) {
    //wdgViewFiles.openDialog(FileRemote.fromFile(workingDir), "files", false, null);
  }
  
  
  
  
  /**Fills the file table with the gotten output after git diff name-status command. This routine is invoked as {@link #exec_fillFileTable4Revision}.
   * 
   */
  void fillFileTable4Revision() {
    wdgTableFiles.clearTable();
    GralTableLine_ifc<String> line = wdgTableFiles.addLine("*", new String[] {"!","(all files)",""}, "*");  
    wdgTableFiles.repaint();
    gitOut.firstlineMaxpart();
    do {
      String sLine = gitOut.getCurrentPart().toString();
      //the line starts with the status character, then '\t', then the file path.
      if(!sLine.startsWith("+ git") && sLine.length() >2) {
        String[] col = new String[3];
        int posSlash = sLine.lastIndexOf('/');
        col[0] = sLine.substring(0,1);
        if(posSlash >0) {
          col[1] = sLine.substring(posSlash+1);
          col[2] = sLine.substring(2, posSlash);
        } else {
          col[1] = sLine.substring(2);
          col[2] = "";
        }
        String key = sLine.substring(2); //without "M\t" (sign of change from <code>diff --name-status</code> output )
        line = wdgTableFiles.addLine(key, col, sLine);  
        line.repaint();
      }
    } while(gitOut.nextlineMaxpart().found());
  }



  Thread cmdThread = new Thread("gitGui-Cmd") {
    @Override public void run() {
      do {
        gitCmd.executeCmdQueue(true);
        try {
          synchronized(this){ wait(1000); }
        } catch (InterruptedException e) { }
      } while (!bCmdThreadClose);
    }
  };




}
