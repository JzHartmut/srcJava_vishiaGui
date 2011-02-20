package org.vishia.mainGuiSwt;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.vishia.mainGui.PanelActivatedGui;
import org.vishia.mainGui.TabPanel;
import org.vishia.mainGui.PanelContent;
import org.vishia.mainGui.WidgetDescriptor;

public class TabPanelSwt extends TabPanel
{

	private final TabFolder tabMng;
	
	final GuiPanelMngSwt mng;
	
	TabPanelSwt(GuiPanelMngSwt mng, PanelActivatedGui user)
	{ super(user);
		this.mng = mng;
		tabMng = new TabFolder(mng.graphicFrame, SWT.TOP);
		tabMng.addSelectionListener(tabItemSelectListener);
  	
	}
	
	@Override public Object getGuiComponent()
	{ return tabMng;
	}
	
	static TabPanel xxxcreateTabPanel(GuiPanelMngSwt mng, PanelActivatedGui user)
	{
		TabPanelSwt tabPanel = new TabPanelSwt(mng, user);
		//tabMng.addSelectionListener(panelMng.tabItemSelectListener);
	  return tabPanel;
	}

	
  
  
	@Override public Object addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2)
	{ TabItem tabItemOperation = new TabItem(tabMng, SWT.None);
		tabItemOperation.setText(sLabel);
		CanvasStorePanelSwt panel;
		Color colorBackground = mng.propertiesGui.color(0xeeeeee);
	  if(yGrid <0 || xGrid <0){
			panel = new CanvasStorePanelSwt(tabMng, 0, colorBackground);
		} else {
	  	panel = new GridPanelSwt(tabMng, 0, colorBackground, mng.propertiesGui.xPixelUnit(), mng.propertiesGui.yPixelUnit(), 5, 5);
		}
		PanelContent<Control> panelContent = mng.registerPanel(sName, panel);
	  tabItemOperation.setControl(panel);
	  panel.setData(panelContent);
	  return panel;
  }

  
	@Override public Object addCanvasPanel(String sName, String sLabel)
	{ TabItem tabItemOperation = new TabItem(tabMng, SWT.None);
		tabItemOperation.setText(sLabel);
		Color colorBackground = mng.propertiesGui.color(0xeeeeee);
	  CanvasStorePanelSwt panel = new CanvasStorePanelSwt(tabMng, 0, colorBackground);
	  PanelContent<Control> panelContent = mng.registerPanel(sName, panel);
	  tabItemOperation.setControl(panel);
	  panel.setData(panelContent);
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
					PanelContent<Control> panelContent = (PanelContent<Control>)(data);
					List<WidgetDescriptor<?>> widgetInfos = panelContent.widgetList; 
					notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
				}
			}
		}
  	
  };
  
  
  

	
}
