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

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCanvasStorage;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralMouseWidgetAction_ifc;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.cfg.GralCfgElement;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPoint;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralLabel;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.CalculatorExpr;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions_B;

public class GralCfgBuilder
{

  /**Version and history
   * <ul>
   * <li>2023-08-08 Hartmut chg: The content is back moved from {@link GralCfgZbnf}, tested, improved there since 2022-08.
   *   It is comparable. It is yet here the new concept to build the Gui, so this class can be used yet. 
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
  public static final String version = "2023-08-08";
  
  private final GralCfgData cfgData;
  
  private final GralMng gralMng;

  /**The current position for building all widgets.
   * This current position holds the value from the last built widget
   * which can be changed relative to always the next widget.
   * It means this current position is really the current while building.
   * Note that the aggregated internal widget position is always a clone of this with the current values whilce building.  
   */
  private GralPos currPos;

  
  public GralWindow window;
  
  
  
  public List<GralWidget> widgets = new LinkedList<GralWidget>();



  /**The current directory is that directory, where the config file is located. 
   * It is used if other files are given with relative path.*/
  private final File currentDir;

  private final Map<String, String> indexAlias = new TreeMap<String, String>();
  
  
  public GralCfgBuilder(GralCfgData cfgData, GralMng gui, File currentDir)
  {
    this.cfgData = cfgData;
    this.gralMng = gui;
    this.currentDir = currentDir;
    if(currentDir !=null) {
      String sCanonicalPath = org.vishia.util.FileSystem.getCanonicalPath(currentDir);
      indexAlias.put("cfg", sCanonicalPath);
    }
  }
  
  
  /**Builds the appearance of the whole graphic with the given {@link GralCfgData} cfgData.
   * The cfgData can be filled manually per programming, or especially by {@link #configureWithZbnf(CharSequence, GralCfgData)}.
   * Calls {@link #buildPanel(org.vishia.gral.cfg.GralCfgPanel)} for the any panel 
   * in the {@link GralCfgData#idxPanels}. Fills the panels one after another.
   * 
   * @param log maybe null, errors and warnings are written
   * @param msgIdent The message identification for output.
   * @return null if ok, elsewhere the error hints which maybe written to log too, one per line.
   *   The window can be gotten by #window. The rest of this class may be not furthermore used.
   */
  public String buildGui ( String sWinTitle ) {
    String sError = null;
    this.gralMng.getReplacerAlias().addDataReplace(this.cfgData.dataReplace);
    this.currPos = new GralPos(this.gralMng);
    try {
      Set<Map.Entry<String, GralCfgElement>> iterWindow = this.cfgData.getWindows();
      for(Map.Entry<String, GralCfgElement> eWin : iterWindow) {
        GralCfgElement cfg = eWin.getValue();
        GralCfgWindow win = (GralCfgWindow)cfg.widgetType;
        String posName = cfg.positionString !=null ? "@" + cfg.positionString + "=" + win.name
                       : "@screen, 10+80, 20+120 = mainWin";
        int windowProps = GralWindow_ifc.windResizeable | GralWindow_ifc.windRemoveOnClose;
        String sWinTitle1 = sWinTitle == null ? win.title : sWinTitle;
        this.window = new GralWindow(this.currPos, posName, sWinTitle1, windowProps, this.gralMng);
        this.window.mainPanel.setGrid(2,2,5,5,-8,-30);
        this.currPos = new GralPos(this.window.mainPanel);             // initial GralPos for widgets inside the window.
        //
        //======>>>>
        buildPanel(win.panelWin, this.window.mainPanel);
        
      }
    } catch (Exception exc) {
      sError = exc.getMessage();
    }
    return sError;
  }
  
  
  
  /**Builds the appearance of the whole graphic with the given {@link GralCfgData} cfgData.
   * The cfgData can be filled manually per programming, or especially by {@link #configureWithZbnf(CharSequence, GralCfgData)}.
   * Calls {@link #buildPanel(org.vishia.gral.cfg.GralCfgPanel)} for the any panel 
   * in the {@link GralCfgData#idxPanels}. Fills the panels one after another.
   * 
   * @param log maybe null, errors and warnings are written
   * @param msgIdent The message identification for output.
   * @return null if ok, elsewhere the error hints which maybe written to log too, one per line.
   *   The window can be gotten by #window. The rest of this class may be not furthermore used.
   */
  public String buildGui ( GralCfgData guiCfgData, GralPanelContent dstPanel ) {
    String sError = null;
    this.gralMng.getReplacerAlias().addDataReplace(this.cfgData.dataReplace);
    try {
        //
        //======>>>>
        buildPanel(guiCfgData.currWindow.panelWin, dstPanel);
        
    } catch (Exception exc) {
      sError = exc.getMessage();
    }
    return sError;
  }
  
  
  
