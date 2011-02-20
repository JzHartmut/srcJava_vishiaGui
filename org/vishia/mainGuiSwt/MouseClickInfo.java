package org.vishia.mainGuiSwt;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Widget;
import org.vishia.mainGui.WidgetDescriptor;

public class MouseClickInfo implements MouseListener
{

	protected final GuiPanelMngSwt guiMng;
	
	
	
	public MouseClickInfo(GuiPanelMngSwt guiMng)
	{
		this.guiMng = guiMng;
	}

	/**The mouse doubleclick is left empty. It may be overridden by an derived class. */
	@Override public void mouseDoubleClick(MouseEvent arg0)
	{}

	/**The mouse-down action save some informations about the widget.
	 * It may be overridden by an derived class, then this method should be invoked within.
	 */
	@Override public void mouseDown(MouseEvent ev)
	{
		Widget widget = ev.widget;
		Object oInfo = widget.getData();
		if(oInfo instanceof WidgetDescriptor<?>){
			WidgetDescriptor<?> widgetInfo = (WidgetDescriptor<?>)oInfo;
			if(widgetInfo ==null || widgetInfo.sDataPath ==null || !widgetInfo.sDataPath.equals("widgetInfo")){
			  guiMng.setLastClickedWidgetInfo(widgetInfo );
			}
		}
		
	}

	/**The mouse up is left empty. It may be overridden by an derived class. */
	@Override public void mouseUp(MouseEvent arg0)
	{}
	
}
