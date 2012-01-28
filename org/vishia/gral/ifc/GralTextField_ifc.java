package org.vishia.gral.ifc;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public interface GralTextField_ifc extends GralWidget_ifc
{
  
  /**Version and history
   * <ul>
   * <li>2012-01-17 new {@link #setSelection(String)}. The possible values of the string parameter are not defined well yet.
   *   The method is only used to show a text right aligned. 
   * <li>2011-12-11 chg {@link #setText(CharSequence)} now uses CharSequence instead String. 
   *   A CharSequence is more universal. Internally the contained String will be copied.
   * <li>2011-12-01 new {@link #getCursorPos()}, to get parts of selected texts in a box.
   *   It is used for commands in the output window for JavaCommander.
   * <li>2011-09-00 created, universal access to textual kind fields.
   * </ul>
   * 
   */
  public final static int version = 0x20111203;
  
  /**Set the textual content of the widget. This method is able to call in any thread. 
   * The text may be stored in a queue and applied to the widget in the graphical thread.
   * @param text The content
   */
  void setText(CharSequence text);
  
  /**Sets the textual content and show it with the given caret position.
   * It is adequate {@link #setText(CharSequence)}.
   * @param text The text to show in the widget.
   * @param caretPos 0 for left, -1 for right, 0 to Integer.MAXINT for a given position.
   *   If the caret position is greater then the number of chars, it is set right.
   *   That character which is left from the caret position is shown guaranteed in the text field.
   *   Especially -1 can be given to show the text right-aligned.
   */
  void setText(CharSequence text, int caretPos);
  
  String getText();
  
  int getCursorPos();
  
  /**Sets the style of all new set and added texts. The content contained in the text field or box are not changed.
   * @param color
   * @param font
   */
  void setTextStyle(GralColor color, GralFont font);
  
  
  /**Sets the selection of the text.
   * <ul>
   * <li>|..< from left to rigth, right aligned
   * <li>TODO
   * </ul>
   * @param how
   */
  //void setSelection(String how);
  
}
