package org.vishia.gral.base;

import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralWidget;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTextField extends GralWidget implements GralTextField_ifc
{
  public GralTextField(String name, char whatis){
    super(name, whatis);
  }
  
}
