package org.vishia.gral.base;

import org.vishia.gral.ifc.GralWidget;

public abstract class GralLed extends GralWidget
{
  /**Version and history
  * <li>2011-12-03 new Baseclass for LED visualization.
  *   It is the concept of specialized {@link GralWidget}.
  */
  @SuppressWarnings("hiding")
  static final public int version = 0x20111203;
  
  protected GralLed(String name, GralWidgetMng mng)
  { super(name, 'D', mng);
  }

  public abstract void setColor(int nBorderColor, int nInnerColor);

  
}
