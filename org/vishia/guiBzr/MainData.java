package org.vishia.guiBzr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.vishia.cmd.CmdExecuter;
import org.vishia.gral.area9.GralArea9_ifc;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.mainCmd.MainCmd_ifc;

public class MainData
{
  
  final MainCmd_ifc mainCmdifc;

  final CmdExecuter cmdExec = new CmdExecuter();
  
  MainAction mainAction;
  
  GralMng_ifc panelAccess;
  
  GralArea9_ifc guifc;
  
  final DataConfig cfg = new DataConfig();
  
  /**The current selected software project. */
  DataProject currPrj;
  
  /**The current selected component. */
  DataCmpn currCmpn;
  
  final BzrGetStatus getterStatus;
  
  final DateFormat dateFormatShowingFull =      new SimpleDateFormat("yy-MM-dd  HH:mm");
  final DateFormat dateFormatShowingHour =     new SimpleDateFormat("HH:mm");
  
  /**Only one command invocation should be active in one time. */
  //final ProcessBuilder cmdMng = new ProcessBuilder("");
  
  private final ConcurrentLinkedQueue<Runnable> ordersBackground =new ConcurrentLinkedQueue<Runnable>();

  /**Data of the currently selected component.
   * 
   */
  DataCmpn selectedCmpn;
  
  
  GralWindow_ifc infoWindow;
  
  GralTextBox infoBox;
  
  GralTextField infoLine;
  

  
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
  
  
  /**Formats the given date into a String using 'yesterday' and 'today' if possible
   * @param date 
   * @return 
   */
  String formatTimestampYesterday(long date)
  { String ret;
    Date date1 = new Date(date);
    long millisecPerday = 24*3600*1000;
    long dateToday = System.currentTimeMillis() / millisecPerday * millisecPerday;
    if(date >= dateToday && date < dateToday + millisecPerday){
      ret = "   today  " + dateFormatShowingHour.format(date1);
    } else if(date >= dateToday - millisecPerday && date < dateToday){
      ret = "yesterday " + dateFormatShowingHour.format(date1);
    } else {
      ret = dateFormatShowingFull.format(date1);
    }
    return ret;
  }
  
  
}
