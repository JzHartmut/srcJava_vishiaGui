package org.vishia.mainGui;

import java.io.File;

public interface FileDialogIfc
{
  /**Creates a dialog to select directories, not files. It is used while creation of the FileDialog. */
  static final int directory = 1;
  /**Assume selecting of more as one files or directories. It is used while creation of the FileDialog or while activating. */
  static final int multi = 2;
  
	String show(String startDirMask, String sTitle);
	
	String[] getMultiSelection();
	
	String getSelection();

}
