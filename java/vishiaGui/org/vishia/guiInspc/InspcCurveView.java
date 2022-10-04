package org.vishia.guiInspc;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.curves.WriteCurveCsv;
import org.vishia.curves.WriteCurve_ifc;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTable.TableLineData;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralFactory;
//import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.gral.widget.GralFileSelectWindow;
import org.vishia.gral.widget.GralColorSelector.SetColorFor;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.inspcPC.mng.InspcFieldOfStruct;
import org.vishia.util.Assert;
import org.vishia.util.Debugutil;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;

/**A curve view window inside the inspector.
 * @author hartmut Schorrig
 *
 */
public final class InspcCurveView
{

  /**Version, history and license. 
   * <ul>
   * <li>2021-12-19 Hartmut new: Now has a help button 
   * <li>2021-12-19 Hartmut chg: in {@link #actionSelectVariableInTable}:
   *   repaint after 500 ms without forced repaint instead 100 ms, then variables in the table can be selected fastly.
   * <li>2021-12-19 Hartmut new right mouse menu in variable list "bold all selected" as new feature:
   *   {@link #actionBoldSelected}
   *   You can mark some lines in this table, then this curves are repaint with 3 thickness. 
   *   This is proper for example to get a print result.
   * <li>2021-12-18 Hartmut new in {@link #actionSelectVariableInTable}: 
   *   It shows the values on cursor for this variable immediately. Very helpfull extension.
   * <li>2021-12-16 Hartmut only formally, now {@link #fileCurveData} instead dirCurveSave, 
   *   {@link #actionOpenFileDialog_i(GralWidget_ifc)} called in the {@link #actionOpenFileDialog}.
   *   {@link #actionReadValues(int, GralWidget_ifc, Object...)} stores the file, not the directory in {@link #fileCurveData}
   *   to show the same file later again (as also completed in {@link GralFileSelector}.
   * <li>2018-01-08 Hartmut refactoring 
   *   <ul>
   *   <li>private class TrackValues removed, it hasn't any specific content, replaced by {@link GralCurveViewTrack_ifc}
   *   which refers a {@link GralCurveView.Track}.
   *   <li> new {@link #fillTableTracks()}, content from {@link #actionReadCfg(int, GralWidget_ifc, Object...)} 
   *     but also invoked in {@link #actionReadValues(int, GralWidget_ifc, Object...)}. That may add tracks. 
   *   </ul> 
   * <li>2018-01-08 Hartmut new: If a datapath was written in a cell of variable paths which is not part of table, especially on an clean or small table,
   *   than the routine {@link GralTable#getCellTextFocus()} is called to get the datapath as text. This is a new line, it is added.
   *   Therewith now it is easy to add lines to an empty table via path in clibboard.
   * <li>2016-05-03 Hartmut chg: Selection of file in this window instead an own dialog window: 
   *   Advantage: This window is always on top. A opened dialog window may be in background - fatal.
   *   It is well to use. 
   * <li>2015-07-12 Hartmut new: switch on and off for curves. 
   * <li>2015-07-12 Hartmut new: Better usability for scaling. [+] and [-] - Buttons works correctly. 
   *   Enter in field for scaling values has an effect immediately. 
   *   Support for more as one track with the same scaling.  
   * <li>2013-05-19 Hartmut new: {@link #actionTrackSelected} with ctrl and left mouse pressed
   * <li>2013-05-15 Hartmut new: Presentation of value on cursor
   * <li>2013-05-14 Hartmut chg: 12 Tracks instead 10. A variableWindowSize cause problems (TODO)
   * <li>2013-05-14 Hartmut progress: {@link #actionSwapVariable}, {@link #actionShiftVariable}
   * <li>2013-05-13 Hartmut new: {@link #actionSwapVariable}, {@link #actionSelectOrChgVarPath}
   * <li>2013-03-28 Hartmut adapt new {@link GralFileSelectWindow}
   * <li>2013-03-27 Hartmut improved/bugfix: The {@link TrackValues#trackView} is the reference to the track in the 
   *   {@link GralCurveView} instance. If a new config is loaded all tracks in {@link GralCurveView#getTrackInfo()}
   *   are created newly using {@link GralCurveView#addTrack(String, String, GralColor, int, int, float, float)}.
   *   Therefore the {@link TrackValues#trackView} should be updated. 
   * <li>2012-10-09 Hartmut now ctrl-mouse down sets the scale settings for the selected channel. Faster user operation.
   * <li>2012-08-10 Hartmut now uses a default directory for config file given in constructor.
   * <li>2012-07-06 Hartmut now read and save of the file works.
   * <li>2012-06-29 Hartmut new open file dialog
   * <li>2012-06-08 Hartmut: new Buttons for read and save the configuration (setting). Yet only a simple file is used.
   *   TODO: File selection.
   * <li>2012-03-17 Hartmut creating using the {@link GralCurveView} in a special window
   *   with the communication in {@link InspcMng}.
   * </ul>
   * <br><br> 
   * 
   * <b>Copyright/Copyleft</b>:
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
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  //@SuppressWarnings("hiding")
  public final static String sVersion = "2014-01-10";

  //boolean bTabWidget = true;

  public static String sBtnReadCfg = "read cfg";
  
  public static String sBtnSaveCfg = "save cfg";
  
  public static String sBtnReadValues = "read data";
  
  public static String sBtnSaveValues = "save data";
  
  
  protected final String sName;
  
  private final Map<String, String> curveExporterClasses;
  
  private final WriteCurve_ifc writerCurveCsv = new WriteCurveCsv();
  
  /**The window for curve view. */
  GralWindow windCurve;
  
  GralWindow windVariables;
  
  /**Used for read/write config and for read/write data*/
  private final GralFileSelector widgFileSelector;
  
  private String sWhatTodoWithFile;
  
  /**Shows the name and input name for read/write config and data. */
  private final GralTextField widgFilename;

  
  
  final GralMng gralMng;
  
  final VariableContainer_ifc variables;
  
  private final static String[] colorCurveDefault = new String[]{"rd", "gn", "lbl", "or", "ma", "bn", "dgn", "drd", "cy", "bl", "gn2", "pu"};
  
  
  /**The input field which is the current scaling field. */
  GralCurveViewTrack_ifc trackScale;
  int ixTrackScale;
  
  //GralColor colorLineTrackSelected;

  final GralColor colorBlack = GralColor.getColor("bk");
  
  GralColor colorTrackSameScale = GralColor.getColor("pgn");

  GralColor colorTrackSameScaleSelected = GralColor.getColor("lgn");
  
  GralColor colorTrackOtherScaleSelected = GralColor.getColor("lam");
  
  GralColor colorTrackOtherScale = GralColor.getColor("wh");
  
  GralColor colorTrackNotShown = GralColor.getColor("lgr");

  GralColor colorTrackNotShownSelected = GralColor.getColor("gr");
  
  GralColor colorBtnFileActive = GralColor.getColor("am");

  GralColor colorBtnFileInactive = GralColor.getColor("wh");
  
  GralTable<GralCurveViewTrack_ifc> widgTableVariables;
  
  GralTextField widgScale, widgScale0, widgline0;
  
  /**The one of {@link #widgScale0}, {@link #widgScale0} or {@link #widgline0} which was focused lastly.
   * To use for [+] and [-] button.
   */
  GralTextField scalingWidg;
  
  GralTextField widgValCursorLeft, widgValCursorRight, widgValdTime; ///
  
  GralButton widgBtnHelp;
  
