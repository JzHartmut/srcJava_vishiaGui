package org.vishia.commander.target;

import org.vishia.communication.Address_InterProcessComm;
import org.vishia.communication.InterProcessComm;

public class FcmdtComm
{
  InterProcessComm ipc;
  
  Address_InterProcessComm sender = new Address_InterProcessComm()
  {
  };
  
  Thread threadMngComm;
  
  boolean bRun;
  
  Runnable threadRunComm = new Runnable(){
    @Override public void run(){ threadRunComm(); }
  };
  
  
  void threadRunComm(){
    bRun = true;
    while(bRun){
      int[] result = new int[1];
      byte[] data = ipc.receive(result, sender);
      if(result[1]>0){
        //TODO Use the reflection protocol for target access.
      }
    }
  }
}
