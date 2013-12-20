package org.vishia.guiInspc;

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
public class InspcFieldTable
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

  
  
  /**The window to present. */
  private final GralWindow wind;
  
  /**Shows the path in target to this struct. */
  private final GralTextField widgPath;
  
  /**Table of fields, type and value. */
  private final GralTable<InspcStruct.Field> widgTable;

  
  private final GralButton btnBack, btnRefresh, btnShowAll;
  
  private final InspcMng variableMng;
  
  
  private String sPathStruct;
  
  public InspcFieldTable(InspcMng variableMng)
  {
    super();
    this.wind = new GralWindow("InspcFieldTableWind", "Fields of ...", GralWindow_ifc.windOnTop);
    this.widgPath = new GralTextField("InspcFieldTableWind");
    this.widgTable = new GralTable<InspcStruct.Field>("InspcFieldTable", new int[]{20, 0, -10});
    this.widgTable.setActionChange(actionChgTable);
    this.btnBack = null; //new GralButton("@InspcFieldBack", null);
    this.btnRefresh = null; //new GralButton("@InspcFieldRefresh", null);
    this.btnShowAll = null; //new GralButton("@InspcFieldShowAll", null);
    this.variableMng = variableMng;
  }
  
  
  public void setToPanel(GralMng mng){
    wind.setToPanel(mng);
    mng.setPosition(0, 2, 3, -5, 0, 'd');
    widgPath.setToPanel(mng);
    mng.setPosition(2, -2, 0, 0, 0, 'd');
    widgTable.setToPanel(mng);
  }
  
  
  
  void chgTable(int key, GralTableLine_ifc<InspcStruct.Field> line){
    if(key == KeyCode.enter){
      InspcStruct.Field field = line.getUserData();
      InspcVariable var = field.variable();
      if(var == null){
        String sPathVar = sPathStruct + '.' + field.name;
        var = (InspcVariable)variableMng.getVariable(sPathVar);
        if(var !=null){
          field.setVariable(var);
        }
      }
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
      Assert.stop();
    }
    
  }
  
  
  void fillTableWithFields(){
    GralWidget widgd = wind.gralMng().getWidgetInFocus();
    if(widgd !=null){
      String sDatapathWithPrefix = widgd.getDataPath();
      String sDatapath = widgd.gralMng().replaceDataPathPrefix(sDatapathWithPrefix);
      int posLastDot = sDatapath.lastIndexOf('.');
      sPathStruct = sDatapath.substring(0, posLastDot);  //With device:path
      VariableAccess_ifc vari = variableMng.getVariable(sDatapath);
      if(vari instanceof InspcVariable){
        InspcVariable var = (InspcVariable)vari;
        InspcStruct struct = var.struct();
        //- sPathStruct = struct.path();  //NOTE it is without device.
        widgTable.clearTable();
        if(struct.isUpdated()){
          for(InspcStruct.Field field: struct.fieldIter()){
            GralTableLine_ifc<InspcStruct.Field> line = widgTable.addLine(field.name, null, field);
            line.setCellText(field.name, 0);
            line.setCellText(field.type, 2);
          }
        } else {
          GralTableLine_ifc<InspcStruct.Field> line = widgTable.addLine("$", null, null);
          line.setCellText("pending request", 0);
        }
      }
      widgPath.setText(sPathStruct);
    }

    wind.setVisible(true);
    
  }
  
  
  
  
  
  GralUserAction actionOpenWindow = new GralUserAction("InspcFieldTable - open window"){
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        fillTableWithFields();
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
        GralTableLine_ifc<InspcStruct.Field> line = (GralTableLine_ifc<InspcStruct.Field>)params[0];
        chgTable(key, line);
        return true;
      } else { 
        return false;
      }
    }
  };

  
  
  
  
  
  
}
