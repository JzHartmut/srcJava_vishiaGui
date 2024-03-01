package org.vishia.guiViewCfg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.byteData.ByteDataSymbolicAccessReadConfig;
import org.vishia.byteData.VariableAccessArray_ifc;
import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.Report;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Arguments;
import org.vishia.util.Debugutil;
import org.vishia.util.StringFunctions;
import org.vishia.util.StringFunctions_C;
import org.vishia.byteData.ByteDataSymbolicAccess;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralCurveView;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelActivated_ifc;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralSetValue_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralVisibleWidgets_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;


/**This class supports dealing with values which are received with a stream channel, usual Ethernet. 
 * It is used primary in {@link ViewCfg}, a GUI tool script programmable.
 * 
 * @author Hartmut Schorrig
 *
 */
public class OamShowValues
{

  
  /**Version and history
   * <ul>
   * <li>2022-10-26 {@link #idxAllVariables}: Not it is a common index/Map for all variables, 
   *   the {@link ByteDataSymbolicAccess#indexVariable} refers to the same. 
   *   This is a common index together with the possible {@link #evalValues}: {@link OamEvalValues_ifc#getVariables()}.
   *   The concept of plugin is improved.
   * <li>2022-10-25: {@link #wdgBtnWrHex} can be given in the scripted config. 
   *   If existing the button is used to write a hex output of data on pressing. Super helpful for data debugging.  
   * <li>2022-09-03 {@link #curveView} as extra reference for only one curve view if only one is used for fast access. 
   * <li>2022-08-26 Hartmut Refactored, firstly used after 10 years and detect some difficulties.
   *   The {@link ByteDataSymbolicAccess} and {@link ByteDataSymbolicAccessReadConfig} are also refactored.
   *   Prevent warnings (formally)
   * <li>2012-02-25 Hartmut new: OamShowValues: with time, grayed if old
   * <li>2012-02-25 Hartmut new: CurveView: All data have a short timestamp. 
   * <li>2012-02-21 Hartmut chg Now a curve view can be accessed symbolically.
   * <li>2010-06-00 Hartmut created
   * </ul>
   * 
   */
  public static final int version = 0x20220826;
  
  final LogMessage log;

  /**Index (fast access) of all variable which are sent from the automation device (contained in the cfg-file). */
  protected final ByteDataSymbolicAccess accessOamVariable;

  /**Index (fast access) of all variable which are sent from the automation device (contained in the cfg-file). */
  protected final ByteDataSymbolicAccessReadConfig cfgOamVariable;

  boolean dataValid = false;
  
  List<GralWidget> widgetsInTab;
  
  /**The access to the gui, to change data to show. */
  protected final GralMng gralMng;
  
  /**If this buttons are configured, ability to write received value to console out */
  protected final GralButton wdgBtnWrHex, wdgBtnWrVal;
  
  /**This is a plugin class delivered by an user application with the argument {@link ViewCfg.CallingArguments#argClassEvalRcvValues}
   * or null if not given. The user class can contribute to the pool of variables and access to all Oam variables, see {@link #idxAllVariables} 
   */
  final OamEvalValues_ifc evalValues;
  
  /**This index (map) contains all variables, both from {@link #accessOamVariable} as well as content of {@link #evalValues}. */
  final Map<String, VariableAccess_ifc> idxAllVariables = new TreeMap<String, VariableAccess_ifc>();
  
  
  /**Test variable used for values only if no communication is selected,
   * that is cmd line argument -ownIpc: is not given. 
   * The variables are filled with values in a range -100..100 to demonstrate values. 
   */
  VariableAccess_ifc testVar1, testVar2;
  
  Set<Map.Entry<String, GralWidget>> fieldsToShow;
  
  GralCurveView curveView;
  
  /**The access to received data for the timestamp as milliseconds after a base year.
   * It is not null if that variable is contained in the received data description
   * See {@link #readVariableCfg()}.
   */
  VariableAccess_ifc varTimeMilliSecFromBaseyear;
  
  
  GralColor colorBackValueOk = GralColor.getColor("wh");
  
  GralColor colorBackValueOld = GralColor.getColor("lgr");
  
  long timeMilliSecFromBaseyear;
  
  /**Set in {@link #writeValuesOfTab()} to check newless. */
  private long timeNow;
  
