package org.vishia.guiViewCfg;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.MsgDispatcher;
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
	public Map<Integer, MsgConfigItem> indexIdentNr = new TreeMap<Integer, MsgConfigItem>(); 
	
	
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
	
	
  public boolean setMsgDispaching(MsgDispatcher msgDispatcher, String chnChars){

    String dstMsg = "";
    int firstIdent = 0, lastIdent = -1;
    for(Map.Entry<Integer,MsgConfig.MsgConfigItem> entry: indexIdentNr.entrySet()){
      MsgConfig.MsgConfigItem item = entry.getValue();
      if(dstMsg.equals(item.dst)){
        lastIdent = item.identNr;
      } else {
        //a new dst, process the last one.
        if(lastIdent >=0){
          setRange(msgDispatcher, dstMsg, firstIdent, lastIdent, chnChars);
        }
        //for next dispatching range: 
        firstIdent = lastIdent = item.identNr;
        dstMsg = item.dst;
      }
    }
    setRange(msgDispatcher, dstMsg, firstIdent, lastIdent, chnChars);  //for the last block.
    System.err.println("MsgReceiver - test message; test");
    return true;
  }
	
  
  private void setRange(MsgDispatcher msgDispatcher, String dstMsg, int firstIdent, int lastIdent, String chnChars){
    int dstBits = 0;
    for(int ixChn = 0; ixChn < chnChars.length(); ++ixChn){
      char chnChar = chnChars.charAt(ixChn);
      if(dstMsg.indexOf(chnChar)>=0){ dstBits |= (1<<ixChn); }  //output to file
    }
    msgDispatcher.setOutputRange(firstIdent, lastIdent, dstBits, MsgDispatcher.mSet, 3);
  }
  

  
}
