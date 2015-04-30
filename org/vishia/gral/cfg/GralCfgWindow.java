package org.vishia.gral.cfg;

import java.io.File;
import java.text.ParseException;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.mainCmd.MainCmdLogging_ifc;

/*Test with jzcmd: call jzcmd with this java file with its full path:
file: D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/cfg/GralCfgWindow.java
==JZcmd==
java org.vishia.gral.test.HelloWorld.openWindow();                 
String windowTitle = <:>Test GralCfgWindow<.>;
String gConfig = 
<:>
 @2+20,2+60:Show("test");
<.>;
java org.vishia.gral.cfg.GralCfgWindow.createWindow("testCfgWind", windowTitle, gConfig, jzcmdsub.currdir, jzcmd.log);                 
java org.vishia.gral.test.HelloWorld.waitForClosePrimaryWindow();
==endJZcmd==
*/

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

  
  final private MainCmdLogging_ifc log;
  
  /**The configuration data for graphical appearance. */
  final private GralCfgData guiCfgData;

  /**Directory for images. */
  File imgDir;
  
  final public GralWindow window;
  
  /**Creates a new Window or opens the existing one with given name.
   * @param sName Name of the window inside the gral manager to address the window.
   * @param sTitle text in the title bar
   * @param sCfg textual given configuration for the window.
   * @param imgDir start directory path where images are located if given with relative path.
   * @param log interface for logging output of parser and creation.
   * @throws ParseException on errors in the sCfg
   */
  GralCfgWindow(String sName, String sTitle, CharSequence sCfg, File imgDir, MainCmdLogging_ifc log) throws ParseException {
    this.log = log;
    this.guiCfgData = new GralCfgData(null);  //no config conditions given.
    this.imgDir = imgDir;
    GralCfgZbnf cfgZbnf = new GralCfgZbnf();  //temporary instance for parsing
    cfgZbnf.configureWithZbnf(sCfg, guiCfgData); //
    int props = GralWindow_ifc.windRemoveOnClose | GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable;
    this.window = new GralWindow("10+30, 10+50", sName, sTitle, props);
    GralMng.get().gralDevice.addDispatchOrder(configGuiWithZbnf);   //runs in graphic thread
  }
  
  /**Creates a window with a given configuration.
   * The window will be removed on closing.
   * @param sName Name of the window inside the gral manager to address the window.
   * @param sTitle text in the title bar
   * @param sCfg textual given configuration for the window.
   * @param imgDir start directory path where images are located if given with relative path.
   * @param log log output for status and parse messages
   * @throws ParseException on errors in the sCfg
   */
  public static void createWindow(String sName, String sTitle, CharSequence sCfg, File imgDir, MainCmdLogging_ifc log) 
  throws ParseException
  {
    new GralCfgWindow(sName, sTitle, sCfg, imgDir, log);
  }
  
  
  
  

  
  /**Code snippet to run the ZBNF-configurator (text controlled GUI)
   * 
   */
  @SuppressWarnings("synthetic-access")  
  GralGraphicTimeOrder configGuiWithZbnf = new GralGraphicTimeOrder("GralCfgWindow.config")
  {
    
    private static final long serialVersionUID = 1L;

    @Override public void executeOrder(){
      GralMng mng = GralMng.get();
      mng.selectPanel("primaryWindow");  //window position relative to the primary window.
      window.setToPanel(mng);
      window.setVisible(true);
      GralCfgBuilder cfgBuilder = new GralCfgBuilder(guiCfgData, mng, imgDir);
      cfgBuilder.buildGui(log, 0);        
    }
  ////
  };


  
  
}