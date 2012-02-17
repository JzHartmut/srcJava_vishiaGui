package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

/**This class should only be used for the implementation of the graphic adapter.
 * It helps to associate a GralWidget to any implementation widget.
 * @author hartmut Schorrig
 *
 */
public interface GetGralWidget_ifc {
  /**Returns the GralWigdet from the given implementation graphic component. */
  GralWidget getGralWidget(Object widgImpl);
}
