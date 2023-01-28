package org.vishia.gitGui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.vishia.cmd.CmdExecuter;
import org.vishia.cmd.JZtxtcmdFilepath;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralTextFieldUser_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.msgDispatch.LogMessageStream;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;
import org.vishia.util.FileList;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions;
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
   * <li>2023-01-28 Hartmut {@link #actionCommit} Now an existing script "+buildTimestamps.sh" is called instead {@link FileList#list()}
   *   because this script contains options which files should be placed in the list. It is the same script callable by the user.
   *   Hence the problem that the ".filelist" contains unnecessary (stupid) file, is solved.
   * <li>2022-06-06 Hartmut the error msg on {@link #execCmd(String, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)} using
   *   {@link #exec_ShowStatus} is improved. It is now appended, to preserve a error message from some commands one after another.
   *   For example on {@link #actionDiffVersion} {@link #startDiffView(String, RevisionEntry, RevisionEntry)}.
   * <li>2022-06-06 Hartmut to definitely clean the info box a "clean" button was added. This is the new outfit.
   * <li>2022-06-06 Hartmut now the ".gitRepository" file can contains more as one line, only the first line without trailing spaces is used.
   *   This enables shift other info in the other lines (more as one repository location).
   *   Secondly, the path in ".gitRepository" can also be a local path, starting from this file. 
   *   This is proper if a "src_git/.git" is beside "src". 
   * <li>2022-01-21 Hartmut now the whole sh.exe call is determinable by the -gitsh option 
   *   which should be ""'C:\\Program Files\\git\\bin\\sh.exe' -x -c"" 
   * <li>2022-01-21 Hartmut echo of command line in info window
   * <li>2022-01-21 Hartmut some, not all refactoring for warnings "Unqualified access"
   * <li>2022-01-21 Hartmut for {@link #actionExecCmd}: If starts with !! then show only "done" on success, else show whole output
   * <li>2022-01-21 Hartmut {@link #prepArgs(String)}) is new, gets the sh.exe call with -x -c completely from the -getsh option.
   *    used on all {@link #gitCmd}.addCmd(...)
   * <li>2022-01-21 Hartmut reorganization of the button positions.
   * <li>2022-01-21 Hartmut {@link #cmdThread}: possible to set a debug breakpoint if contains commands.
   * <li>2020-02-12 Hartmut Now main works (in Test), able to invoke as standalone Java applic. 
   * <li>2020-01-15 Hartmut {@link #startLog(String)} shows the working dir too. 
   * <li>2020-01-15 Hartmut {@link #wdgTableVersion} with fix width, {@link #wdgTableFiles} variable width on resizing of the window. Better for view. 
   * <li>2019-12-25 Hartmut {@link #actionFetch}, {@link #actionPull}. {@link #actionPull} not ready yet. 
   * <li>2019-12-10 Hartmut in {@link #actionExecCmd}, {@link #actionAdd} etc.: shows the cmd output via {@link #execShowCmdOutputDone} 
   * <li>2019-12-10 Hartmut in {@link #exec_CommitDone}: shows commit error 
   * <li>2019-11-25 Hartmut {@link #moveFileListToSelection()} improved creates one directory level on git working dir.  
   * <li>2019-09-00 Hartmut new {@link #actionOutputZip} 
   * <li>2019-04-25 Hartmut Now renamed files are shown with full history, compare with older renamed files is possible. 
   * <li>2019-04-24 Hartmut Enhancements for view diff for some revisions. Shows the date as temp directory name. 
   * <li>2019-04-02 Hartmut Enhancements for add and mov: detect the selected line in status window for new file name (in 'Untracked files:' area),
   *   detect missing file for mov in file list. . 
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
  public final String sVersion = "2023-01-28";  


  static class RevisionEntry
  {
    final String revisionHash;
    /**Output of --name-status*/
    String filePath;
    /**M or A etc. */
    String changeKind;
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


  protected GralMng gralMng = new GralMng(new LogMessageStream(System.out));


  /**Action for open the commit text. 
   * 
   */
  GralUserAction actionShowExec = new GralUserAction("actionShowExec")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      Appendable echoOut = GitGui.this.wdgBtnShowCmd.isOn() ? GitGui.this.wdgInfo : null;
      GitGui.this.gitCmd.setEchoCmdOut(echoOut); //System.out);
      return true;
  } };


  GralUserAction actionCleanInfo = new GralUserAction("actionCleanInfo") { 
    @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      GitGui.this.wdgInfo.setText("");  
      return true;
  } };


  GralUserAction actionOpenCommitText = new GralUserAction("actionTableOpenCommitText")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      String[] args ={"cmd.exe", "/C", "edit", ".gitCommit"};
      GitGui.this.wdgCmd.setText("cmd.exe /C edit .gitCommit");
      GitGui.this.gitCmd.addCmd(args, null, GitGui.this.listOut, null, GitGui.this.workingDir, null);
      GitGui.this.wdgBtnCommit.setText("do commit");
      return true;
  } };


  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_FileListDone = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { if(errorcode !=0) {
        GitGui.this.wdgBtnCommit.setText("filelist error");
        GitGui.this.wdgInfo.setText(GitGui.this.gitOut);
        GitGui.this.gitOut.clear();
      }
    }
  }; 



  /**This code snippet is executed after the 'git diff' command for 2 revisions are executed. 
   * It is used as last argument of {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * and prepares the {@link #wdgTableFiles}.
   */
  CmdExecuter.ExecuteAfterFinish exec_CommitDone = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { if(errorcode ==0) {
        FileFunctions.writeFile("\n", GitGui.this.sWorkingDir + "/.gitcommit");
        GitGui.this.wdgBtnCommit.setText("commit done");
      } else {
        GitGui.this.wdgBtnCommit.setText("commit error");
        GitGui.this.wdgInfo.setText(GitGui.this.gitOut);
        GitGui.this.gitOut.clear();
      }
    }
  }; 



  /**Action for do commi. 
   * 
   */
  GralUserAction actionCommit = new GralUserAction("actionCommit")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      String nameFileListSh = "+buildTimestamps.sh";
      File fileList = new File(GitGui.this.workingDir, nameFileListSh);
      if(fileList.exists()) {
        String sFilelistCmd = "./" + nameFileListSh;     // call existing +buildTimestamps.sh to build the file list 
        String[] argsFileList = prepArgs(sFilelistCmd);  // exec_CommitDone: Writes an error message if sh failes.
        GitGui.this.gitCmd.addCmd(argsFileList, null, GitGui.this.listOut, null, GitGui.this.workingDir, GitGui.this.exec_FileListDone);
      } else {                                           // +buildTimestamps.sh not found, then
        try{ FileList.list(GitGui.this.sWorkingDir, "*", GitGui.this.sFileList);  //build from all files without mask
        } catch(Exception exc){
          GitGui.this.wdgInfo.setText("_filelist.lst problem: " + exc.getMessage());
        }
      }
      //
      File gitCommit = new File(GitGui.this.sWorkingDir, ".gitCommit");
      if(gitCommit.length() > 3) {
        String sGitCmd = "git";
        if(! GitGui.this.sGitDir.startsWith(GitGui.this.sWorkingDir)) {
          sGitCmd += " '--git-dir=" + GitGui.this.sGitDir + "'";
        }
        sGitCmd += " commit -a -F .gitcommit";
        GitGui.this.wdgCmd.setText(sGitCmd);
        String[] args = prepArgs(sGitCmd);
        GitGui.this.gitCmd.addCmd(args, null, GitGui.this.listOut, null, GitGui.this.workingDir, GitGui.this.exec_CommitDone);
      } else {
        GitGui.this.wdgBtnCommit.setText("do commit text?");
      }
      return true;
  } };


  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionRefresh = new GralUserAction("actionRefresh")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        startLog(); return true;
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
    public void exec(int errorcode, Appendable out, Appendable err) { 
      //GitGui.this.wdgInfo.setText(GitGui.this.gitOut);  //The status info
       
      try{
        if(errorcode !=0) { GitGui.this.wdgInfo.append("ERROR: " + errorcode + "\n"); }
        GitGui.this.wdgInfo.append(GitGui.this.gitOut); 
      } catch(Exception exc) {} //nothing, not expected
      GitGui.this.gitOut.buffer().setLength(0);  //prepare for the next command.
      GitGui.this.gitOut.assign(GitGui.this.gitOut.buffer());   //to reset positions to the changed gitOut.buffer()
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
      case (KeyCode.ctrl | 's'): GitGui.this.sLocalFile = sFile; startLog(); return true;
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
  GralUserAction actionDiffVersion = new GralUserAction("actionDiffVersion")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line = wdgTableVersion.getCurrentLine();
        RevisionEntry revision = line.getData();
        GralTable<RevisionEntry>.TableLineData line2 = wdgTableVersion.getLineMousePressed();
        RevisionEntry revision2 = line2.getData();
        GralTable<String>.TableLineData lineFile = wdgTableFiles.getCurrentLine();
        String sLine = lineFile.getData();
        if(sLine !=null && sLine.length() >=2) {
          String sFile = sLine.substring(2);  //pos 0 is change-sign, pos 1 is \t
          startDiffView(sFile, revision2, revision); 
        }
        return true;
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


  /**Action for diff view of the current file between revisions.*/
  GralUserAction actionFileBlame = new GralUserAction("actionFileBlame")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line = wdgTableVersion.getCurrentLine();
        RevisionEntry revision = line.getData();
        startBlame(sLocalFile, null, revision); return true;
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
        GitGui.this.sLocalFile = sFile;
        startLog(); return true;
      } //if;
      return false;
  } };


  GralUserAction actionExecCmd = new GralUserAction("actionExecCmd")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText();
        CmdExecuter.ExecuteAfterFinish execAfter = GitGui.this.execShowCmdOutput;
        if(cmd.startsWith("!!")) {     // A typical git cmd in execution phase
          cmd = cmd.substring(2);
          execAfter = GitGui.this.execShowCmdOutputDone;
        }
        execCmd(cmd, execAfter);
      } //if;
      return false;
  } };


  
  
  /**Action for check the remote archive. 
   * 
   */
  GralUserAction actionDaylyBranch = new GralUserAction("actionDaylyBranch")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!!")) {
          cmd = cmd.substring(2);
          execCmd(cmd, exec_ShowStatus);
        } else {
          String sGitCmd = "!!" + "git";
          if(! GitGui.this.sGitDir.startsWith(GitGui.this.sWorkingDir)) {
            sGitCmd += " '--git-dir=" + GitGui.this.sGitDir + "'";
          }
          sGitCmd += " checkout -b XXX";
          GitGui.this.wdgCmd.setText(sGitCmd);
        }
      } //if;
      return false;
  } };


  /**Action for check the remote archive. 
   * 
   */
  GralUserAction actionMainBranch = new GralUserAction("actionMainBranch")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!!")) {
          cmd = cmd.substring(2);
          execCmd(cmd, exec_ShowStatus);
        } else {
          String sGitCmd = "!!" + "git";
          if(! GitGui.this.sGitDir.startsWith(GitGui.this.sWorkingDir)) {
            sGitCmd += " '--git-dir=" + GitGui.this.sGitDir + "'";
          }
          sGitCmd += " checkout master";
          GitGui.this.wdgCmd.setText(sGitCmd);
        }
      } //if;
      return false;
  } };



  GralUserAction actionPull = new GralUserAction("actionPull")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!!")) {
          cmd = cmd.substring(2);
          execCmd(cmd, exec_ShowStatus);
        } else {
          String sGitCmd = "!!" + "git";
          if(! GitGui.this.sGitDir.startsWith(GitGui.this.sWorkingDir)) {
            sGitCmd += " '--git-dir=" + GitGui.this.sGitDir + "'";
          }
          sGitCmd += " pull --force";
          GitGui.this.wdgCmd.setText(sGitCmd);
        }
      } //if;
      return false;
  } };



  /**Action for check the remote archive. 
   * 
   */
  GralUserAction actionPush = new GralUserAction("actionPush")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!!")) {
          cmd = cmd.substring(2);
          execCmd(cmd, exec_ShowStatus);
        } else {
          String sGitCmd = "!!" + "git";
          if(! GitGui.this.sGitDir.startsWith(GitGui.this.sWorkingDir)) {
            sGitCmd += " '--git-dir=" + GitGui.this.sGitDir + "'";
          }
          sGitCmd += " push ";
          GitGui.this.wdgCmd.setText(sGitCmd);
        }
      } //if;
      return false;
  } };



  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionAdd = new GralUserAction("actionAdd")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!! ")) {
          cmd = cmd.substring(3);
          execCmd(cmd, execShowCmdOutputDone);
        } else {
          addFileFromSelection(); return true;
        }
      } //if;
      return false;
  } };



  /**Action for show the version table for the given file. 
   * 
   */
  GralUserAction actionMove = new GralUserAction("actionMove")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        String cmd = GitGui.this.wdgCmd.getText().trim();
        if(cmd.startsWith("!! ")) {
          cmd = cmd.substring(3);
          execCmd(cmd, execShowCmdOutputDone);
        } else {
          moveFileListToSelection(); return true;
        }
      } //if;
      return false;
  } };




  CmdExecuter.ExecuteAfterFinish execShowCmdOutputDone = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { if(errorcode ==0) {
        wdgCmd.setText("done");
      } else {
        wdgInfo.setText("cmd ouptut:\n");
        try { wdgInfo.append(gitOut);
        } catch (Exception e) { }
      }
  } };

  
  
  
  CmdExecuter.ExecuteAfterFinish execShowCmdOutput = new CmdExecuter.ExecuteAfterFinish()
  { @Override
    public void exec(int errorcode, Appendable out, Appendable err)
    { wdgInfo.setText("cmd ouptut:\n");
      try { wdgInfo.append(gitOut);
      } catch (Exception e) { }
  } };

  
  
  
  GralTextFieldUser_ifc wdgInfoSetSelection = new GralTextFieldUser_ifc() {
    @Override
    public boolean userKey(int keyCode, String content, int cursorPos, int selectStart, int selectEnd) {
      GitGui.this.cursorPosInfo = cursorPos;
      boolean bDone = true;
      return bDone;
    }
  };

  
  
  
  
  /**Action for diff view of the current file to the workspace.*/
  GralUserAction actionOutputZip = new GralUserAction("actionOutputZip")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){ 
        GralTable<RevisionEntry>.TableLineData line2 = wdgTableVersion.getLineMousePressed();
        RevisionEntry revision = line2.getData();
//        GralTable<String>.TableLineData lineFile = wdgTableFiles.getCurrentLine();
//        String sLine = lineFile.getData();
        int posSep = GitGui.this.sWorkingDir.lastIndexOf('/');
        String sZipName = posSep >0 ? GitGui.this.sWorkingDir.substring(posSep +1) : "Version";
        String sDate = dateFmtyyMMdd.format(revision.dateCommit);
        String sGitCmd = "git '--git-dir=" + sGitDir + "' archive  -o " + sZipName + "-" + sDate + ".zip " + revision.revisionHash; ///
        wdgCmd.setText(sGitCmd);
        String[] args = prepArgs(sGitCmd);
        gitCmd.addCmd(args, null, listOut, null, workingDir, null);

//        if(sLine !=null && sLine.length() >=2) {
//          String sFile = sLine.substring(2);  //pos 0 is change-sign, pos 1 is \t
//        }
        return true;
      } //if;
      return false;
  } };



  /**The paths to the executables. */
  GitGuiPaths exepath;
  
  /**The only one opened git repository. null if nothing is open or more as one is open.
   * 
   */
  static File filePathRepository;
  
  
  String sTypeOfImplementation = "SWT";  //default
  
  final GralWindow window = this.gralMng.addWindow("@screen,10+60, 10+90=GitGui", "Git vishia");

  final GralTextField wdgCmd = this.gralMng.addTextField("@2-2,0..-7=cmd", GralTextField.Type.editable);
  
  final GralButton wdgBtnCmd = this.gralMng.addButton("@0..2, -6..0 = cmdExec", "exec", this.actionExecCmd);

  
  final GralTable<RevisionEntry> wdgTableVersion = new GralTable<RevisionEntry>(this.gralMng.refPos(), "@3..-30,0..50=git-versions", 50, new int[] {2, 10, 0, -10});
  
  final GralTable<String> wdgTableFiles = new GralTable<String>(this.gralMng.refPos(), "@3..-30,51..0=git-files", 50, new int[] {3,20,0});
  
  final GralTextBox wdgInfo = this.gralMng.addTextBox("@-30..0, 0..-20=info");

  final GralButton wdgBtnDiffCurrWork = new GralButton(this.gralMng.refPos(), "@-30+2, -18..-2 = diffCurrWork", "diff current file to workspace", this.actionDiffCurrWork);

  final GralButton wdgBtnDiffCurrFile = new GralButton(this.gralMng.refPos(), "@-27+2, -18..-2 = diffCurrFile", "diff current file", this.actionFileDiffRev);

  final GralButton wdgBtnPull = new GralButton(this.gralMng.refPos(), "@-21+2, -20..-15 = pull", "pull", this.actionPull);

  final GralButton wdgBtnPush = new GralButton(this.gralMng.refPos(), "@-21+2, -7..-1 = push", "push", this.actionPush);

  final GralButton wdgBtnBlame = new GralButton(this.gralMng.refPos(), "@-18..-21, -18..-2 = blameFile", "blame", this.actionFileBlame);

  final GralButton wdgBtnDaylyBranch = new GralButton(this.gralMng.refPos(), "@-15+2, -20..-12 = daylyBranch", "daylyBranch", this.actionDaylyBranch);

  final GralButton wdgBtnDaylyMain = new GralButton(this.gralMng.refPos(), "@-15+2, -10..-2 = mainBranch", "mainBranch", this.actionMainBranch);

  final GralButton wdgBtnAdd = new GralButton(this.gralMng.refPos(), "@-12+2, -9..-1 = add", "add", this.actionAdd);

  final GralButton wdgBtnMove = new GralButton(this.gralMng.refPos(), "@-12+2, -18..-10 = move", "move", this.actionMove);

  final GralButton wdgBtnCommitText = new GralButton(this.gralMng.refPos(), "@-9+2, -20..-10 = commitText", "commit-text", this.actionOpenCommitText);

  final GralButton wdgBtnCommit = new GralButton(this.gralMng.refPos(), "@-6+2, -20..-10 = commit", "do commit", this.actionCommit);

  final GralButton wdgBtnRefresh = new GralButton(this.gralMng.refPos(), "@-6+2, -9..-1 = refresh", "refresh", this.actionRefresh);

  final GralButton wdgBtnCleanInfo = new GralButton(this.gralMng.refPos(), "@-3+2, -20..-10 = cleanInfo", "clean Info", this.actionCleanInfo);
  
  final GralButton wdgBtnShowCmd = new GralButton(this.gralMng.refPos(), "@-3+2, -9..-1 = showCmd", "showCmd", this.actionShowExec);
  


  /**If set to true, the {@link #cmdThread} should be aborted.
   * 
   */
  boolean bCmdThreadClose;

  final CmdExecuter gitCmd = new CmdExecuter();


  /**Destination for output of all command line invocations.
   * This buffer will be cleared and filled with the git command, and then parsed to present the result. 
   */
  final StringPartAppend gitOut = new StringPartAppend();
  
  /**The {@link CmdExecuter#execute(String[], boolean, String, List, List, org.vishia.cmd.CmdExecuter.ExecuteAfterFinish)}
   * needs a list of appendable, that is it.*/
  final List<Appendable> listOut = new LinkedList<Appendable>();
  { listOut.add(gitOut); }
  
  
  
  /**Stored arguments from {@link #startLog(String, String, String)}. */
  final String sGitDir, sWorkingDir; //, sLocalFile;

  
  
  final File workingDir;
  
  final String sFileList;

  /**If given the file which's log and diff should be shown.
   * Elsewhere null. set in {@link #startLog(String)}
   */
  String sLocalFile;


  /**The presentation of the time stamps. */
  final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");



  /**The current line and the line before, to the earlier commit. The Predecessor is not the parent in any case, not on branch and merge points. */
  //GralTable<RevisionEntry>.TableLineData currentLine, cmpLine;
  GralTableLine_ifc<RevisionEntry> currentLine, cmpLine;
  
  /**The current entry and the entry before, to the earlier commit. The Predecessor is not the parent in any case, not on branch and merge points. */
  RevisionEntry currentEntry, cmpEntry;
  
  int cursorPosInfo;
  
  final SimpleDateFormat dateFmtyyMMdd = new SimpleDateFormat("yy-MM-dd");

  
  /**For stand alone application: Call as doubleclick in windows:
   * The only one argument is the full path to a .git file directory or .git file or name.gitRepository file.
   * More arguments which controls where git is found, which graphic size, where is the diff viewer are possible
   * Describe it, todo.
   * @param sCmdArgs 
   */
  public static void main(String[]sCmdArgs){
    GitGuiCmd.CmdArgs cmdArgs = GitGuiCmd.parseArgsGitGui(sCmdArgs);
    if(cmdArgs !=null) {
      //guiRepository(settings, null);
      String[] paths = new String[3];
      //String error = searchRepository(new File(cmdArgs.startFile), paths, null, null, null, null);
      //if(error !=null) { System.err.println(error); }
      //else 
      {
        GitGui main = new GitGui(new File(cmdArgs.startFile), cmdArgs);
        main.startLog();
        main.doSomethinginMainthreadTillCloseWindow();
      }

    }
    //only test
  }
  

  public GitGui(File startfile, GitGuiCmd.CmdArgs cmdArgs) {
//    if(args !=null && args.length >=1){
//      sTypeOfImplementation = args[0];
//    }
    String[] files = new String[4];
    try {
      detectGitArchive(startfile, files);
    } catch (IOException e) {
      //will be shown
    }
    this.sGitDir = files[0]; //null on exception
    this.sWorkingDir = files[1];  //null on exception
    this.workingDir = new File(this.sWorkingDir);
    this.sLocalFile = files[2];  //null if not given
    this.sFileList = files[3] + ".filelist";
    
    initializeCmd(cmdArgs);
    this.exepath = cmdArgs.guiPaths;
    if(!this.exepath.dirTemp1.exists()) { this.exepath.dirTemp1.mkdirs(); }
    if(!this.exepath.dirTemp2.exists()) { this.exepath.dirTemp2.mkdirs(); }
    this.wdgTableVersion.specifyActionOnLineSelected(this.actionTableLineVersion);
    this.wdgTableFiles.specifyActionChange("actionTableFile", this.actionTableFile, null);
    this.wdgInfo.setUser(this.wdgInfoSetSelection);
    this.window.specifyActionOnCloseWindow(this.actionOnCloseWindow);
    
    GralMenu menuTableFiles = this.wdgTableFiles.getContextMenuColumn(1);
    menuTableFiles.addMenuItem("diffView", "Diff view [Mouse double], [ctrl-Enter]", actionTableFileDiffView);
    menuTableFiles.addMenuItem("rename/move", "rename/move", actionTableFileRenMove);
    menuTableFiles.addMenuItem("restore", "Restore this file", actionRestore);
    menuTableFiles.addMenuItem("show log for File", "Show log for this file [ctrl-s]", actionTableFileLog);
    //
    //Note for all columns extra:
    
    GralMenu menuTableVersion = this.wdgTableVersion.getContextMenuColumn(1);
    menuTableVersion.addMenuItem("cmpVersion", "create zip form this version", actionOutputZip);
    menuTableVersion.addMenuItem("cmpVersion", "cmp with this version", actionDiffVersion);
    wdgTableVersion.setContextMenuColumn(2, menuTableVersion);
    
    
    this.gralMng.createGraphic(this.sTypeOfImplementation, cmdArgs.graphicSize.charAt(0), null); //, this.initGraphic);
    this.wdgBtnShowCmd.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("lgn"));
    this.wdgCmd.setHtmlHelp(":GitGui.html#exec");
    this.cmdThread.start();
  }

  
  
  /**
   * @param startfile
   * @param files
   * @param dst maybe null, elsewhere new String[2]. 
   *   files[0] will be filled with the absolute path of the .git directory. 
   *   files[1] will be filled with the absolute path to the basic directory for the working tree where .git or .gitRepository were found.
   *   files[2] will be filled with the local file path or null if the .git or .gitRepositiory is the startFile.
   * @throws IOException 
   */
  void detectGitArchive(File startfile, String[] files) throws IOException {
    String fname = startfile.getName();
    File fgit;
    File fshow;
    if(fname.equals(".git") || fname.endsWith(".gitRepository")) {
      fgit = startfile.isAbsolute() ? startfile : startfile.getAbsoluteFile();
      fshow = null;
    } 
    else {
      fgit = FileFunctions.searchFileInParent(startfile, ".git", "*.gitRepository");
      fshow = startfile;
    }
    if(fgit ==null) { //no repository found
      files[0] = files[1] = null; 
    } 
    else if(fgit.getName().equals(".git")) {
      files[0] = FileFunctions.normalizePath(fgit).toString();
      files[1] = FileFunctions.normalizePath(fgit.getParentFile()).toString();
      files[3] = "";
    }
    else {
      String fRepo = fgit.getName();
      assert(fRepo.endsWith(".gitRepository"));
      String sRemote = FileFunctions.readFile(fgit);       // use the first line in this file as path, without trailing spaces
      int end = StringFunctions.indexOfAnyChar(sRemote, 0, -1, "\r\n");
      if(end >0) {
        sRemote = sRemote.substring(0, end);
      }
      sRemote = sRemote.trim();
      if( !FileFunctions.isAbsolutePath(sRemote)) {
        sRemote = FileFunctions.normalizePath(new File(fgit.getParentFile(), sRemote)).toString();
      }
      files[0] = sRemote.trim();
      files[1] = FileFunctions.normalizePath(fgit.getParent().replace('\\', '/')).toString();
      files[3] = fRepo.substring(0, fRepo.length() - 14); //part before .gitRepository, maybe ""
    }
    if(fshow !=null) {
      CharSequence workingfile = FileFunctions.normalizePath(fshow);
      files[2] = workingfile.subSequence(files[1].length()+1, workingfile.length() ).toString();
    } else {
      files[2] =null;
    }
  }
  
  
  

