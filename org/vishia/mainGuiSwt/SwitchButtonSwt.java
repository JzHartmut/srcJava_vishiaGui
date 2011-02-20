package org.vishia.mainGuiSwt;

import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

/**This class presents a button, which is shown as pressed and non-pressed. 
 * Two different colors are used. If the state is changed, a user event will be invoked if given.
 * The state is able to get.
 * @author Hartmut Schorrig
 *
 */
public class SwitchButtonSwt extends ButtonSwt
{

	
	Color colorPressed, colorReleased;
	
	private boolean isSwitchedDown;
	
	private final UserActionGui userActionOnSwitch;

	
	public SwitchButtonSwt(GuiPanelMngSwt mng, char size)
	{
		super(mng, size);
		this.userActionOnSwitch = null;
		this.isSwitchedDown = false;
		MouseClickSwitchButtonAction action = new MouseClickSwitchButtonAction(mng, null);
		addMouseListener( new MouseClickActionForUserActionSwt(mng, action, null, "SwitchButton", null));
  	
	}
	
	
	public void setColorPressed(int colorPressed)
	{
		this.colorPressed = mng.propertiesGui.color(colorPressed);
	}
	
	public void setColorReleased(int colorReleased)
	{
		this.colorReleased = mng.propertiesGui.color(colorReleased);
	}
	
	
	public boolean isOn(){ return isSwitchedDown; }
	
  
	public void setState(String val)
	{
		if(val.equals("1") || val.equals("true") || val.equals("on")){
			isSwitchedDown = true;
			setBackground(colorPressed); 
		} else if(val.equals("0") || val.equals("false") || val.equals("off")){
			isSwitchedDown = false;
			setBackground(colorReleased); 
		}
	}
	
	
  /**The mouse action for a switch button is adequate to the MouseClickActionForUserActionSwt
   * for all GUI-widgets. But the called action isn't the user action immediately,
   * but the here implemented switch action. this calls the given (or not given) user-action.
   * It is a inner non-static class of the button to influence the button with color.
   */
  private class MouseClickSwitchButtonAction extends MouseClickActionForUserActionSwt
  implements UserActionGui
  {
		/**
		 * @param properties
		 * @param userAction //may be null.
		 */
		public MouseClickSwitchButtonAction(GuiPanelMngSwt guiMng, UserActionGui userAction)
		{
			
			super(guiMng, null, null, "switchButton", null);
			setUserAction(this);  //set this action primary, it calls the user action.
		}
  	
		@Override	public void userActionGui(String sCmd, WidgetDescriptor<?> infos, Object... params)
		{
			isSwitchedDown = ! isSwitchedDown;
      if(isSwitchedDown){ setBackground(colorPressed); }
      else {  setBackground(colorReleased); }
			if(userActionOnSwitch != null){
				userActionOnSwitch.userActionGui(sCmd + (isSwitchedDown? "1" : "0"), infos);
			}
			
		}
  }
  
  
  
	
	

}
