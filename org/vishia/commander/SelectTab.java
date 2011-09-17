package org.vishia.commander;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.vishia.cmd.PrepareCmd;
import org.vishia.cmd.CmdStore.CmdBlock;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.widget.SelectList;
import org.vishia.gral.widget.TableLineGui_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.mainCmd.Report;

/**This class implements the selection functionality of tabs and pathes. */
class SelectTab
{

  static class SelectInfo
  { String path;
    String selectName;
    String tabName;
    char active;
  }
  
  private final GralPanelMngWorking_ifc mng;
  
  List<SelectInfo> selectLeft = new LinkedList<SelectInfo>();
  
  List<SelectInfo> selectMid = new LinkedList<SelectInfo>();
  
  List<SelectInfo> selectRight = new LinkedList<SelectInfo>();
  
  List<SelectInfo> selectAll = new LinkedList<SelectInfo>();
  
  SelectTabList listLeft = new SelectTabList();
  
  SelectTabList listMid = new SelectTabList();
  
  SelectTabList listRight = new SelectTabList();
  
  final MainCmd_ifc console;
  
  
  SelectTab(MainCmd_ifc console, GralPanelMngWorking_ifc mng)
  { this.console = console;
    this.mng = mng;
  }
  
  String readCfg(File cfgFile)
  {
    String sError = null;
    BufferedReader reader = null;
    try{
      reader = new BufferedReader(new FileReader(cfgFile));
    } catch(FileNotFoundException exc){ sError = "TabSelector - cfg file not found; " + cfgFile; }
    if(reader !=null){
      selectLeft.clear();
      selectMid.clear();
      selectRight.clear();
      String sLine;
      int posSep;
      List<SelectInfo> list = null;
      boolean bAll = true;
      try{ 
        while( sError == null && (sLine = reader.readLine()) !=null){
          if( sLine.startsWith("==")){
            posSep = sLine.indexOf("==", 2);  
            //a new division
            String sDiv = sLine.substring(2,posSep);
            if(sDiv.equals("left")){ list = selectLeft; }
            else if(sDiv.equals("mid")){ list = selectMid; }
            else if(sDiv.equals("right")){ list = selectRight; }
            else if(sDiv.equals("all")){ list = selectAll; }
            else { sError = "Error in cfg file: ==" + sDiv + "=="; }
          } else { 
            char cActive = sLine.charAt(0);
            if( "lmr".indexOf(cActive) >=0 && sLine.charAt(1)==':'){
              sLine = sLine.substring(2);
            } else {
              cActive = '.';
            }
            String[] sParts = sLine.trim().split(",");
            if(sParts.length < 3){ 
              sError = "SelectTab format error; " + sLine; 
            } else {
              SelectInfo info = new SelectInfo();
              info.tabName = sParts[0].trim();
              info.selectName = sParts[1].trim();
              info.path = sParts[2].trim();
              info.active = cActive;
              list.add(info);
            }
          }      
        }
      } 
      catch(IOException exc){ sError = "selectTab - cfg file read error; " + cfgFile; }
      catch(IllegalArgumentException exc){ sError = "selectTab - cfg file error; " + cfgFile + exc.getMessage(); }
    }
  
    
    return sError;
  }

  
  /**
   * Should be called in the graphic thread.
   */
  void fillInLeft()
  {
    for(SelectInfo info: selectLeft){
      listLeft.add(info, 'l');
    }
    for(SelectInfo info: selectAll){
      listLeft.add(info, 'l');
    }
  }
  
  
  /**
   * Should be called in the graphic thread.
   */
  void fillInMid()
  {
    for(SelectInfo info: selectMid){
      listMid.add(info, 'm');
    }
    for(SelectInfo info: selectAll){
      listMid.add(info, 'm');
    }
  }
  
  
  /**
   * Should be called in the graphic thread.
   */
  void fillInRight()
  {
    for(SelectInfo info: selectRight){
      listRight.add(info, 'r');
    }
    for(SelectInfo info: selectAll){
      listRight.add(info, 'r');
    }

    
  }
  
  
  class SelectTabList extends SelectList
  {
    
    void add(SelectInfo info, char where)
    {
      TableLineGui_ifc line = table.insertLine(null, -1);
      line.setUserData(info);
      line.setCellText(info.active == where ? "" + where: " ",0);
      line.setCellText(info.selectName, 1);
      line.setCellText(info.path, 2);
    }
    
    
    @Override
    protected void actionOk(Object userData, TableLineGui_ifc line)
    {
      SelectInfo info = (SelectInfo)line.getUserData();
      if(info.active != '.'){
        String tabName = info.tabName;
        GralWidget widgd = mng.getWidget(tabName);
        if(widgd !=null){
          mng.setFocus(widgd);
        }
      }
      
    }
  
    @Override
    protected void actionLeft(Object userData, TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionRight(Object userData, TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  
    @Override
    protected void actionUserKey(String sKey, Object userData,
        TableLineGui_ifc line)
    {
      // TODO Auto-generated method stub
      
    }
  } //class SelectList_  
}
