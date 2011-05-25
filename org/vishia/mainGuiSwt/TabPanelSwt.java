package org.vishia.mainGuiSwt;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.vishia.mainGui.PanelActivatedGui;
import org.vishia.mainGui.TabPanel;
import org.vishia.mainGui.PanelContent;
import org.vishia.mainGui.WidgetCmpnifc;
import org.vishia.mainGui.WidgetDescriptor;

public class TabPanelSwt extends TabPanel
{

  private class TabFolder_ implements WidgetCmpnifc
  {
    final TabFolder widgetSwt;
    TabFolder_(Composite parent, int style){ widgetSwt = new TabFolder(parent, style); }
    @Override public Widget getWidget(){ return widgetSwt; } 
  }
  
	private final TabFolder_ tabMng;
	
	final GuiPanelMngSwt mng;
	
	TabPanelSwt(GuiPanelMngSwt mng, PanelActivatedGui user)
	{ super(user);
		this.mng = mng;
		tabMng = new TabFolder_(mng.graphicFrame, SWT.TOP);
		tabMng.widgetSwt.addSelectionListener(tabItemSelectListener);
  	
	}
	
	@Override public WidgetCmpnifc getGuiComponent()
	{ return tabMng;
	}
	
	static TabPanel xxxcreateTabPanel(GuiPanelMngSwt mng, PanelActivatedGui user)
	{
		TabPanelSwt tabPanel = new TabPanelSwt(mng, user);
		//tabMng.addSelectionListener(panelMng.tabItemSelectListener);
	  return tabPanel;
	}

	
  
  
	@Override public PanelContent addGridPanel(String sName, String sLabel, int yGrid, int xGrid, int yGrid2, int xGrid2)
	{ TabItem tabItemOperation = new TabItem(tabMng.widgetSwt, SWT.None);
		tabItemOperation.setText(sLabel);
		CanvasStorePanelSwt panel;
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  if(yGrid <0 || xGrid <0){
			panel = new CanvasStorePanelSwt(tabMng.widgetSwt, 0, colorBackground);
		} else {
	  	panel = new GridPanelSwt(tabMng.widgetSwt, 0, colorBackground, mng.propertiesGui.xPixelUnit(), mng.propertiesGui.yPixelUnit(), 5, 5);
		}
		PanelContent panelContent = mng.registerPanel(sName, panel);
	  tabItemOperation.setControl(panel);
	  panel.setData(panelContent);
	  return panelContent;
  }

  
	@Override public PanelContent addCanvasPanel(String sName, String sLabel)
	{ TabItem tabItemOperation = new TabItem(tabMng.widgetSwt, SWT.None);
		tabItemOperation.setText(sLabel);
		Color colorBackground = mng.propertiesGuiSwt.colorSwt(0xeeeeee);
	  CanvasStorePanelSwt panel = new CanvasStorePanelSwt(tabMng.widgetSwt, 0, colorBackground);
	  PanelContent panelContent = mng.registerPanel(sName, panel);
	  tabItemOperation.setControl(panel);
	  panel.setData(panelContent);
	  return panelContent;
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
				if(data != null && notifyingUserInstanceWhileSelectingTab !=null){
					@SuppressWarnings("unchecked")
					PanelContent panelContent = (PanelContent)(data);
					List<WidgetDescriptor> widgetInfos = panelContent.widgetList; 
					notifyingUserInstanceWhileSelectingTab.panelActivatedGui(widgetInfos);
				}
			}
		}
  	
  };
  
  
  

	
}
