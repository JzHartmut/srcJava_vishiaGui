package org.vishia.mainGuiSwt;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.mainGui.GuiPanelMngBase;
import org.vishia.mainGui.GuiRectangle;
import org.vishia.mainGui.WidgetDescriptor;

/**Universal Mouse Listener which works with the {@link WidgetDescriptor}.
 * <ul>
 * <li>Always sets {@link GuiPanelMngBase#setLastClickedWidgetInfo(WidgetDescriptor)}.
 * <li>Design mode: Left and right button for design in the {@link GuiPanelMngBase}
 *     using the {@link org.vishia.mainGui.cfg.GuiCfgDesigner}.
 * <li>Normal mode: calls {@link WidgetDescriptor#getActionChange()}
 * </ul>      
 * @author Hartmut Schorrig
 *
 */
public class MouseClickInfo implements MouseListener
{

	protected final GuiPanelMngBase guiMng;
	
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
		if(oInfo instanceof WidgetDescriptor){
			WidgetDescriptor widgetInfo = (WidgetDescriptor)oInfo;
      if(widgetInfo ==null || widgetInfo.sDataPath ==null || !widgetInfo.sDataPath.equals("widgetInfo")){
        guiMng.setLastClickedWidgetInfo(widgetInfo );
      }
			if(guiMng.bDesignMode){
			  GuiRectangle rr = new GuiRectangle(ev.x, ev.y, 0, 0);
        if(ev.button == 1){ //left
  			  guiMng.pressedLeftMouseDownForDesign(widgetInfo, rr);  
			  } else if(ev.button == 3){ //right
			    guiMng.pressedRightMouseDownForDesign(widgetInfo, rr);
			  }
			}
		}
		
	}

	/**The mouse up is left empty. It may be overridden by an derived class. */
	@Override public void mouseUp(MouseEvent ev)
	{
    Widget widget = ev.widget;
    Object oInfo = widget.getData();
    if(oInfo instanceof WidgetDescriptor){
      WidgetDescriptor widgetInfo = (WidgetDescriptor)oInfo;
      if(guiMng.bDesignMode){
        boolean bCopy = (ev.stateMask & org.eclipse.swt.SWT.CTRL) !=0;
        GuiRectangle rr = new GuiRectangle(ev.x, ev.y, 0, 0);
        guiMng.releaseLeftMouseForDesign(widgetInfo, rr, bCopy);  
      }
    }
	  
	}
	
	
	private MouseMoveListener mouseMoveListenerDesignMode = new MouseMoveListener()
	{
	  
	  @Override public void mouseMove(MouseEvent e)
	  {
	    //xMouse = e.x;
	    //yMouse = e.y;
	  }//method mouseMove
	};
	  

	
}
