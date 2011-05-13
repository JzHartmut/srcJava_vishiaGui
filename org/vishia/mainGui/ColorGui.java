package org.vishia.mainGui;

public class ColorGui
{
  /**Values 0..255 for the base colors. */
  public final int red, green, blue;

  /**The color-instance for the implementation can be set from the implementation with the necessary type
   * to minimize the effort in dynamic instances.  */
  public Object colorGuimpl;
  
  public ColorGui(int red, int green, int blue){
    this.red = red; this.green = green; this.blue = blue;
  }
  
  public int getColorValue(){ return (red & 0xff)<<16 | (green & 0xff)<<8 | (blue & 0xff); } 
}
