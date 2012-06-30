package org.vishia.gral.base;

import org.vishia.gral.ifc.GralRectangle;


public interface GralWidgetHelper {

  void setMng(GralWidgetMng mng);
  
  GralRectangle getAbsoluteBoundsOf(GralWidget widg);
    
  
  boolean showContextMenu(GralWidget widg);

}
