package org.vishia.gral.ifc;


/**It is a basic interface for any widget of the implementing GUI
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc
{
  /**Returns the implementation class of the widget. */
  Object getWidgetImplementation();
  
  /**Sets the focus to the widget.
   * TODO call GuiPanelMngSwt#setFocusOfTabSwt() for all widgets, see TabelSwt!
   * @return true if set
   */
  boolean setFocus();
  
  GralColor setBackgroundColor(GralColor color);
  
  GralColor setForegroundColor(GralColor color);
  
  

}
