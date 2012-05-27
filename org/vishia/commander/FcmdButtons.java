package org.vishia.commander;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.util.KeyCode;

/**This class contains all functionality of the function buttons in The-file-Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdButtons
{
  
  private final Fcmd main;
  
  GralButton buttonDel;
  
  boolean bButtonVisible = true;

  FcmdButtons(Fcmd main){
    this.main = main;
  }
  
  static class ButtonAction{
    final GralUserAction action;
    final String button;
    final String text;
    public ButtonAction(GralUserAction action, String button, String text)
    { this.action = action;
      this.button = button;
      this.text = text;
    }
  }
  
  ButtonAction emptyButton = new ButtonAction(null, "", "");
  
  Map<String, ButtonAction> idxButtons = new TreeMap<String, ButtonAction>();
  
  final String[] b1 = {"F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "F10"
    , "aF1", "aF2", "aF3", "aF4", "aF5", "aF6", "aF7", "aF8", "aF9", "aF10"
    , "cF1", "cF2", "cF3", "cF4", "cF5", "cF6", "cF7", "cF8", "cF9", "cF10"
    , "sF1", "sF2", "sF3", "sF4", "sF5", "sF6", "sF7", "sF8", "sF9", "sF10"
                      };
  
  ButtonAction currentButton;
  
  
  
  /**Assign an action to any function button.
   * @param action The action
   * @param buttonText use the form "Btn:text" where button is F1..F12, aF1 etc. text is the text.
   */
  void setFnBtn(GralUserAction action, String buttonText){
    int posSep = buttonText.indexOf(':');
    if(posSep > 0){
      ButtonAction buttonAction = new ButtonAction(action, buttonText.substring(0, posSep), buttonText.substring(posSep+1));
      idxButtons.put(buttonAction.button, buttonAction);
    } else {
      System.err.println("faulty button text, should have format \"F1:text\"");
    }
  }
  
  
  /**Get the proper button for the requested idx in {@link #b1}.
   * @param idx
   * @param iterButtonAction
   * @return the {@link #emptyButton} if no button matches.
   */
  ButtonAction getNext(int idx, Iterator<Map.Entry<String, ButtonAction>> iterButtonAction){
    String button = b1[idx];
    while(iterButtonAction.hasNext() && (currentButton == null || currentButton.button.compareTo(button) < 0)){
      currentButton = iterButtonAction.next().getValue();
    }
    if(currentButton.button.equals(button)){ return currentButton; }
    else return emptyButton;
  }
  
  
  private void addButton(int idx, Iterator<Map.Entry<String, ButtonAction>> iterButtonAction){
    ButtonAction button = getNext(idx, iterButtonAction);
    GralButton gralButton = main.gralMng.addButton(button.button, button.action, "", null, null, button.text);
    gralButton.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.Button." + button.text + ".");
  }
  
  
  void initPanelButtons()
  {
    setFnBtn(main.gui.getActionHelp(), main.idents.buttonHelp);
    setFnBtn(main.filePropsCmd.actionOpenDialog, main.idents.buttonFileProps);
    setFnBtn(main.viewCmd.actionOpenView, main.idents.buttonFileView);
    setFnBtn(main.editWind.actionOpenEdit, main.idents.buttonEditIntern);
    setFnBtn(main.actionEdit, main.idents.buttonFileEdit);
    setFnBtn(main.copyCmd.actionConfirmCopy, main.idents.buttonFileCopy);
    setFnBtn(main.copyCmd.actionConfirmCopy, main.idents.buttonFileMove);
    setFnBtn(main.mkCmd.actionOpenDialog, main.idents.buttonMkdirFile);
    setFnBtn(main.deleteCmd.actionConfirmDelete, main.idents.buttonFileDel);
    setFnBtn(main.executer.actionExecuteFileByExtension, main.idents.buttonExecute);
    setFnBtn(main.favorPathSelector.actionRefreshFileTable, main.idents.buttonRefereshFiles);
    setFnBtn(main.selectPanelLeft, main.idents.buttonFavorLeft);
    setFnBtn(main.selectPanelMiddle, main.idents.buttonFavorMiddle);
    setFnBtn(main.selectPanelRight, main.idents.buttonFavorRight);
    setFnBtn(main.actionFocusCmdCard, main.idents.buttonFocusCmd);
    
    setFnBtn(actionViewButtons, main.idents.buttonViewButtons);
    main.gui.addMenuItemGThread("menuBarViewButtons", main.idents.menuBarViewButtons, actionViewButtons);
    
    Iterator<Map.Entry<String, ButtonAction>> iterButtonAction = idxButtons.entrySet().iterator();
    
    main.gralMng.selectPanel("Buttons");
    main.statusLine.buildGraphic();
    
    main.gralMng.setPosition(4, GralPos.size + 1, 10, 20, 1, 'r');
    main.gralMng.addText("F1", 'A', 0x0);
    main.gralMng.addText("F2", 'A', 0x0);
    main.gralMng.addText("F3", 'A', 0x0);
    main.gralMng.addText("F4", 'A', 0x0);
    main.gralMng.addText("F5", 'A', 0x0);
    main.gralMng.addText("F6", 'A', 0x0);
    main.gralMng.addText("F7", 'A', 0x0);
    main.gralMng.addText("F8", 'A', 0x0);
    main.gralMng.addText("F9", 'A', 0x0);
    main.gralMng.addText("F10", 'A', 0x0);
    main.gralMng.setPosition(7, GralPos.size +2, 0, 4, 1, 'd');
    main.gralMng.addText("alt -", 'A', 0x0);
    main.gralMng.addText("ctr -", 'A', 0x0);
    main.gralMng.addText("sh  -", 'A', 0x0);

    main.gralMng.setPosition(5, 7, 4, 14, 1, 'r');
    int idx;
    for(idx = 0; idx < 10; ++idx){
      addButton(idx, iterButtonAction);
    }
    main.gralMng.setPosition(7, 9, 4, 14, 1, 'r');
    for(idx = 10; idx < 20; ++idx){
      addButton(idx, iterButtonAction);
    }
    main.gralMng.setPosition(9, 11, 4, 14, 1, 'r');
    for(idx = 20; idx < 30; ++idx){
      addButton(idx, iterButtonAction);
    }
    main.gralMng.setPosition(11, 13, 4, 14, 1, 'r');
    for(idx = 30; idx < 40; ++idx){
      addButton(idx, iterButtonAction);
    }
    main.gui.setMinMaxSizeArea("A3C3", 13, 13, 0, 0);
  }


  /**Action to focus the cmd card.
   */
  GralUserAction actionViewButtons = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(key == KeyCode.menuEntered || key == KeyCode.mouse1Up){ ///
        if(bButtonVisible){
          bButtonVisible = false;
          main.gui.setMinMaxSizeArea("A3C3", 4, 4, 0, 0);
        } else {
          bButtonVisible = true;
          main.gui.setMinMaxSizeArea("A3C3", 13, 13, 0, 0);
        }
        return true;
      } else return false;
    }
  };


}
