package org.vishia.guiInspc;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.mainCmd.Report;
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

  
  /**This graphic fields represents the content of one line in the file in a GUI-presentation.
   */
  private static class InputFields{

    /**A channel or track number or short designation. */
    GralTextField widgChn;
    
    /**The path to get the data. It is any path for the communication. */
    GralTextField widgPath;
    
    /**A default scaling of presentation. */
    GralTextField widgScale;
    
    /**A bit designation. */
    GralTextField widgBit;
    
    /**Any comment field present in the file and view. It isn't processed, only showed. */
    GralTextField widgComment;
    
  }
  
  /**Presentation of the file. */
  InputFields[] input = new InputFields[16];

  
  final ZbnfResultFile cfgFileInputData = new ZbnfResultFile();

  LogMessage log;
  
  Report report;
  
  public final String syntaxMappingFile = "main::={ <Channel> } \\e. "
    + "Channel::=@K<#?chn> : { <MappingItem> }. "
    + "MappingItem::=<* =?path> = [float <#f?scaleFloat>| intBits <#?nrofBits> | bit <bitSignal>] ; [// <*\\n?comment> ]."
    + "bitSignal::= <#x?mask> \\? <#f?hi> [ : <#f?lo>].";
  

  
  
  /**Sets the fields from any file with syntax see {@link #syntaxMappingFile}.
   * @param sFilePath The file path
   */
  public void setFieldsFromFile(String sFilePath)
  {
    cfgFileInputData.channel.clear();
    File fileHsi = new File(sFilePath);
    if(parse(fileHsi, cfgFileInputData)){
      int ixInput = 0;
      for(Channel cfgLine: cfgFileInputData.channel){
        int chn = cfgLine.chn;
        for(MappingItem item: cfgLine.mappingItem){
          InputFields fields = input[ixInput];
          fields.widgChn.setText("" + chn);
          final String text;
          fields.widgPath.setText(item.path);
          if(item.nrofBits >0){
            fields.widgBit.setText("" + item.nrofBits);
            fields.widgScale.setText("");
          } else if(item.bitSignal !=null){
            fields.widgBit.setText(":" + Integer.toHexString(item.bitSignal.mask));
            fields.widgScale.setText("" + item.bitSignal.hi);
          } else {
            fields.widgBit.setText("");
            fields.widgScale.setText("" + item.scaleFloat);
          }
          fields.widgComment.setText(item.comment !=null ? item.comment : "");
          if(ixInput < input.length -2){ ixInput +=1; }
        }
      }
      for(; ixInput < input.length; ++ixInput){
        InputFields fields = input[ixInput];
        fields.widgChn.setText("-");
        fields.widgBit.setText("x");
        fields.widgScale.setText("");
      
      }
    }
    
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
  

  public boolean writeFile(String sFile){
    StringBuilder hsi = new StringBuilder();
    for(InputFields variable: input){
      String sVariable = variable.widgPath.getValue().trim();
      String sScale = variable.widgScale.getValue().trim();
      String sChn = variable.widgChn.getValue().trim();
      String sBits = variable.widgBit.getValue().trim();
      String sComment = variable.widgComment.getValue().trim();
      if(sChn.length()>0 && "12345678".indexOf(sChn.charAt(0))>=0  //channel 1...8
        && sVariable.length()>0 && sVariable.charAt(0) != '-'){   //- in variable field prevent usage.
        hsi.append("@K").append(sChn).append(": ");
        /*
        if(variable.widgetVariable !=null){
          //
          String sShowMethod = variable.widgetVariable.getShowMethod(); 
          if(sShowMethod !=null && sShowMethod.equals("stc_cmd")){
            String mask = variable.widgetVariable.getDataPath();
            int ix = "abcd".indexOf(mask.charAt(0));
            
            hsi.append("stc_cmdW:[").append(ix).append("] = bit ");
            hsi.append(mask.substring(1));
            hsi.append(" ? ").append(sScale).append(" : 0; //");
            hsi.append(variable.widgetVariable.name);
          } else if(sBits.startsWith(":")){
            hsi.append(variable.widgetVariable.getDataPath()).append(" = bit ");
            hsi.append(sBits.substring(1));
            hsi.append(" ? ").append(sScale).append(" : 0");
          } else if(sBits.length()>0){
            hsi.append(variable.widgetVariable.getDataPath()).append(" = intBits ");
            hsi.append(sBits);
          } else {
            hsi.append(variable.widgetVariable.getDataPath());
            hsi.append(" = float ").append(sScale);
          }
        } else */{
          ///
          hsi.append(sVariable);
          if(sBits.startsWith(":")){
            hsi.append(" = bit ");
            hsi.append(sBits.substring(1));
            hsi.append(" ? ").append(sScale).append(" : 0");
          } else if(sBits.length() >0){
            hsi.append(" = intBits ");
            hsi.append(sBits);
          } else {
            hsi.append(" = float ").append(sScale);
          }
        }
        hsi.append(";");
        if(sComment.length() >0){
          hsi.append(" //").append(sComment);
        }
        hsi.append("\n");
      }
      //channel +=1;
    }
    boolean bOk = FileSystem.writeFile(hsi.toString(), sFile);
    return true;
  }
  
}
