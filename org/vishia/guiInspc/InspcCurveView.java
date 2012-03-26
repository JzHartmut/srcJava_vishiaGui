package org.vishia.guiInspc;

import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralCurveViewTrack_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralPrimaryWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindowMng_ifc;
import org.vishia.gral.widget.GralCurveView;
import org.vishia.inspectorAccessor.InspcAccessEvaluatorRxTelg;
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.util.KeyCode;

public class InspcCurveView
{

  /**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
   * Changes:
   * <ul>
   * <li>2012-03-17 Hartmut creating using the {@link GralCurveView} in a special window
   *   with the communication in {@link InspcGuiComm}.
   * </ul>
   * <br><br> 
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
  @SuppressWarnings("hiding")
  public final static int version = 20120317;

  
  
  /**Three windows for curve view. */
  GralWindow windCurve;
  
  final GralWidgetMng gralMng;
  
  private static class TrackValues{

    /**The track description in the view. */
    GralCurveViewTrack_ifc trackView;
    
    GralTextField widgVarPath;
    
    //GralTextField widgScale;
    
    GralTextField widgBit;
    
    GralTextField widgComment;

    
    GralWidget widgetVariable;
    
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
    
  }
  
  String[] colorCurveDefault = new String[]{"rd", "gn", "lbl", "or", "ma", "bn", "dgn", "drd", "cy", "bl"};
  
  
  /**The input field which is the current scaling field. */
  TrackValues trackScale;
  
  GralColor colorLineTrackSelected;

  final GralColor colorBlack = GralColor.getColor("bk");
  
  GralTextField widgScale, widgScale0, widgline0;
  
  GralButton widgBtnUp, widgBtnDn, widgBtnScale;
  
  
  /**
   * 
   */
  final TrackValues[] tracks = new TrackValues[10];
  
  GralButton widgBtnOff;
  
  final InspcGuiComm comm;
  
  GralCurveView widgCurve;
  
  InspcCurveView(InspcGuiComm comm, GralWidgetMng gralMng){
    this.comm = comm;
    this.gralMng = gralMng;
  }
  
  
  /**Builds the graphic, it should be called only one time on startupt in the graphic thread
   * @param wind The main window where the menu to open will be added
   * @param sName The name, used for menu entry too, sample "curve A"
   */
  public void buildGraphic(GralPrimaryWindow_ifc wind, String sName)
  {
    
    gralMng.selectPanel("primaryWindow");
    gralMng.setPosition(4, 0, 4, 0, 0, '.');
    int windProps = GralWindow.windConcurrently | GralWindow.windOnTop | GralWindow.windResizeable;
    windCurve = gralMng.createWindow("windMapVariables", "Curve A", windProps);
    //gralMng.setPosition(2, GralGridPos.size-1.6f, 0, 3.8f, 0, 'd');
    gralMng.setPosition(0, -2, 0, -10, 0, 'd');
    widgCurve = gralMng.addCurveViewY(sName, 3000, 10);
    gralMng.setPosition(0, GralPos.size +2, -10, 0, 0, 'd', 0);
    gralMng.addText("curve variable");
    for(int ii=0; ii<10; ++ii){
      TrackValues track = new TrackValues();
      String sColor = colorCurveDefault[ii];
      track.colorCurve = GralColor.getColor(sColor);
      if(track.colorCurve ==null){ throw new IllegalArgumentException("InspcCurveView-unknown color; " + sColor); }
      track.rxActionRxValueByPath = new CurveCommRxAction(track);
      this.tracks[ii] = track;
      track.widgVarPath = gralMng.addTextField(null, false, sName, sName);
      track.widgVarPath.setContentInfo(track);
      track.widgVarPath.setMouseAction(actionSetTrackForScale);
      //input[ii].widgPath.setMouseAction(actionDropVariable);
      tracks[ii].widgVarPath.setDataPath("widgetInfo");  //prevent storing the own widgetInfo.
      //input[ii].widgPath.setContentInfo(input[ii]);
      GralMenu menuWidg = track.widgVarPath.getContextMenu();
      GralWidget widgMenuItem = menuWidg.addMenuItemGthread("menuContextShowBackslash", "drop variable", actionDropVariable);
      widgMenuItem.setContentInfo(tracks[ii]);
      track.trackView = widgCurve.initTrack(sName, null, track.colorCurve, ii, 50, 5000.0f, 0.0f);
    }
    gralMng.setPosition(22, GralPos.size +3, -10, 0, 0, 'd', 0);
    widgScale = gralMng.addTextField("scale", true, "scale/div", "t");
    widgScale0 = gralMng.addTextField("scale0", true, "mid", "t");
    widgline0 = gralMng.addTextField("line0", true, "line-%", "t");
    gralMng.setPosition(32, GralPos.size +2, -10, GralPos.size +2, 0, 'r', 1);
    widgBtnDn = gralMng.addButton("btnDn", actionSetScaleValues2Track, "-", null, null, "-");
    widgBtnDn = gralMng.addButton("btnUp", actionSetScaleValues2Track, "+", null, null, "+");
    gralMng.setPosition(GralPos.same, GralPos.size +2, GralPos.next, GralPos.size +4, 0, 'r', 1);
    widgBtnDn = gralMng.addButton("btnScale", actionSetScaleValues2Track, "!", null, null, "set");
    
    gralMng.setPosition(-3, GralPos.size +2, -9, -1, 0, 'd', 0);
    widgBtnOff = gralMng.addSwitchButton(sName + "btnOff", "off / ?on", "on / ?off", GralColor.getColor("lgn"), GralColor.getColor("am"));
    wind.addMenuItemGThread("menuBarCurveView", "&Window/open " + sName, actionOpenWindow);
  }

  
  
  
  
