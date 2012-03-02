package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.cfg.GralCfgElement;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.GralCurveView;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.KeyCode;

public class GralCfgBuilder
{

  /**Version and history
   * <ul>
   * <li>2011-05-00 Hartmut created, the old ZbnfCfg.. class is obsolte now.
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
  public static final int version = 20120303;
  
  private final GralCfgData cfgData;
  
  private final GralGridBuild_ifc gui;
  
  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  private final File currentDir;

  private Map<String, String> indexAlias = new TreeMap<String, String>();
  
  
  public GralCfgBuilder(GralCfgData cfgData, GralGridBuild_ifc gui, File currentDir)
  {
    this.cfgData = cfgData;
    this.gui = gui;
    this.currentDir = currentDir;
    String sCanonicalPath = org.vishia.util.FileSystem.getCanonicalPath(currentDir);
    indexAlias.put("cfg", sCanonicalPath);
  }
  
  
  public GralCfgElement XXXnewCfgElement(GralCfgElement previous)
  { //GuiCfgElement cfge = new GuiCfgElement(cfgData);
    GralCfgElement cfge = previous.clone();
    cfge.next = previous.next;
    cfge.previous = previous;
    previous.next = cfge;
    return cfge;
  }
  
  /**Builds the appearance of the whole graphic with the given {@link GralCfgData} cfgData.
   * Calls {@link #buildPanel(org.vishia.gral.cfg.GralCfgPanel)} for the any panel 
   * in the {@link GralCfgData#idxPanels}. Fills the panels one after another.
   * 
   * @param log maybe null, errors and warnings are written
   * @param msgIdent The message identification for output.
   * @return null if ok, elsewhere the error hints which maybe written to log too, one per line.
   */
  public String buildGui(LogMessage log, int msgIdent)
  {
    String sError = null;

    for(Map.Entry<String, GralCfgPanel> panelEntry: cfgData.idxPanels.entrySet()){
      GralCfgPanel panel = panelEntry.getValue();
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
  
  
  /**Builds the appearance of one panel with the given {@link GralCfgPanel} cfgData.
   * @param cfgDataPanel
   * @return null if ok, elsewhere the error hints, one per line.
   */
  public String buildPanel(GralCfgPanel cfgDataPanel)
  {
    String sError = null;
    gui.selectPanel(cfgDataPanel.name);
    
    for(GralCfgElement cfge: cfgDataPanel.listElements){
      String sErrorWidgd = buildWidget(cfge);
      if(sErrorWidgd !=null){
        if(sError == null){ sError = sErrorWidgd; }
        else { sError += "\n" + sErrorWidgd; }
      }
    }
    return sError;
  }
  
  
  
  /**Builds the graphical widget inclusive its {@link GralWidget} and place it in the GUI.
   * @param cfge The configuration element data read from config file or set from the GUI-editor.
   * @return null if OK, an error String for a user info message on warning or error.
   *         It is possible that a named user action is not found etc. 
   */
  public String buildWidget(GralCfgElement cfge)
  {
    String sError = null;
    GralCfgPosition prevPos = cfge.previous !=null ? cfge.previous.position : cfge.positionInput;
    GralCfgPosition pos = cfge.position;
    GralCfgPosition inp = cfge.positionInput;
    if(cfge.widgetType.text !=null && cfge.widgetType.text.equals("wd:yCos"))
      stop();

    pos.xIncr_ = inp.xIncr_ || (!inp.yIncr_ && prevPos.xIncr_);  //inherit xIncr but not if yIncr. 
    pos.yIncr_ = inp.yIncr_ || (!inp.xIncr_ && prevPos.yIncr_);
    final char dirNext;
    if(inp.yIncr_){ dirNext = 'd';}
    else if(inp.xIncr_){ dirNext = 'r';}
    else { dirNext = '.'; }
    //yPos
    if(inp.yPosFrac !=0){
      stop();
    }
    if(inp.yPos >=0){
      pos.yPos = inp.yPos;
      pos.yPosFrac = inp.yPosFrac;
    } else if(pos.yIncr_){ //position = previous + heigth/width
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
    } else if(pos.xIncr_ || (inp.yPos < 0 && ! pos.yIncr_)){ //if same x and y but no increment, then default increment x 
      //position = previous + width
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
    if(inp.ySizeFrac !=0 && inp.ySizeDown == -3){
      stop();
    }
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
    pos.panel = inp.panel !=null ? inp.panel : prevPos.panel;
    //
    if(pos.xWidth == Integer.MAX_VALUE)
      stop();
    if(pos.yPos == 4 && pos.xPos == 56){
      stop();
    }
 
    final int heightArg;
    if(pos.ySizeDown == Integer.MAX_VALUE){ heightArg = GralPos.useNatSize; }
    else if(pos.ySizeDown < 0 && pos.ySizeFrac !=0) { 
      heightArg = pos.ySizeDown -1 + GralPos.size;    //forex -3.5 means really 3.5 to up, use -4, 5 
    } else {heightArg = pos.ySizeDown + GralPos.size; }
    
    int widthArg = pos.xWidth == Integer.MAX_VALUE ? GralPos.useNatSize : pos.xWidth + GralPos.size;
    gui.setFinePosition(pos.yPos, pos.yPosFrac, heightArg, pos.ySizeFrac
        , pos.xPos, pos.xPosFrac, widthArg, pos.xSizeFrac, 1, dirNext, 0, 0, null);
    //
    GralWidget widgd = null;
    String sName = cfge.widgetType.name;
    if(sName ==null && cfge.widgetType.text !=null ){ sName = cfge.widgetType.text; }  //text of button etc.
    if(sName ==null && cfge.widgetType.prompt !=null){ sName = cfgData.actPanel.name + "/" + cfge.widgetType.prompt; } //the prompt as name
    //the name may be null, then the widget is not registered.
    //
    String sDataPath = cfge.widgetType.info;
    //text is the default for a datapath.
    if(sDataPath ==null && cfge.widgetType.text !=null){ sDataPath = cfge.widgetType.text; }
    if(sDataPath !=null){
      //replace a prefix before ':' with its replacement, if the prefix is found.
      //This is a possibility to shorten data path.
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
    final GralUserAction userAction;
    if(cfge.widgetType.userAction !=null){
      userAction = gui.getRegisteredUserAction(cfge.widgetType.userAction);
      if(userAction == null){
        sError = "GuiCfgBuilder - user action not found: " + cfge.widgetType.userAction;
      }
    } else { userAction = null; }
    //
    if(cfge.widgetType instanceof GralCfgData.GuiCfgButton){
      GralCfgData.GuiCfgButton wButton = (GralCfgData.GuiCfgButton) cfge.widgetType;
      if(wButton.bSwitch){
        widgd = gui.addSwitchButton(cfge.widgetType.name, userAction, cfge.widgetType.cmd, null
          , cfge.widgetType.info, cfge.widgetType.text, cfge.widgetType.color0.color, cfge.widgetType.color1.color);
      } else {
        widgd = gui.addButton(cfge.widgetType.name, userAction, cfge.widgetType.cmd, null, cfge.widgetType.info, cfge.widgetType.text);
      }
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgText){
      GralCfgData.GuiCfgText wText = (GralCfgData.GuiCfgText)cfge.widgetType;
      final int colorValue;
      if(wText.color0 !=null){ colorValue = gui.getColorValue(wText.color0.color); }
      else if(wText.colorName !=null){ colorValue = gui.getColorValue(wText.colorName.color);}
      else{ colorValue = 0; } //black
      cfge.widgetType.colorName = null;  //it is used, don't set background.
      widgd = gui.addText(cfge.widgetType.text, wText.size.charAt(0), colorValue);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgLed){
      GralCfgData.GuiCfgLed ww = (GralCfgData.GuiCfgLed)cfge.widgetType;
      widgd = gui.addLed(sName, ww.showMethod, sDataPath);
      
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){
      GralCfgData.GuiCfgImage wImage = (GralCfgData.GuiCfgImage)cfge.widgetType;
      File fileImage = new File(currentDir, wImage.file_);
      if(fileImage.exists()){
        try{ InputStream imageStream = new FileInputStream(fileImage); 
          gui.addImage(sName, imageStream, 10, 20, "?cmd");
          imageStream.close();
        } catch(IOException exc){ }
          
      }
      
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgShowField){
      //GuiCfgData.GuiCfgShowField wShow = (GuiCfgData.GuiCfgShowField)cfge.widgetType;
      //char cPromptPosition = cfge.widgetType.promptPosition ==null || cfge.widgetType.promptPosition.length() <1 
      //                     ? '.' :  cfge.widgetType.promptPosition.charAt(0);
      widgd = gui.addTextField(sName, false, cfge.widgetType.prompt, cfge.widgetType.promptPosition);
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile){
      GralCfgData.GuiCfgInputFile widgt = (GralCfgData.GuiCfgInputFile)cfge.widgetType;
      final String dirMask;
      if(widgt.info !=null){
        dirMask = replaceAlias(widgt.info);
      } else { dirMask = ""; }
      widgd = gui.addFileSelectField(sName, null, dirMask, null, "t");
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgCurveview){
      GralCfgData.GuiCfgCurveview widgt = (GralCfgData.GuiCfgCurveview)cfge.widgetType;
      int nrofTracks = widgt.lines.size(); 
      GralCurveView widgc = gui.addCurveViewY(sName, widgt.nrofPoints, nrofTracks);
      for(GralCfgData.GuiCfgCurveLine line: widgt.lines){
        String sDataPathLine = line.name;
        final GralColor colorLine;
        if(line.colorName !=null){
          colorLine = GralColor.getColor(line.colorName.color);
        } else {
          colorLine = GralColor.getColor(line.colorValue);  //maybe 0 = black if not given.
        }
        widgc.initLine(line.name, sDataPathLine, colorLine, 0, line.nullLine, line.scale, line.offset);
      }
      widgd = widgc;
    } else {
      switch(cfge.widgetType.whatIs){
        case 'T':{
          widgd = gui.addTextField(sName, true, cfge.widgetType.prompt, cfge.widgetType.promptPosition);
          widgd.setDataPath(sDataPath);
        } break;
        default: {
          widgd = null;
        }//default
      }
      
    }
    if(widgd !=null){
      //set common attributes for widgets:
      //widgd.pos = gui.getPositionInPanel();
      String sShowMethod = cfge.widgetType.showMethod;
      if(sShowMethod !=null){
        GralUserAction actionShow = gui.getRegisteredUserAction(sShowMethod);
        if(actionShow == null){
          sError = "GuiCfgBuilder - show method not found: " + sShowMethod;
        } else {
          widgd.setActionShow(actionShow);
        }
      }
      String sCmd = cfge.widgetType.cmd;
      if(sCmd !=null){
        GralUserAction actionCmd = gui.getRegisteredUserAction(sCmd);
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
      if(cfge.widgetType.colorName != null){
        widgd.setBackColor(GralColor.getColor(cfge.widgetType.colorName.color), 0);
      }
      if(cfge.widgetType.color0 != null){
        widgd.setBackColor(GralColor.getColor(cfge.widgetType.color0.color), 0);
      }
      if(cfge.widgetType.color1 != null){
        widgd.setLineColor(GralColor.getColor(cfge.widgetType.color1.color), 0);
      }
      if(cfge.widgetType.dropFiles !=null){
        GralUserAction actionDrop = gui.getRegisteredUserAction(cfge.widgetType.dropFiles);
        if(actionDrop == null){
          sError = "GuiCfgBuilder - action for drop not found: " + cfge.widgetType.dropFiles;
        } else {
          widgd.setDropEnable(actionDrop, KeyCode.dropFiles);
        }
      }
      if(cfge.widgetType.dragFiles !=null){
        GralUserAction actionDrag = gui.getRegisteredUserAction(cfge.widgetType.dragFiles);
        if(actionDrag == null){
          sError = "GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragFiles;
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragFiles);
        }
      }
      if(cfge.widgetType.dragText !=null){
        GralUserAction actionDrag = gui.getRegisteredUserAction(cfge.widgetType.dragText);
        if(actionDrag == null){
          sError = "GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragText;
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragText);
        }
      }
      //save the configuration element as association from the widget.
      widgd.setCfgElement(cfge);
    }
    return sError;
  }
  
  
  public void updatePanel(String panelName)
  {
    
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
