package org.vishia.gitGui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.vishia.cmd.CmdExecuter;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.FileSystem;
import org.vishia.util.StringPartAppend;

/**This class contains some gui capabilities which works in a vishia-Gral graphic environment. 
 * Especially it was written for the file commander, but it works offline too.
 * @author Hartmut Schorrig
 *
 */
public class GitGui
{



  /**Version, history and license
   * <ul>
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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

  String sTypeOfImplementation = "SWT";  //default
  

  GralWindow window = new GralWindow("0+50, 0+80", "GitGui", "Git vishia", GralWindow_ifc.windResizeable | GralWindow_ifc.windRemoveOnClose);

  GralTextField wdgPath = new GralTextField("@2-2,0..0=path");
  GralTable<RevisionEntry> wdgTableVersion = new GralTable<>("@3..-20,0..-20=git-versions", new int[] {10, 0, -10});
  
  GralTextBox wdgInfo = new GralTextBox("@-20..0, 0..-20=info");


  CmdExecuter cmd = new CmdExecuter();


  StringPartAppend out = new StringPartAppend();



  SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");


  public static void main(String[] args){
    GitGui main = new GitGui(args);
    main.showLog("D:/GitArchive/D/vishia/srcJava_vishiaBase/.git", "D:/GitArchive/D/vishia/srcJava_vishiaBase", "org/vishia/util/CalculatorExpr.java");
    main.doSomethinginMainthreadTillCloseWindow();
    main.cmd.close();
  }
  

  public GitGui(String[] args) {
    if(args !=null && args.length >=1){
      sTypeOfImplementation = args[0];
    }
    initializeCmd();
    wdgTableVersion.specifyActionOnLineSelected(actionTableLineVersion);
    window.create(sTypeOfImplementation, 'B', null);
  }



  @Override public void finalize()
  {
  }

  void initializeCmd() {
    Map<String, String> env = cmd.environment();
    env.put("HOMEPATH", "\\vishia\\HOME");
    env.put("HOMEDRIVE", "D:");
    String sPath = env.get("PATH");
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
   * @param startFile Any file path. Especially a ".git" directory or ".gitRepository" file. In that  case the local file path will be set to null,
   *   because the whole repository is given.
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
  { File fRepo;
    File currDir = startFile.getParentFile();
    String ret = null;
    //search whether a .bzr or .bzr.bat exists and change to parent dir till it is found.
    do{
      fRepo = new File(currDir, ".git");
      if(!fRepo.exists()){
        fRepo = new File(currDir, ".gitRepository");
        if(!fRepo.exists()){
          try{
            currDir = FileSystem.getDirectory(currDir);  //NOTE: currDir.getParent() is not successfully on relative dir "."
          } catch(FileNotFoundException exc){ currDir = null;}
        }
      }
    } while(!fRepo.exists() && currDir !=null);
    if(currDir ==null){
      ret = "searchRepository - .git or .gitRepository not found ;" + startFile.getAbsolutePath();
    } else {
      String sBaseDir = FileSystem.normalizePath(currDir).toString();
      //dst.put(bzrdir, sBzrDir);
      String sRepository;
      if(fRepo.getName().equals(".gitRepository")){   //File with link to repository
        sRepository = FileSystem.readFile(fRepo).trim();
      } else {
        sRepository = sBaseDir;  
      }
      CharSequence sFilePath = FileSystem.normalizePath(startFile);
      String sLocalFilePath = sFilePath.subSequence(sBaseDir.length()+1, sFilePath.length()).toString();
      if(sLocalFilePath.equals(".git") || sLocalFilePath.equals(".gitRepository")){
        sLocalFilePath = null;
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





  public static void showLog(File srcFile)
  {
    String[] paths = new String[3];
    String error = searchRepository(srcFile, paths, null, null, null, null);
    if(error !=null) { System.err.println(error); }
    else {
      GitGui main = new GitGui(null);
      main.showLog(paths[0], paths[1], paths[2]);
      main.doSomethinginMainthreadTillCloseWindow();
      main.cmd.close();
    }
  }




  public void showLog(String sGitDir, String sWorkingDir, String sLocalFile) {
    String sPathShow;
    if(sLocalFile !=null) {
      sPathShow = sGitDir + " : " + sLocalFile;
    } else {
      sPathShow = sGitDir;
    }
    wdgPath.setText(sPathShow);
    
    cmd.setCurrentDir(new File(sWorkingDir));
    out.buffer().setLength(0);
    out.assign(out.buffer());   //to reset positions to the changed out.buffer()
    String sGitCmd = "git";
    if(! sGitDir.startsWith(sWorkingDir)) {
      sGitCmd += " '--git-dir=" + sGitDir + "'";
    }
    sGitCmd += " log --date=iso '--pretty=raw'";
    if(sLocalFile !=null && sLocalFile.length() >0) {
      sGitCmd +=  " '" + sLocalFile + "'";
    }
    String[] args ={"D:/Programs/Gitcmd/bin/sh.exe", "-x", "-c", sGitCmd};
    int error = cmd.execute(args, null,  out, null);
    out.firstlineMaxpart();
    String lineTexts[] = new String[3];
    boolean contCommits = true;
    do {
      if(out.scanStart().scan("commit ").scanOk()) {
        contCommits = false;  //set to true on the next "commit " line.
        String hash = out.getCurrentPart().toString();
        RevisionEntry entry = new RevisionEntry(hash);
        try {
          boolean cont = true;
          while (cont && out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("tree ").scanOk()) {
              entry.treeHash = out.getCurrentPart().toString();
            } 
            else if(out.scanStart().scan("parent ").scanOk()) {
              entry.parentHash = out.getCurrentPart().toString();
            }
            else if(out.scanStart().scan("author ").scanOk()) {
              out.lento('<');
              entry.author = out.getCurrentPart().toString();
              out.fromEnd().seekPosBack(16).lento(' ');
              if(out.scan().scanInteger().scanOk()) { 
                entry.dateAuthor = new Date(1000*out.scan().getLastScannedIntegerNumber());
              }
            } else if(out.scanStart().scan("committer ").scanOk()) {
              out.lento('<');
              entry.committer = out.getCurrentPart().toString();
              out.fromEnd().seekPosBack(16).lento(' ');
              if(out.scan().scanInteger().scanOk()) { 
                entry.dateCommit = new Date(1000*out.scan().getLastScannedIntegerNumber());
              }
            } else {
              do {
                if(out.scanStart().scan("commit ").scanOk()) {
                  out.seekBegin();
                  cont = false;
                  contCommits = true;
                  break;
                } else {
                  CharSequence commitTextline = out.getCurrentPart();
                  if(entry.commitTitle == null && commitTextline.length() >6){
                    entry.commitTitle = commitTextline.toString();  //first line with at least 6 character: Use as title.
                  }
                  entry.commitText.append(commitTextline).append('\n');
                }
              } while(cont && (out.nextlineMaxpart().found()));
            }
          }//while lines of one commit 
        }catch(Exception exc) {
          Debugutil.stop();
        }
        
        lineTexts[0] = dateFormat.format(entry.dateAuthor);
        lineTexts[1] = entry.commitTitle;
        lineTexts[2] = entry.author;
        GralTableLine_ifc<RevisionEntry> line = wdgTableVersion.addLine(hash, lineTexts, entry);  
        line.repaint();
      } //
      else {
        contCommits = out.nextlineMaxpart().found();
      }
    } while(contCommits);
    //wdgTableVersion.
    wdgTableVersion.repaint();
    Debugutil.stop();
  }



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



  GralUserAction actionTableLineVersion = new GralUserAction("actionTablelineVersion")
  { @Override public boolean exec(int actionCode, org.vishia.gral.ifc.GralWidget_ifc widgd, Object... params) {
      @SuppressWarnings("unchecked")
      GralTable<RevisionEntry>.TableLineData line = (GralTable<RevisionEntry>.TableLineData) params[0];
      RevisionEntry entry = line.data;
      wdgInfo.setText(entry.commitText);
      return true;
    }
  };



}
