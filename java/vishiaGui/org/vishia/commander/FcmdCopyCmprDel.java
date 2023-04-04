package org.vishia.commander;

import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.event.EventThread_ifc;
import org.vishia.event.EventWithDst;
import org.vishia.event.Payload;
import org.vishia.fileRemote.FileMark;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteProgress;
import org.vishia.fileRemote.FileRemoteWalkerCallback;
import org.vishia.fileRemote.XXXFileRemoteWalkerEvent;
import org.vishia.fileRemote.FileRemoteProgressEvData;
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
import org.vishia.gral.widget.GralLabel;
import org.vishia.msgDispatch.LogMessage;
import org.vishia.util.Debugutil;
import org.vishia.util.FileFunctions;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;
import org.vishia.util.StringFunctions;


/**Base class for initializing some class variables before the variables with only code in the class body are executed. */
class FcmdFileActionBase {

  /**The command which is given with that window. */
  final FcmdCopyCmprDel.Ecmd cmdWind;
  
  final String helpPrefix;
  
  FcmdFileActionBase(FcmdCopyCmprDel.Ecmd whatisit){
    cmdWind = whatisit;
    switch(whatisit){
      case compare: helpPrefix = "cmpr"; break;
      case copy: helpPrefix = "copy"; break;
      case delete: helpPrefix = "del"; break;
      case search: helpPrefix = "search"; break;
      default: throw new IllegalArgumentException("internal");
    }
  }
}


/**This class contains all functionality to execute copy, move, compare and delete for The.file.Commander.
 * The same class is used for three instances with a different view. See constructor-argument.
 * 
 * 
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

  GralWindow windConfirmCopy;
  
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
  
  
 
  protected final EventWithDst<FileRemoteProgressEvData,?> progressEv;
  
  /**True then some more files or dirs are selected in the current dir in the file panel to copy.
   * False then only the current selected or directory file is used for copy*/
  boolean srcSomeFiles;
  
  /**If more as one files are selected to copy, the names are contained here separated with ' : '.
   * Elsewhere it is "*". */
  String sFilesSrc;
  
  /**The destination selected directory and file as destination for compare and move or second tree for comparison. 
   * The fileDst is build with the name of the source file for copy and move and with the real dst file for compare
   */
  FileRemote fileDst, dirDst; //, dirDstCmpr;

  String sFileMaskDst;
  
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
//  final List<FileRemote.CallbackEvent> listEvCheck = new LinkedList<FileRemote.CallbackEvent>();
  
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
  
//  final List<FileRemote.CallbackEvent> listEvCopy = new LinkedList<FileRemote.CallbackEvent>();
  
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
  
  
  
  EventSource evSrc = new EventSource("FcmdCopy-GUIaction");
  

  
  
  /**This reference is set with the callback of operation cmd. 
   * The event can be used to affect the copying process.
   */