//  GralGraphicTimeOrder initGraphic = new GralGraphicTimeOrder("init", this.gralMng)
//  { @Override protected void executeOrder() {
//      wdgTableFiles.addContextMenuEntryGthread(1, "diffView", "Diff view [Mouse double], [ctrl-Enter]", actionTableFileDiffView);
//      wdgTableFiles.addContextMenuEntryGthread(1, "rename/move", "rename/move", actionTableFileRenMove);
//      wdgTableFiles.addContextMenuEntryGthread(1, "restore", "Restore this file", actionRestore);
//      wdgTableFiles.addContextMenuEntryGthread(1, "show log for File", "Show log for this file [ctrl-s]", actionTableFileLog);
//      //
//      //Note for all columns extra:
//      
//      wdgTableVersion.addContextMenuEntryGthread(1, "cmpVersion", "create zip form this version", actionOutputZip);
//      wdgTableVersion.addContextMenuEntryGthread(2, "cmpVersion", "create zip from this version", actionOutputZip);
//      wdgTableVersion.addContextMenuEntryGthread(1, "cmpVersion", "cmp with this version", actionDiffVersion);
//      wdgTableVersion.addContextMenuEntryGthread(2, "cmpVersion", "cmp with this version", actionDiffVersion);
//    }
//  };




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

  void initializeCmd ( GitGuiCmd.CmdArgs cmdArgs ) {
    this.gitCmd.setCharsetForOutput("UTF-8");  //git outputs in UTF-8
    if(cmdArgs.guiPaths.env !=null) {
      for(String env: cmdArgs.guiPaths.env) {
        int posEq = env.indexOf('=');
        String val = env.substring(posEq+1);
        if(env.charAt(posEq-1) =='+') {
          String name = env.substring(0, posEq-1);
          this.gitCmd.prefixEnvIgnoreCase(name, val);
          System.out.println("set " + name + "+=" + val);
        } else {
          String name = env.substring(0, posEq);
          this.gitCmd.setEnvIgnoreCase(name, val);
          System.out.println("set " + name + "=" + val);
        }
    } }
    try {
      this.gitCmd.outAllEnvironment(System.out);   
    } catch(IOException exc) { System.err.println(exc.getMessage()); }
  }


  public void doSomethinginMainthreadTillCloseWindow()
  { while(! this.window.isGraphicDisposed()){
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
   * @throws NoSuchFieldException 
   */
  public static void guiRepository(GitGuiPaths exepath, JZtxtcmdFilepath repoFile) throws NoSuchFieldException
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
    //String[] paths = new String[3];
    //String error = searchRepository(srcFile, paths, null, null, null, null);
    //if(error !=null) { System.err.println(error); }
    //else 
    {
      GitGui main = new GitGui(new File(repoFile.absfile().toString()), new GitGuiCmd.CmdArgs(exepath));
      main.startLog();
      //main.startLog(paths[0], paths[1], paths[2]);
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
      GitGui main = new GitGui(srcFile, new GitGuiCmd.CmdArgs());
      main.startLog();
    }
  }


  
  
  String[] prepArgs(String cmd) {
    String[] ret = CmdExecuter.splitArgs(exepath.gitsh_exe, 0, 1);
    ret[ret.length-1] = cmd;    //the whole git cmd or other cmd is one arg for the sh.exe invocation.
    return ret;
  }
  
  


  
  /**
   * @param sLocalFile "*" for all files, else "path/in/loacal/tree/file.ext"
   */
  void startLog() {
    //this.sLocalFile = sLocalFile;
    String sPathShow;
    //this.sLocalFile = sLocalFile;
    if(sLocalFile !=null) {
      sPathShow = sWorkingDir + " : " + sLocalFile + " @" + sGitDir;
      wdgBtnDiffCurrFile.setVisible(true);
      wdgBtnDiffCurrWork.setVisible(true);
    } else {
      sPathShow = sWorkingDir + " @" + sGitDir;
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
    
    //--follow does not break on rename.
    //--source writes HEAD (the trunk)
    //--name-status writes M filePath
    sGitCmd += " log --date=iso '--pretty=raw'";
    if(sLocalFile !=null && sLocalFile.length() >0 && !sLocalFile.equals("*")) {
      sGitCmd +=  " --follow --name-status -- '" + sLocalFile + "'";  //--name-status only if one file, elsewhere too much info
    }
    wdgCmd.setText(sGitCmd);
    String[] args = prepArgs(sGitCmd); //{exepath.gitsh_exe, "-x", "-c", sGitCmd};
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
                CharSequence commitTextline;
                if(gitOut.scanStart().scan("commit ").scanOk()) { ////
                  gitOut.seekBegin();
                  cont = false;
                  contCommits = true;
                  break;
                } else {
                  commitTextline = gitOut.getCurrentPart();
                  char cFirst = commitTextline.length() >2 ? commitTextline.charAt(0) : '?';
                  if("MRA".indexOf(cFirst) >=0) {
                    int posTab = StringFunctions.indexOf(commitTextline, '\t');
                    if(posTab >=1) {
                      entry.changeKind = commitTextline.subSequence(0, posTab).toString();
                      int posTab2 = StringFunctions.indexOf(commitTextline, '\t', posTab+1);
                      if(posTab2 < 0) { posTab2 = commitTextline.length(); }
                      entry.filePath = commitTextline.subSequence(posTab+1, posTab2).toString();
//                      if(posTab2 >= posTab) { 
//                        posTab = posTab2;   //on rename the new name is the correct, it is right.
//                      }
//                      entry.filePath = commitTextline.subSequence(posTab+1, commitTextline.length()).toString();
                    }  
                  }
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
          line.redraw();
        }
        entryLast = entry;
      } //
      else {
        contCommits = gitOut.nextlineMaxpart().found();
      }
    } while(contCommits);
    //wdgTableVersion.
    wdgTableVersion.redraw();
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
    GitGui.this.currentEntry = line.nd_data;
    GitGui.this.cmpEntry = cmpLine == null ? null : cmpLine.getUserData();
    String sGitCmd = "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    if(currentEntry ==null) {
      String[] args = prepArgs(sGitCmd + " status");
      gitCmd.addCmd(args, null, listOut, null, workingDir, exec_ShowStatus);
      wdgInfo.setText("(working area)");
      try{
        wdgInfo.append("\n");
      } catch(Exception exc){}
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
    String[] args = prepArgs(sGitCmd);
    //
    gitCmd.addCmd(args, null, listOut, null, workingDir, exec_fillFileTable4Revision);
    synchronized(cmdThread) { cmdThread.notify(); }
  }
  
  

  void restoreFile(String sFile) {
    String sGitCmd2 = "git '--git-dir=" + sGitDir + "' checkout " + cmpEntry.revisionHash + " -- " + this.sFileList; ///
    wdgCmd.setText(sGitCmd2);
    String[] args2 = prepArgs(sGitCmd2);
    gitCmd.addCmd(args2, null, listOut, null, workingDir, null);

    String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + cmpEntry.revisionHash + " -- " + sFile;
    String[] args = prepArgs(sGitCmd);
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


  
  
  /**
   * @param cmd if "git.. " or "+ ... " then call in sh.exe
   * @param showCmd The routine which evaluates the result.
   */
  void execCmd(String cmd, CmdExecuter.ExecuteAfterFinish showCmd) {
    File cmdDir = workingDir;
    int posDir = cmd.indexOf('>');
    if(posDir >0) {
      cmdDir = new File(cmd.substring(0, posDir));
      cmd = cmd.substring(posDir+1);
    }
    boolean bSh = false;
    if(cmd.startsWith("+")) {
      bSh = true;
      int nStart = StringFunctions.indexNoWhitespace(cmd, 1, -1);
      cmd = cmd.substring(nStart);
    }
    String[] args = cmd.split(" ");
    if(bSh || args[0].equals("git")) {
      String[] args2 = args;
      args = prepArgs(cmd);
    }
    gitOut.clear();
    gitCmd.addCmd(args, null, listOut, null, cmdDir, showCmd);
  }
  
  
  
  private String getSelectedInfo() {
    int pos = wdgInfo.getCursorPos();
    String text = wdgInfo.getText();
    int begin = pos; int end = pos;
    while(text.charAt(begin) >=' ') { begin -=1; } //search \t
    while(text.charAt(end) >=' ') { end +=1; } //search \r\n
    return text.substring(begin+1, end);
    
  }
  
  
  void addFileFromSelection() {
    String dst = getSelectedInfo();
    
    String sGitCmd = "!! " + "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    sGitCmd += " add " + dst;
    wdgCmd.setText(sGitCmd);
  
  }


  void moveFileListToSelection() {
    GralTable<String>.TableLineData line = wdgTableFiles.getCurrentLine();
    String sFile = line.getCellText(1);
    String dst = getSelectedInfo();
    if(dst.endsWith("/")) {
      dst += sFile;
    }
    try{
      String sPathDst = this.sGitDir + "/../" + dst;
      FileSystem.mkDirPath(sPathDst);  //if the dst dir does not exist, create it
      int posSep = sGitDir.lastIndexOf('/');
      assert(sGitDir.substring(posSep).equals("/.git"));
      String sGitCmd = "!! " + sGitDir.substring(0, posSep) + ">git mv ";
      sGitCmd += line.getCellText(2) + "/" + sFile + " " + dst;
      wdgCmd.setText(sGitCmd);
    } catch(Exception exc) {
      wdgCmd.setText("Exception: " + exc.getMessage());
    }
    
  }

  
  
  
  
  

  /**Gets the file from repository and writes to tmp directory, starts diff tool
   * @param sFile The local path of the file
   * @param currRev maybe null, then compare with working tree, elsewhere a selected revision
   * @param cmpRev the selected revision to compare.
   */
  void startDiffView(String sFile,  RevisionEntry currRev, RevisionEntry cmpRev) {
    
    String sFile1, sFile2;
    if(currRev == null) {
      sFile1 = sWorkingDir + "/" + sFile;
    } else  {  //sCurrFile is the file path inside the .git archive
      //maybe another filepath for older revisions for moved files.
      String sCurrFile = currRev.filePath !=null ? currRev.filePath : sFile;  //sFile if common revisions
      String dirTemp1 = this.exepath.dirTemp1.getAbsolutePath() + "/" + dateFmtyyMMdd.format(currRev.dateCommit);
      sFile1 = dirTemp1 + "/" + sCurrFile; //sFile;
      try{ FileSystem.mkDirPath(sFile1);} catch(IOException exc) { throw new RuntimeException(exc); }
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + currRev.revisionHash + " -- " + sCurrFile; //sFile;
      String[] args = prepArgs(sGitCmd);
      gitCmd.addCmd(args, null, listOut, null, new File(dirTemp1), exec_ShowStatus);  //checkout in the dirTemp1, it is the working dir.
    }
    if(cmpRev == null) {
      sFile2 = sFile1;
    }
    else { 
      String sCmpFile = cmpRev.filePath !=null ? cmpRev.filePath : sFile;  //sFile if common revisions, incorrect on renamed files
      String dirTemp2 = this.exepath.dirTemp2.getAbsolutePath() + "/" + dateFmtyyMMdd.format(cmpRev.dateCommit);
      sFile2 = dirTemp2 + "/" + sCmpFile; //sFile;
      try{ FileSystem.mkDirPath(sFile2);} catch(IOException exc) { throw new RuntimeException(exc); }
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + cmpRev.revisionHash + " -- " + sCmpFile;
      wdgCmd.setText(this.exepath.dirTemp2 + ">" + sGitCmd);
      String[] args = prepArgs(sGitCmd);
      gitCmd.addCmd(args, null, listOut, null, new File(dirTemp2), exec_ShowStatus);
    }
    sFile1 = sFile1.replace('/', '\\');
    sFile2 = sFile2.replace('/', '\\');
    String sCmdDiff = "cmd.exe /C start " + exepath.diff_exe + " " + sFile1 + " " + sFile2;
    String[] cmdDiffView = CmdExecuter.splitArgs(sCmdDiff); 
    gitCmd.addCmd(cmdDiffView, null, listOut, null, null, exec_ShowStatus);
    synchronized(cmdThread) { cmdThread.notify(); }
  }


  

  /**Gets the file from repository and writes to tmp directory, starts diff tool
   * @param sFile The local path of the file
   * @param currRev maybe null, then compare with working tree, elsewhere a selected revision
   * @param cmpRev the selected revision to compare.
   */
  void startBlame(String sFile,  RevisionEntry currRev, RevisionEntry cmpRev) {
    String sFile1 = this.exepath.dirTemp1.getAbsolutePath() + "/" + sFile;
    String sFile2 = this.exepath.dirTemp2.getAbsolutePath() + "/" + sFile;
    if(currRev == null) {
      sFile1 = sWorkingDir + "/" + sFile;
    } else  { 
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + currRev.revisionHash + " -- " + sFile;
      String[] args = prepArgs(sGitCmd);
      gitCmd.addCmd(args, null, listOut, null, this.exepath.dirTemp1, null);
    }
    if(cmpRev == null) {
      sFile2 = sFile1;
    }
    else { 
      String sGitCmd = "git '--git-dir=" + sGitDir + "' checkout " + cmpRev.revisionHash + " -- " + sFile;
      wdgCmd.setText(this.exepath.dirTemp2 + ">" + sGitCmd);
      String[] args = prepArgs(sGitCmd);
      gitCmd.addCmd(args, null, listOut, null, this.exepath.dirTemp2, null);
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
    wdgTableFiles.redraw();
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
        if(this.sLocalFile !=null && key.equals(this.sLocalFile)) {
          Debugutil.stop();
        }
        line = wdgTableFiles.addLine(key, col, sLine);  
        line.redraw();
      }
    } while(gitOut.nextlineMaxpart().found());
    if(this.sLocalFile !=null) {
      //line = wdgTableFiles.getLine(this.sLocalFile);
      //wdgTableFiles.setCurrentLine(this.sLocalFile);
    }
    if(this.currentEntry !=null && this.currentEntry.filePath !=null) {
      assert(this.sLocalFile !=null);
      wdgTableFiles.setCurrentLine(this.currentEntry.filePath);
    }
  }



  /**This thread executes the git commands concurrently to the GUI.
   * If the commands will be executed inside the GUI event operations,
   * it may need to much time. 
   * <br>
   * For the answer, usual the commands calls a {@link CmdExecuter.ExecuteAfterFinish}
   * operation, which changed the GUI. This execution is in this thread too, of course,
   * and inform the graphic as usual in the GRAL concept. 
   * 
   */
  Thread cmdThread = new Thread("gitGui-Cmd") {
    @Override public void run() {
      do {
        try {
          if(gitCmd.hasEntries()) {
            gitCmd.executeCmdQueue(true);
          }
          synchronized(this){ wait(1000); }
        } catch (Exception e) { 
          System.err.println(e.getMessage()+"\n"  );
        }
      } while (!bCmdThreadClose);
      System.err.println("close cmd thread\n");
    }
  };




}
