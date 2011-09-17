package org.vishia.mainGuiSwt;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.vishia.gral.ifc.GralFileDialog_ifc;

public class FileDialogSwt implements GralFileDialog_ifc
{

	private FileDialog fileDialog;
	
	private DirectoryDialog dirDialog;

	private final Shell shell;
	
	private String sOpenResult;
	
	@Override public boolean open(String sTitle, int mode)
	{
    int modeSwt = 0;
    
    if((mode & GralFileDialog_ifc.multi)!=0) { modeSwt |= SWT.MULTI; }
    
    if((mode & GralFileDialog_ifc.directory)!=0) {
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
	  return true;
	}
	
	
	public FileDialogSwt(Shell shell)
	{ this.shell = shell;
	}
	

	/* (non-Javadoc)
	 * @see org.vishia.gral.FileDialogIfc#show(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override public String show(String sBaseDir, String sLocalDir, String sMask, String sTitle)
	{
    if(fileDialog !=null){
      if(sBaseDir !=null || sLocalDir !=null){
        String sStartDir = (sBaseDir !=null ? sBaseDir : "") + (sLocalDir !=null ? sLocalDir : "");
        fileDialog.setFileName(sStartDir); 
      }
      if(sTitle != null){
        fileDialog.setText(sTitle);
      }
      shell.setVisible(true);
      shell.setActive();
      sOpenResult = fileDialog.open();  //it is opened, and this thread waits.
      //String sDir = fileDialog.getFilterPath();
    } else {
      if(sTitle != null){
        dirDialog.setText(sTitle);
      }
      shell.setVisible(true);
      shell.setActive();
      sOpenResult = dirDialog.open();  //it is opened, and this thread waits.
      //String sDir = dirDialog.getFilterPath();
      
    }
		return sOpenResult;
	}


  @Override public String[] getMultiSelection()
  { return fileDialog.getFileNames();
  }


  @Override public String getSelection()
  { if(fileDialog !=null){
      if(sOpenResult == null){ return null; }    //aborted selection.
      else {
        String sPath = fileDialog.getFilterPath();
        String sName = fileDialog.getFileName();
        return sPath + "/" + sName;
      }
    } else {
      return sOpenResult;  //selected dir is the value from open()
    }
  }
	
	
	
}
