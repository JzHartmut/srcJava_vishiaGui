package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.gridPanel.GralGridMngBase;
import org.vishia.gral.ifc.GralRectangle;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

/**Universal Mouse Listener which works with the {@link GralWidget}.
 * <ul>
 * <li>Always sets {@link GralGridMngBase#setLastClickedWidgetInfo(GralWidget)}.
 * <li>Design mode: Left and right button for design in the {@link GralGridMngBase}
 *     using the {@link org.vishia.gral.cfg.GralCfgDesigner}.
 * <li>Normal mode: calls {@link GralWidget#getActionChange()}
 * </ul>      
 * @author Hartmut Schorrig
 *
 */
public class MouseClickInfo implements MouseListener
{

	protected final GralGridMngBase guiMng;
	
	public MouseClickInfo(GuiPanelMngSwt guiMng)
	{
		this.guiMng = guiMng;
	}

	int xDown, yDown;
	
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
		if(oInfo instanceof GralWidget){
			GralWidget widgetInfo = (GralWidget)oInfo;
      if(widgetInfo ==null || widgetInfo.sDataPath ==null || !widgetInfo.sDataPath.equals("widgetInfo")){
        guiMng.setLastClickedWidgetInfo(widgetInfo );
      }
			if(guiMng.bDesignMode){
			  GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
        if(ev.button == 1){ //left
          xDown = ev.x; yDown = ev.y;
  			  guiMng.pressedLeftMouseDownForDesign(widgetInfo, rr);  
			  } else if(ev.button == 3){ //right
			    //guiMng.pressedRightMouseDownForDesign(widgetInfo, rr);
			  }
			}
		}
		
	}

	/**The mouse up is left empty. It may be overridden by an derived class. */
	@Override public void mouseUp(MouseEvent ev)
	{
    Widget widget = ev.widget;
    Object oInfo = widget.getData();
    if(oInfo instanceof GralWidget){
      GralWidget widgd = (GralWidget)oInfo;
      int dx = ev.x - xDown, dy = ev.y - yDown;
      if(dx < 10 && dx > -10 && dy < 10 && dy > -10){
        GralUserAction action = widgd.getActionChange();
        if(action !=null){
          action.userActionGui("lu", widgd, null);
        }
      } else if(guiMng.bDesignMode && ev.button == 1){
        boolean bCopy = (ev.stateMask & org.eclipse.swt.SWT.CTRL) !=0;
        GralRectangle rr = new GralRectangle(ev.x, ev.y, 0, 0);
        guiMng.releaseLeftMouseForDesign(widgd, rr, bCopy);  
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
