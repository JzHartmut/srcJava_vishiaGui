package org.vishia.guiInspc;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;

public class ButtonInspcCmd extends GralUserAction
{
  
  ButtonInspcCmd(String ident){ super(ident); }
  
  @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
  {
    return true;
  }
  
  
  static void registerUserAction() {
    GralMng.get().registerUserAction("ButtonInspcCmd", new ButtonInspcCmd("ButtonInspcCmd"));
  }
  
}
