package org.vishia.gral.swt;

import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralTabbedPanel;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetGthreadSet_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralRectangle;

public class SwtTabbedPanel extends GralTabbedPanel
{

  /**Version, history and license.
   * <ul>
   * <li>2012-03-31 Hartmut new: supports {@link GralPanelContent.MethodsCalledbackFromImplementation#setVisible(boolean)} 
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120331;

  
  /**The Swt TabFolder implementation. */
  /*pkgprivate*/ TabFolder widgetSwt;
    
	final SwtMng mng;
	
	SwtTabbedPanel(String namePanel, SwtMng mng, GralPanelActivated_ifc user, int property)
	{ super(namePanel, mng, user, property);  //initializes as GralWidget and as GralPanel
		this.mng = mng;
		Object oParent = this.pos.panel.getPanelImpl();
    if(oParent == null || !(oParent instanceof Composite) ){ throw new IllegalArgumentException("Software error. You must select a panel before."); }
		Composite parent = (Composite)oParent;
		
		//this.panelComposite = parent;  
    widgetSwt = new TabFolder(parent, SWT.TOP); 
    this.panelComposite = widgetSwt;  
    
		
		widgetSwt.addSelectionListener(tabItemSelectListener);
		widgetSwt.addControlListener(resizeListener);
  	
	}
	
	//@Override 
	public GralPanelContent XXXgetGuiComponent()
	{ return this; //tabMng;
	}
	
  
  
	@Override public GralPanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2)
	{ ///
	  mng.pos.panel = this;  //add the grid panel to this, 
	  Rectangle sizeTabFolder = widgetSwt.getBounds();
	  TabItem tabItem = new TabItem(widgetSwt, SWT.None);
	  tabItem.setText(sLabel);
	  //tabItem.addFocusListener(SWT.FocusIn, focusTabListener);
	  SwtCanvasStorePanel panel;
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  if(yGrid <0 || xGrid <0){
			panel = new SwtCanvasStorePanel(sName, widgetSwt, 0, colorBackground, mng);
		} else {
	  	panel = new SwtGridPanel(sName, widgetSwt, 0, colorBackground, mng.propertiesGui.xPixelUnit(), mng.propertiesGui.yPixelUnit(), 5, 5, mng);
		}
	  panel.swtCanvas.setBounds(sizeTabFolder);
	  panel.itsTabSwt = tabItem;
		tabItem.setControl(panel.swtCanvas);
		panel.swtCanvas.addFocusListener(focusTabListener);
    
		mng.registerPanel(panel);   //register the panel in the mng.
		mng.registerWidget(panel);
    panels.put(sName, panel);   //register the tab panel in the TabbedPanel
	  mng.setPosition(0, 0, 0, 0, 0, '.');
	  mng.pos.panel = panel;   //use the new panel as default
    return panel;
  }

  
	@Override public GralPanelContent addCanvasPanel(String sName, String sLabel)
	{ TabItem tabItemOperation = new TabItem(widgetSwt, SWT.None);
		tabItemOperation.setText(sLabel);
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  SwtCanvasStorePanel panel = new SwtCanvasStorePanel(sName, widgetSwt, 0, colorBackground, mng);
    mng.registerPanel(panel);
	  tabItemOperation.setControl(panel.swtCanvas);
    panels.put(sName, panel);
	  return panel;
  }

  
	
	/**See {@link GralWidget#setFocus()}
	 * @see org.vishia.gral.base.GralTabbedPanel#selectTab(java.lang.String)
	 */
	@Override public GralPanelContent selectTab(String name)
	{ assert(false);
	  GralPanelContent panel = mng.getPanel(name);
	  if(panel instanceof SwtPanel){
  	  SwtPanel swtPanel = (SwtPanel)panel;
  	  if(swtPanel.itsTabSwt !=null){
	  	  widgetSwt.setSelection(swtPanel.itsTabSwt);
  	  }
	  }
	  return panel;
	}
	
	
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
  					GralPanelContent panelContent = (GralPanelContent)(data);
  					Queue<GralWidget> widgetInfos = panelContent.widgetList; 
  					newWidgetsVisible = widgetInfos;  //the next call of getWidgetsVisible will be move this reference to widgetsVisible.
  					if(focusedTab !=null){
  					  focusedTab.implMethodPanel_.setVisible(false);  //the last focused tab.
  					}
  					focusedTab = panelContent;
  					focusedTab.implMethodPanel_.setVisible(true);   //the currently focused tab.
            focusedTab.setFocus();
  					mng.log.sendMsg(0, "Fcmd-selectTab %s", panelContent.toString());
  					if(notifyingUserInstanceWhileSelectingTab !=null){
              notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
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
      stop();
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      stop();
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  

  ControlListener resizeItemListener = new ControlListener()
  { @Override public void controlMoved(ControlEvent e) 
    { //do nothing if moved.
      stop();
    }

    @Override public void controlResized(ControlEvent e) 
    { 
      stop();
      //validateFrameAreas();  //calculates the size of the areas newly and redraw.
    }
    
  };
  
  
  FocusListener focusTabListener = new FocusListener()
  {

    @Override
    public void focusGained(FocusEvent e)
    {
      // TODO Auto-generated method stub
      stop();
    }

    @Override
    public void focusLost(FocusEvent e)
    {
      // TODO Auto-generated method stub
      
    }
    
  };
  
  @Override public Widget getWidgetImplementation(){ return widgetSwt; }
  
  @Override public GralRectangle getPixelPositionSize(){
    Rectangle r = widgetSwt.getBounds();
    GralRectangle posSize = new GralRectangle(r.x, r.y, r.width, r.height);
    return posSize;
  }


  
  @Override public boolean setFocus(){ return widgetSwt.setFocus(); }

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

  @Override public void setBoundsPixel(int x, int y, int dx, int dy)
  { widgetSwt.setBounds(x,y,dx,dy);
  }
  



  @Override public void repaintGthread(){  widgetSwt.redraw(); widgetSwt.update(); }

  @Override public Composite getPanelImpl() { return widgetSwt; }

  @Override protected void removeWidgetImplementation()
  { widgetSwt.dispose();
    widgetSwt = null;
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
  
  
  
  void stop(){}

	
}