  /**Time for valid values. */
  private final long milliSecondsOk = 3000; 
  
  
  //private final float[] valueUserCurves = new float[6];  

  
  
  
  public OamShowValues ( LogMessage log , GralMng gralMng, Arguments.Argument argClassEvalRcvValues ) {
    this.log = log;
    this.gralMng = gralMng;
    this.wdgBtnWrHex = (GralButton) gralMng.getWidget("btnWrHex");  //maybe null then not existing
    this.wdgBtnWrVal = (GralButton) gralMng.getWidget("btnWrVal");  //maybe null then not existing
    this.accessOamVariable = new ByteDataSymbolicAccess(this.idxAllVariables); //access to variables in a byte[]
    //------------------------------------------------------------- // read the configuration for this byte[] accessed data.
    this.cfgOamVariable = new ByteDataSymbolicAccessReadConfig(this.accessOamVariable);
    // Note: This creates variables in this.accessOamVariables with reference to the accessOamVariable instance.
    // It means the variables uses this access instance to access to the given variable byte array.
    // The variables are also stored in this.idxAllVariables because it is the same reference.
    //--------------------------------------------------------------------------------
    //assign an empty array, it is necessary for local test or without data receive.
    //elsewhere a null-pointer-exception is thrown if the tab-pane is shown.
    //If data are received, this empty array isn't referenced any more.
    this.accessOamVariable.assignData(new byte[1500], System.currentTimeMillis());
    this.dataValid = true;   //it can be set because empty data are present, see above, use to test.
    
    OamEvalValues_ifc evalValues = null;
    if(argClassEvalRcvValues !=null && argClassEvalRcvValues.val !=null) {
      try {
        @SuppressWarnings("unchecked") Class<OamEvalValues_ifc> evalClass = (Class<OamEvalValues_ifc>)Class.forName(argClassEvalRcvValues.val);
        @SuppressWarnings("unchecked") Constructor<OamEvalValues_ifc>[] cactor = (Constructor<OamEvalValues_ifc>[])evalClass.getConstructors();
        Constructor<OamEvalValues_ifc> cctor = cactor[0];  // only one expected
        evalValues = cctor.newInstance(this.accessOamVariable);    //plugin instance
        this.idxAllVariables.putAll(evalValues.getVariables());
        
      } catch(Exception exc) {
        System.err.println(argClassEvalRcvValues.option + ":" + argClassEvalRcvValues.val + " is faulty: " + exc.getMessage());
        throw new IllegalArgumentException("abort, command line error");
      }
    }
    this.evalValues = evalValues;  //null if not parametrized

  }
  
  
  
  
  
  
  public boolean readVariableCfg(ViewCfg.CallingArguments args) throws FileNotFoundException
  { int nrofVariable = this.cfgOamVariable.readVariableCfg(args.sFileOamVariables.val);
    if( nrofVariable>0){
      this.log.sendMsg(0, "success read " + nrofVariable + " variables from file \"GUI/oamVar.cfg\".");
    } else {
      this.log.sendMsg(0, " variables not access-able from file \"exe/SES_oamVar.cfg\".");
    }
    this.varTimeMilliSecFromBaseyear = this.accessOamVariable.getVariable("time_milliseconds1970");
    if(this.evalValues !=null) { this.evalValues.setVariables(); }
    return nrofVariable >0;
  }
  
  public void setFieldsToShow(Set<Map.Entry<String, GralWidget>> fields, Appendable log) throws IOException
  {
    this.fieldsToShow = fields;
    for(Map.Entry<String, GralWidget> e: fields) {         // complete all show fields with variable
      final GralWidget widg = e.getValue();
      final String sData = widg.getDataPath();             // path to the variable, maybe null
      if(sData ==null) {
        log.append(widg.getName()).append(" potential Show field without dataPath ");
      } else {
        final VariableAccess_ifc var = sData == null ? null : this.accessOamVariable.getVariable(sData);
        if(var == null) {
          log.append(widg.getName()).append(" variable not found: ").append(sData);
        } else {
          widg.setVariable(var);                                   //null if no data path or variable not exists.
          log.append(widg.getName()).append(" shows ->").append(var.toString());
        }
      }
    }
    
    
  }
  
  
  
