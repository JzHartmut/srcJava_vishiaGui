package org.vishia.guiBzr;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**This class contains all data from the Source-Archive of one component. 
 * @author Hartmut Schorrig
 *
 */
public class DataCmpn
{
  
  static class Revision{
    /**The version number String. */
    String nr;
    
    /**The timestamp of the version. */
    long date;
  }
  
  /**Name of the component. It is the directory name of the directory 
   * where the archive is contained in. */
  final String sNameCmpn;
  
  /**Directory where the .bzr of the working tree is contained. */
  final File dirWorkingtree;

  /**Directory where the .bzr of the archive (without working tree) is contained. */
  final File dirArchive;

  /**Directory where the .bzr of any remote .bzr is contained. */
  final File dirRemoteArchive;

  /**The version number form the last commit or revert in this sandbox 
   * and the top version number in the branch. If the branch was updated in another Sandbox,
   * it may have a higher number. If the sandbox isn't actual, it may be a older number.
   * <ul>
   * <li>The sandbox version is stored either in the file _BzrVersion.txt, which is the output
   * from a invocation "bzr log -l 1" after last commit or revert.
   * <li>Or the sandbox version is stored in the bzr data file of the project.
   * </ul>  
   */
  final Revision revisionSbox = new Revision(), revisionWorkingTreeTop = new Revision(), revisionArchive = new Revision(), revisionRemoteArchive = new Revision();

  /**Output of bzr status invocation of the working box. */
  StringBuilder uBzrStatusOutput = new StringBuilder();
  /**Output of bzr log -l 1 to get the last version number. */
  StringBuilder uBzrLastVersion = new StringBuilder();
  /**General error output of all commands. */
  StringBuilder uBzrError = new StringBuilder();
  
  List<DataFile> listModifiedFiles;
  
  List<DataFile> listRemovedFiles;
  
  List<DataFile> listNewFiles;
  
  List<DataFile> listAddFiles;
  
  List<DataFile> listRenamedFiles;
  
  final Map<String, DataFile> indexFiles = new TreeMap<String, DataFile>();
  
  public File getBzrLocationDir(){ return dirWorkingtree; }
  

  DataCmpn(File dirComponent, File dirArchive, File dirRemoteArchive)
  {
    this.dirWorkingtree = dirComponent;
    this.dirArchive = dirArchive;
    this.dirRemoteArchive = dirRemoteArchive;
    this.sNameCmpn = dirComponent.getName();
      
  }
  
}
