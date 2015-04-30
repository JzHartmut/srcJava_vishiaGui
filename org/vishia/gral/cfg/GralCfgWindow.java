package org.vishia.gral.cfg;

import java.io.File;
import java.text.ParseException;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.mainCmd.MainCmdLogging_ifc;

/**This is the class to build a graphic window in any application which is configurable.
 * It uses an existing instance of the {@link GralMng}
 * <br><br>
 * Note: See {@link org.vishia.gral.area9.GuiCfg}, that is an application with 9 areas for configuration. 
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralCfgWindow
{
  
  /**The version, history and license.
   * <ul>
   * <li>2015-04-26 Hartmut created: For usage in Jbat-scripts.
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
   * 
   */
  public final static int version = 0x20150426;

  
  final MainCmdLogging_ifc log;
  
  /**The configuration data for graphical appearance. */
  final GralCfgData guiCfgData;

  
  GralWindow wind;
  
  GralCfgWindow(MainCmdLogging_ifc log) {
    this.log = log;
    this.wind = new GralWindow("10+30, 10+50", "GralCfgWindow", "---GralCfgWindow---", GralWindow_ifc.windRemoveOnClose);
    this.guiCfgData = new GralCfgData(null);  //no config conditions given.
  }
  
  /**Directory for images. */
  File currDir;
  
  /**Creates a window with a given configuration.
   * The window will be removed on closing.
   * @param sCfg textual given configuration for the window.
   * @param imgDir start directory path where images are located if given with relative path.
   * @param log log output for status and parse messages
   * @throws ParseException
   */
  public static void createWindow(CharSequence sCfg, File imgDir, MainCmdLogging_ifc log) 
  throws ParseException
  {
    GralCfgWindow thiz= new GralCfgWindow(log);
    thiz.buildConfig(sCfg, imgDir);
  }
  
  
  
  

  
  
  public void buildConfig(CharSequence sCfg, File currDir) 
  throws ParseException
  { this.currDir = currDir;
    GralCfgZbnf cfgZbnf = new GralCfgZbnf();  //temporary instance for parsing
    cfgZbnf.configureWithZbnf(sCfg, guiCfgData); //
    GralMng.get().gralDevice.addDispatchOrder(configGuiWithZbnf);   //runs in graphic thread
  }
  

  
  /**Code snippet to run the ZBNF-configurator (text controlled GUI)
   * 
   */
  @SuppressWarnings("serial") 
  GralGraphicTimeOrder configGuiWithZbnf = new GralGraphicTimeOrder("GralCfgWindow.config")
  {
    
    @Override public void executeOrder(){
      GralMng mng = GralMng.get();
      mng.selectPanel("primaryWindow");  //window position relative to the primary window.
      wind.setToPanel(mng);
      wind.setVisible(true);
      GralCfgBuilder cfgBuilder = new GralCfgBuilder(guiCfgData, mng, currDir);
      cfgBuilder.buildGui(log, 0);        
    }
  ////
  };


  
  
}
