package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget_ifc;

public interface GralKeySpecial_ifc 
{

  /**This method can be overridden by specialized classes of this.
   * This method is called firstly. 
   * Note if a special key should be handled in the {@link GralWidget#getActionChange()}
   * then this special handling should be programmed in the derived class.
   * If this method returns true, the key is not applied to the action and not applied to the common handling.
   * @return true if the key is processed.
   * 
   * 
   * */
  boolean specialKeysOfWidgetType(int key, GralWidget_ifc widgg, Object widgImpl);

}
