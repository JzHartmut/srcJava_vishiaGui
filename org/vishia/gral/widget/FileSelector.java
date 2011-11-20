package org.vishia.gral.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralWidgetMng;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralGridPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.mainCmd.MainCmd_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.SelectMask;

/**This class is a large widget which contains a list to select files in a directory, 
 * supports navigation in the directory tree and shows the current path in an extra text field.
 * Additional 'search in files' is supported.
 * @author Hartmut Schorrig
 *
 */
public class FileSelector //extends GralWidget
{
  
  
  
  /**Version and History:
   * <ul>
   * <li>2011-11-20 new: Phenomenal basic idea: The files may be located in remote hardware. 
   *   It means, that a File can't be access here. Therefore the path, name, date, length in the class {@link FileAndName}
   *   are the data represents on this process on PC. The access to the file is given with remote access. 
   *   Usage of inner class FileAndName containing path, name, date, length instead a File instance. 
   * <li>2011-10-02 new: {@link #setActionOnEnterFile(GralUserAction)}. It executes this action if Enter is pressed (or mouse-left- or doubleclick-TODO).
   * <li>2011-10-01 new: {@link #setOriginDir(File)} and {@link #fillInOriginDir()}.
   *   The origin dir is the directory of first selection or can be set by user. If the user navigates misty,
   *   the origin dir helps to find again the start point.
   * <li>2011-09-28 new: {@link #getCurrentDir()}
   * <li>2011-08-14 created. Firstly used for the Java commander. But it is a universal file select widget.
   * </ul>
   */
  public static final int version = 0x20111002;

  public static class FileAndName //extends SelectMask
  { public final String path;
    public final String name;
    public final long date;
    public final long length;
    public final File file;
    
    FileAndName(String sPath, String sName, long length, long date){
      assert(sPath.endsWith("/"));
      this.path = sPath;
      this.name = sName;
      this.length = length;
      this.date = date;
      this.file = new File(sPath, sName);
    }
  }
  
  
  /**Implementation of the base widget.
   */
  private class FileSelectList extends SelectList
  {
    final FileSelector outer;
    
    FileSelectList(FileSelector outer){
      //super(name, mng);
      this.outer = outer;
    }
    
    @Override public boolean actionOk(Object userData, GralTableLine_ifc line)
    { boolean done = true;
      FileAndName data = (FileAndName)userData;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir) + "/";
      String sName = line.getCellText(1);
      if(sName.equals("..")){
        String sParent = getParentDir(data);
        if(sParent !=null){
          fillIn(sParent); 
        }
      } else {
        if(data.name.endsWith("/")){
          //save the last selection of that level
          indexSelection.put(data.path, data.name);
          fillIn(data.path + data.name);
        } else {
          if(actionOnEnterFile !=null){
            actionOnEnterFile.userActionGui("FileSelector-file", widgdPath, data.file);
          } else {
            done = false;
          }
        }
      }
      return done;
    }
    
    
    private String getParentDir(FileAndName data){
      int zPath = data.path.length();
      int posSep = data.path.lastIndexOf('/',zPath-2);
      if(posSep >=0){
        String sDirP = data.path.substring(0, posSep+1);
        return sDirP;
      }
      else return null;
    }
    
    
    @Override public void actionLeft(Object userData, GralTableLine_ifc line)
    {
      FileAndName data = (FileAndName)userData;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
      //String sName = line.getCellText(1);
      indexSelection.put(data.path, data.name);
      String sParent = getParentDir(data);
      if(sParent !=null){
        fillIn(sParent); 
      }
    }
    
    
    @Override public void actionRight(Object userData, GralTableLine_ifc line)
    {
      FileAndName data = (FileAndName)userData;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
      //String sName = line.getCellText(1);
      if(data.name.endsWith("/")){
        //save the last selection of that level
        indexSelection.put(data.path, data.name);
        fillIn(data.path + "/" + data.name);
      }
    }
    
    
    
