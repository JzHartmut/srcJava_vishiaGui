package org.vishia.gral.base;

public interface GralWindow_setifc
{
  /**Version, history and license.
   * <ul>2012-04-22 Hartmut new {@link #setFullScreen(boolean)}.
   * <li>2012-01-00 Hartmut created.
   * </ul>
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
   * 
   * 
   */
  public final static int version = 0x20120303;

  
  /**Controls whether the whole window, which contains this panel, should be visible or not.
   * It is proper for such panels especially, which are the only one in a window. 
   * If a window is setting visible with this method, it is arranged in the foreground.
   * @param visible
   * @return
   */
  void setWindowVisible(boolean visible);

  void setFullScreen(boolean full);

  //void repaintGthread();
  
  void closeWindow();


}
