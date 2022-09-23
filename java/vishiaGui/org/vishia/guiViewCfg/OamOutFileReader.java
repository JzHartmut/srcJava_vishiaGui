package org.vishia.guiViewCfg;

import java.io.File;

import org.vishia.byteData.ByteDataSymbolicAccessReadConfig;
import org.vishia.byteData.RawDataAccess;
import org.vishia.mainCmd.Report;
import org.vishia.util.FileSystem;


public class OamOutFileReader 
{
  /**Index (fast access) of all variable which are contained in the cfg-file. */
  private final ByteDataSymbolicAccessReadConfig accessOamVariable;
  
  /**Associated class which shows the values */
  //private final OamShowValues showValues;
  
  boolean dataValid = false;
  
  //private final Map<String, String> indexUnknownVariable = new TreeMap<String, String>();
  
  
  
  final Report log;

  private final File fileOam;
  
  /**To detect whether the file is new. */
  private long lastTimeFile = 0;
  
  /**To detect timeout while file waiting. */
  private long lastTimeAccess = System.currentTimeMillis();
  
  private int timeDiffCheckAccess = 3000;
  
  /**To detect whether a new curve point should show. */
  private long lastTimeCurve = 0;
  
  /**The distance between curve points in milliseconds. */
  private long timeUnitCurve = 1000;
  
  
  int checkWithoutNewdata;  
  
  
  private RawDataAccess dataAccess = new RawDataAccess(); 
  
  byte[] binData = new byte[1024];
  
  private RawDataAccess dataAccessUcell = new RawDataAccess(); 
  
  byte[] binDataUcell = new byte[1152];
  
  public OamOutFileReader(
    String sFileOamValues
    , String sFileOamUcell
    , Report log
  , OamShowValues showValues
  )
  { this.log = log;
    //this.showValues = showValues;
    accessOamVariable = new ByteDataSymbolicAccessReadConfig();
    dataAccess.assignClear(binData);
    dataAccess.setBigEndian(true);
    dataAccessUcell.assignClear(binDataUcell);
    dataAccessUcell.setBigEndian(true);
    fileOam = new File(sFileOamValues);
  
  }
  
  
  public void readVariableCfg()
  { int nrofVariable = accessOamVariable.readVariableCfg("GUI/oamVar.cfg");
    if( nrofVariable>0){
      log.writeInfoln("success read " + nrofVariable + " variables from file \"GUI/oamVar.cfg\".");
    }
  }
  
  
  
  
  /**checks whether new data are received.
   * This routine is called in {@link ViewCfg#execute()} in the main thread.
   */
  public void checkData()
  {
    long fileTime = fileOam.lastModified();
    @SuppressWarnings("unused")
    int nrofBytes;
    if(fileTime != lastTimeFile){
      lastTimeFile = fileTime;
      if((nrofBytes = FileSystem.readBinFile(fileOam, binData)) >0){
        ////showValues.show(binData);
      }
      timeDiffCheckAccess = 3000;  //timeout set to 3 seconds.
      checkWithoutNewdata = 0;
      lastTimeAccess = System.currentTimeMillis();
    } else {
      checkWithoutNewdata +=1;
      if((System.currentTimeMillis() - lastTimeAccess) > timeDiffCheckAccess){
        //5 seconds delay:
        log.writeError("no data found, checked " + checkWithoutNewdata + "times. Tested file:"
          + fileOam.getAbsolutePath() 
          + (fileOam.exists() ? " - exists, timestamp = " + fileTime : " - not found."));
        lastTimeAccess = System.currentTimeMillis();
        checkWithoutNewdata = 0;
        timeDiffCheckAccess = 20000;  //new message after 20 seconds only.
      }
    }
    
    long currentTime = System.currentTimeMillis();
    if(currentTime - lastTimeCurve >= timeUnitCurve){
      if(lastTimeCurve == 0){ 
        lastTimeCurve = currentTime;  //the first time. 
      } else { 
        lastTimeCurve += timeUnitCurve;
      }
      //writeCurveRoutine.run();
    }
  }
  
  
  void stop(){}
  
}
