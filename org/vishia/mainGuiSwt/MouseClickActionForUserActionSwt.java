package org.vishia.mainGuiSwt;

import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;
//import org.vishia.mainGuiSwt.GuiPanelMngSwt.UserAction;

/**This action class supports the call of a user method while mouse-button.
 * 
 *
 */
public class MouseClickActionForUserActionSwt extends MouseClickInfo 
implements MouseListener
{
	private final PropertiesGuiSwt propertiesGui;
	
	/**Positions saved on mouse press, to detect whether the mouse-release occurs in the pressed area.
	 * If the mouse-position is shifted outside the area of the widget, the mouse-release-user-action
	 * is not executed.
	 */
	private int xMousePress, yMousePress;
	
	private Color backgroundWhilePressed;
	
  private boolean isPressed;
	
	/**Reference to the users method. */
  private UserActionGui userAction;
  
  private final String sCmdPress, sCmdRelease, sCmdDoubleClick;
  
  /**Constructor.
   * @param guiMng The Gui-manager
   * @param userCmdGui The users method for the action. 
   * @param sCmdPress command string provided as first parameter on mouse button press.
   * @param sCmdRelease
   * @param sCmdDoubleClick
   */
  public MouseClickActionForUserActionSwt(
  	GuiPanelMngSwt guiMng
  , UserActionGui userCmdGui
  , String sCmdPress
  , String sCmdRelease
  , String sCmdDoubleClick
  )
  { super(guiMng);
  	this.propertiesGui = guiMng.propertiesGuiSwt;
  	this.sCmdPress = sCmdPress;
    this.sCmdRelease = sCmdRelease;
    this.sCmdDoubleClick = sCmdDoubleClick;
    this.userAction = userCmdGui;
  }
  
  
  void setUserAction(UserActionGui userAction)
  {
  	this.userAction = userAction;
  }
  

	@Override
	public void mouseDoubleClick(MouseEvent e) {
    userAction.userActionGui(sCmdDoubleClick, null);
	}

	@Override
	public void mouseDown(MouseEvent e) {
    super.mouseDown(e);
		isPressed = true;
		xMousePress = e.x;
    yMousePress = e.y;
    Control widget = (Control) e.widget;  //a widget is a Control always.
    widget.addMouseMoveListener(mouseMoveListener);
    if(e.widget instanceof ButtonSwt){
      ButtonSwt button = (ButtonSwt)e.widget;
      button.setActivated(true);
    }
    else {
      backgroundWhilePressed = widget.getBackground();
      widget.setBackground(propertiesGui.colorSwt(0x800080));
    }
    if(sCmdPress != null){
    	@SuppressWarnings("unchecked")
    	WidgetDescriptor infos = (WidgetDescriptor)widget.getData();
      userAction.userActionGui(sCmdPress,infos);
    }
	}

	@Override
	public void mouseUp(MouseEvent e) {
    //set the background color to the originally value again if it was changed.
	  if(isPressed){
			Control widget = (Control)e.widget;
	  	widget.removeMouseMoveListener(mouseMoveListener);
	    isPressed = false;
	  	if(e.widget instanceof ButtonSwt){
        ButtonSwt button = (ButtonSwt)e.widget;
        button.setActivated(false);
      }
		  else if(backgroundWhilePressed != null){
	  		widget.setBackground(backgroundWhilePressed);
	  	}
	  	backgroundWhilePressed = null;
	  	//
			Object data = e.widget.getData();
			final String sNameWidget, sInfoWidget;
	    @SuppressWarnings("unchecked")
	  	WidgetDescriptor infos = (WidgetDescriptor)widget.getData();
			if(data instanceof WidgetDescriptor){
				@SuppressWarnings("unchecked")
				WidgetDescriptor descr = (WidgetDescriptor)data;
				sNameWidget = descr.name;
				sInfoWidget = descr.sDataPath;
			} else {
				sNameWidget = "unknown";
				sInfoWidget = null;
			}
			/*
	  	TreeMap<String, String> info = new TreeMap<String, String>();
	  	info.put("info", sInfoWidget);
	  	*/
	  	userAction.userActionGui(sCmdRelease, infos);
	  }
	}
	

	public void xxxmouseUp(MouseEvent e) {
    //Point size = e.
	  int xSize, ySize;
	  //set the background color to the originally value again if it was changed.
	  if(e.widget instanceof Control){
	  	Control widget = (Control)e.widget;
	  	widget.removeMouseMoveListener(mouseMoveListener);
	    Point size = widget.getSize();
	  	xSize = size.x; ySize = size.y;
	  	if(e.widget instanceof ButtonSwt){
        ButtonSwt button = (ButtonSwt)e.widget;
        button.setActivated(false);
      }
		  else if(backgroundWhilePressed != null){
	  		widget.setBackground(backgroundWhilePressed);
	  	}
	  } else {
	  	//if the Control isn't registered on widget, the size of the widget is unknown.
	  	//Therefore the mouse release outside of the widget area should be recognized with small movement of mouse.
	  	xSize = xMousePress + 10;
	  	ySize = yMousePress + 10;
		}
		backgroundWhilePressed = null;
  	//
		Object data = e.widget.getData();
		final String sNameWidget, sInfoWidget;
		final WidgetDescriptor descr;
		if(data instanceof WidgetDescriptor){
			descr = (WidgetDescriptor)data;
			sNameWidget = descr.name;
			sInfoWidget = descr.sDataPath;
		} else {
			sNameWidget = "unknown";
			sInfoWidget = null;
			descr = null;
		}
		int xMouse = e.x;
	  int yMouse = e.y;
	  if(  xMouse >=0 && xMouse < xSize  //check whether release is inside area of the widget, 
	  	&& yMouse >=0 && yMouse < ySize  //where pressed was occurred
	  	){
	  	/*
	  	TreeMap<String, String> info = new TreeMap<String, String>();
	  	info.put("info", sInfoWidget);
	  	*/
	  	userAction.userActionGui(sCmdRelease, descr);
	  }	else {
	  	//mouse release outside area:
	  }
	}
	


	private MouseMoveListener mouseMoveListener = new MouseMoveListener()
	{

		@Override	public void mouseMove(MouseEvent e)
		{
		  if(e.widget instanceof Control){
		  	Control widget = (Control)e.widget;
		  	Point size = widget.getSize();
		  	//xSize = size.x; ySize = size.y;
		  	if(  e.x < 0 || e.x > size.x
		  		|| e.y < 0 || e.y > size.y
		  	  ){
		      isPressed = false;
		      widget.removeMouseMoveListener(mouseMoveListener);
			    if(e.widget instanceof ButtonSwt){
		        ButtonSwt button = (ButtonSwt)e.widget;
		        button.setActivated(false);
		      }
		  	}
		  }	
		}//method mouseMove
	};
  

}