  public void setCurveView(GralCurveView curveView) {
    this.curveView = curveView;
  }
  
  
  
  /**Operation is only called if no communication is switched on.
   * 
   */
  void testChgVariable() {
    if(this.testVar1 ==null) {
      for(Map.Entry<String, VariableAccess_ifc> e: this.idxAllVariables.entrySet()) {
        VariableAccess_ifc var = e.getValue();
        if("SIFD".indexOf(var.getType()) >=0) {
          if(this.testVar1 ==null) { this.testVar1 = var; }
          else if(this.testVar2 ==null) { this.testVar2 = var; }
          else break;   //enough variables
        }
      }
    }
    float val1 = 0.0f;
    if(this.testVar1 !=null) {
      val1 = this.testVar1.getFloat();
      val1 +=1.0;
      if(val1 >100.0f) {val1 = -100.0f;}
      this.testVar1.setFloat(val1);
    }
    if(this.testVar2 !=null) {
      this.testVar2.setFloat(val1 / 3.14f);
    }
    writeValuesOfTab();
  }
  
  

  
  /**This routine presents the new received values at the GUI
   * or saves values in traces.
   * <br><br>
   * It is possible that this routine is called more as one time after another. 
   * That is if more as one data set are transfered in 1 datagram. 
   * Because that a redraw isn't send here, see {@link #showRedraw()}
   * 
   * @param binData
   * @param nrofBytes
   * @param from
   */
  public void show(byte[] binData, int nrofBytes, int from)
  {
    this.accessOamVariable.assignData(binData, nrofBytes, from, System.currentTimeMillis());
    this.accessOamVariable.dataAccess.setLittleEndianBig2();
    if(this.wdgBtnWrHex !=null) {
      if(this.wdgBtnWrHex.wasPressed()) {
        try {
          int[] value = new int[48];
          for(int ix=0; ix<value.length; ++ix) {
            value[ix] = this.accessOamVariable.dataAccess.getIntVal(2*ix, 2);
          }
          System.out.append('\n');
          StringFunctions_C.appendHexLine(System.out, value, 0, 48, 4, 16, 16);
        } catch (Exception exc) { throw new RuntimeException("unexpected: ", exc); }
      }
    }
    this.dataValid = true;
    //long timeAbs = this.accessOamVariable.dataAccess.getLongVal(0x0, 8);
    int timeShortAdd = 0; //this.accessOamVariable.dataAccess.getIntVal(0x8, 4);
    int timeShort = this.accessOamVariable.dataAccess.getIntVal(0xc, 4);
    this.accessOamVariable.setTimeShort(timeShort, timeShortAdd);
    //this.timeMilliSecFromBaseyear = timeAbs + (timeShort - timeShortAdd);
    if(this.varTimeMilliSecFromBaseyear !=null){
      //read the time stamp from the record:
      this.timeMilliSecFromBaseyear = this.varTimeMilliSecFromBaseyear.getInt();
    } else {
      //this.timeMilliSecFromBaseyear = System.currentTimeMillis();
    }
    // only test
    VariableAccess_ifc var_yMov = this.accessOamVariable.getVariable("yMov");
    if(var_yMov !=null) {
      int yMov = var_yMov.getInt();
      if(yMov > 100) {
        Debugutil.stop();
      }
    }
    if(this.evalValues !=null) { 
      this.evalValues.calc(); 
    }
    writeValuesOfTab();   //write the values in the current tab, most of them will be received here newly.
    //TEST TODO:
    //accessOamVariable.setFloat("ctrl/energyLoadCapac2Diff", checkWithoutNewdata);
    //current panel:
    List<GralWidgetBase> listWidgets = this.gralMng.getListCurrWidgets();
    for(GralWidgetBase widgetInfo : listWidgets){
      @SuppressWarnings("unused")
      String sName = widgetInfo.name;
    }
//    float[] values = new float[5];
//    values[0] = this.accessOamVariable.getFloat("way");
//    for(GralCurveView_ifc crv: GralMng.get().curveContainer) {
//      crv.setSample(values, (int)(this.timeNow & 0xffffffff));
//    }
    //read all variables which are necessary to show.
    //writeCurveValues();   //write values for curve scoping

  }


  public void showRedraw()
  {
    redrawCurveValues();
  }
  
