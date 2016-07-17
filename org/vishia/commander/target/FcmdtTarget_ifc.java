package org.vishia.commander.target;

import java.io.Closeable;

/**This is the interface from the GUI to a target.
 * @author Hartmut Schorrig
 *
 */
public interface FcmdtTarget_ifc extends Closeable
{
  public final static int copyFile = 0x1;
  
  void cmdTarget(int cmd, String sParam);
}
