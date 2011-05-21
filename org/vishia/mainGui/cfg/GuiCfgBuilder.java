package org.vishia.mainGui.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.mainGui.cfg.GuiCfgData.GuiCfgElement;

public class GuiCfgBuilder
{

  private final GuiCfgData cfgData;
  
  private final GuiPanelMngBuildIfc gui;
  
  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  private final File currentDir;

  
  public GuiCfgBuilder(GuiCfgData cfgData, GuiPanelMngBuildIfc gui, File currentDir)
  {
    this.cfgData = cfgData;
    this.gui = gui;
    this.currentDir = currentDir;
  }
  
  public String buildGui()
  {
    String sError = null;

    for(Map.Entry<String, GuiCfgData.GuiCfgPanel> panelEntry: cfgData.idxPanels.entrySet()){
      GuiCfgData.GuiCfgPanel panel = panelEntry.getValue();
      buildPanel(panel);  
    }
    
    return sError;
  }
  
  
  public String buildPanel(GuiCfgData.GuiCfgPanel cfgDataPanel)
  {
    String sError = null;
    gui.selectPanel(cfgDataPanel.name);
    for(GuiCfgElement cfge: cfgDataPanel.listElements){
      buildWidget(cfge);
    }
    return sError;
  }
  
  
  
  public String buildWidget(GuiCfgElement cfge)
  {
    String sError = null;
    GuiCfgData.GuiCfgPosition prevPos = cfge.previous !=null ? cfge.previous.position : cfge.positionInput;
    GuiCfgData.GuiCfgPosition pos = cfge.position;
    GuiCfgData.GuiCfgPosition inp = cfge.positionInput;
    if(cfge.widgetType.text !=null && cfge.widgetType.text.equals("wd:yCos"))
      stop();

    //yPos
    if(inp.yPos >=0){
      pos.yPos = inp.yPos;
      pos.yPosFrac = inp.yPosFrac;
    } else if(prevPos.yIncr_){ //position = previous + heigth/width
      int yPosAdd = 0;  
      if(prevPos.ySizeDown >=0){ //positive if yPos is on top of widget.
        pos.yPosFrac = prevPos.yPosFrac + prevPos.ySizeFrac;
        if(pos.yPosFrac >=1){ yPosAdd = 1; pos.yPosFrac -=1; }
      } else { //negative if yPos is the bottom line.
        pos.yPosFrac = prevPos.yPosFrac - prevPos.ySizeFrac;  //ySizeFrac is a positiv number always.
        if(pos.yPosFrac <=0){ yPosAdd = -1; pos.yPosFrac +=1; }
      }
      pos.yPos = prevPos.yPos + prevPos.ySizeDown + yPosAdd;
    } else { //!prevPos.Incr: use the previous position
      pos.yPos = prevPos.yPos;
      pos.yPosFrac = prevPos.yPosFrac;
    }
    //xPos
    if(inp.xPos >=0){
      pos.xPos = inp.xPos;
      pos.xPosFrac = inp.xPosFrac;
    } else if(prevPos.xIncr_){ //position = previous + heigth/width
      int xPosAdd = 0;  
      if(prevPos.xWidth >=0){ //positive if yPos is on top of widget.
        pos.xPosFrac = prevPos.xPosFrac + prevPos.xSizeFrac;
        if(pos.xPosFrac >=1){ xPosAdd = 1; pos.xPosFrac -=1; }
      } else { //negative if yPos is the bottom line.
        pos.xPosFrac = prevPos.xPosFrac - prevPos.xSizeFrac;  //ySizeFrac is a positiv number always.
        if(pos.xPosFrac <=0){ xPosAdd = -1; pos.xPosFrac +=1; }
      }
      pos.xPos = prevPos.xPos + prevPos.xWidth + xPosAdd;
    } else { //!prevPos.Incr: use the previous position
      pos.xPos = prevPos.xPos;
      pos.xPosFrac = prevPos.xPosFrac;
    }
    //ySizeDown, xWidth
    if(inp.ySizeDown !=0){ //ySize is given here.
      pos.ySizeDown = inp.ySizeDown;
      pos.ySizeFrac = inp.ySizeFrac;
    } else { //use ySize from previous.
      pos.ySizeDown = prevPos.ySizeDown;
      pos.ySizeFrac = prevPos.ySizeFrac;
    }
    if(inp.xWidth !=0){ //xWidth is given here
      pos.xWidth = inp.xWidth;
      pos.xSizeFrac = inp.xSizeFrac;
    } else { //use xWidth from previous
      pos.xWidth = prevPos.xWidth;
      pos.xSizeFrac = prevPos.xSizeFrac;
    }
    //
    pos.xIncr_ = inp.xIncr_ || (!inp.yIncr_ && prevPos.xIncr_);  //inherit xIncr but not if yIncr. 
    pos.yIncr_ = inp.yIncr_ || (!inp.xIncr_ && prevPos.yIncr_);
    //
    gui.setFinePosition(pos.yPos, pos.yPosFrac, pos.xPos, pos.xPosFrac
      , pos.ySizeDown, pos.ySizeFrac, pos.xWidth, pos.xSizeFrac, 'r');
    //
    WidgetDescriptor widgd;
    String sName = cfge.widgetType.name;
    if(sName ==null){ sName = cfge.widgetType.text; }
    //
    String sDataPath = cfge.widgetType.info != null ? cfge.widgetType.info : sName;
    if(sDataPath !=null){
      int posSep = sDataPath.indexOf(':');
      if(posSep > 0){
        String sPre = cfge.itsCfgData.dataReplace.get(sDataPath.substring(0, posSep));
        if(sPre !=null){
          sDataPath = sPre + sDataPath.substring(posSep+1);
        }
      } else {
        String sReplace = cfge.itsCfgData.dataReplace.get(sDataPath);
        if(sReplace !=null){
          sDataPath = sReplace;
        }
      }
    }
    //
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgButton){
      widgd = gui.addButton(cfge.widgetType.name, null, cfge.widgetType.cmd, null, cfge.widgetType.info, cfge.widgetType.text);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgText){
      GuiCfgData.GuiCfgText wText = (GuiCfgData.GuiCfgText)cfge.widgetType;
      final int colorValue;
      if(wText.color0 !=null){ colorValue = gui.getColorValue(wText.color0.color); }
      else if(wText.colorName !=null){ colorValue = gui.getColorValue(wText.colorName.color);}
      else{ colorValue = 0; } //black
      widgd = gui.addText(cfge.widgetType.text, wText.size.charAt(0), colorValue);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgImage){
      GuiCfgData.GuiCfgImage wImage = (GuiCfgData.GuiCfgImage)cfge.widgetType;
      File fileImage = new File(currentDir, wImage.file_);
      if(fileImage.exists()){
        try{ InputStream imageStream = new FileInputStream(fileImage); 
          gui.addImage(sName, imageStream, 10, 20, "?cmd");
          imageStream.close();
        } catch(IOException exc){ }
          
      }
      
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgShowField){
      GuiCfgData.GuiCfgShowField wShow = (GuiCfgData.GuiCfgShowField)cfge.widgetType;
      widgd = gui.addTextField(sName, false, null, '.');
      widgd.setDataPath(sDataPath);
    }
    return sError;
  }
  
  
  
  private WidgetDescriptor xxxnewWidget(GuiCfgData.GuiCfgElement cfge)
  {
    String sDataPath = cfge.widgetType.info != null ? cfge.widgetType.info : cfge.widgetType.name;
    if(sDataPath !=null){
      int posSep = sDataPath.indexOf(':');
      if(posSep > 0){
        String sPre = cfge.itsCfgData.dataReplace.get(sDataPath.substring(0, posSep));
        if(sPre !=null){
          sDataPath = sPre + sDataPath.substring(posSep+1);
        }
      } else {
        String sReplace = cfge.itsCfgData.dataReplace.get(sDataPath);
        if(sReplace !=null){
          sDataPath = sReplace;
        }
      }
    }
    char whatIs = cfge.widgetType instanceof GuiCfgData.GuiCfgShowField ? 'S'
      : '.';
    WidgetDescriptor widgd = new WidgetDescriptor(cfge, cfge.widgetType.name, whatIs, sDataPath);
    return widgd;
  }
  
  
  void stop(){}
}
