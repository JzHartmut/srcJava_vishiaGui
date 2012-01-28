package org.vishia.gral.widget;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.gral.base.GralTextField;
import org.vishia.gral.ifc.GralGridBuild_ifc;
import org.vishia.gral.ifc.GralPos;
import org.vishia.gral.ifc.GralPanelMngWorking_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.util.FileRemote;
import org.vishia.util.FileRemoteAccessor;
import org.vishia.util.FileRemoteAccessorLocalFile;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;

/**This class is a large widget which contains a list to select files in a directory, 
 * supports navigation in the directory tree and shows the current path in an extra text field.
 * Additional 'search in files' is supported.
 * <br>
 * The type of a file is {@link FileRemote} which is an derivation of java.io.File. It means that
 * the files may be existing on any remote device too. The local file system is a special case,
 * whereby it's the usual case mostly.
 * @author Hartmut Schorrig
 *
 */
public class GralFileSelector implements Removeable //extends GralWidget
{
  
  
  
  /**Version and History:
   * <ul>
   * <li>2011-12-11 chg: If the directory is empty, show --empty-- because the table should not be empty.
   * <li>2011-11-27 new: {@link FileAndName#isWriteable}-property.
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
  
  FileRemoteAccessor localFileAccessor = FileRemoteAccessorLocalFile.getInstance();
  
  /**Implementation of the base widget.
   */
  protected class FileSelectList extends GralSelectList
  {
    final GralFileSelector outer;
    
    FileSelectList(GralFileSelector outer){
      //super(name, mng);
      this.outer = outer;
    }
    
