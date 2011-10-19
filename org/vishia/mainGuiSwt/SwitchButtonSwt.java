package org.vishia.mainGuiSwt;

import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

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
	
	//private final UserActionGui userActionOnSwitch;

	
	public SwitchButtonSwt(GuiPanelMngSwt mng, GralWidget widgd, char size)
	{
		super(mng, widgd, size);
		//this.userActionOnSwitch = null;
		this.isSwitchedDown = false;
		MouseClickSwitchButtonAction action = new MouseClickSwitchButtonAction(mng, null);
		addMouseListener( new MouseClickActionForUserActionSwt(mng, action, null, "SwitchButton", null));
  	
	}
	
	
	public void setColorPressed(int colorPressed)
	{
		this.colorPressed = mng.propertiesGuiSwt.colorSwt(colorPressed);
	}
	
	public void setColorReleased(int colorReleased)
	{
		this.colorReleased = mng.propertiesGuiSwt.colorSwt(colorReleased);
	}
	
	
	public boolean isOn(){ return isSwitchedDown; }
	
  
	public void setState(Object val)
	{
	  String sVal = (val instanceof String) ? (String)val : null;
	  int nVal = val instanceof Integer ? ((Integer)val).intValue(): -1;
		if(sVal !=null && (sVal.equals("1") || sVal.equals("true") || sVal.equals("on"))
		  || sVal == null && nVal !=0){
			isSwitchedDown = true;
			setBackground(colorPressed); 
		} else if(sVal !=null && (sVal.equals("0") || sVal.equals("false") || sVal.equals("off"))
		    || nVal == 0){
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
  implements GralUserAction
  {
		/**
		 * @param properties
		 * @param userAction //may be null.
		 */
		public MouseClickSwitchButtonAction(GuiPanelMngSwt guiMng, GralUserAction userAction)
		{
			
			super(guiMng, null, null, "switchButton", null);
			setUserAction(this);  //set this action primary, it calls the user action.
		}
  	
		@Override	public boolean userActionGui(String sCmd, GralWidget infos, Object... params)
		{
			isSwitchedDown = ! isSwitchedDown;
      if(isSwitchedDown){ setBackground(colorPressed); }
      else {  setBackground(colorReleased); }
			GralUserAction action = infos.getActionChange();
      if(action != null){
			  action.userActionGui(sCmd + (isSwitchedDown? "1" : "0"), infos);
			}
			return true;
		}
  }
  
  
  
	
	

}
