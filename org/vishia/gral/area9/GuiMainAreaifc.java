package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.gui.GuiDispatchCallbackWorker;
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
public interface GuiMainAreaifc
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

  
  /**Sets the title and the pixel size for the whole window. */
  void setTitleAndSize(String sTitle, int left, int top, int xSize, int ySize);
  
  
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

  /**Starts the execution of the graphic initializing and handling in a own thread.
   * The following sets should be called already:
   * <pre>
   * setTitleAndSize("Title bar text", 800, 600);  //This instruction should be written first to output syntax errors.
     setStandardMenus(new File("."));
     setOutputArea("A3C3");        //whole area from mid to bottom
     setFrameAreaBorders(20, 80, 75, 80);
     </pre>
   * They sets only some values, no initializing of the graphic is executed till now.
   * <br><br>
   * The graphic thread inside this class {@link #guiThread} initializes the graphic.
   * All other GUI-actions should be done only in the graphic-thread. 
   * The method {@link #addDispatchListener(Runnable)} is to be used therefore.
   * <br><br>
   * The method to use an own thread for the GUI is not prescribed. The initialization
   * and all actions can be done in the users thread too. But then, the user thread
   * have to be called {@link #dispatch()} to dispatch the graphic events. It is busy with it than.   
   * @return true if started
   */
  boolean startGraphicThread();

  
  /**Returns true if the graphical application runs. 
   * @return
   */
  boolean isRunning();
  
  void exit();
  
  /**Activates the menu bar with the standard entries, especially the file open entry.
   * Note: This method should only be called in the same thread where the graphic runs.
   * 
   * @param openStandardDirectory If given, then this is the default directory for file open.
   * @param actionFile Action on file menu entries.
   */
  void setStandardMenusGThread(File openStandardDirectory, UserActionGui actionFile);
  
  
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
  
  
  /**Adds a listener, which will be called in the dispatch loop.
   * @param listener
   */
  void addDispatchListener(GuiDispatchCallbackWorker listener);
  
  
  /**Removes a listener, which was called in the dispatch loop.
   * @param listener
   */
  public void removeDispatchListener(GuiDispatchCallbackWorker listener);
  
  
  MainCmd_ifc getMainCmd();
  
  
  /**Returns the Frame class of the underlying graphic.
   * SWT: Shell, swing: TODO
   * @return
   */
  Object getitsGraphicFrame();
  
  

}
