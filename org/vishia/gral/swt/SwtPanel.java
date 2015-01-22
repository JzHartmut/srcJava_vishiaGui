package org.vishia.gral.swt;


import java.awt.Component;
import java.awt.Dimension;

import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;

public class SwtPanel extends GralPanelContent.ImplAccess
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

  
  /**It is either a Composite or a SwtCanvas
   * 
   */
  public Composite panelComposite;
  
  /**The associated tab in a TabFolder if this panel is the main panel of the TabItem, or null 
   * if it isn't a main panel of a tab in a tabbed panel.
   * <br><br>    
   * Note: can't be final because it may be unknown on calling constructor. The property whether 
   * a panel is a tab-panel can't be presented with an extra subclass, because this class is the subclass 
   * of several Swt panel types. Use the aggregation principle instead multi-inheritance.   
   */
  public TabItem itsTabSwt; 
  

  //protected Composite panelSwt;
  
  private SwtPanel(GralPanelContent panelg)
  { super(panelg);
    panelComposite = null;
  }

  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelComposite} should be set 
   *   after construction of a derived class.
   */
  public SwtPanel(GralPanelContent panelg, Composite panelSwt)
  { super(panelg);
    panelComposite = panelSwt;
    if(panelSwt !=null){
      panelSwt.addControlListener(resizeItemListener);
    }
  }

  /*
  @Override public Composite getPanelImpl()
  {
    return (Composite)panelComposite;
  }*/
  


  
  @Override public GralRectangle getPixelPositionSize(){ return SwtWidgetHelper.getPixelPositionSize((Composite)panelComposite); }


  @Override public GralRectangle getPixelSize(){
    Rectangle r = ((Composite)panelComposite).getClientArea();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Composite)panelComposite).setBounds(x,y,dx,dy);
  }
  
  
  
  @Override public void repaintGthread(){
    if(panelComposite !=null){
      ((Composite)panelComposite).redraw();
    }
  }




  @Override public void removeWidgetImplementation()
  { if(panelComposite !=null){
      ((Composite)panelComposite).dispose();
      panelComposite = null;
    }
  }
  

  //@Override 
  public boolean remove(){
    
    //super.remove();
    if(itsTabSwt !=null){
      itsTabSwt.dispose();
      itsTabSwt = null;
    }
    if(panelComposite !=null){
      panelComposite.dispose();
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
      for(GralWidget widg1: ((GralPanelContent)widgg).widgetsToResize){
        widg1.gralMng().resizeWidget(widg1, 0, 0);
      }
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };


  @Override public boolean setFocusGThread()
  {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return panelComposite;
  }


  
  
  
}
