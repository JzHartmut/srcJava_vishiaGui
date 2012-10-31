package org.vishia.commander;

import org.vishia.util.KeyCode;

/**This class contains all texts and keys which are used in any menu, hot key etc.
 * The variables are attempt to set by a script - user or language specific menus and keys.
 * You find all menu and key actions named here, use find operation in all files (cross references)
 * to evaluate, where these functions are used.
 * 
 * @author Hartmut Schorrig
 *
 */
public class FcmdIdents
{
  String menuBarSettings = "&Help/&Settings [cP]";
  String menuContextSettings = "Settings [cP]";
  String buttonSettings = "gF1:settings";
  int key1Settings = KeyCode.ctrl + 'p';
  int key2Settings = KeyCode.ctrl + 'P';

  String menuBarStatus = "&Help/s&Tatus [c�]";
  String menuContextStatus = "Status [c�]";
  String buttonStatus = "gF9:status";
  int key1Status = KeyCode.ctrl + '�';
  int key2Status = KeyCode.ctrl + '�';

  String menuSaveFavoriteSel = "fa&Vors/&Save favorite paths";
  String menuReadFavoriteSel = "fa&Vors/&Read favorite paths";
  
  String menuDelTab = "fa&Vors/close &tab";


  String menuBarCreateFavor = "fa&Vors/new [cB]";
  String menuContextCreateFavor = "new Favor [cB]";
  int keyCreateFavor = KeyCode.ctrl + 'b';
  
  String menuContextShowBackslash = "backslash\\path (Windows)";
  String menuContextShowSlash = "slash path (compatible)";
  
  
  String menuBarFolderSyncMidRight = "fol&Der/s&Ync mid - right";
  

  
  String menuBarSetOriginDir = "fol&Der/set &Origin dir [c<] [cHome]";
  String menuContextSetOriginDir = "set Origin dir [c<] [cHome]";
  String buttonSetOriginDir = null; //"cF2:origin";
  /**Show files in the start dir of the selection. Go to the origin dir.*/
  int keyOriginDir1 = KeyCode.ctrl + '<';  //like total commander: jump to the root directory
  int keyOriginDir2 = KeyCode.ctrl + KeyCode.home;  //like total commander: jump to the root directory

  
  
  
  
  String menuFileNaviRefreshBar = "fol&Der/&Refresh [cR]";
  String menuFileNaviRefreshContext = "Refresh [cR]";
  String buttonRefereshFiles = "F5:refresh";
  /**Referesh files.*/
  int keyRefresh1 = KeyCode.ctrl + 'r'
    , keyRefresh2 = KeyCode.ctrl + 'R'  //like total commander: refresh
    , keyRefresh3 = KeyCode.F5;

  String menuHelpBar = "&Help/&Help [F1]";

  String menuHelpContext = "Help";
  
  String buttonHelp = "F1:help";

  int keyHelp = KeyCode.F1;

  String menuBarViewButtons = "&Window/view Buttons [cF1]";
  String buttonViewButtons = "cF1:hide Btns";
  int keyViewButtons = KeyCode.ctrl + KeyCode.F1;

  
  
  String menuBarAbout = "&Help/&About";

  String menuBarExit = "&Help/e&Xit";

  
  
  
  String menuFilePropsBar = "&File/&Properties [F2]";

  String menuFilePropsContext = "Properties [F2]";
  
  String buttonFileProps = "F2:props";

  int keyFileProps = KeyCode.F2;
  
  final String buttonFilePropsChg = "change file";

  final String buttonFilePropsChanging = "changing ...";

  final String buttonFilePropsCopying = "copying ...";

  final String buttonFilePropsRetry = "retry";

  final String buttonFilePropsAbort = "abort change";

  final String buttonFilePropsOk = "done";

  final String buttonFilePropsCopy = "copy file";

  final String buttonFilePropsChgRecurisve = "change recursive";

  final String buttonFilePropsGetAll = "get all properties";

  final String buttonFilePropsCntLen = "count length all files in dir";


  
  String menuFileViewBar = "&File/&View [F3]";

  String menuFileViewContext = "View [F3]";

  String buttonFileView = "F3:view";

  int keyFileView = KeyCode.F3;

  
  String menuFileEditBar = "&File/&Edit [F4]";

  String menuFileEditContext = "Edit [F4]";

  String buttonFileEdit = "F4:edit";

  int keyFileEdit = KeyCode.F4;
  
  String menuBarEditIntern = "&File/&Edit-intern [sh-F4]";

  String menuContextEditIntern = "Edit-intern [sh-F4]";

  String buttonEditIntern = "sF4:edit-i";

