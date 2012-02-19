package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

/**This class should only be used for the implementation of the graphic adapter.
 * It helps to associate a GralWidget to any implementation widget.
 * @author hartmut Schorrig
 *
 */
public interface GetGralWidget_ifc {
  /**Returns the GralWigdet from the data of the given implementation graphic component. 
   * In Any graphic implementation a implementation widget may refer untyped user data.
   * This data may set to the associated instance of GralWidget usually. 
   * But sometimes other data should be referred and the GralWidget is existent one time for more
   * as one implementation widgets. The this interface helps. 
   * */
  GralWidget getGralWidget();
}
