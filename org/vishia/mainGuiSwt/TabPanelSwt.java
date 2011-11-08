package org.vishia.mainGuiSwt;

import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
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
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralWidget;

public class TabPanelSwt extends GralTabbedPanel
{

  /**The Swt TabFolder implementation. */
  /*pkgprivate*/ TabFolder widgetSwt;
    
	final SwtWidgetMng mng;
	
	TabPanelSwt(String namePanel, SwtWidgetMng mng, GralPanelActivated_ifc user, int property)
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
	  Rectangle sizeTabFolder = widgetSwt.getBounds();
	  TabItem tabItem = new TabItem(widgetSwt, SWT.None);
	  tabItem.setText(sLabel);
		CanvasStorePanelSwt panel;
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  if(yGrid <0 || xGrid <0){
			panel = new CanvasStorePanelSwt(sName, widgetSwt, 0, colorBackground, mng);
		} else {
	  	panel = new GridPanelSwt(sName, widgetSwt, 0, colorBackground, mng.propertiesGui.xPixelUnit(), mng.propertiesGui.yPixelUnit(), 5, 5, mng);
		}
	  panel.swtCanvas.setBounds(sizeTabFolder);
	  panel.itsTabSwt = tabItem;
		tabItem.setControl(panel.swtCanvas);
		mng.registerPanel(panel);
		mng.registerWidget(panel);
    panels.put(sName, panel);
	  return panel;
  }

  
	@Override public GralPanelContent addCanvasPanel(String sName, String sLabel)
	{ TabItem tabItemOperation = new TabItem(widgetSwt, SWT.None);
		tabItemOperation.setText(sLabel);
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  CanvasStorePanelSwt panel = new CanvasStorePanelSwt(sName, widgetSwt, 0, colorBackground, mng);
    mng.registerPanel(panel);
	  tabItemOperation.setControl(panel.swtCanvas);
    panels.put(sName, panel);
	  return panel;
  }

  
	
	@Override public GralPanelContent selectTab(String name)
	{
	  GralPanelContent panel = mng.getPanel(name);
	  TabItem tabItem = (TabItem)panel.itsTabSwt;
	  widgetSwt.setSelection(tabItem);
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
			TabItem tab = (TabItem)event.item;    //The tab
			Control container = tab.getControl(); //Its container
			if(container != null){
			//TabFolder tabFolder = tab.getParent();
				Object data = container.getData();
				if(data != null){
					@SuppressWarnings("unchecked")
					GralPanelContent panelContent = (GralPanelContent)(data);
					Queue<GralWidget> widgetInfos = panelContent.widgetList; 
					if(notifyingUserInstanceWhileSelectingTab !=null){
					  notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
					}
					//TODO remove visible infos for last panel, active act panel.
					newWidgetsVisible = widgetInfos;  //the next call of getWidgetsVisible will be move this reference to widgetsVisible.
					//mng.changeWidgets(widgetInfos);
					focusedTab = panelContent;
				}
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
  
  @Override public Widget getWidgetImplementation(){ return widgetSwt; }
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
  



  @Override public void redraw(){  widgetSwt.redraw(); widgetSwt.update(); }

  @Override public Composite getPanelImpl() { return widgetSwt; }

  @Override protected void removeWidgetImplementation()
  { widgetSwt.dispose();
    widgetSwt = null;
  }

  
  void stop(){}

	
}
