package org.vishia.gral.base;

import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;

/**This class represents a window of an application.
 * The {@link GralPos#pos} of the baseclass is the position of the window derived from any other 
 * Position.
 * @author Hartmut Schorrig
 *
 */
public abstract class GralWindow extends GralPanelContent implements GralWindow_ifc
{

  /**Version, history and license.
   * <ul>
   * <li>2012-04-16 Hartmut new: {@link #actionResizeOnePanel}
   * <li>2012-03-13 Hartmut chg: Some abstract method declarations moved to its interface.
   * <li>2011-12-31 Hartmut chg: Implements the set-methods of {@link GralWindow_ifc} in form of calling
   *   {@link GralMng_ifc#setInfo(GralWidget, int, int, Object, Object)}. This methods
   *   can be called in any thread, it may be stored using 
   *   {@link GralGraphicThread#addRequ(org.vishia.gral.base.GralWidgetChangeRequ)}.
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
   * <b>Copyright/Copyleft</b>:
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
   * 
   * 
   */
  @SuppressWarnings("hiding")
  public static final int version = 20120416;
  
  /**Or of some wind... constants.
   */
  final int windProps;  
  
  /**See {@link GralWindow_ifc#setResizeAction(GralUserAction)}. */
  protected GralUserAction resizeAction;
  
  protected GralUserAction invisibleSetAction;  
  
  /**See {@link GralWindow_ifc#setMouseAction(GralUserAction)}. */
  protected GralUserAction mouseAction;
  
  protected boolean visibleFirst;
  
  
  /**Standard action for resizing, used if the window contains one panel.
   * It calls {@link GralMng_ifc#resizeWidget(GralWidget, int, int)} 
   * for all widgets in the {@link GralPanelContent#widgetsToResize}
   */
  protected GralUserAction actionResizeOnePanel = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { for(GralWidget widgd: widgetsToResize){
        widgd.getMng().resizeWidget(widgd, 0, 0);
      }
      return true;
    }
  };



  
  /**Constructs a window. 
   * 
   * @param nameWindow
   * @param windProps
   * @param mng
   * @param panelComposite The implementing instance for a panel.
   */
  public GralWindow(String nameWindow, int windProps, GralWidgetMng mng, Object panelComposite)
  {
    super( nameWindow, mng, panelComposite);
    this.windProps = windProps;

  }

  
  
  @Override public void setActionOnSettingInvisible(GralUserAction action)
  { invisibleSetAction = action;
  }


  
  @Override public void setWindowVisible(boolean visible){
    itsMng.setInfo(this, GralMng_ifc.cmdSetWindowVisible, visible? 1: 0, null, null);
  }
  

  @Override public void closeWindow(){
    itsMng.setInfo(this, GralMng_ifc.cmdCloseWindow, 0, null, null);
  }
  
  
  @Override public void repaint(){
    itsMng.setInfo(this, GralMng_ifc.cmdRedraw, 0, null, null);
  }
  
  
  @Override public void repaint(int delay, int latest){
    itsMng.setInfoDelayed(this, GralMng_ifc.cmdRedraw, 0, null, null, delay);
  }
  

  
}
