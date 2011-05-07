package org.vishia.guiBzr;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainGui.GuiMainAreaifc;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;

public class MainData
{
  
  final MainCmd_ifc mainCmdifc;

  MainAction mainAction;
  
  GuiPanelMngWorkingIfc panelAccess;
  
  GuiMainAreaifc guifc;
  
  /**The current selected software project. */
  DataProject currPrj;
  
  /**The current selected component. */
  DataCmpn currCmpn;
  
  final BzrGetStatus getterStatus;
  
  /**Only one command invocation should be active in one time. */
  final ProcessBuilder cmdMng = new ProcessBuilder("");
  
  private final ConcurrentLinkedQueue<Runnable> ordersBackground =new ConcurrentLinkedQueue<Runnable>();

  /**Data of the currently selected component.
   * 
   */
  DataCmpn selectedCmpn;
  
  
  MainData(MainCmd_ifc mainCmd)
  {
    this.mainCmdifc = mainCmd;
    getterStatus = new BzrGetStatus(mainCmd, this);

  }
  
  
  void addOrderBackground(Runnable order)
  {
    ordersBackground.add(order);
    synchronized(ordersBackground){
      ordersBackground.notify();
    }
  }
  
  
  /**Gets a order. Waits a defined time, then returns also if no order is given.
   * @return
   */
  Runnable awaitOrderBackground(int timeout)
  {
    Runnable order;
    if(ordersBackground.isEmpty()){
      try{ 
        synchronized(ordersBackground)
        { ordersBackground.wait(timeout); }
      } 
      catch (InterruptedException e){}
    }
    if(!ordersBackground.isEmpty()){
      order = ordersBackground.poll();
    } else {
      order = null;
    }
    return order;
  }
  
  
}