  /**Builds the appearance of one panel with the given {@link GralCfgPanel} cfgData.
   * @param cfgDataPanel
   * @return null if ok, elsewhere the error hints, one per line.
   */
  public String buildPanel(GralCfgPanel cfgPanel, GralPanelContent parentPanel)
  { String sError = null;
    if(cfgPanel.listTabs.size()>0) {                  // tabs in this panel
      parentPanel.setToTabbedPanel();
      for(GralCfgPanel cfgTabPanel : cfgPanel.listTabs) {
        this.currPos = new GralPos(parentPanel);
        GralPanelContent panelTab = parentPanel.addTabPanel(cfgTabPanel.name, cfgTabPanel.name, false);
        panelTab.setGrid(2,2,5,5,-12,-20);
        this.currPos = new GralPos(panelTab);              // GralPos describes the whole panel area of this panel.
        sError = buildPanel(cfgTabPanel, panelTab);        // build the content of this tab
        if(sError !=null) { break; }
      }
    }
    else {
      this.currPos = new GralPos(parentPanel);
      for(GralCfgElement cfge: cfgPanel.listElements){
        //=================>>
        String sErrorWidgd;
        try{
          //======>>>>
          sErrorWidgd = buildWidget(cfge, parentPanel); 
        }
        catch(ParseException exc) { sErrorWidgd = exc.getMessage(); }
        if(sErrorWidgd !=null){
          if(sError == null){ sError = sErrorWidgd; }
          else { sError += "\n" + sErrorWidgd; }
        }
      }
    }
    return sError;
  }
  

  
  
  /**Builds the instance of one of the {@link GralWidget} from the read ZBNF data.
   * This operation does nothing with the Graphic Implementation (SWT, AWT,...). 
   * Hence it can run in the main thread (or any other thread).
   * <br>
   * It is new since 2022-08. History: First the GralMng creates also the Implementation widgets
   * calling {@link GralMng#addSwitchButton(String, String, String, org.vishia.gral.ifc.GralColor, org.vishia.gral.ifc.GralColor)}
   * and the other operations. This was the originally concept. Hence the building of the GUI should run only in the GUI thread
   * and was a little bit more difficult to debug.
   * Since ~2014 more and more only the GralWidget instances without GUI Implementation are firstly created on manual building of the GUI,
   * for example in {@link org.vishia.gitGui.GitGui}. This is better to manage, better to debug.
   * The graphical implementation is created with all given GralWidgets later based on even this given GralWidgets.
   * That is more simple. 
   * - Now this concept is also used for the configured GralWidgets . 
   * 
   * @param cfge The configuration element data read from config file or set from the GUI-editor.
   * @return null if OK, an error String for a user info message on warning or error.
   *         It is possible that a named user action is not found etc. 
   * <br>
   */
  public String buildWidget(GralCfgElement cfge, GralPanelContent currPanel)
  throws ParseException {
    String sError = null;
    
    if(cfge.widgetType.type !=null){
      GralCfgData.GuiCfgWidget typeData = this.cfgData.idxTypes.get(cfge.widgetType.type);
      if(typeData == null){
        throw new IllegalArgumentException("GralCfgBuilder.buildWidget - unknown type; " + cfge.widgetType.type + "; in " + cfge.content); 
      } else {
        cfge.widgetType.setFromType(typeData);
      }
    }
    
    GralWidget widgd = null;
    String sName = cfge.widgetType.name;
    if(sName !=null && sName.equals("msgOfDay"))
      Debugutil.stop();
    
    if(sName ==null && cfge.widgetType.text !=null ){ sName = cfge.widgetType.text; }  //text of button etc.
    if(sName ==null && cfge.widgetType.prompt !=null){ sName = cfgData.currPanel.name + "/" + cfge.widgetType.prompt; } //the prompt as name
    //the name may be null, then the widget is not registered.
    //
    
    String sDataPath = cfge.widgetType.data;
    //text is the default for a datapath.
    //no confuse: if(sDataPath ==null && cfge.widgetType.text !=null){ sDataPath = cfge.widgetType.text; }
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

    GralColor color0 = null;
    if(cfge.widgetType.color0 !=null) {                    // color name for main color given: 
      color0 = cfge.widgetType.color0.color;  //by name 
    }
    GralColor color1 = null;
    if(cfge.widgetType.color1 !=null) {                    // color name for main color given: 
      color1 = cfge.widgetType.color1.color;  //by name 
    }
    //char promptPosition = cfge.widgetType.promptPosition == null ? '.' : cfge.widgetType.promptPosition.charAt(0);
    String sPrompt = cfge.widgetType.prompt;
    String sPromptPos = cfge.widgetType.promptPosition;
    boolean bHasPrompt = sPrompt !=null;
    
    //
    final GralUserAction userAction; //, mouseAction;
    final String[] sUserActionArgs;
    GralWidget_ifc.ActionChangeWhen whenUserAction = null;
    if(cfge.widgetType.userAction !=null){
      String sUserAction = cfge.widgetType.userAction; 
      if(sUserAction.startsWith("@")){
        int posEnd = sUserAction.indexOf(':');
        if(posEnd < 0) { this.gralMng.log.writeError("GuiCfgBuilder - @m: ':' not found. ");  sUserAction = null; }
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
          
          this.gralMng.log.writeError("GuiCfgBuilder - user action ignored because not found: " + cfge.widgetType.userAction);
          sUserActionArgs = null;
        } else {
          sUserActionArgs = sMethod[1] == null ? null : CalculatorExpr.splitFnParams(sMethod[1]);
        }
 
      } else { userAction = null; sUserActionArgs = null; }
    } else { userAction = null; sUserActionArgs = null; }
    

