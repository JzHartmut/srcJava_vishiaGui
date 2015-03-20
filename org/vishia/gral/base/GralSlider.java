package org.vishia.gral.base;

import org.vishia.gral.ifc.GralSlider_ifc;

public abstract class GralSlider extends GralWidget implements GralSlider_ifc
{
  protected float posSlider;

  protected float sizeSlider;
  
  
  public GralSlider(String sName, GralMng mng)
  {
    super(sName, 'V', mng);
  }

  @Override
  public float getSliderPosition() {
    return posSlider;
  }

  @Override
  public void setSliderSize(float ratio) {
    sizeSlider = ratio;
    
  }
}
