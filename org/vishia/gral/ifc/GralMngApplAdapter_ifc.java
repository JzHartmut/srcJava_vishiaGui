package org.vishia.gral.ifc;

/**This interface is used to adapt the widget manager to the application frame.
 * It is implemented by {@link org.vishia.gral.area9.GralArea9Window}.
 * @author Hartmut Schorrig
 *
 */
public interface GralMngApplAdapter_ifc
{
  /**For context sensitive help, it assigns an URL to the widget which has the focus gained.
   * @param url It msy be a relative path.
   */
  void setHelpUrl(String url);
}
