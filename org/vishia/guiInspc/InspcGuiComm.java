package org.vishia.guiInspc;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InspcDataExchangeAccess.Info;
import org.vishia.inspectorAccessor.InspcAccessEvaluatorRxTelg;
import org.vishia.inspectorAccessor.InspcAccessExecAnswerTelg_ifc;
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.inspectorAccessor.InspcAccessor;
import org.vishia.mainCmd.Report;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

/**The communication manager. */
public class InspcGuiComm
{
  /**Version, able to read as hex yyyymmdd.
   * Changes:
   * <ul>
   * <li>2011-05-17 execInspcRxOrder() int32AngleDegree etc. to present a angle in degrees
   * <li>2011-05-01 Hartmut: Created
   * </ul>
   */
  public final static int version = 0x20110617;

  /**This class joins the GUI-Widget with the inspector communication info block.
   * It is created for any Widget one time if need and used for the communication after that. 
   * The routine {@link #execInspcRxOrder(Info)} is used to show the received values.
   */
  private static class WidgetCommAction implements InspcAccessExecRxOrder_ifc
  {
    final WidgetDescriptor widgd;
    
    WidgetCommAction(WidgetDescriptor widgd)
    { this.widgd = widgd;
    }

    @Override public void execInspcRxOrder(InspcDataExchangeAccess.Info info)
    {
      String sShow;
      int order = info.getOrder();
      int cmd = info.getCmd();
      if(cmd == InspcDataExchangeAccess.Info.kAnswerValue){
        if(widgd.sFormat !=null && widgd.sFormat.equals("int32AngleDegree")){
          int value = InspcAccessEvaluatorRxTelg.valueIntFromRxValue(info);
          float angle = value * (180.0f/2147483648.0f);
          sShow = String.format("%3.2f°", angle);
        } else if(widgd.sFormat !=null && widgd.sFormat.equals("int16AngleDegree")){
            int value = InspcAccessEvaluatorRxTelg.valueIntFromRxValue(info);
            float angle = value * (180.0f/32768.0f);
            sShow = String.format("%3.2f°", angle);
        } else {
          float value = InspcAccessEvaluatorRxTelg.valueFloatFromRxValue(info);
          sShow = Float.toString(value);
        }
      } else {
        sShow = "??" + cmd;
      }
      widgd.setValue(org.vishia.mainGui.GuiPanelMngWorkingIfc.cmdSet, 0, sShow);
    }
  }
 
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  private final Report console;

  private final InspcPlugUser_ifc user;
  
  /**Instance for the inspector access to the target. */
  private final InspcAccessor inspcAccessor;
  
  /**The target ipc-address for Interprocess-Communication with the target.
   * It is a string, which determines the kind of communication.
   * For example "UDP:0.0.0.0:60099" to create a socket port for UDP-communication.
   */
  private final Map<String, String> indexTargetIpcAddr;

  private Map<String, String> indexFaultDevice;

  long millisecTimeoutOrders = 5000;
  
  long timeLastRemoveOrders;
  
  String sIpTarget;
  
  boolean bUserCalled;

  /**true while running {@link #procComm()} if a send order is stored in telegram but send ins't invoked. */
  boolean bSendOrder;
  
  boolean bShouldSend;
  
  /**List of all Panels, which have values to show repeating.
   * Any Panel can be an independent window. Any panel may have other values to show.
   * But any panel can select more as one tabs (tabPanel). Then it will be select which values to show.
   */
  private final List<InspcGuiPanelContent> listPanels = new LinkedList<InspcGuiPanelContent>();
  
  
  
  InspcGuiComm(Report console, Map<String, String> indexTargetIpcAddr, InspcPlugUser_ifc user)
  {
    this.console = console;
    this.user = user;
    this.inspcAccessor = new InspcAccessor(new InspcAccessEvaluatorRxTelg());
    this.indexTargetIpcAddr = indexTargetIpcAddr;
    if(user !=null){
      user.init(inspcAccessor);
    }
  }
  
  
  void addPanel(InspcGuiPanelContent panel)
  {
    listPanels.add(panel);
  }
  
  
  /**Opens the communication port to the target.
   * Doesn't start any communication.
   * @param sOwnAddr The own address, for socket like "UDP:0.0.0.0:60099"
   */
  void openComm(String sOwnAddr)
  {
    inspcAccessor.open(sOwnAddr);
    //inspcAccessor.setExecuterAnswer(executerAnwer);
  }
  
  
  
