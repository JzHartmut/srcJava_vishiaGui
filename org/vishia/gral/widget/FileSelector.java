package org.vishia.gral.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.gridPanel.GralGridPosition;
import org.vishia.gral.gridPanel.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileSystem;

public class FileSelector extends SelectList
{

  private static class FileAndName
  { String sPath;
    String sName;
  }
  
  
  private Map<String, String> indexSelection = new TreeMap<String, String>(); 
  
  
  WidgetDescriptor widgdPath;
  
  final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MMM-dd HH:mm:ss"); 
  
  final MainCmd_ifc mainCmd;
  
  public FileSelector(MainCmd_ifc mainCmd)
  {
    this.mainCmd = mainCmd;
  }
  
  
  @Override public void setToPanel(GuiPanelMngBuildIfc panel, String name, int rows, int[] columns, char size)
  {
    GralGridPosition posAll = panel.getPositionInPanel().clone();
    //Text field for path above list
    panel.setPositionInPanel(posAll.y + 0.1f * posAll.yFrac, posAll.x + 0.1f * posAll.xFrac
      , 2 + posAll.y + 0.1f * posAll.yFrac, posAll.xEnd + 0.1f * posAll.xEndFrac, ' ');
    widgdPath = panel.addTextField(name + "-Path", false, null, '.');
    //the list
    panel.setPositionInPanel(2 + posAll.y + 0.1f * posAll.yFrac, posAll.x + 0.1f * posAll.xFrac
      , posAll.yEnd + 0.1f * posAll.yFrac, posAll.xEnd + 0.1f * posAll.xEndFrac, ' ');
    super.setToPanel(panel, name, rows, columns, size);
  }

  
  
  public void fillIn(File dir)
  {
    String sDir = FileSystem.getCanonicalPath(dir);
    String sFileSelected = indexSelection.get(sDir);
    widgdPath.setValue(GuiPanelMngWorkingIfc.cmdSet, 0, sDir);
    String[] files = dir.list();
    String[] line = new String[4];
    wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdClear, -1, null, null);
    if(dir.getParent() !=null){
      line[0] = "<";
      line[1] = "..";
      line[2] = "";
      line[3] = "";
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, line, dir);
      
    }
    int lineSelect = 1;
    int lineCt = 1;
    for(String name: files){
      if(sFileSelected != null && name.equals(sFileSelected)){
        lineSelect = lineCt;
      }
      File file = new File(dir, name);
      if(file.isDirectory()){ line[0] = "D"; }
      else { line[0] = "";}
      line[1] = name;
      Date timestamp = new Date(file.lastModified());
      line[3] = dateFormat.format(timestamp);
      wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, line, file);
      lineCt +=1;
    }
    table.setCurrentCell(lineSelect, 1);
    //wdgdTable.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, line);
    //wdgdTable.setDataIx(5);
  }
  
  
  public File getSelectedFile()
  {
    if(table == null){
      stop();
      return null;
    }
    TableLineGui_ifc line = table.getCurrentLine();
    if(line !=null){
      File file = (File)line.getUserData();
      return file;
    } else {
      return null;
    }
  }
  
  
  @Override public void actionOk(Object userData, TableLineGui_ifc line)
  {
    File file = (File)(userData);
    File dir = file.getParentFile();
    String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
    String sName = line.getCellText(1);
    if(sName.equals("..")){
      if(dir !=null){
        fillIn(dir); 
      }
    } else {
      if(file.isDirectory()){
        //save the last selection of that level
        indexSelection.put(sDir, sName);
        fillIn(file);
      }
    }
  }
  
  
  @Override public void actionLeft(Object userData, TableLineGui_ifc line)
  {
    File file = (File)(userData);
    File dir = file.getParentFile();
    String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
    String sName = line.getCellText(1);
    indexSelection.put(sDir, sName);
    if(dir !=null){
      dir = dir.getParentFile();  
    }
    if(dir !=null){
      fillIn(dir); 
    }
  }
  
  
  @Override public void actionRight(Object userData, TableLineGui_ifc line)
  {
    File file = (File)(userData);
    File dir = file.getParentFile();
    String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
    String sName = line.getCellText(1);
    if(file.isDirectory()){
      //save the last selection of that level
      indexSelection.put(sDir, sName);
      fillIn(file);
    }
  }
  
  
  
  @Override public void actionUserKey(String sIntension, Object data, TableLineGui_ifc line)
  {
    File file = (File)(data);
    if(sIntension.equals("a-f7")){ 
      String[] result = FileSystem.searchInFiles(new File[]{file}, "ordersBackground"); 
      stop();
    }
  }
  
  
  
  
  void stop(){}
  
}
