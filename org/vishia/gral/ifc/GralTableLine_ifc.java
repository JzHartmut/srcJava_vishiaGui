package org.vishia.gral.ifc;

import org.vishia.util.SelectMask_ifc;



public interface GralTableLine_ifc extends GralWidget_ifc, SelectMask_ifc
{

  String getCellText(int column);
  
  String[] getCellTexts();
  
  String setCellText(String text, int column);
  
  void setUserData(Object data);
  
  Object getUserData();
  
}
