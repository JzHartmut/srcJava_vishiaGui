package org.vishia.commander;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.vishia.event.EventCmdPingPongType;
import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.event.TimeOrderBase;
import org.vishia.fileRemote.FileMark;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteCallback;
import org.vishia.fileRemote.FileRemoteCallbackCmp;
import org.vishia.fileRemote.FileRemoteProgressTimeOrder;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.SortedTreeWalkerCallback;
import org.vishia.util.StringFormatter;
import org.vishia.util.StringFunctions;


/**Base class for initializing some class variables before the variables with only code in the class body are executed. */
class FcmdFileActionBase {

  /**The command which is given with that window. */
  final FcmdCopyCmprDel.Ecmd cmdWind;
  
  
  FcmdFileActionBase(FcmdCopyCmprDel.Ecmd whatisit){
    cmdWind = whatisit;  
  }
}


/**This class contains all functionality to execute copy, move, compare and delete for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public final class FcmdCopyCmprDel extends FcmdFileActionBase
{
  /**Version and History
   * <ul>
   * <li>2015-01-03 Hartmut refactory
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
  
  
  String sTextExecuteForFile = (cmdWind == Ecmd.delete) ? "del file" : "overwr file";
  
  protected final Fcmd main;

  GralWindow_ifc windConfirmCopy;
  
  GralPos posWindConfirmCopy;
  
  //GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralTextField widgShowSrc, widgFromConditions;
  
  GralButton widgButtonModeDst;
  
  GralTextField widgInputDst, widgCopyState;
  
  GralTextField widgCopyDirDst, widgCopyNameDst;  //TODO with progress
  
  GralButton widgdChoiceCreateNew, widgdChoiceOverwrExists, widgdChoiceOverwrReadOnly;
  
  GralButton widgOverwrFile, widgSkipFile, widgSkipDir, widgBtnPause;
  
  
  GralButton widgState, widgButtonSetSrc, widgButtonSetDst, widgButtonCheck, widgButtonMove, widgButtonOk;

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
  FileRemote srcDir, srcFile;
  
  boolean srcSomeFiles;
  
  /**If more as one files are selected to copy, the names are contained here separated with ' : '.
   * Elsewhere it is "*". */
  String sFilesSrc;
  
  /**The destination selected directory and file as destination for compare and move or second tree for comparison. 
   * The fileDst is build with the name of the source file for copy and move and with the real dst file for compare
   */
  FileRemote fileDst, dirDst; //, dirDstCmpr;

  /**Name of the file for dst. */
  CharSequence sFileDstCopy;
  
  StringBuilder bufferDstChars = new StringBuilder(100);
  
  StringFormatter formatShow = new StringFormatter(100);
  
  /**If true then the {@link #widgInputDst} was changed for this session. Not automatically change the content. */
  boolean bDstChanged;

  boolean bFirstSelect;
  
  /**Content from the input fields while copy is pending. */
  //String sDstDir, sDstName;
  
  long zBytes;
  int zFiles;
  
  FileRemote fileProcessed, dirProcessed;
  
  
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
  
  
  
  EventSource evSrc = new EventSource("FcmdCopy"){
    
  };
  

  
  
  /**This reference is set with the callback of operation cmd. 
   * The event can be used to affect the copying process.
   */
  FileRemote.CallbackEvent evCurrentFile;
  
  Actions action;
  
  FcmdCopyCmprDel(Fcmd main, Ecmd cmdArg)
  { super(cmdArg);
    this.main = main;
  }
  
  
  /**Last files which are in copy process. Typical contains one file only. 
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();
  
  
  
  /**Builds the content of the confirm-copy window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmCopy(String sTitle)
  {
    action = new Actions();
    main.gralMng.selectPanel("primaryWindow"); //"output"); //position relative to the output panel
    //System.out.println("CopyWindow frame: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    //panelMng.setPosition(1, 30+GralGridPos.size, 1, 40+GralGridPos.size, 1, 'r');
    main.gralMng.setPosition(10, GralPos.size+37, 10, GralPos.size+70, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    int windprops = GralWindow.windConcurrently; // + GralWindow.windResizeable;
    windConfirmCopy = main.gralMng.createWindow("copyWindow", sTitle, windprops);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    //source path and check:
    if(cmdWind != Ecmd.delete) {
      main.gralMng.setPosition(0.5f, GralPos.size +2, 1, 7.5f, 0, 'r', 0);
      widgButtonModeDst = main.gralMng.addSwitchButton(null, "dst/..", "dst/dst", GralColor.getColor("gn"), GralColor.getColor("ye") );
      widgButtonModeDst.setActionChange(actionChgModeDst);
    }
    main.gralMng.setPosition(0.5f, GralPos.size +2.5f, 15, GralPos.size+12, 0, 'r', 1);
    widgButtonSetSrc = main.gralMng.addButton(null, actionConfirmCopy, "setSrc", null, "set source" );
    widgButtonSetSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.setSrc.");
    widgButtonClearSel = main.gralMng.addButton(null, null, "clrSel", null, "clear selection" );
    widgButtonClearSel.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.clearSel.");
    if(cmdWind != Ecmd.delete) {
      main.gralMng.setPosition(GralPos.same, GralPos.samesize, -17, -1, 0, 'r', 1);
      widgButtonSetDst = main.gralMng.addButton(null, actionSetDst, "setDst", null, "set destination" );
      widgButtonSetDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.setDst.");
    }
    main.gralMng.setPosition(2.5f, GralPos.size +3.2f, 1, -4, 0, 'd', 0);
    //main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgShowSrc = main.gralMng.addTextField("copyFrom", false, "source root path", "t");
    widgShowSrc.setBackColor(GralColor.getColor("am"),0);
    widgShowSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.pathSrc.");
    main.gralMng.setPosition(GralPos.refer+0.9f, GralPos.size +2.5f, -4f, -1, 0, 'r', 1);
    widgButtonShowSrc = main.gralMng.addButton(null, null, "showSrc", null, "=>" );
    widgButtonShowSrc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.show.");
    
    
    main.gralMng.setPosition(GralPos.refer + 2.3f, GralPos.size +3.2f, 1, -13, 0, 'r', 0.3f);
    widgFromConditions = main.gralMng.addTextField("copyCond",true, "select src files: mask*:*.ext / 2012-08-05..06", "t");
    widgFromConditions.setActionChange(actionSelectMask);
    widgFromConditions.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.selcond.");
    
    main.gralMng.setPosition(GralPos.refer+0.2f, GralPos.size+3.5f, -13, -1, 0, 'd', 0);
    widgButtonCheck = main.gralMng.addButton("buttonCheck", actionCheck, "check", null, "check" );
    widgButtonCheck.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.check.");
    
    //dst path, set dst
    if(cmdWind != Ecmd.delete) {
      main.gralMng.setPosition(GralPos.refer+3.0f, GralPos.size -3.2f, 1, -4, 0, 'r', 0);
      widgInputDst = main.gralMng.addTextField("copyDirDst", true, "destination:", "t");
      widgInputDst.setActionChange(actionEnterTextInDst);
      widgInputDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.setDst.");
      main.gralMng.setPosition(GralPos.refer+0.9f, GralPos.size +2.5f, -4, -1, 0, 'r', 1);
      widgButtonShowDst = main.gralMng.addButton(null, null, "showDst", null, "=>" );
      widgButtonShowDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.show.");
    }  
    
    main.gralMng.setPosition(GralPos.refer+1.5f, GralPos.size -1.5f, 1, 18, 0, 'r', 1);
    if(cmdWind == Ecmd.delete) {
      main.gralMng.addText("Del read only ?");
    } else if(cmdWind == Ecmd.compare) {
      //nothing such
    } else {
      main.gralMng.addText("Overwr read only ?");
      main.gralMng.addText("Overwr exists ?");
      main.gralMng.addText("Create ?");
    }
    if(cmdWind != Ecmd.compare) {
      main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, GralPos.size +12, 0, 'r',1);
      widgdChoiceOverwrReadOnly = main.gralMng.addButton("overwritero", actionOverwrReadonly, "overwritero", null,"ask ?yes ?no");
      widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
      widgdChoiceOverwrReadOnly.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.ctrl.overwrRo.");
    }
    if(cmdWind != Ecmd.delete && cmdWind != Ecmd.compare) {
      main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +20, 0, 'r',1);
      widgdChoiceOverwrExists = main.gralMng.addButton("copyOverwriteReadonly", actionOverwrDate, "copyOverwriteReadonly", null, "ask ?newer?older?all ?no");
      widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"), 0);
      widgdChoiceOverwrExists.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.ctrl.overwrExists.");
      main.gralMng.setPosition(GralPos.same, GralPos.size -3, GralPos.next, GralPos.size +12, 0, 'r',1);
      widgdChoiceCreateNew = main.gralMng.addButton("copyCreate", actionCreateCopy, "copyCreate", null, "yes ?no ?ask");
      widgdChoiceCreateNew.setBackColor(GralColor.getColor("lgn"), 0);
      widgdChoiceCreateNew.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.ctrl.createNew.");
    }
    
    //field for showing the current action or state, not for input:
    //field for showing the current name, not for input:
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -1, 0, 'd');
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", false, "current directory:", "t");
    widgCopyDirDst.setBackColor(GralColor.getColor("lam"),0);
    widgCopyDirDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.currfile.");
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", false, "current file:", "t");
    widgCopyNameDst.setBackColor(GralColor.getColor("lam"),0);
    widgCopyNameDst.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.currfile.");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size +3.2f, 1, -15, 0, 'd', 0.3f); //same line as del
    widgCopyState = main.gralMng.addTextField("copyStatus", false, "current state:", "t");
    widgCopyState.setBackColor(GralColor.getColor("lam"),0);
    widgCopyState.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.state.");
    
    main.gralMng.setPosition(-4, GralPos.size +1, 10, -14, 0, 'd', 1);
    if(cmdWind != Ecmd.delete) {
      //widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null);
    }
    //widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null);

    if(cmdWind == Ecmd.copy) {
      main.gralMng.setPosition(-8, GralPos.size+2.5f, -13f, -1, 0, 'd', 0.3f);
      widgButtonMove = main.gralMng.addSwitchButton("copyMove", "move ?", "Move/ ?copy", GralColor.getColor("wh"), GralColor.getColor("lgn"));
      widgButtonMove.setActionChange(actionButtonCmprDelMove);
      widgButtonMove.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.fn.");
    }
    main.gralMng.setPosition(-4, GralPos.size+3.2f, 1, 9, 0, 'r');
    widgButtonEsc = main.gralMng.addButton("copyEsc", actionButtonAbort, "esc", null, "close");
    widgButtonEsc.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.escape.");
    main.gralMng.setPosition(GralPos.same, GralPos.size +2.5f, GralPos.next, GralPos.size+3.0f, 0, 'r', 1);
    widgState = main.gralMng.addButton(null, actionShowState, "?", null, "?" );
    widgState.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.state.");
    
    if(cmdWind != Ecmd.compare){
      //main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 12, 0, 'r', 1);
      main.gralMng.setPosition(GralPos.same, GralPos.size +2.5f, GralPos.next, GralPos.size + 11, 0, 'r', 1);
      //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 0, 'r', 1);
      widgSkipDir = main.gralMng.addButton("copySkipDir", actionButtonSkipDir, "copySkipDir", null, "skip dir");
      widgSkipDir.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.quest.skipDir.");
      widgSkipFile = main.gralMng.addButton("copyskip", actionButtonSkipFile, "copyskip", null, "skip file");
      widgSkipFile.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.quest.skipFile.");
      widgOverwrFile = main.gralMng.addButton(sTextExecuteForFile, actionOverwriteFile, "copyOverwrite", null, sTextExecuteForFile);
      widgOverwrFile.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.quest.wr.");
      //widgBtnPause = main.gralMng.addButton("pause", null, "pause", null, "pause");
      //widgBtnPause.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.pause.");
    }  
    main.gralMng.setPosition(-4, GralPos.size+3.5f, -13f, -1, 0, 'd', 0.4f);
    widgButtonOk = main.gralMng.addButton("copyOk", actionButtonOk, "close", null, "close");
    widgButtonOk.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.ok.");
  
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
  
  
  
  
  
  /**Last files which are in copy process. Typical contains one file only. 
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();
  
  
  
  /**All mode bits of the 3 variables.*/
  private int modeCopy(){
    int mode = modeCreateCopy | modeOverwrReadonly |modeOverwrDate;
    return mode;            
  }


  /**Starts the execution of mark in another thread. Note that the mark works with the walk-file algorithm
   * and refreshes the files therewith. 
   * See {@link FileRemote#refreshAndMark(int, boolean, String, int, int, FileRemoteCallback)}.
   */
  final void execMark(){
    setTexts(Estate.busyCheck);
    widgCopyState.setText("busy-check");
    widgButtonOk.setText("busy-check");
    widgButtonCheck.setState(GralButton.State.Disabled);
    widgButtonEsc.setText("abort");
    String sSrcMask= widgFromConditions.getText();
    //FileSystem: acts in other thread.
    //regards mark in first level ?
    int depths = srcSomeFiles ? -Integer.MAX_VALUE : Integer.MAX_VALUE;
    //====>
    srcFile.refreshAndMark(depths, bFirstSelect, sSrcMask, FileMark.select, 0, callbackFromFilesCheck, action.showFilesProcessing);
    widgCopyNameDst.setText(srcDir.getStateDevice());
    bFirstSelect = false;
  }
  
  
  final protected void execDel(){
    if(state == Estate.checked){
      srcFile.deleteMarkedInThread(FileMark.select, callbackFromFilesExec);      
    } else if(state == Estate.start){
    }
  }
  
  
  
  /**Starts the execution of copy in another thread. Note that the compy works with a state machine which uses
   * the found files in the {@link FileRemote#children()} tree. 
   * See {@link FileRemote#copyChecked(String, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execCopy(){
    widgCopyState.setText("busy-copy");
    widgButtonOk.setText("busy-copy");
    widgButtonEsc.setText("abort");
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
      sDstDir = srcDir.getAbsolutePath() + "/" + sDstDir;
    }
    //====>
    srcFile.copyChecked(sDstDir, sDstMask, modeCopy(), null, action.showFilesProcessing);  //, evCallback);
    //String stateCopy = copyStates.getStateInfo();
    setTexts(Estate.busy);
    //widgCopyState.setText("State: " + stateCopy);
  } 
  
  
  /**Starts the execution of compare in another thread. Note that the compare works with the walk-file algorithm
   * and refreshes the files therewith. See {@link FileRemote#refreshAndCompare(FileRemote, int, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execCompare(){
    String sDst = widgInputDst.getText();
    int posSep = sDst.lastIndexOf('/');
    int posSep2 = sDst.lastIndexOf('\\');
    if(posSep2 >=0 && posSep2 > posSep){ posSep = posSep2; }
    int posWildcard = sDst.indexOf('*');
    String sDstDir;
    if(posWildcard > posSep){
      sDstDir = sDst.substring(0, posSep);
    } else {
      //no asterisk, either it is one file or it is the destination dir:
      sDstDir = sDst;
    }
    FileRemote fileDst;
    if(FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = srcDir.child(sDstDir);  //relative to source
    }
    if(fileDst.isDirectory()){ dirDst = fileDst; } 
    else { dirDst = fileDst.getParentFile(); } 
    boolean bOk = true;
    //check whether the event is able to occupy, use it to check.
    if(evCallback.occupyRecall(100, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
      if(evCallback.occupyRecall(1000, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
        System.err.println("FcmdCopyCmd event occupy hangs");
        bOk = false;
      }
    }
    if(bOk) {
      String sSrcMask= widgFromConditions.getText();
      evCallback.sendEvent(FileRemote.CallbackCmd.start);  //sends to myself for showing the state, 
      //it is a check of sendEvent and it should relinguish the event.
      //====>
      srcFile.refreshAndCompare(dirDst, 0, sSrcMask, 0, null, action.showFilesProcessing); //evCallback); //evCallback able to use from callback.
      //setTexts(Estate.busy);
    } else {
      widgCopyState.setText("evCallback hangs");
      setTexts(Estate.quest);
    }
    ///
  }
  
  
  final protected void execMove() {
    String sDstDir = widgInputDst.getText();
    FileRemote fileDst;
    if(FileSystem.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = srcDir.child(sDstDir);  //relative to source
    }
    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, srcFile, null, evConsumerCallbackFromFileMachine, null, evSrc);
    //====>
    srcFile.moveTo(sFilesSrc, fileDst, callback);
    setTexts(Estate.busy);
    ///
  }
  
  final protected void abortCopy(){
    //if(fileSrc !=null ) {fileSrc.abortAction();}
    for(FileRemote.CallbackEvent ev: listEvCopy){
      ev.copyAbortAll();
      //ev.sendEvent(FileRemote.cmdAbortAll);
    }
    String sDirSrc = widgShowSrc.getText();
    //FileRemote dirSrc = main.fileCluster.getFile(sDirSrc, null); //new FileRemote(sDirSrc);
    if(srcDir !=null) { srcDir.abortAction(); }  //to set stateMachine of copy in ready state 
    else {
      System.err.println("FcmdCopyCmd: abort, dirSrc not given.");
    }
    listEvCheck.clear();
    listEvCopy.clear();
    filesToCopy.clear();
    FcmdCopyCmprDel.this.evCurrentFile = null;
    //bLockSrc = false;
    //widgButtonOk.setText("close");
    //widgButtonOk.setCmd("close");
    setTexts(Estate.finit);
  }
  
  
  /**Sets the source and destination depending on the cmd and given {@link #srcFile}, {@link #dirDst}
   * The {@link #widgInputDst} will be not changed if {@link #bDstChanged} is set.
   */
  final void setTextSrcDst(){
    //
    //show source files, it is only to show, no functionality.
    if(srcFile == null){
      widgShowSrc.setText("--no source--");
    } else {
      if(sFilesSrc == null) {
        if(srcFile.isDirectory() && cmd == Ecmd.copy) {
          if(srcSomeFiles){
            widgShowSrc.setText(srcFile.getAbsolutePath() + "/?+");  //copy some files dir/?+ signals, there are more files but add mask into
          } else {
            widgShowSrc.setText(srcFile.getAbsolutePath() + "/*");  //copy a directory: show dir/* to signal, there are more files.
          }
        } else {
          widgShowSrc.setText(srcFile.getAbsolutePath());         //compare, move or copy only one file: show only srcfile or srcdir
        }
      } else {
        widgShowSrc.setText(srcFile.getAbsolutePath() + "/" + sFilesSrc);  //more as one 
      }
    }
    //
    if(cmdWind != Ecmd.delete) {
      if(dirDst == null){
        bufferDstChars.setLength(0); bufferDstChars.append("??");
      } else if(!bDstChanged) {
        if(cmd == Ecmd.compare) {
          bufferDstChars.setLength(0); bufferDstChars.append(fileDst.getPathChars()); //.append('/').append(sFileDstCopy);
        } else {
          bufferDstChars.setLength(0); 
          final FileRemote dstSet;
          if(!widgButtonModeDst.isOn() || srcFile.isDirectory() && !fileDst.isDirectory()){
            dstSet = dirDst;
            bufferDstChars.append(dirDst.getPathChars()); //the directory of the file.
          } else {
            //select "dst/dst" in mode or 
            dstSet = fileDst;
            bufferDstChars.append(fileDst.getPathChars()); //file or directory
          }
          //if(cmd == Ecmd.copy || cmd == Ecmd.move){
          //if(dirDst == fileDst || fileSrc.isDirectory() && !fileDst.isDirectory()) {  // .. is selected
          if(dstSet.isDirectory()) {  // .. is selected
            if(srcSomeFiles){
              bufferDstChars.append("/*");  
            } else {
              String nameSrc = srcFile.getName();
              bufferDstChars.append('/').append(nameSrc);
            }
          }
          if(cmd == Ecmd.copy && srcFile != null && srcFile.isDirectory()) {
            bufferDstChars.append("/*");
          }
        }
      }
      widgInputDst.setText(bufferDstChars);
    }
    
  }


  /**Set the texts to any widgets depending on the state of execution and the activated switch key.
   * @param newState
   */
  void setTexts( Estate newState) {  ////
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
    
    int ix1;
    if(cmdWind == Ecmd.delete){ 
      ix1=0; 
      cmd = Ecmd.delete; 
      if(cmdWind != Ecmd.delete) {
        widgButtonMove.setState(GralButton.State.Off);
        widgInputDst.setEditable(false);
        widgInputDst.setBackColor(colorGrayed, 0);
      }
    } else if(cmdWind == Ecmd.compare){ 
      ix1 = 3; 
      cmd = Ecmd.compare; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    } else if(widgButtonMove.isOn()) { 
      ix1 = 1; 
      cmd = Ecmd.move; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    } else { 
      ix1 = 2; 
      cmd = Ecmd.copy; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    }
    if(cmdWind != Ecmd.delete) {
      widgInputDst.setPrompt(textDest[ix1]);
    }
    if(!bDstChanged) {
      setTextSrcDst();
    }
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
  
  
  
  /**Invoked with a time order.
   * 
   */
  void showCurrentProcessedFileAndDir(FileRemoteProgressTimeOrder order) //FileRemote fileProcessed, int zFiles, boolean bDone) {
  { StringBuilder u = new StringBuilder(100);
    if(order.currFile !=null) {
      widgCopyDirDst.setText(order.currFile.getParent());
      widgCopyNameDst.setText(order.currFile.getName());
    } else {
      widgCopyDirDst.setText("?");
      widgCopyNameDst.setText("?");
    }
    if(order.bDone) {
      widgCopyState.setText("ok: "); // + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
      setTexts(Estate.finit);
    }
    if(order.quest() !=null) {
      switch(order.quest()){
        case askDstOverwr: 
          u.append("overwrite file? "); 
          widgOverwrFile.setBackColor(GralColor.getColor("lng"), 0);
          widgSkipFile.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case askDstNotAbletoOverwr: 
          u.append("cannot overwrite file? "); 
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        case askErrorCopy: 
          u.append("error copy? "); 
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        case askErrorDstCreate: 
          u.append("cannot create? "); 
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        case askDstReadonly: 
          u.append("overwrite readonly file? "); 
          widgOverwrFile.setBackColor(GralColor.getColor("lng"), 0);
          widgSkipFile.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case askErrorSrcOpen: 
          u.append("cannot open sourcefile "); 
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        case done:       
          u.append("ok: "); // + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
          widgOverwrFile.setBackColor(GralColor.getColor("lgr"), 0);
          widgSkipFile.setBackColor(GralColor.getColor("lgr"), 0);
          setTexts(Estate.finit);
          break;
        
      }
    }
    u.append("; Files:").append(Integer.toString(zFiles)).append(": ");
    widgCopyState.setText(u.toString());
  }
  
  
  void showFinishState(CharSequence start, SortedTreeWalkerCallback.Counters cnt)
  {
    FcmdCopyCmprDel.this.zFiles = cnt.nrofLeafss;
    FcmdCopyCmprDel.this.zBytes = cnt.nrofBytes;
    formatShow.reset();
    formatShow.add(start);
    formatShow.add(" Files:").addint(cnt.nrofLeafSelected, "3333333331");
    //StringBuilder u = new StringBuilder();
    //u.append("Files:").append(Integer.toString(cnt.nrofLeafSelected));
    if(cnt.nrofLeafSelected != cnt.nrofLeafss){  //u.append(" (").append(Integer.toString(cnt.nrofLeafSelected)).append(")"); }
      formatShow.add(" /").addint(cnt.nrofLeafss, "3333333331");
    }
    if(cnt.nrofBytes > 1000000){
      formatShow.addint(cnt.nrofBytes/1000, ", 33331.111 MByte");
    }
    else if(cnt.nrofBytes > 1000){
      formatShow.addint(cnt.nrofBytes, ", 331.111 kByte");
    } else {
      formatShow.addint(cnt.nrofBytes, ", 331 Byte");
    }
    widgCopyState.setText(formatShow.toString());
  }
  
  void actionConfirmCopy(){
    
  }

  
  /**Will be initialized if the main.gralMng is available.
   */
  private class Actions
  {
    
    @SuppressWarnings("serial") 
    FileRemoteProgressTimeOrder showFilesProcessing = 
        new FileRemoteProgressTimeOrder("showFilesProcessing", main.gralMng.gralDevice.orderList(), 100) 
    {
      @Override public void executeOrder() { 
        showCurrentProcessedFileAndDir(this); //this.currFile, this.nrFilesProcessed, this.bDone); 
        this.currFile = null;  //to invoke second time.
      }
    };
  }  

  /**Shows the state of FileRemote, especially for debug and problems. */
  GralUserAction actionShowState = new GralUserAction("actionConfirmCopy")
  {
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(srcFile !=null) { widgCopyState.setText(srcFile.getStateDevice()); }
        else { widgCopyState.setText("no source file"); }
      }
      return true;
    }
  };
  
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
        } 
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
              //widgShowSrc.setText("??? listFileSrc==null");
              
            } else {
              if(listFileSrc.size() ==1){
                //only one file is selected:
                srcFile = listFileSrc.get(0);
                srcDir = srcFile.getParentFile();
                srcSomeFiles = false;
                sFilesSrc = null;  //only one file, 
                //widgShowSrc.setText(fileSrc.getAbsolutePath());
                if(srcFile.isDirectory()) {
                  sFileDstCopy = "*";
                } else {
                  sFileDstCopy = srcFile.getName();  //name of source file as default for destination.
                }
              } else {
                //more as one file:
                sFileDstCopy = "*";
                //StringBuilder uFileSrc = new StringBuilder();
                //fileSrc = fileCardSrc.currentFile();
                srcFile = srcDir = fileCardSrc.currentDir();
                srcDir.setMarked(FileMark.selectSomeInDir);
                srcSomeFiles = true;
                //srcFile = null; 
                sFilesSrc = null;  //only one file, 
                //widgShowSrc.setText(dirSrc.getAbsolutePath() + "/?+");
                /*
                fileSrc = dirSrc 
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
                */
              }
            }
            //sDstName = listFileSrc.size() >1 ? "*" 
            //           : listFileSrc.size() ==1 ? listFileSrc.get(0).getName() : "??";
            //sSrc = fileSrc.getAbsolutePath() + "/" + sDstName;
            if(fileCardDst !=null){
              dirDst = fileCardDst.currentDir();
              fileDst = fileCardDst.currentFile();  //it is ==dirDst if .. is selected.
              /*
              if(dirDst !=null) {
                if(!dirDst.isDirectory()) {
                  dirDst = dirDst.getParentFile();  //a file selected, use the directory of the panel.
                } else {
                  //dirDst.getPathChars(bufferDstChars);
                }
              }
              */
              //dirDstCmpr = fileCardDst.currentFile();  //should be a directory, check later before compare.
              //sDstDir = dirDst.getAbsolutePath();
            } else {
              dirDst = null;
              fileDst = null;
              //sDstDir = "??";
            }
            //bLockSrc = true;
            //widgFromConditions.setText("");
          } else { //FileCard not found:
            srcFile = null;
            dirDst = null;
            fileDst = null;
            
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
          if(widgInputDst !=null) {
            widgInputDst.setBackColor(colorNoChangedText, 0);
          }
          setTexts(Estate.start);
          //widgButtonOk.setText("check");
          //widgButtonOk.setCmd("check");
          widgCopyState.setText("check?", 0);
          if(setVisible){
            windConfirmCopy.setVisible(true);
            //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
            main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.");
          }
          //widgButtonMove.setValue(GralMng_ifc.cmdSet, 0, 0);
          zFiles = 0; zBytes = 0;
        }
        windConfirmCopy.setVisible(true);
        //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
        main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.confileaction.");
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
         /*
         if(sFilesSrc == null || sFilesSrc.isEmpty()){
           StringBuilder u = new StringBuilder(sText);
           sText = u.append("/").append( fileSrc.getName());
         }
         */
         bDstChanged = false;
         setTextSrcDst();
       }
       return true;
     }
   };
   
   
   protected GralUserAction actionChgModeDst = new GralUserAction("actionChgModeDst") ///
   { @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
     { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         setTextSrcDst();
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
   * {@link FileRemote#copyTo(FileRemote, EventCmdPingPongType)} invocation. It is used to callback either from the thread
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
            execMark();
          } else if(cmd == Ecmd.move){
            execMark();
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
        } else {
          //should be state pause
          action.showFilesProcessing.triggerStateMachine(FileRemote.Cmd.docontinue);
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
  protected GralUserAction actionOverwriteFile = new GralUserAction(sTextExecuteForFile)
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(evCurrentFile !=null){
          int modeCopyOper = modeCopy();
          evCurrentFile.copyOverwriteFile(modeCopyOper);
        }
        else if(action.showFilesProcessing.quest() == FileRemote.CallbackCmd.askDstOverwr
             || action.showFilesProcessing.quest() == FileRemote.CallbackCmd.askDstReadonly) {
          action.showFilesProcessing.modeCopyOper = modeCopy();
          action.showFilesProcessing.answer(FileRemote.Cmd.overwr);
          widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
        } else {
          action.showFilesProcessing.triggerStateMachine(FileRemote.Cmd.docontinue);
        }
        //widgCopyNameDst.setText("");
        widgCopyState.setText("skipped");
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
        else if(action.showFilesProcessing !=null) {
          action.showFilesProcessing.modeCopyOper = modeCopy();
          action.showFilesProcessing.answer(FileRemote.Cmd.abortCopyFile);
         
        }
        widgCopyNameDst.setText("");
        widgCopyState.setText("skipped");
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

  
  
  
  
  private void eventConsumed(EventObject evp, boolean ok){
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
      //windConfirmCopy.setVisible(false);      
    }
  }
  
  
  
  
  /**Instance of an event consumer which is used for all callback actions from the FileRemote machine.
   * The given event contains a command which depends on the action which is done by the file state machine or the file process.
   * Especially
   * <ul>
   * <li>{@link FileRemote.CallbackCmd#nrofFilesAndBytes}: shows the progress of the action. 
   * </ul>
   */
  EventConsumer evConsumerCallbackFromFileMachine = new EventConsumer(){
    @Override public int processEvent(EventObject ev)
    {
      FileRemote.CallbackEvent ev1 = (FileRemote.CallbackEvent)ev;
      FileRemote.CallbackCmd cmd = ev1.getCmd();
      String sCmd = cmd.name();
      System.out.println("FcmdCopy - callbackCopy;" + sCmd);
      switch(ev1.getCmd()){
        case start: {
          setTexts(Estate.busy);
        } break;
        case doneCheck:{ ///
          //if(listEvCheck.remove(ev)){  ///
            FcmdCopyCmprDel.this.evCurrentFile = ev1;
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
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          String sPath = StringFunctions.z_StringJc(ev1.fileName);
          widgCopyNameDst.setText(sPath);
        } break;
        case nrofFilesAndBytes:{
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          int percent = ev1.promilleCopiedBytes / 10;
          widgProgressFile.setValue(percent);
          widgProgressAll.setValue(ev1.promilleCopiedFiles / 10);
          widgCopyNameDst.setText(StringFunctions.z_StringJc(ev1.fileName));
          widgCopyState.setText("... " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
        }break;
        case askDstOverwr: {
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("exists: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askDstReadonly: {
            FcmdCopyCmprDel.this.evCurrentFile = ev1;
            widgCopyNameDst.setText("read only: " + StringFunctions.z_StringJc(ev1.fileName));
            widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
            widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askDstNotAbletoOverwr: {
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("can't overwrite: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
        } break;
        case askErrorDstCreate: {
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("can't create: " + StringFunctions.z_StringJc(ev1.fileName));
          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
        } break;
        case askErrorCopy: {
          FcmdCopyCmprDel.this.evCurrentFile = ev1;
          widgCopyNameDst.setText("copy error: " + StringFunctions.z_StringJc(ev1.fileName));
          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
        } break;
        case error: {
          FcmdCopyCmprDel.this.evCurrentFile = null;
          widgCopyState.setText("error");
          setTexts(Estate.error);
          eventConsumed(ev, false);
        }break;
        case nok: {
          FcmdCopyCmprDel.this.evCurrentFile = null;
          widgCopyState.setText("nok");
          setTexts(Estate.error);
          eventConsumed(ev, false);
        }break;
        case done: {
          FcmdCopyCmprDel.this.evCurrentFile = null;
          widgCopyState.setText("ok: " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
          eventConsumed(ev, true);
          setTexts(Estate.finit);
        }break;
        default:
          FcmdCopyCmprDel.this.evCurrentFile = ev1;

      }
      //windConfirmCopy.setWindowVisible(false);
      return 1;
    }
    @Override public String toString(){ return "FcmdCopy-success"; }

    @Override public String getStateInfo(){ return "no-state"; }

    
  };

  
 
  
  /**This event instance with back event and a possible command event as opponent is generally used for invoke and callback to routines
   * for file handling. That methods should be execute in another thread. Only one process can be run for one of this window for
   * mark, move, copy, compare and delete. Therefore only one event instance is necessary which is reused. 
   * For hanging processes there is an "abort" button which sends the abort event at last. Usual the hanging is forced by an
   * Exception while handling files or while communication with an device. In that cases the exception had terminate the other process already,
   * so the abort command for this event is not used and not necessary. But if the process does not hang and it should be aborted
   * that event is essential. 
   * <br><br>
   * If the event will be reused the "abort" was send for a longer time (at least 1 second for manual handling, pressing buttons). 
   * Therefore it can be occupied usual without waiting, at least with thread switch to finish execution of the event.
   * See {@link EventCmdPingPongType#occupyRecall(int, EventSource, EventConsumer, org.vishia.event.EventThread, boolean)}  
   */
  FileRemote.CallbackEvent evCallback = new FileRemote.CallbackEvent(evConsumerCallbackFromFileMachine, null, evSrc); 
  

  
  
  FileRemoteCallback callbackFromFilesCheck = new FileRemoteCallback() {
    @Override public void start(FileRemote startDir) {  
      fileProcessed = null;
    }
    
    @Override public Result offerParentNode(FileRemote file) {
      dirProcessed = file;
      return Result.cont;      
    }
    
    /**Finish a directory, check whether a file panel should be refreshed.
     * @see org.vishia.util.SortedTreeWalkerCallback#finishedParentNode(java.lang.Object, org.vishia.util.SortedTreeWalkerCallback.Counters)
     */
    @Override public Result finishedParentNode(FileRemote dir, FileRemoteCallback.Counters cnt) {
      main.refreshFilePanel(dir);
      return Result.cont;      
    }
    
    

    @Override public Result offerLeafNode(FileRemote file) {
      boolean bShow = (fileProcessed == null);
      //actionShowFilesCmp.fileProcessed = file;
      //actionShowFilesCmp.zFiles +=1;
      if(bShow){
        //actionShowFilesCmp.addToList(widgButtonShowSrc.gralMng().gralDevice.orderList(), 300);
      }
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(FileRemote startFile, SortedTreeWalkerCallback.Counters cnt) {  
      showFinishState("checked ", cnt);
      main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
      setTexts(Estate.checked);
    }


  };
  
  
  
  FileRemoteCallback callbackFromFilesExec = new FileRemoteCallback() {
    @Override public void start(FileRemote startDir) {  }
    
    @Override public Result offerParentNode(FileRemote file) {
      return Result.cont;      
    }
    
    @Override public Result finishedParentNode(FileRemote dir, FileRemoteCallback.Counters cnt) {
      String path = dir.getAbsolutePath();
      showFinishState(path, cnt);
      main.refreshFilePanel(dir);
      return Result.cont;      
    }
    
    

    @Override public Result offerLeafNode(FileRemote file) {
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(FileRemote startFile, SortedTreeWalkerCallback.Counters cnt) {  
      showFinishState("done", cnt);
      setTexts(Estate.finit);
      main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
    }


  };
  
  
  enum Estate{ inactive, start, checked, busyCheck, busy, quest, error, finit};
  
  enum Ecmd{ copy, move, delete, compare};
  
  void stop(){}
  
}
