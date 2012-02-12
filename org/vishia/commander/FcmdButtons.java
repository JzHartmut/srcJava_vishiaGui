package org.vishia.commander;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralButton;
import org.vishia.gral.ifc.GralPos;
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
  
  
  
  void setIdx(GralUserAction action, String buttonText){
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
    setIdx(main.gui.getActionHelp(), main.idents.buttonHelp);
    setIdx(main.filePropsCmd.actionOpenDialog, main.idents.buttonFileProps);
    setIdx(main.viewCmd.actionOpenView, main.idents.buttonFileView);
    setIdx(main.actionEdit, main.idents.buttonFileEdit);
    setIdx(main.copyCmd.actionConfirmCopy, main.idents.buttonFileCopy);
    setIdx(main.copyCmd.actionConfirmCopy, main.idents.buttonFileMove);
    setIdx(main.mkCmd.actionOpenDialog, main.idents.buttonMkdirFile);
    setIdx(main.deleteCmd.actionConfirmDelete, main.idents.buttonFileDel);
    setIdx(main.executer.actionExecuteFileByExtension, main.idents.buttonExecute);
    setIdx(main.favorPathSelector.actionRefreshFileTable, main.idents.buttonRefereshFiles);
    setIdx(main.selectPanelLeft, main.idents.buttonFavorLeft);
    setIdx(main.selectPanelMiddle, main.idents.buttonFavorMiddle);
    setIdx(main.selectPanelRight, main.idents.buttonFavorRight);
    setIdx(main.actionFocusCmdCard, main.idents.buttonFocusCmd);
    
    Iterator<Map.Entry<String, ButtonAction>> iterButtonAction = idxButtons.entrySet().iterator();
    
    main.gralMng.selectPanel("Buttons");
    main.gralMng.setPosition(0, 2, 0, 0, 1, 'r');
    main.widgFilePath = main.gralMng.addTextField(main.nameTextFieldFilePath, false, null, null);
    main.widgFilePath.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    main.widgFilePath.setDragEnable(main.actionDragFileFromStatusLine, KeyCode.dragFiles);
    main.gralMng.setPosition(2, 4, 0, 9.8f, 1, 'r');
    main.widgRunInfo = main.gralMng.addTextField(main.nameTextFieldRunInfo, false, null, null);
    main.widgRunInfo.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");
    main.gralMng.setPosition(2, 4, 10, 0, 1, 'r');
    main.widgFileInfo = main.gralMng.addTextField(main.nameTextFieldInfo, false, null, null);
    main.widgFileInfo.setHtmlHelp(main.cargs.dirHtmlHelp + "/Fcmd.html#Topic.FcmdHelp.layout.pathCurr.");

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
  }


}
