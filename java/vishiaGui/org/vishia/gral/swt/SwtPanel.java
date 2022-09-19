package org.vishia.gral.swt;


import java.awt.Component;
import java.awt.Dimension;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.util.Debugutil;

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
  public Composite panelSwtImpl;
  
  
  /**If this panel is a TabbedPanel, this is the adequate tabFolder instance.
   * 
   */
  TabFolder tabFolder;
  
  /**The associated tab in a TabFolder if this panel is the main panel of the TabItem, or null 
   * if it isn't a main panel of a tab in a tabbed panel.
   * <br><br>    
   * Note: can't be final because it may be unknown on calling constructor. The property whether 
   * a panel is a tab-panel can't be presented with an extra subclass, because this class is the subclass 
   * of several Swt panel types. Use the aggregation principle instead multi-inheritance.   
   */
  public TabItem itsTabSwt; 
  
  /**If this panel represents a Window, it is the GralWindow implementation.
   * The {@link SwtSubWindow} does not inherit from {@link GraphicImplAccess}.
   * This aggregation is null if the panel is not the window's panel.
   */
  protected GralWindow.WindowImplAccess swtGralWindow;
  

  //protected Composite panelSwt;
  
  SwtPanel(GralPanelContent panelg)
  { super(panelg);
    panelSwtImpl = null;
    this.swtGralWindow = null;
  }

  
  protected void setWindowImpl(GralWindow.WindowImplAccess swtGralWindow) {
    if(this.swtGralWindow !=null) throw new IllegalStateException("can only be done once");
    this.swtGralWindow = swtGralWindow;
  }
  
  
  /**Constructs a panel
   * @param name of panel.
   * @param mng The widget manager
   * @param panelSwt may be null, then the {@link GralPanelContent#panelSwtImpl} should be set 
   *   after construction of a derived class.
   */
  public SwtPanel(GralPanelContent panelg, Composite panelSwt)
  { super(panelg);
    panelSwtImpl = panelSwt;
    if(panelSwt !=null){
      panelSwt.addControlListener(resizeItemListener);
    }
    this.swtGralWindow = null;
  }

  /*
  @Override public Composite getPanelImpl()
  {
    return (Composite)panelComposite;
  }*/
  

  
  void checkCreateTabFolder(Composite composite, SwtMng swt) {
    if(super.isTabbed()) {
      this.tabFolder = new TabFolder(composite, SWT.BORDER);
      Rectangle areaFolder = composite.getClientArea();
      //GralRectangle rectangle = swt.calcWidgetPosAndSizeSwt(widgg.pos(), composite, 500, 300);
      this.tabFolder.setBounds(areaFolder);
      this.panelSwtImpl = this.tabFolder;
      //todo this.tabFolder.addSelectionListener(this.tabItemSelectListener);
      //todo this.tabFolder.addControlListener(this.resizeListener);

    }
  }

  
  @Override public GralRectangle getPixelPositionSize(){ return SwtWidgetHelper.getPixelPositionSize((Composite)panelSwtImpl); }




  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { ((Composite)panelSwtImpl).setBounds(x,y,dx,dy);
  }
  
  
  
  @Override public void repaintGthread(){
    if(panelSwtImpl !=null){
      ((Composite)panelSwtImpl).redraw();
      SwtMng.storeGralPixBounds(this, (Composite)panelSwtImpl);
    }
  }




  @Override public void removeWidgetImplementation()
  { if(panelSwtImpl !=null){
      ((Composite)panelSwtImpl).dispose();
      panelSwtImpl = null;
    }
  }
  

  //@Override 
  public boolean remove(){
    
    //super.remove();
    if(itsTabSwt !=null){
      itsTabSwt.dispose();
      itsTabSwt = null;
    }
    if(panelSwtImpl !=null){
      panelSwtImpl.dispose();
    }
    return true;
  }
  
  
  
  @Override public boolean setFocusGThread()
  {
    setVisibleGThread(true);
    boolean bRet = panelSwtImpl.setFocus();
    return bRet;
  }

  @Override public void setVisibleGThread(boolean bVisible) { super.setVisibleState(bVisible); panelSwtImpl.setVisible(bVisible); }


  @Override public Object getWidgetImplementation()
  {
    // TODO Auto-generated method stub
    return panelSwtImpl;
  }

  @Override
  public void updateValuesForAction() {
    // TODO Auto-generated method stub
    
  }


  protected ControlListener resizeItemListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
    }
  
    @Override public void controlResized(ControlEvent e) 
    { 
      Widget wparent = e.widget; //it is the SwtCanvas because this method is assigned only there.
      //Control parent = wparent;
      for(GralWidget widg1: ((GralPanelContent)widgg).getWidgetsToResize()){
        widg1.gralMng().resizeWidget(widg1, 0, 0);
      }
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };


  public SelectionListener tabItemSelectListener = new SelectionListener(){

    @Override
    public void widgetDefaultSelected(SelectionEvent event)
    {
      widgetSelected(event);
    }
    

    /**It is the selected method of the TabFolder.
     * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
     */
    @Override public void widgetSelected(SelectionEvent event)
    {
      try{
        TabItem tab = (TabItem)event.item;    //The tab
        Control container = tab.getControl(); //Its container
        if(container != null){
        //TabFolder tabFolder = tab.getParent();
          Object data = container.getData();
          if(data != null){
            @SuppressWarnings("unchecked")
            SwtPanel swtPanel = (SwtPanel)data;
            GralPanelContent gralPanel = (GralPanelContent)(swtPanel.widgg);
            List<GralWidget> widgetInfos = gralPanel.getWidgetList(); 
            //widgg.newWidgetsVisible = widgetInfos;  //the next call of getWidgetsVisible will be move this reference to widgetsVisible.
            if(gralPanel.getFocusedWidget() !=null){
              widgg.getFocusedWidget().setVisibleState(false);  //the last focused tab.
            }
            widgg.setPrimaryWidget( gralPanel );
            //done with setFocus: widgg.focusedTab.setVisibleState(true);   //the currently focused tab.
            gralPanel.setFocus();
            //System.out.printf("Fcmd-selectTab; %s", panelContent.toString());
            //mng.log.sendMsg(0, "Fcmd-selectTab %s", panelContent.toString());
            if(SwtPanel.this._panel.notifyingUserInstanceWhileSelectingTab !=null){
              SwtPanel.this._panel.notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
            }
          }
        }
      }
      catch(Exception exc){
        String sMsg = exc.getMessage();
        if(sMsg == null){ sMsg = "nullPointer"; }
        System.err.println(sMsg);
        exc.printStackTrace(System.err);
      }
    }
  };

  
  
  ControlListener resizeListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
      Debugutil.stop();
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      Debugutil.stop();
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  


  
}
