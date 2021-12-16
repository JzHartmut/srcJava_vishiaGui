package org.vishia.gral.swt;

import org.eclipse.swt.graphics.GC;
import org.vishia.gral.ifc.GralCanvas_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralFont;
import org.vishia.gral.ifc.GralImageBase;
import org.vishia.gral.ifc.GralRectangle;

public class SwtCanvasDrawer implements GralCanvas_ifc
{
  
  private final GC graphicContext;
  
  private final SwtMng widgMng;

  SwtCanvasDrawer(SwtMng widgMng, GC graphicContext){
    this.widgMng = widgMng;
    this.graphicContext = graphicContext;
  }
  
  
  @Override
  public void drawImage(GralImageBase image, int x, int y, int dx, int dy,
    GralRectangle imagePixelSize)
  {
    // TODO Auto-generated method stu
    
  }

  @Override
  public void drawLine(GralColor color, int x1, int y1, int x2, int y2)
  {
    graphicContext.setForeground(widgMng.getColorImpl(color));
    graphicContext.drawLine(x1, y1, x2, y2);
    
  }

  @Override
  public void drawText(String text)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void setTextStyle(GralColor color, GralFont font, int origin)
  {
    // TODO Auto-generated method stub
    
  }
  
}
