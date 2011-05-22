package org.vishia.mainGui;

public abstract class GuiImageBase
{
  final Object oImage;
  
  public Object getImage(){ return oImage; }
  
  public GuiImageBase(Object oImage){ this.oImage = oImage; }
  
  public abstract GuiRectangle getPixelSize();
}
