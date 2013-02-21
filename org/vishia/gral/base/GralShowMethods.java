package org.vishia.gral.base;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.KeyCode;

/**This class contains some standard {@link GralUserAction} for widgets.
 * This class should be realized on user level with proper {@link VariableContainer_ifc}.
 * The show methods can be used by giving its name with {@link GralWidget#setShowMethod(String)} in the configuration
 * of any widget. The show method may use parameter given with {@link GralWidget#setDataPath(String)}
 * or alternatively with parameter of the show method, for example:
 * <pre>
 * myWidget.setShowMethod("showBool(datapath, green, red)";
 * </pre>
 * The string given parameters should be separated with a colon. They are evaluated by first calling the show method.
 * The show method can be configured in a script, see {@link org.vishia.gral.cfg.GralCfgZbnf}.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralShowMethods 
{

  private String getParams;
  
  
  /**Common aggregation to variables. */
  protected final VariableContainer_ifc variableContainer;
  

  
  public GralShowMethods(VariableContainer_ifc container){
    variableContainer = container;
  }
  
  
  /**Shows the back color of the widget depending on the boolean value of a variable.
   * param of exec should be a VariableAccessWithIdx-instance. The variable value 0, 1, ... is used to select one of the back colors. */
  public final GralUserAction showBackColor = new GralUserAction("showBackColor"){
    
    //Note: don't save data here. It is a common instance.
    
    @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params){ 
      GralWidget widgg = (GralWidget) widgd;
      if(widgg.cfg.showParam == null){ //not configurated yet:
        String[] sColors = widgg.getShowParam();
        if(sColors == null){
          System.err.println("GralShowMethods.showBackColor config error; The show methods should contain \"(color, color)\";"
              + widgg.getShowMethod());
          widgg.cfg.showParam = new GralColor[1];
          widgg.cfg.showParam[0] = GralColor.getColor("ma");
        } else {
          widgg.cfg.showParam = new GralColor[sColors.length];
          for(int iDst = 0; iDst < sColors.length; ++iDst){
            widgg.cfg.showParam[iDst] = GralColor.getColor(sColors[iDst]);
          }
        }
      }
      int value;
      if(params[0] instanceof VariableAccessWithIdx){
        VariableAccessWithIdx variable = (VariableAccessWithIdx)params[0];
        value = variable.getInt();
        if(value>=0 && value < widgg.cfg.showParam.length){ widgd.setBackColor((GralColor)widgg.cfg.showParam[value], 0); }
        else { widgd.setBackColor((GralColor)widgg.cfg.showParam[0], 0); }
      } else {
        String name = widgd.getName();
        System.err.println("GralShowMethods.showBackColor parameter error; widget=" + name);
      }
      return true;
    }

  };
  
  
  
  /**This userAction can be used by name (calling {@link #addFocusAction(String, GralUserAction, String, String)} 
   * to set a variable when an input field is leaved.
   */
  private final GralUserAction syncVariableOnFocus = new GralUserAction("syncVariableOnFocus")
  { /**Writes the value to the named variable on leaving the focus.
     * The name of the variable is contained in the {@link GralWidget}.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(java.lang.String, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
    {
      GralWidget widg = (GralWidget)widgi;
      final VariableAccessWithIdx variable = widg.getVariableFromContentInfo(variableContainer);
      if(variable !=null){
        if(actionCode == KeyCode.focusGained){
          widg.setText(variable.getString());
          //if(oWidget instanceof Text){ sValue = ((Text)oWidget).getText(); variable.setString(sValue); }
          //else { sValue = null; }
        } else if(actionCode == KeyCode.focusLost){
          if(widg.isChanged(true)){
            variable.setString(widg.getValue());
          }
          //if(oWidget instanceof Text){ sValue = variable.getString(); ((Text)oWidget).setText(sValue == null ? "" : sValue); }
          //else { sValue = null; }
        } //else throw new IllegalArgumentException("GralMng.syncVariableOnFocus: unexpected intension on focus: " + actionCode); 
      } else throw new IllegalArgumentException("GralMng.syncVariableOnFocus: variable not found: " + widg.getDataPath()); 
      return true;
    }
  };

  
  
  public void registerShowMethods(GralMngBuild_ifc mng){
    mng.registerUserAction("showBackColor", showBackColor);
    mng.registerUserAction("syncVariableOnFocus", this.syncVariableOnFocus);
    

  }
  
  
}
