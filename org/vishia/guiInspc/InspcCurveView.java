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
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralCurveView_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.gral.widget.GralFileSelectWindow;
import org.vishia.gral.widget.GralColorSelector.SetColorFor;
import org.vishia.gral.widget.GralFileSelector;
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
   *   are created newly using {@link GralCurveView#initTrack(String, String, GralColor, int, int, float, float)}.
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
  
  /**Used for read/write config and for read/write data*/
  private final GralFileSelector widgFileSelector;
  
  private String sWhatTodoWithFile;
  
  /**Shows the name and input name for read/write config and data. */
  private final GralTextField widgFilename;

  
  
  final GralMng gralMng;
  
  final VariableContainer_ifc variables;
  
  private static final class TrackValues{

    /**The reference to the track in {@link GralCurveView#initTrack(String, String, GralColor, int, int, float, float)}.
     * If this field is null, there is no track associated to this field. 
     * The {@link GralCurveViewTrack_ifc#getIxTrack()} may not be equal to the index of this instance
     * inside the {@link InspcCurveView#tracks} array because the tracks may be swapped and shifted.
     * See {@link InspcCurveView#actionSwapVariable} and {@link InspcCurveView#actionShiftVariable} 
     * */
    GralCurveViewTrack_ifc trackView;
    
    /**Visible information about the shown variable. The path may be the closest presentation what are shown. */
    //GralTextField widgVarPath;
    
    //GralTextField widgScale;
    
    //GralTextField widgBit;
    
    //GralTextField widgComment;

    
    //GralWidget XXXwidgetVariable;
    
    /**The value for this curve. */
    float val;
    
    float min,max,mid;
    
    //float scale, scale0;
    
    /**Percent value of 0-line. 0..100 
     * 
     */
    //int line0;
    
    /**Marked with true if it is the last variable, forces output to graphic. */
    boolean bLast;
    
    /**Index in the array where this is member of. */
    //final int ix;

    /**Color of the curve. */
    GralColor colorCurve;
    
    /**This is the rxAction joined with the track directly. Therefore an 'unused reference' here. */
    //@SuppressWarnings("unused")
    //CurveCommRxAction rxActionRxValueByPath;
    
    TrackValues(){ } 
    
  }
  
  private final static String[] colorCurveDefault = new String[]{"rd", "gn", "lbl", "or", "ma", "bn", "dgn", "drd", "cy", "bl", "gn2", "pu"};
  
  
  /**The input field which is the current scaling field. */
  TrackValues trackScale;
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
  
  GralTable<TrackValues> widgTableVariables;
  
  GralTextField widgScale, widgScale0, widgline0;
  
  /**The one of {@link #widgScale0}, {@link #widgScale0} or {@link #widgline0} which was focused lastly.
   * To use for [+] and [-] button.
   */
  GralTextField scalingWidg;
  
  GralTextField widgValCursorLeft, widgValCursorRight; ///
  
  GralButton widgBtnUp, widgBtnDn, widgBtnScale, widgBtnReadCfg, widgBtnSaveCfg;
  
  GralButton widgBtnReadValues, widgBtnSaveValues, wdgButtonAutosave, widgBtnColor; 
  
  
  /**
   * 
   */
  final TrackValues[] tracks = new TrackValues[12];
  
  GralButton widgBtnOff;
  
  /**The currently loaded file for curve settings. */
  FileRemote fileCurveCfg;
  
  FileRemote dirCurveSave;
  
  //long timeLastSave;
  
  //long timePeriodSave = 1000L * 60 * 2;
  
  //boolean hasDataTosave;
  
  /**Temporary used formatter. */
  StringFormatter sFormatter = new StringFormatter(50);
  
  /**Common ColorSelector for all curve views. */
  GralColorSelector colorSelector;
  
  
  
  //final InspcGuiComm comm;
  
  GralCurveView widgCurve;
  
  /**Creates the instance. The graphical appearance will not be created. 
   * Call {@link #buildGraphic(GralPrimaryWindow_ifc, GralColorSelector)} to do that.
   * @param sName Name shown in title bar
   * @param variables Container to find variables
   * @param gralMng The Gral Graphic Manager
   * @param defaultDir
   * @param curveExporterClasses Class which is used to export curves.
   */
  InspcCurveView(String sName, VariableContainer_ifc variables, GralMng gralMng, FileRemote defaultDirCfg, FileRemote defaultDirSave, Map<String, String> curveExporterClasses){
    //this.comm = comm;
    this.sName = sName;
    this.curveExporterClasses = curveExporterClasses;
    this.variables = variables;
    this.gralMng = gralMng;
    fileCurveCfg = defaultDirCfg;
    dirCurveSave = defaultDirSave;
    widgFileSelector = new GralFileSelector("-selectFile", 100, new int[]{2,0,-6,-12}, 'C');
    widgFileSelector.specifyActionOnFileSelected(actionSelectFile);
    widgFileSelector.setActionOnEnterFile(actionEnterFile);

    widgFilename = new GralTextField("-filename", GralTextField.Type.editable);
     
  }
  
  
  /**Builds the graphic, it should be called only one time on startup in the graphic thread
   * @param wind The main window where the menu to open will be added
   * @param sName The name, used for menu entry too, sample "curve A"
   */
  public void buildGraphic(GralWindow_ifc wind, GralColorSelector colorSelector, GralCurveView.CommonCurve common)
  {
    this.colorSelector = colorSelector;
    gralMng.selectPanel("primaryWindow");
    //gralMng.setPosition(4, 0, 4, 0, 0, '.');
    gralMng.setPosition(4, 56, 4, 104, 0, '.');
    //int windProps = GralWindow.windConcurrently | GralWindow.windOnTop | GralWindow.windResizeable;
    int windProps = GralWindow.windConcurrently | GralWindow.windOnTop; // | GralWindow.windResizeable;
    windCurve = gralMng.createWindow("windMapVariables", sName, windProps);
    //gralMng.setPosition(2, GralGridPos.size-1.6f, 0, 3.8f, 0, 'd');
    buildGraphicInCurveWindow(common);
    GralMenu menu = wind.getMenuBar();
    menu.addMenuItemGthread("&Window/open " + sName, actionOpenWindow);
  }

  
  
  void buildGraphicInCurveWindow(GralCurveView.CommonCurve common)
  {
    int posright = -20;
    gralMng.setPosition(0, -4, 0, posright, 0, 'd');
    widgFileSelector.setToPanel();
    widgFileSelector.setVisible(false);
    //widgFileSelector.set
    //widgFileSelector.specifyActionOnFileSelected(actionSelectFile);
    //widgFileSelector.setActionOnEnterFile(actionOk);
    gralMng.setPosition(-4, -2, 0, posright, 0, 'd');
    widgFilename.setToPanel();
    widgFilename.setVisible(false);
    widgFilename.setText("TEST xyz");
    gralMng.setPosition(0, -2, 0, posright, 0, 'd');
    widgCurve = gralMng.addCurveViewY(sName, 15000, common);
    widgCurve.setActionMoveCursor(actionShowCursorValues);
    widgCurve.setActionTrackSelected(actionTrackSelectedFromGralCurveViewCtrlMousePressed);
    gralMng.setPosition(0, GralPos.size +2, posright, 0, 0, 'd', 0);
    gralMng.addText("curve variable");
    widgTableVariables = new GralTable<TrackValues>("variables", new int[]{-posright});
    gralMng.setPosition(2, GralPos.size +20, posright, 0, 0, 'd', 0);
    widgTableVariables.setColumnEditable(0, true);
    widgTableVariables.setToPanel(gralMng);
    widgTableVariables.specifyActionOnLineSelected(actionSelectVariableInTable);
    widgTableVariables.setActionChange(actionKeyHandlingTable);
    //widgTableVariables.set
    widgTableVariables.addContextMenuEntryGthread(0, null, "switch on-off <F2>", actionOnOffTrack);
    widgTableVariables.addContextMenuEntryGthread(0, null, "insert variable", actionInsertVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "replace variable", actionReplaceVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "swap variable", actionSwapVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "delete variable", actionDeleteVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "shift variable", actionShiftVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "set color", actionColorSelectorOpen);
    widgTableVariables.addContextMenuEntryGthread(0, null, "group scale", actionShareScale);
    widgTableVariables.addContextMenuEntryGthread(0, null, "ungroup scale", actionUnshareScale);
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
    gralMng.setPosition(-22, GralPos.size +3, posright, GralPos.size +11, 0, 'd', 0);
    widgValCursorLeft = gralMng.addTextField(null, true, "cursor left", "t");
    widgValCursorRight = gralMng.addTextField(null, true, "cursor right", "t");
    gralMng.setPosition(-15, GralPos.size +2, posright, GralPos.size +8, 0, 'd', 1);
    widgBtnReadCfg = gralMng.addButton("btnReadCfg", actionOpenFileDialog, sBtnReadCfg, null, sBtnReadCfg);
    widgBtnSaveCfg = gralMng.addButton("btnSaveCfg", actionOpenFileDialog, sBtnSaveCfg, null, sBtnSaveCfg);
    widgBtnReadValues = gralMng.addButton("btnReadValues", actionOpenFileDialog, sBtnReadValues, null, sBtnReadValues);
    widgBtnSaveValues = gralMng.addButton("btnSaveValues", actionOpenFileDialog, sBtnSaveValues, null, sBtnSaveValues);
    wdgButtonAutosave = gralMng.addSwitchButton("btnAutoSaveValues", "off-autosave", "on-autosave", GralColor.getColor("lgn"), GralColor.getColor("am") );
    
    gralMng.setPosition(-3, GralPos.size +2, -9, -1, 0, 'd', 0);
    widgBtnOff = gralMng.addSwitchButton(sName + "btnOff", "off / ?on", "on / ?off", GralColor.getColor("lgn"), GralColor.getColor("am"));
  
    
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
      //GralTextField widgText = (GralTextField)widgd;
      //String sVariable = variableWidget.name;
      Object oContentInfo = widgd.getContentInfo();
      String sPath = variableWidget.getDataPath();
      TrackValues input;
      assert(widgd instanceof GralTable<?>);  //NOTE: context menu to table lines, has GralTable as widget.
      @SuppressWarnings("unchecked")
      GralTable<TrackValues> table = (GralTable<TrackValues>)widgd; //oContentInfo;
      GralTable<TrackValues>.TableLineData refline = table.getLineMousePressed();
      if(bInsert || refline == null){  //insert a line, build a new one
        input = new TrackValues();
        GralTableLine_ifc<?> newline;
        if(refline ==null){
          newline = table.addLine(sPath, new String[]{""}, input);  //add on end
        } else {
          newline = refline.addPrevLine(sPath, new String[]{""}, input);
        }
        newline.setCellText(sPath, 0);
      } 
      else {
        input = (TrackValues)refline.getContentInfo();
        refline.setCellText(sPath, 0);
      }
      input.min = Float.MAX_VALUE;
      input.max = -Float.MAX_VALUE;
      input.mid = 0.0f;
      //String sShowMethod = variableWidget.getShowMethod();
      if(sPath !=null){
        if(input.trackView == null){
          //A new trackview, in Table in the required order, in the widgCurve added on end.
          //The order of tracks in widgCurve are not the same like in table.
          input.trackView = widgCurve.initTrack(sName, sPath, input.colorCurve, 0, 50, 5000.0f, 0.0f);
        } else {
          input.trackView.setDataPath(sPath);
        }
      } else {
        System.out.printf("InspcCurveView - invalid widget to drop; %s\n", variableWidget.toString());
      }
      table.repaint();
    }
    return true;
    
  }
  
  
  
  protected boolean deleteVariable(int actionCode, GralWidget widgd, boolean bInsert){
    if(actionCode == KeyCode.menuEntered){
      TrackValues input;
      assert(widgd instanceof GralTable<?>);  //NOTE: context menu to table lines, has GralTable as widget.
      @SuppressWarnings("unchecked")
      GralTable<TrackValues> table = (GralTable<TrackValues>)widgd; //oContentInfo;
      GralTable<TrackValues>.TableLineData refline = table.getLineMousePressed();
      if(refline != null){  
        input = (TrackValues)refline.getContentInfo();
        if(input.trackView != null) {
          Debugutil.stop();
          //TODO
          //refline.deleteLine();
          //delete the track line!
          
          
      } }
     
      table.repaint();
    }
    return true;
    
  }
  
  
  
  void setDatapath(GralTable<TrackValues>.TableLineData line, String sDatapath){
    TrackValues input = line.data;
    if(input.trackView == null){
      input.trackView = widgCurve.initTrack(sName, sDatapath, input.colorCurve, 0, 50, 5000.0f, 0.0f);
    } else {
      input.trackView.setDataPath(sDatapath);
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
      //fileCurveSave.mkdirs();
      FileRemote fileCurveSave = dirCurveSave.child(sNameFile + ".csv");
      FileSystem.mkDirPath(fileCurveSave);
      System.out.println("InspcCurveView - save curve data to; " + fileCurveSave.getAbsolutePath());
      writerCurveCsv.setFile(fileCurveSave);
      widgCurve.writeCurve(writerCurveCsv, mode);
      
      String sClassExportDat = curveExporterClasses.get("dat");
      if(sClassExportDat !=null){
        Class<?> clazzCurveWriter2 = Class.forName(sClassExportDat);
        
        WriteCurve_ifc writerCurve2 = (WriteCurve_ifc)clazzCurveWriter2.newInstance();
        File fileDat = new File(dirCurveSave, sNameFile + ".dat");
        
        writerCurve2.setFile(fileDat);
        widgCurve.writeCurve(writerCurve2, mode);
      }
      System.out.println("InspcCurveView - data saved; " + fileCurveSave.getAbsolutePath());

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
      TrackValues inpLast = null;
      for(TrackValues inp: tracks){
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

  
  GralUserAction actionOnOffTrack = new GralUserAction("actionOnOffTrack"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(trackScale.trackView.getVisible() ==0) {
        trackScale.trackView.setVisible(2);
        //line.setBackColor(colorTrackSameScale, -1);
      } else {
        trackScale.trackView.setVisible(0);
        //line.setBackColor(GralColor.getColor("gr"), -1);
      }
      GralTable<TrackValues>.TableLineData line = widgTableVariables.getCurrentLine();
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
        GralTable<TrackValues>.TableLineData line = (GralTable.TableLineData)params[0];
        String sDatapath = line.getCellText(0);
        InspcCurveView.this.setDatapath(line, sDatapath);
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
        GralTable<TrackValues> table = (GralTable<TrackValues>) widgd;
        GralTableLine_ifc<TrackValues> linesrc = table.getCurrentLine();
        GralTableLine_ifc<TrackValues> linedst = table.getLineMousePressed();
        /*
        if(linedst !=null && linesrc != linedst){
          TrackValues trackDst = linedst.getUserData();  //(TrackValues)oContent;
          //if(trackDst != trackScale){
            String sPathSwap = trackDst.trackView.getDataPath();
            GralColor colorSwap = trackDst.trackView.getLineColor(); //trackDst.colorCurve; //  
            GralCurveViewTrack_ifc trackViewSwap = trackDst.trackView;
            String sPathDst = trackScale.trackView.getDataPath();
            GralColor colorDst = trackScale.trackView.getLineColor();  //colorCurve; //  
            GralCurveViewTrack_ifc trackViewDst = trackScale.trackView;
            //set paths to other.
            trackScale.colorCurve = colorSwap;
            linesrc.setCellText(sPathSwap, 0);
            //linesrc.setKey(sPathSwap);
            trackScale.trackView.setDataPath(sPathSwap);
            linesrc.setTextColor(colorSwap);
            trackScale.trackView = trackViewSwap;
            //the new one can be an empty track:
            trackDst.colorCurve = colorDst;
            trackDst.trackView.setDataPath(sPathDst);
            lineDst.setCellText(sPathDst, 0);
            lineDst.setTextColor(colorDst);
            trackDst.trackView = trackViewDst;
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
        GralTable<TrackValues> table = (GralTable<TrackValues>) widgd;
        GralTableLine_ifc<TrackValues> linesrc = table.getCurrentLine();
        GralTableLine_ifc<TrackValues> linedst = table.getLineMousePressed();
        /*
        //save values of the current selected.
        if(trackScale != trackDst){
          
          String sPathSel = trackScale.widgVarPath.getText();
          GralColor colorSel = trackScale.trackView.getLineColor();  //colorCurve; //  
          GralCurveViewTrack_ifc trackViewSel = trackScale.trackView;
          if(trackScale.ix > trackDst.ix){  //shift current selected up, shift rest down
            int ix;
            for(ix = trackScale.ix -1; ix >= trackDst.ix; --ix){
              String sPath1 = tracks[ix].widgVarPath.getText();
              GralColor color1 = tracks[ix].trackView.getLineColor();  //colorCurve; //  
              GralCurveViewTrack_ifc trackView1 = tracks[ix].trackView;
              tracks[ix+1].colorCurve = color1;
              tracks[ix+1].widgVarPath.setTextColor(color1);
              tracks[ix+1].widgVarPath.setText(sPath1);
              tracks[ix+1].trackView = trackView1;
            }
            //write into dst selected.
            trackDst.colorCurve = colorSel;
            trackDst.widgVarPath.setText(sPathSel);
            trackDst.widgVarPath.setTextColor(colorSel);
            trackDst.trackView = trackViewSel;
          } else { //shift current selected down, shift rest up
            int ix;
            for(ix = trackScale.ix; ix < trackDst.ix-1; ++ix){
              String sPath1 = tracks[ix+1].widgVarPath.getText();
              GralColor color1 = tracks[ix+1].trackView.getLineColor();  //colorCurve; //  
              GralCurveViewTrack_ifc trackView1 = tracks[ix+1].trackView;
              tracks[ix].colorCurve = color1;
              tracks[ix].widgVarPath.setTextColor(color1);
              tracks[ix].widgVarPath.setText(sPath1);
              tracks[ix].trackView = trackView1;
            }
            //write into dst selected.
            tracks[ix].colorCurve = colorSel;
            tracks[ix].widgVarPath.setText(sPathSel);
            tracks[ix].widgVarPath.setTextColor(colorSel);
            tracks[ix].trackView = trackViewSel;
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
        GralTable<TrackValues>.TableLineData refline = widgTableVariables.getLineMousePressed();
        TrackValues dst = refline.getUserData();     //the line which should be entered in the scale group
        dst.trackView.groupTrackScale(trackScale.trackView);  //trackScale is the line which was  previously selected.
      }
      return true;
    }
  };
  
  
  GralUserAction actionUnshareScale = new GralUserAction("actionShareScale"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if( KeyCode.isControlFunctionMouseUpOrMenu(actionCode) && trackScale !=null){
        GralTable<TrackValues>.TableLineData refline = widgTableVariables.getLineMousePressed();
        TrackValues dst = refline.getUserData();
        dst.trackView.ungroupTrackScale();  
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
        TrackValues dst;
        if(actionCode == KeyCode.enter) {
          dst = trackScale; //TEST
          setScale = true;
        }
        else if(actionCode == KeyCode.menuEntered && widgd.sCmd == null){ //from menu
          assert(false);  //unused since shareTrackScale
          GralTable<TrackValues>.TableLineData refline = widgTableVariables.getLineMousePressed();
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
            if(dst.trackView == null){
              dst.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
            }
            widgCurve.repaint(500,500);
            dst.trackView.setTrackScale(scale, scale0, line0);
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
      GralTableLine_ifc<TrackValues> line = (GralTableLine_ifc<TrackValues>)params[0];
      if(line !=null){
        TrackValues track = line.getUserData(); //(TrackValues)line.getContentInfo();
        if(track !=null){
          //set the background color of all line with the same scaling.
          if( track.trackView ==null //deselect scaled lines
            || trackScale !=null && !track.trackView.isGroupedTrackScale(trackScale.trackView) //don't do if it is the same scale group
            || actionCode == KeyCode.F2 //invoked from actionOnOffTrack
            ) {
            for(GralTableLine_ifc<TrackValues> line1: widgTableVariables.iterLines()) { ////
              TrackValues track1 = line1.getUserData();
              if(track1.trackView !=null && track1.trackView.getVisible() == 0) {
                line1.setBackColor(colorTrackNotShown, colorTrackNotShownSelected, colorTrackNotShownSelected, colorTrackNotShownSelected, colorTrackNotShownSelected, -1);
              }
              else if(track1.trackView !=null && track.trackView!=null && track1.trackView.isGroupedTrackScale(track.trackView)){
                line1.setBackColor(colorTrackSameScale, colorTrackSameScaleSelected, colorTrackSameScaleSelected, colorTrackSameScaleSelected, colorTrackSameScaleSelected, -1);  //same scale
              } else {
                line1.setBackColor(colorTrackOtherScale, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, colorTrackOtherScaleSelected, -1);
              }
            }
            widgTableVariables.repaint(500,500);
          }
          //set the line from the last selection to normal
          if(trackScale !=null && trackScale.trackView !=null){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 1, 0);
            if(trackScale.trackView.getVisible() !=0){
              trackScale.trackView.setVisible(1);
            }
          }
          //set the track bold and show the scaling of the track 
          InspcCurveView.this.trackScale = track;
          if(trackScale.trackView !=null && trackScale.trackView.getVisible() !=0){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 3, 0);
            trackScale.trackView.setVisible(2);
            widgScale.setText("" + track.trackView.getScale7div());
            widgScale0.setText("" + track.trackView.getOffset());
            widgline0.setText("" + track.trackView.getLinePercent());
          }
          System.out.println("InspcCurveView.action - SelectVariableInTable");
          widgCurve.repaint(100, 200);
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
        chgSelectedTrack((TrackValues)widgd.getContentInfo());
      }
      else if(actionCode == (KeyCode.mouse1Down | KeyCode.ctrl)){
        if(trackScale !=null){
          //last variable
          //trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
          if(trackScale.trackView !=null){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 1, 0);
          }
        }
        trackScale = (TrackValues)widgd.getContentInfo();
        if(trackScale.trackView == null){
          trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
        }
        //colorLineTrackSelected = trackScale.trackView.getLineColor();
        trackScale.trackView.setLineProperties(trackScale.colorCurve, 3, 0);
        //trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
        try{
          String s1 = widgScale.getText();
          float scale = Float.parseFloat(s1);
          s1 = widgScale0.getText();
          float scale0 = Float.parseFloat(s1);
          s1 = widgline0.getText();
          int line0 = (int)Float.parseFloat(s1);
          //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
          trackScale.trackView.setTrackScale(scale, scale0, line0);
        } catch(NumberFormatException exc){
          System.err.println("InspcCurveView - read scale values format error.");
        }
      }
      else if(actionCode == KeyCode.focusLost && widgd.isChanged(true)){
        String sPath = (widg).getValue();
        if(sPath.length() >0 && !sPath.endsWith(".")){
          TrackValues track = (TrackValues)widgd.getContentInfo();
          track.trackView.setDataPath(sPath);
        }
      }
      return true;
    }
  };
  
  
  
  GralUserAction actionOpenFileDialog = new GralUserAction("OpenFileDialog"){
    @SuppressWarnings("synthetic-access") @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up){
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
            final FileRemote dir;
            if(sCmd.equals(sBtnReadCfg)){
              dir = fileCurveCfg;
            } else if(widgd.getCmd().equals(sBtnSaveCfg)){
              //windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), true, actionSaveCfg);
              dir = fileCurveCfg;
            } else if(widgd.getCmd().equals(sBtnReadValues)){
              //windFileCfg.openDialog(dirCurveSave, "read values", false, actionReadValues);
              dir = dirCurveSave;
            } else if(widgd.getCmd().equals(sBtnSaveValues)){
              //windFileCfg.openDialog(dirCurveSave, "write values", true, actionSaveValues);
              dir = dirCurveSave;
            } else {
              dir = null; //unexpected
            }
            //windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), false, actionReadCfg);
            widgCurve.setVisible(false);
            widgFilename.setVisible(true);
            widgFileSelector.setVisible(true);
            widgFileSelector.fillIn(dir, false);
            widgFileSelector.setFocus();
          }
        } catch(Exception exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        }
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
            Iterable<? extends GralCurveViewTrack_ifc> listTracks = widgCurve.getTrackInfo();
            widgTableVariables.clearTable();
            int iTrack = 0;
            for(GralCurveViewTrack_ifc track: listTracks){
              TrackValues trackValue = new TrackValues(); //tracks[iTrack];
              trackValue.trackView = track;   //sets the new Track to the text field's data association.
              String sDataPath = track.getDataPath();
              String[] sCellTexts = new String[1];
              sCellTexts[0] = sDataPath;
              trackValue.colorCurve = track.getLineColor();
              GralTableLine_ifc<TrackValues> line = widgTableVariables.addLine(sDataPath, sCellTexts, trackValue);
              line.setTextColor(trackValue.colorCurve);
              iTrack +=1;
            }
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
        List<TrackValues> listTable = widgTableVariables.getListContent();
        for(TrackValues trackValue: listTable){
          GralCurveViewTrack_ifc track = trackValue.trackView;
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
        dirCurveSave = file.getParentFile();
        
        readCurve(file);
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
        dirCurveSave = (FileRemote)params[0];
        if(dirCurveSave.exists() && !dirCurveSave.isDirectory()){
          dirCurveSave = dirCurveSave.getParentFile();
        }
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
          if(trackScale.trackView == null){
            trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
          }
          trackScale.trackView.setLineProperties(color, 3, 0);  //change color immediately to see what happen
          trackScale.colorCurve = color;
          */
          GralTableLine_ifc<TrackValues> line = widgTableVariables.getCurrentLine();
          line.setTextColor(color);
          TrackValues track = (TrackValues)line.getContentInfo();
          if(track == trackScale){
            track.trackView.setLineProperties(color, 3, 0);  //change color immediately to see what happen
            trackScale.colorCurve = color;
          } else {
            track.trackView.setLineProperties(color, 1, 0);  //change color immediately to see what happen
          }
          //trackScale.widgVarPath.setTextStyle(color, null);
      }
    } else {
      System.out.println("InspcCurveView - unexpected what");
    }
  } };

  
  public GralUserAction actionShowCursorValues = new GralUserAction("actionShowCursorValues"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(trackScale !=null && trackScale.trackView !=null){
        float valueCursorLeft = trackScale.trackView.getValueCursorLeft();
        float valueCursorRight = trackScale.trackView.getValueCursorRight();
        widgValCursorLeft.setText("" + valueCursorLeft);
        widgValCursorRight.setText("" + valueCursorRight);
      }
      return true;
    }
  };
 
  

  public GralUserAction XXXactionTrackSelected = new GralUserAction("actionTrackSelected"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      assert(false);
      GralCurveViewTrack_ifc trackNew = (GralCurveViewTrack_ifc)params[0];
      if(trackScale !=null && trackScale.trackView == trackNew) return true;  //do nothing.
      else {
        TrackValues trackValueNew = null;
        for(TrackValues trackValue: tracks){
          if(trackValue.trackView == trackNew){
            trackValueNew = trackValue;
            break;
          }
        }
        if(trackValueNew !=null){
          chgSelectedTrack(trackValueNew);
        }
      }
      return true;
    }
  };
 
  

  public GralUserAction actionTrackSelectedFromGralCurveViewCtrlMousePressed = new GralUserAction("actionTrackSelected"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      //assert(false);
      GralCurveViewTrack_ifc trackNew = (GralCurveViewTrack_ifc)params[0];
      if(trackScale !=null && trackScale.trackView == trackNew) return true;  //do nothing.
      else {
        widgTableVariables.setCurrentLine(trackNew.getDataPath());
        //Note: the GralTable will call actionSelectVariableInTable and mark the line.
      }
      return true;
    }
  };
 
  

  protected void chgSelectedTrack(TrackValues trackNew){
    assert(false);
    if(trackScale !=null){
      //last variable
      //trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
      if(trackScale.trackView !=null){
        trackScale.trackView.setLineProperties(trackScale.colorCurve, 1, 0);
      }
    }
    trackScale = trackNew; //(TrackValues)widgd.getContentInfo();
    if(trackScale.trackView == null){
      trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
    }
    //colorLineTrackSelected = trackScale.trackView.getLineColor();
    trackScale.trackView.setLineProperties(trackScale.colorCurve, 3, 0);
    //trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
    widgScale.setText("" + trackScale.trackView.getScale7div());
    widgScale0.setText("" + trackScale.trackView.getOffset());
    widgline0.setText("" + trackScale.trackView.getLinePercent());
    widgCurve.repaint(100, 200);

  }
  
  
  
  
  
}
