package org.vishia.guiBzr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class DataConfig
{

	final List<String> listSwPrjs = new LinkedList<String>();
	
	final Map<String, String> indexCmds = new TreeMap<String, String>();
	
	DataConfig()
	{
		
	}
	
	
	String readConfig(File fileCfg)
	{ String sError = null;
	  FileReader fileReader = null;
	  String sDivision;
	  try{
	  	fileReader = new FileReader(fileCfg);
			BufferedReader reader = new BufferedReader(fileReader);
			String sLine;
			while(sError == null && (sLine = reader.readLine())!=null){
				sLine = sLine.trim();
				if(sLine.length()>0){
					if(sLine.startsWith("==")){
						sDivision = sLine.trim();
					} else if(sLine.startsWith("cmd ")){
		            int posSep = sLine.indexOf('=');
            if(posSep < 0){ 
            	sError = "missing \"=\" in line:" + sLine;
            }
            String sCmdIdent = sLine.substring(4, posSep).trim();
            String sCmdString = sLine.substring(posSep+1).trim();
            indexCmds.put(sCmdIdent, sCmdString);
					} else {
						listSwPrjs.add(sLine.trim());
					}
				}
			}
			reader.close();
		} catch(IOException exc){
			sError = "File error: " + fileCfg.getAbsolutePath();
		}
		try{ if(fileReader !=null){ fileReader.close();}} catch(IOException exc){ 
			if(sError == null){ sError = "close error"; }
		}
		return sError;	
	}
	
}
