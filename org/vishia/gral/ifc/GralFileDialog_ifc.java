package org.vishia.gral.ifc;


public interface GralFileDialog_ifc
{
  /**Creates a dialog to select directories, not files. It is used while creation of the FileDialog. */
  static final int directory = 1;
  /**Assume selecting of more as one files or directories. It is used while creation of the FileDialog or while activating. */
  static final int multi = 2;
  
  boolean open(String sTitle, int mode);
  
  /**Shows the file dialog. 
	 * @param sBaseDir Part of start path, which should not be leave in selection. May be null
	 * @param sLocalDir Part of start path which is shown as default. May be null.
	 *                  If both sBaseDir and sLocalDir are null, the dialog may start with the last saved base dir.
	 * @param sMask If not null, then mask for file selection. If null, all files will be shown.
	 * @param sTitle The title in the dialog's title. If null, then the title is unchanged.
	 * @return null if the selection was aborted.
	 */
	String show(String sBaseDir, String sLocalDir, String sMask, String sTitle);
	
	String[] getMultiSelection();
	
	/**Returns the last selected local path and file.
	 * @return null if the last selection was aborted.
	 */
	String getSelection();

}
