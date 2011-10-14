package org.vishia.gral.ifc;



public interface GralTableLine_ifc extends GralWidget_ifc
{

  String getCellText(int column);
  
  String setCellText(String text, int column);
  
  void setUserData(Object data);
  
  Object getUserData();
  
}
