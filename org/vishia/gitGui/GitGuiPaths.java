package org.vishia.gitGui;

import java.io.File;

/**This class contains the paths to some executable in the hard disk.
 * @author Hartmut Schorrig
 *
 */
public class GitGuiPaths {

  /**The git linux shell, should be written with / */
  public String gitsh_exe = "C:\\Programs\\git\\bin\\sh.exe";
  
  /**Difftool, should be written with \ because it is used as Windows cmd invocation. */
  public String diff_exe = "c:\\Programs\\WinMerge-2.12.4-exe\\WinMerge.exe";
  
  File dirTemp1 = new File("t:/git_tmp1");
  File dirTemp2 = new File("t:/git_tmp2");

}
