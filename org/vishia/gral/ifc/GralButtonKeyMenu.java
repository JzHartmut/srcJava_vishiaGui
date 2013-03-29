package org.vishia.gral.ifc;

import org.vishia.util.KeyCode;

/**This class contains Strings and keys for functions of an application
 * in a common way to build menus, buttons etc.
 * @author Hartmut Schorrig
 *
 */
public class GralButtonKeyMenu
{
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2013-03-30 created. Situation before: discrete variables in {@link org.vishia.commander.FcmdIdents} 
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
  public static final int version = 20130330;

  
  /**Menu bar string.
   * Use for example  = "&Help/&Settings [cP]".
   */
  public String menu;
  
  /**Context menu string. */
  public String menuContext = "Settings [cP]";
  
  /**Identification for the button to present. 
   * 3 chars: gF1
   */
  public String buttonId;
  
  /**Text for the button*/
  public String buttontext;
  
  /**One ot two {@link KeyCode} for the keys. */
  public int key1, key2;

  /**The action which is associated to all the functions. */
  public GralUserAction action;



  public GralButtonKeyMenu(GralUserAction action, String menu, String menuContext
      , String buttonId, String buttontext, int key1, int key2){
    this.action = action;
    this.menu = menu;
    this.menuContext = menuContext;
    this.buttonId = buttonId;
    this.buttontext = buttontext;
    this.key1 = key1;
    this.key2 = key2;
  }

}
