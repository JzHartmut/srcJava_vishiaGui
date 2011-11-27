package org.vishia.gral.base;

import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class represents a window of an application.
 * The {@link GralGridPos#pos} of the baseclass is the position of the window derived from any other 
 * Position.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralWindow extends GralPanelContent implements GralWindow_ifc
{

  /**Version and history:
   * <ul>
   * <li>2011-11-27 Hartmut new: {@link #addMenuItemGThread(String, String, GralUserAction)} copied
   *   from {@link org.vishia.gral.ifc.GralPrimaryWindow_ifc}. The capability to have a menu bar
   *   should be enabled for sub-windows too. To support regularity, the property bit {@link #windHasMenu}
   *   is created. The property whether a window has a menu bar or not should be given on creation already.
   * <li>2011-11-27 Hartmut new: {@link #windProps} now stored for all implementations here.
   *   {@link #visibleFirst}: Maybe a problem while creation a menu bar in SWT (?)  
   * <li>2011-11-18 Hartmut new: {@link #windExclusive} etc. as properties for creation a window.
   * <li>2011-11-12 Hartmut chg: Because the {@link GralPanelContent} inherits {@link GralWidget}
   *   and this class has a {@link GralWidget#pos}, the member 'posWindow' is removed.
   * <li>2011-10-31 Hartmut new: {@link #setResizeAction(GralUserAction)} and {@link #setMouseAction(GralUserAction)}
   *   for operations with the whole window.
   * <li>2011-10-30 Hartmut new: {@link #resizeAction}
   * <li>2011-09-23 Hartmut new: member GralGridPos: Position of the window. The position is referred 
   *   to any Panel in another Window. It can be tuned and it is used if the Window is set visible. 
   * <li>2011-09-18 Hartmut creation: Now a PrimaryWindow and a SubWindow are defined.
   * </ul>
   * 
   */
  public static final int version = 0x20111127;
  
  /**Property defines that the window is opened exclusive for the application. 
   * It means that the primary window is not accessible if this window is opened.
   * It is 'application modal'. 
   */
  public static final int windExclusive = 1<<16;
  
  /**Property defines that the window is opened concurrently together with other windows 
   * of the application, especially concurrently to the primary window. This property should be set
   * if the {@link #windExclusive} property is not set.
   * It is 'application non-modal'. 
   */
  public static final int windConcurrently = 1<<30;
  
  public static final int windOnTop =  1 << 14;
  
  public static final int windHasMenu =  1 << 31;
  
  /**Or of some wind... constants.
   */
  final int windProps;  
  
  /**See {@link GralWindow_ifc#setResizeAction(GralUserAction)}. */
  protected GralUserAction resizeAction;
  
  /**See {@link GralWindow_ifc#setMouseAction(GralUserAction)}. */
  protected GralUserAction mouseAction;
  
  protected boolean visibleFirst;
  
  public GralWindow(String nameWindow, int windProps, GralWidgetMng mng, Object panelComposite)
  {
    super( nameWindow, mng, panelComposite);
    this.windProps = windProps;
  }
  
  /**Sets an action which is invoked if the whole window is resized by user handling on the window borders.
   * @param action The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   *   without parameter.
   */
  public abstract void setResizeAction(GralUserAction action);
  
  /**Sets an action which is invoked if any mouse button is pressed in the windows area on the screen.
   * @param action The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   *   with parameter key: The mouse key. params[0]: Instance of {@link GralRectangle} with mouse coordinates.
   */
  abstract public void setMouseAction(GralUserAction action);
  
  /**Adds any menu item
   * @param name name of the menu, it is used as widget name.
   * @param sMenuPath Menu position. Use slash as separator, use & for hot key.
   *   For example "&edit/&search/co&ntinue" creates a menu 'edit' or uses the existing one in the top level (menu bar),
   *   then creates the search menu item as pull down in menu bar, and then 'continue' with 'n' as hot key as sub-menu. 
   *   It is stored in {@link GralWidget#sDataPath}  
   * @param action called on menu activation.
   */
  abstract public void addMenuItemGThread(String nameWidg, String sMenuPath, GralUserAction action);
  
  

  
}
