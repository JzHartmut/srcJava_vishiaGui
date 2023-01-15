package org.vishia.gral.widget;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.fileRemote.FileAccessZip;
import org.vishia.fileRemote.FileCluster;
import org.vishia.fileRemote.FileMark;
import org.vishia.fileRemote.FileRemote;
import org.vishia.fileRemote.FileRemoteCallback;
import org.vishia.gral.base.GralButton;
import org.vishia.gral.base.GralSwitchButton;
import org.vishia.gral.base.GralMenu;
import org.vishia.gral.base.GralMng;
import org.vishia.gral.base.GralPos;
import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralTextBox;
import org.vishia.gral.base.GralTextField;
import org.vishia.gral.base.GralValueBar;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWidgetBase;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralColor;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralTable_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidgetBase_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.util.Debugutil;
import org.vishia.util.ExcUtil;
import org.vishia.util.FileSystem;
import org.vishia.util.IndexMultiTable;
import org.vishia.util.KeyCode;
import org.vishia.util.Removeable;
import org.vishia.util.MarkMask_ifc;
import org.vishia.util.SortedTreeWalkerCallback;

/**This class is a large widget which contains a list to select files in a directory, 
 * supports navigation in the directory tree and shows the current path in an extra text field.
 * Additional 'search in files' is supported.
 * <br><br>
 * The type of a file is a {@link FileRemote}. This type inherits from {@link java.io.File}
 * and is supported for a local file system on PC. It is possible to work with any remote file system. 
 * <br><br>
 * The graphical user interface allows some action to do with the right mouse menu:
 * <ul>
 * <li>Refresh content
 * <li>sort files by date latest, newest, by size largest, smallest, by name and by extension.
 * <li>Search content in files with a dialog window.
 * </ul>
 * <b>Callback to users program</b>:<br>
 * The method {@link #specifyActionOnFileSelected(GralUserAction)} allows any action while the user
 * select any file. For example a user's program can show the content of the file.
 * <br><br> 
 * @author Hartmut Schorrig
 *
 */
public class GralFileSelector extends GralWidgetBase implements Removeable //extends GralWidget
{
  
  /**Version, history and copyright/copyleft.
   * <ul>
   * <li>2023-01-15 Now {@link GralFileProperties} is able to aggregate. 
   * <li>2023-01-14 Now a favor which is selected is marked green, remove of tabs works. (!)
   * <li>2023-01-06 progress, practical usage for The.file.Commander
   * <li>2022-12-21 progress, tabs for file cards with {@link GralHorizontalSelector} as before in Fcmd only
   * <li>2022-12-21 progress, now switch between both tables and also [F3] key with a view window.
   * <li>2022-12-20 Favor table activated now at last.
   * <li>2022-12-17 improved while test, positions use now the new {@link GralPos#setAsFrame()}.
   *   A test class {@link org.vishia.gral.test.basics.Test_GralFileSelector} is created for test. 
   * <li>2021-12-18 Hartmut: new feature: {@link #fillIn(FileRemote, boolean)}, if the file is a file not directory
   *   then this file is highlighted. Firstly used on InspcCurveViewApp to get cfg and data from the same directory,
   *   re-read the same file is before by saving the gotten file before. 
   * <li>2018-10-28 Hartmut: The Creation and Position is old style yet. Should invoked in the graphic thread. 
   * TODO: create and position in other (main) thread, createImplWidget_Gthread for Swt incarnation.
   * <li>2018-10-28 Hartmut: The {@link #wdgFavorTable} is unused yet, create only if position is given.
   * <li>2018-10-28 Hartmut: questionWindow is removed yet, TODO: should have better solution. Status line!
   * <li>2018-10-28 Hartmut: {@link #GralFileSelector(String, int, int[], int[])}: The rows argument is used yet for table zLinesMax. 
   *   The old last argument size was never used. replaced by the column designation for the {@link #wdgFavorTable}. 
   *   If it is null, do not create a {@link #wdgFavorTable}.
   * <li>2016-05-02 Hartmut: actionFileSearch writes a result list in the panel of the file window. 
   *   Yet you can copy the path and insert in the directory text widget to select the file. 
   *   TODO: Pressing enter to select, store the list etc.
   * <li>2016-05-02 Hartmut now: derived from GralWidget, it is a large widget 
   * <li>2016-05-03 Hartmut new: {@link #setVisible(boolean)} for this large widget.
   * <li>2015-11-19 Hartmut chg: {@link #fillInCurrentDir()} does not refresh if it was refreshed in the last seconds.
   * <li>2014-12-26 Hartmut chg: {@link #fillIn(FileRemote, boolean)} now with new meaning of boolean: show newly without refresh with the file system.
   *   This is used in callback routines which does a refresh but does not invoke {@link #showFile(FileRemote)}
   *   especially called from {@link org.vishia.commander.Fcmd#refreshFilePanel(FileRemote)}. 
   * <li>2014-01-02 Hartmut chg: does not call 'file.refreshProperties(null);' in {@link #actionSetPath}
   *   because the property whether it is a directory or not should be known. Prevents a timeout waiting in graphic thread!!! 
   * <li>2013-09-06 Hartmut chg: Now a new request of fillIn can be executed with aborting the old one.
   * <li>2013-09-15 Hartmut chg: New implementation of {@link #fillIn(FileRemote, boolean)}
   *   using the new {@link FileRemote#getChildren(org.vishia.fileRemote.FileRemote.ChildrenEvent).} 
   * <li>2013-09-14 Hartmut chg: Sets the background to magenta while refreshing.
   * <li>2013-09-05 Hartmut chg: {@link #getSelectedFiles(boolean, int)} now has that 2 arguments for directory and mark mask.
   *   Now it is possible and necessary for the application to choice whether directories are gotten too.
   *   The usage is improved. If some files are marked but not visible, it was able that the user does not know about
   *   this situation and an unexpected behavior for the user is done. Now only marked files are returned
   *   if the current file is marked too. The method is correct named 'selected' because the selection is returned,
   *   either the current file or all marked. 
   * <li>2013-06-15 Hartmut chg: {@link #fillIn(FileRemote, boolean)} modi of refresh.
   * <li>2013-05-30 Hartmut new: switch refresh mode on and off
   * <li>2013-05-20 Hartmut chg: {@link #fillInRefreshed(FileRemote, boolean)} now does not clear
   *   the table but writes only lines if the data are changed. This method can be called 
   *   in a higher frequency without disturbing the appearance of the table. Used for cyclically refresh.
   * <li>2013-05-20 Hartmut new: Context menu for sort etc. It was part of Fcmd, now here.
   * <li>2013-04-30 Hartmut new: context menu now available. Sort, refresh, deselect
   * <li>2013-04-30 Hartmut new: {@link #checkRefresh(long)}, chg: Don't change the content in the table
   *   if the content is identical with the current presentation in table, for refreshing. 
   * <li>2013-04-30 Hartmut chg: {@link #fillIn(FileRemote, boolean)} now uses the {@link FileRemote#timeRefresh}
   * <li>2013-04-28 Hartmut new: {@link #actionOnMarkLine} changes the select status of {@link FileRemote#setMarked(int)}
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
   * <li>2012-02-14 corr: Anytime a file was selected as current, it was added in the {@link #idxSelectFileInDir}.
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
  public static final String sVersion = "2018-10-28";
  
  //FileRemoteAccessor localFileAccessor = FileRemoteAccessorLocalFile.getInstance();
  
  
  /**A window for search-in-file dialogue.
   * It is instantiated calling {@link GralFileSelector#createWindowConfirmSearchGthread(GralMngBuild_ifc)}.
   * The user can invoke {@link #confirmSearchInFiles(GralFileSelector, Appendable)} to open that window. 
   * Note: An application may have only one of this window thow more as one GralFileSelector may be exist.
   * Therefore the window is not instantiated automatically in this class. The user can instantiate it.
   *
   */
  public class WindowConfirmSearch {
    
    GralWindow windConfirmSearch;

    GralTextField widgPath, widgMask, widgText;
    
    GralTextBox wdgResults;
    
    GralValueBar widgProgression;
    
    /**Buttons. */
    GralButton widgEsc, widgSubdirs, widgSearch;

    GralFileSelector fileSelector;
    
    Appendable searchOutput;
    