  GralButton widgBtnUp, widgBtnDn, widgBtnScale, widgBtnReadCfg, widgBtnSaveCfg;
  
  GralButton widgBtnReadValues, widgBtnSaveValues, wdgButtonAutosave, widgBtnColor; 
  
  
  GralButton widgBtnOff;
  
  /**The currently loaded file for curve settings. */
  FileRemote fileCurveCfg;
  
  FileRemote fileCurveData;
  
  final String sHelpDir;
  
  //long timeLastSave;
  
  //long timePeriodSave = 1000L * 60 * 2;
  
  //boolean hasDataTosave;
  
  /**Temporary used formatter. */
  StringFormatter sFormatter = new StringFormatter(50);
  
  /**Common ColorSelector for all curve views. */
  GralColorSelector colorSelector;
  
  
  
  //final InspcGuiComm comm;
  
  GralCurveView widgCurve;
  
  /**Creates the instance with all Gral Widgets. The graphical appearance will not be created. 
   * Call {@link GralFactory#createGraphic(GralWindow, char, org.vishia.msgDispatch.LogMessage) to do that with the whole graphic definition.
   * @param sName Name shown in title bar, can also contain a posString syntax ::=[@<position>=]<$-?name>.
   *   The name is the name of the panel. Example "curveView" or "@screen,12+100,20+100=curveView"
   * @param variables Container to find variables
   * @param gralMng The Gral Graphic Manager
   * @param defaultDir
   * @param curveExporterClasses Class which is used to export curves.
   */
  public InspcCurveView(String sName, VariableContainer_ifc variables, GralMng gralMng
      , FileRemote defaultDirCfg, FileRemote defaultDirSave, String sHelpDir
      , Map<String, String> curveExporterClasses){
    //this.comm = comm;
    this.gralMng = gralMng;
    int windowProps = GralWindow_ifc.windResizeable;
    final String sPosNameWin = sName.startsWith("@") ? sName + "Window" : "@screen,12+100,20+100=" + sName + "Window";
    this.windCurve = this.gralMng.createWindow(sPosNameWin, null, windowProps);
    this.sName = this.windCurve.mainPanel.getName();
    
    this.colorSelector = new GralColorSelector("colorSelector", this.gralMng);
    //need a panel and position:
    gralMng.selectPanel("curveView");
    //gralMng.setPosition(4, 0, 4, 0, 0, '.');
    gralMng.setPosition(44, 56, 94, 104, 0, '.');
    this.widgFileSelector = null;
    //    this.widgFileSelector = new GralFileSelector("-selectFile", 100, new int[]{2,0,-6,-12}, null);
//    this.widgFileSelector.specifyActionOnFileSelected(this.actionSelectFile);
//    this.widgFileSelector.setActionOnEnterFile(this.actionEnterFile);

    this.widgFilename = new GralTextField("-filename", GralTextField.Type.editable);
    buildGraphic(this.windCurve, colorSelector, null);
    this.curveExporterClasses = curveExporterClasses;
    this.variables = variables;
    this.fileCurveCfg = defaultDirCfg;
    this.fileCurveData = defaultDirSave;
    this.sHelpDir = sHelpDir;
     
  }
  
  
  /**Builds the graphic, it should be called only one time on startup in the graphic thread
   * @param wind The main window where the menu to open will be added
   * @param sName The name, used for menu entry too, sample "curve A"
   */
  public void buildGraphic(GralWindow_ifc wind, GralColorSelector colorSelector, GralCurveView.CommonCurve common)
  { gralMng.selectPanel(this.sName);
    //gralMng.setPosition(4, 0, 4, 0, 0, '.');
    gralMng.setPosition(4, 56, 4, 104, 0, '.');
    //int windProps = GralWindow.windConcurrently | GralWindow.windOnTop | GralWindow.windResizeable;
    int windProps = GralWindow.windConcurrently | GralWindow.windOnTop; // | GralWindow.windResizeable;
    //windVariables = gralMng.createWindow("windMapVariables", sName, windProps);
    //gralMng.setPosition(2, GralGridPos.size-1.6f, 0, 3.8f, 0, 'd');
    buildGraphicInCurveWindow(common);
    GralMenu menu = wind.getMenuBar();
    menu.addMenuItem("&Window/open " + sName, actionOpenWindow);
  }

  
  
