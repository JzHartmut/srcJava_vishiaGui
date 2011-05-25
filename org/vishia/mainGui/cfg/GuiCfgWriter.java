package org.vishia.mainGui.cfg;

import java.io.IOException;
import java.io.Writer;

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
    GuiCfgData.GuiCfgElement cfge = cfg.firstElement;
    try{
      while(cfge !=null){
        writeElement(dest, cfge);
        cfge = cfge.next;
      }
      dest.append("\n");
    } catch(IOException exc){ log.sendMsg(-1, "exception writing config"); }
    return sError;
  }
  
  
  void writeElement(Writer ww, GuiCfgData.GuiCfgElement cfge) 
  throws IOException
  {
    writePosition(ww, cfge.positionInput);
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgShowField){ writeShowField((GuiCfgData.GuiCfgShowField)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgText){ writeText((GuiCfgData.GuiCfgText)cfge.widgetType); }
    else if(cfge.widgetType instanceof GuiCfgData.GuiCfgLed){ writeLed((GuiCfgData.GuiCfgLed)cfge.widgetType); }
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
      if(pp.ySizeDown>0) ww.append("+").append(Integer.toString(pp.ySizeDown));
      else if(pp.ySizeDown<0) ww.append(Integer.toString(pp.ySizeDown)); //with negativ sign!
      if(pp.ySizeFrac !=0) ww.append(".").append(Integer.toString(pp.ySizeFrac));
      if(pp.yIncr_) ww.append("++");    
  
      ww.append(",");
      
      if(pp.xPosRelative) ww.append("&");
      if(pp.xPos>=0) ww.append(Integer.toString(pp.xPos));
      if(pp.xPosFrac !=0) ww.append(".").append(Integer.toString(pp.xPosFrac));
      if(pp.xWidth>0) ww.append("+").append(Integer.toString(pp.xWidth));
      else if(pp.xWidth<0) ww.append(Integer.toString(pp.xWidth)); //with negativ sign!
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

  
  
  
  void writeParam(GuiCfgData.WidgetTypeBase pp) throws IOException
  { String sep = "";
    if(pp.text !=null)      { ww.            append("\"")       .append(pp.text).append("\""); sep = ", "; }
    if(pp.name !=null)      { ww.append(sep).append("name=")    .append(pp.name); sep = ", "; }
    if(pp.cmd  !=null)      { ww.append(sep).append("cmd=")     .append(pp.cmd);  sep = ", "; }
    if(pp.showMethod !=null){ ww.append(sep).append("show=\"")  .append(pp.showMethod).append("\""); sep = ", "; }
    if(pp.format !=null)    { ww.append(sep).append("format=\"").append(pp.format).append("\""); sep = ", "; }
    if(pp.info !=null)      { ww.append(sep).append("info=\"")  .append(pp.info).append("\""); sep = ", "; }
    
  }

  
  
  
  
  
  
  
}
