package org.vishia.mainGui;

public class ColorGui
{
  public int red, green, blue;
  
  public ColorGui(int red, int green, int blue){
    this.red = red; this.green = green; this.blue = blue;
  }
  
  public int getColorValue(){ return (red & 0xff)<<16 | (green & 0xff)<<8 | (blue & 0xff); } 
}
