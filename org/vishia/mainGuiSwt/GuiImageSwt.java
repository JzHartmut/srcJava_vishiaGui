package org.vishia.mainGuiSwt;

import org.eclipse.swt.graphics.Image;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralRectangle;

public class GuiImageSwt extends GralImageBase
{
  final Image image;
  
  public GuiImageSwt(Image oImage)
  {
    super(oImage);
    this.image = oImage;
  }
  
  public GralRectangle getPixelSize()
  { org.eclipse.swt.graphics.ImageData data = image.getImageData();
    GralRectangle rr = new GralRectangle(0, 0, data.width, data.height);
    return rr;
  }
  
  
}