  /**Also used from Inspector
   * @param common
   */
  public void buildGraphicInCurveWindow(GralCurveView.CommonCurve common)
  {
    int posright = -20;
    gralMng.selectPanel(this.sName);
    
    gralMng.setPosition(0, -4, 0, posright, 0, 'd');
//    widgFileSelector.createImplWidget_Gthread();
//    widgFileSelector.setVisible(false);
    //widgFileSelector.set
    //widgFileSelector.specifyActionOnFileSelected(actionSelectFile);
    //widgFileSelector.setActionOnEnterFile(actionOk);
    gralMng.setPosition(-4, -2, 0, posright, 0, 'd');
//    widgFilename.createImplWidget_Gthread();
    widgFilename.setVisible(false);
    widgFilename.setText("TEST xyz");
    gralMng.setPosition(0, -2, 0, posright, 0, 'd');
    widgCurve = gralMng.addCurveViewY(sName, 15000, common);
    widgCurve.setActionMoveCursor(actionShowCursorValues);
    widgCurve.setActionTrackSelected(actionTrackSelectedFromGralCurveViewCtrlMousePressed);
    gralMng.setPosition(3, GralPos.size -3, posright, -6, 0, 'r', 0);
    gralMng.addText("curve variable");
    if(this.sHelpDir !=null) {
      gralMng.setPosition(3, GralPos.size -3, -6, 0, 0, 'r', 0);
      widgBtnHelp = gralMng.addButton("btnHelp", this.gralMng.actionHelp, "help", null, "help");
    }
    widgTableVariables = new GralTable<GralCurveViewTrack_ifc>("variables", new int[]{-posright});
    gralMng.setPosition(3, GralPos.size +20, posright, 0, 0, 'd', 0);
    widgTableVariables.setColumnEditable(0, true);
    //widgTableVariables.setToPanel(gralMng);
    widgTableVariables.specifyActionOnLineSelected(actionSelectVariableInTable);
    widgTableVariables.setActionChange(actionKeyHandlingTable);
    //widgTableVariables.set
    widgTableVariables.addContextMenuEntryGthread(0, null, "switch on-off <F2>", this.actionOnOffTrack);
    widgTableVariables.addContextMenuEntryGthread(0, null, "insert variable", this.actionInsertVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "replace variable", this.actionReplaceVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "swap variable", this.actionSwapVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "delete variable", this.actionDeleteVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "shift variable", this.actionShiftVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "set color", this.actionColorSelectorOpen);
    widgTableVariables.addContextMenuEntryGthread(0, null, "group scale", this.actionShareScale);
    widgTableVariables.addContextMenuEntryGthread(0, null, "ungroup scale", this.actionUnshareScale);
    widgTableVariables.addContextMenuEntryGthread(0, null, "bold all selected", this.actionBoldSelected);
    gralMng.setPosition(/*22*/-19, GralPos.size +3, -8, 0, 0, 'd', 0);
    widgScale = gralMng.addTextField("scale", true, "scale/div", "t");
    widgScale.setActionFocused(actionFocusScaling);         //store which field, set color
    widgScale.setActionChange(actionSetScaleValues2Track);  //on enter
    scalingWidg = widgScale;  //set which is focused
    widgScale0 = gralMng.addTextField("scale0", true, "mid", "t");
    widgScale0.setActionFocused(actionFocusScaling);        //store which field, set color
    widgScale0.setActionChange(actionSetScaleValues2Track);  //on enter
    widgline0 = gralMng.addTextField("line0", true, "line-%", "t");
    widgline0.setActionFocused(actionFocusScaling);         //store which field, set color
    widgline0.setActionChange(actionSetScaleValues2Track);  //on enter
    gralMng.setPosition(/*32*/-9, GralPos.size +2, -10, GralPos.size +2, 0, 'r', 1);
    /*
    gralMng.setPosition(-23, GralPos.size +1, -10, 0, 0, 'd', 2);
    gralMng.addText("scale/div");
    gralMng.addText("mid");
    gralMng.addText("line-%");
    gralMng.setPosition(-22, GralPos.size +2, -10, 0, 0, 'd', 1);
    widgScale = gralMng.addTextField("scale", true, null, "t");
    widgScale0 = gralMng.addTextField("scale0", true, null, "t");
    widgline0 = gralMng.addTextField("line0", true, null, "t");
    gralMng.setPosition(-12, GralPos.size +2, -10, GralPos.size +2, 0, 'r', 1);
    */
    widgBtnDn = gralMng.addButton("btnDn", actionSetScaleValues2Track, "-", null,  "-");
    widgBtnUp = gralMng.addButton("btnUp", actionSetScaleValues2Track, "+", null, "+");
    gralMng.setPosition(GralPos.same, GralPos.size +2, GralPos.next, GralPos.size +4, 0, 'r', 1);
    gralMng.addButton("btnScale", actionColorSelectorOpen, "!", null,  "color");  
    gralMng.setPosition(/*35*/ -6, GralPos.size +2, -10, GralPos.size +6, 0, 'r', 1);
    widgBtnScale = gralMng.addButton("btnScale", actionSetScaleValues2Track, "!", null,  "set");
    gralMng.setPosition(-22, GralPos.size +3, posright, GralPos.size +9, 0, 'r', 2);
    widgValCursorLeft = gralMng.addTextField(null, true, "cursor left", "t");
    widgValCursorRight = gralMng.addTextField(null, true, "cursor right", "t");
    gralMng.setPosition(-19, GralPos.size +3, posright, GralPos.size +8, 0, 'r', 0);
    widgValdTime = gralMng.addTextField(null, true, "dtime", "t");
    gralMng.setPosition(-15, GralPos.size +2, posright, GralPos.size +8, 0, 'd', 1);
    widgBtnReadCfg = gralMng.addButton("btnReadCfg", actionOpenFileDialog, sBtnReadCfg, null, sBtnReadCfg);
    widgBtnSaveCfg = gralMng.addButton("btnSaveCfg", actionOpenFileDialog, sBtnSaveCfg, null, sBtnSaveCfg);
    widgBtnReadValues = gralMng.addButton("btnReadValues", actionOpenFileDialog, sBtnReadValues, null, sBtnReadValues);
    widgBtnSaveValues = gralMng.addButton("btnSaveValues", actionOpenFileDialog, sBtnSaveValues, null, sBtnSaveValues);
    wdgButtonAutosave = gralMng.addSwitchButton("btnAutoSaveValues", "off-autosave", "on-autosave", GralColor.getColor("lgn"), GralColor.getColor("am") );
    
    gralMng.setPosition(-3, GralPos.size +2, -9, -1, 0, 'd', 0);
    widgBtnOff = gralMng.addSwitchButton(sName + "btnOff", "off / ?on", "on / ?off", GralColor.getColor("lgn"), GralColor.getColor("am"));
  
    if(this.sHelpDir !=null) {
      this.gralMng.createHtmlInfoBoxes(null);
      this.gralMng.setHelpBase(this.sHelpDir);
      this.gralMng.setHelpUrl("+CurveView_help.html");
    }
    
  }
  
  
  /**Shows the window if it is deactivated or deactivates the presentation of the Window. 
   * The Window can be deactivated also by click on the close widget on the title bar as usual in the operation system.
   * @param bShow false then deactivate.
   * 
   */
  public void showWindow ( boolean bShow) {
    this.windCurve.setVisible(bShow);
  }
  
  
  void fillTableTracks() {
    Iterable<? extends GralCurveViewTrack_ifc> listTracks = widgCurve.getTrackInfo();
    widgTableVariables.clearTable();
    int iTrack = 0;
    for(GralCurveViewTrack_ifc track: listTracks){
      String sDataPath = track.getDataPath();
      String[] sCellTexts = new String[1];
      sCellTexts[0] = sDataPath;
      GralTableLine_ifc<GralCurveViewTrack_ifc> line = widgTableVariables.addLine(sDataPath, sCellTexts, track);
      line.setTextColor(track.getLineColor());
      iTrack +=1;
    }
    widgTableVariables.setVisible(true);
    widgTableVariables.repaint(500,500);

  }
  
  
  
  void refreshCurve(){
    if(widgCurve !=null && widgBtnOff !=null){
      GralButton.State state = widgBtnOff.getState();
      boolean bActive = state == GralButton.State.On;
      widgCurve.activate( bActive);
      if(bActive){
        //hasDataTosave = true;
        widgCurve.refreshFromVariable(variables);
      }
    }
    actionShowCursorValues.exec(0, widgCurve);
  }
  
  
  
  protected boolean dropVariable(int actionCode, GralWidget widgd, boolean bInsert){
    if(actionCode == KeyCode.menuEntered){
      GralWidget_ifc variableWidget = gralMng.getLastClickedWidget();
      if(variableWidget == null) {
        System.err.println("No variable clicked");
        return false;
      }
      String sPath;
      if(variableWidget instanceof TableLineData) {
        Object data = variableWidget.getData();
        if(data instanceof InspcFieldOfStruct) {
          @SuppressWarnings("unchecked")
          GralTable<InspcFieldOfStruct>.TableLineData line = (GralTable<InspcFieldOfStruct>.TableLineData)variableWidget;
          sPath = InspcFieldTable.getDataPath(line); //((InspcFieldOfStruct)data).
        } else {
          sPath = null;
        }
      }
      else {
        sPath = variableWidget.getDataPath();
      }
      if(sPath !=null) {
        GralCurveViewTrack_ifc input;
        assert(widgd instanceof GralTable<?>);  //NOTE: context menu to table lines, has GralTable as widget.
        @SuppressWarnings("unchecked")
        GralTable<GralCurveViewTrack_ifc> table = (GralTable<GralCurveViewTrack_ifc>)widgd; //oContentInfo;
        GralTable<GralCurveViewTrack_ifc>.TableLineData refline = table.getLineMousePressed();
        int ixnewline =-1;
        GralTableLine_ifc<GralCurveViewTrack_ifc> newline = null;
        if(bInsert || refline == null){  //insert a line, build a new one
          GralColor colorCurve = GralColor.getColor("rd");
          input = widgCurve.addTrack(sName, sPath, colorCurve, 0, 50, 5000.0f, 0.0f);
          if(refline ==null){
            newline = table.addLine(sPath, new String[]{""}, input);  //add on end
            ixnewline = table.size();  //first line
          } else {
            newline = refline.addPrevLine(sPath, new String[]{""}, input);
            ixnewline = table.getIxLine(refline);
          }
          newline.setCellText(sPath, 0);
        } 
        else {
          input = (GralCurveViewTrack_ifc)refline.getContentInfo();
          input.setDataPath(sPath);
          refline.setCellText(sPath, 0);
        }
        if(newline !=null && ixnewline >=0) {
          table.setCurrentLine(newline, ixnewline, 0);
        }
        table.repaint();
      } else {
        System.out.printf("InspcCurveView - invalid widget to drop; %s\n", variableWidget.toString());
      }
    }
    return true;
    
  }
  
  
  
