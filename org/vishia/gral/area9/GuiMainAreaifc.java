package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.ifc.GuiDispatchCallbackWorker;
import org.vishia.gral.ifc.GuiWindow_ifc;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.widget.Widgetifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainGuiSwt.MainCmdSwt;



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
 * @author Hartmut Schorrig
 *
 */
public interface GuiMainAreaifc extends GuiWindow_ifc
{
  /**Version history:
   * <ul>
   * <li>2011-08-06 Hartmut chg: This interface is based on MainCmd_ifc. It is a non-side-effect change
   *   because the implementation classes {@link MainCmdSwt} and maybe the adequate Swing class base on {@link MainCmd}.
   *   Using this change only this interface is necessary for applications to use {@link MainCmdSwt} or adequate Swing 
   * <li>2011-05-03 Hartmut created: The idea is a common GUI interface independent of Swing or SWT graphics.
   *     The classes {@link org.vishia.mainGuiSwt.MainCmdSwt} and {@link MainCmdSwing} exists.
   *     The interface should combine the usage of both.
   *     Second approach is the documentation of the concept of the 9 areas in a non-implementing class.
   * <li>All other changes from 2010 and in the past
   * </ul>
   * 
   */
  final static int version = 0x20110806;

  
  /**Sets the output window to a defined area. .
   * Adds the edit-menu too. 
   * @param area Two letter combination A1..C3 for horizontal and vertical area. 
   *        A..C is left to right, 1..3 is top to bottom.
   *        The first combination is the top left area for output, 
   *        the second combination is the bottom right area. 
   *        The output area can use more as one basic areas.
   *        For example "A3C3" means that the output area uses the full bottom part of window.
   *        For example "B2C3" means an area consist of 4 basic areas right bottom.  
   */
  void setOutputArea(String area);

  /**Sets the divisions of the frame. The frame is divide into 9 parts,
   * where two horizontal and two vertical lines built them:
   * <pre>
   * +=======+===============+===========+
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +-------+---------------+-----------+ 
   * |       |               |           | 
   * |       |               |           | 
   * +=======+===============+===========+
   * </pre>
   * 
   * @param x1p percent from left for first vertical divide line.
   * @param x2p percent from left for second vertical divide line.
   * @param y1p percent from left for first horizontal divide line.
   * @param y2p percent from left for first horizontal divide line.
   */
  void setFrameAreaBorders(int x1p, int x2p, int y1p, int y2p);
  
  
  /**Sets a Component into a defined area. See {@link #setFrameAreaBorders(int, int, int, int)}.
   * It should be called only in the GUI-Thread.
   * @param xArea 1 to 3 for left, middle, right
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   * @param component The component.
   * @throws IndexOutOfBoundsException if the arguments are false or the area is occupied already.
   */
  void addFrameArea(int xArea, int yArea, int dxArea, int dyArea, Widgetifc component)
  throws IndexOutOfBoundsException;
  
  
}