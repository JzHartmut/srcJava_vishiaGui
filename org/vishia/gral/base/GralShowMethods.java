package org.vishia.gral.base;

import org.vishia.byteData.VariableAccessWithIdx;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;

/**This class contains some show methods for widgets.
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
  
  
  public void registerShowMethods(GralMngBuild_ifc mng){
    mng.registerUserAction("showBackColor", showBackColor);
  }
  
  
}