  private void writeField(GralWidget widgetInfo)
  { String sName = widgetInfo.name;
    //String sInfo = widgetInfo.sDataPath;
    String sValue;
    /*int posFormat = sInfo.indexOf('%');
    final String sPathValue = posFormat <0 ? sInfo : sInfo.substring(0, posFormat);
    */
    String sFormat = widgetInfo.getFormat();
    VariableAccess_ifc variable = getVariableFromContentInfo(widgetInfo);
    int ixInVariable = widgetInfo.getDataIx();
    VariableAccessArray_ifc arrayVar = ixInVariable >=0 ? (VariableAccessArray_ifc) variable: null;
    //DBbyteMap.Variable variable = accessOamVariable.getVariable(sPathVariable);
    if(variable == null){
      sValue = "XXXXX";
    } else {
      char varType = variable.getType();
      if(varType == 'F'){
        float value= arrayVar !=null ? arrayVar.getFloat(ixInVariable) : variable.getFloat();
        if(sFormat ==null){
          if(value < 1.0F && value > -1.0F){ sFormat = "%1.5f"; }
          else if(value < 100.0F && value > -100.0F){ sFormat = "% 2.3f"; }
          else if(value < 10000.0F && value > -10000.0F){ sFormat = "% 4.1f"; }
          else if(value < 100000.0F && value > -100000.0F){ value = value / 1000.0F; sFormat = "% 2.3f k"; }
          else { sFormat = "%3.3g"; }
        } 
        sValue = String.format(sFormat, value);
      } else if("JISB".indexOf(varType)>=0){
        //integer
        int value = arrayVar !=null ? arrayVar.getInt(ixInVariable) : variable.getInt();
        if(sFormat ==null){
          sFormat = "%d";
        } 
        sValue = String.format(sFormat, value);
      } else {
        //other format
        sValue = "?type=" + varType;
      }
      GralWidgetBase widg = this.gralMng.getWidget(sName);  
      ((GralWidget)widg).setText(sValue);
      //guiAccess.setText(sName, sValue);
      long timeLastRefresh = variable.getLastRefreshTime();
      if( (this.timeNow - timeLastRefresh) < this.milliSecondsOk){
        ((GralWidget)widg).setBackColor(this.colorBackValueOk, 0);
      } else {
        ((GralWidget)widg).setBackColor(this.colorBackValueOld, 0);
      }
    }
    
  }
  
  
  
  

  /**
   * 
   */
  void writeValuesOfTab()
  { if(this.dataValid){
      this.timeNow = System.currentTimeMillis();
      ConcurrentLinkedQueue<GralVisibleWidgets_ifc> listPanels = this.gralMng.getVisiblePanels();
      if(this.curveView !=null) {
        this.curveView.bActive = true;
        VariableContainer_ifc variables = this.evalValues !=null ? this.evalValues.getVariableContainer() : this.accessOamVariable;
        this.curveView.refreshFromVariable(variables);
      }
      
      //GralWidget widgdRemove = null;
      try{
        for(GralVisibleWidgets_ifc panel: listPanels){
          List<GralWidgetBase> widgetsVisible = panel.getWidgetsVisible();
          if(widgetsVisible !=null) for(GralWidgetBase widget: widgetsVisible){
            if(widget instanceof GralCurveView){
              GralCurveView curve = (GralCurveView)widget;
              curve.bActive = true;
              curve.refreshFromVariable(this.accessOamVariable);
              //This is all clarified in refreshFromVariable, given before.
//              List<GralSetValue_ifc> listLines = curve.getTracks();
//              float[] values = new float[listLines.size()];
//              int ixValues = -1;
//              for(GralSetValue_ifc line: listLines){
//                ByteDataSymbolicAccess.Variable variable = getVariableFromContentInfo(line);
//                float value;
//                if(variable !=null){
//                  value= variable.getFloat(line.getDataIx());
//                } else {
//                  value = 0;
//                }
//                line.setValue(value);
//                values[++ixValues] = value;
//              }
//              curve.setSample(values, (int)timeMilliSecFromBaseyear);
            } else if(widget instanceof GralWidget){
              //Note: the variable is assigned from container only once, no effort
              ((GralWidget)widget).refreshFromVariable(this.accessOamVariable);  
            }
          }
        }
      } catch(Exception exc){ 
      }
      
    }
    if(this.widgetsInTab != null){
      for(GralWidget widgetInfo: this.widgetsInTab){
        String sContentInfo = widgetInfo.getDataPath();
        if(sContentInfo !=null && sContentInfo.length() >0 && widgetInfo !=null){
          stop();
          //if(!callMethod(widgetInfo)){
            //show value direct
            writeField(widgetInfo);
          //}
          //log.reportln(3, "TAB: " + sContentInfo);
        }
      }
    }
  }
  
  
  VariableAccess_ifc getVariableFromContentInfo(GralSetValue_ifc widgetInfo)
  {
    VariableAccess_ifc variable;
    Object oContentInfo = widgetInfo.getContentInfo();
    final int[] ixArrayA = new int[1];
    if(oContentInfo == null){
      //first usage:
      variable = getVariable(widgetInfo.getDataPath(), ixArrayA);
      widgetInfo.setContentInfo(variable);
      widgetInfo.setDataIx(ixArrayA[0]);
    } else if(oContentInfo instanceof VariableAccess_ifc){
      variable = (VariableAccess_ifc)oContentInfo;
    } else {
      variable = null;  //other info in widget, not a variable.
    }
    return variable; 
  }
  
  
  
