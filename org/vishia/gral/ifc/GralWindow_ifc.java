package org.vishia.gral.ifc;

import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow_setifc;


/**Interface to a Gral Window. In SWT ist is named as 'shell'. An application can have more as one window.
 * The base classes {@link org.vishia.gral.base.GralWindow} is the base of implementation.
 * This interface contains the get and set access-interfaces (the base ones)
 * and contains the build-method-definition for windows.
 */
public interface GralWindow_ifc extends GralWindow_getifc, GralWindow_setifc, GralWidget_ifc 
{
  /**Version, history and license.
   * <ul>
   * <li>2012-03-16 Hartmut new: {@value #windResizeable} defined and supported in SWT-implementation.
   * <li>2012-03-13 Hartmut chg: This interface should be used for building the window. Therefore
   *   all building methods should be contained here. Gotten from implementors. 
   * <li>2011-06-00 Hartmut created
   * </ul>
   * <br><br>
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
  public static final int version = 20120317;

  
  
  
  /**Property defines that the window is able to resize
   */
  public static final int windResizeable = 1<<4;
  
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
  
  /**Sets an action which is invoked if the window is set invisible.
   * @param action The {@link GralUserAction#userActionGui(int, GralWidget, Object...)} will be called
   *   without parameter.
   */
  public abstract void setActionOnSettingInvisible(GralUserAction action);

  
  
  public abstract void setTitle(String sTitle);


}