  /**Processes the communication.
   * This routine should be invoked in a cycle about 100..500 ms to get actual values.
   * The invoking thread may be any thread of the system. The routine sends a request to the target.
   * It doesn't wait for any answer.
   */
  void procComm()
  {
    sIpTarget = null; 
    bUserCalled = false;
    bSendOrder = false;
    //
    //
    for(InspcGuiPanelContent panel: listPanels){
      if(panel.newWidgets !=null){
        if(panel.widgets !=null){
          //remove communication request for actual widgets.
        }
        panel.widgets = panel.newWidgets;
        panel.newWidgets = null;
      }
      if(panel.widgets !=null) for(WidgetDescriptor widget: panel.widgets){
        
        if(widget.whatIs == 'D'){
          int cc = 0;
        }
        //special action to request content from target:  
        UserActionGui actionShow = widget.getActionShow();
        if(actionShow !=null){
          actionShow.userActionGui("tx", widget);
        } else {
          actionShowTextfield.userActionGui("tx", widget);
        }
        if(bShouldSend){
          sendAndAwaitAnswer();
          //repeat the request for the field:
          //TODO: don't use the show action! use datapath with meta info.
          actionShowTextfield.userActionGui("tx", widget);
        }
      }//for widgets in panel
    }
    if(bShouldSend){
      sendAndAwaitAnswer();
    }
    if(user !=null){
      user.isSent(0);
    }
    
    long time = System.currentTimeMillis();
    if(time >= timeLastRemoveOrders + millisecTimeoutOrders){
      timeLastRemoveOrders = time;
      int removedOrders = inspcAccessor.rxEval.checkAndRemoveOldOrders(time - timeLastRemoveOrders);
      if(removedOrders >0){
        console.writeWarning("Communication problem, removed Orders = " + removedOrders);
      }
    }
    
  }


  
  
  void sendAndAwaitAnswer()
  { bSendOrder = false;
    bShouldSend = false;
    inspcAccessor.send();
    InspcDataExchangeAccess.Datagram[] answerTelgs = inspcAccessor.awaitAnswer(2000);
    if(answerTelgs !=null){
      inspcAccessor.rxEval.evaluate(answerTelgs, null); //executerAnswerInfo);  //executer on any info block.
    } else {
      console.writeWarning("no communication");
      
    }
  }

  
  
  /**This action is used to request a telegram from target.
   * 
   */
  UserActionGui actionShowTextfield = new UserActionGui()
  { @Override public void userActionGui(String sIntension, WidgetDescriptor widget, Object... params)
    {
    
      String sDataPath = widget.getDataPath() + ".";
      int posSepDevice = sDataPath.indexOf(':');
      if(posSepDevice >0){
        String sDevice = sDataPath.substring(0, posSepDevice);
        String sIpTargetNew = indexTargetIpcAddr.get(sDevice);
        if(sIpTargetNew == null){
          errorDevice(sDevice);
        } else {
          if(sIpTarget == null){
            sIpTarget = sIpTargetNew;
            inspcAccessor.setTargetAddr(sIpTarget);
          }
        }
        sDataPath = sDataPath.substring(posSepDevice +1);
      }
      //
      if(sIpTarget !=null){
        if(user !=null && !bUserCalled){  //call only one time per procComm()
          user.requData(0);
          bUserCalled = true;
        }
        //check whether the widget has an comm action already. 
        //First time a widgets gets its WidgetCommAction. Then for ever the action is kept.
        WidgetCommAction commAction;
        Object oCommAction;
        if( (oCommAction = widget.getContentInfo()) ==null){
          commAction = new WidgetCommAction(widget);
          widget.setContentInfo(commAction);
        } else {
          commAction = (WidgetCommAction)oCommAction;
        }
        //
        //create the send command to target.
        int order = inspcAccessor.cmdGetValueByPath(sDataPath);    
        if(order !=0){
          //save the order to the action. It is taken on receive.
          inspcAccessor.rxEval.setExpectedOrder(order, commAction);
          bSendOrder = true;
        } else {
          bShouldSend = true;
          //too much orders.
        }
      }
    }
    
  };
  
  
  
  
  
  
  
  void errorDevice(String sDevice){
    if(indexFaultDevice ==null){ indexFaultDevice = new TreeMap<String, String>(); }
    if(indexFaultDevice.get(sDevice) == null){
      //write the error message only one time!
      indexFaultDevice.put(sDevice, sDevice);
      console.writeError("unknown device key: " + sDevice);
    }
  }
  
  
  InspcAccessExecRxOrder_ifc xxxexecuterAnswerInfo = new InspcAccessExecRxOrder_ifc()
  { @Override public void execInspcRxOrder(InspcDataExchangeAccess.Info info)
    {
      int order = info.getOrder();
    }
    
  };
  
  
  
  private InspcAccessExecAnswerTelg_ifc XXXexecuterAnwer = new InspcAccessExecAnswerTelg_ifc()
  { @Override public void execInspcRxTelg(InspcDataExchangeAccess.Datagram[] telgs)
    { 
      inspcAccessor.rxEval.evaluate(telgs, null); //executerAnswerInfo);  //executer on any info block.
    }
    
  };
  
}
