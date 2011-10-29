package org.vishia.guiViewCfg;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Widget;
import org.vishia.byteData.RawDataAccess;
import org.vishia.mainCmd.Report;
import org.vishia.util.FileSystem;

import org.vishia.byteData.ByteDataSymbolicAccess;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;

public class OamOutFileReader 
{
	/**Index (fast access) of all variable which are contained in the cfg-file. */
	private final ByteDataSymbolicAccess accessOamVariable;
	
	/**Associated class which shows the values */
	private final OamShowValues showValues;
	
	boolean dataValid = false;
	
	private final Map<String, String> indexUnknownVariable = new TreeMap<String, String>();
	
	
	
	final Report log;

	private final File fileOam, fileUcell;
	
	/**To detect whether the file is new. */
	private long lastTimeFile = 0;
	
	/**To detect whether the file is new. */
	private long lastTimeFileUcell = 0;
	
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
		this.showValues = showValues;
		accessOamVariable = new ByteDataSymbolicAccess(log);
	  dataAccess.assignEmpty(binData);
		dataAccess.setBigEndian(true);
		dataAccessUcell.assignEmpty(binDataUcell);
		dataAccessUcell.setBigEndian(true);
		fileOam = new File(sFileOamValues);
		fileUcell = new File(sFileOamUcell);
	
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
