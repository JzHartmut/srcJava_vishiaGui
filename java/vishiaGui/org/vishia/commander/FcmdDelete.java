package org.vishia.commander;

import java.io.File;
import java.util.EventObject;
import java.util.LinkedList;
import java.util.List;

import org.vishia.event.EventCmdtype;
import org.vishia.event.EventCmdtypeWithBackEvent;
import org.vishia.event.EventConsumer;
import org.vishia.event.EventSource;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class contains all functionality to execute copy and move for The.file.Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdDelete
{
  protected final Fcmd main;

  
  GralWindow_ifc windConfirmDelete;

  GralTextField_ifc widgDeleteDir, widgDeletePath, widgTrashPath;
  
  GralValueBar widgProgress;
  
  /**Buttons. */
  GralWidget widgRemoveToTrash, widgDelete;
  
  GralWidget widgButtonOk;

  /**Name of the file which is attempt to delete. */
  List<FileRemote> listFileDel;
  
  /**Content of the widgDeletePath on confirmation invocation, to compare on actionDelete.
   * If the user does not change the field, the listFileDel is valid.
   */
  String sFileDelete;
 
  final List<EventCmdtypeWithBackEvent<?,?>> listEvDel = new LinkedList<EventCmdtypeWithBackEvent<?, ?>>();

  
  /**The file card where the directory content is shown where the file will be deleted.
   */
  FcmdFileCard fileCard;
  
  FileRemote currentDirWhereDelete;
  
  //File dirDelete;
  
  EventSource evSrc = new EventSource("FcmdDelete"){
    
  };
  
  FcmdDelete(Fcmd main)
  { this.main = main;
  }
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmDelete()
  { ///
    main.gui.gralMng.selectPanel("primaryWindow");
    main.gui.gralMng.setPosition(-22, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmDelete = main.gui.gralMng.createWindow("windConfirmDelete", main.idents.windConfirmDelete, GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gui.gralMng.setPosition(4, GralPos.size -3.8f, 1, -1, 0, 'd', 0.2f);
    widgDeleteDir = main.gui.gralMng.addTextField("deletedirectory", false, "directory", "t");
    widgDeletePath = main.gui.gralMng.addTextField("deletePath", true, "delete (Note: path/*.ext or path/file* possible)", "t");
    
    //main.gralMng.setPosition(12, GralPos.size -3.5f, 1, -1, 0, 'd');
    widgTrashPath = main.gui.gralMng.addTextField("deleteTrashPath", true, "trash path (Note: left empty to delete forever)", "t");
    
    main.gui.gralMng.setPosition(-6, GralPos.size +1, 7, -11, 0, 'd', 1);
    //widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null, null);
    widgProgress = main.gui.gralMng.addValueBar("copyProgressAll", null);
    main.gui.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gui.gralMng.addButton("deleteEsc", actionDelete, "esc", null, "esc");
    main.gui.gralMng.setPosition(-1, GralPos.size-3, -19,-11, 0, 'r');
    widgRemoveToTrash = main.gui.gralMng.addButton("deleteToTrash", actionDelete, "trash", null,  "trash");
    main.gui.gralMng.setPosition(-1, GralPos.size-3, -9, -1, 0, 'r');
    widgButtonOk = main.gui.gralMng.addButton("deleteOk", actionDelete, "delete", null,  "delete");
    widgButtonOk.setPrimaryWidgetOfPanel();
  }
  
  
  
  /**Opens the confirm-delete window and fills its fields to ask the user whether confirm.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void confirmDelete(File src111)
  { fileCard = main.getLastSelectedFileCards()[0];
    sFileDelete = null;
    currentDirWhereDelete = fileCard.currentDir();
    if(fileCard !=null){
      listFileDel = fileCard.getSelectedFiles(true, 1);
      int nrofFilesDel = listFileDel.size();
      if(nrofFilesDel >1){
        sFileDelete = "" + nrofFilesDel + " Files";
        for(FileRemote file: listFileDel){
          sFileDelete += " : " + file.getName();
        }
      } else if(nrofFilesDel >0){
        FileRemote file = listFileDel.get(0);
        sFileDelete = file.getName();
      } else {
        sFileDelete = "??No files";
      }
      if(sFileDelete == null){ //NOTE: 
        sFileDelete = "--no files selected--";
      }
      widgDeleteDir.setText(FileSystem.getCanonicalPath(currentDirWhereDelete));
      widgDeletePath.setText(sFileDelete);
      widgTrashPath.setText("TODO");
    }
    else {
      widgDeleteDir.setText("??No filecard active");
      widgDeletePath.setText("?? abort");
    }
    windConfirmDelete.setFocus(); //WindowVisible(true);
    

  }
  

  /**
   * Key F8 for delete command. Its like Norton Commander.
   */
  GralUserAction actionConfirmDelete = new GralUserAction("actionConfirmDelete")
  {
    @Override
    public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    {
      FileRemote fileSrc = main.lastFilePanels.get(0).actFileCard.currentFile();
      confirmDelete(fileSrc);
      return true;
      // /
    }
  };

  
  GralUserAction actionDelete = new GralUserAction("actionDelete")
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { try{ 
        if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
          if(widgg.sCmd.equals("delete")){
            String sPathDelete = widgDeletePath.getText().trim();
            if(sPathDelete.equals(sFileDelete)){  
              //no user changing:
              if(!sPathDelete.equals("--no files selected--")){
                for(FileRemote file : listFileDel){
                  if(file !=null){
                    if(!file.canWrite()){
                      //file.setWritable();
                    }
                    FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, file
                        , null, success, null,  evSrc);  //NOTE: store file as src to get its name for report in callback.
                    listEvDel.add(callback);
                    //
                    //The delete action:
                    if(file instanceof FileRemote){
                      (file).delete(callback);
                    } else {
                      if(!file.delete()){
                        file.setWritable(true);
                        file.delete();
                      }
                    }
                  }
                  //      
                }
              }
            } else { //user has changed the path
              FileRemote dirRemote = currentDirWhereDelete;
              FileRemote.CallbackEvent callback = new FileRemote.CallbackEvent(evSrc, dirRemote, null, success, null, evSrc);  
              dirRemote.delete(sPathDelete, true, callback);
            }
          } else if(widgg.sCmd.equals("esc")){
            windConfirmDelete.setWindowVisible(false);
          }
        }
      } catch(Exception exc){ main.gui.gralMng.log.sendMsg(0, "FcmdDelete-actionDelete"); }
      return true;
    }
  };

  
  
  EventConsumer success = new EventConsumer(){
    @Override public int processEvent(EventObject ev)
    { FileRemote.CallbackEvent callback = (FileRemote.CallbackEvent)ev;
      if(callback.successCode !=0){
        main.mainCmd.writeError("can't delete " + callback.getFileSrc().getCanonicalPath());
      }
      listEvDel.remove(ev);
      int nrofPendingFiles = listEvDel.size();
      int percent = nrofPendingFiles * 100 / listFileDel.size();
      widgProgress.setValue(percent);
      if(nrofPendingFiles == 0){
        windConfirmDelete.setWindowVisible(false);      
      }
      fileCard.fillInCurrentDir();
      return 1;
    }

     @Override public String toString(){ return "FcmdDelete - success"; }

  };
  
  //XX Event evSuccess = new Event(this, success);
  
  
  
}
