package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.cfg.GuiCfgData.GuiCfgElement;
import org.vishia.gral.gridPanel.GralPos;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.msgDispatch.LogMessage;

public class GuiCfgBuilder
{

  private final GuiCfgData cfgData;
  
  private final GuiPanelMngBuildIfc gui;
  
  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  private final File currentDir;

  private Map<String, String> indexAlias = new TreeMap<String, String>();
  
  
  public GuiCfgBuilder(GuiCfgData cfgData, GuiPanelMngBuildIfc gui, File currentDir)
  {
    this.cfgData = cfgData;
    this.gui = gui;
    this.currentDir = currentDir;
    String sCanonicalPath = org.vishia.util.FileSystem.getCanonicalPath(currentDir);
    indexAlias.put("cfg", sCanonicalPath);
  }
  
  
  public GuiCfgData.GuiCfgElement XXXnewCfgElement(GuiCfgData.GuiCfgElement previous)
  { //GuiCfgData.GuiCfgElement cfge = new GuiCfgData.GuiCfgElement(cfgData);
    GuiCfgData.GuiCfgElement cfge = previous.clone();
    cfge.next = previous.next;
    cfge.previous = previous;
    previous.next = cfge;
    return cfge;
  }
  
  /**Builds the gui with the given {@link GuiCfgData} cfgData.
   * 
   * @param log maybe null, errors and warnings are written
   * @param msgIdent The message identification for output.
   * @return null if ok, elsewhere the error hints which are written to log, one per line.
   */
  public String buildGui(LogMessage log, int msgIdent)
  {
    String sError = null;

    for(Map.Entry<String, GuiCfgData.GuiCfgPanel> panelEntry: cfgData.idxPanels.entrySet()){
      GuiCfgData.GuiCfgPanel panel = panelEntry.getValue();
      String sErrorPanel = buildPanel(panel);  
      if(sErrorPanel !=null){
        if(log !=null){
          log.sendMsg(msgIdent, "GUIPanelMng - cfg error; %s", sErrorPanel);
        }
        if(sError == null){ sError = sErrorPanel; }
        else { sError += "\n" + sErrorPanel; }
      }
    }
    
    return sError;
  }
  
  
  public String buildPanel(GuiCfgData.GuiCfgPanel cfgDataPanel)
  {
    String sError = null;
    gui.selectPanel(cfgDataPanel.name);
    
    for(GuiCfgElement cfge: cfgDataPanel.listElements){
      String sErrorWidgd = buildWidget(cfge);
      if(sErrorWidgd !=null){
        if(sError == null){ sError = sErrorWidgd; }
        else { sError += "\n" + sErrorWidgd; }
      }
    }
    return sError;
  }
  
  
  
  /**Builds the graphical widget inclusive its {@link WidgetDescriptor} and place it in the GUI.
   * @param cfge The configuration element data read from config file or set from the GUI-editor.
   * @return null if OK, an error String for a user info message on warning or error.
   *         It is possible that a named user action is not found etc. 
   */
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
        pos.yPosFrac = prevPos.yPosFrac + prevPos.ySizeFrac;  //frac part from pos + size
        if(pos.yPosFrac >=1){ yPosAdd = 1; pos.yPosFrac -=1; } //overflow detection >1
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
    pos.yIncr_ = inp.yIncr_ || (!inp.yIncr_ && prevPos.yIncr_);
    pos.panel = inp.panel !=null ? inp.panel : prevPos.panel;
    //
    if(pos.xWidth == Integer.MAX_VALUE)
      stop();
    int heightArg = pos.ySizeDown == Integer.MAX_VALUE ? GralPos.useNatSize : pos.ySizeDown + GralPos.size;
    int widthArg = pos.xWidth == Integer.MAX_VALUE ? GralPos.useNatSize : pos.xWidth + GralPos.size;
    gui.setFinePosition(pos.yPos, pos.yPosFrac, heightArg, pos.ySizeFrac
        , pos.xPos, pos.xPosFrac, widthArg, pos.xSizeFrac, 1, 'r', null);
    //
    WidgetDescriptor widgd = null;
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
    final UserActionGui userAction;
    if(cfge.widgetType.userAction !=null){
      userAction = gui.getRegisteredUserAction(cfge.widgetType.userAction);
      if(userAction == null){
        sError = "GuiCfgBuilder - user action not found: " + cfge.widgetType.userAction;
      }
    } else { userAction = null; }
    //
    if(cfge.widgetType instanceof GuiCfgData.GuiCfgButton){
      widgd = gui.addButton(cfge.widgetType.name, userAction, cfge.widgetType.cmd, null, cfge.widgetType.info, cfge.widgetType.text);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgText){
      GuiCfgData.GuiCfgText wText = (GuiCfgData.GuiCfgText)cfge.widgetType;
      final int colorValue;
      if(wText.color0 !=null){ colorValue = gui.getColorValue(wText.color0.color); }
      else if(wText.colorName !=null){ colorValue = gui.getColorValue(wText.colorName.color);}
      else{ colorValue = 0; } //black
      widgd = gui.addText(cfge.widgetType.text, wText.size.charAt(0), colorValue);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgLed){
      GuiCfgData.GuiCfgLed ww = (GuiCfgData.GuiCfgLed)cfge.widgetType;
      widgd = gui.addLed(sName, ww.showMethod, sDataPath);
      
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
      //GuiCfgData.GuiCfgShowField wShow = (GuiCfgData.GuiCfgShowField)cfge.widgetType;
      widgd = gui.addTextField(sName, false, null, '.');
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GuiCfgData.GuiCfgInputFile){
      GuiCfgData.GuiCfgInputFile widgt = (GuiCfgData.GuiCfgInputFile)cfge.widgetType;
      final String dirMask;
      if(widgt.info !=null){
        dirMask = replaceAlias(widgt.info);
      } else { dirMask = ""; }
      widgd = gui.addFileSelectField(sName, null, dirMask, null, 't');
      widgd.setDataPath(sDataPath);
    } else {
      widgd = null;
    }
    if(widgd !=null){
      String sShowMethod = cfge.widgetType.showMethod;
      if(sShowMethod !=null){
        UserActionGui actionShow = gui.getRegisteredUserAction(sShowMethod);
        if(actionShow == null){
          sError = "GuiCfgBuilder - show method not found: " + sShowMethod;
        } else {
          widgd.setActionShow(actionShow);
        }
      }
      String sCmd = cfge.widgetType.cmd;
      if(sCmd !=null){
        UserActionGui actionCmd = gui.getRegisteredUserAction(sCmd);
        if(actionCmd == null){
          sError = "GuiCfgBuilder - cmd action not found: " + sCmd;
        } else {
          widgd.setActionChange(actionCmd);
        }
      }
      String sFormat = cfge.widgetType.format;
      if(sFormat !=null){
         widgd.setFormat(sFormat);
      }
      widgd.setCfgElement(cfge);
    }
    return sError;
  }
  
  
  
  String replaceAlias(String src)
  {
    int posSep;
    if((posSep = src.indexOf("<*")) < 0) { return src; } //unchanged
    else {
      StringBuilder u = new StringBuilder(src);
      do{
        int posEnd = u.indexOf(">", posSep+2);
        String sAlias = src.substring(posSep + 2, posEnd);
        String sValue = indexAlias.get(sAlias);
        if(sValue == null){ sValue = "??" + sAlias + "??";}
        u.replace(posSep, posEnd+1, sValue);
      } while((posSep = u.indexOf("<*", posSep)) >=0);  //note nice side effect: replace in sValue too
      return u.toString();
    }
  }
  
  
  void stop(){}
}