  protected boolean deleteVariable(int actionCode, GralWidget widgd, boolean bInsert){
    if(actionCode == KeyCode.menuEntered){
      GralCurveViewTrack_ifc input;
      assert(widgd instanceof GralTable<?>);  //NOTE: context menu to table lines, has GralTable as widget.
      @SuppressWarnings("unchecked")
      GralTable<GralCurveViewTrack_ifc> table = (GralTable<GralCurveViewTrack_ifc>)widgd; //oContentInfo;
      GralTable<GralCurveViewTrack_ifc>.TableLineData refline = table.getLineMousePressed();
      if(refline != null){  
        input = (GralCurveViewTrack_ifc)refline.getContentInfo();
        if(input != null) {
          Debugutil.stop();
          //TODO
          //refline.deleteLine();
          //delete the track line!
          
          
      } }
     
      table.repaint();
    }
    return true;
    
  }
  
  
  
  void setDatapath(GralTable<GralCurveViewTrack_ifc>.TableLineData line, String sDatapath){
    GralCurveViewTrack_ifc input = line.data;
    if(input == null){
      GralColor colorLine = GralColor.getColor("rd");
      input = widgCurve.addTrack(sName, sDatapath, colorLine, 0, 50, 5000.0f, 0.0f);
    } else {
      input.setDataPath(sDatapath);
    }
    
  }
  
  
  
  
  /**Step routine to save a curve. A curve will be saved only if the time has expired and the autoSave button is on.
   * 
   */
  public void stepSaveCurve(){
    //long timeNextSave = timeLastSave + timePeriodSave;
    //long timeNow = System.currentTimeMillis();
    if(wdgButtonAutosave !=null && wdgButtonAutosave.isOn() && widgCurve.shouldAutosave()) { //xx hasDataTosave && timeNextSave <= timeNow){
      saveCurve(GralCurveView_ifc.ModeWrite.autoSave);
    }
  }
  
  
  
  /**Reads a curve ///
   * 
   */
  protected void readCurve(File file){
    try{
      widgCurve.readCurve(file);
    } catch(Exception exc){
      widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
      System.err.println(Assert.exceptionInfo("InspcCurveView-read", exc, 1, 2));
    }
    
  }
  
  
  /**Saves a curve ///
   * 
   */
  protected void saveCurve(GralCurveView_ifc.ModeWrite mode){
    try{
      //long dateStart;
      CharSequence sDate;
      if(mode == GralCurveView_ifc.ModeWrite.autoSave){
        sDate = widgCurve.timeInitAutoSave(); 
      } else {
        sDate = widgCurve.timeInitSaveViewArea(); 
       // widgCurve.
      }
      String sNameFile = sDate + "_" + sName;
      //fileCurveData.mkdirs();
      FileRemote fileCurveData = this.fileCurveData.child(sNameFile + ".csv");
      FileSystem.mkDirPath(fileCurveData);
      System.out.println("InspcCurveView - save curve data to; " + fileCurveData.getAbsolutePath());
      writerCurveCsv.setFile(fileCurveData);
      widgCurve.writeCurve(writerCurveCsv, mode);
      
      String sClassExportDat = curveExporterClasses.get("dat");
      if(sClassExportDat !=null){
        Class<?> clazzCurveWriter2 = Class.forName(sClassExportDat);
        
        WriteCurve_ifc writerCurve2 = (WriteCurve_ifc)clazzCurveWriter2.newInstance();
        File fileDat = new File(this.fileCurveData, sNameFile + ".dat");
        
        writerCurve2.setFile(fileDat);
        widgCurve.writeCurve(writerCurve2, mode);
      }
      System.out.println("InspcCurveView - data saved; " + fileCurveData.getAbsolutePath());

      //timeLastSave = System.currentTimeMillis();
    } catch(Exception exc){
      widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
      System.err.println(Assert.exceptionInfo("InspcCurveView-save", exc, 1, 2));
    }
    
  }
  
  
  
