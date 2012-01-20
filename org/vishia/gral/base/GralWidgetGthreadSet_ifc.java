package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;

public interface GralWidgetGthreadSet_ifc {
  
  void clearGthread();
  
  void redrawGthread();
  
  void setTextGthread(String text, Object data);
  
  void insertGthread(int pos, Object visibleInfo, Object data);
  
  void setBackGroundColorGthread(GralColor color);

  void setForeGroundColorGthread(GralColor color);
}
