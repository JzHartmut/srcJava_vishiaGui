package org.vishia.commander;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.widget.GralValueBar;
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
import org.vishia.util.FileRemote;
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

  GralTextField_ifc widgDeletePath, widgTrashPath;
  
  GralValueBar widgProgress;
  
  /**Buttons. */
  GralWidget widgRemoveToTrash, widgDelete;
  
  GralWidget widgButtonOk;

  /**Name of the file which is attempt to delete. */
  List<FileRemote> listFileDel;
  //String sFileDelete;
 
  final List<Event> listEvDel = new LinkedList<Event>();

  
  /**The file card where the directory content is shown where the file will be deleted.
   */
  FcmdFileCard fileCard;
  
  FcmdDelete(Fcmd main)
  { this.main = main;
  }
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmDelete()
  { ///
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmDelete = main.gralMng.createWindow("windConfirmDelete", main.idents.windConfirmDelete, GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(2, GralPos.size -2, 1, -1, 0, 'd');
    main.gralMng.addText("delete:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgDeletePath = main.gralMng.addTextField("deleteTrashPath", true, null, "t");
    
    main.gralMng.setPosition(7, GralPos.size -2, 1, -1, 0, 'd');
    main.gralMng.addText("trash path:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgTrashPath = main.gralMng.addTextField("deleteTrashPath", true, null, "t");
    
    main.gralMng.setPosition(-6, GralPos.size +1, 7, -11, 0, 'd', 1);
    //widgProgressFile = main.gralMng.addValueBar("copyProgressFile", null, null);
    widgProgress = main.gralMng.addValueBar("copyProgressAll", null, null);
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("deleteEsc", actionDelete, "esc", null, null, "esc");
    main.gralMng.setPosition(-1, GralPos.size-3, -19,-11, 0, 'r');
    widgRemoveToTrash = main.gralMng.addButton("deleteToTrash", actionDelete, "trash", null, null, "trash");
    main.gralMng.setPosition(-1, GralPos.size-3, -9, -1, 0, 'r');
    widgButtonOk = main.gralMng.addButton("deleteOk", actionDelete, "delete", null, null, "delete");
    widgButtonOk.setPrimaryWidgetOfPanel();
  }
  
  
  
  /**Opens the confirm-delete window and fills its fields to ask the user whether confirm.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void confirmDelete(File src111)
  { fileCard = main.getLastSelectedFileCards()[0];
    String sFileDel = null;
    if(fileCard !=null){
      listFileDel = fileCard.getSelectedFiles();
      if(listFileDel.size() >0){
        sFileDel = FileSystem.getCanonicalPath(fileCard.getCurrentDir()) + "/*";
      } else {
        sFileDel = FileSystem.getCanonicalPath(fileCard.currentFile);
        listFileDel.add(fileCard.currentFile);
      }
    }
    if(sFileDel == null){
      sFileDel = "--no files selected--";
    }
    widgDeletePath.setText(sFileDel);
    widgTrashPath.setText("TODO");
    
    windConfirmDelete.setWindowVisible(true);

  }
  

  /**
   * Key F6 for delete command. Its like Norton Commander.
   */
  GralUserAction actionConfirmDelete = new GralUserAction()
  {
    @Override
    public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    {
      main.getterFiles.prepareFileSelection();
      //File[] files = new File[3];
      File fileSrc = main.getterFiles.getFile1();
      //files[2] = getterFiles.getFile3();
      confirmDelete(fileSrc);
      return true;
      // /
    }
  };

  
  GralUserAction actionDelete = new GralUserAction()
  { @Override public boolean userActionGui(int key, GralWidget widgg, Object... params)
    { try{ 
        if(key == KeyCode.mouse1Up){
          if(widgg.sCmd.equals("delete")){
            for(FileRemote file : listFileDel){
              if(!file.canWrite()){
                //file.setWritable();
              }
              Event callback = new Event(file, success);  //NOTE: store file as src to get its name for report in callback.
              listEvDel.add(callback);
              //
              //The delete action:
              file.delete(callback);
              //      
            }
          } else if(widgg.sCmd.equals("esc")){
            windConfirmDelete.setWindowVisible(false);
          }
        }
      } catch(Exception exc){ main.gralMng.log.sendMsg(0, "FcmdDelete-actionDelete"); }
      return true;
    }
  };

  
  
  EventConsumer success = new EventConsumer(){
    @Override public boolean processEvent(Event ev)
    {
      if(ev.data1 !=0){
        main.mainCmd.writeError("can't delete " + ((FileRemote)(ev.getSrc())).getCanonicalPath());
      }
      listEvDel.remove(ev);
      int nrofPendingFiles = listEvDel.size();
      int percent = nrofPendingFiles * 100 / listFileDel.size();
      widgProgress.setValue(percent, 0, null);
      if(nrofPendingFiles == 0){
        windConfirmDelete.setWindowVisible(false);      
      }
      fileCard.fillInCurrentDir();
      return true;
    }
    
  };
  
  //XX Event evSuccess = new Event(this, success);
  
  
  
}
