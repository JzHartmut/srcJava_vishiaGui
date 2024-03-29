package org.vishia.commander;

import java.io.File;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.KeyCode;

public class FcmdSettings
{
  /**Version, history and license
   * <ul>
   * <li>2023-02-11 Hartmut now with new concept all Gral widgets created without implementation firstly. 
   * <li>2016-08-28 Hartmut chg: extra button now for the jzcmd.cfg, 
   *   extra button {@link #widgOkError} to open the infoBox, but open the infoBox automatically on error.
   *   The Button for open Infobox will get a button text "error" if an error in the config is found. 
   *   It is nice to have on editing the scripts for commands to see whether it is okay or not and to detect the cause of errors.
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
  public static final String sVersion = "2023-02-11";
  
  protected final Fcmd main;

  
  GralWindow_ifc windSettings;
  GralTextField_ifc widgRefreshTime;
  
  GralTextField_ifc widgCfgPath;
  
  /**Path to call the editor for standard editing texts. */
  GralTextField_ifc widgEditorPath;
  
  
  
  GralButton widgOkError, widgOk; 
  
  /**Buttons for edit and apply the several configuration files.
   * Edit opens the file in the standard editor. Apply set the content to the File Commander. 
   */
  GralButton widgEditCmd, widgApplyCmd, widgEditExt, widgApplyExt, widgEditPaths, widgApplyPaths;

  int secondsRefresh = 5;
  
  
  public FcmdSettings(Fcmd main)
  { this.main = main;
 
    main.gui.gralMng.selectPanel("primaryWindow");
    int windProps = GralWindow_ifc.windConcurrently | GralWindow_ifc.windOnTop;
    GralWindow window =  main.gui.gralMng.addWindow("@10+29, 10+47 = windSettings", "Settings - The.file.Commander", windProps);
    //window.createImplWidget_Gthread();
    this.windSettings = window; 
    main.gui.gralMng.setPosition(3.5f, GralPos.size -3, 1, -1, 'd');
    this.widgRefreshTime = main.gui.gralMng.addTextField(null, true, "refresh time file panel", "t");
    this.widgEditorPath = main.gui.gralMng.addTextField(null, true, "standard editor path", "t");
    this.widgCfgPath = main.gui.gralMng.addTextField(null, false, "configuration directory path", "t");
    
    //main.gui.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 'r', 0.5f);
    main.gui.gralMng.setPosition(-7, GralPos.size -2.0f, 1, GralPos.size + 8, 'r', 0.5f);
    this.widgEditCmd = main.gui.gralMng.addButton("editCmd", this.actionEditCfgFile, "cmdjz.cfg", null, "edit");
    this.widgApplyCmd = main.gui.gralMng.addButton("applyCmd", this.actionApplyCfgCmd, "cmdjz.cfg", null, "apply");
    main.gui.gralMng.addText("cmd cfg file");
    
    main.gui.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 'r', 0.5f);
    this.widgEditCmd = main.gui.gralMng.addButton("editCmd", this.actionEditCfgFile, "extjz.cfg", null, "edit");
    this.widgApplyCmd = main.gui.gralMng.addButton("applyExt", this.actionApplyCfgExt, "extjz.cfg", null, "apply");
    main.gui.gralMng.addText(".ext cfg file");
    
    main.gui.gralMng.setPosition(GralPos.refer + 3.0f, GralPos.size -2.0f, 1, GralPos.size + 8, 'r', 0.5f);
    this.widgEditCmd = main.gui.gralMng.addButton("editPaths", this.actionEditCfgFile, "path.cfg", null, "edit");
    this.widgApplyCmd = main.gui.gralMng.addButton("applyPaths", this.actionApplyCfgPath, "path.cfg", null, "apply");
    main.gui.gralMng.addText("favor paths file");
    
    main.gui.gralMng.setPosition(-10, GralPos.size -2, -18, -1, 'd', 0.5f);
    this.widgOkError = main.gui.gralMng.addButton("ok_error", this.actionOpenInfo, "infoBox");
    main.gui.gralMng.setPosition(-1, GralPos.size -2.5f, -9, -1, 'd', 0.5f);
    this.widgOk = main.gui.gralMng.addButton("close", this.actionButton, "close");
    this.widgOk.setCmd("close");
    this.windSettings.setVisible(false);
  }

  /**Opens the view window and fills its content.
   * @param src The path which is selected as source. It may be a directory or a file.
   */
  void openDialog(File src)
  {
    widgOkError.setText("infoBox");
    widgRefreshTime.setText("" + secondsRefresh);
    widgCfgPath.setText(main.cargs.dirCfg.getAbsolutePath());
    windSettings.setFocus(); //setWindowVisible(true);

  }
  
  
  /**Action for Close. 
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

  
  /**Action for Close. 
   */
  GralUserAction actionOpenInfo = new GralUserAction("FcmdSettings-info")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params)
    { if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        FcmdSettings.this.main.gui.gralMng.showInfo(null);
      }
      return true;
  } };

  
  /**Action for open the dialog window. It fills the value of the fields from internal variable. 
   */
  GralUserAction actionOpenDialog = new GralUserAction("FcmdSettings-actionOpenDialog")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        openDialog(main.currentFile());
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
        File fileCfg = new File(main.cargs.dirCfg, ((GralWidget)widg).getCmd());
        main.openExtEditor(fileCfg);
        /*
        CmdStore.CmdBlock cmdBlock = main.buttonCmds.getCmd("edit");
        if (cmdBlock == null) {
          main.mainCmd.writeError("internal problem - don't find 'edit' command. ");
        } else {
          File[] files = new File[1];
          files[0] = new File(main.cargs.dirCfg, ((GralWidget)widg).getCmd());
          File dir = FileSystem.getDir(files[0]);
          main.executer.cmdQueue.addCmd(cmdBlock, files, dir); // to execute.
        }
        */
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
        main.executer.cmdSelector.clear();
        String sError = main.executer.readCmdCfgSelectList(main.executer.cmdSelector.addJZsub2SelectTable, new File(main.cargs.dirCfg, sFileCfg), main.gui.gralMng.log);
        if(sError != null) {
          main.gui.outputBox.setText(sError);
          widgOkError.setText("error");
        } else {
          widgOkError.setText("success");
          main.gui.outputBox.setText("ok read " + main.cargs.dirCfg + "/" + sFileCfg);
        }
      }
      return true;
    }
  };


  /**Action to set the content of the extension configuration file. */
  GralUserAction actionApplyCfgExt = new GralUserAction("FcmdSettings-actionApplyCfgExt")
  {
    @Override public boolean exec(int keyCode, GralWidget_ifc widg, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(keyCode)){
        main.executer.readCfgExt(new File(main.cargs.dirCfg, "extjz.cfg"));
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
