package org.vishia.guiBzr;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.vishia.mainCmd.Report;
import org.vishia.mainGui.ColorGui;
import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.TableLineGui_ifc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

public class GuiFilesDiffPanel
{
  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  final StringBuilder uRenameOut =  new StringBuilder();
  
  /**The table (list) which contains the selectable project paths. */
  private WidgetDescriptor widgdTableFilesCmpn;
  

  private static final int columnMark = 2;
  
  private static final ColorGui colorMarked = new ColorGui(128,255, 128);  //light green
  
  private static final ColorGui colorNonMarked = new ColorGui(255,255, 255);  //white
  
  final Map<String, TableLineGui_ifc> indexMarkedFiles = new TreeMap<String, TableLineGui_ifc>();
  
  
  public GuiFilesDiffPanel(MainData mainData, GuiPanelMngBuildIfc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }

  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { panelBuildifc.selectPanel("FilesDiff");
    panelBuildifc.setPosition(2,0, 30, 60, 'r');
    int[] columnWidths = {40, 10, 2,8};
    
    widgdTableFilesCmpn = panelBuildifc.addTable("selectFile", 20, columnWidths);
    widgdTableFilesCmpn.setAction(actionTableLineFile);
    panelBuildifc.setPosition(2, 61, 3, 9, 'd');
    panelBuildifc.addButton("view", actionView, "","","","&view");
    panelBuildifc.addButton("diff", actionViewdiff, "","","","view &diff");
    //panelBuildifc.addButton("add", actionAdd, "","","","&add");
    panelBuildifc.setPosition(10, 61, 3, 9, 'd');
    panelBuildifc.addButton("rename", actionRename, "","","","&rename");
    panelBuildifc.addButton("commit", actionCommit, "","","","&rename");
    
  }
    

  void fillFileTable(DataCmpn cmpn)
  {
    widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdClear, -1, null);  //clear the whole table
    if(cmpn.listModifiedFiles !=null) for(DataFile file: cmpn.listModifiedFiles){
      //String name = file.getName();
      StringBuilder uLine = new StringBuilder(200); 
      uLine.append(file.sLocalpath).append("\t");
      if(file.dateFile !=0){
        uLine.append(mainData.formatTimestampYesterday(file.dateFile));
      } else {
        uLine.append("unknown"); 
      }
      uLine.append("\t \tchg");
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
    if(cmpn.listAddFiles !=null) for(DataFile file: cmpn.listAddFiles){
      //String name = file.getName();
      StringBuilder uLine = new StringBuilder(200); 
      uLine.append(file.sLocalpath).append("\t");
      if(file.dateFile !=0){
        uLine.append(mainData.formatTimestampYesterday(file.dateFile));
      } else {
        uLine.append("unknown"); 
      }
      uLine.append("\t \tadd");
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
    if(cmpn.listNewFiles !=null) for(DataFile file: cmpn.listNewFiles){
      //String name = file.getName();
      StringBuilder uLine = new StringBuilder(200); 
      uLine.append(file.sLocalpath).append("\t");
      if(file.dateFile !=0){
        uLine.append(mainData.formatTimestampYesterday(file.dateFile));
      } else {
        uLine.append("unknown"); 
      }
      uLine.append("\t \tnew");
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
    if(cmpn.listRenamedFiles !=null) for(DataFile file: cmpn.listRenamedFiles){
      //String name = file.getName();
      StringBuilder uLine = new StringBuilder(200); 
      uLine.append(file.sLocalpath).append("\t");
      if(file.dateFile !=0){
        uLine.append(mainData.formatTimestampYesterday(file.dateFile));
      } else {
        uLine.append("unknown"); 
      }
      uLine.append("\t \tmove");
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
    if(cmpn.listRemovedFiles !=null) for(DataFile file: cmpn.listRemovedFiles){
      //String name = file.getName();
      StringBuilder uLine = new StringBuilder(200); 
      uLine.append(file.sLocalpath).append("\t");
      if(file.dateFile !=0){
        uLine.append(mainData.formatTimestampYesterday(file.dateFile));
      } else {
        uLine.append("unknown"); 
      }
      uLine.append("\t \tdel");
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
  }
  
  
  
  
  private final UserActionGui actionTableLineFile = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
      if(sCmd.equals("mark")){
        TableLineGui_ifc line = (TableLineGui_ifc) values[0];
        String isMarked = line.getCellText(columnMark);
        if(isMarked.equals("*")) {
          line.setCellText("", columnMark);
          line.setBackgroundColor(colorNonMarked);
          String sFile = line.getCellText(0);
          indexMarkedFiles.remove(sFile);
        } else {
          line.setCellText("*", columnMark);
          line.setBackgroundColor(colorMarked);
          String sFile = line.getCellText(0);
          String sType = line.getCellText(3);
          indexMarkedFiles.put(sFile, line);
        } 
      }
      stop();
    }
  };


  private final UserActionGui actionAdd = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
    }
  };
  
  
  private final UserActionGui actionRename = new UserActionGui()
  { 
    public void userActionGui(String sActionCmd, WidgetDescriptor widgetInfos, Object... values)
    { if(sActionCmd.equals("Button-up")){
        String sFileOld =null, sFileNew =null;
        TableLineGui_ifc lineOld = null, lineNew = null;
        for(Map.Entry<String, TableLineGui_ifc> entry: indexMarkedFiles.entrySet()){
          TableLineGui_ifc line = entry.getValue();
          String sType = line.getCellText(3);
          String sFile = entry.getKey();
          if(sType.equals("del")){
            sFileOld = sFile;
            lineOld = line;
          }
          else if(sType.equals("new")){
            sFileNew = sFile;
            lineNew = line;
          }
        }
        
        if(sFileOld !=null && sFileNew !=null){
          //mainData.log 
          lineOld.setCellText("old", 3);
          lineNew.setCellText("ren", 3);
          String sCmd = "bzr mv " + sFileOld + " " + sFileNew;
          mainData.mainCmdifc.executeCmdLine(mainData.cmdMng, sCmd, null, Report.error, uRenameOut, uRenameOut);
          mainData.mainCmdifc.writeInfoln(sCmd + "\n" + uRenameOut);
        }
      }
    }
  };
  
  
  
