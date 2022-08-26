package org.vishia.gitGui;

import java.io.File;
import java.util.List;

/**This class contains the paths to some executable in the hard disk.
 * @author Hartmut Schorrig
 *
 */
public class GitGuiPaths {

  /**The git linux shell, should be written with / */
  public String gitsh_exe = "'C:\\Program Files\\git\\bin\\sh.exe' -x -c";
  
  /**Difftool, should be written with \ because it is used as Windows cmd invocation. */
  public String diff_exe = "c:\\Programs\\WinMerge-2.12.4-exe\\WinMerge.exe";
  
  File dirTemp1 = new File("t:/git_tmp1");
  File dirTemp2 = new File("t:/git_tmp2");
  
  /**Can contain "name=value" or "name=value+" for additional environments.
   * 
   */
  public List<String> env;

}
