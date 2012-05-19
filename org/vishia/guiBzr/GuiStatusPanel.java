package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.base.GralPanelContent;
import org.vishia.gral.base.GralWidget;
import org.vishia.gral.base.GralWindow;
import org.vishia.gral.ifc.GralMngBuild_ifc;
import org.vishia.gral.ifc.GralMng_ifc;
import org.vishia.gral.ifc.GralWindow_ifc;
import org.vishia.gral.ifc.GralUserAction;
import org.vishia.gral.ifc.GralTableLine_ifc;
import org.vishia.gral.widget.GralSwitchExclusiveButtonMng;
import org.vishia.util.FileSystem;
import org.vishia.util.KeyCode;

/**This class contains all data and methods of the status (select) panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GuiStatusPanel
{
  
  private static class SelectInfoBoxWidgds
  {
    final GralPanelContent box;
    final DataCmpn data;
    GralWidget widgdTextRevision, widgdTextStatus;
    
    SelectInfoBoxWidgds(GralPanelContent box, DataCmpn data)
    { this.box = box; this.data = data; }
    
  }

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GralMngBuild_ifc panelBuildifc;
  
  
  /**Widget to select the project path. 
   * The project is a user-project which contains one or more source archive-sandboxes.
   * 
   * */
  GralWidget widgdProjektpath; 
  
  
  /**A Panel which contains the table to select some projectPaths. */
  private GralWindow selectorProjectPath;
  
  /**The table (list) which contains the selectable project paths. */
  private GralWidget selectorProjectPathTable;
  
  
  /**Any component has its PanelManager. It is one line with some widgets.
   */
  private GralPanelContent[] bzrComponentBox = new GralPanelContent[100]; 

  private GralWindow_ifc testDialogBox;

  
  /**Instance for some buttons to exclude switch on only for one button.
   * GUI-concept: This is an alternative to TODO
   * 
   */
  private GralSwitchExclusiveButtonMng switchExcluder;
  
  /**Save the switchButtons to remove it when the widget is removed. */ 
  private GralWidget[] switchButtons;
  

  public GuiStatusPanel(MainData mainData, GralMngBuild_ifc panelBuildifc)
  {
    this.panelBuildifc = panelBuildifc;
    this.mainData = mainData;
  }
  
  /**Initializes the graphic. 
   * It will be called in the GUI-Thread.
   */
  void initGui()
  { int xposProjectPath = 0, yposProjectPath=5; 
  
    panelBuildifc.selectPanel("Select");
    panelBuildifc.setPositionSize(yposProjectPath, xposProjectPath, -3, 70, 'r');
    //widgdProjektpath = panelBuildifc.addTextField("projectPath", true, "Project path", 't');
    widgdProjektpath = panelBuildifc.addFileSelectField("projectPath", null, "D:/:/", "Project path", "t");
    panelBuildifc.setPositionSize(-1, -1, -2, 2, 'r');
    panelBuildifc.addButton("selectProjectPath", selectProjectPath, "c", "s", "d", "?");
    ///
    //WidgetDescriptor widgdRefresh = new WidgetDescriptor("refresh", 'B');
    //widgdRefresh.setAction(refreshProjectBzrComponents);
    panelBuildifc.setPositionSize(-1, -1, -3, 10, 'r');
    panelBuildifc.addButton("Brefresh", refreshProjectBzrComponents, "c", "s", "d", "Get/Refresh");

    String[] lines = {"1", "2"};
    
    //testDialogBox = panelBuildifc.createInfoBox( "Title", lines, true);
    //testDialogBox = new InfoBox(mainData.guifc.getitsGraphicFrame(), "Title", lines, true);

    panelBuildifc.setPositionSize(yposProjectPath, xposProjectPath, 20, 60, 'r');
    selectorProjectPath = panelBuildifc.createWindow("sel", null, GralWindow.windConcurrently);
    int[] columnWidths = {40, 10};
    panelBuildifc.setPositionSize(0, 0, 10, 60, 'd');
    selectorProjectPathTable = panelBuildifc.addTable("selectProjectPath", 20, columnWidths);
    selectorProjectPathTable.setActionChange(actionSelectorProjectPathTable);
    panelBuildifc.setPositionSize(20, 0, -3, 10, 'r');
    panelBuildifc.addButton("closeProjectBzrComponents", actionCloseProjectBzrComponents, "","","","ok");
    String sPrjPath = null;
    for(String sPrjPath1: mainData.cfg.listSwPrjs){
      if(sPrjPath ==null){ sPrjPath = sPrjPath1; } //The first is offered.
    	mainData.panelAccess.setInfo(selectorProjectPathTable, GralMng_ifc.cmdInsert, 0, sPrjPath1, null);
    }
    if(sPrjPath==null){ 
      sPrjPath = "??no project directories found.";
    }
    //Test only in one Project
    widgdProjektpath.setValue(GralMng_ifc.cmdInsert, 0,sPrjPath);
    mainData.currPrj = new DataProject(FileSystem.absolutePath(sPrjPath, null));
    
  }
  
  
  /**Removes all existing GUI-container for selection and info of the components.
   * It is called if a new project is selected. 
   */
  private void cleanComponentsInfoSelectBoxes()
  {
    if(bzrComponentBox !=null){
      panelBuildifc.selectPanel("Select");
      for(int ii=0; ii< bzrComponentBox.length; ++ii){
        GralPanelContent item = bzrComponentBox[ii]; 
        if(item !=null){ 
          bzrComponentBox[ii] = null;
          panelBuildifc.remove(item); 
        }
      }
    }
  }
  
  
  /**Builds all select and info GUI-components for all source-components of the given project.
   * 
   */
  private void buildComponentsInfoSelectBoxes()
  {
    String sProjectPath = widgdProjektpath.getValue();
    //
    mainData.getterStatus.getBzrLocations(FileSystem.absolutePath(sProjectPath, null));
    //
    cleanComponentsInfoSelectBoxes();
    switchExcluder = new GralSwitchExclusiveButtonMng();
    switchButtons = new GralWidget[mainData.currPrj.data.length];
    //
    //Only one of the switch buttons are checked. If another button is pressed, it should be deselect.
    //The switchExcluder helps to do so. 
    //It contains a special method, which captures the text of the last pressed switch button. 
    for(int ixCmpn = 0; ixCmpn < mainData.currPrj.data.length; ++ixCmpn){
      createComponentsInfoSelectBox(ixCmpn);
    }
  }
  
  
  
  private void createComponentsInfoSelectBox(int iComponent)
  { int yPosComponents = 10 + 2* iComponent;
    if(bzrComponentBox[iComponent]!=null){
      panelBuildifc.remove(bzrComponentBox[iComponent]);
    }
    DataCmpn data = mainData.currPrj.data[iComponent]; 
    String sName = data.getBzrLocationDir().getName();
    String sNamePanel = "BzrStatusSelect-"+sName;
    panelBuildifc.selectPanel("Select");
    panelBuildifc.setPositionSize(yPosComponents, 1, 2, 70, 'r');
    GralPanelContent box;
    bzrComponentBox[iComponent] = box = panelBuildifc.createCompositeBox(sNamePanel);
    SelectInfoBoxWidgds widgds = new SelectInfoBoxWidgds(box,data);
    panelBuildifc.selectPanel(sNamePanel);
    panelBuildifc.setPositionSize(0, 0, 2, 2, 'r');
    if(switchButtons[iComponent] !=null){
      switchExcluder.remove(switchButtons[iComponent]);
    }
    GralWidget widgdButton = panelBuildifc.addSwitchButton("selectMain", actionSelectCmpn, "", null, data.sNameCmpn, "", "wh", "rd");
    switchExcluder.add(widgdButton);
    widgdButton.setContentInfo(widgds);
    switchButtons[iComponent] = widgdButton;
    panelBuildifc.setPositionSize(0, 6, 2, 15, 'r');
    panelBuildifc.addText(sName, 'B', 0);
    widgds.widgdTextRevision = panelBuildifc.addText("Rev. unknown", 'B', 0x808080);
    widgds.widgdTextStatus = panelBuildifc.addText("- select it", 'B', 0x808080);
    
  }
  
  
  private void setInfoWidgetsInSelectBox(SelectInfoBoxWidgds widgds)
  {
    panelBuildifc.selectPanel(widgds.box.namePanel);
    panelBuildifc.remove(widgds.widgdTextRevision);
    panelBuildifc.remove(widgds.widgdTextStatus);
    panelBuildifc.setPositionSize(0, 21, 2, 15, 'r');
    String sRev = "Rev. ";
    if(widgds.data.nrTopRev == widgds.data.nrSboxRev){
      sRev = "Rev. " + widgds.data.nrSboxRev + " uptodate ";
    } else {
      sRev = "Rev. " + widgds.data.nrSboxRev + " / "+ widgds.data.nrTopRev;
    }
    widgds.widgdTextRevision = panelBuildifc.addText(sRev, 'B', 0x0);
    String sBzrStatus = widgds.data.uBzrStatusOutput.toString();
    boolean isModified = sBzrStatus.indexOf("modified:") >=0;
    boolean hasNew = sBzrStatus.indexOf("non-versioned:") >=0;
    if(isModified){
      //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
      widgds.widgdTextStatus = panelBuildifc.addText("- modified", 'B', 0xff0000);
    } else if(hasNew){
      //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
      widgds.widgdTextStatus = panelBuildifc.addText("- new Files", 'B', 0xff0000);
    } else {
      widgds.widgdTextStatus = panelBuildifc.addText("- no changes", 'B', 0x00ff00);
    }
    panelBuildifc.repaintCurrentPanel();
  }
  
  
  
  private final GralUserAction selectProjectPath = new GralUserAction()
  { 
    @Override public boolean userActionGui(int key, GralWidget widgetInfos, Object... values)
    {
      //testDialogBox.open();
      selectorProjectPath.setWindowVisible(true);
      return true;
    }
  };
  
  
  
  /**Action if a line is confirmed.
   * If a line is confirmed, the path is set to {@link #widgdProjektpath}.
   * If the top line is leaved, the table will be closed.
   * 
   */
  private final GralUserAction actionSelectorProjectPathTable = new GralUserAction()
  { 
    public boolean userActionGui(int key, GralWidget widgetInfos, Object... values)
    { boolean bDone = true;
      if(key == KeyCode.enter){
        GralTableLine_ifc line = (GralTableLine_ifc)values[0];
        String sPath = line.getCellText(0);
        widgdProjektpath.setValue(GralMng_ifc.cmdInsert, 0, sPath);
        buildComponentsInfoSelectBoxes();
        selectorProjectPath.setWindowVisible(false);
      } else { bDone = false; }
      return bDone;
    }
  };
  
  
  private final GralUserAction actionCloseProjectBzrComponents = new GralUserAction()
  { 
    public boolean userActionGui(int key, GralWidget widgetInfos, Object... values)
    {
      selectorProjectPath.setWindowVisible(false);
      return true;
    }
  };

  
  private final GralUserAction refreshProjectBzrComponents = new GralUserAction()
  { 
    public boolean userActionGui(int key, GralWidget widgetInfos, Object... values)
    { buildComponentsInfoSelectBoxes();
      return true;
    }
  };

  
  
  private final GralUserAction actionSelectCmpn = new GralUserAction()
  { 
    public boolean userActionGui(int key, GralWidget widgd, Object... values)
    {
      mainData.currCmpn = mainData.currPrj.selectComponent(widgd.getDataPath());
      //
      //gets the status of the components archive in the GUI-action,
      //because the appearance of the GUI should be updated:
      mainData.getterStatus.captureStatus(mainData.currCmpn);
      //
      //Build the GUI widgets for this project new:
      //it is an example for dynamic GUI appearance. 
      //Here it may be possible too to set only other information to the given widgets.
      SelectInfoBoxWidgds widgds = (SelectInfoBoxWidgds)widgd.getContentInfo();
      setInfoWidgetsInSelectBox(widgds);
      //
      //Gets all information about files in background.
      //It is necessary in another panel (Files & Diff).
      mainData.addOrderBackground(mainData.mainAction.initNewComponent);
      
      //call the exclusion of the other button:
      switchExcluder.switchAction.userActionGui(key, widgd, values);
      return true;
    }
  };
  
  
  void stop(){}
}