  /**Adds an info block to the request telegram to get values. This routine is called
   * when the tx telegram to get values from target is assembled.
   * 
   */
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
  
  
  
  
  
  /**This action will be called if the mouse is pressed on the drop field.
   * It is not drag and drop because drag'ndrop doesn't works on a field which content is not be able to select.
   * That is on show fields. Therefore the {@link GralWidgetMng#getWidgetOnMouseDown()} is used to detect
   * which show field was clicked last.
   * 
   */
  GralUserAction actionDropVariable = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.menuEntered){
        GralWidget variableWidget = gralMng.getWidgetOnMouseDown();
        //GralTextField widgText = (GralTextField)widgd;
        GralWidget widgText = (GralWidget)widgd;
        String sVariable = variableWidget.name;
        Object oContentInfo = widgd.getContentInfo();
        TrackValues input = (TrackValues)oContentInfo;
        input.widgetVariable = variableWidget;
        input.min = Float.MAX_VALUE;
        input.max = -Float.MAX_VALUE;
        input.mid = 0.0f;
        String sShowMethod = variableWidget.getShowMethod();
        String sPath = variableWidget.getDataPath();
        if(sShowMethod !=null && sShowMethod.equals("stc_cmd")){
          //stc_cmd led:
          String mask = variableWidget.getDataPath();
          int ix = "abcd".indexOf(mask.charAt(0));
          sPath = "stc_cmdW:[" + ix + "]";
          String sBit = ":" + mask.substring(1);
          input.widgVarPath.setText(sPath);
          input.widgBit.setText(sBit);
          input.widgComment.setText(variableWidget.name);
        } else {
          input.widgVarPath.setText(variableWidget.name);
        }
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
  

  GralUserAction actionSetScaleValues2Track = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Up && trackScale !=null){
        if(widgd.sCmd == "!"){
          try{
            String s1 = widgScale.getText();
            float scale = Float.parseFloat(s1);
            s1 = widgScale0.getText();
            float scale0 = Float.parseFloat(s1);
            s1 = widgline0.getText();
            int line0 = (int)Float.parseFloat(s1);
            //widgCurve.setMinMax(trackScale.scale, -trackScale.scale);
            trackScale.trackView.setTrackScale(scale, scale0, line0);
            widgBtnScale.setForegroundColor(GralColor.getColor("lgn"));
          } catch(NumberFormatException exc){
            widgBtnScale.setForegroundColor(GralColor.getColor("lrd"));
          }
        }
      }
      return true;
  } };
  
  
  /**
   * 
   */
  GralUserAction actionSetTrackForScale = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params)
    { if(actionCode == KeyCode.mouse1Down){
        if(trackScale !=null){
          //last variable
          trackScale.widgVarPath.setBackgroundColor(GralColor.getColor("wh"));
          trackScale.trackView.setLineColor(colorLineTrackSelected);
        }
        trackScale = (TrackValues)widgd.getContentInfo();
        colorLineTrackSelected = trackScale.trackView.getLineColor();
        trackScale.trackView.setLineColor(colorBlack);
        trackScale.widgVarPath.setBackgroundColor(GralColor.getColor("lam"));
        widgScale.setText("" + trackScale.trackView.getScale7div());
        widgScale0.setText("" + trackScale.trackView.getOffset());
        widgline0.setText("" + trackScale.trackView.getLinePercent());
      }
      return true;
  } };
  
  
  private void showValues(){
    float[] values = new float[tracks.length];
    int ix = 0;
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
    @Override public void execInspcRxOrder(InspcDataExchangeAccess.Info info)
    {
      int order = info.getOrder();
      int cmd = info.getCmd();
      if(cmd == InspcDataExchangeAccess.Info.kAnswerValue){
        GralWidget widgd = inp.widgetVariable;
        float val = inp.val = InspcAccessEvaluatorRxTelg.valueFloatFromRxValue(info);
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
