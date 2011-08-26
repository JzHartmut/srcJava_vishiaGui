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

  /**TODO only private local using here, it should be a part of DataProject.
   * 
   */
  List<File> listBzrDirs = new LinkedList<File>();
  
  final MainData mainData;
  
  final MainCmd_ifc mainCmdifc;
  
  /**The format of a timestamp in the log output. */
  private final DateFormat logDateFormat= new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
  
  
  private List<DataFile> listUnknownFiles = new LinkedList<DataFile>();
  
  public BzrGetStatus(MainCmd_ifc mainCmdifc, MainData mainData)
  { this.mainCmdifc = mainCmdifc;
    this.mainData = mainData;
  }



  /**Searches all locations of source-archives in the current project folder and all sub folders.
   * A project means a software project with some sources consisting of some Components with its own archives.
   * @calls {@link #captureStatus(DataCmpn)} for all found software-archives.
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
  void captureStatus(DataCmpn data)
  {
    //DataCmpn data = mainData.currPrj.data[ixData];    
    data.uBzrError.setLength(0);
    data.uBzrLastVersion.setLength(0);
    data.uBzrStatusOutput.setLength(0);
    mainData.cmdExec.setCurrentDir(data.fileBzrLocation);
    String sCmdStatus = mainData.cfg.indexCmds.get("status");
    String sCmdheadRevision = mainData.cfg.indexCmds.get("headRevision");
    mainData.cmdExec.execWait(sCmdStatus, null, data.uBzrStatusOutput, data.uBzrError);
    mainData.cmdExec.execWait(sCmdheadRevision, null, data.uBzrLastVersion, data.uBzrError);
    getVersionFromLogOutput(data.uBzrLastVersion, data, false);
    File fileBzrVersion = new File(data.fileBzrLocation, "_bzrVersion.txt");
    if(fileBzrVersion.exists()){ 
      String lastLogfile = FileSystem.readFile(fileBzrVersion);
      StringBuilder uLog = new StringBuilder(lastLogfile);
      getVersionFromLogOutput(uLog, data, true);
    }
  }
  
  
  /**Reads the status output and fills the {@link #listUnknownFiles},
   * {@link DataCmpn#listModifiedFiles}, {@link DataCmpn#listNewFiles}, 
   * {@link DataCmpn#listAddFiles}, {@link DataCmpn#listRemovedFiles}, 
   * 
   */
  void initListFiles()
  {
    DataCmpn data = mainData.currCmpn;
    StringBuilder uStatus = data.uBzrStatusOutput;
    String sLine;
    listUnknownFiles.clear();
    List<DataFile> listFiles = listUnknownFiles;
    String sType = "?";
    int posLine = 0, posLineEnd;
    int pos;
    data.listAddFiles = null;
    data.listModifiedFiles = null;
    data.listNewFiles = null;
    data.listRemovedFiles = null;
    data.listRenamedFiles = null;
    data.indexFiles.clear();
    
    while( (posLineEnd = uStatus.indexOf("\n", posLine))>=0){
      sLine = uStatus.substring(posLine, posLineEnd);
      if( (pos = sLine.indexOf("modified:"))>=0){
        listFiles = data.listModifiedFiles = new LinkedList<DataFile>();
        sType = "chg";
      } else if( (pos = sLine.indexOf("unknown:"))>=0){
        listFiles = data.listNewFiles = new LinkedList<DataFile>();
        sType = "new";
      } else if( (pos = sLine.indexOf("removed:"))>=0){
        listFiles = data.listRemovedFiles = new LinkedList<DataFile>();
        sType = "del";
      } else if( (pos = sLine.indexOf("added:"))>=0){
        listFiles = data.listAddFiles = new LinkedList<DataFile>();
        sType = "add";
      } else if( (pos = sLine.indexOf("renamed:"))>=0){
        listFiles = data.listRenamedFiles = new LinkedList<DataFile>();
        sType = "mov";
      } else {
        //line with a file path
        String sFilePath = sLine.trim();
        if(sFilePath.endsWith("*")){
          sFilePath = sFilePath.substring(0, sFilePath.length()-1).trim();
        }
        int posSep = sFilePath.indexOf("=>");
        if( posSep >=0){
          sFilePath = sFilePath.substring(posSep+2).trim();
        }
        sFilePath = sFilePath.replace('\\', '/');
        File file = new File(mainData.currCmpn.fileBzrLocation, sFilePath);
        DataFile fileData = new DataFile(file, sFilePath, sType);
        listFiles.add(fileData);
        data.indexFiles.put(sFilePath, fileData);
      }
      posLine = posLineEnd +1;    
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
          //int nrRev = Integer.parseInt(sRevision);
          if(sbox){ data.nrSboxRev = sRevision; } 
          else    { data.nrTopRev= sRevision; } 
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
