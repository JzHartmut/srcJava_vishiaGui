package org.vishia.mainGui;

public interface TableLineGui_ifc extends WidgetGui_ifc
{

  String getCellText(int column);
  
  String setCellText(String text, int column);
  
}
