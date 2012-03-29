package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.awt.AwtFactory;
import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.swt.FactorySwt;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;
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
  
  /**Version, history and licence
   * <ul>
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
   * If you are indent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public final static int version = 20120330;
  
  public GralArea9_ifc gui;
  
  final MsgPrintStream systemErrAdapter;
  
  final LogMessage log;

  
  /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
   * It is given as ctor-parameter.
   */
  protected final GuiCallingArgs cargs;
  
  public GralWidgetMng gralMng;
  
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
    this.systemErrAdapter = new MsgPrintStream(log);
    System.err.println("GralArea9MainCmd - test message; test");
    System.err.println("GralArea9MainCmd - test message; test2");
    
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
    boolean bOk = true;
    try{ parseArguments(); }
    catch(Exception exception)
    { sArgError = "Cmdline argument error:" + exception.getLocalizedMessage();
      setExitErrorLevel(exitWithArgumentError);
      bOk = false;
    }
    if(cargs.graphicFactory == null){
      cargs.graphicFactory = new FactorySwt();
    }
    if(sOutputArea == null){ sOutputArea = "A3C3"; }
    
    GralWindow primaryWindow = cargs.graphicFactory.createWindow(getLogMessageOutputConsole(), sTitle, 50,50,800, 600);
    gui = new GralArea9Window(this, primaryWindow);
    gui.getGralMng().setApplicationAdapter(gui);
    gui.initGraphic(sOutputArea);
    gralMng = gui.getGralMng();
    if(sArgError !=null){
      writeError(sArgError);
    }
    return bOk;
  }

  
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
      if(arg.startsWith("-gui="))      
      { cargs.fileGuiCfg = new File(getArgument(5));  //the graphic GUI-appearance
      
      }
      else if(arg.startsWith("-ownIpc=")) 
      { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
      }
      else if(arg.startsWith("-timeZone=")) 
      { cargs.sTimeZone = getArgument(10);   //an example for default output
      }
      else if(arg.startsWith("-size=")) 
      { cargs.sSize = getArgument(6);   //an example for default output
      }
      else if(arg.startsWith("-plugin=")) 
      { cargs.sPluginClass = getArgument(8);   //an example for default output
      }
      
      else if(arg.startsWith("-SWT")) 
      { cargs.graphicFactory = new FactorySwt();   //an example for default output
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
  { gui.closeWindow();
  }
  
}
