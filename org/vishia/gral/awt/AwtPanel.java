package org.vishia.gral.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import org.eclipse.swt.widgets.Control;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

public class AwtPanel  extends GralPanelContent
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
  public Container itsTabAwt; 
  

  //protected Composite panelSwt;
  
  private AwtPanel(String name, GralWidgetMng mng)
  {
    super(name, mng, null);
  }

  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelComposite} should be set 
   *   after construction of a derived class.
   */
  public AwtPanel(String name, GralWidgetMng mng, Container panelAwt)
  {
    super(name, mng, panelAwt);
    if(panelAwt !=null){
      panelAwt.addComponentListener(resizeItemListener);
    }
  }

  @Override public Container getPanelImpl()
  {
    return (Container)panelComposite;
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
  

  
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = ((Component)panelComposite).getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }


  @Override public GralRectangle getPixelSize(){
    Dimension r = ((Component)panelComposite).getSize();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Container)panelComposite).setBounds(x,y,dx,dy);
  }
  
  
  @Override protected void repaintGthread(){
    if(panelComposite !=null){
      ((Container)panelComposite).repaint();
    }
  }




  @Override protected void removeWidgetImplementation()
  { if(panelComposite !=null){
      //((Container)panelComposite).dispose();
      panelComposite = null;
    }
  }
  

  @Override public boolean remove(){
    super.remove();
    if(itsTabAwt !=null){
      //itsTabAwt.dispose();
      itsTabAwt = null;
    }
    return true;
  }
  
  
  @Override public GralWidgetGthreadSet_ifc getGthreadSetifc(){ return gThreadSet; }

  /**Implementation of the graphic thread widget set interface. */
  GralWidgetGthreadSet_ifc gThreadSet = new GralWidgetGthreadSet_ifc(){

    @Override public void clearGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void insertGthread(int pos, Object visibleInfo, Object data)
    { // TODO Auto-generated method stub
    }

    @Override public void redrawGthread()
    { // TODO Auto-generated method stub
    }

    @Override public void setBackGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setForeGroundColorGthread(GralColor color)
    { // TODO Auto-generated method stub
    }

    @Override public void setTextGthread(String text, Object data)
    { // TODO Auto-generated method stub
    }
  };
  
  
  
  protected ComponentListener resizeItemListener = new ComponentListener()
  {

    @Override
    public void componentHidden(ComponentEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void componentMoved(ComponentEvent e)
    {
      // TODO Auto-generated method stub
      
    }

    @Override
    public void componentResized(ComponentEvent e)
    {
    }

    @Override
    public void componentShown(ComponentEvent e)
    {
      // TODO Auto-generated method stub
      
    } 
    
    
    /*
    @Override public void controlMoved(ControlEvent e) 
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
    */
    
  };


  
  
  
}