  VariableAccess_ifc getVariable(String sDataPath, int[] ixArrayA)
  {
    final String sPathVariable = ByteDataSymbolicAccess.separateIndex(sDataPath, ixArrayA);
    VariableAccess_ifc variable = this.idxAllVariables.get(sPathVariable);
    return variable;
  }
  
  
//  boolean callMethod(GralWidget widgetInfo)
//  { String sName = widgetInfo.name;
//    String sInfo = widgetInfo.getDataPath();
//    final String sMethodName;
//    final String sVariablePath;
//    final String[] sParam;
//    final int posParanthesis = sInfo.indexOf('(');
//    if(posParanthesis >=0){
//      sMethodName = sInfo.substring(0, posParanthesis);
//      sParam = sInfo.substring(posParanthesis+1).split("[,)]");
//      sVariablePath = sParam[0].trim();
//    } else {
//      sMethodName = widgetInfo.getShowMethod();
//      sParam = sInfo.split(",");
//      sVariablePath = sParam[0];
//    }
//    if(sMethodName != null){
//      if(sMethodName.equals("setValue")){
//        setValue(widgetInfo);
//      }
//      if(sMethodName.equals("setBar")){
//        setBar(widgetInfo);
//      }
//      else if(sMethodName.equals("uCapMaxRed")){
//        float value= this.accessOamVariable.getFloat(sVariablePath);
//        if(value > 120.0F){
//          this.gralMng.setBackColor(sName, 0, 0xffe0e0);
//        } else {
//          this.gralMng.setBackColor(sName, 0, 0xffffff);
//        }
//        String sValue = "" + value;
//        this.gralMng.setText(sName, sValue);
//      } 
//      else if(sMethodName.equals("showBinManValue")){
//        int value= this.accessOamVariable.getInt(sVariablePath);
//        int color;
//        if((value & 0x10)==0){
//          color = 0xffffff;  //white: not set
//        }
//        else { //it is set
//          int mode = value & 0x60;  //bit 6=manEnable, 5=manMode
//          switch(mode){
//          case 0: color=0xff0000; break;     //dark red: error, manual preset but not enabled
//          case 0x20: color=0xff0000; break;  //dark red: error, manual preset but not enabled
//          case 0x40: color=0xff0000; break;  //orange: set, enabled or not
//          case 0x60: color=0xff8000; break;  //orange: set and enabled.
//          default: color=0xff00ff;    //it isn't used.
//          }
//        }
//        this.gralMng.setBackColor(widgetInfo, 0, color);
//      }
//      else if(sMethodName.equals("showBinEnable")){
//        int value= this.accessOamVariable.getInt(sVariablePath);
//        int color;
//        int mode = value & 0x60;  //bit 6=manEnable, 5=manMode
//        switch(mode){
//        case 0: color=0xffffff; break;     //white: no manual
//        case 0x20: color=0xff0000; break;  //dark red: error, manual not enabled, but on
//        case 0x40: color=0x00ff00; break; //green: manual enabled
//        case 0x60: color=0xff8000; break;  //orange: manual enabled and switched.
//        default: color=0xff00ff;    //it isn't used.
//        }
//        this.gralMng.setBackColor(widgetInfo, 0, color);
//      }
//      else if(sMethodName.equals("xxxshowBin")){
//        int value= this.accessOamVariable.getInt(sVariablePath);
//        int color;
//        if((value & 0x06) ==0x04) color = 0x00ff00;  //green: manual enable
//        else if((value & 0x06) ==0x06) color = 0x0000ff;  //blue: manual enable and manual control
//        else color = 0xffffff;  
//        this.gralMng.setBackColor(sName, 0, color);
//      }
//      else if(sMethodName.equals("showBool")){
//        ByteDataSymbolicAccess.Variable variable = this.accessOamVariable.getVariable(sVariablePath);
//        int color;
//        if(variable !=null){
//          int value= this.accessOamVariable.getInt(variable, -1);
//          
//          if((value & 0xff) ==0x0) color = this.gralMng.getColorValue(sParam[1].trim());  
//          else color = this.gralMng.getColorValue(sParam[2].trim());  
//          //guiAccess.setBackColor(sName, 0, color);
//        } else {
//          color = 0xb0b0b0;  //gray
//        }
//        if(widgetInfo.whatIs == 'D'){
//          //a LED
//           this.gralMng.setLed(widgetInfo, color, color);
//        } else {
//          this.gralMng.setBackColor(widgetInfo, 0, color);
//        }
//      }
//      else if(sMethodName.equals("setColor")){
//        widgetSetColor(sName, sParam, widgetInfo);
//      }
//      else if(sMethodName.equals("showBinFromByte")){
//        showBinFromByte(widgetInfo);
//      }
//      else {
//        stop();
//      }
//    }
//    return sMethodName !=null;
//  }
  
  
  static class ValueColorAssignment
  { 
    static class Element{
      int from; int to;
      int color;
    }
    
