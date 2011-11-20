package org.vishia.commander.target;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.vishia.util.FileSystem;

public class FcmdtCopyCmd implements Closeable
{
  
  boolean bRun;

  String sSrc, sDst;
  
  String[] param;
  
  Runnable threadRun = new Runnable(){
    @Override public void run(){ threadRun(); }
  };
  
  Thread threadMng = new Thread(threadRun, "CopyCmd");

  FcmdtCopyCmd(){
    threadMng.start();    
  }
  
  void startCopy(int cmd, String sParam){
    String sSrc; String sDst;
    String[] srcDst = sParam.split("\n");
    synchronized(this){
      this.sDst = srcDst[0]; this.sSrc = srcDst[1];
      this.param = srcDst;
      notify();
    }
  }
  

  private boolean doCopy(){
    boolean bOk = true;
    File fileSrc = new File(sSrc);
    if(sDst.indexOf('/') <0){ 
      //dst: only the filename is given, use the source directory.
      File srcDir = fileSrc.getParentFile();
      if(srcDir !=null){
        String srcPath = FileSystem.getCanonicalPath(srcDir);
        sDst = srcPath + "/" + sDst;
      }
    }
    File fileDst = new File(sDst);
    if(fileSrc.exists()){
      if(fileSrc.isDirectory()){
        
      } else { //fileSrc is a file
        if(fileDst.exists()){
          if(!fileDst.canWrite()){
            //confirm overwrite
            bOk = false;
          } else {
            bOk = fileDst.delete();
          }
        }//fileDst exists
        if(bOk){
          File dstDir = fileDst.getParentFile();
          if(!dstDir.exists()){
            try{ FileSystem.mkDirPath(fileDst); }
            catch(FileNotFoundException exc){
              bOk = false;
              //confirm faulty dst path
            }
          }
        }
        //fileSrc is a file
        if(bOk){
          int nrofBytesCopied;
          try{ nrofBytesCopied = FileSystem.copyFile(fileSrc, fileDst); }
          catch(IOException exc){
            bOk = false;
          }
        }
      }
    } else { //fileSrc doesn't exists
      bOk = false;
    }
    return bOk;
  }

  
  void threadRun(){
    bRun = true;
    while(bRun){
      synchronized(this){
        if(param == null){
          try{wait(); } catch(InterruptedException exc){}
        }  
      }
      if(param !=null){
        doCopy();
        param = null;
      }
    }
  }

  
  
  @Override public void close()
  {
    bRun = false;
    synchronized(this){ notify(); }
  }
  
  
}
