package org.vishia.gral.ifc;


public interface GralFileDialog_ifc
{
  /**Version, history and license.
   * <ul>
   * <li>2011-06-00 Hartmut created
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120303;

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
