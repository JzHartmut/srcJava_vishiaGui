package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralWidget;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralTextField extends GralWidget implements GralTextField_ifc
{
  protected final GralWindowMng_ifc windowMng;
  
  public GralTextField(String name, char whatis, GralWidgetMng mng){
    super(name, whatis, mng);
    this.windowMng = mng.gralDevice;
  }
  
}
