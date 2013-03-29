package org.vishia.guiBzr;

import java.util.TreeMap;

import org.vishia.gral.ifc.GralButtonKeyMenu;

public class BzrIdents
{
  TreeMap<String, GralButtonKeyMenu> idents = new TreeMap<String, GralButtonKeyMenu>();
  
  void initIdents(MainData mainData) {
    idents.put("cleanSelectTable", new GralButtonKeyMenu(mainData.panels.guiSelectPanel.actionRefreshSelectTable
        ,"&Select/&Clean table", "&Clean table", "x","clean",0,0));

    idents.put("statusCmpn", new GralButtonKeyMenu(mainData.panels.guiSelectPanel.actionGetStatus
        ,"&Component/get &Status", "get &Status", "x","status",0,0));

    idents.put("revertAll", new GralButtonKeyMenu(mainData.mainAction.actionRevertWithTimestamp
        ,"&Component/&Revert", "&Revert", "x","revert", 0,0));
  };
  
  GralButtonKeyMenu get(String key){ return idents.get(key); }
  
}