  int keyEditIntern = KeyCode.shift | KeyCode.F4;
  
  String menuConfirmCopyBar = "&File/&Copy, move [F6]";

  String menuConfirmCopyContext = "&Copy, move [F6]";

  String buttonFileCopy = "F6:copy / mv";

  int keyFileCopy = KeyCode.F6;
  /**Window title. */
  String windConfirmCopy = "confirm copy";

  
  
  
  String menuConfirmMkdirFileBar = "fol&Der/&Mkdir-file [F7]";

  String menuConfirmMkDirFileContext = "Mkdir-file [F7]";

  String buttonMkdirFile = "F7:create";

  int keyFileCreate = KeyCode.F7;


  String menuBarSearchFiles = "fol&Der/&Search files [aF7]";

  String menuContextSearchFiles = "Search files [aF7]";

  String buttonSearchFiles = "aF7:search";

  int keySearchFiles = KeyCode.alt + KeyCode.F7;


  /**Window title. */
  String windConfirmMkdirFile = "confirm make dir / file";

  
  
  String menuConfirmFileDelBar = "&File/&Del [F8]";

  String menuConfirmFileDelContext = "del [F8]";

  String buttonFileDel = "F8:del";

  int keyFileDel1 = KeyCode.F8;
  int keyFileDel2 = KeyCode.del;


  /**Window title. */
  String windConfirmDelete = "confirm delete";

  
  
  /**Opens the 'execute with' choice table with the selected file.  */
  String menuExecuteBar = "&File/&Execute by .ext [Enter]";

  String menuExecuteContext = "execute .ext [Enter]";

  String buttonExecute = null;
  
  int keyExecuteExt = 0;   //Note: It is realized in the file table.

  /**Window title. */
  String windConfirmExecute = "execute with";

  
  /**execute with currently selected cmd.  */
  //String menuExecuteCmdBar = "&File/&Execute [cF9]";

  String menuExecuteCmdContext = "exec [cF9]";
  String menuBarExecuteCmdWithFile = "&Command/e~Xecute cmd with file [cEnter]";
  int keyExecuteCmdWithFile = KeyCode.ctrl + KeyCode.F9;
  int keyExecuteCmdWithFile2 = KeyCode.ctrl + KeyCode.enter;
  String buttonExecuteCmdWithFile = "cF9:exeCmd file";
  
  
  String menuBarEditCmdCfg = "&Command/cmdcfg - &Edit act file";
  String menuContextEditCmdCfg = "cmdcfg - &Edit act file";
  String buttonEditCmdCfg = null;
  int keyEditCmdCfg = 0;
  
  
  /**
   * 
   */
  String menuBarReadCmdCfgAct = "&Command/cmdcfg - &Read act file";
  String menuContextReadCmdCfgAct = "cmdcfg - &Read act file";
  String buttonReadCmdCfgAct = null;
  int keyReadCmdCfgAct = 0;
  
  
  String menuBarNavigationLeft = "&Navigation/themes left [aF1]";
  String buttonFavorLeft = "aF1:fav-left";
  int keyFavorLeft = KeyCode.alt + KeyCode.F1;

  String menuBarNavigationMiddle = "&Navigation/themes mid [aF2]";
  String buttonFavorMiddle = "aF2:fav-mid";
  int keyFavorMiddle = KeyCode.alt + KeyCode.F2;

  String menuBarNavigationRight = "&Navigation/themes right [aF3]";
  String buttonFavorRight = "aF3:fav-right";
  int keyFavorRight = KeyCode.alt + KeyCode.F3;
  
  
  String menuBarSelectPanelLeft = "&Navigation/select left [shF1]";
  String buttonSelectPanelLeft = "sF1:left";
  int keySelectPanelLeft = KeyCode.shift + KeyCode.F1;
  int keySelectPanelLeft2 = KeyCode.shiftAlt + KeyCode.F1;

  String menuBarSelectPanelMiddle = "&Navigation/select mid [shF2]";
  String buttonSelectPanelMiddle = "sF2:middle";
  int keySelectPanelMiddle = KeyCode.shift + KeyCode.F2;
  int keySelectPanelMiddle2 = KeyCode.shiftAlt + KeyCode.F2;

  String menuBarSelectPanelRight = "&Navigation/select right [shF3]";
  String buttonSelectPanelRight = "sF3:right";
  int keySelectPanelRight = KeyCode.shift + KeyCode.F3;
  int keySelectPanelRight2 = KeyCode.shiftAlt + KeyCode.F3;
  
