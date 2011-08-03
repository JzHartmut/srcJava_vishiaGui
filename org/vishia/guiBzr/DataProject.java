package org.vishia.guiBzr;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.ifc.UserActionGui;

/**This class contains or refers all data of a project.
 * A project is a software project which contains one or more archives of sources.
 * Each source-file-bundle with its archive presents a component and 
 * it is arranged in a sub directory of the project folder.
 * 
 * @author Hartmut Schorrig
 *
 */
public class DataProject
{

  String sPrjPath;
  
  /**The project path in the current file system. */
  File filePrjPath;
  
  /**All components in this project. The key is the local path from the project folder
   * to the folder, where the source archive (.bzr or .git) is arranged.
   * The key is the same like {@link DataCmpn#sNameCmpn}.
   */
  private Map<String, DataCmpn> indexCmpn = new TreeMap<String, DataCmpn>();
  
  /**All source components in this project. It will be ascertained while [refresh] button is pressed
   * on the select panel. See {@link GuiStatusPanel#refreshProjectBzrComponents}
   */
  DataCmpn[] data;
  
  /**ix while initialize the data. At last number of data -1 (last index). */
  private int ixDataInit;

  DataProject(String sPrjPath)
  { this.sPrjPath = sPrjPath;
    this.filePrjPath = new File(sPrjPath);
    assert(filePrjPath.exists() && filePrjPath.isDirectory());
  }
  
  /**Initializes newly. It is called on refresh.
   * @param nrofSwArchives
   */
  void init(int nrofSwArchives)
  {
    data = new DataCmpn[nrofSwArchives];
    indexCmpn.clear();
    ixDataInit = -1;
  }
  
  /**Creates one component.
   * @param dirComponent The folder where the source archive (.bzr, .git file) is found.
   * @return index of current component in {@link #data}
   */
  int createComponentsData(File dirComponent)
  {
    DataCmpn data1 = new DataCmpn(dirComponent);
    indexCmpn.put(data1.sNameCmpn, data1);
    data[++ixDataInit] = data1;
    return ixDataInit;
  }
  
  
  
  /**Searches a component.
   * @param sName The local folder path inside the software project to the components folder.
   * @return null if it isn't found.
   */
  DataCmpn selectComponent(String sName)
  {
    DataCmpn data = indexCmpn.get(sName);
    return data;
  }

}
