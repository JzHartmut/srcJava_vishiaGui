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
import org.vishia.fileRemote.FileRemoteCmdEventData;
import org.vishia.fileRemote.FileRemoteProgressEventConsumer;
import org.vishia.fileRemote.FileRemoteWalkerCallback;
import org.vishia.fileRemote.FileRemoteProgressEvData;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
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
import org.vishia.util.FileCompare;
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
    this.cmdWind = whatisit;
    switch(whatisit){
      case compare: this.helpPrefix = "cmpr"; break;
      case copy: this.helpPrefix = "copy"; break;
      case delete: this.helpPrefix = "del"; break;
      case search: this.helpPrefix = "search"; break;
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
   * <li>2024-02-17 ^ and v as mark bits for copy. 
   * <li>2024-02-13 enhanced possibility of compare in the copy diaglog and with set ignoring symbolic links.
   * <li>2023-04-06 Hartmut restructured also in widgets as in functionality, in progress, for version 2.0 of The.file.Commander
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


  String sTextExecuteForFile = (this.cmdWind == Ecmd.delete) ? "del file" : "overwr file";

  protected final Fcmd main;

  GralWindow windConfirmCopy;

  GralPos posWindConfirmCopy;

  //GralWidget widgdCopyFrom, widgdCopyTo;

  GralTextField widgSrcDir, widgSrcSelection;

  //GralButton widgButtonModeDst;

  GralTextField widgDstDir, widgDstFileModification;

  GralTextField widgCopyDirDst, widgCopyNameDst, widgCopyState;  //TODO with progress

  GralButton widgdChoiceCreateNew, widgdChoiceOverwrExists, widgdChoiceOverwrReadOnly;

  GralButton widgOverwrFile, widgSkipFile, widgSkipDir, widgBtnPause;


  GralButton widgState, widgButtonSetSrc, widgButtonSetDst, widgButtonSetSymbolicLinks, widgButtonSelNewChg, widgButtonSelAll;
  
  GralButton widgButtonCheck, widgButtonCmprFast, widgButtonCmprContent, widgButtonMove, widgBtnExec;

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

  /**The source dir and file to use. 
   * It is set in the {@link #activateWindow()} due to the situation of selected files in the first file table of the Fcmd:
   * see {@link #getSrcDstFileDir(boolean, GralWidget, GralWidget)}.
   */
  FileRemote srcDir, srcFile;



  //protected final EventWithDst<FileRemoteProgressEvData,?> progressEv;

  /**True then some more files or dirs are selected in the current dir in the file panel to copy.
   * False then only the current selected or directory file is used for copy*/
  //boolean srcSomeFiles;

  /**If more as one files are selected to copy, the names are contained here separated with ' : '.
   * Elsewhere it is "*". */
  //String sFilesSrc;

  /**The destination selected directory and file as destination for compare and move or second tree for comparison.
   * The fileDst is build with the name of the source file for copy and move and with the real dst file for compare
   */
  FileRemote fileDst, dirDst; //, dirDstCmpr;

  String sFileMaskDst;

  /**Name of the file for dst. */
  //CharSequence sFileDstCopy;

  StringBuilder bufferDstChars = new StringBuilder(100);

  StringFormatter formatShow = new StringFormatter(100);

  /**If true then the {@link #widgDstDir} was changed for this session. Not automatically change the content. */
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


//  static final String[] sTextsBtnSrcSel =        { "check/sel"    , "check/select"    , "check/select", "check/select"  , "search"  };
//  static final String[] sTextsBtnDstExec =    { "delete"       , "move"            , "copy",         "compare"  , null  };


  /**This reference is set with the callback of operation cmd.
   * The event can be used to affect the copying process.
   */
//  FileRemote.CallbackEvent evCurrentFile;


  final ProgressAction progress;

  FcmdCopyCmprDel(Fcmd main, Ecmd cmdArg)
  { super(cmdArg);                         // FcmdActionBase
    this.main = main;
    //                       delete            move               copy                  cmp                  search 
    String[] promptSrcDirSel =  { "delete base dir", "move from base dir", "source base dir", "compare base dir1"  , "search base dir" };
    String[] promptSrcFileSel = { "file mask"   , "file mask"       , "select src files: ?#+ [dirA|dirB]/**/*.ext (F1 for help) ", "file mask"  , "file mask"  };
    String[] promptDstDirSel =  { null           , "move to dir"     , "destination base dir"      , "compare with dir"  , null  };
    String[] promptDstFileSel = { null           , "rename to"       , "file modification", null  , null  };
    String name = cmdArg.name;
    GralPos refPos = new GralPos(main.gui.gralMng.screen);
    int windprops = GralWindow_ifc.windConcurrently + GralWindow_ifc.windResizeable;
    this.windConfirmCopy = new GralWindow(refPos, "@screen,30+42,30+70=" + name + "Window", cmdArg.name, windprops);
    this.windConfirmCopy.setVisible(false);       // invisible per default, activate with setFocus()
    //source path and check:
    //this.widgButtonClearSel = new GralButton(refPos, "clrSel", "clear selection", null);
    //
    this.widgSrcDir = new GralTextField(refPos, "@3.5-3.2, 1..-9=SrcDir-" + name, promptSrcDirSel[cmdArg.ix], "t");
    this.widgSrcDir.setBackColor(GralColor.getColor("am"),0);
    this.widgButtonSetSrc = new GralButton(refPos, "@+0-2, -8..-1=SetSrc-" + name, "set source", this.actionSetSrc);
    //this.widgButtonShowSrc = new GralButton(refPos, "@5.5-2.5, -4..-1=btnShowSrc-" + name, "=>" , null);

    this.widgButtonSelNewChg = new GralButton(refPos, "@+1.5-1.5,-22..-18=SelNewChg" + name, "?+#^", this.actionSelNewChg );
    this.widgButtonSelAll = new GralButton(refPos, "@+0-1.5,-17..-13=SelAll" + name, "*/**", this.actionSelAll );
    this.widgButtonSetSymbolicLinks = new GralButton(refPos, "@+2-2,1..5=SymLinks" + name, null, null );
    this.widgButtonSetSymbolicLinks.setSwitchMode(">no", ">ln");
    this.widgButtonSetSymbolicLinks.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("gn"));
    this.widgSrcSelection = new GralTextField(refPos, "@+0-3.2, 5..-13++0.3=copyCond-" + name
        , promptSrcFileSel[cmdArg.ix] , "t", GralTextField.Type.editable);
    this.widgSrcSelection.specifyActionChange(null, this.actionSelectMask, null);
    if(cmdArg != Ecmd.search) {
      this.widgButtonCheck = new GralButton(refPos, "@+0-3,-12..-1=check" + name, "check/mark", this.actionCheck );
    }
    //dst path, set dst
    String promptDstDir = promptDstDirSel[cmdArg.ix];
    if(promptDstDir !=null) {
      //if(cmdArg == Ecmd.copy) {
      this.widgButtonCmprFast = new GralButton(refPos, "@+3.2-2,15..24=CmprFast" + name, "cmpr fast", this.actionButtonCmprFast );        
      this.widgButtonCmprContent = new GralButton(refPos, "@+0-2,25..39=CmprContent" + name, "cmpr content", this.actionButtonCmprContent );        
      //}
      this.widgDstDir = new GralTextField(refPos, "@+2.2-3.2,1..-9=DstDir-" + name, promptDstDir, "t");
      this.widgButtonSetDst = new GralButton(refPos, "@+0-2.5,-8..-1=setDst-" + name, "set dst", this.actionSetDst );
      //this.widgButtonShowDst = new GralButton(refPos, "+0-2.5, -4..-1=showDst" + name, "=>", null );
      String promptDstFile = promptDstFileSel[cmdArg.ix];
      if(promptDstFile !=null) {
        this.widgDstFileModification = new GralTextField(refPos, "@+3.5-3.2,5..-16=DstFileModification-" + name, promptDstFile, "t", GralTextField.Type.editable);
        this.widgDstFileModification.specifyActionChange(null, this.actionEnterTextInDst, null);
      }
    }
    //String sBtnDstExec = sTextsBtnDstExec[cmdArg.ix]; 
    if(this.cmdWind == Ecmd.delete) {
      new GralLabel(refPos, "@+4+2, 1..18", "Del read only ?", 0);
      this.widgdChoiceOverwrReadOnly = new GralButton(refPos, "@+3-3,1+12++1=overwriter-" + name, "ask ?yes ?no", this.actionOverwrReadonly);
      this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
    } else if(this.cmdWind == Ecmd.compare) {
      try{ refPos.setPosition("+4", refPos);} catch(java.text.ParseException exc) {}
    } else if(this.cmdWind != Ecmd.compare && this.cmdWind != Ecmd.search) {
      new GralLabel(refPos, "@+4+2, 1+17++", "Overwr read only ?", 0);
      new GralLabel(refPos, null, "Overwr exists ?", 0);
      new GralLabel(refPos, null, "Create ?", 0);
      this.widgdChoiceOverwrReadOnly = new GralButton(refPos, "@+3-3,1+12++1=overwriter-" + name, "ask ?yes ?no", this.actionOverwrReadonly);
      this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
      this.widgdChoiceOverwrExists = new GralButton(refPos, "@,+13+17=copyOverwriteReadonly", "ask ?newer?older?all ?no", this.actionOverwrDate );
      this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"), 0);
      this.widgdChoiceCreateNew = new GralButton(refPos, "@,+18+13=copyCreate", "yes ?no ?ask", this.actionCreateCopy );
      this.widgdChoiceCreateNew.setBackColor(GralColor.getColor("lam"), 0);
    }
    //if(sBtnDstExec !=null) {
      this.widgBtnExec = new GralButton(refPos, "@+0-3.5, -15..-1=BtnExec-" + name, "exec-?", this.actionButtonOk);
    //}

    //field for showing the current action or state, not for input:
    //field for showing the current name, not for input:
    this.widgCopyDirDst = new GralTextField(refPos, "@+3.5-3.2++,1..-1=copyDirDst" + name, "current directory:", "t");
    this.widgCopyDirDst.setBackColor(GralColor.getColor("lam"),0);
    this.widgCopyNameDst = new GralTextField(refPos,"copyNameDst" + name, "current file:", "t");
    this.widgCopyNameDst.setBackColor(GralColor.getColor("lam"),0);

    this.widgCopyState = new GralTextField(refPos, "@+,1..-15=copyStatus" + name, "current state:", "t");
    this.widgCopyState.setBackColor(GralColor.getColor("lam"),0);

    if(this.cmdWind == Ecmd.copy) {
      this.widgButtonMove = new GralButton(refPos, "@-8+2.5++0.3, -12..-1=copyMove", null, this.actionButtonCmprDelMove );
      this.widgButtonMove.setSwitchMode("move ?", "Move/ ?copy");
      this.widgButtonMove.setSwitchMode(GralColor.getColor("wh"), GralColor.getColor("lgn"));
    }
    this.widgButtonEsc = new GralButton(refPos, "@-4+3,1..9=copyEsc-" + name, "esc / close", this.actionButtonAbort);
    this.widgState = new GralButton(refPos, "@-4+2,9.2+2++1=showState-" + name, "?", this.actionShowState);

    if(this.cmdWind != Ecmd.compare){
      //main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 12, 'r', 1);
      //main.gui.gralMng.setPosition(GralPos.same, GralPos.size +2.5f, GralPos.next, GralPos.size + 11, 'r', 1);
      //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 'r', 1);
      this.widgSkipDir = new GralButton(refPos, "@,+2+10++1=copySkipDir"+name, "skip dir", this.actionButtonSkipDir);
      this.widgSkipFile =new GralButton(refPos,"copyskip"+name, "skip file",  this.actionButtonSkipFile);
      this.widgOverwrFile = new GralButton(refPos, this.sTextExecuteForFile,  "copyOverwrite", this.actionOverwriteFile);
      //widgBtnPause = main.gralMng.addButton("pause", null, "pause", null, "pause");
      //widgBtnPause.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp." + helpPrefix + ".pause.");
    }
    this.progress = new ProgressAction("progressAction_" + name , FcmdCopyCmprDel.this.widgCopyState.gralMng, null);
    //this.evWalker = new FileRemoteWalkerEvent(name, null, null, this.action.progressEv, 200);

  }





  /**Last files which are in copy process. Typical contains one file only.
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();


  protected void activateWindow ( ) {
    if(this.state == Estate.inactive){
      setTexts(Estate.finit);
    }
    //if(state.equals("close") || state.equals("check")) {
    //if(state == Estate.start || state == Estate.checked || state == Estate.finit || state == Estate.error) {
    //if(!widgButtonSetSrc.isDisabled()) {
    if( !this.windConfirmCopy.isVisible() || this.state == Estate.finit) {
      //only if it is ready to check, get the files.
      this.bFirstSelect = true;
      this.filesToCopy.clear();                 // list todo remove.
      this.widgSrcSelection.setText("");
      FileRemote[] srcDirFile = getSrcDstFileDir(false, this.widgSrcDir, this.widgSrcSelection);
      this.srcDir = srcDirFile[0]; 
      this.srcFile = srcDirFile[1];
      if(this.widgDstFileModification !=null) { this.widgDstFileModification.setText(""); }
      getDstFileDir(FcmdCopyCmprDel.this.main.getLastSelectedFileCards()[1], false);

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
      this.bDstChanged = false;
      if(this.widgDstDir !=null) {
        this.widgDstDir.setBackColor(this.colorNoChangedText, 0);
      }
      //widgButtonOk.setText("check");
      //widgButtonOk.setCmd("check");
      this.widgCopyState.setText("check?", 0);
      //widgButtonMove.setValue(GralMng_ifc.cmdSet, 0, 0);
      this.zFiles = 0; this.zBytes = 0;
    }
    this.windConfirmCopy.setVisible(true);
    this.widgBtnExec.setFocus(); //PrimaryWidgetOfPanel();
    this.main.gui.gralMng.setHelpUrl(this.main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp." + this.helpPrefix + ".");
  }


  protected void closeWindow ( ){
    this.widgBtnExec.setText("close");
    //widgButtonOk.setCmd("close");
    this.filesToCopy.clear();
//    listEvCheck.clear();
//    listEvCopy.clear();
    this.state = Estate.inactive;
    this.windConfirmCopy.setVisible(false);
    //main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
  }



  
  /**Gets the situation of selection in the file panels for source and destination.
   * <ul><li>If ".." is selected, and no entry is marked, it means to somewhat with this whole directory.
   *   Then {@link #srcDir} is set with the tables dir, {@link #srcFile} = null, showMask = "** /*"
   * <li>If ".." is selected, and one or some entries are marked, it means to somewhat with the selected files.
   *   Then {@link #srcDir} is set with the tables dir, {@link #srcFile} = null, showMask = "?!"
   *   It means the marked files should be taken. 
   * <li>If any directory entry is selected and not marked or only this entry is marked, 
   *   then it means somewhat should be done with the whole directory.
   *   Then {@link #srcDir} is set with the tables dir, {@link #srcFile} is set with the dir, 
   *   and showMask is set with  "dirname/** /*". Do not use {@link #srcFile}, use the mask to walk.
   * <li>If any file entry is selected and not marked or only this entry is marked, 
   *   then it means somewhat should be done with the ony one file.
   *   Then {@link #srcFile} is set with it, {@link #srcDir} = null, the mask is set with the file name (but not changeable)
   * <li>If more as one entry is marked and one of them is also selected, 
   *   then it means somewhat should be done with all marked entries.
   *   Then {@link #srcDir} is set with the tables dir, {@link #srcFile} = null
   *   The mask is set with "?!", it means do somewhat with the marked files.
   * </ul>  
   * Hint: The mask is able to change if {@link #srcDir} is set only.   
   * 
   * @param bAlsoDst false then handle only the current panel.
   */
  protected FileRemote[] getSrcDstFileDir ( boolean bAlsoDst, GralWidget widgShowSrcArg, GralWidget widgShowMask) {
    final FileRemote[] setSrc = new FileRemote[2];
    final GralWidget widgShowSrc;
    if(widgShowSrcArg !=null) { widgShowSrc = widgShowSrcArg; }
    else { widgShowSrc = this.widgSrcDir; }                // decide using widgSrcDir or another from argument
    //
    this.fileCardSrc = this.main.getLastSelectedFileCards()[0];
    final Estate newState;
    if(fileCardSrc ==null) { //------------------------------ non first panel given:
      widgShowSrc.setText("??");                           // show ?? as source dir
      widgShowMask.setText("");
      newState = Estate.finit;                             // state finit, offer [close] on widgBtnExec
    } else {
//      this.fileCardSrc = fileCardSrc;     // Filecards: active is src, second is destination.
//      this.fileCardDst = lastFileCards[1];

      //widgButtonDst.setState(GralButton.kOff);  //maybe disabled, set off.
      // detect whether some files are currently marked. If the current file is not marked but some other are marked,
      // this routine returns the only one current file. This behavior assures, that the one file in focus is returned
      // and not some files which are marked outside of the own focus. 
      // If you want to copy some marked files you should select on of these marked files.
      List<FileRemote> listFileSrc = this.fileCardSrc.getSelectedFiles(true, FileMark.selectSomeInDir | FileMark.select);
      FileRemote srcFile = this.fileCardSrc.currentFile(); // the selected file or dir
      FileRemote srcDir = this.fileCardSrc.currentDir();   // the dir of the card
      this.widgSrcDir.gralMng.log.sendMsg(55, "srcDir = %s, srcFile = %s", srcDir.getPathChars(), srcFile.getName());
      //String sDirSrc;
      if(listFileSrc == null){ // this does never occur but show it:
        widgShowSrc.setText("??? listFileSrc==null");
        newState = Estate.error;
      } else if(srcFile == srcDir) { //------------ ".." is selected:
          setSrc[0] = srcDir;
          setSrc[1] = null;                      // offering with "../*" illustrates, it is a directory with selection.
          if(listFileSrc.size() >1) {            // there are some more files immediately selected
            widgShowSrc.setText(srcFile.getPathChars().toString() + "*");  // it ends with "/*" means use the content of directory
            widgShowMask.setText("?!");
            newState = Estate.check;
          } else {                               // one file or dir is selected
            widgShowSrc.setText(srcFile.getPathChars().toString() + "*");  // it ends with "/*" means use the content of directory
            String sShowMask = widgShowMask.getText();
            //if(sShowMask.length() ==0) {
              widgShowMask.setText("**/*");
              newState = Estate.check;
            //}
          }
      } else {                       //------------ one or more files (dirs) are marked
        if(listFileSrc.size() ==1) { //------------ either only one is marked and selected, or one no marked selected file or dir
          assert(srcFile == listFileSrc.get(0));
          if(srcFile.isDirectory()) {  //---------- directory selected, it is assumed that the list is currently refreshed with the file system.
              setSrc[0] = srcDir;                // the base directory of the table
              setSrc[1] = srcFile;               // only one directory is selected, do not use walk-dir
              widgShowSrc.setText(srcDir.getPathChars());  // it ends with "/" means use this directory
              widgShowMask.setText(srcFile.getName() + "/**/*");  // the file is part of the mask
              newState = Estate.check;
          } else {                     //--------- file is selected
            setSrc[0] = null;                    // only one file is selected, do not use walk-dir
            setSrc[1] = srcFile;                 // assume it exists
            widgShowSrc.setText(srcFile.getDirChars());  // not ends with "/"
            widgShowMask.setText(srcFile.getName());     // not ends with "/"
            newState = Estate.ready;
          }
        } else {                     //------------ more as one files/dirs are marked, also the current
          widgShowSrc.setText(srcDir.getPathChars().toString() + "*");  // not ends with "/"
          if(!srcDir.isMarked(FileMark.select | FileMark.selectSomeInDir)) {
            srcDir.setMarked(FileMark.selectSomeInDir);  // srcDir should be not marked, not to copy
          }
          String sShowMask = widgShowMask.getText();
          if(listFileSrc.size() ==0) {
            if(sShowMask.length() == 0) {
              widgShowMask.setText("*");
            }
            newState = Estate.check;
          }else {
            if(!sShowMask.startsWith("?")) {     // showMask: write a "?" for "selected" as first char.
              widgShowMask.setText("?! " + sShowMask);
            }
            newState = Estate.checked;
          }
          setSrc[0] = srcDir;                    // note: srcDir is not selected as info, inside are selected.
          setSrc[1] = null;                      // then srcFile = null;
        }
      }

    }
    setTexts(newState);
    return setSrc;
  }

  
  /**
   * @param fromCard Either the fileCard[0] on command "set dst" or fileCard[1] on command activate.
   * @param bSetFile true then set the selected file as current, it is for "set dst"
   */
  protected void getDstFileDir ( FcmdFileCard fromCard, boolean bSetFile) {
    if(this.widgDstDir ==null) return;
    this.fileCardDst = fromCard;
    if(fromCard ==null) {
      this.dirDst = null;
      this.fileDst = null;
      this.widgDstDir.setText( "??" );
    } else {
//      if(dirFileDst.isDirectory()) {           // destination: a directory is selected:
//        if(this.srcFile !=null) {              // proposal: selected dir / srcFileName, can be changed
//          this.widgInputDst.setText( dirFileDst.getPathChars().toString() + srcFile.getName() ); 
//        } else {
//          this.widgInputDst.setText( dirFileDst.getPathChars().toString() + "*" );
//        }
//      } else {                                 // destination: a file is selected
//        if(this.srcFile !=null) {              // proposal: selected file as destination, can be changed
//          this.widgInputDst.setText( dirFileDst.getPathChars() ); 
//        } else {                               // respectively: use its directory.
//          this.widgInputDst.setText( dirFileDst.getParentFile().getPathChars().toString() + "*" );
//        }
//      }
      
      this.dirDst = fromCard.currentDir();
      this.widgDstDir.setText( this.dirDst.getPathChars() ); 
      if(this.widgDstFileModification !=null) {  // it is null for compare window
        FileRemote fileDst = fromCard.currentFile();
        if(bSetFile) {
          if(this.srcFile !=null) {              // on src any is selected, file or dir
            if(this.srcFile.isDirectory()) {     // a single directory is selected as source
              if(fileDst == this.dirDst) {       // .. is selected, store in this directory:
                this.widgDstFileModification.setText(this.srcFile.getName() + "/*");
              } else if(fileDst.isDirectory()) {        // dst is a directory:
                this.widgDstFileModification.setText(fileDst.getName() + "/" + this.srcFile.getName() + "/*");
              } else {                           // dst is a file
                this.widgDstFileModification.setText("*");
              }
            } else {                             // a single file is selected as source 
              if(fileDst == this.dirDst) {       // .. is selected, store in this directory:
                this.widgDstFileModification.setText(this.srcFile.getName());
              } else if(fileDst.isDirectory()) {        // dst is a directory:
                this.widgDstFileModification.setText(fileDst.getName() + "/" + this.srcFile.getName());
              } else {                           // dst is a file
                this.widgDstFileModification.setText(fileDst.getName());
              }
            }
          }
          else { //-------------------------------- src is on ..
            if(fileDst == this.dirDst) {       // .. is selected, store in this directory:
              this.widgDstFileModification.setText("*");
            } else if(fileDst.isDirectory()) {        // dst is a directory:
              this.widgDstFileModification.setText(fileDst.getName() + "/*");
            } else {                           // dst is a file
              this.widgDstFileModification.setText("*");
            }
          }
        } else { // ------------------------------- on activating the window or setSrc
          if(this.srcFile !=null) {
            if(this.srcFile.isDirectory()) { //------ directory selected
              this.widgDstFileModification.setText(srcFile.getName() + "/**/*.*");  // not ends with "/"
            } else {
              this.widgDstFileModification.setText(this.srcFile.getName());
            }
          } else { //-------------------------------- .. is selected on src.
            this.widgDstFileModification.setText("**/*.*");
          }
        }
        if(this.srcFile !=null) {
          
        }
  //        if(dirFileDst.isDirectory()) {           // destination: a directory is selected:
  //          this.widgDstDir.setText( dirFileDst.getPathChars().toString() + this.srcFile.getName() ); 
  //        } else {
  //          this.widgDstDir.setText( dirFileDst.getPathChars() ); 
  //        }
      }
    }
  }
  
  

  /**Last files which are in copy process. Typical contains one file only.
   * This list will be filled in {@link #actionButtonCopy} if the copy process will be started.
   * It is used in {@link #actionLastSrc} to fill the {@link #filesToCopy}.
   * This list remains after copy process to supply "last files".
   */
  //final List<FileRemote> filesToCopyLast = new LinkedList<FileRemote>();



  /**All mode bits of the 3 variables.*/
  protected int modeCopy(){
    int mode = this.modeCreateCopy | this.modeOverwrReadonly |this.modeOverwrDate;
    return mode;
  }

  
  protected boolean occupyProgress() {
    if(this.progress.evBack.occupy(this.evSrc, true)) {
      return true;
    } else {
      this.widgCopyState.setText("event blocks - may pressing abort");
      return false;
    }
  }
  
  
  
  /**Operation associated to the Ok or Exec key: {@link #widgBtnExec} associated to its {@link #actionButtonOk}.
   * It executes the operation set by construction (via {@link #cmd} and depending from {@link #state}.
   * It calls {@link #execCheck()} if the state is {@link Estate#start} or {@link Estate#check}.
   * It closes the window on state {@link Estate#finit}.
   */
  protected void exec() {
    if(FcmdCopyCmprDel.this.state == Estate.start || FcmdCopyCmprDel.this.state == Estate.check) { //widgg.sCmd.equals("check")){
      FcmdCopyCmprDel.this.progress.clean();
      if(occupyProgress()) {
        if(FcmdCopyCmprDel.this.cmd == Ecmd.compare){
          execCompare(0);
        } else if(FcmdCopyCmprDel.this.cmd == Ecmd.search){ // bFineSelect? todo debug it
          execSearch();
        } else if(FcmdCopyCmprDel.this.cmd == Ecmd.delete){
          execDel();
        } else if(FcmdCopyCmprDel.this.cmd == Ecmd.copy || FcmdCopyCmprDel.this.bFineSelect){
          execCheck();
        } else if(FcmdCopyCmprDel.this.cmd == Ecmd.move){
          execCheck();
        }
      }
    } else if(FcmdCopyCmprDel.this.state == Estate.checked || FcmdCopyCmprDel.this.state == Estate.ready) { //widgg.sCmd.equals("copy")) {
      if(occupyProgress()) {
        switch(FcmdCopyCmprDel.this.cmd){
        case copy: execCopy(); break;
        case move: execMove(); break;
        case delete: execDel(); break;
        case compare: execCompare(0); break;
        case search: execSearch(); break;
        }//switch
      }
    } else if(FcmdCopyCmprDel.this.state == Estate.finit) { //widgg.sCmd.equals("close")){
      closeWindow();
    } else {
        //should be state pause
//          this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.cont, modeCopy()); //triggerStateMachine(evSrc, FileRemote.Cmd.docontinue);
    }
    
  }
  
  
  /**Operation associated to the Ok or Exec key: {@link #widgBtnExec} associated to its {@link #actionButtonOk}.
   * It executes the operation set by construction (via {@link #cmd} and depending from {@link #state}.
   * It calls {@link #execCheck()} if the state is {@link Estate#start} or {@link Estate#check}.
   * It closes the window on state {@link Estate#finit}.
   */
  protected void execBtnCmpr (int cond) {
    if(FcmdCopyCmprDel.this.state == Estate.start || FcmdCopyCmprDel.this.state == Estate.check) { //widgg.sCmd.equals("check")){
      FcmdCopyCmprDel.this.progress.clean();
      if(occupyProgress()) {
        execCompare(cond);
      }
    } else {
        //should be state pause
//          this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.cont, modeCopy()); //triggerStateMachine(evSrc, FileRemote.Cmd.docontinue);
    }
    
  }
  
  

  /**Starts the execution of mark in another thread. Note that the mark works with the walk-file algorithm
   * and refreshes the files therewith. The #this.progress is used as callback.
   * See {@link FileRemote#refreshAndMark(int, boolean, String, int, int, FileRemoteWalkerCallback)}.
   */
  final void execCheck(){
    setTexts(Estate.busyCheck);
    this.widgCopyState.setText("busy-check");
    this.widgBtnExec.setText("busy-check");
    this.widgButtonCheck.setState(GralButton.State.Disabled);
    this.widgButtonEsc.setText("abort");
    String sSrcMask= this.widgSrcSelection.getText();
    //FileSystem: acts in other thread.
    //regards mark in first level ?
    //int depths = this.srcSomeFiles ? -Integer.MAX_VALUE : Integer.MAX_VALUE;
    //long bMarkSelect = this.srcSomeFiles ? 0x200000000L + FileMark.select : 0;
    int bMarkSelect = this.widgButtonSetSymbolicLinks.isOn() ? 0 :  FileMark.ignoreSymbolicLinks;
    int ix = 0;
    boolean bCont = sSrcMask.startsWith("?");
    while(bCont && ++ix < sSrcMask.length()) {
      char cs = sSrcMask.charAt(ix);
      switch(cs) {                       // for file                  for directory
      case '#': bMarkSelect |= FileMark.cmpContentNotEqual | FileMark.cmpFileDifferences; break;
      case '^': bMarkSelect |= FileMark.cmpTimeGreater     | FileMark.cmpFileDifferences;    break;
      case 'v': bMarkSelect |= FileMark.cmpTimeLesser      | FileMark.cmpFileDifferences;    break;
      case '+': bMarkSelect |= FileMark.cmpAlone           | FileMark.cmpMissingFiles;    break;
      case '!': bMarkSelect |= FileMark.select             | FileMark.selectSomeInDir;    break;
      default: bCont = false;                  // ix refers the first char which is not ?#+!
      }
    }
    sSrcMask = sSrcMask.substring(ix).trim();
    if(sSrcMask.isEmpty()) {
      sSrcMask = null;
      //sSrcMask = "**/*";
    }
    //====>
    //srcFile.refreshAndMark(bFirstSelect, sSrcMask, bMarkSelect, depths, callbackFromFilesCheck, this.progress);
    this.progress.clean();
    this.srcDir.refreshAndMark(false, 0, FileMark.resetNonMarked | FileMark.select, FileMark.resetNonMarked | FileMark.selectSomeInDir, sSrcMask, bMarkSelect
        , this.callbackFromFilesCheck, this.progress.evBack);
    this.widgCopyNameDst.setText(this.srcDir.getStateDevice());
    this.bFirstSelect = false;
  }


  final protected void execDel(){
    String selectMask = this.widgSrcSelection.getText();
    if(this.srcDir !=null) {
      assert(selectMask.length()>0);             // some files should be selected in
      if(selectMask.startsWith("?")) {
        this.widgCopyState.setText("todo deal with marked files to delete");
      } else {
        this.srcDir.deleteFilesDirTree(false, 999, selectMask, FileMark.ignoreSymbolicLinks, this.progress.evBack);
      }
    } 
    else if(this.srcFile !=null) {
      this.srcFile.delete(this.progress.evBack);
    }
    // if the progressEv receives "done" or an error, it changes the dialog texts. Especially on done offer 'close' on exec button 
  }



  final protected void execMove() {
    String sDst = this.widgDstDir.getText();
    String sDstDir;
    int posWildcard = sDst.indexOf('*');
    if(posWildcard >0) {
      int posSep = sDst.lastIndexOf('/', posWildcard);
      sDstDir = sDst.substring(0, posSep+1);
    } else {
      sDstDir = sDst;
    }
    if(FileFunctions.isAbsolutePathOrDrive(sDstDir)) {
    } else {
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
    this.widgBtnExec.setText("busy-copy");
    this.widgButtonEsc.setText("abort");
    String sDst = this.widgDstDir.getText();
    String[] sDstFileMaskRet = new String[1];
    //this.dirDst = FileRemote.getDirFileDst(sDst, this.srcDir, sDstFileMaskRet);
    this.sFileMaskDst = this.widgDstFileModification.getText(); //sDstFileMaskRet[0];
    int posStar = this.sFileMaskDst.indexOf('*');
    if(this.srcFile !=null && !this.srcFile.isDirectory()) {
      //------------------------------------------- copy only one file
      FileRemote dstFile;
      if(posStar ==-1) {
        dstFile = this.dirDst.child(this.sFileMaskDst);
      } else {
        dstFile = null; //todo
      }
      this.srcFile.copyTo(dstFile, this.progress.evBack);
    } else {
      if(this.filesToCopy.size()>0) {
        for(FileRemote fileSrc: this.filesToCopy) {
        //======>>>>
          execCopy(fileSrc);
        }
      } else {
        //======>>>>
        if(this.srcDir !=null) {
          int selectMark = FileMark.select | FileMark.selectSomeInDir;
          int resetMark = FileMark.resetMark + selectMark;
          this.srcDir.copyDirTreeTo(false, this.dirDst, 0, resetMark, resetMark, null, selectMark, this.progress.evBack);
        } else {
          //TODO maybe evaluate
          final String sName;
          int posMask;
          //if(this.fileDst !=null && this.fileDst.exists()) {
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

          this.srcFile.copyTo(this.fileDst, null);
        }
      }
    }
    setTexts(Estate.busy);
  }


  /**calls {@link FileRemote#copyDirTreeTo(FileRemote, int, String, int, FileRemoteProgressEvData)}
   * or {@link FileRemote#copyTo(FileRemote, org.vishia.fileRemote.FileRemote.CallbackEvent)}
   * with the given filedir for the {@link #dirDst} or {@link #fileDst}.
   * @param filedir
   */
  final private void execCopy(FileRemote filedir) {
  }


  /**Starts the execution of compare in another thread. Note that the compare works with the walk-file algorithm
   * and refreshes the files therewith. See {@link FileRemote#refreshAndCompare(FileRemote, int, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execSearch(){
    String sSearch = this.widgDstDir.getText();
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
      String sSrcMask= this.widgSrcSelection.getText();
//      evCallback.sendEvent(FileRemote.CallbackCmd.start);  //sends to myself for showing the state,
      //it is a check of sendEvent and it should relinguish the event.
      //====>
      this.srcFile.refreshAndSearch(0, sSrcMask, 0, search, null, this.progress.evBack); //evCallback); //evCallback able to use from callback.
      //setTexts(Estate.busy);
    } else {
      this.widgCopyState.setText("evCallback hangs");
      setTexts(Estate.quest);
    }
    ///
  }



  /**Starts the execution of compare in another thread. Note that the compare works with the walk-file algorithm
   * and refreshes the files therewith. See {@link FileRemote#refreshAndCompare(FileRemote, int, String, int, org.vishia.fileRemote.FileRemote.CallbackEvent)}.
   */
  final protected void execCompare(int cond){
    //setDirFileDst();
    boolean bOk = true;
    //check whether the event is able to occupy, use it to check.
//    if(evCallback.occupyRecall(100, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//      if(evCallback.occupyRecall(1000, evSrc, evConsumerCallbackFromFileMachine, null, true) == 0){
//        System.err.println("FcmdCopyCmd event occupy hangs");
//        bOk = false;
//      }
//    }
    if(bOk) {
      String sSrcMask= this.widgSrcSelection.getText();
//      evCallback.sendEvent(FileRemote.CallbackCmd.start);  //sends to myself for showing the state,
      //it is a check of sendEvent and it should relinguish the event.
      //====>
      if(this.srcDir !=null) {
        int bMaskSel = this.widgButtonSetSymbolicLinks.isOn() ? 0 :  FileMark.ignoreSymbolicLinks;
        int modeCmpOper = cond | FileCompare.withoutLineend; 
        this.srcDir.cmprDirTreeTo(false, this.dirDst, sSrcMask, bMaskSel, modeCmpOper, this.progress.evBack);
      }
      //setTexts(Estate.busy);
    } else {
      this.widgCopyState.setText("evCallback hangs");
      setTexts(Estate.quest);
    }
    ///
  }



  private void setDirFileDst ( ) {
    String sDst = this.widgDstDir.getText();
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
      fileDst = this.srcDir.child(sDstDir);  //relative to source
    }
    if(fileDst.isDirectory()){ this.dirDst = fileDst; }
    else { this.dirDst = fileDst.getParentFile(); }
  }


  final protected void abortCopy(){
    //if(fileSrc !=null ) {fileSrc.abortAction();}
    this.state = Estate.start;
    this.progress.evBack.data().setAbort();
    String sDirSrc = this.widgSrcDir.getText();
    //FileRemote dirSrc = main.fileCluster.getFile(sDirSrc, null); //new FileRemote(sDirSrc);
    if(this.srcDir !=null) { this.srcDir.abortAction(); }  //to set stateMachine of copy in ready state
    else {
      System.err.println("FcmdCopyCmd: abort, dirSrc not given.");
    }
    this.filesToCopy.clear();
//    FcmdCopyCmprDel.this.evCurrentFile = null;
    //bLockSrc = false;
    //widgButtonOk.setText("close");
    //widgButtonOk.setCmd("close");
    this.progress.evBack.relinquish();
    setTexts(Estate.finit);
  }


