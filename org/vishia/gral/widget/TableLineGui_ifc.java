package org.vishia.gral.widget;

import org.vishia.gral.ifc.Widgetifc;


public interface TableLineGui_ifc extends Widgetifc
{

  String getCellText(int column);
  
  String setCellText(String text, int column);
  
  void setUserData(Object data);
  
  Object getUserData();
  
}
