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
  
  DataFile(File file, String sLocalpath)
  {
    this.file = file;
    this.sLocalpath = sLocalpath;
    dateFile = file.lastModified();
  }
}
