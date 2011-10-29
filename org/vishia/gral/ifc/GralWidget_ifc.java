package org.vishia.gral.ifc;


/**It is a basic interface for any widget of the implementing GUI
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc
{
  /**Returns the implementation class of the widget. */
  public abstract Object getWidgetImplementation();
  
  /**Sets the focus to the widget.
   * TODO call GuiPanelMngSwt#setFocusOfTabSwt() for all widgets, see TabelSwt!
   * @return true if set
   */
  public abstract boolean setFocus();
  
  public abstract GralColor setBackgroundColor(GralColor color);
  
  public abstract GralColor setForegroundColor(GralColor color);
  
  //public abstract GralWidget getGralWidget();

  public void redraw();
  
  void setBoundsPixel(int x, int y, int dx, int dy);
  
}
