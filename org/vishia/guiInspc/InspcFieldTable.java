package org.vishia.guiInspc;

import java.util.concurrent.atomic.AtomicBoolean;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralTable.TableLineData;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.inspectorAccessor.InspcMng;
import org.vishia.inspectorAccessor.InspcStruct;
import org.vishia.inspectorAccessor.InspcVariable;
import org.vishia.util.Assert;
import org.vishia.util.KeyCode;

/**This class presents a window with one table and some buttons to view and edit all fields in one instance
 * in a target system.
 * 
 * @author Hartmut Schorrig
 *
 */
public class InspcFieldTable implements Runnable
{
  /**Version, history and license.
   * <ul>
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
  protected final static int version = 0x20131218;

  private static final int sizeName = 20, sizeType = 10;
  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<InspcStruct.FieldOfStruct> widgTable;

  
  private final GralButton btnBack, btnRefresh, btnShowAll, btnSetValue;
  
  private final InspcMng variableMng;
  
  private InspcStruct struct;
  
  private String sPathStruct;
  
  /**Set in graphic thread with {@link #btnSetValue}, ask and reset in the InspcMng-thread. */
  protected AtomicBoolean bSetValue = new AtomicBoolean();
  
  public InspcFieldTable(InspcMng variableMng)
  { variableMng.addUserOrder(this);  //invoke run in any communication step.
    this.wind = new GralWindow("InspcFieldTableWind", "Fields of ...", GralWindow_ifc.windOnTop);
    this.widgPath = new GralTextField("InspcFieldTableWind");
    this.widgTable = new GralTable<InspcStruct.FieldOfStruct>("InspcFieldTable", new int[]{sizeName, 0, -sizeType});
    this.widgTable.setColumnEditable(1, true);
    this.widgTable.setActionChange(this.actionChgTable);
    this.btnBack = new GralButton("@InspcFieldBack", "<<", actionBack);
    this.btnRefresh = new GralButton("@InspcFieldRefresh", "refresh", actionRefresh);
    this.btnShowAll = new GralButton("@InspcFieldShowAll", "show all", actionShowAll);
    this.btnSetValue = new GralButton("@InspcFieldSetValue", "set values", actionSetValues);
    this.variableMng = variableMng;
  }
  
  
  public void setToPanel(GralMng mng){
    wind.setToPanel(mng);
    mng.setPosition(0, 2, 0, 3, 0, 'd');
    btnBack.setToPanel(mng);
    mng.setPosition(0, 2, 3, -5, 0, 'd');
    widgPath.setToPanel(mng);
    mng.setPosition(0, 2, -5, 0, 0, 'd');
    btnRefresh.setToPanel(mng);
    mng.setPosition(2, -2, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
    mng.setPosition(-2, 0, sizeName, sizeName + 15, 0, 'r');
    btnSetValue.setToPanel(mng);
    mng.setPosition(-2, 0, -sizeType, 0, 0, 'r');
    btnShowAll.setToPanel(mng);
  }
  
  
  
  void fillTableFromFocusedVariable(){
    GralWidget widgd = wind.gralMng().getWidgetInFocus();
    if(widgd !=null){
      String sDatapathWithPrefix = widgd.getDataPath();
      String sDatapath = widgd.gralMng().replaceDataPathPrefix(sDatapathWithPrefix);
      int posLastDot = sDatapath.lastIndexOf('.');
      sPathStruct = sDatapath.substring(0, posLastDot);  //With device:path
      VariableAccess_ifc vari = variableMng.getVariable(sDatapath);
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
      }
    } else {
      GralTableLine_ifc<InspcStruct.FieldOfStruct> line = widgTable.addLine("$", null, null);
      line.setCellText("pending request", 0);
      variableMng.requestFields(struct);
      struct.requestFields(actionUpdated);
    }
    widgPath.setText(sPathStruct);
  }
  
  
  
  
  void showValue(GralTableLine_ifc<InspcStruct.FieldOfStruct> line){
    InspcStruct.FieldOfStruct field = line.getUserData();
    if(field !=null){
      InspcVariable var = variableMng.getOrCreateVariable(struct, field);
      if(var !=null){
        var.requestValue(System.currentTimeMillis());
        char cType = var.getType();
        String sVal;
        switch(cType){
          case 'F': { float val = var.getFloat(); sVal = Float.toString(val); } break;
          case 'I': { int val = var.getInt(); sVal = Integer.toHexString(val); } break;
          default: { float val = var.getFloat(); sVal = Float.toString(val); }
        }
        line.setCellText(sVal, 1);
      }
    }
    
  }
  
  
  void showAll(){
    fillTableStruct();
    for(GralTableLine_ifc<InspcStruct.FieldOfStruct> line: widgTable.iterLines()){
      showValue(line);
    }
  }
  
  void actionBack(){
    InspcStruct parent = struct.parent();
    if(parent !=null){
      this.struct = parent;
      this.sPathStruct = parent.path();
      fillTableStruct();
    }
  }
  
  
  void getSubStruct(GralTableLine_ifc<InspcStruct.FieldOfStruct> line){
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
        variableMng.cmdSetValueOfField(struct, field, value);
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

  
  
  GralUserAction actionChgTable = new GralUserAction("InspcFieldTable - change Table"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        assert(params[0] instanceof GralTableLine_ifc<?>);
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<InspcStruct.FieldOfStruct> line = (GralTableLine_ifc<InspcStruct.FieldOfStruct>)params[0];
        if(key == KeyCode.enter){
          showValue(line);
        } else if(key == KeyCode.ctrl + KeyCode.pgup) {
          actionBack();
        } else if(key == KeyCode.ctrl + KeyCode.pgdn) {
          getSubStruct(line);
        }
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
        InspcFieldTable.this.variableMng.addUserOrder(InspcFieldTable.this);
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
