package org.vishia.gral.ifc;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public interface GralTextField_ifc extends GralWidget_ifc
{
  /**Set the textual content of the widget. This method is able to call in any thread. 
   * The text may be stored in a queue and applied to the widget in the graphical thread.
   * @param text The content
   */
  void setText(String text);
  
  String getText();
  
}
