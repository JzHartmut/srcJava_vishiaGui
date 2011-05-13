package org.vishia.mainGui;

//import org.eclipse.swt.widgets.Shell;


/**This interface supports the usage of a window which is divide into 9 areas. 
 * The 2 horizontal and 2 vertical separation lines may able to move in the implementation class.
 * In that kind the areas are adjustable in size. Some of the 9 areas in a rectangle can be combined
 * to build a greater area. In that kind a proper user-adequate dividing of the window is possible.
 * In opposite to some other graphic systems the user have fix positions for some input or output areas,
 * which can only adjusted in size. This may be an advantage in opposite to a free changeable 
 * window layout.
 * <br><br>
 * The 9 areas:
 * <pre>
 * +---------+-------------------+--------------+
 * |         |                   |              |   
 * +---------+-------------------+--------------+   
 * |         |                   |              |   
 * |         |                   |              |   
 * +---------+-------------------+--------------+   
 * |         |                   |              |   
 * +---------+-------------------+--------------+  
 * </pre>
 * Example for combined areas:
 * <pre>
 * +---------+-------------------+--------------+
 * | select  |                                  |   
 * +---------+     edit area                    |   
 * |         |                                  |   
 * | broswing|                                  |   
 * +         +-------------------+--------------+   
 * |         |    output area    | status       |   
 * +---------+-------------------+--------------+  
 * </pre>
 * Usual an application needs only a few but stable areas. 
 * @author Hartmut Schorrig
 *
 */
/**
 * @author hartmut
 *
 */
public interface GuiMainAreaifc
{
  /**Version history:
   * <ul>
   * <li>2011-05-03 Hartmut created: The idea is a common GUI interface independing of Swing or SWT graphics.
   *     The classes {@link org.vishia.mainGuiSwt.MainCmdSwt} and {@link MainCmdSwing} exists.
   *     The interface should combine the usage of both.
   *     Second approach is the documentation of the concept of the 9 areas in a non-implementing class.
   * <li>All other changes from 2010 and in the past
   * </ul>
   * 
   */
  final static int version = 0x20110502;

  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GuiDispatchCallbackWorker listener);
  
  
  /**Returns the Frame class of the underlying graphic.
   * SWT: Shell, swing: TODO
   * @return
   */
  Object getitsGraphicFrame();
  
  

}
