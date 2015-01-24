package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import org.vishia.event.EventCmdType;
import org.vishia.event.EventCmdPingPongType;
import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteAccessor;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralLed;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class FcmdFileProps
{
  /**Version, history and license
   * <ul>
   * <li>2012-03-10 Hartmut improved: Now works tested in windows
   * <li>2012-03-09 Hartmut created, but not used yet
   * </ul>
   * 
   * 
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
   * 
   * 
   */
  public static final int version = 20120309;

  
  protected final Fcmd main;

  GralWindow_ifc windFileProps;
  GralTextField_ifc widgName, widgDir, widgLink, widgDate, widgLength;
  
  GralButton[] widgRd, widgWr, widgEx;
  GralButton widgUID, widgGID, widgSticky;
  GralButton widgHidden, widgDirectory;
  GralButton widgBtnDirBytes;
  
  /**Action button. */
  GralButton widGetAllProps, widgChrRecurs, widgChgFile, widgCopyFile;
  
  
  DateFormat formatDate;
  
  final String sWrAble = "wr / ?rd", sRdOnly = "rd / ?wr";
  
  final String sHidden = "hidden / ?", sNonHidden = "non hidden / ?";
  
  final String sSubdir = "recursive / ?", sNonSubdir = "non recurs/ ?";
  
  static final String sCmdChg = "change", sCmdCopy = "copy", sCmdChgRecurs = "chgRecurs"
    , sCmdQuit = "quit", sCmdAbort = "abort";
  
  /**True if it is a unix system. It checks the "OS" environment variable. */
  boolean bUnixSystem;
  
  /**True then the window is opened. Write acutal file into. */
  boolean isVisible;
  
  /**True while a change commission is send and no answer is received yet. */
  //boolean busyChanging;
  
  FileRemote actFile;
  
  /**
   * 
   */
  final FileRemote.CallbackEvent evChg;
  
  final FileRemote.CallbackEvent evCntLen;
 
  
  
  public FcmdFileProps(Fcmd main)
  { this.main = main;
    this.formatDate = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
    evChg = new FileRemote.CallbackEvent(evSrc, null, null, callbackChgProps, null, evSrc);
    evCntLen = new FileRemote.CallbackEvent(evSrc, null, null, callbackCntLen, null, evSrc);
  }
  
  
  /**Builds the content of the file property window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindow()
  { String sUnix = System.getenv("OS");
    if(sUnix !=null){
      sUnix = sUnix.toUpperCase();
      if(sUnix.contains("WINDOWS")){
        bUnixSystem = false;
      } else {
        bUnixSystem = true;
      }
    } else {
      bUnixSystem = System.getenv("WINDIR") == null;
    }
    main._gralMng.selectPanel("primaryWindow");
    main._gralMng.setPosition(-30, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main._gralMng.createWindow("windProp", "file properties - The.file.Commander", windProps);
    windFileProps = window; 
    main._gralMng.setPosition(3.5f, GralPos.size -3, 1, -1, 0, 'd');
    widgLink = main._gralMng.addTextField("link", false, "symbolic link", "t");
    widgDir = main._gralMng.addTextField("dir", false, "directory path", "t");
    main._gralMng.setPosition(10, GralPos.size -4, 1, -1, 0, 'd');
    widgName = main._gralMng.addTextField("name", true, "filename", "t");
    main._gralMng.setPosition(14, GralPos.size -3.5f, 1, 24, 0, 'r');
    widgLength = main._gralMng.addTextField("length", false, "file-length", "t");
    main._gralMng.setPosition(14, GralPos.size -3.5f, 25, -1, 0, 'r');
    widgDate = main._gralMng.addTextField("data", true, "last modified", "t");
    GralColor colorBack = main._gralMng.propertiesGui.colorBackground_;
    GralColor colorText = GralColor.getColor("bk");
    int ii;
    GralColor colorOn = GralColor.getColor("lgn");
    GralColor colorOff = GralColor.getColor("wh");
    GralColor colorDis = GralColor.getColor("gr");
    String textOn = "X", textOff = " ", textDis = "?";
    
    main._gralMng.setPosition(17, GralPos.size -2, 1, 20, 0, 'r', 0.2f);
    widgBtnDirBytes = main._gralMng.addButton("dirBytes", actionBtnCntLen, "dirBytes", null, main.idents.buttonFilePropsCntLen);
    widgBtnDirBytes.setDisableColorText(colorDis, " (file) ");
    //widgDirectory = main.gralMng.addCheckButton("FileProp:btndir", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    //main.gralMng.addText("directory"); 
    main._gralMng.setPosition(17, GralPos.size -2, 22, GralPos.size +2, 0, 'r', 0.2f);
    widgHidden = main._gralMng.addCheckButton("FileProp:btnhidden", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    main._gralMng.addText("hidden"); 
    
    //bUnixSystem = true;
    if(bUnixSystem){
      main._gralMng.setPosition(20, GralPos.size -2, 10, 28, 0, 'd');
      widGetAllProps = main._gralMng.addButton("buttonFilePropsGetAll", actionButton, "getAll", null, main.idents.buttonFilePropsGetAll);

      main._gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 0, 'd');
      main._gralMng.addText("rd");
      widgRd = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgRd[ii] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
        //widgReadonly[ii] = main.gralMng.addSwitchButton("FileProp:btnro", "X", " ", GralColor.getColor("wh"), GralColor.getColor("lgn"));
      }
      main._gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 0, 'd');
      main._gralMng.addText("wr"); 
      widgWr = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgWr[ii] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      }
      main._gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 0, 'd');
      main._gralMng.addText("ex");
      widgEx = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgEx[ii] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      }
      main._gralMng.setPosition(22, GralPos.size -2, 10, GralPos.size +6, 0, 'd');
      main._gralMng.addText("owner");
      main._gralMng.addText("group");
      main._gralMng.addText("all");
      main._gralMng.setPosition(22, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgUID = main._gralMng.addCheckButton("FileProp:btnUID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main._gralMng.addText("UID"); 
      main._gralMng.setPosition(24, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgGID = main._gralMng.addCheckButton("FileProp:btnGID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main._gralMng.addText("GID"); 
      main._gralMng.setPosition(26, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgSticky = main._gralMng.addCheckButton("FileProp:btnSticky", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main._gralMng.addText("sticky"); 
    } else {
      main._gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 0, 'd');
      main._gralMng.addText("rd");
      widgRd = new GralButton[1];
      widgRd[0] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main._gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 0, 'd');
      main._gralMng.addText("wr"); 
      widgWr = new GralButton[1];
      widgWr[0] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main._gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 0, 'd');
      main._gralMng.addText("ex");
      widgEx = new GralButton[1];
      widgEx[0] = main._gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      
    }
    main._gralMng.setPosition(-12, GralPos.size +3, -16, -1, 0, 'd',1);
    widgCopyFile =  main._gralMng.addButton("buttonFilePropsCopy", actionButton, sCmdCopy, null,  main.idents.buttonFilePropsCopy);
    widgChrRecurs =   main._gralMng.addButton("buttonFilePropsChgRecursive", actionButton, sCmdChgRecurs, null,  main.idents.buttonFilePropsChgRecurisve);
    widgChgFile = main._gralMng.addButton("buttonFilePropsChg", actionButton, sCmdChg, null,  main.idents.buttonFilePropsChg);
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(FileRemote src)
  { //String sSrc, sTrash;
    isVisible = true;
    showFileInfos(src);
    windFileProps.setWindowVisible(true);

  }
  
  
  void showFileInfos(FileRemote src){
    if(isVisible && !evChg.isOccupied()){
      actFile = src;
      //TODO don't access the file system without user activity!!! need: a refresh button!!
      widgChgFile.setText(main.idents.buttonFilePropsChg);
      widgChrRecurs.setText(main.idents.buttonFilePropsChgRecurisve);
      widgCopyFile.setText(main.idents.buttonFilePropsCopy);
      widgName.setText(src.getName());
      widgDir.setText(src.getParent());
      String sDate = formatDate.format(new Date(src.lastModified()));
      widgDate.setText(sDate);
      String sLength;
      long length = src.length();
      sLength = "" + length;
      if(length >= 10000 && length < 10000000){
        sLength += " = " + length/1000 + "k";
      } else if( length >= 10000000){
        sLength += " = " + length/1000000 + "M";
      }
      widgLength.setText(sLength);
      if(src instanceof FileRemote && (src).isSymbolicLink()){
        widgLink.setText(FileSystem.getCanonicalPath(src));
      } else {
        widgLink.setText("");
      }
      widgRd[0].setState(src.canRead() ? GralButton.State.On : GralButton.State.Off);
      widgEx[0].setState(src.canExecute() ? GralButton.State.On : GralButton.State.Off);
      widgWr[0].setState(src.canWrite() ? GralButton.State.On : GralButton.State.Off);
      if(bUnixSystem){
        widgRd[1].setState(GralButton.State.Disabled);
        widgRd[2].setState(GralButton.State.Disabled);
        widgWr[1].setState(GralButton.State.Disabled);
        widgWr[2].setState(GralButton.State.Disabled);
        widgEx[1].setState(GralButton.State.Disabled);
        widgEx[2].setState(GralButton.State.Disabled);
        widgSticky.setState(GralButton.State.Disabled);
        widgUID.setState(GralButton.State.Disabled);
        widgGID.setState(GralButton.State.Disabled);
      }
      widgHidden.setState(src.isHidden() ? GralButton.State.On : GralButton.State.Off);
      widgBtnDirBytes.setState(src.isDirectory() ? GralButton.State.On : GralButton.State.Disabled);
      //widgDirectory.setState(src.isDirectory() ? GralButton.State.On : GralButton.State.Off);
    }
  }
  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionOpenDialog = new GralUserAction("actionOpenDialog")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        openDialog(main.currentFile());
      }
      return true;
    }
  };


  EventSource evSrc = new EventSource("FcmdFileProps"){
    @Override public void notifyDequeued(){
      
    }
    @Override public void notifyConsumed(int ctConsumed){}
    @Override public void notifyRelinquished(int ctConsumed){}
  };



  /**Action for Key F2 for view command. 
   */
  GralUserAction actionButton = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){  //not on mouse down but on mouse up.
        String name = widgName.getText();
        if(name.equals(actFile.getName())){ name = null; } //don't change it.
        int noMask = 0;
        int val = 0; //actFileRemote.getFlags();
        int mask;
        if(bUnixSystem){
          mask = FileRemote.mCanRead | FileRemote.mCanWrite | FileRemote.mExecute;
        } else {
          mask = FileRemote.mCanWrite | FileRemote.mHidden;
        }
        switch(widgRd[0].getState()){
          case Off:       val &= ~FileRemote.mCanRead; break;
          case On:       val |= FileRemote.mCanRead; break;
          case Disabled: mask &= ~FileRemote.mCanRead; break;
        }
        switch(widgWr[0].getState()){
          case Off:       val &= ~FileRemote.mCanWrite; break;
          case On:       val |= FileRemote.mCanWrite; break;
          case Disabled: mask &= ~FileRemote.mCanWrite; break;
        }
        switch(widgEx[0].getState()){
          case Off:       val &= ~FileRemote.mExecute; break;
          case On:       val |= FileRemote.mExecute; break;
          case Disabled: mask &= ~FileRemote.mExecute; break;
        }
        if(bUnixSystem){
          switch(widgRd[1].getState()){
            case Off:       val &= ~FileRemote.mCanReadGrp; break;
            case On:       val |= FileRemote.mCanReadGrp; break;
            case Disabled: mask &= ~FileRemote.mCanReadGrp; break;
          }
          switch(widgWr[1].getState()){
            case Off:       val &= ~FileRemote.mCanWriteGrp; break;
            case On:       val |= FileRemote.mCanWriteGrp; break;
            case Disabled: mask &= ~FileRemote.mCanWriteGrp; break;
          }
          switch(widgEx[1].getState()){
            case Off:       val &= ~FileRemote.mExecuteGrp; break;
            case On:       val |= FileRemote.mExecuteGrp; break;
            case Disabled: mask &= ~FileRemote.mExecuteGrp; break;
          }
          switch(widgRd[2].getState()){
            case Off:       val &= ~FileRemote.mCanReadAny; break;
            case On:       val |= FileRemote.mCanReadAny; break;
            case Disabled: mask &= ~FileRemote.mCanReadAny; break;
          }
          switch(widgWr[2].getState()){
            case Off:       val &= ~FileRemote.mCanWriteAny; break;
            case On:       val |= FileRemote.mCanWriteAny; break;
            case Disabled: mask &= ~FileRemote.mCanWriteAny; break;
          }
          switch(widgEx[2].getState()){
            case Off:       val &= ~FileRemote.mExecuteAny; break;
            case On:       val |= FileRemote.mExecuteAny; break;
            case Disabled: mask &= ~FileRemote.mExecuteAny; break;
          }
        }
        switch(widgHidden.getState()){
          case Off:       val &= ~FileRemote.mHidden; break;
          case On:        val |=  FileRemote.mHidden; break;
          case Disabled: mask &= ~FileRemote.mHidden; break;
        }
        val &= mask;   //only used bits.
        boolean bAbort = false;
        if(infos.sCmd.equals(sCmdAbort)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            widgChgFile.setText(main.idents.buttonFilePropsChg);
            infos.sCmd = sCmdChg;
          } else {
            System.err.println("chg properties hangs");
          }
        } else if(infos.sCmd.equals(sCmdChg)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            //cmds with callback
            widgChgFile.setText(main.idents.buttonFilePropsChanging);
            actFile.chgProps(name, mask, val, 0, evChg);
          } else { bAbort = true; }
          //
        } else if(infos.sCmd.equals(sCmdChgRecurs)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            //cmds with callback
            widgChrRecurs.setText(main.idents.buttonFilePropsChanging);
            actFile.chgPropsRecursive(mask, val, 0, evChg);
          } else { bAbort = true; }
          //
        } else if(infos.sCmd.equals(sCmdCopy)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            if(!name.equals(actFile.getName())){
              widgCopyFile.setText(main.idents.buttonFilePropsCopying);
              FileRemote fileNew = actFile.getParentFile().child(name);
              actFile.copyTo(fileNew, evChg, FileRemote.modeCopyReadOnlyOverwrite | FileRemote.modeCopyCreateYes | FileRemote.modeCopyExistAll);
            } else {
              widgCopyFile.setText("copy - name?");
            }
          } else { bAbort = true; }
        }
        if(bAbort){
          widgChgFile.setText(main.idents.buttonFilePropsAbort);
          widgChgFile.sCmd = sCmdAbort;
        }
      }
      return true;
      // /
    }
  };



  GralUserAction actionInvisible = new GralUserAction("actionInvisible")
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { isVisible = false;
      return true;
    }
  };


  EventConsumer callbackChgProps = new EventConsumer()
  { @Override public int processEvent(EventObject evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        showFileInfos(actFile);
        widgChgFile.setText(main.idents.buttonFilePropsOk);
      } else {
        widgChgFile.setText(main.idents.buttonFilePropsRetry);
      }
      ev.relinquish();
      return 1;
    } 
  
     @Override public String toString(){ return "FcmdFileProps-callbackChgProps"; }

  };


  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionBtnCntLen = new GralUserAction("actionBtnCntLen")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        widgBtnDirBytes.setText("counting ...");
        if(0 != evCntLen.occupyRecall(100, evSrc, callbackCntLen, null, true)){
          actFile.countAllFileLength(evCntLen);
        }
      }
      return true;
  } };
  
  EventConsumer callbackCntLen = new EventConsumer()
  { @Override public int processEvent(EventObject evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        String sLen = "" + ev.nrofBytesAll;
        widgLength.setText(sLen);
      } else {
        widgLength.setText("error count bytes");
      }
      widgBtnDirBytes.setText(main.idents.buttonFilePropsCntLen);
      ev.relinquish();
      return 1;
    } 
  
    @Override public String toString(){ return "FcmdFileProps - callback cnt length"; }

  };


  
}
