package org.vishia.gral.ifc;



public interface GralTextBox_ifc extends GralTextField_ifc, Appendable //WidgetGui_ifc
{

  void append(String text);
  
  int getNrofLines();
  
  /**Sets the view to the trail of the text.
   * 
   */
  void viewTrail();
  
}
