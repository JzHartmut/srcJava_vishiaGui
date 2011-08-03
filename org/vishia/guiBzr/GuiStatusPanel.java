package org.vishia.guiBzr;

import java.io.File;

import org.vishia.gral.gridPanel.GuiShellMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngBuildIfc;
import org.vishia.gral.ifc.GuiPanelMngWorkingIfc;
import org.vishia.gral.ifc.GuiWindowMng_ifc;
import org.vishia.gral.ifc.UserActionGui;
import org.vishia.gral.ifc.WidgetDescriptor;
import org.vishia.gral.widget.SwitchExclusiveButtonMng;
import org.vishia.mainGuiSwt.InfoBox;
import org.vishia.util.FileSystem;

/**This class contains all data and methods of the status (select) panel.
 * 
 * @author Hartmut Schorrig
 *
 */
public class GuiStatusPanel
{
  
  private static class SelectInfoBoxWidgds
  {
    final GuiPanelMngBuildIfc box;
    final DataCmpn data;
    WidgetDescriptor widgdTextRevision, widgdTextStatus;
    
    SelectInfoBoxWidgds(GuiPanelMngBuildIfc box, DataCmpn data)
    { this.box = box; this.data = data; }
    
  }

  /**Aggregation to the main data of the GUI. */
  final MainData mainData;

  /**Aggregation to the build interface of the manager where the panel is member of. */
  final GuiPanelMngBuildIfc panelBuildifc;
  
  
  /**Widget to select the project path. 
   * The project is a user-project which contains one or more source archive-sandboxes.
   * 
   * */
  WidgetDescriptor widgdProjektpath; 
  
  
  /**A Panel which contains the table to select some projectPaths. */
  private GuiShellMngBuildIfc selectorProjectPath;
  
  /**The table (list) which contains the selectable project paths. */
  private WidgetDescriptor selectorProjectPathTable;
  
  
  /**Any component has its PanelManager. It is one line with some widgets.
   */
  private GuiPanelMngBuildIfc[] bzrComponentBox = new GuiPanelMngBuildIfc[100]; 

  private GuiWindowMng_ifc testDialogBox;

  
  /**Instance for some buttons to exclude switch on only for one button.
   * GUI-concept: This is an alternative to TODO
   * 
   */
  private SwitchExclusiveButtonMng switchExcluder;
  
  /**Save the switchButtons to remove it when the widget is removed. */ 
  private WidgetDescriptor[] switchButtons;
  

  public GuiStatusPanel(MainData mainData, GuiPanelMngBuildIfc panelBuildifc)
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
    panelBuildifc.setPosition(yposProjectPath, xposProjectPath, -2, 70, 'r');
    //widgdProjektpath = panelBuildifc.addTextField("projectPath", true, "Project path", 't');
    widgdProjektpath = panelBuildifc.addFileSelectField("projectPath", null, "D:/:/", "Project path", 't');
    panelBuildifc.setPosition(-1, -1, -2, 2, 'r');
    panelBuildifc.addButton("selectProjectPath", selectProjectPath, "c", "s", "d", "?");
    ///
    //WidgetDescriptor widgdRefresh = new WidgetDescriptor("refresh", 'B');
    //widgdRefresh.setAction(refreshProjectBzrComponents);
    panelBuildifc.setPosition(-1, -1, -3, 10, 'r');
    panelBuildifc.addButton("Brefresh", refreshProjectBzrComponents, "c", "s", "d", "Get/Refresh");

    String[] lines = {"1", "2"};
    
    testDialogBox = panelBuildifc.createInfoBox( "Title", lines, true);
    //testDialogBox = new InfoBox(mainData.guifc.getitsGraphicFrame(), "Title", lines, true);

