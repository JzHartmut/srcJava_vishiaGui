package org.vishia.gral.ifc;


public interface GralWindow_ifc
{
  
  /**Controls whether the whole window, which contains this panel, should be visible or not.
   * It is proper for such panels especially, which are the only one in a window. 
   * If a window is setting visible with this method, it is arranged in the foreground.
   * @param visible
   * @return
   */
  void setWindowVisible(boolean visible);
  
  //see GralGrid...
  //void setWindowVisible(boolean visible, GralGridPos atPos);
  
  boolean isWindowsVisible();
  

}
