package org.vishia.gral.ifc;

import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.util.Removeable;


/**It is a basic interface for any widget of the Graphic Adaption Layer (gral).
 * <br>
 * <b>Strategy of changing the graphical content of a widget</b>:
 * The SWT graphical implementation prohibits changing the graphical appearance, for example setText(newText),  
 * in another thread. Other graphical implementations are not threadsafe. Often a graphic application
 * runs in only one thread, so that isn't a problem. But applications with complex data gathering processes
 * needs to run in several threads. It may be fine to set the widgets in that threads immediately,
 * seen from the programmers perspective. For multithread usage see {@link org.vishia.gral.base.GralGraphicThread}.
 * <br><br>
 * The solution in gral is:
 * <ul>
 * <li>A {@link GralWidget} can be invoked with methods to set its content from any thread.
 * <li>If it is the graphic thread and the operation is not delayed, it is executed immediately.
 *   Therefore the {@link #getGthreadSetifc()} method from any widget implementation is called to get
 *   the set interface for the graphic thread, and the proper method is invoked. The implementation of the widget
 *   executes the correct steps.
 * <li>If a set method is invoked in any other thread, the request is queued internally. 
 *   The queue is residently in the {@link org.vishia.gral.base.GralWidgetMng}. 
 *   The queue is processed in the graphic thread. The commission
 *   to change the widget is stored in an instance of {@link org.vishia.gral.base.GralWidgetChangeRequ}
 *   with a cmd, an index, the value.
 * </ul>     
 *  
 * @author Hartmut Schorrig
 *
 */
public interface GralWidget_ifc extends Removeable
{
  
  /**Version, history and license.
   * <ul>
   * <li>2012-03-31 Hartmut new: {@link #isVisible()}
   * <li>2012-01-16 Hartmut new: Concept {@link #repaint()}, can be invoked in any thread. With delay possible.
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120116;

  
  /**Returns the implementation class of the widget. */
  public abstract Object getWidgetImplementation();
  
  GralWidgetGthreadSet_ifc getGthreadSetifc();
  
  public String getName();
  
  /**Sets the focus to the widget.
   * TODO call GuiPanelMngSwt#setFocusOfTabSwt() for all widgets, see TabelSwt!
   * @return true if set
   */
  public abstract boolean setFocus();
  
  
  /**Returns whether the widget is visible or not. This method can be invoked in any thread.
   * It is an estimation because the state of the widget may be changed in the last time or a window
   * can be covered by another one. The widget is not visible if it is a member of a card in a tabbed
   * panel and that tab is not the selected one.
   * @return true if the widget seams to be visible.
   */
  boolean isVisible();
  
  public abstract GralColor setBackgroundColor(GralColor color);
  
  public abstract GralColor setForegroundColor(GralColor color);
  
  
  /**Sets the html help url for this widget. That information will be used if the widget is focused
   * to control the help window output.
   * @param url String given url, maybe a local file too with #internalLabel.
   */
  void setHtmlHelp(String url);
  
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
