package org.vishia.gral.widget;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.commander.Fcmd;
import org.vishia.fileRemote.FileAccessZip;
import org.vishia.fileRemote.FileRemote;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralTextField_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Assert;
import org.vishia.util.Event;
import org.vishia.util.EventConsumer;
import org.vishia.util.EventSource;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;
import org.vishia.util.SelectMask_ifc;
import org.vishia.util.Timeshort;

/**This class is a large widget which contains a list to select files in a directory, 
 * supports navigation in the directory tree and shows the current path in an extra text field.
 * Additional 'search in files' is supported.
 * <br><br>
 * The type of a file is either a java.io.File or {@link FileRemote}. 
 * The last one is an derivation of java.io.File. It means that
 * the files may be existing on any remote device. The local file system is a special case,
 * whereby it's the usual case mostly.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GralFileSelector implements Removeable //extends GralWidget
{
  
  
  
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2013-04-30 Hartmut new: context menu now available. Sort, refresh, deselect
   * <li>2013-04-30 Hartmut new: {@link #checkRefresh(long)}, chg: Don't change the content in the table
   *   if the content is identical with the current presentation in table, for refreshing. 
   * <li>2013-04-30 Hartmut chg: {@link #fillIn(FileRemote, boolean)} now uses the {@link FileRemote#timeRefresh}
   * <li>2013-04-28 Hartmut new: {@link #actionOnMarkLine} changes the select status of {@link FileRemote#setSelected(int)}
   * <li>2013-04-12 Hartmut adapt Event, FileRemote: The attributes Event.data1, data2, oData, refData are removed. Any special data should be defined in any derived instance of the event. A common universal data concept may be error-prone  because unspecified types and meanings.
   *   FileRemote: Dedicated attributes for {@link CallbackCmd#successCode} etc.
   * <li>2013-03-28 Hartmut chg: {@link #setToPanel(GralMngBuild_ifc, String, int, int[], char)} preserves the panel
   *   before calling.
   * <li>2012-11-11 Hartmut bugfix: {@link #fillInRefreshed(File, boolean)}: If all files are complete with file info,
   *   nevertheless the {@link RefreshTimed#delayedFillin(int) was called because the whole routine was called with false 
   *   as bCompleteWithFileInfo-argument. Now that is prevented if all files were tested already.
   * <li>2012-10-12 Hartmut chg {@link WindowFileSelection#openDialog(String, String)} with title.
   * <li>2012-10-01 Hartmut new now {@link #fillIn(File, boolean)} doesn't get the file properties if it is called with false.
   *   This makes it faster to show large content of folders on remote devices (in PC-network). If a file is selected
   *   it replaces its properties. 
   * <li>2012-09-24 Hartmut new.jar files opened as zip file.
   * <li>2012-07-30 Hartmut improved using of extra thread on refreshing file properties. Write first 'waiting',
   *   the other thread writes the content maybe delayed. The user can abort the access if a response is not kept.
   * <li>2012-07-28 Hartmut improved zipfile access.
   * <li>2012-07-29 Hartmut improved access to the file system using the new capabilities of FileRemote.
   *  TODO consequently writing 'wait for response' in table and access to the file in another thread.
   * <li>2012-07-01 Hartmut new {@link #setActionOnEnterPathNewFile(GralUserAction)}.
   *   Now this widget is used to select a file to read and save in an application other than the.File.commander.
   * <li>2012-07-01 Hartmut Refactoring usage of FileRemote: Normally the java.io.File is used.
   *   The FileRemote is a specialization, which is not used in all cases.
   * <li>2012-06-17 new Hartmut: Now sorts list with name, size, date, format of size and date in list adjusted:
   *   Separate timestamp of file for today, last year.
   * <li>2012-06-09 new Hartmut: {@link GralFileSelector.WindowFileSelection}, not ready yet.
   * <li>2012-04-16 chg: Capability to sort enhanced.
   * <li>2012-04-10 chg: Now ctrl-PgUp to select parent dir, like in Norton Commander,
   *   ctrl-PgDn to entry in dir (parallel to Enter).
   * <li>2012-03-09 new {@link #kColFilename} etc. 
   * <li>2012-02-14 corr: Anytime a file was selected as current, it was added in the {@link #indexSelection}.
   *   Not only if the directory was changed. It is necessary for refresh to select the current file again.
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
   * 
   * <b>Copyright/Copyleft</b>:<br>
   * For this source the LGPL Lesser General Public License,
   * published by the Free Software Foundation is valid.
   * It means:
   * <ol>
   * <li> You can use this source without any restriction for any desired purpose.
   * <li> You can redistribute copies of this source to everybody.
   * <li> Every user of this source, also the user of redistribute copies
   *    with or without payment, must accept this license for further using.
   * <li> But the LPGL is not appropriate for a whole software product,
   *    if this source is only a part of them. It means, the user
   *    must publish this part of source,
   *    but doesn't need to publish the whole source of the own product.
   * <li> You can study and modify (improve) this source
   *    for own using or for redistribution, but you have to license the
   *    modified sources likewise under this LGPL Lesser General Public License.
   *    You mustn't delete this Copyright/Copyleft inscription in this source file.
   * </ol>
   * If you intent to use this source without publishing its usage, you can get
   * a second license subscribing a special contract with the author. 
   * 
   * @author Hartmut Schorrig = hartmut.schorrig@vishia.de
   */
  public static final int version = 20130521;
  
  //FileRemoteAccessor localFileAccessor = FileRemoteAccessorLocalFile.getInstance();
  
  
  /**A window for search-in-file dialogue.
   * It is instantiated calling {@link GralFileSelector#createWindowConfirmSearchGthread(GralMngBuild_ifc)}.
   * The user can invoke {@link #confirmSearchInFiles(GralFileSelector, Appendable)} to open that window. 
   * Note: An application may have only one of this window thow more as one GralFileSelector may be exist.
   * Therefore the window is not instantiated automatically in this class. The user can instantiate it.
   *
   */
  public static class WindowConfirmSearch {
    
    GralWindow_ifc windConfirmSearch;

    GralTextField_ifc widgPath, widgMask, widgText;
    
    GralValueBar widgProgression;
    
    /**Buttons. */
    GralButton widgEsc, widgSubdirs, widgSearch;

    GralFileSelector fileSelector;
    
    Appendable searchOutput;
    
    /**Use {@link GralFileSelector#createWindowConfirmSearchGthread(GralMngBuild_ifc)} to create.
    */
    protected WindowConfirmSearch(){}
    
    
    /**Shows the window.
     * @param fileSelector
     */
    public void confirmSearchInFiles(GralFileSelector fileSelector, Appendable searchOutput){
      this.fileSelector = fileSelector;
      this.searchOutput = searchOutput;
      this.widgPath.setText(fileSelector.sCurrentDir);
      windConfirmSearch.setWindowVisible(true);
    }
    
    
    /**Action is called if the button search is pressed. */
    GralUserAction actionFileSearch = new GralUserAction(){
      @Override
      public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
        if(widgd.sCmd.equals("search") && key == KeyCode.mouse1Up){
          boolean subDirs = widgSubdirs.isOn();
          String mask = (subDirs ? "**/" : "") + widgMask.getText();
          String text = widgText.getText();
          List<File> files = new LinkedList<File>(); 
          try{
            FileSystem.addFileToList(fileSelector.currentDir, mask, files);
            if(text.length() > 0){
              FileSystem.searchInFiles(files, text, searchOutput);
            } else {
              try{ 
                for(File file: files){
                  searchOutput.append("<File=").append(file.getPath()).append(">\n");
                }
                searchOutput.append("<done: search in files>\n");
              }catch(IOException exc){}
            }
          } 
          catch(Exception exc){}
        }
        return true;
      }

    };
    
  }
  
  protected WindowConfirmSearch windSearch;
  
  
  
  /**Action to show the file properties in the info line. This action is called anytime if a line
   * was changed in the file view table. */
  private final GralUserAction actionOnFileSelection = new GralUserAction(){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      if(actionCode == KeyCode.tableLineSelect){
        GralTableLine_ifc line = (GralTableLine_ifc) params[0];
        Object oData = line.getUserData();
        if(oData instanceof FileRemote){
          FileRemote file = (FileRemote)oData;  ////
          currentFile = file;
          if(file.exists()){
            String sDir = file.getParent();
            String sName = file.getName();
            indexSelection.put(sDir, sName);
            //System.out.println("GralFileSelector: " + sDir + ":" + sName);
            if(actionOnFileSelected !=null){
              actionOnFileSelected.userActionGui(0, selectList.wdgdTable, line, file);
            }
            if(line.getCellText(kColDesignation).startsWith("?")){
              completeLine(line, file, System.currentTimeMillis());
            }
          }
        }
      }
      return true;
    }
  };
  
  
  
  private final SelectMask_ifc actionOnMarkLine = new SelectMask_ifc(){

    @Override public int getSelection()
    {return 0;
    }

    @Override public int setDeselect(int mask, Object oData)
    { assert(oData instanceof FileRemote);
      FileRemote file = (FileRemote)oData;
      file.resetSelected(mask);
      return mask;
    }

    @Override public int setSelect(int mask, Object oData)
    { assert(oData instanceof FileRemote);
      FileRemote file = (FileRemote)oData;
      file.setSelected(mask);
      return mask;
    }
    
  };

  
  /**Implementation of the base widget.
   */
  protected class FileSelectList extends GralSelectList
  {
    final GralFileSelector outer;
    
    FileSelectList(GralFileSelector outer){
      //super(name, mng);
      this.outer = outer;
      super.setLeftRightKeys(KeyCode.ctrl + KeyCode.pgup, KeyCode.pgdn);
    }
    
    @Override public boolean actionOk(Object userData, GralTableLine_ifc line)
    { boolean done = true;
      File file = (File)userData;
      String fileName;
      //File dir = data.file.getParentFile();
      //String sDir = dir ==null ? "/" : FileSystem.getCanonicalPath(dir) + "/";
      String sName = line.getCellText(1);
      if(sName.equals("..")){
        actionLeft(userData, line);
        //String sParent = getParentDir(file);
        //if(sParent !=null){
        //  fillIn(sParent); 
        //}
      } else if(file !=null && file.isDirectory()){
        actionRight(userData, line);
      } else if(file !=null && ((fileName = file.getName()).endsWith(".zip") || fileName.endsWith(".jar"))){
        actionRightZip(userData, line);
      } else {
        if(actionOnEnterFile !=null){
          actionOnEnterFile.userActionGui(KeyCode.enter, widgdPath, file);
        } else {
          done = false;
        }
      }
      return done;
    }
    
    
    private String getParentDir(File data){
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
     * @param userData The {@link GralTableLine_ifc#getUserData()} from line. 
     *   It is a java.io.File or a {@link FileRemote}
     *   which is currently selected. This file is stored as current for the current directory. 
     *   The parent of the file is the directory which is shown yet.
     * @see org.vishia.gral.widget.GralSelectList#actionLeft(java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override public void actionLeft(Object userData, GralTableLine_ifc line)
    {
      //FileRemote currentFile = (FileRemote)userData;
      if(currentDir !=null){
        String sDir = currentDir.getParent();
        String sName = currentDir.getName();
        FileRemote parentDir = currentDir.getParentFile();
        if(parentDir !=null){
          indexSelection.put(sDir, currentDir.getName());
          //System.out.println("GralFileSelector: " + sDir + ":" + sName);
          fillIn(parentDir, false); 
        }
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
        //indexSelection.put(currentFile.getParent(), currentFile.getName());
        fillIn(currentFile, false);
        //fillIn(data.getParent() + "/" + data.getName());
      }
    }
    
    
    public void actionRightZip(Object userData, GralTableLine_ifc line)
    {
      FileRemote currentFile = (FileRemote)userData;
      FileRemote fileZipAsDir = FileAccessZip.examineZipFile(currentFile);
      //FileZip fileZip = new FileZip(currentFile);
      fillIn(fileZipAsDir, true);
    }
    
    
    
    /* (non-Javadoc)
     * @see org.vishia.gral.widget.SelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc line)
    { boolean ret = true;
      ret = outer.actionUserKey(keyCode, oData, line);
      return ret;
    }


  } //selectList implementation
  
  
  
  
  
  
  
  
  
  
  
  
  /**Number of columns of the table. */
  public static final int zColumns = 4;
  
  /**Column which contains the designation of entry.
   * <ul>
   * <li>"/" a directory
   * <li>">" a linked directory (unix systems, symbolic link)
   * <li>" " space, normal file
   * <li>"*" a linked file
   * <li>"#" comparison result: there are differences
   * <li>"+" comparison result: contains more
   * <li>"-" comparison result: contains less
   * 
   * </ul>
   */
  public static final int kColDesignation = 0;
  
  /**Column which contains the filename. The column contains either the name of the file
   * or ".." or "--unknown--".
   */
  public static final int kColFilename = 1;
  
  /**Column which contains the length of file
   */
  public static final int kColLength = 2;
  
  /**Column which contains the time stamp
   */
  public static final int kColDate = 3;
  
  public static final char kSortName = 'n';
  
  public static final char kSortNameNonCase = 'N';
  
  public static final char kSortExtension = 'x';
  
  public static final char kSortExtensionNonCase = 'X';
  
  public static final char kSortDateNewest = 'd';
  
  public static final char kSortDateOldest = 'o';
  
  public static final char kSortSizeLargest = 'l';
  
  public static final char kSortSizeSmallest = 's';
  
  
  public static MenuTexts contextMenuTexts = new MenuTexts();
  
  
  private char sortOrder = kSortName;
  
  /**The implementation of SelectList. */
  protected FileSelectList selectList;
  
  /**This action will be called any time when the selection of a current file is changed. */
  GralUserAction actionOnFileSelected;
  

  /**This index stores the last selected file for any directory path which was used.
   * If the directory path is reused later, the same file will be selected initially.
   * It helps by navigation through the file tree.
   * <ul>
   * <li>The key is the path in canonical form with '/' as separator (in windows too!) 
   *   but without terminating '/'.
   * <li>The value is the name of the file in this directory.   
   * </ul>
   */
  private final Map<String, String> indexSelection = new TreeMap<String, String>(); 
  
  //int lineSelected;
  
  private final RefreshTimed refreshTimed = new RefreshTimed();
  
  /**The widget for showing the path. */
  protected GralTextField widgdPath;
  
  String sDatePrefixNewer = "";
  SimpleDateFormat dateFormatNewer = new SimpleDateFormat("?yy-MM-dd HH:mm:ss"); 
  
  String sDatePrefixToday = "";
  SimpleDateFormat dateFormatToday = new SimpleDateFormat("@ HH:mm:ss"); 
  
  String sDatePrefixYear = "";
  SimpleDateFormat dateFormatYear = new SimpleDateFormat("MMM-dd HH:mm:ss"); 
  
  String sDatePrefixOlder = "";
  SimpleDateFormat dateFormatOlder = new SimpleDateFormat("yy-MM-dd HH:mm:ss"); 
  
  //final MainCmd_ifc mainCmd;

  /**The current shown directory. */
  FileRemote currentDir;
  
  FileRemote currentFile;
  
  String sCurrentDir;
  
  
  /**The directory which was used on start. */
  FileRemote originDir;
  
  
  
  
  /**This action will be called on pressing enter or mouse-click on a simple file.
   */
  private GralUserAction actionOnEnterFile;

  /**This action will be called on pressing enter or mouse-click on a directory.
   * Usual the directory can be entered and showed. But the user can do any other action.
   * If this action returns false, the default behavior: enter the directory will be done.
   */
  private GralUserAction actionOnEnterDirectory;
  
  
  /**This action will be called on pressing enter or mouse-click on the path text field
   * if it contains any text which can't assigned to an existing file.
   * 
   */
  private GralUserAction actionOnEnterPathNewFile;
  
  
  
  
  GralUserAction actionSetFileAttribs;
  
  
  private GralInfoBox questionWindow;
  
  
  private final EventSource evSrc = new EventSource("GralFileSelector"){
    
  };
  

  
  public GralFileSelector()
  {
    selectList = new FileSelectList(this);
    //this.mainCmd = mainCmd;
  }
  
  
  
  public void setDateFormat(String sFormat){
    dateFormatOlder = new SimpleDateFormat(sFormat);
  }
  
  
  /**Sets the widgets of this instance to a panel.
   * The panel and the position in the panel 
   * should be set before using {@link GralMngBuild_ifc#selectPanel(String)} and 
   * {@link GralMngBuild_ifc#setPositionInPanel(float, float, float, float, char)}.
   * The instance has more as one widget, all widgets are set in the area of the given position.
   * The position area should be a range of at least 3 lines.
   * @param panelMng The panelManager. 
   * @param name The name of the table widget. The Text-widget for the path gets the name * "-Path".
   * @param rows Number of rows to show
   * @param columns Array with column width.
   * @param size Presentation size. It is a character 'A'..'E', where 'A' is a small size. The size determines
   *        the font size especially. 
   */
  public void setToPanel(GralMngBuild_ifc panelMng, String name, int rows, int[] columns, char size)
  {
    //The macro widget consists of more as one widget. Position the inner widgets:
    GralPos posAll = panelMng.getPositionInPanel();
    GralPanelContent panel = posAll.panel;
    String sPanel = panel.getName();
    //Text field for path above list
    panelMng.setPosition(posAll, GralPos.same, GralPos.size + 2.0F, GralPos.same, GralPos.same-6, 1, 'r');
    widgdPath = panelMng.addTextField(name + "-Path", true, null, null);
    widgdPath.setActionChange(actionSetPath);
    widgdPath.setBackColor(panelMng.getColor("pye"), 0xeeffff);  //color pastel yellow
    GralMenu menuFolder = widgdPath.getContextMenu();
    menuFolder.addMenuItemGthread(null, "refresh [cR]", actionRefreshFileTable);
    panelMng.setPosition(GralPos.same, GralPos.same, GralPos.next+0.5f, GralPos.size+5.5f, 1, 'd');
    panelMng.addButton("xxx", null, "favor");
    //the list
    panelMng.setPosition(posAll, GralPos.refer+2, GralPos.same, GralPos.same, GralPos.same, 1, 'd');
    selectList.setToPanel(panelMng, name, rows, columns, size);
    selectList.wdgdTable.addContextMenuEntryGthread(1, null, contextMenuTexts.refresh, actionRefreshFileTable);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortNameCase, actionSortFilePerNameCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortNameNonCase, actionSortFilePerNameNonCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortExtCase, actionSortFilePerExtensionCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortExtNonCase, actionSortFilePerExtensionNonCase);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortDateNewest, actionSortFilePerTimestamp);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortOldest, actionSortFilePerTimestampOldestFirst);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sizeLarge, actionSortFilePerLenghLargestFirst);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortSizeSmall, actionSortFilesPerLenghSmallestFirst);
    selectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.deselectRecursFiles, actionDeselectDirtree);

    //store this in the GralWidgets to get back from widgets later.
    widgdPath.setContentInfo(this);
    selectList.wdgdTable.setContentInfo(this);
    selectList.wdgdTable.specifyActionOnLineSelected(actionOnFileSelection);
    selectList.wdgdTable.specifyActionOnLineMarked(actionOnMarkLine);
    panelMng.setPosition(5, 0, 10, GralPos.size + 40, 1, 'd');
    questionWindow = GralInfoBox.createTextInfoBox(panelMng, "questionInfoBox", "question");  
    panelMng.selectPanel(sPanel);  //if finished this panel is selected for like entry.
  }
  
  
  /**Creates the window to confirm search in files. This window can be created only one time
   * for all file panels, if the application has more as one. On activating the directory
   * and the file panel to show results should be given. But only one search process can be run
   * simultaneously.
   * @return The created window.
   */
  public static WindowConfirmSearch createWindowConfirmSearchGthread(GralMngBuild_ifc mng){
    WindowConfirmSearch wind = new WindowConfirmSearch();
    mng.selectPanel("primaryWindow");
    mng.setPosition(-24, 0, -67, 0, 1, 'r'); //right buttom, about half less display width and hight.
    wind.windConfirmSearch = mng.createWindow("windConfirmSearch", "search in file tree", GralWindow.windConcurrently);
    mng.setPosition(4, GralPos.size -3.5f, 1, -1, 0, 'd', 0.5f);
    wind.widgPath = mng.addTextField("path", false, "path", "t");
    wind.widgMask = mng.addTextField("mask", true, "search name/mask:", "t");
    wind.widgText = mng.addTextField("containsText", true, "contains text:", "t");
    
    mng.setPosition(-5, GralPos.size - 1, 1, -1, 0, 'r',2);
    wind.widgProgression = mng.addValueBar(null, null);
    mng.setPosition(-1, GralPos.size - 3, 1, GralPos.size + 8, 0, 'r',2);
    mng.addButton(null, wind.actionFileSearch, "esc", null, "esc");
    wind.widgSubdirs = mng.addSwitchButton(null, null, "subdirs", null, "subdirs", "wh", "gn");
    wind.widgSearch = mng.addButton(null, wind.actionFileSearch, "search", null, "search");
    wind.widgSearch.setPrimaryWidgetOfPanel();
    return wind;
  }
  
  /**Sets an action which is called any time when another line is selected.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void setActionOnFileSelected(GralUserAction actionOnLineSelected){
    this.actionOnFileSelected = actionOnLineSelected;
  }
  


  public String getCurrentDirPath(){ return sCurrentDir; }
  
  public void setOriginDir(FileRemote dir){ originDir = dir; }

  
  /**Sets the sort order of entries.
   * Valid chars for 'sortOrder' are:
   * <ul>
   * <li>n: sort by name, case sensitive, A..Z a..z in ASCII order
   * <li>N: sort by name, non case-sensitive, Aa..Zz, other characters in ASCII order but '_' first.
   * <li>x: sort by extension, case sensitive. sort by names with equal extension, case sensitive
   * <li>X: sort by extension, non case sensitive. sort by names with equal extension, non case sensitive
   * <li>d: sort by timestamp, newest first
   * <li>o: sort by timestamp, oldest first.
   * <li>l: sort by size, largest first.
   * <li>s: sort by size, smallest first.
   * </ul>
   * @param sortOrder
   */
  public void setSortOrder(char sortOrder){
    this.sortOrder = sortOrder;
  }
  
  
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
  
  
  
  /**This action will be called on pressing enter or mouse-click on a directory.
   * Usual the directory can be entered and showed. But the user can do any other action.
   * If this action returns false, the default behavior: enter the directory will be done.
   */
  public GralUserAction setActionOnEnterDirectory(GralUserAction newAction)
  { GralUserAction oldAction = actionOnEnterDirectory;
  actionOnEnterDirectory = newAction;
    return oldAction;
  }
  
  
  /**This action will be called on pressing enter or mouse-click on the path text field
   * if it contains any text which can't assigned to an existing file.
   * 
   */
  public GralUserAction setActionOnEnterPathNewFile(GralUserAction newAction)
  { GralUserAction oldAction = actionOnEnterPathNewFile;
    actionOnEnterPathNewFile = newAction;
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
    fillIn(originDir, true);
  }
  
  
  
  /**It is the refresh operation.
   * 
   */
  public void fillInCurrentDir(){
    if(currentDir !=null){
      fillIn(currentDir, true);
    }
  }
  

  
  /**Fills the content with given directory.
   * @param fileIn The directory which's files are shown.
   * @param bCompleteWithFileInfo false then write only file names, without information about the file.
   */
  public void fillIn(FileRemote fileIn, boolean bCompleteWithFileInfo) //String path)
  { long timenow = System.currentTimeMillis();
    FileRemote dir = fileIn, file = null;
    boolean bRefresh = bCompleteWithFileInfo;
    if((timenow - fileIn.timeRefresh) < 10000){
      if(!fileIn.isDirectory()){
        dir = fileIn.getParentFile(); file = fileIn;
      }
    } else { //unknown whether it is a directory
      bRefresh = true;
    }
    if(!bRefresh && (timenow - dir.timeChildren) > 10000){
      bRefresh = true;
    }
    if(bRefresh){
    //if((bCompleteWithFileInfo || !(fileIn).isTested())){
      //only refresh if it is necessary (not tested) or it should be refreshed, it means complete.
      if(callbackEventFillIn.occupy(null, fileIn, false)){ //prevent more as one invocation in the same time.
        //selectList.wdgdTable.clearTable(); 
        if(selectList.wdgdTable.size()==0){
          String[] line = new String[zColumns];
          line[kColDesignation] = "";
          line[kColFilename] = "--waiting--";
          line[kColDate] = "";
          selectList.wdgdTable.insertLine(null, -1, line, null);
        }
        callbackEventFillIn.bCompleteWithFileInfo = bCompleteWithFileInfo;
        (fileIn).refreshPropertiesAndChildren(callbackEventFillIn);  //fills the wdgdTable in callbackFillIn, calls fillInRefreshed there.
      } else {
        System.err.println(Assert.stackInfo("GralFileSelector.fillIn - second call is not advisable for the same card.", 4));
      }
    } else {
      //The file is refreshed in the last time, don't access the file system.
      fillInRefreshed(fileIn, bCompleteWithFileInfo);
    }
  }
  
  
  /**Fills the content with given directory without accessing the file system. 
   * All information are contained in the fileIn.
   * @param fileIn Either a directory which's files are shown or a file inside a directory.
   *   In the second case the directory is shown and the file is selected.
   * @param bCompleteWithFileInfo true if alle files in the directory is completed. 
   *   If false then any file is tested.  
   */
  private void fillInRefreshed(FileRemote fileIn, boolean bCompleteWithFileInfo) //String path)
  {
    boolean bAllFilesCompleteWithFileInfo = true;
    //selectList.wdgdTable.clearTable(); 
    FileRemote dir = null;
    if(fileIn !=null && fileIn.exists()){
      if(fileIn.isDirectory()){
        dir =fileIn;
      } else {
        dir = fileIn.getParentFile(); 
        String sDir = FileSystem.getCanonicalPath(dir); //with / as separator!
        String sFile = fileIn.getName();
        indexSelection.put(sDir, sFile);
      }
      if(this.currentDir !=dir){
        Assert.stop();
      }
      this.currentDir = dir;
      if(originDir == null){
        originDir = dir; //path;      //sets on the first invocation. 
      }
      this.sCurrentDir = dir.getPath();
      if(sCurrentDir.endsWith("/"))
        stop();
      widgdPath.setText(sCurrentDir, -1);
    } else {
      widgdPath.setText("?? " + fileIn.getAbsolutePath()); 
      this.currentDir = fileIn;  //though it does not exist, store it for refresh (may be exist later).
      if(originDir == null){
        originDir = fileIn; //path;      //sets on the first invocation. 
      }
      //special case: no files:
      String[] line = new String[zColumns];
      line[kColDesignation] = "";
      line[kColFilename] = "--not found--";
      line[kColDate] = "";
      selectList.wdgdTable.insertLine(null, -1, line, null);
      //lineCt +=1;
    }
    //widgdPath.setSelection("|..<");
    long timeNow = System.currentTimeMillis();
    
    int lineSelect1 = 0;  
    if(dir !=null && dir.exists() && dir.isDirectory()){
      File[] files = dir.listFiles();
      if(files !=null){ 
        Map<String, File> sortFiles = new TreeMap<String, File>();
        for(File file: files){
          if(file ==null){
            System.err.println("GralFileSelector.fillInRefreshedFiles() - file is null;");
          } else {
            String sort;
            boolean bCompleteFileWithInfo = bCompleteWithFileInfo 
                     || (file instanceof FileRemote && ((FileRemote)file).isTested());
            if(bCompleteFileWithInfo){
              switch(sortOrder){
              case kSortName: {
                String sName = file.getName();
                if(file.isDirectory()){ sName += "/"; }
                sort = (file.isDirectory()? "D" : "F") + sName;
              } break;
              case kSortNameNonCase: {
                String sName = file.getName().toLowerCase();
                if(file.isDirectory()){ sName += "/"; }
                sort = (file.isDirectory()? "D" : "F") + sName;
              } break;
              case kSortExtension: {
                String sName = file.getName();
                int posDot = sName.lastIndexOf('.');
                String sExt = sName.substring(posDot+1);
                if(file.isDirectory()){ sName += "/"; }
                sort = (file.isDirectory()? "D" : "F") + sExt + sName;
              } break;
              case kSortExtensionNonCase: {
                String sName = file.getName().toLowerCase();
                int posDot = sName.lastIndexOf('.');
                String sExt = sName.substring(posDot+1);
                if(file.isDirectory()){ sName += "/"; }
                sort = (file.isDirectory()? "D" : "F") + sExt + sName;
              } break;
              case kSortDateNewest: {
                long nDate = -file.lastModified();
                String sDate = String.format("%016X", nDate);
                String sName = file.getName().toLowerCase();
                sort = (file.isDirectory()? "D" : "F") + sDate + sName;
              } break;
              case kSortDateOldest: {
                long nDate = file.lastModified();
                String sDate = String.format("%016X", nDate);
                String sName = file.getName().toLowerCase();
                sort = (file.isDirectory()? "D" : "F") + sDate + sName;
              } break;
              case kSortSizeLargest: {
                long nSize = 0x7fffffffffffffffL - file.length();
                String sSize = String.format("%016d", nSize);
                String sName = file.getName().toLowerCase();
                sort = (file.isDirectory()? "D" : "F") + sSize + sName;
              } break;
              case kSortSizeSmallest: {
                long nSize = file.length();
                String sSize = String.format("%016d", nSize);
                String sName = file.getName().toLowerCase();
                sort = (file.isDirectory()? "D" : "F") + sSize + sName;
              } break;
              default: { sort = file.getName(); }
              }
            } else {
              //without file properties
              switch(sortOrder){
                case kSortName: {
                  String sName = file.getName();
                  if(file.isDirectory()){ sName += "/"; }
                  sort = (file.isDirectory()? "D" : "F") + sName;
                } break;
                case kSortNameNonCase: {
                  String sName = file.getName().toLowerCase();
                  if(file.isDirectory()){ sName += "/"; }
                  sort = (file.isDirectory()? "D" : "F") + sName;
                } break;
                case kSortExtension: {
                  String sName = file.getName();
                  int posDot = sName.lastIndexOf('.');
                  String sExt = sName.substring(posDot+1);
                  if(file.isDirectory()){ sName += "/"; }
                  sort = (file.isDirectory()? "D" : "F") + sExt + sName;
                } break;
                case kSortExtensionNonCase: {
                  String sName = file.getName().toLowerCase();
                  int posDot = sName.lastIndexOf('.');
                  String sExt = sName.substring(posDot+1);
                  if(file.isDirectory()){ sName += "/"; }
                  sort = (file.isDirectory()? "D" : "F") + sExt + sName;
                } break;
                default: { sort = file.getName().toLowerCase(); }
              }
              
            }
            sortFiles.put(sort, file);
          }
        }
        int zLines = selectList.wdgdTable.size();
        int lineCt = 0; //count lines to select the line number with equal sFileSelect.
        if(dir.getParentFile() !=null){
          //write < .. line for parent seletion.
          
          GralTableLine_ifc line;
          if(lineCt < zLines){ line = selectList.wdgdTable.getLine(lineCt); }
          else {
            line = selectList.wdgdTable.insertLine("..", -1, null, null);
            zLines +=1;
          }
          if(!line.getCellText(kColFilename).equals("..")){
            line.setCellText("<", kColDesignation);
            line.setCellText("..", kColFilename);
            line.setCellText("", kColLength);
            line.setCellText("", kColDate);
            selectList.wdgdTable.replaceLineKey(line, null);
          }
          line.setUserData(dir);  //The ".." represents the directory which is shown (instead ".")
          lineCt +=1;
        }
        //The file or directory which was the current one while this directory was shown lastly:
        String sFileCurrentline = indexSelection.get(sCurrentDir);
        for(Map.Entry<String, File> entry: sortFiles.entrySet()){
          //String[] line = new String[zColumns];
          File file = entry.getValue();
          boolean bCompleteFileWithInfo = bCompleteWithFileInfo 
            || (file instanceof FileRemote && ((FileRemote)file).isTested());
          String sFileName = file.getName();
          GralTableLine_ifc tline;
          if(lineCt < zLines){ 
            tline = selectList.wdgdTable.getLine(lineCt);
          }
          else {
            tline = selectList.wdgdTable.insertLine(sFileName, -1, null, null);
            zLines +=1;
          }
          String sCell = tline.getCellText(kColFilename);
          boolean botherfile = sCell == null || !sCell.equals(sFileName);
          boolean isChanged = botherfile;
          if(botherfile){
            String sDesignation = file.isDirectory() ? "/" : "";
            tline.setCellText(sDesignation, kColDesignation);
            tline.setCellText(sFileName, kColFilename);
            tline.setUserData(file);
            tline.setDeselect(1, null);
            selectList.wdgdTable.replaceLineKey(tline, sFileName);
          } else { //same file, don't change line.
            long dateFile = file.lastModified();
            isChanged = tline.setContentIdent(dateFile) != dateFile;
          }
          if(sFileCurrentline != null && sFileName.equals(sFileCurrentline)){
            lineSelect1 = lineCt;
            if(isChanged){
              //this.lineSelected = -1;  //remove info about selection, select newly because content is changed.
            }
          }
          if(isChanged){
            if(bCompleteFileWithInfo){
              completeLine(tline, file, timeNow);
            } 
            else { //!bCompleteFileWithInfo
              bAllFilesCompleteWithFileInfo = false;
              tline.setCellText("?", kColLength);
              tline.setCellText("?", kColDate);
              tline.setContentIdent(0);
            }
            if(bCompleteFileWithInfo && actionSetFileAttribs !=null){
              actionSetFileAttribs.exec(0, selectList.wdgdTable, tline);  //color for the line.
            }
          }
          lineCt +=1;
        }
        while(lineCt < zLines){
          GralTableLine_ifc tline = selectList.wdgdTable.getLine(lineCt);
          selectList.wdgdTable.deleteLine(tline);
          zLines = selectList.wdgdTable.size();
        }
        if(lineCt ==0){
          //special case: no files:
          selectList.wdgdTable.clearTable(); 
          String[] line = new String[zColumns];
          line[kColDesignation] = "";
          line[kColFilename] = "--empty--";
          line[kColDate] = "";
          selectList.wdgdTable.insertLine(null, -1, line, null);
          lineCt +=1;
        }
      } else {
        //faulty directory
        selectList.wdgdTable.clearTable(); 
        String[] line = new String[zColumns];
        line[kColDesignation] = "";
        line[kColFilename] = "--not found-1--";
        line[kColDate] = "";
        selectList.wdgdTable.setValue(GralMng_ifc.cmdInsert, -1, line, currentDir);
      }
    } else {
      //faulty directory
      selectList.wdgdTable.clearTable(); 
      String[] line = new String[zColumns];
      line[kColDesignation] = "";
      line[kColFilename] = "--not found--";
      line[kColDate] = "";
      selectList.wdgdTable.setValue(GralMng_ifc.cmdInsert, -1, line, currentDir);
    }
    GralTableLine_ifc currentLine = selectList.wdgdTable.getCurrentLine();
    if(currentLine == null || lineSelect1 != currentLine.getLineNr()){
      //HINT: setCurrentCell refreshes the line. a current marking action from GUI-user will be aborted therefore.
      selectList.wdgdTable.setCurrentCell(lineSelect1, 1);
      //this.lineSelected = lineSelect1; 
    }
    //selectList.wdgdTable.repaint(200,200);
    if(!bAllFilesCompleteWithFileInfo){
      refreshTimed.delayedFillin(1500);
    }
    
  }
  

  
  @SuppressWarnings("boxing")
  private void completeLine(GralTableLine_ifc tline, File file, long timeNow){
    final String sDesign;
    if(file instanceof FileRemote && ((FileRemote)file).isSymbolicLink()){ 
      sDesign =  file.isDirectory() ? ">" : "s"; 
    }
    else if(file.isDirectory()){ sDesign = "/"; }
    else { sDesign = " ";}
    tline.setCellText(sDesign, kColDesignation);
    long fileTime = file.lastModified();
    long diffTime = timeNow - fileTime;
    Date timestamp = new Date(fileTime);
    String sDate;
    if(diffTime < -10 * 3600000L){
      sDate = sDatePrefixNewer + dateFormatNewer.format(timestamp);
    } else if(diffTime < 18*3600000){
      //files today
      sDate = sDatePrefixToday + dateFormatToday.format(timestamp);
    } else if(diffTime < 320 * 24* 3600000L){
      sDate = sDatePrefixYear + dateFormatYear.format(timestamp);
    } else {
      sDate = sDatePrefixOlder + dateFormatOlder.format(timestamp);
    }
    tline.setCellText(sDate, kColDate);
    //line[kColDate] = sDate;
    //
    String sLength;
    long fileLength = file.length();
    if(fileLength < 1024){
      sLength = "" + fileLength;
    } else if(fileLength < 10000){
      sLength = String.format("%1.1f k", fileLength / 1024.0f);
    } else if(fileLength < 1000000){
      sLength = String.format("%3.0f k", fileLength / 1024.0f);
    } else if(fileLength < 10000000){
      sLength = String.format("%1.1f M", fileLength / (1024 * 1024.0f));
    } else if(fileLength < 1000000000){
      sLength = String.format("%3.0f M", fileLength / (1024 * 1024.0f));
    } else if(fileLength < 10000000000L){
      sLength = String.format("%1.1f G", fileLength / (1024 * 1024.0f));
    } else {
      sLength = String.format("%2.0f G", fileLength / (1024 * 1024.0f));
    }
    tline.setCellText(sLength, kColLength);
    tline.setContentIdent(fileTime);
    //line[kColLength] = sLength;
    
  }
  
  
  
  public void checkRefresh(long since){
    if(currentDir !=null && !currentDir.isTested(since)){
      fillIn(currentDir, true);
    }
  }
  
  
  public FileRemote getCurrentDir(){ return currentDir; }

  
  /**Gets the selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public File XXXgetSelectedFile()
  {
    if(selectList.wdgdTable == null){
      stop();
      return null;
    }
    GralTableLine_ifc line = selectList.wdgdTable.getCurrentLine();
    if(line !=null){
      File data = (File)line.getUserData();
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
  public void setFocus(){ selectList.wdgdTable.setFocus(); }
  
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

  
  /**This class organized a one time or cyclic refreshing of the current content.
   */
  final class RefreshTimed implements Runnable
  {
    Thread thread;
    
    int timeRepeat = 2000;
    
    long timeStampRepeat;
    
    boolean bRepeat = false;
    //boolean isRunning = false;
    
    void delayedFillin(int delayMillisec){
      timeStampRepeat = System.currentTimeMillis() + delayMillisec;
      if(thread ==null){
        thread = new Thread(this, "FileRefresh");
        //isRunning = true;
        thread.start();
      }
    }
    
    @Override public void run()
    { long timeWait;
      do {
        timeWait = timeStampRepeat - System.currentTimeMillis();
        if(timeWait >0){
          synchronized(this){ 
            try{ wait(timeWait);} catch(InterruptedException exc){}
          }
        } else {
          System.out.println("GralFileSelector - delayedFillin");
          fillInCurrentDir();
          timeStampRepeat += timeRepeat;
        }
      } while(timeWait > 0 || bRepeat);
      thread = null;
    }
  }
  
  
  final EventConsumer callbackFillIn = new EventConsumer(){
    @Override public int processEvent(Event evP) {
      ///
      FillinCallback callback = (FillinCallback)evP;
      FileRemote dir = callback.getFileSrc();  //it is completed meanwhile
      //Timeshort.sleep(200); //test
      fillInRefreshed(dir, callback.bCompleteWithFileInfo);
      //setFocus();    //don't set the focus, it may be false. Only fill.
      return 1;
    }
    @Override public String toString(){ return "GralFileSelector - callback fillin"; }

  };
  
  
  /**Only one event instance for fillIn-callback. It should be called only one time. */
  final class FillinCallback extends FileRemote.CallbackEvent{
    boolean bCompleteWithFileInfo;
    FillinCallback(){ 
      //note: call without source because it is not occupied.
      super(callbackFillIn, null, evSrc); 
    }
  }
  
  /**Callback event for this file table, re-used but private for the file table. 
   * It uses {@link GralFileSelector#callbackFillIn} as its {@link EventConsumer}
   * to execute {@link GralFileSelector#fillInRefreshed(FileRemote, boolean)} as callback routine.
   * */
  private final FillinCallback callbackEventFillIn = new FillinCallback();
  
  

  
  
  
  /**Action on [Enter]-key on the path text field.
   * <ul>
   * <li>If the text represents an existing directory, it is used as current.
   * <li>If the text represents an existing file, the parent directory is used as current
   *   and the file is stored as current in this directory, see {@link #indexSelection}.
   * <li>If the path is absolute (starting with '/' or '\\' maybe with leading drive letter for windows
   *   then it is used absolute. If the path is not absolute, it is used starting from the current one.
   * <li>If the path contains a name only, it is a file. You can    
   * <li>If the text represents a file which is not existing, but its directory path is existing,
   *   a quest is posted whether the file should be created. On [OK] either the file will be
   *   created as new file or its path is returned.    
   * </ul>
   */
  GralUserAction actionSetPath = new GralUserAction(){
    @Override
    public boolean userActionGui(int key, GralWidget widg, Object... params)
    {
      if(key == KeyCode.enter){
        String sPath = widg.getValue();
        int posWildcard = sPath.indexOf('*');
        if(posWildcard >=0){
          
        } else {
          FileRemote file = originDir.itsCluster.getFile(sPath);
          file.refreshProperties(null);
          if(file.isDirectory()){
            fillIn(file, false);
          } else if(file.isFile()){
            FileRemote dir = file.getParentFile();
            String sDir = FileSystem.getCanonicalPath(dir);
            String sFile = file.getName();
            indexSelection.put(sDir, sFile);
            fillIn(dir, false);
          } else {
            File parent = file.getParentFile();
            if(parent.exists()){
              if(actionOnEnterPathNewFile !=null){
                actionOnEnterPathNewFile.userActionGui(KeyCode.enter, widgdPath, file);
              } else {
                String question = "Do you want to create file\n"
                  +file.getName()
                  + "\n  in directory\n"
                  + parent.getPath();
                questionWindow.setText(question);
                questionWindow.setActionOk(confirmCreate);
                questionWindow.setWindowVisible(true);
              }
            } else {
              questionWindow.setText("unknown path");
              questionWindow.setActionOk(null);
              questionWindow.setWindowVisible(true);
            }
          }
        }
        //widg.getMng().widgetHelper.showContextMenu(widg);
      }
      
      stop();
      return false;
    }
  };
  

  public void setSortOrderFiles(char order){
    setSortOrder(order);
    fillInCurrentDir();
  }
  

  
  GralUserAction actionSortFilePerNameCase = new GralUserAction("actionSortFilePerNameCase")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortName);
      return true;
  } };


  GralUserAction actionSortFilePerNameNonCase = new GralUserAction("actionSortFilePerNameNonCase")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortNameNonCase);
      return true;
  } };


  GralUserAction actionSortFilePerExtensionCase = new GralUserAction("actionSortFilePerExtensionCase")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortExtension);
      return true;
  } };

  GralUserAction actionSortFilePerExtensionNonCase = new GralUserAction("actionSortFilePerExtensionNonCase")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortExtensionNonCase);
      return true;
  } };

  GralUserAction actionSortFilePerTimestamp = new GralUserAction("actionSortFilePerTimestamp")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortDateNewest);
      return true;
  } };

  GralUserAction actionSortFilePerTimestampOldestFirst = new GralUserAction("actionSortFilePerTimestampOldestFirst")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortDateOldest);
      return true;
  } };

  GralUserAction actionSortFilePerLenghLargestFirst = new GralUserAction("actionSortFilePerLenghLargestFirst")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortSizeLargest);
      return true;
  } };

  GralUserAction actionSortFilesPerLenghSmallestFirst = new GralUserAction("actionSortFilesPerLenghSmallestFirst")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      setSortOrderFiles(GralFileSelector.kSortSizeSmallest);
      return true;
  } };


  GralUserAction actionDeselectDirtree = new GralUserAction("actionDeselectDirtree")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
    //if(fileCard !=null){
    if(currentFile !=null){
      currentFile.resetSelectedRecurs(1, null);
      //fileCard.f  //TODO refresh
    }
    return true;
} };



  
  
  /**Sets the origin dir of the last focused file table.
   * <br>
   * Implementation note: The last focused file tab is searched using {@link Fcmd#getLastSelectedFileCards()}.
   */
  GralUserAction actionRefreshFileTable = new GralUserAction(){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        fillInCurrentDir();
        return true;
      } else return false;
    }
  };
  
  GralUserAction confirmCreate = new GralUserAction()
  {
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      return true;
    }
  };
  
  
  /**This class is instantiated static and contains English menu texts. The user can change it
   * touching the public static instance {@link GralFileSelector#contextMenuTexts}
   * before calling {@link GralFileSelector#setToPanel(GralMngBuild_ifc, String, int, int[], char)}. 
   */
  public static class MenuTexts{
    public String refresh = "&Refresh [F5]";
    public String sortNameCase = "&Sort/&Name case sensit";
    public String sortNameNonCase = "&Sort/&Name non-case";
    public String sortExtCase = "&Sort/e&Xt case sensit";
    public String sortExtNonCase = "&Sort/e&Xt non-case";
    public String sortOldest = "&Sort/date &Oldest";
    public String sortDateNewest = "&Sort/&Date newest";
    public String sizeLarge = "&Sort/size &Largest";
    public String sortSizeSmall = "&Sort/size &Smallest";
    public String deselectRecursFiles = "actionDeselectDirtree";
  }
  
  
}
