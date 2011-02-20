package org.vishia.guiViewCfg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.mainCmd.Report;
import org.vishia.zbnf.ZbnfJavaOutput;

/**This class holds all configuarion informations about messages. */
public class MsgConfig 
{

	public static class MsgConfigItem
	{
		public String text;
		
		public int identNr;
		
		public String dst;
		
		private char type_;
		
		public void set_type(String src){ type_=src.charAt(0); }
	}
	
	
	public static class MsgConfigZbnf
	{ public final List<MsgConfigItem> item = new LinkedList<MsgConfigItem>();
	}
	
	
	/**Index over all ident numbers. */
	Map<Integer, MsgConfigItem> indexIdentNr = new TreeMap<Integer, MsgConfigItem>(); 
	
	
	public MsgConfig(Report log, String sPathZbnf)
	{
		ZbnfJavaOutput parser = new ZbnfJavaOutput(log);
		MsgConfigZbnf rootParseResult = new MsgConfigZbnf();
		File fileConfig = new File(sPathZbnf + "/msg.cfg");
		File fileSyntax = new File(sPathZbnf + "/msgCfg.zbnf");
		String sError = parser.parseFileAndFillJavaObject(MsgConfigZbnf.class, rootParseResult, fileConfig, fileSyntax);
	  if(sError != null){
	  	log.writeError(sError);
	  } else {
	  	//success parsing
	  	for(MsgConfigItem item: rootParseResult.item){
	  		indexIdentNr.put(item.identNr, item);
	  	}
	  	log.writeInfoln("message-config file "+ fileConfig.getAbsolutePath() + " red, " + indexIdentNr.size() + " entries.");
		}
	}
	
	
	
	
}
