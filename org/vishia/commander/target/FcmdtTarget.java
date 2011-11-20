package org.vishia.commander.target;

import java.io.Closeable;

/**This part of the file commander may run on any target system which hasn't a display.
 * For the standard implementation for PC-Usage it is compiled in the same application like the GUI.
 * This files may be translated to C, it can be run in a embedded environment.
 * @author Hartmut Schorrig
 *
 */
public class FcmdtTarget implements FcmdtTarget_ifc, Closeable
{
  /**The module to copy files.
   * 
   */
  FcmdtCopyCmd copyCmd = new FcmdtCopyCmd();
  
  
  public FcmdtTarget(){
    
  }
  
  
  @Override public void cmdTarget(int cmd, String sParam){
    switch(cmd){
      case FcmdtTarget_ifc.copyFile:  copyCmd.startCopy(cmd, sParam); break;
    }
  }
  
  @Override public void close(){
    copyCmd.close();
  }

  
}
