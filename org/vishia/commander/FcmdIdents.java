package org.vishia.commander;

import org.vishia.util.KeyCode;

public class FcmdIdents
{
  String menuSaveFavoriteSel = "fa&Vors/&Save favorite paths";
  
  String menuDelTab = "fa&Vors/close &tab";

  
  
  String menuFileNaviOriginDirBar = "&Folder/set &Origin dir [c<]";
  
  String menuFileNaviOriginDirContext = "set Origin dir [c<]";
  
  /**Show files in the start dir of the selection. Go to the origin dir.*/
  int keyOriginDir = KeyCode.ctrl + '<';  //like total commander: jump to the root directory

  String menuFileNaviRefreshBar = "&Folder/&Refresh [cR]";
  String menuFileNaviRefreshContext = "Refresh [cR]";
  /**Referesh files.*/
  int keyRefresh1 = KeyCode.ctrl + 'r', keyRefresh2 = KeyCode.ctrl + 'R';  //like total commander: refresh


  
  String menuFilePropsBar = "&File/&Properties [F2]";

  String menuFilePropsContext = "Properties [F2]";
  
  String buttonFileProps = "props";

  
  String menuFileViewBar = "&File/&View [F3]";

  String menuFileViewContext = "View [F3]";

  String buttonFileView = "view";

  

  
  String menuFileEditBar = "&File/&Edit [F4]";

  String menuFileEditContext = "Edit [F4]";

  String buttonFileEdit = "edit";

  
  String menuConfirmCopyBar = "&File/&Copy [F5]";

  String menuConfirmCopyContext = "&Copy [F5]";

  String buttonFileCopy = "copy";

  /**Window title. */
  String windConfirmCopy = "confirm copy";

  
  String menuConfirmMoveBar = "&File/&Move [F6]";

  String menuConfirmMoveContext = "Move [F6]";

  String buttonFileMove = "copy";

  /**Window title. */
  String windConfirmMove = "confirm move";

  
  
  
  String menuConfirmMkdirFileBar = "&Folder/&Mkdir-file [F7]";

  String menuConfirmMkDirFileContext = "Mkdir-file [F7]";

  String buttonMkdirFile = "create";

  /**Window title. */
  String windConfirmMkdirFile = "confirm make dir / file";

  
  
  String menuConfirmFileDelBar = "&File/&Del [F8]";

  String menuConfirmFileDelContext = "del [F8]";

  String buttonFileDel = "del";

  /**Window title. */
  String windConfirmDelete = "confirm delete";

  
  
  /**Opens the 'execute with' choice table with the selected file.  */
  String menuExecuteBar = "&File/&Execute [F9]";

  String menuExecuteContext = "exec [F9]";

  String buttonExecute = "exec";

  /**Window title. */
  String windConfirmExecute = "execute with";

  
  /**execute with currently selected cmd.  */
  String menuExecuteCmdBar = "&File/&Execute [cF9]";

  String menuExecuteCmdContext = "exec [cF9]";

  String buttonExecuteCmd = "cmd";

  
  
  

  String menuFilesCpBar = "&Folder/com&Pare folder tree";
  
  String menuFilesCpContext = "Compare folder tree";
  
  /**Window title. */
  String windConfirmCompare = "confirm compare";



}
