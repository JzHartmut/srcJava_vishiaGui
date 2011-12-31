package org.vishia.gral.swt;

import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_setifc;

/**This interface specifies the instance to have any set interfaces 
 * which's methods should be invoked only in the graphic thread.
 * 
 * @author Hartmut Schorrig
 *
 */
public interface SwtSetValue_ifc
{
  GralWindow_setifc getSwtWindow_ifc();
  
  GralWidget_ifc getSwtWidget_ifc();
}
