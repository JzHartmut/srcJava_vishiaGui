package org.vishia.gral.ifc;

import org.vishia.util.Removeable;


/**It is a basic interface for any widget of the implementing GUI
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc extends Removeable
{
  /**Returns the implementation class of the widget. */
  public abstract Object getWidgetImplementation();
  
  public String getName();
  
  /**Sets the focus to the widget.
   * TODO call GuiPanelMngSwt#setFocusOfTabSwt() for all widgets, see TabelSwt!
   * @return true if set
   */
  public abstract boolean setFocus();
  
  public abstract GralColor setBackgroundColor(GralColor color);
  
  public abstract GralColor setForegroundColor(GralColor color);
  
  //public abstract GralWidget getGralWidget();

  /**Have to call only in the graphical thread.
   * 
   */
  public void redraw();
  
  /**Delayed redraw, can be called in any thread.
   * @param delay in milliseconds.
   */
  public void redrawDelayed(int delay);
  
  void setBoundsPixel(int x, int y, int dx, int dy);
  
}
