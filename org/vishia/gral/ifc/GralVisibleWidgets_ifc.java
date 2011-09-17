package org.vishia.gral.ifc;

import java.util.Queue;

/**This interface should be implemented by all classes, which presents some Graphical Panels,
 * which may be visible or not. Especially it is for {@link GralTabbedPanel}, which shows only one Tab
 * to one time. But any Window can be visible or not too. The 
 * @author Hartmut Schorrig
 *
 */
public interface GralVisibleWidgets_ifc
{
 
  /**Gets the list of all widgets which are visible yet and should be updated with values therefore. 
   * @return The list.
   */
  Queue<GralWidget> getWidgetsVisible();
  
  
}
