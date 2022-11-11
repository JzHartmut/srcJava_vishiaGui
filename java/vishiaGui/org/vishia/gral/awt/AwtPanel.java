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
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralWidget_ifc;

/**
 * Note: a tabbed panel does not exist in AWT. 
 * https://coderanch.com/t/330446/java/Tabbed-panel-AWT says: Use Card Layout and small buttons for the tab.
 * @author hartmut Schorrig
 *
 */
public class AwtPanel  extends GralPanelContent.ImplAccess
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
  
  /**It is either a Composite or a SwtCanvas
   * 
   */
  public Container panelComposite;

  //protected Composite panelSwt;
  
  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelSwtImpl} should be set 
   *   after construction of a derived class.
   */
  public AwtPanel(GralPanelContent wdgg)
  {
    super(wdgg);
    GralWidget_ifc wdgp =  wdgg.pos().parent;
    final Component awdgp = AwtMng.getAwtImpl(wdgp); 
    assert awdgp instanceof Container;
    final Container awdgparent = (Container) awdgp;
    final Container awdg;
    if(wdgp instanceof GralWindow) { //the main panel of the window
      awdg = awdgparent;
    } else {
      awdg = new Container();
      awdgparent.add(awdg);
    }
    this.wdgimpl = awdg;
  }


  /*@Override public Container getPanelImpl()
  {
    return (Container)panelComposite;
  }*/
  

 
  
  /*@Override public GralRectangle getPixelPositionSize(){
    Rectangle r = ((Component)panelComposite).getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }*/


  /*@Override public GralRectangle getPixelSize(){
    Dimension r = ((Component)panelComposite).getSize();
    GralRectangle posSize = new GralRectangle(0, 0, r.width, r.height);
    return posSize;
  }*/


  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { panelComposite.setBounds(x,y,dx,dy);
  }
  
  
  //@Override 
  public void XXXrepaintGthread(){
    if(panelComposite !=null){
      ((Container)panelComposite).repaint();
    }
  }





//  @Override public boolean remove(){
//    super.remove();
//    if(itsTabAwt !=null){
//      //itsTabAwt.dispose();
//      itsTabAwt = null;
//    }
//    return true;
//  }
//  
  
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

  @Override public boolean setFocusGThread () {
    // TODO Auto-generated method stub
    return false;
  }

  @Override public void setVisibleGThread ( boolean bVisible ) {
    // TODO Auto-generated method stub
    
  }

  @Override public void removeWidgetImplementation () {
    // TODO Auto-generated method stub
    
  }

  @Override public void repaintGthread () {
    // TODO Auto-generated method stub
    
  }


  @Override public GralRectangle getPixelPositionSize () {
    // TODO Auto-generated method stub
    return null;
  }

  @Override public void updateValuesForAction () {
    // TODO Auto-generated method stub
    
  }


  
  
  
}