    /**Use {@link GralFileSelector#createWindowConfirmSearchGthread(GralMngBuild_ifc)} to create.
    */
    protected WindowConfirmSearch(GralPos refPos, String posName){
      this.windConfirmSearch = new GralWindow(refPos, posName + "Window", "search in file tree", GralWindow.windConcurrently + GralWindow.windResizeable);
      String name = GralFileSelector.this.name;
      this.widgPath = new GralTextField(refPos, "@4-3.2++0.3, 1..-1=path-"+name, "path", "t", GralTextField.Type.editable);
      this.widgMask = new GralTextField(refPos, "mask-"+name, "search name/mask:", "t", GralTextField.Type.editable);
      this.widgText = new GralTextField(refPos, "containsText-"+name, "contains text:", "t", GralTextField.Type.editable);
      this.wdgResults = new GralTextBox(refPos, "@+4+8.5,=result-" + name);
      this.widgProgression = new GralValueBar(refPos, "@-5-1,1..-1=progress" + name);
      new GralButton(refPos, "@-1-3,1+8++2=esc-" + name, "esc", this.actionFileSearch);
      this.widgSubdirs = new GralSwitchButton(refPos, "subdirs-" + name, "subdirs yes", "subdirs no", GralColor.getColor("gn"), GralColor.getColor("wh"));
      this.widgSearch = new GralButton(refPos, "search-" + name, "search", this.actionFileSearch);
      this.widgSearch.setPrimaryWidgetOfPanel();
      this.windConfirmSearch.setVisible(false);

    }
    
    
    /**Shows the window.
     * @param fileSelector
     */
    public void confirmSearchInFiles(GralFileSelector fileSelector, Appendable searchOutput){
      this.fileSelector = fileSelector;
      this.searchOutput = searchOutput;
      this.widgPath.setText(fileSelector.sCurrentDir);
      this.windConfirmSearch.setFocus(); //setWindowVisible(true);
    }
    
    
    /**Action is called if the button search is pressed. */
    GralUserAction actionFileSearch = new GralUserAction("actionFileSearch"){
      @Override
      public boolean exec(int key, GralWidget_ifc widgi, Object... params){ 
        if(widgi.getCmd().equals("search") && key == KeyCode.mouse1Up){
          boolean subDirs = WindowConfirmSearch.this.widgSubdirs.isOn();
          String mask = (subDirs ? "**/" : "") + WindowConfirmSearch.this.widgMask.getText();
          String text = WindowConfirmSearch.this.widgText.getText();
          List<File> files = new LinkedList<File>(); 
          try{
            FileSystem.addFileToList(WindowConfirmSearch.this.fileSelector.currentDir, mask, files);
            WindowConfirmSearch.this.fileSelector.fillIn(files);
            if(text.length() > 0){
              FileSystem.searchInFiles(files, text, WindowConfirmSearch.this.searchOutput);
            } else {
              try{ 
                for(File file: files){
                  WindowConfirmSearch.this.searchOutput.append("<File=").append(file.getPath()).append(">\n");
                }
                WindowConfirmSearch.this.searchOutput.append("<done: search in files>\n");
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
  private final GralUserAction actionOnFileSelection = new GralUserAction("actionOnFileSelection"){
    @Override public boolean userActionGui(int actionCode, GralWidget widgd, Object... params) {
      if(actionCode == KeyCode.userSelect){
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<FileRemote> line = (GralTableLine_ifc<FileRemote>) params[0];
        if(line != null){
          FileRemote file = line.getUserData();
          String sName = line.getCellText(kColFilename);
          if(file.isTested() && file.exists()) {                       //TODO: if the file is not refreshed, new network access, the GUI hangs a long time.
            if(sName.equals("..")){
              GralFileSelector.this.currentFile = file; //.getParentFile();
              GralFileSelector.this.currentDir = file;  //the same, the directory of this panel.
                
            } else {
              GralFileSelector.this.currentDir = file.getParentFile();
              GralFileSelector.this.currentFile = file;
              String sDir = file.getParent();
              GralFileSelector.this.idxSelectFileInDir.put(sDir, file);    //store the last selection.
            }
            //System.out.println("GralFileSelector: " + sDir + ":" + sName);
            if(GralFileSelector.this.actionOnFileSelected !=null){
              GralFileSelector.this.actionOnFileSelected.exec(0, GralFileSelector.this.wdgSelectList.wdgdTable, line, file);
            }
            if(line.getCellText(kColDesignation).startsWith("?")){
              completeLine(line, file, System.currentTimeMillis());
            }
          } else {
            //try refresh the file and show in callback. TODO
            Debugutil.stop();
          }
        }
      }
      return true;
    }
  };
  
  
  
  private final MarkMask_ifc actionOnMarkLine = new MarkMask_ifc(){

    @Override public int getMark()
    {return 0;
    }

    @Override public int setNonMarked(int mask, Object oData)
    { assert(oData instanceof FileRemote);
      FileRemote file = (FileRemote)oData;
      file.resetMarked(mask);
      return mask;
    }

    @Override public int setMarked(int mask, Object oData)
    { assert(oData instanceof FileRemote);
      FileRemote file = (FileRemote)oData;
      file.setMarked(mask);
      return mask;
    }
    
  };

  
  /**Implementation of the base widget.
   */
  protected class FileSelectList extends GralSelectList<FileRemote>
  {
    final GralFileSelector outer;
    
    FileSelectList(GralPos refPos, GralFileSelector outer, String posName, int rows, int[] columns){
      //super(name, mng);
      super(refPos, posName, rows, columns);
      this.outer = outer;
      if(columns.length !=4) { throw new IllegalArgumentException("FileSelectList should have 4 columns");}
      super.setLeftRightKeys(KeyCode.ctrl + KeyCode.pgup, KeyCode.ctrl + KeyCode.pgdn);
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
        if(GralFileSelector.this.actionOnEnterFile !=null){
          GralFileSelector.this.actionOnEnterFile.userActionGui(KeyCode.enter, GralFileSelector.this.widgdPathDir, file);
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
      if(GralFileSelector.this.currentDir !=null){
        String sDir = GralFileSelector.this.currentDir.getParent();
        String sName = GralFileSelector.this.currentDir.getName();
        FileRemote parentDir = GralFileSelector.this.currentDir.getParentFile();
        if(parentDir !=null){
          GralFileSelector.this.idxSelectFileInDir.put(sDir, GralFileSelector.this.currentDir); //currentDir.getName());
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
      fillIn(fileZipAsDir, false);
    }
    
    
    
    /* (non-Javadoc)
     * @see org.vishia.gral.widget.SelectList#actionUserKey(int, java.lang.Object, org.vishia.gral.ifc.GralTableLine_ifc)
     */
    @Override public boolean actionUserKey(int keyCode, Object oData, GralTableLine_ifc lineP) { 
      boolean ret = true;
      if(keyCode == KeyCode.F9) {
        assert(lineP instanceof GralTable.TableLineData);
        @SuppressWarnings("unchecked") GralTable<FileRemote>.TableLineData line = (GralTable<FileRemote>.TableLineData) lineP;
        showFileInfo(line);
      } else {
        ret = this.outer.actionUserKey(keyCode, oData, lineP);
      }
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
  
  
  private char sortOrder = kSortName, sortOrderLast = '0';
  
  /**Determines which time are present in the date/time column.
   * m a c for last modified, last access, creation
   */
  protected char showTime = 'm';
  
  
  
  protected final GralHorizontalSelector<FavorPath> wdgCardSelector;
  
  
  /**The widget for showing the path. */
  protected GralTextField widgdPathDir;



  protected GralButton widgBtnFavor;



  /**The implementation of SelectList. */
  protected FileSelectList wdgSelectList;
  
  
  protected final GralTable<FavorPath> wdgFavorTable;
  
  protected final GralViewFileContent windView;
  
  protected final GralFileProperties wdgFileProperties;

  /**Index of all entries in the visible list. */
  final Map<String, FavorPath> indexFavorPaths;
  
  /**Stores the last selected and used favor path (pressing enter or double click)
   * to select the same line if the favor path will be re-opened.
   */
  String sCurrFavor = "";
  
  /**Association to the current used favor path selection.
  * Note that this instance is re-used for more as one selection.
  */
  FavorPath favorPathInfo;


  GralColor colorBack, colorBackPending;
  
  GralColor colorMarkFavor = GralColor.getColor("lgn");
  
  private final IndexMultiTable<String, GralTableLine_ifc<FileRemote>> idxLines = 
    new IndexMultiTable<String, GralTableLine_ifc<FileRemote>>(IndexMultiTable.providerString);
  
  
  
  //String name; 
  //final int rows; 
  //final int[] columns; 
  //final char size;
  

  /**This index stores the last selected file for any directory path which was used.
   * If the directory path is reused later, the same file will be selected initially.
   * It helps by navigation through the file tree.
   * <ul>
   * <li>The key is the path in canonical form with '/' as separator (in windows too!) 
   *   but without terminating '/'.
   * <li>The value is the name of the file in this directory.   
   * </ul>
   */
  private final Map<String, FileRemote> idxSelectFileInDir = new TreeMap<String, FileRemote>(); 
  
  //int lineSelected;
  
  /**The time after 1970 when the fillin was invoked and finished at last.
   * timeFillinFinished=0, then pending.
   */
  protected long timeFillinInvoked, timeFilesRefreshed, timeFillinFinished;
  
  /**Duration of last fillin. */
  protected int durationRefresh, durationFillin;
  
  protected int refreshCount;
  
  boolean donotCheckRefresh = true;
  
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
  protected FileRemote currentDir;
  
  /**Currently selected file in the table.*/
  protected FileRemote currentFile;
  
  String sCurrentDir;
  
  /**Name of the current file.
   * 
   */
  //String sCurrentFile;
  
  
  /**The directory which was used on start. */
  FileRemote originDir;
  
  
  
  /**This action will be called any time when the selection of a current file is changed. */
  protected GralUserAction actionOnFileSelected;
  

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
  
  
  /**
   * 
   */
  protected GralUserAction actionUserSaveFavors;
  
  GralUserAction actionSetFileAttribs;
  
  
  //private GralInfoBox questionWindow;
  
  
  private enum ERefresh{ doNothing, refreshAll, refreshChildren}

  //private final EventSource evSrc = new EventSource("GralFileSelector"){};
  
  
  
  

  
  /**Creates a new instance of this comprehensive widget for file selection, search and  viewing
   * @param refPosParent Reference position for the whole spread for this comprehensive widget in a panel. 
   * @param posName <code>"@position=name"</code> or <code>"name"</code>, see {@link GralWidget#GralWidget(String, char)}
   * @param rows Number of rows intended to use for the file list (max length)
   * @param columns should have the form <code>new int[]{2,0,-6,-12}</code>, the width of field for size, datd can be justified.
   *   last is date (timestamp), before last is file size, first is mark. The 0 is the rest length of the file name.
   *   TODO proportional determination? 
   * @param bWithFavor true then a favor table is created, select favor. False: prevent this capability
   * @param fileViewer Instance of a file viewer (for content). It is possible to share one fileViewer to more instances of this.
   * @param wdgFileProperties maybe null, then this fuction is not available. 
   *   Should be instantiated recommended by {@link GralFileProperties#createWindow(GralPos, String, String)}
   *   but maybe concurrently used for more as one GralFileSelector. The Key <F9> shows file properties and allows rename and change properties.
   *    
   */
  public GralFileSelector(GralPos refPosParent, String posName, int rows, int[] columns
      , boolean bWithFavor
      , GralViewFileContent fileViewer
      , GralFileProperties wdgFileProperties
      )
  { //this.name = name; this.rows = rows; this.columns = columns; this.size = size;
    super(refPosParent, posName, null);
    GralMng panelMng = this.gralMng;
    StringBuilder sLog = new StringBuilder(100);
    sLog.append("new GralFileSelector ");
    GralPos refPos = this._wdgPos.clone().setAsFrame(); //panelMng.getPositionInPanel();
    refPos.toString(sLog);
    panelMng.log.sendMsg(GralMng.LogMsg.ctorWdg, sLog);
    GralWidgetBase_ifc panel = refPos.parent;
    if(bWithFavor) {
      this.indexFavorPaths = new TreeMap<String, FavorPath>();
      
      this.wdgFavorTable = new GralTable<FavorPath>(refPos, "@4..0,0..0=favorTable-" + this.name, 50, new int[] {10, -20});
      this.wdgFavorTable.specifyActionChange(null, this.action.actionFavorTable, null);
      this.wdgFavorTable.setColumnProportional((new int[] { 3, 10}));
      this.wdgFavorTable.setColumnEditable(0, true);
      this.wdgFavorTable.setColumnEditable(1, true);
      GralMenu menuFavor = this.wdgFavorTable.getContextMenu();
      menuFavor.addMenuItem("save", this.action.actionSaveFavors);
      this.wdgFavorTable.setVisible(true);
    } else {
      this.indexFavorPaths = null;
      this.wdgFavorTable = null;
    }
    this.colorBack = GralColor.getColor("wh");
    this.colorBackPending = GralColor.getColor("pma");
    //this.mainCmd = mainCmd;
    
    String sPanel = panel.getName();
    final String posPathDir;
    if(bWithFavor) {
      this.wdgCardSelector = new GralHorizontalSelector<FavorPath>(refPos, "@0+2, 0..-6=tabs-" + this.name, this.action.actionSetFromTabSelection);
      this.widgBtnFavor = new GralButton(refPos, "@0+2, -5..0=btnFavor-" + this.name, "favor", this.action.actionFavorButton);
      this.widgBtnFavor.setSwitchMode(GralColor.getColor("wh"), this.colorMarkFavor);
      posPathDir = "@2+2, 0..0=infoLine-";
    } else {
      this.wdgCardSelector = null;
      this.widgBtnFavor = null;
      posPathDir = "@0+2, 0..0=infoLine-";
    }
    //widgBtnFavor.setVisible(false);
//  
    //Text field for path above list
    this.widgdPathDir = new GralTextField(refPos, posPathDir + this.name, GralTextField.Type.editable);
       // panelMng.addTextField(null, true, null, null);
    this.widgdPathDir.specifyActionChange(null, this.action.actionSetPath, null);
    this.widgdPathDir.setBackColor(panelMng.getColor("pye"), -1);  //color pastel yellow
    GralMenu menuFolder = this.widgdPathDir.getContextMenu();
    menuFolder.addMenuItem("x", "refresh [cR]", this.action.actionRefreshFileTable);
    //panelMng.setPosition(GralPos.same, GralPos.same, GralPos.next+0.5f, GralPos.size+5.5f, 1, 'd');
    //the list
    //on same position as favor table: the file list.
    //panelMng.setPosition(posAll, GralPos.refer+2, GralPos.same, GralPos.same, GralPos.same, 1, 'd');
    this.wdgSelectList = new FileSelectList(refPos, this, "@+2..0, 0..0=fileTable-" + this.name, rows, columns);
    if(this.wdgFavorTable !=null) {
      this.wdgSelectList.setVisible(false);
    }
    this.wdgSelectList.wdgdTable.setVisible(true);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, null, contextMenuTexts.refresh, this.action.actionRefreshFileTable);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, null, contextMenuTexts.refreshCyclicOff, this.action.actionSwitchoffCheckRefresh);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, null, contextMenuTexts.refreshCyclicOn, this.action.actionSwitchonCheckRefresh);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortNameCase, this.action.actionSortFilePerNameCase);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortNameNonCase, this.action.actionSortFilePerNameNonCase);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortExtCase, this.action.actionSortFilePerExtensionCase);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortExtNonCase, this.action.actionSortFilePerExtensionNonCase);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortDateNewest, this.action.actionSortFilePerTimestamp);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortOldest, this.action.actionSortFilePerTimestampOldestFirst);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.showLastModifiedTime, this.action.actionShowLastModifiedTime);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.showLastAccessTime, this.action.actionShowLastAccessTime);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.showCreationTime, this.action.actionShowCreationTime);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sizeLarge, this.action.actionSortFilePerLenghLargestFirst);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.sortSizeSmall, this.action.actionSortFilesPerLenghSmallestFirst);
    this.wdgSelectList.wdgdTable.addContextMenuEntryGthread(1, "sort", contextMenuTexts.deselectRecursFiles, this.action.actionDeselectDirtree);

    //store this in the GralWidgets to get back from widgets later.
    this.widgdPathDir.setData(this);
    this.wdgSelectList.wdgdTable.setData(this);
    this.wdgSelectList.wdgdTable.specifyActionOnLineSelected(this.actionOnFileSelection);
    this.wdgSelectList.wdgdTable.specifyActionOnLineMarked(this.actionOnMarkLine);
    
    //panelMng.setPosition(refPos, GralPos.refer+2, GralPos.same, GralPos.same, 0, 1, 'd');
    //on same position as favor table: the file list.
    //
    //panelMng.setPosition(5, 0, 10, GralPos.size + 40, 1, 'd');
    //questionWindow = GralInfoBox.createTextInfoBox(panelMng, "questionInfoBox", "question");  
    //panelMng.selectPanel(sPanel);  //if finished this panel is selected for like entry.
    
    this.windView = fileViewer;
    this.wdgFileProperties = wdgFileProperties;
    this.windSearch = new WindowConfirmSearch(refPos, "@screen,7+32, 40+40=search-" + name);
  }
  
  
  /**Maybe called after construction, should be called before {@link #setToPanel(GralMngBuild_ifc)}
   * @param name
   */