    Element[] data;
    
    ValueColorAssignment(String[] sParams, GralMng_ifc guiAccess){
      this.data = new Element[sParams.length-1];
      int state = 0;
      for(int ii = 1; ii < sParams.length; ++ii){
        String sParam = sParams[ii];
        int posValue = sParam.indexOf('=')+1;
        final String sColor;
        final Element data1 = new Element();
        if(posValue >1){
          state = Integer.parseInt(sParam.substring(posValue));
          data1.from = state; data1.to = state;
          sColor = sParam.substring(0, posValue-1).trim();
        } else {
          sColor = sParam.trim();
          data1.from = Integer.MIN_VALUE; data1.to = Integer.MAX_VALUE;
        }
        //String[] sColParam = sParams[ii].split("[=:+]");
        final int color = guiAccess.getColorValue(sColor);
        data1.color = color;
        this.data[ii-1] = data1;
      }
    }
    
    int getColor(int value){
      for(Element element: this.data){
        if(value >= element.from && value <= element.to){
          return element.color;
        }
      }
      return 0x00880088;  //not found, return in for-loop!
    }
    
  }
  
  
  
  
  /**Information to a widget, which is colored by the value.
   */
  static class ColoredWidget
  { 
    ValueColorAssignment valueColorAssignment;
    
    ByteDataSymbolicAccess valueContainer;
    
    VariableAccess_ifc variableContainsValue;

    
    
    public ColoredWidget(ValueColorAssignment valueColorAssignment, ByteDataSymbolicAccess valueContainerBbyteMap
      , VariableAccess_ifc variableContainsValue)
    { this.valueColorAssignment = valueColorAssignment;
      this.valueContainer = valueContainer;
      this.variableContainsValue = variableContainsValue;
    }
    
  }
    
  
  
