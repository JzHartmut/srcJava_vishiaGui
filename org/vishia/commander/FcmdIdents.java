package org.vishia.commander;

import org.vishia.util.KeyCode;

public class FcmdIdents
{
  String menuSaveFavoriteSel = "fa&Vors/&Save favorite paths";
  String menuReadFavoriteSel = "fa&Vors/&Read favorite paths";
  
  String menuDelTab = "fa&Vors/close &tab";


  String menuBarCreateFavor = "fa&Vors/new [cB]";
  String menuContextCreateFavor = "new Favor [cB]";
  int keyCreateFavor = KeyCode.ctrl + 'b';
  
  
  
  
  String menuFileNaviOriginDirBar = "fol&Der/set &Origin dir [c<]";
  
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

  
  
  String menuBarAbout = "&Help/&About";

  String menuBarExit = "&Help/e&Xit";

  
  
  
  String menuFilePropsBar = "&File/&Properties [F2]";

  String menuFilePropsContext = "Properties [F2]";
  
  String buttonFileProps = "F2:props";

  int keyFileProps = KeyCode.F2;

  
  String menuFileViewBar = "&File/&View [F3]";

  String menuFileViewContext = "View [F3]";

  String buttonFileView = "F3:view";

  int keyFileView = KeyCode.F3;

  
  String menuFileEditBar = "&File/&Edit [F4]";

  String menuFileEditContext = "Edit [F4]";

  String buttonFileEdit = "F4:edit";

  int keyFileEdit = KeyCode.F4;
  
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
  
  
  
  String menuBarNavigatonCmd = "&Navigation/cmd [aF9]";
  String buttonFocusCmd = "aF9:cmdTable";

  

  String menuFilesCpBar = "fol&Der/com&Pare folder tree";
  
  String menuFilesCpContext = "Compare folder tree";
  
  /**Window title. */
  String windConfirmCompare = "confirm compare";



  public String menuWindowOutputBar = "&Window/&Output";
  
  public String menuWindowOutputContext = "Zoom/Unzoom";
  
  int keyWindFullOut = KeyCode.ctrl + 'o';
  
}
