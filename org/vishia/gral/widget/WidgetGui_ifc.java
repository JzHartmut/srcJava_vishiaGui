package org.vishia.gral.widget;

import org.vishia.gral.ifc.GralColor;


/**This interface should support changing of widget of the underlying GUI.
 * It can be implemented by GUI-special classes, especially for Lines of tables or nodes of a tree.
 * @author Hartmut Schorrig
 *
 */
public interface WidgetGui_ifc extends Widgetifc
{

  GralColor setBackgroundColor(GralColor color);
  
  GralColor setForegroundColor(GralColor color);
  
  String getText();
  
  String setText(String text);
  
}
