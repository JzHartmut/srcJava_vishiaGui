package org.vishia.guiInspc;

import java.io.File;
import java.io.FileWriter;
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
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralColorSelector;
import org.vishia.gral.widget.GralFileSelectWindow;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.gral.widget.GralColorSelector.SetColorFor;
import org.vishia.inspectorAccessor.InspcAccessEvaluatorRxTelg;
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Assert;
import org.vishia.util.FileRemote;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class InspcCurveView
{

  /**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
   * Changes:
   * <ul>
   * <li>2013-03-28 Harmut adapt new {@link GralFileSelectWindow}
   * <li>2013-03-27 Hartmut improved/bugfix: The {@link TrackValues#trackView} is the reference to the track in the 
   *   {@link GralCurveView} instance. If a new config is loaded all tracks in {@link GralCurveView#getTrackInfo()}
   *   arec reated newly using {@link GralCurveView#initTrack(String, String, GralColor, int, int, float, float)}.
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
  public final static int version = 20130327;

  public static String sBtnReadCfg = "read cfg";
  
  public static String sBtnSaveCfg = "save cfg";
  
  public static String sBtnReadValues = "read values";
  
  public static String sBtnSaveValues = "save values";
  
  
  private final String sName;
  
  private final Map<String, String> curveExporterClasses;
  
  private final WriteCurve_ifc writerCurveCsv = new WriteCurveCsv();
  
  /**Three windows for curve view. */
  GralWindow windCurve;
  
  GralFileSelectWindow windFileCfg, windFileValues;
  
  final GralMng gralMng;
  
  final VariableContainer_ifc variables;
  
  private static class TrackValues{

    /**The reference to the track in {@link GralCurveView#initTrack(String, String, GralColor, int, int, float, float)}.
     * If this field is null, there is no track associated to this field. */
    GralCurveViewTrack_ifc trackView;
    
    /**Visible information about the shown variable. The path may be the closest presentation what are shown. */
    GralTextField widgVarPath;
    
    //GralTextField widgScale;
    
    //GralTextField widgBit;
    
    //GralTextField widgComment;

    
    GralWidget XXXwidgetVariable;
    
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

    GralColor colorCurve;
    
    CurveCommRxAction rxActionRxValueByPath;
    
    TrackValues(){}
    
  }
  
  String[] colorCurveDefault = new String[]{"rd", "gn", "lbl", "or", "ma", "bn", "dgn", "drd", "cy", "bl"};
  
  
  /**The input field which is the current scaling field. */
  TrackValues trackScale;
  
  //GralColor colorLineTrackSelected;

  final GralColor colorBlack = GralColor.getColor("bk");
  
  GralTextField widgScale, widgScale0, widgline0;
  
  GralButton widgBtnUp, widgBtnDn, widgBtnScale, widgBtnReadCfg, widgBtnSaveCfg;
  
  GralButton widgBtnReadValues, widgBtnSaveValues, widgBtnColor; 
  
  
  /**
   * 
   */
  final TrackValues[] tracks = new TrackValues[10];
  
  GralButton widgBtnOff;
  
  /**The currently loaded file for curve settings. */
  FileRemote fileCurveCfg;
  
  FileRemote fileCurveSave;
  
  /**Common ColorSelector for all curve views. */
  GralColorSelector colorSelector;
  
  //final InspcGuiComm comm;
  
  GralCurveView widgCurve;
  
  InspcCurveView(String sName, VariableContainer_ifc variables, GralMng gralMng, FileRemote defaultDir, Map<String, String> curveExporterClasses){
    //this.comm = comm;
    this.sName = sName;
    this.curveExporterClasses = curveExporterClasses;
    this.variables = variables;
    this.gralMng = gralMng;
    fileCurveCfg = defaultDir;
    fileCurveSave = defaultDir;
  }
  
  
  /**Builds the graphic, it should be called only one time on startupt in the graphic thread
   * @param wind The main window where the menu to open will be added
   * @param sName The name, used for menu entry too, sample "curve A"
   */
  public void buildGraphic(GralPrimaryWindow_ifc wind, GralColorSelector colorSelector)
  {
    int posright = -20;
    this.colorSelector = colorSelector;
    gralMng.selectPanel("primaryWindow");
    gralMng.setPosition(4, 0, 4, 0, 0, '.');
    //int windProps = GralWindow.windConcurrently | GralWindow.windOnTop | GralWindow.windResizeable;
    int windProps = GralWindow.windConcurrently ; // | GralWindow.windResizeable;
    windCurve = gralMng.createWindow("windMapVariables", sName, windProps);
    //gralMng.setPosition(2, GralGridPos.size-1.6f, 0, 3.8f, 0, 'd');
    gralMng.setPosition(0, -2, 0, posright, 0, 'd');
    widgCurve = gralMng.addCurveViewY(sName, 3000, 10);
    gralMng.setPosition(0, GralPos.size +2, posright, 0, 0, 'd', 0);
    gralMng.addText("curve variable");
    for(int ii=0; ii<10; ++ii){
      final TrackValues track = new TrackValues();
      String sColor = colorCurveDefault[ii];
      track.colorCurve = GralColor.getColor(sColor);
      if(track.colorCurve ==null){ throw new IllegalArgumentException("InspcCurveView-unknown color; " + sColor); }
      track.rxActionRxValueByPath = new CurveCommRxAction(track);
      this.tracks[ii] = track;
      track.widgVarPath = gralMng.addTextField(null, false, sName, sName);
      track.widgVarPath.setTextColor(track.colorCurve);
      track.widgVarPath.setContentInfo(track);
      track.widgVarPath.setMouseAction(actionSetTrackForScale);
      //input[ii].widgPath.setMouseAction(actionDropVariable);
      tracks[ii].widgVarPath.setDataPath("widgetInfo");  //prevent storing the own widgetInfo.
      //input[ii].widgPath.setContentInfo(input[ii]);
      GralMenu menuWidg = track.widgVarPath.getContextMenu();
      GralWidget widgMenuItem1 = menuWidg.addMenuItemGthread(null, "drop variable", actionDropVariable);
      GralWidget widgMenuItem2 = menuWidg.addMenuItemGthread(null, "set color", actionColorSelectorOpen);
      GralWidget widgMenuItem3 = menuWidg.addMenuItemGthread(null, "set scale", actionSetScaleValues2Track);
      widgMenuItem1.setContentInfo(tracks[ii]);
      widgMenuItem2.setContentInfo(tracks[ii]);
      widgMenuItem3.setContentInfo(tracks[ii]);
      track.trackView = widgCurve.initTrack(sName, null, track.colorCurve, ii, 50, 5000.0f, 0.0f);
    }
    gralMng.setPosition(22, GralPos.size +3, -10, 0, 0, 'd', 0);
    widgScale = gralMng.addTextField("scale", true, "scale/div", "t");
    widgScale0 = gralMng.addTextField("scale0", true, "mid", "t");
    widgline0 = gralMng.addTextField("line0", true, "line-%", "t");
    gralMng.setPosition(32, GralPos.size +2, -10, GralPos.size +2, 0, 'r', 1);
    widgBtnDn = gralMng.addButton("btnDn", actionSetScaleValues2Track, "-", null,  "-");
    widgBtnUp = gralMng.addButton("btnUp", actionSetScaleValues2Track, "+", null, "+");
    gralMng.setPosition(GralPos.same, GralPos.size +2, GralPos.next, GralPos.size +4, 0, 'r', 1);
    widgBtnScale = gralMng.addButton("btnScale", actionColorSelectorOpen, "!", null,  "color");  
    gralMng.setPosition(35, GralPos.size +2, -10, GralPos.size +6, 0, 'r', 1);
    widgBtnScale = gralMng.addButton("btnScale", actionSetScaleValues2Track, "!", null,  "set");
    gralMng.setPosition(23, GralPos.size +2, posright, GralPos.size +8, 0, 'd', 1);
    widgBtnReadCfg = gralMng.addButton("btnReadCfg", actionOpenFileDialog, sBtnReadCfg, null, sBtnReadCfg);
    widgBtnSaveCfg = gralMng.addButton("btnSaveCfg", actionOpenFileDialog, sBtnSaveCfg, null, sBtnSaveCfg);
    widgBtnReadValues = gralMng.addButton("btnReadValues", actionOpenFileDialog, sBtnReadValues, null, sBtnReadValues);
    widgBtnSaveValues = gralMng.addButton("btnSaveValues", actionOpenFileDialog, sBtnSaveValues, null, sBtnSaveValues);
    
    gralMng.setPosition(-3, GralPos.size +2, -9, -1, 0, 'd', 0);
    widgBtnOff = gralMng.addSwitchButton(sName + "btnOff", "off / ?on", "on / ?off", GralColor.getColor("lgn"), GralColor.getColor("am"));
    wind.addMenuItemGThread("menuBarCurveView", "&Window/open " + sName, actionOpenWindow);
  
    windFileCfg = new GralFileSelectWindow("windFileCfg", gralMng);
    windFileValues = new GralFileSelectWindow("windFileValues", gralMng);
  }

  
  void refreshCurve(){
    if(widgCurve !=null && widgBtnOff !=null){
      int state = widgBtnOff.getState();
      widgCurve.activate( state == GralButton.kOn);
      widgCurve.refreshFromVariable(variables);
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
  
  
  
  
  
  /**This action will be called if the mouse is pressed on the drop field.
   * It is not drag and drop because drag'ndrop doesn't works on a field which content is not be able to select.
   * That is on show fields. Therefore the {@link GralMng#getWidgetOnMouseDown()} is used to detect
   * which show field was clicked last.
   * 
   */
  GralUserAction actionDropVariable = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.menuEntered){
        GralWidget variableWidget = gralMng.getWidgetOnMouseDown();
        //GralTextField widgText = (GralTextField)widgd;
        GralWidget widgText = widgd;
        //String sVariable = variableWidget.name;
        Object oContentInfo = widgd.getContentInfo();
        TrackValues input = (TrackValues)oContentInfo;
        //input.widgetVariable = variableWidget;
        input.min = Float.MAX_VALUE;
        input.max = -Float.MAX_VALUE;
        input.mid = 0.0f;
        //String sShowMethod = variableWidget.getShowMethod();
        String sPath = variableWidget.getDataPath();
        if(input.trackView == null){
          input.trackView = widgCurve.initTrack(sName, sPath, input.colorCurve, 0, 50, 5000.0f, 0.0f);
        } else {
          input.trackView.setDataPath(sPath);
        }
        input.widgVarPath.setText(variableWidget.name);
      }
      return true;
    }
  };
  
  GralUserAction actionOpenWindow = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { windCurve.setWindowVisible(true);
      return true;
    }

  };
  

  GralUserAction actionSetScaleValues2Track = new GralUserAction("actionSetScaleValues2Track"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode) && trackScale !=null){
        if(widgd.sCmd == "!"){
          try{
            String s1 = widgScale.getText();
            float scale = Float.parseFloat(s1);
            s1 = widgScale0.getText();
            float scale0 = Float.parseFloat(s1);
            s1 = widgline0.getText();
            int line0 = (int)Float.parseFloat(s1);
            //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
            if(trackScale.trackView == null){
              trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
            }
            trackScale.trackView.setTrackScale(scale, scale0, line0);
            widgBtnScale.setLineColor(GralColor.getColor("lgn"),0);
          } catch(NumberFormatException exc){
            widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
          }
        }
      }
      return true;
  } };
  
  
  /**
   *  ///
   */
  GralUserAction actionSetTrackForScale = new GralUserAction("actionSetTrackForScale"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if((actionCode) == KeyCode.mouse1Down){
        if(trackScale !=null){
          //last variable
          trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
          if(trackScale.trackView !=null){
            trackScale.trackView.setLineProperties(trackScale.colorCurve, 1, 0);
          }
        }
        trackScale = (TrackValues)widgd.getContentInfo();
        if(trackScale.trackView == null){
          trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
        }
        //colorLineTrackSelected = trackScale.trackView.getLineColor();
        trackScale.trackView.setLineProperties(trackScale.colorCurve, 2, 0);
        trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
        widgScale.setText("" + trackScale.trackView.getScale7div());
        widgScale0.setText("" + trackScale.trackView.getOffset());
        widgline0.setText("" + trackScale.trackView.getLinePercent());
        widgCurve.repaint();
      }
      else if(actionCode == (KeyCode.mouse1Down | KeyCode.ctrl)){
        if(trackScale !=null){
          //last variable
          trackScale.widgVarPath.setBackColor(GralColor.getColor("wh"),0);
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
        trackScale.widgVarPath.setBackColor(GralColor.getColor("lam"),0);
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
      return true;
  } };
  
  
  
  GralUserAction actionOpenFileDialog = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up){
        try{
          if(widgd.getCmd().equals(sBtnReadCfg)){
            windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), false, actionReadCfg);
          } else if(widgd.getCmd().equals(sBtnSaveCfg)){
            windFileCfg.openDialog(fileCurveCfg, widgd.getCmd(), true, actionSaveCfg);
          } else if(widgd.getCmd().equals(sBtnReadValues)){
            windFileCfg.openDialog(fileCurveSave, "read values- not implemented yet", false, null);
          } else if(widgd.getCmd().equals(sBtnSaveValues)){
            windFileCfg.openDialog(fileCurveSave, "write values", true, actionSaveValues);
          }
        } catch(Exception exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionReadCfg = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try{
          fileCurveCfg = (FileRemote)params[0];
          System.out.println("InspcCurveView - read curve view from; " + fileCurveCfg.getAbsolutePath());
          String in = FileSystem.readFile(fileCurveCfg);
          if(in ==null){
            System.err.println("InspcCurveView - actionRead, file not found;" + fileCurveCfg.getAbsolutePath());
          } else {
            if(widgCurve.applySettings(in)){ //apply the content of the config file to the GralCurveView
              //and transfer the names into the variable text fields of this widget. 
              List<? extends GralCurveViewTrack_ifc> listTracks = widgCurve.getTrackInfo();
              int iTrack = 0;
              for(GralCurveViewTrack_ifc track: listTracks){
                TrackValues trackValue = tracks[iTrack];
                trackValue.trackView = track;   //sets the new Track to the text field's data association.
                String sDataPath = track.getDataPath();
                trackValue.widgVarPath.setText(sDataPath !=null ? sDataPath : "?dataPath?");
                iTrack +=1;
              }
              while(iTrack < tracks.length){
                //this trackvalue has not a associated track in the curve because it is removed.
                TrackValues trackValue = tracks[iTrack];
                trackValue.trackView = null;
                trackValue.widgVarPath.setText("");
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
          widgCurve.writeSettings(out);
          out.close();
        } catch(Exception exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
        }
      }
      return true;
  } };
  
  
  
  
  GralUserAction actionSaveValues = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        try{
          FileRemote fileParam = (FileRemote)params[0];
          FileRemote dirCurveSave = fileParam.getParentFile();
          long dateStart = widgCurve.timeAtCursorLeft();
          DateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
          String sNameFile = format.format(new Date(dateStart)) + "_" + sName;
          fileCurveSave = dirCurveSave.child(sNameFile + ".csv");
            //File file = new File("curve.save");
          System.out.println("InspcCurveView - save curve data to; " + fileCurveSave.getAbsolutePath());
          
          writerCurveCsv.setFile(fileCurveSave);
          widgCurve.writeCurve(writerCurveCsv);
          
          String sClassExportDat = curveExporterClasses.get("dat");
          if(sClassExportDat !=null){
            Class<?> clazzCurveWriter2 = Class.forName(sClassExportDat);
            
            WriteCurve_ifc writerCurve2 = (WriteCurve_ifc)clazzCurveWriter2.newInstance();
            File fileDat = new File(dirCurveSave, sNameFile + ".dat");
            
            writerCurve2.setFile(fileDat);
            widgCurve.writeCurve(writerCurve2);
          }
          
        } catch(Exception exc){
          widgBtnScale.setLineColor(GralColor.getColor("lrd"),0);
          System.err.println(Assert.exceptionInfo("InspcCurveView", exc, 1, 2));
        }
      }
      return true;
  } };
  

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
          if(trackScale.trackView == null){
            trackScale.trackView = widgCurve.initTrack(sName, null, trackScale.colorCurve, 0, 50, 5000.0f, 0.0f);
          }
          trackScale.trackView.setLineProperties(color, 3, 0);  //change color immediately to see what happen
          trackScale.colorCurve = color;
          trackScale.widgVarPath.setTextStyle(color, null);
      }
    } else {
      System.out.println("InspcCurveView - unexpected what");
    }
  } };

  
  
  private void showValues(){
    float[] values = new float[tracks.length];
    int ix = 0;
    assert(false);
    for(TrackValues inp: tracks){
      values[ix] = inp.val;
      ix +=1;
    }
    int time = (int)System.currentTimeMillis();
    widgCurve.setSample(values, time);
  }
  
  
  /**This class joins the InputFields with the inspector communication info block.
   * It is created for any Curve one time if need and used for the communication after that. 
   * The routine {@link #execInspcRxOrder(Info)} is used to add the received values to the curve.
   */
  private class CurveCommRxAction implements InspcAccessExecRxOrder_ifc
  {
    final TrackValues inp;
    
    
    CurveCommRxAction(TrackValues inp)
    { this.inp = inp;
    }

    /**This method is called for any info block in the received telegram from target,
     * if this implementing instance is stored on the order.
     * It prepares the value presentation.
     * @see org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc#execInspcRxOrder(org.vishia.communication.InspcDataExchangeAccess.Info)
     */
    @Override public void execInspcRxOrder(InspcDataExchangeAccess.Info info, LogMessage log, int identLog)
    {
      int order = info.getOrder();
      int cmd = info.getCmd();
      assert(false);
      if(cmd == InspcDataExchangeAccess.Info.kAnswerValue){
        //GralWidget widgd = inp.widgetVariable;
        int typeInspc = InspcAccessEvaluatorRxTelg.getInspcTypeFromRxValue(info);
        
        float val = inp.val = InspcAccessEvaluatorRxTelg.valueFloatFromRxValue(info, typeInspc);
        if(inp.min > val){ inp.min = val; }
        if(inp.max < val){ inp.max = val; }
        inp.mid += 0.01f * (val - inp.mid);
        if(inp.bLast){
          showValues();
        }
      }
    }
  }
  
  
  
}
