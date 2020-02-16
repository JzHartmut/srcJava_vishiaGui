package org.vishia.gitGui;

import java.io.File;
import java.text.ParseException;

import org.vishia.mainCmd.MainCmd;
//import org.vishia.mainCmd.MainCmd.Argument;

public class GitGuiCmd extends MainCmd {

  
  static class CmdArgs {
    
    
    final GitGuiPaths guiPaths;
    
    public String startFile = ".";
    
    public String graphicSize = "C";

    public CmdArgs(GitGuiPaths guiPaths) {
      this.guiPaths = guiPaths;
    }
    
    public CmdArgs() {
      this.guiPaths = new GitGuiPaths();
    }
    

  }
  
  
  
  @Override
  protected boolean checkArguments() {
    // TODO Auto-generated method stub
    return true;
  }

  
  final CmdArgs args;
  
  
  private GitGuiCmd(CmdArgs args, String[] cmdArgs){
    super();
    super.addArgument(this.argList1);      
    this.args = args;
  }
  
  
  static CmdArgs parseArgsGitGui(String[] sCmdArgs) {
    CmdArgs args = new CmdArgs();
    GitGuiCmd thiz = new GitGuiCmd(args, sCmdArgs);
    try {
      thiz.parseArguments(sCmdArgs);
    } catch (ParseException e) {
      System.err.println(e.getMessage());
      e.printStackTrace();
      args = null;
    }
    return args;
  }
  
  /**This array describes the commands with its help and stores the result to Args. More is not necessary. */
  MainCmd.Argument[] argList1 =
    { new Argument("-gitsh", ":C:/Program Files/git/sh.exe - Path to the sh.exe to execute git" , new MainCmd.SetArgument() 
        { @Override public boolean setArgument(String val) 
          { GitGuiCmd.this.args.guiPaths.gitsh_exe = val; return (new File(val)).exists(); } })
      , new Argument("-diff", ":C:/Programs/difftool/diff.exe - exe for diff tool"
          , new MainCmd.SetArgument() { @Override public boolean setArgument(String val) 
            { GitGuiCmd.this.args.guiPaths.diff_exe = val; return true; } })
      , new Argument("-swt", "C - use Swt with given graphic size"
          , new MainCmd.SetArgument() { @Override public boolean setArgument(String val) 
            { GitGuiCmd.this.args.graphicSize = val; return true; } })
      , new Argument("", " file for log"
        , new MainCmd.SetArgument() { @Override public boolean setArgument(String val) 
          { GitGuiCmd.this.args.startFile = val; return true; } })
    };

  
}
