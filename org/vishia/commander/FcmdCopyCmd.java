package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
import org.vishia.util.EventSource;
import org.vishia.util.FileCompare;
import org.vishia.util.FileRemote;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFunctions;

/**This class contains all functionality to execute copy and move for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdCopyCmd
{
  /**Version and History
   * <ul>
   * <li>2013-02-03 Hartmut new: Copy exception (on file open to write) cases a message, the key {@link #widgOverwrFile}
   *   works together with the Ask state.
   * <li>2012-11-16 Hartmut chg: Copy: The {@link #filesToCopy} will be filled after callback of check. The files which are used
   *   are read only from the input fields of the GUI. The user can change it.
   * <li>2012-02-26 Hartmut chg: Strategy for button texts and abort behavior improved. Problem was: 
   *   The 'check' phase needs some calculation time for a slow network connection. 
   * <li>2012-02-04 Hartmut new: If a file was copied and a comparison result exists, it is set to equal.
   *   That helps to handle with comparison.
   * </ul>
   */
  public static final int version = 0x20120204; 
  
  protected final Fcmd main;

  
  GralWindow_ifc windConfirmCopy;
  
  GralPos posWindConfirmCopy;
  
  //GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralTextField_ifc widgCopyFrom, widgFromConditions, widgCopyDirDst, widgCopyNameDst, widgCopyState;
  
  GralButton widgdCreateNew, widgdOverwrDate, widgdOverwrReadOnly, widgOverwrFile, widgSkipFile, widgSkipDir;
  
  
  GralButton XXXwidgButtonDst, widgButtonSrc, widgButtonDel, widgButtonMove, widgButtonOk;

  GralValueBar widgProgressFile;
  GralValueBar widgProgressAll;
  
  GralButton widgButtonEsc;

  /**Mode overwrite readonly. '?'-ask, y-overwrite n-skip, don't ask
   */
  int modeOverwrReadonly = FileRemote.modeCopyReadOnlyAks;
  
  /**Mode overwrite with timestamp. 'w'-newer, o-older, n-never, ?-ask. */
  int modeOverwrDate = FileRemote.modeCopyExistAsk;
  
  /**Mode create new file while copying. 'n'-no, y-yes, a-ask. */
  int modeCreateCopy = FileRemote.modeCopyCreateYes;
  
  /**The file card where the directory content is shown where the files will be copied to, the destination. */
  FcmdFileCard fileCardSrc, fileCardDst;
  
  /**The source file to copy. It is set in the {@link #actionConfirmCopy()}. Either dirSrc == fileSrc 
   * and the {@link #sFilesSrc} contains some file names, or dirSrc is the directory of fileSrc, fileSrc is the only one
   * file to copy (maybe a directory) and sFilesSrc is empty.
   */
  FileRemote dirSrc, fileSrc;
  
  /**If more as one files are selected to copy, the names are contained here separated with ' : '.
   * Elsewhere it is "". */
  String sFilesSrc;
  
  /**The destination directory where the source files should copy to. 
   */
  FileRemote fileDstDir;

  /**Content from the input fields while copy is pending. */
  String sDstDir, sDstName;
  
  long zBytes, zFiles;
  
  //List<String> listFileSrc;
  
  /**This list is filled with some callback Events for files which are checked before copying.
   * If the callback of the check process occurs, the events will be removed from this list.
   * Only events which are found in this list are considered to build the list of files to copy.
   * This list is definitive cleared if the "abort" button is pressing. A current running check process
   * cannot be aborted because it runs in another unknown thread. But the usage of the callback is prevented then.
   */
  final List<FileRemote.CallbackEvent> listEvCheck = new LinkedList<FileRemote.CallbackEvent>();
  
  int nrofFilesCheck;
  
  
  /**State of the handling. */
  protected Estate state = Estate.inactive;
  
  /**Selected handling command. */
  protected Ecmd cmd = Ecmd.copy;
  
  protected boolean bLockSrc;
  
  /**If true then not all files are selected below the shown source directory.
   * Then move and delete should work in fine steps. 
   */
  protected boolean bFineSelect;
  
  final List<FileRemote.CallbackEvent> listEvCopy = new LinkedList<FileRemote.CallbackEvent>();
  
  /**Current files which are in copy process. Typical contains one file only. 
   * This list will be filled in {@link #actionConfirmCopy}.
   * If the copy process is finished, this list will be empty.
   */
  final List<FileRemote> filesToCopy = new LinkedList<FileRemote>();
  
  /**Last files which are in copy process. Typical contains one file only. 
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();
  
  /**This reference is set with the callback of operation cmd. 
   * The event can be used to affect the copying process.
   */
  FileRemote.CallbackEvent evCurrentFile;
  
  
  int modeCopy(){
    int mode = modeCreateCopy | modeOverwrReadonly |modeOverwrDate;
    return mode;            
  }
  
  
  EventSource evSrc = new EventSource("FcmdCopy"){
    
  };
  
  FcmdCopyCmd(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-copy window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmCopy()
  {
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //System.out.println("CopyWindow frame: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(-35, 0, -65, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    windConfirmCopy = main.gralMng.createWindow("copyWindow", "confirm copy", GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    //source path and check:
    main.gralMng.setPosition(0.5f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f);
    //main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyFrom = main.gralMng.addTextField("copyFrom", false, "source directory", "t");
    widgCopyFrom.setBackColor(GralColor.getColor("am"),0);

    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -13, 0, 'r', 0.3f);
    widgFromConditions = main.gralMng.addTextField("copyCond",true, "select src files: mask*:*.ext / 2012-08-05..06", "t");
    
    main.gralMng.setPosition(GralPos.same, GralPos.size +3.2f, -12, -1, 0, 'd', 0.3f);
    widgButtonSrc = main.gralMng.addButton("buttonSrc", actionConfirmCopy, "set src+dst" );
    widgButtonSrc.setCmd("setSrcDst");
    
    //dst path, set dst
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -1, 0, 'r', 0.3f);
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", true, "destination:", "t");

    //status
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f); //same line as del
    widgCopyState = main.gralMng.addTextField("copyStatus", false, "current state:", "t");
    widgCopyState.setBackColor(GralColor.getColor("lam"),0);
    
    //widgCopyDirDst.setBackColor(GralColor.getColor("am"),0);
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -1, 0, 'r', 1);
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", false, "current file:", "t");
    widgCopyNameDst.setBackColor(GralColor.getColor("lam"),0);
    
    //main.gralMng.setPosition(GralPos.same, GralPos.size +3.2f, -9, -1, 0, 'r',1);
    //widgButtonDst = main.gralMng.addSwitchButton("lastSrc", "? lock src", "lock src", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    //widgButtonDst.setActionChange(actionLastSrc);
    
    main.gralMng.setPosition(GralPos.refer+1.5f, GralPos.size -1.5f, 1, 18, 0, 'r', 1);
    main.gralMng.addText("Overwr read only ?");
    main.gralMng.addText("Overwr exists ?");
    main.gralMng.addText("Create ?");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, GralPos.size +12, 0, 'r',1);
    widgdOverwrReadOnly = main.gralMng.addButton("overwritero", actionOverwrReadonly, null, null,"ask ?yes ?no");
    widgdOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +20, 0, 'r',1);
    widgdOverwrDate = main.gralMng.addButton("copyOverwriteReadonly", actionOverwrDate, null, null, "ask ?newer?older?all ?no");
    widgdOverwrDate.setBackColor(GralColor.getColor("lam"), 0);
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +12, 0, 'r',1);
    widgdCreateNew = main.gralMng.addButton("copyCreate", actionCreateCopy, null, null, "yes ?no ?ask");
    widgdCreateNew.setBackColor(GralColor.getColor("lgn"), 0);

    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r', 1);
    widgOverwrFile = main.gralMng.addButton("copyOverwrite", actionOverwriteFile, "overwr file");
    widgSkipFile = main.gralMng.addButton("copyskip", actionButtonSkipFile, null, null, "skip file");
    //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 0, 'r', 1);
    widgSkipDir = main.gralMng.addButton("copySkipDir", actionButtonSkipDir, null, null, "skip dir");
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    widgButtonEsc = main.gralMng.addButton("copyEsc", actionButtonAbort, "esc", null, "close");
    main.gralMng.setPosition(-4, GralPos.size +1, 7, -14, 0, 'd', 1);
    widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null);
    widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null);

    main.gralMng.setPosition(-11, GralPos.size+3, -13, -1, 0, 'd', 0.5f);
    widgButtonDel = main.gralMng.addSwitchButton("buttonDel", "delete ?", "Delete", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgButtonDel.setActionChange(actionButtonDelMove);
    widgButtonMove = main.gralMng.addSwitchButton("copyMove", "move ?", "Move/ ?copy", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgButtonMove.setActionChange(actionButtonDelMove);
    widgButtonOk = main.gralMng.addButton("copyOk", actionButtonOk, "close", null, "close");
  
  }
  
  
  protected void closeWindow(){
    widgButtonOk.setText("close");
    widgButtonOk.setCmd("close");
    filesToCopy.clear();
    listEvCheck.clear();
    listEvCopy.clear();
    state = Estate.inactive;
    main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
  }
  
  
  protected void execCheck(){
    state = Estate.busyCheck;
    widgCopyState.setText("busy-check");
    widgButtonOk.setText("busy-check");
    widgButtonSrc.setCmd("busy");
    widgButtonSrc.setText("busy-check");
    widgButtonOk.setCmd("busy");
    widgButtonEsc.setText("abort");
    widgButtonEsc.setCmd("abort");
    String sSrcMask= widgFromConditions.getText();
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackCopy, null, evSrc);
    //listEvCheck.add(callback);
    fileSrc.check(sFilesSrc, sSrcMask, callback);   //callback.use() will be called on response
  }
  
  
  
  protected void execCopy(){
    //Starts the copy process (not move)
    widgCopyState.setText("busy-copy");
    widgButtonOk.setText("busy-copy");
    widgButtonOk.setCmd("busy");
    widgButtonEsc.setText("abort");
    widgButtonEsc.setCmd("abort");
    sDstName = widgCopyNameDst.getText();
    sDstDir = widgCopyDirDst.getText();
    if(!FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      sDstDir = dirSrc.getAbsolutePath() + "/" + sDstDir;
    }
    FileRemote dirDst = main.fileCluster.getFile(sDstDir, null); //new FileRemote(sDirSrc);
    dirDst.copyChecked(evCurrentFile, modeCopy());
    state = Estate.busy;
  } 
  
  
  protected void execMove(){
    sDstDir = widgCopyDirDst.getText();
    FileRemote fileDst;
    if(FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = main.fileCluster.getFile(sDstDir);  //maybe a file or directory
    } else {
      fileDst = dirSrc.child(sDstDir);  //relative to source
    }
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackCopy, null, evSrc);
    fileSrc.moveTo(fileDst, callback);
    state = Estate.busy;
    ///
  }
  
  
  protected void execDel(){
    if(bFineSelect){
      assert(evCurrentFile !=null);
      assert(state == Estate.checked);
      fileSrc.deleteChecked(evCurrentFile, 0);      
    } else {
      FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackCopy, null, evSrc);
      fileSrc.delete(callback);
    }
    state = Estate.finit;  //TODO
    ///
  }
  
  
  protected void abortCopy(){
    for(FileRemote.CallbackEvent ev: listEvCopy){
      ev.copyAbortAll();
      //ev.sendEvent(FileRemote.cmdAbortAll);
    }
    String sDirSrc = widgCopyFrom.getText();
    FileRemote dirSrc = main.fileCluster.getFile(sDirSrc, null); //new FileRemote(sDirSrc);
    dirSrc.abortAction();
    listEvCheck.clear();
    listEvCopy.clear();
    filesToCopy.clear();
    FcmdCopyCmd.this.evCurrentFile = null;
    widgButtonOk.setText("close");
    widgButtonOk.setCmd("close");

  }
  
  
  void setTexts(){
    String[][] textOk = 
    { { "delete", "move", "check copy" }
    , { "check del", "check move", "check copy" }
    , { "busy check", "busy check", "busy check" }
    , { "delete", "move", "copy" } 
    , { "pause del", "pause move", "pause copy" }
    , { "close del", "close move", "close copy" }
    };
    String[][] textSrc = 
    { { "set src + dst", "set src + dst", "busy check", "set src + dst", "busy", "set src + dst" }
    , { "set dst", "set dst", "busy check", "set dst", "busy", "set dst" }
    };
    int ix1;
    if(widgButtonDel.isOn()){ ix1=0; cmd = Ecmd.delete; }
    else if(widgButtonMove.isOn()) { ix1 = 1; cmd = Ecmd.move; }
    else { ix1 = 2; cmd = Ecmd.copy; }
    int ix2;
    switch(state){
      case start: ix2= bFineSelect? 1 : 0; break;
      case busyCheck: ix2=2; break;
      case checked: ix2=3; break;
      case busy: ix2=4; break;
      case finit: ix2=5; break;
      default: ix2=0;
    }
    String sTextBtnOk = textOk[ix2][ix1];
    widgButtonOk.setText(sTextBtnOk);
    String sTextSrc = textSrc[bLockSrc? 1: 0][ix2];
    widgButtonSrc.setText(sTextSrc);
  }
  
  

  
  void actionConfirmCopy(){
    
  }

  protected GralUserAction XXXactionLastSrc = new GralUserAction("actionLastSource") ///
  { @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        GralButton widgb = (GralButton)(widgP);
        if(widgb.isOn()){
          widgButtonSrc.setText("get dst");
        } else {
          widgButtonSrc.setText("get src+dst");
        }
        /*
        if(widgButtonOk.sCmd.equals("check")){ //only if check phase
          if(widgb.isOn()){
            int nrofSrcFiles = filesToCopy.size();
            if(nrofSrcFiles >=1){
              FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
              fileCardDst = lastFileCards[0]; //the current file card is destination!
              if(fileCardDst !=null){
                fileDstDir = FileRemote.fromFile(fileCardDst.getCurrentDir());
                sDstDir = fileDstDir.getAbsolutePath();
                widgCopyDirDst.setText(sDstDir);
                File src1 = filesToCopy.get(0);
                if(nrofSrcFiles == 1){
                  widgCopyFrom.setText(src1.getAbsolutePath());
                  widgCopyNameDst.setText(src1.getName());
                } else {
                  File dir = src1.getParentFile();
                  widgCopyFrom.setText(dir.getAbsolutePath() + "/*");
                  widgCopyNameDst.setText("*");
                }
              }
            }
            else { //no src files given
              //widgb.setState(GralButton.kDisabled);  //set state to off, "copy src"
            }
          } else {
            //off or inactive:
            
          }
        }
        */
        //else don't do anything.
      }
      return true;
    }
  };
  
  
  GralUserAction actionOverwrReadonly = new GralUserAction("actionOverwrReadonly")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeOverwrReadonly){
        case FileRemote.modeCopyReadOnlyAks: 
          modeOverwrReadonly= FileRemote.modeCopyReadOnlyOverwrite; 
          widgdOverwrReadOnly.setText("yes ?no ?ask");
          widgdOverwrReadOnly.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyReadOnlyNever: 
          modeOverwrReadonly= FileRemote.modeCopyReadOnlyAks; 
          widgdOverwrReadOnly.setText("ask ?yes ?no");
          widgdOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
          break;
        default:
          modeOverwrReadonly= FileRemote.modeCopyReadOnlyNever; 
          widgdOverwrReadOnly.setText("no ?ask ?yes");
          widgdOverwrReadOnly.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionOverwrDate = new GralUserAction("actionOverwrDate")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeOverwrDate){
        case FileRemote.modeCopyExistAsk: 
          modeOverwrDate= FileRemote.modeCopyExistNewer; 
          widgdOverwrDate.setText("newer ?older?all ?no?ask");
          widgdOverwrDate.setBackColor(GralColor.getColor("lpk"),0);
          break;
        case FileRemote.modeCopyExistNewer: 
          modeOverwrDate= FileRemote.modeCopyExistOlder; 
          widgdOverwrDate.setText("older ?all ?no?ask?newer");
          widgdOverwrDate.setBackColor(GralColor.getColor("lbl"),0);
          break;
        case FileRemote.modeCopyExistOlder:
          modeOverwrDate= FileRemote.modeCopyExistAll; 
          widgdOverwrDate.setText("all ? no?ask?newer?older");
          widgdOverwrDate.setBackColor(GralColor.getColor("lgn"),0);
          break;
        case FileRemote.modeCopyExistAll:
          modeOverwrDate= FileRemote.modeCopyExistSkip; 
          widgdOverwrDate.setText("no ?ask?newer?older?all");
          widgdOverwrDate.setBackColor(GralColor.getColor("lrd"),0);
          break;
        default:
          modeOverwrDate= FileRemote.modeCopyExistAsk; 
          widgdOverwrDate.setText("ask ?newer?older?all ?no");
          widgdOverwrDate.setBackColor(GralColor.getColor("lam"),0);
          break;
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionCreateCopy = new GralUserAction("actionButtonCreateCopy")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeCreateCopy){
        case FileRemote.modeCopyCreateAsk: 
          modeCreateCopy = FileRemote.modeCopyCreateYes; 
          widgdCreateNew.setText("yes ?no ?ask");
          widgdCreateNew.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyCreateYes: 
          modeCreateCopy = FileRemote.modeCopyCreateNever; 
          widgdCreateNew.setText("no ?ask ?yes");
          widgdCreateNew.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        default:
          modeCreateCopy = FileRemote.modeCopyCreateAsk; 
          widgdCreateNew.setText("ask ?yes ?no");
          widgdCreateNew.setBackColor(GralColor.getColor("lam"), 0);
          break;
        }
      }
      return true;
  } };
  
  
  /**Opens the confirm-copy window, prepares the list of src files.
   * It is Key F5 for copy command from the classic NortonCommander.
   * The OK-key is designated to "check". On button pressed the {@link #actionButtonCopy} is called,
   * with the "check" case.
   */
  GralUserAction actionConfirmCopy = new GralUserAction("actionConfirmCopy")
  {
    /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
     * @param dst The path which is selected as destination. It may be a directory or a file
     * @param src The path which is selected as source. It may be a directory or a file.
     */
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { //String sSrc, sDstName, sDstDir;
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(state == Estate.inactive){
          state = Estate.finit;
        }
        //if(state.equals("close") || state.equals("check")) {
        if(state == Estate.start || state == Estate.checked || state == Estate.finit || state == Estate.error) {
          //only if it is ready to check, get the files.
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
          if(lastFileCards[0] !=null){ ///
            if(bLockSrc){  //state src lock
              fileCardDst = lastFileCards[0];
              fileDstDir = fileCardDst.getCurrentDir();
              sDstDir = fileDstDir.getAbsolutePath();
              widgCopyDirDst.setText(sDstDir);
              bLockSrc = false;
            } else {
              fileCardSrc = lastFileCards[0];
              fileCardDst = lastFileCards[1];
              
              //widgButtonDst.setState(GralButton.kOff);  //maybe disabled, set off.
              List<FileRemote> listFileSrc = fileCardSrc.getSelectedFiles();
              //String sDirSrc;
              if(listFileSrc == null || listFileSrc.size()==0){ //nothing selected
                fileSrc = fileCardSrc.currentFile;
                dirSrc = fileSrc.getParentFile();
                sFilesSrc = "";
                widgCopyFrom.setText(fileSrc.getAbsolutePath());
              } else {
                StringBuilder uFileSrc = new StringBuilder();
                dirSrc = fileSrc = fileCardSrc.getCurrentDir();
                String sSep = "";
                for(File srcFile : listFileSrc){
                  uFileSrc.append(sSep).append(srcFile.getName());
                  sSep = " : "; //For next one.
                  //FileRemote fileSrc = FileRemote.fromFile(srcFile);
                  //filesToCopy.add(fileSrc);
                }
                sFilesSrc = uFileSrc.toString();
                uFileSrc.insert(0, " : ").insert(0, fileSrc.getAbsolutePath());
                widgCopyFrom.setText(uFileSrc);
              }
              sDstName = listFileSrc.size() >1 ? "*" 
                         : listFileSrc.size() ==1 ? listFileSrc.get(0).getName() : "??";
              //sSrc = fileSrc.getAbsolutePath() + "/" + sDstName;
              if(fileCardDst !=null){
                fileDstDir = fileCardDst.getCurrentDir();
                sDstDir = fileDstDir.getAbsolutePath();
              } else {
                fileDstDir = null;
                sDstDir = "??";
              }
              bLockSrc = true;
              widgFromConditions.setText("");
            }
          } else { //FileCard not found:
            fileSrc = null;
            fileDstDir = null;
            
            //listFileSrc = null;
            //sSrc = "???";
            sDstName = "??";
            sDstDir = "??";
            widgCopyFrom.setText("??");
          }
          widgCopyDirDst.setText(sDstDir);
          widgCopyNameDst.setText(sDstName);
          boolean setVisible = true; //state == Estate.inactive;
          state = Estate.start;
          setTexts();
          //widgButtonOk.setText("check");
          //widgButtonOk.setCmd("check");
          widgCopyState.setText("check?", 0);
          if(setVisible){
            windConfirmCopy.setWindowVisible(true);
            //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
            main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");
          }
          //widgButtonMove.setValue(GralMng_ifc.cmdSet, 0, 0);
          zFiles = zBytes = 0;
        }
      }
      return true;
   }  };
  
  

   
   
   /**Opens the confirm-copy window, prepares the list of src files.
    * It is Key F5 for copy command from the classic NortonCommander.
    * The OK-key is designated to "check". On button pressed the {@link #actionButtonCopy} is called,
    * with the "check" case.
    */
   GralUserAction actionButtonDelMove = new GralUserAction("actionButtonDel")
   {
     /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         setTexts();
       }
       return true;
     }
   };
   
   
  
  /**Action for any button of the confirm-copy window.
   * in a loop FileRemote.copyTo(dst) is called for any selected file. It may be one file, 
   * or more files in more selected lines.
   * The copy process itself is executed in an own thread in management of the FileRemote implementation,
   * see {@link org.vishia.fileLocalAccessor.FileRemoteAccessorLocalFile#addCommission(org.vishia.util.FileRemoteAccessor.Commission)}
   * for local files. The copy process can be executed in an remote device without copying of data
   * between this device (PC) and the remote device, if the commission is organized in the remote device itself.
   * <br><br>
   * To notify for success or progression of the copy process some events are used.
   * See the {@link #success} {@link EventConsumer} in this class. The event instance is given to the 
   * {@link FileRemote#copyTo(FileRemote, Event)} invocation. It is used to callback either from the thread
   * which copies local or from the thread which receives the copy response telegrams for remote communication.
   * <br><br>
   * All Events which are created are stored in the {@link #listEvCopy}. If the callback occurs, the event 
   * will be removed from the list. The list is only used to check whether the copy action is succeeded 
   * respectively to check whether all copy actions are succeeded. If a copy success callback is not received, 
   * for example because a remote device hangs or the communication fails, the copy process hasn't no
   * progression. The operator on this machine sees this situation because the progression bar stands.
   * The operator can abort the copy process to preset to a default empty state.  
   */
  protected GralUserAction actionButtonOk = new GralUserAction("actionButtonCopy")
  { @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(state == Estate.start) { //widgg.sCmd.equals("check")){    
          if(cmd == Ecmd.copy || bFineSelect){
            execCheck();
          } else if(cmd == Ecmd.move){
            execMove();
          } else if(cmd == Ecmd.delete){
            execDel();
          }
        } else if(state == Estate.checked) { //widgg.sCmd.equals("copy")) {
          switch(cmd){
            case copy: execCopy(); break;
            case move: execMove(); break; 
            case delete: execDel(); break;
          }//switch
        } else if(state == Estate.finit) { //widgg.sCmd.equals("close")){
          closeWindow();
        }
      }
      return true;
    }
  };

  
  
  
  protected GralUserAction actionButtonAbort = new GralUserAction("actionButtonAbort")
  { @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(state){ //inactive, start, checked, busyCheck, busy, quest, error, finit
          case inactive:
          case start:
          case error:
          case finit: closeWindow(); break;
          case quest:
          case busy: abortCopy(); break;
          case checked: break;
          case busyCheck: break;
        }
      }
      return true;
    }
  };

  
  
  

  /**This action is used to skip over the current showed file while copying. Copying of the selected file
   * should be aborted and that file should be deleted.
   * 
   */
  protected GralUserAction actionOverwriteFile = new GralUserAction("overwr file")
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(evCurrentFile !=null){
          int modeCopyOper = modeCopy();
          evCurrentFile.copyOverwriteFile(modeCopyOper);
        }
        widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
        widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
      }
      return true;
    } 
  };
  
  
  
  
  /**This action is used to skip over the current showed file while copying. Copying of the selected file
   * should be aborted and that file should be deleted.
   * 
   */
  protected GralUserAction actionButtonSkipFile = new GralUserAction("actionButtonSkipFile")
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(evCurrentFile !=null){
          int modeCopyOper = modeCopy();
          evCurrentFile.copySkipFile(modeCopyOper);
        }
        widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
        widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
      }
      return true;
    } 
  };
  
  
  /**This action is used to skip over the current showed file while copying. Copying of the selected file
   * should be aborted and that file should be deleted.
   * 
   */
  protected GralUserAction actionButtonSkipDir = new GralUserAction("actionButtonSkipDir")
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(evCurrentFile !=null){
          int modeCopyOper = modeCopy();
          evCurrentFile.copySkipDir(modeCopyOper);
        }
        widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
        widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
      }
      return true;
    } 
  };
  
  
  GralUserAction XXXactionSwitchButtonMove = new GralUserAction("actionButtonMoveCopy")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(widgButtonMove.isOn()){
          widgButtonOk.setText("move");
          widgButtonOk.setCmd("copy");
        } else {
          if(widgCopyState.getText().equals("check")){
            widgButtonOk.setText("check");
            widgButtonOk.setCmd("check");
          } else {
            widgButtonOk.setText("copy");
            widgButtonOk.setCmd("copy");
          }
        }
        return true; 
      } else return false;
  } };
  
  
  EventConsumer XXXcallbackCheck = new EventConsumer(){
    @Override public int processEvent(Event<?,?> evP)
    {
      FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(listEvCheck.remove(ev)){  ///
        filesToCopy.add(ev.getFileSrc());
        zBytes += ev.nrofBytesAll;
        zFiles += ev.nrofFiles;
        int nrofPendingFiles = listEvCheck.size();
        int percent;
        if(nrofFilesCheck >0){
          percent = nrofPendingFiles * 100 / nrofFilesCheck;
        } else {
          percent = 100; 
        }
        widgProgressAll.setValue(percent);
        if(nrofPendingFiles == 0){
          //TODO check dst space
          widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
          widgButtonOk.setText("copy");
          widgButtonOk.setCmd("copy");
        }
      }
      return 1;
    }
    @Override public String toString(){ return "FcmdCopy-check"; }

  };
  
  
  
  private void eventConsumed(Event<?,?> evp, boolean ok){
    FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evp;
    listEvCopy.remove(ev);
    int nrofPendingFiles = listEvCopy.size();
    
    /*
    //int percent = (int)(nrofPendingFiles * 100 / zFiles);  //- filesToCopy.size();
    widgProgressAll.setValue(100);
    if(ok){
      File file = ev.getFileSrc();
      FileCompare.Result cmprResult = fileCardSrc.searchCompareResult(file);
      if(cmprResult !=null){
        cmprResult.setToEqual();  
      }
      fileCardSrc.fillInCurrentDir();  //maybe changed if dst = src or if moved.
      if(fileCardDst !=null){
        fileCardDst.fillInCurrentDir();
      }
    }
    */
    if(nrofPendingFiles == 0){
      if(!ok){
        widgCopyState.setText("error");
        widgButtonOk.setText("quit");
        widgButtonOk.setCmd("quit");
      } else {
        widgButtonOk.setText("close");
        widgButtonOk.setCmd("close");
      }
      widgButtonEsc.setText("close");
      widgButtonEsc.setCmd("close");
      windConfirmCopy.setWindowVisible(false);      
    }
  }
  
  
  
  
  /**The method which is invoked on callback of any action copy, check, move
   * and their intermediate message. 
   * 
   */
  EventConsumer callbackCopy = new EventConsumer(){
    @Override public int processEvent(Event<?,?> ev)
    {
      FileRemote.CallbackEvent ev1 = (FileRemote.CallbackEvent)ev;
      FileRemote.CallbackCmd cmd = ev1.getCmd();
      String sCmd = cmd.name();
      System.out.println("FcmdCopy - callbackCopy;" + sCmd);
      switch(ev1.getCmd()){
        case doneCheck:{ ///
          //if(listEvCheck.remove(ev)){  ///
            FcmdCopyCmd.this.evCurrentFile = ev1;
            zFiles = ev1.nrofFiles;
            zBytes = ev1.nrofBytesAll;
            //int nrofPendingFiles = listEvCheck.size();
            //if(nrofPendingFiles == 0){
              //TODO check dst space
              widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
              //widgButtonOk.setText("copy");
              //widgButtonOk.setCmd("copy");
              //widgButtonSrc.setText("set src");
              //widgButtonSrc.setCmd("setSrc");
            //}
            
          //} else {
            //unexpected doneCheck:
          //}
          state = Estate.checked;
          setTexts();
        } break;
        case copyDir:{
          FcmdCopyCmd.this.evCurrentFile = ev1;
          String sPath = StringFunctions.z_StringJc(ev1.fileName);
          widgCopyNameDst.setText(sPath);
        } break;
        case nrofFilesAndBytes:{
          FcmdCopyCmd.this.evCurrentFile = ev1;
          int percent = ev1.promilleCopiedBytes / 10;
          widgProgressFile.setValue(percent);
          widgProgressAll.setValue(ev1.promilleCopiedFiles / 10);
          widgCopyNameDst.setText(StringFunctions.z_StringJc(ev1.fileName));
          widgCopyState.setText("... " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
        }break;
        case askDstOverwr: {
          FcmdCopyCmd.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("exists: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askDstReadonly: {
            FcmdCopyCmd.this.evCurrentFile = ev1;
            widgCopyNameDst.setText("read only: " + StringFunctions.z_StringJc(ev1.fileName));
            widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
            widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askDstNotAbletoOverwr: {
          FcmdCopyCmd.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("can't overwrite: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
        } break;
        case askErrorDstCreate: {
          FcmdCopyCmd.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("can't create: " + StringFunctions.z_StringJc(ev1.fileName));
          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askErrorCopy: {
          FcmdCopyCmd.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("copy error: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
        } break;
        case error: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("error");
          state = Estate.error;
          setTexts();
          eventConsumed(ev, false);
        }break;
        case nok: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("nok");
          state = Estate.error;
          setTexts();
          eventConsumed(ev, false);
        }break;
        case done: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("ok: " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
          eventConsumed(ev, true);
          state = Estate.finit;
          setTexts();
        }break;
        default:
          FcmdCopyCmd.this.evCurrentFile = ev1;

      }
      //windConfirmCopy.setWindowVisible(false);
      return 1;
    }
    @Override public String toString(){ return "FcmdCopy-success"; }

  };

  enum Estate{ inactive, start, checked, busyCheck, busy, quest, error, finit};
  
  enum Ecmd{ copy, move, delete};
  
  void stop(){}
  
}
