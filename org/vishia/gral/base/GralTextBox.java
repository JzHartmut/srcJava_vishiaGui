package org.vishia.gral.base;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;

public abstract class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{
  public GralTextBox(String name, char whatis, GralGridMngBase mng)
  { super(name, whatis, mng);
  }

}
