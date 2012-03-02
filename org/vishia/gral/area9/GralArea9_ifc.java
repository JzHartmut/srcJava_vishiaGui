package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.base.GralDispatchCallbackWorker;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.ifc.GralMngApplAdapter_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTextBox_ifc;
import org.vishia.mainCmd.MainCmd_ifc;



/**This interface supports the usage of a window which is divide into 9 areas. 
 * License see {@link #version}.
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
public interface GralArea9_ifc extends GralPrimaryWindow_ifc, GralMngApplAdapter_ifc
{
  /**Version history:
   * <ul>
   * <li>2011-12-26 Hartmut new extends {@link GralMngApplAdapter_ifc}
   * <li>2011-11-12 Hartmut chg: {@link #initGraphic(String)} instead initOutputArea().
   * <li>2011-11-12 Hartmut new: {@link #getActionAbout()} and {@link #getActionAbout()} to support menu setting by user
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
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   */
  final static int version = 0x20111227;

  
  /**Initializes the graphic of the Area9-Window. It sets the Output window to a defined area
   * and creates Windows for Help, About and InfoBoxes.
   * This method can be invoked after construction immediately in any thread. 
   * <br><br>
   * In the implementation class {@link org.vishia.gral.area9.GralArea9Window}
   * the working is done in the graphic thread. The caller thread waits for success in this method.
   * The following actions are done:
   * <ul>
   * <li>Creation of all sub windows for InfoBox, InfoLog, Help and About.
   * <li>Creation of the output Area.
   * <li>
   * <li>Adding the {@link org.vishia.gral.base.GralDispatchCallbackWorker}
   *   {@link org.vishia.gral.area9.GralArea9Window#writeOutputTextDirectly} which transfers
   *   text lines stored in {@link org.vishia.gral.area9.GralArea9Window#outputTexts} to the output area.
   * </ul>
   * @param outputArea Two letter combination A1..C3 for horizontal and vertical area. 
   *        A..C is left to right, 1..3 is top to bottom.
   *        The first combination is the top left area for output, 
   *        the second combination is the bottom right area. 
   *        The output area can use more as one basic areas.
   *        For example "A3C3" means that the output area uses the full bottom part of window.
   *        For example "B2C3" means an area consist of 4 basic areas right bottom.  
   */
  void initGraphic(String outputArea);

  
  /**Returns the outputArea, which was created by the graphic thread. 
   */
  GralPanelContent getOutputPanel();
  
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
  
  
  /**Sets a Panel into a defined area. See {@link #setFrameAreaBorders(int, int, int, int)}.
   * It should be called only in the GUI-Thread.
   * @param xArea 1 to 3 for left, middle, right
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   * @param component The component.
   * @throws IndexOutOfBoundsException if the arguments are false or the area is occupied already.
   */
  void addFrameArea(int xArea, int yArea, int dxArea, int dyArea, GralPanelContent component)
  throws IndexOutOfBoundsException;
  
  
  /**Returns the text box which is used as output box for common messages of the application.
   * @return null if no output window is given.
   */
  GralTextBox getOutputBox();
  
  
  GralWidgetMng getGralMng();
  
  /**Returns the prepared action help which opens the help window.
   * It should be used for the users call of {@link #addMenuItemGThread(String, String, GralUserAction) }
   * to install the help menu.  */
  GralUserAction getActionHelp();
  
  /**Returns the prepared action about which opens the help window.
   * It should be used for the users call of {@link #addMenuItemGThread(String, String, GralUserAction) }
   * to install the help menu.  */
  GralUserAction getActionAbout();
  
  
}
