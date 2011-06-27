package org.vishia.gral.cfg;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;

public class GuiCfgWriter
{
  
  final LogMessage log;

  Writer ww;
  
  public GuiCfgWriter(LogMessage log)
  { this.log = log;
  }
  
  
  public String saveCfg(Writer dest, GuiCfgData cfg)
  { this.ww = dest;
    String sError = null;
    try{
      ww.append("size(500,120); ");   //TODO it isn't used yet
      writeDataReplace(cfg);
      GuiCfgData.GuiCfgElement cfge = cfg.firstElement;
      while(cfge !=null){
        writeElement(dest, cfge);
        cfge = cfge.next;
      }
      dest.append("\n");
    } catch(IOException exc){ log.sendMsg(-1, "exception writing config"); }
    return sError;
  }
  
  
  
  void writeDataReplace(GuiCfgData cfg) throws IOException
  { ww.append("\n");
    for( Map.Entry<String, String> entry: cfg.dataReplace.entrySet()){
      ww.append("DataReplace: ").append(entry.getKey()).append(" = ").append(entry.getValue()).append(";\n");
    }
    ww.append("\n");
    
  }
  
  
  void writeElement(Writer ww, GuiCfgData.GuiCfgElement cfge) 
  throws IOException
  {
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgImage){
      ww.append("\n\n//================================================================================\n");
    }
    writePosition(ww, cfge.positionInput);
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgShowField){ writeShowField((GuiCfgData.GuiCfgShowField)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgText){ writeText((GuiCfgData.GuiCfgText)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgLed){ writeLed((GuiCfgData.GuiCfgLed)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgImage){ writeImage((GuiCfgData.GuiCfgImage)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgInputFile){ writeInputFile((GuiCfgData.GuiCfgInputFile)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgButton){ writeButton((GuiCfgData.GuiCfgButton)cfge.widgetType); }
    //else if(cfge.widgetType instanceof GuiCfgData.GuiCfg){ writeButton((GuiCfgData.GuiCfgButton)cfge.widgetType); }
    else { writeUnknown(cfge.widgetType); }
  }
  
  
  void writePosition(Writer ww, GuiCfgData.GuiCfgPosition pp) throws IOException
  {
    if(pp.yPos >=0 || pp.xPos >=0 || pp.ySizeDown !=0 || pp.xWidth !=0){
      ww.append("\n@");
      if(pp.panel !=null){ ww.append(pp.panel).append(", "); }
      if(pp.yPosRelative) ww.append("&");
      if(pp.yPos>=0) ww.append(Integer.toString(pp.yPos));
      if(pp.yPosFrac !=0) ww.append(".").append(Integer.toString(pp.yPosFrac));
      if(pp.ySizeDown>0) ww.append("+").append(pp.ySizeDown == Integer.MAX_VALUE ? "*" : Integer.toString(pp.ySizeDown));
      else if(pp.ySizeDown<0) ww.append(Integer.toString(pp.ySizeDown)); //with negativ sign!
      if(pp.ySizeFrac !=0) ww.append(".").append(Integer.toString(pp.ySizeFrac));
      if(pp.yIncr_) ww.append("++");    
  
      ww.append(",");
      
      if(pp.xPosRelative) ww.append("&");
      if(pp.xPos>=0) ww.append(Integer.toString(pp.xPos));
      if(pp.xPosFrac !=0) ww.append(".").append(Integer.toString(pp.xPosFrac));
      if(pp.xWidth>0) ww.append("+").append(pp.xWidth == Integer.MAX_VALUE ? "*" : Integer.toString(pp.xWidth));
      else if(pp.xWidth<0) ww.append(Integer.toString(pp.xWidth)); //width negativ sign!
      if(pp.xSizeFrac !=0) ww.append(".").append(Integer.toString(pp.xSizeFrac));
      if(pp.xIncr_) ww.append("++");    
      
      ww.append(": ");
    }
  }

  
  void writeShowField(GuiCfgData.GuiCfgShowField ee) throws IOException
  { String sep = "";
    ww.append("Show(");
    writeParam(ee);
    ww.append("); ");
  }

  
  void writeLed(GuiCfgData.GuiCfgLed ee) throws IOException
  { String sep = "";
    ww.append("Led(");
    writeParam(ee);
    ww.append("); ");
  }

  
  void writeImage(GuiCfgData.GuiCfgImage ee) throws IOException
  { //String sep = "";
    ww.append("Imagefile(file=\"");
    ww.append(ee.file_).append("\"");
    writeParam(ee, ",");
    ww.append(");\n");
  }

  
  
  void writeInputFile(GuiCfgData.GuiCfgInputFile ee) throws IOException
  { //String sep = "";
    ww.append("InputFile(");
    writeParam(ee);
    ww.append(");\n");
  }

  
  
  void writeButton(GuiCfgData.GuiCfgButton ee) throws IOException
  { //String sep = "";
    ww.append("Button(");
    writeParam(ee);
    ww.append(");\n");
  }

  
  
  void writeText(GuiCfgData.GuiCfgText ee) throws IOException
  { String sep = "";
    ww.append("Text(");
    writeParam(ee);
    ww.append("); ");
  }

  
  
  void writeUnknown(GuiCfgData.WidgetTypeBase ee) throws IOException
  { String sep = "";
    ww.append("Unknown(");
    writeParam(ee);
    ww.append("); ");
  }

  
  private void writeParam(GuiCfgData.WidgetTypeBase pp) throws IOException
  {
    writeParam(pp, "");
  }
  
  
  private void writeParam(GuiCfgData.WidgetTypeBase pp, String sep) throws IOException
  { //String sep = "";
    if(pp.text !=null)      { ww.            append("\"")       .append(pp.text).append("\""); sep = ", "; }
    if(pp.name !=null)      { ww.append(sep).append("name=")    .append(pp.name); sep = ", "; }
    if(pp.cmd  !=null)      { ww.append(sep).append("cmd=\"")   .append(pp.cmd).append("\"");  sep = ", "; }
    if(pp.showMethod !=null){ ww.append(sep).append("show=\"")  .append(pp.showMethod).append("\""); sep = ", "; }
    if(pp.format !=null)    { ww.append(sep).append("format=\"").append(pp.format).append("\""); sep = ", "; }
    if(pp.type !=null)      { ww.append(sep).append("type=")    .append(pp.type); sep = ", "; }
    if(pp.info !=null)      { ww.append(sep).append("info=\"")  .append(pp.info).append("\""); sep = ", "; }
    if(pp.userAction !=null){ ww.append(sep).append("action=")  .append(pp.userAction); sep = ", "; }
    if(pp.color0 !=null)    { ww.append(sep).append("color=")   .append(pp.color0.color); 
      if(pp.color0 !=null)    { ww.append("/").append(pp.color1.color); }  sep = ", "; }
  }

  
  
  
  
  
  
  
}
