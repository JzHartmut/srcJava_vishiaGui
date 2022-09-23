package org.vishia.guiViewCfg;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.communication.Address_InterProcessComm;
import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InterProcessComm;
import org.vishia.communication.InterProcessCommFactory;
import org.vishia.mainCmd.MainCmdLogging_ifc;
import org.vishia.mainCmd.Report;

/**This class organizes the receiving of data from a automation device. An own thread is created.
 * In its run-routine the thread waits for receiving data via socket or another adequate 
 * interprocess-communication.
 * For evaluating the received data the routine {@link OamShowValues#show(byte[], int, int)} is called.
 * <br><br>
 * A datagram may contain more as one data-set. The datagram is defined with the inspector-datagram-
 * definition, see {@link InspcDataExchangeAccess}.
 * @author Hartmut Schorrig
 *
 */
public class OamRcvValue implements Runnable
{

  /**Version, history and license. The version number is a date written as yyyymmdd as decimal number.
   * Changes:
   * <ul>
   * <li>2022-09-22 for experience check of the timestamp is built in to report lost telegrams. 
   * <li>2022-08 
   * <li>2010-06 created.
   * </ul>
   * <br><br> 
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
   */
  //@SuppressWarnings("hiding")
  public final static String version = "2022-09-23";

  
  
  /**Associated class which shows the values */
  private final OamShowValues showValues;
  
  /**The thread. Composition. */
  private final Thread thread;
  
  boolean bRun;
  
  /**This parameter can be changed on debug suspend to influence output for one received telegram.
   * It can be used if receiving is the problem.
   *
   */
  static class ShowParam {
    /**Default switchted off, used as boolean. */
    int printDotOnReceivedTelegr = 0;
    
    int printTime = 0;
    
    /**ctdot & mask ==0 then write dot, reduction ratio. */
    int maskWriteDot = 0x0f; 
    
    /**Newline after receiving that number of telegrams. Should be proper to maskWriteDot, but changed and hence calculated manual. */
    int newlineOnTelgCt = 16 * 100;
    
    long timeLast;
  }
  ShowParam showParam = new ShowParam();

  int ctCorruptData;
  
  int currCnt = 0;
  
  boolean bIpcOpened;
  
  private final Map<String, String> indexUnknownVariable = new TreeMap<String, String>();
  
  InspcDataExchangeAccess.InspcDatagram datagramRcv = new InspcDataExchangeAccess.InspcDatagram();
  
  InspcDataExchangeAccess.Inspcitem infoEntity = new InspcDataExchangeAccess.Inspcitem();
  
  final MainCmdLogging_ifc log;


  private final InterProcessComm ipc;

  private final Address_InterProcessComm targetAddr = InterProcessCommFactory.getInstance().createAddress("UDP:localHost:60084");
  
  byte[] recvData = new byte[1500];
  
  byte[] sendData = new byte[1500];
  
  public OamRcvValue(
    OamShowValues showValues
  , MainCmdLogging_ifc log
  )
  {
    this.thread = new Thread(this, "oamRcv");
    this.log = log;
    this.showValues = showValues;
    String ownAddr = "UDP:0.0.0.0:0xeab3";
    
    this.ipc = InterProcessCommFactory.getInstance().create(ownAddr); //It creates and opens the UDP-Port.
    this.ipc.open(null, true); //InterProcessComm.receiverShouldbeBlocking);
    if(this.ipc != null){
      this.bIpcOpened = true;
    }
  }

  /**Starts the receiver thread. */
  public void start()
  {
    this.bRun = true;
    this.thread.start();
  }

  
  /**If the application is stopped, the thread have to be stopped firstly. 
   * The socket will be closed in the receiver-thread, when it is stopped. */
  public void stopThread(){
    this.bRun = false;
  }
  
  
  private void receiveAndExec() {
    int[] result = new int[1];
    Address_InterProcessComm sender = this.ipc.createAddress();
    int ctnl=0;
    long time1 = System.currentTimeMillis();
    this.ipc.receiveData(result, this.recvData, sender);
    long time2 = System.currentTimeMillis();
    if(result[0] > 0) {
      if(showParam.printDotOnReceivedTelegr !=0) {
        System.out.append('.');
        if(--ctnl <0) {
          ctnl = showParam.newlineOnTelgCt;
          if( (ctnl & showParam.maskWriteDot) ==0) {
            System.out.append('\n');
          }
        }
      }
      int currCnt1 = (((int)this.recvData[24])<<8) | (this.recvData[25] & 0x00ff);
      if(currCnt1 != this.currCnt+1) {
        System.out.append(" currCnt-Delta: ").append(Integer.toString(currCnt1 - this.currCnt)).append("  ").append(Integer.toHexString(currCnt1)).append('\n');
      }
      this.currCnt = currCnt1;
      try{ evalTelg(this.recvData, result[0]); }
      catch(ParseException exc){
        this.ctCorruptData +=1;
        if(showParam.printDotOnReceivedTelegr !=0) {
          System.out.append('x');
        }
      }
    }
    if(showParam.printTime !=0) {
      long dTimeEval = System.currentTimeMillis() - time2;
      long dTimewait = time2 - time1;
      long dTyimeCycle = time1 - this.showParam.timeLast;
      this.showParam.timeLast = time1;
      System.out.printf("rx %s: %d + %d\n", dTyimeCycle, dTimewait, dTimeEval);
    }
  }
  
  
  
  
  
  
  @Override public void run()
  {
    while(this.bRun){
      receiveAndExec();
    }
    this.ipc.close();
    this.bIpcOpened = false;
  }
  
  
  private void evalTelg(byte[] recvData, int nrofBytes) throws ParseException
  { 
    this.datagramRcv.assign(recvData, nrofBytes);          // The head of the datagram should be appropriate the head of an inspector datagram.
    this.datagramRcv.setBigEndian(true);                   // it is the general approach.
    int nrofBytesInfoHead = this.infoEntity.getLengthHead(); // the symbolic data starts as one item, from position 0x18
    int catastrophicalCount = 1001;
    if(this.datagramRcv.sufficingBytesForNextChild(this.infoEntity.getLengthHead()) && --catastrophicalCount >=0){
      this.datagramRcv.addChild(this.infoEntity);
      int nrofBytesInfo = this.infoEntity.getLenInfo();
      if(nrofBytesInfo < nrofBytesInfoHead) throw new ParseException("head of info corrupt, nrofBytes", nrofBytesInfo);
      this.infoEntity.setLengthElement(nrofBytesInfo);
      int posBuffer = this.infoEntity.getPositionInBuffer() + nrofBytesInfoHead;
      int nrofBytes1 = nrofBytesInfo - nrofBytesInfoHead;
      this.showValues.show(recvData, nrofBytes1, posBuffer);
    }
    this.showValues.showRedraw();
    if(catastrophicalCount <0) throw new RuntimeException("unterminated while-loop");
  }
  
  
  public void sendRequest()
  {    
    try{ Thread.sleep(300);} 
    catch (InterruptedException e)
    { //dialogZbnfConfigurator.terminate();
    }

    this.ipc.send(this.sendData, 10, this.targetAddr);
  }
  
  
  
  /**Sheet anchor: close the socket before the object is removed.
   * @see java.lang.Object#finalize()
   */
  @Override public void finalize()
  {
    if(this.bIpcOpened){
      this.ipc.close();
      this.bIpcOpened = false;
    }
  }
  
  
}
