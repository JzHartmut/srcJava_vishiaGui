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
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
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
  
  GralTextField_ifc widgCopyFrom, widgCopyDirDst, widgCopyNameDst, widgCopyState;
  
  GralWidget widgdOverwrite, widgdOverwriteReadOnly, widgdOverwriteHidden;
  
  GralButton widgdMove;

  GralValueBar widgProgressFile;
  GralValueBar widgProgressAll;
  
  GralButton widgButtonOk, widgButtonEsc;
  
  /**The file card where the directory content is shown where the files will be copied to, the destination. */
  FcmdFileCard fileCardSrc, fileCardDst;
  
  FileRemote fileSrcDir, fileDstDir;

  /**Content from the input fields while copy is pending. */
  String sSrc, sDstDir, sDstName;
  
  long zBytes, zFiles;
  
  //List<String> listFileSrc;
  
  final List<FileRemote.FileRemoteEvent> listEvCheck = new LinkedList<FileRemote.FileRemoteEvent>();
  
  final List<FileRemote.FileRemoteEvent> listEvCopy = new LinkedList<FileRemote.FileRemoteEvent>();
  
  final List<FileRemote> filesToCopy = new LinkedList<FileRemote>();
  
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
    main.gralMng.setPosition(-28, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    

    posWindConfirmCopy = main.gralMng.getPositionInPanel();
    windConfirmCopy = main.gralMng.createWindow("copyWindow", "confirm copy", GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(2, GralPos.size -2, 1, GralPos.size +45, 0, 'd');
    main.gralMng.addText("source:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyFrom = main.gralMng.addTextField("copyFrom", false, null, "t");
    widgCopyFrom.setBackgroundColor(GralColor.getColor("am"));
    main.gralMng.addText("destination dir path:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", true, null, "t");
    widgCopyDirDst.setBackgroundColor(GralColor.getColor("am"));
    //main.gralMng.addText("destination filename:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    main.gralMng.setPosition(GralPos.refer + 4, GralPos.size -3.5f, 1, 25, 0, 'r', 1);
    widgCopyNameDst = main.gralMng.addTextField("copyNameDst", true, "destination filename:", "t");
    widgCopyState = main.gralMng.addTextField("copyStatus", false, "state", "t");
    
    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "overwrite this");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, 30, 0, 'r');
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwrite", null, null, null, null, "overwrite all", "wh", "gn");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 31, -1, 0, 'r');
    widgdOverwrite = main.gralMng.addSwitchButton("copyOverwriteReadonly", null, null, null, null, "overwr all readonly", "wh", "gn");

    main.gralMng.setPosition(GralPos.refer+3.5f, GralPos.size -3, 1, 15, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "skip");
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, GralPos.size +14, 0, 'r', 1);
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "skip dir");
    widgdMove = main.gralMng.addSwitchButton("copyMove", "Copy/ ?move", "Move/ ?copy", GralColor.getColor("wh"), GralColor.getColor("gn"));
    widgdMove.setActionChange(actionSwitchButtonMove);
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    widgButtonEsc = main.gralMng.addButton("copyEsc", actionButtonCopy, "esc", null, null, "close");
    main.gralMng.setPosition(-4, GralPos.size +1, 7, -14, 0, 'd', 1);
    widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null, null);
    widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null, null);
    main.gralMng.setPosition(-4, GralPos.size+3, -13, -1, 0, 'r');
    widgButtonOk = main.gralMng.addButton("copyOk", actionButtonCopy, "check", null, null, "check");
  
  }
  
  
  void actionConfirmCopy(){
    
  }
  
  
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
    @Override
    public boolean userActionGui(int key, GralWidget infos,
        Object... params)
    { //String sSrc, sDstName, sDstDir;
      if(widgButtonOk.sCmd.equals("check")) {
        //only if it is ready to check, get the files.
        filesToCopy.clear();
        listEvCheck.clear();
        listEvCopy.clear();
        FcmdFileCard[] lastFileCards = main.getLastSelectedFileCards();
        fileCardSrc = lastFileCards[0];
        fileCardDst = lastFileCards[1];
        
        if(fileCardSrc !=null){
          fileSrcDir = FileRemote.fromFile(fileCardSrc.getCurrentDir());
          List<File> listFileSrc = fileCardSrc.getSelectedFiles();
          if(listFileSrc.size()==0){ //nothing selected
            listFileSrc.add(fileCardSrc.currentFile);  
          }
          for(File srcFile : listFileSrc){
            FileRemote fileSrc = FileRemote.fromFile(srcFile);
            filesToCopy.add(fileSrc);
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
        } else {
          fileSrcDir = null;
          fileDstDir = null;
          
          //listFileSrc = null;
          sSrc = "???";
          sDstName = "??";
          sDstDir = "??";
        }
        widgCopyFrom.setText(sSrc);
        widgCopyDirDst.setText(sDstDir);
        widgCopyNameDst.setText(sDstName);
        widgButtonOk.setText("check");
        widgButtonOk.sCmd = "check";
        widgCopyState.setText("check?", 0);
        widgdMove.setValue(GralMng_ifc.cmdSet, 0, 0);
        zFiles = zBytes = 0;
      }
      windConfirmCopy.setWindowVisible(true);
      //main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
      main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");
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
          widgButtonOk.sCmd="busy";
          widgButtonEsc.setText("abort");
          
          for(FileRemote fileSrc : filesToCopy){
            FileRemote.FileRemoteEvent callback = new FileRemote.FileRemoteEvent(fileSrc, success);
            listEvCheck.add(callback);
            fileSrc.check(callback);   //callback.use() will be called on response
          }
        } else if(widgg.sCmd.equals("copy")) {
          widgCopyState.setText("busy-copy");
          widgButtonOk.setText("busy-copy");
          widgButtonOk.sCmd="busy";
          widgButtonEsc.setText("abort");
          sDstName = widgCopyNameDst.getText();
          sDstDir = widgCopyDirDst.getText();
          if(!FileSystem.isAbsolutePathOrDrive(sDstDir)) {
            int posSrcDir = sSrc.lastIndexOf('/');
            String sSrcDir = sSrc.substring(0, posSrcDir +1);  //inclusive /
            sDstDir = sSrcDir + sDstDir;
          }
          //
          //loop calls FileRemote.copyTo(dst) for any selected file. 
          for(FileRemote fileSrc : filesToCopy){
            int posWildcard = sDstName.indexOf('*');
            final String nameDst1;
            if(posWildcard >=0){
              nameDst1 = sDstName.substring(0, posWildcard) + fileSrc.getName() + sDstName.substring(posWildcard +1); 
            } else {
              nameDst1 = sDstName;
            }
            FileRemote fileDst = new FileRemote(sDstDir, nameDst1);

            
            if(fileSrc.sameDevice(fileDst)){
              FileRemote.FileRemoteEvent callback = new FileRemote.FileRemoteEvent(fileSrc, success);
              listEvCopy.add(callback);
              if(widgdMove.isOn()){
                fileSrc.moveTo(fileDst, callback);  //callback.use() will be called on response
              } else {
                //The copy:
                fileSrc.copyTo(fileDst, callback);  //callback.use() will be called on response
              }
            } else {
              //TODO.
              //read content and write content.
            }
                  
          }
        } else if(widgg.sCmd.startsWith("abort")) {
          filesToCopy.clear();
          for(FileRemote.FileRemoteEvent ev: listEvCheck){
            ev.setCmd(FileRemote.cmdAbortAll);
            ev.sendEvent();
          }
          listEvCheck.clear();
          listEvCopy.clear();
          widgButtonOk.setText("check");
          widgButtonOk.sCmd="check";
            
        } else if(widgg.sCmd.startsWith("quit")) {
          filesToCopy.clear();
          widgButtonOk.setText("check");
          widgButtonOk.sCmd="check";
            
        } else if(widgg.sCmd.equals("esc")){
          //it is an abort too!
          //clears the list.
          //If some commissions are ordered, it can't be countermanded.
          //but no reaction should be done. Forgot this state.
          filesToCopy.clear();
          listEvCheck.clear();
          listEvCopy.clear();
          widgButtonOk.setText("check");
          widgButtonOk.sCmd="check";
          widgButtonEsc.setText("close");
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        }
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
          widgButtonOk.sCmd="copy";
        } else {
          if(widgCopyState.getText().equals("check")){
            widgButtonOk.setText("check");
            widgButtonOk.sCmd="check";
          } else {
            widgButtonOk.setText("copy");
            widgButtonOk.sCmd="copy";
          }
        }
        return true; 
      } else return false;
  } };
  
  
  private void eventCheckOk(FileRemote.FileRemoteEvent ev){
    if(listEvCheck.remove(ev)){
      zBytes += ev.nrofBytesAll;
      zFiles += ev.nrofFiles;
      int nrofPendingFiles = listEvCheck.size();
      int percent = nrofPendingFiles * 100 / filesToCopy.size();
      widgProgressAll.setValue(percent);
      if(nrofPendingFiles == 0){
        //TODO check dst space
        widgCopyState.setText("files:" + zFiles + ", size:" + zBytes);
        widgButtonOk.setText("copy");
        widgButtonOk.sCmd = "copy";
      }
    }
  }
  
  
  
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
        widgButtonOk.sCmd="quit";
      } else {
        widgButtonOk.setText("check");
        widgButtonOk.sCmd = "check";
      }
      widgButtonEsc.setText("close");
      windConfirmCopy.setWindowVisible(false);      
    }
  }
  
  
  
  
  EventConsumer success = new EventConsumer(){
    @Override public boolean processEvent(Event ev)
    {
      FileRemote.FileRemoteEvent ev1 = (FileRemote.FileRemoteEvent)ev;
      switch(ev.cmd()){
        case FileRemoteAccessor.kNrofFilesAndBytes:{
          eventCheckOk(ev1);
        } break;
        case FileRemoteAccessor.kOperation: {
          int percent = ev.data2 / 10;
          widgProgressAll.setValue(percent);
          widgCopyNameDst.setText(String.copyValueOf(ev1.fileName));
          widgCopyState.setText("" + ev1.nrofBytesInFile/1000000 + " Mbyte");
        }break;
        case FileRemoteAccessor.kFinishError: {
          widgCopyState.setText("error");
          eventConsumed(ev, false);
        }break;
        case FileRemoteAccessor.kFinishNok: {
          widgCopyState.setText("nok");
          eventConsumed(ev, false);
        }break;
        case FileRemoteAccessor.kFinishOk: {
          widgCopyState.setText("ok");
          eventConsumed(ev, true);
        }break;
      }
      //windConfirmCopy.setWindowVisible(false);
      return true;
    }
    
  };

  
  
  void stop(){}
  
}
