package org.vishia.gral.base;

import org.vishia.gral.ifc.GralTextBox_ifc;

public abstract class GralTextBox extends GralTextField implements Appendable, GralTextBox_ifc
{
  public GralTextBox(String name, char whatis)
  { super(name, whatis);
  }

}