    @Override public boolean actionOk(Object userData, GralTableLine_ifc line)
    { boolean done = true;
      FileRemote file = (FileRemote)userData;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir) + "/";
      String sName = line.getCellText(1);
      if(sName.equals("..")){
        actionLeft(userData, line);
        //String sParent = getParentDir(file);
        //if(sParent !=null){
        //  fillIn(sParent); 
        //}
      } else if(file.isDirectory()){
        actionRight(userData, line);
      } else {
        if(actionOnEnterFile !=null){
          actionOnEnterFile.userActionGui(KeyCode.enter, widgdPath, file);
        } else {
          done = false;
        }
      }
      return done;
    }
    
    
    private String getParentDir(FileRemote data){
      int zPath = data.getParent().length();
      int posSep = data.getParent().lastIndexOf('/',zPath-2);
      if(posSep >=0){
        String sDirP = data.getParent().substring(0, posSep+1);
        return sDirP;
      }
      else return null;
    }
    
    
    /**The 'action left' for the FileSelector shows the parent directory.
     * The {@link GralFileSelector#currentDir} is used to get its parent to show.
     * @param line the current line. It is unused because userData contains the file.
     * @param userData The {@link GralTableLine_ifc#getUserData()} from line. it is a {@link FileRemote}
     *   which is currently selected. This file is stored as current for the current directory. 
     *   The parent of the file is the directory which is shown yet.
     * @see org.vishia.gral.widget.GralSelectList#actionLeft(java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override public void actionLeft(Object userData, GralTableLine_ifc line)
    {
      FileRemote currentFile = (FileRemote)userData;
      //File dir = data.file.getParentFile();
      //String sName = line.getCellText(1);
      String sDir = currentDir.getPath();
      if(currentFile !=null && sDir !=null){
        System.out.println("current: " + sDir + " :: " + currentFile.getName());
        indexSelection.put(sDir, currentFile.getName());
      }
      String sParent = currentDir.getParent();
      File parentDir = currentDir.getParentFile();
      if(parentDir !=null){
        indexSelection.put(sParent, currentDir.getName());
        fillIn(parentDir); 
      }
    }
    
    
    @Override public void actionRight(Object userData, GralTableLine_ifc line)
    {
      FileRemote currentFile = (FileRemote)userData;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir);
      //String sName = line.getCellText(1);
      if(currentFile.isDirectory()){
        //save the last selection of that level
        indexSelection.put(currentFile.getParent(), currentFile.getName());
        fillIn(currentFile);
        //fillIn(data.getParent() + "/" + data.getName());
      }
    }
    
    
    
    /* (non-Javadoc)
     * @see org.vishia.gral.widget.SelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
    { boolean ret = true;
      //File file = (File)(data);
      FileRemote data = (FileRemote)oData;
      switch(keyCode){
      case KeyCode.alt + KeyCode.F + '7': 
        FileSystem.searchInFiles(new File[]{data}, "ordersBackground"); break;
      default: ret = false;
      }
      if(!ret){
        ret = outer.actionUserKey(keyCode, oData, line);
      }
      return ret;
    }


  } //selectList implementation
  
  
  
  /**The implementation of SelectList. */
  protected FileSelectList selectList;
  
  
  /**This index stores the last selected file for any directory path which was used.
   * If the directory path is reused later, the same file will be selected initially.
   * It helps by navigation through the file tree.
   * <ul>
   * <li>The key is the path in canonical form without terminating '/'.
   * <li>The value is the name of the file in this directory.   
   * </ul>
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
  File originDir;
  
  
  GralUserAction actionOnEnterFile;
  
  GralUserAction actionSetFileAttribs;
  
  public GralFileSelector()
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
    GralPos posAll = panelMng.getPositionInPanel();
    //Text field for path above list
    panelMng.setPosition(posAll, GralPos.same, GralPos.size + 2.0F, GralPos.same, GralPos.same, 1, 'd');
    widgdPath = panelMng.addTextField(name + "-Path", false, null, null);
    widgdPath.setBackColor(panelMng.getColor("pye"), 0xeeffff);  //color pastel yellow
    //the list
    panelMng.setPosition(posAll, GralPos.refer+2, GralPos.same, GralPos.same, GralPos.same, 1, 'd');
    selectList.setToPanel(panelMng, name, rows, columns, size);
    //store this in the GralWidgets to get back from widgets later.
    widgdPath.setContentInfo(this);
    selectList.wdgdTable.setContentInfo(this);
  }
  

  public String getCurrentDirPath(){ return sCurrentDir; }
  
  public void setOriginDir(FileRemote dir){ originDir = dir; }
  
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
  
  
  /**Sets the action which is called if any file is set to the table. 
   * @param newAction The action to use. The action is invoked with TODO
   * @return The current assigned action or null.
   */
  public GralUserAction setActionSetFileLineAttrib(GralUserAction newAction)
  { GralUserAction oldAction = actionSetFileAttribs;
    actionSetFileAttribs = newAction;
    return oldAction;
  }
  
  
  /**Fills the content with the first directory or the directory which was set with 
   * {@link #setOriginDir(File)}.
   */
  public void fillInOriginDir()
  {
    fillIn(originDir);
  }
  
  
  
