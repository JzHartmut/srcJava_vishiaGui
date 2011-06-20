package org.vishia.mainGuiSwt;

import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.GuiMngBase;
import org.vishia.gral.GuiPanelMngBase;
import org.vishia.gral.GuiShellMngBuildIfc;
import org.vishia.gral.GuiShellMngIfc;
import org.vishia.msgDispatch.LogMessage;

public class GuiShellMngSwt extends GuiPanelMngSwt implements GuiShellMngBuildIfc
{

  /**Set if any external event is set. Then the dispatcher shouldn't sleep after finishing dispatching. 
   * This is important if the external event occurs while the GUI is busy in the operation-system-dispatching loop.
   */
  private final AtomicBoolean extEventSet = new AtomicBoolean(false);

  private final Display guiDevice;  

  boolean bActiv;
  
	public GuiShellMngSwt(Shell graphicFrame, GuiPanelMngBase parent, int width, int height,
		PropertiesGuiSwt propertiesGui, VariableContainer_ifc variableContainer
  	, LogMessage log
	)
	{
		super(parent, graphicFrame, width, height, propertiesGui, variableContainer, log);
	  guiDevice = graphicFrame.getDisplay();
		graphicFrame.addShellListener(shellListener);
	}

	
	@Override public boolean dispatchDisplayAndSleep()
	{ while (guiDevice.readAndDispatch ()){
		}
		//checkTimes.calcTime();
		if(!extEventSet.get()) {
			guiDevice.sleep ();
		}
    return true;		
	}
	
	@Override public boolean dispatchDisplay()
	{ while (guiDevice.readAndDispatch ()){
		}
    return true;		
	}
	
	@Override
	public boolean wakeShell()
	{
		guiDevice.wake();
    return true;
	}
	
  /**This method is able to call by the user anywhere to set a dialog window visible. The dialog window may be existing
   * and holds data, but it isn't visible, if the dialog isn't activate. s
   */
  @Override public void setWindowVisible(boolean visible)
  { bActiv = visible;
    theShellOfWindow.setVisible( visible );
    if(visible){ 
    	theShellOfWindow.setFocus();
    	theShellOfWindow.setActive(); 
    }
  }
  
  
  @Override public boolean isWindowsVisible(){ return bActiv; }
  
	
	ShellListener shellListener = new ShellListener(){

		@Override
		public void shellActivated(ShellEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellClosed(ShellEvent e)
		{
			// TODO Auto-generated method stub
			stop();
			e.doit = false;
			bActiv = false;
			setWindowVisible(false);
		}

		@Override
		public void shellDeactivated(ShellEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellDeiconified(ShellEvent e)
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void shellIconified(ShellEvent e)
		{
			// TODO Auto-generated method stub
			
		}
		
	};
	

	void stop(){}


}
