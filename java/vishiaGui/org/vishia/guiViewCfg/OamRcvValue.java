package org.vishia.guiViewCfg;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.byteData.ByteDataAccessSimple;
import org.vishia.communication.Address_InterProcessComm;
import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InterProcessComm;
import org.vishia.communication.InterProcessCommFactory;
import org.vishia.msgDispatch.LogMessage;

/**This class organizes the receiving of data from a automation device. An own thread is created.
 * In its run-routine the thread waits for receiving data via socket or another adequate 
 * interprocess-communication.
 * For evaluating the received data the routine {@link Plug#show(byte[], int, int)} is called.
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
   * <li>2024-03-02 Answer telegram with the {@link InspcDataExchangeAccess.InspcDatagram} head
   *   with back seqnr. Now ping-pong (requ/ackn) works with C program (Simulation). 
   * <li>2024-02-28 make it more universal, now also used in {@link org.vishia.guiInspc.InspcCurveViewApp},
   *   for that the {@link Plug} interface is created here, should be implemented by user.
   *   The {@link OamShowValues} is no more immediately used by this class, now via the {@link Plug} interface.
   *   All Gral usages are removed, hence this class can be part of vishiaRun.jar in future.
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
  public final static String version = "2024-03-02";

  
  
  /**Associated class which shows the values */
  private final Plug plug;
  
  /**The thread. Composition. */
  private final Thread thread;
  
  boolean bRun;
  
  protected final int addr0;
  
  /**This specific interface should be implemented by a called plug in which deals with the received data. 
   */
  public interface Plug {
    
    /**Quest whether it should send any stuff. It is for example implemented as button in a GUI.
     * @return true then send.
     */
    boolean shouldSend();
    
    /**This is called if a telegram is received and prepared. The goal is: show this received data in a GUI.
     * The format of the data depends from the sender and the requests of the implementor, not from this class.
     * But this class checks the telegram for the head and items.
     * @param binData The ethernet telegram
     * @param nrofBytes length
     * @param from first byte to regard.
     */
    void show(byte[] binData, int nrofBytes, int from);

    
  }
  
  
  /**This parameter can be changed on debug suspend to influence output for one received telegram.
   * It can be used if receiving is the problem.
   *
   */
  static class ShowParam {
    /**Default switchted off, used as boolean. */
    int printDotOnReceivedTelegr = 0;
    
    int printTime = 0;
    
    /**ctdot & mask ==0 then write dot, reduction ratio. */
    //int maskWriteDot = 0x40; 
    
    /**Newline after receiving that number of telegrams. Should be proper to maskWriteDot, but changed and hence calculated manual. */
    int newlineOnTelgCt = 200;
    
    long timeLast;
  }
  ShowParam showParam = new ShowParam();
  
  int ctRxNl=0;
  
  int ctCorruptData;
  
  int currCnt = 0;
  
  boolean bIpcOpened;
  
  private final Map<String, String> indexUnknownVariable = new TreeMap<String, String>();
  
  InspcDataExchangeAccess.InspcDatagram datagramRcv = new InspcDataExchangeAccess.InspcDatagram();
  
  InspcDataExchangeAccess.InspcDatagram datagramTx = new InspcDataExchangeAccess.InspcDatagram();
  
  InspcDataExchangeAccess.Inspcitem infoEntity = new InspcDataExchangeAccess.Inspcitem();
  
  ByteDataAccessSimple txInfoAccess = new ByteDataAccessSimple(true);
  
  final LogMessage log;
  
 
  /**The wrapper arroung the socket communication. It can be implemented also via another kind of communication. 
   */
  private final InterProcessComm ipc;

  /**Create firstly an invalid address (port 0), it is filled from sender, if telegrams are received. */
  private final Address_InterProcessComm targetAddr;
  
  /**Stores here the sender of incoming telegrams. */
  private final Address_InterProcessComm senderAddr;
  
  /**If the {@link #targetAddr} was set by receiving without error, it is true.
   * False if an error occurs while receiving (means the socket is faulty).
   */
  boolean bTargetAddrValid;
  
  byte[] recvData = new byte[1500];
  
  byte[] sendData = new byte[1500];
  
  /**
   * @param plug
   * @param log
   * @param sOwnIpcAddr
   * @param sTargetIpcAddr
   * @param addr0 it is either 0 or 2. It is 0 if transmit from a higher language (PC), 
   *   2 if transmitted without socket driver from a embedded platform, because 2 fill-bytes are necessary 
   *   because elsewhere the payload data starts on an address not divide by 4. (32 bit memory alignment necessary.)
   */
  public OamRcvValue ( Plug plug, LogMessage log
      , String sOwnIpcAddr, String sTargetIpcAddr
      , int addr0) {
    this.thread = new Thread(this, "oamRcv");
    this.log = log;
    this.addr0 = addr0;
    this.plug = plug;
    this.senderAddr = InterProcessCommFactory.getInstance().createAddress("UDP:127.0.0.1:0xffff");
    if(sTargetIpcAddr !=null) {
      this.targetAddr = InterProcessCommFactory.getInstance().createAddress(sTargetIpcAddr);
      this.bTargetAddrValid = true;                             // should transmit commands to this Ethernet destination
    } else {
      this.targetAddr = null;                              // targetAddr is only a placeholder
    }
    if(sOwnIpcAddr !=null) {                          // the slot used for transmit and listen 
      this.ipc = InterProcessCommFactory.getInstance().create(sOwnIpcAddr); //It creates and opens the UDP-Port.
      this.ipc.open(null, true); //InterProcessComm.receiverShouldbeBlocking);
      if(this.ipc != null){
        this.bIpcOpened = true;
      }
    } else {
      this.ipc = null;
      System.out.println("no -ip given, not listen to ethernet.");
    }
    this.txInfoAccess.setLittleEndianBig2();
    this.txInfoAccess.assign(this.sendData, 50, 0);
  }

  /**Starts the receiver thread. */
  public void start()
  {
    this.bRun = true;
    this.thread.start();
  }

  
  /**If the application is stopped, the thread have to be stopped firstly. 
   * The socket will be closed here which interrupts receiving.  */
  public void stopThread(){
    this.bRun = false;
    this.ipc.close();
    this.bIpcOpened = false;
  }
  
  
  private void receiveAndExec() {
    int[] result = new int[1];
    long time1 = System.currentTimeMillis();
    this.ipc.receiveData(result, this.recvData, this.senderAddr);
    long time2 = System.currentTimeMillis();
    if(result[0] > 0) {
      this.bTargetAddrValid = true;
      if(this.showParam.printDotOnReceivedTelegr !=0) {
        System.out.append('.');
        if(--this.ctRxNl <0) {
          this.ctRxNl = this.showParam.newlineOnTelgCt;
//          if( (ctRxNl & showParam.maskWriteDot) !=0) {
            System.out.append('\n');
//          }
        }
      }
      int currCnt1 = (((int)this.recvData[24])<<8) | (this.recvData[25] & 0x00ff);
      if(currCnt1 != this.currCnt+1) {
        //System.out.append(" currCnt-Delta: ").append(Integer.toString(currCnt1 - this.currCnt)).append("  ").append(Integer.toHexString(currCnt1)).append('\n');
      }
      this.currCnt = currCnt1;
      try {  //====>>>>>>                        // evaluate the received telegram: 
        evalTelg(this.recvData, result[0]); 
      }
      catch(Exception exc){
        this.ctCorruptData +=1;
        if(showParam.printDotOnReceivedTelegr !=0) {
          System.out.append('x');
        }
      }
    } else {
      this.bTargetAddrValid = false;
      System.out.println("error receive");
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
  }
  
  
  private void evalTelg(byte[] recvData, int nrofBytes) throws ParseException
  { 
    this.datagramRcv.assign(recvData, nrofBytes, this.addr0);          // The head of the datagram should be appropriate the head of an inspector datagram.
    this.datagramRcv.setLittleEndianBig2();
    //this.datagramRcv.setBigEndian(true);                   // it is the general approach.
    int nrofBytesInfoHead = this.infoEntity.getLengthHead(); // the symbolic data starts as one item, from position 0x18
    int catastrophicalCount = 1001;
    if(this.datagramRcv.sufficingBytesForNextChild(this.infoEntity.getLengthHead()) && --catastrophicalCount >=0){
      this.datagramRcv.addChild(this.infoEntity);
      int nrofBytesInfo = this.infoEntity.getLenInfo();
      if(nrofBytesInfo < nrofBytesInfoHead) throw new ParseException("head of info corrupt, nrofBytes", nrofBytesInfo);
      this.infoEntity.setLengthElement(nrofBytesInfo);
      int posBuffer = this.infoEntity.getPositionInBuffer();
      this.plug.show(recvData, nrofBytesInfo, posBuffer);  // data fro show are the item inclusively its head.
    }
    //this.showValues.showRedraw();
    if(catastrophicalCount <0) throw new RuntimeException("unterminated while-loop");
  }
  
  
  public void sendRequest()
  {    
    try{ Thread.sleep(300);} 
    catch (InterruptedException e)
    { //dialogZbnfConfigurator.terminate();
    }
    if(this.bTargetAddrValid && (this.plug.shouldSend() )) {
      long timeAbs = System.currentTimeMillis();
      this.txInfoAccess.setIntVal(2+0, 2, 0x20);   //Position uint16 data[0] for embedded: length item
      this.txInfoAccess.setIntVal(2+2, 2, 0x65);   //Position uint16 data[4] for embedded: cmd
      this.txInfoAccess.setIntVal(2+8, 8, timeAbs);   //Position uint16 data[4] for embedded
      this.ipc.send(this.sendData, 0x40, this.targetAddr);
    }
  }
  
  
  
  /**Sends an answer to the sender address of the incomming telegrams. 
   * @param txData
   */
  public void sendAnswer(byte[] txData, int nrofAnswerBytes, int seqnr, int cmd) {
    if(this.bTargetAddrValid) {
      this.datagramTx.assign(txData, nrofAnswerBytes);
      int entrant = 0;
      int encryption = 0;
      this.datagramTx.setHeadAnswer(entrant, seqnr, encryption);
      this.ipc.send(txData, nrofAnswerBytes, this.senderAddr);
    }
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
