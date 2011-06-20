package org.vishia.mainGuiSwt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.FileDialogIfc;

public class FileDialogSwt implements FileDialogIfc
{

	private final FileDialog fileDialog;
	
	private final DirectoryDialog dirDialog;

	private final Shell shell;
	
	private String sDir;
	
	
	public FileDialogSwt(Shell shell, String sTitle, int mode)
	{ this.shell = shell;
    int modeSwt = 0;
	  
	  if((mode & FileDialogIfc.multi)!=0) { modeSwt |= SWT.MULTI; }
	  
	  if((mode & FileDialogIfc.directory)!=0) {
	    this.dirDialog = new DirectoryDialog(shell, SWT.MULTI);
	    if(sTitle != null){
	      dirDialog.setText(sTitle);
	    }
	    this.fileDialog = null;
  	} else {
  	  this.dirDialog = null;
      this.fileDialog = new FileDialog(shell, SWT.OPEN | modeSwt);
      if(sTitle != null){
        fileDialog.setText(sTitle);
      }
  	}
	}
	

	@Override public String show(String startDirMask, String sTitle)
	{
    int posSep = startDirMask.indexOf(':', 2); //After windows drive designation 'C:'
    if(fileDialog !=null){
      if(posSep >=2){
        fileDialog.setFileName(startDirMask.substring(0, posSep)); 
      }

      if(sTitle != null){
        fileDialog.setText(sTitle);
      }
      shell.setVisible(true);
      shell.setActive();
      sDir = fileDialog.open();  //it is opened, and this thread waits.
      //String sDir = fileDialog.getFilterPath();
    } else {
      if(sTitle != null){
        dirDialog.setText(sTitle);
      }
      shell.setVisible(true);
      shell.setActive();
      sDir = dirDialog.open();  //it is opened, and this thread waits.
      //String sDir = dirDialog.getFilterPath();
      
    }
		return sDir;
	}


  @Override public String[] getMultiSelection()
  { return fileDialog.getFileNames();
  }


  @Override public String getSelection()
  { return fileDialog !=null ? fileDialog.getFileName() : sDir;
  }
	
	
	
}
