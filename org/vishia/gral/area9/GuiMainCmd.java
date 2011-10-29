package org.vishia.gral.area9;

import java.io.File;

import org.vishia.gral.base.GralGridProperties;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.swt.FactorySwt;
import org.vishia.mainCmd.MainCmd;
import org.vishia.msgDispatch.LogMessage;

/**This class is an extension of the {@link MainCmd} for graphic applications.
 * @author Hartmut Schorrig
 *
 */
public class GuiMainCmd extends MainCmd
{
  
  public GuiMainAreaifc gui;
  
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
  public GuiMainCmd(GuiCallingArgs cargs, String[] args)
  { 
    super(args);
    super.addAboutInfo("made by HSchorrig, 2010-06-07, 2011-08-07");
    this.cargs = cargs;
  }

  
  public boolean parseArgumentsAndInitGraphic(String sTitle, String sOutputArea)
  {
    boolean bOk = true;
    try{ parseArguments(); }
    catch(Exception exception)
    { sArgError = "Cmdline argument error:" + exception.getLocalizedMessage();
      setExitErrorLevel(exitWithArgumentError);
      bOk = false;
    }
    cargs.graphicFactory = new FactorySwt(); 
    if(sOutputArea == null){ sOutputArea = "A3C3"; }
    
    gui = cargs.graphicFactory.createGuiWindow((MainCmd)this, sTitle, 50,50,800, 600, sOutputArea);
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
  { gui.exit();
  }
  
}
