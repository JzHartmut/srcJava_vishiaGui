package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWindow_ifc;
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
  
  /**Buttons. */
  GralWidget widgRemoveToTrash, widgDelete;

  /**Name of the file which is attempt to delete. */
  String sFileDelete;
  
  /**The file card where the directory content is shown where the file will be deleted.
   */
  FcmdFileCard fileCard;
  
  FcmdDelete(Fcmd main)
  { this.main = main;
  }
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmDelete()
  {
    main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    
    windConfirmDelete = main.gralMng.createWindow("copyWindow", "confirm copy", GralWindow.windConcurrently);
    //System.out.println(" window: " + main.gralMng.pos.panel.getPixelPositionSize().toString());
    
    main.gralMng.setPosition(2, GralPos.size -2, 1, -1, 0, 'd');
    main.gralMng.addText("delete:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgDeletePath = main.gralMng.addTextField("deleteTrashPath", true, null, "t");
    
    main.gralMng.setPosition(7, GralPos.size -2, 1, -1, 0, 'd');
    main.gralMng.addText("trash path:", 0, GralColor.getColor("bk"), GralColor.getColor("lgr"));
    widgTrashPath = main.gralMng.addTextField("deleteTrashPath", true, null, "t");
    
    main.gralMng.setPosition(-4, -1, 1, 6, 0, 'r');
    main.gralMng.addButton("deleteEsc", actionDelete, "esc", null, null, "esc");
    main.gralMng.setPosition(-1, GralPos.size-3, -19,-11, 0, 'r');
    widgRemoveToTrash = main.gralMng.addButton("deleteToTrash", actionDelete, "trash", null, null, "trash");
    main.gralMng.setPosition(-1, GralPos.size-3, -9, -1, 0, 'r');
    main.gralMng.addButton("deleteOk", actionDelete, "delete", null, null, "delete");
  
  }
  
  
  
  /**Opens the confirm-delete window and fills its fields to ask the user whether confirm.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void confirmDelete(File src111)
  { String sSrc, sTrash;
    FileRemote src;
    fileCard = main.lastFileCards.get(0);
    src = fileCard ==null ? null :  fileCard.currentFile; 
    if(src !=null){
      sSrc = FileSystem.getCanonicalPath(src);
      widgDeletePath.setText(sSrc);
      widgTrashPath.setText("TODO");
    } else {
      widgDeletePath.setText("--no file selected--");
      widgTrashPath.setText("");
    }
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
            if(!evSuccess.use(0, 1, this)){
              main.mainCmd.writeError("File communication busy.");
            } else {
              sFileDelete = widgDeletePath.getText();
              FileRemote file = new FileRemote(sFileDelete);
              if(!file.canWrite()){
                //file.setWritable();
              }
              file.delete(evSuccess);
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
        main.mainCmd.writeError("can't delete " + sFileDelete);
      }
      ev.consumed();
      windConfirmDelete.setWindowVisible(false);
      fileCard.fillInCurrentDir();
      return true;
    }
    
  };
  
  Event evSuccess = new Event(this, success);
  
  
  
}
