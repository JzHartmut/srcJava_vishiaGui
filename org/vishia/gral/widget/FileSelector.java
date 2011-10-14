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
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class is a large widget which contains a list to select files in a directory, 
 * supports navigation in the directory tree and shows the current path in an extra text field.
 * Additional 'search in files' is supported.
 * @author Hartmut Schorrig
 *
 */
public class FileSelector implements GralWidget_ifc
{
  
  
  
  /**Version and History:
   * <ul>
   * <li>2011-10-02  New: {@link #setActionOnEnterFile(GralUserAction)}. It executes this action if Enter is pressed (or mouse-left- or doubleclick-TODO).
   * <li>2011-10-01 new: {@link #setOriginDir(File)} and {@link #fillInOriginDir()}.
   *   The origin dir is the directory of first selection or can be set by user. If the user navigates misty,
   *   the origin dir helps to find again the start point.
   * <li>2011-09-28 new: {@link #getCurrentDir()}
   * <li>2011-08-14 created. Firstly used for the Java commander. But it is a universal file select widget.
   * </ul>
   */
  public static final int version = 0x20111002;

  private static class FileAndName
  { String sPath;
    String sName;
  }
  
  
  /**Implementation of the base widget.
   */
  private class FileSelectList extends SelectList
  {
    @Override public boolean actionOk(Object userData, GralTableLine_ifc line)
    { boolean done = true;
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
        } else {
          if(actionOnEnterFile !=null){
            actionOnEnterFile.userActionGui("FileSelector-file", widgdPath, file);
          } else {
            done = false;
          }
        }
      }
      return done;
    }
    
    
    @Override public void actionLeft(Object userData, GralTableLine_ifc line)
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
    
    
    @Override public void actionRight(Object userData, GralTableLine_ifc line)
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
    
    
    
    @Override public void actionUserKey(String sIntension, Object data, GralTableLine_ifc line)
    {
      File file = (File)(data);
      KeyCode keyCode = new KeyCode(sIntension);
      switch(keyCode.code){
      case KeyCode.alt + KeyCode.F + '7': FileSystem.searchInFiles(new File[]{file}, "ordersBackground"); break;
      }
    }
  } //selectList implementation
  
  
  
  /**The implementation of SelectList. */
  private FileSelectList selectList = new FileSelectList();
  
  
  /**This index stores the last selected file for any directory path which was used.
   * If the directory path is reused later, the same file will be selected initially.
   * It helps by navigation through the file tree.   
   */
  private Map<String, String> indexSelection = new TreeMap<String, String>(); 
  
  
  /**The widget for showing the path. */
  GralWidget widgdPath;
  
  final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MMM-dd HH:mm:ss"); 
  
  final MainCmd_ifc mainCmd;

  /**The current shown directory. */
  File currentDir;
  
  String sCurrentDir;
  
  
  /**The directory which was used on start. */
  File originDir;
  
  
  GralUserAction actionOnEnterFile;
  
  public FileSelector(MainCmd_ifc mainCmd)
  {
    this.mainCmd = mainCmd;
  }
  
  
  /**Sets the widgets of this instance to a panel.
   * The panel and the position in the panel 
   * should be set before using {@link GralGridBuild_ifc#selectPanel(String)} and 
   * {@link GralGridBuild_ifc#setPositionInPanel(float, float, float, float, char)}.
   * The instance has more as one widget, all widgets are set in the area of the given position.
   * The position area should be a range of at least 3 lines.
   * @param panelMng The panelManager. 
   * @param name The name of the table widget. The Text-widget for the path gets the name * "-Path".
   * @param rows Number of rows to show
   * @param columns Array with column width.
   * @param size Presentation size. It is a character 'A'..'E', where 'A' is a small size. The size determines
   *        the font size especially. 
   */
  public void setToPanel(GralGridBuild_ifc panelMng, String name, int rows, int[] columns, char size)
  {
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralGridPos posAll = panelMng.getPositionInPanel();
    //Text field for path above list
    panelMng.setPosition(posAll, GralGridPos.same, 2.0F, GralGridPos.same, GralGridPos.same, 1, 'd');
    widgdPath = panelMng.addTextField(name + "-Path", false, null, '.');
    widgdPath.setBackColor(panelMng.getColor("pye"), 0xeeffff);  //color pastel yellow
    //the list
    panelMng.setPosition(posAll, 2.0F, GralGridPos.same, GralGridPos.same, GralGridPos.same, 1, 'd');
    selectList.setToPanel(panelMng, name, rows, columns, size);
    //store this in the GralWidgets to get back from widgets later.
    widgdPath.setContentInfo(this);
    selectList.wdgdTable.setContentInfo(this);
  }
  

  public File getCurrentDir(){ return currentDir; }
  
  public void setOriginDir(File dir){ originDir = dir; }
  
  /**Sets the action which is called if any file is entered. It means the Enter-Key is pressed or
   * a mouse double-click is done on a file.
   * @param newAction The action to use. The action is invoked with TODO
   * @return The current assigned action or null.
   */
  public GralUserAction setActionOnEnterFile(GralUserAction newAction)
  { GralUserAction oldAction = actionOnEnterFile;
    actionOnEnterFile = newAction;
    return oldAction;
  }
  
  
  /**Fills the content with the first directory or the directory which was set with 
   * {@link #setOriginDir(File)}.
   */
  public void fillInOriginDir()
  {
    fillIn(originDir);
  }
  
  /**Fills the content with given directory.
   * @param dir The directory which's files are shown.
   */
  public void fillIn(File dir)
  {
    this.currentDir = dir;
    if(originDir == null){
      originDir = dir;      //sets on the first invocation. 
    }
    this.sCurrentDir = FileSystem.getCanonicalPath(dir);
    String sFileSelected = indexSelection.get(sCurrentDir);
    widgdPath.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sCurrentDir);
    File[] files = dir.listFiles();
    if(files !=null){ 
      Map<String, File> sortFiles = new TreeMap<String, File>();
      for(File file: files){
        String sort = (file.isDirectory()? "D" : "F") + file.getName();
        sortFiles.put(sort,file);
      }
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
      int lineSelect = 0;  
      int lineCt = 0; //count lines to select the line number with equal sFileSelect.
      for(Map.Entry<String, File> entry: sortFiles.entrySet()){
        File file = entry.getValue();
        String name = file.getName();
        if(sFileSelected != null && name.equals(sFileSelected)){
          lineSelect = lineCt;
        }
        if(file.isDirectory()){ line[0] = "D"; }
        else if(file.isHidden()){ line[0] = "x"; }
        else { line[0] = "";}
        line[1] = name;
        Date timestamp = new Date(file.lastModified());
        line[3] = dateFormat.format(timestamp);
        selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, line, file);
        lineCt +=1;
      }
      selectList.table.setCurrentCell(lineSelect, 1);
    }
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
    GralTableLine_ifc line = selectList.table.getCurrentLine();
    if(line !=null){
      File file = (File)line.getUserData();
      return file;
    } else {
      return null;
    }
  }
  
  
  
  void stop(){}

  
  
  
  @Override public Object getWidgetImplementation(){ return selectList.table.getWidgetImplementation(); }

  @Override public boolean setFocus(){ return selectList.table.setFocus(); }
  
  

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
