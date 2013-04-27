package org.vishia.guiBzr;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.vishia.gral.base.GralTable;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.ifc.GralButtonKeyMenu;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralWidget_ifc;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;
import org.vishia.util.StringFormatter;
import org.vishia.util.FileSystem.FileAndBasePath;

/**The panel which contains a table to select SVM archive locations
 * @author Hartmut Schorrig
 * @since 2013.03-29
 *
 */
public class GuiSelectPanel
{
  
  String sWorkingTreeBaseDir = "D:/Bzr/D";
  
  String sArchiveTreeBaseDir = "D:/Bzr/Archive";
  
  String sRemoteArchiveTreeBaseDir = "A:/Bzr/Archive";
  
  StringFormatter formatter = new StringFormatter();
  
  /**TODO only private local using here, it should be a part of DataProject.
   * 
   */
  //List<File> listBzrDirs = new LinkedList<File>();

  List<FileAndBasePath> listBzrDirs = new ArrayList<FileAndBasePath>();
  
  
  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GralMngBuild_ifc mng;
  
  GralTable tableSelect;
  
  /**Widget to select the project path. 
   * The project is a user-project which contains one or more source archive-sandboxes.
   * 
   * */
  GralWidget widgdProjektpath; 
  
  public GuiSelectPanel(MainData mainData, GralMngBuild_ifc panelBuildifc)
  {
    this.mng = panelBuildifc;
    this.mainData = mainData;
  }
  
  
  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui(){ 
    mng.selectPanel("Select");
    mng.setPosition(0,2,0,30,0,'r',0);
    mng.addText("Component");
    mng.addText("Working Tree");
    mng.addText("Archive Tree");
    mng.addText("Remote Tree");
    mng.setPosition(2,-3,0,0,0,'.',0);
    int[] columns = {30,30,30,30};
    tableSelect = mng.addTable("selectTable", 100, columns);
    tableSelect.specifyActionOnLineSelected(actionOnSelectedLine);
    mng.setPosition(-3,0,0,9,0,'.',1);
    //
    String fnkey = "cleanSelectTable";
    GralButtonKeyMenu fn = mainData.panels.idents.get(fnkey);
    mng.addButton(fnkey+"-button", fn.action, fn.buttontext);
    //
    fnkey = "statusCmpn";
    fn = mainData.panels.idents.get(fnkey);
    mng.addButton(fnkey+"-button", fn.action, fn.buttontext);
    //
    fnkey = "revertAll";
    fn = mainData.panels.idents.get(fnkey);
    mng.addButton(fnkey+"-button", fn.action, fn.buttontext);
  }
  
  
  
  /**Searches all locations of source-archives in the current project folder and all sub folders.
   * A project means a software project with some sources consisting of some Components with its own archives.
   * @calls {@link #captureStatus(DataCmpn)} for all found software-archives.
   * @param sProjectPath Path to the project folder.
   * 
   */
  void getBzrLocations()
  {
    listBzrDirs.clear();
    try{ 
      File baseDirAllCmpn = new File(sWorkingTreeBaseDir);
      FileSystem.addFilesWithBasePath(baseDirAllCmpn, "/**/.bzr", listBzrDirs);
      //FileSystem.addFileToList(sWorkingTreeBaseDir + "/**/.bzr", listBzrDirs);
    } catch(Exception exc){ 
      
    }
    int zCmpn = listBzrDirs.size();
    mainData.currPrj.init(zCmpn);
    //int ixCmpn = 0;
    tableSelect.clearTable();
    File dirBaseArchive = new File(sArchiveTreeBaseDir);
    File dirBaseRemoteArchive = new File(sRemoteArchiveTreeBaseDir);
    if(!dirBaseRemoteArchive.exists()){
      dirBaseRemoteArchive = null;
    }
    for(FileAndBasePath entrylist: listBzrDirs){
      File fileBzr = entrylist.file;
      File dirArchive = new File(dirBaseArchive, entrylist.localPath);
      File dirRemoteArchive = dirBaseRemoteArchive == null ? null : new File(dirBaseRemoteArchive, entrylist.localPath);
      File dirWorkingTree = fileBzr.getParentFile();
      String nameCmpn = dirWorkingTree.getName();
      String[] cellTexts = new String[4];
      cellTexts[0] = entrylist.localPath;
      cellTexts[1] = "?";
      cellTexts[2] = "?";
      cellTexts[3] = "?";
      DataCmpn dataCmpn = new DataCmpn(dirWorkingTree, dirArchive, dirRemoteArchive);
      tableSelect.insertLine(nameCmpn, -1, cellTexts, dataCmpn);
    }
  }

  
  
  
  
  
  
  
  GralUserAction actionRefreshSelectTable = new GralUserAction("actionRefreshSelection"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... oArgs){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        getBzrLocations();
      }
      return true;
    }
  };


  
  /**Gets the status for the Component of the actual line, refresh the line. */
  GralUserAction actionGetStatus = new GralUserAction("actionGetStatus"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... oArgs){
      if(KeyCode.isControlFunctionMouseUpOrMenu(actionCode)){
        if(mainData.currTableline !=null){
          mainData.getterStatus.captureStatusAllArchives(mainData.currCmpn);
          
          formatter.reset();
          String sDate = formatter.convertTimestampToday(mainData.currCmpn.revisionWorkingTreeTop.date);
          formatter.add(sDate).add(": rev=").add(mainData.currCmpn.revisionWorkingTreeTop.nr);
          mainData.currTableline.setCellText(formatter.getContent(), 1);
          
          formatter.reset();
          if(mainData.currCmpn.revisionArchive.nr !=null){
            sDate = formatter.convertTimestampToday(mainData.currCmpn.revisionArchive.date);
            formatter.add(sDate).add(": rev=").add(mainData.currCmpn.revisionArchive.nr);
            mainData.currTableline.setCellText(formatter.getContent(), 2);
          } else {
            mainData.currTableline.setCellText("not available", 2);
          }
          
          if(mainData.currCmpn.dirRemoteArchive !=null){
            formatter.reset();
            sDate = formatter.convertTimestampToday(mainData.currCmpn.revisionRemoteArchive.date);
            formatter.add(sDate).add(": rev=").add(mainData.currCmpn.revisionRemoteArchive.nr);
            mainData.currTableline.setCellText(formatter.getContent(), 3);
          } else {
            mainData.currTableline.setCellText("not available", 3);
          }
        }
        return true;
      } else return false;
    }
  };

  
  
  GralUserAction actionOnSelectedLine = new GralUserAction("actionRefreshSelectCmpn"){
    @Override public boolean exec(int actionCode, GralWidget_ifc widgi, Object... oArgs){
      assert(oArgs.length >=1 && oArgs[0] instanceof GralTableLine_ifc);
      mainData.currTableline = (GralTableLine_ifc)oArgs[0];
      mainData.currCmpn = (DataCmpn)mainData.currTableline.getUserData();
      return true;
    }
  };
  
  
}
