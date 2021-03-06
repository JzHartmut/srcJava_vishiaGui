package org.vishia.gral.cfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.cfg.GralCfgElement;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.CalculatorExpr;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

public class GralCfgBuilder
{

  /**Version and history
   * <ul>
   * <li>2015-04-27 Hartmut chg: {@link #buildGui(LogMessage, int)} Now regards only one panel in the window, not only tabbed panels.
   * <li>2014-02-24 Hartmut new element help now also in config.
   * <li>2012-09-17 Hartmut chg: showMethod now split functionName and parameters. The function name is used to get
   *   the {@link GralUserAction} for {@link GralWidget#setActionShow(GralUserAction, String[])}. The parameter are stored
   *   in {@link GralWidget#cfg} as {@link GralWidget.ConfigData#showParam}.
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
  
  private final GralMngBuild_ifc gralMng;
  
  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  private final File currentDir;

  private final Map<String, String> indexAlias = new TreeMap<String, String>();
  
  
  public GralCfgBuilder(GralCfgData cfgData, GralMngBuild_ifc gui, File currentDir)
  {
    this.cfgData = cfgData;
    this.gralMng = gui;
    this.currentDir = currentDir;
    if(currentDir !=null) {
      String sCanonicalPath = org.vishia.util.FileSystem.getCanonicalPath(currentDir);
      indexAlias.put("cfg", sCanonicalPath);
    }
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
  /**
   * @param log
   * @param msgIdent
   * @return
   */
  public String buildGui(LogMessage log, int msgIdent)
  {
    String sError = null;
    gralMng.getReplacerAlias().addDataReplace(cfgData.dataReplace);
    
    Set<Map.Entry<String, GralCfgPanel>> setIdxPanels = cfgData.getPanels();
    if(setIdxPanels.size()==0){
      //gralMng.selectPanel(cfgData.actPanel);
      //==================>
      String sErrorPanel = buildPanel(cfgData.actPanel);  
      if(sErrorPanel !=null){
        if(log !=null){
          log.sendMsg(msgIdent, "GralCfgBuilder - cfg error; %s", sErrorPanel);
        }
        if(sError == null){ sError = sErrorPanel; }
        else { sError += "\n" + sErrorPanel; }
      }
    } else {
      //some panels are given, therefore selects given panels by name or create tabbed panels.
      for(Map.Entry<String, GralCfgPanel> panelEntry: setIdxPanels){  //cfgData.idxPanels.entrySet()){
        GralCfgPanel cfgPanel = panelEntry.getValue();
        if(cfgPanel.windTitle !=null) {
          Debugutil.stop();
          GralWindow wind = new GralWindow(cfgPanel.windPos, cfgPanel.name, cfgPanel.windTitle, GralWindow.windOnTop | GralWindow.windResizeable);
          wind.createImplWidget_Gthread();
        }
        else {
          //A tab in the main window
          //==================>
          gralMng.selectPanel(cfgPanel.name);
        }
        String sErrorPanel = buildPanel(cfgPanel);  
        if(sErrorPanel !=null){
          if(log !=null){
            log.sendMsg(msgIdent, "GralCfgBuilder - cfg error; %s", sErrorPanel);
          }
          if(sError == null){ sError = sErrorPanel; }
          else { sError += "\n" + sErrorPanel; }
        } else {
          stop();
        }
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
    for(GralCfgElement cfge: cfgDataPanel.listElements){
      //=================>>
      String sErrorWidgd;
      try{ sErrorWidgd = buildWidget(cfge); }
      catch(ParseException exc) { sErrorWidgd = exc.getMessage(); }
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
  throws ParseException
  {
    String sError = null;
    cfge.setPos(gralMng);
    if(cfge.widgetType.type !=null){
      GralCfgData.GuiCfgWidget typeData = cfgData.idxTypes.get(cfge.widgetType.type);
      if(typeData == null){
        throw new IllegalArgumentException("GralCfgBuilder.buildWidget - unknown type; " + cfge.widgetType.type + "; in " + cfge.content); 
      } else {
        cfge.widgetType.setFromType(typeData);
      }
    }
    
    GralWidget widgd = null;
    String sName = cfge.widgetType.name;
    if(sName !=null && sName.equals("msgOfDay"))
      stop();
    
    if(sName ==null && cfge.widgetType.text !=null ){ sName = cfge.widgetType.text; }  //text of button etc.
    if(sName ==null && cfge.widgetType.prompt !=null){ sName = cfgData.actPanel.name + "/" + cfge.widgetType.prompt; } //the prompt as name
    //the name may be null, then the widget is not registered.
    //
    
    String sDataPath = cfge.widgetType.data;
    //text is the default for a datapath.
    if(sDataPath ==null && cfge.widgetType.text !=null){ sDataPath = cfge.widgetType.text; }
    /*
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
    */
    //
    final GralUserAction userAction; //, mouseAction;
    final String[] sUserActionArgs;
    GralWidget_ifc.ActionChangeWhen whenUserAction = null;
    if(cfge.widgetType.userAction !=null){
      String sUserAction = cfge.widgetType.userAction; 
      if(sUserAction.startsWith("@")){
        int posEnd = sUserAction.indexOf(':');
        if(posEnd < 0) { sError = "GuiCfgBuilder - @m: ':' not found. ";  sUserAction = null; }
        else {
          for(int ix = 1; ix < posEnd; ++ix){
            char whatMouseKey = sUserAction.charAt(ix);
            switch(whatMouseKey){
            case (char)(KeyCode.mouse1Double & 0xff): whenUserAction = GralWidget_ifc.ActionChangeWhen.onMouse1Double; break;
            }
          }
          sUserAction = sUserAction.substring(posEnd+1).trim(); 
        }  
      }
      if(sUserAction !=null) {
        String[] sMethod = CalculatorExpr.splitFnNameAndParams(sUserAction);
        userAction = gralMng.getRegisteredUserAction(sMethod[0]);
        if(userAction == null){
          sError = "GuiCfgBuilder - user action ignored because not found: " + cfge.widgetType.userAction;
          sUserActionArgs = null;
        } else {
          sUserActionArgs = sMethod[1] == null ? null : CalculatorExpr.splitFnParams(sMethod[1]);
        }
 
      } else { userAction = null; sUserActionArgs = null; }
    } else { userAction = null; sUserActionArgs = null; }
    
    
    
    
    /*
    if(cfge.widgetType.mouseAction !=null){
      mouseAction = gralMng.getRegisteredUserAction(cfge.widgetType.mouseAction);
      if(mouseAction == null){
        sError = "GuiCfgBuilder - mouse action ignored because not found: " + cfge.widgetType.mouseAction;
      }
    } else { mouseAction = null; }
    */
    //
    if(cfge.widgetType instanceof GralCfgData.GuiCfgButton){
      GralCfgData.GuiCfgButton wButton = (GralCfgData.GuiCfgButton) cfge.widgetType;
      if(wButton.bSwitch){
        widgd = gralMng.addSwitchButton(cfge.widgetType.name, userAction, cfge.widgetType.cmd
          , cfge.widgetType.data, cfge.widgetType.text, cfge.widgetType.color0.color, cfge.widgetType.color1.color);
      } else {
        widgd = gralMng.addButton(cfge.widgetType.name, userAction, cfge.widgetType.cmd, cfge.widgetType.data, cfge.widgetType.text);
      }
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgText){
      GralCfgData.GuiCfgText wText = (GralCfgData.GuiCfgText)cfge.widgetType;
      final int colorValue;
      if(wText.color0 !=null){ colorValue = gralMng.getColorValue(wText.color0.color); }
      //else if(wText.colorName !=null){ colorValue = gralMng.getColorValue(wText.colorName.color);}
      else{ colorValue = 0; } //black
      cfge.widgetType.color0 = null;  //it is used, don't set background.
      widgd = gralMng.addText(cfge.widgetType.text); 
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgLed){
      GralCfgData.GuiCfgLed ww = (GralCfgData.GuiCfgLed)cfge.widgetType;
      widgd = gralMng.addLed(sName, sDataPath);
      
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){
      GralCfgData.GuiCfgImage wImage = (GralCfgData.GuiCfgImage)cfge.widgetType;
      File fileImage = new File(currentDir, wImage.file_);
      if(fileImage.exists()){
        try{ InputStream imageStream = new FileInputStream(fileImage); 
          gralMng.addImage(sName, imageStream, 10, 20, "?cmd");
          imageStream.close();
        } catch(IOException exc){ }
          
      }
      
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgShowField){
      //GuiCfgData.GuiCfgShowField wShow = (GuiCfgData.GuiCfgShowField)cfge.widgetType;
      //char cPromptPosition = cfge.widgetType.promptPosition ==null || cfge.widgetType.promptPosition.length() <1 
      //                     ? '.' :  cfge.widgetType.promptPosition.charAt(0);
      widgd = gralMng.addTextField(sName, cfge.widgetType.editable, cfge.widgetType.prompt, cfge.widgetType.promptPosition);
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile){
      GralCfgData.GuiCfgInputFile widgt = (GralCfgData.GuiCfgInputFile)cfge.widgetType;
      final String dirMask;
      if(widgt.data !=null){
        dirMask = replaceAlias(widgt.data);
      } else { dirMask = ""; }
      widgd = gralMng.addFileSelectField(sName, null, dirMask, null, "t");
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgTable){
      GralCfgData.GuiCfgTable widgt = (GralCfgData.GuiCfgTable)cfge.widgetType;
      List<Integer> columns = widgt.getColumnWidths();
      int zColumn = columns.size();
      int[] aCol = new int[zColumn];
      int ix = -1;
      for(Integer column: columns){ aCol[++ix] = column; }
      widgd = gralMng.addTable(sName, widgt.height, aCol);
      widgd.setDataPath(sDataPath);
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgCurveview){
      GralCfgData.GuiCfgCurveview widgt = (GralCfgData.GuiCfgCurveview)cfge.widgetType;
      int nrofTracks = widgt.lines.size(); 
      GralCurveView widgc = gralMng.addCurveViewY(sName, widgt.nrofPoints, null);
      widgc.activate(widgt.activate);
      for(GralCfgData.GuiCfgCurveLine line: widgt.lines){
        String sDataPathLine = line.data;
        final GralColor colorLine;
        if(line.color0 !=null){
          colorLine = GralColor.getColor(line.color0.color);
        } else {
          colorLine = GralColor.getColor(line.colorValue);  //maybe 0 = black if not given.
        }
        widgc.addTrack(line.name, sDataPathLine, colorLine, 0, line.nullLine, line.scale, line.offset);
      }
      widgd = widgc;
    } else {
      switch(cfge.widgetType.whatIs){
        case 'T':{
          widgd = gralMng.addTextField(sName, true, cfge.widgetType.prompt, cfge.widgetType.promptPosition);
          widgd.setDataPath(sDataPath);
        } break;
        case 't':{
          char promptPosition = cfge.widgetType.promptPosition == null ? '.' : cfge.widgetType.promptPosition.charAt(0);
          widgd = gralMng.addTextBox(sName, true, cfge.widgetType.prompt, promptPosition);
          widgd.setDataPath(sDataPath);
        } break;
        case 'U':{
          widgd = gralMng.addValueBar(sName, sDataPath);
        } break;
        case 'I':{
          GralCfgData.GuiCfgLine cfgLine = (GralCfgData.GuiCfgLine)cfge.widgetType;
          //copy the points from the type GuiCfgCoord to GralPoint
          List<GralPoint> points = new LinkedList<GralPoint>();
          for(GralCfgData.GuiCfgCoord coord: cfgLine.coords){
            points.add(new GralPoint(coord.x, coord.y));
          }
          gralMng.addLine(GralColor.getColor(cfgLine.color0.color), points);
        } break;
        default: {
          widgd = null;
        }//default
      }
      
    }
    if(widgd !=null){
      //set common attributes for widgets:
      //widgd.pos = gui.getPositionInPanel();
      String sShowMethod1 = cfge.widgetType.showMethod;
      if(sShowMethod1 !=null){
        String[] sShowMethod = CalculatorExpr.splitFnNameAndParams(sShowMethod1);
       
        GralUserAction actionShow = gralMng.getRegisteredUserAction(sShowMethod[0]);
        if(actionShow == null){
          sError = "GuiCfgBuilder - show method not found: " + sShowMethod[0];
        } else {
          String[] param = sShowMethod[1] == null ? null : CalculatorExpr.splitFnParams(sShowMethod[1]);
          widgd.setActionShow(actionShow, param);
        }
      }
      widgd.sCmd = cfge.widgetType.cmd;
      /*
      String sCmd = cfge.widgetType.cmd;
      if(sCmd !=null){
        GralUserAction actionCmd = gralMng.getRegisteredUserAction(sCmd);
        if(actionCmd == null){
          sError = "GuiCfgBuilder - cmd action not found: " + sCmd;
        } else {
          widgd.setActionChange(actionCmd);
        }
      }*/
      if(userAction !=null){
        if(whenUserAction == null) { widgd.specifyActionChange(cfge.widgetType.userAction, userAction, sUserActionArgs); }
        else { widgd.specifyActionChange(cfge.widgetType.userAction, userAction, sUserActionArgs, whenUserAction); }
      }
      //if(mouseAction !=null){
        //fauly type, does not work: widgd.setActionMouse(mouseAction, 0);
      //}
      String sFormat = cfge.widgetType.format;
      if(sFormat !=null){
         widgd.setFormat(sFormat);
      }
      if(cfge.widgetType.help!=null){
        widgd.setHtmlHelp(cfge.widgetType.help);
      }
      if(cfge.widgetType.color0 != null){
        widgd.setBackColor(GralColor.getColor(cfge.widgetType.color0.color), 0);
      }
      if(cfge.widgetType.color1 != null){
        widgd.setLineColor(GralColor.getColor(cfge.widgetType.color1.color), 0);
      }
      if(cfge.widgetType.dropFiles !=null){
        GralUserAction actionDrop = gralMng.getRegisteredUserAction(cfge.widgetType.dropFiles);
        if(actionDrop == null){
          sError = "GuiCfgBuilder - action for drop not found: " + cfge.widgetType.dropFiles;
        } else {
          widgd.setDropEnable(actionDrop, KeyCode.dropFiles);
        }
      }
      if(cfge.widgetType.dragFiles !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragFiles);
        if(actionDrag == null){
          sError = "GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragFiles;
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragFiles);
        }
      }
      if(cfge.widgetType.dragText !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragText);
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
