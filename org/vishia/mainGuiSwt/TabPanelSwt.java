package org.vishia.mainGuiSwt;

import java.util.List;
import java.util.Queue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.gridPanel.PanelActivatedGui;
import org.vishia.gral.gridPanel.PanelContent;
import org.vishia.gral.gridPanel.TabPanel;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.gral.widget.WidgetCmpnifc;

public class TabPanelSwt extends TabPanel
{

  /**Wrapper arround the TabFolder. The swt.TabFolder is the container for all TabItem. */
  private class TabFolder_ implements WidgetCmpnifc
  {
    final TabFolder widgetSwt;
    TabFolder_(Composite parent, int style)
    { widgetSwt = new TabFolder(parent, style); 
    }
    @Override public Widget getWidget(){ return widgetSwt; }
    @Override public boolean setFocus(){ return widgetSwt.setFocus(); }
    
  }
  
	private final TabFolder_ tabMng;
	
	final GuiPanelMngSwt mng;
	
	TabPanelSwt(GuiPanelMngSwt mng, PanelActivatedGui user, int property)
	{ super(user, property);
		this.mng = mng;
		tabMng = new TabFolder_(mng.graphicFrame, SWT.TOP);
		tabMng.widgetSwt.addSelectionListener(tabItemSelectListener);
		tabMng.widgetSwt.addControlListener(resizeListener);
  	
	}
	
	@Override public WidgetCmpnifc getGuiComponent()
	{ return tabMng;
	}
	
  
  
	@Override public PanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2)
	{ ///
	  Rectangle sizeTabFolder = tabMng.widgetSwt.getBounds();
	  TabItem tabItem = new TabItem(tabMng.widgetSwt, SWT.None);
	  tabItem.setText(sLabel);
		CanvasStorePanelSwt panel;
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  if(yGrid <0 || xGrid <0){
			panel = new CanvasStorePanelSwt(tabMng.widgetSwt, 0, colorBackground);
		} else {
	  	panel = new GridPanelSwt(tabMng.widgetSwt, 0, colorBackground, mng.propertiesGui.xPixelUnit(), mng.propertiesGui.yPixelUnit(), 5, 5);
		}
	  panel.swtCanvas.setBounds(sizeTabFolder);
	  panel.itsTabSwt = tabItem;
		tabItem.setControl(panel.swtCanvas);
		mng.registerPanel(sName, panel);
    panels.put(sName, panel);
	  return panel;
  }

  
	@Override public PanelContent addCanvasPanel(String sName, String sLabel)
	{ TabItem tabItemOperation = new TabItem(tabMng.widgetSwt, SWT.None);
		tabItemOperation.setText(sLabel);
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  CanvasStorePanelSwt panel = new CanvasStorePanelSwt(tabMng.widgetSwt, 0, colorBackground);
    mng.registerPanel(sName, panel);
	  tabItemOperation.setControl(panel.swtCanvas);
    panels.put(sName, panel);
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
					PanelContent panelContent = (PanelContent)(data);
					Queue<WidgetDescriptor> widgetInfos = panelContent.widgetList; 
					if(notifyingUserInstanceWhileSelectingTab !=null){
					  notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
					}
					//TODO remove visible infos for last panel, active act panel.
					mng.changeWidgets(widgetInfos);
					currentPanel = panelContent;
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
  

  
  void stop(){}
	
}
