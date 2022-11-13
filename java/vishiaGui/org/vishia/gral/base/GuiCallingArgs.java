package org.vishia.gral.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.ifc.GralFactory;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.util.Arguments;

/**The standard command-line-arguments for a graphic application are stored in an extra class. 
 * This class should be the base class for users command line argument storage.
 * In Generally the separation of command line argument helps to invoke the functionality with different calls, 
 * for example calling in a GUI, calling in a command-line-batch-process or calling from ANT.
 * This class should be the super class of an derived application's CallingArguments class. 
 */
public class GuiCallingArgs extends Arguments
{
  /**Version, history and licence
   * <ul>
   * <li>2015-05-16 Hartmut new "-help:" for help base dir in argument check, "-msgcfg:" in argument check. 
   *   It is moved from Fcmd to this base class of Fcmd, available generally.  
   * <li>2013-11-22 Hartmut new {@link #sTitle}
   * <li>2012-04-22 Hartmut chg {@link #sizeShow} instead sSize, new {@link #xLeftPixelWindow} etc.
   * <li>2011-06-00 Hartmut creation, commonly arguments for each GUI application.
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
   * <li> But the LPGL ist not appropriate for a whole software product,
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
  public final static int version = 20131122;

  
  
  /**String for title bar. */
  String sTitle;
  
  /**The graphic base factory can be detected from command line arguments
   * or set directly from the calling level. */
  public GralFactory graphicFactory;
  
  /**Name of the config-file for the Gui-appearance. */
  //String sFileGui;
  
  /**The configuration file. It is created while parsing arguments.
   * The file is opened and closed while the configuration is used to build the GUI.
   * The file is used to write on menu-save action.
   */
  public File fileGuiCfg;
  
  public String sPathZbnf = "GUI";
  
  /**The time zone to present all time informations. */
  public String sTimeZone = "GMT";
  
  /**Size, either A,B or F for 800x600, 1024x768 or full screen. */
  public char sizeShow = 'C';
  
  /**The position in grid units related to the whole display. */
  public String positionWindow = "0+60, 0+90";
  //public int xLeftPixelWindow = 50, yTopPixelWindow = 50, dxPixelWindow = 930, dyPixelWindow = 520;
  
  /**The own ipc-address for Interprocess-Communication with the target.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  public String sOwnIpcAddr;
  
  public List<String> cfgConditions = new ArrayList<String>();
  
  
  
  /**A class which is used as plugin for user specifies. It is of interface {@link PlugUser_ifc}. */
  String sPluginClass;
  
  /**A plugin specific confic string. */
  public String sPluginCfg;
  

  
  /**The own ipc-address for inspector-Communication with this application.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  String sInspectorOwnPort;

  public File dirHtmlHelp;
  
  public File msgConfig;

  
  public final Argument sFileLogCfg = new Argument("-logcfg", ":path/to/logfile_for_configProcess.txt  - will be closed after startup.");
  
  /**Returns that directory where the configuration file is found. That directory may contain some more files
   * for the application. */
  public File getDirCfg(){ return fileGuiCfg.getAbsoluteFile().getParentFile(); }
  
  
  
  
  /**Tests one argument. This method is invoked from parseArgument. 
   * It overrides the  {@link Arguments#testArgument(String, int)}
   * for specific tests here. The superclass testArgument(...) is called if no argument matches here.
   * This operation is called from {@link Arguments#parseArgs(String[], Appendable)}
   * which is called from the user level.
   * @param arg String of the actual parsed argument from cmd line
   * @param nArg number of the argument in order of the command line, the first argument is number 1.
   * @return true is okay,
   *          false if the argument doesn't match. The parseArgument method in MainCmd throws an exception,
   *          the application should be aborted.
   */
  @Override protected boolean testArgument(String arg, int nArg) { 
    boolean bOk = true;  //set to false if the argc is not passed
    GuiCallingArgs cargs = this;
    String value;
    if( (value = checkArgVal("-title", arg)) !=null) {       
      cargs.sTitle = value;  //the graphic GUI-appearance
    }
    else if((value = checkArgVal("-gui", arg)) !=null)      
    { cargs.fileGuiCfg = new File(value);  //the graphic GUI-appearance
    
    }
    else if((value = checkArgVal("-cfg", arg)) !=null)      
    { String sCfg = value;  //the graphic GUI-appearance
      if(cargs.cfgConditions == null){
        cargs.cfgConditions = new ArrayList<String>();
      }
      cargs.cfgConditions.add(sCfg);
    }
    else if((value = checkArgVal("-ownIpc", arg)) !=null) 
    { cargs.sOwnIpcAddr = value;
    }
    else if((value = checkArgVal("-inspectorPort", arg)) !=null) 
    { cargs.sInspectorOwnPort = value;   //an example for default output
    }
    else if((value = checkArgVal("-timeZone", arg)) !=null) 
    { cargs.sTimeZone = value;   //an example for default output
    }
    else if((value = checkArgVal("-size", arg)) !=null) 
    { String sValue = value;
      if(sValue.length() >=1){
        cargs.sizeShow = sValue.charAt(0);   
      } else {
        bOk = false;
      }
    }
    else if((value = checkArgVal("-pos", arg)) !=null) 
    { cargs.positionWindow = value;
    }
    else if(checkArg("-fullscreen", arg)) 
    { cargs.positionWindow = "0..0,0..0";
    }
    else if ((value = checkArgVal("-help", arg)) !=null) {
      File file1 = new File(value);
      String sPathHelpAbs = file1.getAbsolutePath();
      cargs.dirHtmlHelp = new File(sPathHelpAbs);  //should be absolute because browser.
    } else if ((value = checkArgVal("-msgcfg", arg)) !=null) {
      cargs.msgConfig = new File(value);
    }
    else if((value = checkArgVal("-syntax", arg)) !=null) 
    { cargs.sPathZbnf = value;   //an example for default output
    }
    else if((value = checkArgVal("-plugin", arg)) !=null) 
    { cargs.sPluginClass = value;   //an example for default output
    }
    
    else if(checkArg("-SWT", arg)) 
    { cargs.graphicFactory = new SwtFactory();   //an example for default output
    }
    
    else if(checkArg("-AWT", arg)) 
    { cargs.graphicFactory = new AwtFactory();   //an example for default output
    }
    
    else 
    { super.testArgument(arg, nArg);             // uses the argList of all other arguments
    }
    return bOk;
  }




  @Override
  public boolean testArgs(Appendable msg) throws IOException {
    return true;
  }




  public GuiCallingArgs() {
    super();
    super.aboutInfo = "Configurable Gui, made by Hartmut Schorrig, 2010, 2022-09-23";
    super.helpInfo = "see https://www.vishia.org/gral/index.html";
    super.addArg(this.sFileLogCfg);
  }

  
  
  
  
  
}
