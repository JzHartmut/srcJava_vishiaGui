package org.vishia.guiInspc;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.communication.InterProcessCommFactorySocket;
import org.vishia.gral.area9.GuiCallingArgs;
import org.vishia.gral.area9.GuiCfg;
import org.vishia.gral.area9.GralArea9MainCmd;
import org.vishia.gral.ifc.GralPlugUser2Gral_ifc;
import org.vishia.gral.ifc.GralPlugUser_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.msgDispatch.LogMessage;

public class InspcGui //extends GuiCfg
{

  /**Version and history
   * <ul>
   * <li>2011-04-20 Don't derive this class from {@link GuiCfg}, instead uses the inner class {@link InspcGuiCfg}.
   *   It is a problem of order of instantiation.
   * <li>2011-04-00 Hartmut creation.
   * </ul>
   */
  public final static int version = 0x20111020;
  
  /**The communication manager. */
  final InspcGuiComm inspcComm;
  
  private final CallingArguments cargs;

  final GuiCfg guiCfg;
  
  InspcCurveView curveA, curveB, curveC;

  InspcGui(CallingArguments cargs, GralArea9MainCmd cmdgui)
  {
    guiCfg = new InspcGuiCfg(cargs, cmdgui, userInspcPlug);
    

    LogMessage log = cmdgui.getLogMessageOutputConsole();
    this.cargs = cargs;  //args in the correct derived type.
    /**
    assert(user instanceof InspcPlugUser_ifc);
    if(user !=null){
      user.init(userInspcPlug, log);
    }
    */
    GralPlugUser_ifc user = guiCfg.getPluggedUser(); 
    assert(user instanceof InspcPlugUser_ifc);
    
    this.inspcComm = new InspcGuiComm(this, guiCfg.gralMng, cargs.indexTargetIpcAddr, (InspcPlugUser_ifc)user);
    curveA = new InspcCurveView(inspcComm, cmdgui.gralMng);
    curveB = new InspcCurveView(inspcComm, cmdgui.gralMng);
    curveC = new InspcCurveView(inspcComm, cmdgui.gralMng);

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
  private static class CmdLineAndGui extends GralArea9MainCmd
  {

    /**Aggregation to given instance for the command-line-argument. The instance can be arranged anywhere else.
     * It is given as ctor-parameter.
     */
    final protected CallingArguments cargs;
    
    
    public CmdLineAndGui(CallingArguments cargs, String[] args)
    {
      super(cargs, args);
      this.cargs = cargs;
      super.addAboutInfo("Inspc-GUI-cfg");
      super.addAboutInfo("made by HSchorrig, 2011-05-18, 2012-01-17");
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
  

/**Overrides the GuiCfg with special initialization and methods.
 * @author Hartmut Schorrig
 *
 */
private class InspcGuiCfg extends GuiCfg
{
  
  InspcGuiCfg(CallingArguments cargs, GralArea9MainCmd cmdgui, GralPlugUser2Gral_ifc plugUser2Gui)
  { super(cargs, cmdgui, null, plugUser2Gui); 
  }
  
  
  /**Initializes the areas for the panels and configure the panels.
   * This routine overrides {@link GuiCfg#initGuiAreas()} and calls its super.
   * Additional some user initialization is done.
   */
  @Override protected void initGuiAreas(String sAreaMainPanel)
  {
    super.initGuiAreas("A1C2");
    curveA.buildGraphic(gui, "curve A");
    curveB.buildGraphic(gui, "curve B");
    curveC.buildGraphic(gui, "curve C");
    
    if(user !=null){
      user.initGui(gralMng);
      user.addGuiMenu(gui);
    }
    gui.addMenuItemGThread("menuHelp", "&Help/&Help", gui.getActionHelp());
    gui.addMenuItemGThread("menuAbout", "&Help/&About", gui.getActionAbout());
    gui.addMenuItemGThread("menuAbout", "&Help/e&Xit", gui.getActionAbout());

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
  
  @Override protected void finishMain()
  {
    super.finishMain();
    try{ inspcComm.inspcAccessor.close(); } catch(IOException exc){}
  }
  
} //class InspcGuiCfg
  
  private UserInspcPlug_ifc userInspcPlug = new UserInspcPlug_ifc()
  {

    @Override public String replacePathPrefix(String path, String[] target)
    {
      // TODO Auto-generated method stub
      String pathRet = guiCfg.guiCfgData.replacePathPrefix(path, target);
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
    bOk = cmdgui.parseArgumentsAndInitGraphic("Inspc-GUI-cfg", "3A3C");

    LogMessage log = cmdgui.getLogMessageOutputConsole();
    
    //String ipcFactory = "org.vishia.communication.InterProcessComm_Socket";
    //try{ ClassLoader.getSystemClassLoader().loadClass(ipcFactory, true);
    //}catch(ClassNotFoundException exc){
    //  System.out.println("class not found: " + "org.vishia.communication.InterProcessComm_Socket");
    //}
    //Loads the named class, and its base class InterProcessCommFactory. 
    //In that kind the calling of factory methods are regarded to socket.
    new InterProcessCommFactorySocket();
    
    InspcGui main = new InspcGui(cargs, cmdgui);

    main.guiCfg.execute();
    
    cmdgui.exit();
  }


  
}
