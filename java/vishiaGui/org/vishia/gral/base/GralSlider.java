package org.vishia.gral.base;

import org.vishia.gral.ifc.GralSlider_ifc;

public class GralSlider extends GralWidget implements GralSlider_ifc
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
  
  
  /**This class contains the access to the GralWidget class. It is used only as super class for the implementation level.
   * Don't use this class from user applications! It is public only because it should be seen from the graphic implementation.
   */
  public abstract class _GraphicImplAccess_ extends GralWidget.ImplAccess 
  implements GralWidgImplAccess_ifc
  {
    
    /**Because this class is not a static one, the constructor is invoked with the following pattern:
     * <pre>
     * widgg.super(widgg);
     * </pre>
     * @param widgg
     */
    protected _GraphicImplAccess_(GralWidget widgg)
    {
      super(widgg);
    }


  }
  
}
