package org.vishia.commander;

import java.io.File;
import java.io.IOException;

import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

/**The F7-Key mkdir is used for mkfile too. This class builds the mkDirFile- dialog window
 * and contains its functionality.
 * @author Hartmut Schorrig
 *
 */
public class FcmdMkDirFile
{
  protected final Fcmd main;

  GralWindow_ifc windMk;

  GralTextField_ifc widgParentPath, widgName;

  GralButton widgButtonClose, widgButtonMkFile, widgButtonMkDir;
  
  public FcmdMkDirFile(Fcmd main)
  { this.main = main;
  }
  
  
  /**Builds the content of the confirm-delete window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindowConfirmMk()
  {
    main._gralMng.selectPanel("primaryWindow");
    main._gralMng.setPosition(-19, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main._gralMng.createWindow("windMk", "mkdir/file - The.file.Commander", windProps);
    main._gralMng.setPosition(4, GralPos.size -3.8f, 1, -1, 0, 'd', 0.4f);
    widgParentPath = main._gralMng.addTextField("mkParentPath", false, "parent path:", "t");
    widgName = main._gralMng.addTextField("mkName", true, "name:", "t");
    main._gralMng.setPosition(-3.5f, -0.5f, 1, GralPos.size + 10, 0, 'r', 2);
    widgButtonClose = main._gralMng.addButton(null, actionButton, "c", null, "close");
    widgButtonMkDir = main._gralMng.addButton(null, actionButton, "d", null, "mkdir");
    widgButtonMkFile = main._gralMng.addButton(null, actionButton, "f", null, "create file");
    
    windMk = window; 
 
  }
  
  
  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void dialogMkDirFile(File src){ 
    FcmdFileCard card = main.getLastSelectedFileCards()[0];
    if(card !=null){
      File dir = card.getCurrentDir();
      widgParentPath.setText(dir.getAbsolutePath());
    } else {
      widgParentPath.setText("?? nothing selected ??");
    }
    main.gui.setHelpUrl(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.mkdirfile.");
    windMk.setFocus(); //setWindowVisible(true);
  }
  

  /**Action for Key F7 for open dialog. Its like Norton Commander.
   */
  GralUserAction actionOpenDialog = new GralUserAction("actionOpenDialog")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget infos, Object... params)
    { dialogMkDirFile(null);
      return true;
      // /
    }
  };



  /**Action for Buttons */
  GralUserAction actionButton = new GralUserAction("actionButton")
  {
    @Override public boolean userActionGui(int keyCode, GralWidget widg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        switch(widg.sCmd.charAt(0)){
          case 'f':{
            String path = widgParentPath.getText();
            String name = widgName.getText();
            FileRemote file = main.fileCluster.getFile(path, name);
            boolean bOk = false;
            try{ bOk = file.createNewFile(); }
            catch(IOException exc){ main.mainCmd.writeError(exc.getLocalizedMessage()); }
            if(!bOk){ main.mainCmd.writeError("file exists already");}
            
          }break;
          case 'd':{
            String path = widgParentPath.getText();
            String name = widgName.getText();
            FileRemote dir = main.fileCluster.getFile(path, name);
            boolean bOk = false;
            try{ bOk = dir.mkdir(); }
            catch(SecurityException exc){ main.mainCmd.writeError(exc.getLocalizedMessage()); }
            if(!bOk){ main.mainCmd.writeError("directory exists already");}
          }break;
        }
        windMk.setWindowVisible(false);
      }
      return true;
      // /
    }
  };




}
