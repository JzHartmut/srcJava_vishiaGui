package org.vishia.gitGui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.vishia.cmd.CmdExecuter;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.jgit.JgitFactory;
import org.vishia.util.Debugutil;
import org.vishia.util.JgitFactory_ifc;
import org.vishia.util.Jgit_ifc;
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



  GralWindow window = new GralWindow("0+50, 0+80", "GitGui", "Git vishia", GralWindow.windResizeable);

  GralTable<RevisionEntry> wdgTableVersion = new GralTable<>("2..-20,2..-20", "git-versions", new int[] {10, 0, -10});
  
  GralTextBox wdgInfo = new GralTextBox("info");


  CmdExecuter cmd = new CmdExecuter();


  StringPartAppend out = new StringPartAppend();


  Jgit_ifc jgit;
  
  JgitFactory_ifc jgitFactory = new JgitFactory();


  SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");


  public static void main(String[] args){
    GitGui main = new GitGui();
    String sTypeOfImplementation = "SWT";  //default
    if(args.length >=1){
      sTypeOfImplementation = args[0];
    }
    main.window.create(sTypeOfImplementation, 'B', null);
    main.revisionsCommit("D:/GitArchive/D/vishia/srcJava_vishiaBase");
    main.doSomethinginMainthreadTillClosePrimaryWindow();
  }
  

  public GitGui() {
    Map<String, String> env = cmd.environment();
    env.put("HOMEPATH", "\\vishia\\HOME");
    env.put("HOMEDRIVE", "D:");
    String sPath = env.get("PATH");
    sPath = "D:\\Programs\\Gitcmd\\bin;" + sPath;
    env.put("PATH", sPath);
  }


  public void doSomethinginMainthreadTillClosePrimaryWindow()
  { int ix = 0;
    while(GralMng.get().gralDevice.isRunning()){
      try{ Thread.sleep(1000);} 
      catch (InterruptedException e) { }
    }
    
  }


  /**Don't use it. org.eclipse.jgit seems to be only a wrapper around the original gitcmd.exe. Use the original git.exe instead.  
   * @param sGitDir
   */
  public void revisionsCommitJ(String sGitDir) {
    jgit = jgitFactory.getRepository(sGitDir, sGitDir + "/.gitx");
    String status = jgit.status();
    Debugutil.stop();
  }




  public void revisionsCommit(String sGitDir) {
    cmd.setCurrentDir(new File(sGitDir));
    out.buffer().setLength(0);
    out.assign(out.buffer());   //to reset positions to the changed out.buffer()
    //String[] args ={"D:\\Programs\\Gitcmd\\bin\\sh.exe", "-x", "-c", "git log --date=iso \'--pretty=format:Commit::%H\t%h\t%an\t%ad\n%s::::::%f\' org/vishia/util/CalculatorExpr.java"};
    String[] args ={"D:\\Programs\\Gitcmd\\bin\\sh.exe", "-x", "-c", "git log --date=iso \'--pretty=raw' org/vishia/util/CalculatorExpr.java"};
    int error = cmd.execute(args, null,  out, null);
    out.firstlineMaxpart();
    String lineTexts[] = new String[3];
    boolean cont = true;
    do {
      if(out.scanStart().scan("commit ").scanOk()) {
        //out.seek(8);
        cont = false;  //set to true on the next "commit " line.
        String hash = out.getCurrentPart().toString();
        RevisionEntry entry = new RevisionEntry(hash);
        try {
          if(out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("tree ").scanOk()) {
              entry.treeHash = out.getCurrentPart().toString();
            } else throw new IllegalArgumentException("log output structure faulty");
          }
          if(out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("parent ").scanOk()) {
              entry.parentHash = out.getCurrentPart().toString();
            } else throw new IllegalArgumentException("log output structure faulty");
          }
          if(out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("author ").scanOk()) {
              out.lento('<');
              entry.author = out.getCurrentPart().toString();
              out.fromEnd().seekPos(-16).lento(' ');
              if(out.scan().scanInteger().scanOk()) { 
                entry.dateAuthor = new Date(1000*out.scan().getLastScannedIntegerNumber());
              }
            } else throw new IllegalArgumentException("log output structure faulty");
          }
          if(out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("committer ").scanOk()) {
              out.lento('<');
              entry.committer = out.getCurrentPart().toString();
              out.fromEnd().seekPos(-16).lento(' ');
              if(out.scan().scanInteger().scanOk()) { 
                entry.dateCommit = new Date(1000*out.scan().getLastScannedIntegerNumber());
              }
            } else throw new IllegalArgumentException("log output structure faulty");
          }
          while(out.nextlineMaxpart().found()) {
            if(out.scanStart().scan("commit ").scanOk()) {
              out.seekBegin();
              cont = true;
              break;
            } else {
              CharSequence commitTextline = out.getCurrentPart();
              if(entry.commitTitle == null && commitTextline.length() >6){
                entry.commitTitle = commitTextline.toString();  //first line with at least 6 character: Use as title.
              }
              entry.commitText.append(commitTextline);
            }
          }
        }catch(Exception exc) {
          
        }
        
        lineTexts[0] = dateFormat.format(entry.dateAuthor);
        lineTexts[1] = entry.commitTitle;
        lineTexts[2] = entry.author;
        GralTableLine_ifc<RevisionEntry> line = wdgTableVersion.addLine(hash, lineTexts, entry);  
        line.repaint();
      }
      else {
        cont = out.nextlineMaxpart().found();
      }
    } while(cont);
    //wdgTableVersion.
    wdgTableVersion.repaint();
    Debugutil.stop();
  }



  class RevisionEntry
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


}
