package org.vishia.guiInspc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.communication.InspcDataExchangeAccess;
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
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;

public final class InspcCurveView
{

  /**Version, history and license. 
   * <ul>
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
  
  /**Three windows for curve view. */
  GralWindow windCurve;
  
  GralFileSelectWindow windFileCfg, windFileValues;
  
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
    final int ix;

    /**Color of the curve. */
    GralColor colorCurve;
    
    /**This is the rxAction joined with the track directly. Therefore an 'unused reference' here. */
    @SuppressWarnings("unused")
    //CurveCommRxAction rxActionRxValueByPath;
    
    TrackValues(int ix){ this.ix = ix; }
    
  }
  
  private final static String[] colorCurveDefault = new String[]{"rd", "gn", "lbl", "or", "ma", "bn", "dgn", "drd", "cy", "bl", "gn2", "pu"};
  
  
  /**The input field which is the current scaling field. */
  TrackValues trackScale;
  int ixTrackScale;
  
  //GralColor colorLineTrackSelected;

  final GralColor colorBlack = GralColor.getColor("bk");
  
  GralTable<TrackValues> widgTableVariables;
  
  GralTextField widgScale, widgScale0, widgline0;
  
  /**The one of {@link #widgScale0}, {@link #widgScale0} or {@link #widgline0} which was focused lastly.
   * To use for [+] and [-] button.
   */
  GralTextField scalingWidg = widgScale;
  
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
    int windProps = GralWindow.windConcurrently; // | GralWindow.windResizeable;
    windCurve = gralMng.createWindow("windMapVariables", sName, windProps);
    //gralMng.setPosition(2, GralGridPos.size-1.6f, 0, 3.8f, 0, 'd');
    buildGraphicInCurveWindow(common);
    wind.addMenuBarItemGThread("menuBarCurveView", "&Window/open " + sName, actionOpenWindow);
  }

  
  
  void buildGraphicInCurveWindow(GralCurveView.CommonCurve common)
  {
    int posright = -20;
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
    widgTableVariables.setActionChange(actionEnterDatapath);
    widgTableVariables.addContextMenuEntryGthread(0, null, "insert variable", actionInsertVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "replace variable", actionReplaceVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "swap variable", actionSwapVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "shift variable", actionShiftVariable);
    widgTableVariables.addContextMenuEntryGthread(0, null, "set color", actionColorSelectorOpen);
    widgTableVariables.addContextMenuEntryGthread(0, null, "set scale", actionSetScaleValues2Track);
    gralMng.setPosition(/*22*/-19, GralPos.size +3, -8, 0, 0, 'd', 0);
    widgScale = gralMng.addTextField("scale", true, "scale/div", "t");
    widgScale.setActionFocused(actionFocusScaling);
    widgScale0 = gralMng.addTextField("scale0", true, "mid", "t");
    widgScale0.setActionFocused(actionFocusScaling);
    widgline0 = gralMng.addTextField("line0", true, "line-%", "t");
    widgline0.setActionFocused(actionFocusScaling);
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
    widgBtnScale = gralMng.addButton("btnScale", actionColorSelectorOpen, "!", null,  "color");  
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
  
    windFileCfg = new GralFileSelectWindow("windFileCfg", gralMng);
    windFileValues = new GralFileSelectWindow("windFileValues", gralMng);
    
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
        input = new TrackValues(-1);
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
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { windCurve.setWindowVisible(true);
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
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { return dropVariable(actionCode, widgd, false);
    }
  };

  
  GralUserAction actionInsertVariable = new GralUserAction("actionInsertVariable"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { return dropVariable(actionCode, widgd, true);
    }
  };


  public GralUserAction actionEnterDatapath = new GralUserAction("actionEnterDatapath"){
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params){
      if(key == KeyCode.enter){
        @SuppressWarnings("unchecked")  //compatible to Java-6
        GralTable<TrackValues>.TableLineData line = (GralTable.TableLineData)params[0];
        String sDatapath = line.getCellText(0);
        InspcCurveView.this.setDatapath(line, sDatapath);
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

  

  GralUserAction actionSetScaleValues2Track = new GralUserAction("actionSetScaleValues2Track"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode) && trackScale !=null){
        boolean setScale = false;
        TrackValues dst;
        if(widgd.sCmd == null){ //from menu
          GralTable<TrackValues>.TableLineData refline = widgTableVariables.getLineMousePressed();
          dst = refline.getUserData();
          setScale = true;
        } else {
          dst = trackScale;
        }
        if(widgd.sCmd == "-"){
          try{
            String s1 = widgScale.getText();
            float value = Float.parseFloat(s1);
            int exp = (int)Math.log10(value);
            float base = (float)Math.pow(10.0, exp);
            value = value/2;
            s1 = Float.toString(value);
            widgScale.setText(s1);
            setScale = true;
          } catch(NumberFormatException exc){
            widgScale.setBackColor(GralColor.getColor("lrd"),0);
          }
        }
        else if(widgd.sCmd == "+"){
          try{
            String s1 = widgScale.getText();
            float value = Float.parseFloat(s1);
            int exp = (int)Math.log10(value);
            float base = (float)Math.pow(10.0, exp);
            value = value*2;
            s1 = Float.toString(value);
            widgScale.setText(s1);
            setScale = true;
          } catch(NumberFormatException exc){
            widgScale.setBackColor(GralColor.getColor("lrd"),0);
          }
        }
        else if(dst !=null && (  setScale   //maybe cmd '+' or '-', maybe menu 
                               || widgd.sCmd == "!"  //set key
                )              ){  
          try{
            String s1 = widgScale.getText();
            float scale = Float.parseFloat(s1);
            s1 = widgScale0.getText();
            float scale0 = Float.parseFloat(s1);
            s1 = widgline0.getText();
            int line0 = (int)Float.parseFloat(s1);
            //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
            if(dst.trackView == null){
              dst.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
            }
            dst.trackView.setTrackScale(scale, scale0, line0);
            widgBtnScale.setLineColor(GralColor.getColor("lgn"),0);
          } catch(NumberFormatException exc){
            widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
          }
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
      @SuppressWarnings("unchecked")
      GralTableLine_ifc<TrackValues> line = (GralTableLine_ifc<TrackValues>)params[0];
      if(line !=null){
        TrackValues track = (TrackValues)line.getContentInfo();
        if(track !=null){
          if(trackScale !=null && trackScale.trackView !=null){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 1, 0);
          }
          InspcCurveView.this.trackScale = track;
          if(trackScale.trackView !=null){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 3, 0);
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
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up){
        try{
          if(widgd.getCmd().equals(sBtnReadCfg)){
            windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), false, actionReadCfg);
          } else if(widgd.getCmd().equals(sBtnSaveCfg)){
            windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), true, actionSaveCfg);
          } else if(widgd.getCmd().equals(sBtnReadValues)){
            windFileCfg.openDialog(dirCurveSave, "read values", false, actionReadValues);
          } else if(widgd.getCmd().equals(sBtnSaveValues)){
            windFileCfg.openDialog(dirCurveSave, "write values", true, actionSaveValues);
          }
        } catch(Exception exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionReadCfg = new GralUserAction("actionReadCfg"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
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
              List<? extends GralCurveViewTrack_ifc> listTracks = widgCurve.getTrackInfo();
              widgTableVariables.clearTable();
              int iTrack = 0;
              for(GralCurveViewTrack_ifc track: listTracks){
                TrackValues trackValue = new TrackValues(-1); //tracks[iTrack];
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
        windFileCfg.closeWindow();
      }
      return true;
  } };
  
  
  

  GralUserAction actionSaveCfg = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
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
        windFileCfg.closeWindow();
      }
      return true;
  } };
  
  
  
  
  /**Action invoked if the read file was selected in the {@link GralFileSelectWindow}
   */
  GralUserAction actionReadValues = new GralUserAction("actionReadValues"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
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
        windFileCfg.closeWindow();
      }
      return true;
  } };
  

  /**Action invoked if the write file was selected in the {@link GralFileSelectWindow}
   */
  GralUserAction actionSaveValues = new GralUserAction("actionSaveValues"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
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
        windFileCfg.closeWindow();
      }
      return true;
  } };
  

  public GralUserAction actionFocusScaling = new GralUserAction("actionSetFocusScaling"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){
      if(scalingWidg !=null){
        scalingWidg.setBackColor(GralColor.getColor("wh"), 0);   //current, old  
      }
      scalingWidg = (GralTextField)widgd;
      scalingWidg.setBackColor(GralColor.getColor("lam"), 0);  //the new one
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
  
  
  
  
  protected void showValues(){
    float[] values = new float[tracks.length];
    //int ix = 0;
    assert(false);
    for(TrackValues inp: tracks){
      int ix = inp.trackView.getIxTrack();
      values[ix] = inp.val;
      //ix +=1;
    }
    int time = (int)System.currentTimeMillis();
    widgCurve.setSample(values, time);
  }
  
  
  
}
