package org.vishia.commander;

import java.io.File;
import java.io.FileNotFoundException;
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
import org.vishia.util.FileRemoteAccessor;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class contains all functionality to execute copy and move for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdCopyCmd
{
  /**Version and History
   * <ul>
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
  
  GralButton widgdMove;
  
  GralButton widgLastSrc, widgGetPath;

  GralValueBar widgProgressFile;
  GralValueBar widgProgressAll;
  
  GralButton widgButtonOk, widgButtonEsc;

  /**Mode overwrite readonly. '?'-ask, y-overwrite n-skip, don't ask
   */
  char modeOverwrReadonly;
  
  /**Mode overwrite with timestamp. 'w'-newer, o-older, n-never, ?-ask. */
  char modeOverwrDate;
  
  /**Mode create new file while copying. 'n'-no, y-yes, a-ask. */
  char modeCreateCopy;
  
  /**The file card where the directory content is shown where the files will be copied to, the destination. */
  FcmdFileCard fileCardSrc, fileCardDst;
  
  FileRemote fileSrcDir, fileDstDir;

  /**Content from the input fields while copy is pending. */
  String sSrc, sDstDir, sDstName;
  
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
  
  boolean bSkipFile, bSkipDir;
  
  
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
    
    main.gralMng.setPosition(0.5f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f);
    //main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyFrom = main.gralMng.addTextField("copyFrom", false, "source path", "t");
    widgCopyFrom.setBackColor(GralColor.getColor("am"),0);
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -10, 0, 'd', 0.3f);
    widgFromConditions = main.gralMng.addTextField("copyCond",true, "select src files: mask*:*.ext / 2012-08-05..06", "t");
    
    main.gralMng.setPosition(GralPos.same, GralPos.size +3.2f, -9, -1, 0, 'r',1);
    widgLastSrc = main.gralMng.addSwitchButton("lastSrc", "? lock src", "lock src", GralColor.getColor("wh"), GralColor.getColor("lgn"));
    widgLastSrc.setActionChange(actionLastSrc);
    
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f);
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", true, "destination directory:", "t");
    //widgCopyDirDst.setBackColor(GralColor.getColor("am"),0);
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -14, 0, 'r', 1);
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", true, "destination filename:", "t");
    
    main.gralMng.setPosition(GralPos.same, GralPos.size +3.2f, -13, -1, 0, 'r',1);
    widgGetPath = main.gralMng.addButton("getPath", actionConfirmCopy, "set src+dst" );
    
    main.gralMng.setPosition(GralPos.refer + 3.5f, GralPos.size +3.2f, 1, -1, 0, 'd', 0.3f);
    widgCopyState = main.gralMng.addTextField("copyStatus", false, "state", "t");
    
    main.gralMng.setPosition(GralPos.refer+1.5f, GralPos.size -1.5f, 1, 20, 0, 'r', 1);
    main.gralMng.addText("Overwrite readonly");
    main.gralMng.addText("Overwrite - date");
    main.gralMng.addText("Create new file");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 20, 0, 'r',1);
    //widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, "overwr file");
    //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, 35, 0, 'r');
    widgdOverwrReadOnly = main.gralMng.addButton("overwritero", actionOverwrReadonly, null, null,"ask ?no ?yes");
    //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 33, -1, 0, 'r');
    widgdOverwrDate = main.gralMng.addButton("copyOverwriteReadonly", actionOverwrDate, null, null, "older ?no ?newer ?all");
    widgdCreateNew = main.gralMng.addButton("copyCreate", actionCreateCopy, null, null, "only overwr ? yes");

    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r', 1);
    widgOverwrFile = main.gralMng.addButton("copyOverwrite", null, null, null, "overwr file");
    widgSkipFile = main.gralMng.addButton("copyskip", actionButtonSkipFile, null, null, "skip file");
    //main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 0, 'r', 1);
    widgSkipDir = main.gralMng.addButton("copySkipDir", actionButtonSkipDir, null, null, "skip dir");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, -13, -1, 0, 'd', 1);
    widgdMove = main.gralMng.addSwitchButton("copyMove", "Copy/ ?move", "Move/ ?copy", GralColor.getColor("wh"), GralColor.getColor("gn"));
    widgdMove.setActionChange(actionSwitchButtonMove);
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    widgButtonEsc = main.gralMng.addButton("copyEsc", actionButtonCopy, "esc", null, "close");
    main.gralMng.setPosition(-4, GralPos.size +1, 7, -14, 0, 'd', 1);
    widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null);
    widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null);
    main.gralMng.setPosition(-4, GralPos.size+3, -13, -1, 0, 'r');
    widgButtonOk = main.gralMng.addButton("copyOk", actionButtonCopy, "close", null, "close");
  
  }
  
  
  void actionConfirmCopy(){
    
  }

  protected GralUserAction actionLastSrc = new GralUserAction() ///
  { @Override public boolean exec(int key, GralWidget_ifc widgP, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        GralButton widgb = (GralButton)(widgP);
        if(widgb.isOn()){
          widgGetPath.setText("get dst");
        } else {
          widgGetPath.setText("get src+dst");
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
  
  
  GralUserAction actionOverwrReadonly = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeOverwrReadonly){
        case '?': 
          modeOverwrReadonly= 'y'; 
          widgdOverwrReadOnly.setText("yes ?no ?ask");
          widgdOverwrReadOnly.setBackgroundColor(GralColor.getColor("lgn"));
          break;
        case 'n': 
          modeOverwrReadonly= '?'; 
          widgdOverwrReadOnly.setText("ask ?yes ?no");
          widgdOverwrReadOnly.setBackgroundColor(GralColor.getColor("lam"));
          break;
        default:
          modeOverwrReadonly= 'n'; 
          widgdOverwrReadOnly.setText("no ?ask ?yes");
          widgdOverwrReadOnly.setBackgroundColor(GralColor.getColor("lrd"));
          break;
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionOverwrDate = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeOverwrDate){
        case 'o': 
          modeOverwrDate= 'n'; 
          widgdOverwrDate.setText("newer ?older ? all ?ask");
          widgdOverwrDate.setBackgroundColor(GralColor.getColor("lpk"));
          break;
        case 'a': 
          modeOverwrDate= 'o'; 
          widgdOverwrDate.setText("older ? all ?ask ?newer");
          widgdOverwrDate.setBackgroundColor(GralColor.getColor("lbl"));
          break;
        case '?':
          modeOverwrDate= 'a'; 
          widgdOverwrDate.setText("all ?ask ?newer?older");
          widgdOverwrDate.setBackgroundColor(GralColor.getColor("lgn"));
          break;
        default:
          modeOverwrDate= '?'; 
          widgdOverwrDate.setText("ask ?newer ?older ?all");
          widgdOverwrDate.setBackgroundColor(GralColor.getColor("lam"));
          break;
        }
      }
      return true;
  } };
  
  
  
  GralUserAction actionCreateCopy = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        switch(modeCreateCopy){
        case 'n': 
          modeCreateCopy= 'y'; 
          widgdCreateNew.setText("yes ?no ?ask");
          widgdCreateNew.setBackgroundColor(GralColor.getColor("lgn"));
          break;
        case '?': 
          modeCreateCopy= 'n'; 
          widgdCreateNew.setText("no ?ask ?yes");
          widgdCreateNew.setBackgroundColor(GralColor.getColor("lrd"));
          break;
        default:
          modeCreateCopy= '?'; 
          widgdCreateNew.setText("ask ?yes ?no");
          widgdCreateNew.setBackgroundColor(GralColor.getColor("lam"));
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
  GralUserAction actionConfirmCopy = new GralUserAction()
  {
    /**Opens the confirm-copy window and fills its fields to ask the user whether confirm.
     * @param dst The path which is selected as destination. It may be a directory or a file
     * @param src The path which is selected as source. It may be a directory or a file.
     */
    @Override public boolean userActionGui(int key, GralWidget infos,
        Object... params)
    { //String sSrc, sDstName, sDstDir;
      String state = widgButtonOk.getCmd();
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(state.equals("close") || state.equals("check")) {
          //only if it is ready to check, get the files.
          widgButtonOk.setText("check");
          widgButtonOk.setCmd("check");
  
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
          if(lastFileCards[0] !=null){ ///
            if(widgLastSrc.isOn()){  //state src lock
              fileCardDst = lastFileCards[0];
              fileDstDir = FileRemote.fromFile(fileCardDst.getCurrentDir());
              sDstDir = fileDstDir.getAbsolutePath();
              widgCopyDirDst.setText(sDstDir);
            } else {
              fileCardSrc = lastFileCards[0];
              fileCardDst = lastFileCards[1];
              
              StringBuilder uFileSrc = new StringBuilder();
              widgLastSrc.setState(GralButton.kOff);  //maybe disabled, set off.
              fileSrcDir = FileRemote.fromFile(fileCardSrc.getCurrentDir());
              String sDirSrc = fileSrcDir.getAbsolutePath();
              List<File> listFileSrc = fileCardSrc.getSelectedFiles();
              if(listFileSrc.size()==0){ //nothing selected
                listFileSrc.add(fileCardSrc.currentFile);  
              }
              String sSep = "";
              for(File srcFile : listFileSrc){
                uFileSrc.append(sSep).append(srcFile.getName());
                sSep = ", "; //For next one.
                FileRemote fileSrc = FileRemote.fromFile(srcFile);
                //filesToCopy.add(fileSrc);
              }
              sDstName = listFileSrc.size() >1 ? "*" 
                         : listFileSrc.size() ==1 ? listFileSrc.get(0).getName() : "??";
              sSrc = fileSrcDir.getAbsolutePath() + "/" + sDstName;
              if(fileCardDst !=null){
                fileDstDir = FileRemote.fromFile(fileCardDst.getCurrentDir());
                sDstDir = fileDstDir.getAbsolutePath();
              } else {
                fileDstDir = null;
                sDstDir = "??";
              }
              widgCopyFrom.setText(sDirSrc);
              widgFromConditions.setText(uFileSrc);
            }
          } else {
            fileSrcDir = null;
            fileDstDir = null;
            
            //listFileSrc = null;
            sSrc = "???";
            sDstName = "??";
            sDstDir = "??";
            widgCopyFrom.setText(sSrc);
          }
          widgCopyDirDst.setText(sDstDir);
          widgCopyNameDst.setText(sDstName);
          widgButtonOk.setText("check");
          widgButtonOk.setCmd("check");
          widgCopyState.setText("check?", 0);
          widgdMove.setValue(GralMng_ifc.cmdSet, 0, 0);
          zFiles = zBytes = 0;
        }
        windConfirmCopy.setWindowVisible(true);
        //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
        main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");
      }
      return true;
   }  };
  
  
  
  
  /**Action for any button of the confirm-copy window.
   * in a loop FileRemote.copyTo(dst) is called for any selected file. It may be one file, 
   * or more files in more selected lines.
   * The copy process itself is executed in an own thread in management of the FileRemote implementation,
   * see {@link org.vishia.util.FileRemoteAccessorLocalFile#addCommission(org.vishia.util.FileRemoteAccessor.Commission)}
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
  protected GralUserAction actionButtonCopy = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(widgg.sCmd.equals("check")){
          widgCopyState.setText("busy-check");
          widgButtonOk.setText("busy-check");
          widgButtonOk.setCmd("busy");
          widgButtonEsc.setText("abort");
          widgButtonEsc.setCmd("abort");
          
          String sDirSrc = widgCopyFrom.getText();
          FileRemote dirSrc = new FileRemote(sDirSrc);
          String sFilesSrc= widgFromConditions.getText();
          List<File> listFileSrc = new LinkedList<File>();
          //
          //check the amount of files in field widgFromConditions
          if(sFilesSrc.contains("*")){
            try{ FileSystem.addFileToList(dirSrc, sFilesSrc, listFileSrc);}
            catch(Exception exc){
              System.err.println(exc.getMessage());
            }
          } else if(sFilesSrc.contains(", ")){
            String[] sFilesSrc1 = sFilesSrc.split(", ");
            for(String sFileSrc: sFilesSrc1){
              listFileSrc.add(new FileRemote(sDirSrc, sFileSrc));
            }
          } else { //a simple file name
            listFileSrc.add(new FileRemote(dirSrc, sFilesSrc));
          }
          //
          //
          nrofFilesCheck = 0;
          for(File fileSrc1: listFileSrc){
            FileRemote fileSrc = FileRemote.fromFile(fileSrc1);
            FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, null, callbackCheck, null);
            listEvCheck.add(callback);
            fileSrc.check(callback);   //callback.use() will be called on response
            nrofFilesCheck +=1;
          }
          /*
          for(FileRemote fileSrc : filesToCopy){
            FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(fileSrc, success, null);
            listEvCheck.add(callback);
            fileSrc.check(callback);   //callback.use() will be called on response
          }
          */
        } else if(widgg.sCmd.equals("copy")) {
          //Starts the copy process (not move)
          widgCopyState.setText("busy-copy");
          widgButtonOk.setText("busy-copy");
          widgButtonOk.setCmd("busy");
          widgButtonEsc.setText("abort");
          widgButtonEsc.setCmd("abort");
          sDstName = widgCopyNameDst.getText();
          sDstDir = widgCopyDirDst.getText();
          if(!FileSystem.isAbsolutePathOrDrive(sDstDir)) {
            int posSrcDir = sSrc.lastIndexOf('/');
            String sSrcDir = sSrc.substring(0, posSrcDir +1);  //inclusive /
            sDstDir = sSrcDir + sDstDir;
          }
          //filesToCopyLast.clear();
          //
          //loop calls FileRemote.copyTo(dst) for any selected file. 
          for(FileRemote fileSrc : filesToCopy){
            //filesToCopyLast.add(fileSrc);
            int posWildcard = sDstName.indexOf('*');
            final String nameDst1;
            if(posWildcard >=0){
              nameDst1 = sDstName.substring(0, posWildcard) + fileSrc.getName() + sDstName.substring(posWildcard +1); 
            } else {
              nameDst1 = sDstName;
            }
            FileRemote fileDst = new FileRemote(sDstDir, nameDst1);

            
            if(fileSrc.sameDevice(fileDst)){  //all files in the standard file system of the computer, network files too!
              //Note: create a callback event without source, it is not occupied yet!
              //The callback event contains a command event.
              FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, fileSrc, fileDst, success, null);
              listEvCopy.add(callback);
              int mode = 0;
              switch(modeCreateCopy){
              case 'n': mode |= FileRemote.modeCopyCreateNever; break;
              case 'y': mode |= FileRemote.modeCopyCreateYes; break;
              case '?': mode |= FileRemote.modeCopyCreateAsk; break;
              }
              switch(modeOverwrReadonly){
              case 'n': mode |= FileRemote.modeCopyReadOnlyNever; break;
              case 'y': mode |= FileRemote.modeCopyReadOnlyOverwrite; break;
              case '?': mode |= FileRemote.modeCopyReadOnlyAks; break;
              }
              switch(modeOverwrDate){
              case 'n': mode |= FileRemote.modeCopyExistNewer; break;
              case 'o': mode |= FileRemote.modeCopyExistOlder; break;
              case 'a': mode |= FileRemote.modeCopyExistAll; break;
              case '?': mode |= FileRemote.modeCopyExistAsk; break;
              }
              if(widgdMove.isOn()){
                fileSrc.moveTo(fileDst, callback);  //callback.use() will be called on response
              } else {
                //The copy:
                fileSrc.copyTo(fileDst, callback, mode);  //callback.use() will be called on response
              }
            } else { //Only if special driver for FileRemote.
              //TODO.
              //read content and write content.
            }
                  
          }
        } else if(widgg.sCmd.startsWith("abort")) {
          for(FileRemote.CallbackEvent ev: listEvCopy){
            ev.abort(FileRemote.Cmd.abortAll);
            //ev.sendEvent(FileRemote.cmdAbortAll);
          }
          listEvCheck.clear();
          listEvCopy.clear();
          filesToCopy.clear();
          FcmdCopyCmd.this.evCurrentFile = null;
          widgButtonOk.setText("close");
          widgButtonOk.setCmd("close");
            
        } else if(widgg.sCmd.startsWith("quit")) {
          filesToCopy.clear();
          widgButtonOk.setText("close");
          widgButtonOk.setCmd("close");
            
        } else if(widgg.sCmd.equals("esc")){
          //it is an abort too!
          //clears the list.
          //If some commissions are ordered, it can't be countermanded.
          //but no reaction should be done. Forgot this state.
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          widgButtonOk.setText("close");
          widgButtonOk.setCmd("close");
          widgButtonEsc.setText("close");
          widgButtonEsc.setCmd("close");
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        } else if(widgg.sCmd.equals("close")){
          widgButtonOk.setText("close");
          widgButtonOk.setCmd("close");
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        }
      }
      return true;
    }
  };
  

  
  
  /**This action is used to skip over the current showed file while copying. Copying of the selected file
   * should be aborted and that file should be deleted.
   * 
   */
  protected GralUserAction actionButtonSkipFile = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        bSkipFile = true;  
      }
      return true;
    } 
  };
  
  
  /**This action is used to skip over the current showed file while copying. Copying of the selected file
   * should be aborted and that file should be deleted.
   * 
   */
  protected GralUserAction actionButtonSkipDir = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        bSkipDir = true;  
      }
      return true;
    } 
  };
  
  
  GralUserAction actionSwitchButtonMove = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    { 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(widgdMove.isOn()){
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
  
  
  EventConsumer callbackCheck = new EventConsumer("FcmdCopy-check"){
    @Override protected boolean processEvent_(Event evP)
    {
      FileRemote.CallbackEvent ev = (FileRemote.CallbackEvent)evP;
      if(listEvCheck.remove(ev)){
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
      return true;
    }
  };
  
  
  
  private void eventConsumed(Event ev, boolean ok){
    listEvCopy.remove(ev);
    int nrofPendingFiles = listEvCopy.size();
    int percent = nrofPendingFiles * 100 / filesToCopy.size();
    widgProgressAll.setValue(percent);
    if(ok){
      File file = (File)ev.getRefData();
      FileCompare.Result cmprResult = fileCardSrc.searchCompareResult(file);
      if(cmprResult !=null){
        cmprResult.setToEqual();  
      }
      fileCardSrc.fillInCurrentDir();  //maybe changed if dst = src or if moved.
      if(fileCardDst !=null){
        fileCardDst.fillInCurrentDir();
      }
    }
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
  EventConsumer success = new EventConsumer("FcmdCopy-success"){
    @Override protected boolean processEvent_(Event ev)
    {
      FileRemote.CallbackEvent ev1 = (FileRemote.CallbackEvent)ev;
      switch(ev1.getCmd()){
        case nrofFilesAndBytes:{
          FcmdCopyCmd.this.evCurrentFile = ev1;
          int percent = ev.data2 / 10;
          widgProgressAll.setValue(percent);
          widgCopyNameDst.setText(String.copyValueOf(ev1.fileName));
          widgCopyState.setText("" + ev1.nrofBytesInFile/1000000 + " Mbyte");
          if(bSkipFile){
            ev1.abort(FileRemote.Cmd.abortCopyFile );
            //ev1.sendEvent(FileRemote.cmdAbortFile);
            bSkipFile = false;
          } else if(bSkipDir){
            ev1.abort(FileRemote.Cmd.abortCopyDir);
            //ev1.sendEvent(FileRemote.cmdAbortDir);
            bSkipDir = false;
          } 
        }break;
        case error: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("error");
          bSkipDir = bSkipFile = false;
          eventConsumed(ev, false);
        }break;
        case nok: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("nok");
          bSkipDir = bSkipFile = false;
          eventConsumed(ev, false);
        }break;
        case done: {
          FcmdCopyCmd.this.evCurrentFile = null;
          widgCopyState.setText("ok");
          bSkipDir = bSkipFile = false;
          eventConsumed(ev, true);
        }break;
      }
      //windConfirmCopy.setWindowVisible(false);
      return true;
    }
    
  };

  
  
  void stop(){}
  
}
