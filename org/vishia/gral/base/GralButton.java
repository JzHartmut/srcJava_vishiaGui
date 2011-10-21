package org.vishia.gral.base;

import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.ifc.GralWidget;

public abstract class GralButton extends GralWidget
{
  //final GralWindowMng_ifc mainWindow;
  
  protected boolean pressed;
  protected boolean switchedOn;

  protected String sButtonText;
  
  //final GralGridMngBase mng;
  protected boolean isActivated;
  
  
  protected boolean shouldSwitched;
  
  //public GralButton(String sName, GralWindowMng_ifc mainWindow, GralGridMngBase mng)
  public GralButton(String sName, GralGridMngBase mng)
  {
    super(sName, 'B', mng);  //GralWidget
    //this.mng = mng;
    //this.mainWindow = mainWindow;
  }
  
  
  
}
