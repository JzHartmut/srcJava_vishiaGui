package org.vishia.commander;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.vishia.commander.target.FcmdtTarget_ifc;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.FileSelector;
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
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
  protected final Fcmd main;

  
  GralWindow_ifc windConfirmCopy;
  
  GralPos posWindConfirmCopy;
  
  //GralWidget widgdCopyFrom, widgdCopyTo;
  
  GralTextField_ifc widgCopyFrom, widgCopyDirDst, widgCopyNameDst, widgCopyState;
  
  GralWidget widgdOverwrite, widgdOverwriteReadOnly, widgdOverwriteHidden;

  GralWidget widgProgressFile, widgProgressAll;
  
  /**The file card where the directory content is shown where the files will be copied to, the destination. */
  FcmdFileCard fileCardSrc, fileCardDst;
  
  FileRemote fileSrcDir, fileDstDir;

  /**Content from the input fields while copy is pending. */
  String sSrc, sDstDir, sDstName;
  
  List<String> listFileSrc;
  
  List<Event> listEvCopy = new LinkedList<Event>();
  
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
    widgCopyDirDst = main.gralMng.addTextField("copyDirDst", false, null, "t");
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
    main.gralMng.setPosition(GralPos.same, GralPos.size -3, 16, 30, 0, 'r');
    widgdOverwrite = main.gralMng.addButton("copyOverwrite", null, null, null, null, "skip dir");
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("copyEsc", actionDoCopy, "esc", null, null, "esc");
    main.gralMng.setPosition(-4, GralPos.size +1, 7, -7, 0, 'd', 1);
    widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null, null);
    widgProgressAll = main.gralMng.addValueBar("copyProgressAll", null, null);
    main.gralMng.setPosition(-4, GralPos.size+3, -6, -1, 0, 'r');
    main.gralMng.addButton("copyOk", actionDoCopy, "ok", null, null, "OK");
  
  }
  
  
  void actionConfirmCopy(){
    
  }
  
  
  /**
   * Key F5 for copy command. Its like Norton Commander.
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
      if(main.lastFileCards.size() >=2){
        fileCardSrc = main.lastFileCards.get(0);
        fileCardDst = main.lastFileCards.get(1);
        fileSrcDir = fileCardSrc.getCurrentDir();
        fileDstDir = fileCardDst.getCurrentDir();
        listFileSrc = fileCardSrc.getSelectedFiles();
        if(listFileSrc.size()==0){ //nothing selected
          listFileSrc.add(fileCardSrc.currentFile.getName());  
        }
        sDstName = listFileSrc.size() >1 ? "*" 
                   : listFileSrc.size() >=1 ? listFileSrc.get(0) : "??";
        sSrc = fileSrcDir.getAbsolutePath() + "/" + sDstName;
        sDstDir = fileDstDir.getAbsolutePath();
      } else {
        fileCardSrc = null;
        fileCardDst = null;
        fileSrcDir = null;
        fileDstDir = null;
        listFileSrc = null;
        sSrc = "???";
        sDstName = "???";
        sDstDir = "???";
      }
      widgCopyFrom.setText(sSrc);
      widgCopyDirDst.setText(sDstDir);
      widgCopyNameDst.setText(sDstName);
      main.gralMng.setWindowsVisible(windConfirmCopy, posWindConfirmCopy);
      main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.copy.");

      return true;
   }  };
  
  
  
  
  GralUserAction actionDoCopy = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { if(key == KeyCode.mouse1Up){
        if(widgg.sCmd.equals("ok")){
          sDstName = widgCopyNameDst.getText();
          for(String srcName : listFileSrc){
            FileRemote fileSrc = new FileRemote(fileSrcDir, srcName);
            int posWildcard = sDstName.indexOf('*');
            final String nameDst1;
            if(posWildcard >=0){
              nameDst1 = sDstName.substring(0, posWildcard) + srcName + sDstName.substring(posWildcard +1); 
            } else {
              nameDst1 = sDstName;
            }
            FileRemote fileDst = new FileRemote(sDstDir, nameDst1);

            
            if(fileSrc.sameDevice(fileDst)){
              Event callback = new Event(fileSrc, success);
              listEvCopy.add(callback);
              fileSrc.copyTo(fileDst, callback);
            } else {
              //TODO.
              //read content and write content.
            }
                  
          }
        } else if(widgg.sCmd.equals("esc")){
          main.gralMng.setWindowsVisible(windConfirmCopy, null); //set it invisible.
        }
      }
      return true;
    }
  };
  
  
  private void eventConsumed(Event ev){
    listEvCopy.remove(ev);
    int nrofPendingFiles = listEvCopy.size();
    int percent = nrofPendingFiles * 100 / listFileSrc.size();
    widgProgressAll.setValue(percent, 0, null);
    fileCardDst.fillInCurrentDir();
    if(nrofPendingFiles == 0){
      windConfirmCopy.setWindowVisible(false);      
    }
  }
  
  
  
  
  EventConsumer success = new EventConsumer(){
    @Override public boolean processEvent(Event ev)
    {
      switch(ev.id){
        case FileRemoteAccessor.kOperation: {
          int percent = ev.iData;
          widgProgressFile.setValue(percent, 0, null, null);
        }break;
        case FileRemoteAccessor.kFinishError: {
          widgCopyState.setText("error");
          eventConsumed(ev);
        }break;
        case FileRemoteAccessor.kFinishNok: {
          widgCopyState.setText("nok");
          eventConsumed(ev);
        }break;
        case FileRemoteAccessor.kFinishOk: {
          widgCopyState.setText("ok");
          eventConsumed(ev);
        }break;
      }
      //windConfirmCopy.setWindowVisible(false);
      return true;
    }
    
  };

  
  
  void stop(){}
  
}
