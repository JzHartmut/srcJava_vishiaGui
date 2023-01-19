package org.vishia.gral.base;

import java.io.File;

import org.vishia.byteData.VariableAccess_ifc;
import org.vishia.byteData.VariableContainer_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralPanel_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.widget.GralInfoBox;
import org.vishia.util.KeyCode;
import org.vishia.util.Java4C;

/**This class contains some standard {@link GralUserAction} for widgets.
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
//@Java4C.ParseError
public class GralShowMethods 
{

  
  /**Version, history and license.
   * <ul>
   * <li>2014-01-15 Hartmut chg: {@link #showBackColor} stores the value which is used in {@link GralWidget#getFloatValue()} (dyda).
   * <li>2013-03-13 Hartmut new {@link #setBar} for a {@link GralValueBar}
   * <li>2012-06-00 Hartmut created as common container for methods for different widgets.
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
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
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20130313;

  
  
  
  private String getParams;
  
  
  /**Common aggregation to variables. */
  protected final VariableContainer_ifc variableContainer;
  

  
  public GralShowMethods(VariableContainer_ifc container){
    variableContainer = container;
  }
  
  
  /**Shows the back color of the widget depending on the boolean value of the associated variable.
   * The params[0] of exec should be an {@link VariableAccess_ifc} instance from which the value is read.
   * It is because the routine is called as show method in {@link GralWidget#refreshFromVariable(VariableContainer_ifc)}.
   * The colors should be part of the textual arguments of the show method given in {@link GralWidget#setActionShow(GralUserAction, String[])}.
   * That textual colors are converted and stored in {@link GralWidget.ConfigData#showParam}.
   * <br><br>
   * The value which is read from the variable is stored in {@link GralWidget.DynamicData#fValue} for further using,
   * for example for a change action (on mouse pressed etc. ).
   *  
   * param of exec should be a VariableAccess_ifc-instance. The int value 0, 1, ... is used to select one of the back colors. 
   */
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
      if(params[0] instanceof VariableAccess_ifc){
        VariableAccess_ifc variable = (VariableAccess_ifc)params[0];
        value = variable.getInt();
        widgg.dyda.fValue = value;        //store the value from variable.
        if(value>=0 && value < widgg.cfg.showParam.length){ widgd.setBackColor((GralColor)widgg.cfg.showParam[value], 0); }
        else { widgd.setBackColor((GralColor)widgg.cfg.showParam[0], 0); }
      } else {
        String name = widgd.getName();
        System.err.println("GralShowMethods.showBackColor parameter error; widget=" + name);
      }
      return true;
    }

  };
  
  
  
  /**Shows the back color of the widget depending on the boolean value of a variable.
   * param of exec should be a VariableAccessWithIdx-instance. The variable value 0, 1, ... is used to select one of the back colors. */
  public final GralUserAction setBar = new GralUserAction("setBar"){
    
    //Note: don't save data here. It is a common instance.
    
    @Override public boolean exec(int actionCode, GralWidget_ifc wdgi, Object... params){ 
      if(!(wdgi instanceof GralValueBar)) return false;
      GralValueBar wdg = (GralValueBar) wdgi;
      if(wdg.fLevels == null){ //not configurated yet:
        String[] sShowParam = wdg.getShowParam();
        wdg.setBorderAndColors(sShowParam);
      }
      if(params[0] instanceof VariableAccess_ifc){
        VariableAccess_ifc variable = (VariableAccess_ifc)params[0];
        float value = variable.getFloat();
        wdg.setValue(value);
      } else {
        String name = wdgi.getName();
        System.err.println("GralShowMethods.showBackColor parameter error; widget=" + name);
      }
      return true;
    }

  };
  
  
  
  /**This userAction can be used by name (calling {@link #addFocusAction(String, GralUserAction, String, String)} 
   * to set a variable when an input field is leaved.
   */
  public final GralUserAction syncVariableOnFocus = new GralUserAction("syncVariableOnFocus")
  { /**Writes the value to the named variable on leaving the focus.
     * The name of the variable is contained in the {@link GralWidget}.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(java.lang.String, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
    {
      GralWidget widg = (GralWidget)widgi;
      final VariableAccess_ifc variable = widg.getVariable(variableContainer);
      if(variable !=null){
        if(actionCode == KeyCode.focusGained){
          widg.setText(variable.getString());
          //if(oWidget instanceof Text){ sValue = ((Text)oWidget).getText(); variable.setString(sValue); }
          //else { sValue = null; }
        } else if(actionCode == KeyCode.focusLost){
          if(widg.isChanged(true)){
            String sValue = widg.getValue();
            variable.setString(sValue);
          }
          //if(oWidget instanceof Text){ sValue = variable.getString(); ((Text)oWidget).setText(sValue == null ? "" : sValue); }
          //else { sValue = null; }
        } //else throw new IllegalArgumentException("GralMng.syncVariableOnFocus: unexpected intension on focus: " + actionCode); 
      } else {
        //throw new IllegalArgumentException("GralMng.syncVariableOnFocus: variable not found: " + widg.getDataPath()); 
      }
      return true;
    }
  };

  
  
  
  
  /**This userAction can be used by name (calling {@link #addFocusAction(String, GralUserAction, String, String)} 
   * to set a variable when an input field is leaved.
   */
  public final GralUserAction action_openWindow = new GralUserAction("openWindow")
  { /**Writes the value to the named variable on leaving the focus.
     * The name of the variable is contained in the {@link GralWidget}.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(java.lang.String, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
    {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode) || actionCode == KeyCode.mouse1Double) {
        GralWidget widg = (GralWidget)widgi;
        String nameWindow = (params.length >=1 && (params[0] instanceof String)) ? (String)params[0] : widg.sCmd;  //sCmd for buttons
        if(nameWindow !=null) {
          if(nameWindow.endsWith("wind")) { nameWindow = nameWindow.substring(0, nameWindow.length()-4); }
          GralMng mng = widgi.gralMng();
          GralPanel_ifc panelWind = mng.getPanel(nameWindow);
          if(panelWind !=null) { panelWind.setFocus(); }
          else { System.err.println("action_openWindow: window not found: " + nameWindow); }
        }
        else  { System.err.println("action_openWindow: name not given: ");}
      }
      return true;
    }
  };

  
  
  
  
  /**This userAction can be used by name (calling {@link #addFocusAction(String, GralUserAction, String, String)} 
   * to set a variable when an input field is leaved.
   */
  public final GralUserAction action_openHelp = new GralUserAction("openHtml")
  { /**Writes the value to the named variable on leaving the focus.
     * The name of the variable is contained in the {@link GralWidget}.
     * @see org.vishia.gral.ifc.GralUserAction#userActionGui(java.lang.String, org.vishia.gral.base.GralWidget, java.lang.Object[])
     */
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... params)
    {
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode) || actionCode == KeyCode.mouse1Double) {
        GralWidget widg = (GralWidget)widgi;
        String path = (params.length >=1 && (params[0] instanceof String)) ? (String)params[0] : widg.sCmd;  //sCmd for buttons
        File fileCurrdir = new File(".").getAbsoluteFile();
        String sDir = System.getProperty("pwd");
        String path2 = fileCurrdir.getAbsolutePath() + "/" + path;
        GralMng mng = widgi.gralMng();
        GralInfoBox windhelp= mng.infoHelp;
        windhelp.activate();
        windhelp.setUrl(path2);
        //GralPanelContent panelWind = mng.getPanel("help");
        //if(panelWind !=null) { panelWind.setFocus(); }
        //else { System.err.println("action_openWindow: window not found: help"); }
      }
      return true;
    }
  };

  
  
  
  
  
  
  
  
  public void registerShowMethods(GralMngBuild_ifc mng){
    mng.registerUserAction("showBackColor", showBackColor);
    mng.registerUserAction("syncVariableOnFocus", this.syncVariableOnFocus);
    mng.registerUserAction("setBar", this.setBar);
    mng.registerUserAction("openWindow", action_openWindow);
    mng.registerUserAction("openHelp", action_openHelp);

  }
  
  
}
