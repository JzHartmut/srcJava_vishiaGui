package org.vishia.gral.base;

import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

public abstract class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{
  protected GralTextBox(String name, char whatis, GralWidgetMng mng)
  { super(name, whatis, mng);
  }

}
