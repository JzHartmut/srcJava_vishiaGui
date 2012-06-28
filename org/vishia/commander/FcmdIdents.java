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
  String menuSaveFavoriteSel = "fa&Vors/&Save favorite paths";
  String menuReadFavoriteSel = "fa&Vors/&Read favorite paths";
  
  String menuDelTab = "fa&Vors/close &tab";


  String menuBarCreateFavor = "fa&Vors/new [cB]";
  String menuContextCreateFavor = "new Favor [cB]";
  int keyCreateFavor = KeyCode.ctrl + 'b';
  
  String menuContextShowBackslash = "backslash\\path (Windows)";
  String menuContextShowSlash = "slash path (compatible)";
  
  
  String menuFileNaviOriginDirBar = "fol&Der/set &Origin dir [c<]";
  
  String menuBarFolderSyncMidRight = "fol&Der/s&Ync mid - right";
  

  
  String menuFileNaviOriginDirContext = "set Origin dir [c<]";
  
  /**Show files in the start dir of the selection. Go to the origin dir.*/
  int keyOriginDir = KeyCode.ctrl + '<';  //like total commander: jump to the root directory

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

  String menuBarViewButtons = "&Window/view Buttons [sF1]";
  String buttonViewButtons = "sF1:hide Btns";
  int keyViewButtons = KeyCode.shift + KeyCode.F1;

  
  
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
  
  String menuConfirmCopyBar = "&File/&Copy [F6]";

  String menuConfirmCopyContext = "&Copy [F6]";

  String buttonFileCopy = "F6:copy";

  int keyFileCopy = KeyCode.F6;
  /**Window title. */
  String windConfirmCopy = "confirm copy";

  
  String menuConfirmMoveBar = "&File/&Move [F6]";

  String menuConfirmMoveContext = "Move [F6]";

  String buttonFileMove = "Fx:copy";

  /**Window title. */
  String windConfirmMove = "confirm move";

  
  
  
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
  String menuExecuteBar = "&File/&Execute [F9]";

  String menuExecuteContext = "exec [F9]";

  String buttonExecute = "F9:exec";

  /**Window title. */
  String windConfirmExecute = "execute with";

  
  /**execute with currently selected cmd.  */
  String menuExecuteCmdBar = "&File/&Execute [cF9]";

  String menuExecuteCmdContext = "exec [cF9]";

  String buttonExecuteCmd = "cF9:cmd";

  
  String menuBarNavigationLeft = "&Navigation/left [aF1]";
  String buttonFavorLeft = "aF1:left";
  int keyFavorLeft = KeyCode.alt + KeyCode.F1;

  String menuBarNavigationMiddle = "&Navigation/mid [aF2]";
  String buttonFavorMiddle = "aF2:middle";
  int keyFavorMiddle = KeyCode.alt + KeyCode.F2;

  String menuBarNavigationRight = "&Navigation/right [aF3]";
  String buttonFavorRight = "aF3:right";
  int keyFavorRight = KeyCode.alt + KeyCode.F3;
  
  
  int keyExecCmdFile = KeyCode.ctrl + KeyCode.enter;
  
  
  
  String menuBarNavigatonCmd = "&Navigation/cmd [F9]";
  String buttonFocusCmd = "F9:cmdTable";

  

  String menuFilesCpBar = "fol&Der/com&Pare folder tree";
  
  String menuFilesCpContext = "Compare folder tree";
  
  /**Window title. */
  String windConfirmCompare = "confirm compare";



  public String menuWindowOutputBar = "&Window/&Output";
  
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
  int keyFileSortDateNewest = KeyCode.shift + KeyCode.F5;

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
