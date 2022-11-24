package org.vishia.gral.cfg;

import java.util.LinkedList;
import java.util.List;

/**ZBNF: Window::= ... ;
 */
public class GralCfgWindow extends GralCfgData.GuiCfgWidget
{
  /**Version, history and license.
   * <ul>
   * <li>2022-11-14 Hartmut created similar GralCfgPanel. 
   *   Before only one window are used in script configured GUIs. It means the window was not part of the script. 
   * </ul>
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
  public static final String sVersion = "2022-11-14";

  
  
  /**All elements of this window. If it is a tabbed panel, the elements are only panels. */
  //final List<GralCfgElement> listElements = new LinkedList<GralCfgElement>();
  GralCfgPanel panelWin;

  String title;
  
  public GralCfgWindow(GralCfgElement itsElement){ 
    super(itsElement, 'w'); 
  }
  
  public void set_title(String val) {
    this.title = val;
  }
    //colorName = 
  @Override public String toString() { return "Window: " + super.name; }

}
