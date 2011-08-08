package org.vishia.gral.area9;

import org.vishia.gral.base.GralDevice;

public class GuiMainAreaBase
{
  protected final GralDevice gralDevice;
  
  /**Area settings for output. */
  protected String outputArea;
  
  /**Sets the output window to a defined area. .
   * Adds the edit-menu too. 
   * @param xArea 1 to 3 for left, middle, right, See {@link #setFrameAreaBorders(int, int, int, int)}
   * @param yArea 1 to 3 for top, middle, bottom
   * @param dxArea 1 to 3 for 1 field to 3 fields to right.
   * @param dyArea 1 to 3 for 1 field to 3 field to bottom
   */
  public void setOutputArea(String area){
    outputArea = area;
  }

  public GuiMainAreaBase(GralDevice guiDevice)
  {
    super();
    this.gralDevice = guiDevice;
  }



}