//  public void setNameWidget(String name){ 
//    //this.name = name;
//    selectList.wdgdTable.name = name;
//  }
  
  
  public void setDateFormat(String sFormat){
    this.dateFormatOlder = new SimpleDateFormat(sFormat);
  }
  

  
  private void createGraphic(GralMng panelMng) {

  }
  
  
  /**Sets the widgets of this instance to a panel.
   * The panel and the position in the panel 
   * should be set before using {@link GralMngBuild_ifc#selectPanel(String)} and 
   * {@link GralMngBuild_ifc#setPositionInPanel(float, float, float, float, char)}.
   * The instance has more as one widget, all widgets are set in the area of the given position.
   * The position area should be a range of at least 3 lines.
   * @param panelMng The panelManager. 
   * @param identArgJbat The name of the table widget. The Text-widget for the path gets the name * "-Path".
   * @param rows Number of rows to show
   * @param columns Array with column width.
   * @param size Presentation size. It is a character 'A'..'E', where 'A' is a small size. The size determines
   *        the font size especially. 
   */
//  public void createImplWidget_Gthread() //GralMngBuild_ifc panelMng)
//  { GralMng panelMng = GralMng.get();
//    //The macro widget consists of more as one widget. Position the inner widgets:
//    Debugutil.stop();
//  
//  }
  
  
  
  /**New, not tested, instead {@link GralFileSelectWindow} ???
   * @param posName
   * @param mng
   * @return
   */
  public static GralFileSelector createGralFileSelectorWindow(String posName, GralMng mng) {
    GralPos pos = null;
    try {
      pos = new GralPos(mng, "@screen,0..0,0..0");
    } catch(ParseException exc) { ExcUtil.throwUnexpect(exc);}
    GralViewFileContent fileViewer = new GralViewFileContent(pos, "@50..100, 50..100=fileViewer" + ".view");
    GralFileProperties wdgFileProperties = GralFileProperties.createWindow(pos, "fileProperties", "file properties");
    new GralFileSelectWindow(posName, mng);
    int windProps = GralWindow_ifc.windResizeable;
    GralWindow wind = new GralWindow(pos, posName, null, windProps);
    GralFileSelector thiz = new GralFileSelector(pos, "FileSelector", 10, null, false, fileViewer, wdgFileProperties);
    return thiz;
  }
  
  
  
  
  
  /**Creates the window to confirm search in files. This window can be created only one time
   * for all file panels, if the application has more as one. On activating the directory
   * and the file panel to show results should be given. But only one search process can be run
   * simultaneously.
   * @return The created window.
   */
