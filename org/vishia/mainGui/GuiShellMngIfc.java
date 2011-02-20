package org.vishia.mainGui;

public interface GuiShellMngIfc extends GuiPanelMngWorkingIfc  //, GuiPanelMngBuildIfc
{

	/**The dispatch routine should be invoked in the display's thread, if the manager is the only one
	 * panel of the whole window and the dispatch routine is not called any other.
	 * The instance should be of type {@link org.vishia.mainGuiSwt.GuiShellMngSwt} or its
	 * swing-adequate. If the instance is not of such type, an IllegalInvocationException is thrown.
	 * @return
	 */
	boolean dispatchDisplayAndSleep();
	
	
	boolean dispatchDisplay();
	
	
	/**Controls whether the whole window, which contains this panel, should be visible or not.
	 * It is proper for such panels especially, which are the only one in a window. 
	 * If a window is setting visible with this method, it is arranged in the foreground.
	 * @param visible
	 * @return
	 */
	void setWindowVisible(boolean visible);
	
	boolean isWindowsVisible();

	boolean wakeShell();
	
}
