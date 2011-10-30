package org.vishia.mainGuiSwt;


import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralWidget;

public class PanelSwt extends GralPanelContent
{
  
  //protected Composite panelSwt;
  
  private PanelSwt(String name, GralWidgetMng mng)
  {
    super(name, mng, null);
  }

  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelComposite} should be set 
   *   after construction of a derived class.
   */
  public PanelSwt(String name, GralWidgetMng mng, Composite panelSwt)
  {
    super(name, mng, panelSwt);
    if(panelSwt !=null){
      panelSwt.addControlListener(resizeItemListener);
    }
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
  

  protected ControlListener resizeItemListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      Widget wparent = e.widget; //it is the SwtCanvas because this method is assigned only there.
      //Control parent = wparent;
      for(GralWidget widgd: widgetsToResize){
        widgd.getMng().resizeWidget(widgd, 0, 0);
      }
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  


}
