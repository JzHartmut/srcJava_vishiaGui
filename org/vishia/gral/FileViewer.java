package org.vishia.mainGui;

import java.util.Map;


/**This is an experience only yet. It should show a file manager in an own window
 * of the same application. The extra window {@link org.vishia.mainGuiSwt.GuiShellMngSwt} works, 
 * but it hasn't the focus in any situation. This may be a problem of MS-Windows.
 * In a normal PC application, there is a Task manager etc. where the window can be force
 * in foreground, but for an controller application without access to the MS-Windows-system,
 * it is not able to use. Therefore this application of the {@link org.vishia.mainGuiSwt.GuiShellMngSwt}
 * is not used for the current project.
 * 
 * @author Hartmut Schorrig
 *
 */
public class FileViewer
{
	private final GuiPanelMngBuildIfc guiMng;
	boolean bActiv;
	boolean bActivate;
	
	String sDirDst = "D:/UserData/";
	
	GuiShellMngBuildIfc shell;
	
	public FileViewer(GuiPanelMngBuildIfc guiMng)
	{
		this.guiMng = guiMng;
		
		fileDialogThread.start();
		
	}
	
	private UserActionGui actionFileButton = new UserActionGui(){

		@Override
		public void userActionGui(String sCmd, WidgetDescriptor infos, Object... params)
		{
		  bActiv = true;
		  bActivate = true;
		  shell.wakeShell();
		  
			//fileDialog.open();
			
		}
		
	};
	

	private Thread fileDialogThread = new Thread("fileDialog"){
		
		@Override public void run()
		{
			//NOTE: The origin of the coordinates is the (0,0) of the whole window.
			//GuiPanelMngBase<?>
			shell = guiMng.createWindow(12, 90, 6, 15, null);
			shell.setWindowVisible(false);  //it isn't visible, until the file dialog is activated.
      
			//some buttons to view, delete, select
			shell.setPosition(2, 20, 4, 15, 'd');
			shell.addButton("selStart", null, null, null, null, "select-start");
			shell.addButton("selLast", null, null, null, null, "select-last");
			shell.addButton("selLast", null, null, null, null, "select-last");
			shell.addButton("selLast", null, null, null, null, "select-last");
			shell.addButton("selLast", null, null, null, null, "select-last");
			
			//The files of only one directory should be shown in a list.
			//The user should not be able to select another directory.
			//It is for end-user- human-machine-interface, not for the PC-specialist.
			
			while(true){
				if(bActiv){
					if(bActivate){
						shell.setWindowVisible(false);
						shell.dispatchDisplay();  //dispatch the inactiv setting
						try { sleep(3000);} catch (InterruptedException exc) {}
						shell.setWindowVisible(true);
						bActivate = false;	
					}
					shell.dispatchDisplayAndSleep();
					bActiv = shell.isWindowsVisible();
					if(!bActiv){
						shell.setWindowVisible(false);
						shell.dispatchDisplay();  //dispatch the inactiv setting
					}
					//shell.setWindowVisible(bActiv);
					//bActiv = false;
				} else {
					try { sleep(100);} catch (InterruptedException exc) {}
				}
			}
		}
	};
	
	
	
	
	public UserActionGui getAction(){ return actionFileButton; }
	
	
	

}
