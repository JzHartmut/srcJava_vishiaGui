package org.vishia.mainGui;

public interface TextBoxGuifc extends Widgetifc //WidgetGui_ifc
{

  void setText(String text);
  
  void append(String text);
  
  int getNrofLines();
  
  /**Sets the view to the trail of the text.
   * 
   */
  void viewTrail();
  
}
