package org.vishia.mainGuiSwt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.vishia.mainGui.FileDialogIfc;

public class FileDialogSwt implements FileDialogIfc
{

	private final FileDialog fileDialog;

	private final Shell shell;
	
	public FileDialogSwt(Shell shell)
	{ this.shell = shell;
		this.fileDialog = new FileDialog(shell, SWT.OPEN | SWT.MULTI);
	}
	

	@Override public String open(String sStartFile, String sTitle)
	{
		fileDialog.setFileName(sStartFile);
		if(sTitle != null){
			fileDialog.setText(sTitle);
		}
		shell.setVisible(true);
		shell.setActive();
		fileDialog.open();  //it is opened, and this thread waits.
		String sDir = fileDialog.getFilterPath();
		String sFiles[] = fileDialog.getFileNames();
		String sFileLast = "";
		return sFileLast;
	}
	
	
	
}
