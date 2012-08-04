package org.vishia.commander;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
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
   *   in {@link #setBtnMenuAndKeys(GralUserAction, String)}.
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
    main.gralMng.setMainKeyAction(actionMainKeys);
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
  
  /*
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
  */
  
  /**Actions for all keys in {@link #keys}. */
  //GralUserAction[] keyAction = new GralUserAction[50];
  
  Map<Integer, GralUserAction> idxKeyAction = new TreeMap<Integer, GralUserAction>();
  
  ButtonAction currentButton;
  
  
  /**Assign an action to any function button, menu and key.
   * @param action The action
   * @param buttonText use the form "Btn:text" where button is F1..F12, aF1 etc. text is the text.
   */
  void setBtnMenuAndKeys(GralUserAction action, String buttonText, int key, int key2, String menu){
    if(buttonText !=null){
      int posSep = buttonText.indexOf(':');
      if(posSep > 0){
        ButtonAction buttonAction = new ButtonAction(action, buttonText.substring(0, posSep), buttonText.substring(posSep+1));
        idxButtons.put(buttonAction.button, buttonAction);
      } else {
        System.err.println("faulty button text, should have format \"F1:text\"");
      }
    }
    if(key !=0){
      idxKeyAction.put(key, action);
    }
    if(key2 !=0){
      idxKeyAction.put(key2, action);
    }
    if(menu !=null){
      main.gui.addMenuItemGThread(null, menu, action);
    }
  }
  
  
  /**Assign an action to any function button, menu and key.
   * @param action The action
   * @param buttonText use the form "Btn:text" where button is F1..F12, aF1 etc. text is the text.
   */
  void setBtnMenuAndKeys(GralUserAction action, String buttonText, int key, String menu){
    setBtnMenuAndKeys(action, buttonText, key, 0, menu);
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
    //keyAction[idx] = button.action;
  }
  
  
  void initPanelButtons()
  {
    //This calls creates the menu entries in the menu bar. The order determines the order in menu bar. It registeres the button strings and keys.
    setBtnMenuAndKeys(main.gui.getActionHelp(), main.idents.buttonHelp, main.idents.keyHelp,main.idents.menuHelpBar);
    setBtnMenuAndKeys(main.filePropsCmd.actionOpenDialog, main.idents.buttonFileProps, main.idents.keyFileProps, main.idents.menuFilePropsBar);
    setBtnMenuAndKeys(main.viewCmd.actionOpenView, main.idents.buttonFileView, main.idents.keyFileView, main.idents.menuFileViewBar);
    setBtnMenuAndKeys(main.editWind.actionOpenEdit, main.idents.buttonEditIntern, main.idents.keyEditIntern, main.idents.menuBarEditIntern);
    setBtnMenuAndKeys(main.actionEdit, main.idents.buttonFileEdit, main.idents.keyFileEdit, main.idents.menuFileEditBar);
    setBtnMenuAndKeys(main.copyCmd.actionConfirmCopy, main.idents.buttonFileCopy, main.idents.keyFileCopy, main.idents.menuConfirmCopyBar);
    setBtnMenuAndKeys(main.mkCmd.actionOpenDialog, main.idents.buttonMkdirFile, main.idents.keyFileCreate, main.idents.menuConfirmMkdirFileBar);
    setBtnMenuAndKeys(main.deleteCmd.actionConfirmDelete, main.idents.buttonFileDel, main.idents.keyFileDel1, main.idents.menuConfirmFileDelBar);
    setBtnMenuAndKeys(main.deleteCmd.actionConfirmDelete, null, main.idents.keyFileDel2, null);
    setBtnMenuAndKeys(main.favorPathSelector.actionSetDirOrigin, main.idents.buttonSetOriginDir, main.idents.keyOriginDir1, main.idents.keyOriginDir1, main.idents.menuBarSetOriginDir);
    setBtnMenuAndKeys(main.executer.actionExecuteFileByExtension, main.idents.buttonExecute, main.idents.keyExecuteExt, main.idents.menuExecuteBar);
    setBtnMenuAndKeys(main.favorPathSelector.actionRefreshFileTable, main.idents.buttonRefereshFiles, main.idents.keyRefresh1, main.idents.menuFileNaviRefreshBar);
    setBtnMenuAndKeys(main.favorPathSelector.actionRefreshFileTable, null, main.idents.keyRefresh2, null);
    setBtnMenuAndKeys(main.favorPathSelector.actionRefreshFileTable, null, main.idents.keyRefresh3, null);
    
    setBtnMenuAndKeys(main.selectCardThemesLeft, main.idents.buttonFavorLeft, main.idents.keyFavorLeft, main.idents.menuBarNavigationLeft);
    setBtnMenuAndKeys(main.selectCardThemesMiddle, main.idents.buttonFavorMiddle, main.idents.keyFavorMiddle, main.idents.menuBarNavigationMiddle);
    setBtnMenuAndKeys(main.selectCardThemesRight, main.idents.buttonFavorRight, main.idents.keyFavorRight, main.idents.menuBarNavigationRight);
    
    setBtnMenuAndKeys(main.selectFileCardLeft,  main.idents.buttonSelectPanelLeft,    main.idents.keySelectPanelLeft,   main.idents.menuBarSelectPanelLeft);
    setBtnMenuAndKeys(main.selectFileCardLeft,  null,                                main.idents.keySelectPanelLeft2,   null);
    setBtnMenuAndKeys(main.selectFileCardMid,   main.idents.buttonSelectPanelMiddle,  main.idents.keySelectPanelMiddle, main.idents.menuBarSelectPanelMiddle);
    setBtnMenuAndKeys(main.selectFileCardMid,   null,                                main.idents.keySelectPanelMiddle2, null);
    setBtnMenuAndKeys(main.selectFileCardRight, main.idents.buttonSelectPanelRight,   main.idents.keySelectPanelRight,  main.idents.menuBarSelectPanelRight);
    setBtnMenuAndKeys(main.selectFileCardRight, null,                                main.idents.keySelectPanelRight2,  null);
    setBtnMenuAndKeys(main.selectFileCardOther, main.idents.buttonSelectPanelOther,   main.idents.keySelectPanelOther,  main.idents.menuBarSelectPanelOther);

    setBtnMenuAndKeys(main.actionFocusCardInPanelToLeft, main.idents.buttonFocusLeftCard,   main.idents.keyFocusLeftCard,  main.idents.menuBarFocusLeftCard);
    setBtnMenuAndKeys(main.actionFocusCardInPanelToRight, main.idents.buttonFocusRightCard,   main.idents.keyFocusRightCard,  main.idents.menuBarFocusRightCard);
    setBtnMenuAndKeys(main.actionFocusPanelToLeft, main.idents.buttonFocusPanelToLeft,   main.idents.keyFocusPanelToLeft,  main.idents.menuBarFocusPaneltoLeft);
    setBtnMenuAndKeys(main.actionFocusPanelToRight, main.idents.buttonFocusPanelToRight,   main.idents.keyFocusPanelToRight,  main.idents.menuBarFocusPanelToRight);

    
    setBtnMenuAndKeys(main.actionFocusCmdCard, main.idents.buttonFocusCmd, main.idents.keyFocusCmd, main.idents.menuBarNavigatonCmd);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerNameNonCase, main.idents.buttonFileSortNameNonCase, main.idents.keyFileSortNameNonCase, main.idents.menuBarFileSortNameNonCase);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerNameCase, main.idents.buttonFileSortNameCase, main.idents.keyFileSortNameCase, main.idents.menuBarFileSortNameCase);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerExtensionNonCase, main.idents.buttonFileSortExtNonCase, main.idents.keyFileSortExtNonCase, main.idents.menuBarFileSortExtNonCase);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerExtensionCase, main.idents.buttonFileSortExtCase, main.idents.keyFileSortExtCase, main.idents.menuBarFileSortExtCase);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerTimestamp, main.idents.buttonFileSortDateNewest, main.idents.keyFileSortDateNewest, main.idents.menuBarFileSortDateNewest);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerTimestampOldestFirst, main.idents.buttonFileSortOldest, main.idents.keyFileSortDateLast, main.idents.menuBarFileSortDateOldest);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerLenghLargestFirst, main.idents.buttonFileSortSizeLarge, main.idents.keyFileSortSizeLarge, main.idents.menuBarFileSortSizeLarge);
    setBtnMenuAndKeys(main.favorPathSelector.actionSortFilePerLenghSmallestFirst, main.idents.buttonFileSortSizeSmall, main.idents.keyFileSortSizeSmall, main.idents.menuBarFileSortSizeSmall);
    setBtnMenuAndKeys(main.favorPathSelector.actionSearchFiles, main.idents.buttonSearchFiles, main.idents.keySearchFiles, main.idents.menuBarSearchFiles);
    
    setBtnMenuAndKeys(main.cmdSelector.actionExecCmdWithFiles, main.idents.buttonExecuteCmdWithFile, main.idents.keyExecuteCmdWithFile, main.idents.keyExecuteCmdWithFile2, main.idents.menuBarExecuteCmdWithFile);
    setBtnMenuAndKeys(actionViewButtons, main.idents.buttonViewButtons, main.idents.keyViewButtons, main.idents.menuBarViewButtons);
    setBtnMenuAndKeys(main.windMng.actionWindFullOut, main.idents.buttonWindowOutput, main.idents.keyWindowOutput, main.idents.keyWindowOutput2, main.idents.menuBarWindowOutput);
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
   * The action was set initially from the {@link #setBtnMenuAndKeys(GralUserAction, String)} with key assignment in {@link FcmdIdents}.
   * @param keyCode
   * @return true if done.
   */
  boolean processKey(int keyCode, GralWidget_ifc widg){
    GralUserAction action = idxKeyAction.get(keyCode);
    if(action !=null){
      boolean bDone = action.exec(keyCode, widg);
      return bDone;
    } else {
      return false;
    }
  }
  

  
  /**The main key action registered in the {@link GralMng#setMainKeyAction(GralUserAction)}.
   */
  GralUserAction actionMainKeys = new GralUserAction()
  {
    @Override public boolean exec(int key, GralWidget_ifc widg, Object... params){ 
      return processKey(key, widg);
    }
  };
  
  
  
  
  /**Action to focus the cmd card.
   */
  GralUserAction actionViewButtons = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget infos, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
      if(bButtonVisible){
        bButtonVisible = false;
        main.gui.setMinMaxSizeArea("A3C3", 4, 4, 0, 0);
      } else {
        bButtonVisible = true;
        main.gui.setMinMaxSizeArea("A3C3", 15, 15, 0, 0);
      }
      return true;
      } else return false;
    }
  };


}
