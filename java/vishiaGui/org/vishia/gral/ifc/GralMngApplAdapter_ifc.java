package org.vishia.gral.ifc;

/**This interface is used to adapt the widget manager to the application frame.
 * It is implemented by {@link org.vishia.gral.area9.GralArea9Window}.
 * @author Hartmut Schorrig
 *
 */
public interface GralMngApplAdapter_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
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
  public static final int version = 20120303;

  /**For context sensitive help, it assigns a part of a URL to the widget which has the focus gained.
   * This URL is used on calling the help function (usual F1 key or help button).
   * @param url A ":file.html#label" or ":localdir/file.html#label" 
   *   or "/abs/path/to/help.html#label" or "relatpath/help.html#lablel":<br>
   *   with a colon as first char the specific base directory of the help is used.  
   *   This origin of the url is given with a global context of the help window.
   *   See {@link org.vishia.gral.area9_ifc.GralArea9#setHelpBase(String)}
   *   If url is an absolute path then it is used as is.
   *   If it does not start with a ":" then is should be used as relative path
   *   related to the application's current directory. 
   *   
   */
  void setHelpUrl(String url);
}
