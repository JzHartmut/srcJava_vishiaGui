package org.vishia.gral.swt;

import org.vishia.gral.base.GralWindow_setifc;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This interface specifies the instance to have any set interfaces 
 * which's methods should be invoked only in the graphic thread.
 * 
 * @author Hartmut Schorrig
 *
 */
public interface SwtSetValue_ifc
{
  GralWindow_setifc getSwtWindow_ifc();
  
}