    panelBuildifc.setPosition(yposProjectPath, xposProjectPath, 20, 60, 'r');
    selectorProjectPath = panelBuildifc.createWindow(null, false);
    int[] columnWidths = {40, 10};
    selectorProjectPath.setPosition(0, 0, 10, 60, 'd');
    selectorProjectPathTable = selectorProjectPath.addTable("selectProjectPath", 20, columnWidths);
    selectorProjectPathTable.setActionChange(actionSelectorProjectPathTable);
    selectorProjectPath.setPosition(20, 0, -3, 10, 'r');
    selectorProjectPath.addButton("closeProjectBzrComponents", actionCloseProjectBzrComponents, "","","","ok");
    String sPrjPath = null;
    for(String sPrjPath1: mainData.cfg.listSwPrjs){
      if(sPrjPath ==null){ sPrjPath = sPrjPath1; } //The first is offered.
    	mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,sPrjPath1);
    }
    /*
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,"/home/hartmut/vishia/GUI");
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,"/home/hartmut/vishia/bazaarGui");
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,"/home/hartmut/vishia/Java2C/sf/Java2C");
    mainData.panelAccess.setInfo(selectorProjectPathTable, GuiPanelMngWorkingIfc.cmdInsert, 0,"/home/hartmut/vishia/ZBNF/sf/ZBNF");
    */
    //Test only in one Project
    widgdProjektpath.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0,sPrjPath);
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
        GuiPanelMngBuildIfc item = bzrComponentBox[ii]; 
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
    switchExcluder = new SwitchExclusiveButtonMng();
    switchButtons = new WidgetDescriptor[mainData.currPrj.data.length];
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
    panelBuildifc.selectPanel("Select");
    panelBuildifc.setPosition(yPosComponents, 1, 2, 70, 'r');
    GuiPanelMngBuildIfc box;
    bzrComponentBox[iComponent] = box = panelBuildifc.createCompositeBox();
    SelectInfoBoxWidgds widgds = new SelectInfoBoxWidgds(box,data);
    box.selectPanel("$");
    box.setPosition(0, 0, 2, 2, 'r');
    if(switchButtons[iComponent] !=null){
      switchExcluder.remove(switchButtons[iComponent]);
    }
    WidgetDescriptor widgdButton = box.addSwitchButton("selectMain", actionSelectCmpn, "", null, data.sNameCmpn, "", "wh", "rd");
    switchExcluder.add(widgdButton);
    widgdButton.setContentInfo(widgds);
    switchButtons[iComponent] = widgdButton;
    box.setPosition(0, 6, 2, 15, 'r');
    box.addText(sName, 'B', 0);
    widgds.widgdTextRevision = box.addText("Rev. unknown", 'B', 0x808080);
    widgds.widgdTextStatus = box.addText("- select it", 'B', 0x808080);
    
  }
  
  
  private void setInfoWidgetsInSelectBox(SelectInfoBoxWidgds widgds)
  {
    widgds.box.remove(widgds.widgdTextRevision);
    widgds.box.remove(widgds.widgdTextStatus);
    widgds.box.selectPanel("$");
    widgds.box.setPosition(0, 21, 2, 15, 'r');
    String sRev = "Rev. ";
    if(widgds.data.nrTopRev == widgds.data.nrSboxRev){
      sRev = "Rev. " + widgds.data.nrSboxRev + " uptodate ";
    } else {
      sRev = "Rev. " + widgds.data.nrSboxRev + " / "+ widgds.data.nrTopRev;
    }
    widgds.widgdTextRevision = widgds.box.addText(sRev, 'B', 0x0);
    String sBzrStatus = widgds.data.uBzrStatusOutput.toString();
    boolean isModified = sBzrStatus.indexOf("modified:") >=0;
    boolean hasNew = sBzrStatus.indexOf("non-versioned:") >=0;
    if(isModified){
      //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
      widgds.widgdTextStatus = widgds.box.addText("- modified", 'B', 0xff0000);
    } else if(hasNew){
      //bzrComponentBox[iComponent].setPosition(0, 40, 2, 10, 'r');
      widgds.widgdTextStatus = widgds.box.addText("- new Files", 'B', 0xff0000);
    } else {
      widgds.widgdTextStatus = widgds.box.addText("- no changes", 'B', 0x00ff00);
    }
    widgds.box.repaint();
  }
  
  
  
  private final UserActionGui selectProjectPath = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
      //testDialogBox.open();
      selectorProjectPath.setWindowVisible(true);
    }
  };
  
  
  
  /**Action if a line is confirmed or up command on top line is invoked.
   * If a line is confirmed, the path is set to {@link #widgdProjektpath}.
   * If the top line is leaved, the table will be closed.
   * 
   */
  private final UserActionGui actionSelectorProjectPathTable = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
      if(sCmd.equals("ok")){
        String sPath = (String)values[0];
        widgdProjektpath.setValue(GuiPanelMngWorkingIfc.cmdInsert, 0, sPath);
        buildComponentsInfoSelectBoxes();
      }
      selectorProjectPath.setWindowVisible(false);
    }
  };
  
  
  private final UserActionGui actionCloseProjectBzrComponents = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    {
      selectorProjectPath.setWindowVisible(false);
    }
  };

  
  private final UserActionGui refreshProjectBzrComponents = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgetInfos, Object... values)
    { buildComponentsInfoSelectBoxes();
    }
  };

  
  
  private final UserActionGui actionSelectCmpn = new UserActionGui()
  { 
    public void userActionGui(String sCmd, WidgetDescriptor widgd, Object... values)
    {
      mainData.currCmpn = mainData.currPrj.selectComponent(widgd.sDataPath);
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
      switchExcluder.switchAction.userActionGui(sCmd, widgd, values);
    }
  };
  
  
  void stop(){}
}
