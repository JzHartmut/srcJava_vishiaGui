package org.vishia.gral.area9;

import java.io.File;
import java.util.ArrayList;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.swt.SwtFactory;
import org.vishia.inspectorTarget.Inspector;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.msgDispatch.MsgDispatchSystemOutErr;
import org.vishia.msgDispatch.MsgPrintStream;

/**This class is an extension of the {@link MainCmd} for graphic applications.
 * It provides up to 9 areas for panels. With them a basic partitioning of panels can be done.
 * See {@link GralArea9_ifc}. 
 * This instance can used for graphical output without any inheritance in an user application.
 * The user application may inherit from {@link GuiCfg} instead or use GuiCfg with a special plugin
 * and text-given graphic configuration. GuiCfg aggregates this class.
 * Follow the pattern in {@link GuiCfg#main(String[])} to build a users application.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralArea9MainCmd extends MainCmd
{
  
  /**Version, history and license.
   * <ul>
   * <li>2015-05-16 Hartmut new "-help:" for help base dir in argument check, "-msgcfg:" in argument check. 
   *   It is moved from Fcmd to this base class of Fcmd, available generally.  
   * <li>2013-11-22 Hartmut chg: title of main window as argument -title= 
   * <li>2013-01-26 Hartmut chg: The MsgDispatchSystemOutErr.create(file) was invoked in this constructor. Therefore
   *   all Applications redirect the System.out and System.err outputs to the message system. The outputs were completed
   *   with the time stamp and a number, the {@link org.vishia.msgDispatch.MsgDispatcher} was prepared to use. 
   *   This capability is removed from here yet. It should be a part of the application. See {@link GuiCfg#main(String[])}. 
   * <li>2012-07-09 Hartmut new: The {@link Inspector} will be initialized only if the command line argument 
   *   "-inspectorPort=" is given. That parameter in form "UDP:ip:port" is used.
   * <li>2012-04-22 Hartmut new: {@link #parseArgumentsAndInitGraphic(String, String, int, int, int, int)}
   *   for determination of display coordinates in the users application.
   *   Getting size arguments from main-args. 
   * <li>2012-03-30 MsgPrintStream systemErrAdapter used here.
   *   Now System.err.println can be used to generate an dispatch-able message with numeric identifier.
   * </ul>
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
  public final static int version = 20130126;
  
  public GralArea9_ifc gui;
  
  //final MsgPrintStream systemErrAdapter;
  //final MsgDispatchSystemOutErr msgDisp;
  
  
  final LogMessage log;

  /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
   * It is given as ctor-parameter.
   */
  protected final GuiCallingArgs cargs;
  
  public GralMng gralMng;
  
  String sArgError = null;
  
  /**ctor called in static main.
   * @param cargs aggregation to command-line-argument data, it will be filled here.
   * @param args The command-line-calling arguments from static main
   * @param sTitle Title in title line
   * @param sOutputArea area for output, for example "3A3C".
   */
  public GralArea9MainCmd(GuiCallingArgs cargs, String[] args)
  { 
    super(args);
    this.cargs = cargs;
    this.log = getLogMessageErrorConsole();
    //this.msgDisp = MsgDispatchSystemOutErr.create("D:/DATA/msg/log$yyyy-MMM-dd-HH_mm$.log");
    
    
  }

  
  /**Builds the graphic and parses the command line parameters. Possible command line argument errors
   * or help texts are outputted to the window in the output box.
   * @param sTitle Title for window
   * @param sOutputArea Use 1..3 for row and A..C for column in form for example "3A3C".
   *   In this example the output box occupies all 3 columns (A to C) from the 3. (= bottom) row
   *   of the 9 areas.  If null is used, the default selection is "3A3C".
   * @return true if it is successfully.
   */
  public boolean parseArgumentsAndInitGraphic(String sTitle, String sOutputArea)
  {
    return parseArgumentsAndInitGraphic(sTitle, sOutputArea, '.', -1, -1, -1, -1);
  }

  
  /**Builds the graphic and parses the command line parameters. Possible command line argument errors
   * or help texts are outputted to the window in the output box.
   * @param sTitle Title for window as default if -title= is not given as arg
   * @param sOutputArea Use 1..3 for row and A..C for column in form for example "3A3C".
   *   In this example the output box occupies all 3 columns (A to C) from the 3. (= bottom) row
   *   of the 9 areas.  If null is used, the default selection is "3A3C".
   * @return true if it is successfully.
   */
  public boolean parseArgumentsAndInitGraphic(String sTitle, String sOutputArea, char sizeShow, int left, int top, int xSize, int ySize)
  {
    boolean bOk = true;
    try{ parseArguments(); }
    catch(Exception exception)
    { sArgError = "Cmdline argument error:" + exception.getLocalizedMessage();
      setExitErrorLevel(exitWithArgumentError);
      bOk = false;
    }
    GralMng.create(log);
    gralMng = GralMng.get();
    if(cargs.graphicFactory == null){
      cargs.graphicFactory = new SwtFactory();
    }

    if(sOutputArea == null){ sOutputArea = "A3C3"; }
    if("\0 .".indexOf(sizeShow) >=0){ //undefined per parameter, use args  
      sizeShow = cargs.sizeShow; 
    }   
    if(left < 0){  left = cargs.xLeftPixelWindow; }  //undefined per parameter, use args 
    if(top < 0){  top = cargs.yTopPixelWindow; }  //undefined per parameter, use args 
    if(xSize < 0){  xSize = cargs.dxPixelWindow; }  //undefined per parameter, use args 
    if(ySize < 0){  ySize = cargs.dyPixelWindow; }  //undefined per parameter, use args 
    
    String sTitle1 = cargs.sTitle !=null ? cargs.sTitle : sTitle;
    GralWindow primaryWindow = new GralWindow("!", "primaryWindow", sTitle1, GralWindow.windResizeable + GralWindow.windHasMenu);
    cargs.graphicFactory.createWindow(primaryWindow, sizeShow, left, top, xSize, ySize);
    gui = new GralArea9Window(this, primaryWindow);
    gui.getGralMng().setApplicationAdapter(gui);
    if(cargs.dirHtmlHelp !=null) {
      try{
        gui.setHelpBase(cargs.dirHtmlHelp.getAbsolutePath());
      } catch(Exception exc) {
        System.err.println("GralArea9MainCmd - help faulty, " + cargs.dirHtmlHelp.toString());
      }
    }
    gui.initGraphic(sOutputArea);
    if(sArgError !=null){
      writeError(sArgError);
    }
    return bOk;
  }

  //public void setFullScreen(boolean full){ gui.setFullScreen(full); }

  /*---------------------------------------------------------------------------------------------*/
  /** Tests one argument. This method is invoked from parseArgument. It is abstract in the superclass MainCmd
      and must be overwritten from the user.
      :TODO: user, test and evaluate the content of the argument string
      or test the number of the argument and evaluate the content in dependence of the number.

      @param argc String of the actual parsed argument from cmd line
      @param nArg number of the argument in order of the command line, the first argument is number 1.
      @return true is okay,
              false if the argument doesn't match. The parseArgument method in MainCmd throws an exception,
              the application should be aborted.
  */
  @Override protected boolean testArgument(String arg, int nArg)
  { boolean bOk = true;  //set to false if the argc is not passed
    try {
      if(arg.startsWith("-title="))      
      { cargs.sTitle = getArgument(7);  //the graphic GUI-appearance
      }
      else if(arg.startsWith("-gui="))      
      { cargs.fileGuiCfg = new File(getArgument(5));  //the graphic GUI-appearance
      
      }
      else if(arg.startsWith("-cfg="))      
      { String sCfg = getArgument(5);  //the graphic GUI-appearance
        if(cargs.cfgConditions == null){
          cargs.cfgConditions = new ArrayList<String>();
        }
        cargs.cfgConditions.add(sCfg);
      }
      else if(arg.startsWith("-ownIpc=")) 
      { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
      }
      else if(arg.startsWith("-inspectorPort=")) 
      { cargs.sInspectorOwnPort = getArgument(15);   //an example for default output
      }
      else if(arg.startsWith("-timeZone=")) 
      { cargs.sTimeZone = getArgument(10);   //an example for default output
      }
      else if(arg.startsWith("-size=")) 
      { String sValue = getArgument(6);
        if(sValue.length() >=1){
          cargs.sizeShow = sValue.charAt(0);   
        } else {
          bOk = false;
        }
      }
      else if(arg.startsWith("-fullscreen")) 
      { cargs.dxPixelWindow = cargs.dyPixelWindow = -1;
      }
      else if (arg.startsWith("-help:") || arg.startsWith("help=")) {
        File file1 = new File(arg.substring(6));
        String sPathHelpAbs = file1.getAbsolutePath();
        cargs.dirHtmlHelp = new File(sPathHelpAbs);  //should be absolute because browser.
      } else if (arg.startsWith("-msgcfg:") || arg.startsWith("msgcfg=")) {
        cargs.msgConfig = new File(arg.substring(7));
      }
      else if(arg.startsWith("-syntax=")) 
      { cargs.sPathZbnf = getArgument(8);   //an example for default output
      }
      else if(arg.startsWith("-plugin=")) 
      { cargs.sPluginClass = getArgument(8);   //an example for default output
      }
      
      else if(arg.startsWith("-SWT")) 
      { cargs.graphicFactory = new SwtFactory();   //an example for default output
      }
      
      else if(arg.startsWith("-AWT")) 
      { cargs.graphicFactory = new AwtFactory();   //an example for default output
      }
      
      else if(arg.startsWith("-_")) 
      { //accept but ignore it. Commented calling arguments.
      }
      else 
      { bOk=false;
      }
    } catch(Exception exc){
    }
    return bOk;
  }


  /** Invoked from parseArguments if no argument is given. In the default implementation a help info is written
   * and the application is terminated. The user should overwrite this method if the call without comand line arguments
   * is meaningfull.
   *
   */
  @Override protected void callWithoutArguments()
  { //overwrite with empty method - if the calling without arguments
    //having equal rights than the calling with arguments - no special action.
  }

  /*---------------------------------------------------------------------------------------------*/
  /**Checks the cmdline arguments relation together.
     If there is an inconsistents, a message should be written. It may be also a warning.
     :TODO: the user only should determine the specific checks, this is a sample.
     @return true if successfull, false if failed.
  */
  @Override protected boolean checkArguments()
  { boolean bOk = true;
    return bOk;
  
  }

  
  @Override public void exit()
  { gui.mainWindow().closeWindow();
  }
  
}
