package org.vishia.gral.swt;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public class SwtFocusAction
implements FocusListener
{

	/**Reference to the users method. */
  private GralUserAction userAction;
  
	protected final SwtWidgetMng guiMng;

	String sCmdEnter, sCmdRelease;
	
	private final SwtProperties propertiesGui;
  
  /**Constructor.
   * @param guiMng The Gui-manager
   * @param userCmdGui The users method for the action. 
   * @param sCmdPress command string provided as first parameter on mouse button press.
   * @param sCmdRelease
   * @param sCmdDoubleClick
   */
  public SwtFocusAction(
  	SwtWidgetMng guiMng
  , GralUserAction userCmdGui
  , String sCmdEnter
  , String sCmdRelease
  )
  { this.guiMng = guiMng;
  	this.propertiesGui = guiMng.propertiesGuiSwt;
  	this.sCmdEnter = sCmdEnter;
    this.sCmdRelease = sCmdRelease;
    this.userAction = userCmdGui;
  }
  
  
  void setUserAction(GralUserAction userAction)
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
		final GralWidget widgetInfo;
		final String sContent;
		if(widget instanceof Text){ sContent = ((Text)widget).getText(); }
		else { sContent = null; }
		if(oInfo instanceof GralWidget){
			widgetInfo = (GralWidget)oInfo;
		} else { widgetInfo = null; }
  	userAction.userActionGui(sCmd, widgetInfo, sContent);
	}


}
