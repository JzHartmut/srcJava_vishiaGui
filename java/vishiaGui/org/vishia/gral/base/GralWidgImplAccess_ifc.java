package org.vishia.gral.base;

import org.vishia.gral.ifc.GralRectangle;

/**This interface is used as reference to the implementation access layer for all widgets.
 * It defines all possible calls from the application to the implementation widget.
 * <br><br>
 * Note that most of user requirements are done by the {@link GralWidget} capability and the
 * capabilities of its derived classes, for example {@link GralWidget#setText(CharSequence)}
 * or {@link GralWidget#setBackColor(org.vishia.gral.ifc.GralColor, int)}. That methods 
 * stores the text, color etc. in graphic-independent attributes. The method 
 * {@link #redrawGthread()} is the central method to realize that user stimuli for the
 * implementation graphic layer. That method can use especially the {@link GralWidget.DynamicData}
 * and there quest method {@link GralWidget.DynamicData#whatIsChanged}.
 * There is no needing of methods such as <code>setText(String)</code> etc. because the user
 * should able to set the text in any thread. See concept of data set described on {@link GralWidget#setText(CharSequence)} 
 * See {@link GralWidget#_wdgImpl}. 
 * @since 2013-06
 * @author Hartmut Schorrig
 *
 */
public interface GralWidgImplAccess_ifc
{
  
  
  /**Version, history and license .
   * <ul>
   * <li>2022-01-29 Hartmut add: {@link #updateValuesForAction()} used for GralTextField
   *   for cursor positions. 
   * <li>2031-06-20 Hartmut Creation. 
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
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String version = "2022-01-29";

  
  /**Sets the focus to the widget.
   * See {@link GralMng_ifc#setFocus(GralWidget)}.
   * @return true if it has the focus after this operation.
   *   false on any error.
   */
  boolean setFocusGThread();
  
  
  /**Sets the implementation widget(s) visible state. The widgets are really invisible, or they are visible in principle, but they can be covered by other windows or clipping.
   * This method should set the {@link GralWidget#bVisibleState} too. Therewith it is able to quest {@link GralWidget#isVisible()} in any thread.
   * @param bVisible true then the widget should be visible, false it is set to invisible.
   */
  void setVisibleGThread(boolean bVisible);
  
  /**
   * @param visible
   * @return
   * @deprecated
   */
  //@Deprecated
  //boolean setVisible(boolean visible);

  /**Removes the graphical widget in the graphic. */
  void removeWidgetImplementation();
  
  
  /**This method should be implemented in all Widget implementations of the adapter for the
   * underlying graphic system. 
   * <br>Implementation hints: In SWT it should call redraw(). In AWT this is named 'repaint()'-
   * <br>It is possible that the widget
   * consists of more as one graphical widget, then all of it should be redrawn. 
   * It is possible that some data are set in another thread, they should be applied to the widgets firstly.
   * It is possible that the widget is removed though a repaintGthread-order is pending from the time before deleting,
   * for example if the graphic layout is changed. 
   * <br><br>
   * See {@link #repaintRequ}
   * 
   */
  void redrawGthread ( );

  
  /**Returns the implementation class of the widget. If the widget has more as one implementation widgets,
   * this method returns one of the widgets, usual the first important one respectively that widget which represents
   * the implementation layer. A widget of the implementation layer is a SWT.control or a {@link java.awt.Component}.
   * <br><br>
   * If more as one widget are part of the GralWidget, use the special {@link GralWidget#_wdgImpl} class 
   * which should contain the references to that implementation widgets.
   * */
  Object getWidgetImplementation();
  
  /**This is called especially on resize from the panel, but also on creation for comprehensive widgets.
   * If the widget is more comprehensive, consist of more basic widgets,
   * then this operation should care about the parts of the widget.
   * @param x
   * @param y
   * @param dx
   * @param dy
   */
  void setBoundsPixel(int x, int y, int dx, int dy);
 
  
  /**This is called especially on resize from the panel, but also on creation for comprehensive widgets.
   * If the widget is more comprehensive, consist of more basic widgets,
   * then this operation should care about the parts of the widget.
   * The position is gotten from the gral widget.
   */
  GralRectangle setPosBounds ( GralRectangle parentPix );
  
  
  GralRectangle getPixelPositionSize();
  
  /**This operation should be called before any action is invoked in the user space.
   * It should update all values from the implementation widget to the GralWidget
   * to enable getting it in the action. 
   */
  void updateValuesForAction();
  

  
}