    if(cfge.positionString==null) {
      this.currPos.checkSetNext();
    } else {
      this.currPos.calcNextPos(cfge.positionString);
    }
    
    
    /*
    if(cfge.widgetType.mouseAction !=null){
      mouseAction = gralMng.getRegisteredUserAction(cfge.widgetType.mouseAction);
      if(mouseAction == null){
        this.gralMng.log.writeError("GuiCfgBuilder - mouse action ignored because not found: " + cfge.widgetType.mouseAction;
      }
    } else { mouseAction = null; }
    */
    boolean bColor0Set = false, bColor1Set = false;
    //
    if(cfge.widgetType instanceof GralCfgData.GuiCfgButton){
      GralCfgData.GuiCfgButton wButton = (GralCfgData.GuiCfgButton) cfge.widgetType;
      GralButton widg = new GralButton(this.currPos, sName, cfge.widgetType.text, userAction);
      widg.setData(cfge.widgetType.data);
      widg.sCmd = cfge.widgetType.cmd;
      if(wButton.bSwitch) {
        widg.setSwitchMode(cfge.widgetType.color0.color, cfge.widgetType.color1.color);
        int textSep;
        if(cfge.widgetType.text !=null) {
          textSep = cfge.widgetType.text.indexOf('/');
          if(textSep>0) {
            widg.setSwitchMode(cfge.widgetType.text.substring(0, textSep), cfge.widgetType.text.substring(textSep+1));
          } else {
            widg.setText(cfge.widgetType.text);
          }
        }
      }
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgText){
      GralCfgData.GuiCfgText wText = (GralCfgData.GuiCfgText)cfge.widgetType;
      int origin = 0; //TODO
      String text = StringFunctions_B.convertBackslashChars(wText.text).toString();
      GralLabel widg = new GralLabel(this.currPos, sName, text, origin);
      if(color0 !=null) widg.setTextColor(color0);
      if(color1 !=null) widg.setBackColor(color1, 0);
      bColor0Set = bColor1Set = true;
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgLed){
      GralCfgData.GuiCfgLed ww = (GralCfgData.GuiCfgLed)cfge.widgetType;
      widgd = new GralLed(this.currPos, sName);
      this.widgets.add(widgd);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgImage){
      GralCfgData.GuiCfgImage wImage = (GralCfgData.GuiCfgImage)cfge.widgetType;
      File fileImage = new File(this.currentDir, wImage.file_);
      if(fileImage.exists()){
        try{ InputStream imageStream = new FileInputStream(fileImage); 
          //TODO widgd = new GralImage(imageStream)
          imageStream.close();
        } catch(IOException exc){ }
      }
    } else if(cfge.widgetType instanceof GralCfgData.GuiCfgShowField){
      GralTextField widg = new GralTextField(this.currPos, sName);
      widg.setEditable(cfge.widgetType.editable);
      if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
      this.widgets.add(widgd = widg);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgInputFile) {
      GralCfgData.GuiCfgInputFile widgt = (GralCfgData.GuiCfgInputFile)cfge.widgetType;
      final String dirMask;
      if(widgt.data !=null){
        dirMask = replaceAlias(widgt.data);
      } else { dirMask = ""; }
      //reduce the length of the text field:
      GralPos pos1 = new GralPos(this.currPos);
      pos1.setPosition(this.currPos, GralPos.same, GralPos.same, GralPos.same, GralPos.same -2);
      GralTextField widg = new GralTextField(this.currPos, sName);
      widg.setEditable(cfge.widgetType.editable);
      if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
      this.widgets.add(widgd = widg);
      pos1.setPositionSize(GralPos.same, GralPos.next, 2, 2, 'r', null); // small button right beside the file path field.
      GralButton widgb = new GralButton(pos1, sName + "<",  "<", this.gralMng.actionFileSelect);
      List<String> listRecentFiles = null;
      GralMng.FileSelectInfo fileSelectInfo = new GralMng.FileSelectInfo(sName, listRecentFiles, dirMask, widg);
      widgb.setData(fileSelectInfo); 
      //xSize = xSize1;
      this.widgets.add(widgd = widg);
      this.widgets.add(widgb);

      //widgd = gralMng.addFileSelectField(sName, null, dirMask, null, "t");
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgTable){
      GralCfgData.GuiCfgTable widgt = (GralCfgData.GuiCfgTable)cfge.widgetType;
      List<Integer> columns = widgt.getColumnWidths();
      int zColumn = columns.size();
      int[] aCol = new int[zColumn];
      int ix = -1;
      for(Integer column: columns){ aCol[++ix] = column; }
      widgd = new GralTable<>(this.currPos, sName, widgt.height, aCol);
      this.widgets.add(widgd);
    } 
    else if(cfge.widgetType instanceof GralCfgData.GuiCfgCurveview){
      GralCfgData.GuiCfgCurveview widgt = (GralCfgData.GuiCfgCurveview)cfge.widgetType;
      int nrofTracks = widgt.lines.size(); 
      GralCurveView widg = new GralCurveView(this.currPos, sName, widgt.nrofPoints, null, null);
      widg.activate(widgt.activate);
      for(GralCfgData.GuiCfgCurveLine line: widgt.lines){
        String sDataPathLine = line.data;
        final GralColor colorLine;
        if(line.color0 !=null){
          colorLine = line.color0.color;
        } else {
          colorLine = GralColor.getColor(line.colorValue);  //maybe 0 = black if not given.
        }
        widg.addTrack(line.name, sDataPathLine, colorLine, 0, line.nullLine, line.scale, line.offset);
      }
      this.widgets.add(widgd = widg);
    } else {
      switch(cfge.widgetType.whatIs){
        case 'T': {                                        // T= editable text field
          GralTextField.Type type = GralTextField.Type.editable;
          GralTextField widg = new GralTextField(this.currPos, sName, type);
          if(sPrompt !=null) { widg.setPrompt(sPrompt, sPromptPos); }
          this.widgets.add(widgd = widg);
        } break;
        case 't': {                                        // t= text box
          GralTextBox widg = new GralTextBox(this.currPos, sName);
          widg.setEditable(true);
          this.widgets.add(widgd = widg);
        } break;
        case 'U':{
          widgd = new GralValueBar(this.currPos, sName);
          this.widgets.add(widgd);
        } break;
        case 'I':{                                         // L= Line
          GralCfgData.GuiCfgLine cfgLine = (GralCfgData.GuiCfgLine)cfge.widgetType;
          //copy the points from the type GuiCfgCoord to GralPoint
          List<GralPoint> points = new LinkedList<GralPoint>();
          for(GralCfgData.GuiCfgCoord coord: cfgLine.coords){
            points.add(new GralPoint(coord.x, coord.y));
          }
          GralCanvasStorage canvas = currPanel.getCreateCanvas();
          canvas.drawLine(this.currPos, color0, points);
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
          this.gralMng.log.writeError("GuiCfgBuilder - show method not found: " + sShowMethod[0]);
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
          this.gralMng.log.writeError("GuiCfgBuilder - cmd action not found: " + sCmd;
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
      if(cfge.widgetType.color0 != null && !bColor0Set){
        widgd.setBackColor(cfge.widgetType.color0.color, 0);
      }
      if(cfge.widgetType.color1 != null && !bColor1Set){
        widgd.setLineColor(cfge.widgetType.color1.color, 0);
      }
      if(cfge.widgetType.dropFiles !=null){
        GralUserAction actionDrop = gralMng.getRegisteredUserAction(cfge.widgetType.dropFiles);
        if(actionDrop == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drop not found: " + cfge.widgetType.dropFiles);
        } else {
          widgd.setDropEnable(actionDrop, KeyCode.dropFiles);
        }
      }
      if(cfge.widgetType.dragFiles !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragFiles);
        if(actionDrag == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragFiles);
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragFiles);
        }
      }
      if(cfge.widgetType.dragText !=null){
        GralUserAction actionDrag = gralMng.getRegisteredUserAction(cfge.widgetType.dragText);
        if(actionDrag == null){
          this.gralMng.log.writeError("GuiCfgBuilder - action for drag not found: " + cfge.widgetType.dragText);
        } else {
          widgd.setDragEnable(actionDrag, KeyCode.dragText);
        }
      }
      if(sDataPath !=null) {
        widgd.setDataPath(sDataPath);
      }
      //save the configuration element as association from the widget.
      widgd.setCfgElement(cfge);
    }
    if(sError == null) {
      
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
