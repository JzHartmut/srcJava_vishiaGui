package org.vishia.gral.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.vishia.gral.base.GralPos;
import org.vishia.msgDispatch.LogMessage;

public class GralCfgWriter
{
  
  /**Version and history
   * <ul>
   * <ul>2015-27-01 Hartmut new: Now a text field can be set editable. Usage of a boolean {@link GralCfgData#editable} 
   *  instead changing the type of a text field from 'S' to 'T', but in {@link GralCfgWriter#writeShowField(org.vishia.gral.cfg.GralCfgData.GuiCfgShowField)}
   *  it is written as a "InputTextline(...". It means next read of the config reads it as editable. 
   * <li>2011-06-00 Hartmut created, 
   * </ul>
   *
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final String version = "2015-01-27";

  private String sLastPanel;
  
  final LogMessage log;

  Writer writer;
  
  public GralCfgWriter(LogMessage log)
  { this.log = log;
  }
  
  
  public String saveCfg(Writer dest, GralCfgData cfg)
  { this.writer = dest;
    String sError = null;
    try{
      writer.append("size(500,120); ");   //TODO it isn't used yet
      writeDataReplace(cfg);
      for(GralCfgElement cfge: cfg.listElementsInTextfileOrder) {
        writeElement(dest, cfge);
      }
      /*
      GralCfgElement cfge = cfg.firstElement;
      while(cfge !=null){
        writeElement(dest, cfge);
        cfge = cfge.next;
      }
      */
      dest.append("\n");
    } catch(IOException exc){ log.sendMsg(-1, "exception writing config"); }
    return sError;
  }
  
  
  
  void writeDataReplace(GralCfgData cfg) throws IOException
  { writer.append("\n");
    if(cfg !=null)for( Map.Entry<String, String> entry: cfg.dataReplace.entrySet()){
      writer.append("DataReplace: ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");
    }
    writer.append("\n");
    
  }
  
  
  void writeElement(Writer ww, GralCfgElement cfge) 
  throws IOException
  {
    if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){
      ww.append("\n\n//================================================================================\n");
    }
    
    if(cfge.widgetType.whatIs == 'w'){ 
      GralCfgPanel cfgp = (GralCfgPanel)cfge;
      writer.append("\n\n//================================================================================\n");
      writer.append("Window: @").append(cfgp.windPos);
      //writePosition(ww, cfge);
      writer.append(": ");
      writer.append(cfgp.name).append(", \"").append(cfgp.windTitle).append("\";\n\n");

    } else {
      //<Element>
      writePosition(ww, cfge);
      if(cfge.widgetType instanceof GralCfgData.GuiCfgShowField){ writeShowField((GralCfgData.GuiCfgShowField)cfge.widgetType); }
      else if(cfge.widgetType instanceof GralCfgData.GuiCfgText){ writeText((GralCfgData.GuiCfgText)cfge.widgetType); }
      else if(cfge.widgetType instanceof GralCfgData.GuiCfgLed){ writeLed((GralCfgData.GuiCfgLed)cfge.widgetType); }
      else if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){ writeImage((GralCfgData.GuiCfgImage)cfge.widgetType); }
      else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile){ writeInputFile((GralCfgData.GuiCfgInputFile)cfge.widgetType); }
      //else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile){ writeInputFile((GralCfgData.GuiCfgInputFile)cfge.widgetType); }
      else if(cfge.widgetType instanceof GralCfgData.GuiCfgButton){ writeButton((GralCfgData.GuiCfgButton)cfge.widgetType); }
      //else if(cfge.widgetType instanceof GuiCfgData.GuiCfg){ writeButton((GuiCfgData.GuiCfgButton)cfge.widgetType); }
      else if(cfge.widgetType.whatIs == 'T'){ 
        writer.append("InputTextline(");
        writeParam(cfge.widgetType);
        writer.append(");\n");
  
      }
      else if(cfge.widgetType.whatIs == 't'){ 
        writer.append("InputBox(");
        writeParam(cfge.widgetType);
        writer.append(");\n");
  
      }
      else { 
        writeUnknown(cfge.widgetType); 
      }
    }
  }
  
  
  void writePosition(Writer ww, GralCfgElement cfge) throws IOException
  {
    if(cfge.positionString !=null) {
      ww.append(cfge.positionString);
    } else {
      GralCfgPosition pp = cfge.positionInput;
      if(pp.yPos >=0 || pp.xPos >=0 || pp.ySizeDown !=0 || pp.xWidth !=0){
        if(pp.panel !=null){ 
          if(!pp.panel.equals(sLastPanel)) {
            ww.append("\n\n//================================================================================\n");
            sLastPanel = pp.panel;
          }
          ww.append("\n@").append(pp.panel).append(", "); 
        } else {
          ww.append("\n@");
        }
        if(pp.yPosRelative) ww.append("&");
        if(pp.yPos>=0) ww.append(Integer.toString(pp.yPos));
        if(pp.yPosFrac !=0) ww.append(".").append(Integer.toString(pp.yPosFrac));
        if(pp.ySizeDown>0) ww.append("+").append(pp.ySizeDown == GralPos.useNatSize/*Integer.MAX_VALUE*/ ? "*" : Integer.toString(pp.ySizeDown));
        else if(pp.ySizeDown<0) ww.append(Integer.toString(pp.ySizeDown)); //with negativ sign!
        if(pp.ySizeFrac !=0) ww.append(".").append(Integer.toString(pp.ySizeFrac));
        if(pp.yIncr_) ww.append("++");    
    
        ww.append(",");
        
        if(pp.xPosRelative) ww.append("&");
        if(pp.xPos>=0) ww.append(Integer.toString(pp.xPos));
        if(pp.xPosFrac !=0) ww.append(".").append(Integer.toString(pp.xPosFrac));
        if(pp.xWidth>0) ww.append("+").append(pp.xWidth == GralPos.useNatSize/*Integer.MAX_VALUE*/ ? "*" : Integer.toString(pp.xWidth));
        else if(pp.xWidth<0) ww.append(Integer.toString(pp.xWidth)); //width negativ sign!
        if(pp.xSizeFrac !=0) ww.append(".").append(Integer.toString(pp.xSizeFrac));
        if(pp.xIncr_) ww.append("++");    
        
        ww.append(": ");
      }
    }
  }

  
  void writeShowField(GralCfgData.GuiCfgShowField ee) throws IOException
  { //String sep = "";
    if(ee.editable){
      writer.append("InputTextline(");
    } else {
      writer.append("Show(");
    }
    writeParam(ee);
    writer.append("); ");
  }

  
  void writeLed(GralCfgData.GuiCfgLed ee) throws IOException
  { //String sep = "";
    writer.append("Led(");
    writeParam(ee);
    writer.append("); ");
  }

  
  void writeImage(GralCfgData.GuiCfgImage ee) throws IOException
  { //String sep = "";
    writer.append("Imagefile(file=\"");
    writer.append(ee.file_).append("\"");
    writeParam(ee, ",");
    writer.append(");\n");
  }

  
  
  void writeInputFile(GralCfgData.GuiCfgInputFile ee) throws IOException
  { //String sep = "";
    writer.append("InputFile(");
    writeParam(ee);
    writer.append(");\n");
  }

  
  
  void writeButton(GralCfgData.GuiCfgButton ee) throws IOException
  { //String sep = "";
    writer.append("Button(");
    writeParam(ee);
    writer.append(");\n");
  }

  
  
  void writeText(GralCfgData.GuiCfgText ee) throws IOException
  { //String sep = "";
    writer.append("Text(");
    writeParam(ee);
    writer.append("); ");
  }

  
  
  void writeUnknown(GralCfgData.WidgetTypeBase ee) throws IOException
  { //String sep = "";
    writer.append("Unknown(");
    writeParam(ee);
    writer.append("); ");
  }

  
  private void writeParam(GralCfgData.WidgetTypeBase pp) throws IOException
  {
    writeParam(pp, "");
  }
  
  
  private void writeParam(GralCfgData.WidgetTypeBase pp, String sep) throws IOException
  { //String sep = "";
    if(pp.prompt !=null && pp.promptPosition !=null && pp.promptPosition.length() >=1) {
      writer.append("%").append(pp.promptPosition).append(":\"")  .append(pp.prompt).append("\""); sep = ", "; 
    }
    if(pp.text !=null)      { writer.append(sep).append("\"")       .append(pp.text).append("\""); sep = ", "; }
    if(pp.name !=null)      { writer.append(sep).append("name=")    .append(pp.name); sep = ", "; }
    if(pp.cmd  !=null)      { writer.append(sep).append("cmd=\"")   .append(pp.cmd).append("\"");  sep = ", "; }
    if(pp.showMethod !=null){ writer.append(sep).append("show=\"")  .append(pp.showMethod).append("\""); sep = ", "; }
    if(pp.format !=null)    { writer.append(sep).append("format=\"").append(pp.format).append("\""); sep = ", "; }
    if(pp.type !=null)      { writer.append(sep).append("type=")    .append(pp.type); sep = ", "; }
    if(pp.data !=null)      { writer.append(sep).append("data=\"")  .append(pp.data).append("\""); sep = ", "; }
    if(pp.userAction !=null){ writer.append(sep).append("action=\"")  .append(pp.userAction).append("\""); sep = ", "; }
    if(pp.dragFiles !=null) { writer.append(sep).append("dragFiles=\"") .append(pp.dragFiles).append("\""); sep = ", "; }
    if(pp.dragText !=null)  { 
      writer.append(sep).append("dragText=\"")  .append(pp.dragText).append("\""); sep = ", "; }
    if(pp.dropFiles !=null) { writer.append(sep).append("dropFiles=\"") .append(pp.dropFiles).append("\""); sep = ", "; }
    if(pp.dropText !=null)  { writer.append(sep).append("dropText=\"")  .append(pp.dropText).append("\""); sep = ", "; }
    if(pp.color0 !=null)    { writer.append(sep).append("color=")       .append(pp.color0.color); 
      if(pp.color1 !=null)  { writer            .append("/")            .append(pp.color1.color); }  sep = ", "; 
    }
  }

  
  
  
  
  
  
  
}
