package org.vishia.commander;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.widget.FileAndName;
import org.vishia.gral.widget.GralFileSelector;
import org.vishia.util.FileRemote;
import org.vishia.util.KeyCode;

/**This class contains all functionality of the function buttons in The-file-Commander.
 * @author Hartmut Schorrig
 *
 */
public class FcmdButtons
{
  
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2012-06-16 Hartmut new: {@link #processKey(int)}: Now all control keys are set together with the buttons
   *   in {@link #setFnBtn(GralUserAction, String)}.
   * <li>2012-01-01 created. 
   * </ul>
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20120617;

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
  
  /**This is only a helper String array to associate button texts from {@link FCmdIdent} in form "aF1:Text"
   * and there action to the viewable buttons.
   * The order of character are in alphabetic one because the given button control texts are processed in that order.
   */
  final String[] b1 = 
    {  "F1",  "F2",  "F3",  "F4",  "F5",  "F6",  "F7",  "F8",  "F9", "F10"
    , "aF1", "aF2", "aF3", "aF4", "aF5", "aF6", "aF7", "aF8", "aF9", "aF10"
    , "cF1", "cF2", "cF3", "cF4", "cF5", "cF6", "cF7", "cF8", "cF9", "cF10"
    , "gF1", "gF2", "gF3", "gF4", "gF5", "gF6", "gF7", "gF8", "gF9", "gF10"
    , "sF1", "sF2", "sF3", "sF4", "sF5", "sF6", "sF7", "sF8", "sF9", "sF10"
    };
  
  
  int[] keys = { KeyCode.F1, KeyCode.F2, KeyCode.F3, KeyCode.F4, KeyCode.F5, KeyCode.F6, KeyCode.F7, KeyCode.F8, KeyCode.F9, KeyCode.F10
      , KeyCode.alt + KeyCode.F1, KeyCode.alt + KeyCode.F2, KeyCode.alt + KeyCode.F3, KeyCode.alt + KeyCode.F4, KeyCode.alt + KeyCode.F5, KeyCode.alt + KeyCode.F6
      , KeyCode.alt + KeyCode.F7, KeyCode.alt + KeyCode.F8, KeyCode.alt + KeyCode.F9, KeyCode.alt + KeyCode.F10
      , KeyCode.ctrl + KeyCode.F1, KeyCode.ctrl + KeyCode.F2, KeyCode.ctrl + KeyCode.F3, KeyCode.ctrl + KeyCode.F4, KeyCode.ctrl + KeyCode.F5, KeyCode.ctrl + KeyCode.F6
      , KeyCode.ctrl + KeyCode.F7, KeyCode.ctrl + KeyCode.F8, KeyCode.ctrl + KeyCode.F9, KeyCode.ctrl + KeyCode.F10
      , KeyCode.shiftCtrl + KeyCode.F1, KeyCode.shiftCtrl + KeyCode.F2, KeyCode.shiftCtrl + KeyCode.F3, KeyCode.shiftCtrl + KeyCode.F4, KeyCode.shiftCtrl + KeyCode.F5, KeyCode.shiftCtrl + KeyCode.F6
      , KeyCode.shiftCtrl + KeyCode.F7, KeyCode.shiftCtrl + KeyCode.F8, KeyCode.shiftCtrl + KeyCode.F9, KeyCode.shiftCtrl + KeyCode.F10
      , KeyCode.shift + KeyCode.F1, KeyCode.shift + KeyCode.F2, KeyCode.shift + KeyCode.F3, KeyCode.shift + KeyCode.F4, KeyCode.shift + KeyCode.F5, KeyCode.shift + KeyCode.F6
      , KeyCode.shift + KeyCode.F7, KeyCode.shift + KeyCode.F8, KeyCode.shift + KeyCode.F9, KeyCode.shift + KeyCode.F10
  };
  
  /**Actions for all keys in {@link #keys}. */
  GralUserAction[] keyAction = new GralUserAction[50];
  
  
  
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
  
  
  /**Assigns the next action to the {@link #keyAction} and creates the button.
   * @param idx
   * @param iterButtonAction
   */
  private void addButton(int idx, Iterator<Map.Entry<String, ButtonAction>> iterButtonAction){
    ButtonAction button = getNext(idx, iterButtonAction);
    GralButton gralButton = main.gralMng.addButton(button.button, button.action, "", null, null, button.text);
    gralButton.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.Button." + button.text + ".");
    keyAction[idx] = button.action;
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
    setFnBtn(main.favorPathSelector.actionSortFilePerNameNonCase, main.idents.buttonFileSortNameNonCase);
    setFnBtn(main.favorPathSelector.actionSortFilePerNameCase, main.idents.buttonFileSortNameCase);
    setFnBtn(main.favorPathSelector.actionSortFilePerExtensionNonCase, main.idents.buttonFileSortExtNonCase);
    setFnBtn(main.favorPathSelector.actionSortFilePerExtensionCase, main.idents.buttonFileSortExtCase);
    setFnBtn(main.favorPathSelector.actionSortFilePerTimestamp, main.idents.buttonFileSortDateNewest);
    setFnBtn(main.favorPathSelector.actionSortFilePerTimestampOldestFirst, main.idents.buttonFileSortOldest);
    setFnBtn(main.favorPathSelector.actionSortFilePerLenghLargestFirst, main.idents.buttonFileSortSizeLarge);
    setFnBtn(main.favorPathSelector.actionSortFilePerLenghSmallestFirst, main.idents.buttonFileSortSizeSmall);
    
    setFnBtn(actionViewButtons, main.idents.buttonViewButtons);
    main.gui.addMenuItemGThread("menuBarViewButtons", main.idents.menuBarViewButtons, actionViewButtons);
    
    Iterator<Map.Entry<String, ButtonAction>> iterButtonAction = idxButtons.entrySet().iterator();
    
    main.gralMng.selectPanel("Buttons");
    main.statusLine.buildGraphic();
    
    main.gralMng.setPosition(4, GralPos.size + 1, 10, 20, 1, 'r');
    main.gralMng.addText("F1");
    main.gralMng.addText("F2");
    main.gralMng.addText("F3");
    main.gralMng.addText("F4");
    main.gralMng.addText("F5");
    main.gralMng.addText("F6");
    main.gralMng.addText("F7");
    main.gralMng.addText("F8");
    main.gralMng.addText("F9");
    main.gralMng.addText("F10");
    main.gralMng.setPosition(7, GralPos.size +2, 0, 4, 1, 'd');
    main.gralMng.addText("alt -");
    main.gralMng.addText("ctr -");
    main.gralMng.addText("shctr-");
    main.gralMng.addText("sh  -");

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
    main.gralMng.setPosition(13, 15, 4, 14, 1, 'r');
    for(idx = 40; idx < 50; ++idx){
      addButton(idx, iterButtonAction);
    }
    main.gui.setMinMaxSizeArea("A3C3", 15, 15, 0, 0);
  }


  
  /**Searches the given keyCode and processes its action. 
   * The action was set initially from the {@link #setFnBtn(GralUserAction, String)} with key assignment in {@link FcmdIdents}.
   * @param keyCode
   * @return true if done.
   */
  boolean processKey(int keyCode){
    for(int ix = 0; ix< keyAction.length; ++ix){
      if(keys[ix] == keyCode){
        if(keyAction[ix] !=null){
          keyAction[ix].userActionGui(keyCode, null);
          return true;
        } else return false;
      }
    }
    return false; //NOTE: returns inside too!
  }
  
  
  
  /**Action to focus the cmd card.
   */
  GralUserAction actionViewButtons = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(bButtonVisible){
        bButtonVisible = false;
        main.gui.setMinMaxSizeArea("A3C3", 4, 4, 0, 0);
      } else {
        bButtonVisible = true;
        main.gui.setMinMaxSizeArea("A3C3", 15, 15, 0, 0);
      }
      return true;
    }
  };


}
