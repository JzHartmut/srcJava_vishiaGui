package org.vishia.commander;

import org.vishia.util.KeyCode;

public class FcmdIdents
{
  String menuSaveFavoriteSel = "fa&Vors/&Save favorite paths";
  
  String menuDelTab = "fa&Vors/close &tab";

  String menuFilesCp = "&Files/com&Pare";
  
  String menuFileNaviOriginDir = "&Files/set &Origin dir [c<]";
  
  /**Show files in the start dir of the selection. Go to the origin dir.*/
  int keyOriginDir = KeyCode.ctrl + '<';  //like total commander: jump to the root directory

  String menuFileNaviRefresh = "&Files/&Refresh [cR]";
  /**Referesh files.*/
  int keyRefresh1 = KeyCode.ctrl + 'r', keyRefresh2 = KeyCode.ctrl + 'R';  //like total commander: refresh

  
  /**Window title. */
  String windConfirmCompare = "confirm compare";

  /**Window title. */
  String windConfirmDelete = "confirm delete";

  /**Window title. */
  String windConfirmCopy = "confirm copy";





}
