package org.vishia.gral.widget;

/**It is a basic interface for any widget of the implementing GUI
 * @author Hartmut Schorrig
 *
 */
public interface Widgetifc
{
  /**Returns the implementation class of the widget. */
  Object getWidget();
  
  /**Sets the focus to the widget.
   * TODO call GuiPanelMngSwt#setFocusOfTabSwt() for all widgets, see TabelSwt!
   * @return true if set
   */
  boolean setFocus();
}
