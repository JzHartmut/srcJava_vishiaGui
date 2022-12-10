package org.vishia.guiInspc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralTextField;
import org.vishia.mainCmd.MainCmd;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.ReportWrapperLog;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.FileSystem;
import org.vishia.zbnf.ZbnfJavaOutput;

/**This classes contains a configuration of some variables.
 * They are filled from a file which contains the paths.
 * They can be saved in a file.
 * The variables can be used to show in a curve view, to show in an extra window etc.
 * The advantage: It is flexible which variables are present.
 * @author Hartmut Schorrig
 *
 */
public class InspcGuiFieldsFromFile
{
  /**Version, history and license. 
   * <ul>
   * <li>2014-04-29 Hartmut cleanup, now really used in a professional application. 
   * <li>2013-03-17 Hartmut creating 
   * </ul>
   * <br><br> 
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  //@SuppressWarnings("hiding")
  public final static String sVersion = "2014-04-30";

  
  /**ZBNF result:
   */
  public static class MappingItem
  { public String path;
    public String comment;
    public float scaleFloat = 0.0f;
    public int nrofBits = -1;
    public BitSignal bitSignal = null;
  }
  
  
  /**ZBNF result:
   */
  public static class BitSignal
  { public int mask;
    public float hi = 0.0f;
    public float lo = 0.0f;
  }
  
  
  
  
  
  /**ZBNF result: one line.
   */
  public static class Channel
  {
    public int chn;
    public List<MappingItem> mappingItem = new LinkedList<MappingItem>();
  }
  
  
  /**ZBNF result: the whole file
   */
  public static class ZbnfResultFile {
    /**ZBNF result:  */
    public List<Channel> channel = new LinkedList<Channel>();
  }; 

  
  
  final ZbnfResultFile cfgFileInputData = new ZbnfResultFile();

  LogMessage log;
  
  MainCmdLogging_ifc report;
  
  public final String syntaxMappingFile = "main::={ <Channel> } \\e. "
    + "Channel::=@K<#?chn> : { <MappingItem> }. "
    + "MappingItem::=<* =?path> = [float <#f?scaleFloat>| intBits <#?nrofBits> | bit <bitSignal>] ; [// <*\\n?comment> ]."
    + "bitSignal::= <#x?mask> \\? <#f?hi> [ : <#f?lo>].";
  

  public InspcGuiFieldsFromFile(){
    report = MainCmd.getLogging_ifc();
    assert(report !=null);  //it is null if the application does not use MainCmd.
    log = report;
  }
  
  public InspcGuiFieldsFromFile(LogMessage log){
    this.log = log;
    this.report = new ReportWrapperLog(log);
  }
  
  
  
  
  /**Parses the config file for HSI and fill data.
   * @param fileCfg
   * @param data Proper data instance for parse result, the fields should be match to the semantic.
   * @return true if ok. false then a log message was sent.
   * @throws IllegalArgumentException
   */
  public boolean parse(File fileCfg, ZbnfResultFile data) throws IllegalArgumentException
  {
    boolean bOk = true;
    if(!fileCfg.exists()){
      bOk = false;
      log.sendMsg(0, "HsiMapping - File not found: %s;", fileCfg.getAbsoluteFile());
      throw new IllegalArgumentException();
    }
    ZbnfJavaOutput parser = new ZbnfJavaOutput(report);
    String sError = parser.parseFileAndFillJavaObject(data.getClass(), data, fileCfg, syntaxMappingFile);
    if(sError != null)
    { log.sendMsg(0, sError);
      bOk = false;
    }
    return bOk;    
  }
  

  
}
