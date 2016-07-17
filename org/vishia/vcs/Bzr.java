package org.vishia.vcs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.vishia.util.DataAccess;
import org.vishia.util.FileSystem;
import org.vishia.util.StringPartScan;

public class Bzr
{
  /**Searches the repository starting from startDir in outer direction.
   * If the startDir contains a ".bzr" directory, it is the repository.
   * If any parent dir contains it, bzrdir will be set to the parent dir.
   * If startDir or any parent contains a file ".bzr.bat" and does not contain a ".bzr" dir,
   * that file is read in to find out where the repository is located.
   * It is the line which contains "bzr_mvExpl.bat BZRDIR". 
   * The BZRDIR should be relative to a system width defined location of all repositories.
   * This routine does not deal with them, only returns the relativ path.
   * 
   * 
   * @param startDir The start directory where to find the ".bzr"
   * @param dst A map which contains variables, the result will be put into.
   * @param bzrdir Name of the variable for dst to put the absolute path of the ".bzr" or ".bzr.bat"-location
   * @param bzrsrc Name of the variable for dst to put the relativ ".bzr" location find out in the ".bzr.bat".
   *   This variable is put with "$" if a ".bzr" is found in bzrdir.
   * @return null if success, an error message if ".bzr" or ".bzr.bat" was not found.
   * @throws IOException on any unexpected exception.
   * @throws IllegalAccessException 
   */
  public static String searchRepository(File startDir, Map<String, DataAccess.Variable<Object>> dst, String bzrdir, String bzrsrc) 
  throws IOException, IllegalAccessException
  { File fBzr;
    File currDir = startDir.isDirectory() ? startDir : startDir.getParentFile();
    String ret = null;
    //search whether a .bzr or .bzr.bat exists and change to parent dir till it is found.
    do{
      fBzr = new File(currDir, ".bzr.bat");
      if(!fBzr.exists()){
        fBzr = new File(currDir, "_bzr.bat");
        if(!fBzr.exists()){
          fBzr = new File(currDir, ".bzr");
          if(!fBzr.exists()){
            try{
              currDir = FileSystem.getDirectory(currDir);  //NOTE: currDir.getParent() is not successfully on relative dir "."
            } catch(FileNotFoundException exc){ currDir = null;}
          }
        }
      }
    } while(!fBzr.exists() && currDir !=null);
    if(currDir ==null){
      throw new IOException("Bzr.searchRepository - .bzr... not found ;" + startDir.getAbsolutePath());
    } else {
      String sBzrDir = FileSystem.getCanonicalPath(currDir);
      DataAccess.createOrReplaceVariable(dst, bzrdir, 'S', sBzrDir, true);
      //dst.put(bzrdir, sBzrDir);
      if(!fBzr.getName().equals(".bzr")){   //one of the batch files found
        ret = FileSystem.readFile(fBzr);
        String sLine = FileSystem.grep1line(fBzr, "bzr_mvExpl.bat");
        if(sLine !=null){
          int pos = sLine.indexOf("bzr_mvExpl.bat");
          String sBzrSrc = sLine.substring(pos + 15).trim();
          DataAccess.createOrReplaceVariable(dst, bzrsrc, 'S', sBzrSrc, true);
          //dst.put(bzrsrc, sBzrSrc);
        }
        if(sLine == null) {
          throw new IOException("Bzr.searchRepository - .bzr.bat found but does not contain \"bzr_mvExpl.bat\"");
        }
      } else {
        dst.put(bzrsrc, null);  
      }
    }
    return ret;
  }
}
