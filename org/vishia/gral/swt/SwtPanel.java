package org.vishia.gral.swt;


import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget;

public class SwtPanel extends GralPanelContent
{
  
  /**Version history:
   * <ul>
   * <li>2011-11-19 Hartmut chg: {@link #itsTabSwt} with correct type moved from {@link GralPanelContent}.
   * <li>2011-09-25 Hartmut creation: Common class for all Swt implementations of Panels.
   *   This class can implement the abstract methods from {@link GralPanelContent} for the implementation
   *   in a common form.
   * </ul>
   * 
   */
  @SuppressWarnings("hiding")
  public final static int version = 0x20111119;

  
  /**The associated tab in a TabFolder if this panel is the main panel of the TabItem, or null 
   * if it isn't a main panel of a tab in a tabbed panel.
   * <br><br>    
   * Note: can't be final because it may be unknown on calling constructor. The property whether 
   * a panel is a tab-panel can't be presented with an extra subclass, because this class is the subclass 
   * of several Swt panel types. Use the aggregation principle instead multi-inheritance.   
   */
  public TabItem itsTabSwt; 
  

  //protected Composite panelSwt;
  
  private SwtPanel(String name, GralWidgetMng mng)
  {
    super(name, mng, null);
  }

  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelComposite} should be set 
   *   after construction of a derived class.
   */
  public SwtPanel(String name, GralWidgetMng mng, Composite panelSwt)
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

  
  @Override public void redrawDelayed(int delay){
    redraw();
  }
  
  

  
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = ((Control)panelComposite).getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Composite)panelComposite).setBounds(x,y,dx,dy);
  }
  

  @Override protected void removeWidgetImplementation()
  { if(panelComposite !=null){
      ((Composite)panelComposite).dispose();
      panelComposite = null;
    }
  }
  

  @Override public boolean remove(){
    super.remove();
    if(itsTabSwt !=null){
      itsTabSwt.dispose();
      itsTabSwt = null;
    }
    return true;
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
