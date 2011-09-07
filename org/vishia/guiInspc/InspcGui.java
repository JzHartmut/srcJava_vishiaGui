package org.vishia.guiInspc;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GuiMainCmd;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainGuiSwt.MainCmdSwt;

public class InspcGui extends GuiCfg
{

  /**The communication manager. */
  final InspcGuiComm inspcComm;
  
  private final CallingArguments cargs;



  InspcGui(CallingArguments cargs, GuiMainCmd cmdgui)
  {
    super(cargs, cmdgui);
    this.cargs = cargs;  //args in the correct derived type.
    assert(user instanceof InspcPlugUser_ifc);
    if(user !=null){
      user.init(userInspcPlug, console.getLogMessageOutputConsole());
    }
    this.inspcComm = new InspcGuiComm(console, panelMng, cargs.indexTargetIpcAddr, (InspcPlugUser_ifc)user);
    //inspcComm.addPanel(panelContent);

  }
  
  
  
  private static class CallingArguments extends GuiCallingArgs
  {
    /**The target ipc-address for Interprocess-Communication with the target.
     * It is a string, which determines the kind of communication.
     * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
     */
    Map<String, String> indexTargetIpcAddr = new TreeMap<String, String>();

    /**File with the values from the S7 to show. */
    protected String sFileOamValues;



  }
  
  /**Organisation class for the GUI.
   */
  private static class CmdLineAndGui extends GuiMainCmd
  {

    /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
     * It is given as ctor-parameter.
     */
    final protected CallingArguments cargs;
    
    
    public CmdLineAndGui(CallingArguments cargs, String[] args)
    {
      super(cargs, args, "Inspc-GUI-cfg");
      this.cargs = cargs;
      super.addAboutInfo("Inspc-GUI-cfg");
      super.addAboutInfo("made by HSchorrig, 2011-05-18, 2011-05-19");
    }

    @Override protected boolean testArgument(String arg, int nArg)
    {
      boolean bOk = true;  //set to false if the argc is not passed
      try{
        if(arg.startsWith("-targetIpc=")) 
        { String sArg = getArgument(11);
          int posSep = sArg.indexOf('@');
          if(posSep < 0){
            writeError("argument -targetIpc=KEY@ADDR: The '@' is missed.");
            bOk = false;
          } else {
            String sKey = sArg.substring(0, posSep);
            String sValue = sArg.substring(posSep+1);
            cargs.indexTargetIpcAddr.put(sKey, sValue);
          }
        }
        else if(arg.startsWith("-ownIpc=")) 
        { cargs.sOwnIpcAddr = getArgument(8);   //an example for default output
        }
        else if(arg.startsWith("-oambin=")) 
        { cargs.sFileOamValues = getArgument(8);   //an example for default output
        }
        else { bOk = super.testArgument(arg, nArg); }
      } catch(Exception exc){ bOk = false; }
      return bOk;
    }

  
  } //class CmdLineAndGui 
  

  
  
  /**Initializes the areas for the panels and configure the panels.
   * This routine can be overridden if other areas are need.
   */
  @Override protected void initGuiAreas()
  {
    gui.setFrameAreaBorders(20, 80, 60, 85);
    gui.setStandardMenusGThread(new File("."), actionFile);
      
    gui.addFrameArea(1,1,3,2, mainTabPanel.getGuiComponent()); //dialogPanel);
   
  }

  
  /**The user may contain any other routines which are Inspc-specific. 
   * 
   */
  @Override public void userInit()
  {
    //left empty, userInit is done in its own constructor.    
    
  }


  
  
  @Override protected void initMain()
  {
    inspcComm.openComm(cargs.sOwnIpcAddr);
    //msgReceiver.start();
    //oamRcvUdpValue.start();
    super.initMain();  //starts initializing of graphic. Do it after reading some configurations.

  }
  
  @Override protected void stepMain()
  {
    try{ 
      inspcComm.procComm();  
      //oamRcvUdpValue.sendRequest();
    } catch(Exception exc){
      //tread-Problem: console.writeError("unexpected Exception", exc);
      System.out.println("unexpected Exception: " + exc.getMessage());
      exc.printStackTrace();
    }

  }
  
  
  
  private UserInspcPlug_ifc userInspcPlug = new UserInspcPlug_ifc()
  {

    @Override public String replacePathPrefix(String path, String[] target)
    {
      // TODO Auto-generated method stub
      String pathRet = guiCfgData.replacePathPrefix(path, target);
      if(target[0] !=null){
        String targetIp = inspcComm.translateDeviceToAddrIp(target[0]);
        if(targetIp !=null){ target[0] = targetIp; }  //else let it unchanged.
      }
      return pathRet;
    }
    
  };

  
  
  
  /**The command-line-invocation (primary command-line-call. 
   * @param args Some calling arguments are taken. This is the GUI-configuration especially.   
   */
  public static void main(String[] args)
  { boolean bOk = true;
    CallingArguments cargs = new CallingArguments();
    //Initializes the GUI till a output window to show informations:
    CmdLineAndGui cmdgui = new CmdLineAndGui(cargs, args);  //implements MainCmd, parses calling arguments
    try{ cmdgui.parseArguments(); }
    catch(Exception exception)
    { cmdgui.writeError("Cmdline argument error:", exception);
      cmdgui.setExitErrorLevel(MainCmd_ifc.exitWithArgumentError);
      bOk = false;  //not exiting, show error in GUI
    }
    
    //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
    //try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
    //}catch(ClassNotFoundException exc){
    //  System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
    //}
    //Loads the named class, and its base class InterProcessCommFactory. 
    //In that kind the calling of factory methods are regarded to socket.
    new InterProcessCommFactorySocket();
    
    InspcGui main = new InspcGui(cargs, cmdgui);

    main.execute();
    
    cmdgui.exit();
  }


  
}
