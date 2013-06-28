package org.vishia.gral.base;

public interface GralWidgImpl_ifc
{
  /**Sets the focus to the widget.
   * See {@link GralMng_ifc#setFocus(GralWidget)}.
   * @return true if the focus is set really.
   */
  boolean setFocusGThread();
  
  boolean setVisible(boolean visible);

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
   * this method returns null. You should call {@link #getWidgetMultiImplementations()} instead. */
  Object getWidgetImplementation();
  
  void setBoundsPixel(int x, int y, int dx, int dy);
 
  GralWidgetGthreadSet_ifc getGthreadSetifc();
  
  

  
}
