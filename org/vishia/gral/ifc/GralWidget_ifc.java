package org.vishia.gral.ifc;

import org.vishia.util.Removeable;


/**It is a basic interface for any widget of the implementing GUI
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc extends Removeable
{
  
  /**Version and history:
   * <ul>
   * <li>2012-01-16 Hartmut new: Concept {@link #repaint()}, can be invoked in any thread. With delay possible.
   * </ul> 
   */
  public static final int version = 0x20120116;
  
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

  /**Immediately repaint. It can be called in any thread. If it is called in the graphic thread,
   * the repaint action is executed immediately in the thread. Elsewhere the graphic thread will be woken up.
   */
  public void repaint();
  
  /**Possible delayed repaint, can be called in any thread.
   * If this method is re-called in the time where the delay is not elapsed
   * then the delay will be set newly, the timer is winding up again. 
   * But the latest time on first call will be used.
   * @param delay in milliseconds. If 0 or less 0, then it is executed immediately if the calling
   *   thread is the graphic thread.
   * @param latest The latest draw time in milliseconds. If it is less 0, then it is unused.
   *   If it is 0 or less delay if a delay is given, then the delay isn't wound up on re-call.
   *   
   */
  public void repaint(int delay, int latest);
  
  void setBoundsPixel(int x, int y, int dx, int dy);
  
}
