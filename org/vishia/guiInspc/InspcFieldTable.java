package org.vishia.guiInspc;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.inspectorAccessor.InspcMng;
import org.vishia.inspectorAccessor.InspcStruct;
import org.vishia.inspectorAccessor.InspcTargetAccessor;
import org.vishia.inspectorAccessor.InspcVariable;
import org.vishia.inspectorAccessor.InspcStruct.FieldOfStruct;
import org.vishia.util.KeyCode;

/**This class presents a window with one table and some buttons to view and edit all fields in one instance
 * in a target system.
 * 
 * 
 * <img src="../../../img/guiInspc_getFieldsTableOmd.png">
 * <br>
 * The InspcFieldTable shows one struct from target data or one instance, all fields of a class of Java reflection view. 
 * The fields are stored in instances of {@link InspcStruct.FieldOfStruct} which are referenced as data in the {@link GralTable}
 * of the private composite reference {@link #widgTable}. 
 * <br><br>
 * The content of the table is filled with a request {@link InspcMng#requestFields(InspcStruct)} which is invoked via the 
 * private method {@link #fillTableStruct()} respectively {@link #fillTableFromFocusedVariable()} on opening the window.
 * If the {@link InspcMng#requestFields(InspcStruct)} was invoked it sets the reference to the requested {@link InspcStruct}
 * into a private variable InspcMng#requestedFields. If this variable is not null a {@link InspcTargetAccessor#cmdGetFields(String, org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc)}
 * will be invoked for that struct. On receive the result the assignment to the requested order invokes the {@link InspcStruct.VariableRxAction}
 * for any filed which fills the struct. 
 *  
 * <br><br>
 * <b>Filling the table with the fields of a reflection target</b>:<br>
 *   
 * <img src="../../../img/guiInspc_getFieldsTableSeq.png">
 * <br>
 * One of the methods {@link #fillTableFromFocusedVariable()},  {@link #actionBack()}, {@link #getSubStruct(GralTableLine_ifc)}
 * are invoked from operator actions. They call 
 * <br> {@link #fillTableStruct()}<br>
 * This method checks the {@link InspcStruct} of the variable respectively the shown struct in table whether it is known already, 
 * using {@link InspcStruct#isUpdated()}. It it is not updated, on selection a new variable, the <br>
 * {@link InspcMng#requestFields(InspcStruct)} <br>
 * is called. This routine sets only the reference to the {@link InspcStruct} in a variable. This action is done especially in the graphical thread.
 * <br><br>
 * If {@link InspcStruct#isUpdated()} returns true, then the struct was updated already. Then it can be shown in the {@link GralTable}
 * with the names of its elements and the last gotten values.
 * <br><br>
 * The organization thread of {@link InspcMng} invokes {@link InspcMng#procComm()} cyclically to show and request values. 
 * In this thread the requested struct is recognized, for that the {@link InspcTargetAccessor#cmdGetFields(String, org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc)}
 * is invoked for the path of the struct. That is sent to the target.
 * <br><br>
 * The target answers with some items for all fields in one or more datagram but for that request with the same sequence number. 
 * For any items respectively element of the struct the method {@link InspcStruct#rxActionGetFieldsInspcDataExchangeAccess.Inspcitem, long)}
 * is invoked which fills the struct.
 * <br><br>
 * The {@link InspcMng#requestFields(InspcStruct)} was sent with an {@link InspcAccessExecRxOrder_ifc} which knows an
 * {@link InspcAccessExecRxOrder_ifc#callbackOnAnswer()}. TODO give direct, be explicitely! This callback is invoked on the last received datagram
 * for this request, which call {@link #fillTableStruct()} again, now with updated fields. 
 * 
 * <ul> 
 * 
 * 
 * The {@link InspcStruct#requestFields(Runnable)} invokes 
 * 
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcFieldTable implements Runnable
{
  /**Version, history and license.
   * <ul>
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
  protected final static String sVersion = "2014-01-06";

  class RunOnReceive implements Runnable {
    //GralTable<FieldOfStruct>.TableLineData line;
    GralTableLine_ifc<FieldOfStruct> line;
    
    RunOnReceive(GralTableLine_ifc<FieldOfStruct> line){
      this.line = line;
    }
    
    @Override public void run(){
      InspcFieldTable.this.showValue(line, false);
    }
  }
  
  
  
  private static final int sizeName = 20, sizeType = 10;
  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<InspcStruct.FieldOfStruct> widgTable;

  
  private final GralButton btnBack, btnRefresh, btnShowAll, btnSetValue;
  
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
  

  
  private InspcStruct struct;
  
  /**Path in target to the struct which is shown in table.  */
  private String sPathStruct;
  
  
  /**Name and path to the current selected field. */
  private String sFieldCurrent, sPathCurrent;
  
  private long timeLineSelected;
  
  /**Set in graphic thread with {@link #btnSetValue}, ask and reset in the InspcMng-thread. */
  protected AtomicBoolean bSetValue = new AtomicBoolean();
  
  public InspcFieldTable(InspcMng inspcMng)
  { inspcMng.addUserOrder(this);  //invoke run in any communication step.
    this.wind = new GralWindow(null, "InspcFieldTableWind", "Fields of ...", GralWindow_ifc.windOnTop | GralWindow_ifc.windResizeable);
    this.widgPath = new GralTextField("InspcFieldTableWind");
    this.widgTable = new GralTable<InspcStruct.FieldOfStruct>("InspcFieldTable", new int[]{sizeName, 0, -sizeType});
    this.widgTable.setColumnEditable(1, true);
    this.widgTable.setActionChange(this.actionChgTable);
    this.widgTable.specifyActionOnLineSelected(actionLineSelected);
    this.btnBack = new GralButton("@InspcFieldBack", "<<", actionBack);
    this.btnRefresh = new GralButton("@InspcFieldRefresh", "refresh", actionRefresh);
    this.btnShowAll = new GralButton("@InspcFieldShowAll", "show all", actionShowAll);
    this.btnSetValue = new GralButton("@InspcFieldSetValue", "set values", actionSetValues);
    this.inspcMng = inspcMng;
  }
  
  
  public void setToPanel(GralMng mng){
    wind.setToPanel(mng);
    mng.setPosition(0, 2, 0, 3, 0, 'd');
    btnBack.setToPanel(mng);
    mng.setPosition(0, 2, 3, 0, 0, 'd');
    widgPath.setToPanel(mng);
    mng.setPosition(2, -2, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
    mng.setPosition(-2, 0, 0, 7, 0, 'd');
    btnRefresh.setToPanel(mng);
    mng.setPosition(-2, 0, sizeName, sizeName + 12, 0, 'r');
    btnSetValue.setToPanel(mng);
    mng.setPosition(-2, 0, sizeName + 13, sizeName + 23, 0, 'r');
    btnShowAll.setToPanel(mng);
  }
  
  
  
  void fillTableFromFocusedVariable(){
    GralWidget widgd = wind.gralMng().getWidgetInFocus();
    if(widgd !=null){
      String sDatapathWithPrefix = widgd.getDataPath();
      String sDatapath = widgd.gralMng().replaceDataPathPrefix(sDatapathWithPrefix);
      int posLastDot = sDatapath.lastIndexOf('.');
      if(posLastDot >=0) {
        sPathStruct = sDatapath.substring(0, posLastDot);  //With device:path
      } else {
        sPathStruct = "";
      }
      VariableAccess_ifc vari = inspcMng.getVariable(sDatapath);
      if(vari instanceof InspcVariable){
        InspcVariable var = (InspcVariable)vari;
        struct = var.struct();
        //- sPathStruct = struct.path();  //NOTE it is without device.
        //
        fillTableStruct();
        //
      } else { //NOTE: fillTableStruct sets the widgPath too.
        widgPath.setText(sPathStruct);
      }
    }
    widgTable.setFocus(); 
    wind.setVisible(true);
    
  }
  
  
  
  void fillTableStruct(){
    widgTable.clearTable();
    if(struct.isUpdated()){
      //
      //fill with all fields
      //
      for(InspcStruct.FieldOfStruct field: struct.fieldIter()){
        GralTableLine_ifc<InspcStruct.FieldOfStruct> line = widgTable.addLine(field.name, null, field);
        String sField1 = field.hasSubstruct ? "+ " + field.name : field.name;
        line.setCellText(sField1, 0);
        line.setCellText(field.type, 2);
        line.setDataPath(sPathStruct + "." + field.name);
      }
      //
      //Select last line:
      //
      String key = indexSelection.get(sPathStruct);
      if(key !=null){
        widgTable.setCurrentLine(key);
      }
    } else {
      GralTableLine_ifc<InspcStruct.FieldOfStruct> line = widgTable.addLine("$", null, null);
      line.setCellText("pending request", 0);
      InspcTargetAccessor target = struct.targetAccessor();
      struct.requestFields();
      target.requestFields(struct, actionUpdated);
      //struct.requestFields(actionUpdated);
      //inspcMng.requestFields(struct);
    }
    
  }
  
  
  
  
  /**Shows the received value inside the target for the specified line.
   * This routine is called both if the operator requests a refresh of content of the line: {@link #actionChgTable}
   * or if a new value was received: {@link RunOnReceive#run()} invoked from {@link InspcVariable#requestValue(long, Runnable)}.
   * The last one method requestValue() is invoked in this routine if the argument request is set to true. 
   * @param line The line of this table, contains the {@link InspcStruct.FieldOfStruct} as user data , {@link GralTableLine_ifc#getUserData()}.
   * Therein the variable for this line is stored or will be created with {@link InspcMng#getOrCreateVariable(InspcStruct, FieldOfStruct)}. 
   * @param request true then a new value for the variable of this line will be requested here. 
   */
  private void showValue(GralTableLine_ifc<InspcStruct.FieldOfStruct> line, boolean request){
    InspcStruct.FieldOfStruct field = line.getUserData();
    if(field !=null){
      InspcVariable var = inspcMng.getOrCreateVariable(struct, field);
      if(var !=null){
        long time = System.currentTimeMillis();
        long timelast = var.getLastRefreshTime();
        if(request){ var.requestValue(time, this.new RunOnReceive(line)); }
        char cType = var.getType();
        String sVal;
        switch(cType){
          case 'F': { float val = var.getFloat(); sVal = Float.toString(val); } break;
          case 'I': { int val = var.getInt(); sVal = "0x" + Integer.toHexString(val); } break;
          case 'c': case 's': { sVal = var.getString(); } break;
          default: { float val = var.getFloat(); sVal = Float.toString(val); }
        }
        if(timelast == 0 || (time - timelast) > 10000){ //10 sec
          sVal = "? " + sVal;
        }
        line.setCellText(sVal, 1);
      }
    }
    
  }
  
  
  void showAll(){
    fillTableStruct();
    for(GralTableLine_ifc<InspcStruct.FieldOfStruct> line: widgTable.iterLines()){
      showValue(line, true);
    }
  }
  
  
  
  public String getCurrentPathField(){ return sPathCurrent; }
  
  
  
  
  
  void setCurrentFieldInfo(GralTable<InspcStruct.FieldOfStruct>.TableLineData line){
    sFieldCurrent = line.getKey();
    sPathCurrent = line.getDataPath();
    timeLineSelected = System.currentTimeMillis();
    widgPath.setText(sPathCurrent + " : " + line.getCellText(2));

  }
  
  
  void actionBack(){
    GralTable<InspcStruct.FieldOfStruct>.TableLineData line = widgTable.getCurrentLine();
    String key = line.getKey();
    indexSelection.put(sPathStruct, key);  //select the current field if the view goes back to this table
    
    InspcStruct parent = struct.parent();
    if(parent !=null){
      int posNameInPath = sPathStruct.lastIndexOf('.')+1; 
      key = sPathStruct.substring(posNameInPath);   //it is the field name of this table in the parent.
      this.struct = parent;
      this.sPathStruct = parent.path();
      indexSelection.put(sPathStruct, key);  //select the field of the leaved table in its parent!
      fillTableStruct();
    }
  }
  
  
  void getSubStruct(GralTableLine_ifc<InspcStruct.FieldOfStruct> line){
    String key = line.getKey();
    indexSelection.put(sPathStruct, key);
    
    InspcStruct.FieldOfStruct field = line.getUserData();
    InspcStruct substruct = field.substruct();
    if(substruct != null){
      struct = substruct;
      sPathStruct = struct.path();
      fillTableStruct();
    }
  }
  
  
  
  void actionSetValues(){
    for(GralTableLine_ifc<InspcStruct.FieldOfStruct> line: widgTable.iterLines()){
      if(line.isChanged(true)){
        InspcStruct.FieldOfStruct field = line.getUserData();
        String value = line.getCellText(1);
        inspcMng.cmdSetValueOfField(struct, field, value);
      }
    }
  }
  
  
  /**This method is invoked in {@link InspcMng#procComm()} because it is registered there.
   * It prepares set value.
   */
  @Override public void run(){
    if(bSetValue.compareAndSet(true, false)){
      actionSetValues();
    }
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
        GralTableLine_ifc<InspcStruct.FieldOfStruct> line = (GralTableLine_ifc<InspcStruct.FieldOfStruct>)params[0];
        if(key == KeyCode.enter){
          showValue(line, true);
        } else if(key == KeyCode.ctrl + KeyCode.enter) {
          bSetValue.set(true);
          InspcFieldTable.this.inspcMng.addUserOrder(InspcFieldTable.this);
        } else if(key == KeyCode.ctrl + 'R' || key == KeyCode.F5){
          showAll();
        } else if(key == KeyCode.ctrl + KeyCode.pgup) {
          actionBack();
        } else if(key == KeyCode.ctrl + KeyCode.pgdn) {
          getSubStruct(line);
        }
        return true;
      } else if(key == KeyCode.mouse1Double){
        assert(params[0] instanceof GralTableLine_ifc<?>);
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<InspcStruct.FieldOfStruct> line = (GralTableLine_ifc<InspcStruct.FieldOfStruct>)params[0];
        InspcStruct.FieldOfStruct field = line.getUserData();
        InspcStruct substruct = field.substruct();
        if(substruct != null){
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
        GralTable<InspcStruct.FieldOfStruct>.TableLineData line = (GralTable.TableLineData)params[0];
        if(line !=null) { setCurrentFieldInfo(line); }
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
        bSetValue.set(true);
        InspcFieldTable.this.inspcMng.addUserOrder(InspcFieldTable.this);
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  Runnable actionUpdated = new Runnable(){
    @Override public void run(){ fillTableStruct(); }
  };

  
  
}
