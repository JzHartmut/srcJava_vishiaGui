package org.vishia.guiBzr;

import java.io.File;

/**This class contains the data for one file in a Component and its counterpart in the source archive.
 * @author Hartmut Schorrig
 *
 */
public class DataFile
{
  /**The local path in the component, relative to the archive. */
  final String sLocalpath;
  
  /**The real file. */
  final File file;
  
  /**Timestamp in archive and of file. If 0 then the file isn't existing in the archive respectively as file.
   */
  long dateInArchive, dateFile;
  
  /**Type of file:
   * <ul>
   * <li>chg: modified, changed
   * <li>new: not archived yet
   * <li>add: added to archive, but not commited yet
   * <li>del: removed, not found
   * <li>mov: moved or renamed in archive
   * 
   */
  String sType;
  
  DataFile(File file, String sLocalpath, String sType)
  {
    this.file = file;
    this.sLocalpath = sLocalpath;
    this.sType = sType;
    dateFile = file.lastModified();
  }
}
