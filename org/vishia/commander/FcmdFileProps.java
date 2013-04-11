package org.vishia.commander;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
import org.vishia.util.EventSource;
import org.vishia.util.FileRemote;
import org.vishia.util.FileRemoteAccessor;
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
  
  File actFile;
  
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
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-30, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main.gralMng.createWindow("windProp", "file properties - The.file.Commander", windProps);
    windFileProps = window; 
    main.gralMng.setPosition(3.5f, GralPos.size -3, 1, -1, 0, 'd');
    widgLink = main.gralMng.addTextField("link", false, "symbolic link", "t");
    widgDir = main.gralMng.addTextField("dir", false, "directory path", "t");
    main.gralMng.setPosition(10, GralPos.size -4, 1, -1, 0, 'd');
    widgName = main.gralMng.addTextField("name", true, "filename", "t");
    main.gralMng.setPosition(14, GralPos.size -3.5f, 1, 24, 0, 'r');
    widgLength = main.gralMng.addTextField("length", false, "file-length", "t");
    main.gralMng.setPosition(14, GralPos.size -3.5f, 25, -1, 0, 'r');
    widgDate = main.gralMng.addTextField("data", true, "last modified", "t");
    GralColor colorBack = main.gralMng.propertiesGui.colorBackground_;
    GralColor colorText = GralColor.getColor("bk");
    int ii;
    GralColor colorOn = GralColor.getColor("gn");
    GralColor colorOff = GralColor.getColor("wh");
    GralColor colorDis = GralColor.getColor("gr");
    String textOn = "X", textOff = " ", textDis = "?";
    
    main.gralMng.setPosition(17, GralPos.size -2, 1, 20, 0, 'r', 0.2f);
    widgBtnDirBytes = main.gralMng.addButton("dirBytes", actionBtnCntLen, "dirBytes", null, main.idents.buttonFilePropsCntLen);
    widgBtnDirBytes.setDisableColorText(colorDis, " (file) ");
    //widgDirectory = main.gralMng.addCheckButton("FileProp:btndir", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    //main.gralMng.addText("directory"); 
    main.gralMng.setPosition(17, GralPos.size -2, 22, GralPos.size +2, 0, 'r', 0.2f);
    widgHidden = main.gralMng.addCheckButton("FileProp:btnhidden", textOn, textOff, textDis, colorOn, colorOff, colorDis);
    main.gralMng.addText("hidden"); 
    
    //bUnixSystem = true;
    if(bUnixSystem){
      main.gralMng.setPosition(20, GralPos.size -2, 10, 28, 0, 'd');
      widGetAllProps = main.gralMng.addButton("buttonFilePropsGetAll", actionButton, "getAll", null, main.idents.buttonFilePropsGetAll);

      main.gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 0, 'd');
      main.gralMng.addText("rd");
      widgRd = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgRd[ii] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
        //widgReadonly[ii] = main.gralMng.addSwitchButton("FileProp:btnro", "X", " ", GralColor.getColor("wh"), GralColor.getColor("gn"));
      }
      main.gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 0, 'd');
      main.gralMng.addText("wr"); 
      widgWr = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgWr[ii] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      }
      main.gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 0, 'd');
      main.gralMng.addText("ex");
      widgEx = new GralButton[3];
      for(ii=0; ii < 3; ++ii){
        widgEx[ii] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      }
      main.gralMng.setPosition(22, GralPos.size -2, 10, GralPos.size +6, 0, 'd');
      main.gralMng.addText("owner");
      main.gralMng.addText("group");
      main.gralMng.addText("all");
      main.gralMng.setPosition(22, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgUID = main.gralMng.addCheckButton("FileProp:btnUID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main.gralMng.addText("UID"); 
      main.gralMng.setPosition(24, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgGID = main.gralMng.addCheckButton("FileProp:btnGID", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main.gralMng.addText("GID"); 
      main.gralMng.setPosition(26, GralPos.size -2, 18, GralPos.size +2, 0, 'r', 0.2f);
      widgSticky = main.gralMng.addCheckButton("FileProp:btnSticky", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main.gralMng.addText("sticky"); 
    } else {
      main.gralMng.setPosition(20, GralPos.size -2, 1, GralPos.size +2, 0, 'd');
      main.gralMng.addText("rd");
      widgRd = new GralButton[1];
      widgRd[0] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main.gralMng.setPosition(20, GralPos.size -2, 4, GralPos.size +2, 0, 'd');
      main.gralMng.addText("wr"); 
      widgWr = new GralButton[1];
      widgWr[0] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      main.gralMng.setPosition(20, GralPos.size -2, 7, GralPos.size +2, 0, 'd');
      main.gralMng.addText("ex");
      widgEx = new GralButton[1];
      widgEx[0] = main.gralMng.addCheckButton("FileProp:btnro", textOn, textOff, textDis, colorOn, colorOff, colorDis);
      
    }
    main.gralMng.setPosition(-12, GralPos.size +3, -16, -1, 0, 'd',1);
    widgCopyFile =  main.gralMng.addButton("buttonFilePropsCopy", actionButton, sCmdCopy, null,  main.idents.buttonFilePropsCopy);
    widgChrRecurs =   main.gralMng.addButton("buttonFilePropsChgRecursive", actionButton, sCmdChgRecurs, null,  main.idents.buttonFilePropsChgRecurisve);
    widgChgFile = main.gralMng.addButton("buttonFilePropsChg", actionButton, sCmdChg, null,  main.idents.buttonFilePropsChg);
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(File src)
  { //String sSrc, sTrash;
    isVisible = true;
    showFileInfos(src);
    windFileProps.setWindowVisible(true);

  }
  
  
  void showFileInfos(File src){
    if(isVisible && !evChg.isOccupied()){
      actFile = src;
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
      if(src instanceof FileRemote && ((FileRemote)src).isSymbolicLink()){
        widgLink.setText(FileSystem.getCanonicalPath(src));
      } else {
        widgLink.setText("");
      }
      widgRd[0].setState(src.canRead() ? GralButton.kOn : GralButton.kOff);
      widgEx[0].setState(src.canExecute() ? GralButton.kOn : GralButton.kOff);
      widgWr[0].setState(src.canWrite() ? GralButton.kOn : GralButton.kOff);
      if(bUnixSystem){
        widgRd[1].setState(GralButton.kDisabled);
        widgRd[2].setState(GralButton.kDisabled);
        widgWr[1].setState(GralButton.kDisabled);
        widgWr[2].setState(GralButton.kDisabled);
        widgEx[1].setState(GralButton.kDisabled);
        widgEx[2].setState(GralButton.kDisabled);
        widgSticky.setState(GralButton.kDisabled);
        widgUID.setState(GralButton.kDisabled);
        widgGID.setState(GralButton.kDisabled);
      }
      widgHidden.setState(src.isHidden() ? GralButton.kOn : GralButton.kOff);
      widgBtnDirBytes.setState(src.isDirectory() ? GralButton.kOn : GralButton.kDisabled);
      //widgDirectory.setState(src.isDirectory() ? GralButton.kOn : GralButton.kOff);
    }
  }
  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionOpenDialog = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        openDialog(main.currentFile);
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
  GralUserAction actionButton = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){  //not on mouse down but on mouse up.
        String name = widgName.getText();
        if(name.equals(actFile.getName())){ name = null; } //don't change it.
        int noMask = 0;
        FileRemote actFileRemote;
        if(actFile instanceof FileRemote){ actFileRemote = (FileRemote)actFile; }
        else { actFileRemote = FileRemote.fromFile(actFile); }
        int val = 0; //actFileRemote.getFlags();
        int mask;
        if(bUnixSystem){
          mask = FileRemote.mCanRead | FileRemote.mCanWrite | FileRemote.mExecute;
        } else {
          mask = FileRemote.mCanWrite | FileRemote.mHidden;
        }
        switch(widgRd[0].getState()){
          case GralButton.kOff:       val &= ~FileRemote.mCanRead; break;
          case GralButton.kOn:       val |= FileRemote.mCanRead; break;
          case GralButton.kDisabled: mask &= ~FileRemote.mCanRead; break;
        }
        switch(widgWr[0].getState()){
          case GralButton.kOff:       val &= ~FileRemote.mCanWrite; break;
          case GralButton.kOn:       val |= FileRemote.mCanWrite; break;
          case GralButton.kDisabled: mask &= ~FileRemote.mCanWrite; break;
        }
        switch(widgEx[0].getState()){
          case GralButton.kOff:       val &= ~FileRemote.mExecute; break;
          case GralButton.kOn:       val |= FileRemote.mExecute; break;
          case GralButton.kDisabled: mask &= ~FileRemote.mExecute; break;
        }
        if(bUnixSystem){
          switch(widgRd[1].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mCanReadGrp; break;
            case GralButton.kOn:       val |= FileRemote.mCanReadGrp; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mCanReadGrp; break;
          }
          switch(widgWr[1].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mCanWriteGrp; break;
            case GralButton.kOn:       val |= FileRemote.mCanWriteGrp; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mCanWriteGrp; break;
          }
          switch(widgEx[1].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mExecuteGrp; break;
            case GralButton.kOn:       val |= FileRemote.mExecuteGrp; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mExecuteGrp; break;
          }
          switch(widgRd[2].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mCanReadAny; break;
            case GralButton.kOn:       val |= FileRemote.mCanReadAny; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mCanReadAny; break;
          }
          switch(widgWr[2].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mCanWriteAny; break;
            case GralButton.kOn:       val |= FileRemote.mCanWriteAny; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mCanWriteAny; break;
          }
          switch(widgEx[2].getState()){
            case GralButton.kOff:       val &= ~FileRemote.mExecuteAny; break;
            case GralButton.kOn:       val |= FileRemote.mExecuteAny; break;
            case GralButton.kDisabled: mask &= ~FileRemote.mExecuteAny; break;
          }
        }
        switch(widgHidden.getState()){
          case GralButton.kOff:       val &= ~FileRemote.mHidden; break;
          case GralButton.kOn:        val |=  FileRemote.mHidden; break;
          case GralButton.kDisabled: mask &= ~FileRemote.mHidden; break;
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
            actFileRemote.chgProps(name, mask, val, 0, evChg);
          } else { bAbort = true; }
          //
        } else if(infos.sCmd.equals(sCmdChgRecurs)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            //cmds with callback
            widgChrRecurs.setText(main.idents.buttonFilePropsChanging);
            actFileRemote.chgPropsRecursive(mask, val, 0, evChg);
          } else { bAbort = true; }
          //
        } else if(infos.sCmd.equals(sCmdCopy)){
          if(evChg.occupy(evSrc, callbackChgProps, null, true)){
            if(!name.equals(actFile.getName())){
              widgCopyFile.setText(main.idents.buttonFilePropsCopying);
              FileRemote fileNew = new FileRemote(actFileRemote.getParentFile(), name);
              actFileRemote.copyTo(fileNew, evChg, FileRemote.modeCopyReadOnlyOverwrite | FileRemote.modeCopyCreateYes | FileRemote.modeCopyExistAll);
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



  GralUserAction actionInvisible = new GralUserAction()
  { @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { isVisible = false;
      return true;
    }
  };


  EventConsumer callbackChgProps = new EventConsumer("FcmdFileProps-callbackChgProps")
  { @Override protected boolean processEvent_(Event evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        showFileInfos(actFile);
        widgChgFile.setText(main.idents.buttonFilePropsOk);
      } else {
        widgChgFile.setText(main.idents.buttonFilePropsRetry);
      }
      ev.relinquish();
      return true;
    } 
  };


  
  
  /**Action for Key F2 for view command. 
   */
  GralUserAction actionBtnCntLen = new GralUserAction()
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        widgBtnDirBytes.setText("counting ...");
        if(evCntLen.occupyRecall(100, evSrc, callbackCntLen, null, true)){
          FileRemote.fromFile(actFile).countAllFileLength(evCntLen);
        }
      }
      return true;
  } };
  
  EventConsumer callbackCntLen = new EventConsumer("FcmdFileProps - callback cnt length")
  { @Override protected boolean processEvent_(Event evP)
    { FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(ev.getCmd() == FileRemote.CallbackCmd.done){
        String sLen = "" + ev.nrofBytesAll;
        widgLength.setText(sLen);
      } else {
        widgLength.setText("error count bytes");
      }
      widgBtnDirBytes.setText(main.idents.buttonFilePropsCntLen);
      evP.relinquish();
      return true;
    } 
  };


  
}
