package org.vishia.gral.base;

import org.vishia.gral.ifc.GralRectangle;

/**This interface is used as reference to the implementation layer for all widgets.
 * It defines all possible calls from the application to the implementation widget.
 * <br><br>
 * Note that most of user requirements are done by the {@link GralWidget} capability and the
 * capabilities of its derived classes, for example {@link GralWidget#setText(CharSequence)}
 * or {@link GralWidget#setBackColor(org.vishia.gral.ifc.GralColor, int)}. That methods 
 * stores the text, color etc. in graphic-independent attributes. The method 
 * {@link #repaintGthread()} is the central method to realize that user stimuli for the
 * implementation graphic layer. That method can use especially the {@link GralWidget.DynamicData}
 * and there quest method {@link GralWidget.DynamicData#whatIsChanged}.
 * There is no needing of methods such as <code>setText(String)</code> etc. because the user
 * should able to set the text in any thread. See concept of data set described on {@link GralWidget#setText(CharSequence)} 
 * See {@link GralWidget#_wdgImpl}. 
 * @since 2013-06
 * @author Hartmut Schorrig
 *
 */
public interface GralWidgImpl_ifc
{
  
  
  
  
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
   * <br>Implementation hints: In SWT it should call redraw(). 
   * <br>It is possible that the widget
   * consists of more as one graphical widget, then all of it should be redrawn. 
   * It is possible that some data are set in another thread, they should be applied to the widgets firstly.
   * It is possible that the widget is removed though a repaintGthread-order is pending from the time before deleting,
   * for example if the graphic layout is changed. 
   * <br><br>
   * See {@link #repaintRequ}
   * 
   */
  void repaintGthread();

  
  /**Returns the implementation class of the widget. If the widget has more as one implementation widgets,
   * this method returns one of the widgets, usual the first important one respectively that widget which represents
   * the implementation layer. A widget of the implementation layer is a SWT.control or a {@link java.awt.Component}.
   * <br><br>
   * If more as one widget are part of the GralWidget, use the special {@link GralWidget#_wdgImpl} class 
   * which should contain the references to that implementation widgets.
   * */
  Object getWidgetImplementation();
  
  void setBoundsPixel(int x, int y, int dx, int dy);
 
  GralRectangle getPixelPositionSize();
  
  
  

  
}
