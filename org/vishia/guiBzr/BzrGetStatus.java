package org.vishia.guiBzr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.util.FileSystem;

public class BzrGetStatus
{

  List<File> listBzrDirs = new LinkedList<File>();
  
  final MainData mainData;
  
  final MainCmd_ifc mainCmdifc;
  
  /**The format of a timestamp in the log output. */
  private final DateFormat logDateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  
  
  public BzrGetStatus(MainCmd_ifc mainCmdifc, MainData mainData)
  { this.mainCmdifc = mainCmdifc;
    this.mainData = mainData;
  }



  /**Searches all locations of source-archives in the current project folder and all sub folders.
   * A project means a software project with some sources consisting of some Components with its own archives.
   * @calls {@link #captureStatus(int)} for all found software-archives.
   * @param sProjectPath Path to the project folder.
   * 
   */
  void getBzrLocations(String sProjectPath)
  {
    listBzrDirs.clear();
    try{ FileSystem.addFileToList(sProjectPath + "/**/.bzr", listBzrDirs);
    } catch(FileNotFoundException exc){ }
    int zCmpn = listBzrDirs.size();
    mainData.currPrj.init(zCmpn);
    //int ixCmpn = 0;
    for(File fileBzr: listBzrDirs){
      File bzrLocation = fileBzr.getParentFile();
      int ixCmpn = mainData.currPrj.createComponentsData(bzrLocation);
      captureStatus(ixCmpn);
      ixCmpn +=1;
    }
  }
  
  
  
  /**Captures the status of a software archive of one component in comparison of its real source files.
   * <ul>
   * <li>last revision of files, last revision in archive, number and timestamp
   * <li>get status
   * </ul>
   * It fills the indexed instance of {@link DataCmpn}
   * @param ixData The index of the component, index in {@link #data}
   */
  void captureStatus(int ixData)
  {
    DataCmpn data = mainData.currPrj.data[ixData];    
    mainData.cmdMng.directory(data.fileBzrLocation);      
    mainCmdifc.executeCmdLine(mainData.cmdMng, "bzr status", null, Report.info, data.uBzrStatusOutput, data.uBzrError);
    mainCmdifc.executeCmdLine(mainData.cmdMng, "bzr log -l 1", null, Report.info, data.uBzrLastVersion, data.uBzrError);
    getVersionFromLogOutput(data.uBzrLastVersion, data, false);
    File fileBzrVersion = new File(data.fileBzrLocation, "_bzrVersion.txt");
    if(fileBzrVersion.exists()){ 
      String lastLogfile = FileSystem.readFile(fileBzrVersion);
      StringBuilder uLog = new StringBuilder(lastLogfile);
      getVersionFromLogOutput(uLog, data, true);
    }
  }
  
  
  
  
  void getVersionFromLogOutput(StringBuilder uLog, DataCmpn data, boolean sbox)
  {
    
    try{ 
      String sLine;
      int posLine = 0, posLineEnd;
      int pos;
      boolean bRevnrOk = false, bRevDateOk = false;
      while( (!bRevnrOk || !bRevDateOk) && (posLineEnd = uLog.indexOf("\n", posLine))>=0){
        sLine = uLog.substring(posLine, posLineEnd);
        if( (pos = sLine.indexOf("revno:"))>=0){
          //line: revno: <#?revision>
          String sRevision = sLine.substring(pos + 6).trim();
          int nrRev = Integer.parseInt(sRevision);
          if(sbox){ data.nrSboxRev = nrRev; } 
          else    { data.nrTopRev= nrRev; } 
          bRevnrOk = true;
        } else if( (pos = sLine.indexOf("timestamp:"))>=0){
          //line: timestamp: <timestamp>
          String sDate = sLine.substring(pos + 15, pos+34).trim();
          long dateVersion = logDateFormat.parse(sDate).getTime();
          if(sbox){ data.dateSboxRevSbox = dateVersion; } 
          else    { data.dateTopRev = dateVersion; } 
          bRevDateOk = true;
        }
        posLine = posLineEnd +1;    
      }
    } catch(Exception exc){ throw new RuntimeException(exc); }
  }
}
