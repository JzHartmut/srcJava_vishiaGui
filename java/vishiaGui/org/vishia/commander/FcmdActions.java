package org.vishia.commander;

import java.util.List;

import org.vishia.cmd.JZtxtcmdScript;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.DataAccess;
import org.vishia.util.Debugutil;
import org.vishia.util.KeyCode;

public class FcmdActions {

  final Fcmd fcmd;
  
  FcmdActions(Fcmd fcmd){
    this.fcmd = fcmd;
  }
  
  /**
   * This action is invoked for all general key pressed actions. It tests the
   * key and switches to the concretely action for the pressed key. General keys
   * are [F1] for help, [F4] for edit etc.
   */
  GralUserAction actionTest = new GralUserAction("actionTest")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params)
    {
      FcmdActions.this.fcmd.gui.gralMng.addInfo("Test\n", true);
      return true;
    }
  };

  /**
   * This action is invoked for all general key pressed actions. It tests the
   * key and switches to the concretely action for the pressed key. General keys
   * are [F1] for help, [F4] for edit etc.
   */
  GralUserAction actionKey = new GralUserAction("actionKey")
  {
    @Override
    public boolean userActionGui(String sIntension, GralWidget infos,
        Object... params)
    {
      Debugutil.stop();
      return true;
    }
  };

  /**Key alt-F1 to select a directory/cmd list in a list of directories for the
   * left panel. The original Norton Commander approach is to select a drive
   * letter for windows. Selection of paths instead are adequate.
   */
  GralUserAction actionReadMsgConfig = new GralUserAction("actionReadMsgConfig")
  {
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        if(FcmdActions.this.fcmd.cargs.msgConfig !=null && FcmdActions.this.fcmd.cargs.msgConfig.exists()){
          //msgDisp.readConfig(cargs.msgConfig);
        }
        return true;
      } else return false;
    }
  };

  /**Key alt-F1 to select a directory/cmd list in a list of directories for the
   * left panel. The original Norton Commander approach is to select a drive
   * letter for windows. Selection of paths instead are adequate.
   */
  GralUserAction selectCardThemesLeft = new GralUserAction("selectCardThemesLeft")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        FcmdActions.this.fcmd.favorPathSelector.panelLeft.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key alt-F2 to select a directory/cmd list in a list of directories for the
   * middle panel.
   */
  GralUserAction selectCardThemesMiddle = new GralUserAction("selectCardThemesMiddle")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        FcmdActions.this.fcmd.favorPathSelector.panelMid.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key alt-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectCardThemesRight = new GralUserAction("selectCardThemesRight")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdActions.this.fcmd.favorPathSelector.panelRight.cardFavorThemes.wdgdTable.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F1 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardLeft = new GralUserAction("selectFileCardLeft")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdActions.this.fcmd.favorPathSelector.panelLeft.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F2 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardMid = new GralUserAction("selectFileCardMid")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdActions.this.fcmd.favorPathSelector.panelMid.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardRight = new GralUserAction("selectFileCardRight")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        FcmdActions.this.fcmd.favorPathSelector.panelRight.setFocus();
        return true;
      } else return false;
    }
  };

  /**Key sh-F3 to select a directory/cmd list in a list of directories for the
   * right panel.
   */
  GralUserAction selectFileCardOther = new GralUserAction("selectFileCardOther")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){
        if(FcmdActions.this.fcmd.lastFilePanels.size() >=2){
          FcmdLeftMidRightPanel otherPanel = FcmdActions.this.fcmd.lastFilePanels.get(1);
          otherPanel.setFocus();
        }
        return true;
      } else return false;
    }
  };
  

  GralUserAction actionFocusPanelToLeft = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      FcmdLeftMidRightPanel actPanel = FcmdActions.this.fcmd.lastFilePanels.get(0);
      if(actPanel.cc == 'm'){ FcmdActions.this.fcmd.favorPathSelector.panelLeft.setFocus(); }
      else if(actPanel.cc == 'r'){ FcmdActions.this.fcmd.favorPathSelector.panelMid.setFocus(); }
      return true; 
    }
  };
  

  GralUserAction actionFocusPanelToRight = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      FcmdLeftMidRightPanel actPanel = FcmdActions.this.fcmd.lastFilePanels.get(0);
      if(actPanel.cc == 'm'){ FcmdActions.this.fcmd.favorPathSelector.panelRight.setFocus(); }
      else if(actPanel.cc == 'l'){ FcmdActions.this.fcmd.favorPathSelector.panelMid.setFocus(); }
      return true; 
    }
  };
  

  GralUserAction actionFocusFileCard = new GralUserAction("FcmdLeftMidRightPanel.actionFileCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      return true; 
    }
  };
  

  GralUserAction actionFocusThemeCard = new GralUserAction("FcmdLeftMidRightPanel.actionThemeCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      return true; 
    }
  };
  

  
  

  /**Action to focus the cmd card.
   */
  GralUserAction actionFocusCmdCard = new GralUserAction("actionFocusCmdCard")
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        FcmdActions.this.fcmd.executer.cmdSelector.wdgdTable.setFocus();
      return true;
      } else return false;
    }
  };
  
  
  
  GralUserAction actionFocusCardInPanelToLeft = new GralUserAction("FcmdLeftMidRightPanel.actionLeftCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      FcmdActions.this.fcmd.lastFilePanels.get(0).focusLeftCard();
      return true; 
    }
  };
  
  
  GralUserAction actionFocusCardInPanelToRight = new GralUserAction("FcmdLeftMidRightPanel.actionRightCard"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params){ 
      //sets focus to right
      FcmdActions.this.fcmd.lastFilePanels.get(0).focusRightCard();
      return true; 
    }
  };
  

  /**
   * Key F4 for edit command. Its like Norton Commander.
   */
  GralUserAction actionEdit = new GralUserAction("actionEdit")
  {
    @Override public boolean exec(int key, GralWidget_ifc wdg, Object... params) {
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        JZtxtcmdScript.Subroutine jzsub = FcmdActions.this.fcmd.buttonCmds.get("edit");
        if (jzsub == null) {
          FcmdActions.this.fcmd.gui.writeError("internal problem - don't find 'edit' command. ");
        } else {
        
          String sMsg = "GralCommandSelector - put cmd;" + jzsub.toString();
          System.out.println(sMsg);
          List<DataAccess.Variable<Object>> args = FcmdActions.this.fcmd.getterFileArguments.getArguments(jzsub);
          //executer.cmdQueue.addCmd(jzsub, args, Fcmd.this.currentFileCard.currentDir());  //to execute.
          FcmdActions.this.fcmd.executer.cmdExecuter.addCmd(jzsub, args, FcmdActions.this.fcmd.gui.getOutputBox(), FcmdActions.this.fcmd.currentFileCard.currentDir());
        }
      }
      return true;
    }
  };

  
  
  /**This callback will be invoked in the drag event while the mouse is released in the destination. 
   */
  GralUserAction actionDragFileFromStatusLine = new GralUserAction("actionDragFileFromStatusLine"){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params) {
      if(key == KeyCode.dragFiles){
        String path = widgd.getValue();
        String[][] sFiles = (String[][])params[0];  //sFiles has lenght 1
        sFiles[0] = new String[1]; 
        sFiles[0][0] = path;
        Debugutil.stop();
      }
      return true;
    }
  };
  

  
}
