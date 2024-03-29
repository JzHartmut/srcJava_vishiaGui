package org.vishia.guiInspc;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspcPC.InspcAccessExecRxOrder_ifc;
import org.vishia.inspcPC.InspcAccess_ifc;
import org.vishia.inspcPC.accTarget.InspcTargetAccessor;
import org.vishia.inspcPC.mng.InspcFieldOfStruct;
import org.vishia.inspcPC.mng.InspcMng;
import org.vishia.inspcPC.mng.InspcStruct;
import org.vishia.inspcPC.mng.InspcVariable;
import org.vishia.util.KeyCode;


/**This class presents a window with one table and some buttons to view and edit all fields in one instance
 * in a target system.
 * 
 * 
 * <br>
 * The InspcFieldTable shows one struct from target data or one instance, all fields of a class of Java reflection view. 
 * The fields are stored in instances of {@link InspcFieldOfStruct} which are referenced as data in the {@link GralTable}
 * of the private composite reference {@link #widgTable}. 
 * <br><br>
 *  
 * <br><br>
 * <b>Filling the table with the fields of a reflection target</b>:<br>
 * <img src="../../../img/guiInspc_getFieldsTableOmd.png">
 * <br>guiInspc_getFieldsTableOmd  
 * <br>
 * One of the methods {@link #fillTableFromFocusedVariable()},  {@link #actionBack()}, {@link #getSubStruct(GralTableLine_ifc)}
 * are invoked from operator GUI actions. They call 
 * <br> {@link #fillTableStruct()}<br>
 * This method checks the {@link InspcStruct} of the variable respectively the shown struct in table whether it is known already, 
 * using {@link InspcStruct#isUpdated()}. It it is not updated, on selection a new variable, the 
 * <br><br>
 * {@link InspcTargetAccessor#requestFields(org.vishia.inspcPC.InspcTargetAccessData, InspcAccessExecRxOrder_ifc, Runnable)} 
 * <br><br>
 * is called. This routine sets only the reference to the {@link InspcTargetAccessData} and the 2 callbacks. 
 * This action is done especially in the graphical thread.
 * <br><br>
 * If {@link InspcStruct#isUpdated()} returns true, then the struct was updated already. Then it can be shown in the {@link GralTable}
 * with the names of its elements and the last gotten values.
 * <br><br>
 * The organization thread of {@link InspcMng} invokes {@link InspcMng#procComm()} cyclically to show and request values. 
 * In this thread via calling {@link InspcTargetAccessor#requestStart(long)} the request for getFields is recognized.
 * For that the {@link InspcTargetAccessor#cmdGetFields(String, org.vishia.inspcPC.InspcAccessExecRxOrder_ifc)}
 * is invoked for the path of the struct. That is sent to the target.
 * <br><br>
 * The target answers with some items for all fields in one or more datagram but for that request with the same sequence number. 
 * For any item respectively element of the struct the given instance to {@link InspcStruct#rxActionGetFields}
 * is invoked which fills the struct.
 * <br><br>
 * After the last datagram of this answer the stored reference to the {@link Runnable}: {@link #actionUpdated} of this class
 * is invoked. This action calls {@link #fillTableStruct()} now with received and stored fields.
 * <br>
 * <img src="../../../img/guiInspc_getFieldsTableSeq.png">
 * <br>guiInspc_getFieldsTableSeq<br>
 * <b>Show and request values for the fields</b><br>
 * This is simple done by getting maybe with creation of the variable for the requested field: 
 * {@link InspcFieldOfStruct#variable(InspcVariable, org.vishia.byteData.VariableContainer_ifc)}
 * and request a new value with {@link InspcVariable#requestValue(long, Runnable)}. The callback invoked on end of the last datagram
 * is a temporary instance {@link RunOnReceive#RunOnReceive(GralTableLine_ifc)} with the given line of the table which invokes
 * {@link #showValue(GralTableLine_ifc, boolean)}.
 * <br>
 * <img src="../../../img/guiInspc_getValueTableOmd.png">
 * <br>guiInspc_getValueTableOmd<br>
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcFieldTable
{
  /**Version, history and license.
   * <ul>
   * <li>2018-09-08 Hartmut chg: because {@link InspcFieldOfStruct} hasSubstruct.
   * <li>2016-10 Hartmut chg: Regards a field which is a node of tree, not only leaf. 
   * <li>2015-06-21 requests a new value if the older is older than 1 second, fast refresh
   * <li>2015-05-29 requests a new value only if the current is older than 5 seconds.
   * <li>2014-01-05 indexSelection
   * <li>2013-12-18 Created.
   * </ul>
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
   * 
   */
  //@SuppressWarnings("hiding")
  protected final static String sVersion = "2015-05-30";

  @SuppressWarnings("synthetic-access") 
  class RunOnReceive implements Runnable {
    //GralTable<FieldOfStruct>.TableLineData line;
    GralTableLine_ifc<InspcFieldOfStruct> line;
    
    RunOnReceive(GralTableLine_ifc<InspcFieldOfStruct> line){
      this.line = line;
    }
    
    @Override public void run(){
      InspcFieldTable.this.showValue(line, false);
    }
  }
  
  
  /**An instance of this class is used to prepare setValueByPath on a changed value in a line.
   */
  public static class TxOrderSetValue implements Runnable
  {
    final String sValue;
    final InspcVariable var;

    public TxOrderSetValue(String sValue, InspcVariable var)
    { this.sValue = sValue;
      this.var = var;
    }

    /**This method will be invoked before finish of a send session. */
    @Override public void run()
    {
      var.ds.targetAccessor.cmdSetStringByPath(var, sValue);
    }
    
    
  }
  
  
  /**Sizes in table in Graphic grid units.*/
  private static final int sizeStruct = 3, sizeName = 20, sizeType = 10;
  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<InspcFieldOfStruct> widgTable;

  
  private final GralButton btnBack, btnHelp, btnRefresh, btnShowAll, btnSetValue, btnRepeat;
  
  /**Aggregation. To get variable, send requests etc. */
  private final InspcMng inspcMng;
  
  /**This index stores the last selected file for any component which was used.
   * If the component path is reused later, the same field will be selected initially.
   * It helps by navigation through the file tree.
   * <ul>
   * <li>The key is the path in canonical form with '/' as separator (in windows too!) 
   *   but without terminating '/'.
   * <li>The value is the name of the file in this directory.   
   * </ul>
   */
  private final Map<String, String> indexSelection = new TreeMap<String, String>(); 
  

  
  //private InspcStruct struct;
  private InspcVariable structVar;
  
  /**Path in target to the struct which is shown in table.  */
  private String sPathStruct;
  
  
  /**Name and path to the current selected field. */
  private String sFieldCurrent, sPathCurrent;
  
  private long timeLineSelected;
  
  /**Set in graphic thread with {@link #btnSetValue}, ask and reset in the InspcMng-thread. */
  protected AtomicBoolean XXXbSetValue = new AtomicBoolean();
  
  public InspcFieldTable(GralMng gralMng, InspcMng inspcMng)
  { //inspcMng.addUserOrder(this);  //invoke run in any communication step.
    final GralPos refPos = new GralPos(gralMng);
    this.wind = new GralWindow(refPos, "@screen,16+44, 20+60=InspcFieldTableWin", "Fields of ...", GralWindow_ifc.windOnTop | GralWindow_ifc.windResizeable);
    this.wind.setVisible(false);
    this.widgPath = new GralTextField(refPos, "@0+2,3..0=Path");
    this.widgTable = new GralTable<InspcFieldOfStruct>(refPos, "@2..-2,0..0=InspcFieldTable", 20, new int[]{sizeStruct, sizeName, 0, -sizeType});
    this.widgTable.setColumnEditable(2, true);
    this.widgTable.setActionChange(this.actionChgTable);
    this.widgTable.specifyActionOnLineSelected(actionLineSelected);
    this.widgTable.setHtmlHelp("HelpInspc.html#Topic.HelpInspc.fieldsof.");
    this.btnBack = new GralButton(refPos, "@0..2,0+3=InspcFieldBack", "<<", actionBack);
    this.btnHelp = new GralButton(refPos,"@-2..0,0+9++1=InspcFieldHelp", "help [F1]", gralMng.actionHelp);
    this.btnRefresh = new GralButton(refPos, "InspcFieldRefresh", "refresh [F5]", actionRefresh);
    this.btnShowAll = new GralButton(refPos, "InspcFieldShowAll", "showAll [c+]", actionShowAll);
    this.btnSetValue = new GralButton(refPos, "InspcFieldSetValue", "set values", actionSetValues);
    this.btnRepeat = new GralButton(refPos, "InspcFieldSetValue", "repeat", null);
    this.btnRepeat.setSwitchMode(GralColor.getColor("gn"), GralColor.getColor("wh"), null);
    this.inspcMng = inspcMng;
  }
  
  
  
  
  
  void fillTableFromFocusedVariable(){
    GralWidget widgd = wind.gralMng().getWidgetInFocus();
    if(widgd !=null){
      String sDatapathWithPrefix = widgd.getDataPath();
      String sDatapath = widgd.gralMng().getReplacerAlias().replaceDataPathPrefix(sDatapathWithPrefix);
      int posLastDot = sDatapath.lastIndexOf('.');
      if(posLastDot >=0) {
        sPathStruct = sDatapath.substring(0, posLastDot);  //With device:path
      } else {
        sPathStruct = "";
      }
      VariableAccess_ifc vari = inspcMng.getVariable(sDatapath);
      if(vari instanceof InspcVariable){
        InspcVariable vars = (InspcVariable) vari;
        if(vars.getStruct() != null) {
          this.structVar = vars; //It is a non-leafe-variable, use it to show the fields. 
        } else {
          this.structVar = vars.parent;   //for a leaf variable, normal case, if it is a non-leafe-variable, use its parent though.
        }
        //if(structVar == null)
        //struct = var.struct();
        //- sPathStruct = struct.path();  //NOTE it is without device.
        //
        fillTableStruct(true);  //checks whether the struct is updated, requests update.
        //
      } else { //NOTE: fillTableStruct sets the widgPath too.
        widgPath.setText(sPathStruct);
      }
    }
    widgTable.setFocus(); 
    wind.setVisible(true);
    
  }
  
  
  
  /**Fills the graphic table to show the structural content of a data node (a C struct or class). 
   * @since 2018-09: For array elements uses the {@link InspcFieldOfStruct#hasSubstruct} info from the parent (the field of the array).
   *   Note: It comes from the target: "type..." or "type[size]..." in the telegram. 
   * @param bCanRequest
   */
  void fillTableStruct(boolean bCanRequest){
    widgTable.clearTable();
    InspcStruct struct = structVar.getOrCreateStructForNonLeafVariables();
    if(struct.isUpdated()){
      //search the root:
      List<InspcVariable> parents = new LinkedList<InspcVariable>();
      InspcVariable parent = structVar;
      while(parent !=null) {
        parents.add(parent);
        parent = parent.parent;
      }
      //fill parents
      ListIterator<InspcVariable> iter = parents.listIterator(parents.size());
      
      while( iter.hasPrevious()) {  //traverse through list from start.
        parent = iter.previous();
        //InspcStruct struct1 = parent.struct();
        InspcFieldOfStruct fieldParent = new InspcFieldOfStruct(parent, null, 0, true);
        GralTableLine_ifc<InspcFieldOfStruct> line = widgTable.addLine(parent.ds.sName, null, fieldParent);
        line.setCellText("/", 0);
        line.setCellText(parent.ds.sName, 1);
        //line.setCellText()
        line.setDataPath(parent.ds.sDataPath);
      }
      //
      //fill with all fields
      //
      for(InspcFieldOfStruct field: struct.fieldIter()){
        GralTableLine_ifc<InspcFieldOfStruct> line = widgTable.addLine(field.nameShow, null, field);
        if(field.hasSubstruct) { 
          line.setCellText("+", 0);
        } else {
          line.setCellText("", 0);
        }
        line.setCellText(field.nameShow, 1);
        line.setCellText(field.type, 3);
        line.setDataPath(sPathStruct + "." + field.nameShow);
      }
      //
      //Select last line:
      //
      String key = indexSelection.get(sPathStruct);
      if(key !=null){
        widgTable.setCurrentLine(key);
      }
    } else if(bCanRequest) {
      GralTableLine_ifc<InspcFieldOfStruct> line = widgTable.addLine("$", null, null);
      line.setCellText("pending request", 1);
      InspcAccess_ifc target = structVar.ds.targetAccessor;
      //=========>
      struct.requestFields();  //clear the struct, set to request.
      target.requestFields(struct.varOfStruct(inspcMng).ds, struct.rxActionGetFields, actionUpdated);
    } else { //!bCanRequest - calling parameter:
      GralTableLine_ifc<InspcFieldOfStruct> line = widgTable.addLine("?", null, null);
      line.setCellText("...no answer", 1);
    }
    
  }
  
  
  
  
  /**Shows the current value inside the target for the specified line.
   * This routine is called both if the operator requests a refresh of content of the line: {@link #actionChgTable}
   * or if a new value was received: {@link RunOnReceive#run()} invoked from {@link InspcVariable#requestValue(long, Runnable)}.
   * The last one method requestValue() is invoked in this routine if the argument request is set to true. 
   * @param line The line of this table, contains the {@link InspcFieldOfStruct} as user data , {@link GralTableLine_ifc#getUserData()}.
   * Therein the variable for this line is stored or will be created with {@link InspcMng#getOrCreateVariable(InspcStruct, InspcFieldOfStruct)}. 
   * @param request true then a new value for the variable of this line will be requested here. 
   */
  private void showValue(GralTableLine_ifc<InspcFieldOfStruct> line, boolean request){
    InspcFieldOfStruct field = line.getUserData();
    if(field !=null){
      InspcVariable var = field.variable(structVar, inspcMng);  //get or create the variable for the field
      if(var !=null){
        long time = System.currentTimeMillis();
        long timelast = var.getLastRefreshTime();
        char cType = var.getType();
        String sVal;
        switch(cType){
          case 'F': { float val = var.getFloat(); sVal = Float.toString(val); } break;
          case 'I': { int val = var.getInt(); sVal = "0x" + Integer.toHexString(val); } break;
          case 'c': case 's': { sVal = var.getString(); } break;
          default: { float val = var.getFloat(); sVal = Float.toString(val); }
        }
        int dtime = (int)(time - timelast);
        if(timelast == 0 || dtime > 5000){ //5 sec
          sVal = "? " + sVal;  //a really old value or never gotten.
        }
        if(request && timelast == 0 || dtime > 500){ //request a new value if it is older than 0.5 sec
          var.requestValue(time, this.new RunOnReceive(line)); 
        }
        line.setCellText(sVal, 2);
      }
    }
    
  }
  
  
  void refresh(){
    InspcStruct struct = structVar.getOrCreateStructForNonLeafVariables();
    struct.requestFields();
    fillTableStruct(true);
  }
  
  
  void showAll(){
    fillTableStruct(true);
    for(GralTableLine_ifc<InspcFieldOfStruct> line: widgTable.iterLines()){
      showValue(line, true);
    }
  }
  
  
  
  public String getCurrentPathField(){ return sPathCurrent; }
  
  
  
  public static String getDataPath(GralTable<InspcFieldOfStruct>.TableLineData line)
  { String sPath = null;
    String sField1 = line.getKey();
    //sPathCurrent = line.getDataPath();
    InspcFieldOfStruct field = line.nd_data;
    InspcVariable var = field.variableExisting();
    if(var !=null) {
      sPath = var.ds.sPathWithAlias;
    }
    else 
    { //The varOfStruct is exisiting in any case. get it to get the short data path.
      InspcVariable varStruct = field.struct.varOfStruct(null);
      if(sField1.charAt(0) =='[') { //array element
        sPath = varStruct.ds.sPathWithAlias + sField1;
      }
      else if(":.".indexOf(varStruct.ds.sPathWithAlias.charAt(varStruct.ds.sPathWithAlias.length()-1))<0) {
        sPath = varStruct.ds.sPathWithAlias + "." + sField1;
      } else {
        sPath = varStruct.ds.sPathWithAlias + sField1;
      }
    }
    return sPath;
  }
  
  
  
  
  void setCurrentFieldInfo(GralTable<InspcFieldOfStruct>.TableLineData line){
    sFieldCurrent = line.getKey();
    sPathCurrent = getDataPath(line);
    timeLineSelected = System.currentTimeMillis();
    widgPath.setText(sPathCurrent + " : " + line.getCellText(3));

  }
  
  
  void actionBack(){
    GralTable<InspcFieldOfStruct>.TableLineData line = widgTable.getCurrentLine();
    String key;
    if(line !=null) {
      key = line.getKey();
      indexSelection.put(sPathStruct, key);  //select the current field if the view goes back to this table
    }  
    InspcVariable parent = structVar.parent;
    if(parent !=null){
      int posNameInPath = sPathStruct.lastIndexOf('.')+1; 
      key = sPathStruct.substring(posNameInPath);   //it is the field name of this table in the parent.
      this.structVar = parent;
      this.sPathStruct = parent.ds.sDataPath;
      indexSelection.put(sPathStruct, key);  //select the field of the leaved table in its parent!
      fillTableStruct(true);
    }
  }
  
  
  void getSubStruct(GralTableLine_ifc<InspcFieldOfStruct> line){
    String key = line.getKey();
    indexSelection.put(sPathStruct, key);
    
    InspcFieldOfStruct field = line.getUserData();
    if(field.nrofArrayElements >1){
      if(!line.hasChildren()){
        //no array elements yet initialized:
        //InspcVariable varArray = field.variable(structVar, inspcMng);
        InspcStruct structArray = field.struct;  //varArray.struct();
        String[] lineTexts = new String[4];
        for(int ix = 0; ix < field.nrofArrayElements; ++ix) {
          String ident = field.identifier + "[" + ix + "]";  //creates a field with index
          //The field for the array element:
          boolean bHasSubstruct = field.hasSubstruct; //The info about sub structure is stored in the array field already.
          InspcFieldOfStruct fieldElement = new InspcFieldOfStruct(structArray, ident, ident, field.type, -1, bHasSubstruct);
          lineTexts[0] = "-";
          lineTexts[1] = ident;
          lineTexts[2] = "";
          lineTexts[3] = field.type;
          line.addChildLine(ident, lineTexts, fieldElement);
        }
      }
      line.showChildren(true, false, true);
    }
    else if(field.hasSubstruct){
      structVar = field.variable(structVar, inspcMng);
      sPathStruct = structVar.ds.sDataPath;
      fillTableStruct(true);
    }
  }
  
  
  
  void actionSetValues(){
    for(GralTableLine_ifc<InspcFieldOfStruct> line: widgTable.iterLines()){
      if(line.isChanged(true)){
        sendValueChange(line);
      }
    }
  }
  
  /**This method is invoked if the value in the table is changed and this change should be applied to the target, key ctrl-enter. */
  void sendValueChange(GralTableLine_ifc<InspcFieldOfStruct> line) {
    InspcFieldOfStruct field = line.getUserData(); ////
    String sValue = line.getCellText(2);
    InspcVariable var = field.variable(structVar, inspcMng);  //creates the variable if not given yet.
    TxOrderSetValue order = new TxOrderSetValue(sValue, var);
    var.ds.targetAccessor.addUserTxOrder(order);
  }
  
  
  
  
  
  
  GralUserAction actionOpenWindow = new GralUserAction("InspcFieldTable - open window"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        fillTableFromFocusedVariable();
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  /**This action is invoked on any keyboard hits which are not handled in the GralTable.
   * 
   */
  GralUserAction actionChgTable = new GralUserAction("InspcFieldTable - change Table"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        assert(params[0] instanceof GralTableLine_ifc<?>);
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<InspcFieldOfStruct> line = (GralTableLine_ifc<InspcFieldOfStruct>)params[0];
        if(key == KeyCode.enter){
          showValue(line, true);
        } else if(key == KeyCode.ctrl + KeyCode.enter) {
          sendValueChange(line); ////
        } else if(key == KeyCode.ctrl + 'R' || key == KeyCode.F5){
          refresh();
        } else if(key == KeyCode.ctrl + KeyCode.pgup) {
          actionBack();
        } else if(key == KeyCode.ctrl + KeyCode.pgdn) {
          getSubStruct(line);
        } else if(key == KeyCode.ctrl + '+') {
          showAll();
        }
        return true;
      } else if(key == KeyCode.mouse1Double){
        assert(params[0] instanceof GralTableLine_ifc<?>);
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<InspcFieldOfStruct> line = (GralTableLine_ifc<InspcFieldOfStruct>)params[0];
        InspcFieldOfStruct field = line.getUserData();
        if(field.hasSubstruct){
          getSubStruct(line);
        } else {
          showValue(line, true);
        }
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  GralUserAction actionLineSelected = new GralUserAction("InspcFieldTable - line selected"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(key == KeyCode.defaultSelect || key == KeyCode.userSelect){
        @SuppressWarnings("unchecked")
        GralTable<InspcFieldOfStruct>.TableLineData line = (GralTable.TableLineData)params[0];
        if(line !=null && line.nd_data !=null) { setCurrentFieldInfo(line); }
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  
  GralUserAction actionBack = new GralUserAction("InspcFieldTable - back"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        actionBack();
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  
  GralUserAction actionRefresh = new GralUserAction("InspcFieldTable - refresh"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        refresh();
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  GralUserAction actionShowAll = new GralUserAction("InspcFieldTable - show all"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        showAll();
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  GralUserAction actionSetValues = new GralUserAction("InspcFieldTable - set values"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        //bSetValue.set(true);
        actionSetValues();
        //InspcFieldTable.this.inspcMng.addUserOrder(InspcFieldTable.this);
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  Runnable actionUpdated = new Runnable(){
    @Override public void run(){ fillTableStruct(false); }  //after receive the datagram, can not request newly.
  };

  
  
}
