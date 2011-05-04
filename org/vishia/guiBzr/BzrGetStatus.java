package org.vishia.guiBzr;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.FileSystem;

public class BzrGetStatus
{

  List<File> listBzrDirs = new LinkedList<File>();
  
  static class Data{
    
    private File fileBzrLocation;
    
    StringBuilder uBzrStatusOutput = new StringBuilder();
    StringBuilder uBzrStatusError = new StringBuilder();
    
    public File getBzrLocationDir(){ return fileBzrLocation; }
    
  };
  
  Data[] data;
  
  final MainCmd_ifc mainCmdifc;
  
  
  
  public BzrGetStatus(MainCmd_ifc mainCmdifc)
  { this.mainCmdifc = mainCmdifc;
  }



  void getBzrLocations(String sProjectPath)
  {
    listBzrDirs.clear();
    try{ FileSystem.addFileToList(sProjectPath + "/**/.bzr", listBzrDirs);
    } catch(FileNotFoundException exc){ }
    int zCmpn = listBzrDirs.size();
    data = new Data[zCmpn];
    int ixCmpn = 0;
    for(File fileBzr: listBzrDirs){
      data[ixCmpn] = new Data();
      File bzrLocation = fileBzr.getParentFile();
      data[ixCmpn].fileBzrLocation = bzrLocation;
      ProcessBuilder cmdMng = new ProcessBuilder("");
      cmdMng.directory(bzrLocation);
      cmdMng.command("bzr status");
      mainCmdifc.executeCmdLine(cmdMng, "bzr status", null, Report.info, data[ixCmpn].uBzrStatusOutput, data[ixCmpn].uBzrStatusError);
      ixCmpn +=1;
    }
  }
  
}