//  FileRemote.CallbackEvent evCurrentFile;
  
  
  Actions action;
  
  FcmdCopyCmprDel(Fcmd main, Ecmd cmdArg)
  { super(cmdArg);                         // FcmdActionBase
    this.main = main;
    String name = cmdArg.name;
    this.action = new Actions();
    GralPos refPos = new GralPos(main.gui.gralMng.screen);
    int windprops = GralWindow_ifc.windConcurrently; // + GralWindow.windResizeable;
    this.windConfirmCopy = new GralWindow(refPos, "@screen,30+37,30+70=" + name + "Window", cmdArg.name, windprops); 
    this.windConfirmCopy.setVisible(false);                // invisible per default, activate with setFocus()
    //source path and check:
    if(this.cmdWind != Ecmd.delete && this.cmdWind != Ecmd.search) {
      this.widgButtonModeDst = new GralButton(refPos, "@2.5-2, 1..7.5++=dst-" + name, null, this.actionChgModeDst);
      this.widgButtonModeDst.setSwitchMode(GralColor.getColor("gn"), GralColor.getColor("ye"));
      this.widgButtonModeDst.setSwitchMode("dst/..", "dst/dst");
    }
    this.widgButtonSetSrc = new GralButton(refPos, "@2.5-2, 15+12++=setSrc-" + name, "set source", this.actionConfirmCopy);
    this.widgButtonClearSel = new GralButton(refPos, "clrSel", "clear selection", null);
    //
    if(this.cmdWind != Ecmd.delete && this.cmdWind != Ecmd.search) {
      this.widgButtonSetDst = new GralButton(refPos, "@,-17..-1=setDst-" + name, "set destination", this.actionSetDst );
    }
    //main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    this.widgShowSrc = new GralTextField(refPos, "@5.5-3.2, 1..-4=showSrc-" + name, "source root path", "t");
    this.widgShowSrc.setBackColor(GralColor.getColor("am"),0);
    this.widgButtonShowSrc = new GralButton(refPos, "@5.5-2.5, -4..-1=btnShowSrc-" + name, "=>" , null);

    this.widgFromConditions = new GralTextField(refPos, "@+3.5-3.2, 1..-13++0.3=copyCond-" + name
        , "select src files: ?#+ [dirA|dirB]/**/*.ext (F1 for help) ", "t", GralTextField.Type.editable); 
    this.widgFromConditions.specifyActionChange(null, this.actionSelectMask, null);
    this.widgButtonCheck = new GralButton(refPos, "@+0-3,-12..-1=check" + name, "check", this.actionCheck );
    
    //dst path, set dst
    if(this.cmdWind != Ecmd.delete) {
      this.widgInputDst = new GralTextField(refPos, "@+3.5-3.2,1..-4=InputDst-" + name, "destination:", "t", GralTextField.Type.editable);
      this.widgInputDst.specifyActionChange(null, this.actionEnterTextInDst, null);
      this.widgButtonShowDst = new GralButton(refPos, "+0-2.5, -4..-1=showDst" + name, "=>", null );
    }  
    
    if(this.cmdWind == Ecmd.delete) {
      new GralLabel(refPos, "@+2-2, 1..18", "Del read only ?", 0);
    } else if(cmdWind == Ecmd.compare) {
      //nothing such
    } else {
      new GralLabel(refPos, "@+2-2, 1+17++", "Overwr read only ?", 0);
      new GralLabel(refPos, null, "Overwr exists ?", 0);
      new GralLabel(refPos, null, "Create ?", 0);
    }

    if(this.cmdWind != Ecmd.compare) {
      this.widgdChoiceOverwrReadOnly = new GralButton(refPos, "@+3-3,1+12++1=overwriter-" + name, "ask ?yes ?no", this.actionOverwrReadonly);
      this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
    }
    if(this.cmdWind != Ecmd.delete && this.cmdWind != Ecmd.compare) {
      this.widgdChoiceOverwrExists = new GralButton(refPos, "@,+13+17=copyOverwriteReadonly", "ask ?newer?older?all ?no", this.actionOverwrDate );
      this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"), 0);
      this.widgdChoiceCreateNew = new GralButton(refPos, "@,+18+13=copyCreate", "yes ?no ?ask", this.actionCreateCopy );
      this.widgdChoiceCreateNew.setBackColor(GralColor.getColor("lam"), 0);
    }

    //field for showing the current action or state, not for input:
    //field for showing the current name, not for input:
    this.widgCopyDirDst = new GralTextField(refPos, "@+3.5-3.2++,1..-1=copyDirDst" + name, "current directory:", "t");
    this.widgCopyDirDst.setBackColor(GralColor.getColor("lam"),0);
    this.widgCopyNameDst = new GralTextField(refPos,"copyNameDst" + name, "current file:", "t");
    this.widgCopyNameDst.setBackColor(GralColor.getColor("lam"),0);
    
    this.widgCopyState = new GralTextField(refPos, "@+,1..-15=copyStatus" + name, "current state:", "t");
    this.widgCopyState.setBackColor(GralColor.getColor("lam"),0);
    
    if(cmdWind == Ecmd.copy) {
      this.widgButtonMove = new GralButton(refPos, "@-8+2.5++0.3, -12..-1=copyMove", null, this.actionButtonCmprDelMove );
      this.widgButtonMove.setSwitchMode("move ?", "Move/ ?copy");
      this.widgButtonMove.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("lgn"));
    }
    this.widgButtonEsc = new GralButton(refPos, "@-4+3,1..9=copyEsc-" + name, "esc / close", this.actionButtonAbort);
    this.widgState = new GralButton(refPos, "@-4+2,9.2+2++1=showState-" + name, "?", this.actionShowState);
    
    if(cmdWind != Ecmd.compare){
      //main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 12, 'r', 1);
      //main.gui.gralMng.setPosition(GralPos.same, GralPos.size +2.5f, GralPos.next, GralPos.size + 11, 'r', 1);
      //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 'r', 1);
      this.widgSkipDir = new GralButton(refPos, "@,+2+10++1=copySkipDir"+name, "skip dir", this.actionButtonSkipDir);
      this.widgSkipFile =new GralButton(refPos,"copyskip"+name, "skip file",  this.actionButtonSkipFile);
      this.widgOverwrFile = new GralButton(refPos, this.sTextExecuteForFile,  "copyOverwrite", this.actionOverwriteFile);
      //widgBtnPause = main.gralMng.addButton("pause", null, "pause", null, "pause");
      //widgBtnPause.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp." + helpPrefix + ".pause.");
    }  
    this.widgButtonOk = new GralButton(refPos, "@-4+3, -13+-1=copyOk" + name, "ok", this.actionButtonOk);
    this.progressEv = new EventWithDst<FileRemoteProgressEvData, Payload>(name, this.evSrc, this.action.progressAction, null, new FileRemoteProgressEvData());
    //this.evWalker = new FileRemoteWalkerEvent(name, null, null, this.action.progressEv, 200);
    
  }
  
  
  
  
  
  /**Last files which are in copy process. Typical contains one file only. 
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();
  
  
  protected void activateWindow ( ) {
    if(state == Estate.inactive){
      setTexts(Estate.finit);
    } 
    //if(state.equals("close") || state.equals("check")) {
    //if(state == Estate.start || state == Estate.checked || state == Estate.finit || state == Estate.error) {
    //if(!widgButtonSetSrc.isDisabled()) {
    if( !windConfirmCopy.isVisible() || state == Estate.finit) { 
      //only if it is ready to check, get the files.
      bFirstSelect = true;
      filesToCopy.clear();
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
//            if(srcFile.isDirectory()) {
//              sFileDstCopy = "*";
//            } else {
              sFileDstCopy = srcFile.getName();  //name of source file as default for destination.
//            }
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
      bDstChanged = false;
      if(widgInputDst !=null) {
        widgInputDst.setBackColor(colorNoChangedText, 0);
      }
      setTexts(Estate.start);
      //widgButtonOk.setText("check");
      //widgButtonOk.setCmd("check");
      widgCopyState.setText("check?", 0);
      //widgButtonMove.setValue(GralMng_ifc.cmdSet, 0, 0);
      zFiles = 0; zBytes = 0;
    }
    windConfirmCopy.setVisible(true);
    widgButtonOk.setFocus(); //PrimaryWidgetOfPanel();
    main.gui.gralMng.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp." + helpPrefix + ".");
  }
  
  
  protected void closeWindow ( ){
    widgButtonOk.setText("close");
    //widgButtonOk.setCmd("close");
    filesToCopy.clear();
//    listEvCheck.clear();
//    listEvCopy.clear();
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
   * and refreshes the files therewith. The #this.progress is used as callback.
   * See {@link FileRemote#refreshAndMark(int, boolean, String, int, int, FileRemoteWalkerCallback)}.
   */
  final void execCheck(){
    setTexts(Estate.busyCheck);
    this.widgCopyState.setText("busy-check");
    this.widgButtonOk.setText("busy-check");
    this.widgButtonCheck.setState(GralButton.State.Disabled);
    this.widgButtonEsc.setText("abort");
    String sSrcMask= this.widgFromConditions.getText();
    //FileSystem: acts in other thread.
    //regards mark in first level ?
    int depths = this.srcSomeFiles ? -Integer.MAX_VALUE : Integer.MAX_VALUE;
    long bMarkSelect = this.srcSomeFiles ? 0x200000000L + FileMark.select : 0;
    if(sSrcMask.startsWith("?")) {                         // select mask: use mark bits:
      for(int ix=1; ix < sSrcMask.length(); ++ix) {
        char cs = sSrcMask.charAt(ix);
        switch(cs) {                       // for file                  for directory
        case '#': bMarkSelect |= FileMark.cmpContentNotEqual | FileMark.cmpFileDifferences; break;
        case '+': bMarkSelect |= FileMark.cmpAlone           | FileMark.cmpMissingFiles;    break;
        case '!': bMarkSelect |= FileMark.select             | FileMark.selectSomeInDir;    break;
        }
      }
      sSrcMask = null;
    } else if(sSrcMask.isEmpty()) {
      sSrcMask = "**/*";
    }
    //====>
    //srcFile.refreshAndMark(bFirstSelect, sSrcMask, bMarkSelect, depths, callbackFromFilesCheck, this.progress);
    this.action.progressAction.clear();
    this.srcFile.refreshAndMark(false, depths, FileMark.select, FileMark.selectSomeInDir, sSrcMask, bMarkSelect
        , this.callbackFromFilesCheck, this.progressEv);
    this.widgCopyNameDst.setText(this.srcDir.getStateDevice());
    this.bFirstSelect = false;
  }
  
  
  final protected void execDel(){
    if(state == Estate.checked){
      srcFile.deleteMarkedInThread(FileMark.select, callbackFromFilesExec, this.progressEv.data());      
    } else if(state == Estate.start){
    }
  }
  
  
  
  final protected void execMove() {
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
    FileRemote fileDst;
    if(FileFunctions.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = FileRemote.getDir(sDstDir); //main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = srcDir.child(sDstDir);  //relative to source
    }
//    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent("FcmdCopyCmprDelCallback", evSrc, srcFile, null, evConsumerCallbackFromFileMachine, null, evSrc);
    //====>
    //srcFile.moveChecked(sFilesSrc, fileDst, callback);
    setTexts(Estate.busy);
    ///
  }


  /**Starts the execution of copy in another thread. Note that the compy works with a state machine which uses
   * the found files in the {@link FileRemote#children()} tree. 
   * See {@link FileRemote#copyChecked(String, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execCopy(){
    this.widgCopyState.setText("busy-copy");
    this.widgButtonOk.setText("busy-copy");
    this.widgButtonEsc.setText("abort");
    String sDst = this.widgInputDst.getText();
    String[] sDstFileMaskRet = new String[1];
    this.dirDst = FileRemote.getDirFileDst(sDst, this.srcDir, sDstFileMaskRet);
    this.sFileMaskDst = sDstFileMaskRet[0];
    if(this.filesToCopy.size()>0) {
      for(FileRemote fileSrc: filesToCopy) {
      //======>>>>
        execCopy(fileSrc);
      }
    } else {
      //======>>>>
      execCopy(this.srcFile);                    // maybe the selected directory or a simple file
    }
    setTexts(Estate.busy);
  } 
  
  
  /**calls {@link FileRemote#copyDirTreeTo(FileRemote, int, String, int, FileRemoteProgressEvData)}
   * or {@link FileRemote#copyTo(FileRemote, org.vishia.fileRemote.FileRemote.CallbackEvent)}
   * with the given filedir for the {@link #dirDst} or {@link #fileDst}.
   * @param filedir
   */
  final private void execCopy(FileRemote filedir) {
    if(filedir.isDirectory()) {
      int selectMark = FileMark.select | FileMark.selectSomeInDir;
      int resetMark = FileMark.resetMark + selectMark;
      filedir.copyDirTreeTo(false, this.dirDst, 0, resetMark, resetMark, null, selectMark, null, this.progressEv);
    } else {
      //TODO maybe evaluate 
      final String sName;
      int posMask;
      if(this.sFileMaskDst ==null || this.sFileMaskDst.equals("*")) {
        sName = this.srcFile.getName();
      } else if( (posMask = this.sFileMaskDst.indexOf("*."))>=0) {
        String sNameSrc = this.srcFile.getName();
        int posExt = sNameSrc.lastIndexOf('.');
        if(posExt >0) { sNameSrc = sNameSrc.substring(0, posExt); }
        sName = this.sFileMaskDst.substring(0, posMask) + sNameSrc + this.sFileMaskDst.substring(posMask+1);
      } else {
        sName = this.sFileMaskDst;
      }
      this.fileDst = this.dirDst.child(sName);

      filedir.copyTo(this.fileDst, null);
    }
  }

  
  /**Starts the execution of compare in another thread. Note that the compare works with the walk-file algorithm
   * and refreshes the files therewith. See {@link FileRemote#refreshAndCompare(FileRemote, int, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execSearch(){
    String sSearch = widgInputDst.getText();
    sSearch=sSearch.replace("\\r", "\r");
    sSearch=sSearch.replace("\\n", "\n");
    byte[] search = { 0x0d, 0x0a};
    boolean bOk = true;
    //check whether the event is able to occupy, use it to check.
//    if(evCallback.occupyRecall(100, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//      if(evCallback.occupyRecall(1000, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//        System.err.println("FcmdCopyCmd event occupy hangs");
//        bOk = false;
//      }
//    }
    if(bOk) {
      String sSrcMask= widgFromConditions.getText();
//      evCallback.sendEvent(FileRemote.CallbackCmd.start);  //sends to myself for showing the state, 
      //it is a check of sendEvent and it should relinguish the event.
      //====>
      srcFile.refreshAndSearch(0, sSrcMask, 0, search, null, this.progressEv); //evCallback); //evCallback able to use from callback.
      //setTexts(Estate.busy);
    } else {
      widgCopyState.setText("evCallback hangs");
      setTexts(Estate.quest);
    }
    ///
  }
  
  

  /**Starts the execution of compare in another thread. Note that the compare works with the walk-file algorithm
   * and refreshes the files therewith. See {@link FileRemote#refreshAndCompare(FileRemote, int, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execCompare(){
    setDirFileDst();
    boolean bOk = true;
    //check whether the event is able to occupy, use it to check.
//    if(evCallback.occupyRecall(100, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//      if(evCallback.occupyRecall(1000, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//        System.err.println("FcmdCopyCmd event occupy hangs");
//        bOk = false;
//      }
//    }
    if(bOk) {
      String sSrcMask= widgFromConditions.getText();
//      evCallback.sendEvent(FileRemote.CallbackCmd.start);  //sends to myself for showing the state, 
      //it is a check of sendEvent and it should relinguish the event.
      //====>
      srcFile.refreshAndCompare(dirDst, 0, sSrcMask, 0, this.progressEv); //evCallback); //evCallback able to use from callback.
      //setTexts(Estate.busy);
    } else {
      widgCopyState.setText("evCallback hangs");
      setTexts(Estate.quest);
    }
    ///
  }
  
  
  
  private void setDirFileDst ( ) {
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
    if(FileFunctions.isAbsolutePathOrDrive(sDstDir)) {
      fileDst = FileRemote.getDir(sDstDir); //main.fileCluster.getDir(sDstDir);  //maybe a file or directory
    } else {
      fileDst = srcDir.child(sDstDir);  //relative to source
    }
    if(fileDst.isDirectory()){ dirDst = fileDst; } 
    else { dirDst = fileDst.getParentFile(); } 
  }
  
  
  final protected void abortCopy(){
    //if(fileSrc !=null ) {fileSrc.abortAction();}
    this.state = Estate.start;
    this.progressEv.data().setAbort();
    String sDirSrc = widgShowSrc.getText();
    //FileRemote dirSrc = main.fileCluster.getFile(sDirSrc, null); //new FileRemote(sDirSrc);
    if(srcDir !=null) { srcDir.abortAction(); }  //to set stateMachine of copy in ready state 
    else {
      System.err.println("FcmdCopyCmd: abort, dirSrc not given.");
    }
    filesToCopy.clear();
//    FcmdCopyCmprDel.this.evCurrentFile = null;
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
        if(srcFile.isDirectory()) { // && cmd == Ecmd.copy) {
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
    if(cmdWind != Ecmd.delete && cmdWind != Ecmd.search) {
      if(fileDst == null){
        bufferDstChars.setLength(0); bufferDstChars.append("??");
      } else if(!bDstChanged) {
        if(cmd == Ecmd.compare) {
          FileRemote dirDstCmpr = fileDst !=null && fileDst.isDirectory() ? fileDst : this.dirDst;
          bufferDstChars.setLength(0); bufferDstChars.append(dirDstCmpr.getPathChars()); //.append('/').append(sFileDstCopy);
        } else {
          bufferDstChars.setLength(0); 
          final FileRemote dstSet;
          if(/*!widgButtonModeDst.isOn() || */ srcFile.isDirectory() && !fileDst.isDirectory()){
            dstSet = dirDst;
            bufferDstChars.append(dirDst.getPathChars()); //the directory of the file.
          } else {
            //select "dst/dst" in mode or 
            dstSet = fileDst;
            bufferDstChars.append(fileDst.getPathChars()); //file or directory
          }
          //if(cmd == Ecmd.copy || cmd == Ecmd.move){
          //if(dirDst == fileDst || fileSrc.isDirectory() && !fileDst.isDirectory()) {  // .. is selected
