package org.vishia.guiViewCfg;

import java.text.ParseException;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.communication.Address_InterProcessComm;
import org.vishia.communication.InspcDataExchangeAccess;
import org.vishia.communication.InterProcessComm;
import org.vishia.communication.InterProcessCommFactoryAccessor;
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
	
	final Report log;


	private final InterProcessComm ipc;

	private final Address_InterProcessComm targetAddr = InterProcessCommFactoryAccessor.getInstance().createAddress("UDP:localHost:60083");
	
	byte[] recvData = new byte[1500];
	
	byte[] sendData = new byte[1500];
	
	public OamRcvValue(
		OamShowValues showValues
	, Report log
	)
	{
		thread = new Thread(this, "oamRcv");
		this.log = log;
		this.showValues = showValues;
		String ownAddr = "UDP:0.0.0.0:0xeab2";
		
		ipc = InterProcessCommFactoryAccessor.getInstance().create(ownAddr); //It creates and opens the UDP-Port.
		ipc.open(null, true); //InterProcessComm.receiverShouldbeBlocking);
		if(ipc != null){
			bIpcOpened = true;
		}
	}

	/**Starts the receiver thread. */
	public void start()
	{
		bRun = true;
		thread.start();
	}

	
	/**If the application is stopped, the thread have to be stopped firstly. 
	 * The socket will be closed in the receiver-thread, when it is stopped. */
	public void stopThread(){
		bRun = false;
	}
	
	@Override public void run()
	{
		int[] result = new int[1];
		Address_InterProcessComm sender = ipc.createAddress();
		while(bRun){
			ipc.receiveData(result, recvData, sender);
			if(result[0] > 0) {
				try{ evalTelg(recvData, result[0]); }
				catch(ParseException exc){
					ctCorruptData +=1;
				}
			}
		}
		ipc.close();
		bIpcOpened = false;
	}
	
	
	private void evalTelg(byte[] recvData, int nrofBytes) throws ParseException
	{ 
		datagramRcv.assign(recvData, nrofBytes);
		datagramRcv.setBigEndian(true);
		int nrofBytesInfoHead = infoEntity.getLengthHead();
		int catastrophicalCount = 1000;
		while(datagramRcv.sufficingBytesForNextChild(infoEntity.getLengthHead()) && --catastrophicalCount >=0){
			datagramRcv.addChild(infoEntity);
			int nrofBytesInfo = infoEntity.getLenInfo();
			if(nrofBytesInfo < nrofBytesInfoHead) throw new ParseException("head of info corrupt, nrofBytes", nrofBytesInfo);
			infoEntity.setLengthElement(nrofBytesInfo);
			int posBuffer = infoEntity.getPositionInBuffer() + nrofBytesInfoHead;
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

		ipc.send(sendData, 10, targetAddr);
	}
	
	
	
	/**Sheet anchor: close the socket before the object is removed.
	 * @see java.lang.Object#finalize()
	 */
	@Override public void finalize()
	{
		if(bIpcOpened){
			ipc.close();
			bIpcOpened = false;
		}
	}
	
	
}
