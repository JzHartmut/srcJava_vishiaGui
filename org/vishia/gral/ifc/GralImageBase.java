package org.vishia.gral.ifc;


public abstract class GralImageBase
{
  final Object oImage;
  
  public Object getImage(){ return oImage; }
  
  public GralImageBase(Object oImage){ this.oImage = oImage; }
  
  public abstract GralRectangle getPixelSize();
}
