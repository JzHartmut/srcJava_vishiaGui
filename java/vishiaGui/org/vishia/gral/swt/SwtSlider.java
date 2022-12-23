package org.vishia.gral.swt;

import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralSlider;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

public class SwtSlider extends GralSlider._GraphicImplAccess_
{
  /**It contains the association to the swt widget (Control) and the {@link SwtMng}
   * and implements some methods of {@link GralWidgImplAccess_ifc} which are delegate from this.
   */
  private final SwtWidgetHelper swtWdgW;

  

  public SwtSlider(GralSlider widg, SwtMng mng) {
    widg.super(widg);
    swtWdgW = null;
    // TODO Auto-generated constructor stub
  }

  @Override public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }
  
  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); swtWdgW.setVisibleGThread(bVisible); }


  @Override public void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override public void redrawGthread()
  {
    // TODO Auto-generated method stub
    
  }

  @Override public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override public GralRectangle getPixelPositionSize()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }

  
}
