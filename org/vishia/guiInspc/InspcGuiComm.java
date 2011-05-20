package org.vishia.guiInspc;

import java.util.LinkedList;
import java.util.List;

import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InspcDataExchangeAccess.Info;
import org.vishia.inspectorAccessor.InspcAccessEvaluatorRxTelg;
import org.vishia.inspectorAccessor.InspcAccessExecAnswerTelg_ifc;
import org.vishia.inspectorAccessor.InspcAccessExecRxOrder_ifc;
import org.vishia.inspectorAccessor.InspcAccessor;
import org.vishia.mainCmd.Report;
import org.vishia.mainGui.WidgetDescriptor;

/**The communication manager. */
public class InspcGuiComm
{
  
  /**This class joins the GUI-Widget with the inspector communication info block.
   * It is created for any Widget one time if need and used for the communication after that. 
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
        float value = InspcAccessEvaluatorRxTelg.valueFloatFromRxValue(info);
        sShow = Float.toString(value);
      } else {
        sShow = "??" + cmd;
      }
      widgd.setValue(org.vishia.mainGui.GuiPanelMngWorkingIfc.cmdSet, 0, sShow);
    }
  }
 
  /**To Output log informations. The ouput will be done in the output area of the graphic. */
  private final Report console;

  /**Instance for the inspector access to the target. */
  private final InspcAccessor inspcAccessor;
  
  /**Instance to evaluate received telegrams. TODO should a part of InspcAccessor? */
  InspcAccessEvaluatorRxTelg inspcRxEval = new InspcAccessEvaluatorRxTelg();
  
  /**List of all Panels, which have values to show repeating.
   * Any Panel can be an independent window. Any panel may have other values to show.
   * But any panel can select more as one tabs (tabPanel). Then it will be select which values to show.
   */
  private final List<InspcGuiPanelContent> listPanels = new LinkedList<InspcGuiPanelContent>();
  
  
  
  InspcGuiComm(Report console)
  {
    this.console = console;
    this.inspcAccessor = new InspcAccessor();
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
    inspcAccessor.setExecuterAnswer(executerAnwer);
  }
  
  
  
  /**Processes the communication.
   * This routine should be invoked in a cycle about 100..500 ms to get actual values.
   * The invoking thread may be any thread of the system. The routine sends a request to the target.
   * It doesn't wait for any answer.
   */
  void procComm()
  {
    String sIpTarget = "UDP:127.0.0.1:60080";
    
    inspcAccessor.setTargetAddr(sIpTarget); 
    for(InspcGuiPanelContent panel: listPanels){
      if(panel.newWidgets !=null){
        if(panel.widgets !=null){
          //remove communication request for actual widgets.
        }
        panel.widgets = panel.newWidgets;
        panel.newWidgets = null;
      }
      if(panel.widgets !=null) for(WidgetDescriptor widget: panel.widgets){
        
        String sDataPath = widget.getDataPath() + ".";
        //sDataPath = "workingThread.data.yCos.";
        //
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
        //save the order to the action. It is taken on receive.
        inspcRxEval.setExpectedOrder(order, commAction);
        
      }
    }
    
    inspcAccessor.send();

  }

  
  
  InspcAccessExecRxOrder_ifc xxxexecuterAnswerInfo = new InspcAccessExecRxOrder_ifc()
  { @Override public void execInspcRxOrder(InspcDataExchangeAccess.Info info)
    {
      int order = info.getOrder();
    }
    
  };
  
  
  
  private InspcAccessExecAnswerTelg_ifc executerAnwer = new InspcAccessExecAnswerTelg_ifc()
  { @Override public void execInspcRxTelg(InspcDataExchangeAccess.Datagram[] telgs)
    { 
      inspcRxEval.evaluate(telgs, null); //executerAnswerInfo);  //executer on any info block.
    }
    
  };
  
}