//  /**Sets the source and destination depending on the cmd and given {@link #srcFile}, {@link #dirDst}
//   * The {@link #widgInputDst} will be not changed if {@link #bDstChanged} is set.
//   */
//  final void setTextSrcDst(){
//    //
//    //show source files, it is only to show, no functionality.
//    if(this.srcFile == null){
//      this.widgShowSrc.setText("--no source--");
//    } else {
//      if(this.sFilesSrc == null) {
//        if(this.srcFile.isDirectory()) { // && cmd == Ecmd.copy) {
//          if(this.srcSomeFiles){
//            this.widgShowSrc.setText(this.srcFile.getAbsolutePath() + "/?+");  //copy some files dir/?+ signals, there are more files but add mask into
//          } else {
//            this.widgShowSrc.setText(this.srcFile.getAbsolutePath() + "/*");  //copy a directory: show dir/* to signal, there are more files.
//          }
//        } else {
//          this.widgShowSrc.setText(this.srcFile.getAbsolutePath());         //compare, move or copy only one file: show only srcfile or srcdir
//        }
//      } else {
//        this.widgShowSrc.setText(this.srcFile.getAbsolutePath() + "/" + this.sFilesSrc);  //more as one
//      }
//    }
//    //
//    if(this.cmdWind != Ecmd.delete && this.cmdWind != Ecmd.search) {
//      if(this.fileDst == null){
//        this.bufferDstChars.setLength(0); this.bufferDstChars.append("??");
//      } else if(!this.bDstChanged) {
//        if(this.cmd == Ecmd.compare) {
//          FileRemote dirDstCmpr = this.fileDst !=null && this.fileDst.isDirectory() ? this.fileDst : this.dirDst;
//          this.bufferDstChars.setLength(0); this.bufferDstChars.append(dirDstCmpr.getPathChars()); //.append('/').append(sFileDstCopy);
//        } else {
//          this.bufferDstChars.setLength(0);
//          final FileRemote dstSet;
//          if(/*!widgButtonModeDst.isOn() || */ this.srcFile.isDirectory() && !this.fileDst.isDirectory()){
//            dstSet = this.dirDst;
//            this.bufferDstChars.append(this.dirDst.getPathChars()); //the directory of the file.
//          } else {
//            //select "dst/dst" in mode or
//            dstSet = this.fileDst;
//            this.bufferDstChars.append(this.fileDst.getPathChars()); //file or directory
//          }
//          //if(cmd == Ecmd.copy || cmd == Ecmd.move){
//          //if(dirDst == fileDst || fileSrc.isDirectory() && !fileDst.isDirectory()) {  // .. is selected
////          if(dstSet.isDirectory()) {  // .. is selected
////            if(srcSomeFiles){
////              bufferDstChars.append("/*");
////            } else {
////              String nameSrc = fileDst.getName();
////              bufferDstChars.append('/').append(nameSrc);
////            }
////          }
////          if(cmd == Ecmd.copy && srcFile != null && srcFile.isDirectory()) {
////            bufferDstChars.append("/*");
////          }
//        }
//      }
//      this.widgInputDst.setText(this.bufferDstChars);
//    }
//
//  }


  /**Set the texts to any widgets depending on the state of execution and the activated switch key.
   * @param newState
   */
  void setTexts( Estate newState) {  ////
    this.state = newState;
    //Texts depending from pressed command button:
    String[][] textSrc =
    { { "set src + dst", "set src + dst", "busy check", "set src + dst", "busy", "set src + dst" }
    , { "set dst", "set dst", "busy check", "set dst", "busy", "set dst" }
    };
    String[] textAbort = { "abort"         , "abort"       , "abort"        , "abort", "abort", "close" };

    int ix1;
    if(this.cmdWind == Ecmd.delete){
      ix1=0;
      this.cmd = Ecmd.delete;
      if(this.cmdWind != Ecmd.delete) {
        this.widgButtonMove.setState(GralButton.State.Off);
        this.widgDstDir.setEditable(false);
        this.widgDstDir.setBackColor(this.colorGrayed, 0);
      }
    } else if(this.cmdWind == Ecmd.compare){
      ix1 = 3;
      this.cmd = Ecmd.compare;
      this.widgDstDir.setEditable(true);
      this.widgDstDir.setBackColor(this.bDstChanged ? this.colorChangedText : this.colorNoChangedText, 0);
    } else if(this.cmdWind == Ecmd.search){
      ix1 = 4;
      this.cmd = Ecmd.search;
      //this.widgDstDir.setEditable(true);
      //this.widgDstDir.setBackColor(this.bDstChanged ? this.colorChangedText : this.colorNoChangedText, 0);
    } else {
      //move or copy window:
      if(this.state == Estate.finit) {
        this.widgOverwrFile.setBackColor(GralColor.getColor("lgr"), 0);
        this.widgSkipFile.setBackColor(GralColor.getColor("lgr"), 0);
      }
      if(this.widgButtonMove.isOn()) {
      ix1 = 1;
        this.cmd = Ecmd.move;
        this.widgDstDir.setEditable(true);
        this.widgDstDir.setBackColor(this.bDstChanged ? this.colorChangedText : this.colorNoChangedText, 0);
      } else {
        ix1 = 2;
        this.cmd = Ecmd.copy;
        this.widgDstDir.setEditable(true);
        this.widgDstDir.setBackColor(this.bDstChanged ? this.colorChangedText : this.colorNoChangedText, 0);
      }
    }
    if(!this.bDstChanged) {
//      setTextSrcDst();
    }
    int ix2;
    GralButton.State checkDisable;
    boolean setSrcPossible;
    switch(this.state){
      case start: setSrcPossible = true; checkDisable = GralButton.State.On; ix2= this.bFineSelect? 1 : 0; break;
      case busyCheck: setSrcPossible = false; checkDisable = GralButton.State.Disabled; ix2=2; break;
      case checked: setSrcPossible = false; checkDisable = GralButton.State.On; ix2=3; break;
      case quest:
      case busy: setSrcPossible = false; checkDisable = GralButton.State.Disabled; ix2=4; break;
      case error:
      case inactive:
      case finit: setSrcPossible = true; checkDisable = GralButton.State.On; ix2=5; break;
      default: setSrcPossible = true; checkDisable = GralButton.State.On; ix2=0;
    }
    String sTextBtnOk = this.state.sBtnOk[ix1];
    this.widgBtnExec.setText(sTextBtnOk);
    //String sTextSrc = textSrc[bLockSrc? 1: 0][ix2];
    //widgButtonCheck.setText(sTextSrc);
    if(widgButtonCheck !=null) { this.widgButtonCheck.setState(checkDisable); }
    this.widgButtonSetSrc.setState(setSrcPossible ? GralButton.State.On : GralButton.State.Disabled);
    String sTextAbort = textAbort[ix2];
    this.widgButtonEsc.setText(sTextAbort);

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
      FileRemoteProgressEvData progressEvData = FcmdCopyCmprDel.this.progress.evBack.data();
      progressEvData.clean();
      while(FcmdCopyCmprDel.this.bRunTestProgressThread) {
        progressEvData.nrFilesVisited +=1;
        synchronized(this) { try{ wait(200);} catch(InterruptedException exc) {}}
      }
      System.out.println(LogMessage.timeCurr("stop testProgressThread"));
      progressEvData.done(FileRemoteCmdEventData.Cmd.copyFile, null);
    }
  };

  TestProgressThread testProgressThread;


  /**Invoked with a time order.
   *
   */
  void showCurrentProcessedFileAndDir(FileRemoteProgressEvData order) //FileRemote fileProcessed, int zFiles, boolean bDone) {
  { StringBuilder u = new StringBuilder(100);
    if(order.currFile !=null) {
      this.widgCopyDirDst.setText(order.currFile.getParent());
      this.widgCopyNameDst.setText(order.currFile.getName());
    } else {
      this.widgCopyDirDst.setText("?");
      this.widgCopyNameDst.setText("?");
    }
    this.zFiles = order.nrofFilesSelected;
    if(order.done()) {
      this.zBytes = order.nrofBytesAll;
      String sError = order.error();
      if(sError !=null) {
        u.append(sError);
        this.state = Estate.error;
      } else if(order.answerToCmd == FileRemoteCmdEventData.Cmd.walkRefresh) {
        u.append("checked");
        this.state = Estate.checked;
      } else if(order.answerToCmd == FileRemoteCmdEventData.Cmd.walkCompare) {
        u.append("compared ");
        this.state = Estate.start;
      } else {
        u.append("finish");
        this.state = Estate.finit;
      }
      this.progress.evBack.relinquish();
      this.progress.evBack.clean();
      setTexts(this.state);
      if(this.fileCardDst !=null) {
        this.fileCardDst.refresh();
      }
      //setTexts(Estate.finit);
    }
    else {
      this.zBytes = order.nrofBytesFileCopied;
      if(order.progressCmd !=null) {
        switch(order.progressCmd){
          case askDstOverwr:
            u.append("overwrite file? ");
            this.widgOverwrFile.setBackColor(GralColor.getColor("lng"), 0);
            this.widgSkipFile.setBackColor(GralColor.getColor("lgn"), 0);
            break;
          case askDstNotAbletoOverwr:
            u.append("cannot overwrite file? ");
            this.widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
            break;
          case askErrorCopy:
            u.append("error copy? ");
            this.widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
            break;
          case askErrorDstCreate:
            u.append("cannot create? ");
            this.widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
            break;
          case askDstReadonly:
            u.append("overwrite readonly file? ");
            this.widgOverwrFile.setBackColor(GralColor.getColor("lng"), 0);
            this.widgSkipFile.setBackColor(GralColor.getColor("lgn"), 0);
            break;
          case askErrorSrcOpen:
            u.append("cannot open sourcefile ");
            this.widgSkipFile.setBackColor(GralColor.getColor("lrd"), 0);
            break;
          case done:
            u.append("ok: "); // + ev1.nrofBytesInFile/1000000 + " M / " + ev1.nrofBytesAll + "M / " + ev1.nrofFiles + " Files");
            setTexts(Estate.finit);
            break;
  
        }
      }
    }
    u.append(Integer.toString(order.nrofFilesMarked)).append(" marked / ");
    u.append("; Files:").append(Integer.toString(this.zFiles)).append(" Bytes: ").append(Long.toString(this.zBytes));
    this.widgCopyState.setText(u.toString());
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

    class ProgressAction extends FileRemoteProgressEventConsumer {
//        "progressAction" + , FcmdCopyCmprDel.this.widgCopyState.gralMng, null) {

      public ProgressAction(String name, EventThread_ifc progressThread, EventThread_ifc cmdThread) {
        super(name, progressThread, cmdThread);
      }

      @Override protected int processEvent(FileRemoteProgressEvData progress, EventWithDst<FileRemoteCmdEventData, FileRemoteProgressEvData> evCmd) {
        showCurrentProcessedFileAndDir(progress); //this.currFile, this.nrFilesProcessed, this.bDone);
        return super.processEvent(progress, evCmd);
      }


//      @Override public EventThread_ifc evThread () {
//        return FcmdCopyCmprDel.this.widgCopyState.gralMng;
//      }

    }

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
//  }

  /**Shows the state of FileRemote, especially for debug and problems. */
  GralUserAction actionShowState = new GralUserAction("actionShowState")
  {
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(FcmdCopyCmprDel.this.srcFile !=null) { FcmdCopyCmprDel.this.widgCopyState.setText(FcmdCopyCmprDel.this.srcFile.getStateDevice()); }
        else { FcmdCopyCmprDel.this.widgCopyState.setText("no source file"); }
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
     /**Openls its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         if(!FcmdCopyCmprDel.this.widgButtonCheck.isDisabled()){
           if(occupyProgress()) {
             execCheck();
           }
         }
       }
       return true;
     }
   };



   GralUserAction actionSelNewChg = new GralUserAction("actionSelNewChg")
   {
     /**Openls its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         if(!FcmdCopyCmprDel.this.widgButtonSelNewChg.isDisabled()){
           FcmdCopyCmprDel.this.widgSrcSelection.setText("?+#^");
         }
       }
       return true;
     }
   };



   GralUserAction actionSelAll = new GralUserAction("actionSelAll")
   {
     /**Openls its fields to ask the user whether confirm.
      * @param dst The path which is selected as destination. It may be a directory or a file
      * @param src The path which is selected as source. It may be a directory or a file.
      */
     @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
     { //String sSrc, sDstName, sDstDir;
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         if(!FcmdCopyCmprDel.this.widgButtonSelNewChg.isDisabled()){
           FcmdCopyCmprDel.this.widgSrcSelection.setText("**/*");
         }
       }
       return true;
     }
   };



   void startStopTestProgressThread ( ) {
     if(this.bRunTestProgressThread) {
       this.bRunTestProgressThread = false;
     } else {
       this.bRunTestProgressThread = true;
       this.testProgressThread = new TestProgressThread();
//       progressEvData.timeOrder.activate(200);

     }
   }


   protected GralUserAction actionSetSrc = new GralUserAction("actionSetSrc") { 
     @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params) { 
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)) {
         FileRemote[] srcDirFile = getSrcDstFileDir(false, FcmdCopyCmprDel.this.widgSrcDir, FcmdCopyCmprDel.this.widgSrcSelection);
         FcmdCopyCmprDel.this.srcDir = srcDirFile[0]; FcmdCopyCmprDel.this.srcFile = srcDirFile[1];
         getDstFileDir(FcmdCopyCmprDel.this.main.getLastSelectedFileCards()[1], false);
       }
       return true;
     }
   };


   protected GralUserAction actionSetDst = new GralUserAction("actionSetDst") {
     @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params) {
       if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
         getDstFileDir(FcmdCopyCmprDel.this.main.getLastSelectedFileCards()[0], true);
       
//         GralButton widgb = (GralButton)(widgP);
//         FcmdFileCard[] lastFileCards = FcmdCopyCmprDel.this.main.getLastSelectedFileCards();
//         FcmdCopyCmprDel.this.fileCardDst = lastFileCards[0];
//         FcmdCopyCmprDel.this.fileDst = FcmdCopyCmprDel.this.fileCardDst.currentFile();
//         FcmdCopyCmprDel.this.dirDst = FcmdCopyCmprDel.this.fileCardDst.currentDir();
//
//         CharSequence sText = FcmdCopyCmprDel.this.dirDst.getAbsolutePath();
//         /*
//         if(sFilesSrc == null || sFilesSrc.isEmpty()){
//           StringBuilder u = new StringBuilder(sText);
//           sText = u.append("/").append( fileSrc.getName());
//         }
//         */
//         FcmdCopyCmprDel.this.bDstChanged = false;
//         setTextSrcDst();
       }
       return true;
     }
   };




  protected GralUserAction actionEnterTextInDst = new GralUserAction("actionSelectMask")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { if(key == KeyCode.valueChanged) {
        String text = FcmdCopyCmprDel.this.widgDstDir.getText();
        if(StringFunctions.compare(text, FcmdCopyCmprDel.this.bufferDstChars) ==0) {
          //unchanged text or the original text again:
          FcmdCopyCmprDel.this.widgDstDir.setBackColor(FcmdCopyCmprDel.this.colorNoChangedText, 0);
          FcmdCopyCmprDel.this.bDstChanged = false;
        } else {
          FcmdCopyCmprDel.this.widgDstDir.setBackColor(FcmdCopyCmprDel.this.colorChangedText, 0);
          FcmdCopyCmprDel.this.bDstChanged = true;
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
        switch(FcmdCopyCmprDel.this.modeOverwrReadonly){
        case FileRemote.modeCopyReadOnlyAks:
          FcmdCopyCmprDel.this.modeOverwrReadonly= FileRemote.modeCopyReadOnlyOverwrite;
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setText("yes ?no ?ask");
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyReadOnlyNever:
          FcmdCopyCmprDel.this.modeOverwrReadonly= FileRemote.modeCopyReadOnlyAks;
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setText("ask ?yes ?no");
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lam"), 0);
          break;
        default:
          FcmdCopyCmprDel.this.modeOverwrReadonly= FileRemote.modeCopyReadOnlyNever;
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setText("no ?ask ?yes");
          FcmdCopyCmprDel.this.widgdChoiceOverwrReadOnly.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        }
      }
      return true;
  } };



  GralUserAction actionOverwrDate = new GralUserAction("actionOverwrDate")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(FcmdCopyCmprDel.this.modeOverwrDate){
        case FileRemote.modeCopyExistAsk:
          FcmdCopyCmprDel.this.modeOverwrDate= FileRemote.modeCopyExistNewer;
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setText("newer ?older?all ?no?ask");
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lpk"),0);
          break;
        case FileRemote.modeCopyExistNewer:
          FcmdCopyCmprDel.this.modeOverwrDate= FileRemote.modeCopyExistOlder;
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setText("older ?all ?no?ask?newer");
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lbl"),0);
          break;
        case FileRemote.modeCopyExistOlder:
          FcmdCopyCmprDel.this.modeOverwrDate= FileRemote.modeCopyExistAll;
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setText("all ? no?ask?newer?older");
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lgn"),0);
          break;
        case FileRemote.modeCopyExistAll:
          FcmdCopyCmprDel.this.modeOverwrDate= FileRemote.modeCopyExistSkip;
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setText("no ?ask?newer?older?all");
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lrd"),0);
          break;
        default:
          FcmdCopyCmprDel.this.modeOverwrDate= FileRemote.modeCopyExistAsk;
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setText("ask ?newer?older?all ?no");
          FcmdCopyCmprDel.this.widgdChoiceOverwrExists.setBackColor(GralColor.getColor("lam"),0);
          break;
        }
      }
      return true;
  } };



  GralUserAction actionCreateCopy = new GralUserAction("actionButtonCreateCopy")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(FcmdCopyCmprDel.this.modeCreateCopy){
        case FileRemote.modeCopyCreateAsk:
          FcmdCopyCmprDel.this.modeCreateCopy = FileRemote.modeCopyCreateYes;
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setText("yes ?no ?ask");
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setBackColor(GralColor.getColor("lgn"), 0);
          break;
        case FileRemote.modeCopyCreateYes:
          FcmdCopyCmprDel.this.modeCreateCopy = FileRemote.modeCopyCreateNever;
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setText("no ?ask ?yes");
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setBackColor(GralColor.getColor("lrd"), 0);
          break;
        default:
          FcmdCopyCmprDel.this.modeCreateCopy = FileRemote.modeCopyCreateAsk;
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setText("ask ?yes ?no");
          FcmdCopyCmprDel.this.widgdChoiceCreateNew.setBackColor(GralColor.getColor("lam"), 0);
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
         setTexts(FcmdCopyCmprDel.this.state);
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
  protected GralUserAction actionButtonOk = new GralUserAction("actionButtonOk")
  { @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdCopyCmprDel.this.exec();
      }
      return true;
    }
  };


  protected GralUserAction actionButtonCmprFast = new GralUserAction("actionButtonCmprFast")
  { @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdCopyCmprDel.this.execBtnCmpr(FileCompare.onlyTimestamp);
      }
      return true;
    }
  };



  protected GralUserAction actionButtonCmprContent = new GralUserAction("actionButtonCmprContent")
  { @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdCopyCmprDel.this.execBtnCmpr(0);
      }
      return true;
    }
  };



  protected GralUserAction actionButtonAbort = new GralUserAction("actionButtonAbort") {
    @Override public boolean exec(int key, GralWidget_ifc widgg, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(FcmdCopyCmprDel.this.state){ //inactive, start, checked, busyCheck, busy, quest, error, finit
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
  protected GralUserAction actionOverwriteFile = new GralUserAction(this.sTextExecuteForFile)
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(FcmdCopyCmprDel.this.progress.evBack.data().progressCmd == FileRemoteProgressEvData.ProgressCmd.askDstOverwr
             || FcmdCopyCmprDel.this.progress.evBack.data().progressCmd == FileRemoteProgressEvData.ProgressCmd.askDstReadonly) {
          FcmdCopyCmprDel.this.progress.evBack.data().modeCopyOper = modeCopy();
          FcmdCopyCmprDel.this.progress.evBack.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //.overwr );
          FcmdCopyCmprDel.this.widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
          FcmdCopyCmprDel.this.widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
        } else {
//          FcmdCopyCmprDel.this.progress.evAnswer.send(FileRemoteProgressEvent.Answer.cont, modeCopy() ); //triggerStateMachine(evSrc, FileRemote.Cmd.docontinue);
        }
        //widgCopyNameDst.setText("");
        FcmdCopyCmprDel.this.widgCopyState.setText("skipped");
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
          FcmdCopyCmprDel.this.progress.evBack.data().modeCopyOper = modeCopy();
          FcmdCopyCmprDel.this.progress.evBack.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //.abortCopyFile ); //;

//        }
        FcmdCopyCmprDel.this.widgCopyNameDst.setText("");
        FcmdCopyCmprDel.this.widgCopyState.setText("skipped");
        FcmdCopyCmprDel.this.widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
        FcmdCopyCmprDel.this.widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
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
        FcmdCopyCmprDel.this.progress.evBack.data().modeCopyOper = modeCopy();
        FcmdCopyCmprDel.this.progress.evBack.data().setAnswer(FileRemoteProgressEvData.ProgressCmd.nok); //abortCopyDir ); //;
        FcmdCopyCmprDel.this.widgSkipFile.setBackColor(GralColor.getColor("wh"), 0);
        FcmdCopyCmprDel.this.widgOverwrFile.setBackColor(GralColor.getColor("wh"), 0);
      }
      return true;
    }
  };



  protected GralUserAction actionSelectMask = new GralUserAction("actionSelectMask")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params)
    { String sContent = ((GralTextField_ifc)widgi).getText();
      boolean empty = sContent.isEmpty();
      System.out.println("actionSelectMask");
      if(empty == FcmdCopyCmprDel.this.bFineSelect){
        FcmdCopyCmprDel.this.bFineSelect = !empty;
        setTexts(FcmdCopyCmprDel.this.state);
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
        this.widgCopyState.setText("error");
        this.widgBtnExec.setText("quit");
        //widgButtonOk.setCmd("quit");
      } else {
        this.widgBtnExec.setText("close");
        //widgButtonOk.setCmd("close");
      }
      this.widgButtonEsc.setText("close");
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
    @Override public void start(FileRemote startDir, FileRemoteCmdEventData co) {
      FcmdCopyCmprDel.this.fileProcessed = null;
    }

    @Override public Result offerParentNode(FileRemote file, Object data, Object filter) {
      if(file.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
      } else {
        FcmdCopyCmprDel.this.dirProcessed = file;
        return Result.cont;
      }
    }

    /**Finish a directory, check whether a file panel should be refreshed.
     * @see org.vishia.util.SortedTreeWalkerCallback#finishedParentNode(java.lang.Object, org.vishia.util.SortedTreeWalkerCallback.Counters)
     */
    @Override public Result finishedParentNode(FileRemote dir, Object data, Object oWalkInfo) {
      FcmdCopyCmprDel.this.main.refreshFilePanel(dir);
      return Result.cont;
    }



    @Override public Result offerLeafNode(FileRemote file, Object info) {
      boolean bShow = (FcmdCopyCmprDel.this.fileProcessed == null);
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
      FcmdCopyCmprDel.this.main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
      setTexts(Estate.checked);
    }


  };



  FileRemoteWalkerCallback callbackFromFilesExec = new FileRemoteWalkerCallback() {
    
    @Override public void start(FileRemote startDir, FileRemoteCmdEventData co) {  }

    @Override public Result offerParentNode(FileRemote file, Object data, Object filter) {
      if(file.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
        //but it does not activate this, deselect before
      } else {
        return Result.cont;
      }
    }

    @Override public Result finishedParentNode(FileRemote dir, Object data, Object oWalkInfo) {
      if(dir.isSymbolicLink()) {
        return Result.skipSubtree;  //do not handle symbolic links for cmp, copy and delete
      } else {
        String path = dir.getAbsolutePath();
        showFinishState(path);
        FcmdCopyCmprDel.this.main.refreshFilePanel(dir);
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
      FcmdCopyCmprDel.this.main.refreshFilePanel(startFile.getParentFile());  //The start file is any file or directory in parent. A directory is refreshed by finishParentNode already.
    }


    @Override public String toString(){ return "callbackFromFilesExec"; }

  };

                                  //del            move            copy               cmp            search 
  enum Estate{ inactive(-1, new String[] { "check del"  , "move"        , "check copy"    , "compare"     , "search" }), 
    start(0, new String[]     { "check del"  , "move"        , "check copy"    , "compare"     , "search" }), 
    check(1, new String[]     { "check del"  , "move"        , "check copy"    , "compare"     , "search" }), 
    busyCheck(2, new String[] { "busy check" , "busy check"  , "busy check"    , "compare"     , "search" }), 
    checked(3, new String[]   { "del checked", "move checked", "copy checked"  , "compare"     , "search" }), 
    ready(4, new String[]     { "delete"     , "move"        , "copy"          , "compare"     , "search" }), 
    busy(5, new String[]      { "pause del"  , "pause move"  , "pause copy"    , "pause cmpr"  , "pause" }), 
    quest(6, new String[]     { "cont del"   , "cont move"   , "cont copy"     , "cont cmpr"   , "cont" }), 
    error(7, new String[]     { "abort del"  , "abort move"  , "abort copy"    , "abort cmpr"  , "abort" }), 
    finit(8, new String[]     { "close del"  , "close move"  , "close copy"    , "close cmpr"  , "close" })
    ;
    Estate(int ixText, String[] sBtnOk) { this.ixText = ixText; this.sBtnOk = sBtnOk; }
    public final int ixText;
    public final String[] sBtnOk;
  };

  enum Ecmd{
    copy("copy", 2),
    move("move", 1),
    delete("del", 0),
    compare("cmp", 3),
    search("search", 4);
    Ecmd(String name, int ix){ this.name = name; this.ix = ix; }
    public final String name;
    public final int ix;
  };

  void stop(){}

}