    @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
    { boolean ret = true;
      //File file = (File)(data);
      FileAndName data = (FileAndName)oData;
      switch(keyCode){
      case KeyCode.alt + KeyCode.F + '7': 
        FileSystem.searchInFiles(new File[]{data.file}, "ordersBackground"); break;
      default: ret = false;
      }
      if(!ret){
        ret = outer.actionUserKey(keyCode, oData, line);
      }
      return ret;
    }


  } //selectList implementation
  
  
  
  /**The implementation of SelectList. */
  private FileSelectList selectList;
  
  
  /**This index stores the last selected file for any directory path which was used.
   * If the directory path is reused later, the same file will be selected initially.
   * It helps by navigation through the file tree.   
   */
  private Map<String, String> indexSelection = new TreeMap<String, String>(); 
  
  
  /**The widget for showing the path. */
  GralTextField widgdPath;
  
  final SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MMM-dd HH:mm:ss"); 
  
  //final MainCmd_ifc mainCmd;

  /**The current shown directory. */
  File currentDir;
  
  String sCurrentDir;
  
  
  /**The directory which was used on start. */
  String originDir;
  
  
  GralUserAction actionOnEnterFile;
  
  public FileSelector()
  {
    selectList = new FileSelectList(this);
    //this.mainCmd = mainCmd;
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
  

  public String getCurrentDir(){ return sCurrentDir; }
  
  public void setOriginDir(String dir){ originDir = dir; }
  
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
  public void fillIn(String path)
  {
    File dir = new File(path);
    this.currentDir = dir;
    if(originDir == null){
      originDir = path;      //sets on the first invocation. 
    }
    this.sCurrentDir = FileSystem.getCanonicalPath(dir) + "/";
    String sFileSelected = indexSelection.get(sCurrentDir);
    widgdPath.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sCurrentDir);
    File[] files = dir.listFiles();
    if(files !=null){ 
      Map<String, FileAndName> sortFiles = new TreeMap<String, FileAndName>();
      for(File file: files){
        String sName = file.getName();
        if(file.isDirectory()){ sName += "/"; }
        long length = file.length();
        long date = file.lastModified();
        FileAndName fileItem = new FileAndName(this.sCurrentDir, sName, length, date);
        
        String sort = (file.isDirectory()? "D" : "F") + sName;
        sortFiles.put(sort, fileItem);
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
      for(Map.Entry<String, FileAndName> entry: sortFiles.entrySet()){
        FileAndName file = entry.getValue();
        if(sFileSelected != null && file.name.equals(sFileSelected)){
          lineSelect = lineCt;
        }
        if(file.name.endsWith("/")){ line[0] = "/"; }
        else { line[0] = "";}
        line[1] = file.name;
        Date timestamp = new Date(file.date);
        line[3] = dateFormat.format(timestamp);
        selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, 0, line, file);
        lineCt +=1;
      }
      selectList.wdgdTable.setCurrentCell(lineSelect, 1);
    }
  }
  


  
  /**Gets the selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public FileAndName getSelectedFile()
  {
    if(selectList.wdgdTable == null){
      stop();
      return null;
    }
    GralTableLine_ifc line = selectList.wdgdTable.getCurrentLine();
    if(line !=null){
      FileAndName data = (FileAndName)line.getUserData();
      return data;
    } else {
      return null;
    }
  }
  


  
  /**Gets all selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public List<String> getSelectedFiles()
  { List<String> list = new LinkedList<String>();
    if(selectList.wdgdTable == null){
      stop();
      return null;
    }
    for(GralTableLine_ifc line: selectList.wdgdTable.getSelectedLines()){
      FileAndName data = (FileAndName)line.getUserData();
      list.add(data.name);
    }
    return list;
  }
  
  /**Sets the focus of the associated table widget.
   * @return true if focused.
   */
  public boolean setFocus(){ return selectList.wdgdTable.setFocus(); }
  
  void stop(){}

  
  
  
  
  public boolean actionUserKey(int keyCode, Object data, GralTableLine_ifc line)
  { 
    return false;
  }



}
