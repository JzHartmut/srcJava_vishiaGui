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
   * 
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
  public static final int version = 20120303;

  
  /**Set the textual content of the widget. This method is able to call in any thread. 
   * The text may be stored in a queue and applied to the widget in the graphical thread.
   * @param text The content
   */
  void setText(CharSequence text);
  
  /**Sets the textual content and show it with the given caret position.
   * It is adequate {@link #setText(CharSequence)} if caretPos = 0.
   * @param text The text to show in the widget.
   * @param caretPos 0 for left, -1 for right, 0 to Integer.MAXINT for a given position.
   *   If the caret position is greater then the number of chars, it is set right.
   *   That character which is left from the caret position is shown guaranteed in the text field.
   *   Especially -1 can be given to show the text right-aligned.
   */
  void setText(CharSequence text, int caretPos);
  
  /**Returns the text of this field. Returns always a valid String object, "" if the text was never set. */
  String getText();
  
  int getCursorPos();
  
  int setCursorPos(int pos);
  
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
