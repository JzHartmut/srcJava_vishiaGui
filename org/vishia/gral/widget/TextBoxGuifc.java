package org.vishia.gral.widget;

import org.vishia.gral.ifc.Widgetifc;


public interface TextBoxGuifc extends Widgetifc, Appendable //WidgetGui_ifc
{

  String setText(String text);
  
  void append(String text);
  
  int getNrofLines();
  
  /**Sets the view to the trail of the text.
   * 
   */
  void viewTrail();
  
}
