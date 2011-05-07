package org.vishia.guiBzr;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

/**This class contains or refers all data of a project.
 * A project is a software project which contains one or more archives of sources.
 * Each source-file-bundle with its archive presents a component.
 * @author Hartmut Schorrig
 *
 */
public class DataProject
{

  String sPrjPath;
  
  File filePrjPath;
  
  Map<String, DataCmpn> indexCmpn = new TreeMap<String, DataCmpn>();
  
  DataCmpn[] data;
  
  private int ixDataInit;

  DataProject(String sPrjPath)
  { this.sPrjPath = sPrjPath;
    this.filePrjPath = new File(sPrjPath);
    assert(filePrjPath.exists() && filePrjPath.isDirectory());
  }
  
  void init(int nrofSwArchives)
  {
    data = new DataCmpn[nrofSwArchives];
    indexCmpn.clear();
    ixDataInit = -1;
  }
  
  int createComponentsData(File dirComponent)
  {
    DataCmpn data1 = new DataCmpn(dirComponent);
    indexCmpn.put(data1.sNameCmpn, data1);
    data[++ixDataInit] = data1;
    return ixDataInit;
  }
  
  
  
  DataCmpn selectComponent(String sName)
  {
    DataCmpn data = indexCmpn.get(sName);
    return data;
  }

}
