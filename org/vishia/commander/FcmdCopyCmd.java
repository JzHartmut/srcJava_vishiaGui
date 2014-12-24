package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.event.Event;
import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.fileRemote.FileMark;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteCallback;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralSwitchExclusiveButtonMng;
import org.vishia.util.Debugutil;
import org.vishia.util.FileCompare;
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
   * <li>2013-02-03 Hartmut chg: set the destination only if it is not set on button setSrc
   * <li>2013-02-03 Hartmut chg: {@link #execMove()} with files
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
  
  GralTextField widgShowSrc, widgFromConditions, widgInputDst, widgCopyNameDst, widgCopyState;
  
  GralButton widgdChoiceCreateNew, widgdChoiceOverwrExists, widgdChoiceOverwrReadOnly;
  
  GralButton widgOverwrFile, widgSkipFile, widgSkipDir, widgBtnPause;
  
  
  GralButton widgButtonSetSrc, widgButtonSetDst, widgButtonCheck, widgButtonCmpr, widgButtonDel, widgButtonMove, widgButtonOk;

  GralButton widgButtonClearSel, widgButtonShowSrc, widgButtonShowDst, widgButtonShowResult;
  
  GralValueBar widgProgressFile;
  GralValueBar widgProgressAll;
  
  GralButton widgButtonEsc;

  GralColor colorNoChangedText = GralColor.getColor("am");
  
  GralColor colorChangedText = GralColor.getColor("wh");
  
  GralColor colorGrayed = GralColor.getColor("gr");
  
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
  
  /**The destination selected directory and file as destination for compare and move or second tree for comparison. 
   * The fileDst is build with the name of the source file for copy and move and with the real dst file for compare
   */
  FileRemote dirDst; //, dirDstCmpr;

  /**Name of the file for dst. */
  CharSequence sFileDstCopy;
  
  StringBuilder bufferDstChars = new StringBuilder(100);
  
  /**If true then the {@link #widgInputDst} was changed for this session. Not automatically change the content. */
  boolean bDstChanged;

  boolean bFirstSelect;
  
  /**Content from the input fields while copy is pending. */
  //String sDstDir, sDstName;
  
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
  
  //protected boolean bLockSrc;
  
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
    main.gralMng.setPosition(10, GralPos.size+37, 10, GralPos.size+70, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    int windprops = GralWindow.windConcurrently; // + GralWindow.windResizeable;
    windConfirmCopy = main.gralMng.createWindow("copyWindow", "confirm copy / move / delete / compare", windprops);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    //source path and check:
    main.gralMng.setPosition(0.5f, GralPos.size +2.5f, 15, GralPos.size+12, 0, 'r', 1);
    widgButtonSetSrc = main.gralMng.addButton(null, actionConfirmCopy, "setSrc", null, "set source" );
    widgButtonSetSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.setSrc.");
    widgButtonClearSel = main.gralMng.addButton(null, null, "clrSel", null, "clear selection" );
    widgButtonClearSel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.clearSel.");
    main.gralMng.setPosition(GralPos.same, GralPos.samesize, -17, -1, 0, 'r', 1);
    widgButtonSetDst = main.gralMng.addButton(null, actionSetDst, "setDst", null, "set destination" );
    widgButtonSetDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.setDst.");
    
    main.gralMng.setPosition(2.5f, GralPos.size +3.2f, 1, -4, 0, 'd', 0);
    //main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgShowSrc = main.gralMng.addTextField("copyFrom", false, "source root path", "t");
    widgShowSrc.setBackColor(GralColor.getColor("am"),0);
    widgShowSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.pathSrc.");
    main.gralMng.setPosition(GralPos.refer+0.9f, GralPos.size +2.5f, -4f, -1, 0, 'r', 1);
    widgButtonShowSrc = main.gralMng.addButton(null, null, "showSrc", null, "=>" );
    widgButtonShowSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.show.");
    
    
    main.gralMng.setPosition(GralPos.refer + 2.3f, GralPos.size +3.2f, 1, -13, 0, 'r', 0.3f);
    widgFromConditions = main.gralMng.addTextField("copyCond",true, "select src files: mask*:*.ext / 2012-08-05..06", "t");
    widgFromConditions.setActionChange(actionSelectMask);
    widgFromConditions.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.selcond.");
    
    main.gralMng.setPosition(GralPos.refer+0.2f, GralPos.size+3.5f, -13, -1, 0, 'd', 0);
    widgButtonCheck = main.gralMng.addButton("buttonCheck", actionCheck, "check", null, "check" );
    widgButtonCheck.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.check.");
    //widgButtonCheck.setCmd("setSrcDst");

    
    
    
    
    
    
    //dst path, set dst
    main.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size +3.2f, 1, -4, 0, 'r', 0);
    widgInputDst = main.gralMng.addTextField("copyDirDst", true, "destination:", "t");
    widgInputDst.setActionChange(actionEnterTextInDst);
    widgInputDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.setDst.");
    main.gralMng.setPosition(GralPos.refer+0.9f, GralPos.size +2.5f, -4, -1, 0, 'r', 1);
    widgButtonShowDst = main.gralMng.addButton(null, null, "showDst", null, "=>" );
    widgButtonShowDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.show.");
    
    
    
    
    
    //widgButtonShowDst = main.gralMng.addButton(null, null, "show" );

    //field for showing the current action or state, not for input:
    main.gralMng.setPosition(GralPos.refer+2.6f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f); //same line as del
    widgCopyState = main.gralMng.addTextField("copyStatus", false, "current state:", "t");
    widgCopyState.setBackColor(GralColor.getColor("lam"),0);
    widgCopyState.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.state.");
    
    //field for showing the current name, not for input:
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -4.5f, 0, 'r', 1);
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", false, "current file:", "t");
    widgCopyNameDst.setBackColor(GralColor.getColor("lam"),0);
    widgCopyNameDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.currfile.");

    
    main.gralMng.setPosition(GralPos.refer+0.9f, GralPos.size +2.5f, -4.5f, -1, 0, 'r', 1);
    widgButtonShowResult = main.gralMng.addButton(null, null, "showResult", null, "=>" );
    widgButtonShowResult.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.show.");
    
    //main.gralMng.setPosition(GralPos.same, GralPos.size +3.2f, -9, -1, 0, 'r',1);
    //widgButtonDst = main.gralMng.addSwitchButton("lastSrc", "? lock src", "lock src", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    //widgButtonDst.setActionChange(actionLastSrc);
    
    main.gralMng.setPosition(GralPos.refer+1.5f, GralPos.size -1.5f, 1, 18, 0, 'r', 1);
    main.gralMng.addText("Overwr read only ?");
    main.gralMng.addText("Overwr exists ?");
    main.gralMng.addText("Create ?");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, GralPos.size +12, 0, 'r',1);
    widgdChoiceOverwrReadOnly = main.gralMng.addButton("overwritero", actionOverwrReadonly, "overwritero", null,"ask ?yes ?no");
    widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
    widgdChoiceOverwrReadOnly.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.ctrl.overwrRo.");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +20, 0, 'r',1);
    widgdChoiceOverwrExists = main.gralMng.addButton("copyOverwriteReadonly", actionOverwrDate, "copyOverwriteReadonly", null, "ask ?newer?older?all ?no");
    widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"), 0);
    widgdChoiceOverwrExists.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.ctrl.overwrExists.");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +12, 0, 'r',1);
    widgdChoiceCreateNew = main.gralMng.addButton("copyCreate", actionCreateCopy, "copyCreate", null, "yes ?no ?ask");
    widgdChoiceCreateNew.setBackColor(GralColor.getColor("lgn"), 0);
    widgdChoiceCreateNew.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.ctrl.createNew.");

    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 12, 0, 'r', 1);
    widgOverwrFile = main.gralMng.addButton("copyOverwrite", actionOverwriteFile, "copyOverwrite", null, "overwr file");
    widgOverwrFile.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.quest.wr.");
    widgSkipFile = main.gralMng.addButton("copyskip", actionButtonSkipFile, "copyskip", null, "skip file");
    widgSkipFile.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.quest.skipFile.");
    //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 0, 'r', 1);
    widgSkipDir = main.gralMng.addButton("copySkipDir", actionButtonSkipDir, "copySkipDir", null, "skip dir");
    widgSkipDir.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.quest.skipDir.");
    
    widgBtnPause = main.gralMng.addButton("pause", null, "pause", null, "pause");
    widgBtnPause.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.pause.");
    
    main.gralMng.setPosition(-4, GralPos.size+3.2f, 1, 9, 0, 'r');
    widgButtonEsc = main.gralMng.addButton("copyEsc", actionButtonAbort, "esc", null, "close");
    widgButtonEsc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.escape.");
    
    main.gralMng.setPosition(-4, GralPos.size +1, 10, -14, 0, 'd', 1);
    widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null);
    widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null);

    main.gralMng.setPosition(-13, GralPos.size+2.5f, -13f, -1, 0, 'd', 0.3f);
    widgButtonCmpr = main.gralMng.addSwitchButton(null, "compare ?", "Compare", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgButtonCmpr.setActionChange(actionButtonCmprDelMove);
    widgButtonCmpr.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.fn.");
    widgButtonDel = main.gralMng.addSwitchButton("buttonDel", "delete ?", "Delete", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgButtonDel.setActionChange(actionButtonCmprDelMove);
    widgButtonDel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.fn.");
    widgButtonMove = main.gralMng.addSwitchButton("copyMove", "move ?", "Move/ ?copy", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgButtonMove.setActionChange(actionButtonCmprDelMove);
    widgButtonMove.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.fn.");
    main.gralMng.setPosition(-4, GralPos.size+3.5f, -13f, -1, 0, 'd', 0.4f);
    widgButtonOk = main.gralMng.addButton("copyOk", actionButtonOk, "close", null, "close");
    widgButtonOk.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.ok.");
  
  }
  
  
  protected void closeWindow(){
    widgButtonOk.setText("close");
    //widgButtonOk.setCmd("close");
    filesToCopy.clear();
    listEvCheck.clear();
    listEvCopy.clear();
    state = Estate.inactive;
    windConfirmCopy.setVisible(false);
    //main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
  }
  
  
  protected void execCheck(){
    setTexts(Estate.busyCheck);
    widgCopyState.setText("busy-check");
    widgButtonOk.setText("busy-check");
    widgButtonCheck.setState(GralButton.State.Disabled);
    //widgButtonCheck.setCmd("busy");
    //widgButtonCheck.setText("busy-check");
    //widgButtonOk.setCmd("busy");
    widgButtonEsc.setText("abort");
    //widgButtonEsc.setCmd("abort");
    String sSrcMask= widgFromConditions.getText();
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackFromFileMachine, null, evSrc);
    //listEvCheck.add(callback);
    fileSrc.check(sFilesSrc, sSrcMask, callback);   //callback.use() will be called on response
  }
  
  
  void execMark(){
    setTexts(Estate.busyCheck);
    widgCopyState.setText("busy-check");
    widgButtonOk.setText("busy-check");
    widgButtonCheck.setState(GralButton.State.Disabled);
    widgButtonEsc.setText("abort");
    String sSrcMask= widgFromConditions.getText();
    fileSrc.refreshAndMark(0, bFirstSelect, sSrcMask, FileMark.select, 0, callbackFromFilesCheck);
    bFirstSelect = false;
  }
  
  
  protected void execDel(){
    if(state == Estate.checked){
      fileSrc.deleteMarkedInThread(FileMark.select, callbackFromFilesExec);      
    } else if(state == Estate.start){
    }
  }
  
  
  
  protected void XXXexecDel(){
    if(state == Estate.checked){
      assert(evCurrentFile !=null);
      assert(state == Estate.checked);
      fileSrc.deleteChecked(evCurrentFile, 0);      
    } else if(state == Estate.start){
      FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackFromFileMachine, null, evSrc);
      //fileSrc.deleteChecked(callback, 0);
    }
    //setTexts(Estate.finit);  //TODO
    ///
  }
  
  
  
  protected void execCopy(){
    //Starts the copy process (not move)
    widgCopyState.setText("busy-copy");
    widgButtonOk.setText("busy-copy");
    //widgButtonOk.setCmd("busy");
    widgButtonEsc.setText("abort");
    //widgButtonEsc.setCmd("abort");
    //sDstName = widgCopyNameDst.getText();
    String sDst = widgInputDst.getText();
    String sDstDir, sDstMask;
    int posWildcard = sDst.indexOf('*');
    if(posWildcard >0) {
      int posSep = sDst.lastIndexOf('/', posWildcard);
      sDstDir = sDst.substring(0, posSep+1);
      sDstMask = sDst.substring(posSep+1);
    } else {
      sDstDir = sDst;
      sDstMask = null;
    }
    if(!FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      sDstDir = dirSrc.getAbsolutePath() + "/" + sDstDir;
    }
    FileRemote dirDst = main.fileCluster.getFile(sDstDir, null); //new FileRemote(sDirSrc);
    dirDst.copyChecked(evCurrentFile, sDstMask, modeCopy());
    setTexts(Estate.busy);
  } 
  
  
  protected void execCompare(){
    String sDstDir = widgInputDst.getText();
    FileRemote fileDst;
    if(FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = dirSrc.child(sDstDir);  //relative to source
    }
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackFromFileMachine, null, evSrc);
    //fileSrc.moveTo(sFilesSrc, fileDst, callback);
    FileRemote.cmpFiles(fileSrc, fileDst, callback); ////
    setTexts(Estate.busy);
    ///
  }
  
  
  protected void execMove(){
    String sDstDir = widgInputDst.getText();
    FileRemote fileDst;
    if(FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = dirSrc.child(sDstDir);  //relative to source
    }
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackFromFileMachine, null, evSrc);
    fileSrc.moveTo(sFilesSrc, fileDst, callback);
    setTexts(Estate.busy);
    ///
  }
  
  protected void abortCopy(){
    for(FileRemote.CallbackEvent ev: listEvCopy){
      ev.copyAbortAll();
      //ev.sendEvent(FileRemote.cmdAbortAll);
    }
    String sDirSrc = widgShowSrc.getText();
    //FileRemote dirSrc = main.fileCluster.getFile(sDirSrc, null); //new FileRemote(sDirSrc);
    if(dirSrc !=null) { dirSrc.abortAction(); }  //to set stateMachine of copy in ready state 
    listEvCheck.clear();
    listEvCopy.clear();
    filesToCopy.clear();
    FcmdCopyCmd.this.evCurrentFile = null;
    //bLockSrc = false;
    //widgButtonOk.setText("close");
    //widgButtonOk.setCmd("close");
    setTexts(Estate.finit);
  }
  
  
  /**Set the texts to any widgets depending on the state of execution and the activated switch key.
   * @param newState
   */
  void setTexts( Estate newState){
    state = newState;
    //Texts depending from pressed command button:
    String[][] textOk = 
    { { "check del"  , "move"        , "check copy", "compare" }
    , { "check del"  , "move"        , "check copy", "compare" }
    , { "busy check" , "busy check"  , "busy check", "compare" }
    , { "del checked", "move checked", "copy checked", "compare" } 
    , { "pause del"  , "pause move"  , "pause copy", "pause cmpr" }
    , { "close del"  , "close move"  , "close copy", "close cmpr"  }
    };
    String[][] textSrc = 
    { { "set src + dst", "set src + dst", "busy check", "set src + dst", "busy", "set src + dst" }
    , { "set dst", "set dst", "busy check", "set dst", "busy", "set dst" }
    };
    String[] textAbort = { "abort"         , "abort"       , "abort"        , "abort", "abort", "close" };
    String[] textDest =  { "---"           , "move to"     , "copy to"      , "compare with"  };
    String[] sTitle =    { "processing ...", "confirm move", "confirm copy ", "confirm compare", "close"  };
    
    int ix1;
    if(widgButtonCmpr.isOn()){ 
      ix1 = 3; cmd = Ecmd.compare; 
      widgButtonDel.setState(GralButton.State.Off);
      widgButtonMove.setState(GralButton.State.Off);
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    } else if(widgButtonDel.isOn()){ 
      ix1=0; cmd = Ecmd.delete; 
      widgButtonMove.setState(GralButton.State.Off);
      widgInputDst.setEditable(false);
      widgInputDst.setBackColor(colorGrayed, 0);
    } else if(widgButtonMove.isOn()) { 
      ix1 = 1; cmd = Ecmd.move; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    } else { 
      ix1 = 2; cmd = Ecmd.copy; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    }
    windConfirmCopy.setTitle(sTitle[ix1]);
    widgInputDst.setPrompt(textDest[ix1]);
    int ix2;
    GralButton.State checkDisable;
    boolean setSrcPossible;
    switch(state){
      case start: setSrcPossible = true; checkDisable = GralButton.State.On; ix2= bFineSelect? 1 : 0; break;
      case busyCheck: setSrcPossible = false; checkDisable = GralButton.State.Disabled; ix2=2; break;
      case checked: setSrcPossible = false; checkDisable = GralButton.State.On; ix2=3; break;
      case quest:
      case busy: setSrcPossible = false; checkDisable = GralButton.State.Disabled; ix2=4; break;
      case error:
      case inactive:
      case finit: setSrcPossible = true; checkDisable = GralButton.State.On; ix2=5; break;
      default: setSrcPossible = true; checkDisable = GralButton.State.On; ix2=0;
    }
    String sTextBtnOk = textOk[ix2][ix1];
    widgButtonOk.setText(sTextBtnOk);
    //String sTextSrc = textSrc[bLockSrc? 1: 0][ix2];
    //widgButtonCheck.setText(sTextSrc);
    widgButtonCheck.setState(checkDisable);
    widgButtonSetSrc.setState(setSrcPossible ? GralButton.State.On : GralButton.State.Disabled);
    String sTextAbort = textAbort[ix2];
    widgButtonEsc.setText(sTextAbort);
    
  }
  
  

  
  void actionConfirmCopy(){
    
  }

  
  /**Opens the confirm-copy window, prepares the list of src files.
   */
  GralUserAction actionConfirmCopy = new GralUserAction("actionConfirmCopy")
  {
    /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
     */
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { //String sSrc, sDstName, sDstDir;
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        //sCmd: This method was called from a button or with any function key.
        //sCmd describes from where it was called.
        String sCmd = widgi instanceof GralWidget ? ((GralWidget)widgi).getCmd() : "openCopy";
        //
        if(state == Estate.inactive){
          setTexts(Estate.finit);
        } ////
        //if(state.equals("close") || state.equals("check")) {
        //if(state == Estate.start || state == Estate.checked || state == Estate.finit || state == Estate.error) {
        if(!widgButtonSetSrc.isDisabled()) {
          //only if it is ready to check, get the files.
          bFirstSelect = true;
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
          if(lastFileCards[0] !=null){ ///
            fileCardSrc = lastFileCards[0];
            fileCardDst = lastFileCards[1];
            
            //widgButtonDst.setState(GralButton.kOff);  //maybe disabled, set off.
            List<FileRemote> listFileSrc = fileCardSrc.getSelectedFiles(true, 1);
            //String sDirSrc;
            if(listFileSrc == null){
              widgShowSrc.setText("??? listFileSrc==null");
              
            } else {
              if(listFileSrc.size() ==1){
                //only one file is selected:
                fileSrc = listFileSrc.get(0);
                dirSrc = fileSrc.getParentFile();
                sFilesSrc = "";  //only one file, 
                widgShowSrc.setText(fileSrc.getAbsolutePath());
                if(fileSrc.isDirectory()) {
                  sFileDstCopy = "*";
                } else {
                  sFileDstCopy = fileSrc.getName();  //name of source file as default for destination.
                }
              } else {
                //more as one file:
                sFileDstCopy = "*";
                StringBuilder uFileSrc = new StringBuilder();
                dirSrc = fileSrc = fileCardSrc.currentDir();
                String sSep = "";
                for(FileRemote srcFile : listFileSrc){
                  srcFile.resetMarked(1);
                  uFileSrc.append(sSep).append(srcFile.getName());
                  sSep = " : "; //For next one.
                  //FileRemote fileSrc = FileRemote.fromFile(srcFile);
                  //filesToCopy.add(fileSrc);
                }
                sFilesSrc = uFileSrc.toString();
                uFileSrc.insert(0, " : ").insert(0, fileSrc.getAbsolutePath());
                widgShowSrc.setText(uFileSrc);
              }
            }
            //sDstName = listFileSrc.size() >1 ? "*" 
            //           : listFileSrc.size() ==1 ? listFileSrc.get(0).getName() : "??";
            //sSrc = fileSrc.getAbsolutePath() + "/" + sDstName;
            if(fileCardDst !=null){
              dirDst = fileCardDst.currentFile();
              if(dirDst !=null) {
                if(!dirDst.isDirectory()) {
                  dirDst = dirDst.getParentFile();  //a file selected, use the directory of the panel.
                } else {
                  //dirDst.getPathChars(bufferDstChars);
                }
                bufferDstChars = dirDst.getPathChars(bufferDstChars).append('/').append(sFileDstCopy);
              } else {
                //not active filecard, only favorcard selected.
                bufferDstChars.setLength(0); bufferDstChars.append("?? currFile==null");
              }
              //dirDstCmpr = fileCardDst.currentFile();  //should be a directory, check later before compare.
              //sDstDir = dirDst.getAbsolutePath();
            } else {
              dirDst = null;
              bufferDstChars.setLength(0); bufferDstChars.append("??");
              //sDstDir = "??";
            }
            //bLockSrc = true;
            //widgFromConditions.setText("");
          } else { //FileCard not found:
            fileSrc = null;
            dirDst = null;
            bufferDstChars.setLength(0); bufferDstChars.append("??");
            
            //listFileSrc = null;
            //sSrc = "???";
            //sDstName = "??";
            //sDstDir = "??";
            widgShowSrc.setText("??");
          }
          /*
          String sTextDst = widgCopyDirDst.getText().trim();
          if(!sCmd.equals("setSrc") || sTextDst.length() == 0){
            //set the destination only if it is not set on button setSrc
            widgCopyDirDst.setText(sDstDir);
            widgCopyDirDst.setBackColor(colorNoChangedText, 0);
            bDstChanged = false;
            widgCopyNameDst.setText(sDstName);
          }
          */
          boolean setVisible = true; //state == Estate.inactive;
          bDstChanged = false;
          widgInputDst.setBackColor(colorNoChangedText, 0);
          widgInputDst.setText(bufferDstChars);
          setTexts(Estate.start);
          //widgButtonOk.setText("check");
          //widgButtonOk.setCmd("check");
          widgCopyState.setText("check?", 0);
          if(setVisible){
            windConfirmCopy.setVisible(true);
            //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
            main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");
          }
          //widgButtonMove.setValue(GralMng_ifc.cmdSet, 0, 0);
          zFiles = zBytes = 0;
        }
        windConfirmCopy.setVisible(true);
        //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
        main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");
      }
      return true;
   }  };
  
  

   /**Opens the confirm-copy window, prepares the list of src files.
    * It is Key F5 for copy command from the classic NortonCommander.
    * The OK-key is designated to "check". On button pressed the {@link #actionButtonCopy} is called,
    * with the "check" case.
    */
   GralUserAction actionCheck = new GralUserAction("actionCheck")
   {
     /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){ 
         if(!widgButtonCheck.isDisabled()){
         //if(state == Estate.start || state == Estate.checked) { //widgg.sCmd.equals("check")){    
           execMark();
         }
       }
       return true;
     }
   };
   

  
  protected GralUserAction actionSetDst = new GralUserAction("actionSetDst") ///
  { @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        GralButton widgb = (GralButton)(widgP);
        FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
        fileCardDst = lastFileCards[0];
        dirDst = fileCardDst.currentDir();
        CharSequence sText = dirDst.getAbsolutePath();
        if(sFilesSrc == null || sFilesSrc.isEmpty()){
          StringBuilder u = new StringBuilder(sText);
          sText = u.append("/").append( fileSrc.getName());
        }
        widgInputDst.setText(sText);
      }
      return true;
    }
  };
  
  
  protected GralUserAction actionEnterTextInDst = new GralUserAction("actionSelectMask")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { if(key == KeyCode.valueChanged) {
        String text = widgInputDst.getText();
        if(StringFunctions.compare(text, bufferDstChars) ==0) {
          //unchanged text or the original text again:
          widgInputDst.setBackColor(colorNoChangedText, 0);
          bDstChanged = false;
        } else {
          widgInputDst.setBackColor(colorChangedText, 0);
          bDstChanged = true;
        }
      } else {
        Debugutil.stop();
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
          widgdChoiceOverwrReadOnly.setText("yes ?no ?ask");
          widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyReadOnlyNever: 
          modeOverwrReadonly= FileRemote.modeCopyReadOnlyAks; 
          widgdChoiceOverwrReadOnly.setText("ask ?yes ?no");
          widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
          break;
        default:
          modeOverwrReadonly= FileRemote.modeCopyReadOnlyNever; 
          widgdChoiceOverwrReadOnly.setText("no ?ask ?yes");
          widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lrd"), 0);
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
          widgdChoiceOverwrExists.setText("newer ?older?all ?no?ask");
          widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lpk"),0);
          break;
        case FileRemote.modeCopyExistNewer: 
          modeOverwrDate= FileRemote.modeCopyExistOlder; 
          widgdChoiceOverwrExists.setText("older ?all ?no?ask?newer");
          widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lbl"),0);
          break;
        case FileRemote.modeCopyExistOlder:
          modeOverwrDate= FileRemote.modeCopyExistAll; 
          widgdChoiceOverwrExists.setText("all ? no?ask?newer?older");
          widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lgn"),0);
          break;
        case FileRemote.modeCopyExistAll:
          modeOverwrDate= FileRemote.modeCopyExistSkip; 
          widgdChoiceOverwrExists.setText("no ?ask?newer?older?all");
          widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lrd"),0);
          break;
        default:
          modeOverwrDate= FileRemote.modeCopyExistAsk; 
          widgdChoiceOverwrExists.setText("ask ?newer?older?all ?no");
          widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"),0);
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
          widgdChoiceCreateNew.setText("yes ?no ?ask");
          widgdChoiceCreateNew.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyCreateYes: 
          modeCreateCopy = FileRemote.modeCopyCreateNever; 
          widgdChoiceCreateNew.setText("no ?ask ?yes");
          widgdChoiceCreateNew.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        default:
          modeCreateCopy = FileRemote.modeCopyCreateAsk; 
          widgdChoiceCreateNew.setText("ask ?yes ?no");
          widgdChoiceCreateNew.setBackColor(GralColor.getColor("lam"), 0);
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
   GralUserAction actionButtonCmprDelMove = new GralUserAction("actionButtonDel")
   {
     /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         setTexts(state);
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
          if(cmd == Ecmd.delete){
            execMark();
          } else if(cmd == Ecmd.copy || bFineSelect){
            execCheck();
          } else if(cmd == Ecmd.move){
            execMove();
          } else if(cmd == Ecmd.compare){
            execCompare();
          }
        } else if(state == Estate.checked) { //widgg.sCmd.equals("copy")) {
          switch(cmd){
            case copy: execCopy(); break;
            case move: execMove(); break; 
            case delete: execDel(); break;
            case compare: execCompare(); break;
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
          case start: {
            setTexts(Estate.finit);
          } break;
          case inactive:
          case error:
          case finit: closeWindow(); break;
          case quest:
          case busy: abortCopy(); break;
          case checked: abortCopy(); break;
          case busyCheck: abortCopy(); break;
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
  
  

  protected GralUserAction actionSelectMask = new GralUserAction("actionSelectMask")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { String sContent = ((GralTextField_ifc)widgi).getText();
      boolean empty = sContent.isEmpty();
      System.out.println("actionSelectMask");
      if(empty == bFineSelect){
        bFineSelect = !empty;
        setTexts(state);
      }
      return false;
    }
  };

  
  
  GralUserAction XXXactionSwitchButtonMove = new GralUserAction("actionButtonMoveCopy")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(widgButtonMove.isOn()){
          widgButtonOk.setText("move");
          //widgButtonOk.setCmd("copy");
        } else {
          if(widgCopyState.getText().equals("check")){
            widgButtonOk.setText("check");
            //widgButtonOk.setCmd("check");
          } else {
            widgButtonOk.setText("copy");
            //widgButtonOk.setCmd("copy");
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
          //widgButtonOk.setCmd("copy");
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
        //widgButtonOk.setCmd("quit");
      } else {
        widgButtonOk.setText("close");
        //widgButtonOk.setCmd("close");
      }
      widgButtonEsc.setText("close");
      //widgButtonEsc.setCmd("close");
      windConfirmCopy.setVisible(false);      
    }
  }
  
  
  
  
  /**Instance of an event consumer which is used for all callback actions from the FileRemote machine.
   * The given event contains a command which depends on the action which is done by the file state machine or the file process.
   * Especially
   * <ul>
   * <li>{@link FileRemote.CallbackCmd#nrofFilesAndBytes}: shows the progress of the action. 
   * </ul>
   */
  EventConsumer callbackFromFileMachine = new EventConsumer(){
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
          setTexts(Estate.checked);
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
          setTexts(Estate.error);
          eventConsumed(ev, false);
        }break;
        case nok: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("nok");
          setTexts(Estate.error);
          eventConsumed(ev, false);
        }break;
        case done: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("ok: " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
          eventConsumed(ev, true);
          setTexts(Estate.finit);
        }break;
        default:
          FcmdCopyCmd.this.evCurrentFile = ev1;

      }
      //windConfirmCopy.setWindowVisible(false);
      return 1;
    }
    @Override public String toString(){ return "FcmdCopy-success"; }

  };

  
  
  FileRemoteCallback callbackFromFilesCheck = new FileRemoteCallback() {
    @Override public void start(FileRemote startDir) {  }
    @Override public Result offerDir(FileRemote file) {
      return Result.cont;      
    }
    
    @Override public Result finishedDir(FileRemote file, FileRemoteCallback.Counters cnt) {
      return Result.cont;      
    }
    
    

    @Override public Result offerFile(FileRemote file) {
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(long nrofBytes, int nrofFiles) {  
      FcmdCopyCmd.this.zFiles = nrofFiles;
      FcmdCopyCmd.this.zBytes = nrofBytes;
      widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
      setTexts(Estate.checked);
    }


  };
  
  
  
  FileRemoteCallback callbackFromFilesExec = new FileRemoteCallback() {
    @Override public void start(FileRemote startDir) {  }
    
    @Override public Result offerDir(FileRemote file) {
      return Result.cont;      
    }
    
    @Override public Result finishedDir(FileRemote file, FileRemoteCallback.Counters cnt) {
      return Result.cont;      
    }
    
    

    @Override public Result offerFile(FileRemote file) {
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(long nrofBytes, int nrofFiles) {  
      widgCopyState.setText("ok: " + nrofBytes/1000000 + " M / "  + nrofFiles + " Files");
      setTexts(Estate.finit);
    }


  };
  
  
  enum Estate{ inactive, start, checked, busyCheck, busy, quest, error, finit};
  
  enum Ecmd{ copy, move, delete, compare};
  
  void stop(){}
  
}
