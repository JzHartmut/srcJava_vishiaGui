package org.vishia.gral.base;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWidget;

public abstract class GralButton extends GralWidget
{
  final GralWindowMng_ifc mainWindow;
  
  public GralButton(String sName, GralWindowMng_ifc mainWindow, GralGridMngBase mng)
  {
    super(sName, 'B', mng);
    this.mainWindow = mainWindow;
  }
  
}