//  public static WindowConfirmSearch createWindowConfirmSearchGthread(GralMngBuild_ifc mng){
//    WindowConfirmSearch wind = new WindowConfirmSearch();
//    mng.selectPanel("primaryWindow");
//    mng.setPosition(-24, 0, -67, 0, 'r'); //right buttom, about half less display width and hight.
//    wind.windConfirmSearch = mng.createWindow("windConfirmSearch", "search in file tree", GralWindow.windConcurrently);
//    mng.setPosition(4, GralPos.size -3.5f, 1, -1, 'd', 0.5f);
//    wind.widgPath = mng.addTextField("path", true, "path", "t");
//    wind.widgMask = mng.addTextField("mask", true, "search name/mask:", "t");
//    wind.widgText = mng.addTextField("containsText", true, "contains text:", "t");
//    
//    mng.setPosition(-5, GralPos.size - 1, 1, -1, 'r',2);
//    wind.widgProgression = mng.addValueBar(null, null);
//    mng.setPosition(-1, GralPos.size - 3, 1, GralPos.size + 8, 'r',2);
//    mng.addButton(null, wind.actionFileSearch, "esc", null, "esc");
//    wind.widgSubdirs = mng.addSwitchButton(null, null, "subdirs", null, "subdirs", GralColor.getColor("wh"), GralColor.getColor("gn"));
//    wind.widgSearch = mng.addButton(null, wind.actionFileSearch, "search", null, "search");
//    wind.widgSearch.setPrimaryWidgetOfPanel();
//    return wind;
//  }
  
  /**Sets an action which is called any time when another line is selected.
   * @param actionOnLineSelected The action, null to switch off this functionality.
   */
  public void specifyActionOnFileSelected(GralUserAction actionOnLineSelected){
    this.actionOnFileSelected = actionOnLineSelected;
  }
  


  public String getCurrentDirPath(){ return this.sCurrentDir; }
  
  public void setOriginDir(FileRemote dir){ this.originDir = dir; }

  
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
  { GralUserAction oldAction = this.actionOnEnterFile;
    this.actionOnEnterFile = newAction;
    return oldAction;
  }
  
  
  
  /**Sets the action which is called if any file is entered. It means the Enter-Key is pressed or
   * a mouse double-click is done on a file.
   * @param newAction The action to use. The action is invoked with TODO
   * @return The current assigned action or null.
   */
  public void setActionOnFocusedFileTable(GralUserAction newAction) { 
    //GralUserAction oldAction = this.wdgSelectList.ac;
    this.wdgSelectList.wdgdTable.specifyActionChange("focused", newAction, null, GralWidget_ifc.ActionChangeWhen.onFocusGained);
  }
  
  
  
  /**This action will be called on pressing enter or mouse-click on a directory.
   * Usual the directory can be entered and showed. But the user can do any other action.
   * If this action returns false, the default behavior: enter the directory will be done.
   */
  public GralUserAction setActionOnEnterDirectory(GralUserAction newAction)
  { GralUserAction oldAction = this.actionOnEnterDirectory;
    this.actionOnEnterDirectory = newAction;
    return oldAction;
  }
  
  
  /**This action will be called on context menu for the Favor tab to save its content.
   * It should be implemented at user level, for example save in a file.
   * It is the opposite to {@link #add(FavorPath)} to fill the favor table. 
   */
  public GralUserAction setActionSaveFavors(GralUserAction newAction)
  { GralUserAction oldAction = this.actionUserSaveFavors;
    this.actionUserSaveFavors = newAction;
    return oldAction;
  }
  

  
  
  /**This action will be called on pressing enter or mouse-click on the path text field
   * if it contains any text which can't assigned to an existing file.
   * 
   */
  public GralUserAction setActionOnEnterPathNewFile(GralUserAction newAction)
  { GralUserAction oldAction = this.actionOnEnterPathNewFile;
    this.actionOnEnterPathNewFile = newAction;
    return oldAction;
  }
  

  
  
  /**Sets the action which is called if any file is set to the table. 
   * @param newAction The action to use. The action is invoked with TODO
   * @return The current assigned action or null.
   */
  public GralUserAction setActionSetFileLineAttrib(GralUserAction newAction)
  { GralUserAction oldAction = this.actionSetFileAttribs;
    this.actionSetFileAttribs = newAction;
    return oldAction;
  }
  
  
  public void clear()
  {
    this.indexFavorPaths.clear();
    this.wdgFavorTable.clearTable();
  }
  
  
  /**Adds a line to this table.
   * @param ix Show which index is used for a local table, 0..2 for left, mid, right,
   *   than show the label in the left cell (column)
   * @param favorPathInfo The favorite info
   */
  public GralTableLine_ifc<FavorPath> add(FavorPath favorPathInfo)
  {
    if(this.indexFavorPaths.get(favorPathInfo.selectName) == null){
      this.indexFavorPaths.put(favorPathInfo.selectName, favorPathInfo);
      GralTableLine_ifc<FavorPath> line = this.wdgFavorTable.addLine(favorPathInfo.selectName, null, favorPathInfo);
      line.setCellText(favorPathInfo.selectName, 0);
      line.setCellText(favorPathInfo.path, 1);
      line.redraw(100,100);
      return line;
    }
    else return null;
  }

  
  
  public GralTableLine_ifc<FavorPath> add(String name, String path) {
    //FileRemote fpath = FileRemote.fromFile(new File(path));
    GralFileSelector.FavorPath favor = new GralFileSelector.FavorPath(name, path, FileRemote.clusterOfApplication);
    return add(favor);
  }

  
  /**Add all favor paths from the SelectTab newly
   * @param favorTabInfo
   */
  public void fillFavorPaths(List<FavorPath> listfavorPaths)
  {
    clear();
    int lineCt =0;
    StringBuilder sLog = new StringBuilder();
    sLog.append("GralFileSelector - fillFavorPaths: ").append(this.wdgFavorTable.name);
    if(this.wdgFavorTable !=null) {
      GralTableLine_ifc<FavorPath> currentLine = null;
      for( FavorPath favorPathInfo: listfavorPaths){
        GralTableLine_ifc<FavorPath> line = add(favorPathInfo);
        if(currentLine == null){ currentLine = line; }  //first line
        if(favorPathInfo.selectName.equals(this.sCurrFavor)){
          currentLine = line;  //or the last selected one.
        }
        lineCt +=1;
      }
      this.wdgFavorTable.setCurrentLine(currentLine, 3, 1);
      sLog.append( " lines: ").append(Integer.toString(lineCt)).append(" follows: doActivateFavor()");
      this.gralMng.log.sendMsg(GralMng.LogMsg.ctorWdg, sLog);
      doActivateFavor();
      //this.wdgFavorTable.bPrepareVisibleArea = true;
      this.wdgFavorTable.redraw1(0, 0);
    }

  }
  

  
  
  /**Fills the content with the first directory or the directory which was set with 
   * {@link #setOriginDir(File)}.
   */
  public void fillInOriginDir()
  {
    fillIn(this.originDir, false);
  }
  
  
  
  /**It is the refresh operation.
   * If {@link #fillinPending} is set yet this operation does nothing. It means it is possible to call in a short cycle
   * more as one time after another, for example for all files of a directory without calculation effort.
   */
  public void fillInCurrentDir(){
    if(this.currentDir !=null && !this.wdgSelectList.wdgdTable.fillinPending()) {
      //assume that a yet tested directory should not refreshed twice because another thread had refreshed already.
      //yet is 2 seconds.
      boolean bDonotRefresh = this.currentDir.isTested(System.currentTimeMillis() - 2000);
      fillIn(this.currentDir, bDonotRefresh);
    }
  }
  


  private void fillIn(List<File> files) {
  
    this.wdgSelectList.wdgdTable.clearTable();
    this.idxLines.clear();
    for(File file1: files ){
      FileCluster fc = this.currentDir.itsCluster;
      FileRemote file = fc.getFile(FileSystem.normalizePath(file1.getAbsolutePath()), null);
      String name = file.getName();
      String path = file.getCanonicalPath();
      GralTableLine_ifc<FileRemote> tline = this.wdgSelectList.wdgdTable.insertLine(path, 0, null, file);
      tline.setCellText("?", kColDesignation);
      tline.setCellText(path, kColFilename);
      tline.setCellText(""+file.length(), kColLength);
      long timeNow = System.currentTimeMillis();
      long fileTime;
      switch(this.showTime){
        case 'm': fileTime = file.lastModified(); break;
        case 'c': fileTime = file.creationTime(); break;
        case 'a': fileTime = file.lastAccessTime(); break;
        default: fileTime = -1; //error
      }
      
      long diffTime = timeNow - fileTime;
      Date timestamp = new Date(fileTime);
      String sDate;
      if(diffTime < -10 * 3600000L){
        sDate = this.sDatePrefixNewer + this.dateFormatNewer.format(timestamp);
      } else if(diffTime < 18*3600000){
        //files today
        sDate = this.sDatePrefixToday + this.dateFormatToday.format(timestamp);
      } else if(diffTime < 320 * 24* 3600000L){
        sDate = this.sDatePrefixYear + this.dateFormatYear.format(timestamp);
      } else {
        sDate = this.sDatePrefixOlder + this.dateFormatOlder.format(timestamp);
      }
      tline.setCellText(sDate, kColDate);
      tline.setBackColor(this.colorBack, -1);
      this.idxLines.put(path, tline);
    }
  }




  public void forcefillIn(FileRemote fileIn, boolean bCompleteWithFileInfo) //String path)
  {
    this.wdgSelectList.wdgdTable.fillinPending(false);
    fillIn(fileIn, bCompleteWithFileInfo);
  }
  
  /**Fills the content with given directory.
   * If the same directory was refreshed in a short time before, it is not refreshed here.
   * That is while fast navigation in a tree. 
   * @param fileIn The directory which's files are shown or a file in this directory.
   *   If it is a file this line is marked.
   * @param bDonotRefrehs false then invoke an extra thread to walk through the file system, 
   *   see @{@link FileRemote#refreshPropertiesAndChildren(FileRemoteCallback)} and {@link #callbackChildren1}.
   *   If true then it is presumed that the FileRemote children are refreshed in the last time already.
   *   The fill the table newly with given content in this thread.
   */
  public void fillIn(FileRemote fileIn, boolean bDonotRefrehs) //String path)
  { long timenow = System.currentTimeMillis();
    this.timeFillinInvoked = timenow;
    final FileRemote dir, file;
    if(!fileIn.isDirectory()){
      dir = fileIn.getParentFile(); file = fileIn;
      String sDir = FileSystem.getCanonicalPath(dir); //with / as separator!
      String sFile = fileIn.getName();
      this.idxSelectFileInDir.put(sDir, fileIn); //sFile);
    } else {
      dir = fileIn; file = null;
    }
    if(this.originDir == null){ 
      this.originDir = dir;    //should exist in any case.
    }
    fileIn.internalAccess().setRefreshed();
    boolean bSameDirectory = dir == this.currentDir;
    if(!bSameDirectory){
      this.gralMng.log.sendMsg(GralMng.LogMsg.gralFileSelector_fillin, "fillin GralFileSelector: " + dir );
      this.currentDir = dir;
      this.sCurrentDir = dir.getAbsolutePath();  //though it may not exist, store it for refresh (may be exist later).
    } else {
      this.gralMng.log.sendMsg(GralMng.LogMsg.gralFileSelector_fillin, "fillin sGralFileSelector: " + dir );
    }
    this.widgdPathDir.setText(this.sCurrentDir, this.sCurrentDir.length());    // set directory string, caret on end
    if(!bSameDirectory || !this.wdgSelectList.wdgdTable.fillinPending()){      //new request anytime if other directory, or if it is not pending.
      this.wdgSelectList.wdgdTable.fillinPending(true);
      System.out.println("FcmdFileCard - start fillin; " + this.sCurrentDir + (bSameDirectory ? "; same" : "; new"));
      final ERefresh eRefresh;
      if(bSameDirectory){
        //it is a refresh.
        if(true || (timenow - dir.timeChildren) > 4000){
          eRefresh = ERefresh.refreshAll; //needs to refresh
        } else {
          eRefresh = ERefresh.doNothing;  //do nothing, it is actual
        }
      } else {
        //other directory
        this.currentFile = null;
              //the directory is unknown yet.
          //GralTableLine_ifc<FileRemote> tline = selectList.wdgdTable.insertLine(null, 0, null, null);
          //tline.setCellText("--waiting--", kColFilename);
          eRefresh = ERefresh.refreshChildren;  //do nothing, it is shown and refreshed in execFillIn
      }
      if(!bSameDirectory || this.sortOrder != this.sortOrderLast){
        this.wdgSelectList.wdgdTable.clearTable();
        this.idxLines.clear();
        if(dir.getParentFile() !=null){
          GralTableLine_ifc<FileRemote> tline = this.wdgSelectList.wdgdTable.insertLine("..", 0, null, dir);
          tline.setCellText("<", kColDesignation);
          tline.setCellText("..", kColFilename);
          tline.setCellText("", kColLength);
          tline.setCellText("", kColDate);
          //tline.setBackColor(this.colorBackPending, -1);
          this.idxLines.put("..", tline);
        }
        //Build the table lines newly.
      } else {
        for(GralTable<?>.TableLineData line: this.wdgSelectList.wdgdTable.iterLines()){
          //if(!line.getCellText(kColFilename).equals("..")){
            line.setBackColor(this.colorBackPending, -1);
          //}
        }
      }
      this.sortOrderLast = this.sortOrder;
      ////
      if(bDonotRefrehs) {
        //do not refresh, show given files.
        Map<String, FileRemote> files = dir.children();
        if(files !=null) {
          for(Map.Entry<String,FileRemote> entry: files.entrySet()) {
            FileRemote file1 = entry.getValue();
            showFile(file1);
          }
        }
        finishShowFileTable();   //removed lines with not existing files, 
        ////
      } else {
        //refresh it in an extra thread therefore show all lines with colorBackPending. 
        //Remove lines which remains the colorBackPending after refreshing.
        dir.refreshPropertiesAndChildren(this.action.callbackChildren1, false);
      }
    }
    if(file !=null) {
      selectFile(file.getName());
    }
    this.wdgSelectList.wdgdTable.redraw(500,500);          // use a long redraw with purple color because meanwhile some files may be filled.
  }
  
  
  
  /**This routine is invoked in callback of {@link #callbackChildren1} for {@link #fillIn(FileRemote, boolean)} in the refresh thread.
   * @param file1
   */
  void showFile(FileRemote file1)
  {
    String key = buildKey(file1, true, null);
    boolean[] found = new boolean[1];
    GralTableLine_ifc<FileRemote> tline = this.idxLines.search(key, false, found);
    
    if(!found[0]){ //no such line with this file
      String name = file1.getName();  //use the file name as key in the table for the table line.
      if(tline ==null){
        //on empty table, first line.
        tline = this.wdgSelectList.wdgdTable.insertLine(name, 0, null, file1);
      }else {
        //insert after found line.
        tline = tline.addNextLine(name, null, file1);
      }
      tline.setCellText(file1.getName(), kColFilename);
      this.idxLines.add(key, tline);
    }
    completeLine(tline, file1, System.currentTimeMillis());
    tline.setBackColor(this.colorBack, -1); //set for the whole line.
  }
  
  
  
  /**Finishes a newly showed file table.
   * Removes all lines which have the {@link #colorBackPending} yet, they are not refreshed because that files don't exist furthermore.
   * Gets the {@link #currentFile()} of this table from the {@link #idxSelectFileInDir} if the {@link #currentFile} is null,
   * sets the current line and repaint the table.
   */
  void finishShowFileTable()
  {
    this.gralMng.log.sendMsg(GralMng.LogMsg.gralFileSelector_fillinFinished, "GralFileSelector - finish fillin; " + this.sCurrentDir);
    Iterator<Map.Entry<String, GralTableLine_ifc<FileRemote>>> iter = this.idxLines.entrySet().iterator();
    while(iter.hasNext()){
      Map.Entry<String, GralTableLine_ifc<FileRemote>> entry = iter.next();
      GralTableLine_ifc<FileRemote> tline = entry.getValue();
      if(tline.getKey().equals("..")){
        tline.setBackColor(this.colorBack, -1); //set for the whole line.
      } else if(tline.getBackColor(-1) == this.colorBackPending){
        this.wdgSelectList.wdgdTable.deleteLine(tline);  //it is a non existing one yet.
        iter.remove();
      }
    }
    this.wdgSelectList.wdgdTable.setBackColor(this.colorBack, GralTable_ifc.kEmptyArea);
    if(this.currentFile == null){
      this.currentFile = this.idxSelectFileInDir.get(this.sCurrentDir);
    }
    GralTableLine_ifc<FileRemote> tline;
    if(this.currentFile !=null){
      String key = buildKey(this.currentFile, true, null);
      tline = this.idxLines.search(key); //maybe line before
    } else {
      tline = this.wdgSelectList.wdgdTable.getFirstLine();  //first line is selected
    }
    if(tline !=null){
      this.wdgSelectList.wdgdTable.setCurrentLine(tline, -3, 1);  
      this.currentFile = tline.getUserData();  //adjust the file if the currentFile was not found exactly.
    }
    this.wdgSelectList.wdgdTable.redraw(100, 200);
    this.wdgSelectList.wdgdTable.fillinPending(false);
    if(this.actionOnFileSelected !=null) {
      this.actionOnFileSelected.exec(KeyCode.activated, tline, tline);
    }
  }
  
  

  
  @SuppressWarnings("boxing")
  private void completeLine(GralTableLine_ifc<FileRemote> tline, FileRemote file, long timeNow){
    final String sDesign, sDir;
    int mark = file.mark ==null ? 0 : file.mark.getMark();
    if(file.isSymbolicLink()){ 
      sDir =  file.isDirectory() ? ">" : "s"; 
    }
    else if(file.isDirectory()){
      sDir = "/";
    } else {
      sDir = "";
    }
    if(mark != 0){
      if((mark & FileMark.cmpFileDifferences ) !=0){ sDesign = "#"; }
      else if((mark & FileMark.cmpMissingFiles ) !=0){ sDesign = "*"; }  //directory contains more files
      else if((mark & FileMark.cmpAlone ) !=0){ sDesign = "+"; }
      else if((mark & FileMark.mCmpFile) !=0){
        switch(mark & FileMark.mCmpFile){
          case FileMark.cmpContentEqual: sDesign = " ";break;
          case FileMark.cmpContentNotEqual: sDesign = "#";break;
          default: sDesign = " ";
        }
      } else {
        sDesign = " ";
      }
    }
    else { sDesign = " ";}
    tline.setCellText(sDir + sDesign, kColDesignation);
    long fileTime;
    switch(this.showTime){
      case 'm': fileTime = file.lastModified(); break;
      case 'c': fileTime = file.creationTime(); break;
      case 'a': fileTime = file.lastAccessTime(); break;
      default: fileTime = -1; //error
    }
    
    long diffTime = timeNow - fileTime;
    Date timestamp = new Date(fileTime);
    String sDate;
    if(diffTime < -10 * 3600000L){
      sDate = this.sDatePrefixNewer + this.dateFormatNewer.format(timestamp);
    } else if(diffTime < 18*3600000){
      //files today
      sDate = this.sDatePrefixToday + this.dateFormatToday.format(timestamp);
    } else if(diffTime < 320 * 24* 3600000L){
      sDate = this.sDatePrefixYear + this.dateFormatYear.format(timestamp);
    } else {
      sDate = this.sDatePrefixOlder + this.dateFormatOlder.format(timestamp);
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
  
  
  ////
  protected String buildKey(FileRemote file, boolean bAllCompleteWithFileInfo, String[] retSortName){
    String sort, sortName;
    if(!file.isTested()){
      bAllCompleteWithFileInfo = false;
    }
    switch(this.sortOrder){
    case kSortName: {
      String sName = file.getName();
      if(file.isDirectory()){ sName += "/"; }
      sortName = sort = (file.isDirectory()? "D" : "F") + sName;
    } break;
    case kSortNameNonCase: {
      String sName = file.getName().toLowerCase();
      if(file.isDirectory()){ sName += "/"; }
      sortName = sort = (file.isDirectory()? "D" : "F") + sName;
    } break;
    case kSortExtension: {
      String sName = file.getName();
      int posDot = sName.lastIndexOf('.');
      String sExt = sName.substring(posDot+1);
      if(file.isDirectory()){ sName += "/"; }
      sortName = sort = (file.isDirectory()? "D" : "F") + sExt + sName;
    } break;
    case kSortExtensionNonCase: {
      String sName = file.getName().toLowerCase();
      int posDot = sName.lastIndexOf('.');
      String sExt = sName.substring(posDot+1);
      if(file.isDirectory()){ sName += "/"; }
      sortName = sort = (file.isDirectory()? "D" : "F") + sExt + sName;
    } break;
    case kSortDateNewest: {
      String sName = file.getName().toLowerCase();
      if(bAllCompleteWithFileInfo){
        long nDate = -file.lastModified();
        String sDate = String.format("%016X", new Long(nDate));
        sort = (file.isDirectory()? "D" : "F") + sDate + sName;
      } else { sort = ""; }
      sortName = (file.isDirectory()? "D" : "F") + sName;
    } break;
    case kSortDateOldest: {
      String sName = file.getName().toLowerCase();
      if(bAllCompleteWithFileInfo){
        long nDate = file.lastModified();
        String sDate = String.format("%016X", new Long(nDate));
        sort = (file.isDirectory()? "D" : "F") + sDate + sName;
      } else { sort = ""; }
      sortName = (file.isDirectory()? "D" : "F") + sName;
    } break;
    case kSortSizeLargest: {
      String sName = file.getName().toLowerCase();
      if(bAllCompleteWithFileInfo){
        long nSize = 0x7fffffffffffffffL - file.length();
        String sSize = String.format("%016d", new Long(nSize));
        sort = (file.isDirectory()? "D" : "F") + sSize + sName;
      } else { sort = ""; }
      sortName = (file.isDirectory()? "D" : "F") + sName;
    } break;
    case kSortSizeSmallest: {
      String sName = file.getName().toLowerCase();
      if(bAllCompleteWithFileInfo){
        long nSize = file.length();
        String sSize = String.format("%016d", new Long(nSize));
        sort = (file.isDirectory()? "D" : "F") + sSize + sName;
      } else { sort = ""; }
      sortName = (file.isDirectory()? "D" : "F") + sName;
    } break;
    default: { sortName = sort = file.getName(); }
    }
    if(retSortName !=null){ retSortName[0] = sortName; }
    return sort;
  }
  
  
  
  
  public void checkRefresh(long since){
    if(this.currentDir !=null 
      && (  !this.donotCheckRefresh && !this.currentDir.isTested(since - 5000)
         || this.currentDir.shouldRefresh()
       )   ){
      fillIn(this.currentDir, false);
    }
  }
  
  
  
  public String getLabelCurrFavor ( ) { return this.sCurrFavor; }
  
  //public FileRemote getCurrentDir(){ return currentDir; }

  
  /**Gets the selected file from this panel.
   * @return null if no line is selected, for example if the panel isn't used yet.
   */
  public File XXXgetSelectedFile()
  {
    if(this.wdgSelectList.wdgdTable == null){
      stop();
      return null;
    }
    GralTableLine_ifc<FileRemote> line = this.wdgSelectList.wdgdTable.getCurrentLine();
    if(line !=null){
      File data = line.getUserData();
      return data;
    } else {
      return null;
    }
  }
  


  
  /**Gets the selected file from this panel.
   * If the current file of this panel is marked, then all other marked files and directories
   * of this panel are returned too. If the current file is not marked, then only this current file
   * is returned as selected file.
   * <br><br>
   * Strategy changed since 2013-09-05: Before, the marked files are returned. But it is possible
   * that some files are marked outside of the visible area, and the user does not know that there are
   * marked files.
   * In this case an unexpected behavior from the user's view occurs. 
   * If the user selects a marked file, then one should be sure that there are other marked files 
   * in non visible areas too.
   * 
   * @param bAlsoDirs false then returns never a directory. If the current selected file is a directory
   *   then return null. 
   * @return null if no line is selected, for example if the panel isn't used yet. 
   *   If the current file is non-marked, then returns a list of only 1 element, that current file.
   *   If the current file is marked, return all marked files. 
   */
  public List<FileRemote> getSelectedFiles(boolean bAlsoDirs, int mask)
  { if(this.wdgSelectList.wdgdTable == null){
      stop();
      return null;
    } else if(this.currentFile == null){
      return null;
    } else if(this.currentFile.isMarked(0x1)){
      List<FileRemote> list = new LinkedList<FileRemote>();
      for(GralTableLine_ifc<FileRemote> line: this.wdgSelectList.wdgdTable.getMarkedLines(mask)){
        FileRemote file = line.getUserData();
        if(bAlsoDirs || !file.isDirectory()){
          list.add(file);
        }
      }
      return list;
    } else if(bAlsoDirs || !this.currentFile.isDirectory()) {
      List<FileRemote> list = new LinkedList<FileRemote>();
      list.add(this.currentFile);
      return list;
    } else { //no mark file selected, current file is a directory.
      return null;
    }
  }
  
  
  /**Selects the file with the given name in the table
   * @param name name of file like it is shown in the table (given as key).
   * @return true if found and selected.
   */
  public boolean selectFile(String name){
    return this.wdgSelectList.wdgdTable.setCurrentLine(name);
    
  }
  
  
  /**Gets the current selected file. 
   * Note: If the .. is selected, the current file is the parent directory.
   * If any directory is selected, this is the currentFile(). Check with {@link java.io.File#isDirectory()}.
   */
  public FileRemote currentFile(){ return this.currentFile; }
  
  /**Gets the directory which is currently shown.
   * Note: If the .. is selected, the current directory is the directory where the .. is located,
   * whereby the {@link #currentFile()} is the parent. Elsewhere this method returns the parent of
   * {@link #currentFile()}.
   */
  public FileRemote currentDir(){ return this.currentDir; }
  
  /**same as {@link #currentDir()}. */
  public FileRemote getCurrentDir(){ return this.currentDir; }
  
  
  
  /**Sets the focus of the associated table widget.
   * @return true if focused.
   */
  public void setFocus(){ 
    if(this.widgBtnFavor.isOn()) {
      this.wdgFavorTable.setFocus();
    } else {
      this.wdgSelectList.wdgdTable.setFocus(); 
      //TODO focus maybe also on widgBtnFavor
    }
  }
  
  
  
  @Override public void setFocus ( int delay, int latest){ 
    if(this.widgBtnFavor.isOn()) {
      this.wdgFavorTable.setFocus(delay, latest);
    } else {
      this.wdgSelectList.wdgdTable.setFocus(delay, latest); 
      //TODO focus maybe also on widgBtnFavor
    }
  }
  
  @Override public boolean isInFocus(){ return this.wdgSelectList.wdgdTable.isInFocus(); }
  
  @Override public boolean isVisible(){ return this.wdgSelectList.wdgdTable.isVisible(); }
  
  
  @Override public boolean setVisible(boolean visible)
  {
    this.wdgSelectList.wdgdTable.setVisible(visible);
    this.widgdPathDir.setVisible(visible);
    this.widgBtnFavor.setVisible(visible);
    return this.wdgSelectList.wdgdTable.isVisible(); 
  }
  
  void stop(){}

  @Override public boolean remove(){ 
    this.wdgSelectList.remove();
    this.widgdPathDir.remove();
    this.idxSelectFileInDir.clear();
    this.currentDir = null;
    return true;
  }
  

  
  @Override public void setFocusedWidget ( GralWidgetBase_ifc widg) {
    if(widg == this.wdgFavorTable) {
      this.widgBtnFavor.setState(GralButton.State.On);
    } else {
      this.widgBtnFavor.setState(GralButton.State.Off);
      //TODO focus maybe also on widgBtnFavor
    }
  }

  @Override public GralWidgetBase_ifc getFocusedWidget() { 
    if(this.widgBtnFavor.getState() == GralButton.State.On) {
      return this.wdgFavorTable;
    } else {
      return this.wdgSelectList;
      //TODO focus maybe also on widgBtnFavor
    }
  }

  
  /**Called from click on line in favor table to select this favor
   * and show the adequate file table.
   */
  public void doActionSelectFileTableFromFavor ( ) {
    int nColumnFocus = this.wdgFavorTable.getColumnInFocus();
    GralTable<FavorPath>.TableLineData favorLine = this.wdgFavorTable.getCurrentLine();
    FavorPath favor = favorLine.getData();
    String sFavorDir = favorLine.getCellText(1);
    if(!sFavorDir.equals(favor.path)) {
      favor.path = sFavorDir;
      favor.sCurrDir = sFavorDir;
    }
    if(nColumnFocus == 1) {
      favor.sCurrDir = "";
    }
    selectFileTableFromFavor(favor);
  }

  
  /**Called either from tab selection or from a line in the favor table ({@link #doActionSelectFileTableFromFavor()}.
   * @param favor The favor of the tab or line.
   */
  protected void selectFileTableFromFavor ( FavorPath favor ) {
    this.widgBtnFavor.setState(GralButton.State.Off);
    this.wdgFavorTable.setVisible(false);
    String sFavor = favor.selectName; // favorLine.getCellText(0);
    //if(sFavor != this.sCurrFavor) {                        // only fillin newly if it is another favor
      this.gralMng.log().sendMsg(GralMng.LogMsg.gralFileSelector_selectFavor, "select from favor" + sFavor);
      this.sCurrFavor = sFavor;
      this.favorPathInfo = favor;
      if(favor.sCurrDir == null || favor.sCurrDir.length()==0) {
        favor.sCurrDir = favor.path;                      // use an existing favor
      }
      if( ! this.wdgCardSelector.setActItem(sFavor)){
        this.wdgCardSelector.addItem(sFavor, -1, this.favorPathInfo);
      }
      this.wdgCardSelector.redraw();
      FileRemote fStart = favor.fileCluster.getDir(favor.sCurrDir);
      fillIn(fStart, false);       //bDoNotRefresh = false, refresh it in another thread
      //File favorfile = new File(favor.sCurrFile);
      //fillIn(FileRemote.fromFile(favorfile), false);
//    }
    this.wdgSelectList.wdgdTable.setVisible(true);
    this.wdgSelectList.wdgdTable.setFocus();
  }
  
  

  /**Activates the favor card.
   * If a file on file card was selected before, its path is stored in the favor table as current one
   * calling {@link #updateFavorWithCurrentDir()}
   */
  public void doActivateFavor ( ) {
    this.widgBtnFavor.setState(GralButton.State.On);
    this.wdgSelectList.wdgdTable.setVisible(false);
    if(this.currentFile !=null) {
      updateFavorWithCurrentDir();               // store the current dir of the sCurrFavor before change favor.
    }
    //this.wdgFavorTable.redraw1(100, 100);
    //this.wdgFavorTable.setVisible(true);
    for(Map.Entry<String, FavorPath> eFavor : indexFavorPaths.entrySet()) {
      FavorPath favor = eFavor.getValue();
      GralTable<FavorPath>.TableLineData favorLine = this.wdgFavorTable.getLine(favor.selectName);
      if(favorLine !=null) {                     // mark the line in left column with green if it is an active favor.
        if(favor.sCurrDir !=null) {
          favorLine.setBackColor(this.colorMarkFavor, 0);
        } else {
          favorLine.cleanSpecialColorsOfLine();
        }
      }
    }
    this.wdgFavorTable.setCurrentColumn(0);
    this.wdgFavorTable.setFocus();               // set the favor table visiable and in focus
  }
  
  
  /**Updates the current favor with the current file table's directory path.
   * This is called both on activate the favor table, and also on activating another favor in the tabs
   * via {@link #wdgCardSelector}.
   */
  protected void updateFavorWithCurrentDir() {
    if(this.sCurrFavor !=null && this.currentDir !=null) {
      GralTable<FavorPath>.TableLineData favorLine = this.wdgFavorTable.getLine(this.sCurrFavor);
      if(favorLine !=null) {
        FavorPath favor = favorLine.getData();
        String sDir = this.currentDir.getAbsolutePath();
        favor.sCurrDir = sDir;
        this.gralMng.log().sendMsg(GralMng.LogMsg.gralFileSelector_updateFavor,"update favor " + favor.selectName + " ->" + sDir);
      }
    }
  }
  
  /**Called if a tab is used to change the selection in the favorTab. 
   * The adequate action is done too with selection in the favor tab. 
   * */
  protected void setFromTabSelection ( GralFileSelector.FavorPath favorPathNew ) {
    //before changing the content of this fileTable, store the current directory
    //to restore if this favor respectively selection is used ones more.
    FileRemote dir = null;
    String sCurrentDir;
//    if(GralFileSelector.this.favorPathInfo !=null){
//      dir = getCurrentDir();
//      if(dir != null){
//        sCurrentDir = dir.getAbsolutePath();
//        if(sCurrentDir !=null){
//          mainPanel.indexActualDir.put(favorPathInfo.selectName, sCurrentDir);
//    } } }
//    //main.favorPathSelector.actFavorPathInfo = favorPathNew; //The last used selection (independent of tab left, middle, right)
//    if(favorPathNew == null){
//      //TODO clear filecard
//      this.wdgFavorCard.sActSelectedFavorPath = "??NO FavorPath.selectName";
//      System.out.println("actionSetFromTabSelection: favorPathNew = null");
//      this.main.favorPathSelector.bSyncMidRight = false;
//    } else {
//      this.wdgFavorCard.sActSelectedFavorPath = favorPathNew.selectName;
//      this.sTabSelection = this.label + "." + favorPathNew.selectName;
//      this.main.favorPathSelector.bSyncMidRight = 
//          this.syncTabSelection !=null 
//          && this.syncPartnerTabSelection !=null 
//          && this.syncTabSelection.equals(this.sTabSelection)
//          && this.mainPanel.partnerPanelToSync() !=null
//          && this.mainPanel.partnerPanelToSync().actFileCard !=null
//          && this.syncPartnerTabSelection.equals(this.mainPanel.partnerPanelToSync().actFileCard.sTabSelection);
//      GralColor syncColor = this.main.favorPathSelector.bSyncMidRight ? GralColor.getColor("gn") : GralColor.getColor("wh");
//      this.main.statusLine.widgSyncInfoLeft.setBackColor(syncColor,0);
//      this.main.statusLine.widgSyncInfoRight.setBackColor(syncColor,0);
//      if(this.mainPanel.cc == 'm') {
//        if(this.syncTabSelection ==null) {
//          this.main.statusLine.widgSyncInfoRight.setText("");
//        } else {
//          this.main.statusLine.widgSyncInfoLeft.setText(this.syncTabSelection);
//          this.main.statusLine.widgSyncInfoRight.setText(this.syncPartnerTabSelection);
//        }
//      }
//      else if(this.mainPanel.cc == 'r') {
//        if(this.syncTabSelection ==null) {
//          this.main.statusLine.widgSyncInfoLeft.setText("");
//        } else {
//          this.main.statusLine.widgSyncInfoLeft.setText(this.syncPartnerTabSelection);
//          this.main.statusLine.widgSyncInfoRight.setText(this.syncTabSelection);
//        }
//      }
//      System.out.println("actionSetFromTabSelection: " + favorPathNew.selectName);
//      sCurrentDir  = mainPanel.indexActualDir.get(favorPathNew.selectName);
//      if(sCurrentDir == null){
//        sCurrentDir = favorPathNew.path;
//      }
//      //dir = new FileRemote(currentDir);  
//      dir = main.fileCluster.getFile(sCurrentDir, null);
//      FcmdFileCard.this.setNewContent(favorPathNew, dir);
//    }
    
  }

  
  /**The operation if the [favor] button was pressed.
   * 
   */
  protected void switchFileFavor ( ) {
    if(! this.widgBtnFavor.isOn()) {
      doActionSelectFileTableFromFavor();
    } else {
      doActivateFavor();
    }
  }
  

  
  /**If given in ctor, opens the file property window and show file properties.
   * @param line The line in fileTable
   */
  protected void showFileInfo(GralTable<FileRemote>.TableLineData line) {
    if(GralFileSelector.this.wdgFileProperties !=null) {
      GralFileSelector.this.wdgFileProperties.setVisible(true);
      GralFileSelector.this.wdgFileProperties.showFileInfos(line.getData());
    }
  }
  
  /**This method is called on any user key or mouse event while operating in the file table.
   * It should be overwritten by a derived class. This routine is empty.
   * @param key code or mouse code, one of constants from {@link KeyCode}.
   * @param userDataOfLine The user data stored in the line of table.
   * @param line The table line.
   * @return true if is was relevant for the key.
   */
  public boolean actionUserKey(int keyCode, Object userDataOfLine, GralTableLine_ifc<FileRemote> line)
  { 
    if(keyCode == KeyCode.alt + KeyCode.dn ) { 
      doActivateFavor();
    }
    else if(keyCode == KeyCode.F3) {
      FileRemote file = line.getUserData();
      this.windView.view(file);
    }
    return false;
  }

  
  /**Creates the implementation, which consists of the widgets:
   * {@link #wdgSelectList}
   * {@link #windSearch}
   *
   */
  @Override public boolean createImplWidget_Gthread() throws IllegalStateException {
    this.windSearch.windConfirmSearch.createImplWidget_Gthread();
    if(this.wdgSelectList.createImplWidget_Gthread()) {
      return true;
    }
    else return false;
  }

  
  /**Removes the implementation widget, maybe to re-create with changed properties
   * or also if the GralWidget itself should be removed.
   * This is a internal operation not intent to use by an application. 
   * It is called from the {@link GralMng#runGraphicThread()} and hence package private.
   */
  @Override public void removeImplWidget_Gthread() {
    this.wdgSelectList.removeImplWidget_Gthread();                     // recursively call of same
    this.wdgCardSelector.removeImplWidget_Gthread();                     // recursively call of same
    this.wdgFavorTable.removeImplWidget_Gthread();                     // recursively call of same
    this.widgBtnFavor.removeImplWidget_Gthread();                     // recursively call of same
    this.widgdPathDir.removeImplWidget_Gthread();                     // recursively call of same
    //TODO this.windSearch.removeImplWidget_Gthread();                     // recursively call of same
    this.windView.removeImplWidget_Gthread();
  }

  
  /**Entry in the favorite list. */
  public static class FavorPath
  { /**The path of directory to select. */
    public String path;
    /**The name shown in the list. */
    public final String selectName;
    
    protected String sCurrDir;
    
    public final FileCluster fileCluster;
    /**The label on the tab in tabbed panel. */
    //String label;
    /**bit 0..2 present this favorite on the designated main panel 1..3 or l, m, r,
     * it means, a tab with the label will be created. */
    public int mMainPanel;

    /**Origin dir adequate {@link #path}. It is null on initialization, but build on call of
     * {@link #getOriginDir()}. */
    private FileRemote dir;
    
    public FavorPath(String selectName, String path, FileCluster fileCluster)
    { this.fileCluster = fileCluster;
      this.path = path;
      this.selectName = selectName;
    }

    
    /**Returns the dir instance for the origin path. The dir instance is built only one time
     * but only if it is necessary. It means it is built on the first call of this method.
     * @return
     */
    public FileRemote getOriginDir(){
      if(this.dir == null){ //build it only one time, but only if it is necessary.
        this.dir = this.fileCluster.getFile(this.path, null);  //new FileRemote(path);
      }
      return this.dir;
    }
    
    
    @Override public String toString(){ return this.path; } //for debug
  }

  
  
  protected class Callbacks { 

    
    protected final GralUserAction actionFavorTable = new GralUserAction("actionTable") {
      @Override public boolean userActionGui(int keyCode, GralWidget widgdTable, Object... params)
      {
        //assert(sIntension.equals("table-key"));
        @SuppressWarnings("unchecked")
        GralTableLine_ifc<FavorPath> line = (GralTableLine_ifc<FavorPath>)params[0];
        Object data = line == null ? null : line.getUserData();
        //int keyCode = (Integer)params[1];
        boolean done = true;
        if(data !=null) {
          //if(keyCode == keyLeft){ actionLeft(data, line); }
          //else if(keyCode == keyRight){ actionRight(data, line); }
          //else 
          if(keyCode == KeyCode.enter){ doActionSelectFileTableFromFavor(); done = true; }
          else if(keyCode == KeyCode.mouse1Double){ doActionSelectFileTableFromFavor(); done = true;  }
          //else { done = actionUserKey(keyCode, data, line); }
        } else {
          done = false;
        }
        return done;
      }
    };

    
  public FileRemoteCallback callbackChildren1 = new FileRemoteCallback()
  {

    @Override public void start(FileRemote startDir){}

    @Override public Result offerParentNode(FileRemote file) {
      return Result.cont; 
    }

    @Override public Result finishedParentNode(FileRemote file, FileRemoteCallback.Counters cnt) {
      //don't call showFile(file) because it is the parent which should be shown.
      return Result.cont; 
    }

    @Override public Result offerLeafNode(FileRemote file, Object info)
    { showFile(file);  //invoked also for dirs because depth=1
      return Result.cont;
    }

    @Override public void finished(FileRemote startDir, SortedTreeWalkerCallback.Counters cnt)
    {
      finishShowFileTable();
    }

    @Override public boolean shouldAborted()
    { return false;
    }
  };
  
  
  
  
  
  
  
  /**Action on [Enter]-key on the path text field.
   * <ul>
   * <li>If the text represents an existing directory, it is used as current.
   * <li>If the text represents an existing file, the parent directory is used as current
   *   and the file is stored as current in this directory, see {@link #idxSelectFileInDir}.
   * <li>If the path is absolute (starting with '/' or '\\' maybe with leading drive letter for windows
   *   then it is used absolute. If the path is not absolute, it is used starting from the current one.
   * <li>If the path contains a name only, it is a file. You can    
   * <li>If the text represents a file which is not existing, but its directory path is existing,
   *   a quest is posted whether the file should be created. On [OK] either the file will be
   *   created as new file or its path is returned.    
   * </ul>
   */
  GralUserAction actionSetPath = new GralUserAction("setPath"){
    @Override
    public boolean exec(int key, GralWidget_ifc wdgi, Object... params)
    {
      if(key == KeyCode.enter){
        GralWidget widg = (GralWidget)wdgi;
        widg.gralMng.log().sendMsg(GralMng.LogMsg.gralFileSelector_setPath, "set Path");
        String sPath = widg.getValue();
        int posWildcard = sPath.indexOf('*');
        if(posWildcard >=0){
          
        } else {
          FileRemote file = GralFileSelector.this.originDir.itsCluster.getDir(sPath);
          //file.refreshProperties(null);
          if(file.isDirectory()){
            fillIn(file, false);
          } else if(file.isFile()){
            FileRemote dir = file.getParentFile();
            String sDir = FileSystem.getCanonicalPath(dir);
            String sFile = file.getName();
            GralFileSelector.this.idxSelectFileInDir.put(sDir, file);
            fillIn(dir, false);
          } else {
            File parent = file.getParentFile();
            if(parent !=null && parent.exists()){
              if(GralFileSelector.this.actionOnEnterPathNewFile !=null){
                GralFileSelector.this.actionOnEnterPathNewFile.userActionGui(KeyCode.enter, GralFileSelector.this.widgdPathDir, file);
              } else {
                String question = "Do you want to create file\n"
                  +file.getName()
                  + "\n  in directory\n"
                  + parent.getPath();
//                questionWindow.setText(question);
//                questionWindow.setActionOk(confirmCreate);
//                questionWindow.setFocus(); //setWindowVisible(true);
              }
            } else {
//              questionWindow.setText("unknown path");
//              questionWindow.setActionOk(null);
//              questionWindow.setFocus(); //setWindowVisible(true);
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

  GralUserAction actionShowLastModifiedTime = new GralUserAction("actionSortFilePerTimestamp")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      GralFileSelector.this.showTime = 'm';
      return true;
  } };

  GralUserAction actionShowLastAccessTime = new GralUserAction("actionSortFilePerTimestamp")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      GralFileSelector.this.showTime = 'a';
      return true;
  } };

  GralUserAction actionShowCreationTime = new GralUserAction("actionSortFilePerTimestamp")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      GralFileSelector.this.showTime = 'c';
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


  
  
  GralUserAction actionShowFileProps = new GralUserAction("actionShowFileProps") { 
    @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
      showFileInfo(null);
      return true;
  } };


  
  
  
  
  
  
  GralUserAction actionDeselectDirtree = new GralUserAction("actionDeselectDirtree")
  { @Override public boolean exec(int key, GralWidget_ifc widgi, Object... params) { 
    //if(fileCard !=null){
    if(GralFileSelector.this.currentFile !=null){
      Thread run1 = new Thread("actionDeselectDirtree") {
        @Override public void run() {
          GralFileSelector.this.currentFile.resetMarkedRecurs(0xffffffff, null);
          GralFileSelector.this.currentFile.setDirShouldRefresh();
      } };
      run1.start();
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
  
  /**Sets the check refresh mode to off.
   * <br>
   */
  GralUserAction actionSwitchoffCheckRefresh = new GralUserAction("actionSwitchoffCheckRefresh"){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        GralFileSelector.this.donotCheckRefresh = true;
        return true;
      } else return false;
    }
  };
  
  /**Sets the check refresh mode to on.
   * <br>
   */
  GralUserAction actionSwitchonCheckRefresh = new GralUserAction("actionSwitchoffCheckRefresh"){
    @Override public boolean userActionGui(int key, GralWidget widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        GralFileSelector.this.donotCheckRefresh = false;
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
  
  
  
  
  GralUserAction actionFavorButton = new GralUserAction("actionFavorButton"){
    @Override public boolean exec(int key, GralWidget_ifc widgd, Object... params){ 
      if(KeyCode.isControlFunctionMouseUpOrMenu(key)){  //supress both mouse up and down reaction
        if(GralFileSelector.this.wdgFavorTable !=null) {
          switchFileFavor();
        }
        return true;
      } else return false;
    }
  };
  
  
  
    /**This action is called if a tab is selected. */
    GralUserAction actionSetFromTabSelection = new GralUserAction("actionSetFromTabSelection"){
      @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
        GralFileSelector.FavorPath favor = (GralFileSelector.FavorPath)params[0];
        if(actionCode == KeyCode.removed) {
          favor.sCurrDir = null;                 // deactivated favor
        } else if(actionCode == KeyCode.activated) {
          updateFavorWithCurrentDir();
          selectFileTableFromFavor(favor);
        } else {
          assert(false);
        }
        return true;      
    } };

  
    
    /**This action is called if a tab is selected. */
    GralUserAction actionSaveFavors = new GralUserAction("actionSaveFavors"){
      @Override public boolean exec(int actionCode, GralWidget_ifc widgd, Object... params) {
        if(GralFileSelector.this.actionUserSaveFavors !=null) {
          GralFileSelector.this.actionUserSaveFavors.exec(KeyCode.activated, GralFileSelector.this.wdgFavorTable);
        }
        return true;      
    } };

  
    


  
  } // class Callbacks
  
  final Callbacks action = new Callbacks();
  
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
    public String refreshCyclicOff = "Cyclic refresh/o&ff";
    public String refreshCyclicOn = "Cyclic refresh/&on";
    public String showLastAccessTime = "show date last &Access";
    public String showLastModifiedTime = "show date last &Modified";
    public String showCreationTime = "show date &Creation";
  }
  
  
}