  /**"&Navigation/select &other [ctrl-tab]" */
  String menuBarSelectPanelOther = "&Navigation/select &other [ctrl-tab]";
  String buttonSelectPanelOther = "cTab:other";
  int keySelectPanelOther = KeyCode.ctrl + '\t';
  
  
  String menuBarFocusLeftCard = "&Navigation/focus to left card [sh-alt-left]";
  String buttonFocusLeftCard = null;
  int keyFocusLeftCard = KeyCode.shiftAlt + KeyCode.left;
  
  String menuBarFocusRightCard = "&Navigation/focus to right card [sh-alt-right]";
  String buttonFocusRightCard = null;
  int keyFocusRightCard = KeyCode.shiftAlt + KeyCode.right;
  
  String menuBarFocusPaneltoLeft = "&Navigation/focus to left panel [sh-alt-pgup]";
  String buttonFocusPanelToLeft = null;
  int keyFocusPanelToLeft = KeyCode.shiftAlt + KeyCode.pgup;
  
  String menuBarFocusPanelToRight = "&Navigation/focus to right panel [sh-alt-pgdn]";
  String buttonFocusPanelToRight = null;
  int keyFocusPanelToRight = KeyCode.shiftAlt + KeyCode.pgdn;
  
  
  
  
  
  
  int keyExecCmdFile = KeyCode.ctrl + KeyCode.enter;
  
  
  
  String menuBarNavigatonCmd = "&Navigation/cmd [F9]";
  String buttonFocusCmd = "F9:cmdTable";
  int keyFocusCmd = KeyCode.F9;
  
  

  String menuFilesCpBar = "fol&Der/com&Pare folder tree";
  
  String menuFilesCpContext = "Compare folder tree";
  
  /**Window title. */
  String windConfirmCompare = "confirm compare";



  public String menuBarWindowOutput = "&Window/&Output";
  public String menuContextWindowOutput = "&Output zoom";
  public String buttonWindowOutput = null;
  public int keyWindowOutput = KeyCode.ctrl + 'o';
  public int keyWindowOutput2 = KeyCode.ctrl + 'O';
  
  
  public String menuWindowOutputContext = "Zoom/Unzoom";
  
  int keyWindFullOut = KeyCode.ctrl + 'o';

  
  
  
  String menuBarFileSortNameCase = "&File/&Sort/&Name case sens";
  String menuContextFileSortNameCase = "&Sort/&Name case sens";
  String buttonFileSortNameCase = "gF3:A..Za..z";
  int keyFileSortNameCase = KeyCode.ctrl + KeyCode.F3;

  String menuBarFileSortNameNonCase = "&File/&Sort/&Name non-case";
  String menuContextFileSortNameNonCase = "&Sort/&Name non-case";
  String buttonFileSortNameNonCase = "cF3:Aa..Zz";
  int keyFileSortNameNonCase = KeyCode.shiftCtrl + KeyCode.F3;

  String menuBarFileSortExtCase = "&File/&Sort/e&Xt case sens";
  String menuContextFileSortExtCase = "&Sort/e&Xt case sens";
  String buttonFileSortExtCase = "gF4:.ext A..Za..z";
  int keyFileSortExtCase = KeyCode.ctrl + KeyCode.F4;

  String menuBarFileSortExtNonCase = "&File/&Sort/e&Xt non-case";
  String menuContextFileSortExtNonCase = "&Sort/e&Xt non-case";
  String buttonFileSortExtNonCase = "cF4:.ext Aa..Zz";
  int keyFileSortExtNonCase = KeyCode.shiftCtrl + KeyCode.F4;

  String menuBarFileSortDateNewest = "&File/&Sort/&Date newest";
  String menuContextFileSortDateNewest = "&Sort/&Date newest";
  String buttonFileSortDateNewest = "cF5:newest";
  int keyFileSortDateNewest = KeyCode.ctrl + KeyCode.F5;

  String menuBarFileSortDateOldest = "&File/&Sort/date &Oldest";
  String menuContextFileSortOldest = "&Sort/date &Oldest";
  String buttonFileSortOldest = "gF5:oldest";
  int keyFileSortDateLast = KeyCode.shiftCtrl + KeyCode.F5;

  String menuBarFileSortSizeLarge = "&File/&Sort/size &Largest";
  String menuContextFileSortSizeLarge = "&Sort/size &Largest";
  String buttonFileSortSizeLarge = "cF6:largest";
  int keyFileSortSizeLarge = KeyCode.ctrl + KeyCode.F6;

  String menuBarFileSortSizeSmall = "&File/&Sort/size &Smallest";
  String menuContextFileSortSizeSmall = "&Sort/size &Smallest";
  String buttonFileSortSizeSmall = "gF6:smallest";
  int keyFileSortSizeSmall = KeyCode.shiftCtrl + KeyCode.F6;


}
