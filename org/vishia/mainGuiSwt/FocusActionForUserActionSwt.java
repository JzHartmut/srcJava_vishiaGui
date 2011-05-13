package org.vishia.mainGuiSwt;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;
//import org.vishia.mainGuiSwt.GuiPanelMngSwt.UserAction;

public class FocusActionForUserActionSwt
implements FocusListener
{

	/**Reference to the users method. */
  private UserActionGui userAction;
  
	protected final GuiPanelMngSwt guiMng;

	String sCmdEnter, sCmdRelease;
	
	private final PropertiesGuiSwt propertiesGui;
  
  /**Constructor.
   * @param guiMng The Gui-manager
   * @param userCmdGui The users method for the action. 
   * @param sCmdPress command string provided as first parameter on mouse button press.
   * @param sCmdRelease
   * @param sCmdDoubleClick
   */
  public FocusActionForUserActionSwt(
  	GuiPanelMngSwt guiMng
  , UserActionGui userCmdGui
  , String sCmdEnter
  , String sCmdRelease
  )
  { this.guiMng = guiMng;
  	this.propertiesGui = guiMng.propertiesGuiSwt;
  	this.sCmdEnter = sCmdEnter;
    this.sCmdRelease = sCmdRelease;
    this.userAction = userCmdGui;
  }
  
  
  void setUserAction(UserActionGui userAction)
  {
  	this.userAction = userAction;
  }
  


	
	
	@Override public void focusGained(FocusEvent ev)
	{ if(sCmdEnter !=null) { exec(ev, sCmdEnter); }
	}

	@Override public void focusLost(FocusEvent ev)
	{ if(sCmdRelease !=null) { exec(ev, sCmdRelease); }
	}

	
	private void exec(FocusEvent ev, String sCmd)
	{
		final Widget widget = ev.widget;
		final Object oInfo = widget.getData();
		final WidgetDescriptor widgetInfo;
		final String sContent;
		if(widget instanceof Text){ sContent = ((Text)widget).getText(); }
		else { sContent = null; }
		if(oInfo instanceof WidgetDescriptor){
			widgetInfo = (WidgetDescriptor)oInfo;
		} else { widgetInfo = null; }
  	userAction.userActionGui(sCmd, widgetInfo, sContent);
	}


}
