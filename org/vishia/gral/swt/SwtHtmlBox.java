package org.vishia.gral.swt;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.vishia.gral.base.GralHtmlBox;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;

public class SwtHtmlBox extends GralHtmlBox
{
  
  Browser boxSwt;
  
  public SwtHtmlBox(String name, GralWidgetMng mng)
  {
    super(name, mng);
    SwtWidgetMng mngSwt = (SwtWidgetMng)mng;
    Composite parent = (Composite)(mng.pos.panel.getPanelImpl());
    boxSwt = new Browser(parent,0);
    mngSwt.setPosAndSizeSwt(boxSwt, 0, 0);
  }

  @Override public void setUrl(String url){
    boxSwt.setUrl(url);
  }

  @Override
  protected void removeWidgetImplementation()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void redraw()
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setBoundsPixel(int x, int y, int dx, int dy)
  {
    // TODO Auto-generated method stub
    
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }


}
