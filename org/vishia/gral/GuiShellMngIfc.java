package org.vishia.mainGui;

public interface GuiShellMngIfc extends GuiWindowMng_ifc, GuiPanelMngWorkingIfc  //, GuiPanelMngBuildIfc
{

	/**The dispatch routine should be invoked in the display's thread, if the manager is the only one
	 * panel of the whole window and the dispatch routine is not called any other.
	 * The instance should be of type {@link org.vishia.mainGuiSwt.GuiShellMngSwt} or its
	 * swing-adequate. If the instance is not of such type, an IllegalInvocationException is thrown.
	 * @return
	 */
	boolean dispatchDisplayAndSleep();
	
	
	boolean dispatchDisplay();
	
	boolean wakeShell();
	
}
