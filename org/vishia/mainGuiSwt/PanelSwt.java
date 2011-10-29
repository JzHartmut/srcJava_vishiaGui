package org.vishia.mainGuiSwt;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;

public class PanelSwt extends GralPanelContent
{
  
  //protected Composite panelSwt;
  
  public PanelSwt(String name, GralWidgetMng mng)
  {
    super(name, mng);
  }

  public PanelSwt(String name, Composite panelSwt)
  {
    super(name, panelSwt);
    
  }

  @Override public Composite getPanelImpl()
  {
    return (Composite)panelComposite;
  }
  
  @Override
  public boolean setFocus()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override public void redraw(){  ((Control)panelComposite).redraw(); }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Composite)panelComposite).setBounds(x,y,dx,dy);
  }
  



}
