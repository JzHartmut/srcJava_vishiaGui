package org.vishia.commander;

import java.io.File;

import org.vishia.cmd.CmdStore;
import org.vishia.cmd.ZGenScript;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class FcmdSettings
{
  /**Version, history and license
   * <ul>
   * <li>2012-10-27 Hartmut created
   * </ul>
   * 
   * 
   * <b>Copyright/Copyleft</b>:
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL ist not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but don't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you are intent to use this sources without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   * 
   * 
   */
  public static final int version = 20121027;
  
  protected final Fcmd main;

  
  GralWindow_ifc windSettings;
  GralTextField_ifc widgRefreshTime;
  
  GralTextField_ifc widgCfgPath;
  
  /**Path to call the editor for standard editing texts. */
  GralTextField_ifc widgEditorPath;
  
  
  
  GralButton widgApply, widgOk; 
  
  /**Buttons for edit and apply the several configuration files.
   * Edit opens the file in the standard editor. Apply set the content to the File Commander. 
   */
  GralButton widgEditCmd, widgApplyCmd, widgEditExt, widgApplyExt, widgEditPaths, widgApplyPaths;

  int secondsRefresh = 5;
  
  
  public FcmdSettings(Fcmd main)
  { this.main = main;
  }


  
  /**Builds the content of the file property window. The window is created static. It is shown
   * whenever it is used.  */
  void buildWindow()
  { main.gralMng.selectPanel("primaryWindow");
    main.gralMng.setPosition(-30, 0, -47, 0, 1, 'r'); //right buttom, about half less display width and hight.
    int windProps = GralWindow.windConcurrently;
    GralWindow window =  main.gralMng.createWindow("windSettings", "Settings - The.file.Commander", windProps);
    windSettings = window; 
    main.gralMng.setPosition(3.5f, GralPos.size -3, 1, -1, 0, 'd');
    widgRefreshTime = main.gralMng.addTextField(null, true, "refresh time file panel", "t");
    widgEditorPath = main.gralMng.addTextField(null, true, "standard editor path", "t");
    widgCfgPath = main.gralMng.addTextField(null, false, "configuration directory path", "t");
    
    main.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 0, 'r', 0.5f);
    widgEditCmd = main.gralMng.addButton("editCmd", actionEditCfgFile, "cmd.cfg", null, "edit");
    widgApplyCmd = main.gralMng.addButton("applyCmd", actionApplyCfgCmd, "cmd.cfg", null, "apply");
    main.gralMng.addText("cmd cfg file");
    
    main.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 0, 'r', 0.5f);
    widgEditCmd = main.gralMng.addButton("editCmd", actionEditCfgFile, "ext.cfg", null, "edit");
    widgApplyCmd = main.gralMng.addButton("applyExt", actionApplyCfgExt, "ext.cfg", null, "apply");
    main.gralMng.addText(".ext cfg file");
    
    main.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 0, 'r', 0.5f);
    widgEditCmd = main.gralMng.addButton("editPaths", actionEditCfgFile, "path.cfg", null, "edit");
    widgApplyCmd = main.gralMng.addButton("applyPaths", actionApplyCfgPath, "path.cfg", null, "apply");
    main.gralMng.addText("favor paths file");
    
    main.gralMng.setPosition(-7, GralPos.size +2.5f, -9, -1, 0, 'd', 0.5f);
    widgApply = main.gralMng.addButton("dirBytes", actionButton, "apply");
    widgApply.setCmd("apply");
    widgOk = main.gralMng.addButton("dirBytes", actionButton, "ok");
    widgOk.setCmd("close");
  }

  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(File src)
  {
    widgRefreshTime.setText("" + secondsRefresh);
    widgCfgPath.setText(main.cargs.dirCfg.getAbsolutePath());
    windSettings.setWindowVisible(true);

  }
  
  
  /**Action for OK. 
   */
  GralUserAction actionButton = new GralUserAction("FcmdSettings-close")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        if(widg == widgOk){
          windSettings.closeWindow();
        }
      }
      return true;
  } };

  
  /**Action for open the dialog window. It fills the value of the fields from internal variable. 
   */
  GralUserAction actionOpenDialog = new GralUserAction("FcmdSettings-actionOpenDialog")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        openDialog(main.currentFile);
      }
      return true;
    }
  };


  /**Action for open the editor for the configuration file. 
   * The name of the configuration file is contained in the widget. */
  GralUserAction actionEditCfgFile = new GralUserAction("FcmdSettings-actionEditCfgFile")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        CmdStore.CmdBlock cmdBlock = main.buttonCmds.getCmd("edit");
        if (cmdBlock == null) {
          main.mainCmd.writeError("internal problem - don't find 'edit' command. ");
        } else {
          File[] files = new File[1];
          files[0] = new File(main.cargs.dirCfg, ((GralWidget)widg).getCmd());
          File dir = FileSystem.getDir(files[0]);
          main.executer.cmdQueue.addCmd(cmdBlock, files, dir); // to execute.
        }
      }
      return true;
    }
  };

  
  

  /**Action to set the content of the cmd configuration file. */
  GralUserAction actionApplyCfgCmd = new GralUserAction("FcmdSettings-actionApplyCfgCmd")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        String sFileCfg = ((GralWidget)widg).getCmd();
        FcmdExecuter.readCmdCfg(main.cmdSelector.cmdStore, new File(main.cargs.dirCfg, sFileCfg), main.console, main.executer.cmdQueue);
        main.cmdSelector.fillIn();
      }
      return true;
    }
  };


  /**Action to set the content of the extension configuration file. */
  GralUserAction actionApplyCfgExt = new GralUserAction("FcmdSettings-actionApplyCfgExt")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        main.executer.readCmdFile(new File(main.cargs.dirCfg, ((GralWidget)widg).getCmd()));
      }
      return true;
    }
  };


  /**Action to set the content of the favor path file. */
  GralUserAction actionApplyCfgPath = new GralUserAction("FcmdSettings-actionApplyCfgPath")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        main.favorPathSelector.actionReadFavoritePathes.exec(KeyCode.menuEntered, null);
      }
      return true;
    }
  };


  
}
