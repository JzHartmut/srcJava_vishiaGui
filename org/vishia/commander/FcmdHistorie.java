package org.vishia.commander;

/**This file is only used to store the version historie of the package and tool Fcmd.
 * @author Hartmut Schorrig
 *
 */
public class FcmdHistorie
{

  
  /**
   * <ul>
   * <li>2017-09-15: 
   * <ul><li>_make/makejar_Fcmd.bat: GitGui is part of jar. Location of srcJava_vishiaBase and zbnf.jar now  ..\..\..\ZBNF\
   *   <li>{@link FcmdFavorPathSelector#actionCleanFileRemote}: used for command "fol&Der/&Clean dirtree" to remove all instances of FileRemote in a partial tree. 
   *     It should help on 'delete' file command. Yet 'delete' deletes the physical file but not the FileRemote instance in the FileCluster. yet still TODO: delete FileRemote instance with physical file.
   *   <li>FcmdSettings: remove the old cmd buttons. Only the jzcmd-using files are present. 
   *   <li>FcmdStatusLine: Show parent id, to detect double instances of FileRemote.
   * </ul>
   * </ul>
   * 
   */
  public static final String version = "2017-09-15";

}