  /**Adds an info block to the request telegram to get values. This routine is called
   * when the tx telegram to get values from target is assembled.
   * 
  void addTxInfoBlock(){
    if(widgBtnOff.getState() == GralButton.kOn){
      GralCurveViewTrack_ifc inpLast = null;
      for(GralCurveViewTrack_ifc inp: tracks){
        if(inp.widgetVariable !=null){
          String sDataPath = inp.widgetVariable.getDataPath(); //inp.widgVarPath.getText().trim();
          //if(sDataPath.length() >0){
          inpLast = inp;
          inp.bLast = false;  //all set to fast, but only the inpLast.bLast = true;
          comm.getValueByPath(sDataPath, inp.rxActionRxValueByPath);
        }
        
      } //for
      if(inpLast !=null){
        //at least one variable given.
        inpLast.bLast = true;
      }
    }
  }
   */
  
  
  

  
  GralUserAction actionOpenWindow = new GralUserAction("actionOpenWindow"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      windCurve.setVisible(true); //setWindowVisible(true);
      widgFileSelector.setVisible(false);
      widgFilename.setVisible(false);
      widgBtnReadCfg.setVisible(true);
      widgCurve.setFocus();
      return true;
    }

  };
  
  
  
  /**This action will be called if the mouse is pressed on the drop field.
   * It is not drag and drop because drag'ndrop doesn't works on a field which content is not be able to select.
   * That is on show fields. Therefore the {@link GralMng#getLastClickedWidget()} is used to detect
   * which show field was clicked last.
   * 
   */
  GralUserAction actionReplaceVariable = new GralUserAction("actionReplaceVariable"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { return dropVariable(actionCode, (GralWidget)widgd, false);
    }
  };

  
  GralUserAction actionDeleteVariable = new GralUserAction("actionReplaceVariable"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { return deleteVariable(actionCode, (GralWidget)widgd, false);
    }
  };

  
  GralUserAction actionBoldSelected = new GralUserAction("actionBoldSelected"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { 
      List<GralTableLine_ifc<GralCurveViewTrack_ifc>> markedLines = widgTableVariables.getMarkedLines(0x0001);
      for( GralTableLine_ifc<GralCurveViewTrack_ifc> line : markedLines) {
        GralCurveViewTrack_ifc track = line.getUserData();          // the data of the line is type of Table-Generic
        GralCurveView.Track track2 = (GralCurveView.Track)line.getData();  //the same ---
        track.setLineProperties(null, 3, 0);
      }
      widgCurve.repaint();
      return true;
    }
  };


  GralUserAction actionOnOffTrack = new GralUserAction("actionOnOffTrack"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(trackScale.getVisible() ==0) {
        trackScale.setVisible(2);
        //line.setBackColor(colorTrackSameScale, -1);
      } else {
        trackScale.setVisible(0);
        //line.setBackColor(GralColor.getColor("gr"), -1);
      }
      GralTable<GralCurveViewTrack_ifc>.TableLineData line = widgTableVariables.getCurrentLine();
      actionSelectVariableInTable.exec(KeyCode.F2, widgTableVariables, line);
      widgCurve.repaint();
      return true;
    }
  };


  GralUserAction actionInsertVariable = new GralUserAction("actionInsertVariable"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { return dropVariable(actionCode, (GralWidget)widgd, true);
    }
  };


  public GralUserAction actionKeyHandlingTable = new GralUserAction("actionEnterDatapath"){
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params){
      if(key == KeyCode.enter){
        @SuppressWarnings("unchecked")  //compatible to Java-6
        GralTable<GralCurveViewTrack_ifc>.TableLineData line = (GralTable.TableLineData)params[0];
        if(line == null) {
          String sDatapath = widgTableVariables.getCellTextFocus();
          GralColor colorCurve = GralColor.getColor("red");
          GralCurveViewTrack_ifc input = widgCurve.addTrack(sName, sDatapath, colorCurve, 0, 50, 5000.0f, 0.0f);
          GralTableLine_ifc<GralCurveViewTrack_ifc> newline = widgTableVariables.addLine(sDatapath, new String[]{""}, input);  //add on end
          int ixnewline = widgTableVariables.size();  //first line
          newline.setCellText(sDatapath, 0);
          widgTableVariables.setCurrentLine(newline, ixnewline, 0);
        } else {
          String sDatapath = line.getCellText(0);
          InspcCurveView.this.setDatapath(line, sDatapath);
        }
      }
      else if(key == KeyCode.F2){
        actionOnOffTrack.exec(key, null);
      }
      return true;
    }
  };




  GralUserAction actionSwapVariable = new GralUserAction("actionSwapVariable"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(actionCode == KeyCode.menuEntered && trackScale !=null){
        //read paths                                  
        //Object oContent = widgd.getContentInfo();
        assert(widgd instanceof GralTable<?>);
        @SuppressWarnings("unchecked")
        GralTable<GralCurveViewTrack_ifc> table = (GralTable<GralCurveViewTrack_ifc>) widgd;
        GralTableLine_ifc<GralCurveViewTrack_ifc> linesrc = table.getCurrentLine();
        GralTableLine_ifc<GralCurveViewTrack_ifc> linedst = table.getLineMousePressed();
        /*
        if(linedst !=null && linesrc != linedst){
          TrackValues trackDst = linedst.getUserData();  //(TrackValues)oContent;
          //if(trackDst != trackScale){
            String sPathSwap = trackDst.getDataPath();
            GralColor colorSwap = trackDst.getLineColor(); //trackDst.colorCurve; //  
            GralCurveViewTrack_ifc trackViewSwap = trackDst;
            String sPathDst = trackScale.getDataPath();
            GralColor colorDst = trackScale.getLineColor();  //colorCurve; //  
            GralCurveViewTrack_ifc trackViewDst = trackScale;
            //set paths to other.
            trackScale.colorCurve = colorSwap;
            linesrc.setCellText(sPathSwap, 0);
            //linesrc.setKey(sPathSwap);
            trackScale.setDataPath(sPathSwap);
            linesrc.setTextColor(colorSwap);
            trackScale = trackViewSwap;
            //the new one can be an empty track:
            trackDst.colorCurve = colorDst;
            trackDst.setDataPath(sPathDst);
            lineDst.setCellText(sPathDst, 0);
            lineDst.setTextColor(colorDst);
            trackDst = trackViewDst;
            //change track for scale. It is the same like mouse1Down on this text field:
            //actionSelectOrChgVarPath.exec(KeyCode.mouse1Down, trackDst.widgVarPath);
          }
        }
        */
      }
      return true;
    }
  };
  
  
  

  GralUserAction actionShiftVariable = new GralUserAction("actionShiftVariable"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(actionCode == KeyCode.menuEntered && trackScale !=null){
        //read paths
        assert(widgd instanceof GralTable<?>);
        @SuppressWarnings("unchecked")
        GralTable<GralCurveViewTrack_ifc> table = (GralTable<GralCurveViewTrack_ifc>) widgd;
        GralTableLine_ifc<GralCurveViewTrack_ifc> linesrc = table.getCurrentLine();
        GralTableLine_ifc<GralCurveViewTrack_ifc> linedst = table.getLineMousePressed();
        /*
        //save values of the current selected.
        if(trackScale != trackDst){
          
          String sPathSel = trackScale.widgVarPath.getText();
          GralColor colorSel = trackScale.getLineColor();  //colorCurve; //  
          GralCurveViewTrack_ifc trackViewSel = trackScale;
          if(trackScale.ix > trackDst.ix){  //shift current selected up, shift rest down
            int ix;
            for(ix = trackScale.ix -1; ix >= trackDst.ix; --ix){
              String sPath1 = tracks[ix].widgVarPath.getText();
              GralColor color1 = tracks[ix].getLineColor();  //colorCurve; //  
              GralCurveViewTrack_ifc trackView1 = tracks[ix];
              tracks[ix+1].colorCurve = color1;
              tracks[ix+1].widgVarPath.setTextColor(color1);
              tracks[ix+1].widgVarPath.setText(sPath1);
              tracks[ix+1] = trackView1;
            }
            //write into dst selected.
            trackDst.colorCurve = colorSel;
            trackDst.widgVarPath.setText(sPathSel);
            trackDst.widgVarPath.setTextColor(colorSel);
            trackDst = trackViewSel;
          } else { //shift current selected down, shift rest up
            int ix;
            for(ix = trackScale.ix; ix < trackDst.ix-1; ++ix){
              String sPath1 = tracks[ix+1].widgVarPath.getText();
              GralColor color1 = tracks[ix+1].getLineColor();  //colorCurve; //  
              GralCurveViewTrack_ifc trackView1 = tracks[ix+1];
              tracks[ix].colorCurve = color1;
              tracks[ix].widgVarPath.setTextColor(color1);
              tracks[ix].widgVarPath.setText(sPath1);
              tracks[ix] = trackView1;
            }
            //write into dst selected.
            tracks[ix].colorCurve = colorSel;
            tracks[ix].widgVarPath.setText(sPathSel);
            tracks[ix].widgVarPath.setTextColor(colorSel);
            tracks[ix] = trackViewSel;
          }
        }
        */
        //change track for scale. It is the same like mouse1Down on this text field:
        //actionSelectOrChgVarPath.exec(KeyCode.mouse1Down, trackDst.widgVarPath);
      }
      return true;
    }
  };

  
  GralUserAction actionShareScale = new GralUserAction("actionShareScale"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if( KeyCode.isControlFunctionMouseUpOrMenu(actionCode) && trackScale !=null){
        GralTable<GralCurveViewTrack_ifc>.TableLineData refline = widgTableVariables.getLineMousePressed();
        GralCurveViewTrack_ifc dst = refline.getUserData();     //the line which should be entered in the scale group
        dst.groupTrackScale(trackScale);  //trackScale is the line which was  previously selected.
      }
      return true;
    }
  };
  
  
  GralUserAction actionUnshareScale = new GralUserAction("actionShareScale"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if( KeyCode.isControlFunctionMouseUpOrMenu(actionCode) && trackScale !=null){
        GralTable<GralCurveViewTrack_ifc>.TableLineData refline = widgTableVariables.getLineMousePressed();
        GralCurveViewTrack_ifc dst = refline.getUserData();
        dst.ungroupTrackScale();  
      }
      return true;
    }
  };
  
  
  GralUserAction actionSetScaleValues2Track = new GralUserAction("actionSetScaleValues2Track"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
    { System.out.println("InspcCurveView - actionSetScaleValues2Track, actioncode = " + Integer.toHexString(actionCode));
      GralWidget widgd = (GralWidget)widgi;
      if( (KeyCode.isControlFunctionMouseUpOrMenu(actionCode) || actionCode == KeyCode.enter) && trackScale !=null){
        boolean setScale = false;
        GralCurveViewTrack_ifc dst;
        if(actionCode == KeyCode.enter) {
          dst = trackScale; //TEST
          setScale = true;
        }
        else if(actionCode == KeyCode.menuEntered && widgd.sCmd == null){ //from menu
          assert(false);  //unused since shareTrackScale
          GralTable<GralCurveViewTrack_ifc>.TableLineData refline = widgTableVariables.getLineMousePressed();
          dst = refline.getUserData();
          setScale = true;
        } else {
          dst = trackScale;
        }
        try {
          String s1 = widgScale.getText();
          float scale = Float.parseFloat(s1);
          s1 = widgScale0.getText();
          float scale0 = Float.parseFloat(s1);
          s1 = widgline0.getText();
          int line0 = (int)Float.parseFloat(s1);
          String sNameScalingWidgd = null;
          if(widgd.sCmd != null && widgd.sCmd != "!") { //not for set cmd
            sNameScalingWidgd = scalingWidg.getName();
          }
          float[] fixScales = {7.5f, 6.0f, 5.0f, 4.0f, 3.0f, 2.5f, 2.0f, 1.75f, 1.5f, 1.25f, 1.0f};
          if(widgd.sCmd == "+" && sNameScalingWidgd.equals("scale")){ 
            float value = scale;
            float exp1 = (float)Math.log10(value);
            float exp = (float)Math.floor(exp1); //-1.0f; 
            float base = (float)Math.pow(10.0, exp);
            float unit = value/base;  //1.0 till 9.999
            if(unit < 1.01f) {
              unit = fixScales[0]; base /= 10.0f;
            } else {
              for(int ii = 0; ii < fixScales.length; ++ii){
                if(unit > fixScales[ii]){ unit = fixScales[ii];  break; }
            } }
            scale = value = unit * base;;
            s1 = Float.toString(value);
            widgScale.setText(s1);
            setScale = true;
          }
          else if(widgd.sCmd == "-" && sNameScalingWidgd.equals("scale")){
            float exp1 = (float)Math.log10(1.01f * scale); //regard rounding effect, 0.1 may be presented by 0.0999999
            float exp = (float)Math.floor(exp1); //-1.0f; 
            float base = (float)Math.pow(10.0, exp);
            float unit = scale / base;  //1.0 till 9.999
            if(unit >= 0.99f * fixScales[0]) {
              unit = 1.0f; base *= 10.0f;
            } else {
              for(int ii = fixScales.length-1; ii >=0; --ii){
                if(unit < 0.98f * fixScales[ii]){ unit = fixScales[ii];  break; }
            } }
            //scale = value = value*2;
            scale = unit * base;;
            s1 = Float.toString(scale);
            widgScale.setText(s1);
            setScale = true;
          }
          else if(widgd.sCmd == "-" && sNameScalingWidgd.equals("scale0")){
            if(scale0 >0 && scale0 < scale) {
              scale0 = 0;  //trap the 0.0
            } else {
              scale0 -= scale;
            }
            s1 = Float.toString(scale0);
            widgScale0.setText(s1);
            setScale = true;
          }
          else if(widgd.sCmd == "+" && sNameScalingWidgd.equals("scale0")){
            if(scale0 <0 && scale0 > -scale) {
              scale0 = 0;  //trap the 0.0
            } else {
              scale0 += scale;
            }
            s1 = Float.toString(scale0);
            widgScale0.setText(s1);
            setScale = true;
          }
          else if(widgd.sCmd == "-" && sNameScalingWidgd.equals("line0")){
            if(line0 >= 5.0f) { 
              line0 -= 5.0f;
              s1 = Float.toString(line0);
              widgline0.setText(s1);
              setScale = true;
            }
          }
          else if(widgd.sCmd == "+" && sNameScalingWidgd.equals("line0")){
            if(line0 < 95.0f) { 
              line0 += 5.0f;
              s1 = Float.toString(line0);
              widgline0.setText(s1);
              setScale = true;
            }
          }

          //set the scale.
          if(dst !=null && (  setScale   //maybe cmd '+' or '-', maybe menu 
                           || widgd.sCmd == "!"  //set key
                  )        ) {  
            //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
            if(dst == null){
              dst = widgCurve.addTrack(sName, null, trackScale.getLineColor(), 0, 50, 5000.0f, 0.0f);
            }
            widgCurve.repaint(500,500);
            dst.setTrackScale(scale, scale0, line0);
            widgBtnScale.setTextColor(GralColor.getColor("lgn"));
          }
        } catch(NumberFormatException exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        }
      }
      return true;
  } };
  

  
  
  GralUserAction actionScaleFromMarkedTrack = new GralUserAction("actionScaleFromMarkedTrack"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      GralTableLine_ifc markedLine = widgTableVariables.getFirstMarkedLine(1);
      if(markedLine !=null){
        //float scale = markedLine.
      }
      return true;
  } };


  
  /**called if The text field was entered with mouse 
   * or the focus lost on a changed variable path field.
   * 
   */
  GralUserAction actionSelectVariableInTable = new GralUserAction("actionSelectOrChgVarPath"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      @SuppressWarnings("unchecked") ////
      GralTableLine_ifc<GralCurveViewTrack_ifc> line = (GralTableLine_ifc<GralCurveViewTrack_ifc>)params[0];
      if(line !=null){
        GralCurveViewTrack_ifc track = line.getUserData(); //(GralCurveViewTrack_ifc)line.getContentInfo();
        if(track !=null){
          //set the background color of all line with the same scaling.
          if( track ==null //deselect scaled lines
            || trackScale !=null && !track.isGroupedTrackScale(trackScale) //don't do if it is the same scale group
            || actionCode == KeyCode.F2 //invoked from actionOnOffTrack
            ) {
            for(GralTableLine_ifc<GralCurveViewTrack_ifc> line1: widgTableVariables.iterLines()) { ////
              GralCurveViewTrack_ifc track1 = line1.getUserData();
              if(track1 !=null && track1.getVisible() == 0) {
                line1.setBackColor(colorTrackNotShown, colorTrackNotShownSelected, colorTrackNotShownSelected, colorTrackNotShownSelected, colorTrackNotShownSelected, -1);
              }
              else if(track1 !=null && track!=null && track1.isGroupedTrackScale(track)){
                line1.setBackColor(colorTrackSameScale, colorTrackSameScaleSelected, colorTrackSameScaleSelected, colorTrackSameScaleSelected, colorTrackSameScaleSelected, -1);  //same scale
              } else {
                line1.setBackColor(colorTrackOtherScale, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, -1);
              }
            }
            widgTableVariables.repaint(500,500);
          }
          if(trackScale !=null && trackScale !=null){      // set the line from the last selection to normal
            trackScale.setLineProperties(trackScale.getLineColor(), 1, 0);
            if(trackScale.getVisible() !=0){
              trackScale.setVisible(1);
            }
          }
          //set the track bold and show the scaling of the track 
          InspcCurveView.this.trackScale = track;
          if(trackScale !=null && trackScale.getVisible() !=0){
            trackScale.setLineProperties(trackScale.getLineColor(), 3, 0);
            trackScale.setVisible(2);
            widgScale.setText("" + track.getScale7div());
            widgScale0.setText("" + track.getOffset());
            widgline0.setText("" + track.getLinePercent());
          }
          actionShowCursorValues(track);                   // show the values of this variable on cursor position
          System.out.println("InspcCurveView.action - SelectVariableInTable");
          widgCurve.repaint(500, 0);                       // repaint after ...1 second to have time to select in the table.
        }
      }
      return true;
    }
  };

  
  
  
  /**called if The text field was entered with mouse 
   * or the focus lost on a changed variable path field.
   * 
   */
  GralUserAction actionSelectOrChgVarPath = new GralUserAction("actionSelectOrChgVarPath"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      assert(false);
      GralWidget widg = (GralWidget)widgd;
      if(actionCode == KeyCode.mouse1Down || actionCode == KeyCode.menuEntered){
        chgSelectedTrack((GralCurveViewTrack_ifc)widgd.getContentInfo());
      }
      else if(actionCode == (KeyCode.mouse1Down | KeyCode.ctrl)){
        if(trackScale !=null){
          //last variable
          //trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
          if(trackScale !=null){
            trackScale.setLineProperties(trackScale.getLineColor(), 1, 0);  //the old track
          }
        }
        trackScale = (GralCurveViewTrack_ifc)widgd.getContentInfo();
        if(trackScale == null){
          trackScale = widgCurve.addTrack(sName, null, trackScale.getLineColor(), 0, 50, 5000.0f, 0.0f);
        }
        //colorLineTrackSelected = trackScale.getLineColor();
        trackScale.setLineProperties(trackScale.getLineColor(), 3, 0);     //the new track
        //trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
        try{
          String s1 = widgScale.getText();
          float scale = Float.parseFloat(s1);
          s1 = widgScale0.getText();
          float scale0 = Float.parseFloat(s1);
          s1 = widgline0.getText();
          int line0 = (int)Float.parseFloat(s1);
          //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
          trackScale.setTrackScale(scale, scale0, line0);
        } catch(NumberFormatException exc){
          System.err.println("InspcCurveView - read scale values format error.");
        }
      }
      else if(actionCode == KeyCode.focusLost && widgd.isChanged(true)){
        String sPath = (widg).getValue();
        if(sPath.length() >0 && !sPath.endsWith(".")){
          GralCurveViewTrack_ifc track = (GralCurveViewTrack_ifc)widgd.getContentInfo();
          track.setDataPath(sPath);
        }
      }
      return true;
    }
  };
  
  
  
  
  void actionOpenFileDialog_i ( GralWidget_ifc widgd ) {
    try{
      String sCmd = widgd.getCmd();
      if(sCmd.equals(sWhatTodoWithFile)) {
        //file dialog is open, second press
        actionEnterFile.exec(KeyCode.menuEntered, null);
        //setVisible already done.
      } else if(sWhatTodoWithFile !=null) {
        //other button, it is esc
        sWhatTodoWithFile = null;
        widgFilename.setVisible(false);
        widgFileSelector.setVisible(false);
        widgCurve.setVisible(true);
        widgBtnReadCfg.setBackColor(colorBtnFileInactive,0);
        widgBtnSaveCfg.setBackColor(colorBtnFileInactive,0);
        widgBtnReadValues.setBackColor(colorBtnFileInactive,0);
        widgBtnSaveValues.setBackColor(colorBtnFileInactive,0);
      } else { 
        //button pressed first time because .WhatTodoWithFile == null
        sWhatTodoWithFile = widgd.getCmd(); //The button cmd.
        widgd.setBackColor(colorBtnFileActive, 0);
        final FileRemote fileCurr;
        if(sCmd.equals(sBtnReadCfg)){
          fileCurr = fileCurveCfg;
        } else if(widgd.getCmd().equals(sBtnSaveCfg)){
          //windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), true, actionSaveCfg);
          fileCurr = fileCurveCfg;
        } else if(widgd.getCmd().equals(sBtnReadValues)){
          //windFileCfg.openDialog(dirCurveSave, "read values", false, actionReadValues);
          fileCurr = this.fileCurveData;
        } else if(widgd.getCmd().equals(sBtnSaveValues)){
          //windFileCfg.openDialog(dirCurveSave, "write values", true, actionSaveValues);
          fileCurr = this.fileCurveData;
        } else {
          fileCurr = null; //unexpected
        }
        //windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), false, actionReadCfg);
        widgCurve.setVisible(false);
        widgFilename.setVisible(true);
        widgFileSelector.setVisible(true);
        widgFileSelector.fillIn(fileCurr, false);
        widgFileSelector.setFocus();
      }
    } catch(Exception exc){
      widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
    }
    
  }
  
  
  
  GralUserAction actionOpenFileDialog = new GralUserAction("OpenFileDialog"){
    @SuppressWarnings("synthetic-access") @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up){
        InspcCurveView.this.actionOpenFileDialog_i(widgd);
      }
      return true;
  } };
  
  
  /**Action invoked for any selected file. */
  GralUserAction actionSelectFile = new GralUserAction("GralFileSelector-actionSelectFile"){
    /**The action called from {@link GralTable}.
     * @param params [0] is the Table line. The content of table cells are known here,
     *   because it is the file table itself. The {@link GralTableLine_ifc#getUserData()}
     *   returns the {@link FileRemote} file Object.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(int, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @SuppressWarnings("synthetic-access") @Override 
    public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      GralTableLine_ifc<?> line = (GralTableLine_ifc<?>) params[0];
      String sFileCell = line.getCellText(GralFileSelector.kColFilename);
      widgFilename.setText(sFileCell);
      return true;
    }
  };

  
  /**Action for Enter the file. 
   */
  GralUserAction actionEnterFile = new GralUserAction("GralFileSelector-actionOk"){
    @SuppressWarnings("synthetic-access") @Override 
    public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){  //supress both mouse up and down reaction
        FileRemote dir = widgFileSelector.getCurrentDir();
        String sFilename = widgFilename.getText();
        FileRemote file;
        if(sFilename.length()==0 || sFilename.equals("..")){
          file = dir;
        } else {
          file = dir.child(sFilename);
        }
        if(sWhatTodoWithFile.equals(sBtnReadCfg)) {
          actionReadCfg(KeyCode.menuEntered, null, file);
          widgBtnReadCfg.setBackColor(colorBtnFileInactive, 0);
        } 
        else if(sWhatTodoWithFile.equals(sBtnSaveCfg)) {
          actionSaveCfg(KeyCode.menuEntered, null, file);
          widgBtnSaveCfg.setBackColor(colorBtnFileInactive, 0);
        }
        else if(sWhatTodoWithFile.equals(sBtnReadValues)) {
          actionReadValues(KeyCode.menuEntered, null, file);
          widgBtnReadValues.setBackColor(colorBtnFileInactive, 0);
        }
        else if(sWhatTodoWithFile.equals(sBtnSaveValues)) {
          actionSaveValues(KeyCode.menuEntered, null, file);
          widgBtnSaveValues.setBackColor(colorBtnFileInactive, 0);
        }
        sWhatTodoWithFile = null;
        widgFilename.setVisible(false);
        widgFileSelector.setVisible(false);
        widgCurve.setVisible(true);
           
      }
      return true;
    }
  };

  
  
  
  boolean actionReadCfg(int actionCode, GralWidget_ifc widgd, Object... params)
  { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
      try{
        fileCurveCfg = (FileRemote)params[0];
        System.out.println("InspcCurveView - read curve view from; " + fileCurveCfg.getAbsolutePath());
        windCurve.setTitle(InspcCurveView.this.sName + ": " + fileCurveCfg.getName());
        String in = FileSystem.readFile(fileCurveCfg);
        if(in ==null){
          System.err.println("InspcCurveView - actionRead, file not found;" + fileCurveCfg.getAbsolutePath());
        } else {
          if(widgCurve.applySettings(in)){ //apply the content of the config file to the GralCurveView
            //and transfer the names into the variable text fields of this widget. 
            fillTableTracks();
          }
        }
      } catch(Exception exc){
        widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
      }
      //windFileCfg.closeWindow();
    }
    return true;
  }
  
  
  

  void actionSaveCfg(int actionCode, GralWidget_ifc widgd, Object... params)
  { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
      try{
        fileCurveCfg = (FileRemote)params[0];
        //File file = new File("curve.save");
        System.out.println("InspcCurveView - save curve view to; " + fileCurveCfg.getAbsolutePath());
        Writer out = new FileWriter(fileCurveCfg);
        String sTimeVariable = widgCurve.getTimeVariable();
        if(sTimeVariable !=null){
          out.append("timeVariable = ").append(sTimeVariable).append(";\n");
        }
        //don't use: widgCurve.writeSettings(out);
        //because it writes the order of curves in the view.
        List<GralCurveViewTrack_ifc> listTable = widgTableVariables.getListContent();
        for(GralCurveViewTrack_ifc trackValue: listTable){
          GralCurveViewTrack_ifc track = trackValue;
          String sDataPath;
          int ix = 0;
          if(track !=null && (sDataPath = track.getDataPath()) !=null && sDataPath.length() >0){
            out.append("track ").append("Track").append(Integer.toString(ix)).append(":");
            out.append(" datapath=").append(sDataPath);
            GralColor lineColor = track.getLineColor();
            if(lineColor !=null){
              out.append(", color=").append(track.getLineColor().toString());
            } else {
              out.append(", color=0x000000");
            }
            out.append(", scale=").append(Float.toString(track.getScale7div()));
            out.append(", offset=").append(Float.toString(track.getOffset()));
            out.append(", 0-line-percent=").append(Integer.toString(track.getLinePercent()));
            out.append(";\n");
          }
          ix +=1;
        }
        out.close();  //NOTE: it is not closed on any write exception.
      } catch(Exception exc){
        widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
      }
    }
  }  


  
  /**Action invoked if the read file was selected in the {@link GralFileSelectWindow}
   */
  void actionReadValues(int actionCode, GralWidget_ifc widgd, Object... params)
  { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
      try{
        assert(params[0] instanceof File);
        FileRemote file = (FileRemote)params[0];
        this.fileCurveData = file; //.getParentFile();
        
        readCurve(file);
        fillTableTracks();

        ///
        
      } catch(Exception exc){
        widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        System.err.println(Assert.exceptionInfo("InspcCurveView - Read Curve", exc, 1, 2));
      }
    }
  }
  

  /**Action invoked if the write file was selected in the {@link GralFileSelectWindow}
   */
  void actionSaveValues(int actionCode, GralWidget_ifc widgd, Object... params)
  { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
      try{
        this.fileCurveData = (FileRemote)params[0];
        saveCurve(GralCurveView_ifc.ModeWrite.currentView);
        ///
        
      } catch(Exception exc){
        widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        System.err.println(Assert.exceptionInfo("InspcCurveView - dirCurveSave", exc, 1, 2));
      }
    }
  }
  

  public GralUserAction actionFocusScaling = new GralUserAction("actionSetFocusScaling"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(actionCode == KeyCode.focusGained) {
        if(scalingWidg !=null){
          scalingWidg.setBackColor(GralColor.getColor("wh"), 0);   //current, old  
        }
        scalingWidg = (GralTextField)widgd;
        scalingWidg.setBackColor(GralColor.getColor("lam"), 0);  //the new one
      }
      return true; 
    }
  };
 
  
  public GralUserAction actionColorSelectorOpen = new GralUserAction("GralColorSelector-open"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        if(colorSelector == null) {
          colorSelector = new GralColorSelector("colorSelector", GralMng.get());
          
        }
        colorSelector.openDialog("select Color for selected track", actionColorSet);
        return true;
      } else return false;
    }
  };
 
  
  public GralColorSelector.SetColorIfc actionColorSet = new GralColorSelector.SetColorIfc(){
    @Override public void setColor(GralColor color, SetColorFor what)
    { if(what == SetColorFor.line){
        if(trackScale !=null){
          /*
          if(trackScale == null){
            trackScale = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
          }
          trackScale.setLineProperties(color, 3, 0);  //change color immediately to see what happen
          trackScale.colorCurve = color;
          */
          GralTableLine_ifc<GralCurveViewTrack_ifc> line = widgTableVariables.getCurrentLine();
          line.setTextColor(color);
          GralCurveViewTrack_ifc track = (GralCurveViewTrack_ifc)line.getContentInfo();
          if(track == trackScale){
            track.setLineProperties(color, 3, 0);  //change color immediately to see what happen
            trackScale.setLineProperties(color, ixTrackScale, ixTrackScale);
          } else {
            track.setLineProperties(color, 1, 0);  //change color immediately to see what happen
          }
          //trackScale.widgVarPath.setTextStyle(color, null);
      }
    } else {
      System.out.println("InspcCurveView - unexpected what");
    }
  } };

  
  
  /**Show the values of the given track in the {@link #widgValCursorLeft} and -Right
   * @param track from this track
   */
  void actionShowCursorValues ( GralCurveViewTrack_ifc track) {
    float valueCursorLeft = track.getValueCursorLeft();
    float valueCursorRight = track.getValueCursorRight();
    widgValCursorLeft.setText("" + valueCursorLeft);
    widgValCursorRight.setText("" + valueCursorRight);
    float td = widgCurve.getdTimeCursors();
    widgValdTime.setText("" + td);
  }
  
  
  public GralUserAction actionShowCursorValues = new GralUserAction("actionShowCursorValues"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(InspcCurveView.this.trackScale !=null){
        actionShowCursorValues(InspcCurveView.this.trackScale);
      }
      return true;
    }
  };
 
  

 
  

  public GralUserAction actionTrackSelectedFromGralCurveViewCtrlMousePressed = new GralUserAction("actionTrackSelected"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      //assert(false);
      GralCurveViewTrack_ifc trackNew = (GralCurveViewTrack_ifc)params[0];
      if(trackScale !=null && trackScale == trackNew) return true;  //do nothing.
      else {
        widgTableVariables.setCurrentLine(trackNew.getDataPath());
        //Note: the GralTable will call actionSelectVariableInTable and mark the line.
      }
      return true;
    }
  };
 
  

  protected void chgSelectedTrack(GralCurveViewTrack_ifc trackNew){
    assert(false);
    if(trackScale !=null){
      //last variable
      //trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
      if(trackScale !=null){
        trackScale.setLineProperties(null, 1, 0);
      }
    }
    trackScale = trackNew; //(GralCurveViewTrack_ifc)widgd.getContentInfo();
    if(trackScale == null){
      GralColor lineColor = GralColor.getColor("rd");
      trackScale = widgCurve.addTrack(sName, null, lineColor, 0, 50, 5000.0f, 0.0f);
    }
    //colorLineTrackSelected = trackScale.getLineColor();
    trackScale.setLineProperties(null, 3, 0);
    //trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
    widgScale.setText("" + trackScale.getScale7div());
    widgScale0.setText("" + trackScale.getOffset());
    widgline0.setText("" + trackScale.getLinePercent());
    widgCurve.repaint(100, 200);

  }
  
  
  
  
  
}
