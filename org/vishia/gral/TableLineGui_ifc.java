package org.vishia.gral;

public interface TableLineGui_ifc extends WidgetGui_ifc
{

  String getCellText(int column);
  
  String setCellText(String text, int column);
  
}
