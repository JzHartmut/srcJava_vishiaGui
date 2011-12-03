package org.vishia.gral.ifc;

/**This is the interface to all widgets which represents a simple text.
 * @author Hartmut Schorrig
 *
 */
public interface GralTextField_ifc extends GralWidget_ifc
{
  
  /**Version and history
   * <ul>
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
  public abstract void setText(String text);
  
  public abstract String getText();
  
  public abstract int getCursorPos();
  
}
