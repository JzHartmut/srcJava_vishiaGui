package org.vishia.gral.cfg;

import java.io.File;
import java.text.ParseException;

import org.vishia.gral.base.GralGraphicTimeOrder;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.LogMessageStream;

/*Test with Jbat: call Jbat with this java file with its full path:
file: D:/vishia/Java/srcJava_vishiaGui/org/vishia/gral/cfg/GralCfgWindow.java
==JZcmd==
java org.vishia.gral.test.HelloWorld.openWindow();                 
String windowTitle = <:>Test GralCfgWindow<.>;
String gConfig = 
<:>
 ##A value to show, following by a label.
 @4+2,2+30:Show("name"); Text("label");
 @6..-2, 2..0: InputBox(box);
<.>;
Obj window = java org.vishia.gral.cfg.GralCfgWindow.createWindow("testCfgWind", windowTitle, gConfig, jzcmdsub.currdir, jzcmd.log);
window.setTextIn("name", "example");
window.setTextIn("box", "Box input");                 
java org.vishia.gral.test.HelloWorld.waitForClosePrimaryWindow();
==endJZcmd==
*/

/**This is the class to build a graphic window in any application which is configurable.
 * <br><br>
 * Note: See {@link org.vishia.gral.area9.GuiCfg}, that is an application with 9 areas for configuration. 
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralCfgWindow_Old
{
  
  /**The version, history and license.
   * <ul>
   * <li>2018-09-17 {@link #GralCfgWindow(String, String, char, CharSequence, File, MainCmdLogging_ifc)}
   *   with sPosName, also possible give the position, uses this capability from {@link GralWindow#GralWindow(String, String, String, int)} 
   * <li>2018-09-17 GralCfgWindow: with argument for size and AWT/SWT
   * <li>2015-04-26 Hartmut created: For usage in Jzcmd-scripts.
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
  public final static String version = "2022-01-29";

  
  /**The configuration data for graphical appearance. */
  final private GralCfgData guiCfgData;

  /**Directory for images. */
  File imgDir;
  
  protected final GralMng gralMng = new GralMng(new LogMessageStream(System.out));
  
  final public GralWindow window;
  
  /**Creates a new Window or opens the existing one with given name.
   * @param sPosName Position and Name of the window inside the gral manager to address the window.
   *   Form: "@LINE+SIZE, COLUMN+SIZE=NAME". If pos is not given, not starts with "@", then a default Pos  "10+50, 10+100" is used.   
   * @param sTitle text in the title bar
   * @param size 'A'...'E' as pixel/grid unit for SWT graphic. 'a'...'e' same for AWT-Graphic
   * @param sCfg textual given configuration for the window. 
   *   The syntax is contained in org.vishia.gral.cfg.Syntax.zbnf inside the jar file.
   * @param imgDir start directory path where images are located if given with relative path.
   * @param log interface for logging output of parser and creation.
   * @throws ParseException on errors in the sCfg
   */
  @Deprecated private GralCfgWindow_Old(String sPosName, String sTitle, char size, CharSequence sCfg, File imgDir, MainCmdLogging_ifc log) throws ParseException {
//    this.log = log !=null ? log : log;
    this.guiCfgData = new GralCfgData(null);  //no config conditions given.
    this.imgDir = imgDir;
    String swtOrawt;
    char size1;
    if(size < 'a') { size1 = size; swtOrawt = "SWT"; }
    else { size1 = (char)(size - 'a'-'A'); swtOrawt = "AWT"; }
    GralCfgZbnf cfgZbnf = new GralCfgZbnf(this.gralMng);  //temporary instance for parsing
    cfgZbnf.configureWithZbnf(sCfg, this.guiCfgData); //
    int props = GralWindow_ifc.windRemoveOnClose | GralWindow_ifc.windConcurrently | GralWindow_ifc.windResizeable;
    this.gralMng.selectPrimaryWindow();
    if( !sPosName.startsWith("@")) { sPosName = "@10+50, 10+100 =" + sPosName; } 
    this.window = gralMng.addWindow(sPosName, sTitle, props);
    this.configInGthread.getCtDone(0);
    this.gralMng.createGraphic(swtOrawt, size1, log); //, this.configInGthread);
    this.configInGthread.awaitExecution(1, 0);
  }
  
  /**Creates a window with a given configuration.
   * The window will be removed on closing.
   * @param sName Name of the window inside the gral manager to address the window.
   * @param sTitle text in the title bar
   * @param size 'A'...'E' as pixel/grid unit for SWT graphic. 'a'...'e' same for AWT-Graphic
   * @param sCfg textual given configuration for the window.
   * @param imgDir start directory path where images are located if given with relative path.
   * @param log log output for status and parse messages
   * @throws ParseException on errors in the sCfg
   */
  @Deprecated public static GralWindow createWindow(String sName, String sTitle, char size, CharSequence sCfg, File imgDir, MainCmdLogging_ifc log) 
  throws ParseException
  {
    GralCfgWindow_Old thiz = new GralCfgWindow_Old(sName, sTitle, size, sCfg, imgDir, log);
    return thiz.window;
  }
  
  
  /**Accesses a given widget which is given per name. 
   * @param name from the config file
   * @return null if name is faulty.
   */
  public GralWidget getWidget(String name) {
    return this.window.mainPanel.getWidget(name);
  }
  

  
  /**Code snippet to run the ZBNF-configurator (text controlled GUI)
   * 
   */
  @SuppressWarnings("synthetic-access")  
  GralGraphicTimeOrder configInGthread = new GralGraphicTimeOrder("GralCfgWindow.config", this.gralMng)
  {
    
    private static final long serialVersionUID = 1L;

    @Override public void executeOrder(){
      GralMng mng = gralMng;
      mng.selectPanel("primaryWindow");  //window position relative to the primary window.
      GralCfgWindow_Old.this.window.setToPanel(mng);
      GralCfgWindow_Old.this.window.setVisible(true);
      GralCfgBuilder cfgBuilder = new GralCfgBuilder(GralCfgWindow_Old.this.guiCfgData, mng, GralCfgWindow_Old.this.imgDir);
      String sError = cfgBuilder.buildGui(GralCfgWindow_Old.this.gralMng.log, 0);
      if(sError !=null) {
        System.err.println(sError);
      }
    }
  ////
  };


  
  
}
