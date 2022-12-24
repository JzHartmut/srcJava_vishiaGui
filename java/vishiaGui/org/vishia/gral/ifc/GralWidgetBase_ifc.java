package org.vishia.gral.ifc;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;

/**This is the basic interface of all widgets. 
 * It is especially important for comprehensive widgets which are not derived from {@link org.vishia.gral.base.GralWidget}
 * but only from {@link org.vishia.gral.base.GralWidgetBase}. 
 * 
 * Especially the {@link org.vishia.gral.base.GralPos#parent} uses this type, because all widgets can be the parent. 
 * 
 * @author hartmut
 *
 */
public interface GralWidgetBase_ifc {

  /**Access to the name of the widget. But you can also access the final public {@link org.vishia.gral.base.GralWidgetBase#name} immediately.
   * @return the name
   */
  public String getName();
  
  /**Position of the widget, which refers also the {@link GralPos#parent}. 
   * The parent in a GralPos has usual the reference to the {@link org.vishia.gral.base.GralMng}.
   * 
   * @return the position, always available, never null.
   */
  public GralPos pos();
  
  
  /**Sets the focus to the widget. . It can be called in any thread. If it is called in the graphic thread,
   * the repaint action is executed immediately in the thread. Elsewhere the graphic thread will be woken up.
   */
  void setFocus();
  
  void setFocus(int delay, int latest);
  
  /**Returns true if this widget is the focused one.
   */
  boolean isInFocus();
  
  /**Sets this widget visible on graphic or invisible. Any widget can be visible or not. More as one widgets
   * can use the same position, only one of them may set visible. 
   * For a {@link GralWindow}, its the visibility of the whole window. 
   * Note that a window which is invisible is not shown in the task bar of the operation system. 
   * Note that an application can have more as one window. 
   * Note that a dialog window can be set to invisible if it is not need yet instead destroy and build newly.
   * @param visible
   * @return
   */
  boolean setVisible(boolean visible);
  
  /**Returns whether the widget is visible or not. This method can be invoked in any thread.
   * It is an estimation because the state of the widget may be changed in the last time or a window
   * can be covered by another one. The widget is not visible if it is a member of a card in a tabbed
   * panel and that tab is not the selected one.
   * @return true if the widget seams to be visible.
   */
  boolean isVisible();
  

  /**If this widget is a comprehensive widget or a panel, it sets one of the content as focused.
   * @param widg The sub widget which should be focused.
   * It influences for example the current tab of a tabbed panel,
   * or influences which widget of a comprehensive widget is first focused.
   * On a panel it determines which widget should get the focus if the panel gets the focus.
   */
  void setFocusedWidget ( GralWidgetBase_ifc widg);
  
  
  GralWidgetBase_ifc getFocusedWidget ( );

  
  /**Returns the associated GralMng. The GralMng is associated by construction,
   */
  GralMng gralMng();
  
  
  
  /**This is used to access to the implementation widget instance.
   * For comprehensive widgets one of the implementation widgets is returned which has a main functionality.
   * @return
   */
  GralWidget.ImplAccess getImplAccess();
  

  

}