  /**It is the refresh operation.
   * 
   */
  public void fillInCurrentDir(){
    if(currentDir !=null){
      fillIn(currentDir);
    }
  }
  
  
  /**Fills the content with given directory.
   * @param dir The directory which's files are shown.
   */
  public void fillIn(File dir) //String path)
  {
    selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdClear, -1, null, null);
    //FileRemote dir = new FileRemote(path);
    //FileRemote rdir = (FileRemote)dir;
    this.currentDir = dir;
    if(originDir == null){
      originDir = dir; //path;      //sets on the first invocation. 
    }
    //this.sCurrentDir = FileSystem.getCanonicalPath(dir) + "/";
    this.sCurrentDir = dir.getPath();
    //widgdPath.setValue(GralPanelMngWorking_ifc.cmdSet, 0, sCurrentDir);
    widgdPath.setText(sCurrentDir, -1);
    //widgdPath.setSelection("|..<");
    File[] files = dir.listFiles();
    int lineSelect = 0;  
    if(files !=null){ 
      Map<String, File> sortFiles = new TreeMap<String, File>();
      for(File file: files){
        String sName = file.getName();
        if(file.isDirectory()){ sName += "/"; }
        String sort = (file.isDirectory()? "D" : "F") + sName;
        sortFiles.put(sort, file);
      }
      int lineCt = 0; //count lines to select the line number with equal sFileSelect.
      if(dir.getParent() !=null){
        String[] line = new String[4];
        line[0] = "<";
        line[1] = "..";
        line[2] = "";
        line[3] = "";
        selectList.wdgdTable.insertLine("..", -1, line, dir);
        //selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, -1, line, dir);
        lineCt +=1;
      }
      //The file or directory which was the current one while this directory was shown lastly:
      String sFileCurrentline = indexSelection.get(sCurrentDir);
      for(Map.Entry<String, File> entry: sortFiles.entrySet()){
        String[] line = new String[4];
        File file = entry.getValue();
        if(sFileCurrentline != null && file.getName().equals(sFileCurrentline)){
          lineSelect = lineCt;
        }
        if(file instanceof FileRemote && ((FileRemote)file).isSymbolicLink()){ 
          line[0] =  file.isDirectory() ? ">" : "s"; 
        }
        else if(file.isDirectory()){ line[0] = "/"; }
        else { line[0] = " ";}
        line[1] = file.getName();
        Date timestamp = new Date(file.lastModified());
        line[3] = dateFormat.format(timestamp);
        /*
        GralTableLine_ifc tLine = selectList.wdgdTable.insertLine(null, lineCt);
        tLine.setCellText(line[0], 0);
        tLine.setCellText(line[1], 1);
        tLine.setCellText(line[2], 2);
        tLine.setCellText(line[3], 3);
        */
        GralTableLine_ifc tline = selectList.wdgdTable.insertLine(line[1], -1, line, file);
        //selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, -1, line, file);
        if(actionSetFileAttribs !=null){
          actionSetFileAttribs.userActionGui(0, selectList.wdgdTable, tline);
        }
        lineCt +=1;
      }
      if(lineCt ==0){
        //special case: no files:
        String[] line = new String[4];
        line[0] = "";
        line[1] = "--empty--";
        line[3] = "";
        selectList.wdgdTable.insertLine(null, -1, line, null);
        lineCt +=1;
      }
    } else {
      //faulty directory
      String[] line = new String[4];
      line[0] = "";
      line[1] = "--not found--";
      line[3] = "";
      selectList.wdgdTable.setValue(GralPanelMngWorking_ifc.cmdInsert, -1, line, currentDir);
    }
    selectList.wdgdTable.setCurrentCell(lineSelect, 1);
    selectList.wdgdTable.repaint(200,200);
  }
  

  public File getCurrentDir(){ return currentDir; }

  
  /**Gets the selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public FileRemote getSelectedFile()
  {
    if(selectList.wdgdTable == null){
      stop();
      return null;
    }
    GralTableLine_ifc line = selectList.wdgdTable.getCurrentLine();
    if(line !=null){
      FileRemote data = (FileRemote)line.getUserData();
      return data;
    } else {
      return null;
    }
  }
  


  
  /**Gets all selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public List<FileRemote> getSelectedFiles()
  { List<FileRemote> list = new LinkedList<FileRemote>();
    if(selectList.wdgdTable == null){
      stop();
      return null;
    }
    for(GralTableLine_ifc line: selectList.wdgdTable.getSelectedLines()){
      FileRemote file = (FileRemote)line.getUserData();
      list.add(file);
    }
    return list;
  }
  
  
  /**Selects the file with the given name in the table
   * @param name name of file like it is shown in the table (given as key).
   * @return true if found and selected.
   */
  public boolean selectFile(String name){
    return selectList.wdgdTable.setCurrentLine(name);
    
  }
  
  
  /**Sets the focus of the associated table widget.
   * @return true if focused.
   */
  public boolean setFocus(){ return selectList.wdgdTable.setFocus(); }
  
  void stop(){}

  @Override public boolean remove(){ 
    selectList.remove();
    widgdPath.remove();
    indexSelection.clear();
    currentDir = null;
    return true;
  }
  
  
  
  /**This method is called on any user key or mouse event while operating in the file table.
   * It should be overwritten by a derived class. This routine is empty.
   * @param key code or mouse code, one of constants from {@link KeyCode}.
   * @param userDataOfLine The user data stored in the line of table.
   * @param line The table line.
   * @return true if is was relevant for the key.
   */
  public boolean actionUserKey(int keyCode, Object userDataOfLine, GralTableLine_ifc line)
  { 
    return false;
  }



}
