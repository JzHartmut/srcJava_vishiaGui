package org.vishia.mainGui.cfg;

import java.util.List;
import java.util.Map;

import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.WidgetDescriptor;
import org.vishia.mainGui.cfg.GuiCfgData.GuiCfgElement;

public class GuiCfgBuilder
{

  private final GuiCfgData cfgData;
  
  private final GuiPanelMngBuildIfc gui;
  
  
  public GuiCfgBuilder(GuiCfgData cfgData, GuiPanelMngBuildIfc gui)
  {
    this.cfgData = cfgData;
    this.gui = gui;
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
    //yPos
    if(cfge.positionInput.yPos >=0){
      pos.yPos = cfge.positionInput.yPos;
      pos.yPosFrac = cfge.positionInput.yPosFrac;
    } else if(prevPos.yIncr){ //position = previous + heigth/width
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
    if(cfge.positionInput.xPos >=0){
      pos.xPos = cfge.positionInput.xPos;
      pos.xPosFrac = cfge.positionInput.xPosFrac;
    } else if(prevPos.xIncr){ //position = previous + heigth/width
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
    if(cfge.positionInput.ySizeDown !=0){ //ySize is given here.
      pos.ySizeDown = cfge.positionInput.ySizeDown;
      pos.ySizeFrac = cfge.positionInput.ySizeFrac;
    } else { //use ySize from previous.
      pos.ySizeDown = prevPos.ySizeDown;
      pos.ySizeFrac = prevPos.ySizeFrac;
    }
    if(cfge.positionInput.xWidth !=0){ //xWidth is given here
      pos.xWidth = cfge.positionInput.xWidth;
      pos.xSizeFrac = cfge.positionInput.xSizeFrac;
    } else { //use xWidth from previous
      pos.xWidth = prevPos.xWidth;
      pos.xSizeFrac = prevPos.xSizeFrac;
    }
    //
    gui.setFinePosition(pos.yPos, pos.yPosFrac, pos.xPos, pos.xPosFrac
      , pos.ySizeDown, pos.ySizeFrac, pos.xWidth, pos.xSizeFrac, 'r');
    //
    WidgetDescriptor widgd;
    String sName = cfge.widgetType.name;
    if(sName ==null){ sName = cfge.widgetType.text; }
    
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgButton){
      widgd = gui.addButton(cfge.widgetType.name, null, cfge.widgetType.cmd, null, cfge.widgetType.info, cfge.widgetType.text);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgText){
      GuiCfgData.GuiCfgText wText = (GuiCfgData.GuiCfgText)cfge.widgetType;
      final int colorValue;
      if(wText.color0 !=null){ colorValue = gui.getColorValue(wText.color0.color); }
      else if(wText.colorName !=null){ colorValue = gui.getColorValue(wText.colorName.color);}
      else{ colorValue = 0; } //black
      widgd = gui.addText(cfge.widgetType.text, wText.size.charAt(0), colorValue);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgShowField){
      GuiCfgData.GuiCfgShowField wShow = (GuiCfgData.GuiCfgShowField)cfge.widgetType;
      widgd = gui.addTextField(sName, false, null, '.');
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
  
  
}