//          if(dstSet.isDirectory()) {  // .. is selected
//            if(srcSomeFiles){
//              bufferDstChars.append("/*");  
//            } else {
//              String nameSrc = fileDst.getName();
//              bufferDstChars.append('/').append(nameSrc);
//            }
//          }
//          if(cmd == Ecmd.copy && srcFile != null && srcFile.isDirectory()) {
//            bufferDstChars.append("/*");
//          }
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
    { { "check del"  , "move"        , "check copy", "compare", "search" }
    , { "check del"  , "move"        , "check copy", "compare", "search" }
    , { "busy check" , "busy check"  , "busy check", "compare", "search" }
    , { "del checked", "move checked", "copy checked", "compare", "search" } 
    , { "pause del"  , "pause move"  , "pause copy", "pause cmpr", "pause" }
    , { "close del"  , "close move"  , "close copy", "close cmpr", "close"  }
    };
    String[][] textSrc = 
    { { "set src + dst", "set src + dst", "busy check", "set src + dst", "busy", "set src + dst" }
    , { "set dst", "set dst", "busy check", "set dst", "busy", "set dst" }
    };
    String[] textAbort = { "abort"         , "abort"       , "abort"        , "abort", "abort", "close" };
    String[] textDest =  { "---"           , "move to"     , "copy to"      , "compare with"  , "search"  };
    
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
    } else if(cmdWind == Ecmd.search){ 
      ix1 = 4; 
      cmd = Ecmd.search; 
      widgInputDst.setEditable(true);
      widgInputDst.setBackColor(bDstChanged ? colorChangedText : colorNoChangedText, 0);
    } else {
      //move or copy window:
      if(state == Estate.finit) {
        widgOverwrFile.setBackColor(GralColor.getColor("lgr"), 0);
        widgSkipFile.setBackColor(GralColor.getColor("lgr"), 0);
      }  
      if(widgButtonMove.isOn()) { 
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
  

  boolean bRunTestProgressThread;
  
  class TestProgressThread extends  Thread {
    
    
    public TestProgressThread() {
      super("testProgressThread");
      start();
    }

    
    @Override public void run() {
      System.out.println(LogMessage.timeCurr("testProgressThread"));
      //progress.timeOrder.activate(200);
//      progressEv.data().timeOrder.activateCyclic();
      FileRemoteProgressEvData progressEvData = progressEv.data();
      progressEvData.clean();
      while(bRunTestProgressThread) {
        progressEvData.nrFilesVisited +=1;
        synchronized(this) { try{ wait(200);} catch(InterruptedException exc) {}}
      }
      System.out.println(LogMessage.timeCurr("stop testProgressThread"));
      progressEvData.done(EventConsumer.mEventConsumFinished, null);
    }
  };
  
  TestProgressThread testProgressThread;
  
  
  /**Invoked with a time order.
   * 
   */
  void showCurrentProcessedFileAndDir(FileRemoteProgressEvData order) //FileRemote fileProcessed, int zFiles, boolean bDone) {
  { StringBuilder u = new StringBuilder(100);
    if(order.currFile !=null) {
      widgCopyDirDst.setText(order.currFile.getParent());
      widgCopyNameDst.setText(order.currFile.getName());
    } else {
      widgCopyDirDst.setText("?");
      widgCopyNameDst.setText("?");
    }
    if(order.done()) {
      widgCopyState.setText("ok: "); // + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
      //setTexts(Estate.finit);
    }
    
    
    zFiles = order.nrofFilesSelected;
    zBytes = order.nrofBytesFileCopied;
    widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
    
    if(order.progressCmd !=null) {
      switch(order.progressCmd){
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
          setTexts(Estate.finit);
          break;
        
      }
    }
    u.append("; Files:").append(Integer.toString(zFiles)).append(": ");
    widgCopyState.setText(u.toString());
  }
  
  
  
  
  
  
  void showFinishState(CharSequence start)
  {
//    FcmdCopyCmprDel.this.zFiles = cnt.nrofLeafss;
//    FcmdCopyCmprDel.this.zBytes = cnt.nrofBytes;
//    formatShow.reset();
//    formatShow.add(start);
//    formatShow.add(" Files:").addint(cnt.nrofLeafSelected, "3333333331");
//    //StringBuilder u = new StringBuilder();
//    //u.append("Files:").append(Integer.toString(cnt.nrofLeafSelected));
//    if(cnt.nrofLeafSelected != cnt.nrofLeafss){  //u.append(" (").append(Integer.toString(cnt.nrofLeafSelected)).append(")"); }
//      formatShow.add(" /").addint(cnt.nrofLeafss, "3333333331");
//    }
//    if(cnt.nrofBytes > 1000000){
//      formatShow.addint(cnt.nrofBytes/1000, ", 33331.111 MByte");
//    }
//    else if(cnt.nrofBytes > 1000){
//      formatShow.addint(cnt.nrofBytes, ", 331.111 kByte");
//    } else {
//      formatShow.addint(cnt.nrofBytes, ", 331 Byte");
//    }
//    widgCopyState.setText(formatShow.toString());
  }
  
  void actionConfirmCopy(){
    
  }

  
  /**Will be initialized if the main.gralMng is available.
   */
  protected class Actions
  {
    
    protected final FileRemoteProgress progressAction = new FileRemoteProgress("progressAction", null) {

      @Override protected int processEvent(FileRemoteProgressEvData progress, EventWithDst<FileRemote.CmdEvent, ?> evCmd) {
        showCurrentProcessedFileAndDir(progress); //this.currFile, this.nrFilesProcessed, this.bDone); 
        if(progress.done()) { 
          switch(progress.answerToCmd) {
          case walkSelectMark: setTexts(Estate.checked); break;
          case walkCopyDirTree: setTexts(Estate.finit); break;
          default: setTexts(Estate.error);
          }
          
          return EventConsumer.mEventConsumFinished; 
        }
        else return 0;
      }


      @Override public EventThread_ifc evThread () {
        return FcmdCopyCmprDel.this.widgCopyState.gralMng;
      }
      
    };
    
    
//    @SuppressWarnings("serial") 
//    FileRemoteProgressTimeOrder showFilesProcessing = 
//        new FileRemoteProgressTimeOrder("showFilesProcessing", evSrc, main.gui.gralMng.orderList(), 100) 
//    {
//      @Override public void executeOrder() { 
//        //System.out.println("showFilesProcessing");
//        showCurrentProcessedFileAndDir(this); //this.currFile, this.nrFilesProcessed, this.bDone); 
//        //this.currFile = null;  //to invoke second time.
//      }
//    };
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
  GralUserAction actionConfirmCopy = new GralUserAction("actionConfirmCopy")  //only for button setSrc
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
        activateWindow();
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
           execCheck();
         }
       }
       return true;
     }
   };
   

   
   void startStopTestProgressThread ( ) {
     if(bRunTestProgressThread) {
       bRunTestProgressThread = false;
     } else {
       bRunTestProgressThread = true;
       testProgressThread = new TestProgressThread();
//       progressEvData.timeOrder.activate(200);
       
     }
   }
   
  
   protected GralUserAction actionSetDst = new GralUserAction("actionSetDst") ///
   { @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
     { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         GralButton widgb = (GralButton)(widgP);
         FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
         fileCardDst = lastFileCards[0];
         FcmdCopyCmprDel.this.fileDst = fileCardDst.currentFile();
         FcmdCopyCmprDel.this.dirDst = fileCardDst.currentDir();
         
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
//        if(state == Estate.start) { //widgg.sCmd.equals("check")){    
//          action.progressAction.clear();
//          if(cmd == Ecmd.delete){
//            execCheck();
//          } else if(cmd == Ecmd.copy || bFineSelect){
//            execCheck();
//          } else if(cmd == Ecmd.move){
//            execCheck();
//          } else if(cmd == Ecmd.compare){
//            execCompare();
//          } else if(cmd == Ecmd.search){
//            execSearch();
//          }
//        } else if(state == Estate.checked) { //widgg.sCmd.equals("copy")) {
          if(state != Estate.finit) {  
            switch(cmd){
            case copy: execCopy(); break;
            case move: execMove(); break; 
            case delete: execDel(); break;
            case compare: execCompare(); break;
            case search: execSearch(); break;
          }//switch
        } else if(state == Estate.finit) { //widgg.sCmd.equals("close")){
          closeWindow();
        } else {
          //should be state pause
//          this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.cont, modeCopy()); //triggerStateMachine(evSrc, FileRemote.Cmd.docontinue);
        }
      }
      return true;
    }
  };

  
  
  
  protected GralUserAction actionButtonAbort = new GralUserAction("actionButtonAbort") { 
    @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
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
        //trigger a copy statemachine, only effected if in such a state.
        abortCopy();
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
        if(FcmdCopyCmprDel.this.progressEv.data().progressCmd == FileRemoteProgressEvData.ProgressCmd.askDstOverwr
             || FcmdCopyCmprDel.this.progressEv.data().progressCmd == FileRemoteProgressEvData.ProgressCmd.askDstReadonly) {
          FcmdCopyCmprDel.this.progressEv.data().modeCopyOper = modeCopy();
          FcmdCopyCmprDel.this.progressEv.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //.overwr ); 
          widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
          widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
        } else {
//          FcmdCopyCmprDel.this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.cont, modeCopy() ); //triggerStateMachine(evSrc, FileRemote.Cmd.docontinue);
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
//        if(evCurrentFile !=null){
//          int modeCopyOper = modeCopy();
//          //this.progress.answer(FileRemoteProgressTimeOrder.Answer.abortFile);
//          this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.abortFile, modeCopy() ); 
//        }
//        else if(this.progress !=null) {
          FcmdCopyCmprDel.this.progressEv.data().modeCopyOper = modeCopy();
          FcmdCopyCmprDel.this.progressEv.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //.abortCopyFile ); //;
         
//        }
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
  protected GralUserAction actionButtonSkipDir = new GralUserAction("actionButtonSkipDir") { 
    @Override public boolean userActionGui(int key, GralWidget widgg, Object... params) { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
//        if(evCurrentFile !=null){
//          int modeCopyOper = modeCopy();
//          //evCurrentFile.copySkipDir(modeCopyOper);
//        }
        FcmdCopyCmprDel.this.progressEv.data().modeCopyOper = modeCopy();
        FcmdCopyCmprDel.this.progressEv.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //abortCopyDir ); //;
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
//    FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evp;
//    listEvCopy.remove(ev);
    int nrofPendingFiles = 0; //listEvCopy.size();
    
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
  
  
  
  
//  /**Instance of an event consumer which is used for all callback actions from the FileRemote machine.
//   * The given event contains a command which depends on the action which is done by the file state machine or the file process.
//   * Especially
//   * <ul>
//   * <li>{@link FileRemote.CallbackCmd#nrofFilesAndBytes}: shows the progress of the action. 
//   * </ul>
//   */
//  EventConsumer evConsumerCallbackFromFileMachine = new EventConsumer(){
//    @Override public int processEvent(EventObject ev)
//    {
//      FileRemote.CallbackEvent ev1 = (FileRemote.CallbackEvent)ev;
//      FileRemote.CallbackCmd cmd = ev1.getCmd();
//      String sCmd = cmd.name();
//      System.out.println("FcmdCopy - callbackCopy;" + sCmd);
//      switch(ev1.getCmd()){
//        case start: {
//          setTexts(Estate.busy);
//        } break;
//        case doneCheck:{ ///
//          //if(listEvCheck.remove(ev)){  ///
//            FcmdCopyCmprDel.this.evCurrentFile = ev1;
//            zFiles = ev1.nrofFiles;
//            zBytes = ev1.nrofBytesAll;
//            //int nrofPendingFiles = listEvCheck.size();
//            //if(nrofPendingFiles == 0){
//              //TODO check dst space
//              widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
//              //widgButtonOk.setText("copy");
//              //widgButtonOk.setCmd("copy");
//              //widgButtonSrc.setText("set src");
//              //widgButtonSrc.setCmd("setSrc");
//            //}
//            
//          //} else {
//            //unexpected doneCheck:
//          //}
//          setTexts(Estate.checked);
//        } break;
//        case copyDir:{
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          String sPath = StringFunctions.z_StringJc(ev1.fileName);
//          widgCopyNameDst.setText(sPath);
//        } break;
//        case nrofFilesAndBytes:{
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          int percent = ev1.promilleCopiedBytes / 10;
//          widgProgressFile.setValue(percent);
//          widgProgressAll.setValue(ev1.promilleCopiedFiles / 10);
//          widgCopyNameDst.setText(StringFunctions.z_StringJc(ev1.fileName));
//          widgCopyState.setText("... " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
//        }break;
//        case askDstOverwr: {
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          widgCopyNameDst.setText("exists: " + StringFunctions.z_StringJc(ev1.fileName));
//          widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
//          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
//        } break;
//        case askDstReadonly: {
//            FcmdCopyCmprDel.this.evCurrentFile = ev1;
//            widgCopyNameDst.setText("read only: " + StringFunctions.z_StringJc(ev1.fileName));
//            widgSkipFile.setBackColor(GralColor.getColor("am"), 0);
//            widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
//        } break;
//        case askDstNotAbletoOverwr: {
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          widgCopyNameDst.setText("can't overwrite: " + StringFunctions.z_StringJc(ev1.fileName));
//          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
//          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
//        } break;
//        case askErrorDstCreate: {
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          widgCopyNameDst.setText("can't create: " + StringFunctions.z_StringJc(ev1.fileName));
//          widgOverwrFile.setBackColor(GralColor.getColor("lrd"), 0);
//        } break;
//        case askErrorCopy: {
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//          widgCopyNameDst.setText("copy error: " + StringFunctions.z_StringJc(ev1.fileName));
//          widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
//          widgOverwrFile.setBackColor(GralColor.getColor("am"), 0);
//        } break;
//        case error: {
//          FcmdCopyCmprDel.this.evCurrentFile = null;
//          widgCopyState.setText("error");
//          setTexts(Estate.error);
//          eventConsumed(ev, false);
//        }break;
//        case nok: {
//          FcmdCopyCmprDel.this.evCurrentFile = null;
//          widgCopyState.setText("nok");
//          setTexts(Estate.error);
//          eventConsumed(ev, false);
//        }break;
//        case done: {
//          FcmdCopyCmprDel.this.evCurrentFile = null;
//          widgCopyState.setText("ok: " + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
//          eventConsumed(ev, true);
//          setTexts(Estate.finit);
//        }break;
//        default:
//          FcmdCopyCmprDel.this.evCurrentFile = ev1;
//
//      }
//      //windConfirmCopy.setWindowVisible(false);
//      return 1;
//    }
//    @Override public String toString(){ return "FcmdCopy-success"; }
//
//    
//  };

  
 
  
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
//  FileRemote.CallbackEvent evCallback = new FileRemote.CallbackEvent("FcmdCopyCmpDel", evConsumerCallbackFromFileMachine, null, evSrc); 
  

  
  
  FileRemoteWalkerCallback callbackFromFilesCheck = new FileRemoteWalkerCallback() {
    @Override public void start(FileRemote startDir) {  
      fileProcessed = null;
    }
    
    @Override public Result offerParentNode(FileRemote file) {
      if(file.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
      } else {
        dirProcessed = file;
        return Result.cont;    
      }
    }
    
    /**Finish a directory, check whether a file panel should be refreshed.
     * @see org.vishia.util.SortedTreeWalkerCallback#finishedParentNode(java.lang.Object, org.vishia.util.SortedTreeWalkerCallback.Counters)
     */
    @Override public Result finishedParentNode(FileRemote dir) {
      main.refreshFilePanel(dir);
      return Result.cont;      
    }
    
    

    @Override public Result offerLeafNode(FileRemote file, Object info) {
      boolean bShow = (fileProcessed == null);
      //actionShowFilesCmp.fileProcessed = file;
      //actionShowFilesCmp.zFiles +=1;
      if(bShow){
        //actionShowFilesCmp.addToList(widgButtonShowSrc.gralMng().orderList(), 300);
      }
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(FileRemote startFile) {  
      showFinishState("checked ");
      main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
      setTexts(Estate.checked);
    }


  };
  
  
  
  FileRemoteWalkerCallback callbackFromFilesExec = new FileRemoteWalkerCallback() {
    @Override public void start(FileRemote startDir) {  }
    
    @Override public Result offerParentNode(FileRemote file) {
      if(file.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
        //but it does not activate this, deselect before
      } else {
        return Result.cont; 
      }
    }
    
    @Override public Result finishedParentNode(FileRemote dir) {
      if(dir.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
      } else {
        String path = dir.getAbsolutePath();
        showFinishState(path);
        main.refreshFilePanel(dir);
        return Result.cont;
      }
    }
    
    

    @Override public Result offerLeafNode(FileRemote file, Object info) {
      return Result.cont;
    }

    
    @Override public boolean shouldAborted(){
      return false;
    }

    @Override public void finished(FileRemote startFile) {  
      showFinishState("done");
      setTexts(Estate.finit);
      main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
    }

    
    @Override public String toString(){ return "callbackFromFilesExec"; }

  };
  
  
  enum Estate{ inactive, start, checked, busyCheck, busy, quest, error, finit};
  
  enum Ecmd{ 
    copy("copy"), 
    move("move"), 
    delete("del"), 
    compare("cmp"), 
    search("search");
    Ecmd(String name){ this.name = name; }
    public final String name; 
  };
  
  void stop(){}
  
}
