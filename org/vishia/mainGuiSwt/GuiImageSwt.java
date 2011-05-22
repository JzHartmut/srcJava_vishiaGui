package org.vishia.mainGuiSwt;

import org.eclipse.swt.graphics.Image;
import org.vishia.mainGui.GuiImageBase;
import org.vishia.mainGui.GuiRectangle;

public class GuiImageSwt extends GuiImageBase
{
  final Image image;
  
  public GuiImageSwt(Image oImage)
  {
    super(oImage);
    this.image = oImage;
  }
  
  public GuiRectangle getPixelSize()
  { org.eclipse.swt.graphics.ImageData data = image.getImageData();
    GuiRectangle rr = new GuiRectangle(0, 0, data.width, data.height);
    return rr;
  }
  
  
}
