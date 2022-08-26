package org.vishia.gral.ifc;

public interface GralSlider_ifc {
  
  
  /**Returns a value between 0.0 and 1.0 which presents the slider position. 
   * 0.0 is left or bottom, 1.0 is right or top.
   */
  float getSliderPosition();
  
  /**Sets the slider size as ratio of the slider range. 
   * It may be the ratio between existing range to visible rangefor a slider which presents a visible range of anything.
   * @param ratio Value from 0.0 to 1.0: 0.0 means a 1-pixel-line slider. 1.0 means a non-move-able slider because
   *   it uses the full range for moving.
   */
  void setSliderSize(float ratio);
}
