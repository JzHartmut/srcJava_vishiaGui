package org.vishia.guiBzr;

import java.io.File;

import org.vishia.mainGui.GuiPanelMngBuildIfc;
import org.vishia.mainGui.GuiPanelMngWorkingIfc;
import org.vishia.mainGui.UserActionGui;
import org.vishia.mainGui.WidgetDescriptor;

public class GuiFilesDiffPanel
{
  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  //final StringBuilder uCommitOut =  new StringBuilder();
  
  /**The table (list) which contains the selectable project paths. */
  private WidgetDescriptor widgdTableFilesCmpn;
  

  
  
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
    
    widgdTableFilesCmpn = panelBuildifc.addTable("selectProjectPath", 20, columnWidths, 2, "*");
    widgdTableFilesCmpn.setAction(actionTableLineFile);
    panelBuildifc.setPosition(2, 61, 3, 9, 'd');
    panelBuildifc.addButton("closeProjectBzrComponents", actionTableLineFile, "","","","view");
    panelBuildifc.addButton("closeProjectBzrComponents", actionTableLineFile, "","","","diff");
    panelBuildifc.addButton("closeProjectBzrComponents", actionTableLineFile, "","","","add");
    
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
      widgdTableFilesCmpn.setValue(GuiPanelMngWorkingIfc.cmdInsert, 99999, uLine.toString());
    }
  }
  
  
  
  
  private final UserActionGui actionTableLineFile = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor<?> widgetInfos, Object... values)
    {
      stop();
    }
  };


  
  void stop(){}
  
  
}