  private void refreshFiles()
  {
    //gets the status of the components archive in the GUI-action,
    //because the appearance of the GUI should be updated:
    mainData.getterStatus.captureStatus(mainData.currCmpn);
    //
    mainData.getterStatus.initListFiles();
    fillFileTable(mainData.currCmpn);
    mainData.panelAccess.redrawWidget("selectFile");
  }
  
  
  
  
  private final UserActionGui actionCommit = new UserActionGui()
  { 
    public void userActionGui(String sActionCmd, WidgetDescriptor widgetInfos, Object... values)
    { if(sActionCmd.equals("Button-up")){
        
        File fileCommitText = mainData.mainAction.getContentofCommitText();
        if(fileCommitText == null){
          mainData.mainCmdifc.writeError("The commit text is empty. Please write there: Commit-Tab");
        } else {
          String sCmd = "bzr commit -F " + fileCommitText.getAbsolutePath();
          
          StringBuilder uCmdAdd = new StringBuilder("bzr add");
          StringBuilder uCmdCommit = new StringBuilder(1000);
          uCmdCommit.append("bzr commit -F ");
          uCmdCommit.append(fileCommitText.getAbsolutePath());
          boolean bAdd = false;
          boolean bCommitSel = false;
          for(Map.Entry<String, TableLineGui_ifc> entry: indexMarkedFiles.entrySet()){
            TableLineGui_ifc line = entry.getValue();
            String sType = line.getCellText(3);
            String sFile = entry.getKey();
            if(sType.equals("new")){
              bAdd = true;
              uCmdAdd.append(" ").append(sFile);
            }
            else if(sType.equals("chg") || sType.equals("add") || sType.equals("move")){
              bCommitSel = true;
              uCmdCommit.append(" ").append(sFile);
            }
          }
          
          if(bAdd){
            //mainData.log 
            mainData.mainCmdifc.executeCmdLine(mainData.cmdMng, uCmdAdd.toString(), null, Report.error, uRenameOut, uRenameOut);
            mainData.mainCmdifc.writeInfoln(uCmdAdd + "\n" + uRenameOut);
          }
          if(bCommitSel && bAdd){
            uCmdCommit.append(uCmdAdd.subSequence(7, uCmdAdd.length()));
          }
          if(bCommitSel || bAdd){
            uRenameOut.setLength(0);
            mainData.mainCmdifc.executeCmdLine(mainData.cmdMng, uCmdCommit.toString(), null, Report.error, uRenameOut, uRenameOut);
            mainData.mainCmdifc.writeInfoln(uCmdAdd + "\n" + uRenameOut);
          } else {
            mainData.mainCmdifc.writeError("Nothing to commit - Please select files with space-bar");
          }
          //TODO read status
          refreshFiles();
        }
      }
    }
  };
  
  
  
  
  private final UserActionGui actionView = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
    }
  };
  
  
  private final UserActionGui actionViewdiff = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
    }
  };
  
  
  
  void stop(){}
  
  
}
