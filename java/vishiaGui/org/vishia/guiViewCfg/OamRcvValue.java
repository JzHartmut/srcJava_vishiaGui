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
  public final static String version = "2022-08-31";

  
  
  /**Associated class which shows the values */
  private final OamShowValues showValues;
  
  /**The thread. Composition. */
  private final Thread thread;
  
  boolean bRun;

  int ctCorruptData;
  
  boolean bIpcOpened;
  
  private final Map<String, String> indexUnknownVariable = new TreeMap<String, String>();
  
  InspcDataExchangeAccess.InspcDatagram datagramRcv = new InspcDataExchangeAccess.InspcDatagram();
  
  InspcDataExchangeAccess.Inspcitem infoEntity = new InspcDataExchangeAccess.Inspcitem();
  
  final MainCmdLogging_ifc log;


  private final InterProcessComm ipc;

  private final Address_InterProcessComm targetAddr = InterProcessCommFactory.getInstance().createAddress("UDP:localHost:60083");
  
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
    String ownAddr = "UDP:0.0.0.0:0xeab2";
    
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
  
  @Override public void run()
  {
    int[] result = new int[1];
    Address_InterProcessComm sender = this.ipc.createAddress();
    while(this.bRun){
      this.ipc.receiveData(result, this.recvData, sender);
      if(result[0] > 0) {
        try{ evalTelg(this.recvData, result[0]); }
        catch(ParseException exc){
          this.ctCorruptData +=1;
        }
      }
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
    while(this.datagramRcv.sufficingBytesForNextChild(this.infoEntity.getLengthHead()) && --catastrophicalCount >=0){
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