  void widgetSetColor(String sName, String[] sParam, GralWidget widgetInfo)
  { ColoredWidget userData;
    Object oUserData = widgetInfo.getContentInfo();
    if(oUserData == null){
      //first usage:
      VariableAccess_ifc variable = this.accessOamVariable.getVariable(sParam[0]);
      ValueColorAssignment colorAssignment = new ValueColorAssignment(sParam, this.gralMng);
      userData = new ColoredWidget(colorAssignment, this.accessOamVariable, variable);
      widgetInfo.setContentInfo(userData);
    } else {
      userData = (ColoredWidget)oUserData;
    }
    int color;
//    if(userData.valueContainer !=null){
//      int value = userData.valueContainer.getInt(userData.variableContainsValue);
    if(userData.variableContainsValue !=null){
      int value = userData.variableContainsValue.getInt();
      color = userData.valueColorAssignment.getColor(value);
    } else {
      color=0xaaaaaa;
    }
    this.gralMng.setBackColor(sName, -1, color);
    
  }
  
  
  void showBinFromByte(GralWidget widgetInfo)
  { VariableAccess_ifc variable;
    Object oUserData = widgetInfo.getContentInfo();
    if(oUserData == null){
      //first usage:
      variable = this.accessOamVariable.getVariable(widgetInfo.getDataPath());
      widgetInfo.setContentInfo(variable);
    } else {
      variable = (VariableAccess_ifc)oUserData;
    }
    int value = variable.getInt();
    int mode = value & 0x0c;
    int colorBorder;
    int colorInner;
    switch(mode){
    case 0: colorBorder = colorInner = 0xffffff; break; 
    case 4: colorBorder = 0xff8000; colorInner = 0xffffff; break;  //incoming value is 1, border is red, used value is 0, inner is white 
    case 8: colorBorder = 0xffff80; colorInner = 0xff0000; break;  //incoming value is 0, border is yellow to distinguish, used value is 1, inner is red 
    case 0x0c: colorBorder = colorInner = 0xff8000; break; 
    default: colorBorder = colorInner = 0;  //not realistic
    }
    this.gralMng.setLed(widgetInfo, colorBorder, colorInner);
    
  }
  
  
  void setValue(GralWidget widgetInfo)
  { VariableAccess_ifc variable;
    Object oUserData = widgetInfo.getContentInfo();
    if(oUserData == null){
      //first usage:
      variable = this.accessOamVariable.getVariable(widgetInfo.getDataPath());
      widgetInfo.setContentInfo(variable);
    } else {
      variable = (VariableAccess_ifc)oUserData;
    }
    float value = variable.getFloat();
    GralWidget_ifc oWidget = widgetInfo;
    if(oWidget instanceof GralSetValue_ifc){
      GralSetValue_ifc widget = (GralSetValue_ifc) oWidget;
      widget.setValue(value);
    }
  }
  
  
  void setBar(GralWidget widgetInfo)
  { VariableAccess_ifc variable = getVariableFromContentInfo(widgetInfo);
    if(variable == null){
      debugStop();
    } else {
      float value = variable.getFloat();
      
      GralWidget_ifc oWidget = widgetInfo;
      if(oWidget instanceof GralValueBar){
        GralValueBar widget = (GralValueBar) oWidget;
        String[] sParam;
        if( (sParam = widgetInfo.getShowParam()) != null){
          widget.setBorderAndColors(sParam);
          widgetInfo.clearShowParam();
        }
        widget.setValue(value);
      }
    }
  }
  
  
  



  /**Agent instance to offer the interface for updating the tab in the main TabPanel
   */
  public final GralPanelActivated_ifc tabActivatedImpl = new GralPanelActivated_ifc()
  {
    @Override  public void panelActivatedGui(List<GralWidget> widgets)
    {
      OamShowValues.this.widgetsInTab = widgets;
      writeValuesOfTab();
    }
    
  };
  

  
//  final GralUserAction actionSetValueTestInInput = new GralUserAction("actionSetValueTestInInput")
//  { @Override
//  public boolean userActionGui(String sCmd, GralWidget widgetInfos, Object... values)
//    { 
//      final int[] ixArrayA = new int[1];
//      VariableAccess_ifc variable = getVariable(widgetInfos.getDataPath(), ixArrayA);
//      int value = 0; //TODO Integer.parseInt(sParam);
//      if(variable.byteDataAccess.lengthData() == 0){
//        variable.byteDataAccess.assignData(new byte[1500], System.currentTimeMillis());
//      }
//      variable.setFloat(2.5F* value -120, -1);
//      OamShowValues.this.dataValid = true;
//      writeValuesOfTab();  //to show
//      return true;
//    }
//  };

  
  private void redrawCurveValues()
  {
    this.gralMng.redrawWidget("userCurves");
  }
  


  void stop(){}

  void debugStop(){
    stop();
  }


}
