package org.vishia.gral.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.gridPanel.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.Widgetifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

public class FileSelector implements Widgetifc
{

  private static class FileAndName
  { String sPath;
    String sName;
  }
  
  
  /**Implementation of the base widget.
   */
  private SelectList selectList = new SelectList()
  {
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
      KeyCode keyCode = new KeyCode(sIntension);
      switch(keyCode.code){
      case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{file}, "ordersBackground"); break;
      }
    }
  }; //selectList implementation
  
  private Map<String, String> indexSelection = new TreeMap<String, String>(); 
  
  
  GralWidget widgdPath;
  
  final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MMM-dd HH:mm:ss"); 
  
  final MainCmd_ifc mainCmd;
  
  public FileSelector(MainCmd_ifc mainCmd)
  {
    this.mainCmd = mainCmd;
  }
  
  
  /**Sets this widget to a panel.
   * @param panel The panel where this Widget should be set. The position in the panel 
   *        should be set before using {@link GralGridBuild_ifc#setPositionInPanel(float, float, float, float, char)}.
   * @param name The name of the widget in the panel.
   * @param rows Number of rows to show
   * @param columns Array with column width.
   * @param size Presentation size. It is a character 'A'..'E', where 'A' is a small size. The size determines
   *        the font size especially. 
   */
  public void setToPanel(GralGridBuild_ifc panel, String name, int rows, int[] columns, char size)
  {
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralGridPos posAll = panel.getPositionInPanel();
    //Text field for path above list
    panel.setPosition(posAll, GralGridPos.same, 2.0F, GralGridPos.same, GralGridPos.same, 1, 'd');
    widgdPath = panel.addTextField(name + "-Path", false, null, '.');
    widgdPath.setBackColor(panel.getColor("pye"), 0xeeffff);  //color pastel yellow
    //the list
    panel.setPosition(posAll, 2.0F, GralGridPos.same, GralGridPos.same, GralGridPos.same, 1, 'd');
    selectList.setToPanel(panel, name, rows, columns, size);
  }
  

  @Override public Object getWidget(){ return selectList.table.getWidget(); }

  @Override public boolean setFocus(){ return selectList.table.setFocus(); }
  
  

  
  
  /**Fills the content with given directory.
   * @param dir The directory which's files are shown.
   */
  public void fillIn(File dir)
  {
    String sDir = FileSystem.getCanonicalPath(dir);
    String sFileSelected = indexSelection.get(sDir);
    widgdPath.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sDir);
    String[] files = dir.list();
    String[] line = new String[4];
    selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdClear, -1, null, null);
    /*
    if(dir.getParent() !=null){
      line[0] = "<";
      line[1] = "..";
      line[2] = "";
      line[3] = "";
      selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, line, dir);
      
    }
    */
    int lineSelect = 1;
    int lineCt = 1;
    if(files !=null) for(String name: files){
      if(sFileSelected != null && name.equals(sFileSelected)){
        lineSelect = lineCt;
      }
      File file = new File(dir, name);
      if(file.isDirectory()){ line[0] = "D"; }
      else { line[0] = "";}
      line[1] = name;
      Date timestamp = new Date(file.lastModified());
      line[3] = dateFormat.format(timestamp);
      selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, line, file);
      lineCt +=1;
    }
    selectList.table.setCurrentCell(lineSelect, 1);
  }
  
  

  
  /**Gets the selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public File getSelectedFile()
  {
    if(selectList.table == null){
      stop();
      return null;
    }
    TableLineGui_ifc line = selectList.table.getCurrentLine();
    if(line !=null){
      File file = (File)line.getUserData();
      return file;
    } else {
      return null;
    }
  }
  
  
  
  void stop(){}


  @Override
  public GralColor setBackgroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }


  @Override
  public GralColor setForegroundColor(GralColor color)
  {
    // TODO Auto-generated method stub
    return null;
  }
  
}
